# API REST

## Objetivo y alcance

Esta API REST da soporte end-to-end a la gestión de reservas de salas para estudiantes, cubriendo
los flujos de consulta de disponibilidad, alta y cancelación de reservas, y operando sobre los
recursos modelo definidos en el dominio (Reserva, Sala, Estudiante, etc). La API implementa los
endpoints para todas las historias priorizadas del sistema y está alineada con las pruebas BDD y
los escenarios definidos para la entrega.

## Qué significa REST acá

- Cada endpoint expone operaciones sobre un recurso: sala, estudiante o reserva.
- `GET` consulta información, `POST` crea o modifica recursos, alineado a buenas prácticas REST.
- Los datos simples viajan en parámetros de URL (`GET`), los datos compuestos en `body` (`POST`).
- Los cuerpos de alta y creación se validan con DTOs tipados y Bean Validation; no se reciben
  `Map` genéricos ni se decodifican parámetros manualmente.
- Las respuestas exitosas tienen formato JSON, salvo los endpoints de reserva, cancelación,
  check-in y procesar-ausencias que retornan texto plano con el mensaje de confirmación.
- Los códigos HTTP usados son: `200 OK` para éxito y `400 Bad Request` para cualquier error
  (validación, recurso inexistente, regla de negocio o acción no permitida), acompañado siempre
  de un mensaje de texto que describe la causa.

## Recursos y endpoints

| Recurso                    | Método | Ruta                                          | Uso                                   | Historia                    |
|----------------------------|--------|-----------------------------------------------|---------------------------------------|-----------------------------|
| Consulta de disponibilidad | GET    | /api/salas/disponibilidad?fecha=dd/MM/yyyy    | Consulta cupos para cada sala en fecha | HU-02   |
| Alta de sala               | POST   | /api/salas                                    | Dar de alta una nueva sala            |    HU-01  |
| Alta de estudiante         | POST   | /api/estudiantes                              | Dar de alta un nuevo estudiante       |      HU-01|
| Crear reserva              | POST   | /api/reservas                                 | Crear una nueva reserva de sala       | HU-01  |
| Cancelar reserva           | POST   | /api/reservas/{codigo}/cancelar?solicitante=  | Cancelar una reserva (dueño)          | HU-03       |
| Check-in                   | POST   | /api/reservas/{codigo}/check-in?operario=     | Marcar asistencia (operario personal) | HU-04  |
| Historial de reserva       | GET    | /api/reservas/{codigo}/historial              | Ver historial de cambios de estado    | HU-04                |
| Procesar ausencias         | POST   | /api/reservas/procesar-ausencias              | Marca en lote las reservas ausentes   | HU-04     |
| Health                     | GET    | /api/health                                   | Ping/estado de la API                 | Infra     |
| Storybook UX               | GET    | /docs/ux/storybook/index.html                 | Prototipo navegable servido por el backend | UX        |

## Contratos de request y respuesta

### Consulta de disponibilidad

**Request ejemplo:**
```http
GET /api/salas/disponibilidad?fecha=12/06/2026
Accept: application/json
```

**Respuesta ejemplo:**
```json
[
  { "nombreSala": "Sala 101", "cuposDisponibles": 4 },
  { "nombreSala": "Aula 2",   "cuposDisponibles": 8 }
]
```

Si no hay salas registradas retorna un array vacío. Si falta el parámetro `fecha` o su formato
es inválido: `400 Bad Request`.

El cupo se calcula por día y sala restando las reservas en estado `confirmada` de la capacidad
máxima. La cancelación libera el cupo de inmediato.

---

### Alta de sala

**Request:**
```http
POST /api/salas
Content-Type: application/json

{
  "codigo": "S101",
  "nombre": "Sala 101",
  "capacidad": 6
}
```

**Respuesta:**
```json
{
  "codigo": "S101",
  "nombre": "Sala 101",
  "capacidadMaxima": 6
}
```

Si ya existe una sala con el mismo código o nombre, retorna la entidad existente sin error.
Los campos `codigo` y `nombre` no pueden estar en blanco y `capacidad` debe ser mayor a cero;
en caso contrario retorna `400 Bad Request` con mensaje descriptivo.

---

### Alta de estudiante

**Request:**
```http
POST /api/estudiantes
Content-Type: application/json

{
  "legajo": "111/22",
  "nombre": "Ana Perez"
}
```

**Respuesta:**
```json
{
  "legajo": "111/22",
  "nombre": "Ana Perez",
  "estadoHabilitacion": "ACTIVO"
}
```

Si ya existe un estudiante con el mismo legajo o nombre, retorna el existente sin error.
`legajo` y `nombre` no pueden estar en blanco. Todo estudiante nuevo se registra como `ACTIVO`.

---

### Crear reserva

**Request:**
```http
POST /api/reservas
Content-Type: application/json

{
  "nombreEstudiante": "Ana Perez",
  "nombreSala": "Sala 101",
  "fecha": "12/06/2026",
  "horaInicio": "09:00",
  "horaFin": "09:30"
}
```

**Respuesta (texto plano):**
```
200 OK
RES-1A2B | ¡Reserva confirmada! Te esperamos en la Sala 101 el 12/06/2026 a las 09:00.
```

Toda reserva creada exitosamente nace en estado `confirmada`.
El código de reserva (`RES-XXXX`) se incluye al inicio del mensaje separado por `|` y es el
identificador para las operaciones posteriores (cancelar, check-in, historial). Se genera con
un UUID parcial y se verifica que no colisione con una reserva existente.

Errores posibles (`400 Bad Request`):
- Payload nulo o campos obligatorios ausentes o en blanco.
- Formato de fecha u hora inválido, o `horaInicio` no anterior a `horaFin`.
- Sala o estudiante no registrados en el sistema.
- Estudiante con `estadoHabilitacion` distinto de `ACTIVO`.
- No hay cupo disponible para ese turno.
- El estudiante ya tiene una reserva confirmada para ese mismo horario.

---

### Cancelar reserva

**Request:**
```http
POST /api/reservas/RES-1A2B/cancelar?solicitante=Ana%20Perez
```

El parámetro `solicitante` debe coincidir con el nombre o legajo del estudiante titular
de la reserva. Spring MVC decodifica el parámetro; el cliente debe enviarlo URL-encoded.

**Respuesta (texto plano):**
```
200 OK
Hola Ana, tu reserva en la Sala 101 para el 12/06/2026 ha sido cancelada con éxito.
```

Errores posibles (`400 Bad Request`):
- La reserva no existe.
- La reserva ya estaba cancelada.
- La reserva no está en estado `confirmada`.
- El solicitante no es el titular de la reserva.
- El parámetro `solicitante` está vacío o ausente.

---

### Check-in de reserva

**Request:**
```http
POST /api/reservas/RES-1A2B/check-in?operario=Juan%20Testa
```

Si no se indica operario, se usa `"SISTEMA"` como valor por defecto.

**Respuesta (texto plano):**
```
200 OK
Asistencia registrada: Ana Perez (111/22), Sala 101, 12/06/2026 09:00
```

Errores posibles (`400 Bad Request`):
- La reserva no existe.
- La reserva ya tiene asistencia registrada.
- La reserva está cancelada o no está en estado `confirmada`.

---

### Historial de reserva

**Request:**
```http
GET /api/reservas/RES-1A2B/historial
```

**Respuesta:**
```json
[
  {
    "estado": "confirmada",
    "fechaRegistro": "01/06/2026 08:11",
    "operario": "SISTEMA",
    "fechaTurno": "12/06/2026",
    "horaInicio": "09:00",
    "horaFin": "09:30"
  },
  {
    "estado": "cancelada",
    "fechaRegistro": "02/06/2026 09:14",
    "operario": "SISTEMA",
    "fechaTurno": "12/06/2026",
    "horaInicio": "09:00",
    "horaFin": "09:30"
  }
]
```

La lista está ordenada cronológicamente. El último elemento refleja el estado actual de la reserva.
Cada entrada incluye los datos del turno reservado (`fechaTurno`, `horaInicio`, `horaFin`) para
que el cliente pueda auditar la reserva sin consultar otro endpoint.

Errores posibles (`400 Bad Request`):
- La reserva no existe.

---

### Procesar ausencias

**Request:**
```http
POST /api/reservas/procesar-ausencias
```

Marca como `ausente` todas las reservas en estado `confirmada` cuyo turno ya haya finalizado
(fecha anterior a hoy, o fecha de hoy con hora de fin pasada).

Para pruebas deterministas se pueden indicar `fecha=dd/MM/yyyy` y `hora=HH:mm` como parámetros
opcionales; si se omiten, se usa el reloj del sistema inyectado en el servicio.

**Respuesta (texto plano):**
```
200 OK
Reservas marcadas como ausente: 3
```

## Errores y estados HTTP

Todos los errores retornan `400 Bad Request` con el mensaje de causa en texto plano. No se
distinguen códigos HTTP por tipo de error (recurso inexistente, regla de negocio, permiso
denegado): en todos los casos el mensaje incluye la descripción precisa del problema.

## Flujo de prueba de API (ejemplo real, alineado con BDD)

1. Dar de alta la sala `"Sala 101"` con capacidad 10 y 6 estudiantes
   (Ana Perez, Bruno Diaz, Carla Gomez, Diego Ruiz, Elena Torres, Facundo Silva).
2. Consultar disponibilidad para `12/06/2026` → retorna 10 cupos disponibles.
3. Crear 6 reservas sobre la sala y fecha mencionada con horarios consecutivos
   (09:00–09:30, 09:30–10:00, ..., 11:30–12:00). Todas nacen en estado `confirmada`.
4. Cancelar 2 reservas (Ana Perez y Bruno Diaz) usando su nombre como solicitante.
5. Volver a consultar disponibilidad para `12/06/2026` → retorna 6 cupos disponibles
   (10 − 4 reservas confirmadas activas).

En cada paso: si la acción es exitosa, `200 OK` con la respuesta correspondiente.
Si hay algún error o restricción, `400 Bad Request` con mensaje descriptivo.

## Trazabilidad

| HU                                  | Endpoint                              | Escenario                   | Evidencia           |
|-------------------------------------|---------------------------------------|-----------------------------|---------------------|
| HU de disponibilidad (HU-02)        | GET /api/salas/disponibilidad         | Consulta disponibilidad     | Test BDD, HTTP/JSON |
| HU de creación de reservas (HU-01)  | POST /api/reservas                    | Alta de reservas con datos  | Test BDD, texto     |
| HU de cancelación (HU-03)           | POST /api/reservas/{codigo}/cancelar  | Cancelación por titular     | Test BDD, texto     |
| HU de asistencia (HU-04)            | POST /api/reservas/{codigo}/check-in  | Check-in y ausencias        | Test BDD, unitarios |
| HU de trazabilidad (HU-04)          | GET /api/reservas/{codigo}/historial  | Historial de estados        | Test BDD, unitarios |

---

Este documento refleja fielmente la API desarrollada y su relación con los requerimientos y el
modelo de dominio, manteniendo coherencia end-to-end.

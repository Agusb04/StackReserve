# API REST

## Objetivo y alcance

Describí la API REST que da soporte a las historias priorizadas del cierre end-to-end. El foco está en consultar disponibilidad, crear reservas y cancelarlas. El contrato REST sí es obligatorio. Si incluís `db/schema.sql`, tratá ese archivo como diseño opcional y coherente con la API.

## Qué significa REST acá

No hace falta que el texto suponga conocimientos previos. Explicá con palabras simples:

- qué recurso representa cada endpoint;
- cuándo usar `GET`, `POST`, `PUT`, `PATCH` o `DELETE`;
- qué información viaja en la URL, cuál en el body y cuál vuelve en la respuesta;
- qué códigos HTTP van a devolver en los casos normales y en los errores.

La idea no es escribir teoría abstracta, sino dejar claro cómo se usa la API desde un cliente real o desde un test HTTP.

## Recursos y endpoints

| Recurso | Método | Ruta | Uso | Historia |
| --- | --- | --- | --- | --- |
| Consulta de disponibilidad | `GET` | `/api/disponibilidades` o equivalente | Consulta la disponibilidad de un recurso o franja | HU de disponibilidad |
| Alta | `POST` | `/api/recursos` o equivalente | Crea un nuevo registro principal | HU de creación |
| Detalle | `GET` | `/api/recursos/{id}` o equivalente | Consulta el estado de un registro principal | Seguimiento del flujo |
| Estado / cancelación | `PATCH` / `POST` | `/api/recursos/{id}/estado` o equivalente | Cambia el estado de un registro existente | HU de cancelación |

No hace falta usar exactamente esas rutas. Los nombres de ejemplo son orientativos: documentá las rutas reales que elijas y mantenelas consistentes con los tests.

## Contratos de request y respuesta

### Disponibilidad

Ejemplo de request:

```http
GET /api/disponibilidades?fecha=2026-06-12&desde=09:00&hasta=12:00&recurso=Recurso%20A
Accept: application/json
```

Ejemplo de respuesta `200 OK`:

```json
{
  "fecha": "2026-06-12",
  "recurso": "Recurso A",
  "desde": "09:00",
  "hasta": "12:00",
  "bloquesDisponibles": [
    { "inicio": "09:00", "fin": "09:30" },
    { "inicio": "10:30", "fin": "11:00" }
  ]
}
```

Si el rango no existe, el recurso no existe o el formato es inválido, explicá qué devuelve la API y por qué.

### Reserva

Ejemplo de request:

```http
POST /api/recursos
Content-Type: application/json

{
  "persona": "Persona A",
  "recurso": "Recurso A",
  "fecha": "2026-06-12",
  "desde": "09:00",
  "hasta": "09:30"
}
```

Respuesta esperada cuando el registro se crea:

```http
201 Created
Location: /api/recursos/123
```

```json
{
  "id": 123,
  "persona": "Persona A",
  "recurso": "Recurso A",
  "fecha": "2026-06-12",
  "desde": "09:00",
  "hasta": "09:30",
  "estado": "pendiente"
}
```

Si la franja ya está ocupada o el registro no se puede crear por una regla de negocio, documentá el código de respuesta y el cuerpo de error.

### Cancelación

Ejemplo de request:

```http
PATCH /api/recursos/123/estado
Content-Type: application/json
```

Respuesta esperada cuando la cancelación se aplica:

```http
200 OK
```

```json
{
  "id": 123,
  "estado": "cancelada"
}
```

Si el registro no existe, ya estaba cancelado o no puede cancelarse, explicá el código de respuesta y el mensaje de error.

## Errores y estados HTTP

Incluí al menos estos casos, con el código que quien cursa elija:

- `400 Bad Request` para datos mal formados o faltantes;
- `404 Not Found` para registros o recursos inexistentes;
- `409 Conflict` para superposiciones o reglas de negocio que bloquean una operación;
- `422 Unprocessable Entity` si quien cursa prefiere separar validación semántica de validación sintáctica.

Si usan otro código, justifíquenlo. Lo importante es que el contrato sea consistente y que los tests lo verifiquen.

## Flujo de prueba de API

Explicá un flujo de prueba concreto, con datos concretos, que se pueda ejecutar contra la API por HTTP/JSON. Tomá este ejemplo como referencia mínima:

1. Antes de la primera consulta, dejá claro que el sistema ya tiene datos precargados y que la disponibilidad inicial no arranca vacía.
2. Consultá disponibilidad para `2026-06-12` entre `09:00` y `12:00` en `Recurso A`.
3. Creá 6 registros de personas distintas: `Persona A`, `Persona B`, `Persona C`, `Persona D`, `Persona E` y `Persona F`.
4. Confirmá 4 de esos registros o pasalos a un estado confirmado, según el flujo elegido.
5. Cancelá 2 de los registros confirmados.
6. Volvé a consultar la misma fecha, recurso y franja.
7. Mostrá qué cambió en la disponibilidad después de las altas, confirmaciones y cancelaciones.

Para cada paso, mostrà:

- el request real o resumido;
- el código HTTP esperado;
- el dato observable que cambia;
- qué validación hace el test.

Ese flujo debe existir también como prueba automatizada.

## Trazabilidad

| HU | Endpoint | Escenario | Evidencia |
| --- | --- | --- | --- |
| HU de disponibilidad | `GET /api/disponibilidades` | Consulta inicial y consulta posterior | Test HTTP/JSON |
| HU de creación | `POST /api/recursos` | Alta de registros con datos concretos | Test HTTP/JSON + BDD |
| HU de cancelación | `PATCH /api/recursos/{id}/estado` | Cambio de estado de registros ya creados | Test HTTP/JSON + BDD |

## Cómo se conecta Storybook con esta API

No hace falta inventar una integración distinta por historia. El template ya viene preparado para que el front de Storybook lea una configuración y llame a la API por `fetch`.

### Piezas que ya vienen listas

| Archivo | Qué hace | Qué cambiás |
| --- | --- | --- |
| `docs/ux/storybook/src/storybook-demo.config.json` | Declara el endpoint y los textos visibles | `apiUrl`, `title`, `subtitle`, `requestLabel`, `successHint`, `failureHint` |
| `docs/ux/storybook/src/storybook-demo.js` | Lee la config, hace el `fetch` y pinta la respuesta | Nada por historia |
| `docs/ux/storybook/src/HU-XX-....html` | Muestra la story concreta | El contenido visible del escenario |

### Regla de uso

- implementás el endpoint en Spring Boot;
- dejás la ruta lista en la API;
- copiás la story HTML que corresponde;
- editás solo el JSON de configuración;
- no escribís un JavaScript nuevo por cada servicio;
- probás todo en `http://localhost:8080/docs/ux/storybook/index.html` con la aplicación levantada.

### Ejemplo mínimo

Si tu backend expone `GET /api/health`, la story puede usar:

```json
{
  "title": "Salud del sistema",
  "subtitle": "Consulta la respuesta del servicio de ejemplo",
  "apiUrl": "/api/health",
  "requestLabel": "GET /api/health",
  "successHint": "La API devolvió la respuesta esperada.",
  "failureHint": "Verificá que el backend esté levantado y que la ruta exista."
}
```

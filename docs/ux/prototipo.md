# Prototipo

## Objetivo del prototipo

Demostrar la viabilidad técnica y funcional del ciclo completo de reserva de salas — desde la consulta de disponibilidad hasta el registro de asistencia — validando las reglas de negocio de capacidad máxima (RN-01), orden de llegada (RN-07), restricción de doble reserva (RN-10), liberación de cupo por cancelación (RN-04) y marcado automático de ausencias (RN-09) en un entorno controlado que simula el comportamiento del sistema real a través de transiciones de estado en memoria.

---

# Historias y escenarios cubiertos

| Historia | Escenario principal | Storybook (Archivo fuente) |
| :--- | :--- | :--- |
| **HU-01** | Reserva exitosa en sala con cupo disponible | `docs/ux/storybook/src/HU-01-reserva.html` |
| **HU-01** | Rechazo por capacidad máxima alcanzada (RN-01) | `docs/ux/storybook/src/HU-01-reserva.html` |
| **HU-01** | Resolución de conflicto por orden de llegada — FCFS (RN-07) | `docs/ux/storybook/src/HU-01-reserva.html` |
| **HU-01** | Validación de reserva única por alumno (RN-10) | `docs/ux/storybook/src/HU-01-reserva.html` |
| **HU-02** | Consulta con salas libres (RF-01, RN-01) | `docs/ux/storybook/src/HU-02-disponibilidad.html` |
| **HU-02** | Consulta con sala parcialmente ocupada | `docs/ux/storybook/src/HU-02-disponibilidad.html` |
| **HU-02** | Consulta con sala sin cupo | `docs/ux/storybook/src/HU-02-disponibilidad.html` |
| **HU-02** | Consulta para un día sin turnos configurados (lista vacía) | `docs/ux/storybook/src/HU-02-disponibilidad.html` |
| **HU-03** | Cancelación exitosa y liberación de cupo (RN-04) | `docs/ux/storybook/src/HU-03-cancelacion.html` |
| **HU-03** | Turno liberado reservado nuevamente | `docs/ux/storybook/src/HU-03-cancelacion.html` |
| **HU-03** | Rechazo por reserva ya cancelada | `docs/ux/storybook/src/HU-03-cancelacion.html` |
| **HU-03** | Rechazo por falta de titularidad | `docs/ux/storybook/src/HU-03-cancelacion.html` |
| **HU-03** | Rechazo por reserva inexistente | `docs/ux/storybook/src/HU-03-cancelacion.html` |
| **HU-04** | Check-in exitoso al inicio del turno (RN-06) | `docs/ux/storybook/src/HU-04-asistencia.html` |
| **HU-04** | Rechazo por asistencia ya registrada | `docs/ux/storybook/src/HU-04-asistencia.html` |
| **HU-04** | Marcado automático de ausencia al vencer el turno (RN-09) | `docs/ux/storybook/src/HU-04-asistencia.html` |
| **HU-04** | Historial refleja estado final de cada reserva | `docs/ux/storybook/src/HU-04-asistencia.html` |

---

# Pantallas o recorridos cubiertos

## HU-01 — Reserva de sala

- **Selector de escenario Gherkin:** Permite alternar entre los cuatro escenarios de la historia y ver la reacción del sistema en tiempo real.
- **Tarjetas dinámicas de sala:** Representación visual de la Sala Multimedia A y Silencio B, con indicadores de ocupación y estados visuales reactivos (borde azul para disponible, rojo y opacidad reducida para lleno).
- **Gestor de identidad dinámico:** Botones que cambian según el escenario para simular peticiones de diferentes usuarios (Lucas Martínez, Ana Lizondo, Bruno Díaz) con marcas temporales para validar concurrencia.
- **Log de transacciones:** Panel de historial que registra cada éxito o rechazo, vinculando la acción a la entidad `HistorialEstado` del modelo de dominio.

## HU-02 — Consulta de disponibilidad

- **Selector de escenario Gherkin:** Cubre los cuatro escenarios definidos en `HU-02.feature`.
- **Indicador de fecha consultada:** Muestra la fecha de la consulta (`27/04/2026` o `01/01/2030`) para reflejar el parámetro del paso `Cuando`.
- **Tarjetas de sala con barra de cupo:** Cada sala muestra el número de lugares ocupados y disponibles con una barra de progreso coloreada según estado (verde para disponible, amarillo para parcial, rojo para sin cupo).
- **Resumen de consulta:** Chips que indican la cantidad de salas retornadas y el total de cupos disponibles.
- **Empty state:** Cuando no existen turnos configurados para la fecha, se muestra un estado vacío que refleja el retorno de lista vacía del endpoint.

## HU-03 — Cancelación de reserva

- **Selector de escenario Gherkin:** Cubre los cinco escenarios definidos en `HU-03.feature`.
- **Tarjeta de reserva con estado reactivo:** Muestra el código, titular, sala y turno. El borde y la opacidad cambian al transicionar de `confirmada` a `cancelada`.
- **Indicador de cupo dinámico:** Barra de progreso que refleja la liberación del cupo al confirmar la cancelación (RN-04).
- **Validaciones de error:** Mensajes específicos para reserva ya cancelada, falta de titularidad y código inexistente.

## HU-04 — Registro de asistencia

- **Selector de escenario Gherkin:** Cubre los cuatro escenarios de `HU-04.feature` incluyendo el esquema parametrizado.
- **Reloj de turno simulado:** Indicador de hora que avanza de `10:00` (turno activo) a `11:01` (turno finalizado) al simular el proceso automático de ausencias.
- **Filas de reserva por estudiante:** Cada fila muestra código, nombre, legajo, sala y estado actual con badge de color.
- **Procesamiento de ausencias automático:** Botón que simula el fin del turno y dispara el marcado automático de `RES-0901` como `ausente` (RN-09).
- **Tabla de historial parametrizada:** Vista del esquema del escenario con los dos casos de la tabla de ejemplos del feature.

---

# Storybook

- **Punto de entrada:** `docs/ux/storybook/index.html` — selector central con navegación por historia y sección.
- **Stories:**
  - `docs/ux/storybook/src/HU-01-reserva.html`
  - `docs/ux/storybook/src/HU-02-disponibilidad.html`
  - `docs/ux/storybook/src/HU-03-cancelacion.html`
  - `docs/ux/storybook/src/HU-04-asistencia.html`
- **Lógica de estados:** Implementación en JavaScript Vanilla. Cada story gestiona su propio estado en memoria y se resetea al cambiar de escenario sin recargar el navegador.

---

# Link read-only

- **Prototipo interactivo:** http://127.0.0.1:5500/2026-1c-tpi-Agusb04/docs/ux/storybook/index.html
- **Estado:** Congelado para entrega TPI — Ejercicio 03.

---

# Export congelado

- `docs/ux/exports/HU-01-reserva-exitosa.png`: Muestra el feedback positivo y el registro en el historial tras una reserva exitosa.
- `docs/ux/exports/HU-01-error-capacidad.png`: Muestra la Sala B bloqueada y el mensaje de error al intentar exceder el cupo físico (RN-01).
- `docs/ux/exports/HU-01-error-duplicado-RN10.png`: Muestra el bloqueo de seguridad cuando el usuario intenta reservar dos salas simultáneamente (RN-10).
- `docs/ux/exports/HU-01-concurrencia-fcfs.png`: Evidencia de la asignación de cupo por orden de llegada mediante marcas de tiempo en el log.

---

# Decisiones de UX visibles

1. **Feedback dinámico de estado:** Cada story usa señales visuales (colores de borde, opacidad, badges) para comunicar el estado de las entidades del dominio sin depender solo de texto.

2. **Mapeo 1:1 con Gherkin:** Cada story incluye un panel con el texto exacto del escenario seleccionado, permitiendo validación directa entre los criterios de aceptación y la respuesta de la interfaz.

3. **Prevención proactiva de errores:** Las validaciones de RN-10 (unicidad), titularidad y estado previo se implementan antes de modificar el estado, evitando inconsistencias en la base de datos simulada.

4. **Trazabilidad del dominio:** El panel de historial simula la persistencia en `HistorialEstado`, detallando código de reserva, estado y motivo de la transición para facilitar la auditoría.

5. **Ciclo de vida completo:** Las cuatro stories cubren el flujo completo del dominio: consulta de disponibilidad → reserva → cancelación → registro de asistencia / ausencia automática.
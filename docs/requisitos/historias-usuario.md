# Historias de usuario

## Backlog

| ID | Historia | Evidencia principal | Épica opcional |
|----|----------|--------------------|----------------|
| HU-01 | Para garantizar mi lugar de estudio sin depender del personal, como estudiante, quiero que se reserve un turno en una sala disponible | RF-02, RF-03, RF-08, RF-11, RN-01, RN-02, RN-10 | Gestión de reservas |
| HU-02 | Para saber qué opciones tengo antes de decidir si reservo, como estudiante, quiero que se consulte la disponibilidad de salas en tiempo real | RF-01, RN-02 | Gestión de reservas |
| HU-03 | Para liberar el turno y que otro estudiante pueda usarlo, como estudiante, quiero que se cancele una reserva ya confirmada | RF-05, RN-04 | Gestión de reservas |
| HU-04 | Para validar el uso efectivo de la sala y contabilizar ausencias, como personal de biblioteca, quiero que se registre la asistencia de un estudiante al inicio de su turno | RF-10, RN-05, RN-06 | Operación del mostrador |
| HU-05 | Para organizar mi trabajo en el mostrador, como personal de biblioteca, quiero que se visualicen todas las reservas del día ordenadas por horario | RF-07 | Operación del mostrador |
| HU-06 | Para tomar decisiones sobre la gestión de salas, como coordinación, quiero que se muestre un dashboard con métricas de ocupación y ausentismo | RF-09 | Métricas y trazabilidad |
| HU-07 | Para poder auditar cualquier turno ante un conflicto, como coordinación, quiero que se registren todas las reservas, cancelaciones y ausencias en un historial | RF-06, RN-06 | Métricas y trazabilidad |

---

## Historias priorizadas

| ID | Motivo de prioridad | Feature esperado | Tag canónico |
|----|--------------------|-----------------|--------------|
| HU-01 | Núcleo del sistema. Concentra reglas críticas como evitar superposición y gestionar disponibilidad | `src/test/resources/features/HU-01.feature` | `@creacion-reserva` |
| HU-02 | Prioridad de los interesados para E3. Habilita consultar qué salas tienen cupo disponible para un día determinado antes de reservar | `src/test/resources/features/HU-02.feature` | `@disponibilidad-salas` |
| HU-03 | Prioridad de los interesados para E3. Completa el ciclo del turno desde el lado del estudiante y permite liberar recursos | `src/test/resources/features/HU-03.feature` | `@cancelacion-reserva` |
| HU-04 | Cierra el ciclo operativo y habilita trazabilidad y análisis de ausencias | `src/test/resources/features/HU-04.feature` | `@registrar-asistencia`|

---

## Criterios de calidad

- Las historias siguen el formato **Para / Como / Quiero que**, priorizando el valor de negocio
- Cada historia está vinculada con **requisitos funcionales y no funcionales**
- Se evita solapamiento entre historias
- Se priorizan historias que entregan valor temprano
- Las historias priorizadas cubren el **flujo principal del dominio (reserva → uso → registro)**
- Cada historia es testeable mediante escenarios (Gherkin en el siguiente ejercicio)
- Cada historia priorizada tiene exactamente un archivo `.feature` asociado en `src/test/resources/features/` (relación 1:1 HU ↔ feature)
- Las historias con tag canónico asignado lo llevan explícitamente al inicio de su `.feature`, en la línea inmediata anterior a `Característica:`
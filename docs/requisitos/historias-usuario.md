
# Historias de usuario
## Backlog

| ID | Historia | Evidencia principal | Épica opcional |
|----|----------|--------------------|----------------|
| HU-01 | Como estudiante, quiero reservar un turno en una sala disponible, para garantizar mi lugar de estudio sin depender del personal | RF-02, RF-03, RF-08, RN-01, RN-02 | Gestión de reservas |
| HU-02 | Como estudiante, quiero consultar la disponibilidad de salas en tiempo real, para saber qué opciones tengo antes de decidir si reservo | RF-01, RN-02 | Gestión de reservas |
| HU-03 | Como estudiante, quiero cancelar una reserva que ya tengo confirmada, para liberar el turno y que otro estudiante pueda usarlo | RF-05, RN-04 | Gestión de reservas |
| HU-04 | Como personal de biblioteca, quiero registrar la asistencia de un estudiante al inicio de su turno, para validar el uso efectivo de la sala y contabilizar ausencias | RF-10, RN-05, RN-06 | Operación del mostrador |
| HU-05 | Como personal de biblioteca, quiero visualizar todas las reservas del día ordenadas por horario, para organizar mi trabajo en el mostrador | RF-07 | Operación del mostrador |
| HU-06 | Como coordinación, quiero ver un dashboard con métricas de ocupación y ausentismo, para tomar decisiones sobre la gestión de salas | RF-09 | Métricas y trazabilidad |
| HU-07 | Como coordinación, quiero que el sistema registre todas las reservas, cancelaciones y ausencias en un historial, para poder auditar cualquier turno ante un conflicto | RF-06, RN-06 | Métricas y trazabilidad |

---

## Historias priorizadas

| ID | Motivo de prioridad | Feature esperado |
|----|--------------------|------------------|
| HU-01 | Núcleo del sistema. Concentra reglas críticas como evitar superposición y gestionar disponibilidad | `src/test/resources/features/HU-02.feature` |
| HU-03 | Completa el ciclo del turno desde el lado del estudiante y permite liberar recursos | `src/test/resources/features/HU-03.feature` |
| HU-04 | Cierra el ciclo operativo y habilita trazabilidad y análisis de ausencias | `src/test/resources/features/HU-04.feature` |

---

## Criterios de calidad

- Las historias siguen el formato **Como / quiero / para**  
- Cada historia está vinculada con **requisitos funcionales y no funcionales**  
- Se evita solapamiento entre historias  
- Se priorizan historias que entregan valor temprano  
- Las historias priorizadas cubren el **flujo principal del dominio (reserva → uso → registro)**  
- Cada historia es testeable mediante escenarios (Gherkin en el siguiente ejercicio)
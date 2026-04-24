# Entrega ejercicio 02

## Objetivo de la entrega

Consolidar historias de usuario, escenarios Gherkin, modelo de dominio y prototipos Storybook alineados con el recorte funcional del ejercicio 2.

## Artefactos trabajados

- `docs/requisitos/historias-usuario.md`
- `docs/requisitos/modelo-dominio.md`
- `docs/requisitos/modelo-dominio.puml`
- `src/test/resources/features/HU-*.feature`
- `docs/ux/prototipo.md`
- `docs/ux/freeze.md`
- `docs/ux/storybook/`
- `docs/ux/exports/`

## Cambios principales
### Requisitos y dudas
- **Gestión de la Incertidumbre (D-01):** Se transformó la duda sobre canales de notificación en una decisión de diseño. Se priorizó el uso de email institucional y notificaciones in-app, moviendo el uso de WhatsApp a un estado de evaluación para su desestimación, profesionalizando así la comunicación.
- **Refinamiento de Requisitos:** Se actualizaron los RF y RNF basándose en las nuevas evidencias recolectadas, permitiendo que el sistema cubra el ciclo de vida completo de la reserva.
- **Formalización de Reglas (RN-06 y RN-07):** Se establecieron reglas concretas para la trazabilidad de inasistencias y se definió un modelo de acceso equitativo (First-Come, First-Served), simplificando la lógica de asignación.
- **Definición de Integración (D-04):** Se delimitó el alcance de la interacción con sistemas externos, estableciendo que el sistema institucional actuará únicamente como proveedor de identidad (Autenticación - RN-08). 
- **Incorporación del RF-10 (Registro de Asistencia):** Se agregó este requisito a partir de lo relevado en la **Entrevista P17**. El mismo permite al personal registrar el "check-in" de los estudiantes, cerrando el ciclo de uso de la sala y permitiendo ademas contabilizar las inasistencias.
- **RN-09**: Se formalizó la detección automática de ausencias. Establece que el sistema marca ausente toda reserva confirmada que no tenga check-in al vencer el turno. La definición operativa de cómo el personal gestiona llegadas tardías queda como duda abierta para la próxima iteración.
- **Ajuste de RN-03**: cuando una reserva pasa las reglas de negocio -> Es automaticamente confirmado
- **Incorporación de RN-10 (Unicidad de Reserva)**: Se formalizó la restricción que impide a un estudiante tener dos reservas activas para el mismo bloque horario. Esta decisión surgió del análisis de la Entrevista P15 para garantizar la equidad de acceso y evitar el acaparamiento de recursos (salas).
- **Trazabilidad**: Agregado de preguntas nuevas en entrevista.md, agregado temas tocados en la entrevista a minuta.md y trazabilidad en requisitos.md

### Artefactos ejercicio-02
- `docs/requisitos/historias-usuario.md`: 7 historias de usuario, solo 3 priorizadas para el flujo de trabajo HU-01,HU-03 Y HU-04
- `docs/requisitos/modelo-dominio.md`: Documentación de contexto que describe las entidades (Sala, Turno, Reserva, HistorialEstado), sus atributos y las decisiones de diseño, como la separación del historial para trazabilidad de estados.
- `docs/requisitos/modelo-dominio.puml`: Código fuente en PlantUML que representa la estructura estática del sistema, reflejando las multiplicidades y relaciones necesarias para soportar las Reglas de Negocio.
- `src/test/resources/features/HU-01.feature`: specificación ejecutable que detalla los criterios de aceptación para la reserva de salas, incluyendo validaciones de cupo, orden de llegada y restricciones de duplicidad.
- `src/test/resources/features/HU-03.feature`: Escenarios Gherkin para el flujo de cancelación y liberación automática de recursos.
- `src/test/resources/features/HU-04.feature`: Escenarios para el registro de asistencia (Check-in) y la detección automática de ausencias al finalizar el turno.
- `docs/ux/prototipo.md`: Documento explicativo de la estrategia de prototipado, detallando cómo el Storybook valida las hipótesis de UX y las reglas de negocio críticas.
- `docs/ux/freeze.md`: Registro del estado del diseño al momento de la entrega, garantizando la consistencia entre los requerimientos y la interfaz presentada.
- `docs/ux/storybook/HU-01-reserva.html`: Simulador funcional e interactivo desarrollado en HTML/JS. Permite conmutar entre diferentes estados del sistema para verificar el comportamiento de los escenarios Gherkin en tiempo real.
- `docs/ux/exports/HU-01-reserva-exitosa.png`: Evidencia del "Camino Feliz" (Happy Path) donde el sistema confirma una reserva y actualiza el historial de estados correctamente.
- `docs/ux/exports/HU-01-error-capacidad.png`: Evidencia visual del rechazo de reserva por cupo agotado en la Sala de Silencio B (RN-01).
- `docs/ux/exports/HU-01-error-duplicado-RN10.png`:Captura de la validación de la RN-10, impidiendo que un mismo estudiante reserve dos salas en el mismo bloque horario.
- `docs/ux/exports/HU-01-concurrencia-fcfs.png`:Evidencia del cumplimiento de la RN-07, demostrando que ante solicitudes simultáneas el sistema asigna el cupo por orden de llegada (First-Come, First-Served).

## Feedback incorporado

| Observación del ejercicio 1 | Artefacto de E2 | Qué cambió |
| :--- | :--- | :--- |
| El Onion Model no respeta la forma de cebolla y tiene flechas incorrectas. | `interesados.drawio` | Se rediseñó el diagrama utilizando **capas concéntricas** sin flechas de flujo. Se reubicó a **Soporte/IT** en *Our System* y las **Normativas** en *Wider Environment*. |
| El diagrama de historia de dominio es un diagrama de secuencia inconsistente. | `historia-dominio.egn` | Se ajustó la lógica para representar el **flujo del dominio** (eventos de negocio) y no una secuencia técnica de mensajes, asegurando consistencia con el Markdown. |
| Falta la parte del flujo donde el estudiante hace uso de la sala y se registra asistencia. | `historia-dominio.egn` | Se incorporaron los eventos de **Registro de Asistencia (Check-in)** y **Finalización de Uso**, completando el ciclo de vida del turno en el dominio. |
| Los Compromisos Asumidos de la minuta parecen restricciones o criterios. | `minuta.md` | Se reformularon los compromisos como **acuerdos de acción y próximos pasos** con responsables claros, diferenciándolos de los requisitos del sistema. |
| El sistema está dentro del registro de interesados. | `interesados.md` | Se eliminó el sistema de la tabla de interesados, manteniéndolo únicamente en el diagrama visual como el núcleo técnico (**The Kit**). |


## Pendientes
- Confirmación final de la exclusión de canales informales (WhatsApp). D-1
- Coordinación debe definir el número exacto de ausencias permitidas y la duración de la restricción antes de la próxima iteración.

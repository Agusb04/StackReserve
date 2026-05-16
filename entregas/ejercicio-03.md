# Entrega ejercicio 03

## Objetivo de la entrega

Implementar la primera versión backend del sistema de gestión de turnos a salas de una biblioteca académica con Spring Boot, arquitectura en capas clásica, API REST JSON y BDD ejecutable con Cucumber JVM. La implementación cubre las historias priorizadas del backlog y deja ejecutables todos los escenarios de sus `HU-xx.feature`.

## Artefactos trabajados

- `docs/requisitos/requisitos.md`
- `docs/requisitos/historias-usuario.md`
- `docs/requisitos/modelo-dominio.md`
- `docs/requisitos/modelo-dominio.puml`
- `docs/arquitectura/adr-001.md`
- `docs/arquitectura/c4-contexto.md`
- `docs/arquitectura/c4-contenedores.md`
- `docs/arquitectura/c4-componentes.md`
- `src/main/java/` — dominio, repositorios, servicios y controladores
- `src/test/java/` — CucumberIT, configuración, ScenarioState y step definitions
- `src/test/resources/features/` — HU-01 a HU-04 ejecutables
- `entregas/ejercicio-03.md`

## Cambios principales

- D-01 fue resuelta: Se usaran solo canales oficiales y no oficiales como Whatsapp o Telegram, todo trazado en los requisitos y la minuta.

### Trazabilidad UX y Storybook

Se incorporó completamente la trazabilidad de la Historia de Usuario 02 (consulta de disponibilidad) dentro de los artefactos UX congelados y del prototipo interactivo.

- `HU-02-disponibilidad.html` implementa los cuatro escenarios definidos en `HU-02.feature`:
  - salas con cupo disponible
  - salas parcialmente ocupadas
  - salas sin cupo
  - día sin turnos configurados

- `freeze.md` documenta explícitamente:
  - la existencia de la story funcional de HU-02
  - la relación 1:1 entre historia, feature y story
  - la trazabilidad entre reglas de negocio, modelo de dominio y UX
  - la cobertura de RN-01 y RN-02 en la consulta de disponibilidad

- `prototipo.md` incorpora:
  - HU-02 dentro de `## Historias y escenarios cubiertos`
  - descripción detallada de la pantalla de consulta de disponibilidad
  - barra de cupo dinámica, indicadores visuales y empty state
  - navegación desde el índice central de Storybook

- `docs/ux/storybook/index.html` fue actualizado para incluir navegación a las cuatro historias (`HU-01` a `HU-04`) organizadas por sección.

Con esto queda trazabilidad completa entre:
- historia priorizada
- archivo `.feature`
- story funcional
- prototipo UX
- freeze UX
- reglas de negocio implementadas en backend
- escenarios ejecutables de Cucumber

### Implementación backend

#### Dominio
Se implementaron los POJOs del dominio sin ORM: `Sala`, `Turno`, `Reserva`, `Estudiante` e `HistorialEstado`. La clase `Reserva` encapsula la transición de estados y el registro automático en el historial a través de `setEstado()`, que agrega una entrada a la lista de `HistorialEstado` en cada llamada.

#### Repositorios en memoria
Se implementaron `SalaRepository`, `ReservaRepository` y `EstudianteRepository` como repositorios con almacenamiento `HashMap` in-process. Esto elimina la necesidad de configurar una base de datos externa en esta iteración y permite que `./gradlew check` funcione sin variables de entorno adicionales.

#### Servicio
`ReservaService` orquesta todos los casos de uso priorizados:
- **Consultar disponibilidad** (HU-02, `@disponibilidad-salas`): calcula cupos disponibles por sala para una fecha dada restando reservas confirmadas a la capacidad máxima.
- **Reservar turno** (HU-01): valida cupo disponible (RN-01), evita solapamiento del mismo estudiante (RN-10) y confirma con historial.
- **Cancelar reserva** (HU-03, `@cancelacion-reserva`): valida titularidad, estado previo y libera el cupo del turno.
- **Check-in** (HU-04): registra presencia y detecta doble registro.
- **Ausencias automáticas** (HU-04): marca como ausentes las reservas confirmadas de turnos ya finalizados.

#### Controladores
`ReservaController` expone los endpoints:
- `POST /api/reservas` — crear reserva
- `POST /api/reservas/{codigo}/cancelar` — cancelar reserva
- `POST /api/reservas/{codigo}/check-in` — registrar asistencia
- `GET /api/salas/disponibilidad?fecha=` — consultar disponibilidad

`PingController` mantiene `GET /api/health` para el scaffold inicial.

#### Step definitions
Se implementaron los step definitions organizados en `ar.edu.fiuba.is1.tpi.bdd`:
- `CommonSteps` — setup de salas, estudiantes y steps de verificación compartidos (mensaje, rechazo)
- `ReservaSteps` — escenarios de HU-01
- `DisponibilidadSteps` — escenarios de HU-02
- `CancelacionSteps` — escenarios de HU-03
- `CheckInSteps` — escenarios de HU-04
- `HistorialSteps` — verificación de historial compartida entre features
- `ScenarioState` — contexto de escenario con scope `cucumber-glue` para compartir el `ResponseEntity` entre clases de steps sin duplicar steps de verificación

Los steps entran exclusivamente por los endpoints REST (`TestRestTemplate`) sin saltear capas.

#### Arquitectura documentada
Se completaron los tres diagramas C4 y el ADR-001 reflejando la arquitectura real implementada:
- `adr-001.md` — justifica el recorte backend con POJOs, HashMap repos y sin ORM para esta iteración
- `c4-contexto.md` — tres actores (Estudiante, Personal, Coordinación) y sistema de auth institucional como externo
- `c4-contenedores.md` — backend Spring Boot y persistencia en memoria como `ContainerDb`
- `c4-componentes.md` — separación Controller → Service → Repository → Domain con componentes reales

### Feedback corregido

#### Correcciones de sintaxis Gherkin
- **`HU-01.feature`**: Se corrigió `Escenario Esquema:` por `Esquema del escenario:` en el escenario FCFS.
- **`HU-04.feature`**: Mismo fix en el escenario parametrizado del historial de estados.

#### Reformulación del formato de historias de usuario
- **`historias-usuario.md`**: Se reordenaron todas las historias al formato `Para / Como / Quiero que`, poniendo el valor de negocio primero.

#### Criterio de calidad 1:1 HU ↔ feature
- **`historias-usuario.md`**: Se agregó en `## Criterios de calidad` la regla que establece que cada historia priorizada tiene exactamente un archivo `.feature` asociado.

#### Lenguaje impersonal en pasos de backend
- **`HU-01.feature`, `HU-03.feature`, `HU-04.feature`**: Se aplicó redacción impersonal en todos los pasos `Entonces`. Por ejemplo: `el sistema registra` → `se registra`, `el sistema rechaza` → `se rechaza`.

---

## Feedback incorporado

| Observación del ejercicio 2 | Artefacto de E3 | Qué cambió |
| :--- | :--- | :--- |
| `Escenario Esquema:` no es sintaxis válida en Gherkin; solo se acepta `Escenario:` o `Esquema del escenario:`. Error grave en HU-01 y HU-04. | `HU-01.feature`, `HU-04.feature` | Se reemplazó `Escenario Esquema:` por `Esquema del escenario:` en ambos archivos. |
| `historias-usuario.md` debería explicitar la regla 1:1 entre historia y feature en `## Criterios de calidad`. | `historias-usuario.md` | Se agregó el criterio: cada historia priorizada tiene exactamente un `.feature` asociado (relación 1:1 HU ↔ feature). |
| Falta dejar trazada HU-03 en `## Historias y escenarios cubiertos` de `prototipo.md`. | `prototipo.md` | Se agregaron las filas para HU-03 vinculando historia, escenario y story de Storybook. |
| Falta dejar trazada HU-04 en `## Historias y escenarios cubiertos` de `prototipo.md`. | `prototipo.md` | Se agregaron las filas para HU-04 vinculando historia, escenario y story de Storybook. |
| Falta la story de Storybook en `docs/ux/storybook/src` para HU-03. | `HU-03-cancelacion.html` | Se creó la story con simulador interactivo que cubre los 5 escenarios del feature. |
| Falta la story de Storybook en `docs/ux/storybook/src` para HU-04. | `HU-04-asistencia.html` | Se creó la story con simulador interactivo que cubre los 4 escenarios del feature. |
| Las historias deben iniciar con el objetivo de negocio (`Para`) y usar lenguaje impersonal en los resultados del backend. | `historias-usuario.md`, `HU-01.feature`, `HU-03.feature`, `HU-04.feature` | Se reordenaron todas las historias al formato `Para / Como / Quiero que` y se aplicó redacción impersonal en todos los pasos `Entonces`. |
| La continuidad con el feedback del ejercicio 01 no quedaba explícita artefacto por artefacto. | `ejercicio-03.md` | Se documenta explícitamente cada observación del ejercicio 02, el artefacto afectado y el cambio realizado en esta tabla. |

---

## Pendientes

- Reemplazar repositorios HashMap por Spring Data JPA con H2 o PostgreSQL en iteraciones futuras.
- Implementar autenticación real delegada al sistema institucional (RN-08), actualmente fuera de alcance.
- Agregar lógica de penalización por acumulación de ausencias una vez definido el umbral (D-02).
- Implementar endpoint de dashboard con métricas de ocupación y ausentismo (HU-06, fuera del recorte de esta iteración).
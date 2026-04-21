# Modelo de dominio conceptual

Este artefacto documenta el recorte conceptual del problema para las historias priorizadas del ejercicio 2. El modelo se mantiene en PlantUML como archivo editable.

## Archivo fuente y export visual

- archivo editable: `docs/requisitos/modelo-dominio.puml`
- export visual obligatoria: `docs/requisitos/modelo-dominio.svg` o `docs/requisitos/modelo-dominio.png`
- documento de contexto y trazabilidad: `docs/requisitos/modelo-dominio.md`
- no usar Mermaid inline, drawio ni una imagen suelta como base principal de este artefacto

## Alcance del modelo

Explicar qué recorte del dominio cubre este modelo en función del MVP del módulo y de las historias priorizadas. Aclarar qué parte del problema sí entra en la entrega y qué parte queda explícitamente afuera.

## Inputs usados

Listar y citar explícitamente, como mínimo:

- `docs/requisitos/requisitos.md`
- `docs/requisitos/historias-usuario.md`
- `src/test/resources/features/HU-*.feature`
- `docs/descubrimiento/historia-dominio.md` como apoyo optativo de la mejora de ejercicio 1

## Historias priorizadas de referencia

| Historia | Recorte que aporta al modelo | Escenarios o evidencia usada |
| --- | --- | --- |
| `HU-01` | ... | `src/test/resources/features/HU-01.feature`, requisitos, historia de dominio |

## Entidades conceptuales

Los actores humanos no se listan acá; quedan en historias y escenarios, no como clases del dominio.

| Entidad conceptual | Evidencia visible | Justificación breve |
| --- | --- | --- |
| `SolicitudReserva` | `HU-01`, historia de dominio | registra una petición persistida del flujo |
| `Reserva` | `HU-01`, `HU-02`, `HU-03` | es un objeto que el sistema debe conservar |
| `Turno` | `HU-01`, `HU-02` | es el recurso persistido que se asigna |
| `ReservaTurno` | `HU-02`, `HU-03` | clase asociativa para la vinculación con datos propios |

## Relaciones persistidas

| Relación | Multiplicidad | Evidencia visible |
| --- | --- | --- |
| `SolicitudReserva` - `Reserva` | `1` a `0..1` | `HU-01`, escenario principal |
| `Reserva` - `Turno` | `1` a `1` | `HU-01`, `HU-02`; puede modelarse con `ReservaTurno` si la relación guarda atributos |

## Atributos

| Entidad | Atributos simples | Evidencia visible |
| --- | --- | --- |
| `SolicitudReserva` | `codigo`, `fechaCreacion` | feature y requisito trazado |
| `Reserva` | `codigo`, `estado`, `fechaConfirmacion` | visión y reglas del módulo |
| `Turno` | `fechaHora`, `duracionMinutos` | disponibilidad y confirmación |
| `ReservaTurno` | `motivo`, `observacion` | datos propios de la relación |

## Diagrama

![Modelo de dominio](modelo-dominio.svg)

## Trazabilidad

| Elemento del modelo | Historias / features | Evidencia adicional |
| --- | --- | --- |
| `SolicitudReserva` | `HU-01`, `HU-01.feature` | requisitos, historia de dominio |
| `Reserva` | `HU-01`, `HU-02`, `HU-03` | recorte principal del flujo |
| `ReservaTurno` | `HU-02`, `HU-03` | clase asociativa si la relación necesita datos |

## Fuera de alcance y exclusiones

- no modelar actores humanos como clases del dominio; por ejemplo, no usar `Usuario`, `Alumno` ni `Docente` como entidades del dominio
- no usar verbos de proceso como nombre de una asociación; si la relación lleva nombre, debe describir un vínculo persistido
- no modelar endpoints, controllers, services, repositories, DTOs ni decisiones de arquitectura
- no modelar pantallas, widgets, botones ni navegación de UI
- no convertir esto en modelo ER, schema SQL ni diagrama de código
- no inventar clases o asociaciones sin evidencia visible en los inputs anteriores

## Dudas abiertas

- registrar conflictos entre historias, features, trazabilidad y visión
- si una relación necesita atributos propios, justificar clase asociativa o dejar la duda explícita
- anotar supuestos temporales o vocabulario todavía inestable

## Subset permitido de PlantUML

Usar `docs/requisitos/modelo-dominio.puml` con un diagrama de clases conceptual al estilo Larman. El subset permitido para este artefacto es:

- `class`
- atributos simples
- asociaciones con multiplicidad; si hace falta una etiqueta, debe nombrar el vínculo persistido y no una acción
- generalización solo si realmente aclara el dominio
- clase asociativa cuando la relación necesita atributos propios

Queda prohibido:

- métodos u operaciones
- visibilidades (`+`, `-`, `#`, `~`)
- `!include`
- `skinparam`
- interfaces, packages o estereotipos técnicos
- vocabulario de implementación como `Controller`, `Service`, `Repository`, `DTO`, `API`, `endpoint`, `tabla`, `screen` o `UI`

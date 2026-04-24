# Modelo de dominio conceptual

## Archivo fuente y export visual

- **Archivo editable:** `docs/requisitos/modelo-dominio.puml`
- **Export visual:** `docs/requisitos/modelo-dominio.png`
- **Documento de contexto:** `docs/requisitos/modelo-dominio.md`

---

## Alcance del modelo

Cubre el ciclo de vida de una reserva de sala de estudio desde su creación
hasta su resolución por uso efectivo, cancelación o ausencia.

**Incluye:** salas, turnos, reservas e historial de estados.


## Fuera de alcance y exclusiones 
- **Excluye:** autenticación de usuarios (RN-08), estudiantes y personal
como entidades del dominio, lógica de penalizaciones (D-02), navegación de UI.
---

## Inputs usados

- `docs/requisitos/requisitos.md`
- `src/test/resources/features/HU-02.feature`
- `src/test/resources/features/HU-03.feature`
- `src/test/resources/features/HU-04.feature`

---

## Historias priorizadas de referencia

| Historia | Recorte que aporta al modelo | Escenarios o evidencia usada |
| :--- | :--- | :--- |
| **HU-01** | Estructura de recursos y validación de capacidad | Reserva exitosa, capacidad máxima, FCFS |
| **HU-03** | Ciclo de vida y liberación de cupo | Cancelación, liberación de turno, estados |
| **HU-04** | Trazabilidad de asistencia y ausencia automática | Check-in, marcado automático, historial |

---

## Entidades conceptuales

| Entidad | Evidencia | Justificación |
| :--- | :--- | :--- |
| `Sala` | HU-01, RF-08 | Recurso físico con nombre y capacidad máxima definida |
| `Turno` | HU-01, RN-02 | Intervalo de tiempo asociado a una sala; unidad reservable |
| `Reserva` | HU-01, HU-03, HU-04 | Transacción central que vincula un estudiante con un turno |
| `HistorialEstado` | HU-04, RN-06, RN-09 | Registro persistido de cada transición de estado de una reserva |

---

## Atributos

| Entidad | Atributos | Evidencia |
| :--- | :--- | :--- |
| `Sala` | `codigo {id}`, `nombre`, `capacidadMaxima` | RF-08, HU-01 ("Sala de Multimedia A", cap. 6) |
| `Turno` | `fecha`, `horaInicio`, `horaFin` | RN-02, HU-02 (27/04/2026 10:00 a 11:00) |
| `Reserva` | `codigo {id}`, `estado`, `fechaCreacion` | RN-03, HU-01, HU-03 (RES-0892, "confirmada") |
| `HistorialEstado` | `fechaRegistro`, `estado` | HU-04, RN-06, RN-09 ("presente", "ausente", "cancelada") |

---

## Relaciones persistidas

| Relación | Multiplicidad | Evidencia |
| :--- | :--- | :--- |
| `Sala` tiene `Turno` | 1 a 0..* | Una sala expone múltiples bloques horarios (RN-02) |
| `Turno` agrupa `Reserva` | 1 a 0..* | Un turno acumula reservas hasta agotar la capacidad de la sala (RN-01) |
| `Reserva` registra `HistorialEstado` | 1 a 1..* | Toda reserva tiene al menos un estado desde su creación (RN-06, RN-09) |

---

## Decisiones de modelado

**Capacidad en `Sala`, no en `Turno`:**
La capacidad es una propiedad física de la sala. El cupo disponible
para un turno se deriva contando las reservas activas contra la
`capacidadMaxima` de la sala. Esto evita inconsistencias si la
configuración de la sala cambia. Evidencia: RF-08, HU-01.

**`HistorialEstado` como entidad separada:**
Se modela como entidad propia y no como atributo de `Reserva` para
registrar múltiples transiciones con su timestamp. Soporta RN-06
(trazabilidad de inasistencias) y RN-09 (marcado automático de ausencia
al finalizar el turno sin check-in).

**Actores excluidos del modelo:**
Estudiante y Personal no son entidades del dominio. El sistema delega
la identidad al sistema institucional (RN-08). Aparecen únicamente
como actores en las historias y escenarios Gherkin.

**Estados de `Reserva` e `HistorialEstado`:**
El atributo `estado` se mantiene como valor libre en ambas entidades.
Los valores válidos del dominio son `confirmada`, `cancelada`, `presente`
y `ausente`, definidos por los escenarios de HU-01, HU-03 y HU-04.
---

## Trazabilidad

| Entidad | Historias | Requisitos |
| :--- | :--- | :--- |
| `Sala` | HU-01 | RF-08, RN-01, RN-02 |
| `Turno` | HU-01 | RN-02 |
| `Reserva` | HU-01, HU-03, HU-04 | RN-01, RN-03, RN-04, RN-06 |
| `HistorialEstado` | HU-04 | RN-06, RN-09 |

---

## Dudas abiertas

| ID | Duda | Estado |
| :--- | :--- | :--- |
| D-02 | ¿Se aplicarán penalizaciones por ausencias acumuladas? | Abierta. El `HistorialEstado` está modelado para soportarlo cuando coordinación defina el umbral. |

---
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
## Diagrama

![Modelo de dominio](modelo-dominio.png)
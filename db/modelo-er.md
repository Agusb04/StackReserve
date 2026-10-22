# Modelo ER Corregido

## Alcance del modelo

Este modelo lógico describe el respaldo de datos para el flujo de pruebas y la API del sistema de reservas de salas de estudio. Está orientado a persistir y dar trazabilidad a reservas, turnos, auditoría de asistencia y penalizaciones latentes según el ciclo de vida descripto en el modelo de dominio.

Incluye entidades, relaciones, atributos y correspondencias técnicas de tablas y claves relacionales (PK, FK), ajustado de acuerdo con la implementación actual en memoria y la proyección con JPA. El esquema SQL de referencia está en `db/schema.sql`.

---

## Entidades y atributos

### Sala
- **PK:** id_sala (int, autoincremental)
- **UK:** codigo (string) -- *Ajuste: Clave de negocio requerida por el dominio para búsquedas y mapeo con la API*
- nombre (string)
- capacidad_maxima (int)

### Turno
- **PK:** id_turno (int, autoincremental)
- **FK:** id_sala → sala(id_sala)
- fecha (date)
- hora_inicio (time)
- hora_fin (time)

> **Nota de implementación:** `Turno` se modela como entidad independiente en el DER para
> normalizar la relación Sala–Reserva. En la implementación actual, el turno se persiste como
> atributos embebidos de `Reserva` y no tiene repositorio propio. Se materializará como tabla
> separada en la iteración con JPA.

### Estudiante
- **PK:** legajo (string)
- nombre (string)
- estado_habilitacion (string)

### Personal
- **PK:** legajo (string)
- nombre (string)

> **Nota de implementación:** `Personal` se incluye como entidad prospectiva. En la implementación
> actual el operario se persiste como `String` en `HistorialEstado` sin repositorio propio.
> Se materializará como entidad con repositorio cuando se implementen penalizaciones y validación
> de operarios autorizados (D-02, RN-08).

### Reserva
- **PK:** id_reserva (string) -- *Ajuste: Se mantiene el tipo String para albergar el código de negocio con formato `RES-XXXX`*
- **FK:** legajo_estudiante → estudiante(legajo)
- **FK:** id_turno → turno(id_turno)
- fecha_creacion (datetime)

> **Nota de implementación:** `id_reserva` en la capa relacional corresponde a `codigo` (`RES-XXXX`)
> en el dominio Java, generado con UUID parcial al momento de la creación.

### HistorialEstado
- **PK:** id_historial (int, autoincremental)
- **FK:** id_reserva → reserva(id_reserva)
- **FK (prospectiva, nullable):** legajo_personal → personal(legajo) -- *Ajuste: Columna Nullable. Debe permitir nulos ya que las acciones automáticas registran "SISTEMA" o son iniciadas por el Estudiante*
- fecha_registro (datetime)
- estado (string)

---

## Relaciones persistidas

1. Una Sala tiene muchos Turnos. (1:N, obligatorio para el Turno)
2. Un Turno puede tener muchas Reservas. (1:N, opcional para el Turno)
3. Un Estudiante puede tener muchas Reservas. (1:N, opcional para el Estudiante)
4. Una Reserva tiene uno o muchos registros en HistorialEstado. (1:N, obligatorio, la reserva nace con al menos un estado de forma mandatoria)
5. Un Personal puede auditar muchos HistorialEstado. (1:N, prospectiva y opcional para el HistorialEstado, permitiendo nulos)

---

## Correspondencia DER

| Tabla            | Columna              | Clave | Descripción / Origen en dominio          | Correspondencia técnica             | Justificación                                                                    |
|------------------|---------------------|-------|------------------------------------------|--------------------------------------|----------------------------------------------------------------------------------|
| sala             | id_sala             | PK    | Sala (ID Técnico)                        | sala.id_sala                         | Identificador subrogado para persistencia eficiente y autonúmerica               |
| sala             | codigo              | UK    | Sala (Código de Negocio)                 | sala.codigo                          | Código único (ej. "S01") usado para identificar las salas en los controladores    |
| sala             | nombre              |       | Sala                                     | sala.nombre                          | Identificación legible del recurso                                               |
| sala             | capacidad_maxima    |       | Sala                                     | sala.capacidad_maxima                | Restricción fija del recurso físico                                              |
| turno            | id_turno            | PK    | Turno                                    | turno.id_turno                       | Identificador único de turno (prospectivo; hoy embebido en Reserva)             |
| turno            | id_sala             | FK    | Turno → Sala                             | sala.id_sala                         | Turno asignado a una sola sala física                                             |
| turno            | fecha               |       | Turno                                    | turno.fecha                          | Bloque diario calendarizado                                                      |
| turno            | hora_inicio         |       | Turno                                    | turno.hora_inicio                    | Inicio del bloque horario                                                        |
| turno            | hora_fin            |       | Turno                                    | turno.hora_fin                       | Fin del bloque horario                                                           |
| estudiante       | legajo              | PK    | Estudiante                               | estudiante.legajo                    | Identificador único institucional                                                |
| estudiante       | nombre              |       | Estudiante                               | estudiante.nombre                    | Identificación por nombre                                                        |
| estudiante       | estado_habilitacion |       | Estudiante                               | estudiante.estado_habilitacion       | Estado del estudiante (ACTIVO/SUSPENDIDO)                                        |
| personal         | legajo              | PK    | Personal                                 | personal.legajo                      | Identificador del personal (prospectivo; hoy el operario se guarda como String)  |
| personal         | nombre              |       | Personal                                 | personal.nombre                      | Nombre del operario/auditor                                                      |
| reserva          | id_reserva          | PK    | Reserva                                  | reserva.codigo (RES-XXXX)            | Identificador de transacción; en dominio Java es String tipo RES-XXXX           |
| reserva          | legajo_estudiante   | FK    | Reserva → Estudiante                     | estudiante.legajo                    | Asocia la reserva con el estudiante titular                                      |
| reserva          | id_turno            | FK    | Reserva → Turno                          | turno.id_turno                       | Reserva ligada a un turno específico (prospectivo; hoy embebido)                |
| reserva          | fecha_creacion      |       | Reserva                                  | reserva.fecha_creacion               | Marca temporal de creación del registro                                          |
| historial_estado | id_historial        | PK    | HistorialEstado                          | historial_estado.id_historial        | ID único autonumérico del evento de estado                                       |
| historial_estado | id_reserva          | FK    | HistorialEstado → Reserva                | reserva.id_reserva                   | Vincula la transición histórica a su respectiva reserva                          |
| historial_estado | fecha_registro      |       | HistorialEstado                          | historial_estado.fecha_registro      | Fecha y hora exacta del evento de cambio                                         |
| historial_estado | estado              |       | HistorialEstado                          | historial_estado.estado              | Estado registrado: confirmada, presente, ausente, cancelada                      |
| historial_estado | legajo_personal     | FK    | Personal audita → HistorialEstado (NULL) | personal.legajo                      | Auditoría operativa (prospectiva; nullable para soportar acciones de "SISTEMA")  |

Reglas actualizadas:
- Las entidades asociativas (`reserva`, `historial_estado`) usan PK propia, no compuesta, por requerimiento de trazabilidad lineal.
- La columna `legajo_personal` en `historial_estado` es **nullable** para reflejar que existen transiciones automáticas (ej: procesamiento de ausencias por el sistema) o cancelaciones hechas por el propio estudiante.
- `sala.codigo` posee una restricción de unicidad (`UNIQUE`) para asegurar la correspondencia exacta con el código de negocio actual (`String codigo`).
- Una `reserva` requiere obligatoriamente al menos un `historial_estado` desde su inserción (forzado por la lógica del dominio).

## Columnas DER no mapeadas actualmente en el modelo Java

En la implementación previa estas columnas no estaban mapeadas. Tras los cambios recientes
la situación es la siguiente:

- `reserva.fecha_creacion` (datetime): originalmente no estaba persistida en `Reserva.java`.
  Esta columna ahora está representada en la implementación como `fechaCreacion` (LocalDateTime)
  inicializada en el constructor de `Reserva`. Por lo tanto ya no forma parte del backlog
  de mapeo para la migración a JPA.
- `historial_estado.id_historial` (int autoincremental): el DER define un PK autonumérico
  para los eventos de historial. En la implementación actual `HistorialEstado` sigue sin exponer
  un id; el orden cronológico se garantiza por `fechaRegistro`. En persistencia relacional
  se recomienda materializar `id_historial` como columna @Id generada para facilitar
  referenciación y consultas por rango.

Estas ausencias son intencionales: se mantiene el modelo en memoria simple mientras se
preserva la visión relacional y la trazabilidad requerida por el DER.

---

## Trazabilidad

| Elemento DER    | Historia / feature  | Evidencia de uso                                                           |
|-----------------|---------------------|----------------------------------------------------------------------------|
| Sala            | HU-01, HU-02        | Reservar, consulta de opciones y gestión de cupos físicos de espacio       |
| Turno           | HU-01, HU-02        | Selección calendarizada de bloques temporales                              |
| Estudiante      | HU-01, HU-03, HU-04 | Reserva, penalización, asistencia                                          |
| Personal        | HU-04, HU-07        | Auditoría de asistencia (prospectivo)                                      |
| Reserva         | HU-01, HU-03, HU-04 | Vincula estudiante, turno y estado; permite consulta, creación, cancelación |
| HistorialEstado | HU-04, RN-06, RN-09 | Da trazabilidad completa del ciclo de reserva                              |

---

```mermaid
erDiagram
  SALA {
    int id_sala PK
    string codigo UK
    string nombre
    int capacidad_maxima
  }

  TURNO {
    int id_turno PK
    int id_sala FK
    date fecha
    time hora_inicio
    time hora_fin
  }

  ESTUDIANTE {
    string legajo PK
    string nombre
    string estado_habilitacion
  }

  PERSONAL {
    string legajo PK
    string nombre
  }

  RESERVA {
    string id_reserva PK
    string legajo_estudiante FK
    int id_turno FK
    datetime fecha_creacion
  }

  HISTORIAL_ESTADO {
    int id_historial PK
    string id_reserva FK
    string legajo_personal FK
    datetime fecha_registro
    string estado
  }

  SALA ||--o{ TURNO : tiene
  TURNO ||--o{ RESERVA : incluye
  ESTUDIANTE ||--o{ RESERVA : realiza
  RESERVA ||--|{ HISTORIAL_ESTADO : registra
  PERSONAL |o--o{ HISTORIAL_ESTADO : audita

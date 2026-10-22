# Freeze UX

## Link read-only

- **Punto de entrada:** http://localhost:8080/docs/ux/storybook/index.html
- **Estado:** Congelado para entrega TPI — Ejercicio 04.

---

## Storybook congelado

- `docs/ux/storybook/index.html`: Punto de entrada central con navegación por historia priorizada y sección (Gestión de reservas / Operación del mostrador).
- `docs/ux/storybook/src/HU-01-reserva.html`: Story funcional que implementa la lógica de reserva de salas, validación de cupos y restricciones de usuario (RN-01, RN-07, RN-10).
- `docs/ux/storybook/src/HU-02-disponibilidad.html`: Story funcional que implementa la consulta de disponibilidad de salas para un día determinado, con los cuatro escenarios del feature: salas libres, parcialmente ocupadas, sin cupo y sin salas registradas (RF-01, RN-01, RN-02).
- `docs/ux/storybook/src/HU-03-cancelacion.html`: Story funcional que implementa el flujo de cancelación de reservas, liberación de cupo y validaciones de titularidad y estado (RN-04, RN-06).
- `docs/ux/storybook/src/HU-04-asistencia.html`: Story funcional que implementa el registro de asistencia (check-in), el marcado automático de ausencias al vencer el turno y la consulta del historial de estados (RN-06, RN-09).

---

## Export congelado

Los siguientes archivos representan la evidencia estática del funcionamiento del sistema en sus estados críticos y se encuentran en `docs/ux/exports/`:

- `HU-01-reserva-exitosa.png`: Captura del flujo positivo donde se confirma la reserva de Lucas Martínez en la Sala Multimedia A.
- `HU-01-error-capacidad.png`: Evidencia del rechazo automático por capacidad máxima agotada (4/4) en la Sala Silencio B (RN-01).
- `HU-01-error-duplicado-RN10.png`: Captura de la validación que impide a un estudiante poseer dos reservas simultáneas (RN-10).
- `HU-01-concurrencia-fcfs.png`: Demostración de la asignación de turnos por orden de llegada (RN-07).

---

## Capturas congeladas
- ![Captura 1](docs/ux/exports/HU-01-concurrencia-fcfs.png)
- ![Captura 2](docs/ux/exports/HU-01-error-capacidad.png)
- ![Captura 3](docs/ux/exports/HU-01-error-duplicado-RN10.png)
- ![Captura 4](docs/ux/exports/HU-01-reserva-exitosa.png)

## Consistencia verificada

El prototipo ha sido validado y congelado asegurando trazabilidad total entre los siguientes artefactos:

### 1. Historias priorizadas ↔ Features ↔ Stories (1:1:1)

| Historia | Feature | Story |
| :--- | :--- | :--- |
| HU-01 | `HU-01.feature` | `HU-01-reserva.html` |
| HU-02 | `HU-02.feature` | `HU-02-disponibilidad.html` |
| HU-03 | `HU-03.feature` | `HU-03-cancelacion.html` |
| HU-04 | `HU-04.feature` | `HU-04-asistencia.html` |

Cada escenario definido en los archivos `.feature` tiene su contraparte interactiva en la story correspondiente, usando los mismos datos de entrada (`RES-0892`, `RES-0901`, `EST-1042`, `EST-2087`, `SALA-A`, `SALA-B`) y los mismos mensajes de salida definidos en los pasos `Entonces`.

### 2. Modelo de dominio ↔ Prototipo

Los simuladores no son meramente visuales; gestionan la lógica de las entidades del dominio:

- **`Sala`**: respeta el atributo `capacidadMaxima` en HU-01 y HU-02. La story de HU-02 calcula el cupo disponible como `capacidadMaxima − reservas confirmadas` (RN-01).
- **`Reserva`**: transiciona entre estados `confirmada → cancelada`, `confirmada → presente`, `confirmada → ausente` según las reglas de negocio.
- **`HistorialEstado`**: cada transición queda registrada en el log con timestamp, código de reserva y motivo, simulando la persistencia definida en el modelo.
- **`Estudiante`**: identificado por legajo (`EST-1042`, `EST-2087`), vinculado a sus reservas en HU-04.
- **`Personal`**: Es el que realiza el check in (HU-04) y ademas se persiste para auditoria en futuras iteraciones.

### 3. Reglas de negocio ↔ UX

| Regla | Story que la implementa | Cómo se verifica |
| :--- | :--- | :--- |
| RN-01 — Capacidad máxima | HU-01, HU-02 | Tarjeta de sala en rojo y rechazo al reservar sala llena; barra de cupo en HU-02 |
| RN-02 — Turno asociado a sala y horario | HU-02 | Cada sala muestra su cupo para la fecha consultada |
| RN-04 — Liberación de cupo | HU-03 | Barra de cupo se actualiza al cancelar; turno queda disponible |
| RN-06 — Trazabilidad de estados | HU-03, HU-04 | Log de historial registra cada transición con timestamp |
| RN-07 — FCFS | HU-01 | Botones con marca temporal; el segundo usuario es rechazado |
| RN-09 — Ausencia automática | HU-04 | Botón de fin de turno dispara marcado automático de ausente |
| RN-10 — Unicidad de reserva | HU-01 | Bloqueo al intentar reservar dos salas en el mismo horario |

### 4. Trazabilidad de lenguaje

Cada mensaje de feedback (alertas de éxito, errores y entradas de log) coincide con las cláusulas `Entonces` definidas en los archivos Gherkin. Los términos del dominio (`confirmada`, `cancelada`, `presente`, `ausente`) son consistentes entre features, modelo de dominio y prototipo.

### 5. Feedback ejercicio 02 incorporado

- Se agregaron las stories de HU-02, HU-03 y HU-04 que faltaban en la entrega anterior.
- Se trazaron HU-02, HU-03 y HU-04 en `prototipo.md` bajo `## Historias y escenarios cubiertos`.
- El `index.html` fue actualizado con navegación por sección y links a las cuatro stories.
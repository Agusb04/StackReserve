# Entrega ejercicio 04

## Objetivo de la entrega

Cerrar una primera versión end-to-end del sistema de gestión de turnos a salas. La entrega mantiene trazabilidad entre descubrimiento, requisitos, modelado, UX, BDD y TDD, arquitectura de capas, API REST y el modelo ER. 

## Artefactos trabajados

- `docs/descubrimiento/entrevista.md`;
- `docs/descubrimiento/minuta.md`;
- `docs/descubrimiento/interesados.md`;
- `docs/descubrimiento/historia-dominio.md`;
- `docs/requisitos/requisitos.md`;
- `docs/requisitos/historias-usuario.md`;
- `docs/requisitos/modelo-dominio.md`;
- `docs/requisitos/modelo-dominio.puml`;
- `docs/ux/prototipo.md`;
- `docs/ux/freeze.md`;
- `docs/arquitectura/c4-contexto.md`;
- `docs/arquitectura/c4-contenedores.md`;
- `docs/arquitectura/c4-componentes.md`;
- `docs/arquitectura/adr-001.md`;
- `docs/arquitectura/api-rest.md`;
- `db/modelo-er.md`;
- `db/schema.sql`;
- `src/main/java/ar/edu/fiuba/is1/tpi/config/AppConfig.java`;
- `src/main/java/ar/edu/fiuba/is1/tpi/controller/AuxiliarAltaController.java`;
- `src/main/java/ar/edu/fiuba/is1/tpi/controller/GlobalExceptionHandler.java`;
- `src/main/java/ar/edu/fiuba/is1/tpi/controller/ReservaController.java`;
- `src/main/java/ar/edu/fiuba/is1/tpi/domain/Estudiante.java`;
- `src/main/java/ar/edu/fiuba/is1/tpi/domain/HistorialEstado.java`;
- `src/main/java/ar/edu/fiuba/is1/tpi/domain/Personal.java`;
- `src/main/java/ar/edu/fiuba/is1/tpi/domain/Reserva.java`;
- `src/main/java/ar/edu/fiuba/is1/tpi/domain/Sala.java`;
- `src/main/java/ar/edu/fiuba/is1/tpi/domain/Turno.java`;
- `src/main/java/ar/edu/fiuba/is1/tpi/repository/EstudianteRepository.java`;
- `src/main/java/ar/edu/fiuba/is1/tpi/repository/ReservaRepository.java`;
- `src/main/java/ar/edu/fiuba/is1/tpi/repository/SalaRepository.java`;
- `/src/main/java/ar/edu/fiuba/is1/tpi/service/AuxiliarAltaService.java`;
- `/src/main/java/ar/edu/fiuba/is1/tpi/service/ReservaService.java`;
- `src/main/java/ar/edu/fiuba/is1/tpi/service/dto/AltaSalaRequest.java`;
- `src/main/java/ar/edu/fiuba/is1/tpi/service/dto/AltaEstudianteRequest.java`;
- `src/main/java/ar/edu/fiuba/is1/tpi/service/dto/CrearReservaRequest.java`;
- `src/test/java/ar/edu/fiuba/is1/tpi/bdd/CancelacionSteps.java`;
- `src/test/java/ar/edu/fiuba/is1/tpi/bdd/CheckInSteps.java`;
- `src/test/java/ar/edu/fiuba/is1/tpi/bdd/CommonSteps.java`;
- `src/test/java/ar/edu/fiuba/is1/tpi/bdd/DisponibilidadSteps.java`;
- `src/test/java/ar/edu/fiuba/is1/tpi/bdd/FlujoApiSteps.java`;
- `src/test/java/ar/edu/fiuba/is1/tpi/bdd/HistorialSteps.java`;
- `src/test/java/ar/edu/fiuba/is1/tpi/bdd/ReservaSteps.java`;
- `src/test/java/ar/edu/fiuba/is1/tpi/bdd/ScenarioState.java`;
- `src/test/resources/features/HU-01.feature`;
- `src/test/resources/features/HU-02.feature`;
- `src/test/resources/features/HU-03.feature`;
- `src/test/resources/features/HU-04.feature`;
- `src/test/resources/features/ejercicio-04-flujo-api.feature`;
- `src/test/java/ar/edu/fiuba/is1/tpi/domain/ReservaTest.java`;
- `src/test/java/ar/edu/fiuba/is1/tpi/domain/ValidacionesDominioTest.java`;
- `src/test/java/ar/edu/fiuba/is1/tpi/service/ReservaServiceTest.java`;
- `src/test/java/ar/edu/fiuba/is1/tpi/StorybookResourceTest.java`;
- `entregas/ejercicio-04.md`.

## Cambios principales

### Modelo de dominio

- **Cierre de Brechas de Dominio (Auditoría y Penalizaciones):** Se incorporaron cambios críticos requeridos por los escenarios avanzados del Ejercicio 3:
  * **Entidad Personal e Historial:** Se introdujo formalmente la entidad `Personal` (ej. operarios de biblioteca) para dar soporte a la auditoría estricta de asistencia ante reclamos (`HU-07`). Se vinculó al `HistorialEstado` de forma opcional/nullable para permitir la coexistencia de marcas presenciales hechas por operarios físicos y mutaciones automáticas ejecutadas por el `"SISTEMA"` (como el procesamiento en lote de ausencias de la `HU-04`).
  * **Soporte Relacional para Penalizaciones:** Para resolver la duda abierta (`D-02`) sobre ausencias acumuladas, se dotó a la entidad `Estudiante` del atributo `estado_habilitacion`. Esto permite penalizar y suspender cuentas de alumnos de manera directa en el dominio, sirviendo como control mandatorio previo a la confirmación de nuevas reservas (`HU-01`).

- **modelo-dominio.md**: Se agrego HU-02 en la tabla para mantener trazabilidad, se agrego la entidad Personal como necesidad de persistirlo para auditoria en un futuro, ademas con respecto a D-02: El dominio provee soporte estructural directo mediante el campo `estadoHabilitacion` de `Estudiante` y el registro de ausencias automáticas de `HistorialEstado`, listos para actuar cuando la coordinación fije la lógica reglamentaria.
- **modelo-dominio.png**: Actualizado con la ultima version
- **modelo-dominio.puml**: Se agrego el personal, estadoHabilitacion en estudiante y la relacion persistida: Personal "1" -- "0..*" HistorialEstado : audita para en un futuro implementar las auditorias con la HU-07

### Historias de usuario
- **historias-usuario.md**: Se agrego a **HU-01**: el tag canonico `@creacion-reserva` y a **HU-04**: El tag canonico `@registrar-asistencia`

### Modelo ER
A nivel arquitectónico y de persistencia, se diseñó formalmente el **Modelo Entidad-Relación Lógico (ER)** como paso previo y necesario para la futura migración a Spring Data JPA. El modelo fue normalizado respetando estrictamente las restricciones del dominio y la API implementada:

* **Normalización de Bloques Horarios (`TURNO`):** Aunque en la implementación actual en memoria el objeto `Turno` se comporta de forma embebida dentro de `Reserva`, en el modelo relacional se independizó como entidad propia vinculada a `SALA`. Esto elimina la redundancia de datos horaria y prepara al sistema para realizar consultas relacionales indexadas en lugar de costosos filtrados de Streams en memoria.
* **Modelo Lineal de Trazabilidad:** Las tablas asociativas de transacciones (`RESERVA` e `HISTORIAL_ESTADO`) fueron dotadas de claves primarias subrogadas individuales (`id_reserva` e `id_historial`) en lugar de claves compuestas, garantizando una auditoría secuencial, cronológica y limpia de los cambios de estado solicitada por el endpoint de historial.
* **Convivencia de Claves de Negocio y Técnicas:** En la tabla `SALA`, se incorporó una restricción de unicidad (`UK - Unique Key`) sobre la columna `codigo`. Esto permite utilizar un `id_sala (int)` como PK interna óptima de base de datos, garantizando al mismo tiempo a nivel relacional que no se dupliquen códigos lógicos de negocio (`"S01"`, `"S101"`) validados en el controlador de altas.
* **Opcionalidad en Auditoría de Estados:** Se incorporó el concepto de nulidad (`Nullable`) en la clave foránea `legajo_personal` dentro de la tabla `HISTORIAL_ESTADO` del DER, para mapear exactamente el comportamiento de la clase `HistorialEstado.java`, la cual por defecto registra transiciones como `"SISTEMA"` ante procesos automatizados (como `procesarAusencias`) o acciones directas del estudiante sin mediación de personal físico.
* **Ajuste de Cardinalidad de Ciclo de Vida:** Se actualizó la relación entre `RESERVA` e `HISTORIAL_ESTADO` a obligatoria de uno a muchos (`||--|{`), reflejando que una reserva no puede existir de forma huérfana en la persistencia relacional sin heredar de forma mandatoria un estado inicial (`"confirmada"`) desde el momento de ejecución de su constructor.

### Arquitectura
- **adr-001.md** y **c4-componentes**: Se agrego la documentacion de AuxiliarAltaService y AuxiliarAltaController necesarias para la creacion de salas y estudiantes. Inclusion de todos los tag canonicos `@disponibilidad-salas`, `@cancelacion-reserva`,`@creacion-reserva` y `@registrar-asistencia`.

### Prototipos
- **freeze.md** y **prototipo.md**: Se agregaron capturas de los exports, se mantuvo trazabilidad con el modelo de dominio actualizado y las Historias de usuario (HU-01.feature, HU-02.feature, HU-03.feature Y HU-04.feature).
- **docs/ux/storybook/src/HU-04-asistencia.html**: El personal se persiste con la nueva actualizacion de mi modelo de dominio.
- **index.html**: Se agregaron las 4 historias de usuarios navegables y consistentes.

### Features
**Evolución del Enfoque BDD (Feedback de Reglas de Negocio Incorporado):** Se reescribieron por completo los archivos de características (`.feature`) para subsanar observaciones previas. Se eliminaron las declaraciones imperativas y restrictivas orientadas al software ("*el sistema debe dar un error*"). En su lugar, mediante la palabra clave `Regla:`, se formularon principios lógicos basados puramente en el comportamiento del dominio (ej. la reintegración inmediata de inventario tras una cancelación o las restricciones horarias simultáneas). Esto asegura especificaciones vivas enfocadas en el negocio, perfectamente acopladas a los mensajes y validaciones que arrojan las excepciones de la capa de servicios de la aplicación.

## Codigo java
* **Hitos de Calidad de Código y Cumplimiento Estático (SpotBugs 0 Hallazgos):** Se realizaron refactorizaciones estrictas sobre el Modelo de Dominio y la suite de pruebas para erradicar por completo las advertencias de análisis estático:
  - Encapsulación Defensiva
  * **Invariabilidad Regional:** Se parametrizó taxativamente el uso de `Locale.ROOT` en todas las operaciones de formateo y mutación de cadenas de texto (`.toUpperCase()`), blindando al sistema contra fallas de localización intermitentes en servidores de integración continua.
* **Desacoplamiento mayoritario en Pruebas de Aceptación (BDD de Caja Negra):** Siguiendo las directrices metodológicas de la cátedra, la mayoría de los steps de Cucumber interactúan como clientes externos a través de la superficie pública REST/JSON mediante `TestRestTemplate` y validan respuestas con DTOs tipados. Hay una excepción deliberada: algunos pasos de precondición (`Given`) utilizan helpers que manipulan repositorios en memoria para preparar estados complejos de prueba (esto está documentado en `docs/arquitectura/adr-001.md`). Esta decisión es pragmática y está indicada para ser migrada en iteraciones futuras hacia llamadas a la API o endpoints de test.
* Nota de Diseño sobre Disponibilidad Diaria (HU-02): Se optó conscientemente por un enfoque de inventario integrado por fecha en el endpoint /api/salas/disponibilidad. Las reservas se debitan del total de la capacidad diaria de la sala. Esto mitiga la complejidad transaccional en memoria y garantiza consistencia directa con las aserciones lógicas del BDD, delegando el solapamiento estricto por bloques horarios individuales a las validaciones mandatorias de la capa de servicio (countByTurnoYSala) al momento de efectuar el POST.

### Cambios de bajo nivel en código (nota técnica)

- Reserva.getHistorial() ahora devuelve una copia inmutable para evitar exponer la lista interna mutable.
- Se añadió `Locale.ROOT` donde correspondía para normalización de strings (toUpperCase/toLowerCase).
- Manejo de parseo: los parseos de fecha/hora en `ReservaService.crearReserva` capturan `DateTimeParseException` y la normalizan a `IllegalArgumentException` para que el controlador devuelva `400 Bad Request` en entradas inválidas.
- Generación segura de legajo para `Personal` en `checkIn`: reemplazamos la lógica insegura con `LEG-XXX` derivado del hash (evita substring inseguro).
- Repositorios en memoria: `SalaRepository` y `EstudianteRepository` cambiaron su almacenamiento interno a `ConcurrentHashMap` para reducir posibilidades de condition-race en accesos concurrentes (nota: `ReservaRepository` mantiene sincronización en métodos críticos).
- Se añadieron tests/steps y actualizaciones de BDD para mantener trazabilidad sobre los cambios anteriores.

### Endurecimiento de API y validación (Ejercicio 04)

- Se reemplazaron los `Map` genéricos por DTOs con Bean Validation en altas y creación de reservas.
- Se agregó `GlobalExceptionHandler` para devolver `400 text/plain` ante validaciones, recursos inexistentes, errores de formato y reglas de negocio.
- `ReservaController` ya no decodifica parámetros manualmente ni captura excepciones por endpoint; delega en el handler global.
- `ReservaService` usa inyección por constructor y `Clock` inyectado. `procesarAusencias` acepta fecha/hora opcionales para pruebas deterministas.
- `AppConfig` publica el Storybook bajo `/docs/**` y provee el `Clock`.
- Se agregaron tests unitarios de dominio y servicio; la suite BDD cubre creación, disponibilidad, cancelación, check-in, ausencias e historial.
- `db/schema.sql` materializa el modelo ER como preparación para la migración a JPA.

Nota de calidad: los archivos Java se formatean con `google-java-format`; la verificación final se ejecuta con `./gradlew test` y los checks de `./gradlew check`.


## Flujo de prueba de API 

A continuación se detalla la secuencia lógica del flujo de pruebas end-to-end implementada en el archivo de características `ejercicio-04-flujo-api.feature` y ejecutada mediante `TestRestTemplate` en `FlujoApiSteps.java`, detallando los códigos HTTP, contratos y validaciones de cada paso:

1. **Configuración de Datos Base (Dadas las condiciones):**
   * **Alta de Sala:** Se realiza un `POST /api/salas` enviando un mapa con el nombre `"Sala 101"` y capacidad `10`. Devuelve un código `200 OK`.
   * **Alta de Estudiantes:** Se efectúa un `POST /api/estudiantes` para dar de alta en lote a los 6 alumnos indicados (Ana Perez, Bruno Diaz, Carla Gomez, Diego Ruiz, Elena Torres y Facundo Silva), calculando legajos dinámicos deterministas. Devuelve un código `200 OK`.

2. **Consulta de Disponibilidad Inicial:**
   * **Endpoint:** `GET /api/salas/disponibilidad?fecha=12/06/2026`
   * **Código HTTP:** `200 OK`
   * **Validación:** Se parsea la lista JSON mediante Jackson. Se verifica que para `"Sala 101"`, la propiedad `cuposDisponibles` sea exactamente igual a `10`, validando que el inventario está libre de reservas confirmadas activas para esa fecha.

3. **Creación de Reservas en Serie:**
   * **Endpoint:** `POST /api/reservas`
   * **Payloads:** Se envían de forma consecutiva 6 objetos JSON que contienen los bloques horarios desde las `09:00` hasta las `12:00` (bloques de 30 min por estudiante).
   * **Código HTTP:** `200 OK`
   * **Validación:** Cada petición responde con un mensaje en texto plano. Los pasos de prueba interceptan el caracter de separación (`|`), extraen el código de negocio transaccional dinámico `RES-XXXX` generado por el backend y lo asocian al estudiante en un mapa en memoria (`codigoReservaPorEstudiante`) para asegurar trazabilidad lineal en los siguientes pasos.

4. **Ejecución de Cancelaciones Simultáneas:**
   * **Endpoint:** `POST /api/reservas/{codigo}/cancelar?solicitante={estudiante}`
   * **Operación:** Se consume el endpoint recuperando los códigos guardados para *"Ana Perez"* y *"Bruno Diaz"*. El nombre se codifica de forma segura en formato URL (`StandardCharsets.UTF_8`).
   * **Código HTTP:** `200 OK`
   * **Validación:** Las reservas mutan su estado a `"cancelada"`, impactando un nuevo registro de auditoría en sus respectivos historiales con la marca temporal y el operario `"SISTEMA"`.

5. **Consulta de Disponibilidad Final y Auditoría de Estados:**
   * **Endpoint 1:** `GET /api/salas/disponibilidad?fecha=12/06/2026`
     * **Validación:** Como el servicio calcula la ocupación sumando únicamente las reservas en estado `"confirmada"`, las cancelaciones reducen las celdas ocupadas de 6 a 4. El aserto verifica que la propiedad `cuposDisponibles` de la `"Sala 101"` es exactamente `6` ($10 - 4 = 6$).
   * **Endpoint 2:** `GET /api/reservas/{codigo}/historial`
     * **Validación:** Se consume en bucle el historial de cada estudiante, evaluando mediante Jackson el último elemento del array. Se convalida con éxito rotundo que las reservas de Ana y Bruno se registran como `"cancelada"` de forma definitiva, mientras que Carla, Diego, Elena y Facundo conservan con total seguridad el estado `"confirmada"`.

## Feedback incorporado

| Observación del ejercicio 3 | Artefacto de E4 | Qué cambió |
|-----------------------------|------------------|------------|
| El rol del Personal y la Trazabilidad de Auditoría (HU-04 y HU-07): La HU-04 introduce al "personal de biblioteca" (ej. Ana Pérez) como el actor que registra físicamente la asistencia. El documento del modelo de dominio justifica la exclusión del Personal indicando que "el sistema no necesita persistir su identidad". Aunque esto se ampara en la regla de Larman de conservar solo las asociaciones de las que hay una "necesidad de recordar" (need-to-remember), entra en fricción con la HU-07, que exige auditar cualquier turno ante un conflicto. Si no se modela quién (qué personal) ejecutó el cambio de estado hacia HistorialEstado, la auditoría queda incompleta. | modelo-dominio.md, modelo-dominio.puml, modelo-dominio.png | Se agregó la entidad `Personal` al modelo con su id y su nombre. |
| Ausencia de modelo para Penalizaciones (D-02): El documento menciona como duda abierta (D-02) si se aplicarán penalizaciones por ausencias acumuladas. Dado que la HU-04 ya marca reservas como "ausente" automáticamente, el dominio genera el insumo para penalizar, pero falta la entidad conceptual (ej. `Penalizacion` o un `estadoHabilitacion` en `Estudiante`) que materialice esta regla de negocio en caso de que la coordinación defina el umbral. | modelo-dominio.md, modelo-dominio.puml, modelo-dominio.png | Se agregó en `Estudiante` el atributo `estadoHabilitacion` para futuras penalizaciones según defina la coordinación. |
| BDD: Formulación estática de Reglas de Negocio: El feedback del Ejercicio 2 señaló que algunas reglas de negocio y restricciones estaban formuladas como directivas estáticas en lugar de estar descritas desde la perspectiva del comportamiento. Aunque los pasos Given/When/Then mejoraron sustancialmente, los archivos `.feature` del Ejercicio 3 continúan declarando textualmente la palabra clave "Regla:" de forma estricta e imperativa (ej. HU-04, HU-01). | HU-01.feature, HU-02.feature, HU-03.feature, HU-04.feature | Se reformularon las reglas para que sean impersonales y describan comportamiento del sistema. |
| Asignación de Tags incompleta: A pesar de la mejora en trazabilidad mediante tags en HU-02 y HU-03, este estándar no se aplicó de manera uniforme. HU-01 y HU-04 carecían de tag canónico asignado, dejando incompleto el sistema de clasificación de especificaciones ejecutables. | HU-01.feature, HU-04.feature | Se agregaron los tags canónicos `@creacion-reserva` y `@registrar-asistencia`. |
| Fallos de SpotBugs: se detectaron campos internos mutables, uso de strings para entidades, excepciones genéricas, atributos públicos no protegidos, y otros issues de calidad. | `src/test/java/ar/edu/fiuba/is1/tpi/bdd/*.java`, `src/main/java/ar/edu/fiuba/is1/tpi/*.java` | Se corrigieron todos los errores detectados y se validó con `./gradlew check` incluyendo SpotBugs. |
| En BDD, los steps deberían entrar por la superficie pública REST/JSON del sistema. Usar repository/service/controller directamente rompe el contrato del sistema. Se recomienda usar la API y DTOs tipados, evitando JSON crudo. | `src/test/java/ar/edu/fiuba/is1/tpi/bdd/*.java`, `src/main/java/ar/edu/fiuba/is1/tpi/service/ReservaService.java` | Se refactorizaron los steps para usar la API en todos los casos salvo preparación, incorporando DTOs tipados en `ReservaService`. |
| docs/requisitos/modelo-dominio.md debería trazar HU-02 en sección de trazabilidad. | docs/requisitos/modelo-dominio.md | Se agregó HU-02 para completar la trazabilidad del documento. |


## Pendientes

* Reemplazar repositorios HashMap por Spring Data JPA con H2 o PostgreSQL en iteraciones futuras.
* Agregar turnoRepository y CRUD de turno para poder mapear turnos en una respectiva reserva, hoy esta todo embebido en la reserva
* Implementar autenticación real delegada al sistema institucional (RN-08), actualmente fuera de alcance.
* Implementar endpoint de dashboard con métricas de ocupación y ausentismo (HU-06, fuera del recorte de esta iteración).

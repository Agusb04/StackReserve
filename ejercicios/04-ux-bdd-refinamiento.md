# Ejercicio 4 - Cierre end-to-end y verificación de API

## Objetivo

Cerrar una primera versión end-to-end del sistema de gestión de turnos a salas. La entrega debe demostrar consistencia y coherencia de todo lo desarrollado en los cuatro ejercicios del TPI: descubrimiento, requisitos, modelado, UX, BDD, TDD, arquitectura de capas, API REST y diseño de datos deben hablar del mismo sistema, con trazabilidad donde corresponda y con los flujos prioritarios ejecutándose y verificándose automáticamente.

El foco funcional sigue siendo tres capacidades: consultar disponibilidad, crear reservas y cancelar reservas.

## Operatoria

- Trabajá en la rama `ejercicio-04`.
- Abrí el PR `ejercicio-04 -> main` con título `ejercicio-04`.
- Fecha asumida para validación automática: lunes 8 de junio de 2026 a las 12:00 ART.
- Usá `./gradlew check` como validación local de referencia antes de cada push relevante. Ese comando debe compilar, ejecutar tests, correr la verificación funcional y pasar los chequeos de calidad.
- El template ya trae una demo local resuelta de Storybook contra una API de ejemplo no perteneciente al dominio de turnos. Al correr Spring Boot, abrí `http://localhost:8080/docs/ux/storybook/index.html` y vas a ver el Storybook y la API en el mismo localhost, sin configurar CORS ni un servidor extra.

## Flujo funcional de referencia

Tomá este recorrido como ejemplo mínimo y concreto para evitar suposiciones distintas entre entregas:

La verificación funcional debe implementarse como uno o más escenarios Cucumber ejecutables en `src/test/resources/features/`, con step definitions en `src/test/java/.../bdd/`. No se pide un `main` manual ni una ejecución ad hoc fuera de `./gradlew check`.
El template incluye además `src/test/resources/features/ejercicio-04-flujo-api.feature` como plantilla editable: completá sus steps con llamadas HTTP/JSON reales y quitá `@template` cuando quieras que ese escenario pase a formar parte de la verificación ejecutable.

1. Antes de la primera consulta, cargá datos previos en el sistema para que la disponibilidad inicial no arranque vacía.
2. Consultá disponibilidad para el viernes 12 de junio de 2026, entre las 09:00 y las 12:00, sobre la sala `Sala 101`.
3. Creá 6 reservas distintas para estudiantes concretos, por ejemplo `Ana Perez`, `Bruno Diaz`, `Carla Gomez`, `Diego Ruiz`, `Elena Torres` y `Facundo Silva`, usando horarios compatibles con la disponibilidad inicial.
4. Confirmá 4 de esas reservas.
5. Cancelá 2 de las reservas confirmadas.
6. Volvé a consultar la disponibilidad para la misma fecha, sala y franja.
7. Mostrá en la API y en los tests qué cambió después de las reservas, confirmaciones y cancelaciones, usando los mismos datos concretos.

Si el dominio final usa otros nombres de salas, estados o reglas, adaptá los datos, pero mantené la estructura del flujo y la cantidad de operaciones observables.

## Artefactos obligatorios

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
- `src/main/java/`;
- `src/test/java/`;
- `src/test/resources/features/`;
- `entregas/ejercicio-04.md`.

## Artefactos opcionales

- `db/schema.sql`;
- `docs/ux/refinamiento-ux.md` si querés documentar una iteración extra de UX después del freeze, pero no hace falta para cerrar el ejercicio.

## Criterio

- Todo lo declarado debe ser consistente entre sí: requisitos, historias, modelo de dominio, BDD, arquitectura, API REST, DER, UX y tests no pueden contradecirse.
- La trazabilidad debe quedar visible donde corresponda: historia priorizada ↔ feature ↔ step definitions ↔ endpoint ↔ casos de uso ↔ modelo de dominio ↔ DER.
- Todo lo que forma parte de la solución debe compilar, ejecutarse y pasar tests y chequeos automáticos; no se aceptan artefactos que solo existan en texto si contradicen o no pueden verificarse.
- Las historias priorizadas deben mantenerse en correspondencia 1:1 con `HU-*.feature`.
- Deben quedar visibles las prioridades de disponibilidad, creación de reserva y cancelación; usá al menos los tags `@disponibilidad-salas`, `@creacion-reserva` y `@cancelacion-reserva`.
- La verificación funcional debe incluir un flujo de prueba sobre la API HTTP/JSON con datos concretos: una disponibilidad inicial, 6 reservas de estudiantes identificables, 4 confirmaciones, 2 cancelaciones y una verificación posterior del estado resultante.
- `db/modelo-er.md` debe incluir una sección `## Correspondencia DER` con cada `PK` y cada `FK` trazadas a una entidad o relación del dominio y con su correspondencia técnica explícita. No alcanza con dibujar el DER: cada clave tiene que decir claramente de dónde sale y a qué se corresponde.
- Si se incluye `db/schema.sql`, cada `FK` debe apuntar a la `PK` de otra tabla, y las tablas puente o asociativas deben dejar visible su `PK` compuesta o justificar explícitamente una excepción.
- No se aceptan claves huérfanas ni `id` genéricos sin correspondencia concreta con otra tabla o con una entidad/relación del modelo. Si en `db/modelo-er.md` aparece una clave, tiene que estar respaldada por el dominio y por el schema, cuando exista.
- `db/schema.sql` es opcional; si se incluye, debe ser coherente con el dominio y la API, pero no se exige para aprobar.
- La demo local de Storybook ↔ API ya viene resuelta en el template y usa la API de salud como ejemplo didáctico. No forma parte del dominio de turnos y no hace falta rehacerla.

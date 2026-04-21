# Ejercicio 2 - Historias de usuario, escenarios Gherkin, modelo de dominio y prototipos Storybook

## Objetivo

Transformar los requisitos del ejercicio 1 en backlog trazable, historias priorizadas, features Cucumber/Gherkin 1:1, modelo de dominio conceptual en PlantUML editable y prototipos HTML en Storybook.

## Operatoria

- Trabajá en la rama `ejercicio-02`.
- Abrí el PR `ejercicio-02 -> main` con título `ejercicio-02`.
- Fecha asumida para validación automática: lunes 27 de abril de 2026 a las 12:00 ART.
- Antes de empezar, revisá el feedback del ejercicio 1 y dejá visible en esta iteración qué observaciones incorporaste y en qué artefacto se ven.
- El refinamiento UX final no forma parte de este ejercicio; acá alcanza con el Storybook del happy path y los estados relevantes.

## Artefactos obligatorios

- actualización de `docs/requisitos/requisitos.md` si cambió el alcance;
- `docs/requisitos/historias-usuario.md`;
- `docs/requisitos/modelo-dominio.md`;
- `docs/requisitos/modelo-dominio.puml`;
- `docs/requisitos/modelo-dominio.svg` o `docs/requisitos/modelo-dominio.png`;
- al menos tres `src/test/resources/features/HU-*.feature` reales, uno por historia priorizada;
- `docs/ux/prototipo.md`;
- `docs/ux/freeze.md`;
- `docs/ux/storybook/index.html`;
- `docs/ux/storybook/src/story-template.html`;
- al menos una story HTML concreta en `docs/ux/storybook/src/`;
- al menos un export en `docs/ux/exports/`;
- `entregas/ejercicio-02.md`.

## Criterios de calidad

- El backlog debe tener al menos 6 historias de usuario.
- Deben quedar al menos 3 historias priorizadas.
- Cada historia priorizada debe mapear con exactitud a un archivo `src/test/resources/features/HU-xx.feature`.
- Cada feature priorizada debe tener una sola `Característica`, usar `# language: es` y contener al menos 3 escenarios.
- En cada feature priorizada debe quedar visible un happy path claro y al menos dos escenarios alternativos o excepcionales.
- Usá keywords de Gherkin cuando aporten claridad: `Antecedentes`, `Regla`, `Esquema del escenario`, `Ejemplos`, `Data Table` y `Doc String`.
- Usá valores concretos dentro de cada escenario: nombres, fechas, códigos, estados y cantidades del dominio, no placeholders genéricos.
- No describas en `Dado` datos que el sistema produce como resultado del caso: esos datos van en `Entonces` o en una tabla de salida.
- Si el mismo flujo tiene contextos diferentes, modelalo con `Esquema del escenario` + `Ejemplos` o con `Data Table`.
- Los actores humanos se describen como actores de historias y escenarios, no como entidades del dominio.
- Las relaciones del modelo deben nombrar vínculos persistidos, no acciones de proceso.
- El modelo de dominio debe ser conceptual, trazable a las historias priorizadas y escrito con PlantUML como archivo editable.
- El prototipo debe estar hecho con Storybook HTML y representar pantallas, estados y transiciones del recorrido principal, no pantallas aisladas.
- Los casos alternativos, excepciones y validaciones sólo deben aparecer cuando ayuden a entender el flujo principal.
- No se pide refinamiento UX final ni una iteración visual extra; ese trabajo queda para ejercicios posteriores.
- La nueva iteración debe dejar trazable qué feedback del ejercicio 1 se incorporó, y si alguna observación no aplica, explicá brevemente por qué.

## Material de apoyo

- [Cucumber Gherkin Reference](https://cucumber.io/docs/gherkin/reference)
- [Cucumber](https://cucumber.io/docs/cucumber/)
- [Storybook Docs](https://storybook.js.org/docs)
- [Writing Stories](https://storybook.js.org/docs/writing-stories)
- [Build Pages with Storybook](https://storybook.js.org/docs/writing-stories/build-pages-with-storybook)

# FIUBA IS1 2026 1C - TPI

Repositorio template para la assignment individual `tpi` de GitHub Classroom.

Cada estudiante trabaja durante toda la cursada sobre su propio repositorio derivado de este template. El repositorio es acumulativo: no se recrea por entrega, sino que evoluciona durante los cuatro ejercicios del Trabajo Práctico Individual.

## Estructura

- `AGENTS.md`: reglas fijas de trabajo dentro del repositorio.
- `ejercicios/`: enunciados publicados por docentes.
- `entregas/`: manifiestos de entrega por ejercicio.
- `docs/descubrimiento/`: entrevista, minuta, interesados e historia de dominio.
- `docs/requisitos/`: requisitos, historias de usuario y modelo de dominio conceptual con PlantUML editable y export visual.
- `docs/ux/`: prototipos, freeze, refinamiento UX y el scaffold de Storybook HTML en `docs/ux/storybook/`.
- `docs/arquitectura/`: ADRs y diagramas C4.
- `src/`: implementación backend Spring Boot.
- `db/`: modelo ER y esquema SQL versionado.
- `scripts/`: preflights locales por ejercicio.
- `.github/workflows/`: validaciones automáticas.

## Regla de trabajo

Los docentes controlan `AGENTS.md`, `ejercicios/`, los archivos `*-template`, el scaffold base de Storybook (`docs/ux/storybook/index.html`), `.github/` y los scripts de validación.

Los estudiantes crean y mantienen los artefactos vivos derivados de esos templates, además del código, pruebas y refinamientos del proyecto.

## Convención general

- No ejecutar `/init` ni regenerar `AGENTS.md`.
- No modificar archivos docentes ni archivos `*-template`.
- Copiar o derivar cada template a su archivo vivo sin el sufijo `-template`.
- Mantener nombres estables para los artefactos vivos: la evolución queda en git, no en el filename.
- Antes de hacer push, ejecutar el preflight del ejercicio en curso.
- En `ejercicio-03` y `ejercicio-04`, además del preflight conviene correr `./gradlew check` localmente de forma frecuente.

## Feedback

GitHub Classroom crea un pull request especial titulado `Feedback` para que los docentes dejen comentarios sobre el trabajo.

Ese pull request no debe cerrarse. En repositorios recién creados suele aparecer como `#1`, pero la referencia estable es su título: `Feedback`.

## Actualizaciones docentes

Cuando los docentes publiquen una corrección técnica o una nueva iteración del material docente, el cambio entrará primero en `main`.

La operatoria esperada es:

- los docentes mergean el PR docente hacia `main`;
- luego anuncian por Slack que hay cambios nuevos disponibles;
- cada estudiante trae esos cambios desde `main` a su rama de trabajo antes de seguir.

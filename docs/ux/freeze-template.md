# Freeze UX

## Link read-only

...

## Storybook congelado

- `docs/ux/storybook/index.html`
- stories relevantes del flujo principal

## Capturas congeladas

- Cada captura debe existir como archivo real del repositorio, estar versionada y citarse con su ruta relativa en este documento.
- ![Captura 1](docs/ux/exports/captura-01.png)
- ![Captura 2](docs/ux/exports/captura-02.png)
- ![Captura 3](docs/ux/exports/captura-03.png)
- ![Captura 4](docs/ux/exports/captura-04.png)

## Export congelado

- `docs/ux/exports/captura-01.png`
- `docs/ux/exports/captura-02.png`
- `docs/ux/exports/captura-03.png`
- `docs/ux/exports/captura-04.png`

## Consistencia verificada

No alcanza con escribir "todo coincide". La consistencia tiene que quedar evidenciada con referencias concretas y observables.

Completá una fila por captura y dejá explícito:

- qué pantalla o estado muestra;
- qué storybook la generó;
- qué feature la respalda;
- qué dato visible prueba la coincidencia;
- qué cambia entre la captura inicial y la final.

| Captura | Storybook | Feature | Evidencia visible | Coincidencia concreta |
| --- | --- | --- | --- | --- |
| `docs/ux/exports/captura-01.png` | `docs/ux/storybook/src/HU-XX-....html` | `src/test/resources/features/HU-XX.feature` | se ve el estado inicial del flujo con datos del dominio | la captura resume la story principal |

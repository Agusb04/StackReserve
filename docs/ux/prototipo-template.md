# Prototipo

## Objetivo del prototipo

...

## Historias y escenarios cubiertos

| Historia | Escenario principal | Storybook |
| --- | --- | --- |
| HU-01 | ... | `docs/ux/storybook/src/HU-01-...html` |

## Pantallas o recorridos cubiertos

- ...

## Storybook

- archivo base del prototipo: `docs/ux/storybook/index.html`
- stories editables del alumno: `docs/ux/storybook/src/`
- cada story debe representar una pantalla, un estado o una transición básica del flujo principal
- la demo local resuelta del template usa `GET /api/health` como ejemplo no perteneciente al dominio de turnos
- al correr la aplicación Spring Boot, abrí `http://localhost:8080/docs/ux/storybook/index.html` para ver el Storybook y la API en el mismo localhost

## Cómo conectar una story con la API

No hay que inventar la integración desde cero. El template ya trae tres piezas listas y cada una tiene un rol fijo.

| Archivo | Rol | Qué se cambia |
| --- | --- | --- |
| `docs/ux/storybook/src/storybook-demo.config.json` | Configuración de la story | URL del endpoint, texto visible, nombre del request y mensajes de éxito o error |
| `docs/ux/storybook/src/storybook-demo.js` | Lógica genérica | Lee la config, hace el `fetch` al backend y pinta la respuesta |
| `docs/ux/storybook/src/HU-XX-....html` | Vista de la historia | Muestra los datos de la pantalla y referencia la config y el script comunes |

La regla es simple:

- si cambia el servicio, cambiás `apiUrl` en el JSON;
- si cambia el texto visible de la story, cambiás el mismo JSON;
- si agregás otra historia, copiás el HTML pero no reescribís el JavaScript.

Ejemplo de configuración mínima:

```json
{
  "title": "Salud del sistema",
  "subtitle": "Verificación local contra un endpoint de ejemplo",
  "apiUrl": "/api/health",
  "requestLabel": "GET /api/health",
  "successHint": "La API respondió bien en el mismo localhost.",
  "failureHint": "Si falla, levantá Spring Boot y volvé a probar."
}
```

Pasos concretos:

1. Implementá el endpoint en Spring Boot.
2. Elegí la URL exacta que vas a exponer, por ejemplo `/api/recurso-principal` o `/api/recursos`.
3. Copiá una story HTML del template y cambiá solo el contenido visible de la historia.
4. Editá `storybook-demo.config.json` para apuntar al endpoint nuevo y ajustar los textos.
5. No toques la lógica de `storybook-demo.js` para cada historia: el script ya sabe leer la config y llamar al backend.
6. Probá todo desde `http://localhost:8080/docs/ux/storybook/index.html`.

## Link read-only

...

## Export congelado

- `docs/ux/exports/...`

## Decisiones de UX visibles

- ...

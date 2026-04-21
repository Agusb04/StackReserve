# Historias de usuario

## Backlog

| ID | Historia | Evidencia principal | Épica opcional |
| --- | --- | --- | --- |
| HU-01 | Como ..., quiero ..., para ... | RF-01, minuta H02 | ... |

## Historias priorizadas

| ID | Motivo de prioridad | Feature esperado |
| --- | --- | --- |
| HU-01 | ... | `src/test/resources/features/HU-01.feature` |

## Criterios de calidad

- El backlog debe contener al menos 6 historias.
- Deben priorizarse al menos 3 historias.
- Cada historia priorizada debe tener un `HU-xx.feature` 1:1.
- Cada feature debe usar `# language: es`.
- Las features deben mantenerse legibles y alineadas con el modelo de dominio.
- Las features deben usar valores concretos de la historia: nombres, objetos, fechas, códigos, estados y cantidades reales del recorte, no placeholders genéricos.
- No pongas en `Dado` datos que el sistema genera como parte del resultado del flujo: esos datos van en `Entonces`, en una tabla de salida o en el paso que describa la respuesta del sistema.
- Cuando un mismo escenario admita distintos contextos, usá `Esquema del escenario` + `Ejemplos` o `Data Table` para dejarlo visible.

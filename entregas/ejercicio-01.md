# Entrega ejercicio 01

## Objetivo de la entrega
Construir la base de descubrimiento y requisitos del TPI a partir de una entrevista guiada contra el caso común `biblioteca/turnos`, produciendo los artefactos necesarios para documentar el dominio del problema, los interesados y los requisitos del sistema.

---

## Artefactos trabajados
- `docs/descubrimiento/entrevista.md` — Entrevista simulada con coordinación y personal de biblioteca
- `docs/descubrimiento/minuta.md` — Minuta con hallazgos, decisiones y compromisos
- `docs/descubrimiento/interesados.md` — Análisis de interesados con Onion Model
- `docs/descubrimiento/interesados.puml` — Fuente editable del diagrama de interesados
- `docs/descubrimiento/interesados.svg` — Export visual del diagrama de interesados
- `docs/descubrimiento/historia-dominio.md` — Historia de dominio del escenario de reserva
- `docs/descubrimiento/historia-dominio.puml` — Fuente editable del diagrama de historia de dominio
- `docs/descubrimiento/historia-dominio.svg` — Export visual del diagrama de historia de dominio
- `docs/requisitos/requisitos.md` — Requisitos funcionales, no funcionales y reglas de negocio

- Para realizar los archivos use varias de las skills con modelos como Sonnet 4.5 y use feedback iterativo con otros modelos para ir mejorando la entrega 

---

## Cambios principales
- Se relevó el proceso actual de gestión de turnos mediante entrevista guiada
- Se identificaron tres actores principales: estudiantes, personal de biblioteca y coordinación
- Se detectaron los problemas centrales del dominio: superposiciones, falta de confirmación, cancelaciones no registradas y ausencia de historial
- Se validaron dos hipótesis: visibilidad en tiempo real (confirmada) y recordatorios automáticos (parcialmente confirmada)
- Se modelaron los interesados utilizando el Onion Model con tres capas
- Se documentó la historia de dominio del escenario de reserva (happy path)
- Se levantaron 9 requisitos funcionales, 4 no funcionales y 5 reglas de negocio, todos trazados a evidencia de entrevista

---

## Feedback incorporado
- Sin feedback previo (primera entrega)

---

## Pendientes
- Definir canal de notificación preferido por los usuarios (D-01)
- Establecer reglas ante ausencias y posibles penalizaciones (D-02)
- Determinar si existen prioridades entre usuarios (D-03)
- Evaluar integración con sistemas institucionales existentes (D-04)
- Validar modelo de interesados con actores reales
- Validar necesidad de recordatorios automáticos con mayor evidencia
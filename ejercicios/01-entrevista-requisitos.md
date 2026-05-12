# Ejercicio 1 - Entrevista, minuta, interesados, historia de dominio y requisitos

## Objetivo

Construir la base de descubrimiento y requisitos del sistema de gestion de turnos a salas de una biblioteca a partir de una entrevista guiada contra el caso comun `biblioteca/turnos`.

## Operatoria

- Trabajá en la rama `ejercicio-01`.
- Abrí el PR `ejercicio-01 -> main` con título `ejercicio-01`.
- No cierres el PR `Feedback`.
- Usá el GPT provisto por docentes para realizar la entrevista: [Descubrimiento de Requisitos Biblioteca Turnos](https://chatgpt.com/g/g-69c887b46e4c8191bbfcd0ad926fa6e8-descubrimiento-de-requisitos-biblioteca-turnos).
- Fecha asumida para validación automática: lunes 6 de abril de 2026 a las 12:00 ART.

## Artefactos obligatorios

- `docs/descubrimiento/entrevista.md`
- `docs/descubrimiento/minuta.md`
- `docs/descubrimiento/interesados.md`
- `docs/descubrimiento/interesados.drawio` o `docs/descubrimiento/interesados.puml`
- `docs/descubrimiento/interesados.svg` o `docs/descubrimiento/interesados.png`
- `docs/descubrimiento/historia-dominio.md`
- `docs/descubrimiento/historia-dominio.egn` o `docs/descubrimiento/historia-dominio.puml`
- `docs/descubrimiento/historia-dominio.svg` o `docs/descubrimiento/historia-dominio.png`
- `docs/requisitos/requisitos.md`
- `entregas/ejercicio-01.md`

## Criterio

- La entrevista es la evidencia principal.
- Los artefactos vivos se derivan de los templates locales.
- Para interesados e historia de dominio, entregá Markdown + archivo editable + export visual.
- En interesados, respetá Onion Model con `Our System`, `Containing System` y `Wider Environment`.
- En historia de dominio, trabajá un solo escenario y dejá explícito el recorte aplicado.
- La minuta debe dejar explícitos participantes, fecha y hora, lugar o medio y compromisos asumidos.
- En `docs/requisitos/requisitos.md`, separá alcance, fuera de alcance, requisitos funcionales, requisitos no funcionales y reglas de negocio.
- Cada requisito debe quedar trazado a evidencia de entrevista o minuta; las historias de usuario y Gherkin quedan para el ejercicio 2.
- Como mejora optativa, podés llevar `docs/descubrimiento/historia-dominio.md` a Egon con archivo `.egn` y export visual estable; eso eleva la calidad del artefacto pero no cambia el criterio de aprobación del ejercicio.

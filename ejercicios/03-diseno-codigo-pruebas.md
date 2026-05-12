# Ejercicio 3 - Diseño backend, codificación y pruebas

## Objetivo

Implementar una primera version backend del sistema de gestion de turnos a salas de una biblioteca con Spring Boot, arquitectura en capas clasica, API REST JSON y BDD ejecutable con Cucumber JVM. La implementacion debe cubrir todas las historias priorizadas del backlog y dejar ejecutables todos los escenarios de sus `HU-xx.feature`.

## Alcance prioritario

Los interesados priorizaron dos capacidades que deben quedar visibles en el recorte de esta iteración: la consulta de disponibilidad de salas para un día determinado y la cancelación de una reserva de sala. Ajustá lo necesario en requisitos, especificación y diseño para darles espacio.

Esas prioridades deben reflejarse en las historias priorizadas, en las features `HU-xx`, en los tags canónicos y en la arquitectura documentada.

Por tags canónicos se entiende esto, exactamente así, al inicio de la feature:

```gherkin
# language: es
@disponibilidad-salas
Característica: ...
```

Esas etiquetas sirven para identificar de forma estable esas historias en la revisión automática.

## Step definitions

- Implementá todos los step definitions necesarios para que cada escenario de cada feature priorizada ejecute sin pasos pendientes.
- Ubicalos en `src/test/java`, junto con la configuración de Cucumber y Spring.
- Reutilizá steps comunes entre features y parametrizalos cuando haga falta; no hace falta un método por cada línea del `.feature`.
- Usá los steps para expresar comportamiento visible y preparaciones de prueba simples; si aparece lógica interna relevante, cubrila también con tests unitarios en `service` o `domain`.
- No saltees capas para simplificar la prueba: los steps deben entrar por la superficie pública prevista por la arquitectura, no llamar directo a `repository` ni a detalles internos que el flujo normal no expone.
- El ejemplo `health.feature` funciona como punto de arranque del scaffold, pero no reemplaza las features de las historias priorizadas.

## Operatoria

- Trabajá en la rama `ejercicio-03`.
- Abrí el PR `ejercicio-03 -> main` con título `ejercicio-03`.
- Fecha asumida para validación automática: lunes 18 de mayo de 2026 a las 12:00 ART.
- Usá `./gradlew check` como validación local de referencia antes de cada push relevante.
- Para esta etapa alcanza con JDK 21 y el Gradle Wrapper. No hace falta configurar base de datos ni variables de entorno adicionales.
- Antes de codificar, revisá `docs/requisitos/requisitos.md`, `docs/requisitos/historias-usuario.md`, `docs/requisitos/modelo-dominio.md` y `docs/requisitos/modelo-dominio.puml`.
- Las historias priorizadas deben quedar 1:1 con `src/test/resources/features/HU-XX.feature`.
- Cada feature priorizada debe tener `# language: es`, un flujo principal claro y escenarios alternativos o excepcionales visibles.
- Los tags canónicos `@disponibilidad-salas` y `@cancelacion-reserva` deben aparecer en las features que materialicen esas prioridades, tal como están escritos.

## Artefactos obligatorios

- `docs/requisitos/requisitos.md`;
- `docs/requisitos/historias-usuario.md`;
- `docs/requisitos/modelo-dominio.md`;
- `docs/requisitos/modelo-dominio.puml`;
- `docs/arquitectura/adr-001.md` y los ADRs adicionales que hagan falta;
- `docs/arquitectura/c4-contexto.md`;
- `docs/arquitectura/c4-contenedores.md`;
- `docs/arquitectura/c4-componentes.md`;
- código bajo `src/main/java/`;
- tests bajo `src/test/java/`;
- features ejecutables bajo `src/test/resources/features/`;
- `entregas/ejercicio-03.md`.

## Expectativas de arquitectura

- `docs/arquitectura/adr-001.md` debe dejar explícito el recorte backend elegido y por qué conviene para esta iteración.
- `docs/arquitectura/c4-contexto.md` debe usar `C4Context` con al menos un `Person`, un `System` y un `Rel`.
- `docs/arquitectura/c4-contenedores.md` debe usar `C4Container` con al menos un `Container` o `ContainerDb` y un `Rel`.
- `docs/arquitectura/c4-componentes.md` debe usar `C4Component` con al menos un `Component` y un `Rel`.
- La arquitectura documentada y el código deben coincidir: no se admiten atajos que salteen `controller`, `service` o `repository` solo para hacer pasar los escenarios.
- Los C4 tienen que mostrar esa misma separación de responsabilidades y el recorrido real de las solicitudes.

## Material de apoyo

- [Cucumber Gherkin Reference](https://cucumber.io/docs/gherkin/reference)
- [Cucumber](https://cucumber.io/docs/cucumber/)
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/reference/)
- [Mermaid C4 syntax](https://mermaid.ai/open-source/syntax/c4.html)

# C4 - Contenedores
Usar Mermaid inline con sintaxis `C4Container`.

## Propósito
Explicar el recorte de contenedores del sistema de gestión de turnos a salas de la biblioteca: actores, backend y persistencia en memoria.

## Contenedores

| Contenedor | Responsabilidad | Tecnología |
| --- | --- | --- |
| Backend | Expone la API REST y aplica las reglas de negocio del dominio | Spring Boot 3.3 / Java 21 |
| Persistencia en memoria | Guarda el estado de salas, reservas y estudiantes durante la ejecución | HashMap in-process (sin base de datos externa en esta iteración) |

## Diagrama

```mermaid
C4Container
    title Contenedores del sistema de gestión de turnos a salas
    Person(usuario, "Estudiante / Personal", "Consulta disponibilidad y gestiona reservas o asistencia")
    System_Boundary(sistema_boundary, "Sistema de gestión de turnos a salas") {
        Container(backend, "Backend", "Spring Boot 3.3 / Java 21", "Expone la API REST y aplica reglas de negocio: disponibilidad, reserva, cancelación y check-in")
        ContainerDb(persistencia, "Persistencia en memoria", "HashMap in-process", "Almacena salas, reservas y estudiantes durante la ejecución. Sin persistencia entre reinicios en esta iteración")
    }
    Rel(usuario, backend, "consume la API REST", "HTTPS / JSON")
    Rel(backend, persistencia, "lee y escribe entidades del dominio", "Java in-process")
```

## Decisiones relevantes
- El backend concentra toda la lógica de aplicación y dominio.
- La persistencia se implementa en memoria (HashMap) para eliminar dependencias de entorno en esta iteración. En una iteración posterior se reemplazaría por Spring Data JPA con H2 o PostgreSQL.
- No existe capa de cliente en esta iteración; los consumidores acceden directamente a la API REST.
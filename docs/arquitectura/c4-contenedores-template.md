# C4 - Contenedores

Usar Mermaid inline con sintaxis `C4Container`.

## Propósito

Explicar el recorte de contenedores del sistema de gestion de turnos a salas de la biblioteca: cliente, backend y persistencia conceptual.

## Contenedores

| Contenedor | Responsabilidad | Tecnología |
| --- | --- | --- |
| Cliente | Interactua con la solucion | Web |
| Backend | Expone la API REST y aplica reglas | Spring Boot |
| Persistencia conceptual | Guarda el estado del sistema | SQL |

## Diagrama

```mermaid
C4Container
    title Contenedores del sistema de gestion de turnos a salas
    Person(usuario, "Usuario de biblioteca", "Consulta disponibilidad y gestiona reservas")
    System_Boundary(sistema_boundary, "Sistema de gestion de turnos a salas") {
        Container(cliente, "Cliente", "Web", "Interfaz que consume el backend")
        Container(backend, "Backend", "Spring Boot", "Expone la API REST y aplica reglas")
        ContainerDb(persistencia, "Persistencia conceptual", "SQL", "Guarda el estado del sistema")
    }

    Rel(usuario, cliente, "usa")
    Rel(cliente, backend, "consume")
    Rel(backend, persistencia, "lee y escribe")
```

## Decisiones relevantes

- El backend concentra la logica de aplicacion.
- La persistencia se muestra como recorte conceptual, no como detalle de implementacion.

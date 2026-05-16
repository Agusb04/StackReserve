# C4 - Componentes
Usar Mermaid inline con sintaxis `C4Component`.

## Propósito
Describir la separación lógica por capas del backend del sistema de gestión de turnos a salas.

## Componentes principales

| Componente | Responsabilidad | Interacciones |
| --- | --- | --- |
| Controller | Recibe requests HTTP y devuelve respuestas REST JSON | Service |
| Service | Orquesta los casos de uso y aplica las reglas de negocio | Domain, Repository |
| Domain | Encapsula las reglas del negocio (estados de reserva, historial, cupo) | Service |
| Repository | Persiste y recupera entidades en memoria | Service |

## Diagrama

```mermaid
C4Component
    title Componentes del backend de turnos a salas
    Container_Boundary(backend, "Backend") {
        Component(controller, "ReservaController / PingController", "Spring MVC", "Recibe requests REST y delega al servicio: POST /api/reservas, POST /api/reservas/{id}/cancelar, POST /api/reservas/{id}/check-in, GET /api/salas/disponibilidad")
        Component(service, "ReservaService", "Java", "Orquesta los casos de uso: validar cupo, evitar solapamiento, confirmar, cancelar, registrar asistencia y consultar disponibilidad")
        Component(domain, "Reserva / Sala / Turno / Estudiante / HistorialEstado", "Java", "POJOs que encapsulan las reglas del negocio: transición de estados y registro automático en historial")
        Component(repository, "ReservaRepository / SalaRepository / EstudianteRepository", "Spring Component", "Persiste y recupera entidades en HashMap en memoria")
    }
    Rel(controller, service, "invoca")
    Rel(service, domain, "aplica")
    Rel(service, repository, "usa")
```

## Riesgos o límites del diseño
- No bajar a clases concretas ni wiring de framework.
- La capa de dominio debe seguir siendo conceptual, no una copia del código.
- Los repositorios HashMap no son thread-safe; son válidos para esta iteración pero deberán reemplazarse con Spring Data JPA en producción.
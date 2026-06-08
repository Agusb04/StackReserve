# C4 - Componentes

## Propósito

Describir la separación lógica por capas del backend del sistema de gestión de turnos a salas, incluyendo los componentes principales y los auxiliares de pruebas.

## Componentes principales

| Componente | Responsabilidad | Interacciones |
| --- | --- | --- |
| Controller | Recibe requests HTTP, valida firmas genéricas de entrada (`Map`) y devuelve respuestas REST JSON coherentes (`200 OK` o `400 Bad Request`).| Service | 
| Service | Orquesta los casos de uso principales, extrae tipados seguros y aplica rigurosamente las reglas de negocio. | Domain, Repository |
| Domain | Encapsula las reglas del negocio (estados de reserva, historial, cupo). Nota: el objeto `Turno` hoy está modelado como un valor embebido dentro de `Reserva` (no tiene id persistido en esta iteración). | Service |
| Repository | Persiste y recupera entidades mediante estructuras de datos en memoria (implementadas como `ConcurrentHashMap` o `HashMap` con sincronización). En la implementación actual `ReservaRepository` aplica sincronización en métodos críticos y `SalaRepository`/`EstudianteRepository` usan `ConcurrentHashMap` para reducir riesgos de condiciones de carrera. | Service |

## Diagrama

```mermaid
C4Component
    title Componentes del backend de turnos a salas (con Auxiliares de Alta)
    Container_Boundary(backend, "Backend") {
        Component(controller, "ReservaController / PingController", "Spring MVC", "Recibe requests REST y delega al servicio: POST /api/reservas, POST /api/reservas/{id}/cancelar, POST /api/reservas/{id}/check-in, GET /api/salas/disponibilidad")
        Component(aux_controller, "AuxiliarAltaController", "Spring MVC", "Recibe payloads genéricos para la preparación de datos del entorno de pruebas BDD: POST /api/salas, POST /api/estudiantes")
        
        Component(service, "ReservaService", "Java", "Orquesta los casos de uso: validar cupo, evitar solapamiento, confirmar, cancelar, registrar asistencia y consultar disponibilidad")
        Component(aux_service, "AuxiliarAltaService", "Java", "Aplica validaciones de integridad y de negocio estructurales (RF-08) para dar de alta salas y estudiantes")
        
        Component(domain, "Reserva / Sala / Turno / Estudiante / HistorialEstado", "Java", "POJOs que escapulan las reglas del negocio: transición de estados y registro automático en historial")
        
        Component(repository, "ReservaRepository / SalaRepository / EstudianteRepository", "Spring Component", "Persiste y recupera entidades en memoria (ConcurrentHashMap / HashMap sincronizado). Expone validaciones de existencia mediante existsById")
    }
    Rel(controller, service, "invoca")
    Rel(aux_controller, aux_service, "invoca")
    
    Rel(service, domain, "aplica")
    Rel(aux_service, domain, "instancia")
    
    Rel(service, repository, "usa")
    Rel(aux_service, repository, "usa")
```
## Riesgos o limites del diseno
 - Persistencia en memoria: los repositorios usan HashMap, lo que implica limitaciones en concurrencia y durabilidad. En la implementación actual se mitigó parte del riesgo aplicando sincronización (`synchronized`) en los métodos críticos de los repositorios, pero esto no reemplaza garantías transaccionales ni la durabilidad de una base de datos relacional. En producción se reemplazará por Spring Data JPA.

# C4 - Componentes

## Propósito

Describir la separación lógica por capas del backend del sistema de gestión de turnos a salas, incluyendo los componentes principales y los auxiliares de pruebas.

## Componentes principales

| Componente | Responsabilidad | Interacciones |
| --- | --- | --- |
| Controller | Recibe requests HTTP, valida DTOs con Bean Validation, delega en el servicio y devuelve respuestas REST coherentes (`200 OK` o `400 Bad Request`). | Service |
| Service | Orquesta los casos de uso, aplica reglas de negocio, valida formato de fecha/hora y usa un `Clock` inyectado para procesar ausencias de forma determinista. | Domain, Repository |
| Domain | Encapsula reglas de negocio: invariantes de Sala/Turno/Personal, transiciones de estado de Reserva e historial inmutable. `Turno` sigue embebido en `Reserva` en esta iteración. | Service |
| Repository | Persiste y recupera entidades en memoria (`ConcurrentHashMap` o `HashMap` con sincronización). `ReservaRepository` sincroniza métodos críticos y `SalaRepository`/`EstudianteRepository` usan `ConcurrentHashMap`. | Service |
| Config | `AppConfig` registra el `Clock` del sistema y publica el Storybook estático bajo `/docs/**`. | Controllers |
| Error handling | `GlobalExceptionHandler` normaliza errores de validación, recurso inexistente y reglas de negocio a `400 text/plain`. | Controllers |

## Diagrama

```mermaid
C4Component
    title Componentes del backend de turnos a salas (con Auxiliares de Alta)
    Container_Boundary(backend, "Backend") {
        Component(controller, "ReservaController / PingController", "Spring MVC", "Recibe requests REST con DTOs tipados y delega al servicio: POST /api/reservas, POST /api/reservas/{id}/cancelar, POST /api/reservas/{id}/check-in, POST /api/reservas/procesar-ausencias, GET /api/salas/disponibilidad, GET /api/reservas/{id}/historial")
        Component(aux_controller, "AuxiliarAltaController", "Spring MVC", "Da de alta salas y estudiantes con DTOs validados para preparar datos de pruebas BDD: POST /api/salas, POST /api/estudiantes")
        Component(config, "AppConfig", "Spring Configuration", "Inyecta Clock y sirve el Storybook bajo /docs/**")
        Component(errors, "GlobalExceptionHandler", "Spring MVC Advice", "Traduce errores de validación y negocio a 400 text/plain")
        
        Component(service, "ReservaService", "Java", "Orquesta los casos de uso: validar cupo, evitar solapamiento, confirmar, cancelar, registrar asistencia y consultar disponibilidad")
        Component(aux_service, "AuxiliarAltaService", "Java", "Aplica validaciones de integridad y de negocio estructurales (RF-08) para dar de alta salas y estudiantes")
        
        Component(domain, "Reserva / Sala / Turno / Estudiante / HistorialEstado", "Java", "POJOs que encapsulan las reglas del negocio: transición de estados y registro automático en historial")
        
        Component(repository, "ReservaRepository / SalaRepository / EstudianteRepository", "Spring Component", "Persiste y recupera entidades en memoria (ConcurrentHashMap / HashMap sincronizado). Expone validaciones de existencia mediante existsById")
    }
    Rel(controller, service, "invoca")
    Rel(aux_controller, aux_service, "invoca")
    
    Rel(service, domain, "aplica")
    Rel(aux_service, domain, "instancia")
    
    Rel(service, repository, "usa")
    Rel(aux_service, repository, "usa")
    Rel(controller, errors, "delega errores")
    Rel(aux_controller, errors, "delega errores")
```
## Riesgos o límites del diseño
 - Persistencia en memoria: los repositorios usan colecciones Java, lo que implica limitaciones en concurrencia y durabilidad. En la implementación actual se mitigó parte del riesgo aplicando sincronización (`synchronized`) en los métodos críticos y `ConcurrentHashMap` en salas/estudiantes, pero esto no reemplaza garantías transaccionales ni la durabilidad de una base de datos relacional. En producción se reemplazará por Spring Data JPA; `db/schema.sql` deja preparado el esquema relacional.
 - La disponibilidad se calcula por fecha y sala (inventario diario), no por bloque horario individual. El control de cupo por bloque se aplica al crear la reserva.

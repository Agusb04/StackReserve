# C4 - Componentes

Usar Mermaid inline con sintaxis `C4Component`.

## Propósito

Describir la separacion logica por capas del backend del sistema de gestion de turnos a salas.

## Componentes principales

| Componente | Responsabilidad | Interacciones |
| --- | --- | --- |
| Controller | Recibe requests y responde | Service |
| Service | Orquesta casos de uso | Domain, Repository |
| Domain | Encapsula reglas del negocio | Service |
| Repository | Persiste y recupera datos | Service |

## Diagrama

```mermaid
C4Component
    title Componentes del backend de turnos a salas
    Container_Boundary(backend, "Backend") {
        Component(controller, "Controller", "Spring MVC", "Recibe requests y respuestas")
        Component(service, "Service", "Java", "Orquesta casos de uso")
        Component(domain, "Domain", "Java", "Contiene reglas del negocio")
        Component(repository, "Repository", "Spring Data", "Persiste el estado")
    }

    Rel(controller, service, "invoca")
    Rel(service, domain, "aplica")
    Rel(service, repository, "usa")
```

## Riesgos o limites del diseno

- No bajar a clases concretas ni wiring de framework.
- La capa de dominio debe seguir siendo conceptual, no una copia del codigo.

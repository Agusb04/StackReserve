# Interesados 
 
## Sistema analizado
 
Sistema de gestión de turnos y reservas de salas para una biblioteca académica.
 
El sistema propuesto reemplazará el proceso manual actual (planillas, mails y mensajería), permitiendo centralizar la gestión, evitar errores y mejorar la experiencia de los usuarios.
 
---
 
## Criterio de capas
 
Se utiliza el modelo de capas tipo *Onion Model*:
 
- **Our System:** actores que interactúan directamente con el sistema que se está diseñando.
- **Containing System:** actores que forman parte del entorno organizacional donde el sistema opera.
- **Wider Environment:** actores externos o indirectos que influyen o son impactados.
 
---
 
## Resumen del modelo
 
El sistema tiene como núcleo a los **usuarios directos (estudiantes y personal de biblioteca)**, quienes operarán el sistema para reservar y gestionar turnos.
 
En una segunda capa se encuentra la **coordinación de la biblioteca y la institución**, que utilizan la información generada para supervisión, control y toma de decisiones.
 
En la capa más externa se incluyen los **sistemas y servicios externos**, como canales de comunicación o infraestructura, que soportan el funcionamiento pero no forman parte directa del sistema.
 
---
 
## Registro de interesados
 
![Diagrama de interesados](interesados.svg)
 
| ID   | Interesado                         | Capa              | Relación con el sistema                                                                 | Justificación                                                                                     | Estado     |
|------|-----------------------------------|-------------------|------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------|------------|
| I-01 | Estudiantes                       | Our System        | Reservan turnos, consultan disponibilidad, cancelan reservas                            | Son usuarios directos del sistema propuesto                                                       | explícito  |
| I-02 | Personal de biblioteca            | Our System        | Administra reservas, valida turnos, gestiona conflictos                                 | Operan el sistema diariamente                                                                     | explícito  |
| I-03 | Coordinación de biblioteca        | Our System        | Consulta métricas, supervisa uso, define reglas de negocio                              | Utiliza directamente el sistema (dashboard, reportes)                                             | explícito  |
| I-04 | Institución académica             | Containing System | Define políticas, provee contexto organizacional                                         | El sistema responde a necesidades institucionales                                                 | inferido   |
| I-05 | Responsable de normativas internas | Containing System | Define y aplica las reglas sobre uso de salas y reservas                                | Condiciona el comportamiento del sistema a través de políticas formales                           | inferido   |
| I-06 | Servicios de notificación (email) | Containing System | Envío de confirmaciones, cancelaciones y recordatorios                                   | Servidor de mail institucional, necesario para comunicación automática con usuarios               | inferido   |
| I-07 | Servicios de mensajería           | Wider Environment | Canal alternativo de comunicación (ej: WhatsApp); actualmente utilizado, a reemplazar o integrar | Actualmente utilizados, podrían integrarse o eliminarse con el nuevo sistema               | explícito  |
| I-08 | Infraestructura tecnológica       | Wider Environment | Soporte técnico (servidores, red, hosting)                                               | Necesaria para el funcionamiento del sistema                                                      | inferido   |
| I-09 | Equipo de IT / soporte técnico    | Wider Environment | Gestiona la infraestructura, da soporte al sistema                                       | No identificado explícitamente en la entrevista, pero necesario para operación y mantenimiento    | inferido   |
 
---
 
## Cobertura faltante
 
- No se identificaron explícitamente:
  - Administradores del sistema (rol técnico dentro de la aplicación)
- Falta validar:
  - Integración con sistemas institucionales existentes
  - Reglas formales de penalización por ausencias
  - Uso real de notificaciones automáticas
 
---
 
## Próximas acciones
 
- Validar el modelo de interesados con actores reales  
- Confirmar roles técnicos (administradores, IT)  
- Refinar funcionalidades específicas por tipo de usuario  
- Evaluar integraciones externas (mail, sistemas académicos)  
 
---
 
## Criterios de control
 
- No duplicar interesados  
- Justificar la ubicación en cada capa  
- Clasificar cada interesado como:
  - `explícito`
  - `inferido`
  - `dudoso`
 
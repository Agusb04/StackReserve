# Requisitos

## Objetivo y alcance

### Problema u oportunidad

El dominio del problema corresponde a la gestión de turnos en una biblioteca académica, donde se administran recursos limitados (salas, horarios y personal) frente a una demanda variable.

Un desafío central del dominio es la coordinación entre múltiples actores (estudiantes, personal y coordinación) y la trazabilidad de los turnos, es decir, poder conocer quién realizó una reserva, su estado (confirmado, cancelado o ausente) y optimizar el uso de los recursos disponibles.

El proceso actual es manual y descentralizado, utilizando múltiples herramientas (planillas, mails, mensajería).  

Esto genera:

- Superposición de reservas  
- Falta de confirmación clara de turnos  
- Cancelaciones no registradas correctamente  
- Turnos desaprovechados por ausencias  
- Falta de historial y trazabilidad  
- Alta carga operativa en el personal  

Durante períodos de alta demanda (como semanas de parciales), estos problemas se intensifican, generando caos operativo y conflictos con los usuarios.

---

### Sistema a construir

Un sistema digital centralizado de gestión de turnos y reservas de salas que permita:

- Consultar disponibilidad en tiempo real  
- Reservar turnos de forma autónoma  
- Evitar superposiciones  
- Confirmar reservas de manera clara  
- Gestionar cancelaciones  
- Registrar historial de operaciones  
- Visualizar métricas mediante un dashboard  

---

### Alcance inicial

- Consulta de disponibilidad de salas  
- Reserva de turnos por estudiantes  
- Confirmación de reservas  
- Cancelación de turnos  
- Visualización de reservas por parte del personal  
- Visualización de métricas básicas (ocupación, demanda)  

---

### Fuera de alcance

- Gestión de devoluciones de libros  
- Integración con sistemas institucionales externos  
- Autenticación de usuarios  
- Integración con datos académicos detallados (promedios, regularidad específica) para la asignación de turnos.

---

### Supuestos relevantes

- Los estudiantes utilizarán el sistema como canal principal de reserva  
- El sistema será la fuente única de verdad para disponibilidad  
- Las salas tienen capacidad definida  
- El acceso al sistema está garantizado para los usuarios  
- El sistema delegará la autenticación de usuarios al sistema institucional existente.
---

## Requisitos funcionales

| ID   | Descripción | Justificación o valor | Evidencia o fuente |
|------|------------|----------------------|------------------|
| RF-01 | El sistema debe permitir consultar la disponibilidad de salas en tiempo real | Reducir consultas manuales y errores | Entrevista P01, P06, P08 |
| RF-02 | El sistema debe permitir a los estudiantes reservar turnos | Disminuir carga operativa del personal | Entrevista P04 |
| RF-03 | El sistema debe evitar la superposición de reservas | Reducir conflictos frecuentes | Entrevista P03, P09 |
| RF-04 | El sistema debe confirmar las reservas de manera clara a través de email institucional y/o notificaciones in-app. | Evitar incertidumbre en usuarios | Entrevista P02, P04 |
| RF-05 | El sistema debe permitir cancelar reservas | Liberar turnos no utilizados | Entrevista P03, P06 |
| RF-06 | El sistema debe registrar todas las reservas y cancelaciones | Permitir trazabilidad | Entrevista P03, P10 |
| RF-07 | El sistema debe permitir al personal visualizar y administrar reservas | Mejorar organización del trabajo | Entrevista P04, P06 |
| RF-08 | El sistema debe respetar la capacidad máxima de cada sala al asignar reservas | Evitar sobreocupación | Entrevista P06 |
| RF-09 | El sistema debe proporcionar un dashboard con métricas de uso | Apoyar la toma de decisiones | Entrevista P10 |
| RF-10	| El sistema debe permitir al personal registrar la asistencia (check-in) del estudiante al inicio del turno.	|Validar el uso efectivo del recurso y alimentar las métricas de ausentismo.| Entrevista P17
|RF-11	|El sistema debe validar la existencia de reservas previas del usuario para el horario solicitado antes de confirmar.|	Garantizar la equidad en el acceso a recursos limitados.|	Entrevista P15
---

## Requisitos no funcionales

| ID   | Categoría | Descripción | Criterio de verificación | Evidencia o fuente |
|------|----------|------------|--------------------------|------------------|
| RNF-01 | Usabilidad | El sistema debe ser fácil de usar para estudiantes y personal | Los usuarios pueden completar tareas sin asistencia | Entrevista P04 |
| RNF-02 | Disponibilidad | El sistema debe estar disponible durante el horario de uso de la biblioteca | Acceso sin interrupciones en horario operativo | Entrevista P01, P07 |
| RNF-03 | Consistencia | La información de disponibilidad debe ser precisa y actualizada | No deben ocurrir dobles reservas | Entrevista P03 |
| RNF-04 | Confiabilidad | El sistema debe registrar correctamente todas las operaciones | Auditoría sin inconsistencias | Entrevista P03 |

---

## Reglas de negocio

| ID   | Regla | Impacto en el sistema | Evidencia o fuente |
|------|------|----------------------|------------------|
| RN-01 | La cantidad de reservas confirmadas para una sala en un mismo turno no puede superar su capacidad máxima | Requiere validación de cupo disponible | Entrevista P03, P06 |
| RN-02 | Un turno debe estar asociado a una sala y un horario específico | Define la estructura mínima de una reserva | Entrevista P05 |
| RN-03 | una reserva se considera confirmada en el momento en que el sistema la registra exitosamente tras validar las reglas de negocio. | Requiere mecanismo de confirmación | Entrevista P02 |
| RN-04 | Las cancelaciones deben reflejarse liberando el turno correspondiente | Permite reutilizar el recurso | Entrevista P03 |
| RN-05 | Un turno no utilizado (ausencia) implica pérdida de disponibilidad del recurso | Impacta en métricas y eficiencia del sistema | Entrevista P03, P07 |
| RN-06 | Toda inasistencia (no presentación al turno) debe ser contabilizada en el historial del usuario para el cálculo de métricas y validaciones de reserva. | Define la trazabilidad necesaria para auditoría y futuras restricciones de acceso. | Entrevista P14 |
| RN-07 |  El sistema debe asignar los turnos bajo el principio de "primero en llegar, primero en ser servido" (First-Come, First-Served), sin distinción de jerarquías entre estudiantes. | Simplifica la lógica de asignación al no requerir roles de prioridad académica | Entrevista P15 |
|RN-08	|El acceso al sistema de reservas requiere una autenticación válida a través del sistema institucional.|Define la frontera del sistema y la responsabilidad de la gestión de identidades.|	Entrevista P16|
|RN-09 | Si al finalizar el turno no se registró check-in, el sistema marca la reserva como ausente automáticamente.| Llevar registro de ausentes para futuras penalizaciones | Entrevista P17, Entrevista P14
| RN-10 | El sistema debe impedir que un estudiante posea más de una reserva confirmada para el mismo bloque horario | Requiere validación de reservas previas del usuario antes de confirmar una nueva | Entrevista P15, P04. Decisión de diseño |
---

## Trazabilidad a entrevista

| ID requisito | Evidencia en entrevista | Interesado o historia relacionada | Observaciones |
|--------------|------------------------|----------------------------------|--------------|
| RF-01 | Entrevista P01, P06, P08 | Estudiantes, Personal | Problema de visibilidad y herramientas actuales |
| RF-02 | Entrevista P04 | Estudiantes | Necesidad de autogestión |
| RF-03 | Entrevista P03, P09 | Personal | Superposición frecuente |
| RF-04 | Entrevista P02, P04 | Estudiantes | Falta de confirmación formal |
| RF-05 | Entrevista P03, P06 | Personal | Cancelaciones mal registradas |
| RF-06 | Entrevista P03, P10 | Coordinación | Falta de historial y métricas |
| RF-07 | Entrevista P04, P06 | Personal | Necesidad de organización y visualización |
| RF-08 | Entrevista P06 | Coordinación | Control de capacidad |
| RF-09 | Entrevista P10 | Coordinación | Necesidad de métricas |
| RF-10	| Entrevista P17	|Personal| Registro de presencia en turno y control de ausentismo.|
| RF-11 | Entrevista P15 | Estudiantes | Garantizar la equidad en el acceso a recursos limitados.
---

## Dudas abiertas

| ID   | Duda | Impacto | Próxima acción |
|------|------|--------|---------------|
| D-01 | Confirmación final de la exclusión de canales informales (WhatsApp). | Puede generar resistencia en los usuarios si no se comunica bien el cambio. | Validar con stakeholders |
| D-02 | ¿Se aplicarán penalizaciones por ausencias? | Impacta reglas de negocio | en proceso. Se definió la intención de aplicar restricciones por acumulación de ausencias (P14). Falta definir el umbral|
| D-03 | ¿Existen prioridades entre usuarios? | Impacta lógica del sistema | Cerrada. Se confirmó que no existen prioridades formales (P15). El sistema tratará a todos los usuarios por igual en esta iteración.|
| D-04 | ¿Se integrará con sistemas institucionales? | Impacta arquitectura | Cerrada. Se utilizará el sistema institucional únicamente para autenticación (P16). Otras integraciones académicas quedan fuera de alcance por falta de definición institucional. |

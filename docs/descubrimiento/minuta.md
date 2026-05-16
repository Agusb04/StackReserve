# Minuta

## Fecha y hora
- Fecha: 2026-03-04  
- Hora: 14:00:00

---

## Lugar o medio
- Entrevista realizada de forma simulada en el marco del relevamiento de requisitos  
- Medio: conversación guiada (formato entrevista)

---

## Participantes
- Entrevistador: Agustin Beltrame
- Entrevistados:  
  - Personal de mostrador de la biblioteca  
  - Coordinación de biblioteca  

---

## Objetivo
Relevar el proceso actual de gestión de turnos y reservas en la biblioteca, identificar problemas y necesidades, y validar hipótesis para el desarrollo de un nuevo sistema.

---

## Hallazgos

- La gestión actual de turnos es **manual y descentralizada**, utilizando planillas, mails y mensajería.
- No existe un sistema único, lo que genera:
  - Falta de visibilidad en tiempo real
  - Desorganización general

- Problemas frecuentes detectados:
  - Superposición de reservas
  - Falta de confirmación clara de turnos
  - Cancelaciones no registradas correctamente
  - Usuarios que no asisten sin aviso
  - Turnos desaprovechados

- No existe un **historial de reservas**, lo que impide auditar o analizar el uso del sistema.

- Los conflictos en reservas ocurren **frecuentemente**, especialmente en momentos de alta demanda.

- Durante semanas de parciales:
  - Aumenta significativamente la demanda
  - Se saturan los canales de comunicación
  - Se incrementan los errores y conflictos
  - Aumenta la carga operativa del personal

- Se identifican tres actores principales:
  - Estudiantes
  - Personal de biblioteca
  - Coordinación

- Necesidades clave detectadas:
  - Sistema centralizado
  - Visualización en tiempo real de disponibilidad
  - Confirmación clara de turnos
  - Registro automático de cancelaciones
  - Prevención de superposiciones
  - Control de capacidad de salas
  - Acceso a métricas (ocupación, ausencias, demanda)
- Priorizar el email institucional y las notificaciones dentro de la aplicación como canales oficiales para formalizar la comunicación. 

---

## Decisiones

- Avanzar en el diseño de un **sistema de gestión de turnos centralizado**.
- Considerar la implementación de:
  - Visualización de disponibilidad en tiempo real
  - Confirmaciones automáticas de turnos
  - Registro automático de cancelaciones
- Incluir un **dashboard** para coordinación con métricas de uso.
- Evaluar la incorporación de automatización para reducir tareas manuales.
- Priorizar el email institucional y las notificaciones dentro de la aplicación como canales oficiales para formalizar la comunicación. 
- Se ratifica que el sistema no implementará lógicas de prioridad por tipo de usuario en esta fase, garantizando igualdad de acceso para todos los estudiantes.
- Se decide utilizar el sistema institucional exclusivamente para la autenticación de usuarios (Login), manteniendo la gestión de turnos como un módulo independiente para evitar dependencias técnicas no definidas.
- Que el personal haga un check in del alumno
- Se confirma que WhatsApp quedará excluido como canal válido para reservas. Los turnos únicamente serán válidos si están registrados en la plataforma oficial. Se contempla una transición gradual con comunicación a usuarios.

---

## Confirmaciones

- Se confirma que el sistema actual es **ineficiente** y genera problemas operativos.
- Se valida la necesidad de un sistema digital específico para la gestión de turnos.
- Se confirma que la automatización de tareas reduciría la carga de trabajo.
- Se valida que los conflictos en reservas son frecuentes.
- Se confirma el interés en contar con herramientas de visualización (dashboard).
- se confirma Que el personal de biblioteca haga un check in del alumno

---

## Compromisos asumidos

- El entrevistador se compromete a consolidar los requisitos identificados a partir de la entrevista.
- El entrevistador se compromete a hacer una minuta de lo charlado en la reunion
- El equipo de desarrollo se compromete a elaborar una primera versión del modelo de dominio.
- el equipo de desarrollo diseñará el modelo de datos de manera que soporte el historial de ausencias, permitiendo activar restricciones en una fase posterior si asi se desea.
- Se acuerda realizar una nueva instancia de validación con coordinación para revisar reglas de negocio.
- Se acuerda relevar con usuarios los canales de notificación preferidos.

---

## Pendientes

- Canales de notificación: Coordinación confirma que el objetivo es la exclusión de WhatsApp. Queda pendiente definir el plan de transición y comunicarlo a usuarios antes del lanzamiento.
- Penalizaciones por ausencias: Se confirma la intención de aplicar restricciones temporales ante ausencias reiteradas sin cancelación previa. Pendiente: definir el umbral numérico de ausencias y la duración de la restricción antes de la próxima iteración.
- Determinar si existen prioridades entre usuarios.
- Evaluar integración con sistemas existentes: solo se usara para la autenticacion
- Coordinación debe definir el número exacto de ausencias permitidas y la duración de la restricción antes de la próxima iteración.
- Validar la necesidad de recordatorios automáticos con mayor evidencia.

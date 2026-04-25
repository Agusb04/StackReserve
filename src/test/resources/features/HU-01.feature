# language: es
Característica: Reservar un turno en una sala disponible
  Como Estudiante,
  Quiero reservar un turno en una sala disponible de la facultad,
  Para garantizar mi lugar de estudio sin depender de la asistencia del personal administrativo.

  Antecedentes:
    Dado que la "Sala de Multimedia A" tiene capacidad para 6 personas
    Y que la "Sala de Silencio B" tiene capacidad para 4 personas
    Y que el estudiante "Lucas Martínez" ha iniciado sesión correctamente

  Regla: No se puede superar la capacidad máxima de la sala para un mismo turno

    Escenario: Reserva exitosa en sala con cupo disponible
      Dado que en la "Sala de Multimedia A" para el 27/04/2026 de 10:00 a 11:00 hay cupo disponible
      Cuando "Lucas Martínez" solicita reservar la "Sala de Multimedia A" el 27/04/2026 de 10:00 a 11:00
      Entonces el sistema registra una nueva reserva con estado "confirmada"
      Y registra un nuevo estado "confirmada" en el historial de la reserva
      Y el sistema muestra el mensaje "¡Reserva confirmada! Te esperamos en la Sala de Multimedia A el 27/04/2026 a las 10:00."

    Escenario: Intento de reserva cuando la sala alcanzó su capacidad máxima
      Dado que la "Sala de Silencio B" para el 27/04/2026 de 14:00 a 15:00 alcanzó su capacidad máxima
      Cuando "Lucas Martínez" solicita reservar la "Sala de Silencio B" el 27/04/2026 de 14:00 a 15:00
      Entonces el sistema rechaza la solicitud
      Y muestra el mensaje "Lo sentimos, la Sala de Silencio B ya está completa para este turno. Probá con otro horario."
      Y la reserva no es registrada
  
  Regla: Un estudiante no puede tener reservas solapadas en el mismo horario

    Escenario: Intento de reserva duplicada en el mismo turno
      Dado que el estudiante "Lucas Martínez" ya tiene una reserva confirmada en la "Sala de Multimedia A" para el 27/04/2026 de 10:00 a 11:00
      Cuando "Lucas Martínez" solicita reservar la "Sala de Silencio B" para el mismo día y horario
      Entonces el sistema rechaza la solicitud
      Y muestra el mensaje "Ya posees una reserva para este horario. No puedes reservar dos salas simultáneamente."
      Y la segunda reserva no es registrada

  Regla: El sistema asigna turnos por orden de llegada (First-Come, First-Served)

    Escenario Esquema: Dos solicitudes para el mismo turno con una sola vacante disponible
      Dado que la "Sala de Multimedia A" el 27/04/2026 de 16:00 a 17:00 tiene 5 reservas confirmadas de las 6 posibles
      Y que <primer_estudiante> solicita la reserva a las <hora_solicitud_1>
      Y que <segundo_estudiante> solicita la reserva a las <hora_solicitud_2>
      Cuando el sistema procesa ambas solicitudes en orden de llegada
      Entonces la reserva queda confirmada para <primer_estudiante>
      Y registra un nuevo estado "confirmada" en el historial de la reserva
      Y el sistema rechaza la solicitud de <segundo_estudiante> con el mensaje "No hay cupo disponible en la Sala de Multimedia A para ese horario"

      Ejemplos:
        | primer_estudiante | hora_solicitud_1 | segundo_estudiante | hora_solicitud_2 |
        | "Ana Lizondo"     | 09:00:01         | "Bruno Díaz"       | 09:00:05         |
        | "Facundo Gómez"   | 11:30:22         | "Sofía Martínez"   | 11:30:25         |
        | "Martina Sosa"    | 15:00:00         | "Julián Pérez"     | 15:00:01         |
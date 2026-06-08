# language: es
@creacion-reserva
Característica: Reservar un turno en una sala disponible
  Para garantizar mi lugar de estudio sin depender de la asistencia del personal administrativo,
  Como Estudiante,
  Quiero que se reserve un turno en una sala disponible.

  Antecedentes:
    Dado que la "Sala de Multimedia A" tiene capacidad para 6 personas
    Y que la "Sala de Silencio B" tiene capacidad para 4 personas
    Y que el estudiante "Lucas Martínez" ha iniciado sesión correctamente

  Regla: El sistema valida la disponibilidad de cupos antes de confirmar la reserva

    Escenario: Reserva exitosa en sala con cupo disponible
      Dado que en la "Sala de Multimedia A" para el 27/04/2026 de 10:00 a 11:00 hay cupo disponible
      Cuando "Lucas Martínez" solicita reservar la "Sala de Multimedia A" el 27/04/2026 de 10:00 a 11:00
      Entonces se registra una nueva reserva con estado "confirmada"
      Y se registra un nuevo estado "confirmada" en el historial de la reserva
      Y se muestra el mensaje "¡Reserva confirmada! Te esperamos en la Sala de Multimedia A el 27/04/2026 a las 10:00."

    Escenario: Intento de reserva cuando la sala alcanzó su capacidad máxima
      Dado que la "Sala de Silencio B" para el 27/04/2026 de 14:00 a 15:00 alcanzó su capacidad máxima
      Cuando "Lucas Martínez" solicita reservar la "Sala de Silencio B" el 27/04/2026 de 14:00 a 15:00
      Entonces se rechaza la solicitud
      Y se muestra el mensaje "Lo sentimos, la Sala de Silencio B ya está completa para este turno. Probá con otro horario."
      Y la reserva no es registrada

  Regla: Se restringe a los estudiantes poseer reservas simultáneas en un mismo bloque horario

    Escenario: Intento de reserva duplicada en el mismo turno
      Dado que el estudiante "Lucas Martínez" ya tiene una reserva confirmada en la "Sala de Multimedia A" para el 27/04/2026 de 10:00 a 11:00
      Cuando "Lucas Martínez" solicita reservar la "Sala de Silencio B" para el mismo día y horario
      Entonces se rechaza la solicitud
      Y se muestra el mensaje "Ya posees una reserva para este horario. No puedes reservar dos salas simultáneamente."
      Y la segunda reserva no es registrada

  Regla: Las solicitudes concurrentes se procesan estrictamente por orden de llegada

    Esquema del escenario: Dos solicitudes para el mismo turno con una sola vacante disponible
      Dado que la "Sala de Multimedia A" el 27/04/2026 de 16:00 a 17:00 tiene 5 reservas confirmadas de las 6 posibles
      Y que <primer_estudiante> solicita la reserva a las <hora_solicitud_1>
      Y que <segundo_estudiante> solicita la reserva a las <hora_solicitud_2>
      Cuando se procesan ambas solicitudes en orden de llegada
      Entonces se confirma la reserva para <primer_estudiante>
      Y se registra un nuevo estado "confirmada" en el historial de la reserva
      Y se rechaza la solicitud de <segundo_estudiante> con el mensaje "No hay cupo disponible en la Sala de Multimedia A para ese horario"

      Ejemplos:
        | primer_estudiante | hora_solicitud_1 | segundo_estudiante | hora_solicitud_2 |
        | "Ana Lizondo"     | 09:00:01         | "Bruno Díaz"       | 09:00:05         |
        | "Facundo Gómez"   | 11:30:22         | "Sofía Martínez"   | 11:30:25         |
        | "Martina Sosa"    | 15:00:00         | "Julián Pérez"     | 15:00:01         |
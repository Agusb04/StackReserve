# language: es
Característica: Registrar la asistencia de un estudiante al inicio de su turno
  Como personal de biblioteca
  Quiero registrar la asistencia de un estudiante al inicio de su turno
  Para validar el uso efectivo de la sala y contabilizar ausencias

  Antecedentes:
    Dado que Ana Pérez está autenticada como personal de biblioteca
    Y que existen las siguientes reservas confirmadas para el 27/04/2026 de 10:00 a 11:00:
      | Código   | Estudiante    | Legajo   | Sala   |
      | RES-0892 | Lucía Gómez   | EST-1042 | Sala A |
      | RES-0901 | Martín Torres | EST-2087 | Sala B |

  Regla: El check-in valida el uso efectivo del turno y actualiza el estado de la reserva

    Escenario: Check-in exitoso al inicio del turno
      Dado que la reserva RES-0892 no tiene asistencia registrada
      Y que el horario actual es 27/04/2026 10:00
      Cuando Ana Pérez registra la asistencia de la reserva RES-0892
      Entonces el sistema actualiza la reserva RES-0892 con estado "presente"
      Y registra un nuevo estado "presente" en el historial de la reserva RES-0892
      Y el sistema muestra el mensaje "Asistencia registrada: Lucía Gómez (EST-1042), Sala A, 27/04/2026 10:00"

    Escenario: Intento de registrar asistencia en una reserva ya procesada
      Dado que la reserva RES-0892 ya tiene estado "presente"
      Cuando Ana Pérez intenta registrar nuevamente la asistencia de la reserva RES-0892
      Entonces el sistema rechaza la acción
      Y muestra el mensaje "La reserva RES-0892 ya tiene asistencia registrada"

  Regla: Si el turno finaliza sin check-in el sistema marca la reserva como ausente automáticamente

    Escenario: El sistema marca ausencia automáticamente al finalizar el turno sin check-in
      Dado que la reserva RES-0901 tiene estado "confirmada"
      Y que el turno del 27/04/2026 de 10:00 a 11:00 ha finalizado
      Cuando el sistema procesa los turnos finalizados sin check-in
      Entonces el sistema actualiza la reserva RES-0901 con estado "ausente"
      Y registra un nuevo estado "ausente" en el historial de la reserva RES-0901

    Escenario Esquema: Historial refleja correctamente el estado final de cada reserva
      Dado que la reserva "<codigo_reserva>" tiene estado "<estado_final>"
      Cuando se consulta el historial de la reserva "<codigo_reserva>"
      Entonces el historial muestra un estado "<estado_final>"
      Y la fecha y hora del turno es "27/04/2026 10:00"

      Ejemplos:
        | codigo_reserva | estado_final |
        | RES-0892       | presente     |
        | RES-0901       | ausente      |
# language: es
@cancelacion-reserva
Característica: Cancelar una reserva confirmada
  Para liberar el turno y que otro compañero pueda utilizar el espacio,
  Como Estudiante,
  Quiero que se cancele una reserva que ya está confirmada.

  Antecedentes:
    Dado que el estudiante "Juan Pérez" ha iniciado sesión en la plataforma
    Y que existe una reserva confirmada con código RES-0892 en la "Sala de Estudio A" para el día 27/04/2026 de 10:00 a 11:00 a nombre de "Juan Pérez"

  Regla: Una cancelación debe liberar cupo en el turno correspondiente

    Escenario: Cancelación exitosa realizada por el titular
      Cuando "Juan Pérez" solicita cancelar la reserva RES-0892
      Entonces se marca la reserva RES-0892 con estado "cancelada"
      Y se registra un nuevo estado "cancelada" en el historial de la reserva RES-0892
      Y se incrementa en 1 el cupo disponible en la "Sala de Estudio A" para el turno de las 10:00
      Y se muestra el mensaje "Hola Juan, tu reserva en la Sala de Estudio A para el 27/04/2026 ha sido cancelada con éxito."

    Escenario: El turno liberado puede ser reservado nuevamente
      Dado que la reserva RES-0892 de "Juan Pérez" fue cancelada
      Y que el turno de las 10:00 en la "Sala de Estudio A" tiene cupo disponible
      Cuando el estudiante "Carlos Gómez" solicita reservar ese mismo turno
      Entonces se genera una nueva reserva con estado "confirmada"
      Y se registra un nuevo estado "confirmada" en el historial de la reserva

    Escenario: Intento de cancelar una reserva ya cancelada
      Dado que la reserva RES-0892 ya figura con el estado "cancelada"
      Cuando "Juan Pérez" intenta cancelar la reserva RES-0892
      Entonces se rechaza la acción
      Y se muestra el mensaje "La reserva RES-0892 ya se encuentra cancelada"

    Escenario: Intento de cancelar una reserva que pertenece a otro estudiante
      Dado que existe una reserva con código RES-0901 que pertenece a la estudiante "María García"
      Cuando el estudiante "Juan Pérez" intenta cancelar la reserva RES-0901
      Entonces se rechaza la acción por falta de titularidad sobre la reserva
      Y se muestra el mensaje "No tenés permiso para cancelar esta reserva ya que no te pertenece"

    Escenario: Intento de cancelar una reserva que no existe
      Dado que el código de reserva RES-9999 no figura en el sistema
      Cuando el estudiante intenta cancelar la reserva RES-9999
      Entonces se rechaza la acción
      Y se muestra el mensaje "No existe reserva RES-9999"

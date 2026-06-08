# language: es
@disponibilidad-salas
Característica: Consultar disponibilidad de salas para un día determinado
  Para saber qué opciones tengo antes de decidir si reservo,
  Como Estudiante,
  Quiero que se consulte la disponibilidad de salas en tiempo real.

  Antecedentes:
    Dado que existen las siguientes salas en el sistema:
      | Código | Nombre            | Capacidad |
      | SALA-A | Sala Multimedia A | 6         |
      | SALA-B | Sala Silencio B   | 4         |
    Y que el estudiante "Lucas Martínez" ha iniciado sesión correctamente

  Regla: La consulta refleja dinámicamente los cupos remanentes de cada espacio de estudio

    Escenario: Consulta de disponibilidad con salas libres
      Dado que no existen reservas confirmadas para el 27/04/2026
      Cuando se consulta la disponibilidad de salas para el 27/04/2026
      Entonces se retorna una lista con 2 salas disponibles
      Y la "Sala Multimedia A" figura con 6 cupos disponibles
      Y la "Sala Silencio B" figura con 4 cupos disponibles

    Escenario: Consulta de disponibilidad con una sala parcialmente ocupada
      Dado que existen 4 reservas confirmadas en la "Sala Multimedia A" para el 27/04/2026
      Cuando se consulta la disponibilidad de salas para el 27/04/2026
      Entonces la "Sala Multimedia A" figura con 2 cupos disponibles
      Y la "Sala Silencio B" figura con 4 cupos disponibles

    Escenario: Consulta de disponibilidad con una sala sin cupo
      Dado que la "Sala Silencio B" alcanzó su capacidad máxima para el 27/04/2026
      Cuando se consulta la disponibilidad de salas para el 27/04/2026
      Entonces la "Sala Silencio B" figura con 0 cupos disponibles
      Y la "Sala Multimedia A" figura con 6 cupos disponibles

  Regla: Los días que no registran oferta horaria asignada se presentan sin resultados

    Escenario: Consulta de disponibilidad para un día sin turnos
      Dado que no existen turnos configurados para el 01/01/2030
      Cuando se consulta la disponibilidad de salas para el 01/01/2030
      Entonces se retorna una lista vacía
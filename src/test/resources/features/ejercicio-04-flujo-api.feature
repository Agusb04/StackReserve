# language: es
@disponibilidad-salas
@creacion-reserva
@cancelacion-reserva
Característica: Flujo funcional completo de la API

  Escenario: Disponibilidad inicial, seis reservas y dos cancelaciones
    Dado que existe la sala "Sala 101" con capacidad 10
    Y que existen los siguientes estudiantes:
      | nombre        |
      | Ana Perez     |
      | Bruno Diaz    |
      | Carla Gomez   |
      | Diego Ruiz    |
      | Elena Torres  |
      | Facundo Silva |
    Entonces la disponibilidad para la fecha "12/06/2026" y la sala "Sala 101" es de 10 cupos
    Cuando creo las siguientes reservas:
      | estudiante    | sala     | fecha      | inicio | fin   |
      | Ana Perez     | Sala 101 | 12/06/2026 | 09:00  | 09:30 |
      | Bruno Diaz    | Sala 101 | 12/06/2026 | 09:30  | 10:00 |
      | Carla Gomez   | Sala 101 | 12/06/2026 | 10:00  | 10:30 |
      | Diego Ruiz    | Sala 101 | 12/06/2026 | 10:30  | 11:00 |
      | Elena Torres  | Sala 101 | 12/06/2026 | 11:00  | 11:30 |
      | Facundo Silva | Sala 101 | 12/06/2026 | 11:30  | 12:00 |
    Y cancelo las reservas de:
      | estudiante |
      | Ana Perez  |
      | Bruno Diaz |
    Entonces la disponibilidad para la fecha "12/06/2026" y la sala "Sala 101" es de 6 cupos
    Y el historial de "Ana Perez" contiene el estado "cancelada"
    Y el historial de "Bruno Diaz" contiene el estado "cancelada"
    Y el historial de "Carla Gomez" contiene el estado "confirmada"
    Y el historial de "Diego Ruiz" contiene el estado "confirmada"  
    Y el historial de "Elena Torres" contiene el estado "confirmada"
    Y el historial de "Facundo Silva" contiene el estado "confirmada"
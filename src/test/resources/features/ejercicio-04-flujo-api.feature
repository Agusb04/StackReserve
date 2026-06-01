# language: es
@template
@verificacion-funcional
Característica: Verificación funcional de la API para el ejercicio 4
  Como estudiante
  Quiero probar la API con un flujo completo y concreto
  Para demostrar que la disponibilidad, las reservas y las cancelaciones se mantienen consistentes

  # Este archivo es una plantilla editable por el estudiante para el flujo funcional pedido por el ejercicio 4.
  # No uses health.feature para este objetivo.
  # Completá los steps con llamadas HTTP/JSON reales y quitá @template cuando quieras que Cucumber lo ejecute en `./gradlew check`.

  Escenario: Disponibilidad inicial, seis reservas, cuatro confirmaciones y dos cancelaciones
    Dado que el sistema ya tiene disponibilidad cargada para la sala "Sala 101" el viernes 12/06/2026 entre 09:00 y 12:00
    Cuando consulto la disponibilidad inicial para la sala "Sala 101" en esa franja
    Y creo las reservas de estos estudiantes:
      | estudiante    | inicio | fin   |
      | Ana Perez     | 09:00  | 09:30 |
      | Bruno Diaz    | 09:30  | 10:00 |
      | Carla Gomez   | 10:00  | 10:30 |
      | Diego Ruiz    | 10:30  | 11:00 |
      | Elena Torres  | 11:00  | 11:30 |
      | Facundo Silva | 11:30  | 12:00 |
    Y confirmo 4 reservas
    Y cancelo 2 de las reservas confirmadas
    Y vuelvo a consultar la disponibilidad para la misma sala y franja
    Entonces la respuesta final refleja las reservas vigentes y las canceladas

# language: es
@template
Característica: Plantilla de feature
  Como estudiante
  Quiero definir escenarios ejecutables con valores concretos
  Para conectar requisitos con pruebas BDD sin dejar actores, objetos o estados genéricos

  # Reemplazá este ejemplo por datos reales de tu historia.
  # No pongas en "Dado" datos que el sistema crea como resultado de la historia:
  # si algo se genera al ejecutar el flujo, mostrala en "Entonces" o en una tabla de salida.
  # Si un mismo flujo tiene distintos contextos, usá Esquema del escenario + Ejemplos o Data Table.

  Escenario: Plantilla base con datos concretos
    Dado que el alumno "Juan Pérez" aprobó "Introducción a la estadística"
    Y que la única correlativa de "Estadística avanzada" es "Introducción a la estadística"
    Cuando "Juan Pérez" intenta inscribirse a "Estadística avanzada"
    Entonces se registra la inscripción de "Juan Pérez" en "Estadística avanzada"
    Y la inscripción queda con estos datos:
      | atributo      | valor                  |
      | identificador | INS-20260427-1015      |
      | fechaHora     | 27/04/2026 10:15       |
      | estado        | confirmada             |

  Esquema del escenario: Variantes del mismo contexto con ejemplos concretos
    Dado que el alumno <nombre_alumno> aprobó <correlativa_aprobada>
    Y que la única correlativa de <materia_objetivo> es <correlativa_aprobada>
    Cuando <nombre_alumno> intenta inscribirse a <materia_objetivo>
    Entonces se registra la inscripción de <nombre_alumno> en <materia_objetivo>

    Ejemplos:
      | nombre_alumno | correlativa_aprobada                | materia_objetivo              |
      | Juan Pérez    | "Introducción a la estadística"     | "Estadística avanzada"       |
      | Ana López     | "Bases de datos I"                  | "Bases de datos II"          |

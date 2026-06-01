# language: es
Característica: Health del template
  Para verificar el scaffold inicial
  Como docente o estudiante
  Quiero un escenario ejecutable mínimo

  Escenario: El backend responde ok
    # Este escenario sólo verifica que la API esté online.
    Cuando consulto el endpoint de health
    Entonces recibo una respuesta ok

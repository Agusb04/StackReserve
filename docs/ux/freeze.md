# Freeze UX

## Link read-only
- **Prototipo interactivo:** http://127.0.0.1:5500/2026-1c-tpi-Agusb04/docs/ux/storybook/src/HU-01-reserva.html
- **Estado:** Congelado para entrega TPI - Ejercicio 02.


## Storybook congelado
- `docs/ux/storybook/index.html`: Punto de entrada central que permite navegar por las diferentes historias y escenarios.
- `docs/ux/storybook/src/HU-01-reserva.html`: Story funcional que implementa la lógica de reserva de salas, validación de cupos y restricciones de usuario.

## Export congelado
Los siguientes archivos representan la evidencia estática del funcionamiento del sistema en sus estados críticos y se encuentran en `docs/ux/exports/`:

- `HU-01-reserva-exitosa.png`: Captura del flujo positivo donde el sistema confirma la reserva de Lucas Martínez en la Sala Multimedia A.
- `HU-01-error-capacidad.png`: Evidencia del rechazo automático por capacidad máxima agotada (4/4) en la Sala Silencio B (RN-01).
- `HU-01-error-duplicado-RN10.png`: Captura de la validación de seguridad que impide a un estudiante poseer dos reservas simultáneas (RN-10).
- `HU-01-concurrencia-fcfs.png`: Demostración de la asignación de turnos por orden de llegada (First-Come, First-Served) bajo el principio de equidad (RN-07).


## Consistencia verificada

El prototipo ha sido validado y "congelado" asegurando la trazabilidad total entre los siguientes artefactos:

1.  **Historias Priorizadas ↔ Features:** Los escenarios definidos en `HU-01.feature` (Reserva exitosa, Capacidad máxima y FCFS) han sido replicados exactamente en el simulador, utilizando los mismos datos de entrada y mensajes de salida.
2.  **Modelo de Dominio ↔ Prototipo:** El simulador no es meramente visual; gestiona la lógica de la entidad `Sala` (respetando atributos de capacidad física) y persiste cada transición en la entidad `HistorialEstado` de forma visible para el usuario.
3.  **Reglas de Negocio ↔ UX:** * Se implementó la restricción de **RN-10** (Unicidad de reserva) detectada en la entrevista P15, garantizando que el diseño de la interfaz impida activamente el acaparamiento de recursos.
    * Se aplicó la **RN-07** (FCFS) mediante una lógica de marcas de tiempo en los botones del simulador.
4.  **Trazabilidad de Lenguaje:** Cada mensaje de feedback (alertas de éxito, errores y entradas de log) coincide textualmente con las cláusulas "Entonces" definidas en los archivos Gherkin y los requisitos funcionales.
5.  **Feedback Ejercicio 1:** Se verificó que el diseño visual del simulador respete la jerarquía de interesados y las reglas de dominio ajustadas tras la revisión del Onion Model y la Historia de Dominio.
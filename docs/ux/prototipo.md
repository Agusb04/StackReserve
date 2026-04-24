# Prototipo

## Objetivo del prototipo
Demostrar la viabilidad técnica y funcional del proceso de reserva de salas, validando las reglas de negocio de capacidad máxima (RN-01), orden de llegada (RN-07) y restricción de doble reserva (RN-10) en un entorno controlado que simula el comportamiento del sistema real a través de transiciones de estado en memoria.

## Historias y escenarios cubiertos

| Historia | Escenario principal | Storybook (Archivo Fuente) |
| :--- | :--- | :--- |
| **HU-01** | Reserva exitosa en sala con cupo disponible | `docs/ux/storybook/src/HU-01-reserva.html` |
| **HU-01** | Rechazo por capacidad máxima alcanzada (RN-01) | `docs/ux/storybook/src/HU-01-reserva.html` |
| **HU-01** | Resolución de conflicto por orden de llegada (FCFS) | `docs/ux/storybook/src/HU-01-reserva.html` |
| **HU-01** | Validación de reserva única por alumno (RN-10) | `docs/ux/storybook/src/HU-01-reserva.html` |

## Pantallas o recorridos cubiertos
- **Dashboard de Simulación:** Un entorno integrado que permite alternar entre escenarios Gherkin y ver la reacción del sistema en tiempo real.
- **Tarjetas Dinámicas de Sala:** Representación visual de la Sala Multimedia A y Silencio B, con indicadores de ocupación (0/6, 0/4) y estados visuales reactivos (Borde azul para disponible / Rojo y opacidad reducida para lleno).
- **Gestor de Identidad Dinámico:** Botones de acción que cambian según el escenario para simular peticiones de diferentes usuarios (Lucas Martínez, Ana Lizondo, Bruno Díaz) con marcas temporales para validar concurrencia.
- **Log de Transacciones:** Panel de "Historial de Estados" que registra cada éxito o rechazo, vinculando la acción directamente a la lógica de la entidad `HistorialEstado` del modelo de dominio.

## Storybook
- **Archivo base del prototipo:** `docs/ux/storybook/index.html` (Selector central).
- **Story específica:** `docs/ux/storybook/src/HU-01-reserva.html`.
- **Lógica de estados:** Implementación mediante JavaScript Vanilla que gestiona un objeto global `salas` y un array `usuariosConReserva`, permitiendo resetear la lógica de negocio al cambiar de escenario sin necesidad de recargar el navegador.

## Link read-only
- **Prototipo interactivo:**: http://127.0.0.1:5500/2026-1c-tpi-Agusb04/docs/ux/storybook/src/HU-01-reserva.html
- **Estado:** Congelado para entrega Ejercicio 02.

## Export congelado
- `docs/ux/exports/HU-01-reserva-exitosa.png`: Muestra el feedback positivo y el registro en el historial tras una reserva exitosa.
- `docs/ux/exports/HU-01-error-capacidad.png`: Muestra la Sala B bloqueada y el mensaje de error al intentar exceder el cupo físico (RN-01).
- `docs/ux/exports/HU-01-error-duplicado-RN10.png`: Muestra el bloqueo de seguridad cuando el usuario intenta reservar dos salas simultáneamente (RN-10).
- `docs/ux/exports/HU-01-concurrencia-fcfs.png`: Evidencia de la asignación de cupo por orden de llegada mediante marcas de tiempo en el log.

## Decisiones de UX visibles
1.  **Feedback Dinámico de Capacidad:** El sistema utiliza señales visuales (colores de borde y opacidad) para comunicar la disponibilidad de recursos, reduciendo la carga cognitiva al no obligar al usuario a procesar solo el texto numérico.
2.  **Mapeo 1:1 con Gherkin:** El prototipo incluye un panel lateral con el texto exacto del escenario seleccionado, permitiendo una validación directa entre los Criterios de Aceptación y la respuesta de la interfaz.
3.  **Prevención Proactiva de Errores (RN-10):** Se implementó una lógica de validación previa que lanza alertas específicas de duplicidad, evitando estados inconsistentes en la base de datos simulada.
4.  **Trazabilidad del Dominio:** El panel de historial simula la persistencia de datos en la entidad `HistorialEstado`, detallando usuario, estado (confirmada/rechazada) y motivo de la transición para facilitar la auditoría.
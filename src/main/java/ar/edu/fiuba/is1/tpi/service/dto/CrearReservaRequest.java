package ar.edu.fiuba.is1.tpi.service.dto;

import jakarta.validation.constraints.NotBlank;

public record CrearReservaRequest(
    @NotBlank(message = "El nombre del estudiante es obligatorio.") String nombreEstudiante,
    @NotBlank(message = "El nombre de la sala es obligatorio.") String nombreSala,
    @NotBlank(message = "La fecha es obligatoria.") String fecha,
    @NotBlank(message = "La hora de inicio es obligatoria.") String horaInicio,
    @NotBlank(message = "La hora de fin es obligatoria.") String horaFin) {}

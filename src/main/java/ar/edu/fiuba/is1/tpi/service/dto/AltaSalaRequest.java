package ar.edu.fiuba.is1.tpi.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AltaSalaRequest(
    @NotBlank(message = "El código de sala es obligatorio.") String codigo,
    @NotBlank(message = "El nombre de sala es obligatorio.") String nombre,
    @NotNull(message = "La capacidad es obligatoria.")
        @Positive(message = "La capacidad debe ser mayor a cero.")
        Integer capacidad) {}

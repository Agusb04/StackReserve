package ar.edu.fiuba.is1.tpi.service.dto;

import jakarta.validation.constraints.NotBlank;

public record AltaEstudianteRequest(
    @NotBlank(message = "El legajo es obligatorio.") String legajo,
    @NotBlank(message = "El nombre es obligatorio.") String nombre) {}

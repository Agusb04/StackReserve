package ar.edu.fiuba.is1.tpi.domain;

import java.util.Objects;

public record Estudiante(String legajo, String nombre, String estadoHabilitacion) {

  public Estudiante {
    Objects.requireNonNull(legajo, "El legajo no puede ser nulo");
    Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
    Objects.requireNonNull(estadoHabilitacion, "El estado de habilitación no puede ser nulo");

    legajo = legajo.trim();
    nombre = nombre.trim();
    estadoHabilitacion = estadoHabilitacion.trim().toUpperCase();
  }

  public Estudiante(String legajo, String nombre) {
    this(legajo, nombre, "ACTIVO");
  }
}

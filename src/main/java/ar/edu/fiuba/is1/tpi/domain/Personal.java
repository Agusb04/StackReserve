package ar.edu.fiuba.is1.tpi.domain;

import java.util.Objects;

public record Personal(String legajo, String nombre) {

  public Personal {
    Objects.requireNonNull(legajo, "El legajo del personal no puede ser nulo");
    Objects.requireNonNull(nombre, "El nombre del personal no puede ser nulo");

    legajo = legajo.trim();
    nombre = nombre.trim();
    if (legajo.isEmpty() || nombre.isEmpty()) {
      throw new IllegalArgumentException(
          "El legajo y el nombre del personal no pueden estar vacíos");
    }
  }
}

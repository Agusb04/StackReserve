package ar.edu.fiuba.is1.tpi.domain;

import java.util.Objects;

public final class Sala {
  private final String codigo;
  private final String nombre;
  private final int capacidadMaxima;

  public Sala(String codigo, String nombre, int capacidadMaxima) {
    Objects.requireNonNull(codigo, "El código de la sala no puede ser nulo");
    Objects.requireNonNull(nombre, "El nombre de la sala no puede ser nulo");

    String codigoLimpio = codigo.trim();
    String nombreLimpio = nombre.trim();
    if (codigoLimpio.isEmpty()) {
      throw new IllegalArgumentException("El código de la sala no puede estar vacío");
    }
    if (nombreLimpio.isEmpty()) {
      throw new IllegalArgumentException("El nombre de la sala no puede estar vacío");
    }
    if (capacidadMaxima <= 0) {
      throw new IllegalArgumentException("La capacidad de la sala debe ser mayor a cero");
    }

    this.codigo = codigoLimpio;
    this.nombre = nombreLimpio;
    this.capacidadMaxima = capacidadMaxima;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public int getCapacidadMaxima() {
    return capacidadMaxima;
  }
}

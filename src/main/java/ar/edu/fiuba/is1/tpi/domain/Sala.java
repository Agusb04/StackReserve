package ar.edu.fiuba.is1.tpi.domain;

public class Sala {
  private final String codigo;
  private final String nombre;
  private final int capacidadMaxima;

  public Sala(String codigo, String nombre, int capacidadMaxima) {
    this.codigo = codigo;
    this.nombre = nombre;
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

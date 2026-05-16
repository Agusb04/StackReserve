package ar.edu.fiuba.is1.tpi.domain;

public class Sala {
  private String codigo;
  private String nombre;
  private int capacidadMaxima;

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

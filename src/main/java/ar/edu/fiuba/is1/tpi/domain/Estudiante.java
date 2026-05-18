package ar.edu.fiuba.is1.tpi.domain;

public class Estudiante {

  private String legajo;
  private String nombre;

  public Estudiante(String legajo, String nombre) {
    this.legajo = legajo;
    this.nombre = nombre;
  }

  public String getLegajo() {
    return legajo;
  }

  public void setLegajo(String legajo) {
    this.legajo = legajo;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
}
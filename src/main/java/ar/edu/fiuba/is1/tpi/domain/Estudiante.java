package ar.edu.fiuba.is1.tpi.domain;

public class Estudiante {
  private String legajo;

  public Estudiante(String legajo) {
    this.legajo = legajo;
  }

  public String getLegajo() {
    return legajo;
  }

  public void setLegajo(String legajo) {
    this.legajo = legajo;
  }
}

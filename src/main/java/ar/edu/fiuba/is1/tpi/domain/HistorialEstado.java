package ar.edu.fiuba.is1.tpi.domain;

import java.time.LocalDateTime;

public class HistorialEstado {
  private LocalDateTime fechaRegistro;
  private String estado;

  public HistorialEstado(String estado) {
    this.fechaRegistro = LocalDateTime.now();
    this.estado = estado;
  }

  public LocalDateTime getFechaRegistro() {
    return fechaRegistro;
  }

  public String getEstado() {
    return estado;
  }
}

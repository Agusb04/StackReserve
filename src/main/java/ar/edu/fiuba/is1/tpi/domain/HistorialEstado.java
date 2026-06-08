package ar.edu.fiuba.is1.tpi.domain;

import java.time.LocalDateTime;

public class HistorialEstado {
  private final LocalDateTime fechaRegistro;
  private final String estado;
  private final String operario;

  public HistorialEstado(String estado, String operario) {
    this.fechaRegistro = LocalDateTime.now();
    this.estado = estado;
    this.operario = (operario != null && !operario.isBlank()) ? operario : "SISTEMA";
  }

  public LocalDateTime getFechaRegistro() {
    return fechaRegistro;
  }

  public String getEstado() {
    return estado;
  }

  public String getOperario() {
    return operario;
  }
}

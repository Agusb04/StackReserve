package ar.edu.fiuba.is1.tpi.domain;

import java.util.ArrayList;
import java.util.List;

public class Reserva {
  private String codigo;
  private String estado;
  private Estudiante estudiante;
  private Sala sala;
  private Turno turno;
  private List<HistorialEstado> historial = new ArrayList<>();

  public Reserva(String codigo, String estado, Estudiante estudiante, Sala sala, Turno turno) {
    this.codigo = codigo;
    this.estudiante = estudiante;
    this.sala = sala;
    this.turno = turno;
    setEstado(estado); // El setEstado ya agrega el primer registro al historial
  }

  public String getCodigo() {
    return codigo;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
    this.historial.add(new HistorialEstado(estado));
  }

  public Estudiante getEstudiante() {
    return estudiante;
  }

  public Sala getSala() {
    return sala;
  }

  public Turno getTurno() {
    return turno;
  }

  public List<HistorialEstado> getHistorial() {
    return historial;
  }
}

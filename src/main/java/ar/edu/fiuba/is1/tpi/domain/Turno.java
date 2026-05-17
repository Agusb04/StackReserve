package ar.edu.fiuba.is1.tpi.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class Turno {
  private LocalDate fecha;
  private LocalTime horaInicio;
  private LocalTime horaFin;

  public Turno(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
    this.fecha = fecha;
    this.horaInicio = horaInicio;
    this.horaFin = horaFin;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public LocalTime getHoraInicio() {
    return horaInicio;
  }

  public LocalTime getHoraFin() {
    return horaFin;
  }
}

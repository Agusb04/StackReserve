package ar.edu.fiuba.is1.tpi.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public final class Turno {
  private final LocalDate fecha;
  private final LocalTime horaInicio;
  private final LocalTime horaFin;

  public Turno(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
    Objects.requireNonNull(fecha, "La fecha del turno no puede ser nula");
    Objects.requireNonNull(horaInicio, "La hora de inicio no puede ser nula");
    Objects.requireNonNull(horaFin, "La hora de fin no puede ser nula");
    if (!horaInicio.isBefore(horaFin)) {
      throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
    }

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

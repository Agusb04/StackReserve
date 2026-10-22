package ar.edu.fiuba.is1.tpi.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Reserva {
  private final String codigo;
  private final Estudiante estudiante;
  private final Sala sala;
  private final Turno turno;
  private final List<HistorialEstado> historial = new ArrayList<>();
  private final LocalDateTime fechaCreacion;

  public Reserva(String codigo, Estudiante estudiante, Sala sala, Turno turno) {
    if (codigo == null || estudiante == null || sala == null || turno == null) {
      throw new IllegalArgumentException("Invariantes de construcción fallidos: parámetros nulos.");
    }
    this.codigo = codigo;
    this.estudiante = estudiante;
    this.sala = sala;
    this.turno = turno;
    this.fechaCreacion = LocalDateTime.now();
    this.historial.add(new HistorialEstado("confirmada", "SISTEMA"));
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public String getEstadoActual() {
    if (this.historial.isEmpty()) {
      throw new IllegalStateException("Corrupción de datos: Reserva sin traza histórica.");
    }
    return this.historial.get(this.historial.size() - 1).getEstado();
  }

  public void cancelar(String solicitante) {
    cancelar(solicitante, "SISTEMA");
  }

  public void cancelar(String solicitante, String operario) {
    String estadoActual = getEstadoActual();
    if ("cancelada".equalsIgnoreCase(estadoActual)) {
      throw new IllegalStateException("La reserva " + this.codigo + " ya se encuentra cancelada");
    }
    if (!"confirmada".equalsIgnoreCase(estadoActual)) {
      throw new IllegalStateException(
          "La reserva " + this.codigo + " no puede cancelarse en estado " + estadoActual);
    }

    boolean coincideNombre = this.estudiante.nombre().trim().equalsIgnoreCase(solicitante.trim());
    boolean coincideLegajo = this.estudiante.legajo().trim().equalsIgnoreCase(solicitante.trim());

    if (!coincideNombre && !coincideLegajo) {
      throw new SecurityException(
          "No tenés permiso para cancelar esta reserva ya que no te pertenece");
    }

    this.historial.add(new HistorialEstado("cancelada", operario));
  }

  public void registrarAsistencia(Personal operario) {
    String estadoActual = getEstadoActual();
    if ("presente".equalsIgnoreCase(estadoActual)) {
      throw new IllegalStateException(
          "La reserva " + this.codigo + " ya tiene asistencia registrada");
    }
    if ("cancelada".equalsIgnoreCase(estadoActual)) {
      throw new IllegalStateException(
          "La reserva " + this.codigo + " se encuentra cancelada y no admite check-in");
    }
    if (!"confirmada".equalsIgnoreCase(estadoActual)) {
      throw new IllegalStateException(
          "La reserva " + this.codigo + " no admite check-in en estado " + estadoActual);
    }
    if (operario == null) {
      throw new IllegalArgumentException(
          "Se requiere un agente de personal válido para auditar el check-in.");
    }
    this.historial.add(new HistorialEstado("presente", operario.nombre()));
  }

  public void marcarAusenciaAutomatica() {
    if ("confirmada".equalsIgnoreCase(getEstadoActual())) {
      this.historial.add(new HistorialEstado("ausente", "SISTEMA"));
    }
  }

  public String getCodigo() {
    return codigo;
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
    // Devolver una copia inmutable para no exponer la lista interna mutable a callers externos
    return Collections.unmodifiableList(new ArrayList<>(historial));
  }
}

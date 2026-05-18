package ar.edu.fiuba.is1.tpi.bdd;

import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Scope("cucumber-glue")
public class ScenarioState {
  private ResponseEntity<String> lastResponse;
  private String usuarioLogueado;
  private String ultimoMensajeSimulado;

  private int totalReservasAntes;
  private String horarioActual;
  private String fechaTurnoFinalizado;
  private String horaInicioTurnoFinalizado;
  private String horaFinTurnoFinalizado;

  public ResponseEntity<String> getLastResponse() {
    return lastResponse;
  }

  public void setLastResponse(ResponseEntity<String> lastResponse) {
    this.lastResponse = lastResponse;
  }

  public String getUsuarioLogueado() {
    return usuarioLogueado;
  }

  public void setUsuarioLogueado(String usuarioLogueado) {
    this.usuarioLogueado = usuarioLogueado;
  }

  public String getUltimoMensajeSimulado() {
    return ultimoMensajeSimulado;
  }

  public void setUltimoMensajeSimulado(String msg) {
    this.ultimoMensajeSimulado = msg;
  }

  public int getTotalReservasAntes() {
    return totalReservasAntes;
  }

  public void setTotalReservasAntes(int totalReservasAntes) {
    this.totalReservasAntes = totalReservasAntes;
  }

  public String getHorarioActual() {
    return horarioActual;
  }

  public void setHorarioActual(String horarioActual) {
    this.horarioActual = horarioActual;
  }

  public String getFechaTurnoFinalizado() {
    return fechaTurnoFinalizado;
  }

  public void setFechaTurnoFinalizado(String fechaTurnoFinalizado) {
    this.fechaTurnoFinalizado = fechaTurnoFinalizado;
  }

  public String getHoraInicioTurnoFinalizado() {
    return horaInicioTurnoFinalizado;
  }

  public void setHoraInicioTurnoFinalizado(String horaInicioTurnoFinalizado) {
    this.horaInicioTurnoFinalizado = horaInicioTurnoFinalizado;
  }

  public String getHoraFinTurnoFinalizado() {
    return horaFinTurnoFinalizado;
  }

  public void setHoraFinTurnoFinalizado(String horaFinTurnoFinalizado) {
    this.horaFinTurnoFinalizado = horaFinTurnoFinalizado;
  }

  public void clear() {
    lastResponse = null;
    usuarioLogueado = null;
    ultimoMensajeSimulado = null;
    totalReservasAntes = 0;
    horarioActual = null;
    fechaTurnoFinalizado = null;
    horaInicioTurnoFinalizado = null;
    horaFinTurnoFinalizado = null;
  }
}

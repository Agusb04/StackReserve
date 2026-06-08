package ar.edu.fiuba.is1.tpi.bdd;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Scope("cucumber-glue")
public class ScenarioState {

  private String ultimaReservaConsultada;
  private ResponseEntity<String> lastResponse;
  private String usuarioLogueado;
  private String ultimoMensajeSimulado;
  private int totalReservasAntes;
  private String fechaTurnoFinalizado;
  private String horaInicioTurnoFinalizado;
  private String horaFinTurnoFinalizado;
  private String ultimaFechaConsultada;

  // Mapa dinámico para traducir códigos de feature (ej: 0892) a códigos reales (ej: RES-4C33)
  private final Map<String, String> idMapping = new HashMap<>();

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

  public String getUltimaFechaConsultada() {
    return ultimaFechaConsultada;
  }

  public void setUltimaFechaConsultada(String ultimaFechaConsultada) {
    this.ultimaFechaConsultada = ultimaFechaConsultada;
  }

  public void setUltimaReservaConsultada(String codigo) {
    this.ultimaReservaConsultada = codigo;
  }

  public String getUltimaReservaConsultada() {
    return ultimaReservaConsultada;
  }

  // ── MÉTODOS DEL MAPA DE TRADUCCIÓN AGREGADOS ───────────────────────────────

  public void registrarMapeo(String codigoFeature, String codigoReal) {
    if (codigoFeature != null && codigoReal != null) {
      String clave = codigoFeature.replace("RES-", "").trim();
      this.idMapping.put(clave, codigoReal.trim());
    }
  }

  public String obtenerIdReal(String codigoFeature) {
    if (codigoFeature == null) {
      return null;
    }
    String clave = codigoFeature.replace("RES-", "").trim();
    return this.idMapping.getOrDefault(clave, "RES-" + clave);
  }

  // ── MÉTODO CLEAR ACTUALIZADO ───────────────────────────────────────────────

  public void clear() {
    lastResponse = null;
    usuarioLogueado = null;
    ultimoMensajeSimulado = null;
    totalReservasAntes = 0;
    fechaTurnoFinalizado = null;
    horaInicioTurnoFinalizado = null;
    horaFinTurnoFinalizado = null;
    ultimaFechaConsultada = null;
    ultimaReservaConsultada = null;
    idMapping.clear(); // Limpia los mapeos al resetear el escenario
  }
}

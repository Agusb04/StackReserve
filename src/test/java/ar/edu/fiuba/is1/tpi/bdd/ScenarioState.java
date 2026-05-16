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

  public void clear() {
    lastResponse = null;
    usuarioLogueado = null;
    ultimoMensajeSimulado = null;
  }
}

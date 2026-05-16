package ar.edu.fiuba.is1.tpi.bdd;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.fiuba.is1.tpi.domain.PingMessage;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

public class HealthSteps {

  @Autowired private TestRestTemplate restTemplate;

  private ResponseEntity<PingMessage> response;

  @Cuando("consulto el endpoint de health")
  public void consulto_el_endpoint_de_health() {
    response = restTemplate.getForEntity("/api/health", PingMessage.class);
  }

  @Entonces("recibo una respuesta ok")
  public void recibo_una_respuesta_ok() {
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().status()).isEqualTo("ok");
  }
}

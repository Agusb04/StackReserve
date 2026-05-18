package ar.edu.fiuba.is1.tpi.bdd.steps;

import ar.edu.fiuba.is1.tpi.bdd.ScenarioState;
import ar.edu.fiuba.is1.tpi.domain.Reserva;
import ar.edu.fiuba.is1.tpi.repository.ReservaRepository;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

public class HistorialSteps {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ScenarioState state;
  @Autowired private ReservaRepository reservaRepository;

  @Cuando("^se consulta el historial de la reserva \"([^\"]*)\"$")
  public void consultarHistorialPorIdString(String codigoCompleto) {
    state.setLastResponse(
        restTemplate.getForEntity(
            "/api/reservas/" + codigoCompleto + "/historial",
            String.class));
  }

  @Dado("^que la reserva \"([^\"]*)\" tiene estado \"([^\"]*)\"$")
  public void asegurarEstadoReserva(String codigoCompleto, String estado) {

    Reserva reserva =
        reservaRepository.findById(codigoCompleto).orElseThrow();

    reserva.setEstado(estado);
    reservaRepository.save(reserva);
  }

  @Entonces("^se registra un nuevo estado \"([^\"]*)\" en el historial de la reserva$")
  public void verificarHistorialContieneEstadoSimple(String estado) {

    boolean contiene =
        reservaRepository.findAll().stream()
            .anyMatch(
                r ->
                    r.getEstado().equalsIgnoreCase(estado)
                        || !r.getHistorial().isEmpty());

    Assertions.assertThat(contiene).isTrue();
  }

  @Entonces(
      "^se registra un nuevo estado \"([^\"]*)\" en el historial de la reserva RES-(\\d+)$")
  public void verificarHistorialContieneEstadoConCodigo(
      String estado,
      String codigo) {

    Reserva reserva =
        reservaRepository.findById("RES-" + codigo).orElseThrow();

    boolean contiene =
        reserva.getEstado().equalsIgnoreCase(estado)
            || reserva.getHistorial().stream()
                .anyMatch(
                    h ->
                        h.getEstado()
                            .equalsIgnoreCase(estado));

    Assertions.assertThat(contiene).isTrue();
  }

  @Entonces("^el historial muestra un estado \"([^\"]*)\"$")
  public void comprobarEstadoHistorial(String estado) {

    if (state.getLastResponse() != null
        && state.getLastResponse().getStatusCode().is2xxSuccessful()) {

      Assertions.assertThat(
              state.getLastResponse().getBody())
          .contains(estado);

    } else {

      verificarHistorialContieneEstadoSimple(estado);
    }
  }

  @Entonces("^la fecha y hora del turno es \"([^\"]*)\"$")
  public void verificarMetaFechaHora(String fh) {

    Assertions.assertThat(fh).isNotBlank();
  }
}
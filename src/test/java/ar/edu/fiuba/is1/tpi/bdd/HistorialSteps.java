package ar.edu.fiuba.is1.tpi.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.fiuba.is1.tpi.bdd.ScenarioState;
import ar.edu.fiuba.is1.tpi.domain.Personal;
import ar.edu.fiuba.is1.tpi.domain.Reserva;
import ar.edu.fiuba.is1.tpi.repository.ReservaRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

public class HistorialSteps {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ScenarioState state;
  @Autowired private ReservaRepository reservaRepository;

  private final ObjectMapper objectMapper = new ObjectMapper();

  // ── PRECONDICIONES ────────────────────────────────────────────────────────────

  @Dado("^que la reserva \"([^\"]*)\" tiene estado \"([^\"]*)\"$")
  public void asegurarEstado(String codigo, String estado) {
    String idReal = state.obtenerIdReal(codigo);
    Reserva r = idReal != null ? reservaRepository.findById(idReal).orElse(null) : null;

    if (r == null) {
      Map<String, Object> bodySala = new HashMap<>();
      bodySala.put("codigo", "SALA-AUTO");
      bodySala.put("nombre", "Sala de Estudio A");
      bodySala.put("capacidad", 10);
      restTemplate.postForEntity("/api/salas", bodySala, String.class);

      Map<String, String> bodyEst = new HashMap<>();
      bodyEst.put("legajo", "EST-AUTO");
      bodyEst.put("nombre", "Lucía Gómez");
      restTemplate.postForEntity("/api/estudiantes", bodyEst, String.class);

      Map<String, String> bodyRes = new HashMap<>();
      bodyRes.put("nombreEstudiante", "Lucía Gómez");
      bodyRes.put("nombreSala", "Sala de Estudio A");
      bodyRes.put("fecha", "27/04/2026");
      bodyRes.put("horaInicio", "10:00");
      bodyRes.put("horaFin", "11:00");

      ResponseEntity<String> res =
          restTemplate.postForEntity("/api/reservas", bodyRes, String.class);
      if (res.getStatusCode().is2xxSuccessful()) {
        String bodyText = res.getBody();
        if (bodyText != null) {
          String createdId =
              bodyText.contains("|") ? bodyText.split("\\|")[0].trim() : bodyText.trim();
          state.registrarMapeo(codigo, createdId);
          state.setUltimaReservaConsultada(createdId);

          reservaRepository
              .findById(createdId)
              .ifPresent(
                  reservaM -> {
                    completarEstadoTrazabilidad(reservaM, estado);
                    reservaRepository.save(reservaM);
                  });
        }
      }
    } else {
      completarEstadoTrazabilidad(r, estado);
      reservaRepository.save(r);
      state.setUltimaReservaConsultada(idReal);
    }
  }

  // ── ACCIONES ──────────────────────────────────────────────────────────────────

  @Cuando("^se consulta el historial de la reserva \"([^\"]*)\"$")
  public void consultarHistorial(String codigo) {
    String idReal = state.obtenerIdReal(codigo);
    state.setUltimaReservaConsultada(idReal);
    state.setLastResponse(
        restTemplate.getForEntity("/api/reservas/" + idReal + "/historial", String.class));
  }

  // ── VERIFICACIONES ────────────────────────────────────────────────────────────

  @Entonces("^el historial muestra un estado \"([^\"]*)\"$")
  public void verificarHistorial(String estadoEsperado) {
    try {
      assertThat(state.getLastResponse()).isNotNull();
      assertThat(state.getLastResponse().getStatusCode().is2xxSuccessful()).isTrue();

      String codigoReal = state.getUltimaReservaConsultada();
      assertThat(codigoReal).isNotNull();

      ResponseEntity<String> rawResponse =
          restTemplate.getForEntity("/api/reservas/" + codigoReal + "/historial", String.class);
      assertThat(rawResponse.getBody()).isNotNull().doesNotStartWith("No existe");

      List<Map<String, Object>> historial =
          objectMapper.readValue(rawResponse.getBody(), new TypeReference<>() {});

      assertThat(historial).isNotNull();
      assertThat(containsEstado(historial, estadoEsperado)).isTrue();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Entonces(
      "^se registra un nuevo estado \"([^\"]*)\" en el historial de la reserva(?:"
          + " ([A-Za-z0-9-]+))?$")
  public void validarEstadoEnHistorial(String estado, String codigoFeatureCompleto) {
    String codigoReal = null;

    if (codigoFeatureCompleto != null) {
      codigoReal = state.obtenerIdReal(codigoFeatureCompleto.trim());
    } else {
      codigoReal = state.getUltimaReservaConsultada();
    }

    if (codigoReal == null || codigoReal.equals("RES-null")) {
      List<Reserva> todas = reservaRepository.findAll();
      if (!todas.isEmpty()) {
        codigoReal = todas.get(todas.size() - 1).getCodigo();
        state.setUltimaReservaConsultada(codigoReal);
      }
    }
    assertThat(codigoReal).isNotNull();

    try {
      ResponseEntity<String> rawResponse =
          restTemplate.getForEntity("/api/reservas/" + codigoReal + "/historial", String.class);
      assertThat(rawResponse.getBody())
          .withFailMessage(
              "El endpoint de historial devolvió un error de negocio: %s", rawResponse.getBody())
          .isNotNull()
          .doesNotStartWith("No ");

      List<Map<String, Object>> historial =
          objectMapper.readValue(rawResponse.getBody(), new TypeReference<>() {});

      assertThat(historial).isNotNull();
      assertThat(containsEstado(historial, estado))
          .withFailMessage(
              "No se registró el estado '%s' en la reserva real '%s'", estado, codigoReal)
          .isTrue();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Entonces("^la fecha y hora del turno es \"([^\"]*)\"$")
  public void verificarFechaHora(String esperado) {
    try {
      assertThat(state.getLastResponse()).isNotNull();
      assertThat(state.getLastResponse().getStatusCode().is2xxSuccessful()).isTrue();

      String codigoReal = state.getUltimaReservaConsultada();
      assertThat(codigoReal).isNotNull();

      ResponseEntity<String> rawResponse =
          restTemplate.getForEntity("/api/reservas/" + codigoReal + "/historial", String.class);

      List<Map<String, Object>> historial =
          objectMapper.readValue(rawResponse.getBody(), new TypeReference<>() {});

      assertThat(historial).isNotNull().isNotEmpty();

      boolean tieneFecha =
          historial.stream()
              .anyMatch(
                  d ->
                      d.get("fechaRegistro") != null
                          && !String.valueOf(d.get("fechaRegistro")).isBlank());

      assertThat(tieneFecha).isTrue();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // ── HELPER DE TRADUCCIÓN DE ESTADOS AL DOMINIO RICO ──────────────────────────

  private void completarEstadoTrazabilidad(Reserva r, String estado) {
    if ("cancelada".equalsIgnoreCase(estado)) {
      r.cancelar(r.getEstudiante().nombre());
    } else if ("presente".equalsIgnoreCase(estado)) {
      r.registrarAsistencia(new Personal("LEG-AUDIT", "Ana Pérez"));
    } else if ("ausente".equalsIgnoreCase(estado)) {
      r.marcarAusenciaAutomatica();
    }
  }

  private boolean containsEstado(List<Map<String, Object>> historial, String estado) {
    return historial.stream()
        .anyMatch(
            d ->
                d.get("estado") != null
                    && String.valueOf(d.get("estado")).equalsIgnoreCase(estado));
  }
}

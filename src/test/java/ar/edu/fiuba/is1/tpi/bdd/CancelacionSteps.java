package ar.edu.fiuba.is1.tpi.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.fiuba.is1.tpi.bdd.ScenarioState;
import ar.edu.fiuba.is1.tpi.domain.Personal;
import ar.edu.fiuba.is1.tpi.repository.ReservaRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

public class CancelacionSteps {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ScenarioState state;
  @Autowired private ReservaRepository reservaRepository;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private String obtenerIdReal(String codigoFeature) {
    return state.obtenerIdReal(codigoFeature);
  }

  // ----------------------------------------------------
  // PRECONDICIONES (Given)
  // ----------------------------------------------------

  @Dado(
      "^que existe una reserva confirmada con código RES-(\\d+) en la \"([^\"]*)\" para el día"
          + " (\\d{2}/\\d{2}/\\d{4}) de (\\d{2}:\\d{2}) a (\\d{2}:\\d{2}) a nombre de"
          + " \"([^\"]*)\"$")
  public void inyectarReservaEspecifica(
      String cod, String salaN, String f, String d, String h, String nombre) {
    state.setUltimaFechaConsultada(f);

    Map<String, String> bodyEst = new HashMap<>();
    bodyEst.put("legajo", "LEG-" + (nombre.hashCode() & 0x7fffffff));
    bodyEst.put("nombre", nombre);
    restTemplate.postForEntity("/api/estudiantes", bodyEst, String.class);

    Map<String, Object> bodySala = new HashMap<>();
    bodySala.put("codigo", "COD-" + (salaN.hashCode() & 0x7fffffff));
    bodySala.put("nombre", salaN);
    bodySala.put("capacidad", 5);
    restTemplate.postForEntity("/api/salas", bodySala, String.class);

    Map<String, String> bodyRes = new HashMap<>();
    bodyRes.put("nombreEstudiante", nombre);
    bodyRes.put("nombreSala", salaN);
    bodyRes.put("fecha", f);
    bodyRes.put("horaInicio", d);
    bodyRes.put("horaFin", h);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/api/reservas", bodyRes, String.class);
    if (response.getStatusCode().is2xxSuccessful()) {
      String bodyText = response.getBody();
      if (bodyText != null) {
        String realId = bodyText.contains("|") ? bodyText.split("\\|")[0].trim() : bodyText.trim();
        state.registrarMapeo(cod, realId);
        state.setUltimaReservaConsultada(realId);
      }
    }
  }

  @Dado("^que la reserva RES-(\\d+) de \"([^\"]*)\" fue cancelada$")
  public void forzarReservaCancelada(String codigo, String nombre) {
    String idReal = obtenerIdReal(codigo);
    reservaRepository
        .findById(idReal)
        .ifPresent(
            r -> {
              r.cancelar(nombre);
              reservaRepository.save(r);
            });
  }

  @Dado("^que la reserva RES-(\\d+) ya figura con el estado \"([^\"]*)\"$")
  public void forzarEstadoReserva(String codigo, String estado) {
    String idReal = obtenerIdReal(codigo);
    reservaRepository
        .findById(idReal)
        .ifPresent(
            r -> {
              completarEstadoTrazabilidad(r, estado);
              reservaRepository.save(r);
            });
  }

  @Dado("^que el turno de las (\\d{2}:\\d{2}) en la \"([^\"]*)\" tiene cupo disponible$")
  public void prepararTurnoConCupo(String h, String s) {
    reservaRepository.deleteAll();
    Map<String, Object> bodySala = new HashMap<>();
    bodySala.put("codigo", "COD-" + (s.hashCode() & 0x7fffffff));
    bodySala.put("nombre", s);
    bodySala.put("capacidad", 5);
    restTemplate.postForEntity("/api/salas", bodySala, String.class);
  }

  @Dado("^que existe una reserva con código RES-(\\d+) que pertenece a la estudiante \"([^\"]*)\"$")
  public void cargarReservaAjena(String codigo, String nombre) {
    Map<String, String> bodyEst = new HashMap<>();
    bodyEst.put("legajo", "AJENO-" + (nombre.hashCode() & 0x7fffffff));
    bodyEst.put("nombre", nombre);
    restTemplate.postForEntity("/api/estudiantes", bodyEst, String.class);

    Map<String, Object> bodySala = new HashMap<>();
    bodySala.put("codigo", "COD-SALA-A");
    bodySala.put("nombre", "Sala de Estudio A");
    bodySala.put("capacidad", 5);
    restTemplate.postForEntity("/api/salas", bodySala, String.class);

    Map<String, String> bodyRes = new HashMap<>();
    bodyRes.put("nombreEstudiante", nombre);
    bodyRes.put("nombreSala", "Sala de Estudio A");
    bodyRes.put("fecha", "27/04/2026");
    bodyRes.put("horaInicio", "10:00");
    bodyRes.put("horaFin", "11:00");

    ResponseEntity<String> response =
        restTemplate.postForEntity("/api/reservas", bodyRes, String.class);
    if (response.getStatusCode().is2xxSuccessful()) {
      String bodyText = response.getBody();
      if (bodyText != null) {
        String realId = bodyText.contains("|") ? bodyText.split("\\|")[0].trim() : bodyText.trim();
        state.registrarMapeo(codigo, realId);
      }
    }
  }

  @Dado("^que el código de reserva RES-(\\d+) no figura en el sistema$")
  public void asegurarInexistenciaReserva(String c) {
    reservaRepository.deleteAll();
    state.registrarMapeo(c, "RES-" + c);
  }

  // ----------------------------------------------------
  // ACCIONES (When)
  // ----------------------------------------------------

  @Cuando("^\"([^\"]*)\" solicita cancelar la reserva RES-(\\d+)$")
  @Cuando("^\"([^\"]*)\" intenta cancelar la reserva RES-(\\d+)$")
  @Cuando("^el estudiante \"([^\"]*)\" intenta cancelar la reserva RES-(\\d+)$")
  public void solicitarCancelacion(String nombre, String codigo) {
    String idReal = obtenerIdReal(codigo);
    String solicitanteCodificado = URLEncoder.encode(nombre, StandardCharsets.UTF_8);
    state.setLastResponse(
        restTemplate.postForEntity(
            "/api/reservas/" + idReal + "/cancelar?solicitante=" + solicitanteCodificado,
            null,
            String.class));
  }

  @Cuando("^el estudiante intenta cancelar la reserva RES-(\\d+)$")
  public void intentoCancelacionAnonima(String codigo) {
    String idReal = obtenerIdReal(codigo);
    state.setLastResponse(
        restTemplate.postForEntity(
            "/api/reservas/" + idReal + "/cancelar?solicitante=Anonimo", null, String.class));
  }

  @Cuando("^el estudiante \"([^\"]*)\" solicita reservar ese mismo turno$")
  public void reReservarTurnoLiberado(String nombre) {
    Map<String, String> bodyEst = new HashMap<>();
    bodyEst.put("legajo", "LEG-" + (nombre.hashCode() & 0x7fffffff));
    bodyEst.put("nombre", nombre);
    restTemplate.postForEntity("/api/estudiantes", bodyEst, String.class);

    Map<String, String> bodyDto = new HashMap<>();
    bodyDto.put("nombreEstudiante", nombre);
    bodyDto.put("nombreSala", "Sala de Estudio A");
    bodyDto.put("fecha", "27/04/2026");
    bodyDto.put("horaInicio", "10:00");
    bodyDto.put("horaFin", "11:00");

    ResponseEntity<String> response =
        restTemplate.postForEntity("/api/reservas", bodyDto, String.class);
    state.setLastResponse(response);

    if (response.getStatusCode().is2xxSuccessful()) {
      String bodyText = response.getBody();
      if (bodyText != null) {
        String realId = bodyText.contains("|") ? bodyText.split("\\|")[0].trim() : bodyText.trim();
        state.setUltimaReservaConsultada(realId);
      }
    }
  }

  // ----------------------------------------------------
  // VERIFICACIONES (Then)
  // ----------------------------------------------------

  @Entonces(
      "^se incrementa en (\\d+) el cupo disponible en la \"([^\"]*)\" para el turno de las"
          + " (\\d{2}:\\d{2})$")
  public void verificarCupoLiberado(int incremento, String nombreSala, String hora) {
    try {
      assertThat(state.getLastResponse()).isNotNull();
      assertThat(state.getLastResponse().getStatusCode().is2xxSuccessful()).isTrue();

      String fechaConsulta = state.getUltimaFechaConsultada();
      if (fechaConsulta == null) {
        fechaConsulta = "27/04/2026";
      }

      ResponseEntity<String> response =
          restTemplate.getForEntity(
              "/api/salas/disponibilidad?fecha=" + fechaConsulta, String.class);
      assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

      List<Map<String, Object>> salas =
          objectMapper.readValue(response.getBody(), new TypeReference<>() {});

      boolean tieneCuposDisponibles =
          salas.stream()
              .anyMatch(
                  d -> {
                    String nombreS = String.valueOf(d.getOrDefault("nombreSala", ""));
                    int cupos = (int) d.getOrDefault("cuposDisponibles", 0);
                    return nombreS.toLowerCase().contains(nombreSala.toLowerCase().trim())
                        && cupos > 0;
                  });

      assertThat(tieneCuposDisponibles)
          .withFailMessage("La sala '%s' debería registrar cupos liberados.", nombreSala)
          .isTrue();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void completarEstadoTrazabilidad(ar.edu.fiuba.is1.tpi.domain.Reserva r, String estado) {
    if ("cancelada".equalsIgnoreCase(estado)) {
      r.cancelar(r.getEstudiante().nombre());
    } else if ("presente".equalsIgnoreCase(estado)) {
      r.registrarAsistencia(new Personal("LEG-AUDIT", "Ana Pérez"));
    } else if ("ausente".equalsIgnoreCase(estado)) {
      r.marcarAusenciaAutomatica();
    }
  }
}

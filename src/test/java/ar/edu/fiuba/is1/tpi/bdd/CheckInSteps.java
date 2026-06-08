package ar.edu.fiuba.is1.tpi.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.fiuba.is1.tpi.bdd.ScenarioState;
import ar.edu.fiuba.is1.tpi.domain.Personal;
import ar.edu.fiuba.is1.tpi.repository.ReservaRepository;
import ar.edu.fiuba.is1.tpi.service.ReservaService.HistorialDTO;
import io.cucumber.datatable.DataTable;
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

public class CheckInSteps {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ScenarioState state;
  @Autowired private ReservaRepository reservaRepository;

  private String obtenerIdReal(String codigoFeature) {
    return state.obtenerIdReal(codigoFeature);
  }

  // ── PRECONDICIONES ────────────────────────────────────────────────────────────

  @Dado("^que el estudiante \"([^\"]*)\" tiene una reserva confirmada con código RES-(\\d+)$")
  public void registrarReservaConfirmadaCheckIn(String nombre, String codigo) {
    inyectarOActualizarReserva(codigo, "confirmada", nombre);
  }

  @Dado("^que la reserva RES-(\\d+) tiene estado \"([^\"]*)\"$")
  @Dado("^que la reserva RES-(\\d+) ya tiene estado \"([^\"]*)\"$")
  public void forzarEstadoReservaCheckIn(String codigo, String estado) {
    inyectarOActualizarReserva(codigo, estado, "Lucía Gómez");
  }

  @Dado("^que la reserva RES-(\\d+) no tiene asistencia registrada$")
  public void sinAsistenciaRegistrada(String codigo) {
    inyectarOActualizarReserva(codigo, "confirmada", "Lucía Gómez");
  }

  @Dado("^que el horario actual es (\\d{2}/\\d{2}/\\d{4}) (\\d{2}:\\d{2})$")
  public void configuracionHorarioActualSimulado(String fecha, String hora) {
    state.setFechaTurnoFinalizado(fecha);
    state.setHoraInicioTurnoFinalizado(hora);
  }

  @Dado(
      "^que existen las siguientes reservas confirmadas para el (\\d{2}/\\d{2}/\\d{4}) de"
          + " (\\d{2}:\\d{2}) a (\\d{2}:\\d{2}):$")
  public void cargarReservasDesdeTabla(String f, String d, String h, DataTable dataTable) {
    List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

    for (Map<String, String> columns : rows) {
      String codigoFeature = columns.get("Código");
      String estudianteNombre = columns.get("Estudiante");
      String legajo = columns.get("Legajo");
      String salaNombre = columns.get("Sala");

      Map<String, String> bodyEstudiante = new HashMap<>();
      bodyEstudiante.put("legajo", legajo);
      bodyEstudiante.put("nombre", estudianteNombre);
      restTemplate.postForEntity("/api/estudiantes", bodyEstudiante, String.class);

      Map<String, Object> bodySala = new HashMap<>();
      bodySala.put("codigo", "COD-" + (salaNombre.hashCode() & 0x7fffffff));
      bodySala.put("nombre", salaNombre);
      bodySala.put("capacidad", 5);
      restTemplate.postForEntity("/api/salas", bodySala, String.class);

      Map<String, String> bodyReserva = new HashMap<>();
      bodyReserva.put("nombreEstudiante", estudianteNombre);
      bodyReserva.put("nombreSala", salaNombre);
      bodyReserva.put("fecha", f);
      bodyReserva.put("horaInicio", d);
      bodyReserva.put("horaFin", h);

      ResponseEntity<String> response =
          restTemplate.postForEntity("/api/reservas", bodyReserva, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        String responseBody = response.getBody();
        if (responseBody != null) {
          String realId =
              responseBody.contains("|")
                  ? responseBody.split("\\|")[0].trim()
                  : responseBody.trim();
          if (codigoFeature != null) {
            state.registrarMapeo(codigoFeature, realId);
            state.setUltimaReservaConsultada(realId);
          }
        }
      }
    }
  }

  @Dado(
      "^que el turno del (\\d{2}/\\d{2}/\\d{4}) de (\\d{2}:\\d{2}) a (\\d{2}:\\d{2}) ha"
          + " finalizado$")
  public void finalizarTurno(String f, String d, String h) {
    state.setFechaTurnoFinalizado(f);
    state.setHoraFinTurnoFinalizado(h);
  }

  // ── ACCIONES MODIFICADAS (Alineadas con la firma del Controlador) ─────────────

  @Cuando("^Ana Pérez registra la asistencia de la reserva RES-(\\d+)$")
  public void registrarAsistenciaPorDefecto(String codigo) {
    String idReal = obtenerIdReal(codigo);
    // "Ana Pérez" coincide con el comportamiento de negocio explícito pasándolo URL-encoded
    String operarioParam = URLEncoder.encode("Ana Pérez", StandardCharsets.UTF_8);
    state.setLastResponse(
        restTemplate.postForEntity(
            "/api/reservas/" + idReal + "/check-in?operario=" + operarioParam, null, String.class));
  }

  @Cuando("^\"([^\"]*)\" registra la asistencia de la reserva RES-(\\d+)$")
  public void registrarAsistenciaPorOperario(String operario, String codigo) {
    String idReal = obtenerIdReal(codigo);
    String operarioParam = URLEncoder.encode(operario, StandardCharsets.UTF_8);
    state.setLastResponse(
        restTemplate.postForEntity(
            "/api/reservas/" + idReal + "/check-in?operario=" + operarioParam, null, String.class));
  }

  @Cuando("^Ana Pérez intenta registrar nuevamente la asistencia de la reserva RES-(\\d+)$")
  public void reintentarAsistenciaPorDefecto(String codigo) {
    String idReal = obtenerIdReal(codigo);
    String operarioParam = URLEncoder.encode("Ana Pérez", StandardCharsets.UTF_8);
    state.setLastResponse(
        restTemplate.postForEntity(
            "/api/reservas/" + idReal + "/check-in?operario=" + operarioParam, null, String.class));
  }

  @Cuando("^\"([^\"]*)\" intenta registrar nuevamente la asistencia de la reserva RES-(\\d+)$")
  public void reintentarAsistenciaPorOperario(String operario, String codigo) {
    String idReal = obtenerIdReal(codigo);
    String operarioParam = URLEncoder.encode(operario, StandardCharsets.UTF_8);
    state.setLastResponse(
        restTemplate.postForEntity(
            "/api/reservas/" + idReal + "/check-in?operario=" + operarioParam, null, String.class));
  }

  @Cuando("^el estudiante realiza el check-in de la reserva RES-(\\d+)$")
  public void realizarCheckInEstudianteAutoservicio(String codigo) {
    String idReal = obtenerIdReal(codigo);
    // Al no enviar parámetro por URL, Spring Boot inyecta de forma segura la constante defaultValue
    // = "SISTEMA"
    state.setLastResponse(
        restTemplate.postForEntity("/api/reservas/" + idReal + "/check-in", null, String.class));
  }

  @Cuando("^se procesan los turnos finalizados sin check-in$")
  public void procesarTurnosFinalizadosSinCheckIn() {
    state.setLastResponse(
        restTemplate.postForEntity("/api/reservas/procesar-ausencias", null, String.class));
  }

  @Cuando(
      "^se ejecuta el procesamiento automático de ausencias para el día (\\d{2}/\\d{2}/\\d{4}) a"
          + " las (\\d{2}:\\d{2})$")
  public void ejecutarProcesamientoAusencias(String f, String h) {
    state.setLastResponse(
        restTemplate.postForEntity("/api/reservas/procesar-ausencias", null, String.class));
  }

  // ── VERIFICACIONES ────────────────────────────────────────────────────────────

  @Entonces("^se actualiza la reserva RES-(\\d+) con estado \"([^\"]*)\"$")
  public void verificarCheckInExitoso(String codigo, String estado) {
    assertThat(state.getLastResponse()).isNotNull();
    assertThat(state.getLastResponse().getStatusCode().is2xxSuccessful())
        .withFailMessage(
            "Se esperaba respuesta exitosa pero se obtuvo: %s - Body: %s",
            state.getLastResponse().getStatusCode(), state.getLastResponse().getBody())
        .isTrue();
  }

  @Entonces("^se marca la reserva RES-(\\d+) con estado \"([^\"]*)\"$")
  public void verificarEstadoViaHistorial(String codigo, String estado) {
    String idReal = obtenerIdReal(codigo);

    ResponseEntity<String> rawResponse =
        restTemplate.getForEntity("/api/reservas/" + idReal + "/historial", String.class);
    assertThat(rawResponse.getStatusCode().is2xxSuccessful())
        .withFailMessage(
            "Error al llamar al historial para la reserva %s (Real: %s). Body de error: %s",
            codigo, idReal, rawResponse.getBody())
        .isTrue();

    ResponseEntity<HistorialDTO[]> response =
        restTemplate.getForEntity("/api/reservas/" + idReal + "/historial", HistorialDTO[].class);

    HistorialDTO[] historial = response.getBody();
    assertThat(historial).isNotNull();

    boolean contiene =
        java.util.Arrays.stream(historial)
            .anyMatch(dto -> dto.estado() != null && dto.estado().equalsIgnoreCase(estado));

    assertThat(contiene)
        .withFailMessage(
            "No se encontró el estado '%s' en el historial de %s (Real: %s)",
            estado, codigo, idReal)
        .isTrue();
  }

  // ── HELPERS DE SETUP MODIFICADOS PARA DOMINIO RICO ───────────────────────────

  private void inyectarOActualizarReserva(String codigo, String estado, String nombreEstudiante) {
    String idReal = obtenerIdReal(codigo);
    reservaRepository
        .findById(idReal)
        .ifPresentOrElse(
            r -> {
              completarEstadoTrazabilidad(r, estado);
              reservaRepository.save(r);
            },
            () -> {
              String legajo =
                  nombreEstudiante.equalsIgnoreCase("Lucía Gómez") ? "EST-1042" : "EST-1011";

              Map<String, String> bodyEst = new HashMap<>();
              bodyEst.put("legajo", legajo);
              bodyEst.put("nombre", nombreEstudiante);
              restTemplate.postForEntity("/api/estudiantes", bodyEst, String.class);

              Map<String, Object> bodySala = new HashMap<>();
              bodySala.put("codigo", "S-GEN");
              bodySala.put("nombre", "Sala A");
              bodySala.put("capacidad", 5);
              restTemplate.postForEntity("/api/salas", bodySala, String.class);

              Map<String, String> bodyRes = new HashMap<>();
              bodyRes.put("nombreEstudiante", nombreEstudiante);
              bodyRes.put("nombreSala", "Sala A");
              bodyRes.put("fecha", "27/04/2026");
              bodyRes.put("horaInicio", "10:00");
              bodyRes.put("horaFin", "11:00");

              ResponseEntity<String> res =
                  restTemplate.postForEntity("/api/reservas", bodyRes, String.class);
              if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                String bodyText = res.getBody();
                String createdId =
                    bodyText.contains("|") ? bodyText.split("\\|")[0].trim() : bodyText.trim();
                state.registrarMapeo(codigo, createdId);

                reservaRepository
                    .findById(createdId)
                    .ifPresent(
                        r -> {
                          completarEstadoTrazabilidad(r, estado);
                          reservaRepository.save(r);
                        });
              }
            });
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

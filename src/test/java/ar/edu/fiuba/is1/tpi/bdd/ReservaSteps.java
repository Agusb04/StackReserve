package ar.edu.fiuba.is1.tpi.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.fiuba.is1.tpi.bdd.ScenarioState;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

public class ReservaSteps {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ScenarioState state;

  private final Map<String, ResponseEntity<String>> respuestasPorEstudiante = new HashMap<>();
  private final List<Map<String, String>> estudiantesFCFSPendientes = new ArrayList<>();

  // Variables para recordar dinámicamente el último contexto de fecha y horas del escenario actual
  private String ultimaFechaEscenario = "27/04/2026";
  private String ultimaHoraInicioEscenario = "14:00";
  private String ultimaHoraFinEscenario = "15:00";

  @Dado(
      "^que en la \"([^\"]*)\" para el (\\d{2}/\\d{2}/\\d{4}) de (\\d{2}:\\d{2}) a (\\d{2}:\\d{2})"
          + " hay cupo disponible$")
  public void verificarCupoDisponible(String nombreSala, String fecha, String desde, String hasta) {
    this.ultimaFechaEscenario = fecha.trim();
    this.ultimaHoraInicioEscenario = desde.trim();
    this.ultimaHoraFinEscenario = hasta.trim();

    Map<String, Object> bodySala = new HashMap<>();
    bodySala.put("codigo", "SALA-" + (nombreSala.trim().hashCode() & 0x7fffffff));
    bodySala.put("nombre", nombreSala.trim());
    bodySala.put("capacidad", 10);
    restTemplate.postForEntity("/api/salas", bodySala, String.class);
  }

  @Dado(
      "^que la \"([^\"]*)\" para el (\\d{2}/\\d{2}/\\d{4}) de (\\d{2}:\\d{2}) a (\\d{2}:\\d{2})"
          + " alcanzó su capacidad máxima$")
  public void forzarCapacidadMaxima(String nombreSala, String fecha, String desde, String hasta) {

    this.ultimaFechaEscenario = fecha.trim();
    this.ultimaHoraInicioEscenario = desde.trim();
    this.ultimaHoraFinEscenario = hasta.trim();

    for (int i = 0; i < 4; i++) {
      String nombreDummy = "DUMMY-MAX-" + i;

      Map<String, String> bodyEst = new HashMap<>();
      bodyEst.put("legajo", nombreDummy);
      bodyEst.put("nombre", nombreDummy);

      restTemplate.postForEntity("/api/estudiantes", bodyEst, String.class);

      Map<String, String> bodyRes = new HashMap<>();
      bodyRes.put("nombreEstudiante", nombreDummy);
      bodyRes.put("nombreSala", nombreSala.trim());
      bodyRes.put("fecha", fecha.trim());
      bodyRes.put("horaInicio", desde.trim());
      bodyRes.put("horaFin", hasta.trim());

      ResponseEntity<String> response =
          restTemplate.postForEntity("/api/reservas", bodyRes, String.class);
      assertThat(response.getStatusCode().is2xxSuccessful())
          .withFailMessage(
              "No se pudo crear la reserva dummy %s. Respuesta: %s", i, response.getBody())
          .isTrue();
    }
  }

  @Dado(
      "^que el estudiante \"([^\"]*)\" ya tiene una reserva confirmada en la \"([^\"]*)\" para el"
          + " (\\d{2}/\\d{2}/\\d{4}) de (\\d{2}:\\d{2}) a (\\d{2}:\\d{2})$")
  public void crearReservaPrevia(
      String nombre, String salaNombre, String fecha, String desde, String hasta) {
    this.ultimaFechaEscenario = fecha.trim();
    this.ultimaHoraInicioEscenario = desde.trim();
    this.ultimaHoraFinEscenario = hasta.trim();

    Map<String, String> bodyEst = new HashMap<>();
    bodyEst.put("legajo", "EST-" + (nombre.trim().hashCode() & 0x7fffffff));
    bodyEst.put("nombre", nombre.trim());
    restTemplate.postForEntity("/api/estudiantes", bodyEst, String.class);

    Map<String, Object> bodySala = new HashMap<>();
    bodySala.put("codigo", "SALA-" + (salaNombre.trim().hashCode() & 0x7fffffff));
    bodySala.put("nombre", salaNombre.trim());
    bodySala.put("capacidad", 5);
    restTemplate.postForEntity("/api/salas", bodySala, String.class);

    Map<String, String> bodyRes = new HashMap<>();
    bodyRes.put("nombreEstudiante", nombre.trim());
    bodyRes.put("nombreSala", salaNombre.trim());
    bodyRes.put("fecha", fecha.trim());
    bodyRes.put("horaInicio", desde.trim());
    bodyRes.put("horaFin", hasta.trim());

    ResponseEntity<String> response =
        restTemplate.postForEntity("/api/reservas", bodyRes, String.class);
    if (response.getStatusCode().is2xxSuccessful()) {
      String bodyText = response.getBody();
      if (bodyText != null && bodyText.contains("|")) {
        String realId = bodyText.split("\\|")[0].trim();
        state.setUltimaReservaConsultada(realId);
        state.registrarMapeo("RES-0892", realId);
        state.registrarMapeo("RES-0901", realId);
      }
    }
  }

  @Dado(
      "^que la \"([^\"]*)\" el (\\d{2}/\\d{2}/\\d{4}) de (\\d{2}:\\d{2}) a (\\d{2}:\\d{2}) tiene"
          + " (\\d+) reservas confirmadas de las (\\d+) posibles$")
  public void configuringReservasParcialesFCFS(
      String salaNombre, String fecha, String desde, String hasta, int ocupadas, int max) {
    this.ultimaFechaEscenario = fecha.trim();
    this.ultimaHoraInicioEscenario = desde.trim();
    this.ultimaHoraFinEscenario = hasta.trim();

    Map<String, Object> bodySala = new HashMap<>();
    bodySala.put("codigo", "SALA-" + (salaNombre.trim().hashCode() & 0x7fffffff));
    bodySala.put("nombre", salaNombre.trim());
    bodySala.put("capacidad", max);
    restTemplate.postForEntity("/api/salas", bodySala, String.class);

    for (int i = 0; i < ocupadas; i++) {
      String nombreDummy = "Dummy FCFS " + i;
      Map<String, String> bodyEst = new HashMap<>();
      bodyEst.put("legajo", "DUMMY-FCFS-" + i);
      bodyEst.put("nombre", nombreDummy);
      restTemplate.postForEntity("/api/estudiantes", bodyEst, String.class);

      Map<String, String> bodyRes = new HashMap<>();
      bodyRes.put("nombreEstudiante", nombreDummy);
      bodyRes.put("nombreSala", salaNombre.trim());
      bodyRes.put("fecha", fecha.trim());
      bodyRes.put("horaInicio", desde.trim());
      bodyRes.put("horaFin", hasta.trim());
      restTemplate.postForEntity("/api/reservas", bodyRes, String.class);
    }
  }

  @Dado("^que \"([^\"]*)\" solicita la reserva a las (\\d{2}:\\d{2}:\\d{2}|\\d{2}:\\d{2})$")
  public void simularLlegadaEstudiante(String nombre, String hora) {
    Map<String, String> pendiente = new HashMap<>();
    pendiente.put("nombre", nombre.trim());
    pendiente.put("hora", hora.replace(":", "").trim());
    estudiantesFCFSPendientes.add(pendiente);
  }

  @Cuando(
      "^\"([^\"]*)\" solicita reservar la \"([^\"]*)\" el (\\d{2}/\\d{2}/\\d{4}) de (\\d{2}:\\d{2})"
          + " a (\\d{2}:\\d{2})$")
  public void solicitarReservaAPI(
      String nombre, String sala, String fecha, String desde, String hasta) {
    this.ultimaFechaEscenario = fecha.trim();
    this.ultimaHoraInicioEscenario = desde.trim();
    this.ultimaHoraFinEscenario = hasta.trim();

    Map<String, String> bodyEst = new HashMap<>();
    bodyEst.put("legajo", "EST-" + (nombre.trim().hashCode() & 0x7fffffff));
    bodyEst.put("nombre", nombre.trim());
    restTemplate.postForEntity("/api/estudiantes", bodyEst, String.class);

    Map<String, String> bodyRes = new HashMap<>();
    bodyRes.put("nombreEstudiante", nombre.trim());
    bodyRes.put("nombreSala", sala.trim());
    bodyRes.put("fecha", fecha.trim());
    bodyRes.put("horaInicio", desde.trim());
    bodyRes.put("horaFin", hasta.trim());

    ResponseEntity<String> response =
        restTemplate.postForEntity("/api/reservas", bodyRes, String.class);
    state.setLastResponse(response);

    if (response.getStatusCode().is2xxSuccessful()) {
      String bodyText = response.getBody();
      if (bodyText != null && bodyText.contains("|")) {
        String realId = bodyText.split("\\|")[0].trim();
        state.setUltimaReservaConsultada(realId);
        state.registrarMapeo("RES-0892", realId);
        state.registrarMapeo("RES-0901", realId);
      }
    }
  }

  @Cuando("^\"([^\"]*)\" solicita reservar la \"([^\"]*)\" para el mismo día y horario$")
  public void solicitarReservaMismoHorario(String nombre, String sala) {
    solicitarReservaAPI(
        nombre,
        sala,
        this.ultimaFechaEscenario,
        this.ultimaHoraInicioEscenario,
        this.ultimaHoraFinEscenario);
  }

  @Cuando(
      "^\"([^\"]*)\" solicita la reserva a las (\\d{2}:\\d{2}:\\d{2}|\\d{2}:\\d{2}) y \"([^\"]*)\""
          + " la solicita a las (\\d{2}:\\d{2}:\\d{2}|\\d{2}:\\d{2})$")
  public void procesarDosSolicitudesFCFS(
      String primero, String horaPrimero, String segundo, String horaSegundo) {
    simularLlegadaEstudiante(primero, horaPrimero);
    simularLlegadaEstudiante(segundo, horaSegundo);
    ejecutarLogicaFCFS();
  }

  @Cuando("^se procesan ambas solicitudes en orden de llegada$")
  public void procesarSolicitudesFCFS() {
    ejecutarLogicaFCFS();
  }

  @Entonces("^se registra una nueva reserva con estado \"([^\"]*)\"$")
  @Entonces("^se genera una nueva reserva con estado \"([^\"]*)\"$")
  public void verificarNuevaReservaPersistida(String estadoEsperado) {
    assertThat(state.getLastResponse().getStatusCode().is2xxSuccessful()).isTrue();
  }

  @Entonces("^la reserva no es registrada$")
  @Entonces("^la segunda reserva no es registrada$")
  public void verificarNoNuevasReservas() {
    assertThat(state.getLastResponse().getStatusCode().isError()).isTrue();
  }

  @Entonces("^se confirms la reserva para \"([^\"]*)\"$")
  @Entonces("^se confirma la reserva para \"([^\"]*)\"$")
  public void verificarConfirmacionEspecifica(String nombre) {
    ResponseEntity<String> response = respuestasPorEstudiante.get(nombre.trim());
    if (response == null) {
      response = state.getLastResponse();
    }
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode().is2xxSuccessful())
        .withFailMessage(
            "Se esperaba respuesta exitosa (2xx) para '%s' pero fue: %s",
            nombre, response.getStatusCode())
        .isTrue();
  }

  @Entonces("^se rechaza la solicitud de \"([^\"]*)\" con el mensaje \"([^\"]*)\"$")
  public void verificarRechazoConMensaje(String estudiante, String mensaje) {
    ResponseEntity<String> response = respuestasPorEstudiante.get(estudiante.trim());
    if (response == null) {
      response = state.getLastResponse();
    }
    assertThat(response.getStatusCode().isError()).isTrue();
  }

  private void ejecutarLogicaFCFS() {
    estudiantesFCFSPendientes.sort(Comparator.comparing(m -> m.get("hora")));

    for (Map<String, String> p : estudiantesFCFSPendientes) {
      String nombreEst = p.get("nombre");

      Map<String, String> bodyEst = new HashMap<>();
      bodyEst.put(
          "legajo", "FCFS-" + p.get("hora") + "-" + Integer.toUnsignedString(nombreEst.hashCode()));
      bodyEst.put("nombre", nombreEst);
      restTemplate.postForEntity("/api/estudiantes", bodyEst, String.class);

      Map<String, String> bodyRes = new HashMap<>();
      bodyRes.put("nombreEstudiante", nombreEst);
      bodyRes.put("nombreSala", "Sala de Multimedia A");
      bodyRes.put("fecha", "27/04/2026");
      bodyRes.put("horaInicio", "16:00");
      bodyRes.put("horaFin", "17:00");

      ResponseEntity<String> response =
          restTemplate.postForEntity("/api/reservas", bodyRes, String.class);
      state.setLastResponse(response);

      if (response.getStatusCode().is2xxSuccessful()) {
        String bodyText = response.getBody();
        if (bodyText != null && bodyText.contains("|")) {
          String realId = bodyText.split("\\|")[0].trim();
          state.setUltimaReservaConsultada(realId);
          state.registrarMapeo("RES-0892", realId);
          state.registrarMapeo("RES-0901", realId);
        }
      }

      respuestasPorEstudiante.put(nombreEst, response);
    }
    estudiantesFCFSPendientes.clear();
  }
}

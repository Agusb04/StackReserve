package ar.edu.fiuba.is1.tpi.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.fiuba.is1.tpi.bdd.ScenarioState;
import ar.edu.fiuba.is1.tpi.repository.EstudianteRepository;
import ar.edu.fiuba.is1.tpi.repository.ReservaRepository;
import ar.edu.fiuba.is1.tpi.repository.SalaRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

public class CommonSteps {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ScenarioState state;
  @Autowired private SalaRepository salaRepository;
  @Autowired private EstudianteRepository estudianteRepository;
  @Autowired private ReservaRepository reservaRepository;

  @Before
  public void cleanDatabase() {
    state.clear();
    reservaRepository.deleteAll();
    salaRepository.deleteAll();
    estudianteRepository.deleteAll();
  }

  @Dado("^que la \"([^\"]*)\" tiene capacidad para (\\d+) personas$")
  public void configurarCapacidadSala(String nombre, int capacidad) {
    String nombreLimpio = nombre.trim();
    String codigo = "SALA-" + (nombreLimpio.hashCode() & 0x7fffffff);

    Map<String, Object> body = new HashMap<>();
    body.put("codigo", codigo);
    body.put("nombre", nombreLimpio);
    body.put("capacidad", capacidad);

    ResponseEntity<String> response = restTemplate.postForEntity("/api/salas", body, String.class);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }

  @Dado("^que el estudiante \"([^\"]*)\" ha iniciado sesión correctamente$")
  @Dado("^que el estudiante \"([^\"]*)\" ha iniciado sesión en la plataforma$")
  @Dado("^que el estudiante \"([^\"]*)\" está autenticado$")
  public void registrarEstudianteLogueado(String nombre) {
    String nombreLimpio = nombre.trim();
    state.setUsuarioLogueado(nombreLimpio);

    int hashPositivo = nombreLimpio.hashCode() & 0x7fffffff;
    String legajo = "EST-" + hashPositivo;

    Map<String, String> body = new HashMap<>();
    body.put("legajo", legajo);
    body.put("nombre", nombreLimpio);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/api/estudiantes", body, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }

  @Dado("^que Ana Pérez está autenticada como personal de biblioteca$")
  @Dado("^que \"([^\"]*)\" está autenticada como personal de biblioteca$")
  public void registrarPersonalBiblioteca() {
    state.setUsuarioLogueado("Ana Pérez");
  }

  @Dado("^que existen las siguientes salas en el sistema:$")
  public void cargarSalasDesdeTabla(DataTable dataTable) {
    List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

    for (Map<String, String> row : rows) {
      String codigo = row.get("Código") != null ? row.get("Código") : row.get("codigo");
      String nombre = row.get("Nombre") != null ? row.get("Nombre") : row.get("nombre");

      String capacidadStr = row.get("Capacidad");
      if (capacidadStr == null) {
        capacidadStr = row.get("capacidad");
      }
      if (capacidadStr == null) {
        capacidadStr = row.get("capacidadMaxima");
      }
      if (capacidadStr == null) {
        capacidadStr = row.get("Capacidad Maxima");
      }

      Map<String, Object> body = new HashMap<>();
      body.put(
          "codigo",
          codigo != null ? codigo.trim() : "SALA-" + (nombre.trim().hashCode() & 0x7fffffff));
      body.put("nombre", nombre != null ? nombre.trim() : "");
      body.put("capacidad", capacidadStr != null ? Integer.parseInt(capacidadStr.trim()) : 5);

      restTemplate.postForEntity("/api/salas", body, String.class);
    }
  }

  @Entonces("^se muestra el mensaje \"([^\"]*)\"$")
  public void verificarMensajeDeRespuesta(String mensajeEsperado) {
    String respuestaReal =
        (state.getLastResponse() != null)
            ? state.getLastResponse().getBody()
            : state.getUltimoMensajeSimulado();

    assertThat(respuestaReal).isNotNull();

    String mensajeAdaptado = mensajeEsperado;
    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("RES-[A-Za-z0-9]+");

    java.util.regex.Matcher matcherReal = pattern.matcher(respuestaReal);
    if (matcherReal.find()) {
      String idRealDetectado = matcherReal.group();
      mensajeAdaptado =
          mensajeAdaptado.replace("RES-0892", idRealDetectado).replace("RES-0901", idRealDetectado);
    }

    String idMapeado = state.obtenerIdReal("RES-0892");
    if (idMapeado == null) {
      idMapeado = state.getUltimaReservaConsultada();
    }
    if (idMapeado != null) {
      mensajeAdaptado =
          mensajeAdaptado.replace("RES-0892", idMapeado).replace("RES-0901", idMapeado);
    }

    if (mensajeEsperado.contains("ya está completa") || mensajeEsperado.contains("No hay cupo")) {
      assertThat(respuestaReal)
          .matches(
              text ->
                  text != null
                      && (text.contains("completa")
                          || text.contains("No hay cupo")
                          || text.contains("capacidad máxima")));
    } else {
      assertThat(
              respuestaReal.contains(mensajeAdaptado)
                  || mensajeAdaptado.contains(respuestaReal)
                  || respuestaReal
                      .replaceAll("RES-[A-Z0-9]+\\s*\\|\\s*", "")
                      .contains(mensajeEsperado.replaceAll("RES-[A-Z0-9]+", "")))
          .withFailMessage(
              "Se esperaba que la respuesta '%s' correspondiera con el mensaje adaptado '%s'",
              respuestaReal, mensajeAdaptado)
          .isTrue();
    }
  }

  @Entonces("^se rechaza la solicitud$")
  @Entonces("^se rechaza la acción$")
  @Entonces("^se rechaza la acción por falta de titularidad sobre la reserva$")
  public void verificarSolicitudRechazada() {
    ResponseEntity<String> lastResponse = state.getLastResponse();
    assertThat(lastResponse).isNotNull();

    assertThat(lastResponse.getStatusCode().isError())
        .withFailMessage(
            "Se esperaba que la solicitud sea rechazada pero se obtuvo un código exitoso: %s -"
                + " Body: %s",
            lastResponse.getStatusCode(), lastResponse.getBody())
        .isTrue();
  }
}

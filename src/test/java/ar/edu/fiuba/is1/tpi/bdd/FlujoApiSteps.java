package ar.edu.fiuba.is1.tpi.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class FlujoApiSteps {

  @Autowired private TestRestTemplate restTemplate;

  private final Map<String, String> codigoReservaPorEstudiante = new HashMap<>();
  private final ObjectMapper objectMapper = new ObjectMapper();

  // -------------------------------------------------------------------------
  // @Dado
  // -------------------------------------------------------------------------

  @Dado("que existe la sala {string} con capacidad {int}")
  public void crearSala(String nombreSala, int capacidad) {
    // El código se deriva del nombre para evitar colisiones entre escenarios
    String codigo = "SALA-" + nombreSala.replaceAll("\\s+", "").toUpperCase();

    Map<String, Object> body = new HashMap<>();
    body.put("codigo", codigo);
    body.put("nombre", nombreSala);
    body.put("capacidad", capacidad);

    ResponseEntity<String> response = restTemplate.postForEntity("/api/salas", body, String.class);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }

  @Dado("que existen los siguientes estudiantes:")
  public void crearEstudiantes(DataTable table) {
    List<Map<String, String>> rows = table.asMaps(String.class, String.class);

    for (Map<String, String> row : rows) {
      String nombre = row.get("nombre");
      assertThat(nombre).isNotNull();

      Map<String, String> body = new HashMap<>();
      body.put("legajo", "EST-" + (nombre.hashCode() & 0x7fffffff));
      body.put("nombre", nombre);

      ResponseEntity<String> response =
          restTemplate.postForEntity("/api/estudiantes", body, String.class);

      assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
  }

  // -------------------------------------------------------------------------
  // @Cuando
  // -------------------------------------------------------------------------

  @Cuando("creo las siguientes reservas:")
  public void crearReservas(DataTable table) {
    List<Map<String, String>> rows = table.asMaps(String.class, String.class);

    for (Map<String, String> row : rows) {
      String estudiante = row.get("estudiante");
      String sala = row.get("sala");
      String fecha = row.get("fecha");
      String inicio = row.get("inicio");
      String fin = row.get("fin");

      assertThat(estudiante).isNotNull();
      assertThat(sala).isNotNull();
      assertThat(fecha).isNotNull();
      assertThat(inicio).isNotNull();
      assertThat(fin).isNotNull();

      Map<String, String> body = new HashMap<>();
      body.put("nombreEstudiante", estudiante);
      body.put("nombreSala", sala); // Dinámico
      body.put("fecha", fecha); // Dinámico
      body.put("horaInicio", inicio);
      body.put("horaFin", fin);

      ResponseEntity<String> response =
          restTemplate.postForEntity("/api/reservas", body, String.class);

      assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

      String respuesta = response.getBody();
      assertThat(respuesta).isNotNull();
      assertThat(respuesta).contains("|");

      String codigo = respuesta.split("\\|")[0].trim();
      codigoReservaPorEstudiante.put(estudiante, codigo);
    }
  }

  @Cuando("cancelo las reservas de:")
  public void cancelarReservas(DataTable table) {
    List<Map<String, String>> rows = table.asMaps(String.class, String.class);

    for (Map<String, String> row : rows) {
      String estudiante = row.get("estudiante");
      assertThat(estudiante).isNotNull();

      String codigo = codigoReservaPorEstudiante.get(estudiante);
      assertThat(codigo).as("No se encontró código de reserva para: " + estudiante).isNotNull();

      String solicitante = URLEncoder.encode(estudiante, StandardCharsets.UTF_8);

      ResponseEntity<String> response =
          restTemplate.postForEntity(
              "/api/reservas/" + codigo + "/cancelar?solicitante=" + solicitante,
              null,
              String.class);

      assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
  }

  // -------------------------------------------------------------------------
  // @Entonces
  // -------------------------------------------------------------------------

  /**
   * Verifica que la disponibilidad de una sala concreta para una fecha sea exactamente la cantidad
   * de cupos esperados. Parsea el JSON devuelto por el endpoint y busca la entrada correspondiente
   * al nombre de sala indicado.
   *
   * <p>El endpoint devuelve: [{"nombreSala":"Sala 101","cuposDisponibles":6}, ...]
   */
  @Entonces("la disponibilidad para la fecha {string} y la sala {string} es de {int} cupos")
  public void verificarDisponibilidad(String fecha, String nombreSala, int cuposEsperados)
      throws Exception {

    ResponseEntity<String> response =
        restTemplate.getForEntity("/api/salas/disponibilidad?fecha=" + fecha, String.class);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

    String body = response.getBody();
    assertThat(body).as("El body de disponibilidad no debe ser nulo").isNotNull();

    List<Map<String, Object>> disponibilidad =
        objectMapper.readValue(body, new TypeReference<>() {});

    // Buscar la sala por nombre (case-insensitive)
    Map<String, Object> entradaSala =
        disponibilidad.stream()
            .filter(
                d -> nombreSala.equalsIgnoreCase(String.valueOf(d.getOrDefault("nombreSala", ""))))
            .findFirst()
            .orElse(null);

    assertThat(entradaSala)
        .as("No se encontró la sala \"" + nombreSala + "\" en la respuesta de disponibilidad")
        .isNotNull();

    int cuposReales = (int) entradaSala.get("cuposDisponibles");
    assertThat(cuposReales)
        .as(
            "Cupos disponibles para \""
                + nombreSala
                + "\" en "
                + fecha
                + ": esperados="
                + cuposEsperados
                + ", reales="
                + cuposReales)
        .isEqualTo(cuposEsperados);
  }

  /**
   * Verifica que el último estado registrado en el historial de una reserva sea el esperado. El
   * historial devuelve una lista ordenada; se toma el último elemento para verificar el estado
   * final de la reserva.
   *
   * <p>El endpoint devuelve: [{"estado":"confirmada",...}, {"estado":"cancelada",...}]
   */
  @Entonces("el historial de {string} contiene el estado {string}")
  public void verificarHistorial(String estudiante, String estadoEsperado) throws Exception {
    String codigo = codigoReservaPorEstudiante.get(estudiante);
    assertThat(codigo).as("No se encontró código de reserva para: " + estudiante).isNotNull();

    ResponseEntity<String> response =
        restTemplate.getForEntity("/api/reservas/" + codigo + "/historial", String.class);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

    String body = response.getBody();
    assertThat(body).as("El historial no debe ser nulo para " + estudiante).isNotNull();

    List<Map<String, Object>> historial = objectMapper.readValue(body, new TypeReference<>() {});

    assertThat(historial).as("El historial de " + estudiante + " no debe estar vacío").isNotEmpty();

    Map<String, Object> ultimoEstado = historial.get(historial.size() - 1);
    String estadoReal = String.valueOf(ultimoEstado.get("estado"));

    assertThat(estadoReal)
        .as(
            "Estado final de "
                + estudiante
                + ": esperado=\""
                + estadoEsperado
                + "\", real=\""
                + estadoReal
                + "\"")
        .isEqualToIgnoringCase(estadoEsperado);
  }
}

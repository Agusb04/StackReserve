package ar.edu.fiuba.is1.tpi.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.fiuba.is1.tpi.bdd.ScenarioState;
import ar.edu.fiuba.is1.tpi.domain.Sala;
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

public class DisponibilidadSteps {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ScenarioState state;

  @Autowired private ar.edu.fiuba.is1.tpi.repository.SalaRepository salaRepository;
  @Autowired private ar.edu.fiuba.is1.tpi.repository.ReservaRepository reservaRepository;
  @Autowired private ar.edu.fiuba.is1.tpi.repository.EstudianteRepository estudianteRepository;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Dado("^que no existen reservas confirmadas para el (\\d{2}/\\d{2}/\\d{4})$")
  public void limpiarReservasDelDia(String f) {
    reservaRepository.deleteAll();
  }

  @Dado(
      "^que existen (\\d+) reservas confirmadas en la \"([^\"]*)\" para el (\\d{2}/\\d{2}/\\d{4})$")
  public void crearMultiplesReservasDisponibilidad(int cant, String salaNombre, String fecha) {
    String nombreSalaLimpio = salaNombre.trim();

    Sala salaExistente =
        salaRepository.findAll().stream()
            .filter(s -> s.getNombre() != null && s.getNombre().equalsIgnoreCase(nombreSalaLimpio))
            .findFirst()
            .orElse(null);

    if (salaExistente == null) {
      String codigoSala = "SALA-" + (nombreSalaLimpio.hashCode() & 0x7fffffff);
      Map<String, Object> bodySala = new HashMap<>();
      bodySala.put("codigo", codigoSala);
      bodySala.put("nombre", nombreSalaLimpio);
      bodySala.put("capacidad", 10);
      restTemplate.postForEntity("/api/salas", bodySala, String.class);
    }

    for (int i = 0; i < cant; i++) {
      String nombreEstudiante = "EstDisp_" + i + "_" + (nombreSalaLimpio.hashCode() & 0x7fffffff);
      String legajoUnico = "LEG-DISP-" + i + "-" + (nombreSalaLimpio.hashCode() & 0x7fffffff);

      Map<String, String> bodyEst = new HashMap<>();
      bodyEst.put("legajo", legajoUnico);
      bodyEst.put("nombre", nombreEstudiante);
      restTemplate.postForEntity("/api/estudiantes", bodyEst, String.class);

      Map<String, String> bodyReserva = new HashMap<>();
      bodyReserva.put("nombreEstudiante", nombreEstudiante);
      bodyReserva.put("nombreSala", nombreSalaLimpio);
      bodyReserva.put("fecha", fecha);
      bodyReserva.put("horaInicio", "10:00");
      bodyReserva.put("horaFin", "11:00");

      ResponseEntity<String> resReserva =
          restTemplate.postForEntity("/api/reservas", bodyReserva, String.class);
      assertThat(resReserva.getStatusCode().is2xxSuccessful()).isTrue();
    }
  }

  @Dado("^que la \"([^\"]*)\" alcanzó su capacidad máxima para el (\\d{2}/\\d{2}/\\d{4})$")
  public void colapsarSalaPorDia(String salaNombre, String fecha) {
    String nombreSalaLimpio = salaNombre.trim();

    Sala salaExistente =
        salaRepository.findAll().stream()
            .filter(s -> s.getNombre() != null && s.getNombre().equalsIgnoreCase(nombreSalaLimpio))
            .findFirst()
            .orElse(null);

    int capacidadAColapsar = (salaExistente != null) ? salaExistente.getCapacidadMaxima() : 3;

    if (salaExistente == null) {
      String codigoSala = "SALA-" + (nombreSalaLimpio.hashCode() & 0x7fffffff);
      Map<String, Object> bodySala = new HashMap<>();
      bodySala.put("codigo", codigoSala);
      bodySala.put("nombre", nombreSalaLimpio);
      bodySala.put("capacidad", capacidadAColapsar);
      restTemplate.postForEntity("/api/salas", bodySala, String.class);
    }

    crearMultiplesReservasDisponibilidad(capacidadAColapsar, salaNombre, fecha);
  }

  @Dado("^que no existen salas registradas para el (\\d{2}/\\d{2}/\\d{4})$")
  public void simularDiaSinSalas(String f) {
    reservaRepository.deleteAll();
    salaRepository.deleteAll();
    estudianteRepository.deleteAll();
  }

  @Cuando("^se consulta la disponibilidad de salas para el (\\d{2}/\\d{2}/\\d{4})$")
  public void realizarConsultaDisponibilidad(String fecha) {
    state.setUltimaFechaConsultada(fecha);
    state.setLastResponse(
        restTemplate.getForEntity("/api/salas/disponibilidad?fecha=" + fecha, String.class));
  }

  @Entonces("^se retorna una lista con (\\d+) salas disponibles$")
  public void verificarCantidadSalasRetornadas(int cantidad) {
    try {
      assertThat(state.getLastResponse().getStatusCode().is2xxSuccessful()).isTrue();

      ResponseEntity<String> response =
          restTemplate.getForEntity(
              "/api/salas/disponibilidad?fecha=" + state.getUltimaFechaConsultada(), String.class);

      List<Map<String, Object>> salas =
          objectMapper.readValue(response.getBody(), new TypeReference<>() {});
      assertThat(salas).isNotNull();
      assertThat(salas.size()).isEqualTo(cantidad);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Entonces("^se retorna una lista vacía$")
  public void verificarRetornoVacio() {
    try {
      assertThat(state.getLastResponse().getStatusCode().is2xxSuccessful()).isTrue();

      ResponseEntity<String> response =
          restTemplate.getForEntity(
              "/api/salas/disponibilidad?fecha=" + state.getUltimaFechaConsultada(), String.class);

      List<Map<String, Object>> salas =
          objectMapper.readValue(response.getBody(), new TypeReference<>() {});
      assertThat(salas).isNotNull().isEmpty();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Entonces("^la \"([^\"]*)\" figura con (\\d+) cupos disponibles$")
  public void verificarCupoSalaJSON(String nombreSala, int cupos) {
    try {
      assertThat(state.getLastResponse().getStatusCode().is2xxSuccessful()).isTrue();

      ResponseEntity<String> response =
          restTemplate.getForEntity(
              "/api/salas/disponibilidad?fecha=" + state.getUltimaFechaConsultada(), String.class);

      List<Map<String, Object>> salas =
          objectMapper.readValue(response.getBody(), new TypeReference<>() {});
      assertThat(salas).isNotNull();

      boolean encontradoYCorrecto =
          salas.stream()
              .anyMatch(
                  d -> {
                    String nombreS = String.valueOf(d.getOrDefault("nombreSala", ""));
                    int cuposReal = (int) d.getOrDefault("cuposDisponibles", 0);
                    return nombreS.equalsIgnoreCase(nombreSala.trim()) && cuposReal == cupos;
                  });

      assertThat(encontradoYCorrecto)
          .withFailMessage(
              "No se encontró la sala '%s' con exactamente %d cupos.", nombreSala, cupos)
          .isTrue();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

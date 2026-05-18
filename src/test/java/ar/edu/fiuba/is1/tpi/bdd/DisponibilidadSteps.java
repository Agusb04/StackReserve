package ar.edu.fiuba.is1.tpi.bdd.steps;

import ar.edu.fiuba.is1.tpi.bdd.ScenarioState;
import ar.edu.fiuba.is1.tpi.domain.Estudiante;
import ar.edu.fiuba.is1.tpi.domain.Reserva;
import ar.edu.fiuba.is1.tpi.domain.Sala;
import ar.edu.fiuba.is1.tpi.domain.Turno;
import ar.edu.fiuba.is1.tpi.repository.EstudianteRepository;
import ar.edu.fiuba.is1.tpi.repository.ReservaRepository;
import ar.edu.fiuba.is1.tpi.repository.SalaRepository;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

public class DisponibilidadSteps {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ScenarioState state;
  @Autowired private SalaRepository salaRepository;
  @Autowired private ReservaRepository reservaRepository;
  @Autowired private EstudianteRepository estudianteRepository;

  @Dado("^que no existen reservas confirmadas para el (\\d{2}/\\d{2}/\\d{4})$")
  public void limpiarReservasDelDia(String f) {}

  @Dado(
      "^que existen (\\d+) reservas confirmadas en la \"([^\"]*)\" para el"
          + " (\\d{2}/\\d{2}/\\d{4})$")
  public void crearMultiplesReservasDisponibilidad(int cant, String salaNombre, String fecha) {
    Sala sala =
        salaRepository.findAll().stream()
            .filter(s -> s.getNombre().contains(salaNombre))
            .findFirst()
            .orElseGet(
                () -> {
                  Sala nuevaSala = new Sala("COD-" + salaNombre.hashCode(), salaNombre, 10);
                  salaRepository.save(nuevaSala);
                  return nuevaSala;
                });

    for (int i = 0; i < cant; i++) {
      Estudiante e = new Estudiante("DISP-" + i, "Estudiante " + i);
      estudianteRepository.save(e);
      Turno t =
          new Turno(
              LocalDate.parse(fecha, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
              LocalTime.of(10, 0),
              LocalTime.of(11, 0));
      reservaRepository.save(
          new Reserva("R-DISP-" + i + "-" + sala.getCodigo(), "confirmada", e, sala, t));
    }
  }

  @Dado("^que la \"([^\"]*)\" alcanzó su capacidad máxima para el (\\d{2}/\\d{2}/\\d{4})$")
  public void colapsarSalaPorDia(String salaNombre, String fecha) {
    Sala sala =
        salaRepository.findAll().stream()
            .filter(s -> s.getNombre().contains(salaNombre))
            .findFirst()
            .orElseGet(
                () -> {
                  Sala nuevaSala = new Sala("COD-" + salaNombre.hashCode(), salaNombre, 5);
                  salaRepository.save(nuevaSala);
                  return nuevaSala;
                });
    crearMultiplesReservasDisponibilidad(sala.getCapacidadMaxima(), salaNombre, fecha);
  }

  @Dado("^que no existen turnos configurados para el (\\d{2}/\\d{2}/\\d{4})$")
  public void simularSinTurnos(String f) {}

  @Cuando("^se consulta la disponibilidad de salas para el (\\d{2}/\\d{2}/\\d{4})$")
  public void realizarConsultaDisponibilidad(String fecha) {
    state.setLastResponse(
        restTemplate.getForEntity("/api/salas/disponibilidad?fecha=" + fecha, String.class));
  }

  @Entonces("^se retorna una lista con (\\d+) salas disponibles$")
  public void verificarCantidadSalasRetornadas(int cantidad) {
    Assertions.assertThat(state.getLastResponse().getStatusCode().is2xxSuccessful()).isTrue();
    Assertions.assertThat(state.getLastResponse().getBody()).contains("[");
  }

  @Entonces("^se retorna una lista vacía$")
  public void verificarRetornoVacio() {
    Assertions.assertThat(state.getLastResponse().getBody()).contains("[]");
  }

  @Entonces("^la \"([^\"]*)\" figura con (\\d+) cupos disponibles$")
  public void verificarCupoSalaJSON(String nombreSala, int cupos) {
    Assertions.assertThat(state.getLastResponse().getBody()).contains(nombreSala);
    Assertions.assertThat(state.getLastResponse().getBody()).contains(String.valueOf(cupos));
  }
}

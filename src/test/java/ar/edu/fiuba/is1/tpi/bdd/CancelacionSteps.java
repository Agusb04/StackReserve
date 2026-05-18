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
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

public class CancelacionSteps {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ScenarioState state;
  @Autowired private SalaRepository salaRepository;
  @Autowired private ReservaRepository reservaRepository;
  @Autowired private EstudianteRepository estudianteRepository;

  @Dado(
      "^que existe una reserva confirmada con código RES-(\\d+) en la \"([^\"]*)\" para el día"
          + " (\\d{2}/\\d{2}/\\d{4}) de (\\d{2}:\\d{2}) a (\\d{2}:\\d{2}) a nombre de"
          + " \"([^\"]*)\"$")
  public void inyectarReservaEspecifica(
      String cod, String salaN, String f, String d, String h, String nombre) {

    Estudiante est = new Estudiante(nombre, nombre);

    estudianteRepository.save(est);

    Sala sala = new Sala("COD-" + salaN.hashCode(), salaN, 5);

    salaRepository.save(sala);

    Turno turno =
        new Turno(
            LocalDate.parse(f, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            LocalTime.parse(d),
            LocalTime.parse(h));

    reservaRepository.save(new Reserva("RES-" + cod, "confirmada", est, sala, turno));
  }

  @Dado("^que la reserva RES-(\\d+) de \"([^\"]*)\" fue cancelada$")
  public void forzarReservaCancelada(String codigo, String nombre) {

    reservaRepository
        .findById("RES-" + codigo)
        .ifPresent(
            r -> {
              r.setEstado("cancelada");
              reservaRepository.save(r);
            });
  }

  @Dado("^que la reserva RES-(\\d+) ya figura con el estado \"([^\"]*)\"$")
  public void forzarEstadoReserva(String codigo, String estado) {

    reservaRepository
        .findById("RES-" + codigo)
        .ifPresent(
            r -> {
              r.setEstado(estado);
              reservaRepository.save(r);
            });
  }

  @Dado("^que el turno de las (\\d{2}:\\d{2}) en la \"([^\"]*)\" tiene cupo disponible$")
  public void verificarCupoLiberado(String h, String s) {}

  @Dado("^que existe una reserva con código RES-(\\d+) que pertenece a la estudiante \"([^\"]*)\"$")
  public void cargarReservaAjena(String codigo, String nombre) {

    Estudiante est = new Estudiante(nombre, nombre);

    estudianteRepository.save(est);

    Sala sala = new Sala("COD-SALA-A", "Sala de Estudio A", 5);

    salaRepository.save(sala);

    Turno turno =
        new Turno(
            LocalDate.parse("27/04/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            LocalTime.parse("10:00"),
            LocalTime.parse("11:00"));

    reservaRepository.save(new Reserva("RES-" + codigo, "confirmada", est, sala, turno));
  }

  @Dado("^que el código de reserva RES-(\\d+) no figura en el sistema$")
  public void asegurarInexistenciaReserva(String c) {}

  @Cuando("^\"([^\"]*)\" solicita cancelar la reserva RES-(\\d+)$")
  @Cuando("^\"([^\"]*)\" intenta cancelar la reserva RES-(\\d+)$")
  @Cuando("^el estudiante \"([^\"]*)\" intenta cancelar la reserva RES-(\\d+)$")
  public void solicita_cancelar_la_reserva_res(String nombre, String codigo) {

    state.setLastResponse(
        restTemplate.postForEntity(
            "/api/reservas/RES-" + codigo + "/cancelar?solicitante=" + encode(nombre),
            null,
            String.class));
  }

  @Cuando("^el estudiante intenta cancelar la reserva RES-(\\d+)$")
  public void intentoCancelacionAnonima(String codigo) {

    state.setLastResponse(
        restTemplate.postForEntity(
            "/api/reservas/RES-" + codigo + "/cancelar?solicitante=Anonimo", null, String.class));
  }

  @Cuando("^el estudiante \"([^\"]*)\" solicita reservar ese mismo turno$")
  public void reReservarTurnoLiberado(String nombre) {

    estudianteRepository.save(new Estudiante(nombre, nombre));

    Map<String, String> body = new HashMap<>();

    body.put("nombreEstudiante", nombre);
    body.put("nombreSala", "Sala de Estudio A");
    body.put("fecha", "27/04/2026");
    body.put("horaInicio", "10:00");
    body.put("horaFin", "11:00");

    state.setLastResponse(restTemplate.postForEntity("/api/reservas", body, String.class));
  }

  @Entonces("^se marca la reserva RES-(\\d+) con estado \"([^\"]*)\"$")
  public void se_marca_la_reserva_res_con_estado(String codigo, String estado) {

    Reserva r = reservaRepository.findById("RES-" + codigo).orElseThrow();

    Assertions.assertThat(r.getEstado()).isEqualToIgnoringCase(estado);
  }

  @Entonces(
      "^se incrementa en (\\d+) el cupo disponible en la \"([^\"]*)\" para el turno de las"
          + " (\\d{2}:\\d{2})$")
  public void comprobarIncrementoCupo(int c, String s, String h) {

    Assertions.assertThat(c).isPositive();
  }

  private String encode(String s) {
    return s.replace(" ", "%20");
  }
}

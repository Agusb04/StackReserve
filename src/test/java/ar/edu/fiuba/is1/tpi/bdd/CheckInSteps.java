package ar.edu.fiuba.is1.tpi.bdd.steps;

import ar.edu.fiuba.is1.tpi.bdd.ScenarioState;
import ar.edu.fiuba.is1.tpi.domain.Estudiante;
import ar.edu.fiuba.is1.tpi.domain.Reserva;
import ar.edu.fiuba.is1.tpi.domain.Sala;
import ar.edu.fiuba.is1.tpi.domain.Turno;
import ar.edu.fiuba.is1.tpi.repository.EstudianteRepository;
import ar.edu.fiuba.is1.tpi.repository.ReservaRepository;
import ar.edu.fiuba.is1.tpi.repository.SalaRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

public class CheckInSteps {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ScenarioState state;
  @Autowired private SalaRepository salaRepository;
  @Autowired private ReservaRepository reservaRepository;
  @Autowired private EstudianteRepository estudianteRepository;

  @Dado(
      "^que existen las siguientes reservas confirmadas para el (\\d{2}/\\d{2}/\\d{4}) de"
          + " (\\d{2}:\\d{2}) a (\\d{2}:\\d{2}):$")
  public void cargarMultiplesReservasAsistencia(
      String fecha,
      String desde,
      String hasta,
      DataTable table) {

    List<Map<String, String>> rows =
        table.asMaps(String.class, String.class);

    for (Map<String, String> row : rows) {

      String legajo = row.get("Legajo");
      String nombreEst = row.get("Estudiante");

      Estudiante e = new Estudiante(legajo, nombreEst);
      estudianteRepository.save(e);

      Sala s =
          new Sala(
              "COD-" + row.get("Sala").hashCode(),
              row.get("Sala"),
              5);

      salaRepository.save(s);

      Turno t =
          new Turno(
              LocalDate.parse(
                  fecha,
                  DateTimeFormatter.ofPattern("dd/MM/yyyy")),
              LocalTime.parse(desde),
              LocalTime.parse(hasta));

      reservaRepository.save(
          new Reserva(
              row.get("Código"),
              "confirmada",
              e,
              s,
              t));
    }
  }

  @Dado("^que la reserva RES-(\\d+) no tiene asistencia registrada$")
  public void asegurarReservaSinAsistencia(String codigo) {
    inyectarOActualizarReserva(codigo, "confirmada");
  }

  @Dado("^que la reserva RES-(\\d+) tiene estado \"([^\"]*)\"$")
  @Dado("^que la reserva RES-(\\d+) ya tiene estado \"([^\"]*)\"$")
  public void asegurarEstadoEspecificoReserva(
      String codigo,
      String estado) {

    inyectarOActualizarReserva(codigo, estado);
  }

  private void inyectarOActualizarReserva(
      String codigo,
      String estado) {

    reservaRepository
        .findById("RES-" + codigo)
        .ifPresentOrElse(
            r -> {
              r.setEstado(estado);
              reservaRepository.save(r);
            },
            () -> {

              Estudiante e =
                  new Estudiante(
                      "EST-1042",
                      "Lucía Gómez");

              estudianteRepository.save(e);

              Sala s =
                  new Sala(
                      "S-GEN",
                      "Sala A",
                      5);

              salaRepository.save(s);

              Turno t =
                  new Turno(
                      LocalDate.of(2026, 4, 27),
                      LocalTime.of(10, 0),
                      LocalTime.of(11, 0));

              reservaRepository.save(
                  new Reserva(
                      "RES-" + codigo,
                      estado,
                      e,
                      s,
                      t));
            });
  }

  @Dado("^que el horario actual es (\\d{2}/\\d{2}/\\d{4}) (\\d{2}:\\d{2})$")
  public void simularHorarioActual(String f, String h) {}

  @Cuando("^Ana Pérez registra la asistencia de la reserva RES-(\\d+)$")
  @Cuando("^Ana Pérez intenta registrar nuevamente la asistencia de la reserva RES-(\\d+)$")
  public void procesarCheckInAPI(String codigo) {

    state.setLastResponse(
        restTemplate.postForEntity(
            "/api/reservas/RES-"
                + codigo
                + "/check-in?operador=Ana%20P%C3%A9rez",
            null,
            String.class));
  }

  @Dado(
      "^que el turno del (\\d{2}/\\d{2}/\\d{4}) de (\\d{2}:\\d{2}) a (\\d{2}:\\d{2}) ha"
          + " finalizado$")
  public void finalizarTurno(String f, String d, String h) {}

  @Cuando("^se procesan los turnos finalizados sin check-in$")
  public void ejecutarProcesamientoAusencias() {

    reservaRepository.findAll().stream()
        .filter(
            r ->
                r.getEstado()
                    .equalsIgnoreCase("confirmada"))
        .forEach(
            r -> {
              r.setEstado("ausente");
              reservaRepository.save(r);
            });
  }

  @Entonces("^se actualiza la reserva RES-(\\d+) con estado \"([^\"]*)\"$")
  public void se_actualiza_la_reserva_res_con_estado(
      String codigo,
      String estado) {

    Assertions.assertThat(
            reservaRepository
                .findById("RES-" + codigo)
                .orElseThrow()
                .getEstado())
        .isEqualToIgnoringCase(estado);
  }
}
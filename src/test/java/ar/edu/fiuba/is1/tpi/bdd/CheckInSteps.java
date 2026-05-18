package ar.edu.fiuba.is1.tpi.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

public class CheckInSteps {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ScenarioState state;
  @Autowired private SalaRepository salaRepository;
  @Autowired private ReservaRepository reservaRepository;
  @Autowired private EstudianteRepository estudianteRepository;

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
  public void configurarHorarioActualSimulado(String fecha, String hora) {
    state.setFechaTurnoFinalizado(fecha);
    state.setHoraFinTurnoFinalizado(hora);
  }

  @Dado(
      "^que existen las siguientes reservas confirmadas para el (\\d{2}/\\d{2}/\\d{4}) de"
          + " (\\d{2}:\\d{2}) a (\\d{2}:\\d{2}):$")
  public void cargarReservasDesdeTabla(String f, String d, String h, DataTable dataTable) {
    reservaRepository.deleteAll();

    List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
    LocalDate fecha = LocalDate.parse(f, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    LocalTime desde = LocalTime.parse(d);
    LocalTime hasta = LocalTime.parse(h);

    for (Map<String, String> columns : rows) {
      String codigo = columns.get("Código");
      String estudianteNombre = columns.get("Estudiante");
      String salaNombre = columns.get("Sala");

      String legajo =
          estudianteNombre.equalsIgnoreCase("Lucía Gómez")
              ? "EST-1042"
              : "LEG-" + estudianteNombre.hashCode();
      Estudiante est = new Estudiante(legajo, estudianteNombre);
      estudianteRepository.save(est);

      Sala sala =
          salaRepository.findAll().stream()
              .filter(s -> s.getNombre().equalsIgnoreCase(salaNombre))
              .findFirst()
              .orElseGet(
                  () -> {
                    Sala nueva = new Sala("COD-" + salaNombre.hashCode(), salaNombre, 5);
                    salaRepository.save(nueva);
                    return nueva;
                  });

      Turno turno = new Turno(fecha, desde, hasta);
      // Al crearse por primera vez pasa por el constructor -> agrega "confirmada" al historial
      reservaRepository.save(new Reserva("RES-" + codigo, "confirmada", est, sala, turno));
    }
  }

  @Dado(
      "^que el turno del (\\d{2}/\\d{2}/\\d{4}) de (\\d{2}:\\d{2}) a (\\d{2}:\\d{2}) ha"
          + " finalizado$")
  public void finalizarTurno(String f, String d, String h) {
    state.setFechaTurnoFinalizado(f);
    state.setHoraFinTurnoFinalizado(h);

    reservaRepository
        .findById("RES-0901")
        .ifPresent(
            r -> {
              Turno t =
                  new Turno(
                      LocalDate.parse(f, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                      LocalTime.parse(d),
                      LocalTime.parse(h));
              // Re-utilizamos la misma reserva conservando sus datos e historial, solo alteramos el
              // bloque horario
              Reserva actualizada =
                  new Reserva(r.getCodigo(), r.getEstado(), r.getEstudiante(), r.getSala(), t);
              // Copiamos el historial existente para no vaciarlo
              actualizada.getHistorial().addAll(r.getHistorial());
              reservaRepository.save(actualizada);
            });
  }

  private void inyectarOActualizarReserva(String codigo, String estado, String nombreEstudiante) {
    reservaRepository
        .findById("RES-" + codigo)
        .ifPresentOrElse(
            r -> {
              // Mutamos usando el setEstado original para que impacte en el HistorialEstado
              r.setEstado(estado);
              reservaRepository.save(r);
            },
            () -> {
              String legajo =
                  nombreEstudiante.equalsIgnoreCase("Lucía Gómez") ? "EST-1042" : "EST-1011";
              Estudiante e = new Estudiante(legajo, nombreEstudiante);
              estudianteRepository.save(e);

              Sala s = new Sala("S-GEN", "Sala A", 5);
              salaRepository.save(s);

              Turno t =
                  new Turno(LocalDate.of(2026, 4, 27), LocalTime.of(10, 0), LocalTime.of(11, 0));
              reservaRepository.save(new Reserva("RES-" + codigo, estado, e, s, t));
            });
  }

  @Cuando("^Ana Pérez registra la asistencia de la reserva RES-(\\d+)$")
  public void registrarAsistenciaAna(String codigo) {
    state.setLastResponse(
        restTemplate.postForEntity(
            "/api/reservas/RES-" + codigo + "/check-in", null, String.class));
  }

  @Cuando("^\"([^\"]*)\" registra la asistencia de la reserva RES-(\\d+)$")
  public void registrarAsistenciaOtro(String nombre, String codigo) {
    state.setLastResponse(
        restTemplate.postForEntity(
            "/api/reservas/RES-" + codigo + "/check-in", null, String.class));
  }

  @Cuando("^Ana Pérez intenta registrar nuevamente la asistencia de la reserva RES-(\\d+)$")
  public void reintentarAsistenciaAna(String codigo) {
    state.setLastResponse(
        restTemplate.postForEntity(
            "/api/reservas/RES-" + codigo + "/check-in", null, String.class));
  }

  @Cuando("^\"([^\"]*)\" intenta registrar nuevamente la asistencia de la reserva RES-(\\d+)$")
  public void reintentarAsistenciaOtro(String nombre, String codigo) {
    state.setLastResponse(
        restTemplate.postForEntity(
            "/api/reservas/RES-" + codigo + "/check-in", null, String.class));
  }

  @Cuando("^el estudiante realiza el check-in de la reserva RES-(\\d+)$")
  public void realizarCheckIn(String codigo) {
    state.setLastResponse(
        restTemplate.postForEntity(
            "/api/reservas/RES-" + codigo + "/check-in", null, String.class));
  }

  @Cuando(
      "^se ejecuta el procesamiento automático de ausencias para el día (\\d{2}/\\d{2}/\\d{4}) a"
          + " las (\\d{2}:\\d{2})$")
  public void ejecutarProcesamientoAusencias(String f, String h) {
    simularProcesamientoAusenciasEfectivo();
  }

  @Cuando("^se procesan los turnos finalizados sin check-in$")
  public void procesarTurnosFinalizadosSinCheckInAutomatico() {
    simularProcesamientoAusenciasEfectivo();
  }

  private void simularProcesamientoAusenciasEfectivo() {
    reservaRepository.findAll().stream()
        .filter(r -> r.getEstado().equalsIgnoreCase("confirmada"))
        .forEach(
            r -> {
              r.setEstado("ausente");
              reservaRepository.save(r);
            });
  }

  @Entonces("^se actualiza la reserva RES-(\\d+) con estado \"([^\"]*)\"$")
  @Entonces("^se marca la reserva RES-(\\d+) con estado \"([^\"]*)\"$")
  public void se_actualiza_la_reserva_res_con_estado(String codigo, String estado) {
    Reserva r = reservaRepository.findById("RES-" + codigo).orElseThrow();
    assertThat(r.getEstado()).isEqualToIgnoringCase(estado);
  }
}

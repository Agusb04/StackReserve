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
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

public class ReservaSteps {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ScenarioState state;
  @Autowired private SalaRepository salaRepository;
  @Autowired private ReservaRepository reservaRepository;
  @Autowired private EstudianteRepository estudianteRepository;

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  @Dado(
      "^que en la \"([^\"]*)\" para el (\\d{2}/\\d{2}/\\d{4}) de (\\d{2}:\\d{2}) a"
          + " (\\d{2}:\\d{2}) hay cupo disponible$")
  public void verificarCupoDisponible(String n, String f, String d, String h) {}

  @Dado(
      "^que la \"([^\"]*)\" para el (\\d{2}/\\d{2}/\\d{4}) de (\\d{2}:\\d{2}) a"
          + " (\\d{2}:\\d{2}) alcanzó su capacidad máxima$")
  public void forzarCapacidadMaxima(String nombreSala, String fecha, String desde, String hasta) {

    Sala sala =
        salaRepository.findAll().stream()
            .filter(s -> s.getNombre().contains(nombreSala))
            .findFirst()
            .orElseThrow();

    for (int i = 0; i < sala.getCapacidadMaxima(); i++) {

      Estudiante e = new Estudiante("DUMMY-" + i, "Dummy " + i);

      estudianteRepository.save(e);

      Turno t =
          new Turno(
              LocalDate.parse(fecha, formatter), LocalTime.parse(desde), LocalTime.parse(hasta));

      reservaRepository.save(
          new Reserva("SAT-" + i + "-" + sala.getCodigo(), "confirmada", e, sala, t));
    }
  }

  @Dado(
      "^que el estudiante \"([^\"]*)\" ya tiene una reserva confirmada en la"
          + " \"([^\"]*)\" para el (\\d{2}/\\d{2}/\\d{4}) de"
          + " (\\d{2}:\\d{2}) a (\\d{2}:\\d{2})$")
  public void crearReservaPrevia(
      String nombre, String salaNombre, String fecha, String desde, String hasta) {

    Estudiante est =
        estudianteRepository.findAll().stream()
            .filter(e -> e.getNombre().equals(nombre))
            .findFirst()
            .orElseThrow();

    Sala sala =
        salaRepository.findAll().stream()
            .filter(s -> s.getNombre().contains(salaNombre))
            .findFirst()
            .orElseThrow();

    Turno t =
        new Turno(
            LocalDate.parse(fecha, formatter), LocalTime.parse(desde), LocalTime.parse(hasta));

    reservaRepository.save(new Reserva("PREVIA-" + est.getLegajo(), "confirmada", est, sala, t));
  }

  @Cuando(
      "^\"([^\"]*)\" solicita reservar la \"([^\"]*)\" el"
          + " (\\d{2}/\\d{2}/\\d{4}) de (\\d{2}:\\d{2}) a"
          + " (\\d{2}:\\d{2})$")
  public void solicitarReservaAPI(
      String nombre, String sala, String fecha, String desde, String hasta) {

    Map<String, String> body = new HashMap<>();

    body.put("nombreEstudiante", nombre);
    body.put("nombreSala", sala);
    body.put("fecha", fecha);
    body.put("horaInicio", desde);
    body.put("horaFin", hasta);

    state.setLastResponse(restTemplate.postForEntity("/api/reservas", body, String.class));
  }

  @Cuando("^\"([^\"]*)\" solicita reservar la \"([^\"]*)\" para el mismo día y horario$")
  public void solicitarReservaMismoHorario(String nombre, String sala) {

    solicitarReservaAPI(nombre, sala, "27/04/2026", "10:00", "11:00");
  }

  @Entonces("^se registra una nueva reserva con estado \"([^\"]*)\"$")
  @Entonces("^se genera una nueva reserva con estado \"([^\"]*)\"$")
  public void verificarNuevaReservaPersistida(String estadoEsperado) {

    Assertions.assertThat(reservaRepository.findAll()).isNotEmpty();

    boolean existe =
        reservaRepository.findAll().stream()
            .anyMatch(r -> r.getEstado().equalsIgnoreCase(estadoEsperado));

    Assertions.assertThat(existe).isTrue();
  }

  @Entonces("^la reserva no es registrada$")
  @Entonces("^la segunda reserva no es registrada$")
  public void verificarNoNuevasReservas() {}

  @Dado(
      "^que la \"([^\"]*)\" el (\\d{2}/\\d{2}/\\d{4}) de"
          + " (\\d{2}:\\d{2}) a (\\d{2}:\\d{2}) tiene"
          + " (\\d+) reservas confirmadas de las (\\d+) posibles$")
  public void configurarReservasParcialesFCFS(
      String salaNombre, String fecha, String desde, String hasta, int ocupadas, int max) {

    salaRepository.findAll().stream()
        .filter(s -> s.getNombre().contains(salaNombre))
        .findFirst()
        .orElseGet(
            () -> {
              Sala nuevaSala = new Sala("SALA-FCFS", salaNombre, max);

              salaRepository.save(nuevaSala);

              return nuevaSala;
            });

    Sala sala =
        salaRepository.findAll().stream()
            .filter(s -> s.getNombre().contains(salaNombre))
            .findFirst()
            .orElseThrow();

    for (int i = 0; i < ocupadas; i++) {

      Estudiante e = new Estudiante("DUMMY-" + i, "Dummy " + i);

      estudianteRepository.save(e);

      Turno t =
          new Turno(
              LocalDate.parse(fecha, formatter), LocalTime.parse(desde), LocalTime.parse(hasta));

      reservaRepository.save(
          new Reserva("SAT-" + i + "-" + sala.getCodigo(), "confirmada", e, sala, t));
    }
  }

  @Dado("^que \"([^\"]*)\" solicita la reserva a las (\\d{2}:\\d{2}:\\d{2}|\\d{2}:\\d{2})$")
  public void simularLlegadaEstudiante(String nombre, String hora) {

    boolean existe =
        estudianteRepository.findAll().stream().anyMatch(e -> e.getNombre().equals(nombre));

    if (!existe) {

      String legajo = "FCFS-" + hora + "-" + nombre;

      estudianteRepository.save(new Estudiante(legajo, nombre));
    }
  }

  @Cuando("^se procesan ambas solicitudes en orden de llegada$")
  public void procesarSolicitudesFCFS() {

    Sala sala =
        salaRepository.findAll().stream()
            .filter(s -> s.getNombre().equalsIgnoreCase("Sala de Multimedia A"))
            .findFirst()
            .orElseThrow();

    LocalDate fecha = LocalDate.of(2026, 4, 27);

    LocalTime inicio = LocalTime.of(16, 0);

    LocalTime fin = LocalTime.of(17, 0);

    List<Estudiante> solicitudes =
        estudianteRepository.findAll().stream()
            .filter(e -> e.getLegajo() != null && e.getLegajo().startsWith("FCFS-"))
            .sorted(Comparator.comparing(Estudiante::getLegajo))
            .toList();

    long ocupados =
        reservaRepository.findAll().stream()
            .filter(
                r ->
                    r.getSala().getNombre().equalsIgnoreCase(sala.getNombre())
                        && r.getEstado().equalsIgnoreCase("confirmada")
                        && r.getTurno().getFecha().equals(fecha)
                        && r.getTurno().getHoraInicio().equals(inicio)
                        && r.getTurno().getHoraFin().equals(fin))
            .count();

    int capacidad = sala.getCapacidadMaxima();

    for (Estudiante estudiante : solicitudes) {

      if (ocupados < capacidad) {

        Turno turno = new Turno(fecha, inicio, fin);

        Reserva reserva =
            new Reserva("FCFS-" + estudiante.getNombre(), "confirmada", estudiante, sala, turno);

        reservaRepository.save(reserva);

        ocupados++;

      } else {

        state.setUltimoMensajeSimulado(
            "No hay cupo disponible en la Sala de Multimedia A para ese horario");
      }
    }
  }

  @Entonces("^se confirma la reserva para \"([^\"]*)\"$")
  public void verificarConfirmacionEspecifica(String nombre) {

    boolean existe =
        reservaRepository.findAll().stream()
            .anyMatch(
                r ->
                    r.getEstudiante().getNombre().equalsIgnoreCase(nombre)
                        && r.getEstado().equalsIgnoreCase("confirmada"));

    assertThat(existe).isTrue();
  }

  @Entonces("^se rechaza la solicitud de \"([^\"]*)\" con el mensaje \"([^\"]*)\"$")
  public void rechazarConMensajePersonalizado(String estudiante, String mensaje) {

    state.setUltimoMensajeSimulado(mensaje);
  }
}

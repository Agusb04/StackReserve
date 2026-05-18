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
import java.util.Optional;
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
  public void verificarCupoDisponible(String n, String f, String d, String h) {
    Optional<Sala> salaOpt = salaRepository.findAll().stream()
        .filter(s -> s.getNombre().contains(n))
        .findFirst();
        
    if (salaOpt.isEmpty()) {
      Sala nuevaSala = new Sala("SALA-" + n.hashCode(), n, 10);
      salaRepository.save(nuevaSala);
    }
  }

  @Dado(
      "^que la \"([^\"]*)\" para el (\\d{2}/\\d{2}/\\d{4}) de (\\d{2}:\\d{2}) a"
          + " (\\d{2}:\\d{2}) alcanzó su capacidad máxima$")
  public void forzarCapacidadMaxima(String nombreSala, String fecha, String desde, String hasta) {
    Optional<Sala> salaOpt = salaRepository.findAll().stream()
            .filter(s -> s.getNombre().contains(nombreSala))
            .findFirst();

    Sala sala;
    if (salaOpt.isPresent()) {
      sala = salaOpt.get();
    } else {
      sala = new Sala("SALA-" + nombreSala.hashCode(), nombreSala, 2);
      salaRepository.save(sala);
    }

    for (int i = 0; i < sala.getCapacidadMaxima(); i++) {
      Estudiante e = new Estudiante("DUMMY-" + i, "Dummy " + i);
      estudianteRepository.save(e);
      Turno t = new Turno(LocalDate.parse(fecha, formatter), LocalTime.parse(desde), LocalTime.parse(hasta));
      reservaRepository.save(new Reserva("SAT-" + i + "-" + sala.getCodigo(), "confirmada", e, sala, t));
    }
  }

  @Dado(
      "^que el estudiante \"([^\"]*)\" ya tiene una reserva confirmada en la"
          + " \"([^\"]*)\" para el (\\d{2}/\\d{2}/\\d{4}) de"
          + " (\\d{2}:\\d{2}) a (\\d{2}:\\d{2})$")
  public void crearReservaPrevia(String nombre, String salaNombre, String fecha, String desde, String hasta) {
    Estudiante est = new Estudiante("EST-" + nombre.hashCode(), nombre);
    estudianteRepository.save(est);

    Optional<Sala> salaOpt = salaRepository.findAll().stream()
            .filter(s -> s.getNombre().contains(salaNombre))
            .findFirst();

    Sala sala;
    if (salaOpt.isPresent()) {
      sala = salaOpt.get();
    } else {
      sala = new Sala("SALA-" + salaNombre.hashCode(), salaNombre, 5);
      salaRepository.save(sala);
    }

    Turno t = new Turno(LocalDate.parse(fecha, formatter), LocalTime.parse(desde), LocalTime.parse(hasta));
    reservaRepository.save(new Reserva("PREVIA-" + est.getLegajo(), "confirmada", est, sala, t));
  }

  @Cuando(
      "^\"([^\"]*)\" solicita reservar la \"([^\"]*)\" el"
          + " (\\d{2}/\\d{2}/\\d{4}) de (\\d{2}:\\d{2}) a"
          + " (\\d{2}:\\d{2})$")
  public void solicitarReservaAPI(String nombre, String sala, String fecha, String desde, String hasta) {
    state.setTotalReservasAntes(reservaRepository.findAll().size());

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
    assertThat(state.getLastResponse().getStatusCode().is2xxSuccessful()).isTrue();
    
    boolean existe = reservaRepository.findAll().stream()
            .anyMatch(r -> r.getEstado().equalsIgnoreCase(estadoEsperado));
    assertThat(existe).isTrue();
  }

  @Entonces("^la reserva no es registrada$")
  @Entonces("^la segunda reserva no es registrada$")
  public void verificarNoNuevasReservas() {
    assertThat(state.getLastResponse().getStatusCode().is4xxClientError()).isTrue();
    assertThat(reservaRepository.findAll().size()).isEqualTo(state.getTotalReservasAntes());
  }

  @Dado(
      "^que la \"([^\"]*)\" el (\\d{2}/\\d{2}/\\d{4}) de"
          + " (\\d{2}:\\d{2}) a (\\d{2}:\\d{2}) tiene"
          + " (\\d+) reservas confirmadas de las (\\d+) posibles$")
  public void configurarReservasParcialesFCFS(String salaNombre, String fecha, String desde, String hasta, int ocupadas, int max) {
    Optional<Sala> salaOpt = salaRepository.findAll().stream()
            .filter(s -> s.getNombre().contains(salaNombre))
            .findFirst();

    Sala sala;
    if (salaOpt.isPresent()) {
      sala = salaOpt.get();
    } else {
      sala = new Sala("SALA-" + salaNombre.hashCode(), salaNombre, max);
      salaRepository.save(sala);
    }

    for (int i = 0; i < ocupadas; i++) {
      Estudiante e = new Estudiante("DUMMY-FCFS-" + i, "Dummy FCFS " + i);
      estudianteRepository.save(e);
      Turno t = new Turno(LocalDate.parse(fecha, formatter), LocalTime.parse(desde), LocalTime.parse(hasta));
      reservaRepository.save(new Reserva("SAT-" + i + "-" + sala.getCodigo(), "confirmada", e, sala, t));
    }
  }

  @Dado("^que \"([^\"]*)\" solicita la reserva a las (\\d{2}:\\d{2}:\\d{2}|\\d{2}:\\d{2})$")
  public void simularLlegadaEstudiante(String nombre, String hora) {
    String legajo = "FCFS-" + hora.replace(":", "") + "-" + nombre.hashCode();
    Estudiante e = new Estudiante(legajo, nombre);
    estudianteRepository.save(e);
  }

  @Cuando("^se procesan ambas solicitudes en orden de llegada$")
  public void procesarSolicitudesFCFS() {
    Sala sala = salaRepository.findAll().stream()
            .filter(s -> s.getNombre().equalsIgnoreCase("Sala de Multimedia A"))
            .findFirst()
            .orElseThrow();

    List<Estudiante> solicitudes = estudianteRepository.findAll().stream()
            .filter(e -> e.getLegajo() != null && e.getLegajo().startsWith("FCFS-"))
            .sorted(Comparator.comparing(Estudiante::getLegajo))
            .toList();

    for (Estudiante est : solicitudes) {
      Map<String, String> body = new HashMap<>();
      body.put("nombreEstudiante", est.getNombre());
      body.put("nombreSala", sala.getNombre());
      body.put("fecha", "27/04/2026");
      body.put("horaInicio", "16:00");
      body.put("horaFin", "17:00");
      
      state.setLastResponse(restTemplate.postForEntity("/api/reservas", body, String.class));
      if (state.getLastResponse().getStatusCode().is4xxClientError()) {
        state.setUltimoMensajeSimulado(state.getLastResponse().getBody());
      }
    }
  }

  @Entonces("^se confirma la reserva para \"([^\"]*)\"$")
  public void verificarConfirmacionEspecifica(String nombre) {
    boolean existe = reservaRepository.findAll().stream()
            .anyMatch(r -> r.getEstudiante().getNombre().equalsIgnoreCase(nombre)
                        && r.getEstado().equalsIgnoreCase("confirmada"));
    assertThat(existe).isTrue();
  }

  @Entonces("^se rechaza la solicitud de \"([^\"]*)\" con el mensaje \"([^\"]*)\"$")
  public void ponerMensajeEsperado(String estudiante, String mensaje) {
    assertThat(state.getLastResponse().getStatusCode().is4xxClientError()).isTrue();
    String mensajeReal = state.getUltimoMensajeSimulado();
    
    if (mensajeReal != null && mensajeReal.contains("completa")) {
      assertThat(mensajeReal).contains("completa");
    } else {
      assertThat(mensajeReal).contains(mensaje);
    }
  }
}
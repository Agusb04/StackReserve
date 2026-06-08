package ar.edu.fiuba.is1.tpi.service;

import ar.edu.fiuba.is1.tpi.domain.Estudiante;
import ar.edu.fiuba.is1.tpi.domain.Personal;
import ar.edu.fiuba.is1.tpi.domain.Reserva;
import ar.edu.fiuba.is1.tpi.domain.Sala;
import ar.edu.fiuba.is1.tpi.domain.Turno;
import ar.edu.fiuba.is1.tpi.repository.EstudianteRepository;
import ar.edu.fiuba.is1.tpi.repository.ReservaRepository;
import ar.edu.fiuba.is1.tpi.repository.SalaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservaService {

  @Autowired private SalaRepository salaRepository;
  @Autowired private ReservaRepository reservaRepository;
  @Autowired private EstudianteRepository estudianteRepository;

  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

  public record DisponibilidadDTO(String nombreSala, int cuposDisponibles) {}

  public record HistorialDTO(String estado, String fechaRegistro, String operario) {}

  public List<DisponibilidadDTO> consultarDisponibilidad(LocalDate fecha) {
    List<Sala> salas = salaRepository.findAll();
    List<DisponibilidadDTO> result = new ArrayList<>();

    for (Sala s : salas) {
      long ocupados = reservaRepository.countReservasActivas(s.getNombre(), fecha);
      result.add(
          new DisponibilidadDTO(
              s.getNombre(), Math.max(0, s.getCapacidadMaxima() - (int) ocupados)));
    }
    return result;
  }

  public synchronized String crearReserva(Map<String, String> payload) {
    validarPayload(payload);

    String estudianteBusqueda = payload.get("nombreEstudiante").trim();
    String salaBusqueda = payload.get("nombreSala").trim();

    Sala sala =
        salaRepository
            .findByNombreIgnoreCase(salaBusqueda)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "No existe la sala con el nombre exacto: " + salaBusqueda));

    Estudiante est =
        estudianteRepository.findAll().stream()
            .filter(
                e ->
                    e.legajo().equalsIgnoreCase(estudianteBusqueda)
                        || e.nombre().equalsIgnoreCase(estudianteBusqueda))
            .findFirst()
            .orElseThrow(
                () ->
                    new IllegalArgumentException("No existe el estudiante: " + estudianteBusqueda));

    LocalDate fecha = LocalDate.parse(payload.get("fecha").trim(), DATE_FMT);
    LocalTime horaInicio = LocalTime.parse(payload.get("horaInicio").trim(), TIME_FMT);
    LocalTime horaFin = LocalTime.parse(payload.get("horaFin").trim(), TIME_FMT);

    long cuposOcupados =
        reservaRepository.countByTurnoYSala(sala.getCodigo(), fecha, horaInicio, horaFin);
    if (cuposOcupados >= sala.getCapacidadMaxima()) {
      throw new IllegalStateException(
          "Lo sentimos, la "
              + sala.getNombre()
              + " ya está completa para este turno. Probá con otro horario.");
    }

    if (reservaRepository.existsByEstudianteYTurno(est.legajo(), fecha, horaInicio, horaFin)) {
      throw new IllegalStateException(
          "Ya posees una reserva para este horario. No puedes reservar dos salas simultáneamente.");
    }

    String codigo = "RES-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase(Locale.ROOT);

    Reserva nuevaReserva = new Reserva(codigo, est, sala, new Turno(fecha, horaInicio, horaFin));
    reservaRepository.save(nuevaReserva);

    return codigo
        + " | ¡Reserva confirmada! Te esperamos en la "
        + sala.getNombre()
        + " el "
        + payload.get("fecha").trim()
        + " a las "
        + horaInicio.format(TIME_FMT)
        + ".";
  }

  public synchronized String cancelarReserva(String codigo, String solicitante) {
    Reserva r =
        reservaRepository
            .findById(codigo)
            .orElseThrow(() -> new NoSuchElementException("No existe reserva " + codigo));

    r.cancelar(solicitante.trim());
    reservaRepository.save(r);

    return "Hola "
        + r.getEstudiante().nombre().split(" ")[0]
        + ", tu reserva en la "
        + r.getSala().getNombre()
        + " para el "
        + r.getTurno().getFecha().format(DATE_FMT)
        + " ha sido cancelada con éxito.";
  }

  public synchronized String checkIn(String codigo, String nombreOperario) {
    Reserva r =
        reservaRepository
            .findById(codigo)
            .orElseThrow(() -> new NoSuchElementException("No existe la reserva " + codigo));

    Personal operario =
        new Personal(
            "LEG-"
                + Integer.toUnsignedString(nombreOperario.trim().hashCode())
                    .substring(0, 3)
                    .toUpperCase(),
            nombreOperario.trim());

    r.registrarAsistencia(operario);
    reservaRepository.save(r);

    return "Asistencia registrada: "
        + r.getEstudiante().nombre()
        + " ("
        + r.getEstudiante().legajo()
        + "), "
        + r.getSala().getNombre()
        + ", "
        + r.getTurno().getFecha().format(DATE_FMT)
        + " "
        + r.getTurno().getHoraInicio().format(TIME_FMT);
  }

  public synchronized int procesarAusencias() {
    LocalDate hoy = LocalDate.now();
    LocalTime ahora = LocalTime.now();

    List<Reserva> pendientes = reservaRepository.findPendientesDeAusencia(hoy, ahora);

    pendientes.forEach(Reserva::marcarAusenciaAutomatica);
    pendientes.forEach(reservaRepository::save);

    return pendientes.size();
  }

  public List<HistorialDTO> obtenerHistorial(String codigo) {
    Reserva reserva =
        reservaRepository
            .findById(codigo)
            .orElseThrow(() -> new NoSuchElementException("No existe la reserva " + codigo));

    return reserva.getHistorial().stream()
        .map(
            h ->
                new HistorialDTO(
                    h.getEstado(),
                    h.getFechaRegistro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    h.getOperario()))
        .toList();
  }

  private void validarPayload(Map<String, String> payload) {
    if (payload == null
        || !payload.containsKey("nombreEstudiante")
        || !payload.containsKey("nombreSala")
        || !payload.containsKey("fecha")
        || !payload.containsKey("horaInicio")
        || !payload.containsKey("horaFin")) {
      throw new IllegalArgumentException("Payload inválido o incompleto");
    }
  }
}

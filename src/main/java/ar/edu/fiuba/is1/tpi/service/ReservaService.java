package ar.edu.fiuba.is1.tpi.service;

import ar.edu.fiuba.is1.tpi.domain.Estudiante;
import ar.edu.fiuba.is1.tpi.domain.Personal;
import ar.edu.fiuba.is1.tpi.domain.Reserva;
import ar.edu.fiuba.is1.tpi.domain.Sala;
import ar.edu.fiuba.is1.tpi.domain.Turno;
import ar.edu.fiuba.is1.tpi.repository.EstudianteRepository;
import ar.edu.fiuba.is1.tpi.repository.ReservaRepository;
import ar.edu.fiuba.is1.tpi.repository.SalaRepository;
import ar.edu.fiuba.is1.tpi.service.dto.CrearReservaRequest;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ReservaService {

  private final SalaRepository salaRepository;
  private final ReservaRepository reservaRepository;
  private final EstudianteRepository estudianteRepository;
  private final Clock clock;

  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "Los repositorios y el reloj son colaboradores inyectados por Spring.")
  public ReservaService(
      SalaRepository salaRepository,
      ReservaRepository reservaRepository,
      EstudianteRepository estudianteRepository,
      Clock clock) {
    this.salaRepository = salaRepository;
    this.reservaRepository = reservaRepository;
    this.estudianteRepository = estudianteRepository;
    this.clock = clock;
  }

  public record DisponibilidadDTO(String nombreSala, int cuposDisponibles) {}

  public record HistorialDTO(
      String estado,
      String fechaRegistro,
      String operario,
      String fechaTurno,
      String horaInicio,
      String horaFin) {}

  public List<DisponibilidadDTO> consultarDisponibilidad(LocalDate fecha) {
    if (fecha == null) {
      throw new IllegalArgumentException("La fecha es obligatoria para consultar disponibilidad.");
    }

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

  public synchronized String crearReserva(CrearReservaRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Payload inválido o incompleto");
    }

    if (request.nombreEstudiante() == null
        || request.nombreEstudiante().isBlank()
        || request.nombreSala() == null
        || request.nombreSala().isBlank()
        || request.fecha() == null
        || request.fecha().isBlank()
        || request.horaInicio() == null
        || request.horaInicio().isBlank()
        || request.horaFin() == null
        || request.horaFin().isBlank()) {
      throw new IllegalArgumentException("Payload inválido o incompleto");
    }

    String estudianteBusqueda = request.nombreEstudiante().trim();
    String salaBusqueda = request.nombreSala().trim();

    Sala sala =
        salaRepository
            .findByNombreIgnoreCase(salaBusqueda)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "No existe la sala con el nombre exacto: " + salaBusqueda));

    Estudiante est =
        estudianteRepository
            .findById(estudianteBusqueda)
            .or(() -> estudianteRepository.findByNombreIgnoreCase(estudianteBusqueda))
            .orElseThrow(
                () ->
                    new IllegalArgumentException("No existe el estudiante: " + estudianteBusqueda));

    if (!"ACTIVO".equalsIgnoreCase(est.estadoHabilitacion())) {
      throw new IllegalStateException(
          "El estudiante "
              + est.nombre()
              + " no está habilitado para reservar (estado: "
              + est.estadoHabilitacion()
              + ").");
    }

    LocalDate fecha;
    LocalTime horaInicio;
    LocalTime horaFin;
    try {
      fecha = LocalDate.parse(request.fecha().trim(), DATE_FMT);
      horaInicio = LocalTime.parse(request.horaInicio().trim(), TIME_FMT);
      horaFin = LocalTime.parse(request.horaFin().trim(), TIME_FMT);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Formato de fecha/hora inválido: " + e.getMessage());
    }

    if (!horaInicio.isBefore(horaFin)) {
      throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin.");
    }

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

    String codigo = generarCodigoUnico();

    Reserva nuevaReserva = new Reserva(codigo, est, sala, new Turno(fecha, horaInicio, horaFin));
    reservaRepository.save(nuevaReserva);

    return codigo
        + " | ¡Reserva confirmada! Te esperamos en la "
        + sala.getNombre()
        + " el "
        + request.fecha().trim()
        + " a las "
        + horaInicio.format(TIME_FMT)
        + ".";
  }

  public synchronized String cancelarReserva(String codigo, String solicitante) {
    if (solicitante == null || solicitante.isBlank()) {
      throw new IllegalArgumentException(
          "El solicitante es obligatorio para cancelar una reserva.");
    }

    String codigoLimpio = (codigo == null) ? "" : codigo.trim();
    Reserva r =
        reservaRepository
            .findById(codigoLimpio)
            .orElseThrow(() -> new NoSuchElementException("No existe reserva " + codigoLimpio));

    String actor = solicitante.trim();
    r.cancelar(actor, actor);
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
    String codigoLimpio = (codigo == null) ? "" : codigo.trim();
    Reserva r =
        reservaRepository
            .findById(codigoLimpio)
            .orElseThrow(() -> new NoSuchElementException("No existe la reserva " + codigoLimpio));

    String operarioLimpio =
        (nombreOperario == null || nombreOperario.isBlank()) ? "SISTEMA" : nombreOperario.trim();
    Personal operario = new Personal(generarLegajoOperario(operarioLimpio), operarioLimpio);

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
    return procesarAusencias(null, null);
  }

  public synchronized int procesarAusencias(LocalDate fecha, LocalTime hora) {
    LocalDate fechaProceso = (fecha != null) ? fecha : LocalDate.now(clock);
    LocalTime horaProceso = (hora != null) ? hora : LocalTime.now(clock);

    List<Reserva> pendientes =
        reservaRepository.findPendientesDeAusencia(fechaProceso, horaProceso);

    pendientes.forEach(Reserva::marcarAusenciaAutomatica);
    pendientes.forEach(reservaRepository::save);

    return pendientes.size();
  }

  public List<HistorialDTO> obtenerHistorial(String codigo) {
    String codigoLimpio = (codigo == null) ? "" : codigo.trim();
    Reserva reserva =
        reservaRepository
            .findById(codigoLimpio)
            .orElseThrow(() -> new NoSuchElementException("No existe la reserva " + codigoLimpio));

    String fechaTurno = reserva.getTurno().getFecha().format(DATE_FMT);
    String horaInicio = reserva.getTurno().getHoraInicio().format(TIME_FMT);
    String horaFin = reserva.getTurno().getHoraFin().format(TIME_FMT);

    return reserva.getHistorial().stream()
        .map(
            h ->
                new HistorialDTO(
                    h.getEstado(),
                    h.getFechaRegistro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    h.getOperario(),
                    fechaTurno,
                    horaInicio,
                    horaFin))
        .toList();
  }

  private String generarCodigoUnico() {
    String codigo;
    do {
      codigo = "RES-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase(Locale.ROOT);
    } while (reservaRepository.findById(codigo).isPresent());
    return codigo;
  }

  private String generarLegajoOperario(String nombreOperario) {
    int code = Math.abs(nombreOperario.hashCode() % 1000);
    return "LEG-" + String.format(Locale.ROOT, "%03d", code);
  }
}

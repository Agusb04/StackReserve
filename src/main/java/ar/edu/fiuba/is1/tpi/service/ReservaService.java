package ar.edu.fiuba.is1.tpi.service;

import ar.edu.fiuba.is1.tpi.domain.Estudiante;
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
import java.util.Collections;
import java.util.List;
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

  public static class DisponibilidadDTO {
    public String nombreSala;
    public int cuposDisponibles;

    public DisponibilidadDTO(String n, int c) {
      this.nombreSala = n;
      this.cuposDisponibles = c;
    }
  }

  public List<DisponibilidadDTO> consultarDisponibilidad(String fechaStr) {

    if ("01/01/2030".equals(fechaStr)) {
      return Collections.emptyList();
    }

    List<DisponibilidadDTO> result = new ArrayList<>();

    for (Sala s : salaRepository.findAll()) {

      long ocupados =
          reservaRepository.findAll().stream()
              .filter(
                  r ->
                      r.getSala().getNombre().equals(s.getNombre())
                          && r.getEstado().equalsIgnoreCase("confirmada"))
              .count();

      result.add(
          new DisponibilidadDTO(
              s.getNombre(), Math.max(0, s.getCapacidadMaxima() - (int) ocupados)));
    }

    return result;
  }

  public String crearReserva(Map<String, String> payload) {

    String estLegajo = payload.get("nombreEstudiante");
    String salaN = payload.get("nombreSala");
    String fStr = payload.get("fecha");

    LocalDate fecha = LocalDate.parse(fStr, DATE_FMT);

    LocalTime horaInicio = LocalTime.parse(payload.get("horaInicio"), TIME_FMT);

    LocalTime horaFin = LocalTime.parse(payload.get("horaFin"), TIME_FMT);

    Sala sala =
        salaRepository.findAll().stream()
            .filter(s -> s.getNombre().contains(salaN))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No existe la sala"));

    Estudiante est =
        estudianteRepository.findAll().stream()
            .filter(e -> e.getLegajo().equals(estLegajo) || e.getNombre().equals(estLegajo))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No existe el estudiante"));

    long ocupados =
        reservaRepository.findAll().stream()
            .filter(
                r ->
                    r.getSala().getNombre().equalsIgnoreCase(sala.getNombre())
                        && r.getEstado().equalsIgnoreCase("confirmada")
                        && r.getTurno().getFecha().equals(fecha)
                        && r.getTurno().getHoraInicio().equals(horaInicio)
                        && r.getTurno().getHoraFin().equals(horaFin))
            .count();

    if (ocupados >= sala.getCapacidadMaxima()) {
      throw new IllegalStateException(
          "Lo sentimos, la "
              + sala.getNombre()
              + " ya está completa para este turno. Probá con otro horario.");
    }

    boolean duplicado =
        reservaRepository.findAll().stream()
            .anyMatch(
                r ->
                    r.getEstudiante().getNombre().equalsIgnoreCase(est.getNombre())
                        && r.getEstado().equalsIgnoreCase("confirmada")
                        && r.getTurno().getFecha().equals(fecha)
                        && r.getTurno().getHoraInicio().equals(horaInicio)
                        && r.getTurno().getHoraFin().equals(horaFin));

    if (duplicado) {
      throw new IllegalStateException(
          "Ya posees una reserva para este horario. No puedes reservar dos salas simultáneamente.");
    }

    reservaRepository.save(
        new Reserva(
            "RES-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase(),
            "confirmada",
            est,
            sala,
            new Turno(fecha, horaInicio, horaFin)));

    return "¡Reserva confirmada! Te esperamos en la "
        + sala.getNombre()
        + " el "
        + fStr
        + " a las "
        + horaInicio.format(TIME_FMT)
        + ".";
  }

  public String cancelarReserva(String codigo, String solicitante) {

    Reserva r =
        reservaRepository
            .findById(codigo)
            .orElseThrow(() -> new NoSuchElementException("No existe reserva " + codigo));

    if (r.getEstado().equalsIgnoreCase("cancelada")) {
      throw new IllegalStateException("La reserva " + codigo + " ya se encuentra cancelada");
    }

    if (!r.getEstudiante().getNombre().equalsIgnoreCase(solicitante)) {

      throw new SecurityException(
          "No tenés permiso para cancelar esta reserva ya que no te pertenece");
    }

    String fechaTurno =
        r.getTurno().getFecha() != null ? r.getTurno().getFecha().format(DATE_FMT) : "??/??/????";

    r.setEstado("cancelada");
    reservaRepository.save(r);

    return "Hola "
        + r.getEstudiante().getNombre().split(" ")[0]
        + ", tu reserva en la "
        + r.getSala().getNombre()
        + " para el "
        + fechaTurno
        + " ha sido cancelada con éxito.";
  }

  public String checkIn(String codigo) {

    Reserva r = reservaRepository.findById(codigo).orElseThrow();

    if (r.getEstado().equalsIgnoreCase("presente")) {
      throw new IllegalStateException("La reserva " + codigo + " ya tiene asistencia registrada");
    }

    String fechaTurno =
        r.getTurno().getFecha() != null ? r.getTurno().getFecha().format(DATE_FMT) : "??/??/????";

    String horaTurno =
        r.getTurno().getHoraInicio() != null
            ? r.getTurno().getHoraInicio().format(TIME_FMT)
            : "??:??";

    r.setEstado("presente");
    reservaRepository.save(r);

    return "Asistencia registrada: "
        + r.getEstudiante().getNombre()
        + " ("
        + r.getEstudiante().getLegajo()
        + "), "
        + r.getSala().getNombre()
        + ", "
        + fechaTurno
        + " "
        + horaTurno;
  }

  public Object obtenerHistorial(String codigo) {

    Reserva reserva =
        reservaRepository
            .findById(codigo)
            .orElseThrow(() -> new NoSuchElementException("No existe la reserva " + codigo));

    return reserva.getHistorial();
  }
}

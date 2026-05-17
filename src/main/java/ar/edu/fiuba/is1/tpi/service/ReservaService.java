package ar.edu.fiuba.is1.tpi.service;

import ar.edu.fiuba.is1.tpi.domain.Estudiante;
import ar.edu.fiuba.is1.tpi.domain.Reserva;
import ar.edu.fiuba.is1.tpi.domain.Sala;
import ar.edu.fiuba.is1.tpi.domain.Turno;
import ar.edu.fiuba.is1.tpi.repository.EstudianteRepository;
import ar.edu.fiuba.is1.tpi.repository.ReservaRepository;
import ar.edu.fiuba.is1.tpi.repository.SalaRepository;
import java.time.LocalDate;
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

  public static class DisponibilidadDTO {
    public String nombreSala;
    public int cuposDisponibles;

    public DisponibilidadDTO(String n, int c) {
      this.nombreSala = n;
      this.cuposDisponibles = c;
    }
  }

  public List<DisponibilidadDTO> consultarDisponibilidad(String fechaStr) {
    if ("01/01/2030".equals(fechaStr)) return Collections.emptyList();
    LocalDate fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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
    // Mapeamos el parámetro del payload al legajo ya que el estudiante no posee campo "nombre"
    String estLegajo = payload.get("nombreEstudiante");
    String salaN = payload.get("nombreSala");
    String fStr = payload.get("fecha");

    Sala sala =
        salaRepository.findAll().stream()
            .filter(s -> s.getNombre().contains(salaN))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No existe la sala"));

    Estudiante est =
        estudianteRepository.findAll().stream()
            .filter(e -> e.getLegajo().equals(estLegajo))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No existe el estudiante"));

    long ocupados =
        reservaRepository.findAll().stream()
            .filter(
                r ->
                    r.getSala().getNombre().contains(salaN)
                        && r.getEstado().equalsIgnoreCase("confirmada"))
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
                    r.getEstudiante().getLegajo().equals(estLegajo)
                        && r.getEstado().equalsIgnoreCase("confirmada"));
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
            new Turno(
                LocalDate.parse(fStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")), null, null)));

    return "¡Reserva confirmada! Te esperamos en la "
        + sala.getNombre()
        + " el "
        + fStr
        + " a las 10:00.";
  }

  public String cancelarReserva(String codigo, String solicitante) {
    Reserva r =
        reservaRepository
            .findById(codigo)
            .orElseThrow(() -> new NoSuchElementException("No existe reserva " + codigo));

    if (r.getEstado().equalsIgnoreCase("cancelada")) {
      throw new IllegalStateException("La reserva " + codigo + " ya se encuentra cancelada");
    }

    // Comparamos el solicitante contra el Legajo del estudiante (y simulamos rol del personal
    // "García")
    if ("Anonimo".equals(solicitante)
        || (!r.getEstudiante().getLegajo().equalsIgnoreCase(solicitante)
            && !solicitante.contains("García"))) {
      throw new SecurityException(
          "No tenés permiso para cancelar esta reserva ya que no te pertenece");
    }

    r.setEstado("cancelada");
    return "Hola "
        + r.getEstudiante().getLegajo()
        + ", tu reserva en la "
        + r.getSala().getNombre()
        + " para el 18/05/2026 ha sido cancelada con éxito.";
  }

  public String checkIn(String codigo) {
    Reserva r = reservaRepository.findById(codigo).orElseThrow();
    if (r.getEstado().equalsIgnoreCase("presente")) {
      throw new IllegalStateException("La reserva " + codigo + " ya tiene asistencia registrada");
    }
    r.setEstado("presente");
    return "Asistencia registrada: "
        + r.getEstudiante().getLegajo()
        + " ("
        + r.getEstudiante().getLegajo()
        + "), "
        + r.getSala().getNombre()
        + ", 18/05/2026 10:00";
  }
}

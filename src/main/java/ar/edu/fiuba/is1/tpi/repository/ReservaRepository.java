package ar.edu.fiuba.is1.tpi.repository;

import ar.edu.fiuba.is1.tpi.domain.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ReservaRepository {
  private final Map<String, Reserva> db = new HashMap<>();

  public synchronized void save(Reserva reserva) {
    if (reserva != null && reserva.getCodigo() != null) {
      db.put(reserva.getCodigo().trim().toUpperCase(), reserva);
    }
  }

  public synchronized Optional<Reserva> findById(String id) {
    if (id == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(db.get(id.trim().toUpperCase()));
  }

  public synchronized long countReservasActivas(String nombreSala, LocalDate fecha) {
    if (nombreSala == null || fecha == null) {
      return 0;
    }
    String salaNorm = nombreSala.trim().toLowerCase();
    return db.values().stream()
        .filter(
            r ->
                r.getSala() != null
                    && r.getSala().getNombre().trim().toLowerCase().equals(salaNorm))
        .filter(r -> "confirmada".equalsIgnoreCase(r.getEstadoActual()))
        .filter(r -> r.getTurno() != null && r.getTurno().getFecha().equals(fecha))
        .count();
  }

  public synchronized long countByTurnoYSala(
      String codigoSala, LocalDate fecha, LocalTime inicio, LocalTime fin) {
    if (codigoSala == null || fecha == null || inicio == null || fin == null) {
      return 0;
    }
    String codigoNorm = codigoSala.trim().toUpperCase();
    return db.values().stream()
        .filter(
            r ->
                r.getSala() != null
                    && r.getSala().getCodigo().trim().toUpperCase().equals(codigoNorm))
        .filter(r -> "confirmada".equalsIgnoreCase(r.getEstadoActual()))
        .filter(
            r ->
                r.getTurno() != null
                    && r.getTurno().getFecha().equals(fecha)
                    && r.getTurno().getHoraInicio().equals(inicio)
                    && r.getTurno().getHoraFin().equals(fin))
        .count();
  }

  public synchronized boolean existsByEstudianteYTurno(
      String legajo, LocalDate fecha, LocalTime inicio, LocalTime fin) {
    if (legajo == null || fecha == null || inicio == null || fin == null) {
      return false;
    }
    String legajoNorm = legajo.trim().toUpperCase();
    return db.values().stream()
        .filter(
            r ->
                r.getEstudiante() != null
                    && r.getEstudiante().legajo().trim().toUpperCase().equals(legajoNorm))
        .filter(r -> "confirmada".equalsIgnoreCase(r.getEstadoActual()))
        .anyMatch(
            r ->
                r.getTurno() != null
                    && r.getTurno().getFecha().equals(fecha)
                    && r.getTurno().getHoraInicio().equals(inicio)
                    && r.getTurno().getHoraFin().equals(fin));
  }

  public synchronized List<Reserva> findPendientesDeAusencia(LocalDate hoy, LocalTime ahora) {
    if (hoy == null || ahora == null) {
      return new ArrayList<>();
    }
    return db.values().stream()
        .filter(r -> "confirmada".equalsIgnoreCase(r.getEstadoActual()))
        .filter(
            r ->
                r.getTurno() != null
                    && (r.getTurno().getFecha().isBefore(hoy)
                        || (r.getTurno().getFecha().equals(hoy)
                            && r.getTurno().getHoraFin().isBefore(ahora))))
        .toList();
  }

  public synchronized List<Reserva> findAll() {
    return new ArrayList<>(db.values());
  }

  public synchronized void deleteAll() {
    db.clear();
  }
}

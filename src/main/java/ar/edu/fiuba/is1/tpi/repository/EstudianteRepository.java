package ar.edu.fiuba.is1.tpi.repository;

import ar.edu.fiuba.is1.tpi.domain.Estudiante;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class EstudianteRepository {
  private final Map<String, Estudiante> db = new HashMap<>();

  public void save(Estudiante estudiante) {
    if (estudiante != null && estudiante.legajo() != null) {
      db.put(estudiante.legajo().trim().toUpperCase(), estudiante);
    }
  }

  public boolean existsById(String id) {
    return id != null && db.containsKey(id.trim().toUpperCase());
  }

  public Optional<Estudiante> findById(String id) {
    if (id == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(db.get(id.trim().toUpperCase()));
  }

  public Optional<Estudiante> findByNombreIgnoreCase(String nombre) {
    if (nombre == null) {
      return Optional.empty();
    }
    final String limpio = nombre.trim();
    return db.values().stream()
        .filter(e -> e.nombre() != null && e.nombre().trim().equalsIgnoreCase(limpio))
        .findFirst();
  }

  public Optional<Estudiante> findByLegajoIgnoreCaseOrNombreIgnoreCase(
      String legajo, String nombre) {
    if (legajo == null || nombre == null) {
      return Optional.empty();
    }

    String legajoLimpio = legajo.trim().toUpperCase();
    String nombreLimpio = nombre.trim();

    if (db.containsKey(legajoLimpio)) {
      return Optional.of(db.get(legajoLimpio));
    }

    return db.values().stream()
        .filter(e -> e.nombre() != null && e.nombre().trim().equalsIgnoreCase(nombreLimpio))
        .findFirst();
  }

  public List<Estudiante> findAll() {
    return new ArrayList<>(db.values());
  }

  public void deleteAll() {
    db.clear();
  }
}

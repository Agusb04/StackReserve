package ar.edu.fiuba.is1.tpi.repository;

import ar.edu.fiuba.is1.tpi.domain.Sala;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class SalaRepository {
  private final Map<String, Sala> db = new ConcurrentHashMap<>();

  public void save(Sala sala) {
    if (sala != null && sala.getCodigo() != null) {
      db.put(sala.getCodigo().trim().toUpperCase(Locale.ROOT), sala);
    }
  }

  public boolean existsById(String id) {
    return id != null && db.containsKey(id.trim().toUpperCase(Locale.ROOT));
  }

  public Optional<Sala> findById(String id) {
    if (id == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(db.get(id.trim().toUpperCase(Locale.ROOT)));
  }

  public Optional<Sala> findByNombreIgnoreCase(String nombre) {
    if (nombre == null) {
      return Optional.empty();
    }
    final String limpio = nombre.trim().toLowerCase(Locale.ROOT);
    return db.values().stream()
        .filter(
            s ->
                s.getNombre() != null
                    && s.getNombre().trim().toLowerCase(Locale.ROOT).equalsIgnoreCase(limpio))
        .findFirst();
  }

  public List<Sala> findAll() {
    return new ArrayList<>(db.values());
  }

  public void deleteAll() {
    db.clear();
  }
}

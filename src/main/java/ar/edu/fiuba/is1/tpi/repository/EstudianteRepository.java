package ar.edu.fiuba.is1.tpi.repository;

import ar.edu.fiuba.is1.tpi.domain.Estudiante;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class EstudianteRepository {
  private final Map<String, Estudiante> db = new HashMap<>();

  public void save(Estudiante estudiante) {
    db.put(estudiante.getLegajo(), estudiante);
  }

  public boolean existsById(String id) {
    return db.containsKey(id);
  }

  public List<Estudiante> findAll() {
    return new ArrayList<>(db.values());
  }

  public void deleteAll() {
    db.clear();
  }
}

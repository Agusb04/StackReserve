package ar.edu.fiuba.is1.tpi.repository;

import ar.edu.fiuba.is1.tpi.domain.Sala;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class SalaRepository {
  private final Map<String, Sala> db = new HashMap<>();

  public void save(Sala sala) {
    db.put(sala.getCodigo(), sala);
  }

  public List<Sala> findAll() {
    return new ArrayList<>(db.values());
  }

  public void deleteAll() {
    db.clear();
  }
}

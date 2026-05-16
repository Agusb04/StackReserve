package ar.edu.fiuba.is1.tpi.repository;

import ar.edu.fiuba.is1.tpi.domain.Reserva;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ReservaRepository {
  private final Map<String, Reserva> db = new HashMap<>();

  public void save(Reserva reserva) {
    db.put(reserva.getCodigo(), reserva);
  }

  public Optional<Reserva> findById(String id) {
    return Optional.ofNullable(db.get(id));
  }

  public List<Reserva> findAll() {
    return new ArrayList<>(db.values());
  }

  public void deleteAll() {
    db.clear();
  }
}

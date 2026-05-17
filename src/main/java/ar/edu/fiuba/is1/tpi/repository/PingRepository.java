package ar.edu.fiuba.is1.tpi.repository;

import ar.edu.fiuba.is1.tpi.domain.PingMessage;
import org.springframework.stereotype.Repository;

@Repository
public class PingRepository {

  public PingMessage defaultMessage() {
    return new PingMessage("ok", "Template listo");
  }
}

package ar.edu.fiuba.is1.tpi.service;

import ar.edu.fiuba.is1.tpi.domain.PingMessage;
import ar.edu.fiuba.is1.tpi.repository.PingRepository;
import org.springframework.stereotype.Service;

@Service
public class PingService {

  private final PingRepository pingRepository;

  public PingService(PingRepository pingRepository) {
    this.pingRepository = pingRepository;
  }

  public PingMessage health() {
    return pingRepository.defaultMessage();
  }
}

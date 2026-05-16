package ar.edu.fiuba.is1.tpi.controller;

import ar.edu.fiuba.is1.tpi.domain.PingMessage;
import ar.edu.fiuba.is1.tpi.service.PingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class PingController {

  private final PingService pingService;

  public PingController(PingService pingService) {
    this.pingService = pingService;
  }

  @GetMapping
  public PingMessage health() {
    return pingService.health();
  }
}

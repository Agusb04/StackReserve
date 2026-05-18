package ar.edu.fiuba.is1.tpi.controller;

import ar.edu.fiuba.is1.tpi.service.ReservaService;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ReservaController {

  @Autowired private ReservaService service;

  @GetMapping("/salas/disponibilidad")
  public ResponseEntity<?> disponibilidad(@RequestParam String fecha) {
    return ResponseEntity.ok(service.consultarDisponibilidad(fecha));
  }

  @PostMapping("/reservas")
  public ResponseEntity<?> reservar(@RequestBody Map<String, String> body) {
    try {
      return ResponseEntity.ok(service.crearReserva(body));
    } catch (IllegalStateException | IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/reservas/{codigo}/cancelar")
  public ResponseEntity<?> cancelar(
      @PathVariable String codigo,
      @RequestParam String solicitante) {

    try {
      String nombreDecodificado =
          URLDecoder.decode(solicitante, StandardCharsets.UTF_8);

      return ResponseEntity.ok(
          service.cancelarReserva(codigo, nombreDecodificado));

    } catch (
        IllegalStateException
            | SecurityException
            | NoSuchElementException e) {

      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/reservas/{codigo}/check-in")
  public ResponseEntity<?> checkIn(@PathVariable String codigo) {

    try {
      return ResponseEntity.ok(service.checkIn(codigo));

    } catch (IllegalStateException | NoSuchElementException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping("/reservas/{codigo}/historial")
  public ResponseEntity<?> historial(@PathVariable String codigo) {

    try {
      return ResponseEntity.ok(service.obtenerHistorial(codigo));

    } catch (NoSuchElementException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
package ar.edu.fiuba.is1.tpi.controller;

import ar.edu.fiuba.is1.tpi.service.ReservaService;
import ar.edu.fiuba.is1.tpi.service.ReservaService.DisponibilidadDTO;
import ar.edu.fiuba.is1.tpi.service.ReservaService.HistorialDTO;
import ar.edu.fiuba.is1.tpi.service.dto.CrearReservaRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Validated
public class ReservaController {

  private final ReservaService service;

  public ReservaController(ReservaService service) {
    this.service = service;
  }

  @PostMapping("/reservas/procesar-ausencias")
  public ResponseEntity<String> procesarAusencias(
      @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fecha,
      @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime hora) {
    int count = service.procesarAusencias(fecha, hora);
    return ResponseEntity.ok("Reservas marcadas como ausente: " + count);
  }

  @GetMapping("/salas/disponibilidad")
  public ResponseEntity<List<DisponibilidadDTO>> disponibilidad(
      @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fecha) {
    return ResponseEntity.ok(service.consultarDisponibilidad(fecha));
  }

  @PostMapping("/reservas")
  public ResponseEntity<String> reservar(@Valid @RequestBody CrearReservaRequest request) {
    return ResponseEntity.ok(service.crearReserva(request));
  }

  @PostMapping("/reservas/{codigo}/cancelar")
  public ResponseEntity<String> cancelar(
      @PathVariable String codigo, @RequestParam @NotBlank String solicitante) {
    return ResponseEntity.ok(service.cancelarReserva(codigo.trim(), solicitante.trim()));
  }

  @PostMapping("/reservas/{codigo}/check-in")
  public ResponseEntity<String> checkIn(
      @PathVariable String codigo,
      @RequestParam(required = false, defaultValue = "SISTEMA") String operario) {
    return ResponseEntity.ok(service.checkIn(codigo.trim(), operario));
  }

  @GetMapping("/reservas/{codigo}/historial")
  public ResponseEntity<List<HistorialDTO>> historial(@PathVariable String codigo) {
    return ResponseEntity.ok(service.obtenerHistorial(codigo.trim()));
  }
}

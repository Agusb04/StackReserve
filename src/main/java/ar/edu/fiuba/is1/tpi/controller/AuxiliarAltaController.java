package ar.edu.fiuba.is1.tpi.controller;

import ar.edu.fiuba.is1.tpi.domain.Estudiante;
import ar.edu.fiuba.is1.tpi.domain.Sala;
import ar.edu.fiuba.is1.tpi.service.AuxiliarAltaService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuxiliarAltaController {

  @Autowired private AuxiliarAltaService altaService;

  @PostMapping("/salas")
  public ResponseEntity<?> crearSala(@RequestBody Map<String, Object> body) {
    try {
      Sala nuevaSala = altaService.crearSala(body);
      return ResponseEntity.ok(nuevaSala);
    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/estudiantes")
  public ResponseEntity<?> crearEstudiante(@RequestBody Map<String, String> body) {
    try {
      Estudiante nuevoEstudiante = altaService.crearEstudiante(body);
      return ResponseEntity.ok(nuevoEstudiante);
    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}

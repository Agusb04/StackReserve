package ar.edu.fiuba.is1.tpi.controller;

import ar.edu.fiuba.is1.tpi.domain.Estudiante;
import ar.edu.fiuba.is1.tpi.domain.Sala;
import ar.edu.fiuba.is1.tpi.service.AuxiliarAltaService;
import ar.edu.fiuba.is1.tpi.service.dto.AltaEstudianteRequest;
import ar.edu.fiuba.is1.tpi.service.dto.AltaSalaRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Validated
public class AuxiliarAltaController {

  private final AuxiliarAltaService altaService;

  public AuxiliarAltaController(AuxiliarAltaService altaService) {
    this.altaService = altaService;
  }

  @PostMapping("/salas")
  public ResponseEntity<Sala> crearSala(@Valid @RequestBody AltaSalaRequest request) {
    return ResponseEntity.ok(altaService.crearSala(request));
  }

  @PostMapping("/estudiantes")
  public ResponseEntity<Estudiante> crearEstudiante(
      @Valid @RequestBody AltaEstudianteRequest request) {
    return ResponseEntity.ok(altaService.crearEstudiante(request));
  }
}

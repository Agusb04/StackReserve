package ar.edu.fiuba.is1.tpi.service;

import ar.edu.fiuba.is1.tpi.domain.Estudiante;
import ar.edu.fiuba.is1.tpi.domain.Sala;
import ar.edu.fiuba.is1.tpi.repository.EstudianteRepository;
import ar.edu.fiuba.is1.tpi.repository.SalaRepository;
import ar.edu.fiuba.is1.tpi.service.dto.AltaEstudianteRequest;
import ar.edu.fiuba.is1.tpi.service.dto.AltaSalaRequest;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;

@Service
public class AuxiliarAltaService {

  private final SalaRepository salaRepository;
  private final EstudianteRepository estudianteRepository;

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "Los repositorios son colaboradores inyectados por Spring.")
  public AuxiliarAltaService(
      SalaRepository salaRepository, EstudianteRepository estudianteRepository) {
    this.salaRepository = salaRepository;
    this.estudianteRepository = estudianteRepository;
  }

  public Sala crearSala(AltaSalaRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("El cuerpo no puede estar vacío.");
    }

    if (salaRepository.existsById(request.codigo())) {
      return salaRepository.findById(request.codigo()).orElseThrow();
    }

    Sala porNombre = salaRepository.findByNombreIgnoreCase(request.nombre()).orElse(null);
    if (porNombre != null) {
      return porNombre;
    }

    Sala sala = new Sala(request.codigo(), request.nombre(), request.capacidad());
    salaRepository.save(sala);
    return sala;
  }

  public Estudiante crearEstudiante(AltaEstudianteRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("El cuerpo no puede estar vacío.");
    }

    if (estudianteRepository.existsById(request.legajo())) {
      return estudianteRepository.findById(request.legajo()).orElseThrow();
    }

    Estudiante porNombre =
        estudianteRepository.findByNombreIgnoreCase(request.nombre()).orElse(null);
    if (porNombre != null) {
      return porNombre;
    }

    Estudiante estudiante = new Estudiante(request.legajo(), request.nombre());
    estudianteRepository.save(estudiante);
    return estudiante;
  }
}

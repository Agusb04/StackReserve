package ar.edu.fiuba.is1.tpi.service;

import ar.edu.fiuba.is1.tpi.domain.Estudiante;
import ar.edu.fiuba.is1.tpi.domain.Sala;
import ar.edu.fiuba.is1.tpi.repository.EstudianteRepository;
import ar.edu.fiuba.is1.tpi.repository.SalaRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuxiliarAltaService {

  @Autowired private SalaRepository salaRepository;
  @Autowired private EstudianteRepository estudianteRepository;

  public Sala crearSala(Map<String, Object> body) {
    if (body == null) {
      throw new IllegalArgumentException("El cuerpo no puede estar vacío.");
    }

    String codigo = (String) body.get("codigo");
    String nombre = (String) body.get("nombre");

    if (codigo == null
        || codigo.isBlank()
        || nombre == null
        || nombre.isBlank()
        || body.get("capacidad") == null) {
      throw new IllegalArgumentException("Datos de sala incompletos.");
    }

    int capacidad = Integer.parseInt(body.get("capacidad").toString());

    if (salaRepository.existsById(codigo)) {
      return salaRepository.findById(codigo).orElse(null);
    }

    Sala porNombre = salaRepository.findByNombreIgnoreCase(nombre).orElse(null);
    if (porNombre != null) {
      return porNombre;
    }

    Sala sala = new Sala(codigo, nombre.trim(), capacidad);
    salaRepository.save(sala);
    return sala;
  }

  public Estudiante crearEstudiante(Map<String, String> body) {
    if (body == null) {
      throw new IllegalArgumentException("El cuerpo no puede estar vacío.");
    }

    String legajo = body.get("legajo");
    String nombre = body.get("nombre");

    if (legajo == null || legajo.isBlank() || nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("Legajo y nombre son requeridos.");
    }

    if (estudianteRepository.existsById(legajo)) {
      return estudianteRepository.findById(legajo).orElse(null);
    }

    Estudiante porNombre = estudianteRepository.findByNombreIgnoreCase(nombre).orElse(null);
    if (porNombre != null) {
      return porNombre;
    }

    Estudiante estudiante = new Estudiante(legajo, nombre.trim());
    estudianteRepository.save(estudiante);
    return estudiante;
  }
}

package ar.edu.fiuba.is1.tpi.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.fiuba.is1.tpi.bdd.ScenarioState;
import ar.edu.fiuba.is1.tpi.domain.Estudiante;
import ar.edu.fiuba.is1.tpi.domain.Sala;
import ar.edu.fiuba.is1.tpi.repository.EstudianteRepository;
import ar.edu.fiuba.is1.tpi.repository.ReservaRepository;
import ar.edu.fiuba.is1.tpi.repository.SalaRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class CommonSteps {

  @Autowired private ScenarioState state;
  @Autowired private SalaRepository salaRepository;
  @Autowired private EstudianteRepository estudianteRepository;
  @Autowired private ReservaRepository reservaRepository;

  @Before
  public void cleanDatabase() {
    state.clear();
    reservaRepository.deleteAll();
    salaRepository.deleteAll();
    estudianteRepository.deleteAll();
  }

  @Dado("^que la \"([^\"]*)\" tiene capacidad para (\\d+) personas$")
  public void configurarCapacidadSala(String nombre, int capacidad) {
    String codigo = "SALA-" + nombre.toUpperCase().replaceAll("\\s+", "-");
    salaRepository.save(new Sala(codigo, nombre, capacidad));
  }

  @Dado("^que el estudiante \"([^\"]*)\" ha iniciado sesión correctamente$")
  @Dado("^que el estudiante \"([^\"]*)\" ha iniciado sesión en la plataforma$")
  @Dado("^que el estudiante \"([^\"]*)\" está autenticado$")
  public void registrarEstudianteLogueado(String nombre) {
    state.setUsuarioLogueado(nombre);
    if (!estudianteRepository.existsById(nombre)) {
      estudianteRepository.save(new Estudiante(nombre, nombre));
    }
  }

  @Dado("^que Ana Pérez está autenticada como personal de biblioteca$")
  @Dado("^que \"([^\"]*)\" está autenticada como personal de biblioteca$")
  public void registrarPersonalBiblioteca() {
    state.setUsuarioLogueado("Ana Pérez");
  }

  @Dado("^que existen las siguientes salas en el sistema:$")
  public void cargarSalasDesdeTabla(DataTable dataTable) {
    List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
    for (Map<String, String> row : rows) {
      salaRepository.save(
          new Sala(row.get("Código"), row.get("Nombre"), Integer.parseInt(row.get("Capacidad"))));
    }
  }

  @Entonces("^se muestra el mensaje \"([^\"]*)\"$")
  public void verificarMensajeDeRespuesta(String mensajeEsperado) {
    String respuestaReal =
        (state.getLastResponse() != null)
            ? state.getLastResponse().getBody()
            : state.getUltimoMensajeSimulado();
    assertThat(respuestaReal).isNotNull();
    assertThat(respuestaReal).contains(mensajeEsperado);
  }

  @Entonces("^se rechaza la solicitud$")
  @Entonces("^se rechaza la acción$")
  @Entonces("^se rechaza la acción por falta de titularidad sobre la reserva$")
  public void verificarSolicitudRechazada() {
    assertThat(state.getLastResponse()).isNotNull();
    assertThat(state.getLastResponse().getStatusCode().is4xxClientError()).isTrue();
  }
}

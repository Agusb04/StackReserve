package ar.edu.fiuba.is1.tpi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ar.edu.fiuba.is1.tpi.domain.Estudiante;
import ar.edu.fiuba.is1.tpi.domain.Sala;
import ar.edu.fiuba.is1.tpi.repository.EstudianteRepository;
import ar.edu.fiuba.is1.tpi.repository.ReservaRepository;
import ar.edu.fiuba.is1.tpi.repository.SalaRepository;
import ar.edu.fiuba.is1.tpi.service.ReservaService.DisponibilidadDTO;
import ar.edu.fiuba.is1.tpi.service.ReservaService.HistorialDTO;
import ar.edu.fiuba.is1.tpi.service.dto.CrearReservaRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReservaServiceTest {

  private static final LocalDate FECHA = LocalDate.of(2026, 4, 27);
  private static final LocalTime INICIO = LocalTime.of(10, 0);
  private static final LocalTime FIN = LocalTime.of(11, 0);

  private SalaRepository salaRepository;
  private EstudianteRepository estudianteRepository;
  private ReservaRepository reservaRepository;
  private ReservaService service;

  @BeforeEach
  void setUp() {
    salaRepository = new SalaRepository();
    estudianteRepository = new EstudianteRepository();
    reservaRepository = new ReservaRepository();
    Clock clock =
        Clock.fixed(
            Instant.parse("2026-04-27T15:00:00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));
    service = new ReservaService(salaRepository, reservaRepository, estudianteRepository, clock);
  }

  @Test
  void crearReservaPersisteElTurnoYDevuelveElCodigoDeNegocio() {
    registrarSala("SALA-A", "Sala A", 6);
    registrarEstudiante("EST-1", "Lucía Gómez", "ACTIVO");

    String respuesta = service.crearReserva(request("Lucía Gómez", "Sala A", "27/04/2026"));

    assertThat(respuesta).startsWith("RES-").contains("¡Reserva confirmada!");
    String codigo = respuesta.split("\\|")[0].trim();
    assertThat(reservaRepository.findById(codigo)).isPresent();
    assertThat(reservaRepository.findById(codigo).orElseThrow().getEstadoActual())
        .isEqualTo("confirmada");
  }

  @Test
  void rechazaUnaSalaInexistente() {
    registrarEstudiante("EST-1", "Lucía Gómez", "ACTIVO");

    assertThatThrownBy(
            () -> service.crearReserva(request("Lucía Gómez", "Sala Fantasma", "27/04/2026")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No existe la sala");
  }

  @Test
  void rechazaUnaReservaCuandoLaSalaEstaCompleta() {
    registrarSala("SALA-A", "Sala A", 1);
    registrarEstudiante("EST-1", "Lucía Gómez", "ACTIVO");
    registrarEstudiante("EST-2", "Martín Torres", "ACTIVO");
    service.crearReserva(request("Lucía Gómez", "Sala A", "27/04/2026"));

    assertThatThrownBy(() -> service.crearReserva(request("Martín Torres", "Sala A", "27/04/2026")))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("ya está completa");
  }

  @Test
  void rechazaQueUnEstudianteTengaDosReservasEnElMismoBloque() {
    registrarSala("SALA-A", "Sala A", 6);
    registrarSala("SALA-B", "Sala B", 6);
    registrarEstudiante("EST-1", "Lucía Gómez", "ACTIVO");
    service.crearReserva(request("Lucía Gómez", "Sala A", "27/04/2026"));

    assertThatThrownBy(() -> service.crearReserva(request("Lucía Gómez", "Sala B", "27/04/2026")))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Ya posees una reserva");
  }

  @Test
  void rechazaAUnEstudianteNoHabilitado() {
    registrarSala("SALA-A", "Sala A", 6);
    registrarEstudiante("EST-1", "Lucía Gómez", "SUSPENDIDO");

    assertThatThrownBy(() -> service.crearReserva(request("Lucía Gómez", "Sala A", "27/04/2026")))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("no está habilitado");
  }

  @Test
  void rechazaFechasYHorasConFormatoInvalido() {
    registrarSala("SALA-A", "Sala A", 6);
    registrarEstudiante("EST-1", "Lucía Gómez", "ACTIVO");

    assertThatThrownBy(() -> service.crearReserva(request("Lucía Gómez", "Sala A", "2026-04-27")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Formato de fecha/hora inválido");
  }

  @Test
  void cancelarLiberaElCupoYRegistraElEstado() {
    registrarSala("SALA-A", "Sala A", 1);
    registrarEstudiante("EST-1", "Lucía Gómez", "ACTIVO");
    String codigo =
        service.crearReserva(request("Lucía Gómez", "Sala A", "27/04/2026")).split("\\|")[0].trim();
    assertThat(disponibilidad("Sala A")).isZero();

    String mensaje = service.cancelarReserva(codigo, "EST-1");

    assertThat(mensaje).contains("ha sido cancelada con éxito");
    assertThat(disponibilidad("Sala A")).isEqualTo(1);
    assertThat(reservaRepository.findById(codigo).orElseThrow().getEstadoActual())
        .isEqualTo("cancelada");
  }

  @Test
  void procesarAusenciasSoloMarcaTurnosYaFinalizados() {
    registrarSala("SALA-A", "Sala A", 6);
    registrarEstudiante("EST-1", "Lucía Gómez", "ACTIVO");
    registrarEstudiante("EST-2", "Martín Torres", "ACTIVO");
    String vencida =
        service.crearReserva(request("Lucía Gómez", "Sala A", "27/04/2026")).split("\\|")[0].trim();
    String futura =
        service
            .crearReserva(
                new CrearReservaRequest("Martín Torres", "Sala A", "27/04/2026", "13:00", "14:00"))
            .split("\\|")[0]
            .trim();

    int procesadas = service.procesarAusencias(FECHA, LocalTime.of(12, 0));

    assertThat(procesadas).isEqualTo(1);
    assertThat(reservaRepository.findById(vencida).orElseThrow().getEstadoActual())
        .isEqualTo("ausente");
    assertThat(reservaRepository.findById(futura).orElseThrow().getEstadoActual())
        .isEqualTo("confirmada");
  }

  @Test
  void elHistorialIncluyeLosDatosDelTurnoYDelOperario() {
    registrarSala("SALA-A", "Sala A", 6);
    registrarEstudiante("EST-1", "Lucía Gómez", "ACTIVO");
    String codigo =
        service.crearReserva(request("Lucía Gómez", "Sala A", "27/04/2026")).split("\\|")[0].trim();

    List<HistorialDTO> historial = service.obtenerHistorial(codigo);

    assertThat(historial)
        .singleElement()
        .satisfies(
            h -> {
              assertThat(h.estado()).isEqualTo("confirmada");
              assertThat(h.operario()).isEqualTo("SISTEMA");
              assertThat(h.fechaTurno()).isEqualTo("27/04/2026");
              assertThat(h.horaInicio()).isEqualTo("10:00");
              assertThat(h.horaFin()).isEqualTo("11:00");
              assertThat(h.fechaRegistro()).isNotBlank();
            });
  }

  private CrearReservaRequest request(String estudiante, String sala, String fecha) {
    return new CrearReservaRequest(estudiante, sala, fecha, "10:00", "11:00");
  }

  private void registrarSala(String codigo, String nombre, int capacidad) {
    salaRepository.save(new Sala(codigo, nombre, capacidad));
  }

  private void registrarEstudiante(String legajo, String nombre, String estado) {
    estudianteRepository.save(new Estudiante(legajo, nombre, estado));
  }

  private int disponibilidad(String nombreSala) {
    List<DisponibilidadDTO> disponibles = service.consultarDisponibilidad(FECHA);
    return disponibles.stream()
        .filter(d -> d.nombreSala().equals(nombreSala))
        .mapToInt(DisponibilidadDTO::cuposDisponibles)
        .findFirst()
        .orElseThrow();
  }
}

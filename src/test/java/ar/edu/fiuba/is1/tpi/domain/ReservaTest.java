package ar.edu.fiuba.is1.tpi.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReservaTest {

  private Reserva reserva;
  private Estudiante estudiante;

  @BeforeEach
  void setUp() {
    estudiante = new Estudiante("EST-1042", "Lucía Gómez");
    Sala sala = new Sala("SALA-A", "Sala A", 6);
    Turno turno = new Turno(LocalDate.of(2026, 4, 27), LocalTime.of(10, 0), LocalTime.of(11, 0));
    reserva = new Reserva("RES-0892", estudiante, sala, turno);
  }

  @Test
  void unaReservaNuevaNaceConfirmadaConTrazaDelSistema() {
    assertThat(reserva.getEstadoActual()).isEqualTo("confirmada");
    assertThat(reserva.getHistorial())
        .singleElement()
        .satisfies(
            h -> {
              assertThat(h.getEstado()).isEqualTo("confirmada");
              assertThat(h.getOperario()).isEqualTo("SISTEMA");
            });
  }

  @Test
  void elTitularPuedeCancelarPorNombre() {
    reserva.cancelar("Lucía Gómez");

    assertThat(reserva.getEstadoActual()).isEqualTo("cancelada");
  }

  @Test
  void elTitularPuedeCancelarPorLegajo() {
    reserva.cancelar("EST-1042");

    assertThat(reserva.getEstadoActual()).isEqualTo("cancelada");
  }

  @Test
  void unTerceroNoPuedeCancelarLaReserva() {
    assertThatThrownBy(() -> reserva.cancelar("Martín Torres"))
        .isInstanceOf(SecurityException.class)
        .hasMessageContaining("no te pertenece");
    assertThat(reserva.getEstadoActual()).isEqualTo("confirmada");
  }

  @Test
  void noSePuedeCancelarDosVeces() {
    reserva.cancelar(estudiante.nombre());

    assertThatThrownBy(() -> reserva.cancelar(estudiante.nombre()))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("ya se encuentra cancelada");
  }

  @Test
  void elCheckInRegistraAlOperarioYElEstadoPresente() {
    Personal operario = new Personal("LEG-001", "Ana Pérez");

    reserva.registrarAsistencia(operario);

    assertThat(reserva.getEstadoActual()).isEqualTo("presente");
    assertThat(reserva.getHistorial())
        .last()
        .satisfies(
            h -> {
              assertThat(h.getEstado()).isEqualTo("presente");
              assertThat(h.getOperario()).isEqualTo("Ana Pérez");
            });
  }

  @Test
  void noSePuedeRegistrarAsistenciaDosVeces() {
    reserva.registrarAsistencia(new Personal("LEG-001", "Ana Pérez"));

    assertThatThrownBy(() -> reserva.registrarAsistencia(new Personal("LEG-001", "Ana Pérez")))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("ya tiene asistencia registrada");
  }

  @Test
  void unaReservaCanceladaNoAdmiteCheckIn() {
    reserva.cancelar(estudiante.nombre());

    assertThatThrownBy(() -> reserva.registrarAsistencia(new Personal("LEG-001", "Ana Pérez")))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("no admite check-in");
  }

  @Test
  void soloUnaReservaConfirmadaPuedeMarcarseAusente() {
    reserva.cancelar(estudiante.nombre());
    int historialAntes = reserva.getHistorial().size();

    reserva.marcarAusenciaAutomatica();

    assertThat(reserva.getHistorial()).hasSize(historialAntes);
    assertThat(reserva.getEstadoActual()).isEqualTo("cancelada");
  }

  @Test
  void elHistorialExpuestoNoPuedeModificarseExternamente() {
    List<HistorialEstado> historial = reserva.getHistorial();

    assertThatThrownBy(() -> historial.add(new HistorialEstado("cancelada", "SISTEMA")))
        .isInstanceOf(UnsupportedOperationException.class);
  }
}

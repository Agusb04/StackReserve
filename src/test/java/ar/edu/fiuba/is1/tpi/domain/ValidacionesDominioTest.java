package ar.edu.fiuba.is1.tpi.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class ValidacionesDominioTest {

  @Test
  void unaSalaExigeCodigoNombreYCapacidadValidos() {
    assertThatThrownBy(() -> new Sala(" ", "Sala A", 2))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("código");
    assertThatThrownBy(() -> new Sala("SALA-A", " ", 2))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("nombre");
    assertThatThrownBy(() -> new Sala("SALA-A", "Sala A", 0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("capacidad");
  }

  @Test
  void unaSalaNormalizaEspaciosAlrededorDeSusDatos() {
    Sala sala = new Sala("  SALA-A  ", "  Sala A  ", 3);

    assertThat(sala.getCodigo()).isEqualTo("SALA-A");
    assertThat(sala.getNombre()).isEqualTo("Sala A");
    assertThat(sala.getCapacidadMaxima()).isEqualTo(3);
  }

  @Test
  void unTurnoExigeInicioAnteriorAlFin() {
    LocalDate fecha = LocalDate.of(2026, 4, 27);
    LocalTime hora = LocalTime.of(10, 0);

    assertThatThrownBy(() -> new Turno(fecha, hora, hora))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("anterior");
    assertThatThrownBy(() -> new Turno(fecha, hora, hora.minusMinutes(1)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("anterior");
  }

  @Test
  void elPersonalNormalizaSuNombreYRechazaValoresVacios() {
    Personal personal = new Personal("  LEG-001 ", "  Ana Pérez ");

    assertThat(personal.legajo()).isEqualTo("LEG-001");
    assertThat(personal.nombre()).isEqualTo("Ana Pérez");
    assertThatThrownBy(() -> new Personal(" ", "Ana Pérez"))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> new Personal("LEG-001", " "))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void elEstudianteNormalizaSuEstadoDeHabilitacion() {
    Estudiante estudiante = new Estudiante(" EST-1 ", " Ana ", " activo ");

    assertThat(estudiante.legajo()).isEqualTo("EST-1");
    assertThat(estudiante.nombre()).isEqualTo("Ana");
    assertThat(estudiante.estadoHabilitacion()).isEqualTo("ACTIVO");
  }
}

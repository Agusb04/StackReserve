package ar.edu.fiuba.is1.tpi.controller;

import jakarta.validation.ConstraintViolationException;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
    IllegalArgumentException.class,
    IllegalStateException.class,
    NoSuchElementException.class,
    SecurityException.class
  })
  public ResponseEntity<String> manejarErrorDeNegocio(RuntimeException ex) {
    return error(ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> manejarValidacionDeBody(MethodArgumentNotValidException ex) {
    String mensaje =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining("; "));
    return error(mensaje.isBlank() ? "Payload inválido o incompleto" : mensaje);
  }

  @ExceptionHandler({
    ConstraintViolationException.class,
    HandlerMethodValidationException.class,
    MethodArgumentTypeMismatchException.class,
    MissingServletRequestParameterException.class,
    HttpMessageNotReadableException.class,
    DateTimeParseException.class
  })
  public ResponseEntity<String> manejarRequestInvalido(Exception ex) {
    return error("Solicitud inválida: " + ex.getMessage());
  }

  private ResponseEntity<String> error(String mensaje) {
    String detalle = (mensaje == null || mensaje.isBlank()) ? "Solicitud inválida" : mensaje;
    return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(detalle);
  }
}

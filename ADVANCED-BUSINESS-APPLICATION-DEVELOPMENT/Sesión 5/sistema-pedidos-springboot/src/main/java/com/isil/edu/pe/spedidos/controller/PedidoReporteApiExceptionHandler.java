package com.isil.edu.pe.spedidos.controller;

import com.isil.edu.pe.spedidos.exception.ParametroReporteInvalidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce errores de validación del módulo REST de reportes a respuestas HTTP 400.
 */
@RestControllerAdvice(assignableTypes = PedidoReporteController.class)
public class PedidoReporteApiExceptionHandler {

  @ExceptionHandler(ParametroReporteInvalidoException.class)
  public ResponseEntity<ProblemDetail> manejarParametroInvalido(
      ParametroReporteInvalidoException exception) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        exception.getMessage()
    );
    problem.setTitle("Parámetro de reporte inválido");
    return ResponseEntity.badRequest().body(problem);
  }
}

package com.isil.edu.pe.spedidos.exception;

/**
 * Excepción de negocio para parámetros inválidos del módulo de reportes.
 */
public class ParametroReporteInvalidoException extends RuntimeException {

  public ParametroReporteInvalidoException(String message) {
    super(message);
  }
}

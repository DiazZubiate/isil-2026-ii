package com.isil.edu.pe.spedidos.controller.dto;

import com.isil.edu.pe.spedidos.service.domain.ClienteFrecuente;
import java.math.BigDecimal;

/**
 * DTO JSON para el endpoint de clientes frecuentes.
 */
public record ClienteFrecuenteDto(
    String cliente,
    long totalPedidos,
    long totalUnidades,
    BigDecimal importeAcumulado,
    BigDecimal ticketPromedio
) {

  public static ClienteFrecuenteDto from(ClienteFrecuente clienteFrecuente) {
    return new ClienteFrecuenteDto(
        clienteFrecuente.cliente(),
        clienteFrecuente.totalPedidos(),
        clienteFrecuente.totalUnidades(),
        clienteFrecuente.importeAcumulado(),
        clienteFrecuente.ticketPromedio()
    );
  }
}

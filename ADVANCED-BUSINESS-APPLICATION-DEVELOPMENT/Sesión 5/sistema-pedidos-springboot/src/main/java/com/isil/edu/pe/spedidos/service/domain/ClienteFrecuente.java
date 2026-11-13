package com.isil.edu.pe.spedidos.service.domain;

import java.math.BigDecimal;

/**
 * Modelo de dominio de lectura para clientes frecuentes.
 */
public record ClienteFrecuente(
    String cliente,
    long totalPedidos,
    long totalUnidades,
    BigDecimal importeAcumulado,
    BigDecimal ticketPromedio
) {
}

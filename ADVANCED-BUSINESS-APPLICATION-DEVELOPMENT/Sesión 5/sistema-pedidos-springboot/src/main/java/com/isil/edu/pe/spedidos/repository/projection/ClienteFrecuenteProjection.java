package com.isil.edu.pe.spedidos.repository.projection;

import java.math.BigDecimal;

/**
 * Proyección de solo lectura para el consolidado de pedidos por cliente.
 */
public record ClienteFrecuenteProjection(
    String cliente,
    long totalPedidos,
    long totalUnidades,
    BigDecimal importeAcumulado,
    BigDecimal ticketPromedio
) {
}

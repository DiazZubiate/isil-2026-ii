package com.isil.edu.pe.pedidos.reportes.service;

import com.isil.edu.pe.pedidos.reportes.controller.dto.EstadisticasRevisionDto;
import com.isil.edu.pe.pedidos.reportes.controller.dto.PedidoRevisionDto;
import com.isil.edu.pe.pedidos.reportes.repository.PedidoRevisionReporteJdbcRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PedidoRevisionReporteService {

    private final PedidoRevisionReporteJdbcRepository repository;

    public PedidoRevisionReporteService(PedidoRevisionReporteJdbcRepository repository) {
        this.repository = repository;
    }

    public List<PedidoRevisionDto> obtenerPedidosPorEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("El parámetro 'estado' es obligatorio (PENDIENTE o REVISADO).");
        }
        String normalizado = estado.trim().toUpperCase();
        if ("REVISADO".equals(normalizado)) {
            return repository.listarPorEstadoRevision(true);
        } else if ("PENDIENTE".equals(normalizado)) {
            return repository.listarPorEstadoRevision(false);
        } else {
            throw new IllegalArgumentException("Estado inválido: '" + estado + "'. Valores permitidos: PENDIENTE, REVISADO.");
        }
    }

    public EstadisticasRevisionDto obtenerEstadisticas() {
        long total = repository.contarTotalPedidos();
        long revisados = repository.contarPedidosRevisados();
        long pendientes = total - revisados;
        double porcentaje = 0.0;

        if (total > 0) {
            porcentaje = BigDecimal.valueOf((revisados * 100.0) / total)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }
        return new EstadisticasRevisionDto(total, revisados, pendientes, porcentaje);
    }
}

package com.isil.edu.pe.pedidos.reportes.service;

import com.isil.edu.pe.pedidos.reportes.controller.dto.EstadisticasRevisionDto;
import com.isil.edu.pe.pedidos.reportes.controller.dto.PedidoRevisionDto;
import com.isil.edu.pe.pedidos.reportes.repository.PedidoRevisionReporteJdbcRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoRevisionReporteServiceTest {

    @Test
    void obtenerPedidosPorEstado_estadoRevisado_retornaListaConTrazabilidad() {
        // Arrange
        PedidoRevisionReporteJdbcRepository repo = mock(PedidoRevisionReporteJdbcRepository.class);
        PedidoRevisionDto dto = new PedidoRevisionDto(
                1L, "Cliente Prueba", "Laptop", 2, new BigDecimal("3500.00"),
                true, "Todo conforme", LocalDateTime.now()
        );
        when(repo.listarPorEstadoRevision(true)).thenReturn(List.of(dto));
        PedidoRevisionReporteService service = new PedidoRevisionReporteService(repo);

        // Act
        List<PedidoRevisionDto> resultado = service.obtenerPedidosPorEstado("REVISADO");

        // Assert
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).isRevisado());
        assertEquals("Todo conforme", resultado.get(0).getObservacion());
        verify(repo).listarPorEstadoRevision(true);
    }

    @Test
    void obtenerPedidosPorEstado_parametroInvalido_lanzaIllegalArgumentException() {
        // Arrange
        PedidoRevisionReporteJdbcRepository repo = mock(PedidoRevisionReporteJdbcRepository.class);
        PedidoRevisionReporteService service = new PedidoRevisionReporteService(repo);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.obtenerPedidosPorEstado("CUALQUIER_COSA")
        );
        assertTrue(ex.getMessage().contains("Estado inválido"));
        verifyNoInteractions(repo);
    }

    @Test
    void obtenerEstadisticas_calculaPendientesYPorcentajeCorrectamente() {
        // Arrange
        PedidoRevisionReporteJdbcRepository repo = mock(PedidoRevisionReporteJdbcRepository.class);
        when(repo.contarTotalPedidos()).thenReturn(8L);
        when(repo.contarPedidosRevisados()).thenReturn(2L);
        PedidoRevisionReporteService service = new PedidoRevisionReporteService(repo);

        // Act
        EstadisticasRevisionDto stats = service.obtenerEstadisticas();

        // Assert
        assertEquals(8L, stats.getTotalPedidos());
        assertEquals(2L, stats.getTotalRevisados());
        assertEquals(6L, stats.getTotalPendientes());
        assertEquals(25.0, stats.getPorcentajeRevisado());
    }
}

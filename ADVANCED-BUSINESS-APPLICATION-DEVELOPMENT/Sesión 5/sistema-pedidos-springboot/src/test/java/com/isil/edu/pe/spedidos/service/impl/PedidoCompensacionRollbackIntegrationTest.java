package com.isil.edu.pe.spedidos.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.isil.edu.pe.spedidos.client.DevolucionStockClient;
import com.isil.edu.pe.spedidos.client.InventarioClient;
import com.isil.edu.pe.spedidos.client.dto.DevolucionStockResponse;
import com.isil.edu.pe.spedidos.client.dto.ReservaStockResponse;
import com.isil.edu.pe.spedidos.repository.PedidoRepository;
import com.isil.edu.pe.spedidos.service.PedidoService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Complementa los tests unitarios: usa Service real, JPA real y H2 real.
 * Los servicios REST externos se sustituyen por mocks. NO prueba SQL Server.
 * Importante: no anotar el test con @Transactional, porque la transaccion
 * debe iniciarla y revertirla el proxy real de PedidoService.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
    "spring.datasource.url=jdbc:h2:mem:pa2_rollback_it;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "pa2.compensacion.simular-fallo-local=false",
    "spring.h2.console.enabled=false"
})
class PedidoCompensacionRollbackIntegrationTest {

  @Autowired
  private PedidoService pedidoService;

  @Autowired
  private PedidoRepository pedidoRepository;

  @MockitoBean
  private InventarioClient inventarioClient;

  @MockitoBean
  private DevolucionStockClient devolucionStockClient;

  @MockitoBean
  private FalloLocalRegistroPedidoSimulator simulador;

  @BeforeEach
  void preparar() {
    pedidoRepository.deleteAll();
  }

  @Test
  void reservaConfirmadaFalloLocalCompensacionYRollbackRealEnH2() {
    ReservaStockResponse reserva = new ReservaStockResponse();
    reserva.setCodigo("LAP-001");
    reserva.setNombre("Laptop");
    reserva.setPrecio(new BigDecimal("2500.00"));
    when(inventarioClient.reservarStock("LAP-001", 2)).thenReturn(reserva);
    when(devolucionStockClient.devolverStock("LAP-001", 2))
        .thenReturn(new DevolucionStockResponse("LAP-001", 2, 10));
    doThrow(new IllegalStateException("PA2: fallo local intencional"))
        .when(simulador).verificarAntesDelCommit();

    assertThrows(IllegalStateException.class,
        () -> pedidoService.registrarPedido("Cliente Rollback PA2", "LAP-001", 2));

    // Consulta real posterior a la excepcion: el rollback ya debio ocurrir.
    assertEquals(0L, pedidoRepository.count());
    verify(inventarioClient).reservarStock("LAP-001", 2);
    verify(devolucionStockClient).devolverStock("LAP-001", 2);
  }
}

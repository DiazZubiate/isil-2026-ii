package com.isil.edu.pe.spedidos.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.isil.edu.pe.spedidos.exception.ParametroReporteInvalidoException;
import com.isil.edu.pe.spedidos.repository.PedidoReporteJdbcRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PedidoReporteServiceImplPa3Test {

  @Mock
  private PedidoReporteJdbcRepository repository;

  @InjectMocks
  private PedidoReporteServiceImpl service;

  @Test
  @DisplayName("PA3 service: reject blank customer before repository access")
  void buscarAvanzada_clienteVacio_rechazaSinConsultarRepositorio() {
    // Arrange / Act / Assert
    assertThrows(
        ParametroReporteInvalidoException.class,
        () -> service.buscarPedidosPorClienteYtotalMinimo("  ", BigDecimal.ZERO)
    );
    verify(repository, never()).buscarPedidosPorClienteYTotalMinimo(
        org.mockito.ArgumentMatchers.anyString(),
        org.mockito.ArgumentMatchers.any(BigDecimal.class)
    );
  }

  @Test
  @DisplayName("PA3 service: reject negative total before repository access")
  void buscarAvanzada_totalNegativo_rechazaSinConsultarRepositorio() {
    assertThrows(
        ParametroReporteInvalidoException.class,
        () -> service.buscarPedidosPorClienteYtotalMinimo("Ana", new BigDecimal("-0.01"))
    );
    verify(repository, never()).buscarPedidosPorClienteYTotalMinimo(
        org.mockito.ArgumentMatchers.anyString(),
        org.mockito.ArgumentMatchers.any(BigDecimal.class)
    );
  }

  @Test
  @DisplayName("PA3 service: reject minPedidos lower than one")
  void clientesFrecuentes_minPedidosCero_rechazaSinConsultarRepositorio() {
    assertThrows(
        ParametroReporteInvalidoException.class,
        () -> service.listarClientesFrecuentes(0)
    );
    verify(repository, never()).listarClientesFrecuentes(org.mockito.ArgumentMatchers.anyInt());
  }
}

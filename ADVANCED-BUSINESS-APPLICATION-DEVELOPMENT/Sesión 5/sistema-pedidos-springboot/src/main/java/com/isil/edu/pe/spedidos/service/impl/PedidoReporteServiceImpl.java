package com.isil.edu.pe.spedidos.service.impl;

import com.isil.edu.pe.spedidos.entity.Pedido;
import com.isil.edu.pe.spedidos.exception.ParametroReporteInvalidoException;
import com.isil.edu.pe.spedidos.repository.PedidoReporteJdbcRepository;
import com.isil.edu.pe.spedidos.repository.projection.ResumenPedidosProjection;
import com.isil.edu.pe.spedidos.service.PedidoReporteService;
import com.isil.edu.pe.spedidos.service.domain.ClienteFrecuente;
import com.isil.edu.pe.spedidos.service.domain.ProductoVendido;
import com.isil.edu.pe.spedidos.service.domain.ResumenPedidos;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PedidoReporteServiceImpl implements PedidoReporteService {

  private final PedidoReporteJdbcRepository jdbcRepository;

  public PedidoReporteServiceImpl(PedidoReporteJdbcRepository jdbcRepository) {
    this.jdbcRepository = jdbcRepository;
  }

  @Override
  public ResumenPedidos obtenerResumen() {
    ResumenPedidosProjection resumen = jdbcRepository.obtenerResumen();
    return new ResumenPedidos(
        resumen.totalPedidos(),
        resumen.totalUnidades(),
        resumen.importeAcumulado(),
        resumen.ticketPromedio()
    );
  }

  @Override
  public List<Pedido> buscarPorCliente(String cliente) {
    if (cliente == null || cliente.isBlank()) {
      return List.of();
    }
    return jdbcRepository.buscarPorCliente(cliente);
  }

  @Override
  public List<Pedido> buscarPedidosPorClienteYtotalMinimo(
      String cliente,
      BigDecimal totalMinimo) {
    validarCliente(cliente);
    validarTotalMinimo(totalMinimo);
    return jdbcRepository.buscarPedidosPorClienteYTotalMinimo(cliente, totalMinimo);
  }

  @Override
  public List<ProductoVendido> listarProductosMasVendidos() {
    return jdbcRepository.listarProductosMasVendidos()
        .stream()
        .map(item -> new ProductoVendido(
            item.productoCodigo(),
            item.productoNombre(),
            item.totalPedidos(),
            item.totalUnidades(),
            item.importeAcumulado()
        ))
        .toList();
  }

  @Override
  public List<ClienteFrecuente> listarClientesFrecuentes(int minPedidos) {
    validarMinPedidos(minPedidos);
    return jdbcRepository.listarClientesFrecuentes(minPedidos)
        .stream()
        .map(item -> new ClienteFrecuente(
            item.cliente(),
            item.totalPedidos(),
            item.totalUnidades(),
            item.importeAcumulado(),
            item.ticketPromedio()
        ))
        .toList();
  }

  private void validarCliente(String cliente) {
    if (cliente == null || cliente.isBlank()) {
      throw new ParametroReporteInvalidoException("El cliente no puede estar vacío.");
    }
  }

  private void validarTotalMinimo(BigDecimal totalMinimo) {
    if (totalMinimo == null) {
      throw new ParametroReporteInvalidoException("El total mínimo es obligatorio.");
    }
    if (totalMinimo.compareTo(BigDecimal.ZERO) < 0) {
      throw new ParametroReporteInvalidoException("El total mínimo debe ser mayor o igual que 0.");
    }
  }

  private void validarMinPedidos(int minPedidos) {
    if (minPedidos < 1) {
      throw new ParametroReporteInvalidoException("El mínimo de pedidos debe ser mayor o igual que 1.");
    }
  }
}

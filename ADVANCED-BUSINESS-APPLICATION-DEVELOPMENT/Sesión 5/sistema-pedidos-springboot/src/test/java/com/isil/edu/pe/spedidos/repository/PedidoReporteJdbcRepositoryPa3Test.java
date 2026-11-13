package com.isil.edu.pe.spedidos.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.isil.edu.pe.spedidos.entity.Pedido;
import com.isil.edu.pe.spedidos.repository.projection.ClienteFrecuenteProjection;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PedidoReporteJdbcRepositoryPa3Test {

  @Autowired
  private PedidoReporteJdbcRepository repository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUp() {
    jdbcTemplate.update("DELETE FROM pedido");
    insertarPedido("Ana Torres", "LAP-001", "Laptop", "2500.00", 1, "2500.00");
    insertarPedido("Ana Torres", "MOU-001", "Mouse", "50.00", 2, "100.00");
    insertarPedido("Carlos Perez", "MON-001", "Monitor", "850.00", 1, "850.00");
    insertarPedido("Carlos Perez", "MOU-001", "Mouse", "50.00", 1, "50.00");
    insertarPedido("Carlos Perez", "TEC-001", "Teclado", "120.00", 1, "120.00");
  }

  @Test
  @DisplayName("PA3 repository: filter customer and minimum total with named parameters")
  void buscarPedidosPorClienteYTotalMinimo_filtraAmbosCriterios() {
    // Arrange
    String cliente = "Ana";
    BigDecimal totalMinimo = new BigDecimal("500.00");

    // Act
    List<Pedido> resultado = repository.buscarPedidosPorClienteYTotalMinimo(cliente, totalMinimo);

    // Assert
    assertEquals(1, resultado.size());
    assertEquals("Ana Torres", resultado.getFirst().getCliente());
    assertEquals(0, new BigDecimal("2500.00").compareTo(resultado.getFirst().getTotal()));
  }

  @Test
  @DisplayName("PA3 repository: group customers and apply HAVING minPedidos")
  void listarClientesFrecuentes_consolidaYFiltraConHaving() {
    // Arrange
    int minPedidos = 3;

    // Act
    List<ClienteFrecuenteProjection> resultado = repository.listarClientesFrecuentes(minPedidos);

    // Assert
    assertEquals(1, resultado.size());
    ClienteFrecuenteProjection carlos = resultado.getFirst();
    assertEquals("Carlos Perez", carlos.cliente());
    assertEquals(3L, carlos.totalPedidos());
    assertEquals(3L, carlos.totalUnidades());
    assertEquals(0, new BigDecimal("1020.00").compareTo(carlos.importeAcumulado()));
    assertTrue(new BigDecimal("340.00").compareTo(carlos.ticketPromedio()) == 0);
  }

  private void insertarPedido(
      String cliente,
      String productoCodigo,
      String productoNombre,
      String precioUnitario,
      int cantidad,
      String total) {
    jdbcTemplate.update(
        """
        INSERT INTO pedido (
          cliente, producto_codigo, producto_nombre,
          precio_unitario, cantidad, total, fecha
        ) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
        """,
        cliente,
        productoCodigo,
        productoNombre,
        new BigDecimal(precioUnitario),
        cantidad,
        new BigDecimal(total)
    );
  }
}

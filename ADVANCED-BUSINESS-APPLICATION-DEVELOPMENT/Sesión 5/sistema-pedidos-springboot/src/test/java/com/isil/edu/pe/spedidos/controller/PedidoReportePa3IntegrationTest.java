package com.isil.edu.pe.spedidos.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PedidoReportePa3IntegrationTest {

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(springSecurity())
        .build();

    jdbcTemplate.update("DELETE FROM pedido");
    insertarPedido("Ana Torres", "LAP-001", "Laptop", "2500.00", 1, "2500.00");
    insertarPedido("Ana Torres", "MOU-001", "Mouse", "50.00", 2, "100.00");
    insertarPedido("Carlos Perez", "MON-001", "Monitor", "850.00", 1, "850.00");
  }

  @Test
  @DisplayName("PA3 API: CONSULTA obtains filtered advanced search as JSON")
  void busquedaAvanzada_consultaAutenticado_retornaJsonEsperado() throws Exception {
    mockMvc.perform(get("/api/v1/pedidos/busqueda-avanzada")
            .param("cliente", "Ana")
            .param("totalMinimo", "500")
            .with(user("consulta").roles("CONSULTA")))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].cliente").value("Ana Torres"))
        .andExpect(jsonPath("$[0].total").value(2500.00));
  }

  @Test
  @DisplayName("PA3 API: invalid minPedidos is translated to HTTP 400")
  void clientesFrecuentes_minPedidosInvalido_retornaBadRequest() throws Exception {
    mockMvc.perform(get("/api/v1/pedidos/clientes-frecuentes")
            .param("minPedidos", "0")
            .with(user("consulta").roles("CONSULTA")))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.detail").value("El mínimo de pedidos debe ser mayor o igual que 1."));
  }

  @Test
  @DisplayName("PA3 MVC security: CONSULTA can access report JSP")
  void reporteWeb_consultaAutenticado_retornaVistaYmodelo() throws Exception {
    mockMvc.perform(get("/reportes/pedidos")
            .param("minPedidos", "2")
            .with(user("consulta").roles("CONSULTA")))
        .andExpect(status().isOk())
        .andExpect(view().name("reportes-pedidos.jsp"))
        .andExpect(model().attributeExists("resumen"))
        .andExpect(model().attributeExists("clientesFrecuentes"));
  }

  @Test
  @DisplayName("PA3 MVC security: anonymous user is redirected to authentication")
  void reporteWeb_anonimo_esRedirigidoAAutenticacion() throws Exception {
    mockMvc.perform(get("/reportes/pedidos"))
        .andExpect(status().is3xxRedirection());
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

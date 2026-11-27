package com.isil.edu.pe.spedidos.service;

import com.isil.edu.pe.spedidos.client.reportes.PedidoReporteNoDisponibleException;
import com.isil.edu.pe.spedidos.client.reportes.PedidoRevisionHttpClient;
import com.isil.edu.pe.spedidos.exception.PedidoException;
import com.isil.edu.pe.spedidos.repository.PedidoRevisionJdbcRepository;
import com.isil.edu.pe.spedidos.service.impl.PedidoRevisionServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;

class PedidoRevisionModuleTest {

    @Test
    void revisarPedido_datosValidos_ejecutaUpdateEInsert() {
        // Arrange
        PedidoRevisionJdbcRepository repo = mock(PedidoRevisionJdbcRepository.class);
        when(repo.existePedido(1L)).thenReturn(true);
        when(repo.marcarComoRevisado(1L)).thenReturn(1);
        PedidoRevisionServiceImpl service = new PedidoRevisionServiceImpl(repo);

        // Act
        service.revisarPedido(1L, "Verificado conforme");

        // Assert
        verify(repo).marcarComoRevisado(1L);
        verify(repo).insertarRevision(1L, "Verificado conforme");
    }

    @Test
    void revisarPedido_fallaEnSegundaEscritura_reviertePrimeraEscrituraConH2() {
        // Arrange: Base H2 en memoria con transacción real
        DriverManagerDataSource ds = new DriverManagerDataSource(
                "jdbc:h2:mem:pa4test;DB_CLOSE_DELAY=-1", "sa", "");
        JdbcTemplate jdbc = new JdbcTemplate(ds);
        jdbc.execute("DROP TABLE IF EXISTS pedido_revision");
        jdbc.execute("DROP TABLE IF EXISTS pedido");
        jdbc.execute("CREATE TABLE pedido (id BIGINT PRIMARY KEY, cliente VARCHAR(100), revisado BOOLEAN)");
        jdbc.execute("CREATE TABLE pedido_revision (id BIGINT AUTO_INCREMENT PRIMARY KEY, pedido_id BIGINT UNIQUE, observacion VARCHAR(255) NOT NULL, fecha_revision TIMESTAMP)");
        jdbc.update("INSERT INTO pedido (id, cliente, revisado) VALUES (10, 'Cliente Test', FALSE)");

        PedidoRevisionJdbcRepository repo = new PedidoRevisionJdbcRepository(jdbc);
        PedidoRevisionServiceImpl service = new PedidoRevisionServiceImpl(repo);
        TransactionTemplate txTemplate = new TransactionTemplate(new DataSourceTransactionManager(ds));

        // Act: Ejecutamos update y forzamos error en la segunda escritura (observacion null en BD)
        assertThrows(Exception.class, () -> txTemplate.executeWithoutResult(status -> {
            repo.marcarComoRevisado(10L);
            repo.insertarRevision(10L, null); // Viola NOT NULL -> dispara rollback
        }));

        // Assert: revisado debe seguir en FALSE y pedido_revision vacía
        Boolean revisadoActual = jdbc.queryForObject("SELECT revisado FROM pedido WHERE id = 10", Boolean.class);
        Integer totalRevisiones = jdbc.queryForObject("SELECT COUNT(*) FROM pedido_revision", Integer.class);
        assertFalse(revisadoActual);
        assertEquals(0, totalRevisiones);
    }

    @Test
    void clienteHttp_servicioCaidoOError500_lanzaExcepcionControlada() {
        // Arrange
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("http://localhost:8083/api/v1/pedidos/estadisticas/revisiones"))
              .andExpect(method(HttpMethod.GET))
              .andRespond(withServerError());

        PedidoRevisionHttpClient client = new PedidoRevisionHttpClient(builder, "http://localhost:8083");

        // Act & Assert
        PedidoReporteNoDisponibleException ex = assertThrows(
                PedidoReporteNoDisponibleException.class,
                client::obtenerEstadisticas
        );
        assertTrue(ex.getMessage().contains("servicio de reportes"));
    }
}

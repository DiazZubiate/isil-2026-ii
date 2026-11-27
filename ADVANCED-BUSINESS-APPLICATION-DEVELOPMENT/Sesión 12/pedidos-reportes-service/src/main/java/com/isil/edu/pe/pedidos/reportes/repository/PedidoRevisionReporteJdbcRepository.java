package com.isil.edu.pe.pedidos.reportes.repository;

import com.isil.edu.pe.pedidos.reportes.controller.dto.PedidoRevisionDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class PedidoRevisionReporteJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public PedidoRevisionReporteJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PedidoRevisionDto> listarPorEstadoRevision(boolean revisado) {
        String sql = """
                SELECT p.id, p.cliente, p.producto, p.cantidad, p.total, p.revisado,
                       pr.observacion, pr.fecha_revision
                FROM pedido p
                LEFT JOIN pedido_revision pr ON p.id = pr.pedido_id
                WHERE p.revisado = ?
                ORDER BY p.id DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Timestamp ts = rs.getTimestamp("fecha_revision");
            return new PedidoRevisionDto(
                    rs.getLong("id"),
                    rs.getString("cliente"),
                    rs.getString("producto"),
                    rs.getInt("cantidad"),
                    rs.getBigDecimal("total"),
                    rs.getBoolean("revisado"),
                    rs.getString("observacion"),
                    ts != null ? ts.toLocalDateTime() : null
            );
        }, revisado);
    }

    public long contarTotalPedidos() {
        Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pedido", Long.class);
        return total != null ? total : 0L;
    }

    public long contarPedidosRevisados() {
        Long revisados = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pedido WHERE revisado = TRUE", Long.class);
        return revisados != null ? revisados : 0L;
    }
}

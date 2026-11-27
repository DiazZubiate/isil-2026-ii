package com.isil.edu.pe.spedidos.client.reportes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PedidoRevisionClienteDto {
    private Long id;
    private String cliente;
    private String producto;
    private Integer cantidad;
    private BigDecimal total;
    private boolean revisado;
    private String observacion;
    private LocalDateTime fechaRevision;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public boolean isRevisado() { return revisado; }
    public void setRevisado(boolean revisado) { this.revisado = revisado; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public LocalDateTime getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(LocalDateTime fechaRevision) { this.fechaRevision = fechaRevision; }
}

package com.isil.edu.pe.pedidos.reportes.controller.dto;

public class EstadisticasRevisionDto {
    private long totalPedidos;
    private long totalRevisados;
    private long totalPendientes;
    private double porcentajeRevisado;

    public EstadisticasRevisionDto() {}

    public EstadisticasRevisionDto(long totalPedidos, long totalRevisados, long totalPendientes, double porcentajeRevisado) {
        this.totalPedidos = totalPedidos;
        this.totalRevisados = totalRevisados;
        this.totalPendientes = totalPendientes;
        this.porcentajeRevisado = porcentajeRevisado;
    }

    public long getTotalPedidos() { return totalPedidos; }
    public void setTotalPedidos(long totalPedidos) { this.totalPedidos = totalPedidos; }
    public long getTotalRevisados() { return totalRevisados; }
    public void setTotalRevisados(long totalRevisados) { this.totalRevisados = totalRevisados; }
    public long getTotalPendientes() { return totalPendientes; }
    public void setTotalPendientes(long totalPendientes) { this.totalPendientes = totalPendientes; }
    public double getPorcentajeRevisado() { return porcentajeRevisado; }
    public void setPorcentajeRevisado(double porcentajeRevisado) { this.porcentajeRevisado = porcentajeRevisado; }
}

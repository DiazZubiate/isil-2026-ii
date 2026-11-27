package com.isil.edu.pe.spedidos.client.reportes;

import com.isil.edu.pe.spedidos.client.reportes.dto.EstadisticasRevisionClienteDto;
import com.isil.edu.pe.spedidos.client.reportes.dto.PedidoRevisionClienteDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class PedidoRevisionHttpClient {

    private final RestClient restClient;

    public PedidoRevisionHttpClient(
            RestClient.Builder builder,
            @Value("${reportes.service.base-url:http://localhost:8083}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public List<PedidoRevisionClienteDto> listarPorEstado(String estado) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/pedidos/revisiones")
                            .queryParam("estado", estado)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException ex) {
            throw new PedidoReporteNoDisponibleException(
                    "El servicio de reportes (puerto 8083) no está disponible en este momento.");
        }
    }

    public EstadisticasRevisionClienteDto obtenerEstadisticas() {
        try {
            return restClient.get()
                    .uri("/api/v1/pedidos/estadisticas/revisiones")
                    .retrieve()
                    .body(EstadisticasRevisionClienteDto.class);
        } catch (RestClientException ex) {
            throw new PedidoReporteNoDisponibleException(
                    "No se pudieron obtener las estadísticas porque el servicio de reportes no responde.");
        }
    }
}

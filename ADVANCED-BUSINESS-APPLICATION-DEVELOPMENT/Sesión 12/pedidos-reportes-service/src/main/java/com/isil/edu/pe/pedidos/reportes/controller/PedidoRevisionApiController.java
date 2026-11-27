package com.isil.edu.pe.pedidos.reportes.controller;

import com.isil.edu.pe.pedidos.reportes.controller.dto.EstadisticasRevisionDto;
import com.isil.edu.pe.pedidos.reportes.controller.dto.PedidoRevisionDto;
import com.isil.edu.pe.pedidos.reportes.service.PedidoRevisionReporteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoRevisionApiController {

    private final PedidoRevisionReporteService service;

    public PedidoRevisionApiController(PedidoRevisionReporteService service) {
        this.service = service;
    }

    @GetMapping("/revisiones")
    public ResponseEntity<?> listarPorEstado(@RequestParam(name = "estado", defaultValue = "PENDIENTE") String estado) {
        try {
            List<PedidoRevisionDto> lista = service.obtenerPedidosPorEstado(estado);
            return ResponseEntity.ok(lista);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Parámetro inválido", "detalle", ex.getMessage()));
        }
    }

    @GetMapping("/estadisticas/revisiones")
    public ResponseEntity<EstadisticasRevisionDto> obtenerEstadisticas() {
        return ResponseEntity.ok(service.obtenerEstadisticas());
    }
}

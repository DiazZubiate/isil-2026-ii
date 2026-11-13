package com.isil.edu.pe.spedidos.controller;

import com.isil.edu.pe.spedidos.service.PedidoReporteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador web para la visualización de reportes de pedidos.
 */
@Controller
@RequestMapping("/reportes/pedidos")
public class PedidoReporteWebController {

  private final PedidoReporteService pedidoReporteService;

    public PedidoReporteWebController(PedidoReporteService pedidoReporteService) {
        this.pedidoReporteService = pedidoReporteService;
    }

    @GetMapping
    public String verReporteController(
        @RequestParam(name = "cliente", defaultValue = "") String cliente,
        Model model) {
        model.addAttribute("productos", pedidoReporteService.listarProductosMasVendidos());
        model.addAttribute("pedidos", pedidoReporteService.buscarPorCliente(cliente));
        model.addAttribute("resumen", pedidoReporteService.obtenerResumen());
        model.addAttribute("cliente", cliente);
        return "reportes-pedidos";
    }

}

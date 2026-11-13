package com.isil.edu.pe.spedidos.controller;

import com.isil.edu.pe.spedidos.exception.ParametroReporteInvalidoException;
import com.isil.edu.pe.spedidos.service.PedidoReporteService;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador Spring MVC para la vista JSP del módulo de reportes PA3.
 */
@Controller
@RequestMapping("/reportes")
public class PedidoReporteWebController {

  private final PedidoReporteService reporteService;

  public PedidoReporteWebController(PedidoReporteService reporteService) {
    this.reporteService = reporteService;
  }

  @GetMapping("/pedidos")
  public String mostrarReportes(
      @RequestParam(required = false) String cliente,
      @RequestParam(required = false) BigDecimal totalMinimo,
      @RequestParam(required = false) Integer minPedidos,
      Model model,
      HttpServletResponse response) {

    model.addAttribute("resumen", reporteService.obtenerResumen());
    model.addAttribute("pedidosEncontrados", List.of());
    model.addAttribute("clientesFrecuentes", List.of());
    model.addAttribute("clienteIngresado", cliente == null ? "" : cliente);
    model.addAttribute("totalMinimoIngresado", totalMinimo);
    model.addAttribute("minPedidosIngresado", minPedidos);

    try {
      boolean solicitoBusqueda = cliente != null || totalMinimo != null;
      if (solicitoBusqueda) {
        model.addAttribute(
            "pedidosEncontrados",
            reporteService.buscarPedidosPorClienteYtotalMinimo(cliente, totalMinimo)
        );
      }

      if (minPedidos != null) {
        model.addAttribute(
            "clientesFrecuentes",
            reporteService.listarClientesFrecuentes(minPedidos)
        );
      }
    } catch (ParametroReporteInvalidoException exception) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      model.addAttribute("error", exception.getMessage());
    }

    return "reportes-pedidos";
  }
}

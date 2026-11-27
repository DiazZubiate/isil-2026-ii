package com.isil.edu.pe.spedidos.controller;

import com.isil.edu.pe.spedidos.client.reportes.PedidoReporteNoDisponibleException;
import com.isil.edu.pe.spedidos.client.reportes.PedidoRevisionHttpClient;
import com.isil.edu.pe.spedidos.client.reportes.dto.EstadisticasRevisionClienteDto;
import com.isil.edu.pe.spedidos.client.reportes.dto.PedidoRevisionClienteDto;
import com.isil.edu.pe.spedidos.exception.PedidoException;
import com.isil.edu.pe.spedidos.service.PedidoRevisionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/supervision/pedidos")
public class SupervisionPedidoController {

    private final PedidoRevisionService revisionService;
    private final PedidoRevisionHttpClient revisionHttpClient;

    public SupervisionPedidoController(PedidoRevisionService revisionService,
                                       PedidoRevisionHttpClient revisionHttpClient) {
        this.revisionService = revisionService;
        this.revisionHttpClient = revisionHttpClient;
    }

    @GetMapping
    public String vistaSupervision(
            @RequestParam(name = "estado", defaultValue = "PENDIENTE") String estado,
            Model model) {

        String estadoFiltro = "REVISADO".equalsIgnoreCase(estado) ? "REVISADO" : "PENDIENTE";
        model.addAttribute("estadoActual", estadoFiltro);

        try {
            List<PedidoRevisionClienteDto> pedidos = revisionHttpClient.listarPorEstado(estadoFiltro);
            EstadisticasRevisionClienteDto stats = revisionHttpClient.obtenerEstadisticas();

            model.addAttribute("pedidos", pedidos != null ? pedidos : Collections.emptyList());
            model.addAttribute("estadisticas", stats != null ? stats : new EstadisticasRevisionClienteDto(0, 0, 0, 0.0));
            model.addAttribute("servicioDisponible", true);
        } catch (PedidoReporteNoDisponibleException ex) {
            model.addAttribute("pedidos", Collections.emptyList());
            model.addAttribute("estadisticas", new EstadisticasRevisionClienteDto(0, 0, 0, 0.0));
            model.addAttribute("servicioDisponible", false);
            model.addAttribute("mensajeAdvertencia", ex.getMessage());
        }

        return "supervision-pedidos";
    }

    @PostMapping("/{id}/revisar")
    public String revisarPedido(
            @PathVariable("id") Long id,
            @RequestParam(name = "observacion", required = false) String observacion,
            RedirectAttributes redirectAttributes) {
        try {
            revisionService.revisarPedido(id, observacion);
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Pedido #" + id + " marcado como REVISADO correctamente.");
        } catch (PedidoException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Ocurrió un error controlado al procesar la revisión del pedido #" + id + ".");
        }
        return "redirect:/supervision/pedidos";
    }
}

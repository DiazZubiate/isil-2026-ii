package com.isil.edu.pe.spedidos.service.impl;

import com.isil.edu.pe.spedidos.exception.PedidoException;
import com.isil.edu.pe.spedidos.repository.PedidoRevisionJdbcRepository;
import com.isil.edu.pe.spedidos.service.PedidoRevisionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoRevisionServiceImpl implements PedidoRevisionService {

    private final PedidoRevisionJdbcRepository revisionJdbcRepository;

    public PedidoRevisionServiceImpl(PedidoRevisionJdbcRepository revisionJdbcRepository) {
        this.revisionJdbcRepository = revisionJdbcRepository;
    }

    @Override
    @Transactional
    public void revisarPedido(Long pedidoId, String observacion) {
        if (pedidoId == null || pedidoId <= 0) {
            throw new PedidoException("El identificador del pedido debe ser mayor a 0.");
        }
        if (observacion == null || observacion.trim().isEmpty()) {
            throw new PedidoException("La observación de revisión es obligatoria.");
        }
        if (!revisionJdbcRepository.existePedido(pedidoId)) {
            throw new PedidoException("El pedido con ID " + pedidoId + " no existe.");
        }

        // Primera escritura: actualizar estado revisado en pedido
        int filasActualizadas = revisionJdbcRepository.marcarComoRevisado(pedidoId);
        if (filasActualizadas == 0) {
            throw new PedidoException("El pedido #" + pedidoId + " ya fue revisado anteriormente.");
        }

        // Segunda escritura: registrar trazabilidad en pedido_revision (si falla, revierte el UPDATE)
        revisionJdbcRepository.insertarRevision(pedidoId, observacion.trim());
    }
}

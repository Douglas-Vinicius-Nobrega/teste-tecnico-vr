package com.vrsoftware.controller;

import com.vrsoftware.model.Pedido;
import com.vrsoftware.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestBody Pedido pedido) {
        return pedidoService.criarPedido(pedido);
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<String> consultarStatus(@PathVariable UUID id) {
        return pedidoService.getStatus(id);
    }
}

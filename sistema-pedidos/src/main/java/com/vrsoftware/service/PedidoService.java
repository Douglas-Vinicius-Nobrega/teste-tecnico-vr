package com.vrsoftware.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrsoftware.model.Pedido;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PedidoService {

    private RabbitTemplate rabbitTemplate;
    private ObjectMapper objectMapper;
    private Map<UUID, String> statusMap = new ConcurrentHashMap<>();
    private String queueName = "pedidos.entrada.douglas";

    public PedidoService(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<?> criarPedido(Pedido pedido) {
        if (pedido.getQuantidade() <= 0 || pedido.getProduto() == null || pedido.getProduto().isEmpty()) {
            return ResponseEntity.badRequest().body("Quantidade deve ser > 0 e produto não pode ser vazio.");
        }

        try {
            String pedidoJson = objectMapper.writeValueAsString(pedido);
            rabbitTemplate.convertAndSend(queueName, pedidoJson); // fila
            statusMap.put(pedido.getId(), "AGUARDANDO PROCESSO");
            System.out.println(statusMap);

            return ResponseEntity.accepted().body(Map.of("id", pedido.getId()));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(500).body("Erro ao serializar pedido.");
        }
    }

    public ResponseEntity<String> getStatus(UUID id) {
        String status = statusMap.get(id);
        if (status != null) {
            return ResponseEntity.ok(status);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public void atualizarStatus(UUID id, String status) {
        statusMap.put(id, status);
    }
}

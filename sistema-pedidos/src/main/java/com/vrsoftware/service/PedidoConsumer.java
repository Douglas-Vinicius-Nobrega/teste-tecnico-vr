package com.vrsoftware.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrsoftware.model.Pedido;
import com.vrsoftware.model.StatusPedido;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class PedidoConsumer {

    private final PedidoService pedidoService;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    public PedidoConsumer(PedidoService pedidoService, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.pedidoService = pedidoService;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "pedidos.entrada.douglas")
    public void consumir(String mensagem) throws Exception {
        Pedido pedido = objectMapper.readValue(mensagem, Pedido.class);
        pedidoService.atualizarStatus(pedido.getId(), "PROCESSANDO");

        System.out.println("[INÍCIO] Processando pedido " + pedido.getId());

        Thread.sleep(1000 + random.nextInt(2000));

        if (random.nextDouble() < 0.2) {
            throw new RuntimeException("Erro no processamento do pedido " + pedido.getId());
        }

        StatusPedido status = new StatusPedido();
        status.setIdPedido(pedido.getId());
        status.setStatus("SUCESSO");
        status.setDataProcessamento(LocalDateTime.now());

        rabbitTemplate.convertAndSend("pedidos.status.sucesso.douglas", objectMapper.writeValueAsString(status));
        pedidoService.atualizarStatus(pedido.getId(), "SUCESSO");
        System.out.println("[FIM] Pedido " + pedido.getId() + " processado com sucesso");
    }

    @RabbitListener(queues = "pedidos.entrada.douglas.dlq")
    public void processarDLQ(String mensagem) throws Exception {
        Pedido pedido = objectMapper.readValue(mensagem, Pedido.class);
        StatusPedido status = new StatusPedido();
        status.setIdPedido(pedido.getId());
        status.setStatus("FALHA");
        status.setMensagemErro("Erro no processamento anterior (DLQ)");
        status.setDataProcessamento(LocalDateTime.now());

        rabbitTemplate.convertAndSend("pedidos.status.falha.douglas", objectMapper.writeValueAsString(status));
        pedidoService.atualizarStatus(pedido.getId(), "FALHA");
        System.out.println("[DLQ] Pedido " + pedido.getId() + " movido para falha");
    }

}

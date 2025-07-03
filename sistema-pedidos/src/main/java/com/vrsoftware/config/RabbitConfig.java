package com.vrsoftware.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    private static final String QUEUE_NAME = "pedidos.entrada.douglas"; // Fila principal de entrada de pedidos
    private static final String DLQ_NAME = "pedidos.entrada.douglas.dlq"; // Fila de Dead Letter (mensagens que falharam).

    @Bean
    public Queue pedidosEntradaQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ_NAME)
                .build();
    }

    @Bean
    public Queue pedidosDlqQueue() {
        return QueueBuilder.durable(DLQ_NAME).build();
    }

    @Bean
    public Queue sucessoQueue() {
        // Fila para status de sucesso.
        return QueueBuilder.durable("pedidos.status.sucesso.douglas").build();
    }

    @Bean
    public Queue falhaQueue() {
        // Fila para status de falha.
        return QueueBuilder.durable("pedidos.status.falha.douglas").build();
    }

}

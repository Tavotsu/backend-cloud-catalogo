package com.pedidos360.catalogo.messaging.consumer;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// Deja en el log los mensajes que llegan a la DLQ
@Component
public class CatalogoDlqConsumer {

    private static final Logger log = LoggerFactory.getLogger(CatalogoDlqConsumer.class);

    @RabbitListener(queues = "${app.rabbitmq.queues.stock-dlq}", containerFactory = "dlqListenerContainerFactory")
    public void auditarDlq(Message message, Channel channel) throws IOException {
        log.error("[DLQ AUDITORÍA] Mensaje no procesado en {} | payload={} | x-death={}",
                message.getMessageProperties().getConsumerQueue(),
                new String(message.getBody(), StandardCharsets.UTF_8),
                message.getMessageProperties().getHeaders().get("x-death"));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}

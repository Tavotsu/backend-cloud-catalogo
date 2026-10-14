package com.pedidos360.catalogo.messaging.consumer;

import com.pedidos360.catalogo.dto.OrdenCreadaEventDto;
import com.pedidos360.catalogo.exception.StockException;
import com.pedidos360.catalogo.service.StockService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.stereotype.Component;

import java.io.IOException;

// Descuenta stock por cada orden. Error de negocio -> DLQ, error de BD -> un reintento
@Component
public class StockConsumer {

    private static final Logger log = LoggerFactory.getLogger(StockConsumer.class);

    private final StockService stockService;

    public StockConsumer(StockService stockService) {
        this.stockService = stockService;
    }

    @RabbitListener(queues = "${app.rabbitmq.queues.stock}")
    public void consumirOrdenCreada(OrdenCreadaEventDto evento, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        boolean reentregado = Boolean.TRUE.equals(message.getMessageProperties().getRedelivered());
        log.info("[RabbitMQ Consumer] Orden #{} recibida para descontar stock (tag={}, redelivered={})",
                evento.ordenId(), tag, reentregado);

        try {
            stockService.descontarStock(evento);
            channel.basicAck(tag, false);
            log.info("[RabbitMQ ACK] Stock actualizado para la orden #{}", evento.ordenId());

        } catch (StockException ex) {
            log.error("[RabbitMQ NACK -> DLQ] No se puede descontar stock de la orden #{}: {}", evento.ordenId(), ex.getMessage());
            channel.basicNack(tag, false, false);

        } catch (TransientDataAccessException ex) {
            if (!reentregado) {
                log.warn("[RabbitMQ RETRY] Falla temporal de BD, se reencola una vez (tag={})", tag);
                channel.basicNack(tag, false, true);
            } else {
                log.error("[RabbitMQ NACK -> DLQ] Reintento agotado por falla de BD (tag={})", tag);
                channel.basicNack(tag, false, false);
            }

        } catch (Exception ex) {
            log.error("[RabbitMQ NACK -> DLQ] Error inesperado en la orden #{}: {}", evento.ordenId(), ex.getMessage(), ex);
            channel.basicNack(tag, false, false);
        }
    }
}

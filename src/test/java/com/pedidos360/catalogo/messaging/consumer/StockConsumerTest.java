package com.pedidos360.catalogo.messaging.consumer;

import com.pedidos360.catalogo.dto.OrdenCreadaEventDto;
import com.pedidos360.catalogo.exception.StockException;
import com.pedidos360.catalogo.service.StockService;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.dao.QueryTimeoutException;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

// Verifica la decisión de confirmación: ACK, NACK a la DLQ o un reintento
@ExtendWith(MockitoExtension.class)
class StockConsumerTest {

    private static final long TAG = 7L;

    @Mock
    private StockService stockService;

    @Mock
    private Channel channel;

    @InjectMocks
    private StockConsumer consumer;

    private final OrdenCreadaEventDto evento = new OrdenCreadaEventDto(1L, "cliente@pedidos360.cl",
            List.of(new OrdenCreadaEventDto.Item(2L, 1)));

    private static Message mensaje(boolean reentregado) {
        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(TAG);
        props.setRedelivered(reentregado);
        return new Message(new byte[0], props);
    }

    @Test
    void ordenCorrectaHaceAck() throws Exception {
        consumer.consumirOrdenCreada(evento, mensaje(false), channel);

        verify(channel).basicAck(TAG, false);
        verify(channel, never()).basicNack(TAG, false, false);
    }

    @Test
    void errorDeNegocioVaALaDlqSinReintento() throws Exception {
        doThrow(new StockException("Stock insuficiente")).when(stockService).descontarStock(evento);

        consumer.consumirOrdenCreada(evento, mensaje(false), channel);

        verify(channel).basicNack(TAG, false, false);
        verify(channel, never()).basicAck(TAG, false);
    }

    @Test
    void falloTemporalDeBdSeReintentaUnaVez() throws Exception {
        doThrow(new QueryTimeoutException("BD lenta")).when(stockService).descontarStock(evento);

        consumer.consumirOrdenCreada(evento, mensaje(false), channel);

        verify(channel).basicNack(TAG, false, true);
    }

    @Test
    void falloTemporalYaReintentadoVaALaDlq() throws Exception {
        doThrow(new QueryTimeoutException("BD lenta")).when(stockService).descontarStock(evento);

        consumer.consumirOrdenCreada(evento, mensaje(true), channel);

        verify(channel).basicNack(TAG, false, false);
    }
}

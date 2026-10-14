package com.pedidos360.catalogo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.boot.amqp.autoconfigure.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// RabbitMQ del catálogo: cola de stock y su DLQ
@Configuration
@EnableConfigurationProperties(RabbitMQProperties.class)
public class RabbitMQConfig {

    private final RabbitMQProperties props;

    public RabbitMQConfig(RabbitMQProperties props) {
        this.props = props;
    }

    @Bean
    public TopicExchange ordenesTopicExchange() {
        return ExchangeBuilder.topicExchange(props.exchanges().ordenesTopic()).durable(true).build();
    }

    @Bean
    public DirectExchange catalogoDeadLetterExchange() {
        return ExchangeBuilder.directExchange(props.exchanges().dlx()).durable(true).build();
    }

    @Bean
    public Queue stockQueue() {
        return QueueBuilder.durable(props.queues().stock())
                .withArgument("x-dead-letter-exchange", props.exchanges().dlx())
                .withArgument("x-dead-letter-routing-key", props.routingKeys().stockDlq())
                .build();
    }

    @Bean
    public Queue stockDlq() {
        return QueueBuilder.durable(props.queues().stockDlq()).build();
    }

    @Bean
    public Binding bindingStock() {
        return BindingBuilder.bind(stockQueue()).to(ordenesTopicExchange()).with(props.routingKeys().ordenCreada());
    }

    @Bean
    public Binding bindingStockDlq() {
        return BindingBuilder.bind(stockDlq()).to(catalogoDeadLetterExchange()).with(props.routingKeys().stockDlq());
    }

    // Las DLQ se leen como texto para poder registrar también mensajes con JSON inválido
    @Bean
    public SimpleRabbitListenerContainerFactory dlqListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(new SimpleMessageConverter());
        return factory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}

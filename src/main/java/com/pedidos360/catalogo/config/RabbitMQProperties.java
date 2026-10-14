package com.pedidos360.catalogo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Nombres de colas y exchanges definidos en application.yml
@ConfigurationProperties(prefix = "app.rabbitmq")
public record RabbitMQProperties(Exchanges exchanges, Queues queues, RoutingKeys routingKeys) {

    public record Exchanges(String ordenesTopic, String dlx) {}

    public record Queues(String stock, String stockDlq) {}

    public record RoutingKeys(String ordenCreada, String stockDlq) {}
}

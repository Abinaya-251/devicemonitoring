package com.example.devicemanagement.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SerializerMessageConverter;
import org.springframework.amqp.support.converter.AllowedListDeserializingMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- Exchange Names ---
    public static final String DEVICE_EXCHANGE = "device.events";
    public static final String TELEMETRY_EXCHANGE = "telemetry.exchange";
    public static final String ALERT_EXCHANGE = "alert.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    // --- Queue Names ---
    public static final String DEVICE_EVENTS_QUEUE = "device.events.queue";
    public static final String TELEMETRY_INGESTION_QUEUE = "telemetry.ingestion.queue";
    public static final String ALERT_PROCESSING_QUEUE = "alert.processing.queue";
    public static final String NOTIFICATION_QUEUE = "notification.send.queue";

    // --- Routing Keys ---
    public static final String DEVICE_ROUTING_KEY = "device.#";
    public static final String TELEMETRY_ROUTING_KEY = "telemetry.ingest";
    public static final String ALERT_ROUTING_KEY = "alert.#";

    // ========== EXCHANGES ==========

    @Bean
    public TopicExchange deviceExchange() {
        return new TopicExchange(DEVICE_EXCHANGE);
    }

    @Bean
    public DirectExchange telemetryExchange() {
        return new DirectExchange(TELEMETRY_EXCHANGE);
    }

    @Bean
    public TopicExchange alertExchange() {
        return new TopicExchange(ALERT_EXCHANGE);
    }

    @Bean
    public FanoutExchange notificationExchange() {
        return new FanoutExchange(NOTIFICATION_EXCHANGE);
    }

    // ========== QUEUES ==========

    @Bean
    public Queue deviceEventsQueue() {
        return QueueBuilder.durable(DEVICE_EVENTS_QUEUE).build();
    }

    @Bean
    public Queue telemetryIngestionQueue() {
        return QueueBuilder.durable(TELEMETRY_INGESTION_QUEUE).build();
    }

    @Bean
    public Queue alertProcessingQueue() {
        return QueueBuilder.durable(ALERT_PROCESSING_QUEUE).build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    // ========== BINDINGS ==========

    @Bean
    public Binding deviceBinding(Queue deviceEventsQueue, TopicExchange deviceExchange) {
        return BindingBuilder.bind(deviceEventsQueue).to(deviceExchange).with(DEVICE_ROUTING_KEY);
    }

    @Bean
    public Binding telemetryBinding(Queue telemetryIngestionQueue, DirectExchange telemetryExchange) {
        return BindingBuilder.bind(telemetryIngestionQueue).to(telemetryExchange).with(TELEMETRY_ROUTING_KEY);
    }

    @Bean
    public Binding alertBinding(Queue alertProcessingQueue, TopicExchange alertExchange) {
        return BindingBuilder.bind(alertProcessingQueue).to(alertExchange).with(ALERT_ROUTING_KEY);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, FanoutExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange);
    }

    // ========== MESSAGE CONVERTER ==========

    @Bean
    public MessageConverter jsonMessageConverter() {
        SerializerMessageConverter converter = new SerializerMessageConverter();
        converter.setAllowedListPatterns(java.util.List.of(
                "com.example.devicemanagement.messaging.event.*",
                "java.time.*",
                "java.util.*"
        ));
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}

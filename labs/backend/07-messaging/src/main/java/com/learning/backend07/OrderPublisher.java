package com.learning.backend07;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes messages to the JMS queue.
 *
 * This class simulates how you would send messages via:
 * - JmsTemplate (JMS / ActiveMQ / Artemis)
 * - KafkaTemplate (Apache Kafka)
 * - RabbitTemplate (RabbitMQ)
 *
 * The conceptual approach is similar across brokers.
 */
@Component
public class OrderPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderPublisher.class);
    private final JmsTemplate jmsTemplate;

    public OrderPublisher(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Sends an order message to the configured JMS queue.
     */
    public void sendOrder(OrderMessage order) {
        log.info("Publishing order: {}", order);
        jmsTemplate.convertAndSend(MessagingConfig.ORDER_QUEUE, order);
        log.info("Order published successfully");
    }

    /**
     * Simulates sending to a Kafka topic (conceptual — would use KafkaTemplate).
     */
    public void sendToKafka(String topic, OrderMessage order) {
        log.info("Simulating Kafka publish to topic '{}': {}", topic, order);
        // In production: kafkaTemplate.send(topic, order);
    }

    /**
     * Simulates sending to a RabbitMQ exchange (conceptual — would use RabbitTemplate).
     */
    public void sendToRabbitMQ(String exchange, String routingKey, OrderMessage order) {
        log.info("Simulating RabbitMQ send to exchange '{}' routing '{}': {}",
            exchange, routingKey, order);
        // In production: rabbitTemplate.convertAndSend(exchange, routingKey, order);
    }
}

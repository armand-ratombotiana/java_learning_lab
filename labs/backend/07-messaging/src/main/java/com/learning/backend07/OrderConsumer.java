package com.learning.backend07;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Consumes messages from the JMS queue.
 *
 * @JmsListener marks a method as a listener for JMS messages.
 * The destination attribute refers to the queue or topic name.
 * The containerFactory attribute references the
 * DefaultJmsListenerContainerFactory bean defined in MessagingConfig.
 */
@Component
public class OrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    /**
     * Listens for OrderMessage objects on the configured queue.
     * Container factory provides concurrent consumption (3-10 threads).
     */
    @JmsListener(destination = MessagingConfig.ORDER_QUEUE,
                 containerFactory = "jmsListenerContainerFactory")
    public void receiveOrder(OrderMessage order) {
        log.info("Received order from queue: {}", order);

        // Process the order
        System.out.println("Processing order " + order.getOrderId()
            + " for product: " + order.getProduct()
            + " | Quantity: " + order.getQuantity()
            + " | Total: $" + order.getTotalPrice());

        log.info("Order {} processed successfully", order.getOrderId());
    }

    /**
     * Simulated Kafka listener (conceptual).
     * In production: @KafkaListener(topics = "orders")
     */
    public void receiveFromKafka(String message) {
        log.info("Simulated Kafka consumption: {}", message);
    }

    /**
     * Simulated RabbitMQ listener (conceptual).
     * In production: @RabbitListener(queues = "order.queue")
     */
    public void receiveFromRabbitMQ(String message) {
        log.info("Simulated RabbitMQ consumption: {}", message);
    }
}

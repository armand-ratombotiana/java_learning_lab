# Messaging

Asynchronous messaging with JMS, RabbitMQ, and Kafka in Spring.

## Topics
- JMS with ActiveMQ/Artemis
- RabbitMQ with Spring AMQP
- Apache Kafka with Spring Kafka
- @JmsListener, @RabbitListener, @KafkaListener
- Message converters
- Dead letter queues
- Retry and error handling
- Pub/sub and point-to-point patterns

## Example
```java
@Component
public class OrderMessageHandler {
    @RabbitListener(queues = "order.queue")
    public void handleOrder(OrderEvent event) {
        log.info("Received order: {}", event.orderId());
        orderService.process(event);
    }
}
```

# How Event-Driven Architecture Works

## Basic Event Flow
```
Producer -> (Event) -> Message Broker -> (Event) -> Consumer
```

## Spring Cloud Stream Example
```java
// Producer
@Component
public class OrderEventProducer {
    @Autowired
    private StreamBridge streamBridge;

    public void sendOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
            order.getId(), order.getCustomerId(), order.getTotal()
        );
        streamBridge.send("order-events-out-0", event);
    }
}

// Consumer
@Component
public class OrderEventConsumer {

    @Bean
    public Consumer<OrderCreatedEvent> orderCreated() {
        return event -> {
            log.info("Processing order created: {}", event);
            inventoryService.reserveInventory(event);
            notificationService.sendConfirmation(event);
        };
    }
}
```

## Configuration
```yaml
spring:
  cloud:
    stream:
      bindings:
        order-events-out-0:
          destination: order-events
          content-type: application/json
        order-created-in-0:
          destination: order-events
          group: order-processing-group

spring:
  kafka:
    bootstrap-servers: localhost:9092
```

## Event Processing Guarantees
- **At-most-once** - Fast but may lose events
- **At-least-once** - No loss but may duplicate
- **Exactly-once** - Hardest to achieve, supported by Kafka

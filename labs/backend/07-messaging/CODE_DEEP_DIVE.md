# Code Deep Dive: Messaging

## Kafka Integration
```java
@Configuration
public class KafkaConfig {
    @Bean
    public ProducerFactory<String, OrderEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, OrderEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

@Service
public class OrderEventPublisher {
    private final KafkaTemplate<String, OrderEvent> kafka;

    public void publishOrderCreated(Order order) {
        OrderEvent event = new OrderEvent(order.getId(), order.getTotal());
        kafka.send("order-events", event).whenComplete((result, ex) -> {
            if (ex != null) log.error("Failed to send", ex);
        });
    }
}

@Component
public class OrderEventConsumer {
    @KafkaListener(topics = "order-events", groupId = "order-service")
    public void onOrderEvent(OrderEvent event, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Received event for order: {}", event.orderId());
        orderService.processOrderEvent(event);
    }
}
```

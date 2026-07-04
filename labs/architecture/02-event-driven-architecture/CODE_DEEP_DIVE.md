# Code Deep Dive: Event-Driven Architecture

## Complete Event Service with Spring Cloud Stream

### Event Model
```java
@Value
@Builder
public class OrderEvent {
    String eventId;
    String orderId;
    String eventType;
    String customerId;
    BigDecimal amount;
    Instant timestamp;
    Map<String, Object> metadata;

    public static OrderEvent from(Order order, String type) {
        return OrderEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .orderId(order.getId().toString())
            .eventType(type)
            .customerId(order.getCustomerId())
            .amount(order.getTotalAmount())
            .timestamp(Instant.now())
            .metadata(order.toMetadata())
            .build();
    }
}
```

### Event Publisher
```java
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final StreamBridge streamBridge;

    public void publishOrderPlaced(Order order) {
        OrderEvent event = OrderEvent.from(order, "ORDER_PLACED");
        log.info("Publishing event: {}", event.getEventId());
        boolean sent = streamBridge.send("order-events-out-0", event);
        if (!sent) {
            throw new EventPublishException("Failed to publish event");
        }
    }
}
```

### Event Consumer with Error Handling
```java
@Component
@Slf4j
public class OrderEventHandler {

    private final InventoryService inventoryService;
    private final NotificationService notificationService;

    @Bean
    public Consumer<Message<OrderEvent>> orderPlaced() {
        return message -> {
            OrderEvent event = message.getPayload();
            log.info("Processing order placed: {}", event.getOrderId());

            try {
                inventoryService.reserveStock(event.getOrderId());
                notificationService.sendOrderConfirmation(event);
            } catch (Exception e) {
                log.error("Failed to process event: {}", event.getEventId(), e);
                throw new EventProcessingException("Processing failed", e);
            }
        };
    }
}
```

### Kafka Configuration
```java
@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, OrderEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, OrderEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

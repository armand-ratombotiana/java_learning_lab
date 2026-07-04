# Messaging - REFACTORING

## From Synchronous HTTP to Async Messaging

### Before: Direct HTTP call
```java
@Service
public class OrderService {
    private final PaymentClient paymentClient;

    public Order placeOrder(Order order) {
        order = orderRepository.save(order);
        PaymentResult result = paymentClient.processPayment(order);  // blocking
        if (result.isSuccess()) {
            order.setStatus(OrderStatus.CONFIRMED);
        }
        return orderRepository.save(order);
    }
}
```

### After: Async event-driven
```java
@Service
public class OrderService {
    private final KafkaTemplate<String, Order> kafka;

    @Transactional
    public Order placeOrder(Order order) {
        order = orderRepository.save(order);
        kafka.send("payment-requests", order.getId(), order);  // non-blocking
        return order;
    }
}

@Component
public class PaymentResponseConsumer {
    @KafkaListener(topics = "payment-responses")
    public void onPaymentResponse(PaymentResponse response) {
        orderService.updatePaymentStatus(response.getOrderId(), response.getStatus());
    }
}
```

## From JMS to Kafka

### Before: JMS Queue
```java
@JmsListener(destination = "orders.queue")
public void processOrder(Order order) {
    // JMS processing
}
```

### After: Kafka Topic
```java
@KafkaListener(topics = "orders", groupId = "order-processors")
public void processOrder(Order order) {
    // Kafka processing — better scalability, replay, retention
}
```

## From Single Topic to Domain Events

### Before: Single topic for everything
```java
kafkaTemplate.send("events", event);  // All event types mixed together
```

### After: Domain-specific topics
```java
// Separate topics for different concerns
if (event instanceof OrderCreatedEvent) {
    kafkaTemplate.send("order-events", event.getAggregateId(), event);
} else if (event instanceof PaymentProcessedEvent) {
    kafkaTemplate.send("payment-events", event.getAggregateId(), event);
} else if (event instanceof InventoryUpdatedEvent) {
    kafkaTemplate.send("inventory-events", event.getAggregateId(), event);
}
```

## Adding Schema Registry

### Before: JSON with no schema
```java
// No schema validation — breaking changes go undetected
```

### After: Avro with Schema Registry
```java
@Bean
public KafkaTemplate<String, OrderAvro> kafkaTemplate() {
    // Avro serializer with schema registry
    // Schema evolution: backward/forward compatible
    Map<String, Object> props = new HashMap<>();
    props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://schema-registry:8081");
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
}
```

## Performance Impact

| Refactoring | Before | After | Benefit |
|------------|--------|-------|---------|
| Sync → Async HTTP | 500ms response | 50ms response | 10x faster |
| JMS → Kafka | 10K msg/s | 100K msg/s | 10x throughput |
| Single topic → Domain topics | Coupled consumers | Independent scaling | 5x easier maintenance |
| No schema → Schema registry | Runtime failures | Compile-time safety | Zero breaking changes |

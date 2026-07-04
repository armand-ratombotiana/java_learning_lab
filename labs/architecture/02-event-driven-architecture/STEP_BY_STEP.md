# Step-by-Step Event-Driven Architecture

## Step 1: Set Up Kafka
```bash
docker-compose up -d zookeeper kafka
```

## Step 2: Create Event Model
```java
public record PaymentEvent(
    String eventId, String paymentId, String orderId,
    BigDecimal amount, String status, Instant timestamp
) {}
```

## Step 3: Configure Spring Cloud Stream
```yaml
spring:
  cloud:
    stream:
      function:
        definition: processPayment;sendNotification
      bindings:
        processPayment-in-0:
          destination: payment-events
        processPayment-out-0:
          destination: notification-events
        sendNotification-in-0:
          destination: notification-events
```

## Step 4: Implement Producer
```java
@Component
public class PaymentEventPublisher {
    @Autowired
    private StreamBridge streamBridge;

    public void publishPaymentCompleted(Payment payment) {
        PaymentEvent event = PaymentEvent.from(payment, "COMPLETED");
        streamBridge.send("payment-events-out-0", event);
    }
}
```

## Step 5: Implement Consumer
```java
@Bean
public Consumer<PaymentEvent> processPayment() {
    return event -> {
        log.info("Processing payment: {}", event);
        paymentService.process(event);
    };
}
```

## Step 6: Add Error Handling
```java
@Bean
public Consumer<PaymentEvent> processPayment() {
    return event -> {
        try {
            paymentService.process(event);
        } catch (Exception e) {
            // Send to dead letter queue
            deadLetterQueue.send(event, e);
        }
    };
}
```

## Step 7: Monitor Events
```bash
# Consume from the beginning to verify
kafka-console-consumer.bat --bootstrap-server localhost:9092 \
  --topic payment-events --from-beginning
```

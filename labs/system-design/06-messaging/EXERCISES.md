# Messaging Patterns - EXERCISES

## Exercise 1: Implement Message Producer
Create a Kafka producer with:
- Serialization
- Error handling
- Retry logic

## Exercise 2: Implement Message Consumer
Create a Kafka consumer with:
- Manual offset commit
- Error handling
- Dead letter queue

## Exercise 3: Implement Pub/Sub
Create a pub/sub system with:
- Topic subscription
- Message filtering
- Multiple consumers

---

## Solutions

### Exercise 1: Kafka Producer

```java
@Service
public class OrderProducer {
    private final KafkaTemplate<String, OrderEvent> template;
    
    public void send(OrderEvent event) {
        ListenableFuture<SendResult<String, OrderEvent>> future = 
            template.send("orders", event.getOrderId(), event);
        
        future.addCallback(
            result -> logger.info("Sent: {}", event),
            ex -> logger.error("Failed", ex)
        );
    }
}
```

### Exercise 2: Kafka Consumer

```java
@Service
public class OrderConsumer {
    @KafkaListener(topics = "orders", groupId = "order-service")
    public void consume(OrderEvent event) {
        try {
            processOrder(event);
        } catch (Exception e) {
            deadLetterQueue.send(event, e);
        }
    }
}
```
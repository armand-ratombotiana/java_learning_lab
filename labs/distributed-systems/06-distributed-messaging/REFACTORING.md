# Refactoring for Messaging

## Sync to Async Migration

### Before (Synchronous):
```java
public class OrderService {
    private final EmailService email;
    private final InventoryService inventory;
    
    public void placeOrder(Order order) {
        inventory.reserve(order.items); // Blocking
        email.sendConfirmation(order);  // Blocking
    }
}
```

### After (Event-Driven):
```java
public class OrderService {
    private final KafkaProducer<String, String> producer;
    
    public void placeOrder(Order order) {
        producer.send(new ProducerRecord<>("orders", order.id(), 
            serialize(order)));
        // Returns immediately
    }
}

// Separate services subscribe to "orders" topic
public class InventoryHandler {
    @KafkaListener(topics = "orders")
    public void handleOrder(Order order) {
        inventory.reserve(order.items);
    }
}
```

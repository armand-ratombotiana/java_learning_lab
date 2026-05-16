# Messaging - MINI PROJECT

Build event-driven order processing:
- Kafka producer/consumer
- Event publishing
- Error handling with DLQ

```java
@Service
public class OrderEventService {
    public void processOrder(Order order) {
        OrderEvent event = new OrderEvent(order);
        kafkaTemplate.send("orders", order.getId(), event);
    }
}
```
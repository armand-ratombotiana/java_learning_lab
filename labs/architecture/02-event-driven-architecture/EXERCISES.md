# Event-Driven Architecture Exercises

## Beginner Exercises

### Exercise 1: Simple Event Publisher
Create a Spring Boot app that publishes an event to Kafka when a REST endpoint is called.

### Exercise 2: Event Consumer
Create a consumer that logs all events from a topic with their metadata.

## Intermediate Exercises

### Exercise 3: Multi-Consumer System
Build:
- Order Service publishing OrderPlaced events
- Inventory Service consuming to reserve stock
- Notification Service consuming to send email
- Analytics Service consuming to track metrics

### Exercise 4: Dead Letter Queue
Implement DLQ handling for failed events with retry logic.

### Exercise 5: Event Versioning
Create two versions of an event schema and implement upcasting from v1 to v2.

## Advanced Exercises

### Exercise 6: Stream Processing
Use Kafka Streams to create a real-time dashboard for events:
- Count events per type per minute
- Calculate running totals per customer
- Detect anomaly patterns

### Exercise 7: Exactly-Once Semantics
Implement exactly-once processing using Kafka transactions:
```java
@Transactional
public void processOrder(OrderEvent event) {
    kafkaTemplate.send("processed-events", event);
    orderService.updateStatus(event);
}
```

### Exercise 8: Multi-Datacenter Event Replication
Set up Kafka MirrorMaker for cross-datacenter event replication and failover.

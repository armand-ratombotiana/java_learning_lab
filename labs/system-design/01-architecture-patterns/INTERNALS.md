# Architecture Patterns - INTERNALS

## Layered Architecture Internals

### Dependency Injection Chain
Spring Boot resolves the layer chain:
```java
@Configuration
public class AppConfig {
    @Bean
    public ProductRepository repository() { return new InMemoryProductRepository(); }

    @Bean
    public ProductService service(ProductRepository repo) {
        return new ProductServiceImpl(repo);
    }

    @Bean
    public ProductController controller(ProductService svc) {
        return new ProductController(svc);
    }
}
```

### Layer Skipping Anti-Pattern
Controllers should never directly call repositories. This breaks encapsulation and bypasses business logic.

## Microservices Internals

### Service Registry Heartbeat
Each service sends heartbeats to the registry (Eureka/Consul). If heartbeat fails, the instance is deregistered.

### Circuit Breaker States
```
CLOSED → (failures > threshold) → OPEN → (timeout) → HALF_OPEN → (success) → CLOSED
                                                                    → (failure) → OPEN
```

### Configuration Management
Spring Cloud Config Server serves properties from Git. Clients refresh at runtime via `/actuator/refresh`.

## Event-Driven Internals

### Kafka Partitioning
```
Topic: orders
Partition 0: [event1, event3, event5]
Partition 1: [event2, event4, event6]
```
Keys determine partition assignment. Same key = same partition = ordering guarantee.

### Consumer Group Rebalancing
When a consumer joins or leaves, partitions are reassigned. This can cause processing pauses.

## CQRS Internals

### Event Sourcing Reconstruction
```java
public Order reconstructOrder(String orderId) {
    List<Event> events = eventStore.getEvents(orderId);
    Order order = new Order();
    for (Event e : events) {
        order.apply(e);  // replay each event
    }
    return order;
}
```

### Projection Update
Projections are updated asynchronously. A projection handler listens to events and updates the read model. This introduces eventual consistency.

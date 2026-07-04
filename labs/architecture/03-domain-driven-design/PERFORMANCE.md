# DDD Performance

## Performance Considerations

### Aggregate Loading
```java
// WRONG: Loading entire aggregate graph
Order order = orderRepository.findById(id).get();
// Loads all line items, history, etc.

// CORRECT: Use specific queries for read models
@EntityGraph(attributePaths = {"items"})
Optional<Order> findWithItemsById(OrderId id);
```

### Lazy Loading Tuning
```yaml
spring:
  jpa:
    open-in-view: false  # Disable OSIV for better performance
    properties:
      hibernate:
        batch_fetch_size: 25
        default_batch_fetch_size: 25
```

### Domain Event Publishing
```java
// Batch event publishing
@Service
public class BatchEventPublisher {
    @Scheduled(fixedDelay = 1000)
    public void publishPendingEvents() {
        List<DomainEvent> pending = eventStore.findPendingEvents(100);
        pending.forEach(kafkaTemplate::send);
    }
}
```

## Performance Metrics
- Track aggregate load times
- Monitor transaction duration
- Measure domain event processing latency
- Profile specification query performance

## Optimization Strategies
- Use read models for queries (CQRS)
- Optimize aggregate boundaries
- Batch domain event processing
- Cache reference data

# CQRS Performance

## Performance Optimization

### Read Model Indexing
```java
// Create targeted indexes for query patterns
@Document
@CompoundIndexes({
    @CompoundIndex(name = "status_date", def = "{'status': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "customer_status", def = "{'customerId': 1, 'status': 1}")
})
public class OrderView {
    @Id private String orderId;
    private String customerId;
    private String status;
    private Instant createdAt;
}
```

### Materialized Views
```java
@Component
public class OrderSummaryProjection {
    @EventHandler
    public void on(OrderCreatedEvent event) {
        // Pre-compute aggregates for dashboards
        dailySummaryRepository.incrementCount(
            event.getTimestamp().toLocalDate(),
            1,
            event.getTotal().getAmount()
        );
    }
}
```

### Caching
```java
@Cacheable("orderViews")
public OrderView findOrderView(String orderId) {
    return viewRepository.findById(orderId).orElseThrow();
}
```

## Performance Comparison
| Aspect | CRUD | CQRS |
|--------|------|------|
| Read latency | 50ms | 5ms (optimized) |
| Write throughput | 1000/s | 5000/s |
| Query complexity | ORM limited | Full NoSQL power |
| Scalability | Coupled | Independent |

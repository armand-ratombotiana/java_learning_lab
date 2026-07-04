# CQRS Exercises

## Beginner Exercises

### Exercise 1: Basic Command/Query Separation
Create an account management system with:
- CreateAccountCommand, DepositCommand, WithdrawCommand
- GetAccountQuery, GetTransactionsQuery
- Simple in-memory write/read models

### Exercise 2: Read Model Projection
Build a projection that updates a denormalized OrderSummary each time an order event occurs.

## Intermediate Exercises

### Exercise 3: Axon CQRS
Implement full Axon CQRS for a banking system:
- AccountAggregate with command handlers
- AccountProjection with event handlers
- Query handlers for account views

### Exercise 4: Multiple Read Models
Create two different read models from the same events:
- OrderDetailView (full detail)
- OrderSummaryView (dashboard)
- CustomerOrderHistoryView (customer portal)

### Exercise 5: Event Replay
Implement event replay to rebuild read models from scratch:
```java
@Component
public class RebuildService {
    public void rebuildProjection() {
        eventStore.readEvents(aggregateType)
            .forEach(event -> projection.handle(event));
    }
}
```

## Advanced Exercises

### Exercise 6: Multi-Service CQRS
Implement CQRS across microservices:
- Order Service (write)
- Order Query Service (read)
- Event synchronization via Kafka

### Exercise 7: CQRS Performance Benchmark
Benchmark CQRS vs traditional CRUD:
- Measure read latency under load
- Measure write throughput
- Compare consistency trade-offs

# Event Sourcing Exercises

## Beginner Exercises

### Exercise 1: Event Definitions
Define events for a bank account: AccountCreated, MoneyDeposited, MoneyWithdrawn, InterestApplied.

### Exercise 2: Event Replay
Create a simple in-memory event store and implement state rebuild from events.

## Intermediate Exercises

### Exercise 3: Axon Event Sourcing
Implement a complete event-sourced banking system with Axon:
- CreateAccount, Deposit, Withdraw commands
- Account aggregate with @EventSourcingHandler
- AccountProjection for read model

### Exercise 4: Snapshots
Add snapshot support to the banking system:
- Snapshot after every 50 events
- Load from snapshot on aggregate load
- Measure performance improvement

### Exercise 5: Temporal Query
Implement a method to get account balance at any point in time:
```java
public BigDecimal getBalanceAsOf(String accountId, Instant timestamp) {
    // Replay events up to timestamp
}
```

## Advanced Exercises

### Exercise 6: Event Upcasting
Create event schema migration:
- Version 1: AccountCreated with name only
- Version 2: AccountCreated with name + email
- Implement upcaster for v1 events

### Exercise 7: Event Store Sharding
Design a sharded event store across multiple databases:
- Shard by aggregate ID
- Implement cross-shard queries
- Handle shard rebalancing

### Exercise 8: Full Event Sourcing System
Build a complete order management system with:
- Event-sourced aggregates
- Multiple projections
- Snapshots
- Temporal queries
- Event upcasting
- Performance monitoring

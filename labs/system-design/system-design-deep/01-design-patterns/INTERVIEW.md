# Interview Deep-Dive: Design Patterns

## Common Questions

### Q1: When would you choose CQRS over a traditional CRUD approach?
**Answer**: CQRS is appropriate when read and write workloads have significantly different characteristics. Examples: reporting systems with complex queries but simple writes, high-volume write systems where reads need caching and denormalization. CQRS adds complexity so it should not be used for simple CRUD apps.

### Q2: How do you handle sagas that fail halfway?
**Answer**: Implement compensating transactions for each step. The saga orchestrator (or choreography) tracks state and executes compensating actions in reverse order on failure. Use a saga log table for durability and recovery. Idempotency keys prevent duplicate compensations.

### Q3: What happens if the outbox relay crashes before publishing?
**Answer**: The unpublished outbox records remain in the database. When the relay restarts, it polls for unprocessed records and publishes them. This gives at-least-once delivery. Consumers must be idempotent to handle duplicates.

### Q4: How do you prevent the event store from growing infinitely?
**Answer**: Implement snapshotting — periodically save the current aggregate state so replay only needs to process events since the last snapshot. Archive old snapshots to cold storage. Consider event retention policies for non-critical events.

## System Design Whiteboard

**Design an order management system that guarantees no order is lost between creation and processing.**
- Use Transactional Outbox: insert order + outbox record in same DB transaction
- Message relay polls outbox, publishes to Kafka
- Saga orchestrator coordinates payment, inventory, shipping
- CQRS for admin dashboard (denormalized read models)
- Event sourcing for order audit trail

## Key Trade-offs to Discuss
- CQRS: eventual consistency vs strong consistency
- Saga: eventual consistency vs 2PC (XA) guarantees
- Outbox: at-least-once vs exactly-once delivery
- Event Sourcing: storage vs replay cost

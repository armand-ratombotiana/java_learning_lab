# Event Sourcing Interview Questions

## Junior Level

### Q: What is the difference between Event Sourcing and Audit Logging?
**A:** Audit logging is a side effect (current state + separate log). Event Sourcing stores events as the source of truth; current state is derived from events.

### Q: How does replay work in Event Sourcing?
**A:** All stored events for an aggregate are loaded from the event store in order and applied to a new aggregate instance to reconstruct its state.

## Mid Level

### Q: How do you handle event schema evolution?
**A:** Use upcasting - when reading old events, transform them to the current schema version. Never modify existing events. Each event version is separate class.

### Q: What is the problem with eventual consistency in Event Sourcing?
**A:** Projections may lag behind the event store. The read model shows stale data until projections process pending events. Solutions include synchronous projections for critical paths and monitoring projection lag.

## Senior Level

### Q: Design an event-sourced order management system.
**A:**
Events: OrderCreated, ItemAdded, ItemRemoved, OrderSubmitted, PaymentReceived, OrderShipped
Aggregate: Order with event sourcing handlers
Event Store: PostgreSQL with JSONB
Snapshots: Every 50 events
Projections: OrderView (MongoDB), CustomerHistory (Elasticsearch)
CQRS: Separate write/read models
Compensation: Return events for cancellations

### Q: How would you migrate a CRUD system to Event Sourcing?
**A:**
1. Define events from existing state mutations
2. Create an initial "seed" event from current state
3. Set up dual-write mode (CRUD + events)
4. Build projections from events
5. Compare CRUD and event-sourced states
6. Cut over once verified
7. Use feature flags for gradual rollout

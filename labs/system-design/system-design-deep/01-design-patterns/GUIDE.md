# Implementation Guide: Design Patterns

## 1. CQRS (Command Query Responsibility Segregation)

### Concept
CQRS separates read operations (queries) from write operations (commands). Each uses its own model, enabling independent optimization.

### Implementation Steps
1. Define command objects for mutations (CreateOrderCommand, UpdateInventoryCommand)
2. Define query objects for reads (GetOrderQuery, GetCustomerHistoryQuery)
3. Create separate read/write repositories
4. Synchronize read models via events from the write side
5. Deploy read and write services independently if needed

### Key Considerations
- Eventual consistency between read and write stores
- Command validation before processing
- Optimistic concurrency for write operations
- Read model denormalization for query performance

## 2. Saga Pattern

### Concept
A saga is a sequence of local transactions where each step publishes an event triggering the next step. If a step fails, compensating transactions undo previous steps.

### Choreography vs Orchestration

| Aspect | Choreography | Orchestration |
|--------|-------------|---------------|
| Coordination | Decentralized (events) | Centralized (coordinator) |
| Coupling | Loose | Tighter |
| Complexity | Harder to debug | Easier to manage |
| Best for | Simple workflows | Complex, branching workflows |

### Implementation Steps
1. Define saga steps with forward and compensating actions
2. For choreography: each service listens for events and acts
3. For orchestration: a SagaOrchestrator manages step execution
4. Implement idempotency keys for each step
5. Add timeout and retry logic for stuck sagas

## 3. Transactional Outbox Pattern

### Concept
When a service needs to update the database and publish a message, the outbox pattern ensures atomicity: write both the business data and the message to the same database in a single transaction.

### Implementation Steps
1. Create an `outbox` table alongside business tables
2. In the same database transaction, insert business data + outbox record
3. A separate message relay process polls the outbox table
4. Messages are published to the message broker
5. Published records are deleted or marked as sent

### Reliability
- At-least-once delivery: relay may publish the same message twice
- Idempotent consumers handle duplicate messages
- Consider batch polling for throughput

## 4. Event Sourcing

### Concept
Instead of storing current state, store a sequence of state-changing events. Current state is derived by replaying all events.

### Implementation Steps
1. Define event types (OrderPlaced, PaymentReceived, ItemShipped)
2. Each aggregate root appends events to an event store
3. Rebuild aggregate state by replaying events in order
4. Projections build read models from the event stream
5. Snapshots optimize replay for long event streams

### Benefits
- Complete audit trail
- Temporal queries (state at any point in time)
- Debuggable and replayable
- Enables event-driven architectures

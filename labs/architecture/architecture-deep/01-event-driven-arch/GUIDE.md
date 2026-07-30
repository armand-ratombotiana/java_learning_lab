# Event-Driven Architecture — Step-by-Step Guide

## 1. Event Sourcing
- Store state changes as an append-only sequence of events.
- Reconstruct current state by replaying events.

### Implementation Steps
1. Define base `Event` record with `eventId`, `aggregateId`, `timestamp`, `version`.
2. Create `AccountEvent` sealed interface with `AccountCreated`, `MoneyDeposited`, `MoneyWithdrawn`.
3. Implement `EventStore` that persists events and replays them.

## 2. CQRS
- Commands (writes) use `CommandBus`; Queries (reads) use `QueryBus`.
- Commands validate and produce events; queries read from optimized projections.

## 3. Event Bus & Routing
- `EventBus` dispatches events to registered handlers via topic/type routing.
- Supports multiple subscribers per event type.

## 4. Event Versioning
- Use `EventWrapper` with `eventType` and `version` fields.
- Upcasters transform old-version events to current schema.

## 5. Idempotency
- Store processed event IDs in `IdempotencyRegistry`.
- Skip re-processing of already-handled events.

## Build & Run
```bash
cd labs/architecture/architecture-deep/01-event-driven-arch
javac --enable-preview -source 21 -d out src/com/architecture/deep/lab01/*.java
java --enable-preview -cp out com.architecture.deep.lab01.EventDrivenLab
```

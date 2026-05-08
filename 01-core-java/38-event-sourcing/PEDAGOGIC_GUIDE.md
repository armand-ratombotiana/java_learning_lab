# Event Sourcing - Learning Path

## Beginner
- Understand event sourcing vs traditional persistence
- Know event store concept
- Learn aggregate replay
- Understand event schema design

## Intermediate
- Implement event-sourced aggregate in Java
- Add snapshotting for performance
- Create event projections
- Handle optimistic concurrency

## Advanced
- Implement CQRS pattern
- Handle eventual consistency
- Design event schema evolution
- Integrate with frameworks (Axon, Eventuate)

## Key Concepts
- Event Store: append-only log of events
- Aggregate: entity that applies events
- Replay: rebuild state from event history
- Snapshot: serialized state for performance
- Projection: view built from events
- CQRS: separate read and write models

## Benefits
- Complete audit trail
- Time-travel debugging
- Event replay for testing
- Flexible projections

## Assessment
- Design event schema for a domain
- Implement event-sourced aggregate
- Explain snapshot strategy choices
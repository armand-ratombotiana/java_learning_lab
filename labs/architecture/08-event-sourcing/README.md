# Event Sourcing

## Overview
Event Sourcing stores the state of a system as a sequence of events. Instead of storing the current state, every state change is recorded as an event. The current state is derived by replaying events from the event store.

## Topics Covered
- Event store fundamentals
- Event replay and rebuilding state
- Snapshots for performance optimization
- Audit logging and temporal queries
- CQRS integration with event sourcing
- Aggregates and event handlers

## Java/Spring Stack
- Axon Framework for event sourcing
- Spring Data JPA / MongoDB for event store
- Kafka for event streaming
- Axon Event Store / PostgreSQL
- Jackson for event serialization

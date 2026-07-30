# Lab 01: Design Patterns for System Design

## Overview
Master foundational design patterns that solve core distributed system challenges: CQRS, Saga, Outbox, Transactional Outbox, and Event Sourcing.

## Patterns Covered

| Pattern | Problem Solved | When to Use |
|---------|---------------|-------------|
| **CQRS** | Separate read/write models | High read/write disparity, complex queries |
| **Saga** | Distributed transaction coordination | Multi-service transactions without 2PC |
| **Outbox** | Reliable message publication | Ensuring messages are sent exactly once |
| **Transactional Outbox** | DB + message broker consistency | Atomic DB writes and message publishing |
| **Event Sourcing** | State as event sequence | Audit trails, temporal queries, rebuilding state |

## Learning Objectives
- Implement CQRS with separate read/write data stores
- Orchestrate sagas with choreography and orchestration approaches
- Design reliable outbox patterns to prevent message loss
- Build event-sourced aggregates with full audit capabilities
- Understand trade-offs between consistency models

## Prerequisites
- Java 21+
- Basic understanding of distributed systems
- Familiarity with relational databases and messaging queues

## Project Structure
```
src/main/java/com/systemdesign/deep/lab01/
├── cqrs/
│   ├── CommandHandler.java
│   ├── QueryHandler.java
│   ├── WriteRepository.java
│   └── ReadRepository.java
├── saga/
│   ├── SagaOrchestrator.java
│   ├── SagaStep.java
│   └── SagaCoordinator.java
├── outbox/
│   ├── OutboxProcessor.java
│   ├── OutboxRepository.java
│   └── MessageRelay.java
├── eventsourcing/
│   ├── EventStore.java
│   ├── AggregateRoot.java
│   └── EventBus.java
└── DesignPatternsLab.java
```

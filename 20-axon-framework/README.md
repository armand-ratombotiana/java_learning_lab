# 20 - Axon Framework (CQRS/Event Sourcing)

CQRS (Command Query Responsibility Segregation) and Event Sourcing with Axon Framework concepts. Covers command handling, event storage and replay, aggregate state reconstruction from events, query models, and audit logging via event store.

## Prerequisites

- Java 11+
- Maven 3.x

## Key Concepts

- CQRS: separate command (write) and query (read) models
- Commands: `CreateAccountCommand`, `DepositMoneyCommand`, `WithdrawMoneyCommand`
- Events: `AccountCreatedEvent`, `MoneyDepositedEvent`, `MoneyWithdrawnEvent`
- Event Store: append-only event log, audit trail, event replay
- Aggregate: state reconstruction from event history
- Command Bus: command dispatch to handlers
- Query Handler: read-side projection from events

## Module Structure

- `01-cqrs/` - CQRS and event sourcing implementation

## Learning Objectives

- Implement CQRS pattern with separate command and query sides
- Build an event-sourced aggregate with state reconstruction
- Understand event store and event replay mechanics

## Estimated Time

- 2-3 hours

## How to Build

```bash
cd 20-axon-framework
mvn clean package
```

Run the lab:

```bash
cd 01-cqrs
mvn compile exec:java -Dexec.mainClass="com.learning.axon.Lab"
```

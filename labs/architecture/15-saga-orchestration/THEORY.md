# Saga Orchestration Pattern — Theory

## 1. Introduction
The Saga pattern manages distributed transactions across multiple services by breaking them into a sequence of local transactions, each with a compensating action for rollback. Unlike traditional ACID transactions, sagas provide eventual consistency through coordination and compensation.

## 2. Problem Context
Distributed transactions across services face fundamental challenges: no global transaction coordinator in distributed systems, two-phase commit is not suitable for microservices, network partitions make distributed coordination unreliable, services own their data stores independently, and business transactions span service boundaries.

## 3. Saga Coordination Models

### 3.1 Choreography
Each service publishes events after completing its local transaction. Other services listen for relevant events and execute their own transactions. No central coordinator.

### Advantages:
- Decentralized coordination, simple service contracts, no single point of failure, natural fit for event-driven systems

### Disadvantages:
- Transaction flow implicit in event handlers, difficult to modify flow, cyclic dependencies, limited visibility

### 3.2 Orchestration
A central orchestrator service directs the saga flow. It tells each service what to do and tracks overall state.

### Advantages:
- Clear transaction flow, easy to modify steps, centralized state management, explicit error handling

### Disadvantages:
- Single point of failure, coordination bottleneck, increased coupling, orchestrator complexity grows

## 4. Compensation Strategies

### 4.1 Forward Recovery
Continue processing from the point of failure after the cause is resolved. Requires idempotent operations and state persistence.

### 4.2 Backward Recovery (Rollback)
Execute compensating transactions for all completed steps in reverse order. Most common saga failure recovery approach.

### 4.3 Compensation Characteristics
Compensating actions must be idempotent. Compensations may fail and require retry. Temporal coupling means compensations may run long after the original transaction.

## 5. Transactional Boundaries

### 5.1 Local Transactions
Each service executes its own local ACID transaction. Success is published as an event or response. Failure triggers compensation.

### 5.2 Saga Log
Persistent log of saga state containing saga identifier, current step and status, completed step list, and compensation status.

## 6. Axon Saga Framework
Provides @Saga annotation for saga classes, @StartSaga, @EndSaga, @SagaEventHandler annotations, association properties for saga correlation, deadline management for timeout handling, and event sourcing integration.

## 7. Camunda BPM for Sagas
Provides BPMN 2.0 process definitions for saga flow, visual modeling and monitoring, human task integration, compensation handler configuration, and history audit for compliance.

## 8. Best Practices
Design compensating actions as first-class citizens, ensure idempotency, implement monitoring and alerting, use saga logs for audit, handle timeouts for stalled sagas, test compensation paths thoroughly, and document saga boundaries.

# Saga Pattern

## Overview
The Saga pattern manages distributed transactions across services using a sequence of local transactions with compensating actions.

## Key Features
- Choreography-based saga
- Orchestration-based saga
- Compensating transactions
- Distributed transaction management
- Failure handling

## Project Structure
```
67-saga/
  saga-pattern/
    src/main/java/com/learning/saga/SagaLab.java
```

## Running
```bash
cd 67-saga/saga-pattern
mvn compile exec:java
```

## Concepts Covered
- Saga orchestrator
- Compensating transactions
- Choreography vs orchestration
- Failure recovery

## Implementation
- Order Service -> Payment -> Inventory -> Shipping
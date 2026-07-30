# Microservices Patterns — Step-by-Step Guide

## 1. Decomposition
- Identify bounded contexts (Order, Payment, Inventory, Shipping).
- Each context becomes a separate service with its own database.

## 2. Database per Service
- Each service owns its schema; no direct cross-service queries.
- Use API composition or CQRS for cross-service reads.

## 3. API Gateway
- Single entry point routing to internal services.
- Implement request aggregation, auth, rate limiting.

## 4. Circuit Breaker
- Wrap remote calls; trip on failure threshold.
- Half-open state allows recovery probes.

## 5. Saga Pattern
- Choreography: services react to events.
- Orchestration: a coordinator directs steps and compensations.

## 6. Strangler Fig
- Route traffic gradually from monolith to new service.
- Feature toggle until migration is complete, then remove old code.

## Build & Run
```bash
javac --enable-preview -source 21 -d out src/com/architecture/deep/lab02/*.java
java --enable-preview -cp out com.architecture.deep.lab02.MicroservicesLab
```

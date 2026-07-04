# Mental Models for Microservices

## The Cell Analogy
Each microservice is like a cell - self-contained with its own membrane (API boundary), nucleus (business logic), and organelles (internal components). Cells communicate through chemical signals (messages) and can replicate independently.

## The Shipping Container Model
Services are like shipping containers - standardized interfaces, self-contained, stackable, and transportable across different environments (dev, staging, prod).

## The Bounded Context Map
Visualize your system as a map of territories, each with its own language (ubiquitous language) and borders (API contracts). Services communicate across borders via defined protocols.

## The Circuit Breaker Mental Model
Think of electrical circuits - when a circuit (service) fails, the breaker trips to isolate the fault, preventing a cascade failure across the entire system.

## Distributed State Machine
The entire microservices system is a distributed state machine where each service maintains its part of the state, and messages coordinate transitions between states.

```java
// State transition in Order Service
public enum OrderState {
    CREATED, PAYMENT_PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}
```

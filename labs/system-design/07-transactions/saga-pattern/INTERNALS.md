# Saga Implementation Styles

There are two primary ways to coordinate a Saga: Choreography and Orchestration.

## 💃 Choreography (Event-Driven)
In Choreography, there is no central coordinator. Each microservice listens to events from other services and decides what action to take next.

**The Flow**:
1. Order Service saves order and emits `OrderCreatedEvent`.
2. Inventory Service listens, reserves stock, and emits `InventoryReservedEvent`.
3. Payment Service listens, charges card, emits `PaymentProcessedEvent`.

**Pros**:
- Simple for small sagas (2-3 steps).
- No single point of failure.

**Cons**:
- **The Dependency Hairball**: As the saga grows to 5-10 steps, it becomes impossible to understand the flow without tracing events across multiple codebases. 
- Cyclic dependencies can easily occur.
- Difficult to implement integration tests.

## 🎼 Orchestration (Command-Driven)
In Orchestration, a central component (the Orchestrator) tells the participating microservices what local transactions to execute. The Orchestrator manages the state machine of the saga.

**The Flow**:
1. Order Service creates an `OrderSagaOrchestrator`.
2. Orchestrator sends a `ReserveInventoryCommand` to Inventory Service.
3. Inventory Service replies with `InventoryReservedReply`.
4. Orchestrator sends a `ChargeCardCommand` to Payment Service.
5. Payment Service replies with `PaymentFailedReply`.
6. Orchestrator sends a `ReleaseInventoryCommand` (Compensation) to Inventory Service.

**Pros**:
- Centralized logic: The entire business workflow is defined in one place (the Orchestrator).
- No cyclic dependencies (services don't know about each other, they only reply to the Orchestrator).
- Easier to test and monitor.

**Cons**:
- The Orchestrator can become a "God Object" if not designed carefully.
- Requires deploying and managing an orchestration engine (like Temporal, Camunda, or AWS Step Functions).

## 🛡️ Idempotency
Because network calls can fail or timeout, the Orchestrator might retry a command. Every local transaction and compensating transaction **must be idempotent**. 
If the Payment Service receives the same `ChargeCardCommand(orderId=123)` twice, it must recognize it has already charged the card and simply return a success reply, rather than charging the customer twice.
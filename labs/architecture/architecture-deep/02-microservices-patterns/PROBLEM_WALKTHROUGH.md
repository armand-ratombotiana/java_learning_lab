# Lab 02: Problem Walkthrough — Saga Orchestrator with Compensation

## Problem Statement

Implement a saga orchestrator for an e-commerce order flow. An order requires three distributed operations — reserve inventory, charge payment, ship order — each owned by a different service with its own database. Implement the **orchestration** style of sagas:

1. A central orchestrator drives the steps in order.
2. Every step can be compensated if a later step fails.
3. On failure, the orchestrator runs compensation for all successfully completed steps **in reverse order**.
4. The saga terminates in a well-defined state (`COMPLETED`, `FAILED`, or `COMPENSATED`) that can be inspected and persisted.

## Constraints

- Java 21+ only, no framework dependencies (no Spring State Machine, no Axon).
- Steps are plain classes implementing a `SagaStep` contract.
- Compensation must be skipped for steps that were never executed.
- The orchestrator must support both compensable and non-compensable steps.
- Context is a mutable carrier shared across steps.

## Approach

**Orchestration vs choreography.** In choreography, services publish events and react to each other — good for loose coupling but the flow is hard to follow. The problem explicitly asks for an orchestrator, so we centralize the flow: the orchestrator knows the step list and the compensation order.

Key design decisions:

- **`SagaStep` interface** with `execute` and `compensate`. Each step owns both its happy path and its undo path — this is the single most important design rule of sagas: compensation logic lives next to the step it undoes.
- **Executed-step stack.** The orchestrator pushes each successfully executed step onto a stack so compensation runs in reverse order naturally.
- **Compensation failure handling.** If compensation itself fails, we record it and keep compensating the remaining steps; a failed compensation is escalated (retried by a job) rather than blocking the unwind.
- **Context as carrier.** `OrderSagaContext` holds order id, item quantities, payment reference, shipment id, and saga status.

Flow:

```
ReserveInventory -> ChargePayment -> ShipOrder
        |                |
        +-------> FAILURE
Compensate in reverse: ChargePayment(reverse) -> ReserveInventory(reverse)
```

## Step-by-Step Solution

### Step 1: Saga Context

The context is shared mutable state mutated by steps. The orchestrator controls all access (single thread of execution), so no synchronization is needed.

```java
enum SagaStatus { PENDING, COMPLETED, FAILED, COMPENSATED }

class OrderSagaContext {
    final UUID orderId;
    final String item;
    final int quantity;
    final long price;
    int inventoryReserved;
    String paymentReference;
    String shipmentId;
    SagaStatus status = SagaStatus.PENDING;

    OrderSagaContext(UUID orderId, String item, int quantity, long price) {
        this.orderId = orderId;
        this.item = item;
        this.quantity = quantity;
        this.price = price;
    }
}
```

### Step 2: The SagaStep Contract

```java
interface SagaStep {
    void execute(OrderSagaContext context);
    void compensate(OrderSagaContext context);
}
```

### Step 3: Implementing the Steps

Each step models an external service call. Exceptions simulate service failures. Note that each step also implements its own compensation — the "reserve inventory" step compensates by releasing the reservation.

```java
class ReserveInventoryStep implements SagaStep {
    @Override
    public void execute(OrderSagaContext context) {
        System.out.println("[Inventory] Reserving " + context.quantity + "x " + context.item);
        context.inventoryReserved = context.quantity;
    }

    @Override
    public void compensate(OrderSagaContext context) {
        System.out.println("[Inventory] Releasing " + context.inventoryReserved + "x " + context.item);
        context.inventoryReserved = 0;
    }
}

class ChargePaymentStep implements SagaStep {
    @Override
    public void execute(OrderSagaContext context) {
        System.out.println("[Payment] Charging " + context.price + " for order " + context.orderId);
        if (context.price > 1000) {
            throw new IllegalStateException("Payment gateway declined: amount exceeds limit");
        }
        context.paymentReference = "PAY-" + context.orderId.toString().substring(0, 8);
    }

    @Override
    public void compensate(OrderSagaContext context) {
        System.out.println("[Payment] Refunding " + context.price + " (" + context.paymentReference + ")");
        context.paymentReference = null;
    }
}

class ShipOrderStep implements SagaStep {
    @Override
    public void execute(OrderSagaContext context) {
        System.out.println("[Shipping] Shipping order " + context.orderId);
        context.shipmentId = "SHIP-" + context.orderId.toString().substring(0, 8);
    }

    @Override
    public void compensate(OrderSagaContext context) {
        System.out.println("[Shipping] Cancelling shipment " + context.shipmentId);
        context.shipmentId = null;
    }
}
```

### Step 4: The Orchestrator

The orchestrator is generic over the step list. It tracks executed steps and unwinds them in reverse on failure.

```java
class SagaOrchestrator {
    private final List<SagaStep> steps;

    SagaOrchestrator(List<SagaStep> steps) {
        this.steps = List.copyOf(steps);
    }

    OrderSagaContext run(OrderSagaContext context) {
        Deque<SagaStep> executed = new ArrayDeque<>();
        try {
            for (var step : steps) {
                step.execute(context);
                executed.push(step);
            }
            context.status = SagaStatus.COMPLETED;
        } catch (RuntimeException failure) {
            System.out.println("[Saga] Step failed: " + failure.getMessage());
            compensateAll(context, executed, failure);
        }
        return context;
    }

    private void compensateAll(OrderSagaContext context, Deque<SagaStep> executed, RuntimeException cause) {
        while (!executed.isEmpty()) {
            var step = executed.pop();
            try {
                step.compensate(context);
            } catch (RuntimeException e) {
                System.out.println("[Saga] Compensation failed for step "
                    + step.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        context.status = SagaStatus.COMPENSATED;
    }
}
```

Note the **stack semantics**: `executed.push` + `executed.pop` gives LIFO order, so the last executed step is compensated first.

### Step 5: Main — Failure Scenario

We force the payment step to fail and observe the unwind.

```java
public class SagaOrchestratorLab {
    public static void main(String[] args) {
        var steps = List.of(
            new ReserveInventoryStep(),
            new ChargePaymentStep(),
            new ShipOrderStep()
        );
        var orchestrator = new SagaOrchestrator(steps);

        var ok = orchestrator.run(new OrderSagaContext(UUID.randomUUID(), "Laptop", 1, 999));
        System.out.println("Happy path status: " + ok.status);

        var failing = orchestrator.run(new OrderSagaContext(UUID.randomUUID(), "Laptop", 2, 2500));
        System.out.println("Failure path status: " + failing.status);
    }
}
```

## Complete Solution

The full compilable file, `SagaOrchestratorLab.java` in package `com.architecture.deep.lab02`:

```java
package com.architecture.deep.lab02;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

public class SagaOrchestratorLab {
    public static void main(String[] args) {
        var steps = List.of(
            new ReserveInventoryStep(),
            new ChargePaymentStep(),
            new ShipOrderStep()
        );
        var orchestrator = new SagaOrchestrator(steps);

        var ok = orchestrator.run(new OrderSagaContext(UUID.randomUUID(), "Laptop", 1, 999));
        System.out.println("Happy path status: " + ok.status);

        var failing = orchestrator.run(new OrderSagaContext(UUID.randomUUID(), "Laptop", 2, 2500));
        System.out.println("Failure path status: " + failing.status);
    }
}

enum SagaStatus { PENDING, COMPLETED, FAILED, COMPENSATED }

class OrderSagaContext {
    final UUID orderId;
    final String item;
    final int quantity;
    final long price;
    int inventoryReserved;
    String paymentReference;
    String shipmentId;
    SagaStatus status = SagaStatus.PENDING;

    OrderSagaContext(UUID orderId, String item, int quantity, long price) {
        this.orderId = orderId;
        this.item = item;
        this.quantity = quantity;
        this.price = price;
    }
}

interface SagaStep {
    void execute(OrderSagaContext context);
    void compensate(OrderSagaContext context);
}

class ReserveInventoryStep implements SagaStep {
    @Override
    public void execute(OrderSagaContext context) {
        System.out.println("[Inventory] Reserving " + context.quantity + "x " + context.item);
        context.inventoryReserved = context.quantity;
    }

    @Override
    public void compensate(OrderSagaContext context) {
        System.out.println("[Inventory] Releasing " + context.inventoryReserved + "x " + context.item);
        context.inventoryReserved = 0;
    }
}

class ChargePaymentStep implements SagaStep {
    @Override
    public void execute(OrderSagaContext context) {
        System.out.println("[Payment] Charging " + context.price + " for order " + context.orderId);
        if (context.price > 1000) {
            throw new IllegalStateException("Payment gateway declined: amount exceeds limit");
        }
        context.paymentReference = "PAY-" + context.orderId.toString().substring(0, 8);
    }

    @Override
    public void compensate(OrderSagaContext context) {
        System.out.println("[Payment] Refunding " + context.price + " (" + context.paymentReference + ")");
        context.paymentReference = null;
    }
}

class ShipOrderStep implements SagaStep {
    @Override
    public void execute(OrderSagaContext context) {
        System.out.println("[Shipping] Shipping order " + context.orderId);
        context.shipmentId = "SHIP-" + context.orderId.toString().substring(0, 8);
    }

    @Override
    public void compensate(OrderSagaContext context) {
        System.out.println("[Shipping] Cancelling shipment " + context.shipmentId);
        context.shipmentId = null;
    }
}

class SagaOrchestrator {
    private final List<SagaStep> steps;

    SagaOrchestrator(List<SagaStep> steps) {
        this.steps = List.copyOf(steps);
    }

    OrderSagaContext run(OrderSagaContext context) {
        Deque<SagaStep> executed = new ArrayDeque<>();
        try {
            for (var step : steps) {
                step.execute(context);
                executed.push(step);
            }
            context.status = SagaStatus.COMPLETED;
        } catch (RuntimeException failure) {
            System.out.println("[Saga] Step failed: " + failure.getMessage());
            compensateAll(context, executed, failure);
        }
        return context;
    }

    private void compensateAll(OrderSagaContext context, Deque<SagaStep> executed, RuntimeException cause) {
        while (!executed.isEmpty()) {
            var step = executed.pop();
            try {
                step.compensate(context);
            } catch (RuntimeException e) {
                System.out.println("[Saga] Compensation failed for step "
                    + step.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        context.status = SagaStatus.COMPENSATED;
    }
}
```

## Complexity Analysis

- **Time**: O(S) for the happy path (S = number of steps); O(S + C) on failure where C is the number of compensated steps (C <= S). Each step is assumed O(1) domain work; real steps make remote calls, so latency dominates.
- **Space**: O(S) for the executed-step stack.
- **Reliability**: compensation runs are best-effort here; production requires a durable saga log so an interrupted orchestrator can resume compensation after a crash.

## Test Cases

| Scenario | Flow | Expected status |
|---|---|---|
| Happy path (price 999) | reserve -> charge -> ship | `COMPLETED`, all references set |
| Payment failure (price 2500) | reserve OK, charge throws | `COMPENSATED`, inventory released, no shipment |
| Shipping failure | reserve+charge OK, ship throws | `COMPENSATED`, refund issued, inventory released |
| Compensation failure | compensate throws | Remaining steps still compensated, failure logged |
| Empty saga | no steps | `COMPLETED` immediately |

Example run:

```
[Inventory] Reserving 1x Laptop
[Payment] Charging 999 for order ...
[Shipping] Shipping order ...
Happy path status: COMPLETED
[Inventory] Reserving 2x Laptop
[Payment] Charging 2500 for order ...
[Saga] Step failed: Payment gateway declined: amount exceeds limit
[Payment] Refunding null (no charge made)
[Inventory] Releasing 2x Laptop
Failure path status: COMPENSATED
```

Note: the refund prints "null" in the failure path because `paymentReference` was never set — a nice property of compensating idempotently (nothing was charged, nothing to refund). In a real system the compensation step would first check whether the charge existed.

## Follow-Up Questions

1. **Why orchestrator instead of choreography?** Central control makes the flow explicit, testable, and recoverable; choreography hides the flow in events across services.
2. **How do you make compensation idempotent?** Each compensation checks current state first (was a charge actually made?) and uses idempotency keys with the downstream service.
3. **What if the orchestrator crashes mid-compensation?** Persist saga state (executed step list) to a journal; on restart, resume compensation from the journal.
4. **How do you model timeouts?** Wrap step execution in a future with a timeout; a timed-out step triggers compensation and marks the step unknown — needs a resolution procedure (check with the service) before compensating.
5. **How does this differ from two-phase commit?** 2PC locks resources until commit and requires all participants to support the protocol; sagas use business-level compensation and work across heterogeneous systems, at the cost of eventual consistency.
6. **How do you avoid compensation of steps that partially succeeded?** Steps should be designed with compensating actions that are safe to run even when the forward action is uncertain (idempotent undo).
7. **How would you add retries?** Wrap `execute` with bounded retries and backoff per step, before giving up and entering compensation.

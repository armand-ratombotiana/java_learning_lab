# Architecture — Control Flow

## Validator Pattern

Separate validation from business logic: validate inputs at system boundaries, then call business logic without repeated checks.

## Guard Clauses

Use early returns at method start to handle edge cases, flattening the main path:
```java
public void process(Order order) {
    if (order == null) throw new IllegalArgumentException();
    if (!order.isValid()) return;
    if (order.isPaid()) processPayment();
    // main logic
}
```

## Command Pattern

Encapsulate control flow as objects: `Command` interface with `execute()`. Enables queuing, logging, undo.

## State Machine Pattern

Complex state-dependent behavior: define states and transitions explicitly rather than nested if-else. Enums with state transition tables.

## Template Method Pattern

Define algorithm skeleton with overridable steps. Controls the flow, subclasses fill in details.

## Event-Driven Architecture

Replace tight control flow with event emission/handling. Decouples components. Java provides `EventHandler`, Swing/AWT listeners, and reactive streams.

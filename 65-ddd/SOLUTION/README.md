# DDD Solution

## Overview
This module covers Domain Design and Aggregates.

## Key Features

### Entity
- Identity-based equality
- Base entity class

### Aggregate Root
- Entity with events
- Event publishing
- Event collection

### Value Object
- Immutable value
- Value-based equality

### Domain Events
- OrderItemAddedEvent
- OrderConfirmedEvent
- OrderCancelledEvent

### Order Aggregate
- Creating orders
- Adding items
- Confirming orders
- Cancelling orders
- Total calculation

### Repository
- CRUD operations
- In-memory storage
- Find by ID

### Domain Service
- Business logic
- Discount calculation

## Usage

```java
DDDSolution solution = new DDDSolution();

// Create order
Order order = solution.createOrder("order-1");

// Add items
order.addItem("product-1", 2, new Money(50.0));
order.addItem("product-2", 1, new Money(75.0));

// Confirm order
order.confirm();

// Get total
Money total = order.getTotal();

// Get domain events
List<DomainEvent> events = order.getEvents();

// Repository
Repository<Order> repo = solution.createOrderRepository();
repo.save(order);
Optional<Order> found = repo.findById("order-1");
```

## Dependencies
- JUnit 5
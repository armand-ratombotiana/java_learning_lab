# Common Mistakes in CQRS

## 1. CQRS Without Need
```java
// WRONG: Using CQRS for simple CRUD
// A simple TODO list app doesn't need CQRS

// CORRECT: Only use CQRS when you have:
// - Complex domain logic on write side
// - Different read/write performance needs
// - Multiple read representations
```

## 2. Same Database for Both Models
```java
// WRONG: Command and query models in same database
// Defeats the purpose of CQRS separation

// CORRECT: Separate databases or schemas
// Write: PostgreSQL (normalized, ACID)
// Read: MongoDB (denormalized, fast reads)
```

## 3. Exposing Internal Models
```java
// WRONG: Returning domain object from query
public Order getOrder(String id) {
    return orderRepository.findById(id); // Exposes write model!
}

// CORRECT: Return read model
public OrderView getOrder(String id) {
    return orderViewRepository.findById(id);
}
```

## 4. Mixing Commands and Queries
```java
// WRONG: Command that returns data
public OrderResponse placeOrderAndGetDetails(CreateOrderCommand cmd) {
    // Command AND query combined
}

// CORRECT: Separate
public void placeOrder(CreateOrderCommand cmd) { }
public OrderView getOrderDetails(FindOrderQuery query) { }
```

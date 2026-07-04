# Refactoring to Saga Pattern

## Distributed Transaction to Saga Migration

### Step 1: Identify Transaction Boundaries
```java
// BEFORE: Distributed transaction
@Transactional
public void placeOrder(Order order) {
    orderRepository.save(order);
    inventoryRepository.deduct(order.getProductId(), order.getQuantity());
    paymentRepository.charge(order.getCustomerId(), order.getTotal());
}
```

### Step 2: Split into Local Transactions
```java
// Service 1: Order Service
@Transactional
public void createOrder(CreateOrderCommand cmd) {
    orderRepository.save(new Order(cmd));
}

// Service 2: Inventory Service  
@Transactional
public void reserveInventory(ReserveInventoryCommand cmd) {
    inventoryRepository.reserve(cmd.getProductId(), cmd.getQuantity());
}
```

### Step 3: Add Events
```java
// Each service publishes events after local transaction
@Transactional
public void createOrder(CreateOrderCommand cmd) {
    Order order = new Order(cmd);
    orderRepository.save(order);
    eventPublisher.publish(new OrderCreatedEvent(order));
}
```

### Step 4: Implement Saga Coordinator
```java
@Saga
public class OrderSaga {
    // Coordinate with events and compensating actions
}
```

## Choreography to Orchestration Migration
```java
// Step 1: Identify choreography flow
// Step 2: Create orchestrator class
// Step 3: Move coordination logic to orchestrator
// Step 4: Services become passive (only handle commands)
```

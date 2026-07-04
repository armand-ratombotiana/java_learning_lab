# Refactoring to Pipeline Architecture

## Monolithic Processing to Pipeline

### Step 1: Identify Processing Steps
```java
// BEFORE: Monolithic processing method
public OrderResponse processOrder(OrderRequest request) {
    // Validate request
    if (request.getItems().isEmpty()) throw new ValidationException();
    // Enrich with customer data
    Customer customer = customerClient.findById(request.getCustomerId());
    // Check inventory
    for (OrderItem item : request.getItems()) {
        if (!inventoryClient.isAvailable(item.getProductId())) {
            throw new InventoryException();
        }
    }
    // Calculate pricing
    BigDecimal total = calculateTotal(request.getItems());
    // Create order
    Order order = orderRepository.save(new Order(request, customer, total));
    return OrderResponse.from(order);
}
```

### Step 2: Extract Each Step as Stage
```java
// AFTER: Pipeline stages
public class OrderValidationStage implements Stage<OrderRequest> { }
public class CustomerEnrichmentStage implements Stage<OrderRequest> { }
public class InventoryCheckStage implements Stage<OrderRequest> { }
public class PricingStage implements Stage<OrderRequest> { }
public class OrderCreationStage implements Stage<OrderRequest, OrderResponse> { }
```

### Step 3: Compose Pipeline
```java
Pipeline<OrderRequest, OrderResponse> pipeline = new Pipeline<>();
pipeline.addStage(new OrderValidationStage());
pipeline.addStage(new CustomerEnrichmentStage());
pipeline.addStage(new InventoryCheckStage());
pipeline.addStage(new PricingStage());
pipeline.addStage(new OrderCreationStage());
```

### Step 4: Add Monitoring
```java
// Add metrics to each stage
// Add logging stage wrapper
// Add error handling per stage
```

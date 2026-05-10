# Saga Projects - Module 67

This module covers Saga pattern, Choreography, and Orchestration for distributed transactions.

## Mini-Project: Order Saga with Orchestration (2-4 hours)

### Overview
Build a distributed order processing system using Saga orchestration pattern.

### Project Structure
```
saga-demo/
├── src/main/java/com/learning/saga/
│   ├── orchestrator/
│   │   └── OrderSagaOrchestrator.java
│   ├── steps/
│   │   ├── ValidateOrderStep.java
│   │   ├── ReserveInventoryStep.java
│   │   ├── ProcessPaymentStep.java
│   │   └── ShipOrderStep.java
│   └── compensations/
├── pom.xml
└── run.sh
```

### Implementation
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>saga-demo</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
</project>

// SagaOrchestrator.java
package com.learning.saga.orchestrator;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class OrderSagaOrchestrator {
    
    private final Map<String, SagaStep> steps = new LinkedHashMap<>();
    private final SagaState state = new SagaState();
    
    public OrderSagaOrchestrator registerStep(String name, SagaStep step) {
        steps.put(name, step);
        return this;
    }
    
    public SagaResult execute(SagaData initialData) {
        state.setData(initialData);
        List<String> executedSteps = new ArrayList<>();
        
        for (Map.Entry<String, SagaStep> entry : steps.entrySet()) {
            String stepName = entry.getKey();
            SagaStep step = entry.getValue();
            
            try {
                CompletableFuture.runAsync(() -> {
                    state.setCurrentStep(stepName);
                    step.execute(state);
                }).get();
                
                executedSteps.add(stepName);
                
            } catch (Exception e) {
                return compensate(executedSteps, e);
            }
        }
        
        state.setCompleted(true);
        return SagaResult.success(state);
    }
    
    private SagaResult compensate(List<String> executedSteps, Exception error) {
        Collections.reverse(executedSteps);
        
        for (String stepName : executedSteps) {
            SagaStep step = steps.get(stepName);
            try {
                step.compensate(state);
            } catch (Exception e) {
                state.addCompensationError(stepName, e);
            }
        }
        
        return SagaResult.failure(state, error);
    }
}

interface SagaStep {
    void execute(SagaState state);
    void compensate(SagaState state);
}

class SagaState {
    private Object data;
    private String currentStep;
    private Map<String, Object> stepResults = new HashMap<>();
    private Map<String, Exception> compensationErrors = new HashMap<>();
    private boolean completed;
    
    public void setData(Object data) { this.data = data; }
    public Object getData() { return data; }
    public void setCurrentStep(String step) { this.currentStep = step; }
    public String getCurrentStep() { return currentStep; }
    public void addStepResult(String step, Object result) {
        stepResults.put(step, result);
    }
    public Object getStepResult(String step) { return stepResults.get(step); }
    public void addCompensationError(String step, Exception e) {
        compensationErrors.put(step, e);
    }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}

record SagaResult(SagaState state, boolean success, Exception error) {
    public static SagaResult success(SagaState state) {
        return new SagaResult(state, true, null);
    }
    
    public static SagaResult failure(SagaState state, Exception error) {
        return new SagaResult(state, false, error);
    }
}

record SagaData(String orderId, String customerId, List<OrderItem> items) {
    record OrderItem(String productId, int quantity) {}
}
```

---

## Real-World Project: E-Commerce Distributed Saga (8+ hours)

### Overview
Build a comprehensive Saga orchestration system for order processing across multiple microservices.

### Project Structure
```
ecommerce-saga/
├── orchestrator/
│   └── src/main/java/com/learning/saga/
├── services/
│   ├── order-service/
│   ├── payment-service/
│   ├── inventory-service/
│   └── shipping-service/
├── pom.xml
└── docker-compose.yml
```

### Implementation
```java
// OrderSagaOrchestrator.java
package com.learning.saga;

import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
public class OrderSagaOrchestrator {
    
    private final SagaStep validateOrderStep;
    private final SagaStep reserveInventoryStep;
    private final SagaStep processPaymentStep;
    private final SagaStep createShipmentStep;
    
    public OrderSagaOrchestrator(
            ValidateOrderStep validateOrderStep,
            ReserveInventoryStep reserveInventoryStep,
            ProcessPaymentStep processPaymentStep,
            CreateShipmentStep createShipmentStep) {
        
        this.validateOrderStep = validateOrderStep;
        this.reserveInventoryStep = reserveInventoryStep;
        this.processPaymentStep = processPaymentStep;
        this.createShipmentStep = createShipmentStep;
    }
    
    public SagaResult processOrder(OrderSagaData data) {
        SagaState state = new SagaState();
        state.setOrderId(data.getOrderId());
        
        try {
            // Step 1: Validate Order
            validateOrderStep.execute(state);
            
            // Step 2: Reserve Inventory (async)
            CompletableFuture.runAsync(() -> reserveInventoryStep.execute(state)).get();
            
            // Step 3: Process Payment
            processPaymentStep.execute(state);
            
            // Step 4: Create Shipment
            createShipmentStep.execute(state);
            
            state.setStatus("COMPLETED");
            return SagaResult.success(state);
            
        } catch (Exception e) {
            return handleFailure(state, e);
        }
    }
    
    private SagaResult handleFailure(SagaState state, Exception error) {
        List<String> completedSteps = state.getCompletedSteps();
        
        for (int i = completedSteps.size() - 1; i >= 0; i--) {
            String stepName = completedSteps.get(i);
            try {
                SagaStep step = getStep(stepName);
                step.compensate(state);
            } catch (Exception e) {
                state.addError(stepName + "_COMPENSATION", e.getMessage());
            }
        }
        
        state.setStatus("FAILED");
        return SagaResult.failure(state, error);
    }
    
    private SagaStep getStep(String name) {
        return switch (name) {
            case "validate-order" -> validateOrderStep;
            case "reserve-inventory" -> reserveInventoryStep;
            case "process-payment" -> processPaymentStep;
            case "create-shipment" -> createShipmentStep;
            default -> null;
        };
    }
}

// Saga Steps
@Component
public class ValidateOrderStep implements SagaStep {
    
    @Override
    public void execute(SagaState state) {
        OrderSagaData data = (OrderSagaData) state.getData();
        
        if (data.getItems().isEmpty()) {
            throw new SagaException("Order has no items");
        }
        
        state.addStepResult("validate-order", "VALID");
    }
    
    @Override
    public void compensate(SagaState state) {
        // No compensation needed - validation is read-only
    }
}

@Component
public class ReserveInventoryStep implements SagaStep {
    
    private final InventoryService inventoryService;
    
    public ReserveInventoryStep(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    @Override
    public void execute(SagaState state) {
        OrderSagaData data = (OrderSagaData) state.getData();
        
        for (var item : data.getItems()) {
            boolean reserved = inventoryService.reserveInventory(
                item.productId(), item.quantity(), data.getOrderId()
            );
            
            if (!reserved) {
                throw new SagaException("Failed to reserve inventory for: " + item.productId());
            }
        }
        
        state.addStepResult("reserve-inventory", "RESERVED");
    }
    
    @Override
    public void compensate(SagaState state) {
        OrderSagaData data = (OrderSagaData) state.getData();
        
        for (var item : data.getItems()) {
            inventoryService.releaseReservation(
                item.productId(), item.quantity(), data.getOrderId()
            );
        }
    }
}

@Component
public class ProcessPaymentStep implements SagaStep {
    
    private final PaymentService paymentService;
    
    public ProcessPaymentStep(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @Override
    public void execute(SagaState state) {
        OrderSagaData data = (OrderSagaData) state.getData();
        
        String transactionId = paymentService.processPayment(
            data.getCustomerId(), data.getTotalAmount(), data.getOrderId()
        );
        
        state.addStepResult("process-payment", transactionId);
    }
    
    @Override
    public void compensate(SagaState state) {
        Object paymentResult = state.getStepResult("process-payment");
        
        if (paymentResult instanceof String transactionId) {
            paymentService.refund(transactionId);
        }
    }
}

@Component
public class CreateShipmentStep implements SagaStep {
    
    private final ShippingService shippingService;
    
    public CreateShipmentStep(ShippingService shippingService) {
        this.shippingService = shippingService;
    }
    
    @Override
    public void execute(SagaState state) {
        OrderSagaData data = (OrderSagaData) state.getData();
        
        String shipmentId = shippingService.createShipment(
            data.getOrderId(), data.getShippingAddress()
        );
        
        state.addStepResult("create-shipment", shipmentId);
    }
    
    @Override
    public void compensate(SagaState state) {
        Object shipmentResult = state.getStepResult("create-shipment");
        
        if (shipmentResult instanceof String shipmentId) {
            shippingService.cancelShipment(shipmentId);
        }
    }
}

// Services
@Service
class InventoryService {
    public boolean reserveInventory(String productId, int quantity, String orderId) {
        return true;
    }
    
    public void releaseReservation(String productId, int quantity, String orderId) {}
}

@Service
class PaymentService {
    public String processPayment(String customerId, double amount, String orderId) {
        return "TXN-" + System.currentTimeMillis();
    }
    
    public void refund(String transactionId) {}
}

@Service
class ShippingService {
    public String createShipment(String orderId, String address) {
        return "SHIP-" + System.currentTimeMillis();
    }
    
    public void cancelShipment(String shipmentId) {}
}

// Models
class OrderSagaData {
    private String orderId;
    private String customerId;
    private List<OrderItem> items;
    private double totalAmount;
    private String shippingAddress;
    
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public List<OrderItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public String getShippingAddress() { return shippingAddress; }
}

record OrderItem(String productId, int quantity) {}

class SagaState {
    private String orderId;
    private String status;
    private Map<String, Object> data = new HashMap<>();
    private List<String> completedSteps = new ArrayList<>();
    private Map<String, String> errors = new HashMap<>();
    
    public void setData(Object data) { 
        if (data instanceof OrderSagaData) {
            this.data.put("orderData", data);
        }
    }
    
    public Object getData() { return data.get("orderData"); }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getOrderId() { return orderId; }
    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
    public void addStepResult(String step, Object result) {
        data.put(step, result);
        completedSteps.add(step);
    }
    public Object getStepResult(String step) { return data.get(step); }
    public List<String> getCompletedSteps() { return completedSteps; }
    public void addError(String key, String value) { errors.put(key, value); }
}

record SagaResult(SagaState state, boolean success, Exception error) {
    public static SagaResult success(SagaState state) {
        return new SagaResult(state, true, null);
    }
    
    public static SagaResult failure(SagaState state, Exception error) {
        return new SagaResult(state, false, error);
    }
}

class SagaException extends RuntimeException {
    public SagaException(String message) { super(message); }
}
```

### Build and Run
```bash
mvn clean compile
java -cp target/classes com.learning.saga.SagaApplication
```

### Learning Outcomes
- Implement Saga orchestration pattern
- Handle distributed transactions
- Create compensation logic
- Build fault-tolerant systems
- Manage saga state
- Handle failures gracefully
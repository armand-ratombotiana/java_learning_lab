# Architecture Patterns - MINI PROJECT

## Project Overview

**Project Name**: Inventory Management System
**Time Estimate**: 4-6 hours
**Difficulty**: Intermediate

Build a multi-module inventory management system demonstrating all four architecture patterns. You'll implement:
1. A layered monolith for core operations
2. Extract to microservices for scaling
3. Add event-driven communication
4. Implement CQRS for reporting

---

## Project Structure

```
inventory-system/
├── inventory-core/           # Layered architecture
│   ├── src/main/java/
│   │   └── com/inventory/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── model/
│   │       └── exception/
│   └── pom.xml
├── inventory-service/       # Microservice
├── inventory-events/        # Event-driven module
├── inventory-cqrs/          # CQRS implementation
└── pom.xml
```

---

## Step 1: Layered Architecture Implementation

### 1.1 Create the Core Domain Model

Create `inventory-core/src/main/java/com/inventory/model/InventoryItem.java`:

```java
package com.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryItem {
    private String id;
    private String sku;
    private String name;
    private String description;
    private int quantity;
    private BigDecimal unitPrice;
    private String category;
    private int reorderLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InventoryItem(String sku, String name, String description, 
                         int quantity, BigDecimal unitPrice, String category) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.category = category;
        this.reorderLevel = 10;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; this.updatedAt = LocalDateTime.now(); }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public boolean needsReorder() {
        return quantity <= reorderLevel;
    }

    public boolean canFulfill(int requestedQuantity) {
        return quantity >= requestedQuantity;
    }
}
```

### 1.2 Create Repository Layer

Create `inventory-core/src/main/java/com/inventory/repository/InventoryRepository.java`:

```java
package com.inventory.repository;

import com.inventory.model.InventoryItem;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository {
    InventoryItem save(InventoryItem item);
    Optional<InventoryItem> findById(String id);
    Optional<InventoryItem> findBySku(String sku);
    List<InventoryItem> findAll();
    List<InventoryItem> findByCategory(String category);
    List<InventoryItem> findItemsBelowReorderLevel();
    void deleteById(String id);
    boolean existsBySku(String sku);
}
```

Create `inventory-core/src/main/java/com/inventory/repository/InMemoryInventoryRepository.java`:

```java
package com.inventory.repository;

import com.inventory.model.InventoryItem;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryInventoryRepository implements InventoryRepository {
    private final Map<String, InventoryItem> items = new ConcurrentHashMap<>();

    @Override
    public InventoryItem save(InventoryItem item) {
        if (item.getId() == null) {
            item.setId(UUID.randomUUID().toString());
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<InventoryItem> findById(String id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Optional<InventoryItem> findBySku(String sku) {
        return items.values().stream()
            .filter(i -> i.getSku().equals(sku))
            .findFirst();
    }

    @Override
    public List<InventoryItem> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<InventoryItem> findByCategory(String category) {
        return items.values().stream()
            .filter(i -> i.getCategory().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }

    @Override
    public List<InventoryItem> findItemsBelowReorderLevel() {
        return items.values().stream()
            .filter(InventoryItem::needsReorder)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        items.remove(id);
    }

    @Override
    public boolean existsBySku(String sku) {
        return items.values().stream()
            .anyMatch(i -> i.getSku().equals(sku));
    }
}
```

### 1.3 Create Service Layer

Create `inventory-core/src/main/java/com/inventory/service/InventoryService.java`:

```java
package com.inventory.service;

import com.inventory.model.InventoryItem;
import java.util.List;

public interface InventoryService {
    InventoryItem createItem(InventoryItem item);
    InventoryItem getItemById(String id);
    InventoryItem getItemBySku(String sku);
    List<InventoryItem> getAllItems();
    List<InventoryItem> getItemsByCategory(String category);
    List<InventoryItem> getLowStockItems();
    InventoryItem updateQuantity(String id, int quantity);
    void deleteItem(String id);
}
```

Create `inventory-core/src/main/java/com/inventory/service/InventoryServiceImpl.java`:

```java
package com.inventory.service;

import com.inventory.exception.ItemNotFoundException;
import com.inventory.exception.ValidationException;
import com.inventory.model.InventoryItem;
import com.inventory.repository.InventoryRepository;
import java.math.BigDecimal;
import java.util.List;

public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository repository;

    public InventoryServiceImpl(InventoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public InventoryItem createItem(InventoryItem item) {
        validateItem(item);
        
        if (repository.existsBySku(item.getSku())) {
            throw new ValidationException("SKU already exists: " + item.getSku());
        }
        
        return repository.save(item);
    }

    @Override
    public InventoryItem getItemById(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new ItemNotFoundException("Item not found: " + id));
    }

    @Override
    public InventoryItem getItemBySku(String sku) {
        return repository.findBySku(sku)
            .orElseThrow(() -> new ItemNotFoundException("Item not found with SKU: " + sku));
    }

    @Override
    public List<InventoryItem> getAllItems() {
        return repository.findAll();
    }

    @Override
    public List<InventoryItem> getItemsByCategory(String category) {
        return repository.findByCategory(category);
    }

    @Override
    public List<InventoryItem> getLowStockItems() {
        return repository.findItemsBelowReorderLevel();
    }

    @Override
    public InventoryItem updateQuantity(String id, int quantity) {
        InventoryItem item = getItemById(id);
        
        if (quantity < 0) {
            throw new ValidationException("Quantity cannot be negative");
        }
        
        item.setQuantity(quantity);
        return repository.save(item);
    }

    @Override
    public void deleteItem(String id) {
        if (!repository.findById(id).isPresent()) {
            throw new ItemNotFoundException("Item not found: " + id);
        }
        repository.deleteById(id);
    }

    private void validateItem(InventoryItem item) {
        if (item.getSku() == null || item.getSku().trim().isEmpty()) {
            throw new ValidationException("SKU is required");
        }
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }
        if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price must be positive");
        }
        if (item.getQuantity() < 0) {
            throw new ValidationException("Quantity cannot be negative");
        }
    }
}
```

### 1.4 Create Controller Layer

Create `inventory-core/src/main/java/com/inventory/controller/InventoryController.java`:

```java
package com.inventory.controller;

import com.inventory.model.InventoryItem;
import com.inventory.service.InventoryService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryItem> createItem(@RequestBody InventoryItem item) {
        InventoryItem created = inventoryService.createItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getItem(@PathVariable String id) {
        return ResponseEntity.ok(inventoryService.getItemById(id));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<InventoryItem> getItemBySku(@PathVariable String sku) {
        return ResponseEntity.ok(inventoryService.getItemBySku(sku));
    }

    @GetMapping
    public ResponseEntity<List<InventoryItem>> getAllItems() {
        return ResponseEntity.ok(inventoryService.getAllItems());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<InventoryItem>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(inventoryService.getItemsByCategory(category));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryItem>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<InventoryItem> updateQuantity(
            @PathVariable String id,
            @RequestParam int quantity) {
        return ResponseEntity.ok(inventoryService.updateQuantity(id, quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 1.5 Create Exception Handling

Create `inventory-core/src/main/java/com/inventory/exception/ItemNotFoundException.java`:

```java
package com.inventory.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
```

Create `inventory-core/src/main/java/com/inventory/exception/ValidationException.java`:

```java
package com.inventory.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
```

Create `inventory-core/src/main/java/com/inventory/controller/GlobalExceptionHandler.java`:

```java
package com.inventory.controller;

import com.inventory.exception.ItemNotFoundException;
import com.inventory.exception.ValidationException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ItemNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(ValidationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", message);
        return ResponseEntity.status(status).body(error);
    }
}
```

---

## Step 2: Event-Driven Enhancement

### 2.1 Create Event Classes

Create `inventory-events/src/main/java/com/inventory/events/InventoryEvent.java`:

```java
package com.inventory.events;

import java.time.LocalDateTime;

public class InventoryEvent {
    private String eventId;
    private String eventType;
    private String itemId;
    private Object payload;
    private LocalDateTime timestamp;

    public InventoryEvent(String eventType, String itemId, Object payload) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.eventType = eventType;
        this.itemId = itemId;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
    }

    public String getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public String getItemId() { return itemId; }
    public Object getPayload() { return payload; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
```

Create `inventory-events/src/main/java/com/inventory/events/EventPublisher.java`:

```java
package com.inventory.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventPublisher {
    private static EventPublisher instance;
    private final List<Consumer<InventoryEvent>> handlers = new ArrayList<>();

    private EventPublisher() {}

    public static EventPublisher getInstance() {
        if (instance == null) {
            synchronized (EventPublisher.class) {
                if (instance == null) {
                    instance = new EventPublisher();
                }
            }
        }
        return instance;
    }

    public void subscribe(Consumer<InventoryEvent> handler) {
        handlers.add(handler);
    }

    public void publish(InventoryEvent event) {
        handlers.forEach(handler -> {
            try {
                handler.accept(event);
            } catch (Exception e) {
                System.err.println("Handler error: " + e.getMessage());
            }
        });
    }
}
```

---

## Step 3: CQRS Implementation

### 3.1 Create Read Models

Create `inventory-cqrs/src/main/java/com/inventory/read/InventoryReadModel.java`:

```java
package com.inventory.read;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryReadModel {
    private String itemId;
    private String sku;
    private String name;
    private int quantity;
    private BigDecimal totalValue;
    private boolean lowStock;
    private LocalDateTime lastUpdated;

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    public boolean isLowStock() { return lowStock; }
    public void setLowStock(boolean lowStock) { this.lowStock = lowStock; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
```

### 3.2 Create Projection

Create `inventory-cqrs/src/main/java/com/inventory/projection/InventoryProjection.java`:

```java
package com.inventory.projection;

import com.inventory.events.InventoryEvent;
import com.inventory.read.InventoryReadModel;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InventoryProjection {
    private final ConcurrentHashMap<String, InventoryReadModel> readModels = new ConcurrentHashMap<>();

    public void project(InventoryEvent event) {
        switch (event.getEventType()) {
            case "ITEM_CREATED":
                projectCreated(event);
                break;
            case "QUANTITY_UPDATED":
                projectQuantityUpdated(event);
                break;
            case "ITEM_DELETED":
                projectDeleted(event);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private void projectCreated(InventoryEvent event) {
        var data = (java.util.Map<String, Object>) event.getPayload();
        
        InventoryReadModel readModel = new InventoryReadModel();
        readModel.setItemId(event.getItemId());
        readModel.setSku((String) data.get("sku"));
        readModel.setName((String) data.get("name"));
        readModel.setQuantity((Integer) data.get("quantity"));
        readModel.setTotalValue(((java.math.BigDecimal) data.get("unitPrice"))
            .multiply(java.math.BigDecimal.valueOf((Integer) data.get("quantity"))));
        readModel.setLowStock((Integer) data.get("quantity") <= (Integer) data.get("reorderLevel"));
        readModel.setLastUpdated(event.getTimestamp());
        
        readModels.put(event.getItemId(), readModel);
    }

    @SuppressWarnings("unchecked")
    private void projectQuantityUpdated(InventoryEvent event) {
        InventoryReadModel readModel = readModels.get(event.getItemId());
        if (readModel != null) {
            var data = (java.util.Map<String, Object>) event.getPayload();
            int newQuantity = (Integer) data.get("newQuantity");
            readModel.setQuantity(newQuantity);
            readModel.setLowStock(newQuantity <= 10);
            readModel.setLastUpdated(event.getTimestamp());
        }
    }

    private void projectDeleted(InventoryEvent event) {
        readModels.remove(event.getItemId());
    }

    public List<InventoryReadModel> getAll() {
        return new java.util.ArrayList<>(readModels.values());
    }

    public List<InventoryReadModel> getLowStock() {
        return readModels.values().stream()
            .filter(InventoryReadModel::isLowStock)
            .collect(Collectors.toList());
    }

    public InventoryReadModel getById(String itemId) {
        return readModels.get(itemId);
    }
}
```

---

## Step 4: Run and Test

### 4.1 Create Main Application

Create `inventory-core/src/main/java/com/inventory/InventoryApplication.java`:

```java
package com.inventory;

import com.inventory.controller.InventoryController;
import com.inventory.repository.InMemoryInventoryRepository;
import com.inventory.repository.InventoryRepository;
import com.inventory.service.InventoryService;
import com.inventory.service.InventoryServiceImpl;

public class InventoryApplication {
    public static void main(String[] args) {
        InventoryRepository repository = new InMemoryInventoryRepository();
        InventoryService service = new InventoryServiceImpl(repository);
        InventoryController controller = new InventoryController(service);

        System.out.println("Inventory System Started!");
        System.out.println("Use REST endpoints to interact with the system.");
    }
}
```

### 4.2 Run Tests

```bash
# Test creating items
curl -X POST http://localhost:8080/api/inventory \
  -H "Content-Type: application/json" \
  -d '{"sku":"SKU001","name":"Widget","description":"A widget","quantity":100,"unitPrice":9.99,"category":"Electronics"}'

# Get all items
curl http://localhost:8080/api/inventory

# Get low stock items
curl http://localhost:8080/api/inventory/low-stock
```

---

## Project Deliverables

1. ✅ Working layered architecture with proper separation
2. ✅ Repository pattern implementation
3. ✅ Service layer with business logic
4. ✅ REST controller with proper error handling
5. ✅ Event-driven system for state changes
6. ✅ CQRS read model projection

---

## Extension Ideas

1. **Add Microservices**: Extract to separate services with API Gateway
2. **Add Kafka**: Replace in-memory event bus with Kafka
3. **Add CQRS**: Implement full command/query separation
4. **Add Docker**: Containerize the application
5. **Add Monitoring**: Add Prometheus metrics and Grafana dashboard
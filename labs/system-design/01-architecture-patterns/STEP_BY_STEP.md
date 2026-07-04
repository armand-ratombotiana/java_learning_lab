# Architecture Patterns - STEP BY STEP

## Building a Layered Architecture

### Step 1: Define the Domain Model
```java
public class Product { /* attributes, validation */ }
```

### Step 2: Create Repository Interface
```java
public interface ProductRepository { /* CRUD methods */ }
```

### Step 3: Implement Repository
```java
public class InMemoryProductRepository implements ProductRepository { /* in-memory map */ }
```

### Step 4: Create Service Layer
```java
@Service
public class ProductService {
    public ProductDTO createProduct(ProductDTO dto) { /* validate → save → return */ }
}
```

### Step 5: Build Controller
```java
@RestController
public class ProductController {
    @PostMapping("/products")
    public ResponseEntity<ProductDTO> create(@RequestBody ProductDTO dto) { /* ... */ }
}
```

### Step 6: Add Exception Handling
```java
@RestControllerAdvice
public class GlobalExceptionHandler { /* map exceptions to HTTP status codes */ }
```

## Building Microservices

### Step 1: Create Service Registry
Use Eureka or Consul. All services register on startup.

### Step 2: Build Individual Services
Each service has its own database, API, and business logic.

### Step 3: Configure API Gateway
Route requests to appropriate services based on path prefixes.

### Step 4: Add Inter-Service Communication
Use REST clients or message brokers for service-to-service calls.

### Step 5: Implement Resilience
Add circuit breakers, retries, and timeouts around external calls.

## Building Event-Driven System

### Step 1: Define Events
Create event classes with type, payload, and metadata.

### Step 2: Create Event Bus/Broker
Use Kafka topics or an in-memory event bus for initial prototyping.

### Step 3: Implement Producers
Services emit events when significant state changes occur.

### Step 4: Implement Consumers
Services subscribe to relevant event types and process them.

### Step 5: Handle Failure
Implement dead letter queues and retry logic for failed events.

## Implementing CQRS

### Step 1: Separate Command/Query Models
Commands: `CreateOrderCommand`. Queries: `OrderQueryService`.

### Step 2: Create Event Store
Store all state-changing events in order.

### Step 3: Build Projections
Consume events to build denormalized read models.

### Step 4: Wire Up Dispatcher
Route commands to handlers and queries to query service.

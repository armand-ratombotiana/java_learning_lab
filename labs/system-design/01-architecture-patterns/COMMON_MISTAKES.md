# Architecture Patterns - COMMON MISTAKES

## Layered Architecture

### 1. Layer Skipping
Controllers directly accessing repositories. Breaks separation of concerns.
```java
// WRONG
@GetMapping("/products/{id}")
public Product getProduct(@PathVariable Long id) {
    return productRepository.findById(id).orElseThrow();
}

// RIGHT
@GetMapping("/products/{id}")
public ProductDTO getProduct(@PathVariable Long id) {
    return productService.getProductById(id);
}
```

### 2. Anemic Domain Model
Services with no logic, just pass-through to repositories.

### 3. God Service
One service handles everything, defeating the purpose of layering.

## Microservices

### 1. Shared Database
Two services writing to the same database table. Creates tight coupling.

### 2. Chatty Services
Too many fine-grained calls between services. Increases latency.

### 3. Ignoring Distributed Transactions
Using ACID transactions across service boundaries without Saga pattern.

### 4. Missing Observability
No distributed tracing, centralized logging, or metrics.

## Event-Driven

### 1. Non-Idempotent Consumers
Processing an event twice causes data corruption. Always use idempotency keys.

### 2. Tightly Coupled Event Schemas
Changing an event schema breaks all consumers. Use schema registry.

### 3. Missing Dead Letter Handling
Failed events are lost forever. Implement DLQ for debugging.

## CQRS

### 1. Using CQRS Without Need
CQRS adds complexity. Only use when read/write patterns are significantly different.

### 2. Synchronous Projection Updates
Blocking the command until the read model is updated defeats CQRS benefits.

### 3. Mixing Command and Query Logic
Handling queries in command handlers or vice versa.

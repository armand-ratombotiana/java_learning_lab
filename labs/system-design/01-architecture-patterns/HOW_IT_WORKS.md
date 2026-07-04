# Architecture Patterns - HOW IT WORKS

## Layered Architecture

### Flow of a Request
```
Client → Controller (Presentation) → Service (Business) → Repository (Data) → Database
```

Each layer has a single responsibility:
- **Controller**: Parse HTTP request, validate input, return response
- **Service**: Apply business rules, orchestrate operations
- **Repository**: Abstract database access, map objects to rows

### Spring Boot Layered Flow
```java
@RestController  // Presentation Layer
public class OrderController {
    @PostMapping("/orders")
    public Order create(@RequestBody OrderRequest req) {
        return orderService.create(req);
    }
}

@Service  // Business Layer
public class OrderService {
    public Order create(OrderRequest req) {
        // validate business rules
        return orderRepository.save(mapToEntity(req));
    }
}

@Repository  // Data Layer
public interface OrderRepository extends JpaRepository<Order, Long> {}
```

## Microservices Architecture

### Inter-Service Communication
Services communicate via:
1. **Synchronous**: HTTP REST / gRPC — simple, blocking
2. **Asynchronous**: Message queue (Kafka, RabbitMQ) — decoupled, resilient
3. **Service Mesh**: Sidecar proxy handles communication (Istio)

### Discovery & Gateway Flow
```
Client → API Gateway → Service Discovery → Product Service
                    ↓
              Order Service (via REST call or event)
```

## Event-Driven Architecture

### Event Flow
```
Producer → Event Bus / Broker → Consumer(s)
```
Events are immutable records of something that happened. Consumers process independently.

### Idempotency Key
```java
if (processedEvents.contains(eventId)) return; // skip duplicate
processedEvents.add(eventId);
```

## CQRS

### Command/Query Separation
```
         ┌──────────────┐
Command →│ Command Model│→ Write DB
         └──────────────┘
                ↓ (sync/async)
         ┌──────────────┐
Query   ←│  Query Model │← Read DB (denormalized)
         └──────────────┘
```
Commands mutate state. Queries return state. Never both in one model.

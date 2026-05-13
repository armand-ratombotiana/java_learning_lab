# Microservices Patterns

## Service Communication

```
┌─────────────────────────────────────────────────────────────────┐
│  SYNCHRONOUS                                                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Client → API Gateway → Service A                               │
│                          ↕ REST/gRPC                            │
│                        Service B                                │
│                                                                  │
│  PROS: Simple, direct                                           │
│  CONS: Tight coupling, latency                                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  ASYNCHRONOUS                                                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Client → API Gateway → Message Broker → Service A              │
│                                    ↕                            │
│                                 Service B                       │
│                                                                  │
│  PROS: Loose coupling, resilience                              │
│  CONS: Complexity, eventual consistency                         │
└─────────────────────────────────────────────────────────────────┘
```

## API Gateway Pattern

```
                    ┌──────────────────────┐
                    │   API Gateway        │
                    │   /api/users/*       │
                    │   /api/orders/*      │
                    └──────────┬───────────┘
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
        ▼                      ▼                      ▼
   ┌─────────┐          ┌─────────┐          ┌─────────┐
   │ Users   │          │ Orders  │          │ Products│
   │ Service │          │ Service │          │ Service │
   └─────────┘          └─────────┘          └─────────┘
```

**Responsibilities:**
- Request routing
- Authentication/Authorization
- Rate limiting
- Request/Response transformation
- Logging

## Service Discovery

```
┌─────────────────────────────────────────────────────────────┐
│  CLIENT-SIDE (Eureka)                                       │
├─────────────────────────────────────────────────────────────┤
│  Service A → Service Registry (Eureka) → Service B         │
│              ↕ Register/Discover                            │
│  Service B                                                 │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  SERVER-SIDE (API Gateway)                                  │
├─────────────────────────────────────────────────────────────┤
│  Client → API Gateway → Service Registry → Service A       │
└─────────────────────────────────────────────────────────────┘
```

## Circuit Breaker

```
        ┌───────────────────────────────────────────────┐
        │           Circuit Breaker States              │
        ├───────────────────────────────────────────────┤
        │                                                │
        │   CLOSED → (failures > threshold) → OPEN     │
        │   OPEN → (timeout) → HALF_OPEN                │
        │   HALF_OPEN → (success) → CLOSED             │
        │                                                │
        │   OPEN: Reject requests immediately          │
        │   CLOSED: Normal operation                   │
        │   HALF_OPEN: Test recovery                   │
        └───────────────────────────────────────────────┘
```

## Saga Pattern

```
┌─────────────────────────────────────────────────────────────────┐
│  CHOREOGRAPHY (Event-based)                                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  OrderService ──create──→ OrderCreated                         │
│       ↓                                                        │
│  InventoryService ──reserve──→ InventoryReserved              │
│       ↓                                                        │
│  PaymentService ──charge──→ PaymentCompleted                   │
│                                                                  │
│  Compensating: On failure, send inverse events to undo         │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  ORCHESTRATION (Centralized)                                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  OrderService (Orchestrator)                                    │
│       ↓                                                         │
│  1. call InventoryService.reserve()                            │
│  2. call PaymentService.charge()                                │
│  3. call ShippingService.ship()                                │
│                                                                  │
│  On failure: Call compensating methods in reverse order        │
└─────────────────────────────────────────────────────────────────┘
```

## Data Patterns

```
┌─────────────────────────────────────────────────────────────────┐
│  DATABASE PER SERVICE                                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────┐   ┌──────────┐   ┌──────────┐                    │
│  │ Users DB │   │ Orders DB│   │Products DB│                   │
│  └────┬─────┘   └────┬─────┘   └────┬─────┘                    │
│       │              │              │                           │
│  ┌────┴─────┐   ┌────┴─────┐   ┌────┴─────┐                    │
│  │  Users   │   │  Orders  │   │ Products │                    │
│  │ Service  │   │ Service  │   │ Service  │                    │
│  └──────────┘   └──────────┘   └──────────┘                    │
│                                                                  │
│  Each service owns its data - no shared database!              │
└─────────────────────────────────────────────────────────────────┘
```

## Deployment Patterns

```
┌─────────────────────────────────────────────────────────────────┐
│  CONTAINER ORCHESTRATION (Kubernetes)                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                    Kubernetes Cluster                      │ │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐         │ │
│  │  │ Pod 1   │ │ Pod 2   │ │ Pod 3   │ │ Pod 4   │         │ │
│  │  │ User Svc│ │ Order   │ │Payment  │ │ Gateway │         │ │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘         │ │
│  └────────────────────────────────────────────────────────────┘ │
│                                                                  │
│  - Service discovery via DNS                                    │
│  - Load balancing                                               │
│  - Auto-scaling                                                 │
│  - Self-healing                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Resilience Patterns

| Pattern | Purpose |
|---------|---------|
| Circuit Breaker | Prevent cascade failures |
| Retry | Handle transient failures |
| Timeout | Prevent hanging requests |
| Bulkhead | Isolate failures |
| Fallback | Degrade gracefully |

## Key Principles

1. **Single Responsibility** - One service = one domain
2. **Loose Coupling** - Independent deployment, async communication
3. **High Cohesion** - Related functionality together
4. **Infrastructure as Code** - Automate deployment
5. **Observability** - Logging, metrics, tracing
6. **Zero Trust** - Secure service-to-service communication
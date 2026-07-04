# DDD Interview Questions

## Junior Level

### Q: What is the difference between an entity and a value object?
**A:** Entities have identity that persists over time (e.g., a Person). Value objects are defined by their attributes and are immutable (e.g., Money(50, USD)).

### Q: What is a repository in DDD?
**A:** A repository provides collection-like access to aggregates, hiding the underlying data storage implementation.

## Mid Level

### Q: How do you define aggregate boundaries?
**A:** Consider consistency requirements, transaction boundaries, and business invariants. Ask: what must be consistent together? Keep aggregates small.

### Q: How do you handle cross-aggregate transactions?
**A:** Use eventual consistency with domain events. Each aggregate is its own consistency boundary. For operations spanning aggregates, use a saga pattern.

## Senior Level

### Q: How would you refactor an anemic domain model to a rich one?
**A:**
1. Identify domain logic in services
2. Move domain logic into entities/value objects
3. Encapsulate collections
4. Add typed value objects (replace primitives)
5. Introduce domain events
6. Extract bounded contexts
7. Add anticorruption layers for external systems

### Q: Design a DDD-based order management system.
**A:**
- Bounded contexts: Order Management, Payment, Inventory, Shipping
- Aggregates: Order, Payment, Product, Shipment
- Value Objects: OrderId, Money, Address, Quantity
- Domain Events: OrderPlaced, PaymentReceived, InventoryReserved, OrderShipped
- Services: OrderApplicationService handles orchestration
- Repositories: Interface per aggregate in domain, implementation in infrastructure

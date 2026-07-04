# CQRS Interview Questions

## Junior Level

### Q: What is the difference between CQS and CQRS?
**A:** CQS (Command-Query Separation) operates at method level - methods either change state or return data, not both. CQRS operates at architectural level - separate objects/models for commands and queries.

### Q: What problem does CQRS solve?
**A:** It solves the problem of using a single model for both reads and writes, which forces compromises in optimization, security, and complexity.

## Mid Level

### Q: How do you keep read models in sync with write models?
**A:** Through event-driven projections. When a command modifies the write model, events are published. Projections consume these events and update read models accordingly.

### Q: What happens when a projection fails?
**A:** Failed event handlers typically retry with exponential backoff. If persistent failure occurs, events go to a dead letter queue for manual investigation. The projection can be rebuilt from scratch by replaying events.

## Senior Level

### Q: How would you design CQRS for a high-traffic e-commerce platform?
**A:**
1. Write side: PostgreSQL for product catalog with strong consistency
2. Read side: Elasticsearch for product search, Redis for cart, MongoDB for order history
3. Events: ProductUpdated, InventoryChanged, OrderPlaced
4. Projections: ProductSearchProjection (ES), CartProjection (Redis), OrderHistoryProjection (Mongo)
5. Handle eventual consistency with optimistic UI updates
6. Implement event replay for disaster recovery

### Q: When would you NOT use CQRS?
**A:** Simple CRUD applications, small teams, low complexity domains, or when eventual consistency is unacceptable for the core use case. Start with CRUD and introduce CQRS only when complexity demands it.

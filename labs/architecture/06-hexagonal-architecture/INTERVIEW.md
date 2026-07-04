# Hexagonal Architecture Interview Questions

## Junior Level

### Q: What is the difference between layered and hexagonal architecture?
**A:** In layered architecture, each layer depends on the layer below. In hexagonal, the domain is in the center with ports and adapters, and all dependencies point inward toward the domain.

### Q: What problem does hexagonal architecture solve?
**A:** It solves the problem of tight coupling between business logic and infrastructure (databases, frameworks, external APIs), making the system more testable and maintainable.

## Mid Level

### Q: How do you test a hexagonal application?
**A:** Domain logic is tested with unit tests using in-memory adapters. Adapter integration tests use Testcontainers for real infrastructure. ArchUnit tests verify architectural rules.

### Q: How do you handle transactions in hexagonal architecture?
**A:** Transaction management is typically handled at the adapter layer (e.g., @Transactional in Spring). The domain core shouldn't know about transactions. Use decorator pattern around ports for cross-cutting transaction concerns.

## Senior Level

### Q: How would you design a hexagonal microservice for payment processing?
**A:**
- Domain: Payment, Transaction, Money (value objects), PaymentStatus (enum)
- Inbound ports: ProcessPaymentUseCase, GetPaymentStatusUseCase
- Outbound ports: PaymentRepository, PaymentGatewayClient, EventPublisher
- Driving adapters: REST controller, Kafka consumer (for retries)
- Driven adapters: JPA repository, Stripe/Adyen payment gateway adapter, Kafka event publisher
- Tests: Unit test domain with in-memory adapters, integration test each adapter

### Q: How do you handle adapter selection at runtime?
**A:** Use Spring profiles or configuration to select which adapter implementation to inject. For multiple concurrent adapters, use composite pattern or routing adapter based on context.

# Clean Architecture Interview Questions

## Junior Level

### Q: What is the difference between Clean Architecture and Layered Architecture?
**A:** In layered architecture, layers stack vertically and each layer depends on the one below. Clean Architecture uses concentric layers with the dependency rule pointing inward. Clean Architecture places business rules at the center, while layered architecture often has the database at the bottom.

### Q: What goes in the entity layer?
**A:** Enterprise-wide business rules. These are high-level objects that contain the most critical business logic and are the least likely to change when external concerns change.

## Mid Level

### Q: How do you handle dependency injection in Clean Architecture?
**A:** Use a DI framework (like Spring) in the outer framework layer. Configure beans that wire up implementations to interfaces. The inner layers define interfaces (ports), and the outer layers provide implementations (adapters).

### Q: What is the presenter pattern and why is it useful?
**A:** The presenter is an output boundary adapter that formats data for the specific delivery mechanism. It allows the use case to remain delivery-agnostic. You can swap presenters for REST, GraphQL, gRPC, or even CLI without changing the use case.

## Senior Level

### Q: Design a Clean Architecture system for a banking application.
**A:**
Entity layer: Account, Transaction, Money, Customer
Use case layer: TransferFunds (DebitSource, CreditTarget, RollbackOnFailure)
Adapters: REST controller, JPA repositories, Kafka event publisher
Outer layer: Spring Boot configuration, security, monitoring
The TransferFunds use case orchestrates Account entities and uses AccountRepository (interface) without knowing about JPA.

### Q: How do you handle cross-cutting concerns (logging, transactions, caching) in Clean Architecture?
**A:** Use decorator pattern around use case boundaries: LoggingUseCaseDecorator, TransactionalUseCaseDecorator. These are configured in the outer framework layer. The use case itself remains pure of cross-cutting concerns.

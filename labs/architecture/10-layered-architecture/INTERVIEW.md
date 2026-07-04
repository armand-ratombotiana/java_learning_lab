# Layered Architecture Interview Questions

## Junior Level

### Q: Explain the layers in a typical Spring Boot application.
**A:** Controller layer (HTTP handling), Service layer (business logic), Repository layer (data access). Controllers depend on services, services depend on repositories.

### Q: Why should you use DTOs instead of exposing entities?
**A:** DTOs decouple the API contract from the internal data model, prevent over-fetching, hide sensitive fields, and allow API evolution independently of database schema.

## Mid Level

### Q: How do you handle transactions across multiple repositories?
**A:** Use @Transactional on the service layer method. Spring manages the transaction across all repository calls within that method. Configure isolation level and propagation as needed.

### Q: How would you detect and fix layer violations?
**A:** Use ArchUnit tests to automatically detect violations. Regular code reviews. Static analysis tools. Fix by moving logic to the correct layer or introducing a facade.

## Senior Level

### Q: When would you migrate from Layered to Hexagonal Architecture?
**A:** When the domain logic becomes complex enough that testing requires infrastructure, when you need multiple delivery mechanisms (REST + messaging), or when technology changes become frequent. Start by extracting ports from service interfaces.

### Q: Design a layered order management system.
**A:**
Controller: OrderController (CRUD endpoints)
Service: OrderService (create, cancel, ship logic, validation)
Repository: OrderRepository, OrderItemRepository (JPA)
Cross-cutting: Logging aspect, security config, exception handler
The service handles business rules (minimum order amount, credit check, inventory validation) while the controller only handles HTTP concerns.

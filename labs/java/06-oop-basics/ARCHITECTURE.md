# Architecture — OOP Basics

## Domain-Driven Design

Classes model domain entities: `Order`, `Customer`, `Product`. Value objects: `Money`, `Address`. Services: `PaymentService`, `ShippingService`.

## Layered Architecture

- **Presentation layer**: Controllers, views (DTOs)
- **Application layer**: Services, use cases
- **Domain layer**: Domain entities, value objects, domain services
- **Infrastructure layer**: Repositories, external APIs

## Dependency Injection

Classes declare dependencies in constructors. A DI framework (Spring, Guice) wires them together. Supports testing, swapping implementations.

## Singleton Pattern

Use `static` or enumeration for singletons. Spring's default bean scope is singleton.

## Builder Pattern

For complex objects with many optional fields, use a builder:
```java
User user = User.builder()
    .name("Alice")
    .age(30)
    .email("alice@example.com")
    .build();
```

## Repository Pattern

Abstract data access behind repository interfaces. Domain objects interact with repositories, not databases directly.

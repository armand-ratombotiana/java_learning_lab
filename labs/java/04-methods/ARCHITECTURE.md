# Architecture — Methods

## Service Layer Methods

In a service-oriented architecture, methods define the service API. Design methods as coarse-grained operations (use cases) rather than fine-grained data access.

## Command-Query Separation

Methods should be either commands (change state, return void) or queries (return data, no side effects). Avoid mixing.

## Repository Pattern

Repository methods abstract data access: `findById(id)`, `save(entity)`, `delete(entity)`. These methods form the persistence contract.

## Factory Method Pattern

Replace constructors with factory methods for flexibility: `LocalDate.of(2024, 1, 15)` vs `new LocalDate(2024, 1, 15)`.

## Method Design for Testability

- Pure functions (inputs → outputs, no side effects) are easiest to test
- Inject dependencies rather than creating them inside methods
- Use interfaces for method parameters that need mocking

## Layer Isolation

Each architectural layer (controller, service, repository) communicates through method calls. Define interfaces between layers to enable swapping implementations.

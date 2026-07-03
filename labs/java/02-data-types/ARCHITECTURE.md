# Architecture — Data Types

## Type Selection as Architectural Decision

Choosing the right types for a system is an architectural decision:
- Use `long` for IDs that might exceed int range
- Use `String` for flexible identifiers (but validate)
- Use enums for closed sets of values
- Use `BigDecimal` for monetary values
- Use `record` for value objects (Java 16+)

## Domain Modeling with Types

Types should model the domain. A `CustomerId` class (even wrapping a String) is better than a bare `String`. Use custom types to make the type system work for the domain.

## Primitive Obsession Anti-Pattern

Using primitives for domain concepts (int for age, String for email) misses opportunities for type safety. Create domain-specific types.

## Layered Architecture and Type Conversion

In layered architectures, each layer may use different types. DTOs (Data Transfer Objects) at the boundary, domain objects in the service layer, entities in the persistence layer. Conversion between layers is explicit.

## Type Safety Across Module Boundaries

Java 9+ module system enforces which packages are exported. Use types from exported packages as part of public API contracts.

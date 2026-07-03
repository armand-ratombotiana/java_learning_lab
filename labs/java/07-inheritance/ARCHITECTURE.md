# Architecture — Inheritance

## Template Method Pattern

Define an algorithm skeleton in a base class with abstract steps. Subclasses implement steps without changing the algorithm structure.

## Factory Method Pattern

Base class defines interface for creating objects. Subclasses decide which class to instantiate.

## Abstract Factory Pattern

Interface for creating families of related objects. Concrete factories create platform-specific implementations.

## Inheritance in Layered Architectures

- Base repository with common CRUD operations
- Specific repositories add custom queries
- Base controller with pagination, error handling
- Specific controllers add endpoints

## Strategy Pattern via Inheritance

Base strategy class defines algorithm interface. Subclasses implement different strategies.

## Framework Extension Points

Frameworks provide abstract classes for extension: `HttpServlet`, `JPanel`, `Activity`. Users extend and override specific methods.

## Inheritance vs Composition Decision

Favor composition for most cases. Use inheritance when:
- Clear IS-A relationship
- Subclass needs most of superclass behavior
- Superclass is stable (not changing frequently)
- You need polymorphic behavior

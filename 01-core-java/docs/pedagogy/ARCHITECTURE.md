# Architecture in Core Java

## Core Java Architectural Patterns

### 1. Layered Architecture
```
┌────────────────────────────┐
│  Presentation Layer       │
├────────────────────────────┤
│  Business Logic Layer     │
├────────────────────────────┤
│  Data Access Layer        │
├────────────────────────────┤
│  Database Layer            │
└────────────────────────────┘
```

### 2. Dependency Injection
- Loose coupling through constructor injection
- Use interfaces for dependencies
- Consider DI frameworks (Spring, Guice)
- Favor constructor injection over field injection

### 3. Factory Pattern
- Encapsulate object creation
- Hide implementation details
- Use for complex object construction
- Consider builder for many parameters

### 4. Singleton Pattern
- Use enum for thread-safe singleton
- Lazy initialization double-checked locking
- Consider dependency injection instead

## Design Principles

- **SOLID**: Single responsibility, Open-closed, Liskov substitution, Interface segregation, Dependency inversion
- **DRY**: Don't Repeat Yourself
- **KISS**: Keep It Simple, Stupid
- **YAGNI**: You Aren't Gonna Need It
# Common Mistakes with Design Patterns

## 1. Overusing Singleton

**Mistake**: Making everything a singleton
```java
// Every class is singleton
class UserService { private static UserService instance = ... }
class OrderService { private static OrderService instance = ... }
class Logger { private static Logger instance = ... }
```

**Problem**: Hard to test, hidden dependencies, tight coupling

**Better**: Use dependency injection, consider scope

## 2. Applying Patterns to Small Problems

**Mistake**: Using Factory/Builder for simple creation
```java
// Over-engineered
interface FruitFactory {
    Fruit createApple();
    Fruit createOrange();
}

// Simple is better
class Fruit {
    public static Apple apple() { return new Apple(); }
    public static Orange orange() { return new Orange(); }
}
```

## 3. Not Understanding When to Use Pattern

**Mistake**: Using Adapter where Bridge is better

**Solution**: Understand intent:
- Adapter: Make incompatible interfaces work
- Bridge: Decouple abstraction from implementation

## 4. Complexity Over Clarity

**Mistake**: Applying multiple patterns where simple code works
```java
// Too complex
class OrderFactory extends AbstractFactoryBuilder {
    // ... layers of patterns
}

// Simple is often better
record Order(int id, BigDecimal total) {}
```

## 5. Not Considering Alternatives

**Mistake**: Always using pattern without considering simpler approaches:
- Java 8+ lambdas can replace Strategy
- Dependency injection frameworks solve Singleton problems
- Builder pattern less needed with records

## 6. Copy-Paste Implementation

**Mistake**: Using pattern implementation without understanding

**Solution**: Understand why pattern solves the problem before applying
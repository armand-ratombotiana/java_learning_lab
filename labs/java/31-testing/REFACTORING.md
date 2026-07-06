# Refactoring for Testability

Making code testable often requires structural changes. Here are the most impactful refactorings.

## Dependency Injection for Testability

Replace hard-coded `new` calls with constructor injection. Instead of:

```java
class OrderService {
    private PaymentGateway gateway = new StripeGateway();
}
```

Inject the dependency:

```java
class OrderService {
    private final PaymentGateway gateway;
    OrderService(PaymentGateway gateway) { this.gateway = gateway; }
}
```

Now you can pass a mock or fake `PaymentGateway` in tests. Consider a DI framework (Spring, Guice, Dagger) for large codebases, but constructor injection works even without one.

## Extracting Interfaces

When a concrete class has side effects (database, HTTP, filesystem), extract an interface for the role it plays:

```java
interface PaymentGateway {
    boolean charge(BigDecimal amount);
}
```

The production class implements it; tests use a mock implementing the same interface. This decouples the consumer from the concrete implementation and enables seamless substitution in tests.

## Avoiding Static Methods

Static methods are global state — you cannot mock or override them per test. Refactor statics that touch external resources into instance methods on injectable objects. Utility statics (e.g., `StringUtils.isBlank()`) are fine; I/O statics (`Files.readAllLines()`, `System.currentTimeMillis()`) are not. Wrap the latter behind an interface.

## Breaking God Classes into Testable Units

A "god class" with hundreds of lines and mixed concerns is nearly impossible to test. Apply **Extract Class** and **Extract Method**:

- Identify distinct responsibilities (validation, persistence, notification).
- Move each responsibility into its own class with a single public method.
- Inject those new classes into the original god class.
- Test each extracted class in isolation.

The result: small, focused units that each require only a handful of test cases.

## Refactoring Legacy Code to Add Tests

Working with untested legacy code follows Michael Feathers' approach:

1. **Identify seams** — places where you can intercept behavior without changing the surrounding code (e.g., extract interface, add a constructor parameter).
2. **Sprout method/class** — add new code in a testable wrapper; don't rewrite existing logic.
3. **Characterization tests** — write tests that capture current (possibly buggy) behavior before refactoring.
4. **Break dependencies** — use the **Extract and Override Call** pattern: extract a method for a hard dependency, then override it in a test subclass.

The goal is always to get the code under test with minimal risk before improving its design.

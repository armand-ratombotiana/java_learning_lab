# Module 33: Code Organization - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the Dependency Inversion Principle (DIP)?
**Answer**:
DIP is the "D" in SOLID. It states two things:
1. High-level modules (business rules) should not depend on low-level modules (database, UI). Both should depend on abstractions (interfaces).
2. Abstractions should not depend on details. Details should depend on abstractions.
In practice, instead of an `OrderService` instantiating a `MySQLDatabase` class, the `OrderService` requires an `OrderRepository` interface. The `MySQLDatabase` implements that interface. The dependency direction is inverted.

### Q2: Why is "Package by Feature" generally preferred over "Package by Layer" in large codebases?
**Answer**:
"Package by Layer" groups all controllers together, all services together, etc. If you want to change the "Billing" feature, you have to jump across multiple distant packages, which breaks cohesion.
"Package by Feature" groups everything related to "Billing" (the controller, service, repository, and DTOs) into a single package. This maximizes high cohesion, minimizes coupling between features, allows for package-private visibility to hide internal implementations, and makes it incredibly easy to carve the feature out into its own Microservice later.

### Q3: Explain the "Dependency Rule" in Clean Architecture.
**Answer**:
The Dependency Rule states that source code dependencies must *only* point inward, toward higher-level policies (the core domain). Outer layers (like Web, UI, and Database frameworks) can depend on inner layers (Use Cases and Entities), but an inner layer can *never* know anything about an outer layer. This ensures the business logic remains pure, highly testable, and completely framework-agnostic.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Refactoring a Violation of the Open/Closed Principle
**Problem**: The following class calculates shipping costs. Every time the company adds a new shipping method, this class has to be modified. Refactor it to follow the Open/Closed Principle.

```java
public class ShippingCalculator {
    public double calculate(Order order, String shippingType) {
        if (shippingType.equals("STANDARD")) {
            return 5.00;
        } else if (shippingType.equals("EXPRESS")) {
            return 15.00;
        } else if (shippingType.equals("SAME_DAY")) {
            return 25.00;
        }
        return 0;
    }
}
```

**Solution**:
Use the Strategy Pattern to close the calculator for modification but leave it open for extension.

```java
public interface ShippingStrategy {
    double calculate(Order order);
}

public class StandardShipping implements ShippingStrategy {
    public double calculate(Order order) { return 5.00; }
}

public class ExpressShipping implements ShippingStrategy {
    public double calculate(Order order) { return 15.00; }
}

// The core class never needs to change when a new method is added.
public class ShippingCalculator {
    public double calculate(Order order, ShippingStrategy strategy) {
        return strategy.calculate(order);
    }
}
```
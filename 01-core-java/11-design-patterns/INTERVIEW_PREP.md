# Module 11: Design Patterns - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: Is the Singleton Pattern considered an anti-pattern? Why or why not?
**Answer**:
Yes, in many modern enterprise applications, the Singleton is considered an anti-pattern. 
- It introduces **global state**, making code hard to test and reason about.
- It causes **tight coupling** because classes must call `Singleton.getInstance()` directly, violating the Dependency Inversion Principle.
- Modern applications solve the "I only need one instance" problem using **Dependency Injection (IoC) containers** like Spring, which manage the lifecycle of objects (beans) and inject them where needed, without hardcoding singleton logic.

### Q2: How does the Strategy pattern differ from the State pattern?
**Answer**:
Both are behavioral patterns that define a family of algorithms, but their intent is different:
- **Strategy**: The client actively chooses which algorithm (strategy) to use at runtime (e.g., choosing a `CreditCardPayment` vs `PayPalPayment` strategy in a checkout cart). The strategy usually does not change during the operation.
- **State**: The context object automatically changes its behavior based on its internal state. The state transitions happen organically as a result of actions (e.g., a Vending Machine shifting from `IdleState` to `CoinInsertedState` to `DispensingState`).

### Q3: When would you use a Builder pattern instead of a Factory pattern?
**Answer**:
- **Factory** is used when you need to create different polymorphic implementations of an interface, and the creation logic is relatively simple (a few parameters).
- **Builder** is used when an object requires complex initialization involving numerous parameters, especially optional ones. It solves the "Telescoping Constructor Anti-Pattern" by allowing step-by-step configuration before calling a final `build()` method, returning an immutable object.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Implementing the Decorator Pattern
**Problem**: You have a basic `Coffee` interface that costs $2.00. Customers can add milk ($0.50) or sugar ($0.25). Design this using the Decorator pattern so combinations can be mixed dynamically.

**Solution**:
```java
interface Coffee { double getCost(); }

class SimpleCoffee implements Coffee {
    public double getCost() { return 2.00; }
}

abstract class CoffeeDecorator implements Coffee {
    protected Coffee decoratedCoffee;
    public CoffeeDecorator(Coffee c) { this.decoratedCoffee = c; }
    public double getCost() { return decoratedCoffee.getCost(); }
}

class Milk extends CoffeeDecorator {
    public Milk(Coffee c) { super(c); }
    public double getCost() { return super.getCost() + 0.50; }
}

class Sugar extends CoffeeDecorator {
    public Sugar(Coffee c) { super(c); }
    public double getCost() { return super.getCost() + 0.25; }
}

// Usage
Coffee myCoffee = new Sugar(new Milk(new SimpleCoffee()));
```

### Scenario 2: Double-Checked Locking in Singleton
**Problem**: Write a thread-safe Singleton using lazy initialization and double-checked locking. Explain why `volatile` is necessary.

**Solution**:
```java
public class Singleton {
    // volatile ensures changes to instance are visible across threads immediately
    // and prevents the JVM from reordering the instantiation instructions.
    private static volatile Singleton instance;
    
    private Singleton() {}
    
    public static Singleton getInstance() {
        if (instance == null) { // First check (lock-free)
            synchronized (Singleton.class) {
                if (instance == null) { // Second check (thread-safe)
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```
# Module 11: Design Patterns - Quick Reference

**Quick Lookup**: All patterns at a glance  
**Interview Prep**: Common questions and answers  
**Decision Trees**: Pattern selection flowcharts

---

## 🎯 Pattern Quick Lookup

### Creational Patterns

| Pattern | Purpose | When to Use | Key Benefit |
|---------|---------|-------------|------------|
| **Singleton** | Single instance | Global access needed | Controlled instantiation |
| **Factory** | Object creation | Multiple types | Decoupled creation |
| **Builder** | Complex construction | Many optional params | Readable construction |
| **Prototype** | Clone objects | Expensive creation | Fast duplication |

### Structural Patterns

| Pattern | Purpose | When to Use | Key Benefit |
|---------|---------|-------------|------------|
| **Adapter** | Interface conversion | Incompatible interfaces | Reuse existing code |
| **Decorator** | Add responsibilities | Dynamic behavior | Flexible composition |
| **Facade** | Simplify subsystem | Complex system | Easy-to-use interface |
| **Proxy** | Control access | Lazy loading, security | Controlled access |

### Behavioral Patterns

| Pattern | Purpose | When to Use | Key Benefit |
|---------|---------|-------------|------------|
| **Observer** | One-to-many notification | Event handling | Loose coupling |
| **Strategy** | Algorithm selection | Multiple algorithms | Runtime flexibility |
| **Command** | Encapsulate request | Undo/redo, queuing | Request as object |
| **State** | Behavior by state | State-dependent behavior | Clean state handling |

### Architectural Patterns

| Pattern | Purpose | When to Use | Key Benefit |
|---------|---------|-------------|------------|
| **MVC** | Separation of concerns | UI applications | Maintainability |
| **Repository** | Data access abstraction | Database operations | Testability |

---

## 🔍 Pattern Selection Decision Tree

```
Need to create objects?
├─ Single instance needed?
│  └─ YES → Singleton
├─ Different types?
│  └─ YES → Factory
├─ Complex construction?
│  └─ YES → Builder
└─ Clone existing?
   └─ YES → Prototype

Need to compose objects?
├─ Adapt interface?
│  └─ YES → Adapter
├─ Add responsibilities?
│  └─ YES → Decorator
├─ Simplify subsystem?
│  └─ YES → Facade
└─ Control access?
   └─ YES → Proxy

Need to define interactions?
├─ One-to-many notification?
│  └─ YES → Observer
├─ Encapsulate algorithm?
│  └─ YES → Strategy
├─ Encapsulate request?
│  └─ YES → Command
└─ Change by state?
   └─ YES → State
```

---

## 💡 Pattern Cheat Sheets

### Singleton Cheat Sheet

```java
// Enum (BEST PRACTICE)
public enum Singleton {
    INSTANCE;
    public void doSomething() { }
}

// Bill Pugh (Thread-safe, Lazy)
public class Singleton {
    private Singleton() { }
    private static class Helper {
        private static final Singleton INSTANCE = new Singleton();
    }
    public static Singleton getInstance() {
        return Helper.INSTANCE;
    }
}

// Usage
Singleton singleton = Singleton.INSTANCE;
// or
Singleton singleton = Singleton.getInstance();
```

### Factory Cheat Sheet

```java
// Simple Factory
public class Factory {
    public static Product create(String type) {
        switch(type) {
            case "A": return new ProductA();
            case "B": return new ProductB();
            default: throw new IllegalArgumentException();
        }
    }
}

// Usage
Product product = Factory.create("A");
```

### Builder Cheat Sheet

```java
public class User {
    private final String name;
    private final String email;
    private final int age;
    
    private User(Builder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.age = builder.age;
    }
    
    public static class Builder {
        private final String name;
        private final String email;
        private int age;
        
        public Builder(String name, String email) {
            this.name = name;
            this.email = email;
        }
        
        public Builder age(int age) {
            this.age = age;
            return this;
        }
        
        public User build() {
            return new User(this);
        }
    }
}

// Usage
User user = new User.Builder("John", "john@example.com")
    .age(30)
    .build();
```

### Observer Cheat Sheet

```java
public interface Observer {
    void update(String message);
}

public class Subject {
    private List<Observer> observers = new ArrayList<>();
    
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}

// Usage
Subject subject = new Subject();
subject.attach(new ConcreteObserver("Observer 1"));
subject.notifyObservers("Hello");
```

### Strategy Cheat Sheet

```java
public interface Strategy {
    void execute();
}

public class Context {
    private Strategy strategy;
    
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
    
    public void executeStrategy() {
        strategy.execute();
    }
}

// Usage
Context context = new Context();
context.setStrategy(new ConcreteStrategyA());
context.executeStrategy();
```

### Decorator Cheat Sheet

```java
public interface Component {
    void operation();
}

public abstract class Decorator implements Component {
    protected Component component;
    
    public Decorator(Component component) {
        this.component = component;
    }
    
    @Override
    public void operation() {
        component.operation();
    }
}

public class ConcreteDecorator extends Decorator {
    public ConcreteDecorator(Component component) {
        super(component);
    }
    
    @Override
    public void operation() {
        super.operation();
        addedBehavior();
    }
    
    private void addedBehavior() {
        // Additional behavior
    }
}

// Usage
Component component = new ConcreteComponent();
component = new ConcreteDecorator(component);
component.operation();
```

---

## 🎤 Interview Questions & Answers

### Q1: What is a Design Pattern?
**Answer**: A design pattern is a reusable solution to a common problem in software design. It provides a template for writing maintainable, scalable code.

### Q2: What are the three categories of design patterns?
**Answer**: 
1. **Creational** - Object creation (Singleton, Factory, Builder, Prototype)
2. **Structural** - Object composition (Adapter, Decorator, Facade, Proxy)
3. **Behavioral** - Object interaction (Observer, Strategy, Command, State)

### Q3: When would you use Singleton pattern?
**Answer**: When you need exactly one instance of a class with global access. Examples: Logger, Configuration Manager, Database Connection Pool.

### Q4: What's the difference between Factory and Builder?
**Answer**: 
- **Factory** creates objects of different types
- **Builder** constructs complex objects step by step with optional parameters

### Q5: Why use Observer pattern?
**Answer**: To establish loose coupling between objects. When one object changes state, all observers are notified automatically without tight coupling.

### Q6: What's the difference between Strategy and State?
**Answer**:
- **Strategy** - Client chooses algorithm at runtime
- **State** - Object changes behavior based on internal state

### Q7: When would you use Decorator instead of inheritance?
**Answer**: When you need to add responsibilities dynamically at runtime. Inheritance is static; Decorator is dynamic and more flexible.

### Q8: What's the purpose of Facade pattern?
**Answer**: To provide a simplified, unified interface to a complex subsystem, making it easier for clients to use.

### Q9: How does Proxy differ from Decorator?
**Answer**:
- **Proxy** - Controls access to another object (lazy loading, security)
- **Decorator** - Adds new functionality to an object

### Q10: What are anti-patterns?
**Answer**: Anti-patterns are common mistakes in software design that should be avoided. Examples: God Object, Spaghetti Code, Circular Dependencies.

### Q11: Can you combine multiple patterns?
**Answer**: Yes. Many real-world systems use multiple patterns together. For example, Factory + Singleton, or Strategy + Observer.

### Q12: How do you choose the right pattern?
**Answer**: Identify the problem you're solving, then match it to a pattern. Use the Pattern Selection Guide. Avoid over-engineering.

---

## 📋 Pattern Comparison Matrix

```
                Singleton  Factory  Builder  Prototype
Purpose         Single     Create   Complex  Clone
                instance   types    objects  objects
Complexity      Low        Low      Medium   Medium
Flexibility     Low        High     High     Medium
Thread-safe     Yes        Yes      Yes      Depends
Serializable    Tricky     Yes      Yes      Yes

                Adapter    Decorator Facade  Proxy
Purpose         Convert    Add      Simplify Control
                interface  behavior subsystem access
Complexity      Low        Medium   Medium   Medium
Flexibility     Medium     High     Low      Medium
Performance     Good       Good     Good     Depends
Coupling        Loose      Loose    Loose    Loose

                Observer   Strategy Command  State
Purpose         Notify     Algorithm Encapsulate Change
                multiple   selection request  behavior
Complexity      Medium     Low      Medium   Medium
Flexibility     High       High     High     High
Coupling        Loose      Loose    Loose    Loose
```

---

## 🔧 Common Pattern Combinations

### Combination 1: Factory + Singleton
```java
// Create Singleton instances through Factory
public class SingletonFactory {
    private static final Map<String, Object> singletons = new HashMap<>();
    
    public static <T> T getSingleton(String key, Class<T> type) {
        return (T) singletons.computeIfAbsent(key, k -> {
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
```

### Combination 2: Strategy + Factory
```java
// Factory creates different strategies
public class StrategyFactory {
    public static PaymentStrategy createStrategy(String type) {
        switch(type) {
            case "credit_card": return new CreditCardPayment();
            case "paypal": return new PayPalPayment();
            default: throw new IllegalArgumentException();
        }
    }
}
```

### Combination 3: Decorator + Factory
```java
// Factory creates decorated objects
public class CoffeeFactory {
    public static Coffee createCoffee(String type) {
        Coffee coffee = new SimpleCoffee();
        if (type.contains("milk")) {
            coffee = new MilkDecorator(coffee);
        }
        if (type.contains("sugar")) {
            coffee = new SugarDecorator(coffee);
        }
        return coffee;
    }
}
```

### Combination 4: Observer + Strategy
```java
// Observer notifies about strategy changes
public class StrategySubject {
    private List<Observer> observers = new ArrayList<>();
    private Strategy strategy;
    
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
        notifyObservers("Strategy changed");
    }
    
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
```

---

## 🚀 Pattern Implementation Checklist

### Before Implementing a Pattern

- [ ] Identify the problem clearly
- [ ] Confirm the pattern solves this problem
- [ ] Check if pattern is not over-engineering
- [ ] Review edge cases and pitfalls
- [ ] Plan implementation approach
- [ ] Consider thread-safety requirements
- [ ] Plan for testing strategy

### After Implementing a Pattern

- [ ] Code follows pattern structure
- [ ] All edge cases handled
- [ ] Thread-safe if needed
- [ ] Proper error handling
- [ ] Unit tests written
- [ ] Documentation complete
- [ ] Code review passed

---

## 📚 Pattern Reference by Use Case

### Use Case: Logging
**Patterns**: Singleton (Logger instance), Decorator (add logging to operations)

### Use Case: Database Access
**Patterns**: Singleton (Connection Pool), Factory (different DB types), Repository (data access abstraction)

### Use Case: UI Framework
**Patterns**: Observer (event handling), Strategy (rendering algorithms), Decorator (UI components)

### Use Case: Payment Processing
**Patterns**: Strategy (payment methods), Factory (create payment processors), Command (transaction history)

### Use Case: Configuration Management
**Patterns**: Singleton (config instance), Factory (different config sources), Facade (simplified access)

### Use Case: Plugin System
**Patterns**: Factory (create plugins), Observer (plugin events), Strategy (plugin algorithms)

---

## 🎓 Study Tips for Interviews

1. **Know the Gang of Four**: Be familiar with the 23 classic patterns
2. **Understand Trade-offs**: Know pros and cons of each pattern
3. **Real-World Examples**: Be able to give practical examples
4. **Implementation Details**: Know how to implement each pattern
5. **When NOT to Use**: Know when patterns are overkill
6. **Pattern Combinations**: Understand how patterns work together
7. **Anti-Patterns**: Know what to avoid
8. **Code Examples**: Be ready to write code

---

## 🔗 Quick Links

- **DEEP_DIVE.md** - Detailed explanations with 140+ examples
- **QUIZZES.md** - 24 questions across 4 difficulty levels
- **EDGE_CASES.md** - 18 pitfalls and prevention strategies
- **PEDAGOGIC_GUIDE.md** - 4-phase learning path with exercises

---

**Module 11 - Design Patterns Quick Reference**  
*Your go-to guide for pattern selection and implementation*
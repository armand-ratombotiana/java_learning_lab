# Object-Oriented Programming Theory

## First Principles

### What is OOP?

Object-Oriented Programming is a programming paradigm that organizes code around **objects** - data structures that combine state (fields) and behavior (methods). This differs from procedural programming, which organizes code around functions and data is separate from operations.

**Core Idea**: Model software as interacting objects, where each object is a self-contained entity responsible for its own state and behavior.

### Why OOP?

OOP emerged to solve problems in large-scale software development:
1. **Modularity**: Objects can be developed independently
2. **Reuse**: Inheritance enables code reuse through hierarchies
3. **Maintainability**: Encapsulation isolates change impact
4. **Abstraction**: Complex systems become manageable through interfaces

---

## The Four Pillars

### 1. Encapsulation

**Definition**: Bundling data and methods that operate on that data within a single unit (class), while restricting direct access to some components.

**Why It Matters**:
- Prevents external code from depending on internal representation
- Allows internal changes without breaking consumers
- Protects invariants (valid state constraints)

**Mechanism**: Access modifiers control visibility
- `private`: Class-only access
- `package-private`: Same package
- `protected`: Subclasses + same package
- `public`: Everyone

```java
public class BankAccount {
    private double balance;  // Hidden from outside
    
    public void deposit(double amount) {
        if (amount > 0) {    // Invariant enforcement
            balance += amount;
        }
    }
    
    public double getBalance() {  // Controlled access
        return balance;
    }
}
```

**Theory**: Encapsulation implements the principle of **information hiding** - the internal workings of an object are hidden from external view. This creates a **contract** between the object and its users: the public interface remains stable while internal implementation changes.

### 2. Inheritance

**Definition**: A mechanism where a new class (subclass) derives properties and behavior from an existing class (superclass).

**Why It Matters**:
- Code reuse: Subclass inherits parent implementation
- Polymorphism: Uniform handling of related types
- Hierarchical modeling: Natural way to represent "is-a" relationships

**Mechanism**: `extends` for classes, `implements` for interfaces

```java
class Animal {
    void eat() { /* ... */ }
}

class Dog extends Animal {
    void bark() { /* ... */ }
}
```

**Theory**: Inheritance creates an **"is-a"** relationship. A Dog is an Animal. The subclass specializes (adds behavior) or narrows (restricts) the parent.

**Key Concepts**:
- **super**: Reference to parent class
- **Method Overriding**: Subclass redefines parent method
- **Constructor Chaining**: Parent constructor runs before subclass

### 3. Polymorphism

**Definition**: The ability of objects of different types to be treated uniformly through a common interface.

**Why It Matters**:
- Write generic code that works with multiple types
- Decouple code from concrete implementations
- Enable extensibility without modification

**Mechanism**:
- **Compile-time (Overloading)**: Same method name, different parameters
- **Runtime (Overriding)**: Subclass method invoked via parent reference

```java
class Shape {
    abstract double area();
}

class Circle extends Shape {
    double area() { return Math.PI * r * r; }
}

class Rectangle extends Shape {
    double area() { return width * height; }
}

// Polymorphic call
Shape s = new Circle(5);
s.area();  // Returns circle's area, not rectangle's
```

**Theory**: Polymorphism works through **dynamic dispatch** - the JVM determines which method to call at runtime based on the actual object type, not the reference type. This is achieved through **virtual method tables** (vtables).

### 4. Abstraction

**Definition**: Representing essential features without including the underlying implementation details.

**Why It Matters**:
- Manages complexity by hiding unnecessary details
- Focuses on "what" rather than "how"
- Enables loose coupling between components

**Mechanism**: Abstract classes and interfaces

```java
interface PaymentProcessor {
    void processPayment(double amount);
    // Implementation hidden - any PaymentProcessor works
}

class StripeProcessor implements PaymentProcessor {
    public void processPayment(double amount) {
        // Stripe-specific logic
    }
}
```

**Theory**: Abstraction creates a **contract** - a specification of what something does without dictating how. Users depend on abstraction, enabling:
- Swapping implementations
- Testing with mocks
- Parallel development

---

## Design Principles

### SOLID Principles

Five guidelines for maintainable, flexible object-oriented design:

1. **S**ingle Responsibility: A class should have one reason to change
2. **O**pen/Closed: Open for extension, closed for modification
3. **L**iskov Substitution: Subtypes must be substitutable for base types
4. **I**nterface Segregation: Prefer specific interfaces over general ones
5. **D**ependency Inversion: Depend on abstractions, not concretions

### Composition over Inheritance

**Theory**: While inheritance provides code reuse, it creates tight coupling. Composition (has-a relationship) provides more flexibility:

```java
// Inheritance - tight coupling
class Dog extends Animal {
    // Dog is permanently bound to Animal's implementation
}

// Composition - loose coupling
class Car {
    private Engine engine;  // Car has an engine
    // Can swap engine implementations
}
```

**Rule of Thumb**: "Favor composition over inheritance" because:
- Less fragile when parent changes
- More flexible runtime behavior
- Easier to test

---

## Design Patterns

### Creational Patterns

- **Factory**: Encapsulate object creation
- **Singleton**: Single instance management
- **Builder**: Complex object construction
- **Prototype**: Clone existing objects

### Structural Patterns

- **Adapter**: Convert interface to expected
- **Decorator**: Add behavior dynamically
- **Facade**: Simplified interface
- **Proxy**: Controlled access

### Behavioral Patterns

- **Strategy**: Interchangeable algorithms
- **Observer**: Event notification
- **Command**: Encapsulate request
- **Template Method**: Algorithm skeleton

---

## Why It Works This Way

### Virtual Method Tables

When a class overrides a method, the JVM uses a vtable to dispatch the call:
- Each class gets a vtable
- Object contains pointer to class's vtable
- Method call looks up implementation at runtime

This enables runtime polymorphism efficiently.

### Interface vs Abstract Class

**Theory Distinction**:
- **Abstract class**: "Is-a" relationship, shared implementation
- **Interface**: "Can-do" relationship, capability contract

Java 8+ introduced default methods in interfaces, blurring this line, but the conceptual distinction remains useful.

---

## Common Misconceptions

1. **"Inheritance is reuse"**: Often creates fragile coupling; composition usually better
2. **"More classes is better OOP"**: Simplicity matters; over-abstraction adds complexity
3. **"Getters/setters are encapsulation"**: True encapsulation requires business logic, not just data access
4. **"OOP means everything must be a class"**: Functional approaches have merit; context determines best tool

---

## Further Theory

### From Here

- **Module 3 (Collections)**: How Java's collections use OOP principles
- **Module 8 (Generics)**: Type-safe abstraction over collections
- **Module 13 (Design Patterns)**: Detailed pattern implementations
- **Module 14 (Spring Core)**: OOP in enterprise frameworks
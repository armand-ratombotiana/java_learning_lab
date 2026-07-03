# Abstraction & Interfaces — Theoretical Foundation

## Abstraction

Abstraction hides implementation details and exposes only essential features. It reduces complexity by separating what something does from how it does it.

### Abstract Classes

An abstract class cannot be instantiated and may contain abstract methods (without body) and concrete methods.

```java
public abstract class Shape {
    protected String color;

    public Shape(String color) {
        this.color = color;
    }

    public abstract double getArea();        // No body — subclasses must implement

    public String getColor() {               // Concrete method
        return color;
    }
}
```

Key characteristics:
- Declared with `abstract` keyword
- Cannot be instantiated: `new Shape("red")` — compilation error
- Can have constructors (called from subclass via `super()`)
- Can have fields, concrete methods, static methods
- Can have `main` method
- Subclass must implement all abstract methods or be declared abstract itself

### When to Use Abstract Classes

- Classes share common state (fields) and behavior
- You want to provide partial implementation
- Related classes are in a hierarchy (IS-A relationship)
- You need constructors to initialize shared state

## Interfaces

An interface defines a contract — a set of method signatures that implementing classes must provide.

```java
public interface Drawable {
    void draw();  // Abstract method (implicitly public abstract)
}
```

### Interface Evolution

| Java Version | Interface Features |
|-------------|-------------------|
| Java 1-6 | Only abstract methods (`public abstract` implicitly) |
| Java 7 | Same as Java 6 |
| Java 8 | Default methods, static methods |
| Java 9 | Private methods |
| Java 16 | `sealed` interfaces |

### Default Methods (Java 8+)

Allow adding new methods to interfaces without breaking existing implementations:

```java
public interface PaymentProcessor {
    void processPayment(double amount);

    default void processRefund(double amount) {
        System.out.println("Processing refund of " + amount);
    }
}
```

### Static Methods in Interfaces (Java 8+)

Utility methods related to the interface:

```java
public interface PaymentProcessor {
    static boolean validateCard(String cardNumber) {
        return cardNumber.length() == 16;
    }
}
```

Called as `PaymentProcessor.validateCard("1234567890123456")` — not on instances.

### Private Methods (Java 9+)

Share code between default methods:

```java
public interface PaymentProcessor {
    default void processPayment(double amount) {
        log("Processing payment: " + amount);
    }

    default void processRefund(double amount) {
        log("Processing refund: " + amount);
    }

    private void log(String message) {  // Helper — not part of public contract
        System.out.println("[LOG] " + message);
    }
}
```

## Abstract Class vs Interface

| Aspect | Abstract Class | Interface |
|--------|---------------|-----------|
| Multiple inheritance | Single | Multiple (a class can implement many interfaces) |
| Constructors | Yes | No |
| Fields | Any type, any access | `public static final` only |
| Methods | Abstract and concrete | Abstract, default, static, private |
| State | Can have state | Cannot have instance state |
| When to use | IS-A, shared state | CAN-DO, contract |
| `extends`/`implements` | `extends` | `implements` |

## Multiple Inheritance of Type

Java supports multiple inheritance of type (interfaces) but not of implementation (classes):

```java
public class AmphibiousVehicle implements LandVehicle, WaterVehicle {
    // Must implement all abstract methods from both interfaces
}
```

This avoids the diamond problem (ambiguity when two superclasses define the same method).

## Functional Interfaces (Java 8+)

Interfaces with exactly one abstract method, used as lambda targets:

```java
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}

Predicate<String> isEmpty = s -> s.isEmpty();
```

Common built-in functional interfaces: `Runnable`, `Callable`, `Comparator`, `Consumer<T>`, `Function<T,R>`, `Predicate<T>`, `Supplier<T>`.

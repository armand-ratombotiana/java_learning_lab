# Why Polymorphism Matters

## Programming to Interfaces, Not Implementations

Polymorphism enables the most important design principle in Java: "program to an interface, not an implementation." Code written against `List` works with `ArrayList`, `LinkedList`, `CopyOnWriteArrayList`, or any other `List` implementation — swap implementations without changing code.

## The Open/Closed Principle

Polymorphism makes code open for extension but closed for modification:
```java
// No polymorphism: adding a new shape requires modifying this method
void drawShape(String type) {
    if (type.equals("circle")) drawCircle();
    else if (type.equals("square")) drawSquare();
}

// With polymorphism: adding a new shape requires only a new class
void drawShape(Shape shape) {
    shape.draw();
}
```

## Strategy Pattern

Polymorphism enables the Strategy pattern — selecting algorithms at runtime:
```java
interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardPayment implements PaymentStrategy { ... }
class PayPalPayment implements PaymentStrategy { ... }

// Strategy selected at runtime based on user choice
PaymentStrategy strategy = getStrategy(userChoice);
strategy.pay(amount);
```

## Collections Framework

The entire Collections Framework relies on polymorphism. `Collection`, `List`, `Set`, `Map` are interfaces. Methods accept and return these interfaces, enabling interchangeable implementations. `Collections.sort()` works with any `List` implementation.

## Polymorphism in Java 8+ Lambdas

Functional interfaces enable lambda polymorphism:
```java
Function<String, Integer> parser = Integer::parseInt;
Function<String, Integer> length = String::length;
// Same interface, different behaviors
```

## Real-World Impact

Polymorphism is why we can write generic algorithms that work across types. Sort algorithms, search algorithms, and collection operations are polymorphic at their core. The Strategy, Command, Observer, and Template Method patterns all rely on polymorphism.

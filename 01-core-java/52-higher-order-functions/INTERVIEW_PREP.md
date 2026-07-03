# Interview Preparation: Higher-Order Functions

This document covers advanced questions related to closures, currying, partial application, and the Strategy pattern.

## Q1: What is a Higher-Order Function?
**Answer:**
A higher-order function is a function that either takes one or more functions as arguments, or returns a function as its result (or both).
In Java, this is implemented by passing or returning instances of Functional Interfaces (like `Predicate`, `Function`, `Consumer`, or custom interfaces like `Runnable`). Methods like `Stream.map()` and `Stream.filter()` are classic examples of higher-order functions.

## Q2: Explain what a "Closure" is in Java. What restrictions apply to it?
**Answer:**
A closure is a lambda expression (or anonymous inner class) that captures and remembers the state of local variables from its enclosing scope at the time it was created.
When a higher-order function returns a lambda, that lambda carries the captured variables with it, even after the method that created it has finished executing and its stack frame is destroyed.
**Restriction**: In Java, closures can only capture local variables that are `final` or "effectively final" (never modified after initialization). If you attempt to mutate a captured variable inside or outside the lambda, the compiler will throw an error to prevent unpredictable race conditions.

## Q3: What is the difference between Currying and Partial Application?
**Answer:**
*   **Currying** is the mathematical process of transforming a function that takes multiple arguments into a sequence of nested functions, each taking exactly one argument. For example, `f(x, y, z)` becomes `f(x)(y)(z)`.
*   **Partial Application** is the process of taking a function with multiple arguments, providing (fixing) a subset of those arguments, and returning a new function that takes the remaining arguments. For example, taking `f(x, y, z)`, fixing `x=10` and `y=20`, and returning a new function `g(z)`.
Currying is a technique that *enables* partial application, but they are distinct concepts.

## Q4: How do Higher-Order Functions replace the Strategy Design Pattern?
**Answer:**
The classic Strategy Pattern requires defining an interface (e.g., `PaymentStrategy`), creating multiple concrete classes (`CreditCardStrategy`, `PayPalStrategy`), and passing instances of those classes into a context object. This requires significant boilerplate.
With Higher-Order Functions, the interface is simply a standard Functional Interface (e.g., `Consumer<Order>`). Instead of creating concrete classes, the client simply passes a lambda expression representing the algorithm directly into the context method. This eliminates the need for a deep class hierarchy while achieving the exact same goal: dynamically swapping out behavior at runtime.

## Q5: What is the risk of deeply nested Currying or returning Lambdas that capture large objects?
**Answer:**
1.  **Memory Leaks**: Because lambdas form closures, they hold strong references to any objects they capture from the enclosing scope. If a method returns a lambda that captures a massive 100MB array, that array cannot be garbage collected as long as the lambda object exists.
2.  **Performance Overhead**: Every step in a curried chain (`f.apply(x).apply(y)`) involves allocating a lambda object on the heap and performing virtual method dispatch. While the JIT compiler optimizes this heavily, doing deep currying inside a tight, CPU-intensive inner loop will generate excessive garbage and degrade performance compared to a standard multi-argument method call.
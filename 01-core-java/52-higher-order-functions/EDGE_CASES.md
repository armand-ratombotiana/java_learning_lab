# Edge Cases & Pitfalls: Higher-Order Functions

While higher-order functions enable highly reusable and declarative code, they can also lead to severe memory leaks, unreadable type signatures, and performance degradation if misused.

## 1. The Closure Memory Leak
*   **The Scenario**: You write a method that returns a `Runnable`. The lambda captures a large object from the surrounding scope.
    ```java
    public Runnable createTask() {
        byte[] massiveData = new byte[100 * 1024 * 1024]; // 100MB
        return () -> System.out.println("Processing data of size: " + massiveData.length);
    }
    ```
*   **The Pitfall**: Lambdas in Java form **Closures**. They capture the variables they use from their enclosing scope. Because the returned `Runnable` uses `massiveData`, the lambda object holds a strong, hidden reference to the 100MB array. If you store this `Runnable` in a long-lived list or event registry, the 100MB array can *never* be garbage collected, even after the `createTask` method finishes.
*   **Mitigation**: Be extremely conscious of what variables you capture in lambdas that are returned or passed to long-lived components. If you only need a small piece of data (like the length), extract it *before* the lambda:
    ```java
    int size = massiveData.length;
    return () -> System.out.println("Processing data of size: " + size); // Only captures an int!
    ```

## 2. Type Signature Hell
*   **The Scenario**: You heavily use currying and higher-order functions to build a dynamic validation framework.
*   **The Pitfall**: Java's type system is verbose. A function that takes a function and returns a function can have an unreadable signature.
    ```java
    // DANGER: Unreadable signature
    public Function<Predicate<String>, Function<String, Boolean>> buildValidator() { ... }
    ```
    This makes the code incredibly difficult for other developers to read, understand, and maintain. It also confuses the IDE's autocomplete features.
*   **Mitigation**: Do not overuse raw `Function` interfaces. Create custom, domain-specific `@FunctionalInterface`s with descriptive names.
    ```java
    @FunctionalInterface interface ValidatorFactory { Validator create(Condition c); }
    ```

## 3. The "Effectively Final" Restriction in Loops
*   **The Scenario**: You want to create a list of functions in a `for` loop, each capturing the loop index.
    ```java
    List<Supplier<Integer>> suppliers = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
        suppliers.add(() -> i); // ERROR!
    }
    ```
*   **The Pitfall**: Lambdas can only capture variables that are `final` or effectively final (never mutated). Because `i` is mutated in the loop (`i++`), the compiler throws an error.
*   **Mitigation**: You must create a new, effectively final variable inside the loop block to capture the current state of the iteration.
    ```java
    for (int i = 0; i < 5; i++) {
        int finalI = i; // Effectively final
        suppliers.add(() -> finalI);
    }
    ```

## 4. Performance Overhead of Deep Currying
*   **The Scenario**: You curry a function with 5 arguments: `f(a)(b)(c)(d)(e)`.
*   **The Pitfall**: Every step of the curried chain involves allocating a new lambda object on the heap and performing a virtual method dispatch (`apply()`). While the JVM is very good at optimizing this (escape analysis, inlining), deep currying in the middle of a tight, CPU-intensive loop (e.g., executing a million times a second) will generate significant garbage and degrade performance compared to a standard 5-argument method call.
*   **Mitigation**: Use currying for configuration and factory patterns (executed rarely), not for inner-loop mathematical computations.

## 5. Exception Handling Opacity
*   **The Pitfall**: When a higher-order function executes a passed-in lambda, and that lambda throws a `RuntimeException`, the stack trace can be very confusing. The exception originates inside the higher-order function's internal logic, but the actual bug is in the lambda provided by the caller. This makes debugging difficult, especially if the higher-order function catches the exception and wraps it in a generic framework error.
*   **Mitigation**: Higher-order functions should document exactly what exceptions they expect the provided functions to throw, and they should let unpredictable `RuntimeException`s bubble up without obfuscating the stack trace.
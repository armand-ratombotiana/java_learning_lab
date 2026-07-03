# Interview Preparation: Functional Error Handling

This document covers advanced questions related to the `Either` pattern, the `Try` monad, and integrating functional error handling with traditional Java APIs.

## Q1: Why is throwing Exceptions considered an anti-pattern in pure functional programming?
**Answer:**
Functional programming relies on "referential transparency"—the idea that a function call can be replaced by its return value without changing the program's behavior.
Exceptions break this. An exception acts as a hidden, alternate return path (a `GOTO` statement). It forces the caller to look outside the method signature to understand what the method might do. Furthermore, throwing an exception is a side-effect that alters the control flow of the application stack, breaking the ability to compose functions cleanly and predictably.

## Q2: How does the `Either` monad solve the problem of hidden control flow?
**Answer:**
The `Either` monad brings the error into the type signature. Instead of returning `String` and secretly throwing a `UserNotFoundException`, the method explicitly returns `Either<UserNotFoundException, String>`.
This makes the possibility of failure transparent to the caller. The caller is forced by the compiler to handle both the Success path (Right) and the Failure path (Left), usually via pattern matching, `fold()`, or monadic chaining (`map`/`flatMap`).

## Q3: What is the difference between `Either` and `Try`?
**Answer:**
*   `Either<L, R>` is a general-purpose container for two mutually exclusive types. The Left side can be *anything* (a String message, an Enum, an HTTP status code).
*   `Try<T>` is a specialized, opinionated version of `Either` designed specifically to interoperate with legacy Java code that throws exceptions. The "Left" side of a `Try` is hardcoded to be a `Throwable`. It usually provides static factory methods (like `Try.of(Supplier)`) that automatically wrap a `try-catch` block around the execution and capture the exception into the Failure state.

## Q4: Explain the concept of "Right-Bias" in the `Either` monad.
**Answer:**
Early implementations of `Either` (like in older Scala versions) were unbiased. If you called `.map()`, you had to specify whether you wanted to map the Left value or the Right value.
Modern implementations are **Right-Biased**. By convention, "Right is right" (Success), and Left is the Error. When you call `.map()` or `.flatMap()` on a right-biased `Either`, the operation automatically applies to the Right value. If the `Either` currently holds a Left value, the `map` operation is completely ignored, and the Left value simply bypasses the operation and continues down the chain.

## Q5: How do you safely escape a functional `Try` or `Either` pipeline at the boundaries of your application (e.g., in a REST Controller)?
**Answer:**
At the very edge of your architecture (like a Spring `@RestController`), you often need to convert the functional container back into an HTTP response or a standard Exception.
You use terminal operations like `fold` or `match`.
```java
// Example using fold to create an HTTP response
return processData(input).fold(
    error -> ResponseEntity.status(400).body(error.getMessage()), // Handle Left
    success -> ResponseEntity.ok(success)                         // Handle Right
);
```
This guarantees that both the error state and the success state are explicitly translated into the required boundary format.
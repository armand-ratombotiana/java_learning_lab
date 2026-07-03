# Deep Dive: Functional Error Handling

## 1. The Problem with Exceptions
In traditional Java, errors are handled by throwing Exceptions. While effective, this approach has several drawbacks in a functional programming paradigm:
*   **Breaks the Flow**: An exception acts like a `GOTO` statement. It abruptly halts the current execution path and jumps to a `catch` block, breaking the linear flow of a functional pipeline (e.g., a Stream).
*   **Hidden Control Flow**: Checked exceptions force you to handle them, but unchecked exceptions (`RuntimeException`) are invisible in the method signature. A pure function should be transparent about its possible outcomes.
*   **Side Effects**: Throwing an exception is a side effect. It mutates the state of the call stack.

Functional error handling treats errors as **data**. Instead of throwing an error, a function returns a container that holds *either* the successful result *or* the error.

## 2. The `Option` / `Maybe` Pattern
We have already seen this in Java as `java.util.Optional`. It represents a computation that might fail by returning nothing.
*   **Success**: Returns `Optional.of(data)`.
*   **Failure**: Returns `Optional.empty()`.

While great for missing data (e.g., "User not found"), `Optional` is terrible for true error handling because when it fails, it provides no information about *why* it failed.

## 3. The `Either` / `Result` Pattern
To handle actual errors functionally, we need a container that holds two possible types: a Success type (Right) and a Failure type (Left). This pattern is universally known in functional languages as `Either` or `Result`.

Java does not have a built-in `Either` type. However, it is simple to build one, or use a library like **Vavr**.

```java
// Conceptual Either implementation
public abstract class Either<L, R> {
    public static <L, R> Either<L, R> left(L value) { return new Left<>(value); }
    public static <L, R> Either<L, R> right(R value) { return new Right<>(value); }
    
    public abstract <T> Either<L, T> map(Function<? super R, ? extends T> mapper);
    public abstract <T> Either<L, T> flatMap(Function<? super R, Either<L, T>> mapper);
}
```
*   **Convention**: "Right" is right (Success). "Left" is the error.
*   **The Magic**: If you call `map()` on a `Right`, the function executes. If you call `map()` on a `Left`, the function is completely ignored, and the `Left` (the error) is simply passed down the pipeline.

## 4. The `Try` Monad
A specialized version of `Either` is the `Try` monad. It specifically handles Java Exceptions. It represents a computation that may either result in an exception or return a successfully computed value.

*   **Success**: `Success<T>` holding the value.
*   **Failure**: `Failure<T>` holding the `Throwable`.

```java
// Conceptual Try usage (similar to Vavr's Try)
Try<Integer> result = Try.of(() -> Integer.parseInt("123"))
                         .map(i -> i * 2)
                         .filter(i -> i > 100);
```
If `parseInt` throws a `NumberFormatException`, the `Try` immediately becomes a `Failure`. The subsequent `map` and `filter` operations do not execute, and the exception is safely carried to the end of the pipeline without crashing the thread.

## 5. Error Recovery
Functional containers provide elegant ways to recover from errors at the end of the pipeline without writing massive `try-catch` blocks.

*   **`getOrElse(default)`**: If success, return value. If failure, return default.
*   **`orElseGet(Supplier)`**: Lazy fallback computation.
*   **`recover(Function<Throwable, T>)`**: If failure, examine the specific exception and attempt to compute a valid fallback value based on the error type.
*   **`fold(Function<L, T>, Function<R, T>)`**: The ultimate terminal operation. You provide two functions: one to handle the error, one to handle the success. Both must return the same final type `T`. This forces the developer to handle both outcomes explicitly.
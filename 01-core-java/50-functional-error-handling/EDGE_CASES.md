# Edge Cases & Pitfalls: Functional Error Handling

Functional error handling makes code robust, but mixing it with traditional Java idioms can lead to swallowed exceptions, type erasure issues, and awkward API boundaries.

## 1. Swallowing Fatal Errors in `Try`
*   **The Scenario**: You use a `Try` monad to wrap a block of code.
    ```java
    Try.of(() -> {
        // ... some complex logic ...
        throw new OutOfMemoryError(); 
    }).recover(e -> "Fallback");
    ```
*   **The Pitfall**: A naive implementation of a `Try` monad catches *all* `Throwable`s. If the JVM throws a fatal error like `OutOfMemoryError` or `StackOverflowError`, catching it and continuing execution is extremely dangerous. The JVM is in an unstable state and should be allowed to crash.
*   **Mitigation**: A robust `Try` implementation (like the one in Vavr) distinguishes between non-fatal `Exception`s and fatal `Error`s. It catches `Exception`s but allows `Error`s to propagate up the stack and crash the thread. If you build your own `Try`, you must implement this check.

## 2. The `Either` Type Inference Trap
*   **The Scenario**: You build a method that returns an `Either<Error, String>`. You try to return a success value using a static factory method: `return Either.right("Success");`.
*   **The Pitfall**: The compiler might complain about type mismatch. `Either.right("Success")` creates an `Either<Object, String>` because the compiler has no context to infer the Left (Error) type.
*   **Mitigation**: You must provide type hints to the compiler, or use explicit casting.
    ```java
    // Option 1: Type witness
    return Either.<Error, String>right("Success");
    
    // Option 2: Rely on target typing (if returning directly)
    Either<Error, String> result = Either.right("Success");
    ```

## 3. Mixing `Optional` and Exceptions
*   **The Scenario**: You use an API that returns an `Optional`. If it's empty, you want to throw a specific business exception.
    ```java
    Optional<User> user = findUser();
    if (user.isEmpty()) { throw new UserNotFoundException(); }
    return user.get();
    ```
*   **The Pitfall**: This breaks the functional pipeline and reverts to imperative control flow. It also requires manually calling `.get()`, which is generally considered a code smell if not preceded immediately by `isPresent()`.
*   **Mitigation**: Use `orElseThrow()`. It seamlessly bridges the functional `Optional` world with the imperative Exception world at the very edge of your architecture.
    ```java
    return findUser().orElseThrow(UserNotFoundException::new);
    ```

## 4. The "Left Bias" vs "Right Bias" Confusion
*   **The Scenario**: You have an `Either` object and you call `.map()`.
*   **The Pitfall**: In some older functional libraries, `Either` is unbiased. You have to explicitly say whether you want to map the Left side or the Right side (e.g., `either.right().map(...)`).
*   **Mitigation**: Modern implementations (and the general convention) make `Either` **Right-Biased**. This means calling `.map()` automatically assumes you want to map the Success (Right) value. If you want to map the Error (Left) value to a different type of error, you must explicitly use a method like `mapLeft()`.

## 5. Overusing `Try` for Business Logic
*   **The Scenario**: You need to validate user input. You write a method that throws an `InvalidInputException`, and then you wrap the call to that method in a `Try` monad to handle the failure functionally.
*   **The Pitfall**: Exceptions should be reserved for *exceptional* circumstances (e.g., the database is down). Invalid user input is an expected, normal part of business logic. Using Exceptions (even wrapped in a `Try`) to handle standard business rules is incredibly slow due to the massive overhead of generating stack traces.
*   **Mitigation**: For business validation, use an `Either` or a specialized `Validation` monad. Return an `Either.left("Invalid Email")` directly without ever throwing or catching an Exception.
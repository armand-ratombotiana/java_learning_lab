# Mini Project: Building and Using the `Either` Monad

## Objective
Build a simplified, right-biased `Either` monad from scratch to understand its internal mechanics. Then, use it to build a robust, exception-free data parsing and validation pipeline.

## Prerequisites
*   Java 17+

## Step 1: Implement the `Either` Monad
We create a sealed interface with two implementations: `Left` (for errors) and `Right` (for success).

```java
import java.util.function.Function;

public sealed interface Either<L, R> permits Left, Right {

    // Factory methods
    static <L, R> Either<L, R> left(L value) { return new Left<>(value); }
    static <L, R> Either<L, R> right(R value) { return new Right<>(value); }

    // Core Monadic Operations
    <T> Either<L, T> map(Function<? super R, ? extends T> mapper);
    <T> Either<L, T> flatMap(Function<? super R, Either<L, T>> mapper);
    
    // Terminal Operations
    R getOrElse(R defaultValue);
    <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper);
}
```

## Step 2: Implement the `Left` (Error State)
The `Left` class ignores all `map` and `flatMap` operations, simply passing the error down the chain.

```java
import java.util.function.Function;

public final class Left<L, R> implements Either<L, R> {
    private final L value;

    public Left(L value) { this.value = value; }

    @Override
    public <T> Either<L, T> map(Function<? super R, ? extends T> mapper) {
        // Ignore the mapper, return a new Left with the same error value
        return Either.left(this.value); 
    }

    @Override
    public <T> Either<L, T> flatMap(Function<? super R, Either<L, T>> mapper) {
        return Either.left(this.value);
    }

    @Override
    public R getOrElse(R defaultValue) {
        return defaultValue; // Return the fallback
    }

    @Override
    public <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper) {
        return leftMapper.apply(this.value); // Apply the error handler
    }
}
```

## Step 3: Implement the `Right` (Success State)
The `Right` class executes the mapping functions and wraps the results.

```java
import java.util.function.Function;

public final class Right<L, R> implements Either<L, R> {
    private final R value;

    public Right(R value) { this.value = value; }

    @Override
    public <T> Either<L, T> map(Function<? super R, ? extends T> mapper) {
        // Apply the function and wrap the result in a new Right
        return Either.right(mapper.apply(this.value)); 
    }

    @Override
    public <T> Either<L, T> flatMap(Function<? super R, Either<L, T>> mapper) {
        // Apply the function (which returns an Either) and return it directly (flattening)
        return mapper.apply(this.value);
    }

    @Override
    public R getOrElse(R defaultValue) {
        return this.value; // Return the actual value
    }

    @Override
    public <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper) {
        return rightMapper.apply(this.value); // Apply the success handler
    }
}
```

## Step 4: Build the Processing Pipeline
We will parse a string into an integer, validate it, and format it. Notice that NO exceptions are thrown or caught in the business logic.

```java
public class Pipeline {

    // Step 1: Parse the string safely
    public static Either<String, Integer> parseAge(String input) {
        try {
            return Either.right(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            return Either.left("Invalid number format: " + input);
        }
    }

    // Step 2: Validate the integer (Returns a container, so we must use flatMap later)
    public static Either<String, Integer> validateAdult(int age) {
        if (age >= 18) {
            return Either.right(age);
        } else {
            return Either.left("Age is strictly under 18: " + age);
        }
    }

    public static void process(String input) {
        System.out.println("Processing input: '" + input + "'");

        // The Monadic Pipeline
        String result = parseAge(input)
                .flatMap(Pipeline::validateAdult)      // flatMap because validateAdult returns an Either
                .map(age -> "Valid Adult Age: " + age) // map because we are just transforming the value
                .fold(
                    error -> "ERROR: " + error,        // Handle the Left (Failure)
                    success -> "SUCCESS: " + success   // Handle the Right (Success)
                );

        System.out.println(result + "\n");
    }
}
```

## Step 5: Test the Application
```java
public class Main {
    public static void main(String[] args) {
        // 1. Happy Path
        Pipeline.process("25");

        // 2. Fails Validation (Short-circuits at flatMap)
        Pipeline.process("15");

        // 3. Fails Parsing (Short-circuits immediately, map and flatMap are ignored)
        Pipeline.process("twenty-five");
    }
}
```

## Expected Output
```text
Processing input: '25'
SUCCESS: Valid Adult Age: 25

Processing input: '15'
ERROR: Age is strictly under 18: 15

Processing input: 'twenty-five'
ERROR: Invalid number format: twenty-five
```
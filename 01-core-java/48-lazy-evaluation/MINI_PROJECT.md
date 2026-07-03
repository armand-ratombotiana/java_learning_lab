# Mini Project: The Fibonacci Generator & Memoizer

## Objective
Build a program that generates the Fibonacci sequence using an infinite, lazy Java Stream. Then, implement a thread-safe `Memoizer` utility to cache the results of expensive operations, demonstrating the difference between raw thunks and memoized thunks.

## Prerequisites
*   Java 17+

## Step 1: Infinite Lazy Stream (Fibonacci)
We will use `Stream.iterate` to generate an infinite sequence of Fibonacci numbers. Because it's lazy, it won't crash the JVM.

```java
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;

public class FibonacciGenerator {
    
    // A record to hold the current and next number in the sequence
    private record FibState(BigInteger current, BigInteger next) {}

    public static Stream<BigInteger> generate() {
        // Stream.iterate(seed, unaryOperator)
        return Stream.iterate(
            new FibState(BigInteger.ZERO, BigInteger.ONE),
            state -> new FibState(state.next(), state.current().add(state.next()))
        ).map(FibState::current);
    }
}
```

## Step 2: The Thread-Safe Memoizer
Create a utility class that wraps a `Supplier`. It will execute the supplier exactly once, cache the result, and return the cached result on all subsequent calls. We use Double-Checked Locking to make it thread-safe.

```java
import java.util.function.Supplier;

public class Memoizer<T> implements Supplier<T> {
    private final Supplier<T> delegate;
    
    // volatile ensures memory visibility across threads
    private volatile T value;
    private volatile boolean initialized = false;

    public Memoizer(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    public static <T> Supplier<T> memoize(Supplier<T> delegate) {
        return new Memoizer<>(delegate);
    }

    @Override
    public T get() {
        // 1st Check (Fast path, no locking)
        if (!initialized) {
            // Lock only on first access
            synchronized (this) {
                // 2nd Check (Inside lock to prevent race conditions)
                if (!initialized) {
                    System.out.println(">>> Memoizer: Executing expensive computation... <<<");
                    value = delegate.get();
                    initialized = true;
                }
            }
        }
        return value;
    }
}
```

## Step 3: Test the Implementations
Create a `Main` class to pull data from the infinite stream and compare a raw `Supplier` against our `Memoizer`.

```java
import java.math.BigInteger;
import java.util.List;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- 1. Infinite Lazy Stream ---");
        
        // We have an infinite stream, but we bound it using .limit() BEFORE collecting.
        List<BigInteger> first10Fib = FibonacciGenerator.generate()
            .limit(10)
            .toList();
            
        System.out.println("First 10 Fibonacci numbers: " + first10Fib);

        // Find the 100th Fibonacci number
        BigInteger fib100 = FibonacciGenerator.generate()
            .skip(99)
            .findFirst()
            .orElseThrow();
            
        System.out.println("100th Fibonacci number: " + fib100);

        System.out.println("\n--- 2. Raw Supplier vs Memoized Supplier ---");
        
        // A simulated expensive operation
        Supplier<String> expensiveTask = () -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            return "Database Result";
        };

        System.out.println("Testing Raw Supplier (Called 3 times):");
        long start = System.currentTimeMillis();
        System.out.println(expensiveTask.get());
        System.out.println(expensiveTask.get());
        System.out.println(expensiveTask.get());
        System.out.println("Time: " + (System.currentTimeMillis() - start) + "ms");

        System.out.println("\nTesting Memoized Supplier (Called 3 times):");
        Supplier<String> memoizedTask = Memoizer.memoize(expensiveTask);
        
        start = System.currentTimeMillis();
        System.out.println(memoizedTask.get());
        System.out.println(memoizedTask.get());
        System.out.println(memoizedTask.get());
        System.out.println("Time: " + (System.currentTimeMillis() - start) + "ms");
    }
}
```

## Expected Output
Notice that the Raw Supplier takes 3 seconds (executing the sleep 3 times), while the Memoized Supplier takes 1 second (executing the sleep only once).
```text
--- 1. Infinite Lazy Stream ---
First 10 Fibonacci numbers: [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]
100th Fibonacci number: 218922995834555169026

--- 2. Raw Supplier vs Memoized Supplier ---
Testing Raw Supplier (Called 3 times):
Database Result
Database Result
Database Result
Time: 3015ms

Testing Memoized Supplier (Called 3 times):
>>> Memoizer: Executing expensive computation... <<<
Database Result
Database Result
Database Result
Time: 1005ms
```
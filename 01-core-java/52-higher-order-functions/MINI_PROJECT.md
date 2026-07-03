# Mini Project: Dynamic Retry & Logging Framework

## Objective
Build a higher-order function framework that takes any arbitrary function and wraps it with dynamic behavior (Retry logic and Logging). This demonstrates passing functions as parameters, returning functions, and partial application.

## Prerequisites
*   Java 17+

## Step 1: Define the Domain
We need a functional interface that represents an operation that might fail (throw an exception). Standard `Function` doesn't allow checked exceptions.

```java
@FunctionalInterface
public interface CheckedFunction<T, R> {
    R apply(T t) throws Exception;
}
```

## Step 2: The Higher-Order Functions (Decorators)
We will create static methods that take a `CheckedFunction` and return a *new* `CheckedFunction` wrapped with extra behavior.

```java
public class FunctionDecorators {

    // 1. Higher-Order Function: Adds Logging
    public static <T, R> CheckedFunction<T, R> withLogging(String operationName, CheckedFunction<T, R> function) {
        // Returns a new function (Closure capturing 'operationName' and 'function')
        return input -> {
            System.out.println("[LOG] Starting " + operationName + " with input: " + input);
            long start = System.currentTimeMillis();
            
            try {
                R result = function.apply(input); // Execute the original function
                long duration = System.currentTimeMillis() - start;
                System.out.println("[LOG] " + operationName + " succeeded in " + duration + "ms. Result: " + result);
                return result;
            } catch (Exception e) {
                System.err.println("[LOG] " + operationName + " FAILED: " + e.getMessage());
                throw e; // Rethrow
            }
        };
    }

    // 2. Higher-Order Function: Adds Retry Logic
    public static <T, R> CheckedFunction<T, R> withRetry(int maxRetries, CheckedFunction<T, R> function) {
        return input -> {
            int attempts = 0;
            while (true) {
                try {
                    attempts++;
                    return function.apply(input);
                } catch (Exception e) {
                    if (attempts >= maxRetries) {
                        System.err.println("[RETRY] Max retries (" + maxRetries + ") reached. Giving up.");
                        throw e;
                    }
                    System.out.println("[RETRY] Attempt " + attempts + " failed. Retrying...");
                    Thread.sleep(500); // Backoff
                }
            }
        };
    }
}
```

## Step 3: Currying and Partial Application
Let's create a tax calculator to demonstrate partial application.

```java
import java.util.function.BiFunction;
import java.util.function.Function;

public class TaxCalculators {
    
    // A standard BiFunction
    public static final BiFunction<Double, Double, Double> calculateTax = 
        (amount, taxRate) -> amount + (amount * taxRate);

    // Partial Application: We fix the 'taxRate' argument and return a new function 
    // that only requires the 'amount'.
    public static Function<Double, Double> createRegionalCalculator(double taxRate) {
        return amount -> calculateTax.apply(amount, taxRate);
    }
}
```

## Step 4: Execute the Framework
Combine everything in the main class. We will wrap a flaky network call with both logging and retries.

```java
public class Main {
    
    // A simulated flaky function
    public static String fetchUserData(int userId) throws Exception {
        if (Math.random() < 0.7) { // 70% chance to fail
            throw new RuntimeException("Network Timeout");
        }
        return "User_Data_For_ID_" + userId;
    }

    public static void main(String[] args) {
        System.out.println("--- 1. Testing Higher-Order Decorators ---");
        
        // Wrap the method reference in our decorators!
        // Notice the composition: withLogging( withRetry( fetchUserData ) )
        CheckedFunction<Integer, String> robustFetch = 
            FunctionDecorators.withLogging("FetchUserAPI", 
                FunctionDecorators.withRetry(3, Main::fetchUserData)
            );

        try {
            // Execute the fully decorated function
            String result = robustFetch.apply(101);
            System.out.println("Final Output: " + result);
        } catch (Exception e) {
            System.out.println("Final Output: Operation Aborted.");
        }

        System.out.println("\n--- 2. Testing Partial Application ---");
        
        // Create specific calculators by partially applying the tax rate
        Function<Double, Double> nyTax = TaxCalculators.createRegionalCalculator(0.08); // 8%
        Function<Double, Double> ukTax = TaxCalculators.createRegionalCalculator(0.20); // 20%

        System.out.println("100.0 with NY Tax: " + nyTax.apply(100.0));
        System.out.println("100.0 with UK Tax: " + ukTax.apply(100.0));
    }
}
```

## Expected Output
Notice how the logging wraps the entire retry process. If you swapped the order of the decorators, it would log every single retry attempt instead.
```text
--- 1. Testing Higher-Order Decorators ---
[LOG] Starting FetchUserAPI with input: 101
[RETRY] Attempt 1 failed. Retrying...
[RETRY] Attempt 2 failed. Retrying...
[LOG] FetchUserAPI succeeded in 1005ms. Result: User_Data_For_ID_101
Final Output: User_Data_For_ID_101

--- 2. Testing Partial Application ---
100.0 with NY Tax: 108.0
100.0 with UK Tax: 120.0
```
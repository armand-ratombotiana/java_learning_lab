# Edge Cases & Pitfalls: Lazy Evaluation

Lazy evaluation defers execution, which means it also defers errors. This can make debugging incredibly difficult and lead to unexpected resource exhaustion.

## 1. The `orElse` vs `orElseGet` Trap
*   **The Scenario**: You use `Optional` to fetch data from a cache, and fall back to a database if the cache is empty.
    ```java
    Optional<User> user = cache.get(id);
    return user.orElse(database.fetch(id)); // DANGER!
    ```
*   **The Pitfall**: `orElse()` takes a *value*, not a function. In Java, method arguments are evaluated eagerly before the method is called. This means `database.fetch(id)` is executed *every single time*, regardless of whether the cache hit or missed. If it's a cache hit, the database result is simply thrown away. This destroys the performance benefit of the cache.
*   **Mitigation**: Always use `orElseGet(Supplier)` when the fallback operation is expensive or has side effects.
    ```java
    return user.orElseGet(() -> database.fetch(id)); // Safe. Lazy evaluation.
    ```

## 2. Infinite Stream `OutOfMemoryError`
*   **The Scenario**: You create an infinite stream and attempt to collect it or sort it without a bounding limit.
    ```java
    Stream.iterate(1, n -> n + 1)
          .filter(n -> n % 2 == 0)
          .collect(Collectors.toList()); // DANGER!
    ```
*   **The Pitfall**: The stream will generate numbers infinitely, filter them, and attempt to add them to the list. The list will grow until the JVM runs out of heap memory and crashes.
*   **Mitigation**: Always apply a short-circuiting operator (like `limit()`, `findFirst()`, or `takeWhile()`) to an infinite stream *before* applying a stateful terminal operation (like `collect()`, `sorted()`, or `reduce()`).

## 3. The Deferred Exception
*   **The Scenario**: You create a `Supplier` that contains code which might throw a `RuntimeException`. You pass this supplier to another part of the system.
    ```java
    Supplier<Integer> badMath = () -> 10 / 0;
    System.out.println("Supplier created successfully.");
    // ... much later in the code ...
    badMath.get(); // Crash!
    ```
*   **The Pitfall**: Because evaluation is lazy, the exception is not thrown when the flawed logic is defined. It is thrown when the value is requested (`get()`). If the `Supplier` is passed across multiple layers of your architecture, the stack trace will show the exception originating from the consumer of the data, not the producer, making it very difficult to trace the origin of the bad logic.
*   **Mitigation**: Be extremely careful with side-effects and exceptions inside lazy computations (Thunks). Validate inputs eagerly before wrapping them in a `Supplier` if possible.

## 4. Un-Memoized Thunks (Performance Degradation)
*   **The Scenario**: You wrap a heavy database query in a `Supplier` and pass it to a view rendering engine. The view engine accesses the supplier 5 times to render different parts of the page.
*   **The Pitfall**: A raw `Supplier` in Java is just a function. It does not cache its result. Every time `supplier.get()` is called, the database query executes again. 5 calls = 5 queries.
*   **Mitigation**: You must wrap the `Supplier` in a custom `Memoizer` or `Lazy` class (as shown in the Deep Dive) that caches the result after the first execution and returns the cached value on subsequent calls.

## 5. Capturing Mutable State
*   **The Scenario**: You create a lazy computation that captures an array from the enclosing scope.
    ```java
    int[] config = {10, 20};
    Supplier<Integer> compute = () -> config[0] * 5;
    config[0] = 99; // Mutated!
    System.out.println(compute.get()); // Prints 495, not 50.
    ```
*   **The Pitfall**: The lambda captures the *reference* to the array, not a snapshot of its values. Because execution is delayed, if the external state is mutated before `get()` is called, the lazy computation will use the mutated state, leading to unpredictable results.
*   **Mitigation**: Lambdas should only capture true constants (`final` primitives or immutable objects). If you must capture an array or collection, capture a defensive copy of it.
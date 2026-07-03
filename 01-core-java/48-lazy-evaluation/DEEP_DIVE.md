# Deep Dive: Lazy Evaluation

## 1. Eager vs. Lazy Evaluation
In traditional Java (imperative programming), expressions are evaluated **eagerly**. 
When you write `int x = computeHeavyMath()`, the JVM stops, executes the entire `computeHeavyMath()` method, stores the result in `x`, and then moves to the next line. If `x` is never actually used later in the program, that CPU time was completely wasted.

**Lazy Evaluation** (or call-by-need) is a strategy where the evaluation of an expression is delayed until its value is actually required.

## 2. Thunks (Simulating Laziness in Java)
Java is fundamentally an eager language. To simulate laziness, we wrap the computation inside a functional interface (like `Supplier<T>`). This wrapped computation is often called a **Thunk**.

```java
// Eager: compute() runs immediately
String result = compute(); 
System.out.println("Ready");

// Lazy: compute() does NOT run here. We just store the instructions on how to compute it.
Supplier<String> lazyResult = () -> compute(); 
System.out.println("Ready");
// compute() only runs when we explicitly ask for it
System.out.println(lazyResult.get()); 
```

## 3. Memoization
A pure lazy evaluation system evaluates the expression the first time it is needed, and then *caches* the result. Subsequent requests for the value return the cached result instantly without recomputing. This is called **Memoization**.

Because Java's `Supplier` evaluates the lambda *every time* `get()` is called, we must build a custom wrapper to achieve true memoization.

```java
public class Lazy<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private T value;
    private boolean isEvaluated = false;

    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (!isEvaluated) {
            value = supplier.get(); // Compute once
            isEvaluated = true;
        }
        return value; // Return cached value
    }
}
```
*(Note: For a multi-threaded environment, the `get()` method above would need synchronization or Double-Checked Locking to prevent the supplier from being executed multiple times).*

## 4. Infinite Streams
The most powerful application of lazy evaluation in Java is the `java.util.stream.Stream` API.
Because streams are lazy (intermediate operations like `map` and `filter` don't execute until a terminal operation like `collect` is called), they can represent **infinite** data structures.

You cannot create a `List` of all positive integers; you would run out of memory. But you *can* create a `Stream` of all positive integers.

```java
// Iterate: Start at 0, apply n -> n + 2 infinitely
Stream<Integer> evenNumbers = Stream.iterate(0, n -> n + 2);

// Generate: Call the supplier infinitely
Stream<Double> randomNumbers = Stream.generate(Math::random);
```

### Bounding Infinite Streams
If you call a terminal operation like `forEach` on an infinite stream, it will run forever. You must bound the stream using short-circuiting operations:
*   `limit(n)`: Takes the first `n` elements and stops.
*   `takeWhile(Predicate)` (Java 9+): Takes elements as long as the condition is true, then stops.
*   `findFirst()`, `anyMatch()`: Stop as soon as a result is found.

```java
// Get the first 10 even numbers. Only 10 numbers are ever computed!
List<Integer> firstTenEvens = Stream.iterate(0, n -> n + 2)
                                    .limit(10)
                                    .toList();
```

## 5. Short-Circuiting Logic
Java uses lazy evaluation inherently in its logical operators (`&&` and `||`).
In the expression `if (A() && B())`, if `A()` returns `false`, `B()` is *never executed*. The JVM short-circuits the evaluation because the final result is already known.

You can apply this concept functionally using `Optional.orElseGet()`.
```java
// Eager: computeDefault() runs EVERY TIME, even if value is present!
String s1 = Optional.of("Data").orElse(computeDefault()); 

// Lazy: computeDefault() ONLY runs if the Optional is empty.
String s2 = Optional.of("Data").orElseGet(() -> computeDefault()); 
```
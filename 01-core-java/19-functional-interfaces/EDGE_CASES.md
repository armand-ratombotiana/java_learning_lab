# Edge Cases & Pitfalls: Functional Interfaces

Functional interfaces and lambdas simplify code, but they introduce subtle edge cases regarding scoping, exception handling, and method resolution.

## 1. Exception Handling in Lambdas
*   **The Scenario**: You want to use a lambda that calls a method throwing a checked exception (e.g., `Thread.sleep()` or `Files.readAllLines()`).
*   **The Pitfall**: The standard functional interfaces in `java.util.function` (like `Consumer`, `Function`) do *not* declare a `throws Exception` clause on their abstract methods. Therefore, you cannot throw a checked exception directly from a lambda assigned to them.
    ```java
    List<String> files = Arrays.asList("a.txt", "b.txt");
    // ERROR: Unhandled exception: java.io.IOException
    files.forEach(f -> Files.readAllLines(Paths.get(f))); 
    ```
*   **Mitigation**: 
    1.  Wrap the checked exception in a `RuntimeException` inside the lambda using a try-catch block (ugly).
    2.  Write a custom functional interface that declares `throws Exception`.
    3.  Use a wrapper utility method (like a "SneakyThrows" wrapper) to encapsulate the try-catch logic.

## 2. Variable Capture (Effectively Final)
*   **The Scenario**: You want to use a local variable from the enclosing scope inside a lambda.
*   **The Pitfall**: Lambdas can only capture local variables that are `final` or *effectively final* (never modified after initialization). If you try to increment a counter inside a lambda, the compiler will throw an error.
    ```java
    int count = 0;
    List<String> names = Arrays.asList("Alice", "Bob");
    // ERROR: Variable used in lambda expression should be final or effectively final
    names.forEach(n -> { count++; System.out.println(n); }); 
    ```
*   **Mitigation**: If you need to mutate state from inside a lambda, the variable must be an object reference (e.g., an `AtomicInteger` or a 1-element array). However, mutating state inside a lambda is often a code smell; prefer using Stream reductions (like `count()`) instead.

## 3. Ambiguous Method Overloading
*   **The Scenario**: You have a class with two overloaded methods: one takes a `Callable<T>` (from `java.util.concurrent`, returns `T`, throws Exception) and one takes a `Supplier<T>` (returns `T`, no exception).
    ```java
    public void execute(Supplier<String> s) { ... }
    public void execute(Callable<String> c) { ... }
    ```
*   **The Pitfall**: If you call `execute(() -> "Hello")`, the compiler gets confused. Both `Supplier` and `Callable` are functional interfaces that take no arguments and return a value. The compiler throws an "Ambiguous method call" error.
*   **Mitigation**: You must cast the lambda to explicitly provide the target type: `execute((Supplier<String>) () -> "Hello")`.

## 4. `Object` Methods in Functional Interfaces
*   **The Scenario**: You write an interface with one abstract method, but you also declare an abstract method that overrides a public method from `java.lang.Object`.
    ```java
    @FunctionalInterface
    public interface MyInterface {
        void doWork();
        boolean equals(Object obj); // Abstract!
    }
    ```
*   **The Pitfall**: You might think this has two abstract methods and breaks the SAM rule. However, it *is* a valid functional interface. Methods that override public methods from `Object` do *not* count towards the single abstract method limit.

## 5. Memory Leaks with Method References
*   **The Pitfall**: When you create an instance method reference (e.g., `myObject::doSomething`), the resulting functional interface instance holds a strong reference to `myObject`. If this functional interface is passed to a long-lived registry (like an event listener), it will prevent `myObject` from being garbage collected, causing a memory leak.
*   **Mitigation**: Be mindful of the lifecycle of the objects captured by lambdas and method references. Unregister them when they are no longer needed.
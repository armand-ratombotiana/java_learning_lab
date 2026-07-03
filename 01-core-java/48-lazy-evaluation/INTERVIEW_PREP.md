# Interview Preparation: Lazy Evaluation

This document covers advanced questions related to eager vs lazy evaluation, infinite streams, and memoization.

## Q1: What is the difference between Eager and Lazy evaluation? Give an example of each in Java.
**Answer:**
*   **Eager Evaluation**: An expression is evaluated as soon as it is bound to a variable. This is the default in Java. Example: `int x = expensiveComputation();`. The method runs immediately, even if `x` is never used.
*   **Lazy Evaluation**: An expression is not evaluated until its value is explicitly required. Example: `Supplier<Integer> x = () -> expensiveComputation();`. The method does not run until `x.get()` is called. Java `Stream` intermediate operations (`filter`, `map`) are also lazy; they don't execute until a terminal operation (`collect`, `findFirst`) is invoked.

## Q2: Explain the difference between `Optional.orElse()` and `Optional.orElseGet()`. When must you use `orElseGet()`?
**Answer:**
Because Java method arguments are evaluated eagerly, `Optional.orElse(computeDefault())` will execute `computeDefault()` *every single time* the code runs, regardless of whether the `Optional` is empty or full. If the `Optional` is full, the result of `computeDefault()` is simply discarded.
`Optional.orElseGet(Supplier)` takes a lambda. It only executes the lambda if the `Optional` is actually empty.
You **must** use `orElseGet()` when the default computation is expensive (e.g., a database call) or has side effects (e.g., writing to a log or inserting a new record).

## Q3: How is it possible to create an infinite stream in Java without causing an `OutOfMemoryError`?
**Answer:**
Infinite streams (like `Stream.iterate(0, n -> n + 1)`) are possible because Java Streams are lazy. The `iterate` function does not attempt to generate all the numbers upfront and store them in memory. It only generates a number when a downstream operation specifically requests it.
As long as you apply a short-circuiting operator (like `limit(10)` or `findFirst()`) before calling a stateful terminal operation (like `collect(Collectors.toList())`), the stream will only generate the exact number of elements required to satisfy the short-circuit condition, keeping memory usage minimal.

## Q4: What is a "Thunk", and how does it relate to Memoization?
**Answer:**
A **Thunk** is a parameterless closure used to delay the evaluation of an expression (e.g., a `Supplier` in Java).
By itself, a Thunk evaluates its expression *every time* it is called. If `supplier.get()` is called 5 times, the computation runs 5 times.
**Memoization** is an optimization technique where a Thunk evaluates its expression the *first* time it is called, caches the result, and then returns the cached result on all subsequent calls. This turns a simple lazy evaluation into an efficient, "compute-once" operation.

## Q5: What is the danger of capturing mutable state inside a lazy computation?
**Answer:**
Because a lazy computation (like a lambda passed to a `Supplier` or a Stream `map`) is executed at some unknown point in the future, it relies on the state of the variables it captured at the time of creation.
If it captures a mutable object (like an array or an `ArrayList`), and that object is modified by another part of the program *before* the lazy computation is finally evaluated, the computation will use the mutated, unexpected state. This leads to extremely difficult-to-debug logic errors. Lambdas should ideally only capture `final` or effectively final immutable values.
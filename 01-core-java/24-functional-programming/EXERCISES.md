# Comprehensive Functional Programming Exercises

This document provides a structured catalog of exercises designed to master functional programming paradigms in modern Java. It progresses from foundational concepts to advanced functional techniques.

## Part 1: Core Functional Paradigms

### 1. Lambda Expressions & Method References
1. **Lambda Conversion:** Convert standard anonymous inner classes (e.g., `Runnable`, `Comparator`, `Callable`) into clean, single-line lambda expressions.
2. **Method References:** Refactor lambdas using static method references (`Class::staticMethod`), instance method references (`instance::method`), and constructor references (`Class::new`).
3. **Custom Sorting:** Use `Comparator.comparing()`, `thenComparing()`, and `reversed()` with lambdas to sort complex collections of domain objects.

### 2. Standard Functional Interfaces
1. **Predicates:** Create and combine `Predicate<T>` interfaces using `.and()`, `.or()`, and `.negate()` for complex filtering logic.
2. **Functions:** Chain `Function<T, R>` interfaces using `.andThen()` and `.compose()` to build data transformation pipelines.
3. **Consumers & Suppliers:** Implement `Consumer<T>` for side-effects (e.g., logging) and `Supplier<T>` for lazy evaluation or factory patterns.
4. **Bi-Interfaces:** Utilize `BiFunction`, `BiPredicate`, and `BiConsumer` for operations requiring two inputs.

### 3. The Stream API Deep Dive
1. **Intermediate Operations:** 
    * `filter()`: Extract elements based on conditions.
    * `map()` / `mapToInt()`: Transform objects to other types or primitives.
    * `flatMap()`: Flatten nested collections or lists of lists into a single continuous stream.
    * `distinct()`, `sorted()`, `peek()`, `limit()`, `skip()`.
2. **Terminal Operations:** 
    * `collect()`: Gather results into Lists, Sets, or Maps.
    * `reduce()`: Aggregate stream elements into a single summary result (e.g., sum, product, max).
    * `anyMatch()`, `allMatch()`, `noneMatch()`: Short-circuiting boolean operations.
    * `findFirst()`, `findAny()`: Returning `Optional`.

### 4. Advanced Collectors
1. **Grouping:** Use `Collectors.groupingBy()` to categorize data into a `Map<K, List<V>>`.
2. **Partitioning:** Use `Collectors.partitioningBy()` to split data into two groups based on a predicate (`Map<Boolean, List<V>>`).
3. **Downstream Collectors:** Combine grouping with downstream collectors like `Collectors.counting()`, `Collectors.summingInt()`, `Collectors.mapping()`, or `Collectors.maxBy()`.
4. **Custom Collectors:** Implement the `Collector` interface from scratch to define a custom accumulation strategy.

## Part 2: Advanced Functional Concepts

### 5. Mastering `Optional`
1. **Null-Safety:** Replace standard `if (obj != null)` blocks with `Optional.ofNullable()`.
2. **Safe Extraction:** Chain `.map()` and `.flatMap()` on Optionals to safely extract nested properties without throwing `NullPointerException`.
3. **Fallbacks:** Use `.orElse()`, `.orElseGet()` (for lazy evaluation), and `.orElseThrow()` to handle absent values gracefully.
4. **Stream Integration:** Filter streams of Optionals to only process present values using Java 9's `Optional.stream()`.

### 6. Functional Design Patterns
1. **Strategy Pattern:** Implement strategies using lambda expressions instead of creating multiple concrete class implementations.
2. **Command Pattern:** Execute different commands by passing behavior (lambdas) as arguments.
3. **Execute Around Method:** Use a functional interface to wrap setup and teardown code around a block of business logic (e.g., safe resource management).
4. **Chain of Responsibility:** Build a processing chain by composing `UnaryOperator<T>` instances.

### 7. Pure Functions & Immutability
1. **Immutability Basics:** Design domain objects using Java 14+ `record` classes or `final` fields to ensure thread safety.
2. **Pure Functions:** Write methods that have no side effects, rely entirely on inputs, and return consistent outputs.
3. **Referential Transparency:** Refactor legacy code to replace shared mutable state with pure function transformations.

### 8. Parallel Streams & Concurrency
1. **Parallel Execution:** Convert a large data processing task to use `.parallelStream()` and measure performance differences.
2. **Thread-Safety:** Identify and fix issues caused by modifying shared state (side effects) within a parallel stream pipeline.
3. **Stateful vs. Stateless:** Analyze operations like `distinct()` or `sorted()` to understand their overhead in parallel execution.

## Part 3: Real-World Scenarios

### 9. Data Processing Pipeline
**Scenario:** Build a comprehensive pipeline that parses a large CSV file of user data, cleans the data, filters invalid records, groups users by demographics, and generates an aggregate statistical report using Streams and Advanced Collectors.

### 10. Reactive Principles Basics
**Scenario:** Simulate an asynchronous, event-driven process. Implement callbacks and promise-like structures using `CompletableFuture`, combining it with the functional techniques learned above to handle async results cleanly.
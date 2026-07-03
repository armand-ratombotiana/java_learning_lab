# Module 10: Lambda Expressions - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the underlying implementation of a Lambda Expression in Java?
**Answer**:
Unlike anonymous inner classes (which generate a new `.class` file on disk for every instance, like `MyClass$1.class`), lambda expressions do **not** create separate `.class` files.
Instead, the Java compiler uses the `invokedynamic` bytecode instruction introduced in Java 7. The lambda body is translated into a private static (or instance) method in the host class. At runtime, `invokedynamic` calls a `CallSite` that links the lambda to the target functional interface dynamically, significantly reducing memory footprint and class-loading overhead.

### Q2: What does it mean for a variable to be "effectively final" in the context of variable capture?
**Answer**:
When a lambda expression accesses a local variable from its enclosing scope, that variable must be "effectively final." This means its value is assigned exactly once and never modified afterward. If you attempt to reassign the variable inside or outside the lambda, the compiler will throw an error. This restriction exists because local variables live on the thread stack and are destroyed when the method exits, but lambdas might execute asynchronously later. The JVM makes a copy of the variable for the lambda, so allowing mutation would lead to data inconsistency.

### Q3: Describe the four main categories of built-in functional interfaces in `java.util.function`.
**Answer**:
1. **Predicate<T>**: Takes an argument `T` and returns a `boolean` (method: `test`). Used for filtering.
2. **Function<T, R>**: Takes an argument `T` and returns a result `R` (method: `apply`). Used for mapping/transforming.
3. **Consumer<T>**: Takes an argument `T` and returns `void` (method: `accept`). Used for performing side effects (e.g., printing).
4. **Supplier<T>**: Takes no arguments and returns a result `T` (method: `get`). Used for lazy generation or instantiation.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Refactoring to Method References
**Problem**: Given a list of strings, filter out the empty ones, convert the remaining to uppercase, and print them. Initially write it using lambdas, then refactor it using Method References where possible.

**Solution**:
```java
// With Lambdas
List<String> list = Arrays.asList("a", "", "b");
list.stream()
    .filter(s -> !s.isEmpty())
    .map(s -> s.toUpperCase())
    .forEach(s -> System.out.println(s));

// Refactored with Method References
list.stream()
    .filter(s -> !s.isEmpty()) // Cannot easily use method ref here because of the negation
    .map(String::toUpperCase)
    .forEach(System.out::println);

// To fully use method references for the filter, use a helper or Predicate.not
list.stream()
    .filter(Predicate.not(String::isEmpty))
    .map(String::toUpperCase)
    .forEach(System.out::println);
```

### Scenario 2: Custom Collector
**Problem**: Write a custom mechanism using streams to concatenate a list of strings with a comma separator, without using the built-in `Collectors.joining(",")`.

**Solution**:
Use the `reduce` terminal operation.
```java
List<String> words = Arrays.asList("Java", "is", "awesome");

String result = words.stream()
    .reduce((a, b) -> a + "," + b)
    .orElse("");

System.out.println(result); // "Java,is,awesome"
```
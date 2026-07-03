# Deep Dive: Functional Composition

## 1. Beyond Simple Lambdas
In previous modules, we learned about standard functional interfaces (`Function`, `Predicate`, `Consumer`). While passing a single lambda to a method is useful, the true power of functional programming lies in **Composition**: building complex workflows by chaining together simple, single-purpose functions.

## 2. Function Composition in Java
The `java.util.function` package provides default methods specifically for composing functions.

### `Function<T, R>` Composition
*   **`andThen(Function after)`**: Executes the current function first, then passes its result to the `after` function. (Left-to-Right execution).
*   **`compose(Function before)`**: Executes the `before` function first, then passes its result to the current function. (Right-to-Left execution).

```java
Function<Integer, Integer> multiplyByTwo = x -> x * 2;
Function<Integer, Integer> addThree = x -> x + 3;

// (x * 2) + 3
Function<Integer, Integer> mathPipeline1 = multiplyByTwo.andThen(addThree);
System.out.println(mathPipeline1.apply(5)); // Output: 13

// (x + 3) * 2
Function<Integer, Integer> mathPipeline2 = multiplyByTwo.compose(addThree);
System.out.println(mathPipeline2.apply(5)); // Output: 16
```

### `Predicate<T>` Composition
*   **`and(Predicate other)`**: Logical AND.
*   **`or(Predicate other)`**: Logical OR.
*   **`negate()`**: Logical NOT.

```java
Predicate<String> isNotNull = s -> s != null;
Predicate<String> isNotEmpty = s -> !s.isEmpty();
Predicate<String> startsWithA = s -> s.startsWith("A");

Predicate<String> isValidAString = isNotNull.and(isNotEmpty).and(startsWithA);
```

## 3. Functors
A **Functor** is a design pattern from category theory. In practical Java terms, a Functor is any object that acts as a "container" for a value and provides a `map` operation to transform the value inside the container without altering the container's structure.

*   **The Rule**: `map(Function<T, R>)` takes a function, unwraps the value `T`, applies the function to get `R`, and puts `R` back into a new container of the same type.
*   **Examples in Java**: `Optional<T>`, `Stream<T>`, `CompletableFuture<T>`.

```java
Optional<String> name = Optional.of("Alice");
// Optional is a Functor. It unwraps "Alice", calls length(), and wraps the int 5 in a new Optional.
Optional<Integer> length = name.map(String::length); 
```

## 4. Monads
A **Monad** is a Functor with superpowers. While a Functor has `map`, a Monad also has `flatMap` (often called `bind` in other languages).

### The Problem `flatMap` Solves
What happens if the mapping function itself returns a container?
```java
Function<String, Optional<Integer>> parseAge = s -> {
    try { return Optional.of(Integer.parseInt(s)); } 
    catch (Exception e) { return Optional.empty(); }
};

Optional<String> input = Optional.of("25");

// Using map() results in nested containers: Optional<Optional<Integer>>
Optional<Optional<Integer>> nested = input.map(parseAge); 
```
Nested containers are notoriously difficult to work with. You have to unwrap them twice.

### The Monad Solution
A Monad provides `flatMap()`. It applies the function (which returns a container), but then it "flattens" the result so you don't get nested containers.

```java
// Using flatMap() flattens the result: Optional<Integer>
Optional<Integer> flat = input.flatMap(parseAge);
```

### The Three Laws of Monads
To be a true Monad, a structure must obey three mathematical laws:
1.  **Left Identity**: `Monad.of(x).flatMap(f) == f.apply(x)`
2.  **Right Identity**: `m.flatMap(Monad::of) == m`
3.  **Associativity**: `m.flatMap(f).flatMap(g) == m.flatMap(x -> f.apply(x).flatMap(g))`

Java's `Optional`, `Stream`, and `CompletableFuture` generally follow these laws, making them Monads. By understanding Monads, you can chain together complex sequences of operations that might fail (`Optional`) or take time (`CompletableFuture`) in a clean, linear, and declarative way.
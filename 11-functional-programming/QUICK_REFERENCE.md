# Quick Reference: Functional Programming

<div align="center">

![Module](https://img.shields.io/badge/Module-11-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Functional%20Programming-green?style=for-the-badge)

**Quick lookup guide for FP concepts in Java**

</div>

---

## 📋 Core Concepts

### Pure Functions
- No side effects (no mutation, no I/O)
- Same input → same output
- Referentially transparent

### Immutability
- Never modify existing data
- Create new data instead
- Use final fields and immutable collections

### Higher-Order Functions
- Functions as parameters
- Functions as return values
- Functions as values

### Function Composition
- Combining simple functions
- Building complex operations

---

## 🔑 Key Principles

### Pure Function Example
```java
// Pure - no side effects
int add(int a, int b) {
    return a + b;
}

// Impure - side effect
int addAndPrint(int a, int b) {
    System.out.println(a + b);  // Side effect
    return a + b;
}
```

### Immutability
```java
// Mutable (avoid)
List<String> list = new ArrayList<>();
list.add("a");
list.add("b");

// Immutable (preferred)
List<String> list = List.of("a", "b");

// Creating new from existing
List<String> newList = Stream.concat(list1.stream(), list2.stream())
    .collect(Collectors.toList());
```

### Function Composition
```java
Function<Integer, Integer> addOne = x -> x + 1;
Function<Integer, Integer> doubleIt = x -> x * 2;
Function<Integer, Integer> composed = addOne.andThen(doubleIt);
// 5 → 6 → 12

Function<Integer, Integer> composed2 = addOne.compose(doubleIt);
// 5 → 10 → 11
```

### Currying
```java
// Curried function
Function<Integer, Function<Integer, Integer>> curriedAdd = 
    a -> b -> a + b;

Function<Integer, Integer> add5 = curriedAdd.apply(5);
add5.apply(3);  // 8
```

### Partial Application
```java
Function<Integer, Integer> add = a -> b -> a + b;
IntFunction<IntUnaryOperator> addPartial = a -> b -> a + b;
IntUnaryOperator add5 = a -> 5 + a;
```

---

## 💻 Code Patterns

### Map-Reduce Pattern
```java
// Transform and aggregate
int sumOfSquares = numbers.stream()
    .map(n -> n * n)
    .reduce(0, Integer::sum);
```

### Filter-Map-Collect
```java
List<String> result = items.stream()
    .filter(item -> item.isValid())
    .map(Item::getName)
    .collect(Collectors.toList());
```

### Option/Maybe Pattern
```java
Optional<String> findById(String id) {
    return Optional.ofNullable(cache.get(id));
}

String name = findById("123")
    .map(String::toUpperCase)
    .orElse("UNKNOWN");
```

### Either Pattern (Error Handling)
```java
sealed interface Result<T> {
    record Success<T>(T value) implements Result<T> {}
    record Failure<T>(Exception error) implements Result<T> {}
}

Result<Integer> divide(int a, int b) {
    return b == 0 
        ? new Result.Failure<>(new ArithmeticException("div by zero"))
        : new Result.Success<>(a / b);
}
```

### Lazy Evaluation
```java
// Lazy supplier
Supplier<T> lazy = () -> computeExpensiveValue();

// Memoization
Memoizer.memoize(() -> expensiveOperation());
```

### Function Pipeline
```java
Function<T, R> pipeline = Function.identity()
    .andThen(this::validate)
    .andThen(this::transform)
    .andThen(this::persist);
```

---

## 📊 Declarative vs Imperative

### Imperative
```java
List<String> results = new ArrayList<>();
for (String item : items) {
    if (item.startsWith("A")) {
        results.add(item.toUpperCase());
    }
}
```

### Declarative (Functional)
```java
List<String> results = items.stream()
    .filter(s -> s.startsWith("A"))
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

---

## ✅ DO
- Prefer pure functions
- Use immutable data structures
- Compose small functions
- Prefer declarative style

### ❌ DON'T
- Don't mutate shared state
- Don't use null, use Optional
- Don't mix paradigms unnecessarily

---

## 🔍 FP Checklist

- [ ] Pure functions
- [ ] Immutability
- [ ] Function composition
- [ ] Declarative style
- [ ] Error handling without exceptions

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>
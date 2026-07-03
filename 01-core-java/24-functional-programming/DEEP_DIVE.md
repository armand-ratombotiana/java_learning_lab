# Module 24: Functional Programming - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-23, specifically Module 10 (Lambda Expressions)  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Introduction to Functional Programming in Java](#intro)
2. [Pure Functions & Immutability](#pure)
3. [Higher-Order Functions](#higher)
4. [Currying & Partial Application](#currying)
5. [Monads & Functors (Optional/Stream)](#monads)

---

## 1. Introduction to Functional Programming in Java <a name="intro"></a>
Functional Programming (FP) is a declarative paradigm where programs are constructed by applying and composing functions. Java introduced strong FP support in Java 8 with Lambdas and Streams.

---

## 2. Pure Functions & Immutability <a name="pure"></a>
- **Pure Function**: Given the same input, it always returns the same output and produces no side effects (no modifying external state).
- **Immutability**: Data cannot be changed after creation. In Java, this is achieved using `final` fields, unmodifiable collections, and records (Java 14+).

```java
// Pure function
public int add(int a, int b) {
    return a + b;
}
```

---

## 3. Higher-Order Functions <a name="higher"></a>
A higher-order function is a function that either takes one or more functions as arguments or returns a function as its result.

```java
public <T, R> List<R> mapList(List<T> list, Function<T, R> function) {
    List<R> result = new ArrayList<>();
    for (T item : list) {
        result.add(function.apply(item));
    }
    return result;
}
```

---

## 4. Currying & Partial Application <a name="currying"></a>
- **Currying**: Converting a function taking multiple arguments into a sequence of functions that each take a single argument.
- **Partial Application**: Fixing a number of arguments to a function, producing another function of smaller arity.

```java
// Currying example
Function<Integer, Function<Integer, Integer>> add = a -> b -> a + b;
int sum = add.apply(5).apply(3); // 8

// Partial application
Function<Integer, Integer> addFive = add.apply(5);
int total = addFive.apply(10); // 15
```

---

## 5. Monads & Functors (Optional/Stream) <a name="monads"></a>
- **Functor**: An object that implements a `map` operation (e.g., `Optional.map()`, `Stream.map()`).
- **Monad**: An object that implements a `flatMap` operation, allowing the chaining of operations that produce wrapped values (e.g., `Optional.flatMap()`).
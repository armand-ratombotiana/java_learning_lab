# Interview Preparation: Functional Composition

This document covers advanced questions related to functional pipelines, Monads, and the theoretical underpinnings of functional programming in Java.

## Q1: Explain the difference between `Function.andThen()` and `Function.compose()`.
**Answer:**
Both methods chain two functions together, but they dictate the order of execution.
*   `f1.andThen(f2)` executes `f1` first, and passes the result to `f2`. It reads left-to-right, which is generally more intuitive for data pipelines.
*   `f1.compose(f2)` executes `f2` first, and passes the result to `f1`. It mimics mathematical function composition notation $f(g(x))$, reading right-to-left.

## Q2: What makes a class a "Functor" in Java? Give an example.
**Answer:**
A Functor is a design pattern representing a container or context that holds a value. To be a Functor, it must provide a `map` operation.
The `map` operation takes a function, unwraps the value from the container, applies the function to the value, and then wraps the new result back into a new instance of the container.
**Example**: `Optional<T>`. If you have `Optional<String>`, and you call `.map(String::length)`, the `Optional` unwraps the string, calculates the integer length, and returns a new `Optional<Integer>`. `Stream<T>` and `CompletableFuture<T>` are also Functors.

## Q3: What is a "Monad", and what specific problem does `flatMap` solve?
**Answer:**
A Monad is a Functor with the additional capability to handle functions that *themselves return a container*. It does this via the `flatMap` operation.
**The Problem**: If you have an `Optional<String>` and you use `map()` with a function that returns an `Optional<Integer>`, you end up with a nested container: `Optional<Optional<Integer>>`. This is awkward to work with.
**The Solution**: `flatMap()` applies the function, but instead of wrapping the returned `Optional<Integer>` inside another `Optional`, it "flattens" the structure, returning just `Optional<Integer>`.

## Q4: Why is it dangerous to introduce side-effects (like modifying a global variable) inside a `map()` or `flatMap()` lambda?
**Answer:**
Functional programming relies on "pure functions" (functions where the output is determined solely by the input, with no observable side-effects).
If you mutate external state inside a `map()` operation on a Java `Stream`, your code becomes unpredictable. If another developer changes the stream to `.parallel()`, your lambda will be executed concurrently by multiple threads. If it mutates a standard `ArrayList`, it will cause a `ConcurrentModificationException` or silent data corruption. Side-effects should be isolated to terminal operations like `forEach()`.

## Q5: How does the `Optional` monad eliminate null checks?
**Answer:**
In imperative programming, you write nested `if (obj != null)` checks before accessing nested properties (e.g., `user.getAddress().getCity()`).
The `Optional` monad encapsulates the concept of "presence or absence". You can chain operations using `flatMap` and `map`:
`Optional.ofNullable(user).flatMap(User::getAddress).map(Address::getCity)`.
If at any point in the chain a value is missing (empty), the subsequent `map` and `flatMap` operations simply do nothing and pass the empty state down the line. You only handle the final result at the very end using `ifPresent()` or `orElse()`, completely eliminating explicit `!= null` checks.
# Theory — Functional Programming

## Pure Functions
A function is pure if:
- Given the same input, it always returns the same output
- It has no observable side effects

```java
// Pure
int add(int a, int b) { return a + b; }

// Impure (side effect: I/O)
void log(String msg) { System.out.println(msg); }

// Impure (not deterministic)
int random() { return new Random().nextInt(); }
```

## Immutability
An immutable object cannot change after creation:
```java
record Point(int x, int y) { } // Immutable (Java 16+)
```

## Referential Transparency
An expression is referentially transparent if it can be replaced by its value without changing the program's behaviour.

## Monads (in Java)
A monad is a design pattern that wraps a value and provides:
- `of` (wrap)
- `map` (transform)
- `flatMap` (transform and flatten)

`Optional`, `Stream`, and `CompletableFuture` are monads in Java.

## Combinators
Functions that combine other functions:
```java
Function<String, String> trim = String::trim;
Function<String, String> upper = String::toUpperCase;
Function<String, String> combined = trim.andThen(upper);
```

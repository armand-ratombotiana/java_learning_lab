# Mathematical Foundation — Lambdas

## Lambda Calculus
Java lambdas are an applied form of **lambda calculus** — a formal system for function abstraction and application.

```
λx.x+1    — abstraction: "a function taking x and returning x+1"
λx.λy.x  — currying
(λx.x)(5) — application: evaluates to 5
```

## Higher-Order Functions
A function that takes a function as argument or returns one:
```java
// Higher-order: takes a Function argument
public static <T> List<T> filter(List<T> list, Predicate<T> pred) { ... }
```

## Closures (Lexical Scoping)
A lambda captures its lexical scope — equivalent to a closure in lambda calculus where free variables are bound at definition time.

## Type Inference
The compiler infers lambda parameter types from the target functional interface:
```java
// Compiler infers s is String
Function<String, Integer> f = s -> s.length();
```

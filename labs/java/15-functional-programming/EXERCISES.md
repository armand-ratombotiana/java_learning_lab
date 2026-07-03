# Exercises — Functional Programming

## Beginner
1. Write a pure function that computes the factorial of a number.
2. Convert `List<String>` to `List<Integer>` using `map(Integer::parseInt)`. Handle `NumberFormatException` via `Optional`.
3. Use `Optional` to safely get the first element of an empty list.

## Intermediate
4. Create a function `compose` that takes two `Function` instances and returns their composition.
5. Implement `Validator<T>` combinator with `and()`, `or()`, and `negate()`.
6. Refactor a chain of null checks into Optional `map`/`flatMap` calls.

## Advanced
7. Implement a `Try<T>` monad (success/failure wrapper) with `map` and `flatMap`.
8. Build a pure data pipeline: parse CSV → filter → transform → aggregate.
9. Create an immutable `Money` class with operations like `add`, `multiply`, `compareTo`.

## Combinators
10. Write a `Comparator` combinator that composes multiple comparators in sequence.
11. Implement `memoize` — a function that caches results of a pure function.

## Reflection
12. Analyse the compiled bytecode of a lambda in a pure function — can the JIT inline through Optional?

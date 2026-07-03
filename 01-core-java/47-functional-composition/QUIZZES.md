# Quizzes: Functional Composition

Test your knowledge of Function chaining, Functors, and Monads.

## Quiz 1: Function Composition

**Q1: If you have `Function<String, String> f1 = s -> s.trim();` and `Function<String, String> f2 = s -> s.toUpperCase();`, what is the difference between `f1.andThen(f2)` and `f1.compose(f2)`?**
- A) There is no difference.
- B) `andThen` executes `f1` first, then passes the result to `f2`. `compose` executes `f2` first, then passes the result to `f1`.
- C) `andThen` is for Strings, `compose` is for Integers.
- D) `compose` is faster.
*Answer: B*

**Q2: You want to combine three `Predicate<Integer>` checks: `isEven`, `isPositive`, and `isLessThan100`. How do you compose them?**
- A) `isEven.andThen(isPositive).andThen(isLessThan100)`
- B) `isEven.compose(isPositive).compose(isLessThan100)`
- C) `isEven.and(isPositive).and(isLessThan100)`
- D) `Predicate.combine(isEven, isPositive, isLessThan100)`
*Answer: C*

## Quiz 2: Functors and Monads

**Q1: In functional programming terms, what makes `Optional<T>` a Functor?**
- A) It can be null.
- B) It provides a `map()` method that takes a function, unwraps the value (if present), applies the function, and wraps the result back into a new `Optional`.
- C) It is immutable.
- D) It implements `Serializable`.
*Answer: B*

**Q2: When should you use `flatMap()` instead of `map()`?**
- A) When you are working with multidimensional arrays.
- B) When the mapping function you are applying returns a container (like `Optional` or `Stream`) itself. `flatMap` prevents you from ending up with nested containers (e.g., `Optional<Optional<String>>`).
- C) When you want to map data concurrently.
- D) When you want to throw an exception if the data is null.
*Answer: B*

## Quiz 3: Edge Cases

**Q1: Why is it considered a bad practice to add an element to an external `ArrayList` from inside an `Optional.map()` call?**
- A) Because `Optional` cannot access external variables.
- B) Because `map()` is expected to be a pure function without side-effects. Introducing mutable state breaks the functional paradigm and can cause unpredictable bugs (like `ConcurrentModificationException` if used in parallel streams later).
- C) Because it causes a memory leak.
- D) Because `map()` returns void.
*Answer: B*
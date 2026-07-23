# Mock Interview Transcript: Functional Programming

## Interviewer: Senior SWE, Google
## Candidate: Senior Java developer
## Time: 40 minutes
## Focus: Pure functions, immutability, monadic patterns, Optional

---

**Q1: What makes a function "pure" in functional programming?**

**Candidate**: A pure function: (1) always returns the same output for the same input (referentially transparent), (2) has no side effects (doesn't modify state, doesn't do I/O), (3) doesn't depend on external state. Pure functions are easier to test, reason about, and parallelize.

**Interviewer**: Java isn't a pure FP language. How do you manage side effects in Java streams?

**Candidate**: Minimize side effects. If side effects are necessary: (1) use `peek()` for debugging only (not guaranteed to be called), (2) `forEach()` is explicitly for side effects but breaks functional purity, (3) use `collect()` with a mutable reduction (properly synchronized) for necessary state accumulation, (4) for I/O, use `CompletableFuture` or reactive streams.

**Interviewer**: Explain `Optional` and its monadic properties.

**Candidate**: `Optional<T>` is a container that may or may not hold a value. Monadic properties: (1) `map` — transform the value if present, (2) `flatMap` — transform to another Optional (avoid nesting), (3) `filter` — conditionally keep the value. `Optional` also provides `orElse`, `orElseGet`, `orElseThrow` for safe unwrapping.

**Interviewer**: Compare `orElse()` vs `orElseGet()`.

**Candidate**: `orElse(defaultValue)` evaluates `defaultValue` eagerly — even if the Optional has a value. `orElseGet(Supplier)` evaluates the supplier lazily — only if the Optional is empty. If the default is expensive to compute, always use `orElseGet()`.

**Interviewer**: Write a method that computes a discount. Given a user ID, get their loyalty tier, compute discount percentage, and apply it to a price.

**Candidate**: 
```java
Optional<BigDecimal> calculateDiscount(long userId, BigDecimal price) {
    return findUser(userId)
        .flatMap(this::getLoyaltyTier)
        .map(Tier::discountPercent)
        .map(pct -> price.multiply(pct).divide(BigDecimal.valueOf(100)))
        .map(price::subtract);
}

Optional<User> findUser(long id) { ... }
Optional<Tier> getLoyaltyTier(User u) { ... }
```

**Interviewer**: What if `findUser` throws an exception?

**Candidate**: Use `Optional.ofNullable()` with try-catch, or better, use a `Try` monad pattern. Java doesn't have a built-in `Try` type, but you can pattern:
```java
Optional<User> findUserSafe(long id) {
    try { return Optional.ofNullable(findUser(id)); }
    catch (Exception e) { return Optional.empty(); }
}
```

**Interviewer**: How does `flatMap` on Optional differ from `map`?

**Candidate**: If `map` returns an `Optional<U>` from a function, the result is `Optional<Optional<U>>`. `flatMap` "flattens" it to `Optional<U>`. Example:
```java
// getAddress() returns Optional<Address>, getZip() returns Optional<String>
Optional<Optional<String>> bad = user.map(u -> getAddress(u));  
Optional<String> good = user.flatMap(u -> getAddress(u)).flatMap(a -> getZip(a));
```

**Interviewer**: Discuss `Collectors.toMap()` and handling duplicate keys.

**Candidate**: `toMap` throws `IllegalStateException` on duplicate keys by default. You can provide a merge function: `toMap(keyFn, valFn, (a, b) -> a)` picks first, `(a, b) -> b` picks last, or `(a, b) -> a + b` combines values.

**Interviewer**: Final question: Compare streams with for-loops from a functional programming perspective.

**Candidate**: Streams encourage functional style: (1) pipelines are declarative (what, not how), (2) intermediate operations don't mutate state, (3) parallelization is a one-word change. For-loops are imperative: (1) explicit control flow, (2) often use mutable accumulators, (3) harder to parallelize. Streams are preferred for data transformations; for-loops for complex logic with multiple exit conditions or when performance-critical and the overhead matters.

---

## Feedback

**Strengths**:
- Clear understanding of pure functions and side effect management
- Proper Optional usage with monadic patterns
- Smart about eager vs lazy evaluation
- Balanced analysis of streams vs loops

**Areas for Improvement**:
- Could mention `Vavr` library for richer functional types
- Should discuss `Stream.iterate()` and `Stream.generate()` for infinite streams

**Score**: 4/5 — Good functional programming mindset

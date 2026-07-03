# Mathematical Foundation — Functional Programming

## Lambda Calculus
```
λx.x + 1      — identity extended
(λx.x + 1) 5  →  6  (β-reduction)
```

## Monad Laws
For a type `M<T>` with `flatMap` and `of`:
1. **Left identity:** `of(x).flatMap(f) ≡ f(x)`
2. **Right identity:** `m.flatMap(of) ≡ m`
3. **Associativity:** `m.flatMap(f).flatMap(g) ≡ m.flatMap(x → f(x).flatMap(g))`

```java
// Left identity
Optional.of(5).flatMap(x -> Optional.of(x * 2)) → Optional.of(10)

// Right identity
Optional.of(5).flatMap(Optional::of) → Optional.of(5)

// Associativity
Optional.of(5)
    .flatMap(x -> Optional.of(x + 1))
    .flatMap(x -> Optional.of(x * 2))
// ≡
Optional.of(5)
    .flatMap(x -> Optional.of(x + 1).flatMap(y -> Optional.of(y * 2)))
```

## Referential Transparency
```
f(x) = x + 1
g(x) = f(x) * 2

g(3) → (3 + 1) * 2 → 8   // always, regardless of context
```

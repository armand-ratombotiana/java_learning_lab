# Common Mistakes in Discrete Mathematics

## Confusing $\implies$ and $\iff$

```java
// "If P then Q" does NOT mean "If Q then P"
// P = "is raining", Q = "ground is wet"
// raining → wet (true)
// wet → raining (false: could be sprinkler)
```

## Off-by-One in Induction

```java
// WRONG: assuming P(k) for k=0 when base is P(1)
// WRONG: proving P(k) → P(k+2) instead of P(k) → P(k+1)
```

## Negating Quantifiers

```java
// "Not all students passed" ≠ "No students passed"
// ¬∀x P(x) ≡ ∃x ¬P(x)  [some failed]
// ¬∃x P(x) ≡ ∀x ¬P(x)  [none passed]
```

## Mod of Negative Numbers

```java
// In Java: -7 % 3 = -1 (not 2 as in math)
// Use: ((a % b) + b) % b for non-negative result
```

## Cardinality Misconception

```java
// |A ∪ B| ≠ |A| + |B| when sets overlap
// Correct: |A ∪ B| = |A| + |B| - |A ∩ B|
```

# Common Mistakes in Probability

## Confusing $P(A \cap B)$ with $P(A \mid B)$

```java
// P(rain AND Tuesday) vs P(rain | Tuesday)
// These are very different!
```

## Gambler's Fallacy

```java
// "After 5 heads, next must be tails" — WRONG!
// Each coin flip is independent: P(heads) = 0.5 every time
```

## Base Rate Neglect

```java
// Test for rare disease (1% prevalence, 99% accurate):
// P(disease | positive) ≈ 50%, not 99%!
// Bayes' theorem accounts for base rate
```

## Misinterpreting $p$-values

A $p$-value is NOT the probability that the null hypothesis is true. It's $P(\text{data} \mid H_0)$ under the null.

## Probability of Union for Non-Disjoint Events

```java
// WRONG: P(A ∪ B) = P(A) + P(B)  (only if disjoint)
// CORRECT: P(A ∪ B) = P(A) + P(B) - P(A ∩ B)
```

## Java: Wrong Random.nextInt() Range

```java
// WRONG: nextInt(n) returns 0..n-1, not 1..n!
int die = rng.nextInt(6);    // 0..5, not 1..6!
int die = rng.nextInt(6) + 1; // correct
```

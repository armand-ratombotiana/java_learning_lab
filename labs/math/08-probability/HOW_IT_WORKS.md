# How Probability Works

## Kolmogorov's Axioms

1. $P(A) \ge 0$ for any event $A$
2. $P(\Omega) = 1$ (the certain event)
3. $P(A \cup B) = P(A) + P(B)$ for disjoint events

## Conditional Probability

$$
P(A \mid B) = \frac{P(A \cap B)}{P(B)}
$$

## Bayes' Theorem

$$
P(H \mid E) = \frac{P(E \mid H) P(H)}{P(E \mid H) P(H) + P(E \mid \lnot H) P(\lnot H)}
$$

## Random Variables

A function $X: \Omega \to \mathbb{R}$ assigning numbers to outcomes.

### Expectation
$$
\mathbb{E}[X] = \sum_x x \cdot P(X = x)
$$

### Variance
$$
\text{Var}(X) = \mathbb{E}[(X - \mu)^2] = \mathbb{E}[X^2] - \mu^2
$$

## In Java: Monte Carlo Estimate of $\pi$

```java
public static double estimatePi(int samples) {
    Random rng = new Random();
    int inside = 0;
    for (int i = 0; i < samples; i++) {
        double x = rng.nextDouble();
        double y = rng.nextDouble();
        if (x * x + y * y <= 1) inside++;
    }
    return 4.0 * inside / samples;
}
```

# Common Mistakes in Calculus

## Ignoring the $+C$ in Indefinite Integrals

```java
// The antiderivative of 2x is x² + C
// Forgetting C means losing the family of solutions
```

## Chain Rule Forgetting

```java
// WRONG: d/dx sin(x²) = cos(x²) ← missing inner derivative
// RIGHT: d/dx sin(x²) = cos(x²) * 2x
```

## Limit Confusion

```java
// lim x→0 sin(x)/x = 1 (not 0)
// lim x→0 (1 + x)^(1/x) = e (not 1)
```

## Numerical Instability in Derivatives

```java
// h too small: catastrophic cancellation
double h = 1e-16; // WRONG — f(x+h) == f(x) due to rounding!
// h too large: truncation error dominates
// Rule of thumb: h = cbrt(eps) * max(1, |x|) ≈ 1e-6
```

## Indefinite vs Definite Integral

```java
// Definite: ∫ₐᵇ f(x)dx = number (area)
// Indefinite: ∫ f(x)dx = function + C
```

## Discontinuities in Integration

Numerical integration fails at discontinuities. Split the interval at the discontinuity.

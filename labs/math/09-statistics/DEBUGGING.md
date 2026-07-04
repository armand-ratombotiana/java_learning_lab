# Debugging Statistics in Java

## Common Issues

### Integer Division in Mean

```java
// WRONG: integer division
double mean = sum / n; // if sum and n are int

// RIGHT: promote to double
double mean = (double) sum / n;
```

### Stack Overflow in Recursive Quantiles

Quantile algorithms should be iterative or use `QuickSelect` with median-of-three pivot.

### Numeric Overflow in Sum of Squares

```java
// WRONG: overflow for large values
double sumSq = x1*x1 + x2*x2 + ...;

// BETTER: use Welford's online algorithm
// or use double (53-bit mantissa) carefully
```

## Debugging Checklist

- [ ] Sample vs population variance (n vs n-1)?
- [ ] Integer division anywhere?
- [ ] Assumptions of statistical test met?
- [ ] Outliers properly handled?
- [ ] Multiple comparisons corrected?
- [ ] Missing values handled appropriately?
- [ ] Visualization inspected for anomalies?

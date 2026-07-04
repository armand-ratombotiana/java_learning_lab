# Debugging Calculus in Java

## Common Issues

### Step Size for Derivatives

```java
// h too small: f(x+h) == f(x) due to floating point
// h too large: truncation error
// Optimal: h = eps^(1/3) * max(1, |x|) for central difference
double h = Math.cbrt(Double.MIN_VALUE) * Math.max(1.0, Math.abs(x));
```

### Integration Error

```java
// Simpson's rule needs even number of subintervals
// For oscillatory functions, subdivision helps
// Check error by comparing n and 2n results
```

### Gradient Descent Divergence

```java
// If loss increases, learning rate is too high
// If loss barely changes, learning rate is too low
// Use line search or adaptive methods (Adam, RMSProp)
```

## Debugging Checklist

- [ ] Derivative step size appropriate?
- [ ] Integration method matched to function smoothness?
- [ ] Limits computed correctly (indeterminate forms)?
- [ ] Chain rule applied correctly?
- [ ] Constant of integration included?
- [ ] Gradient descent converging properly?

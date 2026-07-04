# Security in Calculus

## Numerical Stability as Security

Attacks can exploit numerical instability:

```java
// Attacker crafts input where derivative becomes NaN
double x = Double.NaN;
double grad = derivative(f, x); // NaN!

// Attacker causes overflow in gradient computation
// Result: gradient descent takes arbitrary steps
```

## Gradient Manipulation

In ML, adversarial examples are crafted using calculus:
$$
x' = x + \varepsilon \cdot \text{sign}(\nabla_x L(x, y))
$$

This uses the gradient of the loss with respect to the input.

## Safe Numerical Methods

```java
public static double safeDerivative(Function<Double, Double> f, double x) {
    if (!Double.isFinite(x))
        throw new IllegalArgumentException("Input must be finite");
    double h = Math.cbrt(Double.MIN_VALUE) * Math.max(1.0, Math.abs(x));
    double fxph = f.apply(x + h);
    double fxmh = f.apply(x - h);
    if (!Double.isFinite(fxph) || !Double.isFinite(fxmh))
        throw new ArithmeticException("Non-finite function value");
    return (fxph - fxmh) / (2 * h);
}
```

## Lagrange Multipliers in Security

Constrained optimization (Lagrange multipliers) is used in:
- **Resource allocation**: optimal security spending
- **Adversarial robustness**: minimize worst-case loss
- **Privacy**: trade-off between accuracy and privacy

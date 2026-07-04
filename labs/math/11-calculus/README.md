# Calculus

The mathematical study of continuous change.

## Scope

- Limits and continuity
- Derivatives: rates of change, optimization
- Integrals: areas, accumulation, antiderivatives
- Fundamental Theorem of Calculus
- Multivariable calculus: partial derivatives, gradients

## Java Implementation

```java
public class Calculus {
    public static double derivative(Function<Double, Double> f, double x) {
        double h = 1e-8;
        return (f.apply(x + h) - f.apply(x - h)) / (2 * h);
    }

    public static double integrate(Function<Double, Double> f, double a, double b, int n) {
        double h = (b - a) / n;
        double sum = 0;
        for (int i = 0; i < n; i++)
            sum += f.apply(a + (i + 0.5) * h);
        return sum * h;
    }
}
```

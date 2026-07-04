# Internals of Calculus

## Numerical Differentiation

### Forward Difference (error $O(h)$)
$$
f'(x) \approx \frac{f(x+h) - f(x)}{h}
$$

### Central Difference (error $O(h^2)$)
$$
f'(x) \approx \frac{f(x+h) - f(x-h)}{2h}
$$

### Richardson Extrapolation
Combine two approximations with different $h$ values to cancel error terms.

## Numerical Integration

### Midpoint Rule
$$
\int_a^b f(x)\,dx \approx h \sum_{i=1}^n f\left(a + (i-\tfrac{1}{2})h\right)
$$

### Simpson's Rule
$$
\int_a^b f(x)\,dx \approx \frac{h}{3}[f(x_0) + 4f(x_1) + 2f(x_2) + 4f(x_3) + \dots + f(x_n)]
$$

### Adaptive Quadrature
Recursively subdivide intervals where function varies rapidly.

```java
public static double adaptiveQuadrature(Function<Double, Double> f,
                                          double a, double b, double tol) {
    double m = (a + b) / 2;
    double S = simpson(f, a, b);
    double S1 = simpson(f, a, m);
    double S2 = simpson(f, m, b);
    if (Math.abs(S1 + S2 - S) < 15 * tol)
        return S1 + S2;
    return adaptiveQuadrature(f, a, m, tol/2)
         + adaptiveQuadrature(f, m, b, tol/2);
}
```

## Automatic Differentiation

For exact derivatives (used in ML frameworks), compute at compile time using dual numbers.

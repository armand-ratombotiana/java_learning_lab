# How Calculus Works

## Limits

$$
\lim_{x \to a} f(x) = L \iff \forall \varepsilon > 0 \; \exists \delta > 0 : 0 < |x-a| < \delta \implies |f(x)-L| < \varepsilon
$$

## Derivatives

### Definition
$$
f'(x) = \lim_{h \to 0} \frac{f(x+h) - f(x)}{h}
$$

### Power Rule
$$
\frac{d}{dx} x^n = n x^{n-1}
$$

### Chain Rule
$$
\frac{d}{dx} f(g(x)) = f'(g(x)) \cdot g'(x)
$$

## Integrals

### Riemann Sum
$$
\int_a^b f(x)\,dx = \lim_{n \to \infty} \sum_{i=1}^n f(x_i^*) \Delta x
$$

### Fundamental Theorem
$$
\int_a^b f(x)\,dx = F(b) - F(a) \quad \text{where } F'(x) = f(x)
$$

## In Java: Numerical Differentiation

```java
public static double derivative(Function<Double, Double> f, double x) {
    double h = Math.cbrt(Double.MIN_VALUE) * Math.max(1, Math.abs(x));
    return (f.apply(x + h) - f.apply(x - h)) / (2 * h); // central difference
}

public static double gradientDescentStep(Function<Double, Double> f, double x, double lr) {
    return x - lr * derivative(f, x);
}
```

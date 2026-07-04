# Interview Questions on Calculus

## Easy

1. Compute the derivative of $x^3 + 2x^2 - 5x + 1$ analytically.
2. Evaluate $\int_0^1 x^2 \, dx$ analytically.
3. Explain the chain rule with an example.

## Medium

4. Implement numerical differentiation (central difference).
5. Implement numerical integration (Simpson's rule).
6. Find the minimum of $f(x) = x^2 + 3x + 1$ using gradient descent.
7. Use Newton's method to find $\sqrt{2}$.

## Hard

8. Implement automatic differentiation using dual numbers.
9. Implement gradient descent for multivariate functions.
10. Solve an ODE using Runge-Kutta 4th order.
11. Implement backpropagation for a simple neural network.

## Java: Dual Numbers for Auto-Diff

```java
public record Dual(double real, double dual) {
    public static Dual of(double x) { return new Dual(x, 0); }
    public static Dual var(double x) { return new Dual(x, 1); }

    public Dual add(Dual other) {
        return new Dual(real + other.real, dual + other.dual);
    }
    public Dual multiply(Dual other) {
        return new Dual(real * other.real,
                        real * other.dual + dual * other.real);
    }
    public Dual sin() {
        return new Dual(Math.sin(real), Math.cos(real) * dual);
    }
}

// Usage: f(x) = x² + sin(x)
Dual x = Dual.var(2);
Dual result = x.multiply(x).add(x.sin());
// result.real = f(2), result.dual = f'(2)
```

## Java: Runge-Kutta 4

```java
public static double rk4(BiFunction<Double, Double, Double> f,
                          double t0, double y0, double h, int steps) {
    double t = t0, y = y0;
    for (int i = 0; i < steps; i++) {
        double k1 = f.apply(t, y);
        double k2 = f.apply(t + h/2, y + h*k1/2);
        double k3 = f.apply(t + h/2, y + h*k2/2);
        double k4 = f.apply(t + h, y + h*k3);
        y += h * (k1 + 2*k2 + 2*k3 + k4) / 6;
        t += h;
    }
    return y;
}
```

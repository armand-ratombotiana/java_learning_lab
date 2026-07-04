# Step-by-Step: Calculus in Java

## Numerical Derivative

```java
public static double derivative(Function<Double, Double> f, double x, double h) {
    // Central difference: O(h²)
    return (f.apply(x + h) - f.apply(x - h)) / (2 * h);
}

public static double secondDerivative(Function<Double, Double> f, double x, double h) {
    return (f.apply(x + h) - 2 * f.apply(x) + f.apply(x - h)) / (h * h);
}
```

## Numerical Integration with Simpson's Rule

```java
public static double simpson(Function<Double, Double> f, double a, double b, int n) {
    n = n % 2 == 0 ? n : n + 1; // must be even
    double h = (b - a) / n;
    double sum = f.apply(a) + f.apply(b);
    for (int i = 1; i < n; i++) {
        double x = a + i * h;
        sum += (i % 2 == 0 ? 2 : 4) * f.apply(x);
    }
    return sum * h / 3;
}
```

## Gradient Descent

```java
public static double gradientDescent(Function<Double, Double> f,
                                      double start, double lr, int epochs) {
    double x = start;
    for (int i = 0; i < epochs; i++) {
        double grad = derivative(f, x);
        x -= lr * grad;
    }
    return x; // local minimum
}
```

## Newton's Method for Roots

```java
public static double newton(Function<Double, Double> f, double start, int iterations) {
    double x = start;
    for (int i = 0; i < iterations; i++) {
        double fx = f.apply(x);
        double fpx = derivative(f, x);
        if (Math.abs(fpx) < 1e-15) break;
        x -= fx / fpx;
    }
    return x;
}
```

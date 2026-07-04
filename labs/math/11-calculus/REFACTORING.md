# Refactoring Calculus Code

## Create Higher-Order Functions

```java
// BEFORE: repeated differentiation code
double d1 = (f.apply(x+h) - f.apply(x-h)) / (2*h);
double d2 = (g.apply(x+h) - g.apply(x-h)) / (2*h);

// AFTER
Function<Function<Double, Double>, Function<Double, Double>> derivative =
    f -> x -> (f.apply(x + h) - f.apply(x - h)) / (2 * h);
```

## Encapsulate Numerical Methods

```java
// BEFORE
double integral = 0;
for (int i = 0; i < n; i++)
    integral += f.apply(a + i*h) * h;

// AFTER
public interface NumericalMethod {
    double integrate(Function<Double, Double> f, double a, double b);
}

class MidpointRule implements NumericalMethod { ... }
class SimpsonsRule implements NumericalMethod { ... }
class AdaptiveQuadrature implements NumericalMethod { ... }
```

## Use Function Composition

```java
Function<Double, Double> f = x -> x * x;
Function<Double, Double> df = derivative(f);
Function<Double, Double> integral = integrate(f, 0);
```

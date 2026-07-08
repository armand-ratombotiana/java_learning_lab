# Numerical Methods

Computational mathematics for solving real-world problems — integration, root finding, and differential equations.

## Scope

- Numerical integration: rectangle, trapezoidal, Simpson's, Romberg, Gauss-Legendre
- Root finding: bisection, Newton-Raphson, secant, false position
- Interpolation: Lagrange, Newton's divided differences
- ODE solvers: Euler, Heun, Runge-Kutta 4 (RK4)
- Finite differences: forward, backward, central
- Richardson extrapolation for error reduction

## Java Implementation

```java
package com.math15;

public class NumericalMethods {
    public static double simpsonsRule(Function<Double, Double> f, double a, double b, int n) { /* ... */ }
    public static double rombergIntegration(Function<Double, Double> f, double a, double b, int order) { /* ... */ }
    public static double bisection(Function<Double, Double> f, double a, double b, int maxIter) { /* ... */ }
    public static double newtonRaphson(Function<Double, Double> f, Function<Double, Double> fPrime, double start, int maxIter) { /* ... */ }
    public static double[] rungeKutta4(BiFunction<Double, Double, Double> f, double t0, double y0, double h, int steps) { /* ... */ }
    public static double centralDifference(Function<Double, Double> f, double x, double h) { /* ... */ }
}
```

## Key Topics

- Numerical integration techniques and error analysis
- Root-finding algorithms and convergence rates
- Interpolation methods and Runge's phenomenon
- Initial value ODE solvers
- Finite difference approximations
- Richardson extrapolation for error reduction
- Truncation vs. roundoff error

## Prerequisites

- Calculus (derivatives, integrals, differential equations)
- Basic programming experience

## How to Use This Lab

1. Read THEORY.md for comprehensive mathematical treatment
2. Review MATH_FOUNDATION.md for prerequisite review
3. Study CODE_DEEP_DIVE.md for implementation patterns
4. Complete EXERCISES.md problem sets
5. Build the MINI_PROJECT for hands-on application
6. Challenge yourself with the REAL_WORLD_PROJECT

## Time Estimate

- Theory study: 2.5 hours
- Code implementation: 3 hours
- Exercises: 2 hours
- Projects: 3 hours
- **Total: ~10.5 hours**

## Difficulty: ★★★★☆ (Advanced)

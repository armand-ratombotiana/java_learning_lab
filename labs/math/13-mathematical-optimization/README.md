# Mathematical Optimization

Algorithms for finding minima/maxima — the engine behind machine learning training.

## Scope

- Gradient descent: vanilla, momentum, Nesterov accelerated gradient
- Adaptive methods: AdaGrad, Adam optimizer
- Newton's method for optimization and root finding
- Lagrange multipliers for constrained optimization
- Convex optimization fundamentals
- Line search methods (golden section search)
- Quasi-Newton methods (BFGS)
- Numerical gradient computation

## Java Implementation

```java
package com.math13;

public class MathematicalOptimization {
    public static double[] gradientDescent(MultivariateFunction f, GradientFunction grad,
                                            double[] start, double learningRate, int iterations) { /* ... */ }
    public static double[] adam(MultivariateFunction f, GradientFunction grad,
                                 double[] start, double learningRate, double beta1, double beta2, int iterations) { /* ... */ }
    public static double newtonMethod1D(Function<Double, Double> f,
                                         Function<Double, Double> fPrime,
                                         Function<Double, Double> fDoublePrime, double start, int iterations) { /* ... */ }
    public static double[] bfgs(MultivariateFunction f, GradientFunction grad,
                                 double[] start, int iterations) { /* ... */ }
}
```

## Key Topics

- Gradient descent and its many variants
- Adaptive learning rate methods
- Newton and quasi-Newton methods
- Constrained optimization via Lagrange multipliers
- Convex vs. non-convex optimization
- Numerical gradient approximation
- Line search and step size selection
- Convergence analysis and stopping criteria

## Prerequisites

- Multivariable calculus (partial derivatives, gradients)
- Linear algebra (vectors, matrices, eigenvalues)

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

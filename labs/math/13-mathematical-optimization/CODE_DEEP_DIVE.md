# Code Deep Dive: Mathematical Optimization in Java

## 1. Gradient Descent Family

### 1.1 Vanilla Gradient Descent
```java
public static double[] gradientDescent(MultivariateFunction f, GradientFunction grad,
                                        double[] start, double learningRate, int iterations) {
    double[] x = start.clone();
    for (int iter = 0; iter < iterations; iter++) {
        double[] g = grad.apply(x);
        for (int i = 0; i < x.length; i++) {
            x[i] -= learningRate * g[i];
        }
    }
    return x;
}
```

**Key insights:**
- The update rule is x ← x - α∇f(x)
- Learning rate α is critical: too large → divergence, too small → slow progress
- Complexity O(k·n) where k = iterations, n = dimension
- Converges to local minimum (global for convex functions)

### 1.2 Momentum
```java
public static double[] gradientDescentMomentum(MultivariateFunction f, GradientFunction grad,
                                                 double[] start, double learningRate,
                                                 double momentum, int iterations) {
    double[] x = start.clone();
    double[] v = new double[x.length];
    for (int iter = 0; iter < iterations; iter++) {
        double[] g = grad.apply(x);
        for (int i = 0; i < x.length; i++) {
            v[i] = momentum * v[i] + learningRate * g[i];
            x[i] -= v[i];
        }
    }
    return x;
}
```

Momentum reduces oscillation in narrow valleys and accelerates convergence in consistent gradient directions.

### 1.3 Adam Optimizer
```java
public static double[] adam(MultivariateFunction f, GradientFunction grad,
                             double[] start, double learningRate,
                             double beta1, double beta2, int iterations) {
    double[] x = start.clone();
    double[] m = new double[x.length];  // First moment estimate
    double[] v = new double[x.length];  // Second moment estimate
    double eps = 1e-8;
    for (int t = 1; t <= iterations; t++) {
        double[] g = grad.apply(x);
        for (int i = 0; i < x.length; i++) {
            m[i] = beta1 * m[i] + (1 - beta1) * g[i];
            v[i] = beta2 * v[i] + (1 - beta2) * g[i] * g[i];
            double mHat = m[i] / (1 - Math.pow(beta1, t));
            double vHat = v[i] / (1 - Math.pow(beta2, t));
            x[i] -= learningRate * mHat / (Math.sqrt(vHat) + eps);
        }
    }
    return x;
}
```

Adam combines momentum with per-parameter adaptive learning rates. The bias correction terms (1 - βᵗ) compensate for initialization at zero.

## 2. Newton's Method

### 2.1 Root Finding
```java
public static double findRootNewton(Function<Double, Double> f,
                                     Function<Double, Double> fPrime,
                                     double start, int iterations) {
    double x = start;
    for (int i = 0; i < iterations; i++) {
        double fx = f.apply(x);
        double fpx = fPrime.apply(x);
        if (Math.abs(fpx) < 1e-15) break;
        x -= fx / fpx;
    }
    return x;
}
```

Quadratic convergence: error roughly squares each iteration near the root.

### 2.2 Optimization (1D)
```java
public static double newtonMethod1D(Function<Double, Double> f,
                                     Function<Double, Double> fPrime,
                                     Function<Double, Double> fDoublePrime,
                                     double start, int iterations) {
    double x = start;
    for (int i = 0; i < iterations; i++) {
        double fp = fPrime.apply(x);
        double fpp = fDoublePrime.apply(x);
        if (Math.abs(fpp) < 1e-15) break;
        x -= fp / fpp;
    }
    return x;
}
```

## 3. Numerical Gradient

```java
public static double[] numericalGradient(MultivariateFunction f, double[] x) {
    double h = 1e-8;
    double[] grad = new double[x.length];
    double fx = f.apply(x);
    for (int i = 0; i < x.length; i++) {
        double[] xPlus = x.clone();
        xPlus[i] += h;
        grad[i] = (f.apply(xPlus) - fx) / h;
    }
    return grad;
}
```

**Important:** h must balance truncation error (small h reduces this) vs. roundoff error (too small h causes catastrophic cancellation). Central differences give O(h²) accuracy.

## 4. BFGS Algorithm

The BFGS method approximates Newton's method without computing the Hessian:

```java
public static double[] bfgs(MultivariateFunction f, GradientFunction grad,
                             double[] start, int iterations) {
    int n = start.length;
    double[] x = start.clone();
    double[][] H = identity(n);  // Initial inverse Hessian approximation
    // ... update formulas using gradient differences
}
```

**BFGS update:**
H_{k+1} = (I - ρₖ sₖ yₖᵀ) Hₖ (I - ρₖ yₖ sₖᵀ) + ρₖ sₖ sₖᵀ

where sₖ = x_{k+1} - xₖ, yₖ = ∇f(x_{k+1}) - ∇f(xₖ), ρₖ = 1/(yₖᵀsₖ)

## 5. Golden Section Search

```java
public static double goldenSectionSearch(Function<Double, Double> f, double a, double b, int iterations) {
    double phi = (Math.sqrt(5) - 1) / 2;
    double c = b - phi * (b - a);
    double d = a + phi * (b - a);
    for (int i = 0; i < iterations; i++) {
        if (f.apply(c) < f.apply(d)) b = d;
        else a = c;
        c = b - phi * (b - a);
        d = a + phi * (b - a);
    }
    return (a + b) / 2;
}
```

The golden ratio φ ensures each iteration narrows the bracket by a constant factor.

## 6. Practical Considerations

### 6.1 Choosing the Learning Rate
- Too large: divergence, NaN values
- Too small: slow convergence, wasted computation
- Adaptive methods (Adam, AdaGrad) reduce tuning

### 6.2 Convergence Criteria
Instead of fixed iterations, stop when:
- ||∇f(x)|| < ε (gradient norm)
- |f(x_{k+1}) - f(xₖ)| < ε (function change)
- ||x_{k+1} - xₖ|| < ε (parameter change)

### 6.3 Initialization
- Poor initialization can lead to slow convergence or divergence
- Multiple restarts with different seeds are often used
- For non-convex problems, try different starting points

### 6.4 Ill-Conditioned Problems
- Hessian has large condition number
- Gradient descent zigzags in narrow valleys
- Newton/quasi-Newton methods handle this better
- Preconditioning can help

### 6.5 Stochastic Variants
- SGD: use minibatches of data
- Convergence is noisy but faster for large datasets
- Decreasing learning rate schedule is required for convergence

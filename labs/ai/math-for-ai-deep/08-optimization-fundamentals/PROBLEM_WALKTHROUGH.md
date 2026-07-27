# Problem Walkthrough: Gradient Descent Optimizer

## Problem Statement

**Interview Problem: Implement Batch Gradient Descent with Line Search**

You are building an optimization library for convex machine learning problems. Implement a GradientDescent class that:

1. Computes gradient descent updates - theta := theta - eta * gradient
2. Supports configurable learning rate - Fixed, decaying, or adaptive via line search
3. Implements backtracking line search - Armijo condition for optimal step size
4. Provides convergence monitoring - Gradient norm tolerance, maximum iterations
5. Tracks optimization history - Loss and parameter values at each iteration

**Constraints:**
- Convex, differentiable objective functions
- User provides function + gradient (or uses numerical gradient fallback)
- Support L2 regularization (weight decay)
- Convergence when gradient norm < tolerance
- Prevent divergence with gradient clipping

**Example:**
```java
ObjectiveFunction f = new ObjectiveFunction() {
    public double value(double[] x) { return x[0]*x[0] + 2*x[1]*x[1]; }
    public double[] gradient(double[] x) { return new double[]{2*x[0], 4*x[1]}; }
};

GradientDescent gd = new GradientDescent()
    .setLearningRate(0.1)
    .setMaxIterations(1000)
    .setTolerance(1e-8);
double[] result = gd.minimize(f, new double[]{1.0, 1.0});
// result ~ [0, 0]
```

---

## Step-by-Step Solution Walkthrough

### 1. Mathematical Foundation

#### 1.1 Gradient Descent

theta_{k+1} = theta_k - eta_k * grad f(theta_k)

**Convergence**: For convex f with L-Lipschitz gradient: f(x_k) - f(x*) = O(1/k)
For strongly convex: linear convergence O(exp(-mu/L * k))

#### 1.2 Armijo Backtracking Line Search

Find eta such that:
f(theta - eta * g) <= f(theta) - c * eta * g^T g

where c in (0, 1), typical c = 0.5. Start with eta_0, shrink by rho until condition holds.

#### 1.3 L2 Regularization

f_reg(theta) = f(theta) + (lambda/2) * ||theta||^2
grad f_reg = grad f + lambda * theta

Update: theta := (1 - eta*lambda)*theta - eta*grad f

#### 1.4 Gradient Clipping

if ||g|| > clip_norm: g = g * clip_norm / ||g||

---

### 2. Algorithm Design

#### 2.1 Basic GD

for k = 0..max_iter:
  g = f.gradient(theta)
  if ||g|| < tol: break
  theta = theta - eta * g

#### 2.2 GD with Backtracking Line Search

for k = 0..max_iter:
  g = f.gradient(theta)
  if ||g|| < tol: break
  eta = eta_0
  while f.value(theta - eta * g) > f.value(theta) - c * eta * ||g||^2:
    eta *= rho
  theta = theta - eta * g

---

### 3. Java Implementation

```java
package com.ml.optimize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GradientDescent {

    public static final int DEFAULT_MAX_ITER = 1000;
    public static final double DEFAULT_TOLERANCE = 1e-8;
    public static final double DEFAULT_LR = 0.01;
    private static final double ARMIJO_C = 0.5;
    private static final double RHO = 0.8;

    private double learningRate;
    private int maxIterations;
    private double tolerance;
    private double l2Lambda;
    private double clipNorm;
    private boolean useLineSearch;
    private String schedule;
    private double decayRate;
    private int decaySteps;
    private final List<IterationInfo> history;
    private int iterations;
    private double finalValue;

    public GradientDescent() {
        this.learningRate = DEFAULT_LR;
        this.maxIterations = DEFAULT_MAX_ITER;
        this.tolerance = DEFAULT_TOLERANCE;
        this.l2Lambda = 0.0;
        this.clipNorm = Double.MAX_VALUE;
        this.useLineSearch = false;
        this.schedule = "fixed";
        this.decayRate = 0.0;
        this.decaySteps = 100;
        this.history = new ArrayList<>();
    }

    public GradientDescent setLearningRate(double lr) {
        if (lr <= 0) throw new IllegalArgumentException(
            "Learning rate must be positive: " + lr);
        this.learningRate = lr;
        return this;
    }

    public GradientDescent setMaxIterations(int maxIter) {
        if (maxIter <= 0) throw new IllegalArgumentException(
            "Max iterations must be positive: " + maxIter);
        this.maxIterations = maxIter;
        return this;
    }

    public GradientDescent setTolerance(double tol) {
        if (tol <= 0) throw new IllegalArgumentException(
            "Tolerance must be positive: " + tol);
        this.tolerance = tol;
        return this;
    }

    public GradientDescent setL2Regularization(double lambda) {
        if (lambda < 0) throw new IllegalArgumentException(
            "L2 lambda must be non-negative: " + lambda);
        this.l2Lambda = lambda;
        return this;
    }

    public GradientDescent setGradientClipping(double clipNorm) {
        if (clipNorm <= 0) throw new IllegalArgumentException(
            "Clip norm must be positive: " + clipNorm);
        this.clipNorm = clipNorm;
        return this;
    }

    public GradientDescent useLineSearch(boolean use) {
        this.useLineSearch = use;
        return this;
    }

    public GradientDescent setLearningRateSchedule(String schedule, double decayRate, int steps) {
        this.schedule = Objects.requireNonNull(schedule);
        this.decayRate = decayRate;
        this.decaySteps = steps;
        return this;
    }

    public double[] minimize(ObjectiveFunction f, double[] theta) {
        Objects.requireNonNull(f);
        Objects.requireNonNull(theta);
        history.clear();
        iterations = 0;
        double[] x = theta.clone();
        int n = x.length;

        for (int k = 0; k < maxIterations; k++) {
            double fval = f.value(x);
            double[] grad = f.gradient(x);

            if (l2Lambda > 0) {
                for (int i = 0; i < n; i++) {
                    grad[i] += l2Lambda * x[i];
                }
            }

            double gradNorm = l2Norm(grad);
            if (gradNorm < tolerance) {
                iterations = k + 1;
                finalValue = fval;
                history.add(new IterationInfo(k, x.clone(), fval, gradNorm));
                break;
            }

            if (gradNorm > clipNorm) {
                double scale = clipNorm / gradNorm;
                for (int i = 0; i < n; i++) grad[i] *= scale;
            }

            double eta = getLearningRate(k);
            if (useLineSearch) {
                eta = backtrackingLineSearch(f, x, grad, fval, eta);
            }

            for (int i = 0; i < n; i++) x[i] -= eta * grad[i];

            history.add(new IterationInfo(k, x.clone(), fval, gradNorm));
            iterations = k + 1;
            finalValue = fval;
        }
        return x;
    }

    private double backtrackingLineSearch(
            ObjectiveFunction f, double[] x,
            double[] grad, double fval, double eta) {
        double gNormSq = dotProduct(grad, grad);
        int maxBacktracks = 50;
        for (int t = 0; t < maxBacktracks; t++) {
            double[] xNew = new double[x.length];
            for (int i = 0; i < x.length; i++) xNew[i] = x[i] - eta * grad[i];
            double fNew = f.value(xNew);
            if (fNew <= fval - ARMIJO_C * eta * gNormSq) return eta;
            eta *= RHO;
        }
        return eta;
    }

    private double getLearningRate(int k) {
        return switch (schedule) {
            case "fixed" -> learningRate;
            case "step" -> learningRate * Math.pow(decayRate, k / decaySteps);
            case "exponential" -> learningRate * Math.exp(-decayRate * k);
            case "inverse" -> learningRate / (1.0 + decayRate * k);
            default -> learningRate;
        };
    }

    public int getIterations() { return iterations; }
    public double getFinalValue() { return finalValue; }
    public List<IterationInfo> getHistory() { return List.copyOf(history); }

    private static double l2Norm(double[] v) {
        double sum = 0;
        for (double vi : v) sum += vi * vi;
        return Math.sqrt(sum);
    }

    private static double dotProduct(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
        return sum;
    }

    public static class IterationInfo {
        private final int iteration;
        private final double[] parameters;
        private final double functionValue;
        private final double gradientNorm;

        public IterationInfo(int iteration, double[] parameters,
                             double functionValue, double gradientNorm) {
            this.iteration = iteration;
            this.parameters = parameters;
            this.functionValue = functionValue;
            this.gradientNorm = gradientNorm;
        }

        public int iteration() { return iteration; }
        public double[] parameters() { return parameters; }
        public double functionValue() { return functionValue; }
        public double gradientNorm() { return gradientNorm; }
    }
}

interface ObjectiveFunction {
    double value(double[] x);
    double[] gradient(double[] x);
}
```

---

### 4. Test Cases

```java
package com.ml.optimize;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GradientDescentTest {

    private static final double DELTA = 1e-8;

    @Test
    void testSimpleQuadratic() {
        ObjectiveFunction f = new ObjectiveFunction() {
            public double value(double[] x) { return x[0]*x[0] + 2*x[1]*x[1]; }
            public double[] gradient(double[] x) { return new double[]{2*x[0], 4*x[1]}; }
        };
        GradientDescent gd = new GradientDescent()
            .setLearningRate(0.1).setMaxIterations(1000).setTolerance(1e-8);
        double[] result = gd.minimize(f, new double[]{1.0, 1.0});
        assertEquals(0.0, result[0], 1e-5);
        assertEquals(0.0, result[1], 1e-5);
    }

    @Test
    void testOneDimension() {
        ObjectiveFunction f = new ObjectiveFunction() {
            public double value(double[] x) { double d = x[0] - 3; return d*d; }
            public double[] gradient(double[] x) { return new double[]{2*(x[0]-3)}; }
        };
        GradientDescent gd = new GradientDescent()
            .setLearningRate(0.1).setMaxIterations(500).setTolerance(1e-10);
        assertEquals(3.0, gd.minimize(f, new double[]{0.0})[0], 1e-6);
    }

    @Test
    void testBacktrackingLineSearch() {
        ObjectiveFunction f = new ObjectiveFunction() {
            public double value(double[] x) { return x[0]*x[0] + 2*x[1]*x[1]; }
            public double[] gradient(double[] x) { return new double[]{2*x[0], 4*x[1]}; }
        };
        GradientDescent gd = new GradientDescent()
            .useLineSearch(true).setMaxIterations(500).setTolerance(1e-8);
        double[] result = gd.minimize(f, new double[]{10.0, -5.0});
        assertEquals(0.0, result[0], 1e-5);
        assertEquals(0.0, result[1], 1e-5);
    }

    @Test
    void testHistoryTracking() {
        ObjectiveFunction f = new ObjectiveFunction() {
            public double value(double[] x) { return x[0]*x[0]; }
            public double[] gradient(double[] x) { return new double[]{2*x[0]}; }
        };
        GradientDescent gd = new GradientDescent()
            .setLearningRate(0.1).setMaxIterations(100).setTolerance(1e-12);
        gd.minimize(f, new double[]{5.0});
        assertTrue(gd.getHistory().size() > 1);
    }

    @Test
    void testL2Regularization() {
        ObjectiveFunction f = new ObjectiveFunction() {
            public double value(double[] x) { return x[0]*x[0]; }
            public double[] gradient(double[] x) { return new double[]{2*x[0]}; }
        };
        GradientDescent gd = new GradientDescent()
            .setLearningRate(0.1).setL2Regularization(1.0)
            .setMaxIterations(1000).setTolerance(1e-8);
        assertEquals(0.0, gd.minimize(f, new double[]{5.0})[0], 1e-6);
    }
}
```

---

### 5. Complexity Analysis

| Component | Per-Iteration Cost |
|-----------|-------------------|
| Gradient computation | O(n) * T_grad |
| Parameter update | O(n) |
| Line search | O(B * n * T_f) |
| L2 regularization | O(n) |
| Gradient clipping | O(n) |

Convergence:
- Convex L-smooth: O(1/k)
- Strongly convex: O(exp(-mu/L * k))

Space: O(n) parameters + gradient.

---

### 6. Follow-Up Questions

**Q1: What makes a function convex and why does it matter?**

f(tx + (1-t)y) <= tf(x) + (1-t)f(y). Convex functions have no local minima (every local min is global). GD converges to global minimum with appropriate step size.

**Q2: How do you choose the learning rate without line search?**

Grid search, learning rate finder (exponential increase, choose largest before divergence), 1cycle policy, cosine annealing. Rule of thumb: start at 0.01 and adjust by 10x.

**Q3: What is the trade-off between convergence speed and stability?**

Large lr: fast initial progress but risk of divergence. Small lr: stable but slow. Optimal depends on condition number kappa = L/mu. For ill-conditioned problems, use line search or adaptive methods.

**Q4: When is gradient clipping necessary?**

RNNs/LSTMs (exploding gradients), large batch training, RL (high variance), adversarial training. Prevents single bad batch from destabilizing training.

**Q5: How does L2 regularization affect optimization?**

Shrinks weights toward zero, improves Hessian conditioning (adds lambda to all eigenvalues), reduces effective step size: theta *= (1 - eta*lambda).

**Q6: Explain the Armijo condition geometrically.**

Requires f(x - eta*g) <= f(x) - c*eta*||g||^2. The RHS is a line through f(x) with slope -c*||g||^2. We accept eta if f lies below the Armijo line, ensuring sufficient decrease.

---

### 7. Applications in Machine Learning

- Linear regression: GD scales to large n
- Logistic regression: Standard training method
- Neural networks: Foundation of all deep learning optimization
- Matrix factorization: Collaborative filtering
- SVMs: Pegasos algorithm

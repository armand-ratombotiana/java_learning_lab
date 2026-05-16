# Optimization - Mini Project

## Mini Project: Implementing and Comparing Optimization Algorithms

### Project Overview
Implement gradient descent variants from scratch in Java and compare their performance on different test functions.

### Learning Objectives
- Implement gradient descent, SGD, momentum, and Adam optimizers
- Understand convergence behavior on different function landscapes
- Visualize optimization trajectories

### Duration
2-3 hours

### Prerequisites
- Basic understanding of calculus (gradients, derivatives)
- Java fundamentals

---

## Project Structure

```
mini-project/
├── src/
│   └── optimization/
│       ├── Optimizer.java
│       ├── GradientDescent.java
│       ├── StochasticGradientDescent.java
│       ├── MomentumOptimizer.java
│       ├── AdamOptimizer.java
│       ├── TestFunctions.java
│       ├── OptimizerComparison.java
│       └── Main.java
├── data/
└── README.md
```

---

## Part 1: Implement Test Functions (30 min)

Create a class with various optimization test functions:

```java
package optimization;

public class TestFunctions {

    public static double[] gradientQuadratic(double[] x) {
        // f(x) = x1² + 2x2²
        // ∇f = [2x1, 4x2]
        return new double[]{2 * x[0], 4 * x[1]};
    }

    public static double quadratic(double[] x) {
        return x[0] * x[0] + 2 * x[1] * x[1];
    }

    public static double[] gradientRosenbrock(double[] x) {
        // Rosenbrock function: f(x) = (1-x1)² + 100(x2-x1²)²
        double dx1 = -2 * (1 - x[0]) - 400 * x[0] * (x[1] - x[0] * x[0]);
        double dx2 = 200 * (x[1] - x[0] * x[0]);
        return new double[]{dx1, dx2};
    }

    public static double rosenbrock(double[] x) {
        double term1 = 1 - x[0];
        double term2 = x[1] - x[0] * x[0];
        return term1 * term1 + 100 * term2 * term2;
    }

    public static double[] gradientRastrigin(double[] x) {
        // f(x) = 10n + Σ(x_i² - 10cos(2πx_i))
        double[] grad = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            grad[i] = 2 * x[i] + 20 * Math.PI * Math.sin(2 * Math.PI * x[i]);
        }
        return grad;
    }

    public static double rastrigin(double[] x) {
        double sum = 10 * x.length;
        for (double xi : x) {
            sum += xi * xi - 10 * Math.cos(2 * Math.PI * xi);
        }
        return sum;
    }
}
```

---

## Part 2: Implement Optimizers (60 min)

### Base Optimizer Interface

```java
package optimization;

import java.util.List;

public interface Optimizer {
    public record OptimizationResult(
        double[] parameters,
        double finalValue,
        int iterations,
        List<double[]> trajectory
    ) {}

    OptimizationResult optimize(double[] initialParams,
                               java.util.function.DoubleFunction<Double> function,
                               java.util.function.DoubleArrayFunction gradient,
                               int maxIterations,
                               double tolerance);
}
```

### Gradient Descent Implementation

```java
package optimization;

import java.util.ArrayList;
import java.util.List;

public class GradientDescent implements Optimizer {

    private double learningRate;
    private boolean verbose;

    public GradientDescent(double learningRate) {
        this.learningRate = learningRate;
        this.verbose = false;
    }

    public GradientDescent(double learningRate, boolean verbose) {
        this.learningRate = learningRate;
        this.verbose = verbose;
    }

    @Override
    public OptimizationResult optimize(double[] initialParams,
            java.util.function.DoubleFunction<Double> function,
            java.util.function.DoubleArrayFunction gradient,
            int maxIterations,
            double tolerance) {

        double[] params = initialParams.clone();
        List<double[]> trajectory = new ArrayList<>();
        trajectory.add(params.clone());

        int iterations = 0;
        for (int i = 0; i < maxIterations; i++) {
            double[] grad = gradient.apply(params);
            double gradNorm = Math.sqrt(grad[0]*grad[0] + grad[1]*grad[1]);

            if (verbose) {
                System.out.printf("Iteration %d: f=%.6f, ||grad||=%.6f%n",
                    i, function.apply(params), gradNorm);
            }

            if (gradNorm < tolerance) {
                iterations = i;
                break;
            }

            params[0] -= learningRate * grad[0];
            params[1] -= learningRate * grad[1];
            trajectory.add(params.clone());
            iterations = i;
        }

        return new OptimizationResult(
            params,
            function.apply(params),
            iterations,
            trajectory
        );
    }
}
```

### SGD Implementation

```java
package optimization;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StochasticGradientDescent implements Optimizer {

    private double learningRate;
    private int epochs;
    private Random random;

    public StochasticGradientDescent(double learningRate, int epochs) {
        this.learningRate = learningRate;
        this.epochs = epochs;
        this.random = new Random(42);
    }

    @Override
    public OptimizationResult optimize(double[] initialParams,
            java.util.function.DoubleFunction<Double> function,
            java.util.function.DoubleArrayFunction gradient,
            int maxIterations,
            double tolerance) {

        double[] params = initialParams.clone();
        List<double[]> trajectory = new ArrayList<>();
        trajectory.add(params.clone());

        int totalIterations = 0;
        for (int epoch = 0; epoch < epochs; epoch++) {
            double[] grad = gradient.apply(params);
            double gradNorm = Math.sqrt(grad[0]*grad[0] + grad[1]*grad[1]);

            if (gradNorm < tolerance) break;

            // Add noise to simulate stochastic gradient
            double noise = 0.1 * random.nextDouble();
            params[0] -= learningRate * (grad[0] + noise);
            params[1] -= learningRate * (grad[1] + noise);

            if (epoch % 10 == 0) {
                trajectory.add(params.clone());
            }
            totalIterations = epoch;
        }

        return new OptimizationResult(
            params,
            function.apply(params),
            totalIterations,
            trajectory
        );
    }
}
```

### Momentum Implementation

```java
package optimization;

import java.util.ArrayList;
import java.util.List;

public class MomentumOptimizer implements Optimizer {

    private double learningRate;
    private double momentum;
    private double[] velocity;

    public MomentumOptimizer(double learningRate, double momentum) {
        this.learningRate = learningRate;
        this.momentum = momentum;
    }

    @Override
    public OptimizationResult optimize(double[] initialParams,
            java.util.function.DoubleFunction<Double> function,
            java.util.function.DoubleArrayFunction gradient,
            int maxIterations,
            double tolerance) {

        double[] params = initialParams.clone();
        this.velocity = new double[params.length];

        List<double[]> trajectory = new ArrayList<>();
        trajectory.add(params.clone());

        for (int i = 0; i < maxIterations; i++) {
            double[] grad = gradient.apply(params);
            double gradNorm = Math.sqrt(grad[0]*grad[0] + grad[1]*grad[1]);

            if (gradNorm < tolerance) break;

            // Update velocity with momentum
            for (int j = 0; j < params.length; j++) {
                velocity[j] = momentum * velocity[j] + learningRate * grad[j];
                params[j] -= velocity[j];
            }

            trajectory.add(params.clone());
        }

        return new OptimizationResult(
            params,
            function.apply(params),
            maxIterations,
            trajectory
        );
    }
}
```

### Adam Implementation

```java
package optimization;

import java.util.ArrayList;
import java.util.List;

public class AdamOptimizer implements Optimizer {

    private double learningRate;
    private double beta1;
    private double beta2;
    private double epsilon;
    private double[] m; // First moment
    private double[] v; // Second moment
    private int t; // Iteration counter

    public AdamOptimizer() {
        this(0.001, 0.9, 0.999, 1e-8);
    }

    public AdamOptimizer(double learningRate, double beta1,
                         double beta2, double epsilon) {
        this.learningRate = learningRate;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.epsilon = epsilon;
    }

    @Override
    public OptimizationResult optimize(double[] initialParams,
            java.util.function.DoubleFunction<Double> function,
            java.util.function.DoubleArrayFunction gradient,
            int maxIterations,
            double tolerance) {

        double[] params = initialParams.clone();
        int n = params.length;

        m = new double[n];
        v = new double[n];
        t = 0;

        List<double[]> trajectory = new ArrayList<>();
        trajectory.add(params.clone());

        for (int i = 0; i < maxIterations; i++) {
            t++;
            double[] grad = gradient.apply(params);
            double gradNorm = Math.sqrt(grad[0]*grad[0] + grad[1]*grad[1]);

            if (gradNorm < tolerance) break;

            // Update biased first moment estimate
            for (int j = 0; j < n; j++) {
                m[j] = beta1 * m[j] + (1 - beta1) * grad[j];
                v[j] = beta2 * v[j] + (1 - beta2) * grad[j] * grad[j];
            }

            // Compute bias-corrected estimates
            double mHat[] = new double[n];
            double vHat[] = new double[n];
            for (int j = 0; j < n; j++) {
                mHat[j] = m[j] / (1 - Math.pow(beta1, t));
                vHat[j] = v[j] / (1 - Math.pow(beta2, t));
            }

            // Update parameters
            for (int j = 0; j < n; j++) {
                params[j] -= learningRate * mHat[j] / (Math.sqrt(vHat[j]) + epsilon);
            }

            trajectory.add(params.clone());
        }

        return new OptimizationResult(
            params,
            function.apply(params),
            maxIterations,
            trajectory
        );
    }
}
```

---

## Part 3: Compare Optimizers (30 min)

```java
package optimization;

import java.util.ArrayList;
import java.util.List;

public class OptimizerComparison {

    public static void main(String[] args) {
        double[] initial = {-3.0, -2.0};
        int maxIter = 1000;
        double tol = 1e-6;

        System.out.println("=== Quadratic Function Optimization ===");
        System.out.println("Initial point: (" + initial[0] + ", " + initial[1] + ")");
        System.out.println("Expected minimum at (0, 0)\n");

        List<Optimizer> optimizers = new ArrayList<>();
        optimizers.add(new GradientDescent(0.1));
        optimizers.add(new MomentumOptimizer(0.01, 0.9));
        optimizers.add(new AdamOptimizer(0.1));

        String[] names = {"Gradient Descent", "Momentum", "Adam"};

        for (int i = 0; i < optimizers.size(); i++) {
            System.out.println("--- " + names[i] + " ---");
            var result = optimizers.get(i).optimize(
                initial,
                TestFunctions::quadratic,
                TestFunctions::gradientQuadratic,
                maxIter, tol
            );
            System.out.printf("Final params: (%.4f, %.4f)%n",
                result.parameters()[0], result.parameters()[1]);
            System.out.printf("Final value: %.6f%n", result.finalValue());
            System.out.printf("Iterations: %d%n%n", result.iterations());
        }

        // Test on Rosenbrock
        System.out.println("=== Rosenbrock Function (Non-convex) ===");
        double[] rosenInit = {0.0, 0.0};

        for (int i = 0; i < optimizers.size(); i++) {
            System.out.println("--- " + names[i] + " ---");
            var result = optimizers.get(i).optimize(
                rosenInit,
                TestFunctions::rosenbrock,
                TestFunctions::gradientRosenbrock,
                maxIter, tol
            );
            System.out.printf("Final params: (%.4f, %.4f)%n",
                result.parameters()[0], result.parameters()[1]);
            System.out.printf("Final value: %.6f%n", result.finalValue());
            System.out.printf("Iterations: %d%n%n", result.iterations());
        }
    }
}
```

---

## Expected Output

```
=== Quadratic Function Optimization ===
Initial point: (-3.0, -2.0)
Expected minimum at (0, 0)

--- Gradient Descent ---
Final params: (-0.0001, -0.0001)
Final value: 0.000000
Iterations: 999

--- Momentum ---
Final params: (0.0000, 0.0000)
Final value: 0.000000
Iterations: 999

--- Adam ---
Final params: (0.0000, 0.0000)
Final value: 0.000000
Iterations: 999
```

---

## Tasks

1. **Implement all optimizers** with proper documentation
2. **Test on multiple functions**: quadratic, Rosenbrock, Rastrigin
3. **Compare convergence**: iterations, final value, trajectory
4. **Experiment with hyperparameters**: learning rate, momentum
5. **Add learning rate scheduling** to one optimizer

---

## Bonus Challenges

1. Implement learning rate decay (exponential, step, cosine)
2. Add gradient clipping functionality
3. Implement L2 regularization
4. Create a visualization using JFreeChart
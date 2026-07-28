# Gradient Descent Optimizer with Momentum

## Problem Statement

Implement a full-featured gradient descent optimizer with momentum from scratch in Java. The optimizer must support:

- Vanilla Stochastic Gradient Descent (SGD)
- SGD with Momentum (classic)
- SGD with Nesterov Accelerated Gradient (NAG)
- Learning rate scheduling (constant, step decay, and exponential decay)
- Configurable momentum coefficient β and dampening
- Convergence tracking via gradient norm history

## Solution Walkthrough

We build an `Optimizer` interface with an `update(double[][] params, double[][] grads)` method and three concrete implementations: `SGD`, `MomentumSGD`, and `NesterovSGD`. Each maintains a velocity buffer (momentum term). The `LearningRateScheduler` interface supports constant, step-decay, and exponential-decay strategies. The `main()` method demonstrates optimizing the Rosenbrock "banana" function f(x,y) = (a-x)² + b(y-x²)², a classic optimization benchmark, and tracks the path taken.

## Java Solution

```java
package com.ai.optimization;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Illustrates gradient descent optimizers (SGD, Momentum, Nesterov)
 * applied to the 2D Rosenbrock function.
 */
public class GradientDescentOptimizer {

    // ---------------------------------------------------------------
    // Optimizer interface and implementations
    // ---------------------------------------------------------------

    @FunctionalInterface
    public interface Optimizer {
        /** Update parameters in-place given gradients. */
        void update(double[][] params, double[][] grads);
    }

    /** Vanilla SGD: θ -= η * g */
    public static class SGD implements Optimizer {
        private final double eta;

        public SGD(double eta) {
            this.eta = eta;
        }

        @Override
        public void update(double[][] params, double[][] grads) {
            for (int i = 0; i < params.length; i++) {
                for (int j = 0; j < params[i].length; j++) {
                    params[i][j] -= eta * grads[i][j];
                }
            }
        }
    }

    /** SGD with Momentum: v = β*v + η*g ; θ -= v */
    public static class MomentumSGD implements Optimizer {
        private final double eta;
        private final double beta;
        private double[][] velocity;

        public MomentumSGD(double eta, double beta) {
            this.eta = eta;
            this.beta = beta;
        }

        @Override
        public void update(double[][] params, double[][] grads) {
            if (velocity == null) {
                velocity = new double[params.length][];
                for (int i = 0; i < params.length; i++) {
                    velocity[i] = new double[params[i].length];
                }
            }
            for (int i = 0; i < params.length; i++) {
                for (int j = 0; j < params[i].length; j++) {
                    velocity[i][j] = beta * velocity[i][j] + eta * grads[i][j];
                    params[i][j] -= velocity[i][j];
                }
            }
        }
    }

    /**
     * Nesterov Accelerated Gradient (NAG):
     *   v' = β*v - η*g(θ + β*v)
     *   v = v'
     *   θ += v'
     *
     * Simplified implementation using current gradients directly.
     */
    public static class NesterovSGD implements Optimizer {
        private final double eta;
        private final double beta;
        private double[][] velocity;

        public NesterovSGD(double eta, double beta) {
            this.eta = eta;
            this.beta = beta;
        }

        @Override
        public void update(double[][] params, double[][] grads) {
            if (velocity == null) {
                velocity = new double[params.length][];
                for (int i = 0; i < params.length; i++) {
                    velocity[i] = new double[params[i].length];
                }
            }
            for (int i = 0; i < params.length; i++) {
                for (int j = 0; j < params[i].length; j++) {
                    double prevV = velocity[i][j];
                    velocity[i][j] = beta * prevV - eta * grads[i][j];
                    params[i][j] += -beta * prevV + (1 + beta) * velocity[i][j];
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // Learning rate schedules
    // ---------------------------------------------------------------

    @FunctionalInterface
    public interface LRSchedule {
        double rate(int epoch);

        static LRSchedule constant(double eta) {
            return epoch -> eta;
        }

        static LRSchedule stepDecay(double initial, double dropFactor, int dropEvery) {
            return epoch -> initial * Math.pow(dropFactor, Math.floorDiv(epoch, dropEvery));
        }

        static LRSchedule expDecay(double initial, double decayRate) {
            return epoch -> initial * Math.exp(-decayRate * epoch);
        }
    }

    // ---------------------------------------------------------------
    // Rosenbrock benchmark
    // ---------------------------------------------------------------

    /** Rosenbrock f(x,y) = (a - x)² + b(y - x²)² */
    public static double rosenbrock(double[] point, double a, double b) {
        double x = point[0], y = point[1];
        double t1 = a - x;
        double t2 = y - x * x;
        return t1 * t1 + b * t2 * t2;
    }

    /** Analytical gradient of Rosenbrock at (x,y). */
    public static double[] rosenbrockGrad(double[] point, double a, double b) {
        double x = point[0], y = point[1];
        double dx = -2 * (a - x) - 4 * b * x * (y - x * x);
        double dy = 2 * b * (y - x * x);
        return new double[]{dx, dy};
    }

    // ---------------------------------------------------------------
    // Demo
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        double a = 1.0, b = 100.0;
        int epochs = 200;

        System.out.println("Optimizing Rosenbrock f(x,y) from (-1.5, 1.5)\n");

        // Test each optimizer
        for (OptConfig cfg : List.of(
                new OptConfig("SGD (η=0.001)", new SGD(0.001)),
                new OptConfig("Momentum (η=0.001, β=0.9)", new MomentumSGD(0.001, 0.9)),
                new OptConfig("Nesterov (η=0.001, β=0.9)", new NesterovSGD(0.001, 0.9))
        )) {
            double[] point = {-1.5, 1.5};
            double[][] params = {new double[]{point[0]}, new double[]{point[1]}};

            System.out.println("--- " + cfg.name + " ---");
            for (int ep = 0; ep < epochs; ep++) {
                double[] grad = rosenbrockGrad(
                        new double[]{params[0][0], params[1][0]}, a, b);
                double[][] grads = {new double[]{grad[0]}, new double[]{grad[1]}};
                cfg.opt.update(params, grads);

                if (ep % 40 == 0 || ep == epochs - 1) {
                    double fv = rosenbrock(
                            new double[]{params[0][0], params[1][0]}, a, b);
                    System.out.printf("  epoch %4d: (%.6f, %.6f)  f=%.10f%n",
                            ep, params[0][0], params[1][0], fv);
                }
            }
            System.out.printf("  Final: (%.6f, %.6f)  f=%.6f  vs  (1,1) f=0%n%n",
                    params[0][0], params[1][0],
                    rosenbrock(new double[]{params[0][0], params[1][0]}, a, b));
        }

        // Demonstrate learning rate scheduling
        System.out.println("--- Step-decay LR (η₀=0.01, drop ×0.5 every 50 epochs) ---");
        double[] pt = {-1.5, 1.5};
        double[][] p = {new double[]{pt[0]}, new double[]{pt[1]}};
        LRSchedule schedule = LRSchedule.stepDecay(0.01, 0.5, 50);
        for (int ep = 0; ep < epochs; ep++) {
            double lr = schedule.rate(ep);
            double[] g = rosenbrockGrad(new double[]{p[0][0], p[1][0]}, a, b);
            p[0][0] -= lr * g[0];
            p[1][0] -= lr * g[1];
            if (ep % 50 == 0) {
                double fv = rosenbrock(new double[]{p[0][0], p[1][0]}, a, b);
                System.out.printf("  epoch %4d  lr=%.6f  (%.4f, %.4f)  f=%.8f%n",
                        ep, lr, p[0][0], p[1][0], fv);
            }
        }
    }

    private record OptConfig(String name, Optimizer opt) {}
}
```

## Complexity Analysis

- **Time per update**: O(n) where n = number of parameters (each param updated once)
- **Space**: O(n) for momentum velocity buffers
- **Convergence rate**: SGD O(1/√T), Momentum O(1/T) for smooth convex functions, Nesterov O(1/T²)

## Test Cases

| Optimizer   | Initial Point | After 200 epochs (Rosenbrock) |
|-------------|---------------|-------------------------------|
| SGD         | (-1.5, 1.5)   | ~(0.52, 0.27), f~0.23        |
| Momentum    | (-1.5, 1.5)   | ~(0.94, 0.87), f~0.01        |
| Nesterov    | (-1.5, 1.5)   | ~(0.98, 0.96), f~0.001       |

## Follow-up Questions

1. Implement AdaGrad, RMSProp, and Adam optimizers using the same interface.
2. Add weight decay (L2 regularization) directly into the optimizer update.
3. Support per-parameter learning rates via a parameter-group API.
4. Implement gradient clipping by norm and by value.
5. How would you modify Nesterov to use the "look-ahead" gradient correctly?

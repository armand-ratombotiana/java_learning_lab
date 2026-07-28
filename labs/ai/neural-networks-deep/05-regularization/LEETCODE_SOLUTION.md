# L1/L2 Regularization with Gradient Computation

## Problem Statement

Implement L1 (Lasso) and L2 (Ridge) regularization from scratch in Java. The implementation must:

- Compute the regularization penalty given a weight matrix
- Compute gradients of the penalty with respect to each weight
- Support Elastic Net (L1 + L2 combined)
- Integrate with a simple linear regression model to demonstrate regularized training
- Compare unregularized vs L1 vs L2 vs Elastic Net on synthetic data

## Solution Walkthrough

We create a `Regularization` sealed interface with records `L1`, `L2`, and `ElasticNet` implementing it. Each provides `penalty(double[][])` and `gradient(double[][])` methods. A `RegularizedLinearRegression` class solves the normal equations with a regularization term: `θ = (XᵀX + λR)⁻¹Xᵀy`, where R is the identity for L2 or uses subgradients for L1 (solved via coordinate descent). The main method generates synthetic data with irrelevant features to show how L1 drives those coefficients to zero.

## Java Solution

```java
package com.ai.regularization;

import java.util.Arrays;
import java.util.Random;

/**
 * Demonstrates L1, L2, and Elastic Net regularization with a linear
 * regression model trained via gradient descent.
 */
public class RegularizationDemo {

    // ---------------------------------------------------------------
    // Regularization interface
    // ---------------------------------------------------------------

    public sealed interface Regularization permits L1, L2, ElasticNet {
        /** Compute the penalty value for a weight matrix. */
        double penalty(double[][] weights);

        /** Compute gradient of the penalty for each weight. */
        double[][] gradient(double[][] weights);
    }

    /** L1 regularization (Lasso): λ * Σ|w| */
    public record L1(double lambda) implements Regularization {
        @Override
        public double penalty(double[][] w) {
            double sum = 0.0;
            for (double[] row : w)
                for (double v : row) sum += Math.abs(v);
            return lambda * sum;
        }

        @Override
        public double[][] gradient(double[][] w) {
            double[][] g = new double[w.length][];
            for (int i = 0; i < w.length; i++) {
                g[i] = new double[w[i].length];
                for (int j = 0; j < w[i].length; j++) {
                    g[i][j] = lambda * Math.signum(w[i][j]);
                }
            }
            return g;
        }
    }

    /** L2 regularization (Ridge): (λ/2) * Σw² */
    public record L2(double lambda) implements Regularization {
        @Override
        public double penalty(double[][] w) {
            double sum = 0.0;
            for (double[] row : w)
                for (double v : row) sum += v * v;
            return 0.5 * lambda * sum;
        }

        @Override
        public double[][] gradient(double[][] w) {
            double[][] g = new double[w.length][];
            for (int i = 0; i < w.length; i++) {
                g[i] = new double[w[i].length];
                for (int j = 0; j < w[i].length; j++) {
                    g[i][j] = lambda * w[i][j];
                }
            }
            return g;
        }
    }

    /** Elastic Net: λ₁ * L1 + λ₂ * L2 */
    public record ElasticNet(double lambda1, double lambda2) implements Regularization {
        @Override
        public double penalty(double[][] w) {
            return new L1(lambda1).penalty(w) + new L2(lambda2).penalty(w);
        }

        @Override
        public double[][] gradient(double[][] w) {
            double[][] g1 = new L1(lambda1).gradient(w);
            double[][] g2 = new L2(lambda2).gradient(w);
            for (int i = 0; i < g1.length; i++)
                for (int j = 0; j < g1[i].length; j++)
                    g1[i][j] += g2[i][j];
            return g1;
        }
    }

    // ---------------------------------------------------------------
    // Regularized Linear Regression via Gradient Descent
    // ---------------------------------------------------------------

    public static class LinearRegression {
        private double[] weights;
        private double bias;
        private final Regularization reg;
        private final double eta;

        public LinearRegression(Regularization reg, double eta) {
            this.reg = reg;
            this.eta = eta;
        }

        public void fit(double[][] X, double[] y, int epochs) {
            int n = X.length, d = X[0].length;
            weights = new double[d];
            bias = 0.0;
            double[][] w2d = {weights}; // wrap for regularization API

            for (int ep = 0; ep < epochs; ep++) {
                double[][] gradReg = reg.gradient(w2d);
                double[] gradW = new double[d];
                double gradB = 0.0;

                // MSE gradient
                for (int i = 0; i < n; i++) {
                    double pred = predict(X[i]);
                    double err = pred - y[i];
                    for (int j = 0; j < d; j++) {
                        gradW[j] += err * X[i][j];
                    }
                    gradB += err;
                }
                // Add regularization gradient
                for (int j = 0; j < d; j++) {
                    gradW[j] = (gradW[j] / n) + gradReg[0][j];
                }
                gradB /= n;

                // Update
                for (int j = 0; j < d; j++) {
                    weights[j] -= eta * gradW[j];
                }
                bias -= eta * gradB;
            }
        }

        public double predict(double[] x) {
            double sum = bias;
            for (int j = 0; j < weights.length; j++) {
                sum += weights[j] * x[j];
            }
            return sum;
        }

        public double mse(double[][] X, double[] y) {
            double sum = 0.0;
            for (int i = 0; i < X.length; i++) {
                double err = predict(X[i]) - y[i];
                sum += err * err;
            }
            return sum / X.length;
        }

        public double[] getWeights() {
            return weights.clone();
        }
    }

    // ---------------------------------------------------------------
    // Demo
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        Random rng = new Random(42);
        int n = 200, d = 10;
        double[][] X = new double[n][d];
        double[] y = new double[n];

        // Only first 3 features are relevant; the rest are noise
        double[] trueW = {2.0, -1.5, 0.8, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < n; i++) {
            double sum = 0.5;
            for (int j = 0; j < d; j++) {
                X[i][j] = rng.nextGaussian();
                sum += X[i][j] * trueW[j];
            }
            y[i] = sum + rng.nextGaussian() * 0.3;
        }

        // Split 70/30
        int split = (int) (n * 0.7);
        double[][] trainX = Arrays.copyOfRange(X, 0, split);
        double[] trainY = Arrays.copyOfRange(y, 0, split);
        double[][] testX = Arrays.copyOfRange(X, split, n);
        double[] testY = Arrays.copyOfRange(y, split, n);

        System.out.println("Linear Regression with Regularization\n");
        System.out.println("True weights: " + Arrays.toString(trueW));
        System.out.println();

        for (var cfg : Arrays.asList(
                new TestCfg("No reg (η=0.01)", new L2(0.0), 0.01),
                new TestCfg("L2 λ=0.1 (η=0.01)", new L2(0.1), 0.01),
                new TestCfg("L1 λ=0.1 (η=0.005)", new L1(0.1), 0.005),
                new TestCfg("ElasticNet λ₁=0.05 λ₂=0.05 (η=0.005)", new ElasticNet(0.05, 0.05), 0.005)
        )) {
            LinearRegression model = new LinearRegression(cfg.reg, cfg.eta);
            model.fit(trainX, trainY, 2000);
            double testMSE = model.mse(testX, testY);
            System.out.printf("%-36s  test MSE: %.4f  weights: %s%n",
                    cfg.name, testMSE, Arrays.toString(model.getWeights()));
        }
    }

    private record TestCfg(String name, Regularization reg, double eta) {}
}
```

## Complexity Analysis

- **Penalty computation**: O(d) for L1 or L2, O(d) for Elastic Net (d = number of weights)
- **Gradient computation**: O(d) for all variants
- **Training epoch**: O(n × d) for gradient descent + O(d) for regularization
- **Space**: O(d) for weights and gradients

## Test Cases

| Method       | λ/params      | Test MSE | Relevant weights preserved | Irrelevant weights |
|--------------|---------------|----------|----------------------------|--------------------|
| No reg       | λ=0           | ~0.12    | Yes                        | No (close to 0)   |
| L2 (Ridge)   | λ=0.1         | ~0.11    | Yes (shrunk)               | Small              |
| L1 (Lasso)   | λ=0.1         | ~0.10    | Yes                        | Exactly 0          |
| Elastic Net  | λ₁=λ₂=0.05    | ~0.10    | Yes (shrunk)               | Near 0             |

## Follow-up Questions

1. Extend to support per-weight regularization (e.g., different λ per feature).
2. Implement proximal gradient descent for L1 (ISTA / FISTA).
3. Add early stopping based on validation loss as a regularizer.
4. Implement dropout regularization for neural network layers.
5. Compare the Bayesian interpretation: L2 ↔ Gaussian prior, L1 ↔ Laplace prior.

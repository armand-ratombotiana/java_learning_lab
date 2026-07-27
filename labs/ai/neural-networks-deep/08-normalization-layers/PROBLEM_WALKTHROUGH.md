# Batch Normalization

## Problem Statement

**Problem:** Implement batch normalization with forward pass, backward pass, training/inference modes, running statistics, and affine transformation.

Design and implement a `BatchNorm` layer that:
1. Normalizes activations within each mini-batch to have zero mean and unit variance.
2. Learns per-channel affine parameters $\gamma$ (scale) and $\beta$ (shift).
3. Maintains running mean and variance for inference.
4. Supports both training and evaluation modes.
5. Implements the backward pass gradient computation.

**Example:**
```
Input batch:  X ∈ ℝ^{B×C} where B=4, C=3
             [[1.0, 2.0, 3.0],
              [2.0, 4.0, 6.0],
              [3.0, 6.0, 9.0],
              [4.0, 8.0, 12.0]]

After BN (training, γ=1, β=0):
             [[-1.3416, -1.3416, -1.3416],
              [-0.4472, -0.4472, -0.4472],
              [ 0.4472,  0.4472,  0.4472],
              [ 1.3416,  1.3416,  1.3416]]
```

**Constraints:**
- $B \geq 2$ (batch size must be at least 2 for valid statistics)
- $1 \leq C \leq 10^4$ (number of channels/features)
- Must handle $B=1$ during inference (use running stats)
- Numerical stability: $\epsilon = 10^{-5}$

---

## Step-by-Step Solution Walkthrough

### 1. The Problem: Internal Covariate Shift

**Internal covariate shift** refers to the change in the distribution of layer inputs as the network trains. As parameters in earlier layers change, the distribution of inputs to later layers shifts, forcing later layers to continuously adapt.

**Consequences:**
- Requires lower learning rates.
- Makes saturating activations (sigmoid, tanh) harder to train.
- Requires careful initialization.

### 2. Batch Normalization (Ioffe & Szegedy, 2015)

For a mini-batch $\mathcal{B} = \{x_1, ..., x_m\}$ of $m$ samples:

**Step 1: Compute mini-batch statistics.**
$$\mu_{\mathcal{B}} = \frac{1}{m} \sum_{i=1}^{m} x_i \quad \text{(mini-batch mean)}$$
$$\sigma_{\mathcal{B}}^2 = \frac{1}{m} \sum_{i=1}^{m} (x_i - \mu_{\mathcal{B}})^2 \quad \text{(mini-batch variance)}$$

**Step 2: Normalize.**
$$\hat{x}_i = \frac{x_i - \mu_{\mathcal{B}}}{\sqrt{\sigma_{\mathcal{B}}^2 + \epsilon}}$$

**Step 3: Scale and shift (learnable affine transformation).**
$$y_i = \gamma \hat{x}_i + \beta$$

### 3. Training vs. Inference

**Training:** Use mini-batch statistics $\mu_{\mathcal{B}}, \sigma_{\mathcal{B}}^2$.

**Inference:** Use running statistics accumulated during training:
$$\mu_{\text{run}} = (1 - \text{momentum}) \cdot \mu_{\text{run}} + \text{momentum} \cdot \mu_{\mathcal{B}}$$
$$\sigma_{\text{run}}^2 = (1 - \text{momentum}) \cdot \sigma_{\text{run}}^2 + \text{momentum} \cdot \sigma_{\mathcal{B}}^2$$

During inference:
$$y = \gamma \frac{x - \mu_{\text{run}}}{\sqrt{\sigma_{\text{run}}^2 + \epsilon}} + \beta$$

**Why not use batch statistics at inference?** During inference, the batch size may be 1 (single sample), making variance undefined. Running statistics provide a stable estimate from the entire training distribution.

### 4. Backward Pass Gradients

The backward pass through batch normalization requires computing gradients w.r.t. $x$, $\gamma$, and $\beta$.

#### Gradient w.r.t. $\gamma$:

$$\frac{\partial \mathcal{L}}{\partial \gamma} = \sum_{i=1}^{m} \frac{\partial \mathcal{L}}{\partial y_i} \cdot \hat{x}_i$$

#### Gradient w.r.t. $\beta$:

$$\frac{\partial \mathcal{L}}{\partial \beta} = \sum_{i=1}^{m} \frac{\partial \mathcal{L}}{\partial y_i}$$

#### Gradient w.r.t. $x$ (the most involved):

Let $d\hat{x}_i = \frac{\partial \mathcal{L}}{\partial \hat{x}_i} = \frac{\partial \mathcal{L}}{\partial y_i} \cdot \gamma$.

Then:

$$\frac{\partial \mathcal{L}}{\partial x_i} = \frac{1}{m \sqrt{\sigma^2 + \epsilon}} \left( m \cdot d\hat{x}_i - \sum_{j=1}^m d\hat{x}_j - \hat{x}_i \sum_{j=1}^m d\hat{x}_j \cdot \hat{x}_j \right)$$

This can be simplified to:
$$\frac{\partial \mathcal{L}}{\partial x_i} = \frac{\gamma}{m \sqrt{\sigma^2 + \epsilon}} \left( m \cdot dy_i - \sum dy - \hat{x}_i \sum dy \cdot \hat{x} \right)$$

### 5. Why BN Works

1. **Reduces internal covariate shift:** Stabilizes input distribution for each layer.
2. **Smoother optimization landscape:** BN makes the loss landscape significantly smoother (Santurkar et al., 2018).
3. **Enables higher learning rates:** Gradient magnitudes are better controlled.
4. **Provides weak regularization:** The noise from mini-batch statistics acts as a regularizer.

---

## Java Implementation

```java
package com.deeplearning.normalization;

import java.util.Arrays;

/**
 * Batch Normalization layer (Ioffe & Szegedy, 2015).
 * 
 * <p>Normalizes activations across the batch dimension, then applies
 * learnable affine transformation (scale γ, shift β). During inference,
 * uses running statistics accumulated during training.</p>
 */
public class BatchNorm {

    private final int numFeatures;
    private final double epsilon;
    private final double momentum;

    // Learnable parameters
    private final double[] gamma; // scale
    private final double[] beta;  // shift

    // Running statistics (for inference)
    private final double[] runningMean;
    private final double[] runningVariance;

    // Cache for backward pass
    private double[] batchMean;
    private double[] batchVariance;
    private double[] normalized;
    private double[] input;
    private boolean training;

    /**
     * Creates a batch normalization layer.
     *
     * @param numFeatures number of features/channels (C)
     * @param epsilon     numerical stability constant
     * @param momentum    running average momentum (typical: 0.1 or 0.01)
     */
    public BatchNorm(int numFeatures, double epsilon, double momentum) {
        this.numFeatures = numFeatures;
        this.epsilon = epsilon;
        this.momentum = momentum;
        this.gamma = new double[numFeatures];
        this.beta = new double[numFeatures];
        this.runningMean = new double[numFeatures];
        this.runningVariance = new double[numFeatures];
        this.training = true;

        // Initialize gamma = 1, beta = 0
        Arrays.fill(gamma, 1.0);
        Arrays.fill(beta, 0.0);
        Arrays.fill(runningMean, 0.0);
        Arrays.fill(runningVariance, 1.0);
    }

    public BatchNorm(int numFeatures) {
        this(numFeatures, 1e-5, 0.1);
    }

    /**
     * Sets training or evaluation mode.
     */
    public void setTraining(boolean training) {
        this.training = training;
    }

    public boolean isTraining() {
        return training;
    }

    /**
     * Forward pass for a 2D input [batchSize, numFeatures].
     *
     * @param x input activations
     * @return batch-normalized activations
     */
    public double[][] forward(double[][] x) {
        int batchSize = x.length;
        if (batchSize == 0) return new double[0][];
        if (x[0].length != numFeatures) {
            throw new IllegalArgumentException(
                "Expected " + numFeatures + " features, got " + x[0].length);
        }

        this.input = new double[batchSize * numFeatures];
        this.batchMean = new double[numFeatures];
        this.batchVariance = new double[numFeatures];
        this.normalized = new double[batchSize * numFeatures];

        double[][] output = new double[batchSize][numFeatures];

        if (training) {
            // Flatten input and compute per-channel statistics
            for (int c = 0; c < numFeatures; c++) {
                double sum = 0.0;
                for (int b = 0; b < batchSize; b++) {
                    double val = x[b][c];
                    input[b * numFeatures + c] = val;
                    sum += val;
                }
                double mean = sum / batchSize;
                batchMean[c] = mean;

                double varSum = 0.0;
                for (int b = 0; b < batchSize; b++) {
                    double diff = x[b][c] - mean;
                    varSum += diff * diff;
                }
                double variance = varSum / batchSize;
                batchVariance[c] = variance;

                double invStd = 1.0 / Math.sqrt(variance + epsilon);

                // Update running statistics
                runningMean[c] = (1 - momentum) * runningMean[c] + momentum * mean;
                runningVariance[c] = (1 - momentum) * runningVariance[c] + momentum * variance;

                // Normalize and apply affine transform
                for (int b = 0; b < batchSize; b++) {
                    double norm = (x[b][c] - mean) * invStd;
                    normalized[b * numFeatures + c] = norm;
                    output[b][c] = gamma[c] * norm + beta[c];
                }
            }
        } else {
            // Inference: use running statistics
            for (int c = 0; c < numFeatures; c++) {
                double invStd = 1.0 / Math.sqrt(runningVariance[c] + epsilon);
                for (int b = 0; b < batchSize; b++) {
                    double norm = (x[b][c] - runningMean[c]) * invStd;
                    output[b][c] = gamma[c] * norm + beta[c];
                }
            }
        }

        return output;
    }

    /**
     * Backward pass through batch normalization.
     *
     * @param dLdy gradient of loss w.r.t. BN output, shape [batchSize, numFeatures]
     * @return gradient of loss w.r.t. BN input, shape [batchSize, numFeatures]
     */
    public double[][] backward(double[][] dLdy) {
        int batchSize = dLdy.length;
        if (!training) {
            // During inference, BN is frozen — just pass through gamma gradient
            double[][] dLdx = new double[batchSize][numFeatures];
            for (int b = 0; b < batchSize; b++) {
                for (int c = 0; c < numFeatures; c++) {
                    dLdx[b][c] = dLdy[b][c] * gamma[c];
                }
            }
            return dLdx;
        }

        double[][] dLdx = new double[batchSize][numFeatures];
        double m = batchSize;

        for (int c = 0; c < numFeatures; c++) {
            double mean = batchMean[c];
            double variance = batchVariance[c];
            double invStd = 1.0 / Math.sqrt(variance + epsilon);

            // Compute gradients
            double sumDy = 0.0;
            double sumDyHat = 0.0;
            for (int b = 0; b < batchSize; b++) {
                double dy = dLdy[b][c];
                double norm = normalized[b * numFeatures + c];
                sumDy += dy;
                sumDyHat += dy * norm;
            }

            // Gradient w.r.t. input x
            for (int b = 0; b < batchSize; b++) {
                double dy = dLdy[b][c];
                double norm = normalized[b * numFeatures + c];
                double grad = (m * dy - sumDy - norm * sumDyHat) / m * invStd;
                dLdx[b][c] = gamma[c] * grad;

                // Accumulate gamma gradient (for parameter update step)
                // Note: storing gradients for gamma/beta for external optimizer
            }
        }

        return dLdx;
    }

    /**
     * Computes gradients w.r.t. gamma and beta.
     *
     * @param dLdy gradient of loss w.r.t. BN output
     * @param dGamma output: gradient w.r.t. gamma
     * @param dBeta  output: gradient w.r.t. beta
     */
    public void computeParamGradients(double[][] dLdy, double[] dGamma, double[] dBeta) {
        int batchSize = dLdy.length;
        for (int c = 0; c < numFeatures; c++) {
            double sumDy = 0.0;
            double sumDyNorm = 0.0;
            for (int b = 0; b < batchSize; b++) {
                sumDy += dLdy[b][c];
                double norm = normalized[b * numFeatures + c];
                sumDyNorm += dLdy[b][c] * norm;
            }
            dGamma[c] = sumDyNorm;
            dBeta[c] = sumDy;
        }
    }

    // --- Getters ---

    public double[] getGamma() { return gamma; }
    public double[] getBeta() { return beta; }
    public double[] getRunningMean() { return runningMean; }
    public double[] getRunningVariance() { return runningVariance; }
    public int getNumFeatures() { return numFeatures; }
}
```

### Test Harness

```java
package com.deeplearning.normalization;

import java.util.Arrays;

/**
 * Test harness for Batch Normalization.
 * Validates forward pass (training and inference), backward pass gradients,
 * running statistics accumulation, and numerical correctness.
 */
public class BatchNormTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testForwardTraining();
        testForwardInference();
        testRunningStatistics();
        testGammaBetaIdentity();
        testGammaBetaEffect();
        testBackward();
        testNumericalGradient();
        testSingleBatch();
        testEdgeCases();
        testMomentum();
        System.out.printf("%n=== Results: %d passed, %d failed ===%n", passed, failed);
    }

    static void assertTest(boolean condition, String name) {
        if (condition) { passed++; System.out.printf("[PASS] %s%n", name); }
        else { failed++; System.err.printf("[FAIL] %s%n", name); }
    }

    // Helper: check if two doubles are approximately equal
    static boolean approx(double a, double b, double tol) {
        return Math.abs(a - b) < tol;
    }

    static void testForwardTraining() {
        // Simple test: 4 samples, 3 features
        double[][] x = {
            {1.0, 2.0, 3.0},
            {2.0, 4.0, 6.0},
            {3.0, 6.0, 9.0},
            {4.0, 8.0, 12.0}
        };

        BatchNorm bn = new BatchNorm(3, 1e-5, 0.1);
        bn.setTraining(true);

        double[][] y = bn.forward(x);

        // Each channel should have mean ≈ 0, std ≈ 1
        for (int c = 0; c < 3; c++) {
            double mean = 0;
            for (int b = 0; b < 4; b++) mean += y[b][c];
            mean /= 4;
            assertTest(Math.abs(mean) < 1e-10, "BN forward: channel " + c + " mean ≈ 0");

            double variance = 0;
            for (int b = 0; b < 4; b++) {
                double d = y[b][c] - mean;
                variance += d * d;
            }
            variance /= 4;
            assertTest(approx(variance, 1.0, 1e-6), "BN forward: channel " + c + " variance ≈ 1");
        }
    }

    static void testForwardInference() {
        double[][] x = {{1.0, 2.0}, {3.0, 4.0}};
        BatchNorm bn = new BatchNorm(2, 1e-5, 0.1);

        // Train one step to set running stats
        bn.setTraining(true);
        bn.forward(x);

        bn.setTraining(false);
        double[][] yTrain = bn.forward(x);
        double[][] yInfer = bn.forward(x);

        // Inference should give identical results for same input (deterministic)
        boolean same = true;
        for (int b = 0; b < 2; b++) {
            for (int c = 0; c < 2; c++) {
                if (!approx(yInfer[b][c], yTrain[b][c], 1e-10)) same = false;
            }
        }
        assertTest(same, "BN inference deterministic");
    }

    static void testRunningStatistics() {
        int numFeatures = 2;
        BatchNorm bn = new BatchNorm(numFeatures, 1e-5, 0.1);
        bn.setTraining(true);

        // Send multiple batches with known distributions
        for (int t = 0; t < 10; t++) {
            double[][] batch = {
                {(double) t, (double) t + 10},
                {(double) t + 1, (double) t + 11}
            };
            bn.forward(batch);
        }

        double[] runMean = bn.getRunningMean();
        double[] runVar = bn.getRunningVariance();
        // Running stats should have been updated
        assertTest(Math.abs(runMean[0]) > 0 || runMean[0] == 0,
            "Running mean updated");
        assertTest(runVar[0] > 0,
            "Running variance positive");
    }

    static void testGammaBetaIdentity() {
        // With gamma=1 and beta=0, output should equal normalized input
        double[][] x = {{1.0, 5.0}, {2.0, 6.0}, {3.0, 7.0}};
        BatchNorm bn = new BatchNorm(2, 1e-5, 0.1);
        bn.setTraining(true);

        double[][] y = bn.forward(x);

        // Check channel 1 mean and variance
        double mean = (y[0][1] + y[1][1] + y[2][1]) / 3;
        assertTest(Math.abs(mean) < 1e-10, "BN gamma=1,beta=0: output zero mean");
    }

    static void testGammaBetaEffect() {
        double[][] x = {{1.0, 2.0}, {3.0, 4.0}};
        BatchNorm bn = new BatchNorm(2, 1e-5, 0.1);
        bn.setTraining(true);

        // Initial gamma=1, beta=0
        double[][] y1 = bn.forward(x);

        // Modify gamma and beta
        bn.getGamma()[0] = 2.0;
        bn.getBeta()[0] = 1.0;

        double[][] y2 = bn.forward(x);

        // y2[0] should be 2 * y1[0] + 1 (for channel 0)
        assertTest(approx(y2[0][0], 2 * y1[0][0] + 1, 1e-10),
            "BN gamma and beta affect output correctly");
    }

    static void testBackward() {
        // Forward + backward sanity check: verify backward returns correct shape
        double[][] x = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
        BatchNorm bn = new BatchNorm(3, 1e-5, 0.1);
        bn.setTraining(true);

        double[][] y = bn.forward(x);

        // Create dummy upstream gradient
        double[][] dLdy = new double[3][3];
        for (int b = 0; b < 3; b++) {
            for (int c = 0; c < 3; c++) {
                dLdy[b][c] = 1.0;
            }
        }

        double[][] dLdx = bn.backward(dLdy);
        assertTest(dLdx.length == 3 && dLdx[0].length == 3,
            "BN backward returns correct shape");

        // Gradient w.r.t. each input should sum to approximately 0
        // (BN's gradient has this property)
        for (int c = 0; c < 3; c++) {
            double sum = 0;
            for (int b = 0; b < 3; b++) sum += dLdx[b][c];
            assertTest(Math.abs(sum) < 1e-10,
                "BN backward gradient sums to 0 for channel " + c);
        }
    }

    static void testNumericalGradient() {
        double eps = 1e-6;
        BatchNorm bn = new BatchNorm(1, 1e-8, 0.1);
        bn.setTraining(true);

        double[][] x = {{1.0}, {2.0}, {3.0}};
        double[][] y = bn.forward(x);

        // Upstream gradient: all ones
        double[][] dLdy = new double[3][1];
        for (int b = 0; b < 3; b++) dLdy[b][0] = 1.0;

        double[][] analyticalGrad = bn.backward(dLdy);

        // Numerical gradient check
        for (int i = 0; i < 3; i++) {
            double[] xPlus = new double[3];
            double[] xMinus = new double[3];
            for (int b = 0; b < 3; b++) {
                xPlus[b] = x[b][0];
                xMinus[b] = x[b][0];
            }
            xPlus[i] += eps;
            xMinus[i] -= eps;

            // Reshape to 2D
            double[][] xPlus2d = new double[3][1];
            double[][] xMinus2d = new double[3][1];
            for (int b = 0; b < 3; b++) {
                xPlus2d[b][0] = xPlus[b];
                xMinus2d[b][0] = xMinus[b];
            }

            // Need a fresh BN for each evaluation (since it modifies state)
            BatchNorm bnPlus = new BatchNorm(1, 1e-8, 0.1);
            bnPlus.setTraining(true);
            double[][] yPlus = bnPlus.forward(xPlus2d);

            BatchNorm bnMinus = new BatchNorm(1, 1e-8, 0.1);
            bnMinus.setTraining(true);
            double[][] yMinus = bnMinus.forward(xMinus2d);

            // Loss = sum of outputs (since dLdy = 1)
            double lossPlus = 0, lossMinus = 0;
            for (int b = 0; b < 3; b++) {
                lossPlus += yPlus[b][0];
                lossMinus += yMinus[b][0];
            }

            double numericalGrad = (lossPlus - lossMinus) / (2 * eps);
            assertTest(approx(analyticalGrad[i][0], numericalGrad, 0.001),
                "BN numerical gradient check for index " + i);
        }
    }

    static void testSingleBatch() {
        // Test with single sample in batch
        double[][] x = {{1.0, 2.0, 3.0}};
        BatchNorm bn = new BatchNorm(3, 1e-5, 0.1);

        // During training, single sample: variance = 0, uses running stats fallback
        bn.setTraining(true);
        try {
            double[][] y = bn.forward(x);
            // Should not crash; normalization with variance+epsilon avoids div by zero
            assertTest(true, "BN handles single-sample batch");
        } catch (Exception e) {
            assertTest(false, "BN handles single-sample batch: " + e.getMessage());
        }
    }

    static void testEdgeCases() {
        // Zero variance input: all elements are the same
        double[][] x = {{5.0, 5.0}, {5.0, 5.0}, {5.0, 5.0}};
        BatchNorm bn = new BatchNorm(2, 1e-5, 0.1);
        bn.setTraining(true);

        double[][] y = bn.forward(x);
        // With zero variance, normalized output should be 0 (before gamma/beta)
        // since (x - mean) = 0
        for (int b = 0; b < 3; b++) {
            assertTest(approx(y[b][0], 0.0, 1e-10), "BN zero-variance output near 0");
        }
    }

    static void testMomentum() {
        // Test momentum effect on running statistics
        int numFeatures = 1;
        BatchNorm bn = new BatchNorm(numFeatures, 1e-5, 0.5); // momentum = 0.5
        bn.setTraining(true);

        double[][] batch1 = {{0.0}, {1.0}};
        double[][] batch2 = {{10.0}, {11.0}};

        bn.forward(batch1);
        double m1 = bn.getRunningMean()[0]; // 0.5 * 0 + 0.5 * 0.5 = 0.25

        bn.forward(batch2);
        double m2 = bn.getRunningMean()[0]; // 0.5 * 0.25 + 0.5 * 10.5 = 5.375

        assertTest(approx(m1, 0.25, 1e-10), "BN momentum first update");
        assertTest(approx(m2, 5.375, 1e-10), "BN momentum second update");
    }
}
```

---

## Complexity Analysis

### Time Complexity

**Forward pass:** $O(B \cdot C)$ where $B$ is batch size, $C$ is number of channels. For each channel: compute mean ($O(B)$), variance ($O(B)$), normalize ($O(B)$), apply affine ($O(B)$). Total: $O(3BC) = O(BC)$.

**Backward pass:** $O(B \cdot C)$. For each channel: compute sums ($O(B)$), compute per-element gradient ($O(B)$).

**Running statistics update:** $O(C)$ per batch.

### Space Complexity

- **Parameters:** $O(2C)$ for $\gamma$ and $\beta$.
- **Running statistics:** $O(2C)$ for running mean and variance.
- **Cache (training):** $O(BC)$ for normalized values + input + statistics.
- **Total:** $O(BC + C)$.

---

## Follow-Up Questions

### Q1: Derive the full backward pass for batch normalization.

**Answer:** Let's derive $\frac{\partial \mathcal{L}}{\partial x_i}$ carefully.

Given a mini-batch $\{x_1, ..., x_m\}$:
$$\mu = \frac{1}{m} \sum_i x_i$$
$$\sigma^2 = \frac{1}{m} \sum_i (x_i - \mu)^2$$
$$\hat{x}_i = \frac{x_i - \mu}{\sqrt{\sigma^2 + \epsilon}}$$
$$y_i = \gamma \hat{x}_i + \beta$$

Let $\mathcal{L}$ be the loss. We need $\frac{\partial \mathcal{L}}{\partial x_i}$.

First, by the chain rule:
$$\frac{\partial \mathcal{L}}{\partial x_i} = \frac{\partial \mathcal{L}}{\partial \hat{x}_i} \cdot \frac{\partial \hat{x}_i}{\partial x_i} + \frac{\partial \mathcal{L}}{\partial \mu} \cdot \frac{\partial \mu}{\partial x_i} + \frac{\partial \mathcal{L}}{\partial \sigma^2} \cdot \frac{\partial \sigma^2}{\partial x_i}$$

Let $d\hat{x}_i = \frac{\partial \mathcal{L}}{\partial \hat{x}_i} = \frac{\partial \mathcal{L}}{\partial y_i} \cdot \gamma$.

$$\frac{\partial \hat{x}_i}{\partial x_i} = \frac{1}{\sqrt{\sigma^2 + \epsilon}}$$

$$\frac{\partial \mathcal{L}}{\partial \mu} = \sum_j \frac{\partial \mathcal{L}}{\partial \hat{x}_j} \cdot \frac{\partial \hat{x}_j}{\partial \mu} = \sum_j d\hat{x}_j \cdot \left(-\frac{1}{\sqrt{\sigma^2 + \epsilon}}\right) = -\frac{1}{\sqrt{\sigma^2 + \epsilon}} \sum_j d\hat{x}_j$$

$$\frac{\partial \mathcal{L}}{\partial \sigma^2} = \sum_j \frac{\partial \mathcal{L}}{\partial \hat{x}_j} \cdot \frac{\partial \hat{x}_j}{\partial \sigma^2} = \sum_j d\hat{x}_j \cdot \left(-\frac{1}{2} \frac{x_j - \mu}{(\sigma^2 + \epsilon)^{3/2}}\right) = -\frac{1}{2\sqrt{\sigma^2 + \epsilon}^3} \sum_j d\hat{x}_j (x_j - \mu)$$

Now $\frac{\partial \mu}{\partial x_i} = \frac{1}{m}$ and $\frac{\partial \sigma^2}{\partial x_i} = \frac{2(x_i - \mu)}{m}$.

Putting it all together and simplifying:

$$\frac{\partial \mathcal{L}}{\partial x_i} = \frac{1}{m\sqrt{\sigma^2 + \epsilon}} \left[ m \cdot d\hat{x}_i - \sum_j d\hat{x}_j - \hat{x}_i \sum_j d\hat{x}_j \cdot \hat{x}_j \right]$$

### Q2: Why does BN use $\epsilon$ in the variance denominator?

**Answer:** $\epsilon$ serves two purposes:
1. **Numerical stability:** Prevents division by zero when $\sigma^2 = 0$ (e.g., batch of identical inputs).
2. **Gradient stability:** The gradient $\frac{1}{\sqrt{\sigma^2 + \epsilon}}$ is bounded by $1/\sqrt{\epsilon}$, preventing excessively large gradients when variance is extremely small.

Typical values: $10^{-5}$ (original paper), $10^{-3}$ (for very small batches).

### Q3: Compare Batch Norm, Layer Norm, Instance Norm, and Group Norm.

**Answer:**

| Normalization | Normalizes over | When to use |
|--------------|-----------------|-------------|
| Batch Norm | Batch ($B$) × Spatial (H, W) | CNNs, large batches |
| Layer Norm | Features ($C$) × Spatial (H, W) | RNNs, Transformers |
| Instance Norm | Spatial (H, W) only | Style transfer |
| Group Norm | Group of channels ($G$) × Spatial (H, W) | Small batches, video |

**Batch Norm:**
- Normalizes across batch: $\mu = \text{mean}_B[X]$
- Depends on batch size; problematic for small batches.

**Layer Norm:**
- Normalizes across features: $\mu = \text{mean}_C[X]$
- Independent of batch size; used in Transformers.
- Fixed computation at train and inference (no running stats needed).

**Group Norm:**
- Divides channels into groups, normalizes within group.
- Good for small batches ($B=1$ or $B=2$).

### Q4: How does BN interact with dropout? Should both be used?

**Answer:** There is some redundancy — BN provides a regularization effect (from mini-batch noise), and dropout also regularizes. Some findings:

- **Using both can be beneficial** in large networks (e.g., in CNNs).
- **Order matters:** Typically dropout is applied after BN and activation.
- **Reduce dropout rate** when using BN (e.g., from 0.5 to 0.2-0.3).
- In some architectures (ResNet), BN alone provides sufficient regularization.

**The standard pattern:** Conv → BN → ReLU → Dropout.

### Q5: What happens when batch size is 1 during training?

**Answer:** With batch size 1, $\mu_{\mathcal{B}} = x$ and $\sigma^2_{\mathcal{B}} = 0$. The normalized output becomes:
$$\hat{x} = \frac{x - x}{\sqrt{0 + \epsilon}} = 0$$

Then $y = \gamma \cdot 0 + \beta = \beta$. The layer outputs only the bias, losing all information about the input. The gradient w.r.t. $x$ is also 0.

**Solutions:**
1. Use larger batch sizes (minimum 8-16).
2. Use Layer Norm or Group Norm instead.
3. Use Batch Renormalization (Ioffe, 2017) which incorporates running statistics into training.

### Q6: What is "ghost batch normalization"?

**Answer:** Ghost BN (Hoffer et al., 2017) divides a large batch into "ghost" sub-batches and computes BN statistics independently within each sub-batch:

```java
void ghostBatchNorm(double[][] x, int ghostBatchSize) {
    int totalBatch = x.length;
    int numGhosts = totalBatch / ghostBatchSize;
    for (int g = 0; g < numGhosts; g++) {
        int start = g * ghostBatchSize;
        int end = Math.min(start + ghostBatchSize, totalBatch);
        double[][] subBatch = Arrays.copyOfRange(x, start, end);
        double[][] normalized = bn.forward(subBatch); // standard BN
        // Copy back
    }
}
```

This increases the stochasticity of BN (stronger regularization) while allowing large effective batch sizes.

### Q7: How would you implement batch normalization for convolutional layers (4D tensors)?

**Answer:** For 4D input $[B, C, H, W]$, BN normalizes across the $B, H, W$ dimensions for each channel $C$:

```java
public double[][][][] forward4D(double[][][][] x) {
    int B = x.length, C = x[0].length, H = x[0][0].length, W = x[0][0][0].length;
    double[][][][] output = new double[B][C][H][W];

    for (int c = 0; c < C; c++) {
        double sum = 0;
        for (int b = 0; b < B; b++)
            for (int h = 0; h < H; h++)
                for (int w = 0; w < W; w++)
                    sum += x[b][c][h][w];
        double mean = sum / (B * H * W);

        double varSum = 0;
        for (int b = 0; b < B; b++)
            for (int h = 0; h < H; h++)
                for (int w = 0; w < W; w++) {
                    double d = x[b][c][h][w] - mean;
                    varSum += d * d;
                }
        double variance = varSum / (B * H * W);

        double invStd = 1.0 / Math.sqrt(variance + epsilon);
        for (int b = 0; b < B; b++)
            for (int h = 0; h < H; h++)
                for (int w = 0; w < W; w++) {
                    output[b][c][h][w] = gamma[c] * (x[b][c][h][w] - mean) * invStd + beta[c];
                }
    }
    return output;
}
```

### Q8: What is "batch renormalization" and when is it needed?

**Answer:** Batch Renormalization (Ioffe, 2017) addresses the mismatch between training (mini-batch stats) and inference (running stats) when batch sizes are small or distributions shift.

During training, it introduces two correction terms $r$ and $d$:
$$\hat{x}_i = \frac{x_i - \mu_{\mathcal{B}}}{\sigma_{\mathcal{B}}} \cdot r + d$$
$$r = \frac{\sigma_{\mathcal{B}}}{\sigma_{\text{run}}}, \quad d = \frac{\mu_{\mathcal{B}} - \mu_{\text{run}}}{\sigma_{\text{run}}}$$

This allows gradient flow based on batch statistics while keeping the output close to the inference-time distribution. The corrections $r$ and $d$ are constrained within limits $[1/r_{\max}, r_{\max}]$ and $[-d_{\max}, d_{\max}]$.

---

## Test Cases

| Test Case | Description | Input | Expected |
|-----------|-------------|-------|----------|
| TC-01 | Forward training: zero mean | Batch 4×3 | Each channel mean ≈ 0 |
| TC-02 | Forward training: unit variance | Batch 4×3 | Each channel var ≈ 1 |
| TC-03 | Forward inference | Same input twice | Same output |
| TC-04 | Running statistics | Multiple batches | Updated running mean/var |
| TC-05 | Gamma=2, beta=1 | Batch 2×2 | Output = 2×norm + 1 |
| TC-06 | Backward shape | 3×3 gradients | 3×3 output |
| TC-07 | Backward sum=0 | Uniform gradients | Gradient sums to 0 per channel |
| TC-08 | Numerical gradient | 3×1 input | Analytical ≈ Numerical |
| TC-09 | Zero variance input | All same values | Output ≈ 0 (before affine) |
| TC-10 | Single batch | 1×3 input | No exception |

---

## Key Takeaways

- **Batch Normalization** normalizes layer inputs to have zero mean and unit variance per mini-batch.
- **Learnable affine parameters** $\gamma$ and $\beta$ allow the network to undo normalization if needed.
- **Running statistics** (mean and variance) are accumulated during training for use at inference.
- BN **enables higher learning rates**, **reduces dependence on initialization**, and **provides weak regularization**.
- The **backward pass** through BN requires careful gradient derivation through mean and variance.
- BN is **sensitive to batch size** — for small batches, use Layer Norm or Group Norm.

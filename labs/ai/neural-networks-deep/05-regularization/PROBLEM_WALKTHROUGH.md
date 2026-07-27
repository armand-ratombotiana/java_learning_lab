# Regularization Techniques (L1, L2, Dropout)

## Problem Statement

**Problem:** Implement L1 regularization (LASSO), L2 regularization (weight decay), and dropout for neural networks, demonstrating how each technique modifies the forward and backward passes.

Design and implement:
1. **L1Regularizer** — adds L1 penalty on weights to encourage sparsity
2. **L2Regularizer** — adds L2 penalty on weights (weight decay)
3. **ElasticNetRegularizer** — combines L1 and L2 penalties
4. **Dropout** — stochastic regularization that randomly drops neurons during training with inverse scaling at test time

Each component must:
- Apply regularization penalty during the forward pass (loss computation)
- Modify gradients during the backward pass
- Dropout must use **inverted dropout** (scaling at training time, not test time)
- Support configurable regularization strength and dropout rate

**Example:**
```
Layer with 100 neurons, dropout rate = 0.5:
- During training: ~50 neurons randomly set to 0; remaining activations × 2
- During inference: all neurons active, no scaling
```

**Constraints:**
- $0 \leq \text{lambda} \leq 1$ (regularization strength)
- $0 \leq \text{dropout\_rate} < 1$ (fraction of neurons to drop)
- Must handle edge cases: lambda = 0 (no regularization), dropout_rate = 0 (no dropout)

---

## Step-by-Step Solution Walkthrough

### 1. Regularization in Deep Learning

Regularization techniques prevent overfitting by adding constraints to the learning process. The general form of regularized loss:

$$\mathcal{L}_{\text{total}} = \mathcal{L}_{\text{data}} + \lambda \cdot \mathcal{R}(\theta)$$

where $\mathcal{R}(\theta)$ is the regularization penalty on parameters $\theta$.

### 2. L1 Regularization (LASSO)

$$\mathcal{R}_{L1}(\mathbf{w}) = \|\mathbf{w}\|_1 = \sum_i |w_i|$$

**Gradient:** $\frac{\partial \mathcal{R}_{L1}}{\partial w_i} = \text{sign}(w_i)$

**Effect:**
- Encourages **sparsity**: many weights become exactly zero.
- Useful for feature selection.
- Gradient is constant magnitude ($\pm 1$) regardless of weight size.
- Non-differentiable at $w_i = 0$ (subgradient used).

### 3. L2 Regularization (Weight Decay)

$$\mathcal{R}_{L2}(\mathbf{w}) = \frac{1}{2} \|\mathbf{w}\|_2^2 = \frac{1}{2} \sum_i w_i^2$$

**Gradient:** $\frac{\partial \mathcal{R}_{L2}}{\partial w_i} = w_i$

**Effect:**
- Encourages **small weights** but not exactly zero.
- Also called "weight decay" because the weight update becomes:
  $$w_i \leftarrow w_i - \eta(\frac{\partial \mathcal{L}}{\partial w_i} + \lambda w_i) = (1 - \eta\lambda)w_i - \eta\frac{\partial \mathcal{L}}{\partial w_i}$$
  The $(1 - \eta\lambda)$ factor decays the weight multiplicatively.

### 4. Elastic Net

$$\mathcal{R}_{\text{EN}}(\mathbf{w}) = \lambda_1 \|\mathbf{w}\|_1 + \frac{\lambda_2}{2} \|\mathbf{w}\|_2^2$$

Combines the sparsity of L1 with the shrinkage of L2.

### 5. Dropout (Srivastava et al., 2014)

**Training:** For each neuron in a layer, independently keep it with probability $p$ (dropout rate $1-p$):

$$\tilde{a}_i = \begin{cases} \frac{a_i}{p} & \text{with probability } p \\ 0 & \text{with probability } 1-p \end{cases}$$

**Inference:** No dropout, no scaling: $\tilde{a}_i = a_i$

**Why divide by $p$ at training time?** This is **inverted dropout**. The expected activation during training is:
$$\mathbb{E}[\tilde{a}_i] = p \cdot \frac{a_i}{p} + (1-p) \cdot 0 = a_i$$
So the expected activation matches inference. This avoids scaling at test time.

**Effect:**
- Prevents co-adaptation of neurons — each neuron must learn useful features independently.
- Acts as an implicit ensemble of $2^N$ sub-networks.
- Reduces overfitting significantly.

### 6. Mathematical Intuition

#### L1 vs L2: Why L1 gives sparsity

The constrained optimization view: minimize $\mathcal{L}$ subject to $\|\mathbf{w}\|_1 \leq c$ or $\|\mathbf{w}\|_2^2 \leq c$.

- L1 constraint: diamond-shaped region in 2D → optimal solution occurs at corners (where some $w_i = 0$).
- L2 constraint: circular region → optimal solution on boundary typically has all $w_i \neq 0$.

#### Dropout as Bayesian Approximation

Dropout can be interpreted as variational Bayesian inference in a deep Gaussian process (Gal & Ghahramani, 2016). The dropout rate corresponds to the variance of the prior over weights.

---

## Java Implementation

```java
package com.deeplearning.regularization;

import java.util.Random;

/**
 * Interface for neural network regularizers.
 * Each regularizer modifies both the forward loss computation
 * and the backward gradient computation.
 */
public interface Regularizer {

    /**
     * Computes the regularization penalty for a weight matrix.
     *
     * @param weights weight matrix flattened
     * @return scalar regularization penalty
     */
    double computePenalty(double[] weights);

    /**
     * Computes the gradient of the regularization penalty w.r.t. weights.
     *
     * @param weights  weight matrix flattened
     * @param gradient output array for penalty gradients
     */
    void computeGradient(double[] weights, double[] gradient);

    // ---------------------------------------------------------------
    // L1 Regularization
    // ---------------------------------------------------------------

    /**
     * L1 regularization (LASSO).
     * Penalty: λ · Σ|w_i|
     * Gradient: λ · sign(w_i)
     * Encourages weight sparsity.
     */
    final class L1Regularizer implements Regularizer {

        private final double lambda;

        public L1Regularizer(double lambda) {
            this.lambda = lambda;
        }

        @Override
        public double computePenalty(double[] weights) {
            double sum = 0.0;
            for (double w : weights) {
                sum += Math.abs(w);
            }
            return lambda * sum;
        }

        @Override
        public void computeGradient(double[] weights, double[] gradient) {
            for (int i = 0; i < weights.length; i++) {
                if (weights[i] > 0) {
                    gradient[i] = lambda;
                } else if (weights[i] < 0) {
                    gradient[i] = -lambda;
                } else {
                    gradient[i] = 0.0; // subgradient at 0
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // L2 Regularization
    // ---------------------------------------------------------------

    /**
     * L2 regularization (weight decay).
     * Penalty: (λ/2) · Σw_i²
     * Gradient: λ · w_i
     * Encourages small weights.
     */
    final class L2Regularizer implements Regularizer {

        private final double lambda;

        public L2Regularizer(double lambda) {
            this.lambda = lambda;
        }

        @Override
        public double computePenalty(double[] weights) {
            double sum = 0.0;
            for (double w : weights) {
                sum += w * w;
            }
            return 0.5 * lambda * sum;
        }

        @Override
        public void computeGradient(double[] weights, double[] gradient) {
            for (int i = 0; i < weights.length; i++) {
                gradient[i] = lambda * weights[i];
            }
        }
    }

    // ---------------------------------------------------------------
    // Elastic Net Regularization
    // ---------------------------------------------------------------

    /**
     * Elastic Net regularization: combines L1 and L2.
     * Penalty: λ₁ · Σ|w_i| + (λ₂/2) · Σw_i²
     */
    final class ElasticNetRegularizer implements Regularizer {

        private final L1Regularizer l1;
        private final L2Regularizer l2;

        public ElasticNetRegularizer(double lambda1, double lambda2) {
            this.l1 = new L1Regularizer(lambda1);
            this.l2 = new L2Regularizer(lambda2);
        }

        @Override
        public double computePenalty(double[] weights) {
            return l1.computePenalty(weights) + l2.computePenalty(weights);
        }

        @Override
        public void computeGradient(double[] weights, double[] gradient) {
            double[] g1 = new double[weights.length];
            double[] g2 = new double[weights.length];
            l1.computeGradient(weights, g1);
            l2.computeGradient(weights, g2);
            for (int i = 0; i < weights.length; i++) {
                gradient[i] = g1[i] + g2[i];
            }
        }
    }
}
```

### Dropout Implementation

```java
package com.deeplearning.regularization;

import java.util.Random;

/**
 * Dropout regularization layer (Srivastava et al., 2014).
 * 
 * <p>Uses inverted dropout: during training, activations are scaled
 * by 1/p to maintain expected value. During inference, no scaling is applied.
 * This keeps inference code clean and fast.</p>
 */
public class Dropout {

    private final double dropRate; // fraction of neurons to drop (e.g., 0.5)
    private final double keepProb; // fraction to keep (p = 1 - dropRate)
    private final Random random;
    private boolean[] mask;
    private boolean training;

    /**
     * Creates a dropout layer with the specified drop rate.
     *
     * @param dropRate fraction of neurons to drop (0 ≤ dropRate < 1)
     */
    public Dropout(double dropRate) {
        if (dropRate < 0 || dropRate >= 1) {
            throw new IllegalArgumentException(
                "Drop rate must be in [0, 1), got: " + dropRate);
        }
        this.dropRate = dropRate;
        this.keepProb = 1.0 - dropRate;
        this.random = new Random();
        this.training = true;
    }

    /**
     * Sets training or evaluation mode.
     *
     * @param training true for training (apply dropout), false for inference
     */
    public void setTraining(boolean training) {
        this.training = training;
    }

    /**
     * Applies dropout to the input activations.
     *
     * @param input  input activations
     * @param output output array (can be same as input for in-place)
     */
    public void forward(double[] input, double[] output) {
        if (!training || keepProb == 1.0) {
            // Inference mode or no dropout: pass through
            System.arraycopy(input, 0, output, 0, input.length);
            return;
        }

        if (mask == null || mask.length != input.length) {
            mask = new boolean[input.length];
        }

        for (int i = 0; i < input.length; i++) {
            if (random.nextDouble() < keepProb) {
                output[i] = input[i] / keepProb; // scale up
                mask[i] = true;
            } else {
                output[i] = 0.0;
                mask[i] = false;
            }
        }
    }

    /**
     * Applies dropout mask during backward pass.
     * Gradients for dropped neurons are zeroed out.
     *
     * @param inputGrad gradient from the next layer
     * @param outputGrad gradient to pass to the previous layer
     */
    public void backward(double[] inputGrad, double[] outputGrad) {
        if (!training || keepProb == 1.0) {
            System.arraycopy(inputGrad, 0, outputGrad, 0, inputGrad.length);
            return;
        }

        for (int i = 0; i < inputGrad.length; i++) {
            outputGrad[i] = mask[i] ? inputGrad[i] / keepProb : 0.0;
        }
    }

    /**
     * Returns a copy of the current dropout mask for debugging.
     */
    public boolean[] getMask() {
        return mask != null ? mask.clone() : null;
    }

    public double getDropRate() {
        return dropRate;
    }

    public double getKeepProb() {
        return keepProb;
    }

    public boolean isTraining() {
        return training;
    }
}
```

### Demo: MLP with Regularization and Dropout

```java
package com.deeplearning.regularization;

import java.util.Random;

/**
 * A simple MLP layer that supports L1, L2, and ElasticNet regularization
 * with dropout, demonstrating how regularization modifies forward/backward.
 */
public class RegularizedLayer {

    private final int inputSize;
    private final int outputSize;
    private final double[][] weights;
    private final double[] biases;
    private final Regularizer regularizer;
    private final Dropout dropout;

    // Cache for backward pass
    private double[] lastInput;
    private double[] lastPreActivation;
    private double[] lastActivation;
    private double[][] weightGradients;
    private double[] biasGradients;

    public RegularizedLayer(int inputSize, int outputSize,
                            Regularizer regularizer, double dropRate) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.regularizer = regularizer;
        this.dropout = new Dropout(dropRate);
        this.weights = new double[outputSize][inputSize];
        this.biases = new double[outputSize];
        this.weightGradients = new double[outputSize][inputSize];
        this.biasGradients = new double[outputSize];

        // Xavier initialization
        Random rng = new Random(42);
        double scale = Math.sqrt(2.0 / (inputSize + outputSize));
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                weights[i][j] = rng.nextGaussian() * scale;
            }
            biases[i] = 0.0;
        }
    }

    /**
     * Forward pass: linear transform → regularization penalty → dropout.
     */
    public double[] forward(double[] input, boolean training) {
        this.lastInput = input.clone();
        dropout.setTraining(training);

        // Linear transformation
        this.lastPreActivation = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            double sum = biases[i];
            for (int j = 0; j < inputSize; j++) {
                sum += weights[i][j] * input[j];
            }
            lastPreActivation[i] = sum;
        }

        // Apply activation (sigmoid)
        this.lastActivation = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            lastActivation[i] = sigmoid(lastPreActivation[i]);
        }

        // Apply dropout
        double[] dropped = new double[outputSize];
        dropout.forward(lastActivation, dropped);

        return dropped;
    }

    /**
     * Backward pass: gradient through dropout → activation → linear → regularization.
     */
    public double[] backward(double[] upstreamGrad, double learningRate) {
        // Gradient through dropout
        double[] gradAfterDrop = new double[outputSize];
        dropout.backward(upstreamGrad, gradAfterDrop);

        // Gradient through sigmoid activation
        double[] gradAfterSigmoid = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            double sig = lastActivation[i];
            gradAfterSigmoid[i] = gradAfterDrop[i] * sig * (1.0 - sig);
        }

        // Accumulate weight gradients (data term)
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                weightGradients[i][j] += gradAfterSigmoid[i] * lastInput[j];
            }
            biasGradients[i] += gradAfterSigmoid[i];
        }

        // Add regularization gradients
        if (regularizer != null) {
            double[] flatWeights = flatten(weights);
            double[] regGrad = new double[flatWeights.length];
            regularizer.computeGradient(flatWeights, regGrad);

            int idx = 0;
            for (int i = 0; i < outputSize; i++) {
                for (int j = 0; j < inputSize; j++) {
                    weightGradients[i][j] += regGrad[idx++];
                }
            }
        }

        // Compute gradient for previous layer (without regularization)
        double[] prevGrad = new double[inputSize];
        for (int j = 0; j < inputSize; j++) {
            double sum = 0.0;
            for (int i = 0; i < outputSize; i++) {
                sum += weights[i][j] * gradAfterSigmoid[i];
            }
            prevGrad[j] = sum;
        }

        // Update weights
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                weights[i][j] -= learningRate * weightGradients[i][j];
                weightGradients[i][j] = 0.0;
            }
            biases[i] -= learningRate * biasGradients[i];
            biasGradients[i] = 0.0;
        }

        return prevGrad;
    }

    /**
     * Computes the regularization penalty for this layer's weights.
     */
    public double getRegularizationPenalty() {
        if (regularizer == null) return 0.0;
        return regularizer.computePenalty(flatten(weights));
    }

    private static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private static double[] flatten(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[] flat = new double[rows * cols];
        int idx = 0;
        for (double[] row : matrix) {
            for (double val : row) {
                flat[idx++] = val;
            }
        }
        return flat;
    }
}
```

### Test Harness

```java
package com.deeplearning.regularization;

import java.util.Arrays;

/**
 * Test harness for regularization and dropout implementations.
 */
public class RegularizationTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testL1Penalty();
        testL2Penalty();
        testL1Gradient();
        testL2Gradient();
        testElasticNet();
        testDropoutTraining();
        testDropoutInference();
        testDropoutMask();
        testDropoutRateZero();
        testRegularizationEffect();
        System.out.printf("%n=== Results: %d passed, %d failed ===%n", passed, failed);
    }

    static void assertTest(boolean condition, String name) {
        if (condition) { passed++; System.out.printf("[PASS] %s%n", name); }
        else { failed++; System.err.printf("[FAIL] %s%n", name); }
    }

    static void testL1Penalty() {
        Regularizer l1 = new Regularizer.L1Regularizer(0.1);
        double[] w = {1.0, -2.0, 0.0, 3.0};
        double penalty = l1.computePenalty(w);
        // 0.1 * (1 + 2 + 0 + 3) = 0.6
        assertTest(Math.abs(penalty - 0.6) < 1e-12, "L1 penalty");
    }

    static void testL2Penalty() {
        Regularizer l2 = new Regularizer.L2Regularizer(0.1);
        double[] w = {1.0, -2.0, 3.0};
        double penalty = l2.computePenalty(w);
        // 0.1/2 * (1 + 4 + 9) = 0.05 * 14 = 0.7
        assertTest(Math.abs(penalty - 0.7) < 1e-12, "L2 penalty");
    }

    static void testL1Gradient() {
        Regularizer l1 = new Regularizer.L1Regularizer(0.5);
        double[] w = {3.0, -1.5, 0.0};
        double[] grad = new double[3];
        l1.computeGradient(w, grad);
        assertTest(grad[0] == 0.5, "L1 gradient positive");
        assertTest(grad[1] == -0.5, "L1 gradient negative");
        assertTest(grad[2] == 0.0, "L1 gradient zero (subgradient)");
    }

    static void testL2Gradient() {
        Regularizer l2 = new Regularizer.L2Regularizer(0.5);
        double[] w = {3.0, -1.5, 0.0};
        double[] grad = new double[3];
        l2.computeGradient(w, grad);
        assertTest(grad[0] == 1.5, "L2 gradient positive");
        assertTest(grad[1] == -0.75, "L2 gradient negative");
        assertTest(grad[2] == 0.0, "L2 gradient zero");
    }

    static void testElasticNet() {
        Regularizer en = new Regularizer.ElasticNetRegularizer(0.1, 0.2);
        double[] w = {1.0, -2.0};
        double penalty = en.computePenalty(w);
        // L1: 0.1 * (1+2) = 0.3, L2: 0.2/2 * (1+4) = 0.5
        // Total: 0.8
        assertTest(Math.abs(penalty - 0.8) < 1e-12, "Elastic Net penalty");

        double[] grad = new double[2];
        en.computeGradient(w, grad);
        // L1 grad: [0.1, -0.1], L2 grad: [0.2*1, 0.2*(-2)] = [0.2, -0.4]
        // Total: [0.3, -0.5]
        assertTest(Math.abs(grad[0] - 0.3) < 1e-12, "Elastic Net gradient[0]");
        assertTest(Math.abs(grad[1] - (-0.5)) < 1e-12, "Elastic Net gradient[1]");
    }

    static void testDropoutTraining() {
        Dropout d = new Dropout(0.5);
        double[] input = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] output = new double[5];
        d.setTraining(true);

        // Run many times to verify expected value
        double[] expectedSum = new double[5];
        int trials = 10000;
        for (int t = 0; t < trials; t++) {
            d.forward(input, output);
            for (int i = 0; i < 5; i++) {
                expectedSum[i] += output[i];
            }
        }
        // Expected value of each output should be ~input[i]
        for (int i = 0; i < 5; i++) {
            double avg = expectedSum[i] / trials;
            assertTest(Math.abs(avg - input[i]) < 0.05 * input[i],
                "Dropout expected value for index " + i);
        }
    }

    static void testDropoutInference() {
        Dropout d = new Dropout(0.5);
        double[] input = {1.0, 2.0, 3.0};
        double[] output = new double[3];
        d.setTraining(false);
        d.forward(input, output);
        // During inference, output should equal input
        assertTest(output[0] == 1.0, "Dropout inference: no change");
        assertTest(output[1] == 2.0, "Dropout inference: no change");
        assertTest(output[2] == 3.0, "Dropout inference: no change");
    }

    static void testDropoutMask() {
        Dropout d = new Dropout(0.5);
        double[] input = {1.0, 2.0, 3.0, 4.0};
        double[] output = new double[4];
        d.setTraining(true);
        d.forward(input, output);
        boolean[] mask = d.getMask();
        assertTest(mask != null && mask.length == 4, "Dropout mask exists");
        // Each output should be either 0 or input[i] / keepProb
        for (int i = 0; i < 4; i++) {
            if (mask[i]) {
                assertTest(output[i] == input[i] * 2, "Masked neuron scaled");
            } else {
                assertTest(output[i] == 0.0, "Dropped neuron zero");
            }
        }
    }

    static void testDropoutRateZero() {
        Dropout d = new Dropout(0.0);
        double[] input = {1.0, 2.0, 3.0};
        double[] output = new double[3];
        d.setTraining(true);
        d.forward(input, output);
        // With rate 0, all neurons kept, no scaling (1/1 = 1)
        for (int i = 0; i < 3; i++) {
            assertTest(output[i] == input[i], "Dropout rate 0: passthrough");
        }
    }

    static void testRegularizationEffect() {
        // Simulate: without regularization, weights grow large;
        // with L2 regularization, weights stay smaller.
        double[] weights = {5.0, -10.0, 3.0};

        Regularizer l2 = new Regularizer.L2Regularizer(0.01);
        double penalty = l2.computePenalty(weights);
        // 0.01/2 * (25 + 100 + 9) = 0.005 * 134 = 0.67
        assertTest(penalty > 0, "L2 penalty on large weights");

        // L1 on mixed weights
        Regularizer l1 = new Regularizer.L1Regularizer(0.01);
        double l1Penalty = l1.computePenalty(weights);
        // 0.01 * (5 + 10 + 3) = 0.18
        assertTest(Math.abs(l1Penalty - 0.18) < 1e-12, "L1 penalty on large weights");
    }
}
```

---

## Complexity Analysis

### Time Complexity

| Operation | Time |
|-----------|------|
| L1 penalty (n weights) | $O(n)$ |
| L1 gradient (n weights) | $O(n)$ |
| L2 penalty (n weights) | $O(n)$ |
| L2 gradient (n weights) | $O(n)$ |
| Dropout forward (d neurons) | $O(d)$ |
| Dropout backward (d neurons) | $O(d)$ |

### Space Complexity

- Regularizers: $O(1)$ (no state)
- Dropout: $O(d)$ for the mask array

---

## Follow-Up Questions

### Q1: Compare L1 and L2 regularization. When would you use each?

**Answer:**

| Property | L1 | L2 |
|---------|-----|-----|
| Penalty | $\lambda\|w\|_1$ | $\frac{\lambda}{2}\|w\|_2^2$ |
| Gradient | $\lambda \cdot \text{sign}(w)$ | $\lambda \cdot w$ |
| Sparsity | Yes (weights become exactly 0) | No (weights shrink but rarely 0) |
| Feature selection | Yes | No |
| Differentiable | No (at 0) | Yes |
| Bayesian interpretation | Laplace prior | Gaussian prior |

**Guidelines:**
- Use **L1** when feature selection is desired or you expect sparse solutions.
- Use **L2** as the default regularizer (weight decay) for most neural networks.
- Use **Elastic Net** when both sparsity and group selection are desired.

### Q2: Why does L1 produce sparse solutions while L2 does not?

**Answer:** This can be understood geometrically or through subgradient analysis.

**Geometric:** The constrained formulations $\min \mathcal{L}$ s.t. $\|w\|_1 \leq c$ (L1) and $\|w\|_2^2 \leq c$ (L2) have different constraint regions. The L1 diamond has corners on the axes — the optimal solution often occurs at a corner where some $w_i = 0$. The L2 sphere has a smooth boundary, so optimal solutions typically have all $w_i \neq 0$.

**Subgradient:** L1's gradient is $\pm \lambda$ regardless of weight magnitude. A weight near zero with gradient $\mathcal{L}'$ will be pushed to exactly zero if $|\mathcal{L}'| < \lambda$. L2's gradient is $\lambda w$, which approaches 0 as $w \to 0$, so it never quite reaches zero.

### Q3: Explain the difference between "regularization" and "weight decay."

**Answer:** For standard SGD, they are equivalent. The update with L2 is:

$$w_{t+1} = w_t - \eta(\mathcal{L}' + \lambda w_t) = (1 - \eta\lambda)w_t - \eta\mathcal{L}'$$

The $(1 - \eta\lambda)$ factor "decays" the weight.

However, for adaptive optimizers like Adam, **decoupled weight decay** (Loshchilov & Hutter, 2019) is different from L2 regularization. With adaptive optimizers, L2 regularization interacts with the adaptive learning rates, causing the regularization to be scaled per-parameter. Decoupled weight decay applies the decay directly before the adaptive step:

$$w_{t+1} = w_t - \eta(\lambda w_t + \text{AdamUpdate})$$

This decoupling improves generalization, especially for Transformers.

### Q4: How do you set the dropout rate?

**Answer:** 
- **Default rate:** 0.5 (maximal regularization).
- **Smaller networks:** Lower rate (e.g., 0.2-0.3) to avoid underfitting.
- **Input layers:** Lower rate (e.g., 0.2) since inputs are less redundant.
- **Convolutional layers:** Lower rate (e.g., 0.2-0.3) or use spatial dropout (drop entire channels).
- **Recurrent networks:** Use variational dropout (same mask across timesteps).
- **Rule of thumb:** Start with 0.5 for hidden layers, tune via validation.

### Q5: What is Spatial Dropout and when is it used?

**Answer:** In convolutional neural networks, standard dropout drops individual pixels, which is less effective because neighboring pixels are highly correlated. **Spatial dropout** drops entire feature maps (channels):

```java
class SpatialDropout {
    double dropRate;
    double keepProb;
    boolean[] channelMask;

    void forward(double[][][] input, double[][][] output, boolean training) {
        int channels = input.length;
        if (!training) {
            // copy directly
            return;
        }
        for (int c = 0; c < channels; c++) {
            if (random.nextDouble() < keepProb) {
                channelMask[c] = true;
                for (int h ... ) for (int w ... )
                    output[c][h][w] = input[c][h][w] / keepProb;
            } else {
                channelMask[c] = false;
                // entire channel is zero
            }
        }
    }
}
```

This is standard in CNNs (e.g., in segmentation networks).

### Q6: How does dropout affect the backward pass differently from regular layers?

**Answer:** During backpropagation, the dropout mask must be reused to zero out gradients for dropped neurons:

$$\frac{\partial \mathcal{L}}{\partial a_i} = \begin{cases} \frac{1}{p} \cdot \frac{\partial \mathcal{L}}{\partial \tilde{a}_i} & \text{if neuron kept} \\ 0 & \text{if neuron dropped} \end{cases}$$

This means:
1. The mask from the forward pass must be stored.
2. Gradients only flow through kept neurons.
3. The scaling factor $1/p$ is applied during backward as well.

### Q7: What is Monte Carlo Dropout and how is it used for uncertainty estimation?

**Answer:** Monte Carlo Dropout (Gal & Ghahramani, 2016) keeps dropout enabled at test time and performs multiple forward passes:

```java
class MCDropout {
    double predictWithUncertainty(double[] input, Dropout dropout, Model model, int T) {
        double[] predictions = new double[T];
        for (int t = 0; t < T; t++) {
            dropout.setTraining(true); // keep dropout ON
            predictions[t] = model.forward(input);
        }
        double mean = mean(predictions);
        double variance = variance(predictions, mean);
        return mean; // variance quantifies epistemic uncertainty
    }
}
```

This approximates Bayesian inference in deep Gaussian processes and provides well-calibrated uncertainty estimates.

### Q8: Show that the expected activation under inverted dropout equals the inference activation.

**Answer:** Let $a$ be the original activation. During training with keep probability $p$:

$$\tilde{a} = \begin{cases} a/p & \text{with probability } p \\ 0 & \text{with probability } 1-p \end{cases}$$

Expected value:
$$\mathbb{E}[\tilde{a}] = p \cdot (a/p) + (1-p) \cdot 0 = a$$

During inference, $\tilde{a} = a$, which has the same expected value. Without the $1/p$ scaling, the expected value during training would be $p \cdot a$, causing a mismatch that would need to be corrected at test time.

---

## Test Cases

| Test Case | Component | Input | Expected |
|-----------|-----------|-------|----------|
| TC-01 | L1 penalty | w=[1,-2,0,3], λ=0.1 | 0.6 |
| TC-02 | L2 penalty | w=[1,-2,3], λ=0.1 | 0.7 |
| TC-03 | L1 gradient | w=[3,-1.5,0], λ=0.5 | [0.5, -0.5, 0] |
| TC-04 | L2 gradient | w=[3,-1.5,0], λ=0.5 | [1.5, -0.75, 0] |
| TC-05 | Elastic Net | w=[1,-2], λ1=0.1, λ2=0.2 | penalty=0.8, grad=[0.3,-0.5] |
| TC-06 | Dropout expected value | rate=0.5, many trials | average ≈ original |
| TC-07 | Dropout inference | rate=0.5 | output = input |
| TC-08 | Dropout rate 0 | rate=0.0 | passthrough, no scaling |
| TC-09 | Dropout mask | rate=0.5 | output is 0 or scaled input |
| TC-10 | L2 on large weights | w=[5,-10,3], λ=0.01 | penalty=0.67 |

---

## Key Takeaways

- **L1** regularization produces sparse weights (subgradient at 0 enables exact zeros).
- **L2** regularization shrinks weights but doesn't produce zeros (weight decay).
- **Dropout** prevents co-adaptation by randomly dropping neurons, with inverted dropout keeping inference clean.
- Regularization strength $\lambda$ controls the trade-off between fitting data and constraining parameters.
- Modern deep learning typically uses L2 weight decay (often AdamW) and dropout (rate 0.1-0.5).

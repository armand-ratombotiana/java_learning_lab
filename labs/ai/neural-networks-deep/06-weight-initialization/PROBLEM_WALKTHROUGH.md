# Weight Initialization Strategies

## Problem Statement

**Problem:** Implement Xavier/Glorot and He weight initialization strategies with proper variance scaling for deep neural networks.

Implement the following initialization strategies:
1. **Xavier Uniform** (Glorot Uniform) — for sigmoid/tanh activations
2. **Xavier Normal** (Glorot Normal) — for sigmoid/tanh activations
3. **He Uniform** (Kaiming Uniform) — for ReLU/LeakyReLU activations
4. **He Normal** (Kaiming Normal) — for ReLU/LeakyReLU activations
5. **LeCun Normal** — for SELU activations

Each initializer must:
- Generate weights scaled appropriately for the layer dimensions.
- Preserve variance of activations and gradients during forward/backward pass.
- Support configurable gain parameters.
- Properly handle bias initialization.

**Example:**
```
Layer: input_size=1000, output_size=500
XavierNormal:  weights ~ N(0, sqrt(2/(1000+500))) = N(0, 0.0365)
HeNormal:      weights ~ N(0, sqrt(2/1000))      = N(0, 0.0447)
```

**Constraints:**
- $1 \leq \text{input\_size}, \text{output\_size} \leq 10^6$
- Must match the expected variance analytically.
- Must be reproducible (seeded Random).

---

## Step-by-Step Solution Walkthrough

### 1. Why Initialization Matters

Poor initialization causes:
- **Vanishing gradients:** Weights too small → activations shrink to 0 → gradients vanish.
- **Exploding gradients:** Weights too large → activations grow → gradients explode.
- **Saturation:** With sigmoid/tanh, large weights push activations to saturation regions.
- **Slow convergence:** Bad initializations require many epochs to escape poor local regions.

The goal: Initialize weights so that the **variance of activations** is preserved across layers, and the **variance of gradients** is preserved during backpropagation.

### 2. Variance Propagation Analysis

Consider a simple layer without bias:

$$y = W \cdot x \quad \text{where} \quad W \in \mathbb{R}^{n_{\text{out}} \times n_{\text{in}}}, x \in \mathbb{R}^{n_{\text{in}}}$$

Assume $x_i$ and $W_{ij}$ are independent with mean 0. Then:

$$\text{Var}(y_j) = \text{Var}\left(\sum_{i=1}^{n_{\text{in}}} W_{ji} x_i\right) = \sum_{i=1}^{n_{\text{in}}} \text{Var}(W_{ji})\text{Var}(x_i) = n_{\text{in}} \cdot \text{Var}(W) \cdot \text{Var}(x)$$

To preserve variance ($\text{Var}(y) = \text{Var}(x)$):
$$\text{Var}(W) = \frac{1}{n_{\text{in}}}$$

For backpropagation, similar analysis gives:
$$\text{Var}\left(\frac{\partial \mathcal{L}}{\partial x}\right) = n_{\text{out}} \cdot \text{Var}(W) \cdot \text{Var}\left(\frac{\partial \mathcal{L}}{\partial y}\right)$$

To preserve gradient variance:
$$\text{Var}(W) = \frac{1}{n_{\text{out}}}$$

### 3. Xavier/Glorot Initialization (Glorot & Bengio, 2010)

For sigmoid/tanh activations, Xavier initialization takes the harmonic mean of both constraints:

$$\text{Var}(W) = \frac{2}{n_{\text{in}} + n_{\text{out}}}$$

**Xavier Uniform:**
$$W \sim \mathcal{U}\left[-\sqrt{\frac{6}{n_{\text{in}} + n_{\text{out}}}}, \sqrt{\frac{6}{n_{\text{in}} + n_{\text{out}}}}\right]$$

For a uniform distribution $\mathcal{U}[-a, a]$, $\text{Var} = a^2/3$. Setting $a = \sqrt{6/(n_{\text{in}} + n_{\text{out}})}$ gives:
$$\text{Var}(W) = \frac{a^2}{3} = \frac{2}{n_{\text{in}} + n_{\text{out}}}$$

**Xavier Normal:**
$$W \sim \mathcal{N}\left(0, \sqrt{\frac{2}{n_{\text{in}} + n_{\text{out}}}}\right)$$

### 4. He/Kaiming Initialization (He et al., 2015)

For ReLU activations, the positive half-wave rectification halves the variance. The analysis becomes:

$$\text{Var}(y) = \frac{1}{2} \cdot n_{\text{in}} \cdot \text{Var}(W) \cdot \text{Var}(x)$$

To preserve variance:
$$\text{Var}(W) = \frac{2}{n_{\text{in}}}$$

**He Uniform:**
$$W \sim \mathcal{U}\left[-\sqrt{\frac{6}{n_{\text{in}}}}, \sqrt{\frac{6}{n_{\text{in}}}}\right]$$

**He Normal:**
$$W \sim \mathcal{N}\left(0, \sqrt{\frac{2}{n_{\text{in}}}}\right)$$

### 5. LeCun Initialization (LeCun et al., 1998)

For SELU (self-normalizing) activations:
$$W \sim \mathcal{N}\left(0, \sqrt{\frac{1}{n_{\text{in}}}}\right)$$

### 6. Gain Parameters

Some activations require gain scaling:
- Tanh: gain ≈ 5/3
- SELU: gain ≈ 3/4
- ReLU: gain = $\sqrt{2}$

The standard deviation becomes: $\text{std} = \text{gain} \cdot \sqrt{\frac{2}{n_{\text{in}} + n_{\text{out}}}}$

---

## Java Implementation

```java
package com.deeplearning.initialization;

import java.util.Random;

/**
 * Strategy interface for neural network weight initialization.
 * Each implementation generates properly-scaled random weights
 * for the given layer dimensions and activation function.
 */
@FunctionalInterface
public interface Initializer {

    /**
     * Generates a weight matrix for a layer.
     *
     * @param inputSize  number of input features (fan-in)
     * @param outputSize number of output features (fan-out)
     * @return weight matrix of shape [outputSize][inputSize]
     */
    double[][] generate(int inputSize, int outputSize);

    /**
     * Generates bias values for a layer (typically zeros).
     *
     * @param size number of output features
     * @return bias array initialized to 0
     */
    default double[] generateBiases(int size) {
        return new double[size];
    }

    // ---------------------------------------------------------------
    // Xavier Uniform
    // ---------------------------------------------------------------

    /**
     * Xavier/Glorot uniform initialization for sigmoid/tanh.
     * W ~ U[-limit, limit] where limit = sqrt(6 / (fan_in + fan_out))
     */
    final class XavierUniform implements Initializer {

        private final Random random;

        public XavierUniform() {
            this.random = new Random();
        }

        public XavierUniform(long seed) {
            this.random = new Random(seed);
        }

        @Override
        public double[][] generate(int inputSize, int outputSize) {
            double limit = Math.sqrt(6.0 / (inputSize + outputSize));
            double[][] weights = new double[outputSize][inputSize];
            for (int i = 0; i < outputSize; i++) {
                for (int j = 0; j < inputSize; j++) {
                    weights[i][j] = random.nextDouble() * 2 * limit - limit;
                }
            }
            return weights;
        }
    }

    // ---------------------------------------------------------------
    // Xavier Normal
    // ---------------------------------------------------------------

    /**
     * Xavier/Glorot normal initialization for sigmoid/tanh.
     * W ~ N(0, sqrt(2 / (fan_in + fan_out)))
     */
    final class XavierNormal implements Initializer {

        private final Random random;

        public XavierNormal() {
            this.random = new Random();
        }

        public XavierNormal(long seed) {
            this.random = new Random(seed);
        }

        @Override
        public double[][] generate(int inputSize, int outputSize) {
            double std = Math.sqrt(2.0 / (inputSize + outputSize));
            double[][] weights = new double[outputSize][inputSize];
            for (int i = 0; i < outputSize; i++) {
                for (int j = 0; j < inputSize; j++) {
                    weights[i][j] = random.nextGaussian() * std;
                }
            }
            return weights;
        }
    }

    // ---------------------------------------------------------------
    // He Uniform
    // ---------------------------------------------------------------

    /**
     * He/Kaiming uniform initialization for ReLU/LeakyReLU.
     * W ~ U[-limit, limit] where limit = sqrt(6 / fan_in)
     */
    final class HeUniform implements Initializer {

        private final Random random;

        public HeUniform() {
            this.random = new Random();
        }

        public HeUniform(long seed) {
            this.random = new Random(seed);
        }

        @Override
        public double[][] generate(int inputSize, int outputSize) {
            double limit = Math.sqrt(6.0 / inputSize);
            double[][] weights = new double[outputSize][inputSize];
            for (int i = 0; i < outputSize; i++) {
                for (int j = 0; j < inputSize; j++) {
                    weights[i][j] = random.nextDouble() * 2 * limit - limit;
                }
            }
            return weights;
        }
    }

    // ---------------------------------------------------------------
    // He Normal
    // ---------------------------------------------------------------

    /**
     * He/Kaiming normal initialization for ReLU/LeakyReLU.
     * W ~ N(0, sqrt(2 / fan_in))
     */
    final class HeNormal implements Initializer {

        private final Random random;

        public HeNormal() {
            this.random = new Random();
        }

        public HeNormal(long seed) {
            this.random = new Random(seed);
        }

        @Override
        public double[][] generate(int inputSize, int outputSize) {
            double std = Math.sqrt(2.0 / inputSize);
            double[][] weights = new double[outputSize][inputSize];
            for (int i = 0; i < outputSize; i++) {
                for (int j = 0; j < inputSize; j++) {
                    weights[i][j] = random.nextGaussian() * std;
                }
            }
            return weights;
        }
    }

    // ---------------------------------------------------------------
    // LeCun Normal
    // ---------------------------------------------------------------

    /**
     * LeCun normal initialization for SELU activations.
     * W ~ N(0, sqrt(1 / fan_in))
     */
    final class LeCunNormal implements Initializer {

        private final Random random;

        public LeCunNormal() {
            this.random = new Random();
        }

        public LeCunNormal(long seed) {
            this.random = new Random(seed);
        }

        @Override
        public double[][] generate(int inputSize, int outputSize) {
            double std = Math.sqrt(1.0 / inputSize);
            double[][] weights = new double[outputSize][inputSize];
            for (int i = 0; i < outputSize; i++) {
                for (int j = 0; j < inputSize; j++) {
                    weights[i][j] = random.nextGaussian() * std;
                }
            }
            return weights;
        }
    }

    // ---------------------------------------------------------------
    // Constant Initializer (for biases)
    // ---------------------------------------------------------------

    /**
     * Constant value initializer (for biases or debugging).
     * Default: initialize all weights to 0.1.
     */
    final class ConstantInitializer implements Initializer {

        private final double value;
        private final Random random;

        public ConstantInitializer(double value) {
            this.value = value;
            this.random = new Random();
        }

        public ConstantInitializer() {
            this(0.1);
        }

        @Override
        public double[][] generate(int inputSize, int outputSize) {
            double[][] weights = new double[outputSize][inputSize];
            for (int i = 0; i < outputSize; i++) {
                for (int j = 0; j < inputSize; j++) {
                    weights[i][j] = value;
                }
            }
            return weights;
        }
    }
}
```

### Variance Analysis Demo

```java
package com.deeplearning.initialization;

import java.util.Arrays;

/**
 * Demonstrates how different initializers preserve activation variance
 * across layers in a deep network.
 */
public class InitializationDemo {

    public static void main(String[] args) {
        // Build a deep network and examine activation statistics
        int[] layerSizes = {1000, 800, 600, 400, 200, 100};
        int batchSize = 1000;

        System.out.println("=== Weight Initialization Variance Analysis ===\n");

        // Generate random input with mean 0, variance 1
        double[][] input = new double[batchSize][layerSizes[0]];
        Random rng = new Random(1);
        for (int i = 0; i < batchSize; i++) {
            for (int j = 0; j < layerSizes[0]; j++) {
                input[i][j] = rng.nextGaussian();
            }
        }

        // Test each initializer
        Initializer[] initers = {
            new Initializer.XavierUniform(),
            new Initializer.XavierNormal(),
            new Initializer.HeUniform(),
            new Initializer.HeNormal(),
            new Initializer.LeCunNormal()
        };

        for (Initializer init : initers) {
            System.out.println("=== " + init.getClass().getSimpleName() + " ===");
            double[][] activations = input;

            for (int l = 1; l < layerSizes.length; l++) {
                int inSize = layerSizes[l - 1];
                int outSize = layerSizes[l];

                double[][] weights = init.generate(inSize, outSize);

                // Forward pass for the batch
                double[][] newActivations = new double[batchSize][outSize];
                for (int b = 0; b < batchSize; b++) {
                    for (int o = 0; o < outSize; o++) {
                        double sum = 0.0;
                        for (int i = 0; i < inSize; i++) {
                            sum += weights[o][i] * activations[b][i];
                        }
                        // Apply ReLU
                        newActivations[b][o] = Math.max(0, sum);
                    }
                }

                // Compute mean and variance
                double mean = 0.0, var = 0.0;
                for (int b = 0; b < batchSize; b++) {
                    for (int o = 0; o < outSize; o++) {
                        mean += newActivations[b][o];
                    }
                }
                mean /= (batchSize * outSize);
                for (int b = 0; b < batchSize; b++) {
                    for (int o = 0; o < outSize; o++) {
                        double d = newActivations[b][o] - mean;
                        var += d * d;
                    }
                }
                var /= (batchSize * outSize);

                // Count dead neurons (ReLU)
                int deadCount = 0;
                for (int b = 0; b < batchSize; b++) {
                    for (int o = 0; o < outSize; o++) {
                        if (newActivations[b][o] == 0) deadCount++;
                    }
                }
                double deadPct = 100.0 * deadCount / (batchSize * outSize);

                System.out.printf("  Layer %d→%d: mean=%.3f, std=%.3f, dead=%.1f%%%n",
                    l - 1, l, mean, Math.sqrt(var), deadPct);

                activations = newActivations;
            }
            System.out.println();
        }
    }
}
```

### Test Harness

```java
package com.deeplearning.initialization;

/**
 * Test harness for weight initialization strategies.
 */
public class InitializationTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testXavierUniform();
        testXavierNormal();
        testHeUniform();
        testHeNormal();
        testLeCunNormal();
        testVariancePreservation();
        testSymmetricDistribution();
        testReproducibility();
        testBiases();
        testEdgeCases();
        System.out.printf("%n=== Results: %d passed, %d failed ===%n", passed, failed);
    }

    static void assertTest(boolean condition, String name) {
        if (condition) { passed++; System.out.printf("[PASS] %s%n", name); }
        else { failed++; System.err.printf("[FAIL] %s%n", name); }
    }

    static void testXavierUniform() {
        Initializer init = new Initializer.XavierUniform(42);
        double[][] w = init.generate(100, 50);
        // Check dimensions
        assertTest(w.length == 50 && w[0].length == 100, "XavierUniform dimensions");

        // Check variance: 2/(100+50) = 0.01333
        double mean = 0, var = 0;
        int count = 0;
        for (double[] row : w) {
            for (double val : row) {
                mean += val;
                count++;
            }
        }
        mean /= count;
        for (double[] row : w) {
            for (double val : row) {
                double d = val - mean;
                var += d * d;
            }
        }
        var /= count;
        // limit = sqrt(6/150) = sqrt(0.04) = 0.2, var = 0.04/3 = 0.01333
        assertTest(Math.abs(var - 0.01333) < 0.001, "XavierUniform variance ≈ 0.0133");
    }

    static void testXavierNormal() {
        Initializer init = new Initializer.XavierNormal(42);
        double[][] w = init.generate(1000, 500);
        double std = Math.sqrt(2.0 / 1500);
        assertTest(Math.abs(std - 0.0365) < 0.001, "XavierNormal std ≈ 0.0365");

        // Empirical check
        double mean = 0, var = 0;
        int count = 0;
        for (double[] row : w) { for (double v : row) { mean += v; count++; } }
        mean /= count;
        for (double[] row : w) { for (double v : row) { double d = v - mean; var += d * d; } }
        var /= count;
        assertTest(Math.abs(Math.sqrt(var) - 0.0365) < 0.005, "XavierNormal empirical std");
    }

    static void testHeUniform() {
        Initializer init = new Initializer.HeUniform(42);
        double[][] w = init.generate(200, 100);
        double limit = Math.sqrt(6.0 / 200);
        assertTest(Math.abs(limit - 0.1732) < 0.001, "HeUniform limit ≈ 0.1732");

        // All values should be within [-limit, limit]
        boolean inRange = true;
        for (double[] row : w) {
            for (double val : row) {
                if (val < -limit || val > limit) inRange = false;
            }
        }
        assertTest(inRange, "HeUniform all values in range");
    }

    static void testHeNormal() {
        Initializer init = new Initializer.HeNormal(42);
        double[][] w = init.generate(400, 200);
        double std = Math.sqrt(2.0 / 400);
        assertTest(Math.abs(std - 0.07071) < 0.001, "HeNormal std ≈ 0.0707");

        double mean = 0;
        int count = 0;
        for (double[] row : w) { for (double v : row) { mean += v; count++; } }
        mean /= count;
        assertTest(Math.abs(mean) < 0.01, "HeNormal mean near zero");
    }

    static void testLeCunNormal() {
        Initializer init = new Initializer.LeCunNormal(42);
        double[][] w = init.generate(256, 128);
        double std = Math.sqrt(1.0 / 256);
        assertTest(Math.abs(std - 0.0625) < 0.001, "LeCunNormal std ≈ 0.0625");
    }

    static void testVariancePreservation() {
        // Test that He Normal preserves variance through ReLU
        Initializer he = new Initializer.HeNormal(42);
        java.util.Random rng = new java.util.Random(1);

        int inSize = 500;
        int outSize = 500;
        double[][] W = he.generate(inSize, outSize);

        // Generate input with unit variance
        double[] x = new double[inSize];
        for (int i = 0; i < inSize; i++) x[i] = rng.nextGaussian();

        // Forward pass
        double[] y = new double[outSize];
        for (int o = 0; o < outSize; o++) {
            double sum = 0;
            for (int i = 0; i < inSize; i++) sum += W[o][i] * x[i];
            y[o] = Math.max(0, sum); // ReLU
        }

        // Output variance should be ≈ 1 (for ReLU, which halves variance)
        double mean = 0;
        for (double v : y) mean += v;
        mean /= outSize;
        double var = 0;
        for (double v : y) { double d = v - mean; var += d * d; }
        var /= outSize;

        // With He init, variance after ReLU should be ≈ 0.5
        // (ReLU halves variance of zero-mean Gaussian input)
        assertTest(var > 0.1 && var < 1.0, "HeNormal variance preservation through ReLU");
    }

    static void testSymmetricDistribution() {
        Initializer unif = new Initializer.XavierUniform(42);
        double[][] w = unif.generate(100, 100);
        double posCount = 0, negCount = 0;
        for (double[] row : w) {
            for (double val : row) {
                if (val > 0) posCount++;
                else if (val < 0) negCount++;
            }
        }
        double ratio = posCount / (posCount + negCount);
        assertTest(ratio > 0.45 && ratio < 0.55, "XavierUniform symmetric (±~50%)");
    }

    static void testReproducibility() {
        Initializer a = new Initializer.HeNormal(12345);
        Initializer b = new Initializer.HeNormal(12345);
        double[][] w1 = a.generate(10, 5);
        double[][] w2 = b.generate(10, 5);
        boolean same = true;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                if (w1[i][j] != w2[i][j]) same = false;
            }
        }
        assertTest(same, "Reproducibility with same seed");
    }

    static void testBiases() {
        Initializer inst = new Initializer.HeNormal();
        double[] biases = inst.generateBiases(100);
        boolean allZero = true;
        for (double b : biases) {
            if (b != 0.0) allZero = false;
        }
        assertTest(allZero && biases.length == 100, "Biases initialized to zero");
    }

    static void testEdgeCases() {
        Initializer init = new Initializer.HeUniform(42);
        // Very small layer
        double[][] w = init.generate(1, 1);
        assertTest(w.length == 1 && w[0].length == 1, "1x1 layer");
        // Very large layer (just check no exception)
        w = init.generate(10000, 10000);
        assertTest(w.length == 10000, "10Kx10K layer");
    }
}
```

---

## Complexity Analysis

### Time Complexity

- **All initializers:** $O(n_{\text{out}} \cdot n_{\text{in}})$ — must fill every element of the weight matrix.
- **Xavier Uniform:** $O(n_{\text{out}} \cdot n_{\text{in}})$ with 1 multiply, 1 add, 1 random sample per element.
- **Xavier Normal:** $O(n_{\text{out}} \cdot n_{\text{in}})$ with 1 Gaussian sample, 1 multiply per element.
- **He Uniform/Normal:** Same as Xavier but with different scaling factor.

### Space Complexity

$O(n_{\text{out}} \cdot n_{\text{in}})$ for the generated weight matrix.

---

## Follow-Up Questions

### Q1: What happens if you initialize all weights to the same constant (e.g., all zeros)?

**Answer:** With all weights equal, every neuron in a layer computes the same function. During backpropagation, all neurons receive identical gradients (by symmetry), so they remain identical forever. This is called **symmetry breaking failure** — the network cannot learn diverse features.

With all zeros, the gradient is $\frac{\partial \mathcal{L}}{\partial w_{ij}} = \delta_j \cdot x_i$. If $x_i = 0$ or $\delta_j = 0$ for all samples, parameters never update.

**Rule:** Always break symmetry with random initialization.

### Q2: How does initialization interact with batch normalization?

**Answer:** Batch normalization (BN) reduces the dependence on initialization because it explicitly normalizes layer outputs to have mean 0 and variance 1:

$$\hat{x} = \frac{x - \mu}{\sqrt{\sigma^2 + \epsilon}}$$

With BN:
- You can use larger learning rates.
- Initialization becomes less critical (but still matters).
- He/Xavier initialization is still recommended as a starting point.
- Some works even use no scaling (unit Gaussian) with BN.

However, BN adds learnable parameters $\gamma, \beta$, so initialization of those must also be considered ($\gamma = 1, \beta = 0$ by default).

### Q3: Derive the appropriate variance for Leaky ReLU initialization.

**Answer:** For Leaky ReLU with negative slope $\alpha$:
- The variance of the output is reduced by a factor of $\frac{1 + \alpha^2}{2}$.
- To compensate: $\text{Var}(W) = \frac{2}{(1 + \alpha^2) \cdot n_{\text{in}}}$

**He Normal for LeakyReLU:**
$$W \sim \mathcal{N}\left(0, \sqrt{\frac{2}{(1 + \alpha^2) \cdot n_{\text{in}}}}\right)$$

For $\alpha = 0.01$: $\text{std} \approx \sqrt{2 / (1.0001 \cdot n_{\text{in}})} \approx \sqrt{2 / n_{\text{in}}}$ (same as ReLU).
For $\alpha = 0.3$: $\text{std} = \sqrt{2 / (1.09 \cdot n_{\text{in}})} \approx \sqrt{1.83 / n_{\text{in}}}$.

### Q4: What is the "optimal" initialization for very deep networks (>100 layers)?

**Answer:** For very deep networks, standard initialization still exhibits gradient issues. Solutions include:

1. **Residual connections** (ResNet): Skip connections create gradient highways, allowing training of 1000+ layers.
2. **Fixup initialization** (Zhang et al., 2019): Scale layers by $\sqrt{2/L}$ and use zero-initialized residual branches.
3. **Layer-wise adaptive rate scaling** (LARS): Adjusts learning rate per layer based on weight/gradient norm ratio.
4. **ReZero** (Bachlechner et al., 2020): Initialize residual branch scaling to 0, then learn it.

### Q5: Compare uniform vs. normal distributions for initialization.

**Answer:**

| Property | Uniform | Normal |
|---------|---------|--------|
| Bounded | Yes (values in $[-a, a]$) | No (but tails are thin) |
| Tail behavior | No outliers possible | Occasional large values |
| Variance control | Easier (bounded) | Statistical |
| Practical difference | Minimal — both work well |
| Historical preference | Glorot et al. (2010) used uniform | He et al. (2015) used normal |

In practice, both work equally well when scaled correctly. The choice is often determined by the framework's default.

### Q6: How would you initialize embeddings in NLP models?

**Answer:** Embedding initialization follows similar principles:

1. **Standard:** $\mathcal{N}(0, 1/d)$ or $\mathcal{U}[-1/d, 1/d]$ where $d$ is the embedding dimension.
2. **Pre-trained embeddings:** Load from pre-trained word vectors (Word2Vec, GloVe, fastText).
3. **Xavier/He:** Same formula using fan-in = embedding_dim.
4. **Uniform (default in PyTorch):** $\mathcal{U}[-1/\sqrt{d}, 1/\sqrt{d}]$.

For Transformers, embedding weights are often tied with output projection weights, and initialized with the same scheme.

### Q7: What gradient signal do different initializations produce at the start of training?

**Answer:** The initial gradient signal depends on the scale of initial weights:

- **Too small (e.g., 0.001 × Xavier):** Gradients vanish immediately — layer outputs are near zero, and gradients are proportionally small.
- **Xavier/He (correct):** Gradients have healthy magnitude — signal propagates well.
- **Too large (e.g., 100 × Xavier):** Gradients explode — activations saturate (tanh/sigmoid) or grow exponentially (ReLU).

The **gradient norm ratio** $\frac{\|\nabla_{\text{early}}\|}{\|\nabla_{\text{late}}\|}$ should be close to 1 for stable training. Proper initialization achieves this ratio within 1-2 orders of magnitude.

### Q8: How do you initialize weights for recurrent neural networks?

**Answer:** RNNs present additional challenges because the same weight matrix is applied at every timestep:

1. **Orthogonal initialization:** For the hidden-to-hidden weight matrix:
   $$W_{hh} = Q$$
   where $Q$ is a random orthogonal matrix $Q^T Q = I$. All eigenvalues have absolute value 1, preventing vanishing/exploding gradients over long sequences.

2. **Identity initialization:** Initialize $W_{hh} \approx I$ (for LSTMs, the forget gate bias is often initialized to 1-2, not 0).

3. **Spectral radius control:** Scale weights so that the largest singular value is exactly 1 (for tanh RNNs) or slightly less than 1 for stability.

In practice, LSTMs and GRUs are more robust to initialization than vanilla RNNs, but orthogonal initialization of $W_{hh}$ remains state-of-the-art.

---

## Test Cases

| Test Case | Initializer | Layer | Expected Std/Limit |
|-----------|------------|-------|-------------------|
| TC-01 | XavierUniform | 100→50 | limit=0.2, var=0.0133 |
| TC-02 | XavierNormal | 1000→500 | std≈0.0365 |
| TC-03 | HeUniform | 200→100 | limit≈0.1732 |
| TC-04 | HeNormal | 400→200 | std≈0.0707 |
| TC-05 | LeCunNormal | 256→128 | std=0.0625 |
| TC-06 | XavierUniform symmetric | Large N | ~50% positive, ~50% negative |
| TC-07 | Seed reproducibility | Any | Same seed = same weights |
| TC-08 | Bias initialization | 100 neurons | All zeros |
| TC-09 | Single neuron | 1→1 | Valid 1x1 matrix |
| TC-10 | Large layer | 10K×10K | No exception |

---

## Key Takeaways

- **Xavier/Glorot:** $\text{Var}(W) = 2/(n_{\text{in}} + n_{\text{out}})$ — for sigmoid/tanh.
- **He/Kaiming:** $\text{Var}(W) = 2/n_{\text{in}}$ — for ReLU.
- Proper initialization preserves activation variance across layers, preventing vanishing/exploding gradients.
- Uniform distributions use $[-a, a]$ where $a = \sqrt{3 \cdot \text{Var}(W)}$.
- Biases should always be initialized to 0 (except LSTM forget gate).
- He initialization is the default for modern ReLU-based networks.

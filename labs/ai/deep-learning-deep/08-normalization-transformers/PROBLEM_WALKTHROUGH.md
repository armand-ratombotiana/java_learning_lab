# PROBLEM WALKTHROUGH: Layer Normalization

## Problem Statement

**Difficulty:** Medium | **Category:** Normalization | **Estimated Time:** 60 minutes

Implement layer normalization as used in Transformers. Unlike batch normalization (which normalizes across the batch dimension), layer normalization normalizes across the feature dimension, making it independent of batch size and well-suited for sequence models. Your `LayerNorm` class must compute mean and variance along the hidden dimension, normalize, and apply learnable scale (gamma) and shift (beta) parameters. Additionally, implement both pre-norm and post-norm variants.

**Input:**
- `input`: A 3D or 2D array:
  - 3D: shape `(batchSize, seqLength, dModel)` for Transformer encoder/decoder layers.
  - 2D: shape `(batchSize, dModel)` for feed-forward outputs.
- `gamma`: A 1D array of length `dModel` — learnable scale parameter.
- `beta`: A 1D array of length `dModel` — learnable shift parameter.

**Output:**
- `output`: Same shape as input, with each feature vector normalized.

**Constraints:**
- Normalize across the last dimension only (the feature/hidden dimension).
- Compute mean and variance along that dimension.
- Add a small epsilon (`1e-5`) to the variance for numerical stability.
- The learnable parameters gamma and beta should have the same dimension as the hidden size.
- Support both pre-norm (normalize before the sub-layer) and post-norm (normalize after the sub-layer).

**Evaluation Criteria:**
- Correct normalization: mean ≈ 0, variance ≈ 1 after normalization.
- Numerical stability with extreme values.
- Proper application of gamma and beta.
- Independence across batch and sequence dimensions (each feature vector is normalized independently).

---

## Step-by-Step Solution Walkthrough

### 1. The Layer Normalization Equation

For an input `x` of dimension `d_model`:

```
μ = (1 / d_model) * Σ_{i=1}^{d_model} x_i
σ² = (1 / d_model) * Σ_{i=1}^{d_model} (x_i - μ)²
x̂_i = (x_i - μ) / sqrt(σ² + ε)
y_i = γ_i * x̂_i + β_i
```

Where:
- `μ` is the mean across the feature dimension.
- `σ²` is the variance across the feature dimension.
- `ε` is a small constant (typically 1e-5).
- `γ_i` and `β_i` are learnable parameters (initialized as 1 and 0 respectively).

### 2. Layer Normalization vs Batch Normalization

| Property | Layer Norm | Batch Norm |
|----------|-----------|------------|
| Normalization dimension | Features (last dim) | Batch (first dim) |
| Batch size dependence | Independent | Dependent |
| Sequence length dependence | Independent | Dependent |
| Training vs inference behavior | Same (no running stats) | Different (uses running stats at inference) |
| Parameters per layer | `2 * d_model` (γ, β) | `2 * d_model` (γ, β) + `2 * d_model` (running mean, var) |
| Suitable for RNNs | Yes | No (variable length) |
| Suitable for Transformers | Yes | Not typically (though some models use BN) |

**Why LayerNorm for Transformers:**
- Transformers process variable-length sequences in the same batch.
- Batch normalization would compute different statistics for different positions.
- Layer normalization treats each position independently, making it natural for sequence modeling.

### 3. Pre-Norm vs Post-Norm Architecture

**Post-Norm (original Transformer):**
```
x → [LayerNorm → Sublayer] → add → output
```
```
output = x + Sublayer(LayerNorm(x))
```

**Pre-Norm (modern practice):**
```
x → [Sublayer → add] → LayerNorm → output
```
```
output = LayerNorm(x + Sublayer(x))
```

| Aspect | Post-Norm | Pre-Norm |
|--------|-----------|----------|
| Original Transformer | Yes | No |
| Training stability | Lower (needs warmup + careful LR) | Higher (stable without warmup) |
| Gradient flow | Residual path has Norm | Clean residual path |
| Final layer output | Normalized | Must add final Norm |
| Used in | Original paper | GPT, BERT, Llama, most modern models |

**Why Pre-Norm is more stable:** The residual path `x + Sublayer(x)` in Pre-Norm goes through LayerNorm *after* the addition, so the identity path is completely clean:

```
Output = Norm(x + F(x))
∂Output/∂x = Norm'(...) * (1 + ∂F/∂x)
```

In Post-Norm:
```
Output = x + F(Norm(x))
∂Output/∂x = 1 + F'(Norm(x)) * Norm'(x)
```

Post-Norm has an additional `Norm'(x)` term which can cause vanishing gradients when the variance is small.

### 4. RMSNorm — An Efficient Alternative

Root Mean Square Normalization (RMSNorm) simplifies LayerNorm by omitting the mean subtraction:

```
RMS(x) = sqrt((1/d) * Σ x_i²)
x̂_i = x_i / RMS(x + ε)
y_i = γ_i * x̂_i
```

**Why RMSNorm works:**
- The mean subtraction in LayerNorm is often redundant because the mean is already close to 0 due to the preceding layer normalization.
- RMSNorm saves computation (no mean, no subtraction).
- Empirically, RMSNorm performs similarly to LayerNorm in large models.

### 5. Algorithm Pseudocode

```
function layerNorm(x, gamma, beta, epsilon):
    // x shape: (..., dModel)
    // Compute mean and variance along the last dimension
    mean = mean(x, dim=-1)          // shape: (..., 1)
    var  = variance(x, dim=-1)      // shape: (..., 1)

    // Normalize
    x_hat = (x - mean) / sqrt(var + epsilon)

    // Scale and shift
    y = gamma * x_hat + beta

    return y
```

---

## Java Implementation

```java
package lab08.transformer;

/**
 * Layer normalization as used in Transformers.
 * <p>
 * Normalizes across the feature (last) dimension independently for each
 * sample and position. Supports both pre-norm and post-norm placement.
 * <p>
 * LayerNorm(x) = gamma * (x - mean) / sqrt(var + epsilon) + beta
 */
public class LayerNorm {

    private final int dModel;
    private final double epsilon;

    // Learnable parameters
    private double[] gamma; // scale
    private double[] beta;  // shift

    // Running statistics for the last forward pass (for debugging)
    private double lastMean;
    private double lastVar;

    /**
     * Constructs a LayerNorm module.
     *
     * @param dModel  the feature/hidden dimension
     * @param epsilon small constant for numerical stability (default 1e-5)
     */
    public LayerNorm(int dModel, double epsilon) {
        if (dModel <= 0) {
            throw new IllegalArgumentException("dModel must be positive, got: " + dModel);
        }
        if (epsilon <= 0) {
            throw new IllegalArgumentException("epsilon must be positive, got: " + epsilon);
        }
        this.dModel = dModel;
        this.epsilon = epsilon;

        // Initialize gamma = 1, beta = 0
        this.gamma = new double[dModel];
        this.beta = new double[dModel];
        for (int i = 0; i < dModel; i++) {
            this.gamma[i] = 1.0;
            this.beta[i] = 0.0;
        }
    }

    /**
     * Forward pass for a 3D input (batchSize, seqLength, dModel).
     *
     * @param input 3D tensor
     * @return normalized 3D tensor of the same shape
     */
    public double[][] forward3d(double[][] input) {
        int batchSize = input.length;
        int seqLength = input[0].length;
        int d = input[0][0].length;

        if (d != dModel) {
            throw new IllegalArgumentException(
                "Input last dim " + d + " doesn't match dModel " + dModel);
        }

        double[][] output = new double[batchSize][seqLength][dModel];

        double totalMean = 0;
        double totalVar = 0;
        int count = 0;

        for (int b = 0; b < batchSize; b++) {
            for (int s = 0; s < seqLength; s++) {
                // Compute mean
                double mean = 0;
                for (int i = 0; i < dModel; i++) {
                    mean += input[b][s][i];
                }
                mean /= dModel;

                // Compute variance
                double var = 0;
                for (int i = 0; i < dModel; i++) {
                    double diff = input[b][s][i] - mean;
                    var += diff * diff;
                }
                var /= dModel;

                // Normalize, scale, shift
                double std = Math.sqrt(var + epsilon);
                for (int i = 0; i < dModel; i++) {
                    double normalized = (input[b][s][i] - mean) / std;
                    output[b][s][i] = gamma[i] * normalized + beta[i];
                }

                totalMean += mean;
                totalVar += var;
                count++;
            }
        }

        this.lastMean = totalMean / count;
        this.lastVar = totalVar / count;

        return output;
    }

    /**
     * Forward pass for a 2D input (batchSize, dModel).
     *
     * @param input 2D tensor
     * @return normalized 2D tensor of the same shape
     */
    public double[][] forward2d(double[][] input) {
        int batchSize = input.length;
        int d = input[0].length;

        if (d != dModel) {
            throw new IllegalArgumentException(
                "Input last dim " + d + " doesn't match dModel " + dModel);
        }

        double[][] output = new double[batchSize][dModel];

        double totalMean = 0;
        double totalVar = 0;

        for (int b = 0; b < batchSize; b++) {
            // Compute mean
            double mean = 0;
            for (int i = 0; i < dModel; i++) {
                mean += input[b][i];
            }
            mean /= dModel;

            // Compute variance
            double var = 0;
            for (int i = 0; i < dModel; i++) {
                double diff = input[b][i] - mean;
                var += diff * diff;
            }
            var /= dModel;

            // Normalize, scale, shift
            double std = Math.sqrt(var + epsilon);
            for (int i = 0; i < dModel; i++) {
                double normalized = (input[b][i] - mean) / std;
                output[b][i] = gamma[i] * normalized + beta[i];
            }

            totalMean += mean;
            totalVar += var;
        }

        this.lastMean = totalMean / batchSize;
        this.lastVar = totalVar / batchSize;

        return output;
    }

    /**
     * Applies pre-norm: normalize before the sublayer.
     * output = LayerNorm(input)
     */
    public double[][] preNorm(double[][] input) {
        // input is 3D (batchSize, seqLength, dModel)
        return forward3d(input);
    }

    /**
     * Applies post-norm after residual addition.
     * output = LayerNorm(input + sublayerOutput)
     */
    public double[][] postNorm(double[][] input, double[][] sublayerOutput) {
        int batchSize = input.length;
        int seqLength = input[0].length;
        int d = input[0][0].length;

        double[][] sum = new double[batchSize][seqLength][d];
        for (int b = 0; b < batchSize; b++) {
            for (int s = 0; s < seqLength; s++) {
                for (int i = 0; i < d; i++) {
                    sum[b][s][i] = input[b][s][i] + sublayerOutput[b][s][i];
                }
            }
        }
        return forward3d(sum);
    }

    // ---- Getters ----

    public double[] getGamma() { return gamma; }
    public double[] getBeta() { return beta; }

    /**
     * Returns the average mean from the last forward pass (for debugging).
     */
    public double getLastMean() { return lastMean; }

    /**
     * Returns the average variance from the last forward pass (for debugging).
     */
    public double getLastVar() { return lastVar; }

    public int getDModel() { return dModel; }
}
```

**Example Usage:**

```java
package lab08.transformer;

import java.util.Arrays;

public class LayerNormExample {
    public static void main(String[] args) {
        int dModel = 8;
        LayerNorm ln = new LayerNorm(dModel, 1e-5);

        // 2D input: batch of 4 samples
        double[][] input2d = new double[4][dModel];
        for (int b = 0; b < 4; b++) {
            for (int i = 0; i < dModel; i++) {
                input2d[b][i] = (b + 1) * (i + 1); // Different scales per sample
            }
        }

        double[][] output2d = ln.forward2d(input2d);

        // Check normalization
        for (int b = 0; b < 4; b++) {
            double mean = 0;
            double var = 0;
            for (int i = 0; i < dModel; i++) {
                mean += output2d[b][i];
            }
            mean /= dModel;
            for (int i = 0; i < dModel; i++) {
                var += (output2d[b][i] - mean) * (output2d[b][i] - mean);
            }
            var /= dModel;
            System.out.println("Sample " + b + ": mean=" + mean + ", var=" + var);
        }

        // 3D input: batch of 2, seq len 3
        double[][] input3d = new double[2][3][dModel];
        for (int b = 0; b < 2; b++) {
            for (int s = 0; s < 3; s++) {
                for (int i = 0; i < dModel; i++) {
                    input3d[b][s][i] = Math.sin(b + s + i * 0.5);
                }
            }
        }
        double[][] output3d = ln.forward3d(input3d);
        System.out.println("3D output shape: " + output3d[0].length + "x" + output3d[0][0].length);
    }
}
```

---

## Complexity Analysis

### Time Complexity

For an input of shape `(B, N, d)`:

| Operation | Complexity |
|-----------|------------|
| Mean computation | `B * N * d` additions |
| Variance computation | `B * N * d` multiplications + `B * N * d` additions |
| Normalization | `B * N * d` subtractions + `B * N * d` divisions |
| Scale and shift | `2 * B * N * d` multiply-adds |

**Total:** `O(6 * B * N * d) = O(B * N * d)`

### Space Complexity

**Parameters:** `2 * d` (gamma and beta) — negligible.

**Forward pass:**
- Input: `B * N * d`
- Temporary (mean, var): `B * N * 2`
- Output: `B * N * d`

**Total:** `O(B * N * d)`

### Comparison of Normalization Techniques

| Norm Type | Operations per Element | Parameters | Batch Dependent |
|-----------|----------------------|------------|-----------------|
| LayerNorm | ~6 FLOPs | `2*d` | No |
| BatchNorm | ~4 FLOPs | `2*d` + running stats | Yes |
| RMSNorm | ~4 FLOPs | `d` | No |
| InstanceNorm | ~6 FLOPs | `2*d` | No (per sample) |

---

## Follow-Up Questions with Answers

### Q1: Why does LayerNorm work better than BatchNorm in Transformers?

**Answer:**

1. **Variable sequence length:** In NLP, sequences in a batch have different lengths. BatchNorm would compute different statistics for padding vs real tokens. LayerNorm treats each position independently, avoiding this issue.

2. **Batch size independence:** LayerNorm's statistics are independent of batch size. At inference, small batch sizes or single samples work identically. BatchNorm requires running averages and behaves differently at training vs inference.

3. **Gradient flow:** LayerNorm doesn't create dependencies between samples in a batch, which can lead to more stable gradients.

4. **Theoretical motivation:** LayerNorm is invariant to scaling of the entire hidden vector, which matches the intuition that the "direction" of a hidden state matters more than its magnitude in attention-based models.

5. **Empirical evidence:** Transformers trained with LayerNorm consistently outperform those trained with BatchNorm across a wide range of NLP tasks.

### Q2: Explain the difference between pre-norm and post-norm in Transformer architectures.

**Answer:**

**Post-Norm (original Transformer):**
```
Output = LayerNorm(x + Sublayer(x))
```
- Normalization is applied *after* the residual addition.
- The residual branch passes through LayerNorm, which can hinder gradient flow.
- Requires careful initialization and learning rate warmup.
- More sensitive to hyperparameters.

**Pre-Norm (modern practice, GPT, BERT, Llama):**
```
Output = x + Sublayer(LayerNorm(x))
```
- Normalization is applied *before* each sublayer.
- The residual path has no normalization — clean gradient highway.
- More stable training, works without warmup.
- Slightly worse final perplexity with the same number of layers (but allows much deeper models).

**Empirical findings:**
- Pre-Norm can train twice as fast as Post-Norm for deep models.
- Post-Norm has slightly better representational power for shallow models.
- Modern practice: Pre-Norm with a final LayerNorm after the last layer.

### Q3: What is RMSNorm and when would you use it instead of LayerNorm?

**Answer:** RMSNorm simplifies LayerNorm by removing the mean-centering step:

```
RMSNorm(x) = x / sqrt(mean(x_i²) + ε) * γ_i
```

**Differences from LayerNorm:**
- No mean computation or subtraction (~30% fewer FLOPs).
- Only uses `d` parameters (gamma only, no beta) — removes shift.
- Empirically, the mean subtraction is often redundant since the mean of hidden states is usually near zero after the first layer.

**When to use RMSNorm:**
- When training very large models (every bit of computation matters).
- When the mean of hidden states is expected to be near zero.
- When parameter efficiency is critical.
- Used in: Llama, Mistral, Gemma, and many modern LLMs.

**Trade-off:** RMSNorm loses the shift invariance property of LayerNorm, but in practice this is not a significant issue.

### Q4: How would you handle the backward pass for LayerNorm?

**Answer:** The backward pass computes gradients of the loss with respect to input, gamma, and beta.

**Gradients:**

```
∂L/∂γ_i = Σ_{b,s} ∂L/∂y_{b,s,i} * x̂_{b,s,i}
∂L/∂β_i = Σ_{b,s} ∂L/∂y_{b,s,i}
```

Where `x̂` is the normalized input (before scale and shift).

For `∂L/∂x`, we need to backpropagate through the normalization:

```
∂L/∂x_i = (1 / (N * σ)) * (N * ∂L/∂x̂_i - Σ_j ∂L/∂x̂_j - x̂_i * Σ_j ∂L/∂x̂_j * x̂_j)
```

Where `N = d_model`, `σ = sqrt(var + ε)`.

This can be simplified using the gradient formula:

```
dx = (1 / (N * σ)) * (N * dx̂ - sum(dx̂) - x̂ * sum(dx̂ * x̂))
```

**Efficient implementation:**
- First compute `dx̂ = γ * ∂L/∂y` (element-wise).
- Then compute `sum(dx̂)` and `sum(dx̂ * x̂)` (scalar values).
- Apply the formula to get `dx`.

### Q5: What happens to LayerNorm when the input is all zeros?

**Answer:** With all-zero input:
```
mean = 0
var = 0
x̂_i = (0 - 0) / sqrt(0 + ε) = 0
y_i = γ_i * 0 + β_i = β_i
```

So the output is just the bias term `β`. This is the correct behavior — when there's no signal, the layer outputs its learned bias.

**Without epsilon:** `var = 0` would cause division by zero, which is why `ε` is critical.

**The variance stabilization property:** Even with very small variance, the normalization ensures that the output has variance approximately equal to `γ²`, which keeps activations in a well-behaved range for subsequent layers.

---

## Test Cases

### Test Case 1: Basic Normalization (2D)

```java
void testBasicNormalization2D() {
    int dModel = 10;
    LayerNorm ln = new LayerNorm(dModel, 1e-5);

    double[][] input = new double[1][dModel];
    for (int i = 0; i < dModel; i++) {
        input[0][i] = i + 1; // 1, 2, 3, ..., 10
    }

    double[][] output = ln.forward2d(input);

    // Check mean ≈ 0, var ≈ 1
    double mean = 0;
    double var = 0;
    for (int i = 0; i < dModel; i++) {
        mean += output[0][i];
    }
    mean /= dModel;
    for (int i = 0; i < dModel; i++) {
        var += (output[0][i] - mean) * (output[0][i] - mean);
    }
    var /= dModel;

    assert Math.abs(mean) < 1e-10 : "Mean should be ~0, got " + mean;
    assert Math.abs(var - 1.0) < 1e-6 : "Variance should be ~1, got " + var;
}
```

### Test Case 2: Basic Normalization (3D)

```java
void testBasicNormalization3D() {
    int dModel = 8;
    LayerNorm ln = new LayerNorm(dModel, 1e-5);

    double[][] input = new double[2][4][dModel];
    for (int b = 0; b < 2; b++) {
        for (int s = 0; s < 4; s++) {
            for (int i = 0; i < dModel; i++) {
                input[b][s][i] = (b + 1) * (s + 1) * (i + 1);
            }
        }
    }

    double[][] output = ln.forward3d(input);

    for (int b = 0; b < 2; b++) {
        for (int s = 0; s < 4; s++) {
            double mean = 0;
            double var = 0;
            for (int i = 0; i < dModel; i++) {
                mean += output[b][s][i];
            }
            mean /= dModel;
            for (int i = 0; i < dModel; i++) {
                var += (output[b][s][i] - mean) * (output[b][s][i] - mean);
            }
            var /= dModel;

            assert Math.abs(mean) < 1e-10 :
                "Mean at [" + b + "][" + s + "] should be ~0, got " + mean;
            assert Math.abs(var - 1.0) < 1e-6 :
                "Var at [" + b + "][" + s + "] should be ~1, got " + var;
        }
    }
}
```

### Test Case 3: Gamma and Beta Application

```java
void testGammaBeta() {
    int dModel = 6;
    LayerNorm ln = new LayerNorm(dModel, 1e-5);

    // Override gamma and beta
    double[] customGamma = {2, 2, 2, 2, 2, 2};
    double[] customBeta = {10, 10, 10, 10, 10, 10};
    ln.getGamma()[0] = 2; Arrays.fill(ln.getGamma(), 2.0);
    Arrays.fill(ln.getBeta(), 10.0);

    double[][] input = new double[1][dModel];
    for (int i = 0; i < dModel; i++) {
        input[0][i] = i + 1;
    }

    double[][] output = ln.forward2d(input);

    // After gamma=2 and beta=10:
    // output should have mean ≈ 10, std ≈ 2
    double mean = 0;
    double var = 0;
    for (int i = 0; i < dModel; i++) {
        mean += output[0][i];
    }
    mean /= dModel;
    for (int i = 0; i < dModel; i++) {
        var += (output[0][i] - mean) * (output[0][i] - mean);
    }
    var /= dModel;

    assert Math.abs(mean - 10.0) < 1e-10 : "Mean should be ~10 with beta=10, got " + mean;
    assert Math.abs(var - 4.0) < 1e-6 : "Variance should be ~4 with gamma=2, got " + var;
}
```

### Test Case 4: Numerical Stability with Zero Input

```java
void testZeroInput() {
    int dModel = 8;
    LayerNorm ln = new LayerNorm(dModel, 1e-5);

    double[][] input = new double[3][dModel]; // all zeros

    double[][] output = ln.forward2d(input);

    // Mean should be 0, variance should be close to 1 (with gamma=1, beta=0,
    // and all-zero input, output should be 0/epsilon normalization)
    for (int b = 0; b < 3; b++) {
        double mean = 0;
        double var = 0;
        for (int i = 0; i < dModel; i++) {
            mean += output[b][i];
        }
        mean /= dModel;
        for (int i = 0; i < dModel; i++) {
            var += (output[b][i] - mean) * (output[b][i] - mean);
        }
        var /= dModel;

        assert !Double.isNaN(mean) : "Mean should not be NaN";
        assert !Double.isNaN(var) : "Variance should not be NaN";
    }
}
```

### Test Case 5: Different Batch Elements Have Different Normalization

```java
void testIndependence() {
    int dModel = 10;
    LayerNorm ln = new LayerNorm(dModel, 1e-5);

    double[][] input = new double[2][dModel];
    // Batch 0: all ones
    Arrays.fill(input[0], 1.0);
    // Batch 1: linear ramp from 1 to 10
    for (int i = 0; i < dModel; i++) {
        input[1][i] = i + 1;
    }

    double[][] output = ln.forward2d(input);

    // Batch 0: input = [1, 1, ..., 1], normalized = [0, 0, ..., 0]
    // scaled by gamma and shifted by beta: output = beta
    for (int i = 0; i < dModel; i++) {
        assert Math.abs(output[0][i] - 0.0) < 1e-10 :
            "Constant input should normalize to 0+beta=0, got " + output[0][i];
    }

    // Batch 1 should be non-constant
    boolean hasVariation = false;
    for (int i = 1; i < dModel; i++) {
        if (Math.abs(output[1][i] - output[1][0]) > 1e-6) {
            hasVariation = true;
            break;
        }
    }
    assert hasVariation : "Varying input should produce varying output";
}
```

### Test Case 6: Scale Invariance

```java
void testScaleInvariance() {
    int dModel = 10;
    LayerNorm ln = new LayerNorm(dModel, 1e-5);

    double[][] inputSmall = new double[1][dModel];
    double[][] inputLarge = new double[1][dModel];
    for (int i = 0; i < dModel; i++) {
        inputSmall[0][i] = i + 1;
        inputLarge[0][i] = (i + 1) * 100.0;
    }

    double[][] outputSmall = ln.forward2d(inputSmall);
    double[][] outputLarge = ln.forward2d(inputLarge);

    // LayerNorm is scale-invariant: scaling the input doesn't change
    // the normalized output (except through epsilon)
    for (int i = 0; i < dModel; i++) {
        assert Math.abs(outputSmall[0][i] - outputLarge[0][i]) < 1e-6 :
            "LayerNorm should be scale-invariant (diff at " + i + ": "
            + outputSmall[0][i] + " vs " + outputLarge[0][i] + ")";
    }
}
```

### Test Case 7: Pre-Norm vs Post-Norm Architecture

```java
void testPreNormPostNorm() {
    int dModel = 8;
    int seqLen = 4;
    int batchSize = 2;

    LayerNorm ln = new LayerNorm(dModel, 1e-5);

    double[][] input = new double[batchSize][seqLen][dModel];
    double[][] sublayerOutput = new double[batchSize][seqLen][dModel];

    for (int b = 0; b < batchSize; b++) {
        for (int s = 0; s < seqLen; s++) {
            for (int i = 0; i < dModel; i++) {
                input[b][s][i] = Math.sin(b + s + i);
                sublayerOutput[b][s][i] = Math.cos(b + s + i);
            }
        }
    }

    // Pre-norm: normalize before sublayer
    double[][] preNormOut = ln.preNorm(input);

    // Post-norm: normalize after adding sublayer output
    double[][] postNormOut = ln.postNorm(input, sublayerOutput);

    assert preNormOut.length == batchSize;
    assert preNormOut[0].length == seqLen;
    assert preNormOut[0][0].length == dModel;

    assert postNormOut.length == batchSize;
    assert postNormOut[0].length == seqLen;
    assert postNormOut[0][0].length == dModel;

    // Pre-norm output should have ~0 mean, ~1 variance per position
    for (int b = 0; b < batchSize; b++) {
        for (int s = 0; s < seqLen; s++) {
            double mean = 0, var = 0;
            for (int i = 0; i < dModel; i++) mean += preNormOut[b][s][i];
            mean /= dModel;
            for (int i = 0; i < dModel; i++) var += (preNormOut[b][s][i] - mean) * (preNormOut[b][s][i] - mean);
            var /= dModel;
            assert Math.abs(mean) < 1e-10 : "Pre-norm mean should be 0";
            assert Math.abs(var - 1.0) < 1e-6 : "Pre-norm var should be 1";
        }
    }
}
```

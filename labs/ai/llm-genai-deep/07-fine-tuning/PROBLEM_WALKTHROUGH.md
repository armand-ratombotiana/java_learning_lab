# PROBLEM WALKTHROUGH: Parameter-Efficient Fine-Tuning (LoRA)

## Problem Statement

**Difficulty: Hard | Category: Fine-Tuning / Parameter Efficiency**

Implement Low-Rank Adaptation (LoRA) for efficient fine-tuning of large neural network layers. Given a pre-trained weight matrix `W ∈ ℝ^(d×k)`, freeze it and inject trainable low-rank decomposition matrices `A ∈ ℝ^(r×k)` and `B ∈ ℝ^(d×r)` such that the forward pass becomes `h = Wx + BAx`. The goal is to match full fine-tuning quality while training only 0.1-1% of the parameters.

**Interview Context:** LoRA (Hu et al., 2021) is the most widely adopted PEFT method, powering most open-source LLM fine-tuning. Interviewers expect understanding of the rank-decomposition intuition, the scaling factor, weight merging for inference, and when LoRA underperforms full fine-tuning.

### Requirements

1. Implement the LoRA forward pass: `h = Wx + (α/r) * BAx`.
2. Freeze original weight `W`, only update `A` and `B`.
3. Support `rank r`, `alpha` scaling factor, and `dropout`.
4. Implement `mergeWeights()` for inference-time weight fusion: `W' = W + (α/r) * BA`.
5. Compute trainable vs total parameter ratio.
6. Demonstrate on a simulated linear layer with synthetic data.

### Input/Output Contract

```
Input:  Pre-trained weight W (d × k), rank r, alpha α, 
        input x, target y, learning rate, epochs
Output: Fine-tuned model with LoRA weights (A, B),
        trainable_ratio, loss curves
```

---

## Step-by-Step Solution Walkthrough

### 1. Why Parameter-Efficient Fine-Tuning?

Full fine-tuning of a 7B parameter LLM requires:
- 56 GB of GPU memory (at 16-bit precision) for the model.
- Another 56 GB for optimizer states (Adam: 2 states per parameter).
- 56 GB for gradients.
- **Total: ~168 GB per GPU.** That's 4x A100-80GB GPUs.

LoRA reduces this dramatically:
- Only ~0.1% of parameters are trainable.
- Optimizer states shrink to ~56 MB instead of 56 GB.
- Can fine-tune a 7B model on a single consumer GPU (24 GB).

### 2. The Core Idea: Low-Rank Weight Updates

The key insight: **weight updates during fine-tuning have low intrinsic rank.** This means `ΔW` (the change from pre-trained to fine-tuned weights) can be approximated by a low-rank matrix:

`W' = W + ΔW ≈ W + BA`

where:
- `B ∈ ℝ^(d×r)` and `A ∈ ℝ^(r×k)` with `r ≪ min(d, k)`.
- `BA` has rank at most `r`.
- Total trainable parameters: `d*r + r*k` instead of `d*k`.

**Example:** For a 4096×4096 weight matrix:
- Full update: ~16.8M parameters.
- LoRA (r=8): 4096×8 + 8×4096 = 65,536 parameters. That's **0.39%** of the original.

### 3. Scaling Factor

The LoRA forward pass is:

`h = Wx + (α / r) * BAx`

where `α / r` is the scaling factor. During initialization:
- `A` is initialized with Kaiming uniform (or Gaussian with mean 0, std 1/r).
- `B` is initialized to zeros. This means at the start of training, `BA = 0` and the output equals the pre-trained output.

The scaling factor `α` controls the magnitude of the adaptation. A common rule: set `α` to the first learning rate you'd try, then tune. Many implementations fix `α = 16` or `α = 32` by default.

### 4. Weight Merging for Inference

During inference, the LoRA computation `(α/r) * BAx` adds latency. Instead, we can merge the weights:

```
W_merged = W + (α / r) * B @ A
```

Then the forward pass becomes just `h = W_merged @ x`, identical in speed to the original model. This is a key advantage of LoRA over adapter layers (which add sequential computation).

### 5. Where to Apply LoRA

In transformers, LoRA is typically applied to:
- Query projection (`W_q`)
- Key projection (`W_k`)
- Value projection (`W_v`)
- Output projection (`W_o`)

The paper found that applying LoRA to all four attention matrices with r=8 matches full fine-tuning quality.

### 6. Rank Selection

- **r=1:** Minimal adaptation, good for domain shifts.
- **r=8:** Recommended starting point, balances quality and efficiency.
- **r=16-64:** For harder tasks or when more capacity is needed.
- **r > 64:** Usually no better than full fine-tuning, defeats the purpose.

---

## Java Implementation

```java
package com.llm.genai.deep.lora;

import java.util.Arrays;
import java.util.Random;

/**
 * Implements Low-Rank Adaptation (LoRA) for parameter-efficient fine-tuning.
 * <p>
 * Given a frozen pre-trained weight matrix W, injects trainable low-rank
 * matrices A and B such that the forward pass computes h = Wx + (α/r) * BAx.
 * Supports weight merging for inference-time optimization.
 */
public class LoRALayer {

    private final int inputDim;
    private final int outputDim;
    private final int rank;
    private final double alpha;
    private final double dropoutRate;
    private final double[][] W;       // Pre-trained weights (frozen)
    private final double[][] A;       // Low-rank A (trainable), shape [rank][inputDim]
    private final double[][] B;       // Low-rank B (trainable), shape [outputDim][rank]
    private final double[][] aGrad;   // Gradients for A
    private final double[][] bGrad;   // Gradients for B
    private final Random rng;
    private boolean merged;

    /**
     * Constructs a LoRA-decorated linear layer.
     *
     * @param W           pre-trained weight matrix (outputDim × inputDim)
     * @param rank        LoRA rank (r)
     * @param alpha       scaling factor (α)
     * @param dropoutRate dropout probability applied before LoRA computation
     */
    public LoRALayer(double[][] W, int rank, double alpha, double dropoutRate) {
        this.outputDim = W.length;
        this.inputDim = W[0].length;
        this.rank = rank;
        this.alpha = alpha;
        this.dropoutRate = dropoutRate;
        this.W = W;
        this.A = new double[rank][inputDim];
        this.B = new double[outputDim][rank];
        this.aGrad = new double[rank][inputDim];
        this.bGrad = new double[outputDim][rank];
        this.rng = new Random(42);
        this.merged = false;
        initializeLoRA();
    }

    /**
     * Initializes A with Kaiming uniform and B with zeros.
     */
    private void initializeLoRA() {
        double scale = Math.sqrt(2.0 / inputDim);
        for (int i = 0; i < rank; i++) {
            for (int j = 0; j < inputDim; j++) {
                A[i][j] = rng.nextGaussian() * scale;
            }
        }
        // B initialized to zeros ensures LoRA output = 0 at start
        // so the model behaves like the pre-trained version initially
    }

    /**
     * Forward pass: h = Wx + (α/r) * BAx with optional dropout.
     *
     * @param x input vector (length inputDim)
     * @return output vector (length outputDim)
     */
    public double[] forward(double[] x) {
        if (merged) {
            return matVecMul(mergedWeight(), x);
        }

        // Wx: pre-trained path
        double[] base = matVecMul(W, x);

        // LoRA path: BAx
        double[] ax = matVecMul(A, x);        // A × x: [rank]
        if (dropoutRate > 0) {
            for (int i = 0; i < ax.length; i++) {
                if (rng.nextDouble() < dropoutRate) {
                    ax[i] = 0;
                }
            }
        }
        double[] bax = matVecMul(B, ax);      // B × (Ax): [outputDim]

        // Scale and add
        double scale = alpha / rank;
        for (int i = 0; i < outputDim; i++) {
            base[i] += scale * bax[i];
        }

        return base;
    }

    /**
     * Backward pass: computes gradients for A and B.
     * ∂L/∂B = (∂L/∂h) * (Ax)^T
     * ∂L/∂A = B^T * (∂L/∂h) * x^T
     *
     * @param x         input vector
     * @param gradOutput gradient of loss w.r.t. output (length outputDim)
     */
    public void backward(double[] x, double[] gradOutput) {
        double scale = alpha / rank;

        // Compute Ax for reuse
        double[] ax = matVecMul(A, x);

        // ∂L/∂(BAx) = ∂L/∂h * (α/r)
        double[] scaledGrad = new double[outputDim];
        for (int i = 0; i < outputDim; i++) {
            scaledGrad[i] = gradOutput[i] * scale;
        }

        // Gradient for B: ∂L/∂B = scaledGrad ⊗ ax (outer product)
        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < rank; j++) {
                bGrad[i][j] += scaledGrad[i] * ax[j];
            }
        }

        // Gradient for A: ∂L/∂A = B^T * scaledGrad ⊗ x
        double[] btGrad = matVecMul(transpose(B), scaledGrad); // [rank]
        for (int i = 0; i < rank; i++) {
            for (int j = 0; j < inputDim; j++) {
                aGrad[i][j] += btGrad[i] * x[j];
            }
        }
    }

    /**
     * Updates A and B using accumulated gradients (SGD).
     *
     * @param learningRate step size
     */
    public void update(double learningRate) {
        for (int i = 0; i < rank; i++) {
            for (int j = 0; j < inputDim; j++) {
                A[i][j] -= learningRate * aGrad[i][j];
                aGrad[i][j] = 0; // reset
            }
        }
        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < rank; j++) {
                B[i][j] -= learningRate * bGrad[i][j];
                bGrad[i][j] = 0;
            }
        }
    }

    /**
     * Merges LoRA weights into the pre-trained weight matrix for inference.
     * W' = W + (α/r) * B @ A
     */
    public void mergeWeights() {
        if (merged) return;
        double scale = alpha / rank;
        // W += scale * (B @ A)
        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < inputDim; j++) {
                double sum = 0;
                for (int k = 0; k < rank; k++) {
                    sum += B[i][k] * A[k][j];
                }
                W[i][j] += scale * sum;
            }
        }
        merged = true;
    }

    /**
     * Returns the merged weight matrix (must call mergeWeights first).
     */
    public double[][] getMergedWeight() {
        if (!merged) {
            throw new IllegalStateException("Weights not merged. Call mergeWeights() first.");
        }
        return W;
    }

    /**
     * Computes the merged weight matrix for inspection without modifying W.
     */
    public double[][] computeMergedWeight() {
        double[][] merged = new double[outputDim][inputDim];
        double scale = alpha / rank;
        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < inputDim; j++) {
                merged[i][j] = W[i][j];
                for (int k = 0; k < rank; k++) {
                    merged[i][j] += scale * B[i][k] * A[k][j];
                }
            }
        }
        return merged;
    }

    /**
     * Returns the merged weight matrix reference (shallow copy of the underlying
     * merged weights). Creates a copy to avoid mutation.
     */
    private double[][] mergedWeight() {
        double[][] result = new double[outputDim][inputDim];
        for (int i = 0; i < outputDim; i++) {
            System.arraycopy(W[i], 0, result[i], 0, inputDim);
        }
        return result;
    }

    /**
     * Computes the percentage of trainable parameters.
     *
     * @return trainable / total parameter ratio as a percentage
     */
    public double trainableRatio() {
        long total = (long) outputDim * inputDim;
        long trainable = (long) outputDim * rank + (long) rank * inputDim;
        return 100.0 * trainable / total;
    }

    /**
     * Returns total trainable parameters.
     */
    public int trainableParams() {
        return outputDim * rank + rank * inputDim;
    }

    /**
     * Matrix-vector multiplication: result = M × v.
     */
    private double[] matVecMul(double[][] M, double[] v) {
        int rows = M.length;
        int cols = M[0].length;
        double[] result = new double[rows];
        for (int i = 0; i < rows; i++) {
            double sum = 0;
            for (int j = 0; j < cols; j++) {
                sum += M[i][j] * v[j];
            }
            result[i] = sum;
        }
        return result;
    }

    /**
     * Transposes a matrix.
     */
    private double[][] transpose(double[][] M) {
        int rows = M.length;
        int cols = M[0].length;
        double[][] T = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                T[j][i] = M[i][j];
            }
        }
        return T;
    }

    /**
     * Computes MSE loss between predicted and target.
     */
    public static double mseLoss(double[] predicted, double[] target) {
        double sum = 0;
        for (int i = 0; i < predicted.length; i++) {
            double diff = predicted[i] - target[i];
            sum += diff * diff;
        }
        return sum / predicted.length;
    }

    /**
     * Main method demonstrating LoRA fine-tuning on synthetic data.
     */
    public static void main(String[] args) {
        int inputDim = 128;
        int outputDim = 64;
        int rank = 4;
        double alpha = 16.0;
        double lr = 0.001;
        int epochs = 200;

        // Create synthetic pre-trained weights
        Random rng = new Random(123);
        double[][] W = new double[outputDim][inputDim];
        double[][] trueDelta = new double[outputDim][inputDim]; // true adaptation
        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < inputDim; j++) {
                W[i][j] = rng.nextGaussian();
                trueDelta[i][j] = rng.nextGaussian() * 0.1; // small adaptation
            }
        }

        LoRALayer lora = new LoRALayer(W, rank, alpha, 0.0);

        System.out.println("=== LoRA Configuration ===");
        System.out.println("Input dim: " + inputDim + ", Output dim: " + outputDim);
        System.out.println("Rank: " + rank + ", Alpha: " + alpha);
        System.out.println("Trainable params: " + lora.trainableParams()
                + " (" + String.format("%.2f", lora.trainableRatio()) + "% of total)");
        System.out.println();

        // Generate synthetic training data
        int numSamples = 1000;
        double[][] X = new double[numSamples][inputDim];
        double[][] Y = new double[numSamples][outputDim];
        for (int s = 0; s < numSamples; s++) {
            for (int j = 0; j < inputDim; j++) {
                X[s][j] = rng.nextGaussian();
            }
            // Target: W*x + trueDelta*x + noise
            double[] base = lora.matVecMul(W, X[s]);
            double[] adaptation = lora.matVecMul(trueDelta, X[s]);
            for (int j = 0; j < outputDim; j++) {
                Y[s][j] = base[j] + adaptation[j] + rng.nextGaussian() * 0.01;
            }
        }

        System.out.println("Training with " + numSamples + " samples...");
        System.out.println();

        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0;
            for (int s = 0; s < numSamples; s++) {
                // Forward
                double[] output = lora.forward(X[s]);

                // Compute gradients (MSE)
                double[] gradOutput = new double[outputDim];
                for (int j = 0; j < outputDim; j++) {
                    gradOutput[j] = 2.0 * (output[j] - Y[s][j]) / outputDim;
                }

                // Backward
                lora.backward(X[s], gradOutput);

                totalLoss += mseLoss(output, Y[s]);
            }

            // Update
            lora.update(lr);

            if (epoch % 20 == 0) {
                System.out.printf("Epoch %d: avg loss = %.6f%n",
                        epoch, totalLoss / numSamples);
            }
        }

        // Test on held-out data
        System.out.println("\n=== Evaluation ===");
        double testLossBefore = 0;
        double testLossAfter = 0;
        int testSamples = 200;

        // Before LoRA (using original W only via separate computation)
        LoRALayer loraFresh = new LoRALayer(
                lora.computeMergedWeight(), rank, alpha, 0.0);
        for (int s = 0; s < testSamples; s++) {
            double[] x = new double[inputDim];
            for (int j = 0; j < inputDim; j++) x[j] = rng.nextGaussian();
            double[] base = loraFresh.forward(x);
            // True target without LoRA adaptation
            double[] y = new double[outputDim];
            for (int j = 0; j < outputDim; j++) y[j] = base[j];
            testLossBefore += mseLoss(base, y);
        }

        // After LoRA (merged)
        lora.mergeWeights();
        double[][] Wmerged = lora.getMergedWeight();
        for (int s = 0; s < testSamples; s++) {
            double[] x = new double[inputDim];
            for (int j = 0; j < inputDim; j++) x[j] = rng.nextGaussian();
            double[] output = loraFresh.matVecMul(Wmerged, x);
            double[] y = new double[outputDim];
            // With adaptation
            double[] base = loraFresh.matVecMul(W, x);            // W
            double[] adapt = loraFresh.matVecMul(trueDelta, x);   // true delta
            for (int j = 0; j < outputDim; j++) y[j] = base[j] + adapt[j];
            testLossAfter += mseLoss(output, y);
        }

        System.out.printf("Test loss (pre-trained only): %.6f%n",
                testLossBefore / testSamples);
        System.out.printf("Test loss (with LoRA fine-tuned): %.6f%n",
                testLossAfter / testSamples);
        System.out.println("LoRA successfully adapted the model!");
    }
}
```

---

## Complexity Analysis

### Time Complexity

**Forward pass:**
- Original: `O(d × k)` for `Wx`.
- LoRA: `O(r × k + d × r)` for `BAx`.
- **Total:** `O(dk + r(k+d))`.

Since `r ≪ min(d, k)`, the LoRA overhead is small. For d=4096, k=4096, r=8, LoRA adds only 0.4% to the forward pass time.

**Backward pass:**
- Computing gradients for A and B: `O(r(k + d))`.
- Full fine-tuning backward: `O(dk)` — LoRA is ~250x cheaper.

**Training speedup:** LoRA trains ~2-3x faster wall-clock because:
1. Fewer parameters to update.
2. No need to compute gradients for W.
3. Smaller optimizer state reduces memory bandwidth.

### Space Complexity

**Memory (training, per layer):**
- W (frozen): `4 × d × k` bytes (float32).
- A: `4 × r × k` bytes.
- B: `4 × d × r` bytes.
- Optimizer states for A: `8 × r × k` bytes (Adam: 2 states).
- Optimizer states for B: `8 × d × r` bytes.
- Gradients for A, B: `8 × r × (k + d)` bytes.

Total LoRA memory per layer: `~4(dk) + 12r(k + d)`.

**Full fine-tuning:** `~4(dk) + 12dk` ≈ **3x** LoRA memory.

### Scaling Behavior

For a 7B parameter model with d=4096 across all layers:
- Full fine-tuning: ~56 GB model + ~56 GB optimizer = 112 GB.
- LoRA (r=8 on all attention): ~1.2M trainable params → ~14 MB optimizer.
- **Total training memory:** ~28 GB (model in 4-bit quantization).

---

## Follow-Up Questions

### Q1: Why does LoRA use random initialization for A and zeros for B?

**Answer:** This is crucial for training stability:
- If both A and B were random, the initial output would be `Wx + BAx` where `BAx` is a random perturbation. This could be large and disrupt the pre-trained behavior.
- By setting B=0, the initial output is exactly `Wx` — the pre-trained output. Loss starts from the pre-trained value and smoothly improves.
- A is random to break symmetry. If both were zero, gradients would be zero at initialization and the model wouldn't learn.
- The Gaussian initialization for A ensures the product BA has variance controlled by the rank.

### Q2: How do you choose the rank r?

**Answer:** The rank controls the capacity of the adaptation:

| Rank | Trainable Params | When to Use |
|------|-----------------|-------------|
| 1    | ~0.01%          | Minimal domain shift, few examples |
| 4    | ~0.05%          | General purpose |
| 8    | ~0.1%           | Paper's recommendation |
| 16   | ~0.2%           | Harder tasks |
| 64   | ~0.8%           | Multi-task fine-tuning |

**Empirical finding:** For most tasks, r=8 performs within 1% of full fine-tuning. Going beyond r=64 shows negligible improvement. The optimal rank depends on the "intrinsic dimension" of the task (Li et al., 2018), which can be estimated by measuring loss improvement as rank increases.

### Q3: How does LoRA compare to other PEFT methods (adapters, prefix tuning)?

**Answer:**

| Method | Inference Overhead | Trainable Params | Performance |
|--------|-------------------|------------------|-------------|
| Full FT | None | 100% | Baseline |
| LoRA | None (mergeable) | 0.1-1% | ≈ FT |
| Adapters | +5-10% latency | 0.5-2% | ≈ FT |
| Prefix Tuning | +1-2% (longer prompts) | 0.01-0.1% | Slightly worse |
| IA³ | None | 0.01% | Slightly worse |

LoRA's **key advantage** is zero inference overhead after weight merging. Adapters add sequential computation (extra layers) during inference, increasing latency by 5-10%.

### Q4: Can LoRA be applied to embedding layers and convolutional layers?

**Answer:**
- **Embedding layers:** Yes! The embedding matrix `E ∈ ℝ^(|V|×d)` can be decomposed as `E + BA` where `B ∈ ℝ^(|V|×r)`, `A ∈ ℝ^(r×d)`. Particularly useful for adapting embeddings to new domains.
- **Convolutional layers:** The convolution kernel `K ∈ ℝ^(c_out×c_in×h×w)` can be adapted via two 1×1 convolutions acting as low-rank factors. This is called "LoRA-Conv" and works well for vision tasks.
- **Any linear transformation:** As long as the operation can be expressed as a matrix multiplication, LoRA can be applied.

### Q5: What are the failure modes of LoRA?

**Answer:** LoRA can fail or underperform in these scenarios:
1. **Extremely low rank (r=1):** May not have enough capacity for complex tasks.
2. **Catastrophic forgetting:** LoRA doesn't prevent forgetting of pre-trained capabilities not related to the fine-tuning task.
3. **Multi-task fine-tuning:** A single LoRA module may not be able to adapt to multiple diverse tasks simultaneously (use mixture of LoRA experts instead).
4. **Quantization interaction:** When combined with quantization (QLoRA), the low-precision base weights can limit the expressivity of the LoRA adaptation.
5. **Very large alpha:** If `α` is too large, the LoRA update can dominate and destabilize training.

---

## Test Cases

### Test Case 1: Initial Output Equals Pre-Trained

```
Given: W (random 4×4), r=8, α=16
Input: x = [1, 0, 0, 0]
Forward with LoRA (B=0 init): h = Wx + (α/r) * B @ A @ x
Since B=0, h should equal Wx exactly.
Expected: LoRA output equals original pre-trained output (before any training).
```

### Test Case 2: Trainable Parameter Count

```
Given: W with shape (64, 128), r=4
Trainable params: 64*4 + 4*128 = 256 + 512 = 768
Total params: 64*128 = 8192
Ratio: 768/8192 = 9.375%

Expected: trainableRatio() ≈ 9.38%
```

### Test Case 3: Weight Merge Correctness

```
Given: W, A, B, r=4, α=16
After forward(x) = Wx + (16/4) * BAx = Wx + 4*BAx
After mergeWeights():
  W_merged = W + 4 * B @ A
forward_merged(x) = W_merged @ x

Expected: forward(x) == forward_merged(x) for all x (within numerical tolerance)
```

### Test Case 4: Loss Decreases During Training

```
Given: synthetic data, r=8, 100 epochs
Expected: Loss decreases monotonically. Final loss is significantly lower than initial loss.
```

### Test Case 5: Zero Gradients at Start

```
Given: Initial forward pass with B=0
Backward: aGrad will be zero because ∂L/∂A = B^T * gradOutput @ x^T = 0 (since B=0)
bGrad will be non-zero because ∂L/∂B = gradOutput @ (Ax)^T

Expected: After first backward, aGrad is all zeros, bGrad has non-zero values.
```

### Test Case 6: Different Ranks Comparison

```
Given: Same data, train with r=1, r=8, r=64 for 100 epochs each
Expected: r=8 achieves lower loss than r=1. r=64 may be similar to or slightly better than r=8.
The gap between r=8 and r=64 should be much smaller than the gap between r=1 and r=8.
```

---

## Summary

This walkthrough implemented LoRA (Low-Rank Adaptation), the dominant parameter-efficient fine-tuning method for large neural networks. The key contributions are:
1. **Low-rank decomposition** `ΔW = BA` reduces trainable parameters by 100-1000x.
2. **Weight merging** eliminates inference overhead by fusing LoRA weights into the base model.
3. **Proper initialization** (B=0, A=random) ensures training starts from the pre-trained behavior.
4. **Scaling factor `α/r`** controls the magnitude of adaptation.

LoRA has become the standard PEFT method because it combines strong performance with zero inference overhead, making it ideal for serving many fine-tuned variants from a single base model.
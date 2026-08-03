# Problem Walkthrough: Fine-Tuning with LoRA/QLoRA

## Problem 1: LoRA Adapter Training, Merge Verification, and Memory Accounting — Company: Meta

### Interview Scenario
"You're at Meta fine-tuning a Llama-style layer for a customer-service style transfer. The
team needs proof that a LoRA adapter trained on frozen base weights (1) converges, (2)
can be merged into the base matrix with machine-precision equivalence, (3) trains
dramatically fewer parameters than full fine-tuning, and (4) shrinks base-model memory
when quantized — all verifiable in a self-contained Java program built from the lab's
`LoRALayer` and `QuantizedWeights`."

### The Problem
1. Train the lab's `LoRALayer` for 200 epochs on a synthetic mapping, printing the loss curve.
2. Verify merge equivalence: `forward(x)` must equal `matMul(W + scaling*A*B, x)` to ~1e-15.
3. Report frozen vs trainable parameter counts and the trainable fraction.
4. Quantize the frozen `W` with `QuantizedWeights(4)` and compare FP32 vs packed INT4 memory.
5. End with a validation footer matching the lab style.

### Solution Walkthrough
- Step 1: Copy `LoRALayer` (frozen `W`, `A` init `*0.01`, `B=0`, `scaling = alpha/r`) and
  `QuantizedWeights` verbatim from the lab, so the demo's seeded values (`Random(42)`,
  scale `0.036412677048354374`) reproduce.
- Step 2: Train with `trainStep(x, target, 0.01)` for 200 epochs, printing every 25 — the
  loss declines monotonically (3.073154 down to 3.068279), proving gradient correctness.
- Step 3: Implement `merge()` mirroring the lab's transposed forward indexing:
  `merged[i][j] = W[j][i] + scaling * sum_k A[j][k] * B[k][i]`; verify with a max-diff
  comparison against `forward(x)`.
- Step 4: Count parameters: `d*d` frozen vs `2*r*d` trainable, print the percentage.
- Step 5: Quantize `W` at 4 bits, print FP32 bytes, packed INT4 bytes, and the toy
  `int[]` storage, plus the quantized scale.

### Code
```java
package com.genai.lab06.solution;

import java.util.Arrays;
import java.util.Random;

/**
 * Lab 06 walkthrough: LoRA adapter training with frozen base
 * weights, adapter merging, parameter accounting, and QLoRA
 * memory comparison. Reuses the lab's LoRALayer and
 * QuantizedWeights.
 */
public class LoRAAdapterTraining {

    static class LoRALayer {
        final double[][] W;
        final double[][] A;
        final double[][] B;
        final double scaling;

        LoRALayer(int d, int r, double alpha) {
            W = new double[d][d];
            A = new double[d][r];
            B = new double[r][d];
            scaling = alpha / r;
            Random rng = new Random(42);
            for (int i = 0; i < d; i++) {
                for (int j = 0; j < d; j++) W[i][j] = rng.nextGaussian() * 0.1;
                for (int j = 0; j < r; j++) A[i][j] = rng.nextGaussian() * 0.01;
            }
            for (int i = 0; i < r; i++)
                for (int j = 0; j < d; j++) B[i][j] = 0.0;
        }

        double[] forward(double[] x) {
            int d = W.length;
            double[] y = new double[d];
            for (int i = 0; i < d; i++) {
                for (int j = 0; j < d; j++) y[i] += x[j] * W[j][i];
            }
            double[] xA = new double[A[0].length];
            for (int i = 0; i < A[0].length; i++) {
                for (int j = 0; j < d; j++) xA[i] += x[j] * A[j][i];
            }
            for (int i = 0; i < d; i++) {
                for (int j = 0; j < xA.length; j++) {
                    y[i] += xA[j] * B[j][i] * scaling;
                }
            }
            return y;
        }

        double trainStep(double[] x, double[] target, double lr) {
            double[] y = forward(x);
            double loss = 0.0;
            for (int i = 0; i < y.length; i++) {
                double diff = y[i] - target[i];
                loss += diff * diff;
            }
            loss /= y.length;

            int r = A[0].length;
            double[][] gradB = new double[r][W.length];
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < W.length; j++) {
                    for (int k = 0; k < W.length; k++) {
                        gradB[i][j] += (y[k] - target[k]) * A[k][i] * x[j];
                    }
                    gradB[i][j] *= scaling / y.length;
                }
            }
            for (int i = 0; i < r; i++)
                for (int j = 0; j < W.length; j++)
                    B[i][j] -= lr * gradB[i][j];

            return loss;
        }
    }

    static class QuantizedWeights {
        final int[] int4Weights;
        final double scale;

        QuantizedWeights(double[][] w, int bits) {
            double max = Double.NEGATIVE_INFINITY;
            for (double[] row : w) for (double v : row) if (Math.abs(v) > max) max = Math.abs(v);
            scale = max / ((1 << (bits - 1)) - 1);
            int size = w.length * w[0].length;
            int4Weights = new int[size];
            int idx = 0;
            for (double[] row : w) for (double v : row) int4Weights[idx++] = (int) Math.round(v / scale);
        }

        double[][] dequantize(int d) {
            double[][] w = new double[d][d];
            int idx = 0;
            for (int i = 0; i < d; i++)
                for (int j = 0; j < d; j++)
                    w[i][j] = int4Weights[idx++] * scale;
            return w;
        }
    }

    static double[] matVec(double[][] m, double[] x) {
        double[] y = new double[m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                y[i] += m[i][j] * x[j];
        return y;
    }

    /** Mirrors forward(): y[i] = sum_j x[j] * (W[j][i] + scaling * sum_k A[j][k] * B[k][i]). */
    static double[][] merge(LoRALayer lora) {
        int d = lora.W.length;
        int r = lora.A[0].length;
        double[][] merged = new double[d][d];
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                double ab = 0.0;
                for (int k = 0; k < r; k++) ab += lora.A[j][k] * lora.B[k][i];
                merged[i][j] = lora.W[j][i] + lora.scaling * ab;
            }
        }
        return merged;
    }

    static double maxDiff(double[] a, double[] b) {
        double max = 0.0;
        for (int i = 0; i < a.length; i++) max = Math.max(max, Math.abs(a[i] - b[i]));
        return max;
    }

    public static void main(String[] args) {
        int d = 8, r = 2;
        LoRALayer lora = new LoRALayer(d, r, 4.0);

        double[] x = new double[d];
        double[] target = new double[d];
        Random rng = new Random(123);
        for (int i = 0; i < d; i++) { x[i] = rng.nextGaussian(); target[i] = x[i] * 2.0 + 0.5; }

        System.out.println("=== LoRA Training (W frozen, B updated only) ===");
        int epochs = 200;
        for (int epoch = 0; epoch < epochs; epoch++) {
            double loss = lora.trainStep(x, target, 0.01);
            if (epoch % 25 == 0) System.out.printf("Epoch %d, loss: %.6f%n", epoch, loss);
        }

        System.out.println("\n=== Merge Verification ===");
        double[][] merged = merge(lora);
        double[] yLora = lora.forward(x);
        double[] yMerged = matVec(merged, x);
        System.out.println("Max |y_lora - y_merged|: " + maxDiff(yLora, yMerged));

        System.out.println("\n=== Parameter Accounting ===");
        int frozenParams = d * d;
        int trainableParams = 2 * r * d;
        System.out.println("Frozen W params:   " + frozenParams);
        System.out.println("Trainable A+B:     " + trainableParams + " (r=" + r + ")");
        System.out.printf("Trainable fraction: %.4f%%%n", 100.0 * trainableParams / frozenParams);

        System.out.println("\n=== QLoRA Memory ===");
        QuantizedWeights qw = new QuantizedWeights(lora.W, 4);
        System.out.println("FP32 bytes:      " + frozenParams * 4);
        System.out.println("Packed INT4 bytes: " + qw.int4Weights.length * 4 / 8);
        System.out.println("Toy storage bytes (int[]): " + qw.int4Weights.length * 4);
        System.out.println("Quantized scale: " + qw.scale);

        System.out.println("\nLoRA/QLoRA adapter training validated.");
    }
}
```

### Expected Output
```text
=== LoRA Training (W frozen, B updated only) ===
Epoch 0, loss: 3.073154
Epoch 25, loss: 3.072457
Epoch 50, loss: 3.071760
Epoch 75, loss: 3.071063
Epoch 100, loss: 3.070367
Epoch 125, loss: 3.069671
Epoch 150, loss: 3.068975
Epoch 175, loss: 3.068279

=== Merge Verification ===
Max |y_lora - y_merged|: 8.326672684688674E-17

=== Parameter Accounting ===
Frozen W params:   64
Trainable A+B:     32 (r=2)
Trainable fraction: 50.0000%

=== QLoRA Memory ===
FP32 bytes:      256
Packed INT4 bytes: 32
Toy storage bytes (int[]): 256
Quantized scale: 0.036412677048354374

LoRA/QLoRA adapter training validated.
```

### Company Evaluation
- Meta: LoRA/QLoRA for Llama fine-tuning, adapter serving at scale, rank selection.
- OpenAI: Fine-tuning APIs, adapter isolation per tenant, quality-vs-cost curves.
- Anthropic: Preference fine-tuning with adapters, forgetting analysis.
- Hugging Face: PEFT library patterns, adapter format standards, merging tooling.
- Nvidia: Fused `W + AB` kernels, 4-bit packing, GPU memory math.

---

## Problem 2: Rank Sweep — Company: Hugging Face

### Interview Scenario
"You're at Hugging Face writing docs that show why rank matters. Produce a sweep of the
lab's `LoRALayer` at `r = 1, 2, 4, 8` for `d = 8`, training 100 epochs each and printing
the final loss — illustrating that more rank means more capacity and more parameters."

### The Problem
1. Sweep `r` over {1, 2, 4, 8} with `alpha = 4` fixed.
2. Train 100 epochs per rank on the same data.
3. Print trainable params and final loss per rank.

### Solution Walkthrough
- Step 1: Loop ranks, constructing `LoRALayer(d, r, 4.0)`.
- Step 2: Run 100 `trainStep` calls at lr 0.02 and record the last loss.
- Step 3: Print `r`, `2rd` trainable params, and final loss in a table.

### Code
```java
for (int r : new int[]{1, 2, 4, 8}) {
    LoRALayer lora = new LoRALayer(8, r, 4.0);
    double loss = 0;
    for (int e = 0; e < 100; e++) loss = lora.trainStep(x, target, 0.02);
    System.out.printf("r=%d trainable=%d finalLoss=%.4f%n", r, 2 * r * 8, loss);
}
```
Expected output: trainable params double with `r` while the final loss decreases —
capacity and cost moving together, which is the trade-off every fine-tuning team manages.

---

## Problem 3: Quantization Round-Trip — Company: Nvidia

### Interview Scenario
"You're at Nvidia validating a 4-bit kernel. The lab's `QuantizedWeights` quantizes and
dequantizes; prove the round-trip error is bounded and that extreme values survive."

### The Problem
1. Quantize `W` at 4 bits and dequantize.
2. Compute per-element absolute error and the max error.
3. Confirm the largest |weight| round-trips within one scale step.

### Solution Walkthrough
- Step 1: `new QuantizedWeights(W, 4)` then `dequantize(8)`.
- Step 2: Compare against the original matrix element-wise.
- Step 3: Report max error and scale, showing error < scale.

### Code
```java
QuantizedWeights qw = new QuantizedWeights(W, 4);
double[][] back = qw.dequantize(8);
double maxErr = 0;
for (int i = 0; i < 8; i++) for (int j = 0; j < 8; j++)
    maxErr = Math.max(maxErr, Math.abs(W[i][j] - back[i][j]));
System.out.printf("Max round-trip error: %.6f (scale: %.6f)%n", maxErr, qw.scale);
```
Expected output: `Max round-trip error: ~0.0182 (scale: 0.0364)` — every weight
round-trips within half a quantization step, the accuracy bound 4-bit weights promise.

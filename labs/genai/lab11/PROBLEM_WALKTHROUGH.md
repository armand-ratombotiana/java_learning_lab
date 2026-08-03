# Problem Walkthrough: Model Quantization & Deployment

## Problem 1: Quantization Accuracy Report — Company: Nvidia

### Interview Scenario
"You're at Nvidia on the TensorRT team. A partner wants to ship a layer of 16 weights
at the smallest precision. Using the lab's FP16 conversion, `SymmetricQuantization`,
`AsymmetricQuantization`, and `GraphOptimizer`, produce an accuracy-vs-memory report
and justify the precision choice."

### The Problem
1. Quantize a 16-weight FP32 layer to FP16, INT8-symmetric, and INT8-asymmetric.
2. Measure total absolute dequantization error for each.
3. Report the memory footprint of each representation.
4. Demonstrate the graph fusion savings.
5. Recommend a precision.

### Solution Walkthrough
- Step 1: Copy `toFP16`, `fromFP16`, both quantizers, and `GraphOptimizer` verbatim.
- Step 2: FP16 round-trip each weight; total error 0.0036 (near-lossless, 50% memory).
- Step 3: Symmetric: scale = 2.1/127 ≈ 0.0165; the 2.1 outlier saturates to 127 and
  coarse steps punish small weights → error 0.0717.
- Step 4: Asymmetric: scale = (2.1 - (-1.5))/255 ≈ 0.0141, zeroPoint 106, the 0.0
  weight maps exactly to 106 → error 0.0541, 24% better than symmetric.
- Step 5: Fusion: 3 kernels become 1; `max(0, w + bias)` per element.
- Step 6: Recommendation: INT8-asymmetric for this layer (75% memory, 0.0541 error).

### Code
```java
package com.genai.lab11.solution;

import java.util.*;

/**
 * Lab 11 walkthrough: quantization accuracy report. Reuses the lab's
 * FP16 conversion, symmetric and asymmetric INT8 quantizers, and graph
 * fusion, then measures dequantization error and memory footprint
 * across precisions on a realistic skewed weight distribution.
 */
public class QuantizationAccuracyReport {

    /** FP16: simulate half-precision float storage. */
    static short toFP16(float value) {
        int fbits = Float.floatToIntBits(value);
        int sign = (fbits >> 31) & 1;
        int exp = (fbits >> 23) & 0xff;
        int mant = fbits & 0x7fffff;

        if (exp == 0) return 0;
        if (exp == 0xff) return (short) ((sign << 15) | 0x7c00 | (mant >> 13));

        int newExp = exp - 127 + 15;
        if (newExp >= 31) return (short) ((sign << 15) | 0x7c00);
        if (newExp <= 0) return 0;

        return (short) ((sign << 15) | (newExp << 10) | (mant >> 13));
    }

    static float fromFP16(short hbits) {
        int sign = (hbits >> 15) & 1;
        int exp = (hbits >> 10) & 0x1f;
        int mant = hbits & 0x3ff;

        if (exp == 0) {
            return (sign == 0) ? 0.0f : -0.0f;
        }
        if (exp == 31) {
            return (mant == 0) ? (sign == 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY)
                               : Float.NaN;
        }

        int fbits = (sign << 31) | ((exp - 15 + 127) << 23) | (mant << 13);
        return Float.intBitsToFloat(fbits);
    }

    /** INT8 symmetric quantization. */
    static class SymmetricQuantization {
        final double scale;
        final int[] quantized;

        SymmetricQuantization(double[] weights) {
            double absMax = 0.0;
            for (double w : weights) if (Math.abs(w) > absMax) absMax = Math.abs(w);
            scale = absMax / 127.0;
            quantized = new int[weights.length];
            for (int i = 0; i < weights.length; i++) {
                quantized[i] = (int) Math.round(weights[i] / scale);
                quantized[i] = Math.clamp(quantized[i], -128, 127);
            }
        }

        double[] dequantize() {
            double[] result = new double[quantized.length];
            for (int i = 0; i < quantized.length; i++) result[i] = quantized[i] * scale;
            return result;
        }
    }

    /** INT8 asymmetric quantization with zero-point. */
    static class AsymmetricQuantization {
        final double scale;
        final int zeroPoint;
        final int[] quantized;

        AsymmetricQuantization(double[] weights) {
            double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
            for (double w : weights) { if (w < min) min = w; if (w > max) max = w; }
            scale = (max - min) / 255.0;
            zeroPoint = (int) Math.round(-min / scale);
            quantized = new int[weights.length];
            for (int i = 0; i < weights.length; i++) {
                quantized[i] = (int) Math.round(weights[i] / scale) + zeroPoint;
                quantized[i] = Math.clamp(quantized[i], 0, 255);
            }
        }

        double[] dequantize() {
            double[] result = new double[quantized.length];
            for (int i = 0; i < quantized.length; i++) result[i] = (quantized[i] - zeroPoint) * scale;
            return result;
        }
    }

    /** Graph optimization (TensorRT concept): fuse operations. */
    static class GraphOptimizer {
        record Node(String op, double[] weights) {}
        record FusedNode(String op, double[] weights) {}

        static FusedNode fuseConvBiasRelu(double[] convWeights, double[] bias) {
            double[] fused = Arrays.copyOf(convWeights, convWeights.length);
            for (int i = 0; i < fused.length; i++) {
                fused[i] = Math.max(0, fused[i] + bias[i % bias.length]);
            }
            return new FusedNode("Conv+Bias+ReLU", fused);
        }
    }

    public static void main(String[] args) {
        double[] layer = {0.5, -1.2, 0.8, -0.3, 2.1, -1.5, 0.0, -0.7,
                          1.1, -0.9, 0.6, -0.2, 0.9, -1.1, 0.4, 0.3};
        int n = layer.length;
        System.out.println("=== Layer Weights (" + n + " FP32 weights) ===");
        System.out.println(Arrays.toString(layer));

        System.out.println("\n=== FP16 Conversion ===");
        float fp16Err = 0;
        for (int i = 0; i < n; i++) {
            fp16Err += Math.abs(layer[i] - fromFP16(toFP16((float) layer[i])));
        }
        System.out.printf("3.14159 -> FP16(0x%04x) -> %.5f%n", toFP16(3.14159f), fromFP16(toFP16(3.14159f)));
        System.out.printf("Total abs error: %.4f%n", (double) fp16Err);

        System.out.println("\n=== INT8 Symmetric ===");
        SymmetricQuantization sq = new SymmetricQuantization(layer);
        System.out.printf("Scale: %.6f%n", sq.scale);
        System.out.println("Quantized: " + Arrays.toString(sq.quantized));

        System.out.println("\n=== INT8 Asymmetric ===");
        AsymmetricQuantization aq = new AsymmetricQuantization(layer);
        System.out.printf("Scale: %.6f, zeroPoint: %d%n", aq.scale, aq.zeroPoint);
        System.out.println("Quantized: " + Arrays.toString(aq.quantized));

        System.out.println("\n=== Dequantization Error (L1) ===");
        double[] deqS = sq.dequantize();
        double[] deqA = aq.dequantize();
        double errS = 0, errA = 0;
        for (int i = 0; i < n; i++) {
            errS += Math.abs(layer[i] - deqS[i]);
            errA += Math.abs(layer[i] - deqA[i]);
        }
        System.out.printf("FP16 total error:   %.4f%n", (double) fp16Err);
        System.out.printf("Symmetric error:    %.4f%n", errS);
        System.out.printf("Asymmetric error:   %.4f%n", errA);

        System.out.println("\n=== Memory Footprint ===");
        System.out.printf("FP32: %d bytes  FP16: %d bytes (-50%%)  INT8: %d bytes (-75%%)%n",
            n * 4, n * 2, n * 1);

        System.out.println("\n=== Graph Optimization (Fusion) ===");
        double[] convW = {0.5, -0.2, 0.3, 0.1};
        double[] bias = {0.1, -0.1};
        var fused = GraphOptimizer.fuseConvBiasRelu(convW, bias);
        System.out.println("Fused op: " + fused.op());
        System.out.println("Fused weights: " + Arrays.toString(fused.weights()));
        System.out.println("Kernels: 3 -> 1 (Conv, BiasAdd, ReLU)");

        System.out.println("\nQuantization & deployment concepts validated.");
    }
}
```

### Expected Output
```text
=== Layer Weights (16 FP32 weights) ===
[0.5, -1.2, 0.8, -0.3, 2.1, -1.5, 0.0, -0.7, 1.1, -0.9, 0.6, -0.2, 0.9, -1.1, 0.4, 0.3]

=== FP16 Conversion ===
3.14159 -> FP16(0x4248) -> 3.14063
Total abs error: 0.0036

=== INT8 Symmetric ===
Scale: 0.016535
Quantized: [30, -73, 48, -18, 127, -91, 0, -42, 67, -54, 36, -12, 54, -67, 24, 18]

=== INT8 Asymmetric ===
Scale: 0.014118, zeroPoint: 106
Quantized: [141, 21, 163, 85, 255, 0, 106, 56, 184, 42, 149, 92, 170, 28, 134, 127]

=== Dequantization Error (L1) ===
FP16 total error:   0.0036
Symmetric error:    0.0717
Asymmetric error:   0.0541

=== Memory Footprint ===
FP32: 64 bytes  FP16: 32 bytes (-50%)  INT8: 16 bytes (-75%)

=== Graph Optimization (Fusion) ===
Fused op: Conv+Bias+ReLU
Fused weights: [0.6, 0.0, 0.4, 0.0]
Kernels: 3 -> 1 (Conv, BiasAdd, ReLU)

Quantization & deployment concepts validated.
```

### Company Evaluation
- Nvidia: TensorRT fusion, INT8 calibration, kernel auto-tuning.
- Meta: PTQ/QAT for LLM compression, sensitivity-based precision selection.
- Apple: On-device FP16/INT8, memory-bandwidth-bound deployment.

---

## Problem 2: Positive-Skewed Weights — Company: Meta

### Interview Scenario
"You're at Meta quantizing a ReLU-network layer whose weights are all positive.
Show why asymmetric wins decisively here, using the lab's quantizers."

### The Problem
1. Weights {0.1..0.8} have a 0.1-to-0.8 range entirely in the positives.
2. Symmetric wastes half the 256 levels on the unused negative range.
3. Quantize both ways and compare L1 error.

### Solution Walkthrough
- Step 1: Symmetric scale = 0.8/127 — only 127 levels for a 0.7-wide range.
- Step 2: Asymmetric scale = 0.7/255 — double the resolution over the same range.
- Step 3: Sum L1: asymmetric ≈ half of symmetric.

### Code
```java
double[] pos = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8};
SymmetricQuantization sq = new SymmetricQuantization(pos);
AsymmetricQuantization aq = new AsymmetricQuantization(pos);
double eS = 0, eA = 0;
for (int i = 0; i < pos.length; i++) {
    eS += Math.abs(pos[i] - sq.dequantize()[i]);
    eA += Math.abs(pos[i] - aq.dequantize()[i]);
}
System.out.printf("Symmetric L1: %.4f, Asymmetric L1: %.4f%n", eS, eA);
```
Expected output: `Symmetric L1: 0.0126, Asymmetric L1: 0.0059` — asymmetric is
roughly half the error, exactly because the zero-point recovers the unused
negative half of the range.

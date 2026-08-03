# Problem Walkthrough: Context Window Management

## Problem 1: Long-Context Efficiency Analyzer — Company: Anthropic

### Interview Scenario
"You're at Anthropic evaluating attention mechanisms for a long-document model.
Using the lab's `slidingWindowAttention`, `applyRoPE`, `alibiAttention`, and
`ContextCompressor` on a fixed 8-token sequence, quantify complexity reduction,
show position-dependent rotation, measure ALiBi's recency bias, and report KV
cache savings."

### The Problem
1. Run sliding-window attention (W=3) and verify the output shape.
2. Count pairwise score computations: full vs windowed, plus a 1024-token projection.
3. Apply RoPE and show the rotation angle growing with position (dim 0).
4. Measure how ALiBi concentrates weight on recent tokens (last row).
5. Compress the KV stream 8 → 4 and report memory savings.

### Solution Walkthrough
- Step 1: Copy the four mechanisms verbatim from the lab; reproduce the demo's
  seed-42 Q/K/V (8×4).
- Step 2: Score count: full = 64; windowed = 1+2+3×6 = 21 (67% fewer);
  projected 1024×64: 1,048,576 vs 65,536 (93.75% fewer).
- Step 3: RoPE dim-0 angles: pos → theta → degrees: 0/57.3/114.6/171.9/229.2/286.5/
  343.8/41.1 (the last wraps past 360° — a tiny-dk artifact).
- Step 4: Recency probe: recompute the last row's softmax with and without the
  `-0.1·|i-j|` bias; recent-3-token weight rises from 0.220 to 0.302.
- Step 5: `ContextCompressor.compress(V, 0.5)`: 8×4 → 4×4 tokens, 50% KV memory.

### Code
```java
package com.genai.lab13.solution;

import java.util.*;

/**
 * Lab 13 walkthrough: context window efficiency analyzer. Reuses the
 * lab's sliding window attention, RoPE, ALiBi, and context compressor,
 * then quantifies complexity reduction, position encoding behavior,
 * and recency bias on a fixed 8-token sequence.
 */
public class ContextEfficiencyAnalyzer {

    /** Sliding window attention: each token attends to last W tokens. */
    public static double[][] slidingWindowAttention(double[][] Q, double[][] K,
                                                     double[][] V, int windowSize) {
        int seqLen = Q.length;
        int dk = Q[0].length;
        int dv = V[0].length;
        double[][] output = new double[seqLen][dv];

        for (int i = 0; i < seqLen; i++) {
            int start = Math.max(0, i - windowSize + 1);
            int end = i + 1;
            int wLen = end - start;

            double[] scores = new double[wLen];
            for (int j = start; j < end; j++) {
                double dot = 0.0;
                for (int k = 0; k < dk; k++) dot += Q[i][k] * K[j][k];
                scores[j - start] = dot / Math.sqrt(dk);
            }

            double max = Double.NEGATIVE_INFINITY;
            for (double s : scores) if (s > max) max = s;
            double sum = 0.0;
            for (int j = 0; j < wLen; j++) { scores[j] = Math.exp(scores[j] - max); sum += scores[j]; }
            for (int j = 0; j < wLen; j++) scores[j] /= sum;

            for (int j = 0; j < dv; j++) {
                for (int k = 0; k < wLen; k++) {
                    output[i][j] += scores[k] * V[start + k][j];
                }
            }
        }
        return output;
    }

    /** RoPE: apply rotary position encoding to Q and K. */
    public static void applyRoPE(double[][] Q, double[][] K) {
        int seqLen = Q.length;
        int dk = Q[0].length;
        for (int pos = 0; pos < seqLen; pos++) {
            for (int i = 0; i < dk; i += 2) {
                double theta = pos / Math.pow(10000.0, (double) i / dk);
                double cos = Math.cos(theta);
                double sin = Math.sin(theta);
                int i2 = i + 1;
                if (i2 >= dk) continue;
                double q1 = Q[pos][i], q2 = Q[pos][i2];
                double k1 = K[pos][i], k2 = K[pos][i2];
                Q[pos][i] = q1 * cos - q2 * sin;
                Q[pos][i2] = q1 * sin + q2 * cos;
                K[pos][i] = k1 * cos - k2 * sin;
                K[pos][i2] = k1 * sin + k2 * cos;
            }
        }
    }

    /** ALiBi: add linear biases to attention scores. */
    public static double[][] alibiAttention(double[][] Q, double[][] K, double[][] V) {
        int seqLen = Q.length;
        int dk = Q[0].length;
        int dv = V[0].length;
        double m = 0.1; // slope (single head, simplified)
        double[][] output = new double[seqLen][dv];

        for (int i = 0; i < seqLen; i++) {
            double[] scores = new double[seqLen];
            for (int j = 0; j < seqLen; j++) {
                double dot = 0.0;
                for (int k = 0; k < dk; k++) dot += Q[i][k] * K[j][k];
                scores[j] = dot / Math.sqrt(dk) - m * Math.abs(i - j);
            }
            double max = Double.NEGATIVE_INFINITY;
            for (double s : scores) if (s > max) max = s;
            double sum = 0.0;
            for (int j = 0; j < seqLen; j++) { scores[j] = Math.exp(scores[j] - max); sum += scores[j]; }
            for (int j = 0; j < seqLen; j++) scores[j] /= sum;
            for (int j = 0; j < dv; j++)
                for (int k = 0; k < seqLen; k++)
                    output[i][j] += scores[k] * V[k][j];
        }
        return output;
    }

    /** Context compressor: keep only top-k tokens by attention weight. */
    static class ContextCompressor {
        static double[][] compress(double[][] tokens, double keepRatio) {
            int keep = Math.max(1, (int) (tokens.length * keepRatio));
            return Arrays.copyOf(tokens, keep);
        }
    }

    static double[] rowWeights(double[][] Q, double[][] K, int row, boolean alibi, double m) {
        int seqLen = Q.length;
        int dk = Q[0].length;
        double[] scores = new double[seqLen];
        for (int j = 0; j < seqLen; j++) {
            double dot = 0.0;
            for (int k = 0; k < dk; k++) dot += Q[row][k] * K[j][k];
            scores[j] = dot / Math.sqrt(dk) - (alibi ? m * Math.abs(row - j) : 0.0);
        }
        double max = Double.NEGATIVE_INFINITY;
        for (double s : scores) if (s > max) max = s;
        double sum = 0.0;
        for (int j = 0; j < seqLen; j++) { scores[j] = Math.exp(scores[j] - max); sum += scores[j]; }
        for (int j = 0; j < seqLen; j++) scores[j] /= sum;
        return scores;
    }

    public static void main(String[] args) {
        int seqLen = 8, dk = 4, dv = 4, windowSize = 3;
        double[][] Q = new double[seqLen][dk];
        double[][] K = new double[seqLen][dk];
        double[][] V = new double[seqLen][dv];
        Random rng = new Random(42);
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < dk; j++) { Q[i][j] = rng.nextGaussian(); K[i][j] = rng.nextGaussian(); }
            for (int j = 0; j < dv; j++) V[i][j] = rng.nextGaussian() * 0.5;
        }

        System.out.println("=== Sliding Window Attention (W=3) ===");
        double[][] swOut = slidingWindowAttention(Q, K, V, windowSize);
        System.out.println("Output shape: " + swOut.length + " x " + swOut[0].length);

        System.out.println("\n=== Complexity: score computations ===");
        int full = seqLen * seqLen;
        int windowed = 0;
        for (int i = 0; i < seqLen; i++) windowed += Math.min(i + 1, windowSize);
        System.out.printf("Full attention (8 tokens):  %d pairwise scores%n", full);
        System.out.printf("Sliding W=3 (8 tokens):     %d pairwise scores (%.0f%% fewer)%n",
            windowed, 100.0 * (full - windowed) / full);
        long longFull = 1024L * 1024;
        long longW = 1024L * 64;
        System.out.printf("Projected (1024 tokens, W=64): %d vs %d (%.2f%% fewer)%n",
            longFull, longW, 100.0 * (longFull - longW) / longFull);

        System.out.println("\n=== RoPE: rotation angle by position (dim 0, theta = pos) ===");
        applyRoPE(Q, K);
        System.out.println("Rotated Q[0] (first 4 dims): " + Arrays.toString(Arrays.copyOf(Q[0], 4)));
        for (int pos = 0; pos < seqLen; pos++) {
            double theta = pos / Math.pow(10000.0, 0.0 / dk);
            System.out.printf("  pos %d: theta=%.1f rad (rotates by %.1f deg)%n", pos, theta,
                Math.toDegrees(theta) % 360);
        }

        System.out.println("\n=== ALiBi Attention ===");
        double[][] alibiOut = alibiAttention(Q, K, V);
        System.out.println("ALiBi output shape: " + alibiOut.length + " x " + alibiOut[0].length);
        double[] plain = rowWeights(Q, K, seqLen - 1, false, 0.1);
        double[] biased = rowWeights(Q, K, seqLen - 1, true, 0.1);
        double plainRecent = 0, biasedRecent = 0;
        for (int j = seqLen - 3; j < seqLen; j++) { plainRecent += plain[j]; biasedRecent += biased[j]; }
        System.out.printf("Last row (pos 7): weight on 3 most recent tokens -> plain %.3f, ALiBi %.3f%n",
            plainRecent, biasedRecent);

        System.out.println("\n=== Context Compression ===");
        double[][] compressed = ContextCompressor.compress(V, 0.5);
        System.out.println("Compressed from " + V.length + " to " + compressed.length + " tokens.");
        System.out.printf("KV cache: %d x %d -> %d x %d (%.0f%% memory saved)%n",
            V.length, dv, compressed.length, dv,
            100.0 * (V.length - compressed.length) / V.length);

        System.out.println("\nContext window management concepts validated.");
    }
}
```

### Expected Output
```text
=== Sliding Window Attention (W=3) ===
Output shape: 8 x 4

=== Complexity: score computations ===
Full attention (8 tokens):  64 pairwise scores
Sliding W=3 (8 tokens):     21 pairwise scores (67% fewer)
Projected (1024 tokens, W=64): 1048576 vs 65536 (93.75% fewer)

=== RoPE: rotation angle by position (dim 0, theta = pos) ===
Rotated Q[0] (first 4 dims): [1.1419053154730547, -0.9498666368908959, 0.2809776380727795, -0.8172214073987268]
  pos 0: theta=0.0 rad (rotates by 0.0 deg)
  pos 1: theta=1.0 rad (rotates by 57.3 deg)
  pos 2: theta=2.0 rad (rotates by 114.6 deg)
  pos 3: theta=3.0 rad (rotates by 171.9 deg)
  pos 4: theta=4.0 rad (rotates by 229.2 deg)
  pos 5: theta=5.0 rad (rotates by 286.5 deg)
  pos 6: theta=6.0 rad (rotates by 343.8 deg)
  pos 7: theta=7.0 rad (rotates by 41.1 deg)

=== ALiBi Attention ===
ALiBi output shape: 8 x 4
Last row (pos 7): weight on 3 most recent tokens -> plain 0.220, ALiBi 0.302

=== Context Compression ===
Compressed from 8 to 4 tokens.
KV cache: 8 x 4 -> 4 x 4 (50% memory saved)

Context window management concepts validated.
```

### Company Evaluation
- Anthropic: Long-context evals (needle tests), context engineering.
- OpenAI: RoPE-based models, context extension via interpolation.
- Mistral/FAIR: Sliding-window attention in production (Mistral 7B).
- Microsoft: LongNet, RingAttention, compression-based context reduction.

---

## Problem 2: Extrapolation Check with RoPE — Company: OpenAI

### Interview Scenario
"You're at OpenAI verifying that RoPE extrapolates: the dot product of rotated
vectors should depend on relative distance, not absolute position. Test it."

### The Problem
1. Copy the same Q vector to two positions and the same K vector to two other
   positions such that the relative offsets are equal (e.g., (2,5) and (3,6)).
2. After RoPE, the two dot products must be equal (rotation invariance).
3. A same-position pair (offset 0) must equal the unrotated dot product.

### Solution Walkthrough
- Step 1: Take Q[0] as vector u and K[0] as vector v; copy u into Q[2] and Q[3],
  and v into K[5] and K[6].
- Step 2: Apply RoPE to the whole arrays; rotations by theta(2)/theta(3) and
  theta(5)/theta(6).
- Step 3: Because 2D rotations preserve inner products up to the angle
  difference theta(q) - theta(p), both pairs (2,5) and (3,6) share the offset 3
  and must yield identical dots.

### Code
```java
double[][] Q = new double[8][4], K = new double[8][4];
Random rng = new Random(42);
for (int i = 0; i < 8; i++) for (int j = 0; j < 4; j++) { Q[i][j] = rng.nextGaussian(); K[i][j] = rng.nextGaussian(); }
// Copy: u at Q[2] and Q[3]; v at K[5] and K[6]  -> relative offsets 3 and 3
for (int j = 0; j < 4; j++) { Q[3][j] = Q[2][j]; K[6][j] = K[5][j]; }
applyRoPE(Q, K);
double dotA = 0, dotB = 0;
for (int j = 0; j < 4; j++) { dotA += Q[2][j] * K[5][j]; dotB += Q[3][j] * K[6][j]; }
System.out.printf("dot(Q[2],K[5]) = %.4f, dot(Q[3],K[6]) = %.4f%n", dotA, dotB);
```
Expected output: both dots are equal (rotation preserves inner products for
equal relative offsets), demonstrating that RoPE's positional signal is relative
— which is why the model generalizes to unseen absolute positions.

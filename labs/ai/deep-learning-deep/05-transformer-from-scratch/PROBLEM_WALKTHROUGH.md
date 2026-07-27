# PROBLEM WALKTHROUGH: Scaled Dot-Product Attention

## Problem Statement

**Difficulty:** Medium | **Category:** Transformers | **Estimated Time:** 60 minutes

Implement the core attention mechanism of the Transformer architecture as described in "Attention Is All You Need" (Vaswani et al., 2017). Your `ScaledDotProductAttention` class must compute attention scores using queries, keys, and values, apply an optional mask, and produce weighted outputs with proper scaling.

**Input:**
- `Q`: A 3D array of shape `(batchSize, seqLenQ, d_k)` — the queries.
- `K`: A 3D array of shape `(batchSize, seqLenK, d_k)` — the keys.
- `V`: A 3D array of shape `(batchSize, seqLenK, d_v)` — the values.
- `mask` (optional): A 2D or 3D array indicating positions to ignore (e.g., padding or causal mask). If 2D, shape `(batchSize, seqLenK)`; if 3D, shape `(batchSize, seqLenQ, seqLenK)`.

**Output:**
- `output`: A 3D array of shape `(batchSize, seqLenQ, d_v)` — the attention-weighted values.
- `attentionWeights`: A 3D array of shape `(batchSize, seqLenQ, seqLenK)` — the attention probability distribution.

**Constraints:**
- Scale the dot product by `1 / sqrt(d_k)` for numerical stability.
- Implement softmax along the key dimension (last axis).
- Support masking for both padding (setting masked positions to -infinity) and causal/autoregressive masking (preventing positions from attending to future positions).
- Handle cases where `Q` and `K` come from different sources (cross-attention) or the same source (self-attention).
- Do NOT use external linear algebra libraries.

**Evaluation Criteria:**
- Correct attention score computation: `softmax(Q * K^T / sqrt(d_k)) * V`.
- Masked positions receive near-zero attention weight.
- Output shape matches expected dimensions.
- Numerical stability: no NaN or Inf in output, even with extreme inputs.

---

## Step-by-Step Solution Walkthrough

### 1. The Scaled Dot-Product Attention Equation

The core operation is:

```
Attention(Q, K, V) = softmax(Q * K^T / sqrt(d_k)) * V
```

Where:
- `Q` (Queries): What information am I looking for?
- `K` (Keys): What information do I contain?
- `V` (Values): The actual information content.
- `d_k`: The dimension of the keys (and queries).

### 2. Why the Scaling Factor?

Without scaling, for large `d_k`, the dot products grow in magnitude:
```
E[q · k] = d_k (for unit variance q, k)
Var(q · k) = d_k
```

Large magnitudes push the softmax into regions with extremely small gradients:
```
softmax(x)_i ≈ one-hot for large ||x||
d(softmax)/dx ≈ 0 for the non-maximum entries
```

The scaling factor `1 / sqrt(d_k)` normalizes the variance:
```
Var((q · k) / sqrt(d_k)) = d_k / (sqrt(d_k))² = 1
```

This keeps the softmax in a well-behaved regime where gradients flow properly.

**Mathematical derivation:**

Let `q_i ~ N(0, 1)` and `k_i ~ N(0, 1)` be i.i.d. random variables.

```
q · k = Σ_{i=1}^{d_k} q_i * k_i
E[q · k] = Σ_i E[q_i] * E[k_i] = 0
Var(q · k) = Σ_i Var(q_i * k_i) = d_k (since E[q_i²] = E[k_i²] = 1)
```

After scaling:
```
Var((q · k) / sqrt(d_k)) = d_k / d_k = 1
```

### 3. Masking in Attention

**Padding Mask:** Used when sequences in a batch have different lengths. Mask out padding tokens so they don't contribute to attention:

```
if mask[b][j] == 0:
    score[b][i][j] = -infinity
```

**Causal (Look-Ahead) Mask:** Used in autoregressive decoding to prevent positions from attending to future positions:

```
if j > i:  // future positions
    score[b][i][j] = -infinity
```

This creates a lower-triangular attention pattern.

**Combined mask:** Both masks can be combined by creating a mask that is `-infinity` at all positions that should be masked. After adding the mask, softmax maps `-infinity` to `exp(-infinity) = 0`.

### 4. Self-Attention vs Cross-Attention

**Self-Attention:** `Q`, `K`, `V` all come from the same source:
```
Q = K = V = X (input embeddings)
```

Each position attends to all other positions in the same sequence.

**Cross-Attention:** `Q` comes from one source (e.g., decoder), `K` and `V` from another (e.g., encoder):
```
Q = decoder_states
K = V = encoder_states
```

This allows the decoder to attend to the encoder's representations.

### 5. Algorithm Pseudocode

```
function scaledDotProductAttention(Q, K, V, mask):
    // Q: (B, Q_len, d_k)
    // K: (B, K_len, d_k)
    // V: (B, K_len, d_v)

    // Step 1: Compute raw attention scores
    // scores = Q @ K^T  (batch matmul)
    // scores shape: (B, Q_len, K_len)
    scores[b][i][j] = sum_{k} Q[b][i][k] * K[b][j][k]

    // Step 2: Scale
    scores = scores / sqrt(d_k)

    // Step 3: Apply mask (if provided)
    if mask is not null:
        scores[b][i][j] += (mask[b][i][j] == 0 ? -inf : 0)

    // Step 4: Softmax along key dimension
    weights[b][i][j] = exp(scores[b][i][j]) / sum_{j'} exp(scores[b][i][j'])

    // Step 5: Weighted sum of values
    output[b][i][d] = sum_{j} weights[b][i][j] * V[b][j][d]

    return output, weights
```

### 6. Numerical Considerations

**Softmax with masking:**
- Directly computing `exp(-infinity)` gives 0, which is what we want.
- However, if the entire row is masked, softmax produces 0/0 = NaN.
- Handle this edge case by setting the weights to 0 for fully masked rows.

**Numerical stability trick:** Subtract the maximum value before exponentiating:
```
shifted = scores[b][i] - max(scores[b][i])  // max along key dim
exp_scores = exp(shifted)
weights = exp_scores / sum(exp_scores)
```

---

## Java Implementation

```java
package lab05.transformer;

import java.util.Arrays;

/**
 * Scaled dot-product attention as defined in "Attention Is All You Need"
 * (Vaswani et al., 2017).
 * <p>
 * Computes Attention(Q, K, V) = softmax(Q K^T / sqrt(d_k)) V with
 * optional masking for padding and causal (autoregressive) attention.
 */
public class ScaledDotProductAttention {

    /**
     * Computes the forward pass of scaled dot-product attention.
     *
     * @param q   queries tensor of shape (batchSize, seqLenQ, dk)
     * @param k   keys tensor of shape (batchSize, seqLenK, dk)
     * @param v   values tensor of shape (batchSize, seqLenK, dv)
     * @param mask optional mask: 2D (batchSize, seqLenK) for padding,
     *             3D (batchSize, seqLenQ, seqLenK) for causal combos.
     *             Use {@code Double.NaN} for masked positions, or pass null for no mask.
     * @return array [output, weights] where:
     *         output shape: (batchSize, seqLenQ, dv)
     *         weights shape: (batchSize, seqLenQ, seqLenK)
     */
    public double[][][][] forward(double[][] q, double[][] k, double[][] v, double[][][] mask) {
        int batchSize = q.length;
        int seqLenQ = q[0].length;
        int seqLenK = k[0].length;
        int dk = q[0][0].length;
        int dv = v[0][0].length;

        // Validate dimensions
        if (k[0][0].length != dk) {
            throw new IllegalArgumentException("K dimension " + k[0][0].length
                + " != dk " + dk);
        }

        // Step 1 & 2: Compute scores = Q @ K^T / sqrt(dk)
        double scale = Math.sqrt(dk);
        double[][] scores = new double[batchSize][seqLenQ][seqLenK];

        for (int b = 0; b < batchSize; b++) {
            for (int i = 0; i < seqLenQ; i++) {
                for (int j = 0; j < seqLenK; j++) {
                    double dotProduct = 0;
                    for (int kd = 0; kd < dk; kd++) {
                        dotProduct += q[b][i][kd] * k[b][j][kd];
                    }
                    scores[b][i][j] = dotProduct / scale;
                }
            }
        }

        // Step 3: Apply mask
        if (mask != null) {
            // Determine if mask is 2D (broadcast across Q) or 3D
            boolean is2D = mask.length == batchSize && mask[0].length == seqLenK
                && mask[0][0].length == 1;

            for (int b = 0; b < batchSize; b++) {
                for (int i = 0; i < seqLenQ; i++) {
                    for (int j = 0; j < seqLenK; j++) {
                        boolean masked;
                        if (is2D) {
                            masked = mask[b][j][0] == 0;
                        } else {
                            masked = mask[b][i][j] == 0;
                        }
                        if (masked) {
                            scores[b][i][j] = Double.NEGATIVE_INFINITY;
                        }
                    }
                }
            }
        }

        // Step 4: Softmax along key dimension
        double[][] weights = new double[batchSize][seqLenQ][seqLenK];
        for (int b = 0; b < batchSize; b++) {
            for (int i = 0; i < seqLenQ; i++) {
                // Find max for numerical stability
                double maxVal = Double.NEGATIVE_INFINITY;
                for (int j = 0; j < seqLenK; j++) {
                    if (scores[b][i][j] > maxVal) {
                        maxVal = scores[b][i][j];
                    }
                }

                // Handle edge case: all -infinity (fully masked row)
                if (maxVal == Double.NEGATIVE_INFINITY) {
                    Arrays.fill(weights[b][i], 0.0);
                    continue;
                }

                // Compute exp and sum
                double sum = 0;
                for (int j = 0; j < seqLenK; j++) {
                    double expVal = Math.exp(scores[b][i][j] - maxVal);
                    weights[b][i][j] = expVal;
                    sum += expVal;
                }

                // Normalize
                if (sum > 0) {
                    for (int j = 0; j < seqLenK; j++) {
                        weights[b][i][j] /= sum;
                    }
                }
            }
        }

        // Step 5: Weighted sum of values
        double[][] output = new double[batchSize][seqLenQ][dv];
        for (int b = 0; b < batchSize; b++) {
            for (int i = 0; i < seqLenQ; i++) {
                for (int d = 0; d < dv; d++) {
                    double sum = 0;
                    for (int j = 0; j < seqLenK; j++) {
                        sum += weights[b][i][j] * v[b][j][d];
                    }
                    output[b][i][d] = sum;
                }
            }
        }

        return new double[][][][]{output, weights};
    }

    /**
     * Convenience method: creates a causal (look-ahead) mask for autoregressive decoding.
     * Mask[b][i][j] = 0 if j > i (future positions are masked), 1 otherwise.
     *
     * @param batchSize batch size
     * @param seqLen    sequence length
     * @return 3D mask array
     */
    public static double[][] createCausalMask(int batchSize, int seqLen) {
        double[][] mask = new double[batchSize][seqLen][seqLen];
        for (int b = 0; b < batchSize; b++) {
            for (int i = 0; i < seqLen; i++) {
                for (int j = 0; j < seqLen; j++) {
                    mask[b][i][j] = (j <= i) ? 1.0 : 0.0;
                }
            }
        }
        return mask;
    }

    /**
     * Convenience method: creates a padding mask.
     * Mask[b][j] = 0 if position j is padding, 1 otherwise.
     *
     * @param batchSize batch size
     * @param seqLen    sequence length
     * @param validLengths length of each sequence before padding
     * @return 3D mask array (broadcast-ready)
     */
    public static double[][] createPaddingMask(int batchSize, int seqLen, int[] validLengths) {
        double[][] mask = new double[batchSize][seqLen][1];
        for (int b = 0; b < batchSize; b++) {
            for (int j = 0; j < seqLen; j++) {
                mask[b][j][0] = (j < validLengths[b]) ? 1.0 : 0.0;
            }
        }
        return mask;
    }
}
```

**Example Usage:**

```java
package lab05.transformer;

import java.util.Arrays;

public class AttentionExample {
    public static void main(String[] args) {
        ScaledDotProductAttention attn = new ScaledDotProductAttention();

        int batchSize = 1;
        int seqLen = 4;
        int dk = 8;
        int dv = 8;

        double[][] Q = new double[batchSize][seqLen][dk];
        double[][] K = new double[batchSize][seqLen][dk];
        double[][] V = new double[batchSize][seqLen][dv];

        // Fill with simple data
        for (int i = 0; i < seqLen; i++) {
            Arrays.fill(Q[0][i], Math.sin(i * 0.5));
            Arrays.fill(K[0][i], Math.cos(i * 0.5));
            Arrays.fill(V[0][i], i * 0.25);
        }

        // Self-attention with causal mask
        double[][] causalMask = ScaledDotProductAttention.createCausalMask(batchSize, seqLen);
        double[][][][] result = attn.forward(Q, K, V, causalMask);
        double[][] output = result[0];
        double[][] weights = result[1];

        System.out.println("Output shape: " + output[0].length + "x" + output[0][0].length);
        System.out.println("Weights:");
        for (int i = 0; i < seqLen; i++) {
            System.out.println("Pos " + i + ": " + Arrays.toString(weights[0][i]));
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

**Score computation:** `O(B * Q_len * K_len * d_k)`
**Softmax:** `O(B * Q_len * K_len)`
**Weighted sum:** `O(B * Q_len * K_len * d_v)`

**Total:** `O(B * Q_len * K_len * (d_k + d_v))`

For self-attention (`Q_len = K_len = N`):
**Total:** `O(B * N² * (d_k + d_v))`

The quadratic complexity in `N` is the primary computational bottleneck of Transformers.

### Space Complexity

**Attention matrix storage:** `O(B * Q_len * K_len)` for the scores and weights.

For self-attention with `N = 4096`:
- `4096² = 16, 777, 216` entries per head
- With `B = 1, h = 16` heads, and float16: `16 * 16M * 2 bytes = 512 MB` — just for attention scores!

### Comparison with Other Attention Mechanisms

| Variant | Time Complexity | Memory |
|---------|----------------|--------|
| Full (scaled dot-product) | `O(N² * d)` | `O(N²)` |
| Linear (Katharopoulos et al.) | `O(N * d²)` | `O(N * d)` |
| Reformer (LSH) | `O(N log N * d)` | `O(N log N)` |
| Performer (FAVOR+) | `O(N * d * log d)` | `O(N * d)` |
| Flash Attention | `O(N² * d)` | `O(N)` (no materialization) |

---

## Follow-Up Questions with Answers

### Q1: Why is the scaling factor `1/sqrt(d_k)` important? What happens without it?

**Answer:** Without scaling, dot products grow with `d_k` because each of the `d_k` terms adds variance. For large `d_k` (~512-1024 in practice), the variance of the dot product is `d_k`, so the scores can be very large.

**Consequences of no scaling:**
1. **Vanishing gradients:** The softmax function becomes extremely peaked (almost one-hot). In the one-hot regime, `softmax(x)_i ≈ 1` for the argmax and `≈ 0` for everything else. The gradient of softmax with respect to non-maximal positions approaches 0, killing learning.
2. **Saturated attention:** The model can't learn to distribute attention smoothly — it always attends to just one position.
3. **Unstable training:** Large score values can cause numerical overflow in `exp()` for moderate `d_k` values.

The scaling factor normalizes the variance to approximately 1, keeping the scores in a range where softmax produces smooth, learnable distributions.

### Q2: Explain the difference between padding mask and causal mask. How are they combined?

**Answer:**

**Padding mask:** Prevents attention to padding tokens (tokens added to make sequences the same length in a batch). Applied to `K/V` positions regardless of `Q` position.

Shape: `(batchSize, K_len)` — broadcast across Q positions.

**Causal (look-ahead) mask:** Prevents position `i` from attending to position `j > i` in autoregressive decoding. Ensures that the model can only use past information to predict the next token.

Shape: `(Q_len, K_len)` — lower triangular matrix.

**Combining them:** Add the two masks element-wise:
```
combined[b][i][j] = 0 if (padding[j] or j > i) else 1
```

Implementation: Create a 3D mask of shape `(batchSize, Q_len, K_len)`:
```java
for i in Q_len:
    for j in K_len:
        combined[b][i][j] = (isValid[b][j] && j <= i) ? 1 : 0
```

### Q3: How does multi-head attention build on scaled dot-product attention?

**Answer:** Multi-head attention runs multiple scaled dot-product attention operations in parallel (each "head") and concatenates the results:

```
MultiHead(Q, K, V) = Concat(head_1, ..., head_h) * W_O
where  head_i = Attention(Q * W_i^Q, K * W_i^K, V * W_i^V)
```

Key aspects:
- Each head has its own projection matrices `W_i^Q`, `W_i^K`, `W_i^V`.
- The per-head dimension is `d_k = d_model / h`.
- Each head can learn to attend to different types of relationships (e.g., syntax vs semantics, local vs global).
- The computational cost is the same as single-head attention with `d_model` because the smaller per-head dimension compensates for having `h` heads.

### Q4: What is Flash Attention and how does it improve the standard attention implementation?

**Answer:** Flash Attention (Dao et al., 2022) is an exact attention algorithm that is significantly faster and more memory-efficient than the standard implementation.

**Standard implementation:**
1. Compute `S = Q * K^T` (N² memory for scores)
2. Apply softmax to get `P`
3. Compute `O = P * V`

The attention matrix (N²) must be materialized in high-bandwidth memory (HBM).

**Flash Attention innovations:**
1. **Tiling:** Process Q, K, V in blocks that fit in fast SRAM (on-chip).
2. **Online softmax:** Compute softmax without materializing the full matrix by tracking cumulative statistics per block.
3. **Recomputation:** During backward pass, recompute attention on the fly rather than storing it.

**Results:**
- 2-4x speedup over standard PyTorch attention.
- Linear memory in sequence length (no N² memory bottleneck).
- Enables training with much longer sequences (e.g., 128K tokens).

### Q5: What issues arise with quadratic attention complexity for long sequences, and how do sparse attention patterns address them?

**Answer:** The `O(N²)` complexity of standard attention becomes prohibitive for long sequences (10K+ tokens):

**Issues:**
- **Memory:** The N² attention matrix can exceed GPU memory.
- **Time:** Quadratic scaling means 2x longer sequences = 4x more computation.
- **Over-attention:** Long sequences tend to produce diffuse attention, where each position attends to many irrelevant positions.

**Sparse attention approaches:**
1. **Sliding window:** Each position attends to only a local window of `w` neighbors. Complexity: `O(N * w)`.
2. **Global tokens:** A small set of "global" tokens (e.g., `[CLS]`) attend to all positions and vice versa.
3. **Strided patterns:** Combine local windows with strided skipping for long-range connections.
4. **LSH (Reformer):** Use locality-sensitive hashing to group similar queries and keys, attending only within groups.
5. **Sinkhorn attention:** Learn a sorting permutation to group related tokens.

**BigBird (2020)** uses the combination of sliding window + global + random attention, achieving `O(N)` complexity while maintaining full expressivity.

---

## Test Cases

### Test Case 1: Basic Self-Attention

```java
void testBasicSelfAttention() {
    ScaledDotProductAttention attn = new ScaledDotProductAttention();
    int B = 1, N = 3, dk = 4, dv = 4;

    double[][] Q = new double[B][N][dk];
    double[][] K = new double[B][N][dk];
    double[][] V = new double[B][N][dv];

    // Simple: all ones
    for (int i = 0; i < N; i++) {
        Arrays.fill(Q[0][i], 1.0);
        Arrays.fill(K[0][i], 1.0);
        Arrays.fill(V[0][i], i + 1.0);
    }

    double[][][][] result = attn.forward(Q, K, V, null);
    double[][] output = result[0];
    double[][] weights = result[1];

    // With all-ones Q and K, each Q attends equally to all K positions
    // Expected weight: 1/3 for each position
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            assert Math.abs(weights[0][i][j] - 1.0 / N) < 1e-6 :
                "Expected uniform weight, got " + weights[0][i][j];
        }
    }

    // Expected output: mean of [1, 2, 3] = 2
    for (int d = 0; d < dv; d++) {
        assert Math.abs(output[0][0][d] - 2.0) < 1e-6 :
            "Expected 2.0, got " + output[0][0][d];
    }
}
```

### Test Case 2: Causal Mask

```java
void testCausalMask() {
    ScaledDotProductAttention attn = new ScaledDotProductAttention();
    int B = 1, N = 4, dk = 4, dv = 4;

    double[][] Q = new double[B][N][dk];
    double[][] K = new double[B][N][dk];
    double[][] V = new double[B][N][dv];

    for (int i = 0; i < N; i++) {
        Arrays.fill(Q[0][i], 1.0);
        Arrays.fill(K[0][i], 1.0);
        Arrays.fill(V[0][i], i + 1.0);
    }

    double[][] causalMask = ScaledDotProductAttention.createCausalMask(B, N);
    double[][][][] result = attn.forward(Q, K, V, causalMask);
    double[][] weights = result[0];

    // Position 0 should attend only to position 0
    assert Math.abs(weights[0][0][0] - 1.0) < 1e-6;
    assert weights[0][0][1] < 1e-6;
    assert weights[0][0][2] < 1e-6;
    assert weights[0][0][3] < 1e-6;

    // Position 1 attends to positions 0, 1 with equal weight (1/2 each)
    assert Math.abs(weights[0][1][0] - 0.5) < 1e-6;
    assert Math.abs(weights[0][1][1] - 0.5) < 1e-6;

    // Position 3 attends to all 4 positions equally (1/4 each)
    for (int j = 0; j < N; j++) {
        assert Math.abs(weights[0][3][j] - 0.25) < 1e-6;
    }
}
```

### Test Case 3: Padding Mask

```java
void testPaddingMask() {
    ScaledDotProductAttention attn = new ScaledDotProductAttention();
    int B = 1, N = 5, dk = 4, dv = 4;

    double[][] Q = new double[B][N][dk];
    double[][] K = new double[B][N][dk];
    double[][] V = new double[B][N][dv];

    for (int i = 0; i < N; i++) {
        Arrays.fill(Q[0][i], 1.0);
        Arrays.fill(K[0][i], 1.0);
        Arrays.fill(V[0][i], i + 1.0);
    }

    // Mask: first 3 positions are valid, last 2 are padding
    double[][] paddingMask = ScaledDotProductAttention.createPaddingMask(B, N, new int[]{3});

    double[][][][] result = attn.forward(Q, K, V, paddingMask);
    double[][] weights = result[0];

    // Last 2 positions should have 0 weight
    for (int i = 0; i < N; i++) {
        assert weights[0][i][3] < 1e-6 : "Padding position 3 should be masked";
        assert weights[0][i][4] < 1e-6 : "Padding position 4 should be masked";
    }

    // Weights for valid positions should be uniform (1/3)
    for (int i = 0; i < N; i++) {
        double sum = 0;
        for (int j = 0; j < 3; j++) {
            assert Math.abs(weights[0][i][j] - 1.0 / 3) < 1e-6 :
                "Expected uniform weight among valid positions";
            sum += weights[0][i][j];
        }
        assert Math.abs(sum - 1.0) < 1e-6 : "Valid position weights should sum to 1";
    }
}
```

### Test Case 4: Cross-Attention (Different Q, K, V Sources)

```java
void testCrossAttention() {
    ScaledDotProductAttention attn = new ScaledDotProductAttention();
    int B = 1, Qlen = 2, Klen = 3, dk = 4, dv = 4;

    double[][] Q = new double[B][Qlen][dk];
    double[][] K = new double[B][Klen][dk];
    double[][] V = new double[B][Klen][dv];

    // Q attends only to position 1 in K
    Arrays.fill(Q[0][0], 1.0);
    Arrays.fill(Q[0][1], 1.0);
    // K: position 1 is distinct
    Arrays.fill(K[0][0], 0.5);
    Arrays.fill(K[0][1], 2.0); // Larger inner product with Q
    Arrays.fill(K[0][2], 0.5);
    Arrays.fill(V[0][0], 10.0);
    Arrays.fill(V[0][1], 20.0);
    Arrays.fill(V[0][2], 30.0);

    double[][][][] result = attn.forward(Q, K, V, null);
    double[][] output = result[0];
    double[][] weights = result[1];

    // Position 1 should have highest attention weight
    // (K=2.0 has highest dot product with Q=1.0)
    double maxWeight = Math.max(weights[0][0][0], Math.max(weights[0][0][1], weights[0][0][2]));
    assert Math.abs(weights[0][0][1] - maxWeight) < 1e-6 :
        "Position with highest K should get most attention";
}
```

### Test Case 5: Numerical Stability with Extreme Values

```java
void testNumericalStability() {
    ScaledDotProductAttention attn = new ScaledDotProductAttention();
    int B = 1, N = 4, dk = 512, dv = 512;

    double[][] Q = new double[B][N][dk];
    double[][] K = new double[B][N][dk];
    double[][] V = new double[B][N][dv];

    // Fill with large random values
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < dk; j++) {
            Q[0][i][j] = (Math.random() - 0.5) * 100;
            K[0][i][j] = (Math.random() - 0.5) * 100;
        }
        Arrays.fill(V[0][i], 1.0);
    }

    double[][][][] result = attn.forward(Q, K, V, null);
    double[][] output = result[0];
    double[][] weights = result[1];

    // Check no NaN or Inf
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            assert !Double.isNaN(weights[0][i][j]) : "NaN in weights";
            assert !Double.isInfinite(weights[0][i][j]) : "Inf in weights";
        }
        for (int d = 0; d < dv; d++) {
            assert !Double.isNaN(output[0][i][d]) : "NaN in output";
            assert !Double.isInfinite(output[0][i][d]) : "Inf in output";
        }
    }
}
```

### Test Case 6: Output Shape Verification

```java
void testOutputShapes() {
    ScaledDotProductAttention attn = new ScaledDotProductAttention();

    int B = 3, Qlen = 5, Klen = 7, dk = 16, dv = 24;

    double[][] Q = new double[B][Qlen][dk];
    double[][] K = new double[B][Klen][dk];
    double[][] V = new double[B][Klen][dv];

    double[][][][] result = attn.forward(Q, K, V, null);
    double[][] output = result[0];
    double[][] weights = result[1];

    assert output.length == B : "Output batch dim";
    assert output[0].length == Qlen : "Output Q length dim";
    assert output[0][0].length == dv : "Output dv dim";
    assert weights.length == B : "Weights batch dim";
    assert weights[0].length == Qlen : "Weights Q length dim";
    assert weights[0][0].length == Klen : "Weights K length dim";
}
```

### Test Case 7: Weights Sum to 1

```java
void testWeightsSumToOne() {
    ScaledDotProductAttention attn = new ScaledDotProductAttention();
    int B = 2, Qlen = 4, Klen = 6, dk = 8, dv = 8;

    double[][] Q = new double[B][Qlen][dk];
    double[][] K = new double[B][Klen][dk];
    double[][] V = new double[B][Klen][dv];

    for (int b = 0; b < B; b++) {
        for (int i = 0; i < Qlen; i++) {
            for (int kd = 0; kd < dk; kd++) {
                Q[b][i][kd] = Math.random();
            }
        }
        for (int j = 0; j < Klen; j++) {
            for (int kd = 0; kd < dk; kd++) {
                K[b][j][kd] = Math.random();
            }
        }
    }

    double[][][][] result = attn.forward(Q, K, V, null);
    double[][] weights = result[1];

    for (int b = 0; b < B; b++) {
        for (int i = 0; i < Qlen; i++) {
            double sum = 0;
            for (int j = 0; j < Klen; j++) {
                sum += weights[b][i][j];
            }
            assert Math.abs(sum - 1.0) < 1e-6 :
                "Weights[" + b + "][" + i + "] sum to " + sum;
        }
    }
}
```

# PROBLEM WALKTHROUGH: Multi-Head Attention

## Problem Statement

**Difficulty:** Hard | **Category:** Attention Variants | **Estimated Time:** 75 minutes

Extend the single scaled dot-product attention mechanism to multi-head attention with concatenation and output projection, as described in "Attention Is All You Need". Your `MultiHeadAttention` class must split the queries, keys, and values into multiple heads, compute attention in parallel per head, concatenate the results, and project to the output dimension.

**Input:**
- `Q`: A 3D array of shape `(batchSize, seqLenQ, d_model)` — the queries.
- `K`: A 3D array of shape `(batchSize, seqLenK, d_model)` — the keys.
- `V`: A 3D array of shape `(batchSize, seqLenK, d_model)` — the values.
- `mask` (optional): Same semantics as in ScaledDotProductAttention.

**Parameters:**
- `numHeads`: Number of attention heads `h`.
- `d_model`: Model dimension (input and output dimension).
- `d_k`: Per-head key/query dimension = `d_model / h`.
- `d_v`: Per-head value dimension = `d_model / h` (typically `d_k = d_v`).

**Learnable Weights:**
- `wQ`, `wK`, `wV`: Projection matrices of shape `(d_model, d_model)` — one each for Q, K, V.
- `wO`: Output projection matrix of shape `(d_model, d_model)`.

**Output:**
- `output`: A 3D array of shape `(batchSize, seqLenQ, d_model)`.
- Optionally return per-head attention weights for visualization.

**Constraints:**
- Implement the complete multi-head attention pipeline: project → split → scaled dot-prod → concat → project.
- Use the `ScaledDotProductAttention` class from the previous lab (or re-implement inline).
- Ensure `d_model` is divisible by `numHeads`.
- Support both self-attention (Q = K = V) and cross-attention (Q ≠ K, V).

**Evaluation Criteria:**
- Correct output shape: `(batchSize, seqLenQ, d_model)`.
- The output should be a linear projection of concatenated per-head attention outputs.
- All heads are computed in parallel (not sequential loops over heads — use vectorized dimensions).
- The total parameter count matches: `4 * d_model²` (for Q, K, V, O projections).

---

## Step-by-Step Solution Walkthrough

### 1. The Multi-Head Attention Equation

```
MultiHead(Q, K, V) = Concat(head_1, ..., head_h) * W^O

where head_i = Attention(Q * W_i^Q, K * W_i^K, V * W_i^V)
and   W_i^Q ∈ R^{d_model × d_k}, W_i^K ∈ R^{d_model × d_k}, W_i^V ∈ R^{d_model × d_v}
       W^O ∈ R^{h * d_v × d_model}
```

In practice, we implement this more efficiently:

```
// Single projection for all heads:
Q_proj = Q * W^Q  // shape: (B, Q_len, d_model)
K_proj = K * W^K  // shape: (B, K_len, d_model)
V_proj = V * W^V  // shape: (B, K_len, d_model)

// Reshape to separate heads:
// (B, Q_len, d_model) → (B, Q_len, h, d_k) → (B * h, Q_len, d_k)
// (B, K_len, d_model) → (B, K_len, h, d_k) → (B * h, K_len, d_k)

// Compute attention:
// output = attention(Q_reshaped, K_reshaped, V_reshaped)
// shape: (B * h, Q_len, d_v)

// Reshape back:
// (B * h, Q_len, d_v) → (B, Q_len, h * d_v) → (B, Q_len, d_model)

// Output projection:
// final = output * W^O  // (B, Q_len, d_model)
```

### 2. Why Multiple Heads?

Single-head attention produces one distribution over positions. Multiple heads allow the model to:

1. **Attend to different positions simultaneously:** One head might focus on local syntax, another on long-range semantics.
2. **Learn different representation subspaces:** Each head's projection matrices project into different subspaces.
3. **Model different relationship types:** Subject-verb, verb-object, pronoun-coreference, etc.

**Analogy:** Multi-head attention is like having multiple "interpreters" reading the same sentence — one focuses on grammar, one on sentiment, one on entities, etc., and then their analyses are combined.

### 3. Head Splitting Mechanics

The standard approach concatenates all heads after attention:

```
// From: (B, Q_len, d_model)
// First, project to d_model (all heads combined)
Q_proj = matmul(Q, W^Q)  // (B, Q_len, d_model)

// Then, split d_model into h heads of d_k each
// View as: (B, Q_len, h, d_k)
// Transpose to: (B, h, Q_len, d_k)
// Reshape to: (B * h, Q_len, d_k)  // treat each head as an independent batch item
```

The batch multiplication approach: Instead of looping over heads, we can perform batched matrix multiplication by reshaping to merge the batch and head dimensions.

### 4. Comparison with Single-Head Attention

| Property | Single-Head | Multi-Head (h=8) |
|----------|-------------|-------------------|
| Parameters (Q proj) | `d_model²` | `d_model²` (same — just different shape) |
| Per-head dimension | `d_model` | `d_model / h` |
| Attention distributions | 1 | h |
| Output dimension | `d_model` | `d_model` |
| FLOPs | `O(N² * d_model)` | `O(N² * d_model)` (same total FLOPs) |

The total FLOPs are the same because we replaced one `d_model`-dimensional attention with `h` attention operations of dimension `d_model/h`. The factor `h * (d_model/h)² = d_model²/h` per head, times `h` heads, gives `d_model²` total.

### 5. Individual Head Behavior

Different heads learn different attention patterns. In trained Transformer models:
- **Lower layers:** More local attention (adjacent tokens).
- **Higher layers:** More global attention patterns (syntactic relations, long-range dependencies).
- **Some heads specialize:** E.g., attending to the next word in a sentence, attending to verbs from their subjects, etc.

Visualizing attention patterns reveals these specializations and is a key tool for interpretability.

---

## Java Implementation

```java
package lab06.transformer;

import java.util.Arrays;

/**
 * Multi-head attention as defined in "Attention Is All You Need" (Vaswani et al., 2017).
 * <p>
 * Splits queries, keys, and values into multiple heads, computes scaled dot-product
 * attention per head, concatenates, and projects to the output dimension.
 * Supports both self-attention and cross-attention.
 */
public class MultiHeadAttention {

    private final int dModel;
    private final int numHeads;
    private final int dk;
    private final int dv;

    // Learnable projection matrices
    private double[][] wQ; // (dModel, dModel)
    private double[][] wK; // (dModel, dModel)
    private double[][] wV; // (dModel, dModel)
    private double[][] wO; // (dModel, dModel)

    // Reusable attention module (or could be inlined)
    private final ScaledDotProductAttention attention;

    // Debug: store per-head attention weights
    private double[][][][] lastHeadWeights;

    /**
     * Constructs multi-head attention with given dimensions.
     *
     * @param dModel   model dimension (input and output)
     * @param numHeads number of attention heads (dModel must be divisible by numHeads)
     * @throws IllegalArgumentException if dModel is not divisible by numHeads
     */
    public MultiHeadAttention(int dModel, int numHeads) {
        if (dModel % numHeads != 0) {
            throw new IllegalArgumentException(
                "dModel (" + dModel + ") must be divisible by numHeads (" + numHeads + ")");
        }
        this.dModel = dModel;
        this.numHeads = numHeads;
        this.dk = dModel / numHeads;
        this.dv = dModel / numHeads;
        this.attention = new ScaledDotProductAttention();

        double std = Math.sqrt(2.0 / (dModel + dModel));
        this.wQ = glorotInit(dModel, dModel, std);
        this.wK = glorotInit(dModel, dModel, std);
        this.wV = glorotInit(dModel, dModel, std);
        this.wO = glorotInit(dModel, dModel, std);
    }

    /**
     * Performs multi-head attention forward pass.
     *
     * @param q    queries of shape (batchSize, seqLenQ, dModel)
     * @param k    keys of shape (batchSize, seqLenK, dModel)
     * @param v    values of shape (batchSize, seqLenK, dModel)
     * @param mask optional mask (see ScaledDotProductAttention)
     * @return output of shape (batchSize, seqLenQ, dModel)
     */
    public double[][] forward(double[][] q, double[][] k, double[][] v, double[][][] mask) {
        int batchSize = q.length;
        int seqLenQ = q[0].length;
        int seqLenK = k[0].length;

        // Step 1: Project Q, K, V to dModel
        double[][] qProj = matmul3D(q, wQ);
        double[][] kProj = matmul3D(k, wK);
        double[][] vProj = matmul3D(v, wV);

        // Step 2: Reshape to separate heads
        // Shape: (B * numHeads, seqLen, dk)
        double[][] qHeaded = reshapeForHeads(qProj, batchSize, seqLenQ);
        double[][] kHeaded = reshapeForHeads(kProj, batchSize, seqLenK);
        double[][] vHeaded = reshapeForHeads(vProj, batchSize, seqLenK);

        // Step 3: Expand mask for heads (each head in the batch uses same mask)
        double[][][] expandedMask = null;
        if (mask != null) {
            // mask shape: (B, seqLenQ, seqLenK)
            // expanded: (B * numHeads, seqLenQ, seqLenK)
            expandedMask = new double[batchSize * numHeads][seqLenQ][seqLenK];
            for (int b = 0; b < batchSize; b++) {
                for (int h = 0; h < numHeads; h++) {
                    // Shallow copy row pointers
                    expandedMask[b * numHeads + h] = mask[b];
                }
            }
        }

        // Step 4: Compute attention for all heads in parallel
        double[][][][] attnResult = attention.forward(qHeaded, kHeaded, vHeaded, expandedMask);
        double[][] attnOutput = attnResult[0]; // (B * numHeads, seqLenQ, dv)
        double[][] headWeights = attnResult[1]; // (B * numHeads, seqLenQ, seqLenK)

        // Store per-head weights for visualization
        this.lastHeadWeights = new double[batchSize][numHeads][seqLenQ][seqLenK];
        for (int b = 0; b < batchSize; b++) {
            for (int h = 0; h < numHeads; h++) {
                lastHeadWeights[b][h] = headWeights[b * numHeads + h];
            }
        }

        // Step 5: Concatenate heads
        double[][] concat = reshapeFromHeads(attnOutput, batchSize, seqLenQ);

        // Step 6: Output projection
        double[][] output = matmul3D(concat, wO);

        return output;
    }

    /**
     * Reshapes (B, seqLen, dModel) to (B * numHeads, seqLen, dk).
     */
    private double[][] reshapeForHeads(double[][] input, int batchSize, int seqLen) {
        int totalBatch = batchSize * numHeads;
        double[][] output = new double[totalBatch][seqLen][dk];
        for (int b = 0; b < batchSize; b++) {
            for (int h = 0; h < numHeads; h++) {
                int outIdx = b * numHeads + h;
                for (int s = 0; s < seqLen; s++) {
                    for (int d = 0; d < dk; d++) {
                        output[outIdx][s][d] = input[b][s][h * dk + d];
                    }
                }
            }
        }
        return output;
    }

    /**
     * Reshapes from (B * numHeads, seqLen, dv) back to (B, seqLen, dModel).
     */
    private double[][] reshapeFromHeads(double[][] input, int batchSize, int seqLen) {
        double[][] output = new double[batchSize][seqLen][dModel];
        for (int b = 0; b < batchSize; b++) {
            for (int h = 0; h < numHeads; h++) {
                int inIdx = b * numHeads + h;
                for (int s = 0; s < seqLen; s++) {
                    for (int d = 0; d < dv; d++) {
                        output[b][s][h * dv + d] = input[inIdx][s][d];
                    }
                }
            }
        }
        return output;
    }

    /**
     * Batched matrix multiplication: (B, seqLen, dIn) @ (dIn, dOut) → (B, seqLen, dOut).
     */
    private double[][] matmul3D(double[][] input, double[][] weight) {
        int batchSize = input.length;
        int seqLen = input[0].length;
        int dIn = input[0][0].length;
        int dOut = weight[0].length;

        double[][] output = new double[batchSize][seqLen][dOut];
        for (int b = 0; b < batchSize; b++) {
            for (int s = 0; s < seqLen; s++) {
                for (int d = 0; d < dOut; d++) {
                    double sum = 0;
                    for (int i = 0; i < dIn; i++) {
                        sum += input[b][s][i] * weight[i][d];
                    }
                    output[b][s][d] = sum;
                }
            }
        }
        return output;
    }

    // ---- Initialization Helpers ----

    private double[][] glorotInit(int rows, int cols, double std) {
        double[][] m = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m[i][j] = randn() * std;
            }
        }
        return m;
    }

    private double randn() {
        double u1 = Math.random();
        double u2 = Math.random();
        return Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
    }

    // ---- Getters ----

    public double[][][][] getLastHeadWeights() {
        return lastHeadWeights;
    }

    public int getNumHeads() { return numHeads; }
    public int getDModel() { return dModel; }
}
```

**Example Usage:**

```java
package lab06.transformer;

import java.util.Arrays;

public class MultiHeadExample {
    public static void main(String[] args) {
        int dModel = 512;
        int numHeads = 8;
        int batchSize = 2;
        int seqLen = 10;

        MultiHeadAttention mha = new MultiHeadAttention(dModel, numHeads);

        double[][] Q = new double[batchSize][seqLen][dModel];
        double[][] K = new double[batchSize][seqLen][dModel];
        double[][] V = new double[batchSize][seqLen][dModel];

        // Fill with data
        for (int b = 0; b < batchSize; b++) {
            for (int i = 0; i < seqLen; i++) {
                for (int d = 0; d < dModel; d++) {
                    Q[b][i][d] = Math.sin((i + b * seqLen) * 0.01 * d);
                    K[b][i][d] = Math.cos((i + b * seqLen) * 0.01 * d);
                    V[b][i][d] = (i + b * seqLen) / (double)(batchSize * seqLen);
                }
            }
        }

        double[][] output = mha.forward(Q, K, V, null);
        System.out.println("Output shape: " + output[0].length + "x" + output[0][0].length);
        System.out.println("Output[0][0] sum: " + Arrays.stream(output[0][0]).sum());
    }
}
```

---

## Complexity Analysis

### Time Complexity

**Projections (Q, K, V):**
- Each: `O(B * seqLen * d_model²)` — three separate projections.
- Total: `O(3 * B * N * d_model²)`.

**Attention per head:**
- `h` heads, each with `d_k = d_model / h`.
- Per head: `O(B * N² * d_k)`.
- Total: `O(B * N² * d_model)`.

**Output projection:**
- `O(B * N * d_model²)`.

**Overall:** `O(B * N * d_model² + B * N² * d_model)`

For typical Transformers (`d_model ≈ N`):
- Complexity: `O(B * N * d_model²)` (dominated by projections).
- For very long sequences (`N >> d_model`): `O(B * N² * d_model)` (dominated by attention).

### Space Complexity

**Parameters:** `4 * d_model²` (Q, K, V, O projections).

For `d_model = 512`: `4 * 262144 ≈ 1M` parameters.
For `d_model = 1024`: `4 * 1, 048, 576 ≈ 4.2M` parameters.

**Forward memory:** `O(B * N * d_model + B * N² * h)` for the attention scores.

### Comparison of Attention Variants

| Variant | Parameters (excl. projections) | Score Function | Complexity |
|---------|------|------|------------|
| Multi-Head (ours) | `4*d_model²` | `Q*K^T/sqrt(dk)` | `O(N²*d_model)` |
| Multi-Query (Shazeer, 2019) | `d_model²+2*d_model*d_k` | Same | Same (but less memory for K/V) |
| Grouped-Query (GQA) | In between MHA and MQA | Same | Same |
| Linear Attention | Depends | `φ(Q)*φ(K)^T` | `O(N*d²)` |

---

## Follow-Up Questions with Answers

### Q1: What is the difference between multi-head, multi-query, and grouped-query attention?

**Answer:**

**Multi-Head Attention (MHA)** — used in original Transformer:
- Each head has its own Q, K, V projections.
- Parameters: `(3 * h + 1) * d_model²/h * d_model = 4 * d_model²`

**Multi-Query Attention (MQA)** — used in PaLM, Llama 1:
- All heads share the same K and V projections (one set of keys and values).
- Each head has its own Q projection.
- Parameters: `(h + 2) * d_model²/h * d_model = d_model² + 2 * d_model²/h`
- ~30-50% fewer KV cache entries → significantly less memory.

**Grouped-Query Attention (GQA)** — used in Llama 2, Mistral:
- Intermediate: K and V are shared among groups of heads.
- If `g` groups: each group has its own K/V.
- Parameters: between MHA and MQA.
- GQA-8 (8 groups) with 32 heads: each group has 4 query heads.

Trade-off: MQA uses less memory (less KV cache), but MHA has higher quality. GQA is a practical compromise.

### Q2: Why is `d_k = d_model / h` typically chosen? What if `d_k` is not a divisor of `d_model`?

**Answer:** Setting `d_k = d_model / h` is chosen so that:

1. **Total computation stays constant:** Each head operates in `d_model / h` dimensions, and there are `h` heads, so the total FLOPs are the same as single-head attention with `d_model`.
2. **Concatenation fits perfectly:** `h * d_k = d_model`, so concatenating heads produces exactly `d_model` dimensions.

**If `d_model` is not divisible by `h`:** Two common approaches:
1. **Floor + pad:** Set `d_k = floor(d_model / h)`, and the last head has additional dimensions to make up the difference.
2. **Uneven heads:** Allow heads to have slightly different dimensions.

Most modern implementations require `d_model % h == 0` for simplicity.

### Q3: How do you parallelize multi-head attention across multiple GPUs?

**Answer:** Multi-head attention is naturally parallelizable:

**Tensor Parallelism (Megatron-LM approach):**
1. Split `W^Q`, `W^K`, `W^V` column-wise across GPUs: each GPU holds `d_model × (d_model / p)`.
2. Each GPU computes attention for `h/p` heads independently.
3. All-gather to collect heads across GPUs.
4. Split `W^O` row-wise: each GPU computes part of the output.

**Sequence Parallelism:**
- Split the sequence dimension across GPUs.
- Each GPU processes a chunk of the sequence.
- Requires communication for attention (each position needs all keys/values).

**Data Parallelism:**
- Each GPU processes different micro-batches.
- All-reduce gradients during backprop.
- Simplest but doesn't reduce per-GPU memory for large models.

### Q4: What are some visualization techniques for understanding multi-head attention patterns?

**Answer:**

1. **Attention matrices:** Plot per-head attention weights as a heatmap. Rows = query positions, columns = key positions. Reveals:
   - Diagonal patterns (local attention).
   - Vertical strips (attending to special tokens like `[SEP]`).
   - Block patterns (syntactic phrases).

2. **Head importance:** Mask out individual heads and measure performance drop. Some heads are critical; others can be pruned.

3. **Attention rollout (Abnar & Zuidema, 2020):** Compute the effective attention flow from input to output by multiplying attention weights across layers.

4. **Logit lens:** Project intermediate representations through the output embedding to see what tokens the model "expects" at each position.

5. **Attention entropy:** Measure the entropy of each head's attention distribution. Low entropy = focused attention, high entropy = diffuse.

### Q5: Could we use multi-head attention with different per-head dimensions or different attention mechanisms per head?

**Answer:**

**Different per-head dimensions:** Possible but uncommon. Some work explores:
- Variable heads in deeper layers (fewer, larger heads).
- Mixture-of-Expert attention: heads can have different capacities.

**Different mechanisms per head:** This is an active research area:
- **Talker-heads:** Some heads attend to all positions; others use local attention.
- **Mixture of attentions:** Some heads use softmax attention, others use linear attention.
- **Adaptive heads:** The type of attention per head is learned or determined dynamically.

Example: BigBird uses a fixed pattern (global + local + random), but learned variations could decide which positions are global.

---

## Test Cases

### Test Case 1: Basic Multi-Head Forward

```java
void testBasicForward() {
    int dModel = 64;
    int numHeads = 4;
    int B = 2, Qlen = 8, Klen = 8;

    MultiHeadAttention mha = new MultiHeadAttention(dModel, numHeads);
    double[][] Q = new double[B][Qlen][dModel];
    double[][] K = new double[B][Klen][dModel];
    double[][] V = new double[B][Klen][dModel];

    for (int b = 0; b < B; b++) {
        for (int i = 0; i < Qlen; i++) {
            Arrays.fill(Q[b][i], 0.1 * (i + 1));
            Arrays.fill(K[b][i], 0.1 * (i + 1));
            Arrays.fill(V[b][i], 0.1 * (i + 1));
        }
    }

    double[][] output = mha.forward(Q, K, V, null);

    assert output.length == B : "Batch dimension";
    assert output[0].length == Qlen : "Q sequence length dimension";
    assert output[0][0].length == dModel : "Output dModel dimension";
}
```

### Test Case 2: Output Shape with Different Q and K Lengths (Cross-Attention)

```java
void testCrossAttentionShapes() {
    int dModel = 32;
    int numHeads = 4;
    int B = 1, Qlen = 5, Klen = 10;

    MultiHeadAttention mha = new MultiHeadAttention(dModel, numHeads);
    double[][] Q = new double[B][Qlen][dModel];
    double[][] K = new double[B][Klen][dModel];
    double[][] V = new double[B][Klen][dModel];

    double[][] output = mha.forward(Q, K, V, null);
    assert output[0].length == Qlen : "Output length equals query length";
    assert output[0][0].length == dModel : "Output dimension preserved";
}
```

### Test Case 3: Self-Attention (Q=K=V)

```java
void testSelfAttention() {
    int dModel = 16;
    int numHeads = 2;
    int B = 1, N = 4;

    MultiHeadAttention mha = new MultiHeadAttention(dModel, numHeads);
    double[][] X = new double[B][N][dModel];
    for (int i = 0; i < N; i++) {
        Arrays.fill(X[0][i], i + 1.0);
    }

    // Self-attention: Q, K, V all from same source
    double[][] output = mha.forward(X, X, X, null);
    assert output.length == B;
    assert output[0].length == N;
    assert output[0][0].length == dModel;
}
```

### Test Case 4: Head Count Validation

```java
void testHeadCount() {
    // Should throw if dModel not divisible by numHeads
    boolean threw = false;
    try {
        new MultiHeadAttention(64, 3); // 64 % 3 != 0
    } catch (IllegalArgumentException e) {
        threw = true;
    }
    assert threw : "Should throw when dModel not divisible by numHeads";

    // Should succeed for valid configuration
    MultiHeadAttention mha = new MultiHeadAttention(64, 4);
    assert mha.getNumHeads() == 4;
    assert mha.getDModel() == 64;
}
```

### Test Case 5: Per-Head Shape Verification

```java
void testPerHeadShape() {
    int dModel = 128;
    int numHeads = 8;
    int B = 2, N = 10;

    MultiHeadAttention mha = new MultiHeadAttention(dModel, numHeads);
    double[][] Q = new double[B][N][dModel];
    double[][] K = new double[B][N][dModel];
    double[][] V = new double[B][N][dModel];

    double[][] output = mha.forward(Q, K, V, null);

    // 8 heads of size 16 each → concatenated to 128
    double[][][][] headWeights = mha.getLastHeadWeights();
    assert headWeights.length == B : "Head weights batch dimension";
    assert headWeights[0].length == numHeads : "Number of heads";
    assert headWeights[0][0].length == N : "Head attention Q length";
    assert headWeights[0][0][0].length == N : "Head attention K length";
}
```

### Test Case 6: Mask Propagation

```java
void testMaskPropagation() {
    int dModel = 32;
    int numHeads = 4;
    int B = 1, N = 4;

    MultiHeadAttention mha = new MultiHeadAttention(dModel, numHeads);
    double[][] Q = new double[B][N][dModel];
    double[][] K = new double[B][N][dModel];
    double[][] V = new double[B][N][dModel];

    for (int i = 0; i < N; i++) {
        Arrays.fill(Q[0][i], 1.0);
        Arrays.fill(K[0][i], 1.0);
        Arrays.fill(V[0][i], i + 1.0);
    }

    // Causal mask
    double[][] causalMask = ScaledDotProductAttention.createCausalMask(B, N);
    double[][] output = mha.forward(Q, K, V, causalMask);

    // Output should be finite and valid
    for (int i = 0; i < N; i++) {
        for (int d = 0; d < dModel; d++) {
            assert !Double.isNaN(output[0][i][d]) : "NaN in output at pos " + i;
        }
    }
}
```

### Test Case 7: Different dModel Values

```java
void testVariousDModel() {
    // Test with different dModel values
    int[] dModels = {16, 32, 64, 128};
    int numHeads = 4;

    for (int dModel : dModels) {
        MultiHeadAttention mha = new MultiHeadAttention(dModel, numHeads);
        double[][] Q = new double[1][5][dModel];
        double[][] K = new double[1][5][dModel];
        double[][] V = new double[1][5][dModel];

        double[][] output = mha.forward(Q, K, V, null);
        assert output[0][0].length == dModel :
            "dModel=" + dModel + " output dim mismatch";
    }
}
```

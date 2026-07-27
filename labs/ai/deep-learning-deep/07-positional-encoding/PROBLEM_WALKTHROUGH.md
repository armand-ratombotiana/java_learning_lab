# PROBLEM WALKTHROUGH: Sinusoidal Positional Encoding

## Problem Statement

**Difficulty:** Medium | **Category:** Positional Encodings | **Estimated Time:** 60 minutes

Implement the sinusoidal positional encoding as defined in "Attention Is All You Need" (Vaswani et al., 2017). Your `PositionalEncoding` class must generate position-dependent sinusoidal signals at different frequencies, combine them with token embeddings, and support variable-length sequences up to a maximum length. Since Transformer self-attention is permutation-invariant (it processes sets, not sequences), positional encodings are essential for the model to use order information.

**Input:**
- `seqLength`: Number of positions to encode.
- `dModel`: The model dimension (same as the token embedding dimension).
- `maxLength` (optional): Maximum sequence length to pre-compute (default 2048).

**Output:**
- `positionalEncoding`: A 2D array of shape `(maxLength, dModel)` containing the fixed sinusoidal positional encodings.

**Integration with Embeddings:**
- `tokenEmbedding`: Shape `(batchSize, seqLength, dModel)`.
- Add the positional encoding (broadcast across batch): `finalEmbedding = tokenEmbedding + positionalEncoding[:seqLength]`.

**Constraints:**
- Use the exact formulas from the Transformer paper:
  - `PE(pos, 2i) = sin(pos / 10000^(2i / d_model))`
  - `PE(pos, 2i+1) = cos(pos / 10000^(2i / d_model))`
- Pre-compute all encodings up to `maxLength` during initialization.
- Support both scalar addition (broadcast across batch) and individual position lookup.

**Evaluation Criteria:**
- Correct application of sine and cosine at alternating positions.
- The frequency should decrease along the dimension axis (wavelengths from 2π to ~10000 * 2π).
- The output for position 0 should be all zeros (sin(0) = 0, cos(0) = 1 for even positions, but careful: PE(0, 0) = sin(0) = 0, PE(0, 1) = cos(0) = 1, etc.).
- Proper division of the dimension into even (sin) and odd (cos) indices.

---

## Step-by-Step Solution Walkthrough

### 1. Why Positional Encodings?

Self-attention is permutation-equivariant: if you permute the input tokens, the output is permuted the same way. Formally:

```
Attention(π(Q), π(K), π(V)) = π(Attention(Q, K, V))
```

Where `π` is a permutation. This means the model has no inherent notion of token position — it treats input as a set, not a sequence.

Positional encodings break this symmetry by adding position-dependent signals to the input embeddings. The model can then learn to use these signals for:
- Word order (subject before verb).
- Relative distances (how far apart are two words).
- Absolute position (position 1 vs position 10 in a sentence).

### 2. The Sinusoidal Encoding Formula

```
PE(pos, 2i)   = sin(pos / 10000^(2i / d_model))
PE(pos, 2i+1) = cos(pos / 10000^(2i / d_model))
```

Where:
- `pos` is the position (0-indexed).
- `i` is the dimension index (0 to `d_model/2 - 1`).

**Equivalent formulation:**

For a given position `pos`, the encoding vector has `d_model` dimensions. The frequency for dimension pair `(2i, 2i+1)` is:

```
ω_i = 1 / 10000^(2i / d_model)
```

So:
- Low dimensions (small `i`): high frequency, fast oscillation.
- High dimensions (large `i`): low frequency, slow oscillation.

### 3. Intuition: Frequency Bands

The encoding consists of sinusoids at geometrically spaced wavelengths:

| i | Frequency | Wavelength | What it captures |
|---|-----------|------------|------------------|
| 0 | 1.0 | 2π ≈ 6.28 | Adjacent positions |
| 1 | 1/10000^(2/d) | 2π * 10000^(2/d) | Slightly larger range |
| ... | ... | ... | ... |
| d/2-1 | 1/10000 | 2π * 10000 ≈ 62832 | Very long-range relationships |

This creates a multi-resolution encoding where lower dimensions encode fine-grained position information and higher dimensions encode coarse position information.

**Why this is useful:**
- Short-range attention uses lower dimensions (fast oscillations discriminate nearby positions).
- Long-range attention uses higher dimensions (slow oscillations provide global position context).
- The model can learn to attend to different frequency bands for different linguistic phenomena.

### 4. Properties of Sinusoidal Encodings

| Property | Description |
|----------|-------------|
| **Deterministic** | No learned parameters, fixed function of position |
| **Bounded** | Values in [-1, 1] (from sin/cos range) |
| **Periodic** | Encodings repeat every `2π / ω_i` positions per dimension |
| **Relative encoding** | The dot product of two position encodings depends only on their relative offset |
| **Extrapolatable** | Can handle sequences longer than those seen during training |

### 5. The Relative Position Property

A key insight: the dot product between two positional encodings depends only on the offset `Δ = pos2 - pos1`.

For a given dimension pair `(2i, 2i+1)`:

```
PE(pos + Δ) · PE(pos) ∝ cos(Δ * ω_i)
```

This is derived from trigonometric identities:
```
cos(A - B) = cos(A)cos(B) + sin(A)sin(B)
```

So:
```
PE(pos+Δ, 2i) * PE(pos, 2i) + PE(pos+Δ, 2i+1) * PE(pos, 2i+1)
= sin((pos+Δ)*ω_i) * sin(pos*ω_i) + cos((pos+Δ)*ω_i) * cos(pos*ω_i)
= cos((pos+Δ)*ω_i - pos*ω_i)
= cos(Δ * ω_i)
```

This means the attention score between two positions depends only on their relative distance, not their absolute positions! This is a very desirable property for sequence modeling.

### 6. Learned vs Fixed Encodings

| Aspect | Sinusoidal (Fixed) | Learned |
|--------|-------------------|---------|
| Parameters | 0 | `maxLen * d_model` |
| Extrapolation | Natural (sin/cos defined for all pos) | Requires training on max length |
| Inductive bias | Smooth, relative position bias | Task-specific |
| Training speed | No learned embeddings to train | Slight overhead |
| Performance | Comparable on most tasks | Slightly better on short sequences |

In practice, learned positional encodings perform similarly to sinusoidal encodings on most tasks. The sinusoidal version is preferred when:
- The model may need to handle longer sequences at inference than during training.
- Parameter efficiency is a concern.
- A strong prior for relative position encoding is desired.

---

## Java Implementation

```java
package lab07.transformer;

/**
 * Sinusoidal positional encoding as defined in "Attention Is All You Need"
 * (Vaswani et al., 2017).
 * <p>
 * Generates position-dependent sinusoidal signals at different frequencies
 * and adds them to token embeddings to provide position information to the
 * permutation-invariant self-attention mechanism.
 * <p>
 * PE(pos, 2i)   = sin(pos / 10000^(2i / d_model))
 * PE(pos, 2i+1) = cos(pos / 10000^(2i / d_model))
 */
public class PositionalEncoding {

    private final int dModel;
    private final int maxLength;
    private final double[][] encoding; // (maxLength, dModel)

    /**
     * Constructs sinusoidal positional encodings up to maxLength.
     *
     * @param dModel    the model dimension (must be even)
     * @param maxLength maximum sequence length to pre-compute
     * @throws IllegalArgumentException if dModel is not even or parameters are invalid
     */
    public PositionalEncoding(int dModel, int maxLength) {
        if (dModel <= 0) {
            throw new IllegalArgumentException("dModel must be positive, got: " + dModel);
        }
        if (dModel % 2 != 0) {
            throw new IllegalArgumentException(
                "dModel must be even for sinusoidal encoding, got: " + dModel);
        }
        if (maxLength <= 0) {
            throw new IllegalArgumentException(
                "maxLength must be positive, got: " + maxLength);
        }

        this.dModel = dModel;
        this.maxLength = maxLength;
        this.encoding = new double[maxLength][dModel];
        computeEncoding();
    }

    /**
     * Pre-computes the positional encoding matrix.
     * PE(pos, 2i)   = sin(pos / 10000^(2i / d_model))
     * PE(pos, 2i+1) = cos(pos / 10000^(2i / d_model))
     */
    private void computeEncoding() {
        for (int pos = 0; pos < maxLength; pos++) {
            for (int i = 0; i < dModel / 2; i++) {
                double angle = pos / Math.pow(10000.0, (2.0 * i) / dModel);
                encoding[pos][2 * i] = Math.sin(angle);
                encoding[pos][2 * i + 1] = Math.cos(angle);
            }
        }
    }

    /**
     * Returns the full encoding matrix.
     *
     * @return 2D array of shape (maxLength, dModel)
     */
    public double[][] getEncoding() {
        return encoding;
    }

    /**
     * Returns the encoding for a specific position.
     *
     * @param position the position index (0-indexed, must be &lt; maxLength)
     * @return encoding vector of length dModel
     * @throws IndexOutOfBoundsException if position &gt;= maxLength
     */
    public double[] getPosition(int position) {
        if (position >= maxLength) {
            throw new IndexOutOfBoundsException(
                "Position " + position + " exceeds maxLength " + maxLength);
        }
        return encoding[position];
    }

    /**
     * Returns encodings for a subsequence of positions.
     *
     * @param start start position (inclusive)
     * @param end   end position (exclusive)
     * @return 2D array of shape (end - start, dModel)
     */
    public double[][] getRange(int start, int end) {
        if (start < 0 || end > maxLength || start >= end) {
            throw new IllegalArgumentException(
                "Invalid range: [" + start + ", " + end + ") within [0, " + maxLength + ")");
        }
        int len = end - start;
        double[][] result = new double[len][dModel];
        for (int i = 0; i < len; i++) {
            System.arraycopy(encoding[start + i], 0, result[i], 0, dModel);
        }
        return result;
    }

    /**
     * Adds positional encodings to token embeddings (in-place).
     *
     * @param tokenEmbeddings token embeddings of shape (batchSize, seqLength, dModel)
     * @throws IllegalArgumentException if seqLength &gt; maxLength or dimensions mismatch
     */
    public void addToEmbeddings(double[][] tokenEmbeddings) {
        int batchSize = tokenEmbeddings.length;
        int seqLength = tokenEmbeddings[0].length;
        int d = tokenEmbeddings[0][0].length;

        if (seqLength > maxLength) {
            throw new IllegalArgumentException(
                "Sequence length " + seqLength + " exceeds maxLength " + maxLength);
        }
        if (d != dModel) {
            throw new IllegalArgumentException(
                "Embedding dimension " + d + " doesn't match dModel " + dModel);
        }

        for (int b = 0; b < batchSize; b++) {
            for (int pos = 0; pos < seqLength; pos++) {
                for (int dim = 0; dim < dModel; dim++) {
                    tokenEmbeddings[b][pos][dim] += encoding[pos][dim];
                }
            }
        }
    }

    /**
     * Creates a new tensor with positional encodings added.
     *
     * @param tokenEmbeddings token embeddings of shape (batchSize, seqLength, dModel)
     * @return new tensor with positional encodings added
     */
    public double[][] apply(double[][] tokenEmbeddings) {
        int batchSize = tokenEmbeddings.length;
        int seqLength = tokenEmbeddings[0].length;
        int d = tokenEmbeddings[0][0].length;

        double[][] result = new double[batchSize][seqLength][d];
        for (int b = 0; b < batchSize; b++) {
            for (int pos = 0; pos < seqLength; pos++) {
                for (int dim = 0; dim < dModel; dim++) {
                    result[b][pos][dim] = tokenEmbeddings[b][pos][dim] + encoding[pos][dim];
                }
            }
        }
        return result;
    }

    /**
     * Returns the model dimension.
     */
    public int getDModel() {
        return dModel;
    }

    /**
     * Returns the maximum length.
     */
    public int getMaxLength() {
        return maxLength;
    }
}
```

**Example Usage:**

```java
package lab07.transformer;

import java.util.Arrays;

public class PositionalEncodingExample {
    public static void main(String[] args) {
        int dModel = 6;   // Small for display
        int maxLen = 10;

        PositionalEncoding pe = new PositionalEncoding(dModel, maxLen);

        // Print encodings
        System.out.println("Positional encodings (first 5 positions):");
        for (int pos = 0; pos < 5; pos++) {
            System.out.println("pos " + pos + ": " +
                Arrays.toString(pe.getPosition(pos)));
        }

        // Apply to embeddings
        int batchSize = 2;
        int seqLength = 5;
        double[][] embeddings = new double[batchSize][seqLength][dModel];
        // Fill with some values
        for (int b = 0; b < batchSize; b++) {
            for (int pos = 0; pos < seqLength; pos++) {
                Arrays.fill(embeddings[b][pos], 1.0);
            }
        }

        double[][] result = pe.apply(embeddings);
        System.out.println("\nFirst batch, first position after PE added:");
        System.out.println(Arrays.toString(result[0][0]));
    }
}
```

---

## Complexity Analysis

### Time Complexity

**Pre-computation:** `O(maxLength * dModel)` — computed once during construction.

This is negligible — for `maxLength = 2048` and `dModel = 512`, it's about 1 million values.

**Runtime addition per forward pass:** `O(batchSize * seqLength * dModel)` — element-wise addition.

### Space Complexity

**Encoding storage:** `O(maxLength * dModel)` — `2048 * 512 ≈ 1M` doubles ≈ 8 MB (for float64).

This is a one-time cost, constant regardless of batch size.

### Comparison with Alternatives

| Method | Storage | Extrapolation | Learnability |
|--------|---------|---------------|--------------|
| Sinusoidal (ours) | `O(maxLen * d)` | Excellent | None |
| Learned absolute | `O(maxLen * d)` | None (fixed length) | Full |
| Relative (RPR) | `O(maxLen²)` for full | Limited | Full |
| Rotary (RoPE) | `O(d)` (just angles) | Excellent | None (fixed freq) |
| ALiBi | `O(1)` (bias slopes) | Excellent | None |

---

## Follow-Up Questions with Answers

### Q1: Why can't self-attention inherently model position without positional encodings?

**Answer:** Self-attention is permutation-equivariant. For any permutation `π`:

```
Attention(π(Q), π(K), π(V))_i = Σ_j softmax(π(Q)_i · π(K)_j / sqrt(d)) * π(V)_j
                                = π(Attention(Q, K, V))_i
```

This means the model treats the input as an unordered set. The word "dog" at position 1 produces the same attention output as "dog" at position 10, all else being equal.

**Concrete example:** Without positional encoding, the sentences "The dog bit the man" and "The man bit the dog" would have identical representations because they contain the same bag of words. Positional encodings allow the model to distinguish "dog" as subject vs "dog" as object.

### Q2: How do sinusoidal encodings enable the model to attend to relative positions?

**Answer:** The dot product between two positional encodings at positions `p1` and `p2` depends only on their offset `Δ = p2 - p1`:

For each dimension pair:
```
PE(p1, 2i) * PE(p2, 2i) + PE(p1, 2i+1) * PE(p2, 2i+1)
  = cos((p1 - p2) * ω_i)
```

So the attention logit (before softmax) for position `p1` attending to position `p2` includes:

```
(PE(p1) + embed(p1)) · (PE(p2) + embed(p2))
  = embed(p1)·embed(p2) + embed(p1)·PE(p2) + PE(p1)·embed(p2) + PE(p1)·PE(p2)
```

The last term is `Σ_i cos(Δ * ω_i)`, which only depends on the relative offset. The model can learn to weight the "relative position content" via the value projections.

### Q3: What is Rotary Positional Encoding (RoPE) and how does it differ from sinusoidal encoding?

**Answer:** Rotary Position Encoding (RoPE, Su et al., 2021) applies rotation to the query and key vectors based on their positions, rather than adding a positional signal to the input.

**Sinusoidal (additive):**
```
q' = q + PE(pos_q)
k' = k + PE(pos_k)
Score = (q + PE(pos_q)) · (k + PE(pos_k))
```

**RoPE (multiplicative):**
```
q' = R(pos_q) * q   (rotate q by pos_q-dependent angle)
k' = R(pos_k) * k   (rotate k by pos_k-dependent angle)
Score = (R(pos_q)*q) · (R(pos_k)*k) = q · R(pos_k - pos_q)*k
```

**Key differences:**
1. RoPE multiplies (rotates) instead of adds.
2. RoPE preserves the relative position explicitly in the score function.
3. RoPE has better theoretical properties for long-range dependencies.
4. Sinusoidal decoding is simpler and more interpretable.

RoPE is used in Llama, Mistral, and many modern LLMs.

### Q4: What is ALiBi (Attention with Linear Biases) and how does it differ from RoPE and sinusoidal?

**Answer:** ALiBi (Press et al., 2022) takes a simpler approach: instead of adding positional signals to embeddings, it adds a bias to the attention scores:

```
Score(q_i, k_j) = q_i · k_j / sqrt(d) + m * (j - i + 1)
```

Where `m` is a head-specific slope (pre-defined geometric sequence).

**Comparison:**

| Method | Computation | Parameters | Extra-Long Extrapolation |
|--------|-------------|------------|-------------------------|
| Sinusoidal | `+ PE` to embeddings | 0 | Good |
| RoPE | Rotate Q/K vectors | 0 | Very good |
| ALiBi | `+ bias` to scores | 0 | Excellent |

ALiBi was designed specifically for extrapolation to sequences much longer than training. Models trained with ALiBi on 512-length sequences can extrapolate to 2048+ without finetuning. However, RoPE has become more popular in practice.

### Q5: How would you implement relative positional encoding (RPR) as used in TransformerXL?

**Answer:** Relative Positional Representations (Shaw et al., 2018) modify the attention score to include a learned relative position embedding:

```
Score(q_i, k_j) = (q_i · k_j) + (q_i · a_{i-j})
```

Where `a_{i-j}` is a learned embedding for the offset `i - j`, clipped to a maximum distance (`k_max`).

**TransformerXL extends this** by also applying relative position to the values and using a sinusoidal bias:

```
Score = q_i · k_j + q_i · W_K^R · r_{i-j} + u · k_j + v · W_K^R · r_{i-j}
```

Where:
- `r_{i-j}` is the sinusoidal encoding of the relative position.
- `u` and `v` are learned global biases.
- `W_K^R` is a learned projection.

This enables the model to handle positions beyond the training length while maintaining relative position awareness, which is crucial for transformer-based language models.

---

## Test Cases

### Test Case 1: Basic Encoding Shape and Values

```java
void testBasicEncoding() {
    int dModel = 4;
    int maxLen = 10;

    PositionalEncoding pe = new PositionalEncoding(dModel, maxLen);

    // Check dimensions
    assert pe.getPosition(0).length == dModel : "Encoding vector length";
    assert pe.getEncoding().length == maxLen : "Encoding matrix rows";
    assert pe.getEncoding()[0].length == dModel : "Encoding matrix cols";

    // Position 0: PE(0, 0) = sin(0) = 0, PE(0, 1) = cos(0) = 1
    //           PE(0, 2) = sin(0) = 0, PE(0, 3) = cos(0) = 1
    double[] pos0 = pe.getPosition(0);
    assert Math.abs(pos0[0] - 0.0) < 1e-10 : "PE(0, 0) should be 0";
    assert Math.abs(pos0[1] - 1.0) < 1e-10 : "PE(0, 1) should be 1";
    assert Math.abs(pos0[2] - 0.0) < 1e-10 : "PE(0, 2) should be 0";
    assert Math.abs(pos0[3] - 1.0) < 1e-10 : "PE(0, 3) should be 1";
}
```

### Test Case 2: Sin-Cos Alternation

```java
void testSinCosAlternation() {
    int dModel = 8;
    PositionalEncoding pe = new PositionalEncoding(dModel, 100);

    double[] pos = pe.getPosition(5);
    // Even indices should follow sin pattern, odd indices cos pattern
    // PE(5, 0) = sin(5 / 10000^(0/8)) = sin(5) ≈ -0.9589
    // PE(5, 1) = cos(5 / 10000^(0/8)) = cos(5) ≈ 0.2837
    // PE(5, 2) = sin(5 / 10000^(2/8)) = sin(5/sqrt(10)) = sin(1.581) ≈ 0.9999
    // PE(5, 3) = cos(5 / 10000^(2/8)) = cos(1.581) ≈ -0.005
    assert Math.abs(pos[0] - Math.sin(5)) < 1e-10 : "i=0 even should be sin";
    assert Math.abs(pos[1] - Math.cos(5)) < 1e-10 : "i=0 odd should be cos";
}
```

### Test Case 3: Frequency Decreases with Dimension Index

```java
void testFrequencyDecreases() {
    int dModel = 32;
    PositionalEncoding pe = new PositionalEncoding(dModel, 1000);

    // Higher dimension index = lower frequency = more similar adjacent positions
    // For adjacent positions p and p+1:
    // Low dim (i=0, high freq): values should differ significantly
    // High dim (i=large, low freq): values should be similar

    double[] pos10 = pe.getPosition(100);
    double[] pos11 = pe.getPosition(101);

    // Low-frequency dims (near dModel/2) should have small differences
    double diffLow = Math.abs(pos10[dModel - 2] - pos11[dModel - 2]);
    double diffHigh = Math.abs(pos10[0] - pos11[0]);

    // The low-frequency dimension should change more slowly
    // (diffLow might not always be smaller since sin isn't monotonic,
    //  but the angular frequency is definitely lower)
    double angleLow = 100 / Math.pow(10000, (dModel - 2.0) / dModel);
    double angleHigh = 100 / Math.pow(10000, 0.0);
    assert Math.abs(angleLow) < Math.abs(angleHigh) :
        "Lower dimensions should have higher angular frequency";
}
```

### Test Case 4: Add to Embeddings

```java
void testAddToEmbeddings() {
    int dModel = 8;
    int maxLen = 50;
    PositionalEncoding pe = new PositionalEncoding(dModel, maxLen);

    int batchSize = 2;
    int seqLen = 10;
    double[][] embeddings = new double[batchSize][seqLen][dModel];

    // All-zero embeddings
    double[][] result = pe.apply(embeddings);

    // Result should equal the positional encoding itself
    for (int b = 0; b < batchSize; b++) {
        for (int pos = 0; pos < seqLen; pos++) {
            double[] expected = pe.getPosition(pos);
            for (int d = 0; d < dModel; d++) {
                assert Math.abs(result[b][pos][d] - expected[d]) < 1e-10 :
                    "Result[" + b + "][" + pos + "][" + d + "] should equal PE";
            }
        }
    }
}
```

### Test Case 5: Non-zero Embeddings Addition

```java
void testNonZeroEmbeddings() {
    int dModel = 6;
    PositionalEncoding pe = new PositionalEncoding(dModel, 10);

    double[][] embeddings = new double[1][5][dModel];
    for (int pos = 0; pos < 5; pos++) {
        Arrays.fill(embeddings[0][pos], 2.0);
    }

    pe.addToEmbeddings(embeddings);

    // Each position should be 2.0 + PE(pos)
    for (int pos = 0; pos < 5; pos++) {
        double[] peVec = pe.getPosition(pos);
        for (int d = 0; d < dModel; d++) {
            assert Math.abs(embeddings[0][pos][d] - (2.0 + peVec[d])) < 1e-10 :
                "Expected " + (2.0 + peVec[d]) + " at [" + pos + "][" + d + "]";
        }
    }
}
```

### Test Case 6: Bounds Verification

```java
void testBounds() {
    int dModel = 16;
    int maxLen = 100;
    PositionalEncoding pe = new PositionalEncoding(dModel, maxLen);

    // All values should be in [-1, 1]
    for (int pos = 0; pos < maxLen; pos++) {
        double[] encoding = pe.getPosition(pos);
        for (int d = 0; d < dModel; d++) {
            assert encoding[d] >= -1.0 && encoding[d] <= 1.0 :
                "Value out of bounds at [" + pos + "][" + d + "]: " + encoding[d];
        }
    }
}
```

### Test Case 7: Sequence Length Exceeds MaxLength

```java
void testExceedsMaxLength() {
    int dModel = 8;
    int maxLen = 10;
    PositionalEncoding pe = new PositionalEncoding(dModel, maxLen);

    double[][] longEmbeddings = new double[1][15][8];
    boolean threw = false;
    try {
        pe.addToEmbeddings(longEmbeddings);
    } catch (IllegalArgumentException e) {
        threw = true;
    }
    assert threw : "Should throw when sequence length exceeds maxLength";
}
```

### Test Case 8: Relative Offset Dot Product

```java
void testRelativeOffsetProperty() {
    int dModel = 16;
    PositionalEncoding pe = new PositionalEncoding(dModel, 100);

    double[] pos5 = pe.getPosition(5);
    double[] pos8 = pe.getPosition(8);
    double[] pos12 = pe.getPosition(12);

    // Dot product of (pos8, pos5) should equal dot product of (pos12, pos9)
    // because both have offset = 3
    double dot8_5 = 0;
    double dot12_9 = 0;
    double[] pos9 = pe.getPosition(9);
    for (int d = 0; d < dModel; d++) {
        dot8_5 += pos8[d] * pos5[d];
        dot12_9 += pos12[d] * pos9[d];
    }

    assert Math.abs(dot8_5 - dot12_9) < 1e-10 :
        "Dot products for same offset should be equal: " + dot8_5 + " vs " + dot12_9;

    // Dot product of same position (offset=0) should be higher
    double dot5_5 = 0;
    for (int d = 0; d < dModel; d++) {
        dot5_5 += pos5[d] * pos5[d];
    }
    assert dot5_5 >= dot8_5 : "Same position should have highest dot product";
}
```

# PROBLEM WALKTHROUGH: Sequence-to-Sequence with Attention

## Problem Statement

**Difficulty:** Hard | **Category:** Sequence-to-Sequence Models | **Estimated Time:** 100 minutes

Implement a Bahdanau-style additive attention mechanism for an encoder-decoder architecture. Your `Attention` class must compute alignment scores between a decoder hidden state and all encoder hidden states, produce a context vector as a weighted sum, and integrate with a decoder that uses teacher forcing during training.

**Input:**
- `decoderHidden`: A 2D array of shape `(batchSize, decoderHiddenSize)` representing the current decoder hidden state `s_{t-1}`.
- `encoderOutputs`: A 3D array of shape `(batchSize, srcLength, encoderHiddenSize)` representing all encoder hidden states `h_1, ..., h_{T_src}`.

**Attention Parameters:**
- `W_a`: A weight matrix of shape `(decoderHiddenSize, attentionSize)`.
- `U_a`: A weight matrix of shape `(encoderHiddenSize, attentionSize)`.
- `v_a`: A weight vector of length `attentionSize` (the "context vector" parameter in Bahdanau's formulation).

**Output:**
- `contextVector`: A 2D array of shape `(batchSize, encoderHiddenSize)` — the weighted sum of encoder outputs.
- `attentionWeights`: A 2D array of shape `(batchSize, srcLength)` — the normalized alignment scores.

**Constraints:**
- Implement additive attention (Bahdanau) — not multiplicative (Luong) attention.
- Use the softmax function for alignment score normalization.
- Support variable-length source sequences via masking (provide an optional `srcMask` parameter).
- The decoder should combine the context vector with the current input embedding and previous decoder output to produce the next output.

**Evaluation Criteria:**
- Correct alignment score computation.
- Softmax normalizes across the source dimension (sum to 1 for each batch element).
- Context vector shape and numerical correctness.
- Masking properly ignores padding positions.

---

## Step-by-Step Solution Walkthrough

### 1. The Encoder-Decoder Architecture

**Encoder:** Processes the source sequence `x_1, ..., x_{T_src}` and produces a sequence of hidden states `h_1, ..., h_{T_src}`. In vanilla Seq2Seq (Sutskever et al., 2014), only the final encoder state is passed to the decoder.

**Decoder:** Generates the target sequence `y_1, ..., y_{T_tgt}` one token at a time. At each timestep `t`, the decoder uses its previous hidden state `s_{t-1}`, the previous output `y_{t-1}`, and (with attention) a context vector `c_t` to produce the next output.

**The bottleneck problem:** Vanilla Seq2Seq compresses the entire source into a single vector (the final encoder state). For long sequences, this is a severe bottleneck — information about early tokens can be lost.

### 2. Bahdanau Attention (Additive Attention)

Bahdanau et al. (2015) proposed attention to address the bottleneck:

**Step 1 — Alignment Scores:**

For each encoder position `j`, compute how well the decoder state `s_{t-1}` aligns with encoder state `h_j`:

```
e_{t,j} = v_a^T * tanh(W_a * s_{t-1} + U_a * h_j)
```

- `v_a`, `W_a`, `U_a` are learned parameters.
- The `tanh` non-linearity allows complex interactions between `s` and `h`.
- This is "additive" attention because the contributions of `s` and `h` are added before the non-linearity.

**Step 2 — Attention Weights:**

Normalize the alignment scores using softmax:

```
α_{t,j} = exp(e_{t,j}) / Σ_{k=1}^{T_src} exp(e_{t,k})
```

**Step 3 — Context Vector:**

Compute a weighted sum of encoder hidden states:

```
c_t = Σ_{j=1}^{T_src} α_{t,j} * h_j
```

**Step 4 — Decoder Output:**

Combine the context vector with the decoder's state and input:

```
s_t = f(s_{t-1}, y_{t-1}, c_t)   // decoder RNN step
P(y_t | y_{<t}, x) = softmax(g(s_t, c_t))
```

### 3. Alignment Score Computation Detail

The score computation can be optimized by pre-computing the encoder contributions:

```
// Pre-compute for all encoder positions
encoder_proj_j = U_a * h_j   // shape: (srcLength, attentionSize)

// At each decoder timestep:
decoder_proj = W_a * s_{t-1}  // shape: (attentionSize)
energy_j = v_a * tanh(decoder_proj + encoder_proj_j)  // scalar per position
```

This pre-computation is why Bahdanau attention is efficient: the encoder projection doesn't change, so it's computed once.

### 4. Masking for Variable-Length Sequences

When sequences in a batch have different lengths, shorter sequences are padded. The attention must ignore padded positions:

```
// Apply mask before softmax
e_{t,j} = e_{t,j} + (mask[j] == 0 ? -INF : 0)

// Softmax of -INF values will produce 0 attention
```

A common technique is to set masked positions to `-1e9` (negative large) before softmax so that `exp(-1e9) ≈ 0`.

### 5. Teacher Forcing

During training, the decoder receives the ground truth previous token as input instead of its own prediction:

```
// Teacher forcing: use true target
decoder_input_t = y_{t-1}  // ground truth

// Without teacher forcing (inference):
decoder_input_t = argmax(softmax(output_{t-1}))  // predicted
```

Teacher forcing:
- **Pros:** Faster convergence, stable training.
- **Cons:** Exposure bias — the model never sees its own errors during training, causing issues at inference.

### 6. Beam Search (Conceptual)

At inference, instead of greedy decoding (choosing the most likely token at each step), beam search maintains `k` hypotheses:

1. Start with one hypothesis (the `<sos>` token).
2. At each step, expand each hypothesis by the top `k` tokens.
3. Keep the top `k` hypotheses by cumulative probability.
4. Continue until all hypotheses hit `<eos>` or max length.
5. Choose the hypothesis with the highest normalized score.

---

## Java Implementation

```java
package lab04.seq2seq;

import java.util.Arrays;

/**
 * Bahdanau-style additive attention mechanism for sequence-to-sequence models.
 * <p>
 * Computes alignment scores between a decoder hidden state and encoder hidden states,
 * produces a context vector as a weighted sum. Supports variable-length source
 * sequences via masking.
 */
public class Attention {

    private final int decoderHiddenSize;
    private final int encoderHiddenSize;
    private final int attentionSize;

    // Learnable parameters
    private double[][] wA;  // (decoderHiddenSize, attentionSize)
    private double[][] uA;  // (encoderHiddenSize, attentionSize)
    private double[] vA;    // (attentionSize)

    /**
     * Constructs an Attention mechanism with given dimensions.
     *
     * @param decoderHiddenSize size of the decoder hidden state
     * @param encoderHiddenSize size of the encoder hidden states
     * @param attentionSize     size of the attention hidden layer
     */
    public Attention(int decoderHiddenSize, int encoderHiddenSize, int attentionSize) {
        this.decoderHiddenSize = decoderHiddenSize;
        this.encoderHiddenSize = encoderHiddenSize;
        this.attentionSize = attentionSize;

        double stdDec = Math.sqrt(2.0 / (decoderHiddenSize + attentionSize));
        double stdEnc = Math.sqrt(2.0 / (encoderHiddenSize + attentionSize));
        double stdV = Math.sqrt(2.0 / (attentionSize + 1));

        this.wA = glorotInit(decoderHiddenSize, attentionSize, stdDec);
        this.uA = glorotInit(encoderHiddenSize, attentionSize, stdEnc);
        this.vA = new double[attentionSize];
        for (int i = 0; i < attentionSize; i++) {
            this.vA[i] = randn() * stdV;
        }
    }

    /**
     * Computes attention scores and context vector for a single decoder timestep.
     *
     * @param decoderHidden decoder hidden state s_{t-1} of shape (batchSize, decoderHiddenSize)
     * @param encoderOutputs all encoder hidden states of shape (batchSize, srcLength, encoderHiddenSize)
     * @param srcMask       optional mask of shape (batchSize, srcLength) — 1 for valid, 0 for padding
     * @return array of two tensors: [contextVector, attentionWeights]
     *         contextVector shape: (batchSize, encoderHiddenSize)
     *         attentionWeights shape: (batchSize, srcLength)
     */
    public double[][][] forward(double[][] decoderHidden, double[][] encoderOutputs,
                                 double[][] srcMask) {
        int batchSize = decoderHidden.length;
        int srcLength = encoderOutputs[0].length;
        int encSize = encoderOutputs[0][0].length;

        // Step 1: Project decoder hidden to attention space
        // score[batch][j] = v_a^T * tanh(W_a * s + U_a * h_j)
        double[][] attentionScores = new double[batchSize][srcLength];

        for (int b = 0; b < batchSize; b++) {
            for (int j = 0; j < srcLength; j++) {
                double sum = 0;
                for (int a = 0; a < attentionSize; a++) {
                    double decProj = 0;
                    for (int d = 0; d < decoderHiddenSize; d++) {
                        decProj += decoderHidden[b][d] * wA[d][a];
                    }
                    double encProj = 0;
                    for (int e = 0; e < encoderHiddenSize; e++) {
                        encProj += encoderOutputs[b][j][e] * uA[e][a];
                    }
                    sum += vA[a] * Math.tanh(decProj + encProj);
                }
                attentionScores[b][j] = sum;
            }
        }

        // Step 2: Apply mask (if provided) and softmax
        if (srcMask != null) {
            for (int b = 0; b < batchSize; b++) {
                for (int j = 0; j < srcLength; j++) {
                    if (srcMask[b][j] == 0) {
                        attentionScores[b][j] = -1e9; // mask padding
                    }
                }
            }
        }

        double[][] attentionWeights = softmax2D(attentionScores);

        // Step 3: Compute context vector as weighted sum of encoder outputs
        double[][] contextVector = new double[batchSize][encoderHiddenSize];
        for (int b = 0; b < batchSize; b++) {
            for (int j = 0; j < srcLength; j++) {
                double alpha = attentionWeights[b][j];
                for (int e = 0; e < encoderHiddenSize; e++) {
                    contextVector[b][e] += alpha * encoderOutputs[b][j][e];
                }
            }
        }

        return new double[][][]{contextVector, attentionWeights};
    }

    /**
     * Applies softmax along the last dimension (rows) of a 2D array.
     */
    private double[][] softmax2D(double[][] input) {
        int rows = input.length;
        int cols = input[0].length;
        double[][] output = new double[rows][cols];

        for (int r = 0; r < rows; r++) {
            // Find max for numerical stability
            double max = Double.NEGATIVE_INFINITY;
            for (int c = 0; c < cols; c++) {
                if (input[r][c] > max) max = input[r][c];
            }

            double sum = 0;
            for (int c = 0; c < cols; c++) {
                output[r][c] = Math.exp(input[r][c] - max);
                sum += output[r][c];
            }

            for (int c = 0; c < cols; c++) {
                output[r][c] /= sum;
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
}
```

**Decoder with Attention and Teacher Forcing:**

```java
package lab04.seq2seq;

import java.util.Arrays;

/**
 * Decoder with Bahdanau attention and teacher forcing support.
 */
public class AttentionDecoder {

    private final int vocabSize;
    private final int embeddingSize;
    private final int hiddenSize;
    private final int encoderHiddenSize;

    private final Attention attention;
    private double[][] embedding;     // (vocabSize, embeddingSize)
    private double[][] wRnn;          // (embeddingSize + hiddenSize, hiddenSize)
    private double[][] uRnn;          // (hiddenSize, hiddenSize)
    private double[] bRnn;
    private double[][] wOut;          // (hiddenSize, vocabSize)
    private double[] bOut;

    public AttentionDecoder(int vocabSize, int embeddingSize, int hiddenSize,
                            int encoderHiddenSize, int attentionSize) {
        this.vocabSize = vocabSize;
        this.embeddingSize = embeddingSize;
        this.hiddenSize = hiddenSize;
        this.encoderHiddenSize = encoderHiddenSize;
        this.attention = new Attention(hiddenSize, encoderHiddenSize, attentionSize);

        double stdEmb = Math.sqrt(2.0 / (vocabSize + embeddingSize));
        this.embedding = glorotInit(vocabSize, embeddingSize, stdEmb);

        int rnnInputSize = embeddingSize + encoderHiddenSize;
        double stdRnn = Math.sqrt(2.0 / (rnnInputSize + hiddenSize));
        this.wRnn = glorotInit(rnnInputSize, hiddenSize, stdRnn);
        this.uRnn = glorotInit(hiddenSize, hiddenSize, stdRnn);
        this.bRnn = new double[hiddenSize];

        double stdOut = Math.sqrt(2.0 / (hiddenSize + vocabSize));
        this.wOut = glorotInit(hiddenSize, vocabSize, stdOut);
        this.bOut = new double[vocabSize];
    }

    /**
     * Forward pass across the entire target sequence (with teacher forcing).
     *
     * @param targetTokens  target sequence token indices (batchSize, tgtLength)
     * @param encoderOutputs encoder hidden states (batchSize, srcLength, encoderHiddenSize)
     * @param srcMask       source mask (batchSize, srcLength)
     * @return decoder outputs (batchSize, tgtLength, vocabSize) — logits before softmax
     */
    public double[][] forwardSequence(int[][] targetTokens, double[][] encoderOutputs,
                                               double[][] srcMask) {
        int batchSize = targetTokens.length;
        int tgtLength = targetTokens[0].length;
        int srcLength = encoderOutputs[0].length;

        double[][] decoderOutputs = new double[batchSize * tgtLength][vocabSize];
        double[][] hiddenState = new double[batchSize][hiddenSize];

        for (int t = 0; t < tgtLength; t++) {
            // Get teacher-forced input token
            int inputToken = (t == 0) ? 0 : targetTokens[0][t - 1]; // <sos> = 0

            // Look up embedding
            double[][] inputEmbed = new double[batchSize][embeddingSize];
            for (int b = 0; b < batchSize; b++) {
                int token = (t == 0) ? 0 : targetTokens[b][t - 1];
                System.arraycopy(embedding[token], 0, inputEmbed[b], 0, embeddingSize);
            }

            // Compute attention context
            double[][][] attnResult = attention.forward(hiddenState, encoderOutputs, srcMask);
            double[][] context = attnResult[0];

            // Concatenate embedding and context
            double[][] rnnInput = new double[batchSize][embeddingSize + encoderHiddenSize];
            for (int b = 0; b < batchSize; b++) {
                System.arraycopy(inputEmbed[b], 0, rnnInput[b], 0, embeddingSize);
                System.arraycopy(context[b], 0, rnnInput[b], embeddingSize, encoderHiddenSize);
            }

            // Simple RNN step: h_t = tanh(W * [emb; ctx] + U * h_{t-1} + b)
            double[][] newHidden = new double[batchSize][hiddenSize];
            for (int b = 0; b < batchSize; b++) {
                Arrays.fill(newHidden[b], bRnn[0]); // broadcast bias
                for (int i = 0; i < rnnInput[0].length; i++) {
                    for (int h = 0; h < hiddenSize; h++) {
                        newHidden[b][h] += rnnInput[b][i] * wRnn[i][h];
                    }
                }
                for (int hPrev = 0; hPrev < hiddenSize; hPrev++) {
                    for (int h = 0; h < hiddenSize; h++) {
                        newHidden[b][h] += hiddenState[b][hPrev] * uRnn[hPrev][h];
                    }
                }
                for (int h = 0; h < hiddenSize; h++) {
                    newHidden[b][h] = Math.tanh(newHidden[b][h]);
                }
            }
            hiddenState = newHidden;

            // Output projection
            double[][] logits = new double[batchSize][vocabSize];
            for (int b = 0; b < batchSize; b++) {
                System.arraycopy(bOut, 0, logits[b], 0, vocabSize);
                for (int h = 0; h < hiddenSize; h++) {
                    for (int v = 0; v < vocabSize; v++) {
                        logits[b][v] += hiddenState[b][h] * wOut[h][v];
                    }
                }
                // Copy to flat output
                System.arraycopy(logits[b], 0, decoderOutputs[t * batchSize + b], 0, vocabSize);
            }
        }

        return decoderOutputs;
    }

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
}
```

---

## Complexity Analysis

### Time Complexity

**Attention computation per decoder timestep:**

| Step | Complexity |
|------|------------|
| Project decoder state | `O(B * D_dec * A)` |
| Project encoder states | `O(B * T_src * D_enc * A)` (can be pre-computed) |
| Score computation | `O(B * T_src * A)` |
| Softmax | `O(B * T_src)` |
| Context vector | `O(B * T_src * D_enc)` |

**Total per timestep:** `O(B * A * (D_dec + T_src * D_enc) + B * T_src * D_enc)`

**Over the full sequence:** `O(T_tgt * (above))`

Where:
- `B` = batch size
- `T_src` = source length
- `T_tgt` = target length
- `D_dec` = decoder hidden size
- `D_enc` = encoder hidden size
- `A` = attention size

### Space Complexity

**Attention parameters:** `O(D_dec * A + D_enc * A + A)`

**Forward pass memory:**
- Encoder outputs: `B * T_src * D_enc`
- Decoder hidden: `B * D_dec`
- Attention scores: `B * T_src`
- Attention weights: `B * T_src`
- Context vector: `B * D_enc`

**Total:** `O(B * T_src * D_enc + B * D_dec)`

---

## Follow-Up Questions with Answers

### Q1: Compare Bahdanau (additive) attention with Luong (multiplicative/dot) attention.

**Answer:**

| Feature | Bahdanau (Additive) | Luong (Dot/General) |
|---------|-------------------|-------------------|
| Score function | `v^T tanh(W·s + U·h)` | `s^T · h` (dot) or `s^T · W · h` (general) |
| When context is used | To compute decoder state `s_t` | After computing decoder state `s_t` |
| Decoder alignment | At each step before RNN update | After RNN update |
| Computation | More expensive (tanh + multiple matmuls) | Cheaper (just dot product) |
| Parameters | More (W, U, v) | Fewer (could be just one matrix) |
| Performance | Generally similar on most tasks | Slightly faster |

**Key architectural difference:** Bahdanau uses the previous decoder state `s_{t-1}` to compute attention, while Luong uses the current decoder state `s_t` (computed without attention first).

### Q2: Explain teacher forcing. What is exposure bias and how can it be mitigated?

**Answer:**

**Teacher forcing:** During training, feed the ground truth target token `y_{t-1}` as input to the decoder at timestep `t`, regardless of what the model predicted.

**Exposure bias:** During inference, the model feeds its own predictions as input. If the model makes a mistake early, the error propagates — a distributional shift from the clean training inputs.

**Mitigation strategies:**
1. **Scheduled sampling:** Gradually reduce teacher forcing probability during training. Start with 100% teacher forcing, end with 0%.
   - `p_teacher = max(ε, 1 - epoch / total_epochs)`
2. **Professor forcing:** Use adversarial training to make the hidden state distributions of teacher-forced and free-running modes indistinguishable.
3. **DAGGER (Dataset Aggregation):** Collect training data from the model's own rollouts and train on the aggregated dataset.
4. **RL fine-tuning:** After supervised training, use reinforcement learning (e.g., REINFORCE) to optimize the evaluation metric directly.

### Q3: How does beam search work? Compare with greedy decoding.

**Answer:**

**Greedy decoding:** At each timestep, choose the token with the highest probability:
```
y_t = argmax P(y_t | y_{<t}, x)
```
- Simple and fast.
- Prone to local optima — an early suboptimal choice cannot be corrected.

**Beam search:** Maintain `k` candidate hypotheses:
1. Start with `<sos>` (score = 0 in log space).
2. At each step, for each of the `k` hypotheses, consider all `V` possible next tokens.
3. Score each new hypothesis: `score(new) = score(old) + log P(next | old)`.
4. Keep only the top `k` hypotheses by score.
5. Continue until all hypotheses hit `<eos>` or max length.

**Length normalization:** Longer sequences have lower scores (more probability multiplications). Normalize by length:
```
score_normalized = score / length^α  (α typically 0.6-1.0)
```

**Comparison:**
- Greedy: `O(T * V)` time, 1 hypothesis.
- Beam search: `O(T * k * V)` time, `k` hypotheses.
- Beam search (k=5-10) typically outperforms greedy by 1-3 BLEU points.

### Q4: What is the role of the context vector in attention-based Seq2Seq? How does it help with long sequences?

**Answer:** The context vector `c_t` provides a direct connection from the decoder to the source, allowing the decoder to "look at" relevant parts of the source at each generation step.

**Benefits for long sequences:**
1. **Avoids information bottleneck:** The encoder no longer needs to compress the entire source into one fixed-size vector.
2. **Provides aligned information:** Each decoder step can focus on the most relevant source positions.
3. **Shortcuts gradient path:** Gradients flow directly from decoder to encoder through attention weights, reducing vanishing gradient issues.

**The attention distribution visualizes alignment:**
- In translation: "Je" attends to "I", "suis" attends to "am", etc.
- In summarization: Each summary word attends to relevant source words.

### Q5: What is the difference between global and local attention?

**Answer:**

**Global attention (Bahdanau/Luong):**
- Attends to all source positions at every decoder timestep.
- `O(T_src)` computation per step.
- Computationally expensive for very long sequences.
- Works well for most tasks.

**Local attention (Luong et al., 2015):**
- Attends to only a subset of source positions around a predicted alignment point.
- First predicts an alignment position `p_t` for each decoder step.
- Then computes attention over a window `[p_t - D, p_t + D]`.
- `O(2D)` computation per step (constant window size).
- Hybrid of soft (global) and hard (discrete) attention.

**When to use local:**
- Very long source sequences (document-level tasks).
- When computational budget is limited.
- Monotonic alignment tasks (speech recognition).

---

## Test Cases

### Test Case 1: Single Batch, Basic Attention

```java
void testBasicAttention() {
    int batchSize = 1;
    int srcLength = 4;
    int decHiddenSize = 8;
    int encHiddenSize = 6;
    int attnSize = 10;

    Attention attn = new Attention(decHiddenSize, encHiddenSize, attnSize);

    double[][] decHidden = new double[batchSize][decHiddenSize];
    Arrays.fill(decHidden[0], 0.5);

    double[][] encOutputs = new double[batchSize][srcLength][encHiddenSize];
    for (int j = 0; j < srcLength; j++) {
        Arrays.fill(encOutputs[0][j], j * 0.2);
    }

    double[][][] result = attn.forward(decHidden, encOutputs, null);
    double[][] context = result[0];
    double[][] weights = result[1];

    assert context.length == batchSize : "Batch dimension preserved";
    assert context[0].length == encHiddenSize : "Context has encoder hidden size";
    assert weights.length == batchSize : "Weights batch dimension";
    assert weights[0].length == srcLength : "Weights source length";

    // Weights should sum to 1
    double sum = 0;
    for (int j = 0; j < srcLength; j++) {
        sum += weights[0][j];
    }
    assert Math.abs(sum - 1.0) < 1e-6 : "Attention weights should sum to 1, got " + sum;
}
```

### Test Case 2: Masked Attention

```java
void testMaskedAttention() {
    Attention attn = new Attention(8, 6, 10);

    double[][] decHidden = new double[1][8];
    Arrays.fill(decHidden[0], 0.5);

    double[][] encOutputs = new double[1][5][6];
    for (int j = 0; j < 5; j++) {
        Arrays.fill(encOutputs[0][j], j * 0.1);
    }

    // Mask out last 2 positions (padding)
    double[][] mask = new double[1][5];
    Arrays.fill(mask[0], 1.0);
    mask[0][3] = 0;
    mask[0][4] = 0;

    double[][][] result = attn.forward(decHidden, encOutputs, mask);
    double[][] weights = result[1];

    // Masked positions should have near-zero weight
    assert weights[0][3] < 0.01 : "Masked position 3 should have ~0 weight, got " + weights[0][3];
    assert weights[0][4] < 0.01 : "Masked position 4 should have ~0 weight, got " + weights[0][4];

    // Attention should sum to 1
    double sum = 0;
    for (int j = 0; j < 5; j++) sum += weights[0][j];
    assert Math.abs(sum - 1.0) < 1e-6 : "Masked weights should still sum to 1";
}
```

### Test Case 3: Context Vector Alignment

```java
void testContextVectorCorrectness() {
    Attention attn = new Attention(8, 6, 10);

    double[][] decHidden = new double[1][8];
    Arrays.fill(decHidden[0], 0.1);

    double[][] encOutputs = new double[1][3][6];
    encOutputs[0][0] = new double[]{1, 0, 0, 0, 0, 0};
    encOutputs[0][1] = new double[]{0, 1, 0, 0, 0, 0};
    encOutputs[0][2] = new double[]{0, 0, 1, 0, 0, 0};

    double[][][] result = attn.forward(decHidden, encOutputs, null);
    double[][] context = result[0];
    double[][] weights = result[1];

    // Context vector should be convex combination of encoder outputs
    for (int e = 0; e < 6; e++) {
        double expected = weights[0][0] * encOutputs[0][0][e]
                        + weights[0][1] * encOutputs[0][1][e]
                        + weights[0][2] * encOutputs[0][2][e];
        assert Math.abs(context[0][e] - expected) < 1e-10 :
            "Context[" + e + "] mismatch: expected " + expected + ", got " + context[0][e];
    }
}
```

### Test Case 4: Multi-Batch Attention

```java
void testMultiBatch() {
    int batchSize = 3;
    Attention attn = new Attention(8, 6, 10);

    double[][] decHidden = new double[batchSize][8];
    double[][] encOutputs = new double[batchSize][5][6];

    for (int b = 0; b < batchSize; b++) {
        Arrays.fill(decHidden[b], 0.1 * (b + 1));
        for (int j = 0; j < 5; j++) {
            Arrays.fill(encOutputs[b][j], 0.05 * (b * 5 + j));
        }
    }

    double[][][] result = attn.forward(decHidden, encOutputs, null);
    double[][] weights = result[1];

    // Each batch item's weights should sum to 1
    for (int b = 0; b < batchSize; b++) {
        double sum = 0;
        for (int j = 0; j < 5; j++) {
            sum += weights[b][j];
        }
        assert Math.abs(sum - 1.0) < 1e-6 :
            "Batch " + b + " weights sum to " + sum;
    }

    // Different batches should have different distributions
    boolean allSame = true;
    for (int j = 0; j < 5; j++) {
        if (Math.abs(weights[0][j] - weights[1][j]) > 1e-6) {
            allSame = false;
            break;
        }
    }
    assert !allSame : "Batches with different inputs should have different attention";
}
```

### Test Case 5: Softmax Numerical Stability

```java
void testSoftmaxStability() {
    Attention attn = new Attention(8, 6, 10);

    // Extreme values to test softmax stability
    double[][] decHidden = new double[1][8];
    Arrays.fill(decHidden[0], 100.0);

    double[][] encOutputs = new double[1][3][6];
    Arrays.fill(encOutputs[0][0], 100.0);
    Arrays.fill(encOutputs[0][1], -100.0);
    Arrays.fill(encOutputs[0][2], 0);

    double[][][] result = attn.forward(decHidden, encOutputs, null);
    double[][] weights = result[1];

    // Should not have NaN
    for (int j = 0; j < 3; j++) {
        assert !Double.isNaN(weights[0][j]) : "NaN in weight at position " + j;
    }

    // Sum to 1
    double sum = 0;
    for (int j = 0; j < 3; j++) sum += weights[0][j];
    assert Math.abs(sum - 1.0) < 1e-6 : "Weights should sum to 1";
}
```

### Test Case 6: Decoder with Teacher Forcing

```java
void testDecoderTeacherForcing() {
    int vocabSize = 50;
    int embedSize = 16;
    int hiddenSize = 32;
    int encHiddenSize = 24;
    int attnSize = 20;
    int batchSize = 2;
    int srcLen = 10;
    int tgtLen = 8;

    AttentionDecoder decoder = new AttentionDecoder(
        vocabSize, embedSize, hiddenSize, encHiddenSize, attnSize
    );

    int[][] targetTokens = new int[batchSize][tgtLen];
    for (int b = 0; b < batchSize; b++) {
        targetTokens[b][0] = 0; // <sos>
        for (int t = 1; t < tgtLen; t++) {
            targetTokens[b][t] = (int)(Math.random() * (vocabSize - 1)) + 1;
        }
    }

    double[][] encoderOutputs = new double[batchSize][srcLen][encHiddenSize];
    double[][] srcMask = new double[batchSize][srcLen];
    for (int b = 0; b < batchSize; b++) {
        Arrays.fill(srcMask[b], 1.0);
        for (int j = 0; j < srcLen; j++) {
            Arrays.fill(encoderOutputs[b][j], 0.1);
        }
    }

    double[][] outputLogits = decoder.forwardSequence(
        targetTokens, encoderOutputs, srcMask
    );

    int expectedRows = batchSize * tgtLen;
    assert outputLogits.length == expectedRows :
        "Expected " + expectedRows + " output rows, got " + outputLogits.length;
    assert outputLogits[0].length == vocabSize :
        "Output logits should have vocab size dimension";
}
```

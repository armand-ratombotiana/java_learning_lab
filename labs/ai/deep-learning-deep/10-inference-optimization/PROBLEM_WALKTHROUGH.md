# PROBLEM WALKTHROUGH: Speculative Decoding

## Problem Statement

**Difficulty:** Hard | **Category:** Inference Optimization | **Estimated Time:** 90 minutes

Implement a simplified version of speculative decoding, a technique that accelerates autoregressive generation by using a smaller "draft" model to propose candidate tokens that are then verified in parallel by a larger "target" model. Your `SpeculativeDecoder` class must implement the draft-verify-reject framework, including rejection sampling for guaranteed distributional correctness.

**Input:**
- `draftModel`: A fast, approximate model that generates token proposals quickly (simulated by a smaller vocabulary distribution predictor).
- `targetModel`: The large, high-quality model whose distribution we want to sample from.
- `promptTokens`: An array of `int` representing the initial input tokens (context).
- `maxNewTokens`: Maximum number of tokens to generate.
- `gamma` (`γ`): The number of candidate tokens to propose per speculation step (typically 3-10).
- `temperature`: Sampling temperature (controls randomness).

**Output:**
- `generatedTokens`: An array of `int` representing the complete generated sequence (original prompt + new tokens).
- `acceptanceStats`: Statistics about how many tokens were accepted per speculation round.

**Constraints:**
- Implement the standard rejection sampling scheme from Leviathan et al. (2022) and Chen et al. (2023).
- The draft model proposes `γ` tokens autoregressively.
- The target model verifies all `γ` tokens in parallel with a single forward pass.
- Implement rejection sampling: accept/reject each proposed token based on the ratio of target to draft probability.
- If a token is rejected, resample from a modified distribution.
- The output must be exactly from the target model's distribution (no approximation).

**Evaluation Criteria:**
- Correct rejection sampling: the output distribution matches the target model exactly.
- Speedup: verify that `γ > 1` tokens are accepted on average (depends on draft quality).
- Correct handling of edge cases: end-of-sequence, temperature, variable-length generation.
- The acceptance rate (average number of accepted tokens per speculation round).

---

## Step-by-Step Solution Walkthrough

### 1. The Core Idea

Speculative decoding accelerates inference by using a cheap draft model `M_draft` to propose `γ` tokens, then using the expensive target model `M_target` to verify all `γ` at once.

**Why this works:**
- The draft model runs autoregressively for `γ` steps — each step is fast (small model).
- The target model verifies all `γ` tokens in a single parallel forward pass (like training, not sequential).
- With a good draft model (e.g., the same architecture but smaller), most tokens are accepted.
- The expected speedup is roughly `γ / (1 + γ * (cost_draft / cost_target))`.

**Guarantee:** The rejection sampling scheme ensures the output distribution is *exactly* the same as sampling from the target model directly. This is not an approximation — it's a mathematically exact transformation.

### 2. The Algorithm

```
function speculativeDecoding(prompt, targetModel, draftModel, γ, T):
    // Phase 1: Draft (autoregressive, γ steps)
    for t = 1 to γ:
        x_t ~ M_draft(x | prefix, x_1, ..., x_{t-1})

    // Phase 2: Verify (parallel forward pass)
    // Get target model probabilities for all positions
    q(x | prefix) for original prompt position
    q(x | prefix, x_1) for position after first draft token
    ...
    q(x | prefix, x_1, ..., x_γ) for final position

    // Also get draft model probabilities for the proposed tokens
    p(x_t | prefix, x_1, ..., x_{t-1}) for each draft token

    // Phase 3: Rejection sampling
    for t = 1 to γ:
        r = q(x_t | ...) / p(x_t | ...)  // acceptance ratio
        if r >= 1:
            accept x_t
        else:
            accept x_t with probability r
            if rejected:
                // Resample from (q(x) - p(x))_+ / Z
                sample x from max(0, q(x) - p(x)) / Z
                break

    // Continue from the last accepted token
    return to Phase 1 with the updated prefix
```

### 3. The Acceptance Criterion

The key mathematical insight: for each proposed token `x` with draft probability `p(x)` and target probability `q(x)`:

- If `q(x) >= p(x)`: always accept. The "excess probability" `q(x) - p(x)` is allocated to rejection resampling.
- If `q(x) < p(x)`: accept with probability `q(x) / p(x)`.

This is analogous to the "accept-reject" sampling method in statistics.

**Resampling distribution after rejection:**

```
p_resample(x) = max(0, q(x) - p(x)) / Z
Z = Σ_x max(0, q(x) - p(x))
```

This ensures the overall distribution of accepted + resampled tokens matches `q` exactly.

### 4. Proof of Correctness

For a single token: the probability that a specific token `x` is finally output is:

```
P(output = x) = P(draft = x) * P(accept | draft = x) + P(reject) * P_resample(x)
              = p(x) * min(1, q(x)/p(x)) + (1 - Σ_y p(y) * min(1, q(y)/p(y))) * max(0, q(x)-p(x))/Z
```

If `q(x) >= p(x)`:
```
= p(x) * 1 + (1 - Σ_{y: q(y) < p(y)} q(y) - Σ_{y: q(y) >= p(y)} p(y)) * (q(x)-p(x)) / Z
= p(x) + (1 - (1 - Σ_{y: q(y) >= p(y)} (p(y)-q(y)))) * (q(x)-p(x)) / Z
= p(x) + (Σ_{y: q(y) >= p(y)} (p(y)-q(y))) * (q(x)-p(x)) / Z
= p(x) + Z * (q(x)-p(x)) / Z
= q(x)
```

If `q(x) < p(x)`, similar algebra shows `P(output = x) = q(x)`. Therefore the output distribution is exactly `q`.

### 5. Expected Acceptance Rate

The expected number of accepted tokens per speculation round is:

```
E[N_accepted] = Σ_{t=1}^{γ} P(accept at position t)
P(accept at position t) = E[min(1, q(x_t) / p(x_t))]
                         = 1 - E[max(0, 1 - q(x_t) / p(x_t))]  // higher when p ≈ q
                         = 1 - TV(p, q)  // 1 - total variation distance between p and q
```

When the draft model perfectly matches the target: `E[N_accepted] = γ` (all tokens accepted).
When the draft model is random: `E[N_accepted] ≈ 1` (no amortization).

For a well-aligned draft model (e.g., same architecture, fewer parameters), the acceptance rate is typically 70-90%.

---

## Java Implementation

```java
package lab10.inference;

import java.util.Arrays;
import java.util.Random;

/**
 * Simplified speculative decoding with draft-verify-rejection sampling.
 * <p>
 * Uses a small draft model to propose γ candidate tokens, then verifies
 * them in parallel with a large target model. The rejection sampling
 * scheme guarantees the output distribution matches the target model exactly.
 */
public class SpeculativeDecoder {

    // Simulated models — in practice these would be neural networks
    private final SimulatedModel draftModel;
    private final SimulatedModel targetModel;
    private final int vocabSize;
    private final double temperature;
    private final Random rng;

    /**
     * Constructs a speculative decoder.
     *
     * @param draftModel  the fast draft model (smaller, cheaper)
     * @param targetModel the high-quality target model
     * @param vocabSize   size of the vocabulary
     * @param temperature sampling temperature (&gt; 0)
     */
    public SpeculativeDecoder(SimulatedModel draftModel, SimulatedModel targetModel,
                               int vocabSize, double temperature) {
        this.draftModel = draftModel;
        this.targetModel = targetModel;
        this.vocabSize = vocabSize;
        this.temperature = temperature;
        this.rng = new Random(42);
    }

    /**
     * Runs speculative decoding to generate tokens.
     *
     * @param promptTokens  initial context tokens (array of token IDs)
     * @param maxNewTokens  maximum number of new tokens to generate
     * @param gamma         number of proposals per speculation round
     * @return generated token sequence (prompt + new tokens)
     */
    public int[] generate(int[] promptTokens, int maxNewTokens, int gamma) {
        // Result buffer: start with prompt
        int[] result = Arrays.copyOf(promptTokens, promptTokens.length + maxNewTokens);
        int currentLen = promptTokens.length;
        int totalGenerated = 0;

        int totalProposed = 0;
        int totalAccepted = 0;

        while (totalGenerated < maxNewTokens) {
            // Phase 1: Draft — generate γ proposals autoregressively
            int[] prefix = Arrays.copyOf(result, currentLen);
            int[] draftTokens = new int[gamma];
            double[][] draftProbs = new double[gamma][];

            for (int t = 0; t < gamma; t++) {
                int[] seq = Arrays.copyOf(prefix, prefix.length + t);
                // Copy previously generated draft tokens
                for (int k = 0; k < t; k++) {
                    seq[prefix.length + k] = draftTokens[k];
                }
                draftProbs[t] = draftModel.getProbabilities(seq, temperature);
                draftTokens[t] = sampleFromDistribution(draftProbs[t]);
                totalProposed++;
            }

            // Phase 2: Verify — get target model probabilities
            // For position after prompt and each draft prefix
            double[][] targetProbs = new double[gamma + 1][];
            for (int t = 0; t <= gamma; t++) {
                int[] seq = Arrays.copyOf(prefix, prefix.length + t);
                for (int k = 0; k < t; k++) {
                    seq[prefix.length + k] = draftTokens[k];
                }
                targetProbs[t] = targetModel.getProbabilities(seq, temperature);
            }

            // Phase 3: Rejection sampling
            int accepted = 0;
            boolean rejected = false;

            for (int t = 0; t < gamma; t++) {
                int token = draftTokens[t];

                // Draft prob for this specific token
                double p = draftProbs[t][token];
                // Target prob for this specific token
                double q = targetProbs[t][token];

                // Acceptance criterion
                double ratio = q / p;
                double r = rng.nextDouble();

                if (p <= 0) {
                    // Draft would never propose this, but we handle it
                    rejected = true;
                    // No resampling needed — just break
                    break;
                }

                if (ratio >= 1.0 || r <= ratio) {
                    // Accept
                    result[currentLen] = token;
                    currentLen++;
                    totalGenerated++;
                    accepted++;

                    if (totalGenerated >= maxNewTokens) {
                        break;
                    }
                } else {
                    // Reject: resample from (target - draft)+
                    int resampledToken = resampleFromAdjusted(targetProbs[t], draftProbs[t]);
                    result[currentLen] = resampledToken;
                    currentLen++;
                    totalGenerated++;
                    rejected = true;
                    break;
                }
            }

            if (!rejected) {
                // All γ tokens accepted — sample one more from target
                int extraToken = sampleFromDistribution(targetProbs[gamma]);
                result[currentLen] = extraToken;
                currentLen++;
                totalGenerated++;
            }

            totalAccepted += accepted;
        }

        // Trim to actual length
        return Arrays.copyOf(result, currentLen);
    }

    /**
     * Samples a token from a probability distribution.
     */
    private int sampleFromDistribution(double[] probs) {
        double r = rng.nextDouble();
        double cumulative = 0;
        for (int i = 0; i < probs.length; i++) {
            cumulative += probs[i];
            if (r < cumulative) {
                return i;
            }
        }
        return probs.length - 1; // fallback
    }

    /**
     * Resamples from max(0, q - p) / Z distribution after rejection.
     */
    private int resampleFromAdjusted(double[] targetProbs, double[] draftProbs) {
        double[] adjusted = new double[vocabSize];
        double sum = 0;

        for (int i = 0; i < vocabSize; i++) {
            adjusted[i] = Math.max(0, targetProbs[i] - draftProbs[i]);
            sum += adjusted[i];
        }

        if (sum <= 0) {
            // Fallback: sample from target distribution directly
            return sampleFromDistribution(targetProbs);
        }

        // Normalize
        for (int i = 0; i < vocabSize; i++) {
            adjusted[i] /= sum;
        }

        return sampleFromDistribution(adjusted);
    }

    /**
     * Gets acceptance statistics from the last generation run.
     *
     * @param totalProposed number of tokens proposed
     * @param totalAccepted number of tokens accepted
     * @return acceptance rate
     */
    public double getAcceptanceRate(int totalProposed, int totalAccepted) {
        return (double) totalAccepted / totalProposed;
    }

    /**
     * Simulated model interface — represents either the draft or target model.
     */
    public interface SimulatedModel {
        /**
         * Returns the probability distribution over the vocabulary for the given prefix.
         *
         * @param tokens      the prefix token sequence
         * @param temperature sampling temperature
         * @return probability distribution (vocabSize length array)
         */
        double[] getProbabilities(int[] tokens, double temperature);
    }

    /**
     * A simple simulated model for testing. Uses a configurable probability
     * distribution shape to simulate different draft-target alignments.
     */
    public static class SimpleSimulatedModel implements SimulatedModel {
        private final int vocabSize;
        private final double sharpness; // higher = more peaked distribution

        /**
         * @param vocabSize vocabulary size
         * @param sharpness  controls how peaked the distribution is (1 = uniform, 5+ = very peaked)
         */
        public SimpleSimulatedModel(int vocabSize, double sharpness) {
            this.vocabSize = vocabSize;
            this.sharpness = sharpness;
        }

        @Override
        public double[] getProbabilities(int[] tokens, double temperature) {
            double[] logits = new double[vocabSize];

            // Generate a deterministic but position-dependent distribution
            int hash = Arrays.hashCode(tokens);
            for (int i = 0; i < vocabSize; i++) {
                logits[i] = Math.sin(hash * 0.1 + i * 0.3) * sharpness;
            }

            // Apply temperature
            for (int i = 0; i < vocabSize; i++) {
                logits[i] /= temperature;
            }

            // Softmax
            double max = Double.NEGATIVE_INFINITY;
            for (double v : logits) if (v > max) max = v;
            double sum = 0;
            double[] probs = new double[vocabSize];
            for (int i = 0; i < vocabSize; i++) {
                probs[i] = Math.exp(logits[i] - max);
                sum += probs[i];
            }
            for (int i = 0; i < vocabSize; i++) {
                probs[i] /= sum;
            }

            return probs;
        }
    }
}
```

**Example Usage:**

```java
package lab10.inference;

import java.util.Arrays;

public class SpeculativeDecodingExample {
    public static void main(String[] args) {
        int vocabSize = 1000;
        int promptLen = 10;

        // Create models: draft is less sharp (more uniform), target is very sharp
        // They share the same basic distribution shape (same hash-based logits),
        // so the draft is "aligned" with the target but noisier.
        SpeculativeDecoder.SimpleSimulatedModel draftModel =
            new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 2.0);
        SpeculativeDecoder.SimpleSimulatedModel targetModel =
            new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 5.0);

        SpeculativeDecoder decoder = new SpeculativeDecoder(
            draftModel, targetModel, vocabSize, 1.0);

        // Create a prompt
        int[] prompt = new int[promptLen];
        for (int i = 0; i < promptLen; i++) {
            prompt[i] = i % vocabSize;
        }

        int gamma = 4;
        int maxNewTokens = 20;

        long start = System.nanoTime();
        int[] generated = decoder.generate(prompt, maxNewTokens, gamma);
        long end = System.nanoTime();

        System.out.println("Prompt length: " + promptLen);
        System.out.println("Generated length: " + generated.length);
        System.out.println("Generated tokens: " + Arrays.toString(
            Arrays.copyOfRange(generated, promptLen, generated.length)));
        System.out.println("Time: " + (end - start) / 1e6 + " ms");
    }
}
```

---

## Complexity Analysis

### Time Complexity

Let:
- `T` = new tokens generated
- `γ` = number of draft proposals per round
- `C_draft` = cost of one draft model forward pass
- `C_target` = cost of one target model forward pass
- `α` = acceptance rate (fraction of draft tokens accepted)

**Without speculation:** `T * C_target` sequential forward passes.

**With speculation per round:**
1. Draft phase: `γ * C_draft` sequential passes (each depends on previous).
2. Verify phase: `1 * C_target` parallel pass (all `γ` positions at once).
3. Expected accepted tokens: `γ * α + 1` (accept all γ plus one extra from target).

**Number of rounds:** `T / (γ * α + 1)`

**Total cost:** `(T / (γ * α + 1)) * (γ * C_draft + C_target)`

**Speedup:**
```
Speedup = (T * C_target) / (T / (γ*α + 1) * (γ*C_draft + C_target))
        = (γ*α + 1) * C_target / (γ*C_draft + C_target)
        ≈ (γ*α + 1) / (γ * C_draft / C_target + 1)
```

When `C_draft << C_target` (draft is much cheaper):
```
Speedup ≈ γ*α + 1
```

When `α ≈ 1` (draft is perfect):
```
Speedup ≈ γ + 1
```

### Memory Complexity

**Draft model cache:** `L_draft * h_draft * d_k_draft * γ` — small.
**Target model KV cache:** Grows with total sequence length `(prompt + generated)`.
**Verify batch:** Target processes `γ + 1` positions in one batch — `O((γ + 1) * d_model)`.

### Latency vs Throughput Trade-off

| Aspect | Without Speculation | With Speculation (γ=5) |
|--------|-------------------|----------------------|
| Latency per token | `C_target` | `(γ*C_draft + C_target) / (γ*α + 1)` |
| Batch throughput | Low (sequential) | Higher (parallel verify) |
| Memory bandwidth | Sequential K/V load | Batched K/V load |

**Key insight:** Speculation improves throughput more than latency. The main benefit comes from batching the verification step, which utilizes hardware parallelism better than sequential decoding.

---

## Follow-Up Questions with Answers

### Q1: What guarantees does speculative decoding provide about output quality?

**Answer:** Speculative decoding with the standard rejection sampling scheme provides an **exact guarantee**: the output distribution is identical to sampling from the target model directly. This is not an approximation — it's mathematically exact.

**Why it's exact:** The rejection sampling procedure (accept with probability `min(1, q/p)`, resample from adjusted distribution on rejection) is a standard Monte Carlo method for sampling from a distribution `q` when you have proposals from `p`. The proof shows:

```
P_final(x) = p(x) * min(1, q(x)/p(x)) + P(reject) * max(0, q(x)-p(x))/Z = q(x)
```

**Practical implication:** You can use speculative decoding as a drop-in replacement for standard autoregressive decoding with zero quality degradation. This is the main advantage over other acceleration methods like:
- **Quantization**: Approximate (loses precision).
- **Pruning**: Approximate (may lose accuracy).
- **Knowledge distillation**: Approximate (student never perfectly matches teacher).

### Q2: How does the draft model quality affect the speedup? What draft model would you choose?

**Answer:** The speedup is roughly proportional to `(γ * α + 1)`, where `α` is the acceptance rate.

**Relationship between draft quality and acceptance rate:**
- `α = 1 - TV(p, q)` where `TV` is the total variation distance between draft and target distributions.
- A good draft model has `TV ≈ 0.1-0.3`, giving `α ≈ 0.7-0.9`.
- A random draft has `TV ≈ 0.5-0.9`, giving `α ≈ 0.1-0.5`.

**Choosing a draft model:**
1. **Same architecture, fewer parameters** (e.g., Llama 2 7B as draft for 70B target): Good alignment, ~5-10x cheaper.
2. **Same architecture, fewer layers** (e.g., 12-layer draft for 24-layer target): Shares embedding/vocabulary.
3. **Differently quantized version** (e.g., INT8 draft for FP16 target): Nearly identical distributions.
4. **N-gram or small LSTM**: Very cheap, but lower acceptance rate.
5. **Self-speculation**: Use the same model at earlier layers (Early Exiting) as draft.

**Optimal γ:** Depends on `C_draft / C_target` ratio and acceptance rate. Typically `γ = 3-10`.
- Higher `γ`: More parallelization, but lower marginal acceptance rate (later tokens are less likely to be accepted).
- Lower `γ`: Higher per-token acceptance rate, less parallelization.

### Q3: Explain the "warm-up" or "speculation depth" problem. Why might longer speculations become less effective?

**Answer:** The acceptance rate decreases with speculation depth. The first draft token is conditioned on the known prefix, so its distribution is relatively accurate. The second draft token is conditioned on the first draft token (which may be wrong), making it less likely to match the target distribution.

**Causes of decreasing acceptance:**
1. **Error compounding:** Draft errors at position `t` change the conditioning context for position `t+1`, pushing the draft distribution further from the target.
2. **Distribution shift:** The draft model's hidden states drift from the target model's hidden states.

**Empirical pattern:**
```
Position 1: α ≈ 0.85
Position 2: α ≈ 0.80
Position 3: α ≈ 0.75
Position 4: α ≈ 0.70
Position 5: α ≈ 0.65
```

**Impact on `γ` choice:**
Increasing `γ` adds more tokens with lower acceptance probability. The expected number of accepted tokens is:
```
E[accepted] = Σ_{t=1}^{γ} Π_{k=1}^{t} α_k
```

For `α = [0.85, 0.80, 0.75, 0.70, 0.65]`:
```
γ=1: 0.85
γ=2: 0.85 + 0.85*0.80 = 1.53
γ=3: 1.53 + 0.85*0.80*0.75 = 2.04
γ=4: 2.04 + 0.85*0.80*0.75*0.70 = 2.40
γ=5: 2.40 + 0.85*0.80*0.75*0.70*0.65 = 2.63
```

Returns diminish with `γ`. The optimal `γ` balances the cost of drafting `γ` tokens against the diminishing returns.

### Q4: How does speculative decoding interact with beam search and KV cache?

**Answer:**

**KV cache interaction:**
1. **Draft phase:** The draft model maintains its own KV cache. Each draft step appends to this cache.
2. **Verify phase:** The target model's KV cache is updated. Two strategies:
   - **Append-then-verify:** Append the draft tokens to the target's KV cache, compute verification in one forward pass. If rejected, truncate the cache back to the last accepted token.
   - **Verify-then-append:** Compute target probabilities for all positions without modifying the cache, then only append accepted tokens.
3. **Cache rollback:** On rejection, the target model's KV cache must be restored to the state before the rejected token. This requires either:
   - Copying the cache before speculation (expensive).
   - Keeping a "checkpoint" pointer and truncating on rejection (efficient).

**Beam search interaction:**
- Speculative decoding is designed for sampling, not beam search.
- For beam search, you need "speculative beam search" (Spectr, 2023) or "parallel decoding with beam constraints."
- The rejection sampling guarantee only holds for sampling — beam search modifies the distribution.

**Practical implementation:** Most efficient speculative decoding systems (e.g., SpecInfer, Medusa) use tree-based speculation to handle multiple possible continuations from the draft model.

### Q5: What are the main failure modes of speculative decoding?

**Answer:**

1. **Poor draft alignment:** If the draft model's distribution diverges significantly from the target, acceptance rates drop to near 1 (i.e., almost always rejecting). In this case, speculation adds overhead without benefit. **Mitigation:** Use a well-aligned draft (same architecture, smaller size).

2. **Low temperature:** At temperature → 0 (greedy decoding), both draft and target become deterministic. Acceptance is 100% if they agree, 0% if they disagree. **Mitigation:** At very low temperatures, the draft may always propose the wrong token, making speculation useless.

3. **High temperature:** At very high temperatures, distributions become more uniform. Acceptance rate approaches 1 (because uniform distributions are easier to match), but the quality of generation degrades.

4. **Speculation on already-perfect drafts:** If the draft model is already as good as the target, why use the target? This happens when the target's advantage is only in rare, hard cases. **Mitigation:** Only speculate on "easy" tokens; use target for "hard" tokens.

5. **Batched serving complexity:** In continuous batching, different requests are at different stages of speculation. Implementing speculative decoding efficiently in a batching server is significantly more complex than standard decoding.

---

## Test Cases

### Test Case 1: Single Token Generation (γ=1)

```java
void testSingleTokenGeneration() {
    int vocabSize = 100;
    SpeculativeDecoder.SimpleSimulatedModel draft =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 3.0);
    SpeculativeDecoder.SimpleSimulatedModel target =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 5.0);

    SpeculativeDecoder decoder = new SpeculativeDecoder(draft, target, vocabSize, 1.0);

    int[] prompt = {0, 1, 2, 3, 4};
    int[] result = decoder.generate(prompt, 1, 1);

    assert result.length == prompt.length + 1 : "Should generate 1 token";
    System.out.println("Generated token: " + result[prompt.length]);
}
```

### Test Case 2: Multiple Rounds

```java
void testMultipleRounds() {
    int vocabSize = 100;
    SpeculativeDecoder.SimpleSimulatedModel draft =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 3.0);
    SpeculativeDecoder.SimpleSimulatedModel target =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 5.0);

    SpeculativeDecoder decoder = new SpeculativeDecoder(draft, target, vocabSize, 1.0);

    int[] prompt = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    int[] result = decoder.generate(prompt, 50, 4);

    assert result.length >= prompt.length : "Should at least have prompt tokens";
    assert result.length <= prompt.length + 50 : "Should not exceed max tokens";
    System.out.println("Generated " + (result.length - prompt.length) + " tokens");
}
```

### Test Case 3: Uniform Draft (Acceptance Rate Test with Random Draft)

```java
void testUniformDraft() {
    int vocabSize = 1000;

    // Draft is nearly uniform
    SpeculativeDecoder.SimpleSimulatedModel draft =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 0.1);
    // Target is very peaked
    SpeculativeDecoder.SimpleSimulatedModel target =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 10.0);

    SpeculativeDecoder decoder = new SpeculativeDecoder(draft, target, vocabSize, 1.0);

    int[] prompt = new int[5];
    int[] result = decoder.generate(prompt, 20, 4);

    // With a uniform draft and peaked target, acceptance rate should be low
    // but generation should still work
    assert result.length > prompt.length : "Should generate some tokens";
    System.out.println("Generated " + (result.length - prompt.length) + " with uniform draft");
}
```

### Test Case 4: Perfect Draft (Same Distribution)

```java
void testPerfectDraft() {
    int vocabSize = 100;

    // Draft and target have same sharpness → same distribution
    SpeculativeDecoder.SimpleSimulatedModel draft =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 5.0);
    SpeculativeDecoder.SimpleSimulatedModel target =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 5.0);

    SpeculativeDecoder decoder = new SpeculativeDecoder(draft, target, vocabSize, 1.0);

    int[] prompt = {0, 1, 2, 3, 4};
    int[] result = decoder.generate(prompt, 10, 5);

    // With perfect draft (same distribution), acceptance rate should be ~100%
    assert result.length == prompt.length + 10 : "All tokens should be accepted with perfect draft";
}
```

### Test Case 5: Different Gamma Values

```java
void testDifferentGammaValues() {
    int vocabSize = 500;
    SpeculativeDecoder.SimpleSimulatedModel draft =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 2.0);
    SpeculativeDecoder.SimpleSimulatedModel target =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 6.0);

    int[] prompt = new int[10];
    int maxNew = 30;

    // Test with γ = 1 (no speculation)
    SpeculativeDecoder decoder1 = new SpeculativeDecoder(draft, target, vocabSize, 1.0);
    long start1 = System.nanoTime();
    int[] result1 = decoder1.generate(prompt, maxNew, 1);
    long time1 = System.nanoTime() - start1;

    // Test with γ = 5 (aggressive speculation)
    SpeculativeDecoder decoder5 = new SpeculativeDecoder(draft, target, vocabSize, 1.0);
    long start5 = System.nanoTime();
    int[] result5 = decoder5.generate(prompt, maxNew, 5);
    long time5 = System.nanoTime() - start5;

    // Test with γ = 10 (very aggressive)
    SpeculativeDecoder decoder10 = new SpeculativeDecoder(draft, target, vocabSize, 1.0);
    long start10 = System.nanoTime();
    int[] result10 = decoder10.generate(prompt, maxNew, 10);
    long time10 = System.nanoTime() - start10;

    System.out.println("Time γ=1: " + time1 / 1e6 + " ms");
    System.out.println("Time γ=5: " + time5 / 1e6 + " ms");
    System.out.println("Time γ=10: " + time10 / 1e6 + " ms");

    // All should generate approximately maxNew tokens
    assert Math.abs((result1.length - prompt.length) - maxNew) <= 1;
    assert Math.abs((result5.length - prompt.length) - maxNew) <= 1;
    assert Math.abs((result10.length - prompt.length) - maxNew) <= 1;
}
```

### Test Case 6: Temperature Effect

```java
void testTemperatureEffect() {
    int vocabSize = 200;
    SpeculativeDecoder.SimpleSimulatedModel draft =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 3.0);
    SpeculativeDecoder.SimpleSimulatedModel target =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 6.0);

    int[] prompt = new int[5];

    // Low temperature
    SpeculativeDecoder lowTemp = new SpeculativeDecoder(draft, target, vocabSize, 0.1);
    int[] resultCold = lowTemp.generate(prompt, 5, 3);

    // High temperature
    SpeculativeDecoder highTemp = new SpeculativeDecoder(draft, target, vocabSize, 2.0);
    int[] resultHot = highTemp.generate(prompt, 5, 3);

    assert resultCold.length == prompt.length + 5 :
        "Low temp should generate exactly maxNew tokens (close to deterministic)";
    assert resultHot.length == prompt.length + 5 ||
           resultHot.length == prompt.length + 5 :
        "High temp should also generate maxNew tokens";
}
```

### Test Case 7: Deterministic Output Comparison

```java
void testDeterministicComparison() {
    // At temperature → 0, the output should be deterministic
    int vocabSize = 100;
    SpeculativeDecoder.SimpleSimulatedModel draft =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 5.0);
    SpeculativeDecoder.SimpleSimulatedModel target =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 5.0);

    int[] prompt = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    // Run twice with same seed
    SpeculativeDecoder dec1 = new SpeculativeDecoder(draft, target, vocabSize, 0.01);
    int[] result1 = dec1.generate(prompt, 10, 4);

    // Re-seed by creating fresh decoder
    SpeculativeDecoder dec2 = new SpeculativeDecoder(draft, target, vocabSize, 0.01);
    int[] result2 = dec2.generate(prompt, 10, 4);

    assert Arrays.equals(result1, result2) :
        "Low temperature runs should be deterministic and identical";
}
```

### Test Case 8: Maximum New Tokens Constraint

```java
void testMaxNewTokensConstraint() {
    int vocabSize = 500;
    SpeculativeDecoder.SimpleSimulatedModel draft =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 3.0);
    SpeculativeDecoder.SimpleSimulatedModel target =
        new SpeculativeDecoder.SimpleSimulatedModel(vocabSize, 5.0);

    SpeculativeDecoder decoder = new SpeculativeDecoder(draft, target, vocabSize, 1.0);

    int[] prompt = {1, 2, 3};
    int maxNew = 15;

    int[] result = decoder.generate(prompt, maxNew, 5);

    int newTokens = result.length - prompt.length;
    assert newTokens <= maxNew :
        "Should not exceed maxNew tokens: " + newTokens + " > " + maxNew;
    assert newTokens >= maxNew - 1 :
        "Should generate close to maxNew tokens: " + newTokens + " < " + (maxNew - 1);
}
```

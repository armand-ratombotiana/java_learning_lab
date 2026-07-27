# PROBLEM WALKTHROUGH: KV Cache for Autoregressive Inference

## Problem Statement

**Difficulty:** Hard | **Category:** Inference Optimization | **Estimated Time:** 75 minutes

Implement a key-value (KV) cache for efficient autoregressive transformer decoding. During text generation, the decoder generates tokens one at a time, and each new token must attend to all previously generated tokens. Without caching, the attention computation for each new token would recompute key and value projections for all previous tokens — an `O(N² * d)` waste. Your `KVCache` class must store and reuse projected keys and values across decoding steps, handling both the prefill (initial prompt processing) and decode (token-by-token generation) phases.

**Input (per decoding step):**
- `newKeys`: A 3D array of shape `(batchSize, 1, d_k)` — the key projection of the newly generated token.
- `newValues`: A 3D array of shape `(batchSize, 1, d_v)` — the value projection of the newly generated token.
- `layerIndex` (optional): Index of the transformer layer (for multi-layer caching).

**Cache Operations:**
- `append(keys, values, layerIndex)`: Append new key-value pairs to the cache for a specific layer.
- `getKeys(layerIndex)`: Retrieve all cached keys for a specific layer.
- `getValues(layerIndex)`: Retrieve all cached values for a specific layer.
- `reset()`: Clear the cache (for a new generation sequence).

**Output (from the attention module using the cache):**
- Instead of computing `Attention(Q, K_all, V_all)` by projecting all positions each time, compute `Attention(Q_new, K_cached, V_cached)` where `K_cached` and `V_cached` already include all previous positions.

**Constraints:**
- Support multiple transformer layers independently (each layer has its own K/V cache).
- Support multiple beams or batch elements.
- Handle incremental attention: compute attention only for the new query token against all cached key-value pairs.
- During the prefill phase, process all prompt tokens at once and cache them.
- During the decode phase, process one token at a time and append to the cache.

**Evaluation Criteria:**
- Correctness: cached K/V values match the non-cached version exactly.
- The attention output using the cache should be identical to recomputing from scratch.
- Proper ordering: keys/values are stored in the order they are generated.
- Memory efficiency: cache size grows linearly with generated tokens.

---

## Step-by-Step Solution Walkthrough

### 1. The Problem: Redundant Computation in Autoregressive Decoding

In autoregressive generation, we produce tokens one at a time:

```
y_1 = decode(y_0)           // y_0 = <sos>
y_2 = decode(y_0, y_1)
y_3 = decode(y_0, y_1, y_2)
...
y_T = decode(y_0, ..., y_{T-1})
```

At step `t`, the decoder attends to all `t-1` previous tokens. The attention computation is:

```
Attention(Q_t, K_{1:t-1}, V_{1:t-1}) = softmax(Q_t * K_{1:t-1}^T / sqrt(d_k)) * V_{1:t-1}
```

**Without caching:** At each step, we:
1. Project ALL previous tokens through the key and value projection matrices: `O(t * d_model * d_k)`.
2. Compute attention between the new Q and all K, V: `O(t * d_k + t * d_v)`.

**With caching:** At each step, we:
1. Project ONLY the new token: `O(1 * d_model * d_k)`.
2. Append to cache: `O(1 * (d_k + d_v))`.
3. Compute attention between new Q and cached K, V: `O(t * d_k + t * d_v)`.

The savings grow linearly with `t`. Over an entire generation of `T` tokens:
- Without cache: `O(T³ * d)` (sum of t*2 from 1 to T).
- With cache: `O(T² * d)` (just the attention).

### 2. Prefill vs Decode Phase

**Prefill Phase:**
- Process the entire input prompt in one forward pass.
- All prompt tokens are processed in parallel (like training).
- Compute and cache the key and value projections for all prompt tokens.
- Time complexity: `O(promptLength * d²)` — dominated by projections, not attention.

**Decode Phase:**
- Generate tokens one at a time.
- For each new token: project to Q, K, V → append K, V to cache → compute attention with cached K, V.
- Time complexity per step: `O(d² + t * d)` where `t` is the current sequence length.
- Memory-bound: the main bottleneck is loading the growing K/V cache from memory.

### 3. Memory Footprint of KV Cache

For a single layer, batch size `B`, sequence length `N`, dimension `d_k`, number of heads `h`:

```
KV_cache_size = 2 * B * N * h * d_k * bytes_per_element
```

For a model with `L` layers:
```
Total_KV_cache = L * 2 * B * N * h * d_k
```

**Example:** Llama 2 7B:
- `L = 32`, `h = 32`, `d_k = 128`, `d_model = 4096`
- Per token, per layer: `2 * 32 * 128 = 8, 192` values = 32 KB (fp16)
- Per token, all layers: `32 * 32 KB = 1 MB`
- For 1024 tokens: `1 GB` of KV cache per sequence!

This is why KV cache management is critical for long-context inference.

### 4. Incremental Attention

With the KV cache, attention changes from:

```
// Full (parallel) attention — used in prefill and training
S = Q @ K^T    // (B, h, N, N)
P = softmax(S)
O = P @ V      // (B, h, N, d_v)
```

To:

```
// Incremental attention — used in decode
S_new = Q_new @ K_cached^T   // (B, h, 1, t)
P_new = softmax(S_new)
O_new = P_new @ V_cached     // (B, h, 1, d_v)
```

Where `Q_new` is the query for the newly generated token only.

### 5. Algorithm

```
class KVCache:
    cache: dict[int -> (tensor(K), tensor(V))]

    def append(K, V, layerIdx):
        if layerIdx not in cache:
            cache[layerIdx] = (K, V)   // first token
        else:
            cachedK, cachedV = cache[layerIdx]
            cache[layerIdx] = (concat(cachedK, K, dim=seq),
                               concat(cachedV, V, dim=seq))

    def getK(layerIdx): return cache[layerIdx][0]
    def getV(layerIdx): return cache[layerIdx][1]
    def reset(): cache = {}
    def size(layerIdx): return cache[layerIdx][0].shape[seq_dim]

// In the decoder:
def decode_step(token_embed, cache):
    Q = Q_proj(token_embed)          // (B, 1, d_model)
    K = K_proj(token_embed)          // (B, 1, d_k)
    V = V_proj(token_embed)          // (B, 1, d_v)

    cache.append(K, V, layerIdx)

    cachedK = cache.getK(layerIdx)   // (B, t, d_k)
    cachedV = cache.getV(layerIdx)   // (B, t, d_v)

    // Attention with cache
    attn_output = attention(Q, cachedK, cachedV)

    return attn_output
```

---

## Java Implementation

```java
package lab09.inference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Key-value (KV) cache for efficient autoregressive transformer decoding.
 * <p>
 * Stores key and value projections for previously generated tokens,
 * avoiding redundant recomputation during sequential token generation.
 * Supports multiple layers and batch elements.
 */
public class KVCache {

    // We store per-layer KV pairs
    // Layer index → [keys (B, seqLen, dk), values (B, seqLen, dv)]
    private final Map<Integer, CacheEntry> cache;
    private final int dk;
    private final int dv;
    private final int maxMemoryTokens; // safety limit

    private static class CacheEntry {
        double[][] keys;   // (B, seqLen, dk)
        double[][] values; // (B, seqLen, dv)
        int currentLength;

        CacheEntry(int batchSize, int maxTokens, int dk, int dv) {
            this.keys = new double[batchSize][maxTokens][dk];
            this.values = new double[batchSize][maxTokens][dv];
            this.currentLength = 0;
        }
    }

    /**
     * Constructs a KV cache.
     *
     * @param dk              dimension of keys
     * @param dv              dimension of values
     * @param maxMemoryTokens maximum number of tokens to cache (to bound memory)
     */
    public KVCache(int dk, int dv, int maxMemoryTokens) {
        this.dk = dk;
        this.dv = dv;
        this.maxMemoryTokens = maxMemoryTokens;
        this.cache = new HashMap<>();
    }

    /**
     * Constructs a KV cache with default max tokens (2048).
     */
    public KVCache(int dk, int dv) {
        this(dk, dv, 2048);
    }

    /**
     * Appends new key-value pairs to the cache for a specific layer.
     * Creates the cache entry if it doesn't exist yet.
     *
     * @param keys       new keys of shape (batchSize, 1, dk)
     * @param values     new values of shape (batchSize, 1, dv)
     * @param layerIndex the transformer layer index
     */
    public void append(double[][] keys, double[][] values, int layerIndex) {
        int batchSize = keys.length;
        int seqLen = keys[0].length; // should be 1 in decode mode

        CacheEntry entry = cache.get(layerIndex);
        if (entry == null) {
            // Estimate initial capacity based on maxMemoryTokens
            entry = new CacheEntry(batchSize, maxMemoryTokens, dk, dv);
            cache.put(layerIndex, entry);
        }

        // Check dimensions
        if (entry.keys.length != batchSize) {
            throw new IllegalArgumentException("Batch size mismatch");
        }

        int writePos = entry.currentLength;
        if (writePos + seqLen > maxMemoryTokens) {
            throw new RuntimeException("KV cache overflow: " + (writePos + seqLen)
                + " > " + maxMemoryTokens);
        }

        // Copy keys and values into the cache
        for (int b = 0; b < batchSize; b++) {
            for (int s = 0; s < seqLen; s++) {
                for (int d = 0; d < dk; d++) {
                    entry.keys[b][writePos + s][d] = keys[b][s][d];
                }
                for (int d = 0; d < dv; d++) {
                    entry.values[b][writePos + s][d] = values[b][s][d];
                }
            }
        }
        entry.currentLength += seqLen;
    }

    /**
     * Gets the cached keys for a layer.
     *
     * @param layerIndex the layer index
     * @return keys of shape (batchSize, currentSeqLen, dk) or null if not cached
     */
    public double[][] getKeys(int layerIndex) {
        CacheEntry entry = cache.get(layerIndex);
        if (entry == null) return null;

        int batchSize = entry.keys.length;
        int len = entry.currentLength;
        double[][] result = new double[batchSize][len][dk];
        for (int b = 0; b < batchSize; b++) {
            for (int s = 0; s < len; s++) {
                System.arraycopy(entry.keys[b][s], 0, result[b][s], 0, dk);
            }
        }
        return result;
    }

    /**
     * Gets the cached values for a layer.
     *
     * @param layerIndex the layer index
     * @return values of shape (batchSize, currentSeqLen, dv) or null if not cached
     */
    public double[][] getValues(int layerIndex) {
        CacheEntry entry = cache.get(layerIndex);
        if (entry == null) return null;

        int batchSize = entry.values.length;
        int len = entry.currentLength;
        double[][] result = new double[batchSize][len][dv];
        for (int b = 0; b < batchSize; b++) {
            for (int s = 0; s < len; s++) {
                System.arraycopy(entry.values[b][s], 0, result[b][s], 0, dv);
            }
        }
        return result;
    }

    /**
     * Returns the current sequence length (number of cached tokens) for a layer.
     */
    public int getCurrentLength(int layerIndex) {
        CacheEntry entry = cache.get(layerIndex);
        return (entry == null) ? 0 : entry.currentLength;
    }

    /**
     * Returns whether a layer has any cached entries.
     */
    public boolean hasLayer(int layerIndex) {
        return cache.containsKey(layerIndex);
    }

    /**
     * Resets the cache for all layers (call between generation sequences).
     */
    public void reset() {
        cache.clear();
    }

    /**
     * Resets the cache for a specific layer.
     */
    public void resetLayer(int layerIndex) {
        cache.remove(layerIndex);
    }

    /**
     * Returns the total number of cached tokens across all layers.
     */
    public int totalCachedTokens() {
        int total = 0;
        for (CacheEntry entry : cache.values()) {
            total += entry.currentLength;
        }
        return total;
    }

    /**
     * Returns the number of layers currently tracked in the cache.
     */
    public int numLayers() {
        return cache.size();
    }
}
```

**Incremental Attention with KV Cache:**

```java
package lab09.inference;

/**
 * Performs attention using the KV cache for incremental decoding.
 */
public class CachedAttention {

    private final KVCache cache;
    private final int dk;
    private final int dv;

    public CachedAttention(KVCache cache, int dk, int dv) {
        this.cache = cache;
        this.dk = dk;
        this.dv = dv;
    }

    /**
     * Computes attention for the new query against all cached key-value pairs.
     *
     * @param q          query for the new token (batchSize, 1, dk)
     * @param layerIndex layer index for cache lookup
     * @return attention output (batchSize, 1, dv)
     */
    public double[][] forward(double[][] q, int layerIndex) {
        double[][] k = cache.getKeys(layerIndex);
        double[][] v = cache.getValues(layerIndex);

        if (k == null || v == null) {
            throw new IllegalStateException(
                "KV cache is empty for layer " + layerIndex);
        }

        int batchSize = q.length;
        int seqLenK = k[0].length;

        // Compute scores = Q @ K^T / sqrt(dk)
        double[][] scores = new double[batchSize][1][seqLenK];
        double scale = Math.sqrt(dk);

        for (int b = 0; b < batchSize; b++) {
            for (int j = 0; j < seqLenK; j++) {
                double dot = 0;
                for (int d = 0; d < dk; d++) {
                    dot += q[b][0][d] * k[b][j][d];
                }
                scores[b][0][j] = dot / scale;
            }
        }

        // Softmax
        double[][] weights = new double[batchSize][1][seqLenK];
        for (int b = 0; b < batchSize; b++) {
            double max = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < seqLenK; j++) {
                if (scores[b][0][j] > max) max = scores[b][0][j];
            }
            double sum = 0;
            for (int j = 0; j < seqLenK; j++) {
                double expVal = Math.exp(scores[b][0][j] - max);
                weights[b][0][j] = expVal;
                sum += expVal;
            }
            if (sum > 0) {
                for (int j = 0; j < seqLenK; j++) {
                    weights[b][0][j] /= sum;
                }
            }
        }

        // Weighted sum of values
        double[][] output = new double[batchSize][1][dv];
        for (int b = 0; b < batchSize; b++) {
            for (int d = 0; d < dv; d++) {
                double sum = 0;
                for (int j = 0; j < seqLenK; j++) {
                    sum += weights[b][0][j] * v[b][j][d];
                }
                output[b][0][d] = sum;
            }
        }

        return output;
    }
}
```

**Example Usage:**

```java
package lab09.inference;

import java.util.Arrays;

public class KVCacheExample {
    public static void main(String[] args) {
        int dk = 8, dv = 8;
        int batchSize = 1;
        int numLayers = 6;

        KVCache cache = new KVCache(dk, dv);

        // Simulate prefill: process 5 prompt tokens
        System.out.println("=== Prefill Phase (5 tokens) ===");
        for (int layer = 0; layer < numLayers; layer++) {
            for (int t = 0; t < 5; t++) {
                double[][] K = new double[batchSize][1][dk];
                double[][] V = new double[batchSize][1][dv];
                Arrays.fill(K[0][0], Math.sin(t * 0.5 + layer));
                Arrays.fill(V[0][0], Math.cos(t * 0.3 + layer));
                cache.append(K, V, layer);
            }
        }
        System.out.println("Total cached tokens: " + cache.totalCachedTokens());

        // Simulate decode: generate 3 tokens one at a time
        System.out.println("\n=== Decode Phase ===");
        CachedAttention attn = new CachedAttention(cache, dk, dv);
        for (int t = 0; t < 3; t++) {
            double[][] Q = new double[batchSize][1][dk];
            Arrays.fill(Q[0][0], 0.5);

            // For each layer
            for (int layer = 0; layer < numLayers; layer++) {
                // Project new token to K, V and append to cache
                double[][] K = new double[batchSize][1][dk];
                double[][] V = new double[batchSize][1][dv];
                Arrays.fill(K[0][0], 0.1);
                Arrays.fill(V[0][0], 0.2);
                cache.append(K, V, layer);

                // Compute attention with cache
                double[][] output = attn.forward(Q, layer);
                System.out.println("Layer " + layer + ", step " + (5 + t)
                    + ": output sum = " + Arrays.stream(output[0][0]).sum());
            }
        }

        cache.reset();
        System.out.println("After reset, cached tokens: " + cache.totalCachedTokens());
    }
}
```

---

## Complexity Analysis

### Time Complexity Comparison

Let `T` = total generated tokens, `N` = prompt length, `L` = layers, `h` = heads, `d_k` = head dimension.

| Phase | Without KV Cache | With KV Cache |
|-------|-----------------|---------------|
| Prefill | `O(N * d² + N² * d)` | `O(N * d² + N² * d)` (same) |
| Decode (step t) | `O(t * d² + t² * d)` | `O(d² + t * d)` |
| Total decode | `O(T² * d² + T³ * d)` | `O(T * d² + T² * d)` |

**Without cache dominates at T² * d²**: For each new token, we reproject ALL previous tokens through QKV projections (`t * d²` FLOPs), then compute attention (`t² * d` FLOPs).

**With cache, projection is constant**: We only project the one new token (`d²` FLOPs), then compute attention against all cached tokens (`t * d` FLOPs).

### Memory Complexity

**Without KV Cache (recompute):** `O(d²)` — just parameters and current activation.

**With KV Cache:** `O(L * h * d_k * T)` for the cached keys and values.

For Llama 2 7B, `T = 4096`:
- 32 layers * 32 heads * 128 d_k * 4096 * 2 bytes ≈ 1 GB per sequence (fp16).

This is why KV cache management is the primary memory bottleneck in long-context inference.

### Practical Optimizations

| Technique | Savings | Implementation |
|-----------|---------|---------------|
| Multi-Query Attention | Reduce K/V heads to 1 | Single K/V shared across Q heads |
| Grouped-Query Attention | Reduce K/V groups (e.g., 8) | GQA |
| KV cache quantization | 2x-4x memory reduction | INT8 or FP8 caching |
| PagedAttention (vLLM) | Near-zero fragmentation | Page-level cache management |
| Prefix caching | Reuse cache for shared prefixes | Cache KV for system prompts |

---

## Follow-Up Questions with Answers

### Q1: Compare the memory usage of KV cache with and without Flash Attention during decoding.

**Answer:**

**Flash Attention (in training/prefill):** Reduces memory from `O(N²)` (attention scores) to `O(N)` by tiling and online softmax. The KV cache is still materialized (it's needed for the backward pass).

**During decoding with KV cache:**
- Flash Attention changes: the attention scores are still `O(t)` per new token (not `O(t²)`).
- Flash Attention doesn't reduce the KV cache memory — the cache still grows linearly.
- Flash Attention does reduce the memory bandwidth needed for the attention computation.

**Summary:** Flash Attention helps with compute-bound attention in prefill, but during decode, the bottleneck is loading the KV cache from memory (memory-bound), which Flash Attention doesn't solve.

### Q2: What is PagedAttention and how does it improve upon a naive KV cache implementation?

**Answer:** PagedAttention (Kwon et al., 2023) introduces virtual memory paging for the KV cache, similar to how operating systems manage physical memory.

**Problem with naive KV cache:**
- Pre-allocates contiguous memory for each request.
- Memory is fragmented (internal fragmentation from over-allocation, external fragmentation across requests).
- Cannot share memory across requests (e.g., shared system prompt prefix).

**PagedAttention solution:**
- KV cache is divided into fixed-size "pages" (blocks).
- A block table maps logical blocks to physical blocks (like page tables in OS).
- Blocks can be non-contiguous in physical memory.
- Blocks can be shared across requests (copy-on-write for beam search).

**Results:**
- Near-zero memory waste from fragmentation.
- 2-4x higher serving throughput.
- Enables efficient prefix caching (reuse KV cache for common prefixes).
- Up to 90% memory utilization vs ~20-40% for naive implementations.

### Q3: How does continuous batching interact with KV cache management?

**Answer:** Continuous batching (also called in-flight batching or iteration-level batching) processes multiple requests together by batching at the iteration level rather than the request level.

**Traditional batching:** Wait for all requests in a batch to finish generating before starting new requests.
**Continuous batching:** Add new requests to the running batch as existing requests finish.

**Interactions with KV cache:**
1. **Variable sequence lengths:** Each request in the batch has a different KV cache size (different number of generated tokens).
2. **Memory management:** Must allocate KV cache pages for each request and reclaim them when the request finishes.
3. **Shared prefix caching:** Common prefixes (system prompts, few-shot examples) share KV cache pages across requests.
4. **Scheduling:** The scheduler decides when to preempt a request and swap its KV cache to CPU memory.

**Implementation:** Systems like vLLM and TensorRT-LLM use a block-level KV cache manager that supports:
- Allocation of new blocks as requests grow.
- Deallocation when requests finish.
- Copy-on-write for beam search.
- Swapping to CPU for preempted requests.

### Q4: What techniques exist to reduce the memory footprint of the KV cache?

**Answer:**

1. **KV cache quantization:** Store K and V in lower precision (INT8, FP8, or even 4-bit). This can reduce memory by 2-4x with minimal quality loss. Techniques like KIVI (2024) use per-channel quantization for K and per-token for V.

2. **Multi-Query/Grouped-Query Attention:** Reduce the number of K/V heads. MQA (1 K/V head) reduces KV cache by a factor of `h`. GQA (g groups) reduces by `h/g`.

3. **KV cache pruning:** Some tokens have low attention scores across all heads — their K/V entries can be evicted. H2O (Heavy Hitter Oracle, 2023) keeps only the most important recent and "heavy hitter" tokens.

4. **Prefix caching:** Cache the KV for shared prefixes (system prompts, few-shot examples). With PagedAttention, this can be shared across requests.

5. **Windowed attention:** Only cache the last `w` tokens (Sliding Window Attention). Used in Mistral and other models. Limits cache to `O(w)` per layer.

6. **Sparsity-based approaches:** StreamingLLM keeps only the initial tokens and recent tokens, discarding middle tokens.

### Q5: How would you implement speculative decoding with the KV cache?

**Answer:** Speculative decoding uses a draft model to propose multiple tokens, then the target model verifies them in parallel. The KV cache interacts as follows:

1. **Draft phase:** The draft model generates `γ` candidate tokens autoregressively. Each step updates the draft model's own KV cache (which is small).

2. **Verify phase:** The target model processes all `γ` candidate tokens in a single forward pass (parallel, like prefill). This requires:
   - If the target model has been caching K/V from previously accepted tokens, the draft tokens' K/V are appended for the verification.
   - The verification computes attention for all `γ` positions against the cached prefix + draft tokens.
   - If a token is rejected, the KV cache must be "rolled back" to before that token.

3. **KV cache management for speculative decoding:**
   - During draft: iterate draft model's cache (step-by-step).
   - During verify: append draft tokens to target model's cache in one batch.
   - On rejection: truncate cache back to the last accepted token.
   - This "rollback" requires careful cache state management.

Systems like SpecInfer and Medusa implement this efficiently by maintaining multiple cache states for tree-based speculation.

---

## Test Cases

### Test Case 1: Basic Append and Retrieve

```java
void testBasicAppend() {
    KVCache cache = new KVCache(4, 4, 100);
    int batchSize = 1;

    double[][] K1 = new double[batchSize][1][4];
    double[][] V1 = new double[batchSize][1][4];
    Arrays.fill(K1[0][0], 1.0);
    Arrays.fill(V1[0][0], 2.0);

    cache.append(K1, V1, 0);

    double[][] cachedK = cache.getKeys(0);
    double[][] cachedV = cache.getValues(0);

    assert cachedK != null : "Keys should not be null after append";
    assert cachedV != null : "Values should not be null after append";
    assert cachedK[0].length == 1 : "Should have 1 cached token";
    assert Math.abs(cachedK[0][0][0] - 1.0) < 1e-10 : "Key value mismatch";
    assert Math.abs(cachedV[0][0][0] - 2.0) < 1e-10 : "Value value mismatch";
}
```

### Test Case 2: Multiple Appends

```java
void testMultipleAppends() {
    KVCache cache = new KVCache(4, 4, 100);
    int batchSize = 1;

    for (int t = 0; t < 5; t++) {
        double[][] K = new double[batchSize][1][4];
        double[][] V = new double[batchSize][1][4];
        Arrays.fill(K[0][0], t + 1.0);
        Arrays.fill(V[0][0], (t + 1) * 10);
        cache.append(K, V, 0);
    }

    double[][] cachedK = cache.getKeys(0);
    assert cachedK[0].length == 5 : "Should have 5 cached tokens";
    assert Math.abs(cachedK[0][2][0] - 3.0) < 1e-10 : "Third token K mismatch";
    assert Math.abs(cachedV[0][4][0] - 50.0) < 1e-10 : "Fifth token V mismatch";
}
```

### Test Case 3: Multiple Layers

```java
void testMultipleLayers() {
    KVCache cache = new KVCache(4, 4, 100);
    int batchSize = 1;
    int numLayers = 6;

    for (int layer = 0; layer < numLayers; layer++) {
        for (int t = 0; t < 3; t++) {
            double[][] K = new double[batchSize][1][4];
            double[][] V = new double[batchSize][1][4];
            Arrays.fill(K[0][0], layer * 10 + t);
            Arrays.fill(V[0][0], layer * 10 + t);
            cache.append(K, V, layer);
        }
    }

    for (int layer = 0; layer < numLayers; layer++) {
        double[][] cachedK = cache.getKeys(layer);
        assert cachedK[0].length == 3 : "Layer " + layer + " should have 3 tokens";
    }

    assert cache.numLayers() == numLayers : "Should track " + numLayers + " layers";
}
```

### Test Case 4: Reset

```java
void testReset() {
    KVCache cache = new KVCache(4, 4, 100);
    double[][] K = new double[1][1][4];
    double[][] V = new double[1][1][4];
    cache.append(K, V, 0);
    cache.append(K, V, 1);

    assert cache.totalCachedTokens() == 2 : "Should have 2 cached tokens";
    assert cache.numLayers() == 2 : "Should have 2 layers";

    cache.reset();
    assert cache.totalCachedTokens() == 0 : "Should have 0 after reset";
    assert cache.numLayers() == 0 : "Should have 0 layers after reset";
    assert cache.getKeys(0) == null : "Keys should be null after reset";
}
```

### Test Case 5: Incremental Attention Equivalence

```java
void testIncrementalAttentionEquivalence() {
    int dk = 8, dv = 8;
    int seqLen = 5;
    int batchSize = 1;

    // Compute attention the standard way (full Q/K/V)
    double[][] Q = new double[batchSize][1][dk];
    double[][] K_all = new double[batchSize][seqLen][dk];
    double[][] V_all = new double[batchSize][seqLen][dv];

    Arrays.fill(Q[0][0], 0.5);
    for (int j = 0; j < seqLen; j++) {
        Arrays.fill(K_all[0][j], Math.sin(j * 0.5));
        Arrays.fill(V_all[0][j], Math.cos(j * 0.3));
    }

    // Full attention
    double scale = Math.sqrt(dk);
    double[] fullScores = new double[seqLen];
    for (int j = 0; j < seqLen; j++) {
        double dot = 0;
        for (int d = 0; d < dk; d++) {
            dot += Q[0][0][d] * K_all[0][j][d];
        }
        fullScores[j] = dot / scale;
    }
    double max = Double.NEGATIVE_INFINITY;
    for (int j = 0; j < seqLen; j++) if (fullScores[j] > max) max = fullScores[j];
    double sum = 0;
    double[] fullWeight = new double[seqLen];
    for (int j = 0; j < seqLen; j++) {
        fullWeight[j] = Math.exp(fullScores[j] - max);
        sum += fullWeight[j];
    }
    for (int j = 0; j < seqLen; j++) fullWeight[j] /= sum;
    double[] fullOutput = new double[dv];
    for (int d = 0; d < dv; d++) {
        for (int j = 0; j < seqLen; j++) {
            fullOutput[d] += fullWeight[j] * V_all[0][j][d];
        }
    }

    // Cached version: build cache incrementally
    KVCache cache = new KVCache(dk, dv);
    for (int t = 0; t < seqLen; t++) {
        double[][] K_t = new double[batchSize][1][dk];
        double[][] V_t = new double[batchSize][1][dv];
        System.arraycopy(K_all[0][t], 0, K_t[0][0], 0, dk);
        System.arraycopy(V_all[0][t], 0, V_t[0][0], 0, dv);
        cache.append(K_t, V_t, 0);
    }

    CachedAttention cachedAttn = new CachedAttention(cache, dk, dv);
    double[][] cachedOutput = cachedAttn.forward(Q, 0);

    // Should match
    for (int d = 0; d < dv; d++) {
        assert Math.abs(cachedOutput[0][0][d] - fullOutput[d]) < 1e-10 :
            "Output mismatch at dim " + d + ": " + cachedOutput[0][0][d] + " vs " + fullOutput[d];
    }
}
```

### Test Case 6: Current Length Tracking

```java
void testCurrentLength() {
    KVCache cache = new KVCache(8, 8, 100);

    assert cache.getCurrentLength(0) == 0 : "Initial length should be 0";

    double[][] K = new double[1][1][8];
    double[][] V = new double[1][1][8];

    cache.append(K, V, 0);
    assert cache.getCurrentLength(0) == 1 : "Length should be 1 after append";

    cache.append(K, V, 0);
    cache.append(K, V, 0);
    assert cache.getCurrentLength(0) == 3 : "Length should be 3 after 3 appends";

    cache.resetLayer(0);
    assert cache.getCurrentLength(0) == 0 : "Length should be 0 after reset";
}
```

### Test Case 7: Cache Overflow

```java
void testCacheOverflow() {
    KVCache cache = new KVCache(4, 4, 3); // max 3 tokens

    double[][] K = new double[1][1][4];
    double[][] V = new double[1][1][4];

    cache.append(K, V, 0);
    cache.append(K, V, 0);
    cache.append(K, V, 0);

    boolean threw = false;
    try {
        cache.append(K, V, 0); // 4th token should overflow
    } catch (RuntimeException e) {
        threw = true;
    }
    assert threw : "Should throw on cache overflow";
}
```

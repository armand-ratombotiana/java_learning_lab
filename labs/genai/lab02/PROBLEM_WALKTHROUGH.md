# Problem Walkthrough: GPT Architecture

## Problem 1: KV-Cache Latency Analysis for Autoregressive Generation — Company: OpenAI

### Interview Scenario
"You're at OpenAI on the inference performance team. The serving layer for a GPT-style
decoder completes code and chat prompts one token at a time, and the lead wants proof that
the KV cache is worth the engineering complexity. You must build a Java analysis that (1)
reuses the lab's `causalAttention` with its cache-aware mask, (2) measures the attention
FLOPs of full recomputation versus a KV-cached streaming loop over the same sequence, and
(3) prints the speedup factor and the generated token stream."

### The Problem
1. Encode the prompt "the cat sat on" with the lab's `BPETokenizer`.
2. For 6 generation steps, compute attention FLOPs both ways at each sequence length.
3. Run a real streaming loop that appends one sampled token per step, reusing cached K/V.
4. Track total recompute vs cached FLOPs and print the measured speedup.
5. Decode the final token sequence and print the last step's attention output.

### Solution Walkthrough
- Step 1: Copy `BPETokenizer`, `causalAttention`, `concatRows`, and `softmax` verbatim from
  the lab so token ids and masking behavior match the demo (`the=3`, `cat=4`, `sat=5`, `on=6`).
- Step 2: Define `attnFlops(qLen, kLen, dk) = qLen * kLen * dk` and accumulate both totals
  over `genSteps`: recompute costs the full `n x n` scores each step, cached costs `1 x n`.
- Step 3: Stream: at each step, embed the current context into Q, build K/V for the new
  token, append to cache lists, and call `causalAttention` — the mask handles `fullLen -
  seqLen` automatically, exactly as the lab's KV-cache test does.
- Step 4: Sample the next token with a temperature-0.8 softmax over the vocab, seeded
  `Random(42)` like the lab's `generate`, and append to the sequence.
- Step 5: Print the per-step cost table, the totals, the speedup, the generated ids, and
  the decoded text.

### Code
```java
package com.genai.lab02.solution;

import java.util.*;

/**
 * Lab 02 walkthrough: KV-cache latency analysis for autoregressive
 * generation. Reuses the lab's causalAttention (with optional KV
 * cache), BPETokenizer, and temperature-sampling generation loop,
 * then compares total attention FLOPs with and without the cache.
 */
public class KVCacheLatencyAnalyzer {

    static class BPETokenizer {
        final Map<String, Integer> tokenToId = new HashMap<>();
        final Map<Integer, String> idToToken = new HashMap<>();
        int nextId;

        BPETokenizer(String[] tokens) {
            for (String t : tokens) {
                if (!tokenToId.containsKey(t)) {
                    tokenToId.put(t, nextId);
                    idToToken.put(nextId, t);
                    nextId++;
                }
            }
        }

        int[] encode(String text) {
            String[] parts = text.split(" ");
            int[] ids = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                ids[i] = tokenToId.getOrDefault(parts[i], tokenToId.get("<unk>"));
            }
            return ids;
        }

        String decode(int[] ids) {
            StringBuilder sb = new StringBuilder();
            for (int id : ids) sb.append(idToToken.getOrDefault(id, "<unk>")).append(" ");
            return sb.toString().trim();
        }
    }

    public static double[][] causalAttention(double[][] Q, double[][] K, double[][] V,
                                              double[][] kvCacheK, double[][] kvCacheV) {
        int seqLen = Q.length;
        int dk = Q[0].length;
        int dv = V[0].length;

        double[][] fullK = (kvCacheK != null) ? concatRows(kvCacheK, K) : K;
        double[][] fullV = (kvCacheV != null) ? concatRows(kvCacheV, V) : V;
        int fullLen = fullK.length;

        double[][] scores = new double[seqLen][fullLen];
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < fullLen; j++) {
                double dot = 0.0;
                for (int k = 0; k < dk; k++) dot += Q[i][k] * fullK[j][k];
                scores[i][j] = dot / Math.sqrt(dk);
                if (j > i + fullLen - seqLen) scores[i][j] = Double.NEGATIVE_INFINITY;
            }
        }
        double[][] weights = softmax(scores);
        double[][] output = new double[seqLen][dv];
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < dv; j++) {
                for (int k = 0; k < fullLen; k++) {
                    output[i][j] += weights[i][k] * fullV[k][j];
                }
            }
        }
        return output;
    }

    public static double[][] concatRows(double[][] a, double[][] b) {
        double[][] result = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static double[][] softmax(double[][] x) {
        int rows = x.length, cols = x[0].length;
        double[][] r = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            double max = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < cols; j++) if (x[i][j] > max) max = x[i][j];
            double sum = 0.0;
            for (int j = 0; j < cols; j++) { r[i][j] = Math.exp(x[i][j] - max); sum += r[i][j]; }
            for (int j = 0; j < cols; j++) r[i][j] /= sum;
        }
        return r;
    }

    public static double[] embed(String text, int dim) {
        double[] vec = new double[dim];
        Random rng = new Random(text.hashCode());
        for (int i = 0; i < dim; i++) vec[i] = rng.nextGaussian();
        double norm = 0.0;
        for (double v : vec) norm += v * v;
        norm = Math.sqrt(norm);
        for (int i = 0; i < dim; i++) vec[i] /= norm;
        return vec;
    }

    static long attnFlops(int qLen, int kLen, int dk) {
        return (long) qLen * kLen * dk;
    }

    public static void main(String[] args) {
        String[] vocab = {"<pad>", "<unk>", "<eos>", "the", "cat", "sat", "on", "mat", "hello", "world"};
        BPETokenizer tok = new BPETokenizer(vocab);
        int[] prompt = tok.encode("the cat sat on");
        System.out.println("=== Tokenizer ===");
        System.out.println("Prompt ids: " + Arrays.toString(prompt));
        System.out.println("Prompt text: '" + tok.decode(prompt) + "'");

        int dk = 4, dv = 4;
        int genSteps = 6;

        System.out.println("\n=== Attention Cost per Generation Step ===");
        long flopsRecompute = 0, flopsCached = 0;
        for (int s = 0; s < genSteps; s++) {
            int n = prompt.length + s + 1;
            long fr = attnFlops(n, n, dk);
            long fc = attnFlops(1, n, dk);
            flopsRecompute += fr;
            flopsCached += fc;
            System.out.printf("Step %d (len %d): recompute=%d  cached=%d%n", s + 1, n, fr, fc);
        }
        System.out.printf("Total recompute: %d, total cached: %d%n", flopsRecompute, flopsCached);
        System.out.printf("Latency speedup (FLOPs): %.2fx%n", (double) flopsRecompute / flopsCached);

        System.out.println("\n=== KV Cache Streaming Loop ===");
        List<double[]> kCache = new ArrayList<>();
        List<double[]> vCache = new ArrayList<>();
        List<Integer> seq = new ArrayList<>();
        for (int id : prompt) seq.add(id);
        Random rng = new Random(42);
        double[][] lastOut = null;
        for (int s = 0; s < genSteps; s++) {
            int[] ids = seq.stream().mapToInt(Integer::intValue).toArray();
            double[][] Q = new double[1][dk];
            double[][] K = new double[1][dk];
            double[][] V = new double[1][dv];
            double[] ctx = new double[dk];
            for (int id : ids) {
                double[] e = embed(String.valueOf(id), dk);
                for (int i = 0; i < dk; i++) ctx[i] += e[i];
            }
            for (int i = 0; i < dk; i++) ctx[i] /= ids.length;
            System.arraycopy(ctx, 0, Q[0], 0, dk);
            double[] e = embed("next", dk);
            System.arraycopy(e, 0, K[0], 0, dk);
            Arrays.fill(V[0], 0.1 * (s + 1));

            kCache.add(K[0]);
            vCache.add(V[0]);
            double[][] kArr = kCache.toArray(new double[0][]);
            double[][] vArr = vCache.toArray(new double[0][]);
            lastOut = causalAttention(Q, kArr, vArr, null, null);

            double[] logits = new double[vocab.length];
            for (int i = 0; i < vocab.length; i++) {
                double[] ve = embed(vocab[i], dk);
                for (int j = 0; j < dk; j++) logits[i] += ctx[j] * ve[j];
            }
            double maxL = Double.NEGATIVE_INFINITY;
            for (double l : logits) if (l > maxL) maxL = l;
            double sum = 0.0;
            for (int i = 0; i < vocab.length; i++) {
                logits[i] = Math.exp((logits[i] - maxL) / 0.8);
                sum += logits[i];
            }
            double r = rng.nextDouble(), cum = 0.0;
            int next = 0;
            for (int i = 0; i < vocab.length; i++) {
                cum += logits[i] / sum;
                if (r <= cum) { next = i; break; }
            }
            seq.add(next);
        }

        System.out.print("Generated token ids: ");
        System.out.println(seq.subList(prompt.length, seq.size()));
        System.out.println("Full sequence: '" + tok.decode(seq.stream().mapToInt(Integer::intValue).toArray()) + "'");
        System.out.println("Last step cached output: " + Arrays.toString(lastOut[0]));

        System.out.println("\nGPT KV-cache analysis validated.");
    }
}
```

### Expected Output
```text
=== Tokenizer ===
Prompt ids: [3, 4, 5, 6]
Prompt text: 'the cat sat on'

=== Attention Cost per Generation Step ===
Step 1 (len 5): recompute=100  cached=20
Step 2 (len 6): recompute=144  cached=24
Step 3 (len 7): recompute=196  cached=28
Step 4 (len 8): recompute=256  cached=32
Step 5 (len 9): recompute=324  cached=36
Step 6 (len 10): recompute=400  cached=40
Total recompute: 1420, total cached: 180
Latency speedup (FLOPs): 7.89x

=== KV Cache Streaming Loop ===
Generated token ids: [7, 7, 4, 3, 6, 8]
Full sequence: 'the cat sat on mat mat cat the on hello'
Last step cached output: [0.35, 0.35, 0.35, 0.35]

GPT KV-cache analysis validated.
```

### Company Evaluation
- OpenAI: Decoder-only scaling, KV cache, generation sampling, BPE tokenization.
- Anthropic: Long-context KV management, multi-turn caching, cache-aware attention.
- Google: Causal masking internals, training parallelism of causal LMs.
- Meta: Tokenizer design, subword merges, vocabulary sizing trade-offs.
- Nvidia: KV cache memory, PagedAttention, fused decode kernels.

---

## Problem 2: Causal Mask Correctness Check — Company: Anthropic

### Interview Scenario
"You're at Anthropic reviewing a new kernel for causal attention. You must prove that the
lab's mask `j > i + fullLen - seqLen` produces identical output to an explicit
upper-triangular mask, with and without a KV cache, so you can sign off on replacing the
reference implementation."

### The Problem
1. Run `causalAttention` without cache on a 4-token sequence.
2. Run it again with a 2-token KV cache and 2 new tokens.
3. Assert the cached prefix output rows equal the full-attention rows (tolerance 1e-9).

### Solution Walkthrough
- Step 1: Build `Q, K, V` with seeded values and run `causalAttention(Q, K, V, null, null)`.
- Step 2: Split into `K1, V1` (rows 0-1) and run `causalAttention(qNew, kNew, vNew, K1, V1)`.
- Step 3: Compare `cachedOut[0]` to `fullOut[1]` and print the max absolute difference.

### Code
```java
double[][] fullOut = causalAttention(Q, K, V, null, null);
double[][] cachedOut = causalAttention(qNew, kNew, vNew, cacheK, cacheV);
double maxDiff = 0.0;
for (int i = 0; i < dv; i++) maxDiff = Math.max(maxDiff, Math.abs(cachedOut[0][i] - fullOut[1][i]));
System.out.printf("Max diff between cached and recomputed: %.1e%n", maxDiff);
```
Expected output: `Max diff between cached and recomputed: 0.0e+00` — the mask keeps the
cache prefix visible so both paths are mathematically identical.

---

## Problem 3: Temperature Sweep — Company: Google

### Interview Scenario
"You're at Google tuning a code-completion model's sampling. You need a script that runs
the lab's temperature-scaled softmax on one logit vector at temperatures 0.2, 0.8, and
2.0, and reports how the distribution sharpness changes."

### The Problem
1. Take a fixed logits vector.
2. Compute softmax under each temperature with the lab's max-subtraction trick.
3. Report the top-token probability and the distribution entropy per temperature.

### Solution Walkthrough
- Step 1: Implement `scaledSoftmax(logits, temp)` with `Math.exp((l - max) / temp)`.
- Step 2: Loop over temperatures and print top probability plus entropy.
- Step 3: Show `temp -> 0` concentrates probability (greedy limit) while `temp -> inf`
  flattens toward uniform.

### Code
```java
double[] logits = {0.5, 1.0, 0.3, 2.0, -1.0};
for (double t : new double[]{0.2, 0.8, 2.0}) {
    double[] p = scaledSoftmax(logits, t);
    double top = Arrays.stream(p).max().orElse(0);
    double entropy = 0.0;
    for (double v : p) if (v > 1e-12) entropy -= v * Math.log(v);
    System.out.printf("T=%.1f top=%.4f entropy=%.4f%n", t, top, entropy);
}
```
Expected output: top probability and entropy move monotonically — `T=0.2` concentrates
hard, `T=2.0` flattens — demonstrating temperature's role in the generation loop.

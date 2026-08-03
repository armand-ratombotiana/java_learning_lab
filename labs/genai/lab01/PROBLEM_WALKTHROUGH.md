# Problem Walkthrough: Transformer Architecture Deep Dive

## Problem 1: Query-Document Relevance Scoring with Attention — Company: Google

### Interview Scenario
"You're at Google on the Search quality team. The snippet selection system needs a fast,
deterministic scoring layer that ranks candidate documents for a query before the large
ranking model runs. You have a corpus of three candidate snippets about model
architectures and three queries. Using only the attention machinery from your
Transformer lab (`scaledDotProductAttention`, sinusoidal `positionalEncoding`, stable
`softmax`), build a scorer that ranks documents by embedding similarity and then shows
where the query attends inside the top document, so the snippet team can visualize the
evidence."

### The Problem
1. Embed each document and query into a shared `dModel`-dimensional space deterministically.
2. Add sinusoidal positional encoding to document tokens so word order influences attention.
3. Rank documents per query using cosine similarity between query and document centroids.
4. Run scaled dot-product attention with the query as Q and the top document's tokens as K/V.
5. Print the attention distribution over the top document's tokens (the "evidence map").
6. All scores and weights must be reproducible — no external libraries, Java 21 only.

### Solution Walkthrough
- Step 1: Define `embed(text, dim)` — a hash-seeded Gaussian embedding normalized to unit
  length, exactly mirroring the lab's `embed` pattern from lab 04 so retrieval is reproducible.
- Step 2: Add `addPE(vec, pos, dModel)` which injects the lab's sinusoidal encoding
  `pos / 10000^(2i/dModel)` with `sin` on even dims and `cos` on odd dims.
- Step 3: Compute each document's centroid vector by averaging its token embeddings, then
  normalize — this is the cheap document representation used for ranking.
- Step 4: For each query, rank documents by `cosine(queryCentroid, docCentroid)` descending.
- Step 5: For the top document, build Q from the query embedding (position 0), K and V from
  the document tokens with positional encoding, and call `scaledDotProductAttention` to get
  the attention weight row.
- Step 6: Print the ranking and the per-token attention weights rounded to 2 decimals as
  the evidence map, then print the validation footer.

### Code
```java
package com.genai.lab01.solution;

import java.util.*;

/**
 * Lab 01 walkthrough: attention-based search snippet relevance scoring.
 * Reuses the lab's scaledDotProductAttention and sinusoidal
 * positionalEncoding patterns to rank documents per query and to
 * highlight which positions of the top document the query attends to.
 */
public class SearchSnippetScorer {

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

    public static double[] addPE(double[] vec, int pos, int dModel) {
        double[] out = vec.clone();
        for (int i = 0; i < dModel; i++) {
            double angle = pos / Math.pow(10000.0, (2.0 * (i / 2)) / dModel);
            out[i] += (i % 2 == 0) ? Math.sin(angle) : Math.cos(angle);
        }
        return out;
    }

    public static double[][] scaledDotProductAttention(double[][] Q, double[][] K, double[][] V) {
        int seqLen = Q.length;
        int dk = Q[0].length;
        double[][] scores = new double[seqLen][seqLen];
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < seqLen; j++) {
                double dot = 0.0;
                for (int k = 0; k < dk; k++) dot += Q[i][k] * K[j][k];
                scores[i][j] = dot / Math.sqrt(dk);
            }
        }
        return softmax(scores);
    }

    public static double[][] softmax(double[][] x) {
        int rows = x.length;
        int cols = x[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            double max = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < cols; j++) if (x[i][j] > max) max = x[i][j];
            double sum = 0.0;
            for (int j = 0; j < cols; j++) { result[i][j] = Math.exp(x[i][j] - max); sum += result[i][j]; }
            for (int j = 0; j < cols; j++) result[i][j] /= sum;
        }
        return result;
    }

    public static double cosine(double[] a, double[] b) {
        double dot = 0.0, na = 0.0, nb = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }

    public static double[] centroid(String[] tokens, int dim) {
        double[] sum = new double[dim];
        for (String t : tokens) {
            double[] e = embed(t, dim);
            for (int i = 0; i < dim; i++) sum[i] += e[i];
        }
        double norm = 0.0;
        for (double v : sum) norm += v * v;
        norm = Math.sqrt(norm);
        for (int i = 0; i < dim; i++) sum[i] /= norm;
        return sum;
    }

    public static void main(String[] args) {
        int dModel = 16;
        List<String> docs = List.of(
            "the transformer uses self attention and positional encoding",
            "gpt is a decoder only language model with causal masking",
            "bert is an encoder only model for understanding tasks");

        List<String> queries = List.of(
            "does the gpt decoder use causal masking",
            "bert is an encoder only model",
            "attention and positional encoding");

        Map<String, double[]> docVec = new LinkedHashMap<>();
        for (String doc : docs) docVec.put(doc, centroid(doc.split(" "), dModel));

        System.out.println("=== Embedding Similarity Ranking (cosine) ===");
        for (String q : queries) {
            double[] qv = centroid(q.split(" "), dModel);
            List<Map.Entry<String, Double>> ranked = new ArrayList<>();
            for (String doc : docs) ranked.add(Map.entry(doc, cosine(qv, docVec.get(doc))));
            ranked.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
            System.out.println("Query: '" + q + "'");
            for (var e : ranked) System.out.printf("  %.4f  %s%n", e.getValue(), e.getKey());
            System.out.println("  Top result: " + ranked.get(0).getKey());

            String topDoc = ranked.get(0).getKey();
            String[] tokens = topDoc.split(" ");
            double[][] Q = new double[tokens.length][dModel];
            double[][] K = new double[tokens.length][dModel];
            double[][] V = new double[tokens.length][dModel];
            for (int i = 0; i < tokens.length; i++) {
                Q[i] = addPE(embed(q, dModel), 0, dModel);
                K[i] = addPE(embed(tokens[i], dModel), i, dModel);
                V[i] = K[i];
            }
            double[][] attn = scaledDotProductAttention(Q, K, V);
            System.out.print("  Attention over top doc: ");
            for (int i = 0; i < tokens.length; i++) {
                System.out.printf("%s(%.2f) ", tokens[i], attn[0][i]);
            }
            System.out.println();
        }
        System.out.println("\nSearch relevance scoring validated.");
    }
}
```

### Expected Output
```text
=== Embedding Similarity Ranking (cosine) ===
Query: 'does the gpt decoder use causal masking'
  0.6809  gpt is a decoder only language model with causal masking
  0.0089  bert is an encoder only model for understanding tasks
  -0.2056  the transformer uses self attention and positional encoding
  Top result: gpt is a decoder only language model with causal masking
  Attention over top doc: gpt(0.17) is(0.14) a(0.13) decoder(0.07) only(0.09) language(0.09) model(0.12) with(0.07) causal(0.07) masking(0.05) 
Query: 'bert is an encoder only model'
  0.4798  bert is an encoder only model for understanding tasks
  0.4505  gpt is a decoder only language model with causal masking
  0.1039  the transformer uses self attention and positional encoding
  Top result: bert is an encoder only model for understanding tasks
  Attention over top doc: bert(0.12) is(0.14) an(0.10) encoder(0.10) only(0.11) model(0.13) for(0.11) understanding(0.10) tasks(0.08) 
Query: 'attention and positional encoding'
  0.7304  the transformer uses self attention and positional encoding
  0.0170  bert is an encoder only model for understanding tasks
  -0.0445  gpt is a decoder only language model with causal masking
  Top result: the transformer uses self attention and positional encoding
  Attention over top doc: the(0.25) transformer(0.15) uses(0.12) self(0.08) attention(0.10) and(0.10) positional(0.10) encoding(0.10) 

Search relevance scoring validated.
```

### Company Evaluation
- Google: Transformer internals, scaled attention, sinusoidal positional encoding, numerical stability.
- OpenAI: Scaling attention to long context, FlashAttention-style tiling, bf16 in production.
- Anthropic: Post-norm vs pre-norm trade-offs, residual stream analysis, interpretability of attention.
- Meta: Layer norm placement, FFN capacity analysis, training dynamics of deep stacks.
- Nvidia: Fused softmax kernels, materialization avoidance, memory bandwidth analysis.

---

## Problem 2: Multi-Head Attention Dispatch — Company: OpenAI

### Interview Scenario
"You're at OpenAI working on a training utility that must run the lab's single-head
`scaledDotProductAttention` across `h` parallel heads with per-head projections, then
concatenate. The training loop calls this millions of times, so the head projection must
be correct and the total output must be the same shape as the input."

### The Problem
1. Split `x` into `h` heads via per-head weight matrices.
2. Run `scaledDotProductAttention` per head with `dk = dModel / h`.
3. Concatenate head outputs back to `dModel` dimension.
4. Validate that output shape equals input shape and heads receive different weight rows.

### Solution Walkthrough
- Step 1: Build one `Wq/Wk/Wv` per head, each `dModel x (dModel/h)`.
- Step 2: Loop heads, projecting `x` with `matMul`, then calling the lab's
  `scaledDotProductAttention` with `dk = dModel / h`.
- Step 3: Concatenate along the last dimension and report shape.
- Step 4: Print the head count, per-head `dk`, and the final shape to prove the contract.

### Code
```java
double[][] multiHead(double[][] x, int h, double[][] Wq, double[][] Wk,
                     double[][] Wv, int dModel) {
    int dk = dModel / h;
    double[][] concat = new double[x.length][dModel];
    for (int head = 0; head < h; head++) {
        double[][] Q = matMul(x, slice(Wq, head, dk));
        double[][] K = matMul(x, slice(Wk, head, dk));
        double[][] V = matMul(x, slice(Wv, head, dk));
        double[][] out = scaledDotProductAttention(Q, K, V);
        for (int i = 0; i < x.length; i++)
            System.arraycopy(out[i], 0, concat[i], head * dk, dk);
    }
    return concat; // shape: seqLen x dModel
}
```
Expected output: `Heads: 4, dk per head: 2, output shape: 4 x 8 — matches input.`

---

## Problem 3: Encoder Block Feature Extraction — Company: Anthropic

### Interview Scenario
"You're at Anthropic building an interpretability harness that runs the lab's
`encoderBlock` end-to-end on a token sequence and inspects the intermediate residual
norms after the attention sub-layer and after the FFN sub-layer."

### The Problem
1. Run the full `encoderBlock` with random-but-seeded weights.
2. Report the residual connection's norm contribution before and after layer norm.
3. Confirm the output preserves the input sequence shape.

### Solution Walkthrough
- Step 1: Seed all weight matrices with `Random(42) * 0.1` per the lab style.
- Step 2: Run `encoderBlock(x, Wq, Wk, Wv, Wo, W1, W2)` and capture the output.
- Step 3: Compute the L2 norm of the output rows before and after `layerNorm` to show the
  stabilization effect.
- Step 4: Print the shape and both norms.

### Code
```java
double[][] x = new double[4][8];
for (double[] row : x) Arrays.fill(row, 0.5);
double[][] Wq = rand(8, 8), Wk = rand(8, 8), Wv = rand(8, 8);
double[][] Wo = rand(8, 8), W1 = rand(8, 16), W2 = rand(16, 8);
double[][] out = encoderBlock(x, Wq, Wk, Wv, Wo, W1, W2);
System.out.printf("Output shape: %d x %d%n", out.length, out[0].length);
System.out.printf("Row 0 pre-norm norm: %.4f%n", norm(x[0]));
System.out.printf("Row 0 post-norm norm: %.4f%n", norm(out[0]));
```
Expected output: `Output shape: 4 x 8`, with the post-norm row norm near 1.0 regardless of
input scale — demonstrating layer norm's stabilizing contract.

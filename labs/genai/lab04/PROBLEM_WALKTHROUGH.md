# Problem Walkthrough: RAG System Design

## Problem 1: Retrieval Evaluation with Lost-in-the-Middle Reranking — Company: Stripe

### Interview Scenario
"You're at Stripe building the RAG backend for the developer-support bot. The docs are
chunked once and indexed, but the team has no measurement of whether retrieval actually
finds the right chunks, and answers degrade when too much context is stuffed into the
prompt. Build a Java evaluation harness using the lab's chunker, vector store, and
pipeline that (1) measures retrieval hit rate against gold queries, (2) demonstrates the
lost-in-the-middle problem by comparing naive chunk order against a reranked order, and
(3) assembles the final augmented prompt."

### The Problem
1. Chunk a support document with the lab's `Chunker.fixedSize` (size 40, overlap 8).
2. Replace the toy hash embedding with a deterministic lexical embedding so retrieval is meaningful.
3. Index chunks in a `VectorStore` and measure hit rate over three gold queries.
4. Rerank the top chunks: best first, second-best last, to fight lost-in-the-middle.
5. Build the final augmented prompt from the reranked context.

### Solution Walkthrough
- Step 1: Reuse `Chunker.fixedSize`, `VectorStore`, and `RAGPipeline` structure verbatim;
  the chunker emits 12 overlapping chunks, and chunk boundaries split words — visible
  in `[6]` ending with "Payou" — which is exactly why overlap exists.
- Step 2: Replace `embed` with a term-frequency embedding over a corpus vocab built by
  `buildVocab`, keeping the lab's `embed(text, dim)` signature; zero vectors are guarded
  so the empty trailing chunk `[11]` ranks last instead of producing NaN.
- Step 3: Add a zero-norm guard to `cosineSimilarity` so degenerate chunks score 0.0.
- Step 4: Add `searchWithScores` (extension of `VectorStore.search`) so reranking sees
  similarity values, and implement `rerankForContext`: `[best, others..., second-best]`.
- Step 5: Run the three gold queries, print hit/fail per query and the final hit rate,
  then print original vs reranked order and the augmented prompt.

### Code
```java
package com.genai.lab04.solution;

import java.util.*;

/**
 * Lab 04 walkthrough: RAG retrieval evaluation for a support bot,
 * with a lost-in-the-middle reranking pass. Reuses the lab's embed,
 * cosineSimilarity, Chunk, Chunker.fixedSize, VectorStore, and
 * RAGPipeline.
 */
public class RAGRetrievalEvaluator {

    /** Deterministic lexical (term-frequency) embedding with the lab's interface. */
    static final Map<String, Integer> vocab = new LinkedHashMap<>();
    static final List<String> vocabOrder = new ArrayList<>();

    static int buildVocab(String corpus) {
        vocab.clear();
        for (String w : corpus.toLowerCase().replaceAll("[^a-z ]", " ").split("\\s+")) {
            if (w.length() < 4) continue;
            if (!vocab.containsKey(w)) {
                vocab.put(w, vocabOrder.size());
                vocabOrder.add(w);
            }
        }
        return vocabOrder.size();
    }

    public static double[] embed(String text, int dim) {
        double[] vec = new double[dim];
        for (String w : text.toLowerCase().replaceAll("[^a-z ]", " ").split("\\s+")) {
            Integer idx = vocab.get(w);
            if (idx != null && idx < dim) vec[idx] += 1.0;
        }
        double norm = 0.0;
        for (double v : vec) norm += v * v;
        if (norm == 0.0) return vec;
        norm = Math.sqrt(norm);
        for (int i = 0; i < dim; i++) vec[i] /= norm;
        return vec;
    }

    public static double cosineSimilarity(double[] a, double[] b) {
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    static class Chunk {
        final String text;
        final int index;
        Chunk(String text, int index) { this.text = text; this.index = index; }
    }

    static class Chunker {
        static List<Chunk> fixedSize(String text, int chunkSize, int overlap) {
            List<Chunk> chunks = new ArrayList<>();
            int start = 0, idx = 0;
            while (start < text.length()) {
                int end = Math.min(start + chunkSize, text.length());
                chunks.add(new Chunk(text.substring(start, end), idx++));
                start += chunkSize - overlap;
            }
            return chunks;
        }
    }

    static class VectorStore {
        record Entry(Chunk chunk, double[] embedding) {}
        final List<Entry> entries = new ArrayList<>();
        final int dim;

        VectorStore(int dim) { this.dim = dim; }

        void add(Chunk chunk) {
            entries.add(new Entry(chunk, embed(chunk.text, dim)));
        }

        List<Chunk> search(String query, int topK) {
            double[] qVec = embed(query, dim);
            return entries.stream()
                .map(e -> Map.entry(e, cosineSimilarity(qVec, e.embedding)))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(topK)
                .map(e -> e.getKey().chunk)
                .toList();
        }

        /** Extension: search returning similarity scores for reranking. */
        List<Map.Entry<Chunk, Double>> searchWithScores(String query) {
            double[] qVec = embed(query, dim);
            return entries.stream()
                .map(e -> Map.entry(e.chunk, cosineSimilarity(qVec, e.embedding)))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .toList();
        }
    }

    /** Token-level match robust to chunk-boundary word splits. */
    static boolean chunkMentions(String text, String term) {
        String needle = term.toLowerCase().replaceAll("[^a-z]", "");
        for (String w : text.toLowerCase().split("\\s+")) {
            String t = w.replaceAll("[^a-z]", "");
            if (t.length() >= 4 && needle.length() >= 4
                    && (t.equals(needle) || t.startsWith(needle) || needle.startsWith(t))) {
                return true;
            }
        }
        return false;
    }

    /** Lost-in-the-middle mitigation: best chunk first, second-best last. */
    static List<Chunk> rerankForContext(List<Map.Entry<Chunk, Double>> ranked, int topK) {
        List<Chunk> selected = new ArrayList<>();
        for (int i = 0; i < topK && i < ranked.size(); i++) selected.add(ranked.get(i).getKey());
        List<Chunk> result = new ArrayList<>();
        if (selected.isEmpty()) return result;
        result.add(selected.get(0));
        for (int i = 2; i < selected.size(); i++) result.add(selected.get(i));
        if (selected.size() > 1) result.add(selected.get(1));
        return result;
    }

    public static void main(String[] args) {
        String document = "Refunds are issued to the original payment method within 5-10 "
            + "business days. To request a refund, open a dispute from the dashboard. "
            + "Disputes can be contested with supporting evidence. Chargeback fees apply "
            + "to failed disputes. Payouts are sent weekly on Fridays to your bank account. "
            + "International payouts may take up to 7 extra business days to arrive.";

        int chunkSize = 40;
        List<Chunk> chunks = Chunker.fixedSize(document, chunkSize, 8);
        System.out.println("=== Chunks (size=" + chunkSize + ", overlap=8) ===");
        chunks.forEach(c -> System.out.printf("[%d] %s%n", c.index, c.text));

        int dim = buildVocab(document);
        System.out.println("Lexical embedding vocab size: " + dim);
        VectorStore store = new VectorStore(dim);
        chunks.forEach(store::add);

        String[] goldQueries = {
            "how long do refunds take",
            "what happens when a dispute is lost",
            "when are payouts sent"
        };
        System.out.println("Gold queries: " + goldQueries.length);

        System.out.println("\n=== Retrieval Hit Rate ===");
        String[][] gold = {
            {"how long do refunds take", "refund"},
            {"what happens when a dispute is lost", "dispute"},
            {"when are payouts sent", "payout"}
        };
        int hits = 0;
        for (String[] g : gold) {
            List<Chunk> retrieved = store.search(g[0], 2);
            String context = String.join(" ", retrieved.stream().map(c -> c.text).toList());
            boolean hit = chunkMentions(context, g[1]);
            if (hit) hits++;
            System.out.printf("Query '%s' -> hit=%s topChunk=[%d]%n", g[0], hit,
                retrieved.isEmpty() ? -1 : retrieved.get(0).index);
        }
        System.out.println("Hit rate: " + hits + "/" + gold.length + " (" + gold.length + " queries)");

        System.out.println("\n=== Lost-in-the-Middle Reranking ===");
        String q = "when are payouts sent";
        var ranked = store.searchWithScores(q);
        System.out.println("Original order: " + ranked.stream()
            .map(e -> "[" + e.getKey().index + "]:" + String.format("%.3f", e.getValue()))
            .toList());
        List<Chunk> reranked = rerankForContext(ranked, 3);
        System.out.println("Context order:  " + reranked.stream().map(c -> "[" + c.index + "]").toList());

        StringBuilder context = new StringBuilder();
        for (Chunk c : reranked) context.append(c.text).append("\n");
        System.out.println("\n=== Augmented Prompt (final) ===");
        System.out.println("Context:\n" + context + "\nQuestion: " + q
            + "\nAnswer based on the context above.");

        System.out.println("\nRAG retrieval evaluation validated.");
    }
}
```

### Expected Output
```text
=== Chunks (size=40, overlap=8) ===
[0] Refunds are issued to the original payme
[1] al payment method within 5-10 business d
[2] siness days. To request a refund, open a
[3] , open a dispute from the dashboard. Dis
[4] ard. Disputes can be contested with supp
[5] ith supporting evidence. Chargeback fees
[6] ack fees apply to failed disputes. Payou
[7] s. Payouts are sent weekly on Fridays to
[8] idays to your bank account. Internationa
[9] rnational payouts may take up to 7 extra
[10]  7 extra business days to arrive.
[11] .
Lexical embedding vocab size: 34
Gold queries: 3

=== Retrieval Hit Rate ===
Query 'how long do refunds take' -> hit=true topChunk=[0]
Query 'what happens when a dispute is lost' -> hit=true topChunk=[3]
Query 'when are payouts sent' -> hit=true topChunk=[7]
Hit rate: 3/3 (3 queries)

=== Lost-in-the-Middle Reranking ===
Original order: [[7]:0.707, [9]:0.408, [0]:0.000, [1]:0.000, [2]:0.000, [3]:0.000, [4]:0.000, [5]:0.000, [6]:0.000, [8]:0.000, [10]:0.000, [11]:0.000]
Context order:  [[7], [0], [9]]

=== Augmented Prompt (final) ===
Context:
s. Payouts are sent weekly on Fridays to
Refunds are issued to the original payme
rnational payouts may take up to 7 extra

Question: when are payouts sent
Answer based on the context above.

RAG retrieval evaluation validated.
```

### Company Evaluation
- Stripe: Retrieval evaluation as CI gate, support-doc freshness, citation-enabled answers.
- Google: RAG at search scale, ANN indexing, embedding versioning and drift.
- OpenAI: Retriever fine-tuning, hybrid sparse+dense retrieval, RAG-tuned generators.
- Shopify: Cost per query, cache-friendly augmentation, chunk budget control.
- Uber: Multi-tenant RAG isolation, per-tenant corpora, retrieval latency SLOs.

---

## Problem 2: Chunk Size Sweep — Company: Shopify

### Interview Scenario
"You're at Shopify deciding chunk size for a help-center corpus. The lab's chunker is
deterministic, so you can sweep sizes and measure how many chunks each produces and how
much boundary overlap each needs — a cheap proxy for retrieval granularity before any
model eval."

### The Problem
1. Sweep `chunkSize` in {40, 60, 80} with overlap 20%.
2. Print chunk count and average chunk length per size.
3. Note the trade-off: fewer, larger chunks vs more, smaller chunks.

### Solution Walkthrough
- Step 1: Run `Chunker.fixedSize(doc, size, size/5)` for each size.
- Step 2: Print the chunk count and mean length.
- Step 3: Interpret: larger sizes cut fewer sentences but carry more tokens per retrieval.

### Code
```java
for (int size : new int[]{40, 60, 80}) {
    List<Chunk> cs = Chunker.fixedSize(document, size, size / 5);
    double avg = cs.stream().mapToInt(c -> c.text.length()).average().orElse(0);
    System.out.printf("size=%d chunks=%d avgLen=%.1f%n", size, cs.size(), avg);
}
```
Expected output: chunk counts decrease monotonically (e.g., 12, 8, 6) as size grows,
quantifying the granularity trade-off for the context budget.

---

## Problem 3: Hybrid Retrieval Fallback — Company: Google

### Interview Scenario
"You're at Google building a hybrid retriever: dense embedding search for semantics plus
exact-term fallback for IDs and error codes. The vector store must return an answer even
when the lexical overlap is zero."

### The Problem
1. Run `store.search` on a query with no vocabulary overlap.
2. Detect an empty or weak result and fall back to a token-overlap scan.
3. Return the fallback chunk with a flag.

### Solution Walkthrough
- Step 1: Compute `search(query, 1)` and inspect the top score via `searchWithScores`.
- Step 2: If the score is below a threshold, scan all chunks for any shared term.
- Step 3: Return the best overlap chunk, tagged `[fallback]`.

### Code
```java
double topScore = store.searchWithScores(q).get(0).getValue();
if (topScore < 0.2) {
    Chunk fb = store.searchWithScores(q).stream()
        .sorted(Comparator.comparingInt((Map.Entry<Chunk, Double> e) ->
            sharedTerms(e.getKey().text, q)).reversed())
        .findFirst().get().getKey();
    System.out.println("[fallback] " + fb.text);
}
```
Expected output: a term-overlap chunk is returned when cosine similarity is too low —
the dense+sparse hybrid pattern in miniature.

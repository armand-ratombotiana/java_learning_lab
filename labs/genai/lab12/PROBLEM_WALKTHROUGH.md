# Problem Walkthrough: Cost Optimization for LLMs

## Problem 1: Cache and Batching Savings Ledger — Company: Shopify

### Interview Scenario
"You're at Shopify estimating what the lab's cost levers save on the support bot.
Using `ExactCache`, `SemanticCache`, `DynamicBatcher`, `PromptCompressor`, and
`SpeculativeDecoder`, simulate a 40-request workload and a 100-request burst,
then total the inference calls, tokens, and dollars saved."

### The Problem
1. Warm an exact cache with 4 questions; serve 40 requests (all repeats).
2. Replace the lab's random `embed` with a deterministic lexical embedding so the
   semantic cache produces real paraphrase hits and misses.
3. Drain 100 queued requests through a batch-size-8 batcher.
4. Compress a 16-token prompt and count token savings.
5. Run speculative decoding with a reproducible acceptance rate.

### Solution Walkthrough
- Step 1: Copy `ExactCache`, `SemanticCache`, `DynamicBatcher`, `PromptCompressor`,
  and `SpeculativeDecoder` verbatim.
- Step 2: The exact cache converts 40 calls to 0: hits 40/40, $0.08 → $0.00.
- Step 3: `LexicalSemanticCache extends SemanticCache`, overriding `embed` with a
  count vector over a growing vocab; re-embed stored entries when the vocab grows,
  and compute similarity over the shorter of the two vectors (the lab's hardcoded
  `for (int i = 0; i < 8; i++)` overflows once vocab exceeds 8).
- Step 4: Threshold 0.7: exact repeat (1.0) and 'capital of France' (0.7071) hit;
  'France's capital city?' (0.408) and 'Is it raining in Rome?' (~0.18) miss.
- Step 5: Batcher: 13 batches for 100 requests; compressor: 16 → 10 tokens (38%);
  seeded verification: 0.88 acceptance.

### Code
```java
package com.genai.lab12.solution;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lab 12 walkthrough: cost optimization simulator. Reuses the lab's
 * ExactCache, SemanticCache, DynamicBatcher, PromptCompressor and
 * SpeculativeDecoder, adds a deterministic lexical embedding so the
 * semantic cache produces real paraphrase hits, and totals the
 * inference calls saved.
 */
public class CacheCostSimulator {

    /** LRU exact cache. */
    static class ExactCache {
        final LinkedHashMap<String, String> cache;
        int hits = 0, misses = 0;

        ExactCache(int maxSize) {
            cache = new LinkedHashMap<>(maxSize, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                    return size() > maxSize;
                }
            };
        }

        synchronized String get(String query) {
            String result = cache.get(query);
            if (result != null) hits++; else misses++;
            return result;
        }

        synchronized void put(String query, String response) {
            cache.put(query, response);
        }

        double hitRate() { return (double) hits / (hits + misses); }
    }

    /** Semantic cache using embedding similarity. */
    static class SemanticCache {
        record Entry(String query, String response, double[] embedding) {}
        final List<Entry> entries = new ArrayList<>();
        final double threshold;

        SemanticCache(double threshold) { this.threshold = threshold; }

        double[] embed(String text) {
            double[] vec = new double[8];
            Random rng = new Random(text.hashCode());
            for (int i = 0; i < 8; i++) vec[i] = rng.nextGaussian();
            double norm = Math.sqrt(Arrays.stream(vec).map(v -> v * v).sum());
            for (int i = 0; i < 8; i++) vec[i] /= norm;
            return vec;
        }

        String get(String query) {
            double[] qEmb = embed(query);
            for (Entry e : entries) {
                double sim = 0;
                for (int i = 0; i < 8; i++) sim += qEmb[i] * e.embedding[i];
                if (sim > threshold) return e.response;
            }
            return null;
        }

        void put(String query, String response) {
            entries.add(new Entry(query, response, embed(query)));
        }
    }

    /** Deterministic lexical embedding over a shared vocabulary. */
    static class LexicalSemanticCache extends SemanticCache {
        final List<String> vocab = new ArrayList<>();

        LexicalSemanticCache(double threshold) { super(threshold); }

        static List<String> tokens(String text) {
            return Arrays.asList(text.toLowerCase().split("[^a-z]+"));
        }

        double[] embed(String text) {
            double[] vec = new double[vocab.size()];
            for (String t : tokens(text)) {
                int idx = vocab.indexOf(t);
                if (idx >= 0) vec[idx] += 1;
            }
            double norm = Math.sqrt(Arrays.stream(vec).map(v -> v * v).sum());
            if (norm > 0) for (int i = 0; i < vec.length; i++) vec[i] /= norm;
            return vec;
        }

        String get(String query) {
            double[] qEmb = embed(query);
            for (Entry e : entries) {
                double sim = 0;
                int dims = Math.min(qEmb.length, e.embedding.length);
                for (int i = 0; i < dims; i++) sim += qEmb[i] * e.embedding[i];
                if (sim > threshold) return e.response;
            }
            return null;
        }

        void put(String query, String response) {
            for (String t : tokens(query)) {
                if (!vocab.contains(t)) vocab.add(t);
            }
            List<Entry> reembedded = new ArrayList<>();
            for (Entry e : entries) {
                reembedded.add(new Entry(e.query, e.response, embed(e.query)));
            }
            entries.clear();
            entries.addAll(reembedded);
            super.put(query, response);
        }
    }

    /** Dynamic batcher. */
    static class DynamicBatcher {
        final List<String> queue = new ArrayList<>();
        final int maxBatchSize;

        DynamicBatcher(int maxBatchSize) { this.maxBatchSize = maxBatchSize; }

        synchronized void add(String request) { queue.add(request); }

        synchronized List<String> drain() {
            if (queue.isEmpty()) return List.of();
            int batchSize = Math.min(queue.size(), maxBatchSize);
            List<String> batch = new ArrayList<>(queue.subList(0, batchSize));
            queue.subList(0, batchSize).clear();
            return batch;
        }

        List<String> processBatch(List<String> batch) {
            return batch.stream().map(r -> "Processed: " + r).toList();
        }
    }

    /** Prompt compression via stop-word removal. */
    static class PromptCompressor {
        final Set<String> stopWords = Set.of("the", "a", "an", "is", "are", "was", "were",
            "in", "on", "at", "to", "for", "of", "and", "or", "but");

        String compress(String prompt) {
            return Arrays.stream(prompt.toLowerCase().split("\\s+"))
                .filter(w -> !stopWords.contains(w))
                .reduce((a, b) -> a + " " + b)
                .orElse("");
        }
    }

    /** Speculative decoding simulation (deterministic verify). */
    static class SpeculativeDecoder {
        static int[] draft(String prompt, int length) {
            int[] tokens = new int[length];
            Random rng = new Random(prompt.hashCode());
            for (int i = 0; i < length; i++) tokens[i] = rng.nextInt(100);
            return tokens;
        }

        static double verify(int[] draftTokens) {
            Random rng = new Random(7);
            return Arrays.stream(draftTokens).filter(t -> rng.nextDouble() > 0.2).count()
                / (double) draftTokens.length;
        }
    }

    public static void main(String[] args) {
        double costPerRequest = 0.002;

        System.out.println("=== Exact Cache (4 unique questions, 40 requests) ===");
        ExactCache cache = new ExactCache(100);
        List<String> qs = List.of("What is AI?", "What is ML?",
            "Explain transformers", "What is a vector database?");
        for (String q : qs) cache.put(q, "Answer for: " + q);
        int exactHits = 0;
        for (int i = 0; i < 40; i++) {
            String q = qs.get(i % qs.size());
            if (cache.get(q) != null) exactHits++;
        }
        System.out.println("Hits: " + exactHits + ", Misses: " + (40 - exactHits));
        System.out.printf("Hit rate: %.2f%n", cache.hitRate());
        System.out.printf("Cost without cache: $%.4f (40 calls)%n", 40 * costPerRequest);
        System.out.printf("Cost with cache:    $%.4f (%d calls)%n",
            (40 - exactHits) * costPerRequest, 40 - exactHits);
        System.out.printf("Saved: $%.4f (%.0f%%)%n", exactHits * costPerRequest,
            100.0 * exactHits / 40);

        System.out.println("\n=== Semantic Cache (lexical embeddings, threshold 0.7) ===");
        LexicalSemanticCache sc = new LexicalSemanticCache(0.7);
        sc.put("What is the capital of France?", "Paris");
        sc.put("How do I make pasta?", "Boil water, add pasta, strain.");
        List<String> queries = List.of(
            "What is the capital of France?",
            "capital of France",
            "France's capital city?",
            "How do I make pasta?",
            "Is it raining in Rome?");
        for (String q : queries) {
            String r = sc.get(q);
            System.out.printf("  '%-32s' -> %s%n", q, r == null ? "MISS (full inference)" : "HIT: " + r);
        }

        System.out.println("\n=== Dynamic Batching (100 requests, batch size 8) ===");
        DynamicBatcher batcher = new DynamicBatcher(8);
        for (int i = 0; i < 100; i++) batcher.add("Request " + i);
        int batches = 0, total = 0;
        List<String> batch;
        while (!(batch = batcher.drain()).isEmpty()) {
            batches++;
            total += batch.size();
        }
        System.out.printf("Batches: %d, requests: %d -> %d forward passes vs 100%n",
            batches, total, batches);

        System.out.println("\n=== Prompt Compression ===");
        String prompt = "The quick brown fox jumps over the lazy dog and runs to the river for water";
        String compressed = new PromptCompressor().compress(prompt);
        System.out.println("Original:   '" + prompt + "' (" + prompt.split("\\s+").length + " tokens)");
        System.out.println("Compressed: '" + compressed + "' (" + compressed.split("\\s+").length + " tokens)");
        System.out.printf("Token reduction: %.0f%%%n",
            100.0 * (prompt.split("\\s+").length - compressed.split("\\s+").length)
                / prompt.split("\\s+").length);

        System.out.println("\n=== Speculative Decoding ===");
        int[] draft = SpeculativeDecoder.draft("hello", 8);
        double acceptRate = SpeculativeDecoder.verify(draft);
        System.out.printf("Draft tokens: %d, acceptance rate: %.2f%n", draft.length, acceptRate);

        System.out.println("\nCost optimization concepts validated.");
    }
}
```

### Expected Output
```text
=== Exact Cache (4 unique questions, 40 requests) ===
Hits: 40, Misses: 0
Hit rate: 1.00
Cost without cache: $0.0800 (40 calls)
Cost with cache:    $0.0000 (0 calls)
Saved: $0.0800 (100%)

=== Semantic Cache (lexical embeddings, threshold 0.7) ===
  'What is the capital of France?  ' -> HIT: Paris
  'capital of France               ' -> HIT: Paris
  'France's capital city?          ' -> MISS (full inference)
  'How do I make pasta?            ' -> HIT: Boil water, add pasta, strain.
  'Is it raining in Rome?          ' -> MISS (full inference)

=== Dynamic Batching (100 requests, batch size 8) ===
Batches: 13, requests: 100 -> 13 forward passes vs 100

=== Prompt Compression ===
Original:   'The quick brown fox jumps over the lazy dog and runs to the river for water' (16 tokens)
Compressed: 'quick brown fox jumps over lazy dog runs river water' (10 tokens)
Token reduction: 38%

=== Speculative Decoding ===
Draft tokens: 8, acceptance rate: 0.88

Cost optimization concepts validated.
```

### Company Evaluation
- Shopify: Cache-first cost engineering for high-volume support traffic.
- Stripe: Per-request cost accounting, caching hot queries.
- Amazon: Batch-heavy serving for throughput workloads.

---

## Problem 2: Semantic Cache Threshold Tuning — Company: Stripe

### Interview Scenario
"You're at Stripe tuning the semantic cache. The threshold decides between wrong
hits (too low) and missed savings (too high). Show the boundary."

### The Problem
1. A paraphrase shares 3 of 6 words with the cached query → sim 0.7071.
2. A near-topic question shares 2 of 4 words → sim 0.408.
3. Place the threshold between them.

### Solution Walkthrough
- Step 1: Compute cosines with the lexical embeddings from Problem 1.
- Step 2: Threshold 0.7: 0.7071 hits, 0.408 misses.
- Step 3: Note the failure mode below threshold: 0.5-similarity queries (shared
  stopwords only) would wrongly return cached answers.

### Code
```java
// Cosine between cached "What is the capital of France?" (6 words) and:
String q1 = "capital of France";          // 3 shared -> 3/(sqrt(3)*sqrt(6)) = 0.7071
String q2 = "France's capital city?";     // 2 shared -> 2/(sqrt(4)*sqrt(6)) = 0.4082
LexicalSemanticCache sc = new LexicalSemanticCache(0.7);
sc.put("What is the capital of France?", "Paris");
System.out.println("Paraphrase -> " + sc.get(q1));
System.out.println("Near-topic -> " + (sc.get(q2) == null ? "MISS" : sc.get(q2)));
```
Expected output:
```text
Paraphrase -> Paris
Near-topic -> MISS
```
The margin is thin (0.7071 vs 0.4082) — in production you'd measure the wrong-hit
rate on real traffic before trusting any single threshold.

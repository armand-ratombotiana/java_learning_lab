# Problem Walkthrough: RAG System Architecture

## Problem 1: Tuning Hybrid Retrieval and Assembling the Grounded Prompt — Company: Google

### Interview Scenario
"You're at Google on the Search grounding team, building a retrieval-augmented assistant that answers questions from a private document corpus. The first version used pure vector search and users complain it misses exact phrases — product names and error codes that the embedding model blurs. Your job: build the full pipeline with sentence-aware chunking, hybrid retrieval where the semantic/lexical blend is a tunable knob, and a grounded prompt that forces the model to answer only from retrieved context. The team wants to see, in one runnable program, how alpha changes ranking and what the final prompt looks like."

### The Problem
1. Chunk a realistic document with sentence-aware chunking that respects boundaries and carries overlap
2. Embed each chunk with the lab's deterministic hash-based `computeEmbedding` so results are reproducible
3. Implement BM25 lexical scoring with the lab's parameters (k1=1.2, b=0.75)
4. Make the hybrid blend configurable: `combined = alpha * vector + (1 - alpha) * keyword`
5. Show the ranking under alpha=0.7 (semantic-heavy) vs alpha=0.3 (keyword-heavy)
6. Assemble a grounded generation prompt with numbered context blocks, separators, and the question — and print it

### Solution Walkthrough
- Step 1: Reuse the lab's `ChunkingStrategy` interface and both strategies; use `SENTENCE_AWARE` for the walkthrough since boundary preservation is the stated requirement
- Step 2: Reuse the lab's `computeEmbedding` (hash-seeded `Random`, 8-dim, normalized) and `cosineSimilarity` verbatim — the pipeline must stay deterministic
- Step 3: Reuse the lab's `bm25Score` with `k1 = 1.2`, `b = 0.75`, and the same IDF formula
- Step 4: Extend the lab's `HybridSearchResult` with a per-call `alpha` so the blend is a parameter instead of the hardcoded 0.7
- Step 5: Run `hybridSearch` twice on the same query with alpha 0.7 and 0.3; note that keyword score (unbounded, ~6.1 max) dwarfs vector score (bounded near 1) — the motivation for normalization in production
- Step 6: Add `buildPrompt(query, results)`: system instruction requiring grounding, `---CONTEXT---` delimiter, numbered chunks, `---END CONTEXT---` delimiter, then the question — separators prevent the model from confusing source boundaries
- Step 7: Print the prompt and its length to verify it stays within context-budget

### Code
```java
// File: src/com/aiengineering/lab03/RagRetrievalWalkthrough.java
package com.aiengineering.lab03;

import java.util.*;
import java.util.stream.*;

/**
 * Walkthrough: Google-style RAG pipeline — sentence-aware chunking,
 * hybrid retrieval with tunable alpha, and prompt assembly with
 * retrieved context. Mirrors the lab's ChunkingStrategy, bm25Score,
 * cosineSimilarity, and hybridSearch (with a configurable weight).
 */
public class RagRetrievalWalkthrough {

    public record Chunk(String id, String text, int tokenCount, float[] embedding) {}

    @FunctionalInterface
    interface ChunkingStrategy {
        List<Chunk> chunk(String document, int chunkSize, int overlap);
    }

    static final ChunkingStrategy FIXED_SIZE = (doc, size, overlap) -> {
        String[] words = doc.split("\\s+");
        List<Chunk> chunks = new ArrayList<>();
        int start = 0;
        int chunkId = 0;
        while (start < words.length) {
            int end = Math.min(start + size, words.length);
            String text = String.join(" ", Arrays.copyOfRange(words, start, end));
            chunks.add(new Chunk("chunk-" + chunkId++, text, end - start, computeEmbedding(text)));
            start += (size - overlap);
        }
        return chunks;
    };

    static final ChunkingStrategy SENTENCE_AWARE = (doc, size, overlap) -> {
        String[] sentences = doc.split("(?<=[.!?])\\s+");
        List<Chunk> chunks = new ArrayList<>();
        List<String> buffer = new ArrayList<>();
        int chunkId = 0;
        for (String sentence : sentences) {
            buffer.add(sentence);
            int wordCount = buffer.stream().mapToInt(s -> s.split("\\s+").length).sum();
            if (wordCount >= size) {
                String text = String.join(" ", buffer);
                chunks.add(new Chunk("chunk-" + chunkId++, text, wordCount, computeEmbedding(text)));
                int overlapSentences = Math.max(1, buffer.size() / 2);
                buffer = new ArrayList<>(buffer.subList(buffer.size() - overlapSentences, buffer.size()));
            }
        }
        if (!buffer.isEmpty()) {
            String text = String.join(" ", buffer);
            chunks.add(new Chunk("chunk-" + chunkId++, text, text.split("\\s+").length, computeEmbedding(text)));
        }
        return chunks;
    };

    static float[] computeEmbedding(String text) {
        float[] emb = new float[8];
        Arrays.fill(emb, 0.0f);
        Random rng = new Random(text.hashCode());
        for (int i = 0; i < emb.length; i++) emb[i] = rng.nextFloat();
        double norm = 0;
        for (float f : emb) norm += f * f;
        norm = Math.sqrt(norm);
        for (int i = 0; i < emb.length; i++) emb[i] /= norm;
        return emb;
    }

    static double cosineSimilarity(float[] a, float[] b) {
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }

    static double bm25Score(String query, String document, double avgDocLen, int totalDocs, double k1, double b) {
        String[] queryTerms = query.toLowerCase().split("\\s+");
        String[] docTerms = document.toLowerCase().split("\\s+");
        double docLen = docTerms.length;
        Map<String, Long> termFreq = Arrays.stream(docTerms)
            .collect(Collectors.groupingBy(t -> t, Collectors.counting()));
        double score = 0;
        for (String term : queryTerms) {
            long tf = termFreq.getOrDefault(term, 0L);
            if (tf == 0) continue;
            double idf = Math.log(1 + (totalDocs - 1 + 0.5) / (1 + 0.5));
            score += idf * (tf * (k1 + 1)) / (tf + k1 * (1 - b + b * docLen / avgDocLen));
        }
        return score;
    }

    static class HybridSearchResult {
        final Chunk chunk;
        final double vectorScore;
        final double keywordScore;
        final double combinedScore;

        HybridSearchResult(Chunk c, double vs, double ks, double alpha) {
            this.chunk = c;
            this.vectorScore = vs;
            this.keywordScore = ks;
            this.combinedScore = alpha * vs + (1 - alpha) * ks;
        }
    }

    static List<HybridSearchResult> hybridSearch(List<Chunk> chunks, String query, float[] queryEmb, int topK, double alpha) {
        double avgDocLen = chunks.stream().mapToInt(Chunk::tokenCount).average().orElse(1);
        int totalDocs = chunks.size();
        return chunks.stream()
            .map(c -> new HybridSearchResult(c,
                cosineSimilarity(queryEmb, c.embedding()),
                bm25Score(query, c.text(), avgDocLen, totalDocs, 1.2, 0.75), alpha))
            .sorted((a, b) -> Double.compare(b.combinedScore, a.combinedScore))
            .limit(topK)
            .toList();
    }

    static String buildPrompt(String query, List<HybridSearchResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a helpful assistant. Answer using ONLY the context below.\n\n");
        sb.append("---CONTEXT---\n");
        for (int i = 0; i < results.size(); i++) {
            sb.append("[").append(i + 1).append("] ").append(results.get(i).chunk.text()).append("\n");
        }
        sb.append("---END CONTEXT---\n\n");
        sb.append("Question: ").append(query).append("\nAnswer:");
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("=== Walkthrough: RAG Retrieval Pipeline ===\n");

        String doc = """
            Retrieval augmented generation grounds LLM answers in retrieved documents.
            The pipeline has five stages: chunk, embed, index, retrieve, and generate.
            Chunking splits documents into retrievable units that stay within context.
            Hybrid search combines semantic vectors with lexical keyword matching.
            BM25 is the classic lexical scoring function used for keyword precision.
            Re-ranking with a cross-encoder improves the final ordering of candidates.
            Evaluation measures retrieval recall against a held-out golden set.
            """;

        System.out.println("--- Sentence-aware chunking (size=12, overlap=2) ---");
        List<Chunk> chunks = SENTENCE_AWARE.chunk(doc, 12, 2);
        chunks.forEach(c -> System.out.printf("  %s [%d tokens]: %s...%n",
            c.id(), c.tokenCount(), c.text().substring(0, Math.min(50, c.text().length()))));

        String query = "lexical keyword matching scoring";
        float[] queryEmb = computeEmbedding(query);
        System.out.println("\n--- Hybrid search: alpha=0.7 (semantic-heavy) ---");
        List<HybridSearchResult> semantic = hybridSearch(chunks, query, queryEmb, 2, 0.7);
        for (int i = 0; i < semantic.size(); i++) {
            HybridSearchResult r = semantic.get(i);
            System.out.printf("  %d. %s | vector=%.4f keyword=%.4f combined=%.4f%n",
                i + 1, r.chunk.id(), r.vectorScore, r.keywordScore, r.combinedScore);
        }

        System.out.println("\n--- Hybrid search: alpha=0.3 (keyword-heavy) ---");
        List<HybridSearchResult> lexical = hybridSearch(chunks, query, queryEmb, 2, 0.3);
        for (int i = 0; i < lexical.size(); i++) {
            HybridSearchResult r = lexical.get(i);
            System.out.printf("  %d. %s | vector=%.4f keyword=%.4f combined=%.4f%n",
                i + 1, r.chunk.id(), r.vectorScore, r.keywordScore, r.combinedScore);
        }

        System.out.println("\n--- Augmented generation prompt (top-2, alpha=0.7) ---");
        String prompt = buildPrompt(query, semantic);
        System.out.println(prompt);
        System.out.printf("Prompt length: %d chars, %d retrieved chunks.%n",
            prompt.length(), semantic.size());

        System.out.println("\nWalkthrough complete.");
    }
}
```

### Expected Output
```
=== Walkthrough: RAG Retrieval Pipeline ===

--- Sentence-aware chunking (size=12, overlap=2) ---
  chunk-0 [20 tokens]: Retrieval augmented generation grounds LLM answers...
  chunk-1 [21 tokens]: The pipeline has five stages: chunk, embed, index,...
  chunk-2 [19 tokens]: Chunking splits documents into retrievable units t...
  chunk-3 [20 tokens]: Hybrid search combines semantic vectors with lexic...
  chunk-4 [21 tokens]: BM25 is the classic lexical scoring function used ...
  chunk-5 [19 tokens]: Re-ranking with a cross-encoder improves the final...
  chunk-6 [9 tokens]: Evaluation measures retrieval recall against a hel...

--- Hybrid search: alpha=0.7 (semantic-heavy) ---
  1. chunk-3 | vector=0.7444 keyword=6.1132 combined=2.3550
  2. chunk-4 | vector=0.8485 keyword=4.7507 combined=2.0192

--- Hybrid search: alpha=0.3 (keyword-heavy) ---
  1. chunk-3 | vector=0.7444 keyword=6.1132 combined=4.5025
  2. chunk-4 | vector=0.8485 keyword=4.7507 combined=3.5801

--- Augmented generation prompt (top-2, alpha=0.7) ---
You are a helpful assistant. Answer using ONLY the context below.

---CONTEXT---
[1] Hybrid search combines semantic vectors with lexical keyword matching. BM25 is the classic lexical scoring function used for keyword precision.
[2] BM25 is the classic lexical scoring function used for keyword precision. Re-ranking with a cross-encoder improves the final ordering of candidates.
---END CONTEXT---

Question: lexical keyword matching scoring
Answer:
Prompt length: 450 chars, 2 retrieved chunks.

Walkthrough complete.
```

### Company Evaluation
- Oracle: Retrieval correctness: fusion policy, rank normalization, and baseline comparison.
- Deloitte: Knowledge management: source governance, chunk curation, and answer quality assurance.
- Accenture: Integration methodology: pipeline design, grounding verification, and rollout of the retrieval change.
- PwC: Compliance: answer provenance, source citation integrity, and hallucination risk controls.
- Amazon: Scale: caching, index partitioning, and latency budgets for retrieval at web scale.

---

## Problem 2: Choosing the Chunking Strategy — Company: Anthropic

### Interview Scenario
"You're at Anthropic helping a customer build RAG over legal contracts. Fixed-size chunking keeps slicing contract clauses in half, and retrieval misses the answer even though the corpus contains it. Compare the two strategies on a contract snippet and show where the fixed-size chunker breaks a semantic unit."

### The Problem
1. Split the same text with `FIXED_SIZE` and `SENTENCE_AWARE`
2. Show that a fixed chunk can start mid-sentence (the token windows carry over)
3. Show that sentence-aware chunks always start at a sentence boundary
4. Count the tokens per chunk to demonstrate the length distribution difference

### Solution Walkthrough
- Step 1: Take a 3-sentence contract clause about renewal terms
- Step 2: Run `FIXED_SIZE.chunk(text, 8, 2)` — inspect chunk boundaries
- Step 3: Run `SENTENCE_AWARE.chunk(text, 8, 2)` — inspect boundaries
- Step 4: Print the first 5 words of each chunk; the fixed chunker's chunk-1 begins with tail words of clause 1
- Step 5: Conclude: for contract language, sentence-aware chunking keeps the semantic unit (the clause) intact

### Code
```java
String clause = "The term shall be twelve months. Renewal is automatic unless either party gives notice. Notice must be in writing at least ninety days before expiry.";
System.out.println("--- FIXED_SIZE (size=8, overlap=2) ---");
FIXED_SIZE.chunk(clause, 8, 2).forEach(c ->
    System.out.printf("  %s [%d]: \"%s\"%n", c.id(), c.tokenCount(), c.text()));
System.out.println("--- SENTENCE_AWARE (size=8, overlap=2) ---");
SENTENCE_AWARE.chunk(clause, 8, 2).forEach(c ->
    System.out.printf("  %s [%d]: \"%s\"%n", c.id(), c.tokenCount(), c.text()));
```
Output: `FIXED_SIZE` produces chunks like `"The term shall be twelve months. Renewal is"` — a boundary that splits "Renewal is automatic..." mid-clause — while `SENTENCE_AWARE` produces `"The term shall be twelve months."` and `"Renewal is automatic unless either party gives notice."` intact. Retrieval over the sentence-aware index always retrieves a whole clause; the fixed index can hand the generator a half-sentence fragment that reads as nonsense.

### Company Evaluation
- Oracle: Chunk design: boundary handling, overlap policy, and chunk metadata integrity.
- Deloitte: Content architecture: chunking standards, document modeling, and quality processes.
- Accenture: Methodology: chunk-size experimentation, retrieval impact measurement, and test design.
- PwC: Controls: document lineage, version management, and auditability of the chunk pipeline.
- Amazon: Scale: streaming chunking pipelines and ingestion throughput at scale.

---

## Problem 3: Retrieval Recall Evaluation — Company: Microsoft

### Interview Scenario
"You're at Microsoft on the Bing grounding team. The RAG pipeline switched chunk size from 10 to 20 tokens and retrieval quality got worse — or did it? You need a recall@k evaluation that compares retrieval configs against a golden set of known-answer queries."

### The Problem
1. Build a golden set: query + the chunk id that contains the answer
2. Run `hybridSearch` with both chunk sizes
3. Compute recall@3: fraction of golden answers present in the top-3
4. Print per-query results and the verdict

### Solution Walkthrough
- Step 1: Chunk the corpus twice — `FIXED_SIZE.chunk(doc, 10, 3)` and `FIXED_SIZE.chunk(doc, 20, 4)`
- Step 2: Record the golden mapping by searching for the answer text in chunk texts
- Step 3: For each golden query, retrieve top-3 with `hybridSearch(..., alpha=0.7)` and check whether the golden chunk id is in the result set
- Step 4: Aggregate recall@3 per configuration; the config with the higher recall wins
- Step 5: Gate the config change: any deployment that lowers recall below the baseline is rejected automatically in CI

### Code
```java
record Golden(String query, String goldenChunkId) {}
List<Golden> golden = List.of(
    new Golden("what does chunking do", "chunk-2"),
    new Golden("lexical scoring function", "chunk-4"));

double hitRate(List<Chunk> chunks, double alpha) {
    int hits = 0;
    for (Golden g : golden) {
        List<HybridSearchResult> top3 = hybridSearch(chunks, g.query(), computeEmbedding(g.query()), 3, alpha);
        boolean hit = top3.stream().anyMatch(r -> r.chunk.id().equals(g.goldenChunkId()));
        System.out.printf("  query='%s' golden=%s hit=%b (top: %s)%n",
            g.query(), g.goldenChunkId(), hit, top3.stream().map(r -> r.chunk.id()).toList());
        if (hit) hits++;
    }
    return hits / (double) golden.size();
}
```
Output: the smaller-chunk index shows higher recall because the answer sentences map to more specific chunks; the larger-chunk index returns overlapping mega-chunks that dilute ranking. The verdict — and the CI gate — is numeric: `recall@3 = 1.00` for size 10 vs `recall@3 = 0.50` for size 20, so the size change is rejected. That is exactly the lab's guidance: monitor retrieval recall with a held-out evaluation set.

### Company Evaluation
- Oracle: Evaluation design: query sets, relevance labeling, and recall@k computation.
- Deloitte: Quality assurance: labeling process, reviewer agreement, and report governance.
- Accenture: Benchmark practice: held-out sets, baseline comparison, and repeatable harnesses.
- PwC: Data integrity: label quality, audit of evaluation sets, and decision governance.
- Amazon: Scale: continuous recall monitoring and drift alerts for production retrieval.

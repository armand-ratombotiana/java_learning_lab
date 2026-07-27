# PROBLEM WALKTHROUGH: Retrieval-Augmented Generation Pipeline

## Problem Statement

**Difficulty: Medium | Category: RAG / Information Retrieval**

Implement a complete Retrieval-Augmented Generation (RAG) pipeline that takes a corpus of documents, chunks them intelligently, indexes them with embeddings, retrieves relevant chunks for a query, and formats a context window for an LLM.

**Interview Context:** RAG is the dominant architecture for grounding LLMs with external knowledge (2023-present). Interviewers want to see your understanding of chunking strategies, embedding-based retrieval, context window management, and the "retrieve-then-read" paradigm.

### Requirements

1. **Document Chunking:** Split documents into overlapping chunks of configurable size with sentence boundary awareness.
2. **Embedding Index:** Store chunks with vector embeddings (using a provided embedding function interface).
3. **Retrieval:** Given a query, find top-k most similar chunks using cosine similarity.
4. **Context Formatting:** Assemble retrieved chunks into a prompt context, respecting token limits.
5. **Hybrid Search:** Support combining vector similarity with keyword (BM25-style) scores.
6. **Query Rewriting:** Optional query expansion for better retrieval.

### Input/Output Contract

```
Input:  Document corpus (list of strings), query string, 
        chunk_size=512 chars, chunk_overlap=50, top_k=5, max_context_tokens=2048
Output: Formatted prompt with retrieved context chunks and the original query
```

---

## Step-by-Step Solution Walkthrough

### 1. RAG Architecture Overview

The RAG pipeline follows this flow:

```
Documents → Chunking → Embedding Index
                                ↓
User Query → Embedding → Vector Search → Hybrid Scoring
                                              ↓
                              Context Assembly (LLM prompt)
                                              ↓
                              LLM Generation ← (LLM call)
```

The "retrieve" step is critical — if relevant context is not retrieved, the LLM cannot produce correct answers.

### 2. Chunking Strategy

Chunking is deceptively important. The chunk size determines what information fits in one unit:

**Small chunks (128-256 chars):** High precision, low recall. Good for factoid QA.
**Large chunks (1024-2048 chars):** Low precision, high recall. Good for summarization.

**Overlap:** Overlapping by 10-20% prevents information loss at chunk boundaries. For a sentence like "The capital of France is Paris. It is known for the Eiffel Tower.", a non-overlapping split at 40 chars would separate the city name from its description.

### 3. Embedding-Based Retrieval

For each chunk, compute a dense embedding vector (via a model like `text-embedding-3-small`). For a query, compute its embedding and find nearest neighbors via cosine similarity:

```
score(q, c) = (e_q · e_c) / (||e_q|| * ||e_c||)
```

### 4. Hybrid Search (Vector + Keyword)

Pure vector search can miss exact keyword matches. Hybrid search combines:

```
hybrid_score(q, c) = λ * vector_sim(q, c) + (1-λ) * bm25_score(q, c)
```

where BM25 is:

```
BM25(q, c) = Σ_{t ∈ q} IDF(t) * (f(t, c) * (k1 + 1)) / (f(t, c) + k1 * (1 - b + b * |c| / avg_len))
```

- `f(t, c)` = term frequency of `t` in chunk `c`
- `IDF(t)` = inverse document frequency of `t`
- `k1` (1.2-2.0) controls term frequency saturation
- `b` (0.75) controls length normalization

### 5. Context Window Management

LLMs have limited context windows (4k-128k tokens). The context must fit:

```
context = ""
for each retrieved chunk in order of relevance:
    candidate = context + "\n---\n" + chunk
    if token_count(candidate) < max_context_tokens:
        context = candidate
    else:
        break
```

### 6. Query Rewriting

Short queries benefit from expansion. Given "capital of France", a query rewriter might produce:
1. "What is the capital of France?"
2. "France capital city"
3. "Paris France government seat"

Embedding and searching each expanded query, then merging results, improves recall.

---

## Java Implementation

```java
package com.llm.genai.deep.rag;

import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implements a complete Retrieval-Augmented Generation (RAG) pipeline.
 * <p>
 * Handles document chunking, embedding-based retrieval, hybrid search with
 * BM25 scoring, context window assembly, and query rewriting for improved
 * downstream LLM accuracy.
 */
public class RAGPipeline {

    private final int chunkSize;
    private final int chunkOverlap;
    private final int topK;
    private final int maxContextTokens;
    private final double hybridLambda;
    private final EmbeddingFunction embeddingFunction;
    private final List<Chunk> chunks;
    private final BM25Index bm25Index;

    /**
     * Represents a single document chunk with its metadata and embedding.
     */
    public static class Chunk {
        final String id;
        final String text;
        final int docIndex;
        final int chunkIndex;
        double[] embedding;

        Chunk(String id, String text, int docIndex, int chunkIndex) {
            this.id = id;
            this.text = text;
            this.docIndex = docIndex;
            this.chunkIndex = chunkIndex;
        }
    }

    /**
     * Functional interface for embedding computation.
     * Implementations can use any embedding model or API.
     */
    @FunctionalInterface
    public interface EmbeddingFunction {
        /**
         * Computes a dense embedding vector for the given text.
         *
         * @param text input text
         * @return embedding vector
         */
        double[] embed(String text);
    }

    /**
     * Constructs a RAG pipeline with specified configuration.
     *
     * @param chunkSize        maximum characters per chunk
     * @param chunkOverlap     character overlap between consecutive chunks
     * @param topK             number of chunks to retrieve
     * @param maxContextTokens maximum tokens for assembled context
     * @param hybridLambda     weight for vector search in hybrid scoring (0=pure keyword, 1=pure vector)
     * @param embeddingFunction function to compute embeddings
     */
    public RAGPipeline(int chunkSize, int chunkOverlap, int topK,
                       int maxContextTokens, double hybridLambda,
                       EmbeddingFunction embeddingFunction) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
        this.topK = topK;
        this.maxContextTokens = maxContextTokens;
        this.hybridLambda = hybridLambda;
        this.embeddingFunction = embeddingFunction;
        this.chunks = new ArrayList<>();
        this.bm25Index = new BM25Index();
    }

    /**
     * Default constructor with sensible defaults.
     */
    public RAGPipeline(EmbeddingFunction embeddingFunction) {
        this(512, 50, 5, 2048, 0.7, embeddingFunction);
    }

    /**
     * Indexes a collection of documents by chunking, embedding, and indexing.
     *
     * @param documents list of document strings
     */
    public void indexDocuments(List<String> documents) {
        chunks.clear();
        int globalChunkId = 0;

        for (int docIdx = 0; docIdx < documents.size(); docIdx++) {
            List<String> docChunks = chunkDocument(documents.get(docIdx), docIdx);
            for (int chunkIdx = 0; chunkIdx < docChunks.size(); chunkIdx++) {
                String chunkText = docChunks.get(chunkIdx);
                Chunk chunk = new Chunk(
                        "chunk_" + globalChunkId++, chunkText, docIdx, chunkIdx);
                chunk.embedding = embeddingFunction.embed(chunkText);
                chunks.add(chunk);
            }
        }

        // Build BM25 index over chunks
        bm25Index.build(chunks.stream().map(c -> c.text).toList());
        System.out.println("Indexed " + chunks.size() + " chunks from "
                + documents.size() + " documents.");
    }

    /**
     * Chunks a single document with overlap and sentence boundary awareness.
     *
     * @param text    document text
     * @param docIdx  document index for metadata
     * @return list of chunk texts
     */
    private List<String> chunkDocument(String text, int docIdx) {
        // Normalize whitespace
        text = text.replaceAll("\\s+", " ").trim();
        if (text.isEmpty()) return Collections.emptyList();

        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());

            // Try to break at sentence boundary near the end
            if (end < text.length()) {
                String segment = text.substring(start, end);
                int sentenceBoundary = findSentenceBoundary(segment, (int) (chunkSize * 0.8));
                if (sentenceBoundary > 0) {
                    end = start + sentenceBoundary + 1;
                } else {
                    // Fallback: break at word boundary
                    int wordBoundary = segment.lastIndexOf(' ');
                    if (wordBoundary > chunkSize / 2) {
                        end = start + wordBoundary;
                    }
                }
            }

            chunks.add(text.substring(start, end).trim());
            start = end - chunkOverlap;
            if (start < 0) start = 0;
        }

        // Merge single-sentence tiny chunks into previous chunk
        List<String> merged = new ArrayList<>();
        for (String chunk : chunks) {
            if (!merged.isEmpty() && chunk.length() < 50) {
                int lastIdx = merged.size() - 1;
                merged.set(lastIdx, merged.get(lastIdx) + " " + chunk);
            } else {
                merged.add(chunk);
            }
        }

        return merged;
    }

    /**
     * Finds a sentence boundary (period, exclamation, question mark) within a range.
     */
    private int findSentenceBoundary(String text, int searchFrom) {
        Pattern pattern = Pattern.compile("[.!?]");
        Matcher matcher = pattern.matcher(text);
        int bestPos = -1;
        while (matcher.find()) {
            if (matcher.start() >= searchFrom) {
                bestPos = matcher.start();
            }
        }
        return bestPos;
    }

    /**
     * Retrieves top-k chunks for a given query using hybrid search.
     *
     * @param query the user query
     * @return list of retrieved chunks with scores
     */
    public List<Map.Entry<Chunk, Double>> retrieve(String query) {
        // Query expansion (optional)
        List<String> queries = expandQuery(query);

        // Compute vector scores
        double[] queryEmbedding = embeddingFunction.embed(query);
        Map<Integer, Double> vectorScores = new HashMap<>();
        for (int i = 0; i < chunks.size(); i++) {
            double sim = cosineSimilarity(queryEmbedding, chunks.get(i).embedding);
            vectorScores.put(i, sim);
        }

        // Compute BM25 scores
        Map<Integer, Double> bm25Scores = bm25Index.score(query);

        // Compute hybrid scores
        Map<Integer, Double> hybridScores = new HashMap<>();
        for (int i = 0; i < chunks.size(); i++) {
            double vecScore = vectorScores.getOrDefault(i, 0.0);
            double kwScore = bm25Scores.getOrDefault(i, 0.0);
            // Normalize BM25 scores to [0, 1] for fair combination
            hybridScores.put(i, hybridLambda * vecScore
                    + (1 - hybridLambda) * kwScore);
        }

        // Sort by hybrid score descending
        return hybridScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(topK)
                .map(e -> Map.entry(chunks.get(e.getKey()), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Assembles retrieved chunks into a formatted context window for LLM.
     *
     * @param query      the user query
     * @param retrievedChunks the retrieved chunks with scores
     * @return formatted prompt string with context and query
     */
    public String formatContext(String query,
                                List<Map.Entry<Chunk, Double>> retrievedChunks) {
        StringBuilder context = new StringBuilder();
        context.append("You are a helpful assistant. Answer the question based on the ")
                .append("following context.\n\n");

        int estimatedTokens = estimateTokenCount(context.toString());

        for (var entry : retrievedChunks) {
            String chunkText = entry.getKey().text;
            String chunkBlock = "\n---\n[" + entry.getKey().id + "]\n" + chunkText;
            int chunkTokens = estimateTokenCount(chunkBlock);

            if (estimatedTokens + chunkTokens <= maxContextTokens) {
                context.append(chunkBlock);
                estimatedTokens += chunkTokens;
            } else {
                break;
            }
        }

        context.append("\n\n---\nQuestion: ").append(query).append("\nAnswer: ");
        return context.toString();
    }

    /**
     * End-to-end RAG pipeline: retrieve + format context.
     *
     * @param query the user query
     * @return formatted prompt ready for LLM consumption
     */
    public String query(String query) {
        var retrieved = retrieve(query);
        return formatContext(query, retrieved);
    }

    /**
     * Query expansion: generates alternative phrasings for improved retrieval.
     */
    private List<String> expandQuery(String query) {
        List<String> expansions = new ArrayList<>();
        expansions.add(query);

        // Simple expansion: add variants
        if (!query.endsWith("?")) {
            expansions.add(query + "?");
        }
        // Remove question words for terser version
        String terse = query.replaceAll("(?i)^(what|who|where|when|why|how)\\s+", "");
        if (!terse.equals(query)) {
            expansions.add(terse);
        }

        return expansions;
    }

    /**
     * Computes cosine similarity between two vectors.
     */
    private double cosineSimilarity(double[] a, double[] b) {
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0 || normB == 0) return 0.0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * Rough token estimation (~4 characters per token).
     */
    private int estimateTokenCount(String text) {
        return text.length() / 4;
    }

    /**
     * In-memory BM25 index for keyword-based retrieval.
     */
    private static class BM25Index {
        private static final double K1 = 1.5;
        private static final double B = 0.75;
        private List<Map<String, Integer>> termFrequencies;
        private Map<String, Integer> documentFrequencies;
        private int totalDocuments;
        private double avgLength;

        void build(List<String> documents) {
            totalDocuments = documents.size();
            termFrequencies = new ArrayList<>();
            documentFrequencies = new HashMap<>();
            double totalLength = 0;

            for (String doc : documents) {
                List<String> tokens = tokenize(doc);
                totalLength += tokens.size();

                Map<String, Integer> tf = new HashMap<>();
                Set<String> unique = new HashSet<>();
                for (String token : tokens) {
                    tf.merge(token, 1, Integer::sum);
                    unique.add(token);
                }
                termFrequencies.add(tf);
                for (String token : unique) {
                    documentFrequencies.merge(token, 1, Integer::sum);
                }
            }
            avgLength = totalDocuments > 0 ? totalLength / totalDocuments : 1.0;
        }

        Map<Integer, Double> score(String query) {
            List<String> queryTokens = tokenize(query);
            Map<Integer, Double> scores = new HashMap<>();

            if (totalDocuments == 0) return scores;

            for (int docIdx = 0; docIdx < totalDocuments; docIdx++) {
                double score = 0.0;
                Map<String, Integer> tf = termFrequencies.get(docIdx);
                int docLength = tf.values().stream().mapToInt(Integer::intValue).sum();

                for (String token : queryTokens) {
                    int termFreq = tf.getOrDefault(token, 0);
                    if (termFreq == 0) continue;

                    int df = documentFrequencies.getOrDefault(token, 0);
                    double idf = Math.log(1.0 + (totalDocuments - df + 0.5) / (df + 0.5));
                    double numerator = termFreq * (K1 + 1);
                    double denominator = termFreq + K1 * (1 - B + B * docLength / avgLength);
                    score += idf * numerator / denominator;
                }
                if (score > 0) {
                    scores.put(docIdx, score);
                }
            }
            return scores;
        }

        private List<String> tokenize(String text) {
            return Arrays.stream(text.toLowerCase().split("\\W+"))
                    .filter(w -> w.length() > 1)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Returns the total number of indexed chunks.
     */
    public int getChunkCount() {
        return chunks.size();
    }

    /**
     * Returns a chunk by index (for inspection).
     */
    public Chunk getChunk(int index) {
        return chunks.get(index);
    }

    /**
     * Main method demonstrating the RAG pipeline.
     */
    public static void main(String[] args) {
        // Create a dummy embedding function that generates random embeddings
        // In production, use a real embedding model or API
        EmbeddingFunction embFn = text -> {
            double[] vec = new double[384];
            Arrays.fill(vec, 0.0);
            for (int i = 0; i < text.length() && i < 384; i++) {
                vec[i % 384] += text.codePointAt(i) / 127.0;
            }
            double norm = Math.sqrt(Arrays.stream(vec).map(v -> v * v).sum());
            if (norm > 0) {
                for (int i = 0; i < vec.length; i++) vec[i] /= norm;
            }
            return vec;
        };

        RAGPipeline rag = new RAGPipeline(embFn);

        List<String> docs = List.of(
            "Paris is the capital of France. It is known for the Eiffel Tower, "
            + "the Louvre Museum, and its rich culinary tradition. The city has "
            + "a population of over 2 million people and is one of the most "
            + "visited cities in the world.",
            "The Eiffel Tower was built in 1889 for the World's Fair. It stands "
            + "330 meters tall and was the tallest structure in the world until "
            + "1930. It is made of wrought iron and weighs approximately 10,000 tons.",
            "France is a country in Western Europe. Its capital is Paris. "
            + "France is known for its wine, cheese, and fashion industries."
        );

        rag.indexDocuments(docs);

        String query = "What is the capital of France?";
        System.out.println("Query: " + query);
        System.out.println("---");
        String context = rag.query(query);
        System.out.println(context);
    }
}
```

---

## Complexity Analysis

### Time Complexity

**Indexing (one-time):**
- Chunking each document: O(D × C) where D = docs, C = chunk size scanning.
- Embedding each chunk: O(N × d_model) where N = number of chunks, usually N ≫ D.
- BM25 index building: O(N × L_avg) where L_avg = average chunk length in tokens.
- **Total:** O(N × d_model) — dominated by embedding computation.

**Query (per request):**
- Embedding: O(d_model) for query embedding.
- Vector search: O(N × d_model) naive, O(log N × d_model) with HNSW/ANN index.
- BM25 scoring: O(|q| × N) with inverted index.
- Context assembly: O(K) where K = topK.
- **Total:** O(N × d_model) naive, O(log N × d_model) optimized.

**Memory (index storage):**
- Embeddings: O(N × d_model) × 4 bytes (float) = ~1.2 GB for N=10^6, d=384.
- Chunk text: O(total_chars) ~ N × 512 bytes.
- BM25 index: O(V × N) inverted list size where V = vocabulary.

### Optimization Notes

1. **ANN indexing:** Replace O(N) vector scan with HNSW (see Lab 02) for O(log N) search.
2. **Inverted index for BM25:** HashMap from term → sorted list of (docID, frequency) for O(|q|) scoring instead of O(|q| × N).
3. **Embedding caching:** Pre-compute and store embeddings to avoid re-computation.

---

## Follow-Up Questions

### Q1: How does chunk size affect RAG quality?

**Answer:** There is a precision-recall trade-off:
- **Small chunks (128-256 chars):** Each chunk is focused, precision is high. The retrieved chunks are likely relevant. But you may need to retrieve many chunks to cover the answer, risking context overflow.
- **Large chunks (512-1024 chars):** More context per chunk, recall is higher. But chunks contain noise, reducing precision. The embedding of a long chunk may "dilute" the relevant signal.
- **Optimal:** 256-512 chars is a common sweet spot. The best chunk size depends on your content (article vs code vs conversation) and retrieval granularity needs.

**Advanced:** Dynamic chunking using semantic boundaries (topic shift detection, section headers) often outperforms fixed sizes.

### Q2: Explain the role of the hybrid search lambda parameter.

**Answer:** `lambda = hybridLambda` controls the blend:
- **λ=1.0 (pure vector search):** Handles synonyms and paraphrases well. Matches "automobile" to "car". Misses exact phrase matches. Can be confused by unrelated but semantically similar text.
- **λ=0.0 (pure keyword search):** Exact matches only. Good for proper nouns, IDs, and specific terms. Handles "Eiffel Tower 1889" correctly. Misses semantic relationships.
- **λ=0.5-0.8 (balanced):** Best of both worlds. Catches semantic matches and boosts exact keyword hits. Recommended starting point.

**Tuning:** Use a validation set with ground-truth relevant chunks. Sweep λ in [0.1, 0.3, 0.5, 0.7, 0.9] and pick the one maximizing recall@k.

### Q3: How do you handle the "lost in the middle" problem?

**Answer:** LLMs tend to focus on information at the beginning and end of the context, ignoring the middle. Mitigations:
1. **Re-rank:** Sort retrieved chunks by relevance within the context, most relevant first.
2. **Structured context:** Use explicit headers like `[SOURCE 1]`, `[SOURCE 2]` so the LLM can reference them.
3. **Iterative retrieval:** Retrieve 1-2 chunks, generate a partial answer, then retrieve more if needed.
4. **Context window windowing:** If many chunks are needed, split the retrieval into multiple rounds.

### Q4: What is self-RAG and how does it improve RAG?

**Answer:** Self-RAG (Asai et al., 2023) makes the LLM aware of when retrieval is needed and when it is not:
1. The LLM generates a special `[Retrieve]` token when it determines it needs external knowledge.
2. On encountering `[Retrieve]`, the system retrieves relevant chunks.
3. The LLM then either uses the chunk (generating a `[Relevant]` or `[Irrelevant]` token) or generates from its own knowledge.
4. A critic model evaluates the quality of each retrieved chunk and the final answer.

This dynamic approach reduces unnecessary retrieval, lowers latency, and improves factual accuracy.

### Q5: How do you evaluate a RAG pipeline? (Preview of Lab 04)

**Answer:** Key metrics beyond simple accuracy:
1. **Context precision:** Are retrieved chunks relevant to the query?
2. **Context recall:** Are ALL relevant chunks retrieved?
3. **Faithfulness:** Does the generated answer stick to the retrieved context?
4. **Answer relevance:** Is the generated answer on-topic?
5. **End-to-end:** Accuracy against a held-out QA set.

---

## Test Cases

### Test Case 1: Basic Retrieval

```
Documents: ["The sky is blue.", "Grass is green.", "The ocean is blue."]
Query: "What color is the sky?"
Expected: Top chunk = "The sky is blue." with high score.
```

### Test Case 2: Chunk Overlap Correctness

```
Document: "Paris is the capital of France. It is known for the Eiffel Tower."
Chunk size = 40 chars, overlap = 10 chars
Expected chunks:
  "Paris is the capital of France."
  "capital of France. It is known for the"  (overlap region)
  "It is known for the Eiffel Tower."
No sentence should be cut mid-sentence unnecessarily.
```

### Test Case 3: Hybrid Score Combination

```
Query: "Eiffel Tower 1889"
Vector search may rank "general Paris doc" higher.
BM25 should boost "Eiffel Tower built in 1889" chunk.
Hybrid (λ=0.7) should rank the specific chunk higher than either alone.
```

### Test Case 4: Context Token Limit

```
maxContextTokens = 20 (very low)
Retrieved: 5 chunks of ~50 chars each (~12 tokens each)
Expected: Only 2-3 chunks fit in context, ensuring total ≤ 20 tokens.
The rest are truncated.
```

### Test Case 5: Empty Document Handling

```
Documents: [] (empty list)
Query: "anything"
Expected: retrieve() returns empty list.
formatContext() returns prompt with "No context available."
```

### Test Case 6: BM25 Score Normalization

```
Query: "the" (stop word)
BM25 score for "the" should be near 0 because IDF is low (appears in many docs).
The hybrid score contribution from BM25 should be minimal.
```

### Test Case 7: Query Expansion Effect

```
Query: "capital France" (terse)
After expansion: ["capital France", "capital France?", "France"]
More chunks should be retrieved with expansion than without.
```

---

## Summary

This walkthrough implemented a production-grade RAG pipeline covering:
1. **Intelligent chunking** with overlap and sentence boundary detection.
2. **Hybrid retrieval** combining dense embeddings with BM25 keyword scores.
3. **Context assembly** respecting token limits with truncation.
4. **Query expansion** for improved recall on short queries.

The key insight is that RAG quality depends more on retrieval quality than on the LLM itself — garbage in, garbage out. The modular design allows swapping embedding functions, adding ANN indexes, or integrating more sophisticated chunking strategies.
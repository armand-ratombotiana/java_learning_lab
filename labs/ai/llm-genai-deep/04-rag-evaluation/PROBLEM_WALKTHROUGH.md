# PROBLEM WALKTHROUGH: RAG Evaluation Metrics

## Problem Statement

**Difficulty: Medium | Category: Evaluation / Metrics**

Implement a comprehensive evaluation framework for RAG systems. Given a set of queries, retrieved contexts, and generated answers (with ground truth references), compute metrics for faithfulness, relevance, context precision, and context recall.

**Interview Context:** Evaluating RAG is harder than building it because there is no single "correct answer" for many queries. Interviewers want to see you think about automated evaluation proxies, the correlation with human judgment, and the weaknesses of each metric.

### Requirements

1. **Faithfulness Score:** Measure whether the generated answer contradicts the provided context (0.0 = fully contradictory, 1.0 = fully supported).
2. **Answer Relevance:** Measure whether the generated answer addresses the query (0.0 = irrelevant, 1.0 = directly relevant).
3. **Context Precision:** What fraction of retrieved chunks is relevant to the query?
4. **Context Recall:** What fraction of all relevant chunks was retrieved?
5. **Overall RAG Score:** Weighted combination of the above metrics.

### Input/Output Contract

```
Input:  List of evaluation samples, each with:
          - query: String
          - retrieved_chunks: List[String]
          - generated_answer: String
          - reference_answer: String (ground truth)
          - relevant_chunks: List[Int] (indices of truly relevant chunks)
Output: Metrics dictionary with:
          - faithfulness: Double
          - answer_relevance: Double  
          - context_precision: Double
          - context_recall: Double
          - overall: Double
```

---

## Step-by-Step Solution Walkthrough

### 1. Why RAG Evaluation Is Hard

Traditional QA evaluation (exact match, F1) fails for RAG because:
- The LLM can paraphrase — exact overlap penalizes correct answers.
- Multiple correct answers exist for most queries.
- The answer may use knowledge from both context and parametric memory.

RAG evaluation must therefore consider **what the answer should be based on** (context) and **what the user asked** (query relevance).

### 2. Faithfulness (Hallucination Detection)

Faithfulness checks if the answer is **supported by** the retrieved context. It does NOT check if the answer is correct — only that it doesn't contradict the evidence.

**NLI-based approach:** Use a Natural Language Inference (NLI) model:
- For each claim in the answer, check if the context entails it.
- Faithfulness = proportion of claims that are entailed (rather than contradicted or neutral).

```
faithfulness = |{claims : context entails claim}| / |{claims}|
```

**Lexical overlap (fallback):** Without an NLI model, use:

```
faithfulness = answer_ngrams_in_context / total_answer_ngrams
```

where n-grams are checked against the concatenated context. This is a weak proxy but requires no external model.

### 3. Answer Relevance

Does the answer address the query? Relevance is measured by:

**Cosine similarity between query and answer embeddings:**
```
relevance = cos_sim(embed(query), embed(answer))
```

**Length-normalized relevance:** Short answers like "Yes" or "42" can have high cosine similarity but low informativeness. A penalty for answers shorter than expected:

```
adjusted_relevance = relevance * min(answer_len / expected_len, 1.0)
```

### 4. Context Precision

What fraction of retrieved chunks was actually useful?

```
context_precision = |relevant_retrieved_chunks| / |retrieved_chunks|
```

This measures **how well the retriever filtered noise**. High precision means the retriever returned mostly relevant chunks. Low precision means many irrelevant chunks were retrieved, forcing the LLM to ignore or be misled by them.

### 5. Context Recall

What fraction of all relevant chunks was retrieved?

```
context_recall = |relevant_retrieved_chunks ∩ all_relevant_chunks| / |all_relevant_chunks|
```

All relevant chunks must be identified ahead of time (human annotation or via a "gold" relevance set). This measures **how complete the retrieval was**. Low recall means the answer is based on incomplete information.

### 6. Overall RAG Score

A weighted harmonic mean balances the metrics:

```
overall = 4 / (1/faithfulness + 1/relevance + 1/precision + 1/recall)
```

If any metric is 0, the overall score is 0.

An alternative weighted sum:
```
overall = 0.4 * faithfulness + 0.3 * relevance + 0.2 * precision + 0.1 * recall
```

The weights depend on use case: factual QA weighs faithfulness higher; open-ended QA weighs relevance higher.

### 7. Decomposing Answers into Claims

Faithfulness scoring requires claim extraction:

1. **Sentence splitting:** Break the answer into sentences.
2. **Claim decomposition:** For each sentence, extract atomic facts. "The Eiffel Tower is in Paris and was built in 1889" → two claims.
3. **Verification:** Check each claim against the context.

---

## Java Implementation

```java
package com.llm.genai.deep.rageval;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implements a comprehensive RAG evaluation framework.
 * <p>
 * Computes faithfulness, answer relevance, context precision, context recall,
 * and an overall RAG quality score by comparing generated answers against
 * ground-truth references and retrieved context.
 */
public class RAGEvaluator {

    private final EmbeddingFunction embeddingFunction;
    private static final int NGRAM_SIZE = 2;

    /**
     * Functional interface for embedding computation.
     */
    @FunctionalInterface
    public interface EmbeddingFunction {
        double[] embed(String text);
    }

    /**
     * An evaluation sample containing all RAG inputs and ground truth.
     */
    public static class EvaluationSample {
        final String query;
        final List<String> retrievedChunks;
        final String generatedAnswer;
        final String referenceAnswer;
        final Set<Integer> relevantChunkIndices; // indices into retrievedChunks that are relevant
        final Set<String> allRelevantChunks; // all chunks in corpus that are relevant (for recall)

        public EvaluationSample(String query, List<String> retrievedChunks,
                                String generatedAnswer, String referenceAnswer,
                                Set<Integer> relevantChunkIndices,
                                Set<String> allRelevantChunks) {
            this.query = query;
            this.retrievedChunks = retrievedChunks;
            this.generatedAnswer = generatedAnswer;
            this.referenceAnswer = referenceAnswer;
            this.relevantChunkIndices = relevantChunkIndices;
            this.allRelevantChunks = allRelevantChunks;
        }
    }

    /**
     * Container for all computed evaluation metrics.
     */
    public static class RAGMetrics {
        public final double faithfulness;
        public final double answerRelevance;
        public final double contextPrecision;
        public final double contextRecall;
        public final double overall;

        RAGMetrics(double faithfulness, double answerRelevance,
                   double contextPrecision, double contextRecall) {
            this.faithfulness = faithfulness;
            this.answerRelevance = answerRelevance;
            this.contextPrecision = contextPrecision;
            this.contextRecall = contextRecall;
            this.overall = computeOverall(faithfulness, answerRelevance,
                    contextPrecision, contextRecall);
        }

        private static double computeOverall(double f, double r, double p, double rc) {
            // Weighted harmonic mean
            double weightSum = 0.4 + 0.3 + 0.2 + 0.1;
            double invSum = 0.4 / Math.max(f, 1e-10)
                    + 0.3 / Math.max(r, 1e-10)
                    + 0.2 / Math.max(p, 1e-10)
                    + 0.1 / Math.max(rc, 1e-10);
            return weightSum / invSum;
        }

        @Override
        public String toString() {
            return String.format("Faithfulness: %.4f | Answer Relevance: %.4f | "
                            + "Context Precision: %.4f | Context Recall: %.4f | Overall: %.4f",
                    faithfulness, answerRelevance, contextPrecision, contextRecall, overall);
        }
    }

    /**
     * Constructs a RAGEvaluator with the given embedding function.
     *
     * @param embeddingFunction function to embed text for relevance scoring
     */
    public RAGEvaluator(EmbeddingFunction embeddingFunction) {
        this.embeddingFunction = embeddingFunction;
    }

    /**
     * Evaluates a single sample and returns all metrics.
     *
     * @param sample the evaluation sample
     * @return computed metrics
     */
    public RAGMetrics evaluate(EvaluationSample sample) {
        double faithfulness = computeFaithfulness(
                sample.generatedAnswer, sample.retrievedChunks);
        double relevance = computeAnswerRelevance(
                sample.query, sample.generatedAnswer);
        double precision = computeContextPrecision(
                sample.retrievedChunks, sample.relevantChunkIndices);
        double recall = computeContextRecall(
                sample.retrievedChunks, sample.relevantChunkIndices,
                sample.allRelevantChunks);

        return new RAGMetrics(faithfulness, relevance, precision, recall);
    }

    /**
     * Evaluates multiple samples and returns average metrics.
     *
     * @param samples list of evaluation samples
     * @return aggregated metrics
     */
    public RAGMetrics evaluateAll(List<EvaluationSample> samples) {
        if (samples.isEmpty()) {
            return new RAGMetrics(0.0, 0.0, 0.0, 0.0);
        }

        double sumF = 0, sumR = 0, sumP = 0, sumRC = 0;
        for (EvaluationSample sample : samples) {
            RAGMetrics m = evaluate(sample);
            sumF += m.faithfulness;
            sumR += m.answerRelevance;
            sumP += m.contextPrecision;
            sumRC += m.contextRecall;
        }
        int n = samples.size();
        return new RAGMetrics(sumF / n, sumR / n, sumP / n, sumRC / n);
    }

    /**
     * Computes faithfulness: proportion of answer n-grams present in context.
     * In production, this would use an NLI model for claim-level verification.
     *
     * @param answer   generated answer text
     * @param contexts retrieved context chunks
     * @return faithfulness score in [0, 1]
     */
    public double computeFaithfulness(String answer, List<String> contexts) {
        if (answer == null || answer.isBlank()) return 0.0;
        if (contexts == null || contexts.isEmpty()) return 0.0;

        String combinedContext = String.join(" ", contexts).toLowerCase();

        // Extract claims from the answer
        List<String> claims = extractClaims(answer);
        if (claims.isEmpty()) return 1.0; // no claims to contradict

        int supportedClaims = 0;
        for (String claim : claims) {
            if (isClaimSupportedByContext(claim, combinedContext)) {
                supportedClaims++;
            }
        }

        return (double) supportedClaims / claims.size();
    }

    /**
     * Extracts atomic claims from an answer sentence.
     * Splits on conjunctions and lists for finer-grained verification.
     */
    private List<String> extractClaims(String answer) {
        List<String> claims = new ArrayList<>();
        String[] sentences = answer.split("[.!?]+");

        for (String sentence : sentences) {
            sentence = sentence.trim().toLowerCase();
            if (sentence.isEmpty()) continue;

            // Split on "and", "but", "or" for compound sentences
            String[] parts = sentence.split("\\s+(and|but|or|,)\\s+");
            for (String part : parts) {
                part = part.trim();
                if (part.length() > 10) {
                    claims.add(part);
                }
            }
        }
        return claims;
    }

    /**
     * Checks if a claim is supported by context using n-gram overlap.
     * In production, use a trained NLI model for entailment.
     */
    private boolean isClaimSupportedByContext(String claim, String context) {
        // Compute n-gram overlap
        Set<String> claimNGrams = getNGrams(claim, NGRAM_SIZE);
        Set<String> contextNGrams = getNGrams(context, NGRAM_SIZE);

        if (claimNGrams.isEmpty()) return true;

        long overlap = claimNGrams.stream()
                .filter(contextNGrams::contains)
                .count();

        double overlapRatio = (double) overlap / claimNGrams.size();
        return overlapRatio >= 0.5; // threshold for "supported"
    }

    /**
     * Generates character-level n-grams from text.
     */
    private Set<String> getNGrams(String text, int n) {
        String clean = text.replaceAll("\\s+", " ").toLowerCase();
        Set<String> ngrams = new HashSet<>();
        for (int i = 0; i <= clean.length() - n; i++) {
            ngrams.add(clean.substring(i, i + n));
        }
        return ngrams;
    }

    /**
     * Computes answer relevance via cosine similarity between query and answer embeddings.
     *
     * @param query  user query
     * @param answer generated answer
     * @return relevance score in [0, 1]
     */
    public double computeAnswerRelevance(String query, String answer) {
        if (query == null || query.isBlank() || answer == null || answer.isBlank()) {
            return 0.0;
        }

        double[] queryEmb = embeddingFunction.embed(query);
        double[] answerEmb = embeddingFunction.embed(answer);

        double cosineSim = cosineSimilarity(queryEmb, answerEmb);

        // Clamp to [0, 1]
        return Math.max(0.0, Math.min(1.0, (cosineSim + 1.0) / 2.0));
    }

    /**
     * Computes context precision: fraction of retrieved chunks that are relevant.
     *
     * @param retrievedChunks        all chunks retrieved
     * @param relevantChunkIndices   indices of chunks deemed relevant
     * @return precision in [0, 1]
     */
    public double computeContextPrecision(List<String> retrievedChunks,
                                          Set<Integer> relevantChunkIndices) {
        if (retrievedChunks == null || retrievedChunks.isEmpty()) return 0.0;

        long relevant = retrievedChunks.stream()
                .filter(chunk -> relevantChunkIndices != null
                        && relevantChunkIndices.contains(
                                retrievedChunks.indexOf(chunk)))
                .count();

        return (double) relevant / retrievedChunks.size();
    }

    /**
     * Computes context recall: fraction of all relevant chunks that were retrieved.
     *
     * @param retrievedChunks      chunks that were retrieved
     * @param relevantIndices      indices of relevant chunks among retrieved
     * @param allRelevantChunks    the complete set of relevant chunks in the corpus
     * @return recall in [0, 1]
     */
    public double computeContextRecall(List<String> retrievedChunks,
                                       Set<Integer> relevantIndices,
                                       Set<String> allRelevantChunks) {
        if (allRelevantChunks == null || allRelevantChunks.isEmpty()) return 1.0;

        long retrievedRelevant = 0;
        for (int idx : relevantIndices) {
            if (idx >= 0 && idx < retrievedChunks.size()) {
                String chunk = retrievedChunks.get(idx);
                if (allRelevantChunks.contains(chunk)) {
                    retrievedRelevant++;
                }
            }
        }

        return (double) retrievedRelevant / allRelevantChunks.size();
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
     * Computes exact match between generated and reference answer.
     * Simple baseline metric, not suitable for generative answers.
     *
     * @param generated the generated answer
     * @param reference the ground-truth answer
     * @return 1.0 if identical (case-insensitive), 0.0 otherwise
     */
    public double exactMatch(String generated, String reference) {
        return generated.trim().equalsIgnoreCase(reference.trim()) ? 1.0 : 0.0;
    }

    /**
     * Computes F1 score based on token overlap between generated and reference.
     *
     * @param generated the generated answer
     * @param reference the ground-truth answer
     * @return F1 score in [0, 1]
     */
    public double tokenF1(String generated, String reference) {
        Set<String> genTokens = new HashSet<>(tokenize(generated));
        Set<String> refTokens = new HashSet<>(tokenize(reference));

        if (genTokens.isEmpty() && refTokens.isEmpty()) return 1.0;
        if (genTokens.isEmpty() || refTokens.isEmpty()) return 0.0;

        long intersection = genTokens.stream().filter(refTokens::contains).count();
        double precision = (double) intersection / genTokens.size();
        double recall = (double) intersection / refTokens.size();

        if (precision + recall == 0) return 0.0;
        return 2 * precision * recall / (precision + recall);
    }

    /**
     * Tokenizes text into lowercase words.
     */
    private List<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase().split("\\W+"))
                .filter(w -> !w.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Main method demonstrating RAG evaluation with sample data.
     */
    public static void main(String[] args) {
        // Simple embedding function for demo
        EmbeddingFunction embFn = text -> {
            double[] vec = new double[128];
            Arrays.fill(vec, 0.0);
            for (int i = 0; i < text.length() && i < 128; i++) {
                vec[i] += (double) text.codePointAt(i) / 127.0;
            }
            double norm = Math.sqrt(Arrays.stream(vec).map(v -> v * v).sum());
            if (norm > 0) {
                for (int i = 0; i < vec.length; i++) vec[i] /= norm;
            }
            return vec;
        };

        RAGEvaluator evaluator = new RAGEvaluator(embFn);

        List<String> chunks = List.of(
            "Paris is the capital of France.",
            "The Eiffel Tower is in Paris.",
            "France is known for its wine."
        );

        EvaluationSample sample = new EvaluationSample(
            "What is the capital of France?",
            chunks,
            "The capital of France is Paris.",
            "Paris",
            Set.of(0, 1), // chunks 0 and 1 are relevant
            Set.of(chunks.get(0), chunks.get(1)) // these are all relevant chunks
        );

        RAGMetrics metrics = evaluator.evaluate(sample);
        System.out.println(metrics);

        System.out.println("\nAdditional metrics:");
        String answer = "The capital of France is Paris.";
        String reference = "Paris";
        System.out.println("Exact match: " + evaluator.exactMatch(answer, reference));
        System.out.println("Token F1: " + evaluator.tokenF1(answer, reference));
    }
}
```

---

## Complexity Analysis

### Time Complexity

- **Faithfulness (n-gram overlap):** O(N_sentences × N_ngrams × M_chunks × L_chunk)
  - Decomposing answer into sentences: O(|A|)
  - Generating n-grams: O(N_claims × n)
  - Checking overlap: O(N_claims × M × L)
  - Total: O(|A| × M × L) typically small (answers are short).

- **Answer relevance (embedding):** O(d_model) per embedding call.

- **Context precision/recall:** O(M) where M = number of retrieved chunks.

- **Batch evaluation:** O(S × (|A| × M + d_model)) for S samples.

### Space Complexity

- Per sample: O(M × L) for chunk texts.
- Aggregated metrics: O(1) constant.
- Embedding storage: O(S × d_model) if caching.

### Scalability Concerns

- **Large-scale evaluation (1000s of samples):** Embedding each answer and query can be expensive. Pre-compute once.
- **NLI-based faithfulness:** Requires a separate model call per claim, which can be 10-100x more expensive than lexical approaches.
- **Ground-truth annotation:** Context recall requires knowing ALL relevant chunks, which requires exhaustive annotation — expensive for large corpora.

---

## Follow-Up Questions

### Q1: Why is faithfulness hard to evaluate automatically?

**Answer:** Faithfulness requires understanding **entailment**, not just lexical overlap. An answer can be:
- **Faithful but lexically different:** Context says "The capital is Paris", answer says "Paris is the seat of the French government" — both true but no n-gram overlap.
- **Unfaithful but lexically similar:** Context says "The Eiffel Tower is in Paris", answer says "The Eiffel Tower is in London" — has high n-gram overlap but is completely wrong.

**Better approaches:**
1. **NLI models:** Trained on SNLI/MultiNLI to detect entailment, contradiction, neutral. Can detect contradictions that lexical methods miss.
2. **Question-answering based:** Ask "Given context C, what is the answer to Q?" and compare.
3. **SelfCheckGPT-style:** Generate multiple answers, check consistency among them.

### Q2: How do you evaluate context precision without ground-truth relevance labels?

**Answer:** Without manual annotation, use proxy methods:
1. **Query-chunk embedding similarity:** If the chunk embedding has low cosine similarity with the query, it's probably irrelevant.
2. **LLM-based relevance judgment:** Ask an LLM "Is this chunk relevant to answering this query?" with a yes/no output.
3. **Click-through rate (production):** If users rephrase queries or abandon, the context was likely poor.
4. **Answer-chunk alignment:** If the answer doesn't reference or use information from a chunk, that chunk was likely irrelevant.

### Q3: What is the "RAGAS" framework and how does it compare?

**Answer:** RAGAS (RAG Assessment, Es et al., 2023) defines three core metrics:
1. **Faithfulness:** Uses NLI-based claim verification against context.
2. **Answer Relevance:** Uses the LLM to generate questions derived from the answer, then measures cosine similarity between generated and original questions.
3. **Context Relevance:** Measures the "signal-to-noise" ratio — what fraction of the context was needed to generate the answer.

RAGAS relies on LLM-as-judge for most metrics, which is expensive but correlates well with human judgment. Our implementation uses embedding-based relevance as a cheaper alternative.

### Q4: How do you measure context recall when you cannot annotate ALL relevant chunks?

**Answer:** This is the "annotation bottleneck" problem. Mitigations:
1. **Rank-based metrics (nDCG, MAP):** Don't require exhaustive annotation. Use relevance judgments for a subset.
2. **Pooling:** Have a strong "champion" retriever to find candidates, then annotate only the top-100 from each query.
3. **Unbounded recall proxies:** Measure answer recall against the full corpus using an LLM judge.
4. **Relative recall:** Compare your retriever's recall against a stronger "oracle" retriever (e.g., using the answer as the query).

### Q5: How do the weights in the overall score affect rankings of different RAG systems?

**Answer:** Different weightings can reverse system rankings:
- System A: high faithfulness (0.9), low precision (0.3)
- System B: medium faithfulness (0.7), high precision (0.8)

With weights `{f: 0.4, r: 0.3, p: 0.2, c: 0.1}`:
- System A: 0.4/0.9 + 0.3/0.7 + 0.2/0.3 + 0.1/0.5 → overall ≈ 0.68
- System B: 0.4/0.7 + 0.3/0.7 + 0.2/0.8 + 0.1/0.5 → overall ≈ 0.72

System B wins. With weights `{f: 0.6, r: 0.2, p: 0.1, c: 0.1}`:
- System A: overall ≈ 0.85
- System B: overall ≈ 0.74

System A wins. Always report individual metrics separately, not just the aggregate.

---

## Test Cases

### Test Case 1: Perfect RAG Response

```
Query: "What is the capital of France?"
Retrieved: ["Paris is the capital of France.", "The Eiffel Tower is in Paris."]
Answer: "Paris is the capital of France."
Reference: "Paris"
Relevant indices: {0, 1}
All relevant: {"Paris is the capital of France."}

Expected: Faithfulness=1.0, Relevance=1.0, Precision=1.0, Recall=1.0, Overall≈1.0
```

### Test Case 2: Hallucinated Answer (Unfaithful)

```
Query: "What is the capital of France?"
Retrieved: ["Paris is the capital of France."]
Answer: "The capital of France is London."
Reference: "Paris"

Expected: Faithfulness≈0.0 (claims contradict context), Relevance≈0.3 (related but wrong city)
Overall should be low (<0.2).
```

### Test Case 3: Irrelevant Context (Low Precision)

```
Query: "What is the capital of France?"
Retrieved: ["Einstein's theory of relativity.", "Photosynthesis in plants.", "Paris is the capital of France."]
Answer: "Paris."
Reference: "Paris"
Relevant indices: {2}

Expected: Precision = 1/3 ≈ 0.33. Faithfulness=1.0, Recall=1.0 (if all relevant was retrieved).
```

### Test Case 4: Incomplete Retrieval (Low Recall)

```
Query: "What is the capital of France and its population?"
Retrieved: ["Paris is the capital of France."]
Answer: "Paris is the capital of France."
Reference: "Paris. Population: 2 million."
Relevant indices: {0}
All relevant: {"Paris is the capital of France.", "Paris has a population of 2 million."}

Expected: Recall = 1/2 = 0.5. Precision=1.0. Faithfulness=1.0.
```

### Test Case 5: Empty Answer

```
Query: "What is the capital of France?"
Retrieved: ["Paris is the capital of France."]
Answer: "" (empty)
Reference: "Paris"
Relevant indices: {0}

Expected: Faithfulness=0.0, Relevance=0.0, Precision=1.0, Recall=1.0.
```

### Test Case 6: N-gram Faithfulness Threshold

```
Context: "The Eiffel Tower was built in 1889."
Answer: "The Eiffel Tower was built in 1889 for the World's Fair."
Claims extracted: ["the eiffel tower was built in 1889 for the world's fair"]
Bigrams from claim: {"th", "he", "e ", " ei", "ei", "if", "ff", etc.}
Bigrams from context: {"th", "he", "e ", " ei", "ei", "if", "ff", etc.}
Overlap ratio should be high → faithful.

Expected: Faithfulness > 0.7 (most character bigrams overlap).
```

### Test Case 7: Batch Evaluation Consistency

```
Sample 1: Perfect → all metrics = 1.0
Sample 2: All metrics = 0.5
Sample 3: All metrics = 0.0

Expected averages: faithfulness=0.5, relevance=0.5, precision=0.5, recall=0.5, overall≈0.49
```

---

## Summary

This walkthrough implemented four fundamental RAG evaluation metrics:
1. **Faithfulness** — does the answer stick to the evidence? (contradiction detection)
2. **Answer Relevance** — does the answer address the user's question?
3. **Context Precision** — how clean is the retrieval? (noise filtering)
4. **Context Recall** — how complete is the retrieval? (coverage)

The key insight is that no single metric captures RAG quality. A system can have perfect recall but hallucinate (low faithfulness), or be faithful but retrieve nothing useful (low precision). A weighted combination gives a single-number summary, but individual metrics provide actionable diagnostic signals for improving specific pipeline components.
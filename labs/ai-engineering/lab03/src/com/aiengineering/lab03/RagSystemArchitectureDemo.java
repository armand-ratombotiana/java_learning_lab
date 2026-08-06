package com.aiengineering.lab03;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates RAG (Retrieval-Augmented Generation) system architecture:
 * document chunking strategies, embedding-based retrieval, hybrid search
 * (combining keyword BM25 with vector similarity), and a complete
 * retrieval pipeline.
 */
public class RagSystemArchitectureDemo {

    // ---------- Chunking Strategies ----------

    /** Represents a chunk of text with its embedding vector. */
    public record Chunk(String id, String text, int tokenCount, float[] embedding) {}

    /** Interface for chunking strategies. */
    @FunctionalInterface
    interface ChunkingStrategy {
        List<Chunk> chunk(String document, int chunkSize, int overlap);
    }

    /** Fixed-size token-aware chunking with overlap. */
    static final ChunkingStrategy FIXED_SIZE = (doc, size, overlap) -> {
        String[] words = doc.split("\\s+");
        List<Chunk> chunks = new ArrayList<>();
        int start = 0;
        int chunkId = 0;
        while (start < words.length) {
            int end = Math.min(start + size, words.length);
            String text = String.join(" ", Arrays.copyOfRange(words, start, end));
            chunks.add(new Chunk("chunk-" + chunkId++, text, end - start,
                computeEmbedding(text)));
            start += (size - overlap);
        }
        return chunks;
    };

    /** Sentence-aware chunking that respects sentence boundaries. */
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
                chunks.add(new Chunk("chunk-" + chunkId++, text, wordCount,
                    computeEmbedding(text)));
                int overlapSentences = Math.max(1, buffer.size() / 2);
                buffer = new ArrayList<>(buffer.subList(buffer.size() - overlapSentences, buffer.size()));
            }
        }
        if (!buffer.isEmpty()) {
            String text = String.join(" ", buffer);
            int wc = text.split("\\s+").length;
            chunks.add(new Chunk("chunk-" + chunkId++, text, wc, computeEmbedding(text)));
        }
        return chunks;
    };

    // ---------- Embedding Simulation ----------

    static float[] computeEmbedding(String text) {
        // Simplified: hash-based pseudo-embedding of dimension 8
        float[] emb = new float[8];
        Arrays.fill(emb, 0.0f);
        int hash = text.hashCode();
        Random rng = new Random(hash);
        for (int i = 0; i < emb.length; i++) emb[i] = rng.nextFloat();
        double sum = 0;
        for (float f : emb) sum += f * f;
        double norm = Math.sqrt(sum);
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

    // ---------- BM25 Keyword Scoring ----------

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

    // ---------- Hybrid Search ----------

    static class HybridSearchResult {
        final Chunk chunk;
        final double vectorScore;
        final double keywordScore;
        final double combinedScore;

        HybridSearchResult(Chunk c, double vs, double ks) {
            this.chunk = c;
            this.vectorScore = vs;
            this.keywordScore = ks;
            this.combinedScore = 0.7 * vs + 0.3 * ks; // weighted combination
        }
    }

    static List<HybridSearchResult> hybridSearch(List<Chunk> chunks, String query, float[] queryEmb, int topK) {
        double avgDocLen = chunks.stream().mapToInt(Chunk::tokenCount).average().orElse(1);
        int totalDocs = chunks.size();
        return chunks.stream()
            .map(c -> new HybridSearchResult(c,
                cosineSimilarity(queryEmb, c.embedding()),
                bm25Score(query, c.text(), avgDocLen, totalDocs, 1.2, 0.75)))
            .sorted((a, b) -> Double.compare(b.combinedScore, a.combinedScore))
            .limit(topK)
            .toList();
    }

    // ---------- Main Demo ----------

    public static void main(String[] args) {
        System.out.println("=== AI Engineering Academy — Lab 03: RAG System Architecture ===\n");

        String document = """
            Transformers have revolutionized natural language processing.
            They rely on self-attention mechanisms to process sequences.
            Unlike RNNs, transformers process all tokens in parallel.
            This makes them highly efficient for training on large datasets.
            The core innovation is the attention mechanism which computes
            weighted combinations of all input positions.
            BERT and GPT are two famous transformer-based architectures.
            BERT uses encoder-only design while GPT uses decoder-only.
            Recent large language models like GPT-4 show remarkable abilities.
            These models can perform tasks like translation, summarization,
            and question answering with high accuracy.
            """;

        System.out.println("--- Chunking Strategies ---");
        System.out.println("Fixed-size chunks (size=10, overlap=3):");
        List<Chunk> fixedChunks = FIXED_SIZE.chunk(document, 10, 3);
        fixedChunks.forEach(c -> System.out.printf("  %s [%d tokens]: %s...%n",
            c.id(), c.tokenCount(), c.text().substring(0, Math.min(40, c.text().length()))));

        System.out.println("\nSentence-aware chunks (size=10, overlap=2):");
        List<Chunk> sentChunks = SENTENCE_AWARE.chunk(document, 10, 2);
        sentChunks.forEach(c -> System.out.printf("  %s [%d tokens]: %s...%n",
            c.id(), c.tokenCount(), c.text().substring(0, Math.min(40, c.text().length()))));

        System.out.println("\n--- Hybrid Search ---");
        String query = "attention mechanism transformer";
        float[] queryEmb = computeEmbedding(query);
        System.out.println("Query: \"" + query + "\"");

        List<HybridSearchResult> results = hybridSearch(fixedChunks, query, queryEmb, 3);
        System.out.println("Top 3 results (combined score = 0.7*vector + 0.3*kw):");
        for (int i = 0; i < results.size(); i++) {
            HybridSearchResult r = results.get(i);
            System.out.printf("  %d. %s | vector=%.4f keyword=%.4f combined=%.4f%n",
                i + 1, r.chunk.id(), r.vectorScore, r.keywordScore, r.combinedScore);
            System.out.println("     \"" + r.chunk.text().substring(0, Math.min(60, r.chunk.text().length())) + "...\"");
        }

        System.out.println("\nDemo complete. Processed " + fixedChunks.size() + " fixed chunks, "
            + sentChunks.size() + " sentence chunks.");
    }
}

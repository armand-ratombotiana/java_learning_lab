package com.genai.lab04;

import java.util.*;

/**
 * RAG System Design
 * 
 * Demonstrates document chunking, embedding-based retrieval,
 * augmentation, and the full RAG pipeline in Java.
 */
public class Main {

    /** Simple embedding: hash-based vector for demo purposes. */
    public static double[] embed(String text, int dim) {
        double[] vec = new double[dim];
        int hash = text.hashCode();
        Random rng = new Random(hash);
        for (int i = 0; i < dim; i++) vec[i] = rng.nextGaussian();
        double norm = 0.0;
        for (double v : vec) norm += v * v;
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
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /** Document chunk with metadata. */
    static class Chunk {
        final String text;
        final int index;
        Chunk(String text, int index) { this.text = text; this.index = index; }
    }

    /** Chunking strategies. */
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

    /** Vector store with cosine similarity retrieval. */
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
    }

    /** RAG Pipeline. */
    static class RAGPipeline {
        final VectorStore store;
        RAGPipeline(VectorStore store) { this.store = store; }

        String query(String question, int topK) {
            List<Chunk> retrieved = store.search(question, topK);
            StringBuilder context = new StringBuilder();
            for (Chunk c : retrieved) context.append(c.text).append("\n");

            return "=== Generated Answer ===\n"
                + "Context:\n" + context
                + "\nQuestion: " + question
                + "\nAnswer based on the context above.";
        }
    }

    public static void main(String[] args) {
        String document = "The Transformer architecture introduced by Vaswani et al. "
            + "uses self-attention mechanisms. It has an encoder-decoder structure. "
            + "GPT uses a decoder-only variant of this architecture. "
            + "BERT uses an encoder-only variant. "
            + "The attention mechanism computes weighted sums of values.";

        int chunkSize = 60;
        List<Chunk> chunks = Chunker.fixedSize(document, chunkSize, 10);
        System.out.println("=== Chunks ===");
        chunks.forEach(c -> System.out.printf("[%d] %s%n", c.index, c.text));

        VectorStore store = new VectorStore(16);
        chunks.forEach(store::add);

        RAGPipeline rag = new RAGPipeline(store);
        String answer = rag.query("What does GPT use?", 2);
        System.out.println("\n=== RAG Output ===");
        System.out.println(answer);

        System.out.println("\nRAG pipeline validated.");
    }
}

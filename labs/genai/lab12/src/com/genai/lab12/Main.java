package com.genai.lab12;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cost Optimization for LLMs
 * 
 * Demonstrates semantic caching, dynamic batching, prompt compression,
 * and speculative decoding concepts in Java.
 */
public class Main {

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

    /** Speculative decoding simulation. */
    static class SpeculativeDecoder {
        static int[] draft(String prompt, int length) {
            int[] tokens = new int[length];
            Random rng = new Random(prompt.hashCode());
            for (int i = 0; i < length; i++) tokens[i] = rng.nextInt(100);
            return tokens;
        }

        static double verify(int[] draftTokens) {
            Random rng = new Random();
            return Arrays.stream(draftTokens).filter(t -> rng.nextDouble() > 0.2).count()
                / (double) draftTokens.length;
        }
    }

    public static void main(String[] args) {
        ExactCache cache = new ExactCache(100);
        cache.put("What is AI?", "Artificial Intelligence...");
        System.out.println("=== Exact Cache ===");
        System.out.println("Hit: " + cache.get("What is AI?"));
        System.out.println("Miss: " + cache.get("What is ML?"));
        System.out.printf("Hit rate: %.2f%n", cache.hitRate());

        SemanticCache sc = new SemanticCache(0.85);
        sc.put("capital of France", "Paris");
        System.out.println("\n=== Semantic Cache ===");
        System.out.println("Similar query: " + sc.get("What is the capital of France?"));

        DynamicBatcher batcher = new DynamicBatcher(4);
        for (int i = 0; i < 6; i++) batcher.add("Request " + i);
        List<String> batch = batcher.drain();
        System.out.println("\n=== Dynamic Batching ===");
        System.out.println("Batch size: " + batch.size());
        System.out.println(batcher.processBatch(batch));

        PromptCompressor pc = new PromptCompressor();
        System.out.println("\n=== Prompt Compression ===");
        System.out.println("Original: 'The cat is on the mat'");
        System.out.println("Compressed: '" + pc.compress("The cat is on the mat") + "'");

        System.out.println("\n=== Speculative Decoding ===");
        int[] draft = SpeculativeDecoder.draft("hello", 5);
        double acceptRate = SpeculativeDecoder.verify(draft);
        System.out.printf("Draft acceptance rate: %.2f%n", acceptRate);

        System.out.println("\nCost optimization concepts validated.");
    }
}

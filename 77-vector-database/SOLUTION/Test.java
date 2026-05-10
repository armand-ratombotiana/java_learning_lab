package com.learning.vector;

import java.util.*;

public class Test {

    public static void main(String[] args) {
        System.out.println("Running Vector Database Tests\n");

        testEmbeddingModelCreation();
        testEmbeddingGeneration();
        testCosineSimilarity();
        testInMemoryStore();
        testHybridSearch();

        System.out.println("\nAll tests passed!");
    }

    private static void testEmbeddingModelCreation() {
        System.out.println("Test: Embedding Model Creation");
        Solution.EmbeddingExample embeddingExample = new Solution.EmbeddingExample();

        System.out.println("  - Embedding model structure ready");
    }

    private static void testEmbeddingGeneration() {
        System.out.println("Test: Embedding Generation");
        Solution.EmbeddingExample embeddingExample = new Solution.EmbeddingExample();

        float[] embedding = embeddingExample.generateEmbedding(null, "Hello world");
        assert embedding != null && embedding.length > 0 : "Embedding should not be empty";
        System.out.println("  - Embedding generation ready, dimension: " + embedding.length);
    }

    private static void testCosineSimilarity() {
        System.out.println("Test: Cosine Similarity");
        Solution.EmbeddingExample embeddingExample = new Solution.EmbeddingExample();

        float[] vec1 = new float[]{1.0f, 0.0f, 0.0f};
        float[] vec2 = new float[]{1.0f, 0.0f, 0.0f};
        float similarity = embeddingExample.calculateCosineSimilarity(vec1, vec2);

        assert similarity == 1.0f : "Identical vectors should have similarity 1.0";
        System.out.println("  - Cosine similarity: " + similarity);
    }

    private static void testInMemoryStore() {
        System.out.println("Test: In-Memory Vector Store");
        Solution.InMemoryVectorStoreExample storeExample = new Solution.InMemoryVectorStoreExample();

        var store = storeExample.createInMemoryStore();
        assert store != null : "Store should not be null";

        System.out.println("  - In-memory store created");
    }

    private static void testHybridSearch() {
        System.out.println("Test: Hybrid Search");
        Solution.HybridSearchExample hybridSearch = new Solution.HybridSearchExample();

        List<Map<String, Object>> metadataResults = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        result.put("text", "Sample document");
        metadataResults.add(result);

        List<String> searchResults = hybridSearch.hybridSearch(null, metadataResults, "sample", 5);
        assert searchResults != null : "Search results should not be null";

        System.out.println("  - Hybrid search works, found: " + searchResults.size() + " results");
    }
}
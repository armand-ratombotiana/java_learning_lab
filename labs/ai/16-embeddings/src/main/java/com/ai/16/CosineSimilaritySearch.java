package com.ai16;

import java.util.*;

public class CosineSimilaritySearch {
    private Map<String, double[]> index;

    public CosineSimilaritySearch() {
        this.index = new HashMap<>();
    }

    public void addDocument(String id, double[] embedding) {
        index.put(id, embedding);
    }

    public List<Map.Entry<String, Double>> search(double[] query, int topK) {
        List<Map.Entry<String, Double>> results = new ArrayList<>();
        for (Map.Entry<String, double[]> entry : index.entrySet()) {
            double sim = cosineSimilarity(query, entry.getValue());
            results.add(new AbstractMap.SimpleEntry<>(entry.getKey(), sim));
        }
        results.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return results.subList(0, Math.min(topK, results.size()));
    }

    private double cosineSimilarity(double[] a, double[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10);
    }

    public double[][] similarityMatrix() {
        int n = index.size();
        List<String> ids = new ArrayList<>(index.keySet());
        double[][] matrix = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                matrix[i][j] = cosineSimilarity(index.get(ids.get(i)), index.get(ids.get(j)));
        return matrix;
    }

    public static void main(String[] args) {
        System.out.println("=== Cosine Similarity Search Demo ===");
        CosineSimilaritySearch search = new CosineSimilaritySearch();
        Random rng = new Random(42);
        search.addDocument("doc1", new double[]{0.9, 0.1, 0.0});
        search.addDocument("doc2", new double[]{0.8, 0.2, 0.1});
        search.addDocument("doc3", new double[]{0.1, 0.9, 0.8});
        search.addDocument("doc4", new double[]{0.0, 0.8, 0.9});
        double[] query = {0.85, 0.15, 0.05};
        List<Map.Entry<String, Double>> results = search.search(query, 3);
        System.out.println("Query vector: " + Arrays.toString(query));
        for (Map.Entry<String, Double> r : results)
            System.out.println("  " + r.getKey() + " score=" + r.getValue());
    }
}

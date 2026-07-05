package com.ai16;

import java.util.*;

public class EmbeddingOperations {

    public static double[] add(double[] a, double[] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++)
            result[i] = a[i] + b[i];
        return result;
    }

    public static double[] subtract(double[] a, double[] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++)
            result[i] = a[i] - b[i];
        return result;
    }

    public static double cosineSimilarity(double[] a, double[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10);
    }

    public static String findClosestWord(double[] query, Map<String, double[]> embeddings) {
        String best = null;
        double bestSim = -Double.MAX_VALUE;
        for (Map.Entry<String, double[]> e : embeddings.entrySet()) {
            double sim = cosineSimilarity(query, e.getValue());
            if (sim > bestSim) {
                bestSim = sim;
                best = e.getKey();
            }
        }
        return best;
    }

    public static List<String> topKSimilar(double[] query, Map<String, double[]> embeddings, int k) {
        List<Map.Entry<String, Double>> sims = new ArrayList<>();
        for (Map.Entry<String, double[]> e : embeddings.entrySet())
            sims.add(Map.entry(e.getKey(), cosineSimilarity(query, e.getValue())));
        sims.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(k, sims.size()); i++)
            result.add(sims.get(i).getKey());
        return result;
    }

    public static double[] embeddingAnalogy(double[] king, double[] man, double[] woman) {
        return subtract(add(king, woman), man);
    }

    public static void main(String[] args) {
        System.out.println("=== Embedding Operations Demo ===");
        Map<String, double[]> embeddings = new HashMap<>();
        Random rng = new Random(42);
        for (String w : List.of("king", "queen", "man", "woman", "prince", "princess")) {
            double[] emb = new double[10];
            for (int i = 0; i < 10; i++) emb[i] = rng.nextDouble();
            embeddings.put(w, emb);
        }
        double[] queenSim = embeddings.get("queen");
        System.out.println("Closest to queen: " + findClosestWord(queenSim, embeddings));
        System.out.println("Top 3 similar to queen: " + topKSimilar(queenSim, embeddings, 3));
    }
}

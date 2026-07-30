package com.systemdesign.deep.lab07;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Lab 07: Recommendation Systems — Collaborative filtering,
 * content-based, hybrid, matrix factorization, and real-time recs.
 */
public class RecommendationSystemsLab {

    // ──────────────────────────────────────────────
    // 1. Collaborative Filtering (Item-Based)
    // ──────────────────────────────────────────────
    static class CollaborativeFiltering {
        final Map<String, Map<String, Double>> userRatings; // user -> (item -> rating)

        CollaborativeFiltering(Map<String, Map<String, Double>> userRatings) {
            this.userRatings = userRatings;
        }

        // Compute item-item cosine similarity
        Map<String, Map<String, Double>> computeItemSimilarity() {
            // Collect all items
            Set<String> allItems = new HashSet<>();
            for (var ur : userRatings.values()) allItems.addAll(ur.keySet());

            Map<String, Map<String, Double>> similarity = new HashMap<>();
            var items = List.copyOf(allItems);

            for (int i = 0; i < items.size(); i++) {
                for (int j = i + 1; j < items.size(); j++) {
                    String itemA = items.get(i), itemB = items.get(j);
                    double sim = cosineSimilarity(itemA, itemB);
                    if (sim > 0) {
                        similarity.computeIfAbsent(itemA, k -> new HashMap<>()).put(itemB, sim);
                        similarity.computeIfAbsent(itemB, k -> new HashMap<>()).put(itemA, sim);
                    }
                }
            }
            return similarity;
        }

        double cosineSimilarity(String itemA, String itemB) {
            double dot = 0, normA = 0, normB = 0;
            int count = 0;
            for (var entry : userRatings.entrySet()) {
                Double rA = entry.getValue().get(itemA);
                Double rB = entry.getValue().get(itemB);
                if (rA != null && rB != null) {
                    dot += rA * rB;
                    normA += rA * rA;
                    normB += rB * rB;
                    count++;
                }
            }
            if (count < 2) return 0;
            return dot / (Math.sqrt(normA) * Math.sqrt(normB));
        }

        List<String> recommend(String userId, int topN) {
            var ratings = userRatings.get(userId);
            if (ratings == null) return List.of();

            var itemSim = computeItemSimilarity();
            Map<String, Double> scores = new HashMap<>();

            for (var rated : ratings.entrySet()) {
                var similarItems = itemSim.getOrDefault(rated.getKey(), Map.of());
                for (var sim : similarItems.entrySet()) {
                    if (!ratings.containsKey(sim.getKey())) {
                        scores.merge(sim.getKey(), sim.getValue() * rated.getValue(), Double::sum);
                    }
                }
            }

            return scores.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(topN)
                    .map(Map.Entry::getKey)
                    .toList();
        }
    }

    // ──────────────────────────────────────────────
    // 2. Content-Based Filtering
    // ──────────────────────────────────────────────
    static class ContentBasedFiltering {
        final Map<String, String> itemFeatures; // item -> description

        ContentBasedFiltering(Map<String, String> itemFeatures) {
            this.itemFeatures = itemFeatures;
        }

        // Simple TF-IDF-like vector
        Map<String, Double> buildVector(String text) {
            Map<String, Double> vec = new HashMap<>();
            String[] tokens = text.toLowerCase().split("\\W+");
            for (String t : tokens) {
                if (t.length() > 2) vec.merge(t, 1.0, Double::sum);
            }
            double norm = Math.sqrt(vec.values().stream().mapToDouble(v -> v * v).sum());
            if (norm > 0) vec.replaceAll((k, v) -> v / norm);
            return vec;
        }

        double cosineSimilarity(Map<String, Double> a, Map<String, Double> b) {
            double dot = 0;
            for (var e : a.entrySet()) {
                dot += e.getValue() * b.getOrDefault(e.getKey(), 0.0);
            }
            return dot;
        }

        List<String> recommend(String userId, String userHistory, int topN) {
            var userVec = buildVector(userHistory);
            return itemFeatures.entrySet().stream()
                    .map(e -> Map.entry(e.getKey(), cosineSimilarity(userVec, buildVector(e.getValue()))))
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(topN)
                    .map(Map.Entry::getKey)
                    .toList();
        }
    }

    // ──────────────────────────────────────────────
    // 3. Matrix Factorization (SVD-like)
    // ──────────────────────────────────────────────
    static class MatrixFactorization {
        final int factors;
        final double learningRate;
        final double regularization;
        final Map<String, double[]> userFactors = new ConcurrentHashMap<>();
        final Map<String, double[]> itemFactors = new ConcurrentHashMap<>();

        MatrixFactorization(int factors, double learningRate, double regularization) {
            this.factors = factors;
            this.learningRate = learningRate;
            this.regularization = regularization;
        }

        void train(Map<String, Map<String, Double>> ratings, int epochs) {
            Random rand = new Random(42);
            // Initialize factors
            for (var ue : ratings.entrySet()) {
                userFactors.put(ue.getKey(),
                        rand.doubles(factors, -0.1, 0.1).toArray());
                for (var ie : ue.getValue().entrySet()) {
                    itemFactors.computeIfAbsent(ie.getKey(),
                            k -> rand.doubles(factors, -0.1, 0.1).toArray());
                }
            }

            for (int epoch = 0; epoch < epochs; epoch++) {
                double totalError = 0;
                int count = 0;
                for (var ue : ratings.entrySet()) {
                    String user = ue.getKey();
                    double[] uVec = userFactors.get(user);
                    for (var ie : ue.getValue().entrySet()) {
                        String item = ie.getKey();
                        double rating = ie.getValue();
                        double[] iVec = itemFactors.get(item);

                        double pred = dot(uVec, iVec);
                        double error = rating - pred;
                        totalError += error * error;
                        count++;

                        // SGD update
                        for (int f = 0; f < factors; f++) {
                            double uGrad = error * iVec[f] - regularization * uVec[f];
                            double iGrad = error * uVec[f] - regularization * iVec[f];
                            uVec[f] += learningRate * uGrad;
                            iVec[f] += learningRate * iGrad;
                        }
                    }
                }
                if (epoch % 5 == 0) {
                    System.out.println("  [MF] Epoch " + epoch + " RMSE: "
                            + String.format("%.4f", Math.sqrt(totalError / count)));
                }
            }
        }

        double dot(double[] a, double[] b) {
            double sum = 0;
            for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
            return sum;
        }

        double predict(String user, String item) {
            double[] u = userFactors.get(user);
            double[] i = itemFactors.get(item);
            if (u == null || i == null) return 0;
            return dot(u, i);
        }

        List<String> recommend(String user, Set<String> allItems, int topN) {
            var rated = allItems.stream()
                    .filter(item -> predict(user, item) > 0)
                    .sorted(Comparator.<String>comparingDouble(item -> predict(user, item)).reversed())
                    .limit(topN)
                    .toList();
            if (rated.size() < topN) {
                // Fill remaining with unrated items
                var unrated = allItems.stream()
                        .filter(item -> !rated.contains(item))
                        .sorted(Comparator.<String>comparingDouble(item -> predict(user, item)).reversed())
                        .limit(topN - rated.size())
                        .toList();
                var result = new ArrayList<>(rated);
                result.addAll(unrated);
                return result;
            }
            return rated;
        }
    }

    // ──────────────────────────────────────────────
    // 4. Real-Time Recommendation Engine
    // ──────────────────────────────────────────────
    static class RealTimeRecommender {
        final Map<String, Set<String>> userInteractions = new ConcurrentHashMap<>();
        final Map<String, Double> itemPopularity = new ConcurrentHashMap<>();
        final Map<String, Map<String, Integer>> coOccurrence = new ConcurrentHashMap<>();
        final AtomicInteger totalEvents = new AtomicInteger();

        void recordEvent(String userId, String itemId) {
            userInteractions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(itemId);
            itemPopularity.merge(itemId, 1.0, Double::sum);
            // Update co-occurrence with user's other recent items
            var userItems = userInteractions.get(userId);
            for (var other : userItems) {
                if (!other.equals(itemId)) {
                    coOccurrence.computeIfAbsent(itemId, k -> new ConcurrentHashMap<>())
                            .merge(other, 1, Integer::sum);
                    coOccurrence.computeIfAbsent(other, k -> new ConcurrentHashMap<>())
                            .merge(itemId, 1, Integer::sum);
                }
            }
            totalEvents.incrementAndGet();
        }

        List<String> recommend(String userId, int topN) {
            var userItems = userInteractions.getOrDefault(userId, Set.of());
            Map<String, Double> scores = new HashMap<>();

            // Item-based from co-occurrence
            for (var item : userItems) {
                var coOccur = coOccurrence.getOrDefault(item, Map.of());
                for (var e : coOccur.entrySet()) {
                    if (!userItems.contains(e.getKey())) {
                        scores.merge(e.getKey(), e.getValue() * 1.0, Double::sum);
                    }
                }
            }

            // Boost popularity for new users (cold start)
            if (userItems.isEmpty()) {
                return itemPopularity.entrySet().stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                        .limit(topN)
                        .map(Map.Entry::getKey)
                        .toList();
            }

            return scores.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(topN)
                    .map(Map.Entry::getKey)
                    .toList();
        }
    }

    // ──────────────────────────────────────────────
    // Main
    // ──────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  Lab 07: Recommendation Systems Deep-Dive   ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        // 1. Collaborative Filtering
        System.out.println("1. Collaborative Filtering (Item-Based)");
        var ratings = Map.of(
                "Alice", Map.of("MovieA", 5.0, "MovieB", 3.0, "MovieC", 4.0),
                "Bob", Map.of("MovieA", 4.0, "MovieD", 5.0, "MovieE", 2.0),
                "Charlie", Map.of("MovieB", 4.0, "MovieC", 5.0, "MovieD", 3.0),
                "Diana", Map.of("MovieA", 2.0, "MovieC", 5.0, "MovieE", 4.0)
        );
        var cf = new CollaborativeFiltering(ratings);
        var recs = cf.recommend("Alice", 3);
        System.out.println("  Recommendations for Alice: " + recs);
        System.out.println();

        // 2. Content-Based Filtering
        System.out.println("2. Content-Based Filtering");
        var itemFeatures = Map.of(
                "Article1", "machine learning deep neural networks",
                "Article2", "distributed systems consensus protocols",
                "Article3", "deep learning neural network training",
                "Article4", "caching distributed database systems",
                "Article5", "java spring boot microservice architecture"
        );
        var contentBased = new ContentBasedFiltering(itemFeatures);
        var userProfile = "machine learning neural network deep learning";
        var contentRecs = contentBased.recommend("user1", userProfile, 3);
        System.out.println("  Content-based recommendations: " + contentRecs);
        System.out.println();

        // 3. Matrix Factorization
        System.out.println("3. Matrix Factorization (SGD)");
        var mf = new MatrixFactorization(5, 0.01, 0.02);
        mf.train(ratings, 20);
        System.out.println("  Predicted rating for Alice-MovieD: "
                + String.format("%.2f", mf.predict("Alice", "MovieD")));
        System.out.println("  Predicted rating for Bob-MovieC: "
                + String.format("%.2f", mf.predict("Bob", "MovieC")));
        System.out.println();

        // 4. Real-Time Recommendation
        System.out.println("4. Real-Time Recommendations");
        var rt = new RealTimeRecommender();
        rt.recordEvent("user1", "itemA");
        rt.recordEvent("user1", "itemB");
        rt.recordEvent("user2", "itemA");
        rt.recordEvent("user1", "itemC");
        rt.recordEvent("user2", "itemC");
        rt.recordEvent("user2", "itemD");
        System.out.println("  Real-time recs for user1: " + rt.recommend("user1", 3));
        System.out.println("  Real-time recs for new user: " + rt.recommend("new-user", 3));
        System.out.println();

        System.out.println("All recommendation approaches demonstrated successfully.");
    }

    static class AtomicInteger {
        private int value = 0;
        synchronized int incrementAndGet() { return ++value; }
        int get() { return value; }
    }
}

package com.recsys.ml;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class ContentBasedFilter {

    private final Map<String, double[]> itemEmbeddings;
    private final Map<String, Map<String, Double>> userPreferences;
    private final int embeddingSize;

    public ContentBasedFilter() {
        this.itemEmbeddings = new HashMap<>();
        this.userPreferences = new HashMap<>();
        this.embeddingSize = 50;
    }

    public void addItem(String itemId, Map<String, Double> features) {
        double[] embedding = featuresToEmbedding(features);
        itemEmbeddings.put(itemId, embedding);
    }

    public void recordUserPreference(String userId, String itemId, double rating) {
        double[] itemEmbedding = itemEmbeddings.get(itemId);
        if (itemEmbedding == null) return;

        userPreferences.computeIfAbsent(userId, k -> new HashMap<>());

        Map<String, Double> prefs = userPreferences.get(userId);
        double[] profile = prefs.computeIfAbsent("profile", k -> new double[embeddingSize]);

        for (int i = 0; i < embeddingSize; i++) {
            profile[i] += itemEmbedding[i] * rating / 5.0;
        }
    }

    public List<String> recommendItems(String userId, int topN) {
        double[] userProfile = userPreferences.getOrDefault(userId, Map.of())
            .getOrDefault("profile", new double[embeddingSize]);

        return itemEmbeddings.entrySet().stream()
            .filter(e -> !hasInteracted(userId, e.getKey()))
            .map(e -> Map.entry(e.getKey(), cosineSimilarity(userProfile, e.getValue())))
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(topN)
            .map(Map.Entry::getKey)
            .toList();
    }

    private double[] featuresToEmbedding(Map<String, Double> features) {
        double[] embedding = new double[embeddingSize];
        int idx = 0;
        for (Map.Entry<String, Double> entry : features.entrySet()) {
            if (idx < embeddingSize) {
                embedding[idx++] = entry.getValue();
            }
        }
        for (int i = idx; i < embeddingSize; i++) {
            embedding[i] = Math.random() * 0.1;
        }
        return embedding;
    }

    private double cosineSimilarity(double[] a, double[] b) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0 || normB == 0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private boolean hasInteracted(String userId, String itemId) {
        return false;
    }
}
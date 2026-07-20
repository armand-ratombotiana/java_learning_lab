package com.capstone.ecommerce;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RecommendationEngine {
    private final UserBehaviorStore userBehavior = new UserBehaviorStore();
    private final Map<String, Set<String>> productPurchaseGraph = new ConcurrentHashMap<>();
    private static final int DEFAULT_RECOMMENDATIONS = 10;
    private static final double MIN_SIMILARITY = 0.1;

    public record UserAction(String userId, String productId, String actionType, long timestamp) {}

    public static class UserBehaviorStore {
        final Map<String, List<UserAction>> actions = new ConcurrentHashMap<>();
        final Map<String, Set<String>> userProducts = new ConcurrentHashMap<>();
        final Map<String, Set<String>> productUsers = new ConcurrentHashMap<>();

        public void recordAction(UserAction action) {
            actions.computeIfAbsent(action.userId(), k -> new ArrayList<>()).add(action);
            userProducts.computeIfAbsent(action.userId(), k -> ConcurrentHashMap.newKeySet()).add(action.productId());
            productUsers.computeIfAbsent(action.productId(), k -> ConcurrentHashMap.newKeySet()).add(action.userId());
        }

        public List<UserAction> getUserActions(String userId) {
            return List.copyOf(actions.getOrDefault(userId, List.of()));
        }

        public Set<String> getUserProducts(String userId) {
            return userProducts.getOrDefault(userId, Set.of());
        }

        public Set<String> getProductUsers(String productId) {
            return productUsers.getOrDefault(productId, Set.of());
        }

        public void recordPurchase(String userId, String productId) {
            recordAction(new UserAction(userId, productId, "purchase", System.currentTimeMillis()));
            productPurchaseGraph.computeIfAbsent(productId, k -> ConcurrentHashMap.newKeySet());
        }

        public int totalUsers() { return userProducts.size(); }

        public int totalProducts() { return productUsers.size(); }
    }

    private final Map<String, Set<String>> productPurchaseGraph = new ConcurrentHashMap<>();

    public void recordAction(UserAction action) {
        userBehavior.recordAction(action);
    }

    public void recordPurchase(String userId, String productId) {
        userBehavior.recordPurchase(userId, productId);
        productPurchaseGraph.computeIfAbsent(productId, k -> ConcurrentHashMap.newKeySet());
    }

    public record ProductSimilarity(String productId, double similarity) {}

    public List<String> getCollaborativeRecommendations(String userId, int maxResults) {
        Set<String> purchased = userBehavior.getUserProducts(userId);
        if (purchased.isEmpty()) return List.of();

        Map<String, Double> similarityScores = new HashMap<>();
        Map<String, Integer> coOccurrences = new HashMap<>();

        for (String productId : purchased) {
            Set<String> similarUsers = userBehavior.getProductUsers(productId);
            for (String otherUser : similarUsers) {
                if (otherUser.equals(userId)) continue;
                Set<String> otherProducts = userBehavior.getUserProducts(otherUser);
                for (String otherProduct : otherProducts) {
                    if (purchased.contains(otherProduct)) continue;
                    similarityScores.merge(otherProduct, 1.0, Double::sum);
                    coOccurrences.merge(otherProduct, 1, Integer::sum);
                }
            }
        }

        return similarityScores.entrySet().stream()
            .map(e -> new ProductSimilarity(e.getKey(),
                e.getValue() / Math.sqrt(coOccurrences.getOrDefault(e.getKey(), 1))))
            .filter(s -> s.similarity() >= MIN_SIMILARITY)
            .sorted(Comparator.comparing(ProductSimilarity::similarity).reversed())
            .limit(maxResults > 0 ? maxResults : DEFAULT_RECOMMENDATIONS)
            .map(ProductSimilarity::productId)
            .collect(Collectors.toList());
    }

    public List<String> getPopularProducts(int limit) {
        return userBehavior.productUsers.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    public List<String> getFrequentlyBoughtTogether(String productId, int limit) {
        Set<String> coPurchased = productPurchaseGraph.get(productId);
        if (coPurchased == null || coPurchased.isEmpty()) return List.of();
        return coPurchased.stream().limit(limit).collect(Collectors.toList());
    }

    public int getUserCount() { return userBehavior.totalUsers(); }

    public int getProductCount() { return userBehavior.totalProducts(); }

    public void reset() { userBehavior.actions.clear(); userBehavior.userProducts.clear(); userBehavior.productUsers.clear(); }
}

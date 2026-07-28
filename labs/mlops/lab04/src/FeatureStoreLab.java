package com.mlops.lab04;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Feature Store Architecture — Lab 04.
 * <p>
 * Demonstrates online and offline feature store concepts, feature groups,
 * point-in-time joins, and consistent feature serving for training and inference.
 */
public class FeatureStoreLab {

    /** A feature definition with name, type, and optional transformation. */
    static class FeatureDefinition {
        final String name;
        final String type;
        final Function<Map<String, Object>, Object> transformation;

        FeatureDefinition(String name, String type,
                           Function<Map<String, Object>, Object> transformation) {
            this.name = name;
            this.type = type;
            this.transformation = transformation;
        }
    }

    /** A group of related feature definitions. */
    static class FeatureGroup {
        final String name;
        final List<FeatureDefinition> features = new ArrayList<>();

        FeatureGroup(String name) { this.name = name; }

        FeatureGroup addFeature(String name, String type,
                                 Function<Map<String, Object>, Object> transformation) {
            features.add(new FeatureDefinition(name, type, transformation));
            return this;
        }
    }

    /** Offline store: computes features in batch and persists them. */
    static class OfflineStore {
        private final Map<String, Map<String, List<TimeSeriesValue>>> storage = new ConcurrentHashMap<>();

        static class TimeSeriesValue {
            final Instant timestamp;
            final Object value;
            TimeSeriesValue(Instant timestamp, Object value) {
                this.timestamp = timestamp;
                this.value = value;
            }
        }

        void computeAndStore(String entityId, FeatureGroup group, Map<String, Object> rawData, Instant ts) {
            String key = entityId + ":" + group.name;
            storage.putIfAbsent(key, new ConcurrentHashMap<>());
            Map<String, List<TimeSeriesValue>> featureStore = storage.get(key);
            for (FeatureDefinition fd : group.features) {
                Object value = fd.transformation.apply(rawData);
                featureStore.computeIfAbsent(fd.name, k -> new ArrayList<>())
                        .add(new TimeSeriesValue(ts, value));
            }
        }

        Map<String, Object> getPointInTime(String entityId, String groupName, Instant atTime) {
            String key = entityId + ":" + groupName;
            Map<String, List<TimeSeriesValue>> featureStore = storage.get(key);
            if (featureStore == null) return Map.of();
            Map<String, Object> result = new HashMap<>();
            for (Map.Entry<String, List<TimeSeriesValue>> entry : featureStore.entrySet()) {
                // Find most recent value at or before atTime
                entry.getValue().stream()
                        .filter(v -> !v.timestamp.isAfter(atTime))
                        .max(Comparator.comparing(v -> v.timestamp))
                        .ifPresent(v -> result.put(entry.getKey(), v.value));
            }
            return result;
        }
    }

    /** Online store: low-latency, in-memory KV store for real-time serving. */
    static class OnlineStore {
        private final Map<String, Map<String, Object>> store = new ConcurrentHashMap<>();
        private final Map<String, Instant> ttls = new ConcurrentHashMap<>();
        private final long ttlSeconds;

        OnlineStore(long ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }

        void set(String entityId, String groupName, Map<String, Object> features) {
            String key = entityId + ":" + groupName;
            store.put(key, new ConcurrentHashMap<>(features));
            ttls.put(key, Instant.now().plusSeconds(ttlSeconds));
        }

        Map<String, Object> get(String entityId, String groupName) {
            String key = entityId + ":" + groupName;
            Instant expiry = ttls.get(key);
            if (expiry == null || Instant.now().isAfter(expiry)) {
                store.remove(key);
                ttls.remove(key);
                return Map.of();
            }
            return store.getOrDefault(key, Map.of());
        }

        void invalidate(String entityId, String groupName) {
            String key = entityId + ":" + groupName;
            store.remove(key);
            ttls.remove(key);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Feature Store Architecture ===\n");

        // Define feature groups
        FeatureGroup userFeatures = new FeatureGroup("user_features")
                .addFeature("age", "int", raw -> raw.get("age"))
                .addFeature("account_tenure_days", "int",
                        raw -> ((Number) raw.get("account_created_days")).intValue())
                .addFeature("is_premium", "boolean", raw -> raw.get("premium"));

        FeatureGroup transactionFeatures = new FeatureGroup("transaction_features")
                .addFeature("avg_transaction_amount_7d", "double",
                        raw -> raw.get("avg_amt_7d"))
                .addFeature("transaction_count_7d", "int",
                        raw -> raw.get("txn_count_7d"))
                .addFeature("max_transaction_amount_7d", "double",
                        raw -> raw.get("max_amt_7d"));

        // Create stores
        OfflineStore offlineStore = new OfflineStore();
        OnlineStore onlineStore = new OnlineStore(3600); // 1 hour TTL

        // Simulate batch computation for offline store
        System.out.println("=== Offline: Batch Feature Computation ===");
        Instant day1 = Instant.parse("2025-01-01T00:00:00Z");
        Instant day2 = Instant.parse("2025-01-02T00:00:00Z");
        Instant day3 = Instant.parse("2025-01-03T00:00:00Z");

        offlineStore.computeAndStore("user_001", userFeatures,
                Map.of("age", 32, "account_created_days", 365, "premium", true), day1);
        offlineStore.computeAndStore("user_001", transactionFeatures,
                Map.of("avg_amt_7d", 45.50, "txn_count_7d", 12, "max_amt_7d", 150.00), day1);

        offlineStore.computeAndStore("user_001", transactionFeatures,
                Map.of("avg_amt_7d", 52.30, "txn_count_7d", 18, "max_amt_7d", 200.00), day2);

        offlineStore.computeAndStore("user_001", transactionFeatures,
                Map.of("avg_amt_7d", 48.75, "txn_count_7d", 15, "max_amt_7d", 175.00), day3);

        // Point-in-time retrieval for training data
        System.out.println("\n=== Point-in-Time Joins (Training Data) ===");
        Instant labelTime = Instant.parse("2025-01-02T12:00:00Z");
        Map<String, Object> userFeats = offlineStore.getPointInTime("user_001", "user_features", labelTime);
        Map<String, Object> txnFeats = offlineStore.getPointInTime("user_001", "transaction_features", labelTime);
        System.out.println("Training features at " + labelTime + ":");
        Map<String, Object> allFeatures = new HashMap<>();
        allFeatures.putAll(userFeats);
        allFeatures.putAll(txnFeats);
        allFeatures.forEach((k, v) -> System.out.printf("  %s = %s%n", k, v));

        // Online serving
        System.out.println("\n=== Online: Real-Time Feature Serving ===");
        Map<String, Object> onlineFeatures = Map.of(
                "age", 32,
                "account_tenure_days", 366,
                "is_premium", true,
                "avg_transaction_amount_7d", 48.75,
                "transaction_count_7d", 15,
                "max_transaction_amount_7d", 175.00
        );
        onlineStore.set("user_001", "all_features", onlineFeatures);

        Map<String, Object> served = onlineStore.get("user_001", "all_features");
        System.out.println("Online serving for user_001:");
        served.forEach((k, v) -> System.out.printf("  %s = %s%n", k, v));

        System.out.println("\n=== Summary ===");
        System.out.println("Offline features computed: " + offlineStore.storage.size() + " groups");
        System.out.println("Online features served: " + served.size() + " features");
    }
}

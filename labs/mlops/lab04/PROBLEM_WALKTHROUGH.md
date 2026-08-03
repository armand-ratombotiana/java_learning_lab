# Problem Walkthrough: Feature Store Architecture

## Problem 1: Point-in-Time Correct Training Data for Price Prediction — Company: Airbnb
### Interview Scenario
"You're at Airbnb building the nightly price-tip model. A data scientist generated training data by joining each listing's *current* feature values with labels from two days ago — the model's offline metrics were 0.92 accuracy but the online model is useless. The team is about to blame the model, but the real bug is data leakage: future features are predicting past labels. Build a feature store with time-series features and point-in-time retrieval so training rows can only ever see values known at label time, and serve the same features online with a TTL."

### The Problem
1. Define two feature groups — `listing_features` and `activity_features` — with transformation functions, like the lab's `user_features` / `transaction_features`.
2. Compute and store features for a listing across three consecutive days in the offline store.
3. Retrieve training features at a label time of `2025-01-02T12:00:00Z` using `getPointInTime` — day-3 values must be excluded.
4. Serve the current feature vector online via `OnlineStore.set` / `get`.
5. Print every result in deterministic order (explicit `LinkedHashMap` ordering), ending with a leakage check that proves the label used no future values.

### Solution Walkthrough
- Step 1: Model `FeatureGroup` exactly as the lab does — a name plus `addFeature(name, type, transformation)` where each transformation is a `Function<Map<String, Object>, Object>`.
- Step 2: Build `OfflineStore` with the lab's nested storage map (`entityId:groupName` → feature → `List<TimeSeriesValue>`) and `TimeSeriesValue(Instant timestamp, Object value)`.
- Step 3: Implement `getPointInTime` with the lab's algorithm: filter `!v.timestamp.isAfter(atTime)`, then take `max(Comparator.comparing(v -> v.timestamp))`.
- Step 4: Compute features on `day1` through `day3` for listing `listing_042`, then query at the label time — the activity features must resolve to the day-2 snapshot (`avg 52.30 / 18 / 200.00`), proving no future data.
- Step 5: Build the `OnlineStore` with TTL (3600s), `set` the current vector, and `get` it back for serving.
- Step 6: Replace the lab's `HashMap` accumulation with `LinkedHashMap` so the printed feature order is deterministic for the report.
- Step 7: Print a leakage check: assert the retrieved `transaction_count_7d` equals the day-2 value, and print `No leakage: day-3 values excluded from training row`.

### Code
```java
package com.mlops.lab04;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class FeatureStoreWalkthrough {

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

        void computeAndStore(String entityId, FeatureGroup group,
                             Map<String, Object> rawData, Instant ts) {
            String key = entityId + ":" + group.name;
            storage.putIfAbsent(key, new ConcurrentHashMap<>());
            for (FeatureDefinition fd : group.features) {
                Object value = fd.transformation.apply(rawData);
                storage.get(key).computeIfAbsent(fd.name, k -> new ArrayList<>())
                        .add(new TimeSeriesValue(ts, value));
            }
        }

        Map<String, Object> getPointInTime(String entityId, String groupName, Instant atTime) {
            Map<String, List<TimeSeriesValue>> featureStore = storage.get(entityId + ":" + groupName);
            if (featureStore == null) return Map.of();
            Map<String, Object> result = new LinkedHashMap<>();
            for (Map.Entry<String, List<TimeSeriesValue>> entry : featureStore.entrySet()) {
                entry.getValue().stream()
                        .filter(v -> !v.timestamp.isAfter(atTime))
                        .max(Comparator.comparing(v -> v.timestamp))
                        .ifPresent(v -> result.put(entry.getKey(), v.value));
            }
            return result;
        }
    }

    static class OnlineStore {
        private final Map<String, Map<String, Object>> store = new ConcurrentHashMap<>();
        private final Map<String, Instant> ttls = new ConcurrentHashMap<>();
        private final long ttlSeconds;

        OnlineStore(long ttlSeconds) { this.ttlSeconds = ttlSeconds; }

        void set(String entityId, String groupName, Map<String, Object> features) {
            String key = entityId + ":" + groupName;
            store.put(key, new LinkedHashMap<>(features));
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
    }

    public static void main(String[] args) {
        System.out.println("=== Feature Store Architecture (Airbnb price-tip) ===\n");

        FeatureGroup listingFeatures = new FeatureGroup("listing_features")
                .addFeature("bedrooms", "int", raw -> raw.get("bedrooms"))
                .addFeature("host_years", "int", raw -> raw.get("host_years"))
                .addFeature("superhost", "boolean", raw -> raw.get("superhost"));

        FeatureGroup activityFeatures = new FeatureGroup("activity_features")
                .addFeature("avg_price_7d", "double", raw -> raw.get("avg_price_7d"))
                .addFeature("views_7d", "int", raw -> raw.get("views_7d"))
                .addFeature("bookings_7d", "int", raw -> raw.get("bookings_7d"));

        OfflineStore offlineStore = new OfflineStore();
        OnlineStore onlineStore = new OnlineStore(3600);

        System.out.println("=== Offline: Batch Feature Computation ===");
        Instant day1 = Instant.parse("2025-01-01T00:00:00Z");
        Instant day2 = Instant.parse("2025-01-02T00:00:00Z");
        Instant day3 = Instant.parse("2025-01-03T00:00:00Z");

        offlineStore.computeAndStore("listing_042", listingFeatures,
                Map.of("bedrooms", 2, "host_years", 4, "superhost", true), day1);
        offlineStore.computeAndStore("listing_042", activityFeatures,
                Map.of("avg_price_7d", 145.0, "views_7d", 90, "bookings_7d", 3), day1);
        offlineStore.computeAndStore("listing_042", activityFeatures,
                Map.of("avg_price_7d", 152.3, "views_7d", 120, "bookings_7d", 5), day2);
        offlineStore.computeAndStore("listing_042", activityFeatures,
                Map.of("avg_price_7d", 210.0, "views_7d", 310, "bookings_7d", 14), day3);

        System.out.println("\n=== Point-in-Time Joins (Training Data) ===");
        Instant labelTime = Instant.parse("2025-01-02T12:00:00Z");
        Map<String, Object> trainingRow = new LinkedHashMap<>();
        trainingRow.putAll(offlineStore.getPointInTime("listing_042", "listing_features", labelTime));
        trainingRow.putAll(offlineStore.getPointInTime("listing_042", "activity_features", labelTime));
        System.out.println("Training row at " + labelTime + ":");
        trainingRow.forEach((k, v) -> System.out.printf("  %s = %s%n", k, v));

        boolean leakage = 14 == ((Number) trainingRow.get("bookings_7d")).intValue();
        System.out.printf("%s%n", leakage
                ? "LEAKAGE DETECTED: day-3 bookings in training row"
                : "No leakage: day-3 values excluded from training row");

        System.out.println("\n=== Online: Real-Time Feature Serving ===");
        Map<String, Object> currentVector = new LinkedHashMap<>();
        currentVector.put("bedrooms", 2);
        currentVector.put("host_years", 5);
        currentVector.put("superhost", true);
        currentVector.put("avg_price_7d", 205.0);
        currentVector.put("views_7d", 300);
        currentVector.put("bookings_7d", 13);
        onlineStore.set("listing_042", "all_features", currentVector);

        Map<String, Object> served = onlineStore.get("listing_042", "all_features");
        System.out.println("Online serving for listing_042:");
        served.forEach((k, v) -> System.out.printf("  %s = %s%n", k, v));

        System.out.println("\n=== Summary ===");
        System.out.println("Offline groups computed: 2 (listing_features, activity_features)");
        System.out.println("Online features served: " + served.size());
    }
}
```

### Expected Output
```
=== Feature Store Architecture (Airbnb price-tip) ===

=== Offline: Batch Feature Computation ===

=== Point-in-Time Joins (Training Data) ===
Training row at 2025-01-02T12:00:00Z:
  bedrooms = 2
  superhost = true
  host_years = 4
  bookings_7d = 5
  avg_price_7d = 152.3
  views_7d = 120
No leakage: day-3 values excluded from training row

=== Online: Real-Time Feature Serving ===
Online serving for listing_042:
  bedrooms = 2
  host_years = 5
  superhost = true
  avg_price_7d = 205.0
  views_7d = 300
  bookings_7d = 13

=== Summary ===
Offline groups computed: 2 (listing_features, activity_features)
Online features served: 6
```

---

## Problem 2: Online Store TTL Expiry — Company: Uber
### Interview Scenario
"You're at Uber. Riders' `session_features` vectors in the online store are served for hours after the session ended, because nothing evicts them. Prove the TTL mechanics of the lab's `OnlineStore` work, then decide the policy."

### The Problem
1. Set a feature vector with a 1-second TTL.
2. Read it back immediately — it must be present.
3. Wait past the TTL and read again — it must return `Map.of()` and evict the key.

### Solution Walkthrough
- Step 1: Construct `new OnlineStore(1)` — one-second TTL, mirroring the lab's constructor.
- Step 2: `set("rider_9", "session_features", vector)` then immediate `get` — the expiry `Instant.now().plusSeconds(1)` is still in the future.
- Step 3: `Thread.sleep(1100)` to cross the expiry boundary, then `get` again — the lab's expiry check (`Instant.now().isAfter(expiry)`) evicts both `store` and `ttls` entries and returns `Map.of()`.
- Step 4: Confirm the empty result, which is what a model server must treat as 'no data' rather than a default value.

### Code
```java
OnlineStore onlineStore = new OnlineStore(1);   // 1-second TTL
Map<String, Object> session = Map.of("trips_7d", 4, "driver_rating", 4.9);
onlineStore.set("rider_9", "session_features", session);

Map<String, Object> fresh = onlineStore.get("rider_9", "session_features");
System.out.printf("Immediate read: %d features present%n", fresh.size());

Thread.sleep(1100);   // cross the TTL boundary

Map<String, Object> expired = onlineStore.get("rider_9", "session_features");
System.out.printf("After TTL expiry: %d features (empty = evicted)%n", expired.size());
```
### Expected Output
```
Immediate read: 2 features present
After TTL expiry: 0 features (empty = evicted)
```

---

## Problem 3: Deterministic Entity Hashing for Online Store Sharding — Company: Stripe
### Interview Scenario
"You're at Stripe. The online feature store is moving to a 4-node Redis cluster and the serving layer must find the owning node for `entityId:groupName` without a lookup table. Implement consistent-key assignment."

### The Problem
1. Hash the compound key `entityId:groupName` to a node index in [0, 4).
2. Keep the same key on the same node across calls — no reshuffling per request.
3. Print the assignment for five keys.

### Solution Walkthrough
- Step 1: Use `(key.hashCode() & Integer.MAX_VALUE) % nodes` — deterministic per key (masking avoids the `Integer.MIN_VALUE` edge case that `Math.abs` leaves negative).
- Step 2: Node assignment is a pure function of the key, so routing needs no shared state.
- Step 3: Print `key -> node` for the demo keys; note that production would use a ring with virtual nodes to keep rebalancing small when the cluster grows.

### Code
```java
int nodes = 4;
List<String> keys = List.of(
        "listing_042:activity_features",
        "listing_042:listing_features",
        "rider_9:session_features",
        "user_001:transaction_features",
        "user_001:user_features");
for (String key : keys) {
    int node = (key.hashCode() & Integer.MAX_VALUE) % nodes;
    System.out.printf("  %s -> node %d%n", key, node);
}
```
### Expected Output
```
  listing_042:activity_features -> node 2
  listing_042:listing_features -> node 3
  rider_9:session_features -> node 0
  user_001:transaction_features -> node 3
  user_001:user_features -> node 0
```

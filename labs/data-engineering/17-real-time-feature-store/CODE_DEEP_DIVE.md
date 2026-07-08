# Code Deep Dive: Real-Time Feature Store

See Java source files in src/main/java/com/dataeng/seventeen/ for:
- FeatureClient.java: Feature store serving client with caching
- PointInTimeJoin.java: Temporal join implementation for training data

Key patterns:
```java
// Feature serving
FeatureClient client = new FeatureClient("http://localhost:6566");
Map<String, FeatureValue> features = client.getOnlineFeatures(
    "user_features",
    Map.of("user_id", "user_123"),
    List.of("total_orders", "avg_order_value", "days_since_last")
);

// Point-in-time join (conceptual)
Dataset<Row> trainingData = pointInTimeJoin(
    labels, features, "user_id", "label_ts", "feature_ts");
```

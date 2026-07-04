# Debugging Feature Stores

## Training/Serving Skew
```java
spark.read().parquet("s3://features/train/").describe().show();
spark.read().parquet("s3://features/serving/").describe().show();
```

## Feature Freshness
```java
spark.sql("SELECT MAX(feature_timestamp) FROM offline.user_features").show();
```

## Online Store Connectivity
```java
Jedis jedis = new Jedis("feature-store-redis", 6379);
String pong = jedis.ping();
if (!"PONG".equals(pong)) { log.error("Redis not reachable"); }
```

## Missing Features
```java
FeatureDefinition def = registry.getFeature("avg_order_value");
if (def == null) { log.error("Feature not registered"); }
```

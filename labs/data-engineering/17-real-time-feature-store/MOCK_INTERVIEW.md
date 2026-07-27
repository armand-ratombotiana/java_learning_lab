# Mock Interview: Real-time Feature Store (17-real-time-feature-store)

## Scenario: Design a feature store for real-time ML
Your ML team needs a feature store that serves both batch (training) and real-time (inference) features. The ML model is a recommendation system serving 1M+ users.

### Time: 45 minutes | Difficulty: Hard

---

## Part 1: Architecture (15 min)

**Feature store architecture (batch + online):**

```
Feature Pipeline Layer:
├── Batch Features (Spark, daily):
│   ├── Customer aggregates (total spend, order count, avg order value)
│   ├── Product features (popularity, category, price percentile)
│   └── User-product features (purchase history, browse history)
│
├── Streaming Features (Flink, real-time):
│   ├── Real-time aggregates (sessions in last 5 min, current cart)
│   ├── Behavioral signals (click rate, dwell time, scroll depth)
│   └── Context features (time of day, device, location)
│
Storage Layer:
├── Offline Store (S3 / Delta Lake):
│   ├── Parquet/Delta format, partitioned by event_date
│   ├── Full feature history for training
│   └── Backfill capability for new features
│
├── Online Store (Redis / DynamoDB):
│   ├── Key-value store, < 5ms latency
│   ├── Latest feature value per entity key
│   └── TTL-based eviction (24-72 hours)
│
Serving Layer:
├── Training: Point-in-time correct join from offline store
├── Batch inference: Batch read from offline store
├── Real-time inference: Key-value lookup from online store
└── Feature serving API: gRPC / REST (feature-server)
```

**Feature computation with Feast:**
```python
from datetime import datetime, timedelta
from feast import FeatureView, Entity, Field, FileSource, ValueType
from feast.types import Float32, Int32, String

# Define entity
customer = Entity(
    name="customer_id",
    join_keys=["customer_id"],
    value_type=ValueType.INT64,
    description="Unique customer identifier"
)

# Batch feature view
customer_batch_features = FeatureView(
    name="customer_batch_features",
    entities=[customer],
    ttl=timedelta(days=1),
    schema=[
        Field(name="total_spend_30d", dtype=Float32),
        Field(name="order_count_30d", dtype=Int32),
        Field(name="avg_order_value_30d", dtype=Float32),
        Field(name="customer_tenure_days", dtype=Int32),
        Field(name="preferred_category", dtype=String),
    ],
    source=FileSource(
        path="s3://feature-store/batch/customer_features/",
        timestamp_field="event_timestamp",
        created_timestamp_column="created_at",
    ),
)

# Streaming feature view
customer_streaming_features = FeatureView(
    name="customer_session_features",
    entities=[customer],
    ttl=timedelta(hours=2),
    schema=[
        Field(name="session_clicks_5min", dtype=Int32),
        Field(name="session_page_views", dtype=Int32),
        Field(name="current_cart_value", dtype=Float32),
    ],
    source=kafka_source,  # Kafka topic with latest features
    online=True,  # Serve from online store
)
```

---

## Part 2: Online Serving & Consistency (10 min)

**Online feature serving:**

```python
# Feature server (Redis backend)
import redis
import json
from fastapi import FastAPI, HTTPException

app = FastAPI()
redis_client = redis.Redis(host="feature-store-redis", port=6379, decode_responses=True)

@app.get("/features/{customer_id}")
async def get_features(customer_id: int):
    # Try online store first (fast path)
    key = f"customer:{customer_id}:features"
    features = redis_client.get(key)

    if features:
        return json.loads(features)

    # Fallback to offline store (slow path)
    features = compute_features_from_offline(customer_id)
    redis_client.setex(key, 3600, json.dumps(features))  # Cache for 1 hour
    return features
```

**Consistency between batch and online:**

| Feature Type | Batch Compute | Online Compute | Consistency |
|-------------|--------------|----------------|-------------|
| Customer 30d spend | Daily Spark job (midnight) | Same value from Redis | Exact match after daily refresh |
| Session clicks 5min | N/A | Flink streaming | Latest 5-min window |
| Product popularity | Hourly Spark | Hourly cached value | Matches last batch |
| Customer tenure | Daily batch | Daily batch value | Exact match |

**Consistency strategy:**
1. Batch features computed once daily → written to both offline and online
2. Streaming features computed in real-time → written to online only (offline via log)
3. Training always uses offline store (point-in-time correct)
4. Inference uses online store (latest values, may be slightly ahead of training)
5. Reconciliation job runs hourly: compare online vs offline feature values

---

## Part 3: Feature Engineering & Versioning (10 min)

**Feature definition framework:**

```python
# feature_definitions.py - Single source of truth for features
from dataclasses import dataclass
from datetime import datetime
from typing import Callable

@dataclass
class FeatureDefinition:
    name: str
    description: str
    owner: str
    version: str
    compute_fn: Callable
    freshness_sla: str
    dependencies: list[str]

# Registered features
FEATURES = {
    "total_spend_30d": FeatureDefinition(
        name="total_spend_30d",
        description="Total spend in last 30 days",
        owner="data-eng",
        version="v2.1.0",
        compute_fn=lambda df: df.filter(col("order_date") >= dateadd("day", -30, today()))
                               .groupBy("customer_id").agg(sum("amount")),
        freshness_sla="24h",
        dependencies=["orders"],
    ),
    "session_clicks_5min": FeatureDefinition(
        name="session_clicks_5min",
        description="Number of clicks in the last 5 minutes",
        owner="ml-infra",
        version="v1.0.0",
        compute_fn=lambda df: df.groupBy("customer_id")
                               .agg(count(when(col("event_type") == "click", 1))),
        freshness_sla="1min",
        dependencies=["clickstream"],
    ),
}
```

**Versioning strategy:**
```yaml
feature_versions:
  customer_lifetime_value:
    - version: v1.0.0
      description: "Sum of all order amounts"
      status: DEPRECATED
      deprecated_at: 2024-06-01
    - version: v2.0.0
      description: "Weighted sum of orders (recent orders weighted more)"
      status: ACTIVE
      created_at: 2024-06-01
    - version: v2.1.0
      description: "v2.0.0 + churn probability adjustment"
      status: BETA
      created_at: 2024-07-15
      experiment: "recommendation-v3"

  # Backward compatible changes:
  # - Adding features: new version number
  # - Updating feature logic: new version number
  # - Removing features: deprecate, not delete
  # - Training always pins feature versions
```

---

## Part 4: Point-in-time Joins & Backfill (10 min)

**Point-in-time correct joins for training:**
```python
from feast import FeatureStore

store = FeatureStore(repo_path="./feature_repo")

# Training data: for each order at time T, get feature values as of time T
# This avoids data leakage (using future features to predict past)
training_df = store.get_historical_features(
    entity_df=entity_df,  # customer_id, event_timestamp
    features=[
        "customer_batch_features:total_spend_30d",
        "customer_batch_features:order_count_30d",
        "customer_session_features:session_clicks_5min",
    ],
    full_feature_names=True,
).to_df()

# Feast automatically performs point-in-time join:
# For each (customer_id, timestamp) pair:
# 1. Look up batch features BEFORE the timestamp
# 2. Look up streaming features BEFORE the timestamp
# 3. Join all features into one row
```

**Backfill for new features:**
```python
def backfill_feature(feature_name: str, start_date: str, end_date: str):
    """
    Backfill a new feature for historical training data.
    Strategy: compute feature for each day in parallel.
    """

    # Step 1: Register feature definition
    feature_def = FEATURES[feature_name]

    # Step 2: Compute feature for each day
    dates = generate_date_range(start_date, end_date)
    for date_batch in batch(dates, 7):  # Process 1 week at a time
        df = spark.sql(f"""
            SELECT customer_id,
                   {feature_def.compute_sql()}
                   '{date_batch[0]}' AS event_timestamp,
                   CURRENT_TIMESTAMP() AS created_at
            FROM silver.orders
            WHERE order_date BETWEEN '{date_batch[0]}' AND '{date_batch[-1]}'
            GROUP BY customer_id
        """)

        # Write to offline store
        df.write.format("delta").mode("append") \
            .save(f"s3://feature-store/offline/{feature_name}")

        # Write latest value to online store
        write_latest_to_online(df, feature_name)

    # Step 3: Validate backfilled features
    validate_feature_distribution(feature_name, start_date, end_date)

    # Step 4: Update model training pipeline to use new feature
    update_training_pipeline(feature_name)
```

---

## Follow-up Questions

**Streaming feature computation with Flink:**
```java
// Flink job: compute session-level features
DataStream<ClickEvent> clicks = env.addSource(kafkaSource);

DataStream<SessionFeatures> sessionFeatures = clicks
    .keyBy(event -> event.customerId)
    .window(SlidingEventTimeWindows.of(Duration.ofMinutes(5), Duration.ofSeconds(30)))
    .aggregate(new SessionFeatureAggregator())
    .map(feature -> {
        // Write to online store (Redis)
        redisClient.hset("customer:" + feature.customerId + ":features",
            "session_clicks_5min", String.valueOf(feature.clickCount));
        redisClient.expire("customer:" + feature.customerId + ":features", 3600);
        return feature;
    });

sessionFeatures.addSink(kafkaSink);  // Also write to Kafka for offline log
```

**Batch backfill optimization:**
- For new features: compute from source data (may take hours for 1 year)
- For feature logic update: re-compute only affected dates
- Parallel by week/month to maximize Spark resource utilization
- Validate against existing feature values for overlapping dates

**Feast vs Tecton vs Databricks Feature Store:**
| Feature | Feast | Tecton | Databricks FS |
|---------|-------|--------|---------------|
| Open source | Yes | No | No |
| Online serving | Redis, DynamoDB, Firestore | Managed (low latency) | DynamoDB, CosmosDB |
| Point-in-time joins | Yes | Yes | Yes |
| Streaming | Kafka source | Native streaming | Structured Streaming |
| Cost | Free (self-managed) | Enterprise pricing | Included in Databricks |
| Best for | Startups, Python shops | Enterprise ML teams | Databricks users |


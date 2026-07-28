# Lab 04: Interview Questions

## FAANG-Level Questions

### Q1: Design a real-time feature store that serves features with P99 < 10ms.
**Answer**: Use Redis with cluster mode for online serving, with in-memory replication for high availability. Pre-compute features using Kafka Streams/Flink and write to both offline (S3/Parquet) and online (Redis) stores. Use consistent hashing for sharding. Implement local caching (Caffeine) on prediction servers for hot keys. Write-behind cache for batch updates.

### Q2: How do you handle point-in-time correctness in feature stores?
**Answer**: Store each feature value with an associated timestamp. During training data generation, for each label timestamp, retrieve the most recent feature value with timestamp <= label timestamp. Use ASOF JOIN semantics (Pandas merge_asof, Spark's range join). Ensure feature computation pipelines are idempotent and backfillable.

### Q3: Compare Feast vs Tecton vs SageMaker Feature Store.
**Answer**: Feast is open-source with great community; requires self-managed infra. Tecton is managed with automatic point-in-time joins and monitoring. SageMaker Feature Store integrates tightly with AWS ecosystem. Feast wins for flexibility and cost; Tecton for enterprise features; SageMaker for AWS native.

### Q4: How would you serve features for both training and inference consistently?
**Answer**: Use a single feature definition API that generates both training datasets (offline) and online feature vectors. The same transformation logic runs in Spark for batch and in Java microservices for real-time. Feast's `FeatureView` and `FeatureService` abstractions enforce this consistency.

## LeetCode / NeetCode References
- **Design HashMap (LeetCode 706)** — Understanding key-value storage
- **Time-Based Key-Value Store (LeetCode 981)** — Timestamp-based retrieval (point-in-time)
- **LRU Cache (LeetCode 146)** — Cache eviction for hot features
- **Design In-Memory Cache** — Local feature caching

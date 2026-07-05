# Performance: Recommendation Engine

## Throughput Targets
- Recommendation latency: < 100ms P99
- Model training (daily full): < 2 hours
- Incremental update: < 5 minutes
- ANN index rebuild: < 1 minute

## Bottlenecks
- **ANN search**: Brute-force kNN is O(n*d*k). HNSW reduces to O(log n). Index must fit in RAM.
- **Redis serialization**: Large factor matrices (100k users x 50 factors) cause slow deserialization. Use binary protocol.
- **Model training**: ALS on 100M interactions takes hours. Use Spark for distributed training.
- **Feature computation**: TF-IDF on millions of documents. Pre-compute and cache.

## Optimization Strategies
- Shard factor matrices across Redis cluster
- Pre-compute candidate sets for popular queries
- Cache personalized recommendations with TTL (1 hour)
- Use float32 instead of float64 for factor matrices
- Batch process cold-start users with pre-computed default recommendations

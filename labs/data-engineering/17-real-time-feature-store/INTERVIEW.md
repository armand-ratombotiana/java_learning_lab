# Interview Questions: Real-Time Feature Store

### Architecture
**Q**: Explain feature store architecture.
**A**: Registry: feature definitions and metadata. Offline store: historical features for training. Online store: low-latency features for inference. Serving API: retrieve features by entity key. All three are kept consistent.

### Point-in-Time
**Q**: Why is point-in-time join important?
**A**: Prevents data leakage by ensuring features use only information available at prediction time. Without it, features from the future would leak into training, creating overly optimistic model performance.

### Serving
**Q**: How do you serve features for real-time inference?
**A**: Materialize features to online store (Redis, Cassandra). Inference service calls feature store REST/gRPC API with entity keys. Feature values returned in <10ms for real-time predictions.

### Consistency
**Q**: How to ensure online/offline consistency?
**A**: Validation jobs compare online and offline feature values for sample entities. Monitor materialization latency. Alert on drift between online and offline distributions.

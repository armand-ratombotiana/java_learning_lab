# Internals: Fraud Detection

## Pipeline Components
- **TransactionIngestor**: Consumes raw transactions from Kafka, performs initial validation
- **FeatureExtractor**: Computes velocity windows, z-scores, geo-distance, device trust
- **RuleEngine**: Chain-of-responsibility pattern; rules in order: AmountRule, VelocityRule, BlacklistRule, GeoAnomalyRule, DeviceTrustRule
- **MLScorer**: Loads Isolation Forest model; scores transactions via ONNX Runtime
- **DecisionMaker**: Combines rule + ML scores with configurable weights
- **FeedbackCollector**: Stores labeled outcomes for model retraining pipeline

## Key Metrics
- Precision: TP / (TP + FP) — minimizing false positives
- Recall: TP / (TP + FN) — minimizing false negatives (missed fraud)
- F1 Score: 2 * (Precision * Recall) / (Precision + Recall)
- P99 latency: < 300ms for rule engine, < 2s including ML inference

## Storage
- Redis: Feature store, user profiles, device fingerprints
- PostgreSQL: Labeled transactions, model versions, audit log
- S3/MinIO: Model artifacts, training datasets

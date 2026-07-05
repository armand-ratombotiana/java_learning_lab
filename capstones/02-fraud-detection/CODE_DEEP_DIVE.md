# Code Deep Dive: Fraud Detection

## Rule Engine

The `RuleEngine` implements the chain-of-responsibility pattern. Each rule extends `AbstractRule` with an `evaluate(TransactionContext)` method. The chain is ordered: `AmountRule -> VelocityRule -> BlacklistRule -> GeoAnomalyRule -> DeviceTrustRule`. Each rule returns a `RuleResult` with score (0-100) and reason code. If any rule returns score > 90, the chain short-circuits with REJECT.

## ML Scoring

`MLScorer` loads an ONNX-format Isolation Forest model using ONNX Runtime Java API. The model is loaded once and cached. Scoring requests are submitted to a dedicated thread pool (core=4, max=8). If the pool is saturated or inference exceeds 2s timeout, a fallback score of 50 (REVIEW) is used.

## Feature Store

`FeatureStore` wraps Redis with protocol buffers for efficient serialization. Features are stored with TTL (1 hour for velocity, 30 days for user profiles). Cache-aside pattern: check Redis first, compute if missing, populate cache asynchronously.

## Feedback Pipeline

Once a week, labeled transactions (confirmed fraud / false positive) are extracted from PostgreSQL and used to retrain the Isolation Forest model. The new model is evaluated against a holdout set; if F1 improves by > 1%, it's deployed to production with a version tag.

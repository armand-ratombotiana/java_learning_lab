# Refactoring: ML Platform

## Current Pain Points
- Training pipeline tightly coupled to model type (XGBoost-specific)
- No feature backfill support for historical feature computation
- Serving service loads entire model for each request (no batching)
- A/B testing split percentage hardcoded in config
- No canary deployment (all traffic at once)

## Suggested Improvements
- Abstract model training behind interface: `Estimator.train(Dataset) -> Model`
- Add feature backfill pipeline: compute features for arbitrary date ranges
- Implement request batching in serving service for GPU efficiency
- Dynamic A/B split via config server or feature flags
- Add canary deployment: route 1% -> 5% -> 50% -> 100% with automated rollback
- Add explainability (SHAP values) in prediction response

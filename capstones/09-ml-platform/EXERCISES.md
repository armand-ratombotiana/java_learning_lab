# Exercises: ML Platform

## Beginner
1. Implement a feature pipeline that computes min-max scaling for a numeric column
2. Register a model artifact and metadata in the model registry
3. Create a simple REST endpoint that loads a model and returns predictions

## Intermediate
4. Implement online feature store with Redis (set/get features by key)
5. Add A/B testing: route 10% of traffic to new model version, compare metrics
6. Implement batch prediction pipeline with scheduled execution
7. Add model evaluation against holdout set before promotion

## Advanced
8. Implement automated canary deployment (gradual traffic shift with rollback)
9. Add concept drift detection with PSI computation and auto-rollback
10. Implement distributed training with parameter server architecture
11. Build a feature backfill pipeline for historical feature computation
12. Add ML model explainability (SHAP values) to prediction API

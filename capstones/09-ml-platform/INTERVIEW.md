# Interview: ML Platform

## Common Questions

### Q: Design an ML platform for a company with 100 models in production.
Feature store for centralized features. Model registry for versioning. Standardized training pipeline. Automated evaluation and promotion. A/B testing infrastructure. Online serving with canary deployments. Monitoring with drift detection and automated rollback.

### Q: How do you handle training-serving skew?
Use the same feature engineering code in both training and serving pipelines. Feature store ensures consistency. Validate feature distributions in serving against training baselines.

### Q: How do you decide when to retrain a model?
Monitor prediction drift and accuracy metrics. Retrain when: PSI > 0.2, accuracy drops > 5%, or on a fixed schedule (weekly/daily). Automated retraining triggered by these conditions.

### Q: How would you deploy a model to 1000 nodes?
Use a distributed serving architecture with load balancer. Model artifact stored in shared object store (S3). Each node loads model on startup from local cache or S3. Use Kubernetes for auto-scaling.

### Q: How do you ensure ML model fairness and compliance?
Monitor prediction bias across demographic groups. Maintain data lineage for audit. Use explainability tools (SHAP, LIME) for each prediction. Regular model fairness audits.

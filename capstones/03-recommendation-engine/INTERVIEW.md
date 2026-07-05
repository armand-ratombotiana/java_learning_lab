# Interview: Recommendation Engine

## Common Questions

### Q: Design a recommendation system for a video streaming platform.
Use implicit feedback (watch time, completion rate), two-tower DNN model, ANN for candidate generation, lightweight ranking model, business rule filters. Cold start: use popularity + content metadata.

### Q: How do you evaluate recommendations offline?
Holdout set of recent interactions. Metrics: Precision@k, Recall@k, NDCG, Mean Average Precision, Hit Rate. Always evaluate on time-split (not random) data to avoid data leakage.

### Q: How do you handle popularity bias in recommendations?
Down-weight popular items during training (IPW — Inverse Propensity Weighting), or use a separate popularity debiasing stage. Add explore/exploit with epsilon-greedy.

### Q: How often do you retrain the model?
Daily full retrain with weekly cadence for hyperparameter tuning. Online incremental updates for fresh interactions. Monitor metric drift to trigger ad-hoc retraining.

### Q: How would you scale recommendations to 100M users?
Shard user factors, use ANN with HNSW (not brute force), pre-compute popular recommendations, cache aggressively, use distributed training (Spark on EMR).

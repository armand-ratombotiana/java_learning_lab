# How It Works: Recommendation Engine

1. User interaction data (ratings, clicks, purchases) is ingested from Kafka
2. Batch job runs ALS matrix factorization nightly, producing user and item factor matrices
3. Offline evaluation computes precision@k, recall@k, NDCG on holdout set
4. Factor matrices are loaded into Redis for online serving
5. At recommendation time, user vector is retrieved (or computed from recent interactions)
6. ANN (Approximate Nearest Neighbor) search finds top-K similar item vectors
7. Results are filtered (remove already-purchased, apply business rules)
8. Hybrid scores blend collaborative + content-based + popularity signals
9. Recommendations are ranked by final score and returned via REST API
10. User feedback loop: clicks/impressions logged for model retraining

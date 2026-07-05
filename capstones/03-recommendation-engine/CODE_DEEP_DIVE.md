# Code Deep Dive: Recommendation Engine

## ALS Model Training

`ALSModelTrainer` uses the Apache Spark ALS implementation (or a custom Java implementation for learning purposes). Training proceeds in iterations:
1. Initialize U with small random values
2. Fix U, solve V_i = (U^T * C_i * U + λ * I)^(-1) * U^T * C_i * p_i for each item
3. Fix V, solve U_u similarly for each user
4. Repeat for 20 iterations

Incremental updates handle new interactions since last full train by running a few ALS iterations on only the affected users/items.

## ANN Search

`ANNSearcher` builds an HNSW index from item factor vectors. Parameters: M=16 (connections per layer), efConstruction=200, efSearch=50. The index is rebuilt after each full model retrain. Vector dimensionality matches rank (k=50).

## Hybrid Ranking

`HybridRanker` combines scores: final_score = w_cf * collaborative_score + w_cb * content_score + w_pop * popularity_score. Default weights are tuned via grid search on validation data.

## REST API

`RecService` exposes `GET /api/v1/recommendations/{userId}?limit=20`. It retrieves the user vector from Redis, runs ANN search for top-100 candidates, applies business rules (filter purchased items, enforce diversity), ranks via HybridRanker, and returns top-K.

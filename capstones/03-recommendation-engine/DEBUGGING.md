# Debugging: Recommendation Engine

## Common Issues

### All recommendations are the same for all users
- User factor vectors are all identical — check if model training converged
- Redis key lookup may be returning a default vector
- ANN index may not have been rebuilt after model update

### Cold start users get bad recommendations
- Popularity fallback list may be stale
- Content-based features may be missing (check TF-IDF pipeline)
- Consider asking for user preferences on signup

### Low precision@k on holdout
- ALS rank too low (< 20) or too high (> 200)
- Regularization lambda too high (underfitting) or too low (overfitting)
- Training data may have sampling bias

### ANN search returning irrelevant results
- HNSW efSearch parameter too low
- Cosine distance vs Euclidean — ensure metric matches training
- Index needs rebuilding

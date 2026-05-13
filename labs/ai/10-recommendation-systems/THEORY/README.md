# Recommendation Systems - Theory

## 1. Collaborative Filtering

### User-Based
- Find similar users
- Recommend what similar users liked

### Item-Based
- Find similar items
- Recommend similar to what user liked

### Matrix Factorization (SVD)
```
R ≈ U × Vᵀ
```
- U: user embeddings
- V: item embeddings

## 2. Content-Based

### Features
- Item features (text, metadata)
- User features (demographics)

### Similarity
- Cosine similarity
- TF-IDF, embeddings

## 3. Hybrid Systems

### Combinations
- Weighted hybrid
- Switching hybrid
- Feature combination

## 4. Evaluation Metrics

### Accuracy
- RMSE, MAE
- Precision@K, Recall@K

### Ranking
- NDCG
- MAP

## 5. Deep Learning for RecSys

### Neural Collaborative Filtering
- Multi-layer perceptrons
- DeepFM, Wide & Deep

### Sequence Modeling
- Session-based recommendations
- RNN/Transformers
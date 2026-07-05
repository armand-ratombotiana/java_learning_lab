# Exercises: Recommendation Engine

## Beginner
1. Implement a popularity-based fallback recommender (top items by total interactions)
2. Add a new content-based feature: recommend items with same category tags
3. Create a simple cosine similarity function between two item vectors

## Intermediate
4. Implement ALS matrix factorization (not using Spark — pure Java)
5. Add diversity rules: max 2 items per category in top-5
6. Implement an online evaluation metric: A/B test new model vs old model
7. Build an incremental update that handles new interactions without full retrain

## Advanced
8. Implement Neural Collaborative Filtering (NCF) using DL4J or TensorFlow Java
9. Build a session-based recommender using GRU/Transformer for sequential recommendations
10. Implement real-time feature updates using Kafka Streams
11. Build a visualization dashboard showing embedding space (PCA/t-SNE on factors)

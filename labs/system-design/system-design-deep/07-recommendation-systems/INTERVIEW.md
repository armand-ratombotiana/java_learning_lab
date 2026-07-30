# Interview Deep-Dive: Recommendation Systems

## Common Questions

### Q1: Why is item-based CF often preferred over user-based CF in production?
**Answer**: Item-item similarity is more stable (items don't change much) and can be precomputed offline. User-user similarity changes frequently and doesn't scale well. Amazon uses item-based CF with "Customers who bought this also bought" — item similarity matrices are computed offline and loaded into memory for real-time inference.

### Q2: How do you handle the cold start problem?
**Answer**: 
1. **New user**: Use content-based recommendations based on onboarding preferences or popular items
2. **New item**: Use item features (content-based) until interaction data accumulates
3. **Cross-domain**: Transfer knowledge from related domains
4. **Explore-first**: Use bandit algorithms to explore and learn quickly

### Q3: How does ALS matrix factorization work at scale?
**Answer**: ALS factorizes the user-item matrix by alternating between fixing user factors and solving for item factors (and vice versa). Each step is a least-squares problem that can be parallelized. At scale (e.g., Netflix), ALS is distributed across Spark workers — each worker handles a subset of users/items.

## System Design Whiteboard

**Design a movie recommendation system for 100M users and 50K movies.**
- **Offline layer**: Spark ALS on 10B ratings — 20 latent factors, 10 iterations
- **Online layer**: Real-time Java service with precomputed item vectors
- **Hybrid**: 60% CF (ALS scores) + 30% content (genre, director TF-IDF) + 10% popularity
- **Cold start**: Popularity + demographic clustering for new users
- **AB testing**: 5% traffic to experimental models
- **Storage**: Cassandra for user-item matrix, Redis for online vectors
- **API**: 100ms p99 latency, top-20 recommendations

## Key Trade-offs to Discuss
- Accuracy vs diversity (filter bubble problem)
- Computation cost vs freshness (offline vs online updates)
- Exploration vs exploitation (bandit trade-off)

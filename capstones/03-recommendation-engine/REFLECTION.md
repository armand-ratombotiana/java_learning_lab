# Reflection: Recommendation Engine

## What I Learned
- How matrix factorization learns latent features from implicit signals
- The importance of the cold start problem and practical mitigation strategies
- Hybrid recommendation strategies and weight tuning
- Trade-offs between recommendation accuracy and diversity

## Challenges
- Tuning ALS hyperparameters (rank, regularization, alpha) for optimal precision@k
- Building an efficient ANN index that balances recall vs latency
- Debugging popularity bias where model only recommends blockbuster items

## What I'd Do Differently
- Implement exploration (epsilon-greedy) from the start to combat popularity bias
- Build a more comprehensive evaluation framework with multiple metrics
- Start with a simpler model (kNN) to establish baseline before ALS
- Add user features (demographics, location) to improve cold start

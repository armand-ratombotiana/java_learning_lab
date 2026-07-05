# Mental Models: Recommendation Engine

- **User-Item Matrix = Sparse Graph**: Most users interact with few items. Matrix factorization fills in the missing entries.
- **Latent Factors = Hidden Tastes**: Dimensions like "action vs drama" or "cheap vs luxury" that ML discovers from data.
- **Similarity = Cosine of Embeddings**: Users/items are vectors; similarity is dot product in latent space.
- **Cold Start = Bootstrap Problem**: No history means fall back to content-based or popularity-based recommendations.
- **Diversity vs Relevance Trade-off**: Recommend familiar items (exploit) vs novel items (explore) to prevent filter bubbles.
- **Online Serving = Nearest Neighbor Search**: At query time, find nearest item vectors to the user vector.

# Flashcards: Recommendation Engine

Front: What is matrix factorization? | Back: Decomposing the user-item interaction matrix into lower-dimensional user and item factor matrices (R = U * V^T), where latent factors represent hidden features.

Front: What is Alternating Least Squares (ALS)? | Back: An iterative optimization that alternates between fixing user factors and solving for item factors, and vice versa, for matrix factorization.

Front: What is HNSW? | Back: Hierarchical Navigable Small World — an approximate nearest neighbor search algorithm that builds a multi-layer graph for fast vector similarity search.

Front: What is the cold start problem? | Back: The inability to recommend items to new users (or recommend new items) due to lack of interaction history; addressed via content-based or popularity-based fallback.

Front: What is NDCG? | Back: Normalized Discounted Cumulative Gain — a ranking metric that rewards relevant items appearing earlier in the recommendation list.

Front: What is collaborative filtering? | Back: A recommendation approach that predicts user preferences based on preferences of similar users or similar items.

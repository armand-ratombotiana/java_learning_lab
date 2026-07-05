# Theory: Recommendation Engine

## Collaborative Filtering
Users who agreed in the past will agree in the future. Two approaches:
- **User-based**: Find similar users to the target user, recommend items they liked
- **Item-based**: Find items similar to items the target user liked

## Matrix Factorization
Decompose the user-item interaction matrix R into two lower-dimensional matrices: R = U * V^T, where U is user factors and V is item factors. Latent factors represent hidden features (e.g., genre preference, price sensitivity).

## Alternating Least Squares (ALS)
An iterative optimization algorithm that alternates between fixing U and solving for V, then fixing V and solving for U. Each sub-problem is a convex least-squares optimization. Handles implicit feedback (clicks, views) by assigning confidence weights.

## Content-Based Filtering
Recommend items similar to what the user has liked in the past, based on item features (category, tags, description embeddings). Uses cosine similarity on TF-IDF vectors or embeddings.

## Hybrid Approach
Combine collaborative + content-based scores using weighted average or a meta-model. Handles the cold-start problem (new users/items with no interaction history).

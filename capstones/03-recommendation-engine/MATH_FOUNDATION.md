# Math Foundation: Recommendation Engine

## Matrix Factorization
R (m x n) ≈ U (m x k) * V^T (k x n)
Where m = users, n = items, k = latent factors

## ALS Objective
min ∑(u,i) c_ui * (p_ui - u_u^T * v_i)^2 + λ * (∑||u_u||^2 + ∑||v_i||^2)

Where:
- p_ui = 1 if interaction exists, 0 otherwise
- c_ui = 1 + alpha * r_ui (confidence weight)
- r_ui = raw interaction count/rating

## Cosine Similarity
sim(u, v) = (u · v) / (||u|| * ||v||)

## Precision@K
P@K = (# of relevant items in top-K) / K

## NDCG (Normalized Discounted Cumulative Gain)
DCG@K = ∑(i=1..K) (2^rel_i - 1) / log2(i + 1)
NDCG@K = DCG@K / IDCG@K

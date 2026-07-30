# Implementation Guide: Recommendation Systems

## 1. Collaborative Filtering

### User-Based CF
1. Build user-item interaction matrix (ratings, clicks, purchases)
2. Find k nearest neighbors for target user (Pearson correlation or cosine similarity)
3. Aggregate neighbors' ratings for unseen items
4. Recommend top-N items

### Item-Based CF
1. Compute item-item similarity matrix
2. For each item the user liked, find similar items
3. Aggregate similarity scores
4. Recommend top-N items (exclude already interacted items)

### Similarity Metrics
```java
// Pearson correlation
double pearson(double[] a, double[] b) {
    double meanA = mean(a), meanB = mean(b);
    double num = 0, denA = 0, denB = 0;
    for (int i = 0; i < a.length; i++) {
        num += (a[i] - meanA) * (b[i] - meanB);
        denA += Math.pow(a[i] - meanA, 2);
        denB += Math.pow(b[i] - meanB, 2);
    }
    return num / (Math.sqrt(denA) * Math.sqrt(denB));
}
```

## 2. Content-Based Filtering

### TF-IDF Vectorization
1. Tokenize item descriptions
2. Compute TF (term frequency) for each term
3. Compute IDF (inverse document frequency):
   `IDF(t) = log(N / df(t))`
4. Weight = TF × IDF
5. Compute cosine similarity between user profile and item vectors

### User Profile
Aggregate TF-IDF vectors of items the user has liked/interacted with. Average or weighted by interaction strength.

## 3. Matrix Factorization (SVD)

### Concept
Decompose user-item matrix `R (m×n)` into:
```
R ≈ U × Σ × V^T
```
Where:
- `U (m×k)`: user latent factors
- `Σ (k×k)`: singular values
- `V^T (k×n)`: item latent factors

### ALS (Alternating Least Squares)
Efficient for sparse matrices:
1. Fix item factors, solve for user factors
2. Fix user factors, solve for item factors
3. Iterate until convergence

## 4. Hybrid Approaches

| Strategy | Combination | Example |
|----------|-------------|---------|
| Weighted | Linear combination of scores | 0.7 × CF + 0.3 × Content |
| Cascade | Content filters, then CF ranks | Jobs recommendations |
| Feature augmentation | CF features as input to content model | Netflix Prize |

## 5. Real-Time Recommendations

### Online Updates
- Incremental matrix factorization (SGD updates per event)
- Multi-armed bandits for exploration vs exploitation
- Session-based: RNN/GRU on click sequences

### Architecture
```
Event → Feature Store → Real-time Inference → Recommendation API
                                  ↕
                        Model Updates (async)
```

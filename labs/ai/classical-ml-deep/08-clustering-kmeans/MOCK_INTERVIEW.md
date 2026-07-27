# Mock Interview: K-Means Clustering

**Topic:** Implement k-means with k-means++ initialization from scratch

## Core Questions

### Q1: Describe the k-means algorithm.

**Answer:**
1. Initialize $k$ centroids
2. **Assignment step:** Assign each point to nearest centroid (usually Euclidean distance)
3. **Update step:** Recompute centroids as mean of assigned points
4. Repeat steps 2-3 until convergence (centroids stabilize or max iterations)

Converges to a local optimum of inertia: $J = \sum_{i=1}^n \min_{c_j} \|x_i - c_j\|^2$

### Q2: Implement k-means++ initialization and the full algorithm.

```python
def kmeans_plus_plus(X, k):
    n = X.shape[0]
    centroids = [X[np.random.randint(n)]]
    for _ in range(1, k):
        dists = np.min([np.linalg.norm(X - c, axis=1) for c in centroids], axis=0)
        probs = dists ** 2 / np.sum(dists ** 2)
        centroids.append(X[np.random.choice(n, p=probs)])
    return np.array(centroids)

class KMeans:
    def __init__(self, k=3, max_iters=100, tol=1e-4):
        self.k = k
        self.max_iters = max_iters
        self.tol = tol
        self.centroids = None
        self.labels = None

    def fit(self, X):
        self.centroids = kmeans_plus_plus(X, self.k)
        for _ in range(self.max_iters):
            # Assignment
            dists = np.linalg.norm(X[:, None] - self.centroids[None, :], axis=2)
            self.labels = np.argmin(dists, axis=1)
            # Update
            new_centroids = np.array([X[self.labels == i].mean(axis=0)
                                      for i in range(self.k)])
            if np.linalg.norm(new_centroids - self.centroids) < self.tol:
                break
            self.centroids = new_centroids

    def predict(self, X):
        dists = np.linalg.norm(X[:, None] - self.centroids[None, :], axis=2)
        return np.argmin(dists, axis=1)
```

### Q3: Why does k-means++ initialization matter?

**Answer:**
Standard k-means picks random initial centroids, which can lead to poor local minima.

k-means++ spreads out initial centroids: each new centroid is chosen with probability proportional to $\min \|x - c\|^2$ (distance squared from nearest existing centroid).

**Guarantee:** $O(\log k)$-competitive with optimal clustering in expectation.

### Q4: How do you choose $k$?

**Methods:**
- **Elbow method:** Plot inertia vs. $k$, look for knee
- **Silhouette score:** $\frac{b-a}{\max(a,b)}$ where $a$ = mean intra-cluster distance, $b$ = mean nearest-cluster distance. Higher = better
- **Gap statistic:** Compare inertia vs. null reference distribution
- **Davies-Bouldin index:** Ratio of within-cluster to between-cluster distances. Lower = better

## Advanced

- **Limitations:** Assumes spherical clusters, equal size, sensitive to scaling
- **K-medoids (PAM):** Use actual points as centroids, robust to outliers
- **Mini-batch K-means:** Efficient for large datasets, updates centroids with mini-batches
- **Elkan's algorithm:** Uses triangle inequality to avoid redundant distance calculations

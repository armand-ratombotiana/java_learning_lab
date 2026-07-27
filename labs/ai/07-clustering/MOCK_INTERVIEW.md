# Mock Interview: Clustering

## Question 1: K-Means Implementation
**Q**: Implement K-means clustering with K-means++ initialization.

**A**:
```python
class KMeans:
    def __init__(self, k=3, max_iters=100):
        self.k = k
        self.max_iters = max_iters

    def fit(self, X):
        # K-means++ initialization
        centroids = [X[np.random.randint(len(X))]]
        for _ in range(1, self.k):
            dists = np.min([np.sum((X - c)**2, axis=1) for c in centroids], axis=0)
            probs = dists / dists.sum()
            centroids.append(X[np.random.choice(len(X), p=probs)])
        self.centroids = np.array(centroids)

        for _ in range(self.max_iters):
            # Assign
            dists = np.linalg.norm(X[:, None] - self.centroids[None], axis=2)
            labels = np.argmin(dists, axis=1)
            # Update
            new_centroids = np.array([X[labels == i].mean(axis=0)
                                      for i in range(self.k)])
            if np.allclose(self.centroids, new_centroids): break
            self.centroids = new_centroids
        return labels
```

## Question 2: Choosing K
**Q**: How do you choose the optimal number of clusters?

**A**: Methods:
- **Elbow method**: Plot inertia vs K, look for elbow
- **Silhouette score**: Measures cluster cohesion vs separation. Range [-1, 1], higher is better
- **Gap statistic**: Compare against null reference distribution
- **Calinski-Harabasz index**: Ratio of between-cluster to within-cluster variance
- **Domain knowledge**: Business requirements often dictate K

## Question 3: K-Means Limitations
**Q**: What are limitations of K-means? When would you use DBSCAN instead?

**A**: K-means limitations:
- Assumes spherical clusters
- Sensitive to outliers
- Requires specifying K
- Sensitive to initialization
- Gets stuck in local optima
- Fails on non-convex shapes (crescent, spiral)

Use DBSCAN when: clusters have arbitrary shapes, varying densities, or you need to detect outliers.

## Question 4: Hierarchical Clustering
**Q**: Explain agglomerative clustering. Compare single vs complete vs average linkage.

**A**: Bottom-up: each point starts as its own cluster, iteratively merges nearest.

- **Single linkage**: Distance between closest points. Can produce "chaining" effect.
- **Complete linkage**: Distance between farthest points. Produces compact clusters.
- **Average linkage**: Average of all pairwise distances. Compromise between single and complete.
- **Ward's linkage**: Minimize within-cluster variance. Similar to K-means objective.

## Question 5: Evaluation Without Labels
**Q**: How do you evaluate clustering quality when ground truth is unavailable?

**A**: Internal validation metrics:
- **Silhouette score**: (b-a)/max(a,b) for each point
- **Davies-Bouldin index**: Average similarity between each cluster and its most similar one (lower is better)
- **Calinski-Harabasz index**: Variance ratio criterion (higher is better)
- **Dunn index**: Minimum inter-cluster distance / maximum intra-cluster distance
- **Stability**: Consistency across different initializations
- **Profile inspection**: Human evaluation of cluster characteristics

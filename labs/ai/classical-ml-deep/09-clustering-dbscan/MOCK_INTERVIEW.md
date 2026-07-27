# Mock Interview: DBSCAN

**Topic:** Explain DBSCAN — density-reachable, border points, parameter selection

## Core Questions

### Q1: Explain DBSCAN and its core concepts.

**Answer:**
DBSCAN (Density-Based Spatial Clustering of Applications with Noise) groups points that are densely packed.

**Core point:** Has at least `minPts` points within `eps` distance (including itself).

**Border point:** Within `eps` of a core point but has < `minPts` in its neighborhood.

**Noise point:** Neither core nor border.

**Directly density-reachable:** $q$ is directly density-reachable from $p$ if $p$ is core and $q$ is in $p$'s $\epsilon$-neighborhood.

**Density-reachable:** Chain of directly density-reachable points.

**Density-connected:** There exists a point $o$ that is density-reachable to both $p$ and $q$.

**Cluster:** Maximal set of density-connected points.

### Q2: Walk through the algorithm.

**Answer:**
```
For each point p in dataset:
    If p is visited: skip
    Mark p as visited
    neighbors = regionQuery(p, eps)
    If |neighbors| < minPts:
        Mark p as noise (may become border later)
    Else:
        Create new cluster C
        ExpandCluster(p, neighbors, C, eps, minPts)

ExpandCluster(p, neighbors, C, eps, minPts):
    Add p to C
    For each q in neighbors:
        If q not visited:
            Mark q visited
            q_neighbors = regionQuery(q, eps)
            If |q_neighbors| >= minPts:
                neighbors = neighbors ∪ q_neighbors
        If q not in any cluster:
            Add q to C
```

### Q3: How do you select `eps` and `minPts`?

**Answer:**
- **`minPts`:** Rule of thumb: $\ge d+1$ or $2 \times d$. Typically 3-5 for 2D, larger for noisy data.
- **`eps`:** Use k-distance graph. For each point, compute distance to its $k$th nearest neighbor ($k = \text{minPts} - 1$). Sort ascending, look for "elbow" — sudden jump indicates optimal `eps`.

**Heuristics:**
- Too small `eps` = many noise points, fragmented clusters
- Too large `eps` = clusters merge, most points become core
- `minPts` too small = many small clusters; too large = fewer clusters, more noise

### Q4: Advantages and disadvantages vs. k-means.

| Aspect | DBSCAN | K-Means |
|--------|--------|---------|
| **Shape** | Arbitrary shapes | Spherical |
| **Noise** | Handles naturally | Forces all points to clusters |
| **$k$** | Automatic (from data) | Must specify |
| **Density** | Handles varying density (with tuning) | Assumes uniform density |
| **Determinism** | Yes (same order) | No (random init) |
| **Scalability** | $O(n^2)$ naive, $O(n \log n)$ with spatial index | $O(n)$ |
| **High dimensions** | Curse of density | Works |

## Advanced

- **HDBSCAN:** Hierarchical version — varies `eps` automatically, produces hierarchical cluster structure, better for varying densities
- **OPTICS:** Ordering points to identify clustering structure — extends DBSCAN for variable density
- **Border vs. core ambiguity:** In practice, cluster definitions can be sensitive to parameter choice

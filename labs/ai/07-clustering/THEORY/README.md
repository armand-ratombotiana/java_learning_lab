# Clustering - Theory

## 1. K-Means Clustering

### Algorithm
1. Initialize k centroids
2. Assign points to nearest centroid
3. Update centroids to mean of cluster
4. Repeat until convergence

### Objective
```
minimize Σᵢ minⱼ ||xᵢ - μⱼ||²
```

### Initialization
- Random, k-means++
- Sensitivity to initial conditions

## 2. DBSCAN

### Density-Based
- Core points: ≥minPts in ε-neighborhood
- Border points: in neighborhood of core
- Noise points: neither

### Parameters
- ε (epsilon): neighborhood radius
- minPts: minimum points for core

### Advantages
- No need to specify k
- Finds arbitrary shapes
- Identifies outliers

## 3. Hierarchical Clustering

### Agglomerative (Bottom-up)
- Start with each point as cluster
- Merge closest pairs
- Repeat until one cluster

### Divisive (Top-down)
- Start with one cluster
- Recursively split

### Linkage Methods
- **Single**: min distance
- **Complete**: max distance
- **Average**: mean distance
- **Ward**: minimize variance increase

## 4. Gaussian Mixture Models

### Probabilistic Clustering
```
P(x) = Σₖ πₖ N(x|μₖ, Σₖ)
```

### EM Algorithm
- **E-step**: Compute responsibilities
- **M-step**: Update parameters

## 5. Evaluation Metrics

### External (with labels)
- Rand Index, Adjusted Rand Index
- Fowlkes-Mallows
- V-measure (homogeneity + completeness)

### Internal (without labels)
- Silhouette score
- Elbow method (inertia)
- Davies-Bouldin index
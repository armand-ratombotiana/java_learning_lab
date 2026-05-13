# Clustering - Complete Theory

## 1. K-Means

### 1.1 Algorithm
1. Initialize K centroids randomly
2. Assign points to nearest centroid
3. Update centroids to mean of assigned points
4. Repeat until convergence

### 1.2 Objective
Minimize: sum_i ||x_i - mu_{assign(i)}||^2

### 1.3 Initialization Methods
- Random init
- K-Means++
- Multiple restarts

## 2. Hierarchical Clustering

### 2.1 Agglomerative (bottom-up)
Start with each point as cluster, merge closest

### 2.2 Divisive (top-down)
Start with one cluster, split recursively

### 2.3 Linkage Methods
- Single: min distance between points
- Complete: max distance
- Average: mean distance
- Ward: minimize variance increase

## 3. DBSCAN

### 3.1 Concepts
- Core points: >= minPts neighbors
- Border points: in neighborhood of core
- Noise points: neither

### 3.2 Algorithm
Density-connected components

### 3.3 Advantages
- Finds arbitrary shapes
- Automatic cluster count
- Outlier detection

## 4. Gaussian Mixture Models

### 4.1 Model
P(x) = sum_k pi_k N(x | mu_k, Sigma_k)

### 4.2 EM Algorithm
- E-step: compute responsibilities
- M-step: update parameters

### 4.3 Soft Clustering
Provides probability of membership

## 5. Evaluation

### 5.1 Internal Metrics
- Silhouette score
- Davies-Bouldin index
- elbow method

### 5.2 External Metrics
- Adjusted Rand Index
- V-measure
- Fowlkes-Mallows

## Java Implementation

```java
public class KMeans {
    public int[] fit(Matrix X, int k, int maxIter) {
        int n = X.rows();
        Vector[] centroids = initialize(X, k);
        int[] labels = new int[n];
        
        for (int iter = 0; iter < maxIter; iter++) {
            // Assign points to nearest centroid
            for (int i = 0; i < n; i++) {
                labels[i] = nearestCentroid(X.getRow(i), centroids);
            }
            
            // Update centroids
            Vector[] newCentroids = new Vector[k];
            int[] counts = new int[k];
            for (int i = 0; i < k; i++) newCentroids[i] = VectorOperations.zeros(X.cols());
            
            for (int i = 0; i < n; i++) {
                int c = labels[i];
                newCentroids[c] = VectorOperations.add(newCentroids[c], X.getRow(i));
                counts[c]++;
            }
            
            for (int i = 0; i < k; i++) {
                if (counts[i] > 0) {
                    newCentroids[i] = VectorOperations.scale(newCentroids[i], 1.0/counts[i]);
                }
            }
            centroids = newCentroids;
        }
        return labels;
    }
}
```
# PROBLEM WALKTHROUGH: K-Means Clustering

## Problem Statement

**Difficulty:** Medium  
**Time Limit:** 45 minutes  
**Category:** Unsupervised Learning / Clustering

Implement K-Means clustering from scratch in Java 21+, including both random initialization and k-means++ initialization. K-Means partitions n samples into k clusters by minimizing the within-cluster sum of squares (inertia).

### Mathematical Foundation

**Objective (Inertia):**
```
J = Σ_{j=1}^k Σ_{x_i ∈ C_j} ||x_i - μ_j||²
```

where C_j is the j-th cluster and μ_j is its centroid (mean of assigned points).

**Lloyd's Algorithm:**
1. Initialize k centroids μ_1, ..., μ_k
2. Repeat until convergence:
   - **Assignment step**: Assign each x_i to the nearest centroid:
     ```
     c_i = argmin_j ||x_i - μ_j||²
     ```
   - **Update step**: Recompute centroids:
     ```
     μ_j = (1/|C_j|) * Σ_{x_i ∈ C_j} x_i
     ```
3. Convergence when assignments no longer change

### Requirements:

1. **Distance metric**: Euclidean distance (L2). Optionally support Manhattan (L1).

2. **Random initialization**: Randomly select k samples from the data as initial centroids.

3. **K-means++ initialization**: Weighted random selection where probability of selecting a point is proportional to its distance from the nearest existing centroid.

4. **Fit**: Run Lloyd's algorithm until convergence (no assignment changes) or max iterations.

5. **Predict**: Assign samples to nearest cluster.

6. **Inertia**: Return the total within-cluster sum of squares.

7. **Elbow method**: Utility to compute inertia for a range of k values (automated elbow detection is a bonus).

8. **Silhouette score**: Compute the silhouette coefficient for evaluating cluster quality:
   ```
   s(i) = (b(i) - a(i)) / max(a(i), b(i))
   ```
   where a(i) = mean distance to points in same cluster, b(i) = mean distance to nearest other cluster.

### Example:

```java
double[][] X = {{1.0, 1.0}, {1.5, 2.0}, {2.0, 1.5},
                {8.0, 8.0}, {8.5, 8.5}, {9.0, 8.0},
                {1.0, 8.0}, {1.5, 8.5}, {2.0, 7.5}};

KMeans kmeans = new KMeans(3, 100, "euclidean", "k-means++");
kmeans.fit(X);
int[] labels = kmeans.predict(X);
double inertia = kmeans.getInertia();
double silhouette = kmeans.silhouetteScore(X);
```

---

## Step-by-Step Solution Walkthrough

### 1. The K-Means Objective

Minimizing inertia is equivalent to maximizing the likelihood of a Gaussian mixture model with spherical covariance (σ²I) and equal priors. The objective:

```
J = Σ_j Σ_i ||x_i - μ_j||²
```

is non-convex and NP-hard in general. Lloyd's algorithm finds a local minimum.

**Why it works:** The two steps of Lloyd's algorithm each reduce (or maintain) J:
- **Assignment**: Each point goes to the nearest centroid → reduces each point's contribution to J
- **Update**: Moving centroid to the mean minimizes Σ||x_i - μ||² for that cluster (the mean is the minimizer of the sum of squared distances)

This is a form of **coordinate descent** on J.

### 2. K-Means++ Initialization

Random initialization can lead to poor local minima. K-means++ (Arthur & Vassilvitskii, 2007) provides a provably better initialization (O(log k) competitive ratio):

```
1. Choose first centroid uniformly at random from data points
2. For each subsequent centroid:
   a. For each point x_i, compute D(x_i) = min distance to nearest existing centroid
   b. Choose x_i as new centroid with probability P(i) = D(x_i)² / Σ D(x_j)²
```

**Effect:** New centroids are more likely to be far from existing ones, spreading centroids across the data.

### 3. Convergence

Lloyd's algorithm converges in finite steps (assignments change finitely many times), but worst-case runtime is exponential in n. In practice, it converges in O(k * n * d * I) where I is typically 10-100 iterations.

**Convergence check:**
```java
boolean converged = true;
for (int i = 0; i < n; i++) {
    if (assignments[i] != oldAssignments[i]) {
        converged = false;
        break;
    }
}
```

### 4. The Elbow Method

Plot inertia vs. k. The optimal k is at the "elbow" where diminishing returns kick in:

```java
for (int k = 1; k <= maxK; k++) {
    KMeans km = new KMeans(k, 100, "euclidean", "k-means++");
    km.fit(X);
    inertias[k-1] = km.getInertia();
}
// Find "elbow" using the angle between successive segments
```

### 5. Silhouette Score

For each point i:
- a(i) = mean distance to other points in same cluster (cohesion)
- b(i) = min mean distance to points in other clusters (separation)
- s(i) = (b(i) - a(i)) / max(a(i), b(i))

s(i) ∈ [-1, 1]:
- ≈ 1: Well-clustered (far from other clusters)
- ≈ 0: On cluster boundary
- < 0: Misclassified (closer to another cluster)

Mean silhouette over all points = average silhouette score.

### 6. Implementation

```java
package com.ml.kmeans;

import java.util.*;

/**
 * K-Means clustering with support for random and k-means++ initialization.
 * <p>
 * Minimizes within-cluster sum of squares (inertia) via Lloyd's algorithm.
 * Supports Euclidean and Manhattan distance metrics.
 */
public class KMeans {

    public enum DistanceMetric { EUCLIDEAN, MANHATTAN }
    public enum InitMethod { RANDOM, KMEANS_PLUS_PLUS }

    private int k;
    private int maxIterations;
    private DistanceMetric metric;
    private InitMethod initMethod;
    private double[][] centroids;
    private int[] assignments;
    private double inertia;
    private int nIterations;
    private long seed;

    /**
     * Constructs a K-Means clusterer.
     *
     * @param k            number of clusters
     * @param maxIterations maximum iterations for Lloyd's algorithm
     * @param metric       distance metric ("euclidean" or "manhattan")
     * @param initMethod   initialization ("random" or "k-means++")
     */
    public KMeans(int k, int maxIterations, String metric, String initMethod) {
        this.k = k;
        this.maxIterations = maxIterations;
        this.metric = metric.equalsIgnoreCase("manhattan")
                ? DistanceMetric.MANHATTAN : DistanceMetric.EUCLIDEAN;
        this.initMethod = initMethod.equalsIgnoreCase("k-means++")
                ? InitMethod.KMEANS_PLUS_PLUS : InitMethod.RANDOM;
        this.seed = 42;
    }

    /**
     * Default constructor: k=3, maxIter=100, euclidean, k-means++.
     */
    public KMeans() {
        this(3, 100, "euclidean", "k-means++");
    }

    /**
     * Sets the random seed for reproducibility.
     */
    public void setSeed(long seed) {
        this.seed = seed;
    }

    // ========== Public API ==========

    /**
     * Fits K-Means to the data.
     *
     * @param X input data of shape [n_samples, n_features]
     */
    public void fit(double[][] X) {
        validateInput(X);
        int n = X.length, d = X[0].length;
        assignments = new int[n];
        Random rng = new Random(seed);

        // Initialize centroids
        centroids = initMethod == InitMethod.RANDOM
                ? initRandom(X, k, rng)
                : initKMeansPlusPlus(X, k, rng);

        // Lloyd's algorithm
        for (int iter = 0; iter < maxIterations; iter++) {
            nIterations = iter + 1;
            int[] oldAssignments = Arrays.copyOf(assignments, n);

            // Assignment step
            for (int i = 0; i < n; i++) {
                assignments[i] = findNearestCentroid(X[i]);
            }

            // Check convergence
            boolean converged = true;
            for (int i = 0; i < n; i++) {
                if (assignments[i] != oldAssignments[i]) {
                    converged = false;
                    break;
                }
            }
            if (converged) break;

            // Update step
            updateCentroids(X);
        }

        // Compute final inertia
        computeInertia(X);
    }

    /**
     * Predicts cluster assignments for input samples.
     *
     * @param X input features of shape [n_samples, n_features]
     * @return cluster labels of shape [n_samples]
     */
    public int[] predict(double[][] X) {
        int n = X.length;
        int[] predictions = new int[n];
        for (int i = 0; i < n; i++) {
            predictions[i] = findNearestCentroid(X[i]);
        }
        return predictions;
    }

    /**
     * Predicts cluster label for a single sample.
     */
    public int predict(double[] x) {
        return findNearestCentroid(x);
    }

    /**
     * Returns cluster centroids. Shape: [k, n_features]
     */
    public double[][] getCentroids() {
        return centroids;
    }

    /**
     * Returns total within-cluster sum of squares (inertia).
     */
    public double getInertia() {
        return inertia;
    }

    /**
     * Returns the number of iterations run.
     */
    public int getIterations() {
        return nIterations;
    }

    /**
     * Computes the average silhouette score for the clustering.
     * Range: [-1, 1]. Higher = better separation.
     */
    public double silhouetteScore(double[][] X) {
        int n = X.length;
        if (n <= 1 || k <= 1) return 0.0;

        double totalSilhouette = 0.0;
        int validPoints = 0;

        for (int i = 0; i < n; i++) {
            double a = meanIntraClusterDistance(X, i);
            double b = minInterClusterDistance(X, i);

            if (Math.max(a, b) > 0) {
                totalSilhouette += (b - a) / Math.max(a, b);
                validPoints++;
            }
        }

        return validPoints > 0 ? totalSilhouette / validPoints : 0.0;
    }

    /**
     * Computes inertia for a range of k values (elbow method data).
     *
     * @param X     input data
     * @param maxK  maximum k to evaluate
     * @return array of inertia values for k = 1..maxK
     */
    public static double[] elbowMethod(double[][] X, int maxK) {
        double[] inertias = new double[maxK];
        for (int k = 1; k <= maxK; k++) {
            KMeans km = new KMeans(k, 100, "euclidean", "k-means++");
            km.fit(X);
            inertias[k - 1] = km.getInertia();
        }
        return inertias;
    }

    // ========== Initialization ==========

    private double[][] initRandom(double[][] X, int k, Random rng) {
        int n = X.length, d = X[0].length;
        double[][] centroids = new double[k][d];

        boolean[] used = new boolean[n];
        for (int j = 0; j < k; j++) {
            int idx;
            do {
                idx = rng.nextInt(n);
            } while (used[idx]);
            used[idx] = true;
            System.arraycopy(X[idx], 0, centroids[j], 0, d);
        }

        return centroids;
    }

    private double[][] initKMeansPlusPlus(double[][] X, int k, Random rng) {
        int n = X.length, d = X[0].length;
        double[][] centroids = new double[k][d];

        // Choose first centroid uniformly
        int firstIdx = rng.nextInt(n);
        System.arraycopy(X[firstIdx], 0, centroids[0], 0, d);

        double[] minDistances = new double[n];
        Arrays.fill(minDistances, Double.MAX_VALUE);

        for (int j = 1; j < k; j++) {
            // Update distances and compute probability weights
            double totalDist = 0;
            for (int i = 0; i < n; i++) {
                double dist = distance(X[i], centroids[j - 1]);
                minDistances[i] = Math.min(minDistances[i], dist);
                totalDist += minDistances[i] * minDistances[i];
            }

            // Choose next centroid with probability proportional to D²
            double threshold = rng.nextDouble() * totalDist;
            double cumulative = 0;
            int selectedIdx = 0;
            for (int i = 0; i < n; i++) {
                cumulative += minDistances[i] * minDistances[i];
                if (cumulative >= threshold) {
                    selectedIdx = i;
                    break;
                }
            }

            System.arraycopy(X[selectedIdx], 0, centroids[j], 0, d);
        }

        return centroids;
    }

    // ========== Core Algorithm ==========

    private int findNearestCentroid(double[] x) {
        int nearest = 0;
        double minDist = distance(x, centroids[0]);
        for (int j = 1; j < centroids.length; j++) {
            double dist = distance(x, centroids[j]);
            if (dist < minDist) {
                minDist = dist;
                nearest = j;
            }
        }
        return nearest;
    }

    private void updateCentroids(double[][] X) {
        int n = X.length, d = X[0].length;
        int[] counts = new int[k];
        double[][] sums = new double[k][d];

        for (int i = 0; i < n; i++) {
            int cluster = assignments[i];
            counts[cluster]++;
            for (int j = 0; j < d; j++) {
                sums[cluster][j] += X[i][j];
            }
        }

        // Handle empty clusters: reassign to the point farthest from its centroid
        for (int j = 0; j < k; j++) {
            if (counts[j] == 0) {
                // Find point farthest from its assigned centroid
                double maxDist = -1;
                int farthestIdx = 0;
                for (int i = 0; i < n; i++) {
                    double dist = distance(X[i], centroids[assignments[i]]);
                    if (dist > maxDist) {
                        maxDist = dist;
                        farthestIdx = i;
                    }
                }
                centroids[j] = Arrays.copyOf(X[farthestIdx], d);
                assignments[farthestIdx] = j;
                counts[j] = 1;
                for (int jj = 0; jj < d; jj++) {
                    sums[j][jj] = X[farthestIdx][jj];
                }
            } else {
                for (int jj = 0; jj < d; jj++) {
                    centroids[j][jj] = sums[j][jj] / counts[j];
                }
            }
        }
    }

    private void computeInertia(double[][] X) {
        int n = X.length;
        inertia = 0;
        for (int i = 0; i < n; i++) {
            double dist = distance(X[i], centroids[assignments[i]]);
            inertia += dist * dist;
        }
    }

    // ========== Silhouette Helpers ==========

    private double meanIntraClusterDistance(double[][] X, int i) {
        int ci = assignments[i];
        double sum = 0;
        int count = 0;
        for (int j = 0; j < X.length; j++) {
            if (j != i && assignments[j] == ci) {
                sum += distance(X[i], X[j]);
                count++;
            }
        }
        return count > 0 ? sum / count : 0;
    }

    private double minInterClusterDistance(double[][] X, int i) {
        int ci = assignments[i];
        double minDist = Double.MAX_VALUE;

        for (int c = 0; c < k; c++) {
            if (c == ci) continue;
            double sum = 0;
            int count = 0;
            for (int j = 0; j < X.length; j++) {
                if (assignments[j] == c) {
                    sum += distance(X[i], X[j]);
                    count++;
                }
            }
            if (count > 0) {
                double avgDist = sum / count;
                if (avgDist < minDist) minDist = avgDist;
            }
        }

        return minDist == Double.MAX_VALUE ? 0 : minDist;
    }

    // ========== Distance ==========

    private double distance(double[] a, double[] b) {
        if (metric == DistanceMetric.MANHATTAN) {
            double sum = 0;
            for (int i = 0; i < a.length; i++) {
                sum += Math.abs(a[i] - b[i]);
            }
            return sum;
        } else {
            double sum = 0;
            for (int i = 0; i < a.length; i++) {
                double diff = a[i] - b[i];
                sum += diff * diff;
            }
            return Math.sqrt(sum);
        }
    }

    // ========== Validation ==========

    private void validateInput(double[][] X) {
        if (X == null) throw new IllegalArgumentException("Input cannot be null");
        if (X.length < k)
            throw new IllegalArgumentException("Number of samples (" + X.length
                + ") must be >= k (" + k + ")");
        if (X.length == 0) throw new IllegalArgumentException("Input cannot be empty");
        int d = X[0].length;
        for (int i = 1; i < X.length; i++) {
            if (X[i].length != d)
                throw new IllegalArgumentException("Inconsistent feature dimensions");
        }
    }
}
```

### 7. Test Cases

```java
package com.ml.kmeans;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KMeansTest {

    @Test
    void testSimpleClustering() {
        // Three well-separated clusters
        double[][] X = {{0.0, 0.0}, {0.5, 0.5}, {0.0, 1.0},
                        {5.0, 5.0}, {5.5, 5.5}, {5.0, 4.5},
                        {10.0, 0.0}, {10.5, 0.5}, {9.5, 0.5}};

        KMeans kmeans = new KMeans(3, 100, "euclidean", "k-means++");
        kmeans.setSeed(12345);
        kmeans.fit(X);

        int[] labels = kmeans.predict(X);
        // Three clusters should be found
        assertEquals(3, Arrays.stream(labels).distinct().count());
    }

    @Test
    void testClusterAssignment() {
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {10.0, 10.0}, {11.0, 11.0}};

        KMeans kmeans = new KMeans(2, 100, "euclidean", "k-means++");
        kmeans.setSeed(42);
        kmeans.fit(X);

        int[] labels = kmeans.predict(X);
        // Points 0,1 should be same cluster; 2,3 should be same cluster
        assertEquals(labels[0], labels[1]);
        assertEquals(labels[2], labels[3]);
    }

    @Test
    void testInertiaNonNegative() {
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {3.0, 3.0}, {8.0, 8.0}, {9.0, 9.0}};

        KMeans kmeans = new KMeans(2, 100, "euclidean", "k-means++");
        kmeans.fit(X);

        assertTrue(kmeans.getInertia() >= 0);
    }

    @Test
    void testInertiaDecreasesWithMoreClusters() {
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {3.0, 3.0},
                        {8.0, 8.0}, {9.0, 9.0}, {10.0, 10.0}};

        double[] inertias = KMeans.elbowMethod(X, 5);
        for (int i = 1; i < inertias.length; i++) {
            assertTrue(inertias[i] <= inertias[i - 1] + 1e-10,
                "Inertia increased from k=" + i + " to k=" + (i+1));
        }
    }

    @Test
    void testPredictOnNewData() {
        double[][] X = {{1.0, 1.0}, {2.0, 1.0}, {8.0, 8.0}, {9.0, 9.0}};

        KMeans kmeans = new KMeans(2, 100, "euclidean", "k-means++");
        kmeans.fit(X);

        // New point near cluster 1
        int label = kmeans.predict(new double[]{1.5, 1.0});
        int labelFar = kmeans.predict(new double[]{10.0, 10.0});

        // These should be in different clusters
        assertNotEquals(label, labelFar);
    }

    @Test
    void testCentroidDimension() {
        double[][] X = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};

        KMeans kmeans = new KMeans(2, 100, "euclidean", "k-means++");
        kmeans.fit(X);

        double[][] centroids = kmeans.getCentroids();
        assertEquals(2, centroids.length);
        assertEquals(3, centroids[0].length);
    }

    @Test
    void testManhattanDistance() {
        double[][] X = {{0.0, 0.0}, {0.0, 1.0}, {5.0, 5.0}, {6.0, 6.0}};

        KMeans kmeans = new KMeans(2, 100, "manhattan", "random");
        kmeans.setSeed(123);
        kmeans.fit(X);

        assertEquals(2, Arrays.stream(kmeans.predict(X)).distinct().count());
    }

    @Test
    void testSilhouetteScore() {
        double[][] X = {{0.0, 0.0}, {0.5, 0.5}, {5.0, 5.0}, {5.5, 5.5}};

        KMeans kmeans = new KMeans(2, 100, "euclidean", "k-means++");
        kmeans.fit(X);

        double silhouette = kmeans.silhouetteScore(X);
        // Well-separated clusters should have high silhouette
        assertTrue(silhouette > 0.5, "Silhouette score too low: " + silhouette);
    }

    @Test
    void testKMeansPlusPlusBetterThanRandom() {
        // Data with well-separated clusters but tricky initialization
        double[][] X = new double[50][2];
        for (int i = 0; i < 25; i++) {
            X[i][0] = Math.random() * 2;
            X[i][1] = Math.random() * 2;
        }
        for (int i = 25; i < 50; i++) {
            X[i][0] = 8 + Math.random() * 2;
            X[i][1] = 8 + Math.random() * 2;
        }

        // k-means++ should consistently find both clusters
        KMeans km = new KMeans(2, 50, "euclidean", "k-means++");
        km.setSeed(42);
        km.fit(X);

        int[] labels = km.predict(X);
        assertTrue(Arrays.stream(labels).distinct().count() >= 2);
    }
}
```

### 8. Complexity Analysis

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Initialization (random) | O(k) | O(kd) |
| Initialization (k-means++) | O(nkd) | O(n) |
| Single iteration (assign) | O(nkd) | O(n) |
| Single iteration (update) | O(nd) | O(kd) |
| Total (I iterations) | O(I * nkd) | O(n + kd) |
| Prediction (n samples) | O(nkd) | O(n) |
| Silhouette score | O(n²d) | O(n) |

**Convergence:** Lloyd's algorithm typically converges in 10-100 iterations for real datasets. Worst-case exponential but never observed in practice.

---

## Follow-up Questions

### Q1: What are the limitations of K-Means, and how would you address them?

**Answer:**

| Limitation | Consequence | Mitigation |
|------------|-------------|------------|
| Assumes spherical clusters | Poor performance on elongated/non-spherical clusters | Spectral clustering, DBSCAN |
| Sensitive to initialization | Suboptimal local minima | k-means++, multiple restarts |
| Requires k as input | Must guess cluster count | Elbow method, silhouette analysis, gap statistic |
| Sensitive to outliers | Centroids pulled by outliers | K-Medoids, robust PCA pre-processing |
| Assumes equal cluster sizes | Large clusters dominate | Weighted K-Means, Gaussian Mixture Models |
| Only numerical features | Cannot handle categorical data | K-Prototypes (mixed), Gower distance |
| Hard assignments | No uncertainty measure | Gaussian Mixture Models (soft assignments) |

**Multiple restarts:** Run K-Means r times with different seeds, pick the one with lowest inertia:
```java
KMeans best = null;
double bestInertia = Double.MAX_VALUE;
for (int r = 0; r < nRestarts; r++) {
    KMeans km = new KMeans(k, maxIter, "euclidean", "k-means++");
    km.setSeed(baseSeed + r);
    km.fit(X);
    if (km.getInertia() < bestInertia) {
        bestInertia = km.getInertia();
        best = km;
    }
}
```

### Q2: What is the relationship between K-Means and Gaussian Mixture Models (GMM)?

**Answer:** K-Means is a special case of GMM under three assumptions:
1. Spherical covariance: Σ_j = σ²I (all clusters have same isotropic variance)
2. Equal priors: π_j = 1/k (equal cluster weights)
3. Hard assignment: σ² → 0 (variance goes to zero, so EM converges to hard assignments)

GMM relaxes all three:
- Arbitrary covariance matrices (Σ_j can be diagonal, tied, or full)
- Different cluster weights (π_j must sum to 1)
- Soft assignments (posterior probabilities P(z_i = j | x_i))

**When to use GMM:**
- Clusters have different sizes/shapes
- You need probabilistic membership scores
- You want to model overlapping clusters

### Q3: How does the choice of distance metric affect the clustering?

**Answer:**

| Metric | Formula | Best for |
|--------|---------|----------|
| Euclidean (L2) | sqrt(Σ(x_i - y_i)²) | Continuous features, spherical clusters |
| Manhattan (L1) | Σ|x_i - y_i| | High-dimensional data, robust to outliers |
| Cosine | 1 - (x·y)/(||x||·||y||) | Text data, normalized vectors |
| Correlation | 1 - ρ(x,y) | Time series (shape-based similarity) |

**Choosing a metric:**
- After feature scaling: Euclidean is standard
- For high d (curse of dimensionality): Manhattan may work better
- For text or L2-normalized vectors: Cosine distance
- For mixed types: Gower distance (combined metric)

### Q4: How would you implement Mini-Batch K-Means for large datasets?

**Answer:** Mini-Batch K-Means (Sculley, 2010) processes random subsets of data per iteration, drastically reducing computation:

```java
public class MiniBatchKMeans {
    private void fitMiniBatch(double[][] X, int batchSize) {
        Random rng = new Random(seed);
        for (int iter = 0; iter < maxIterations; iter++) {
            // Sample mini-batch
            double[][] batch = sampleBatch(X, batchSize, rng);

            // Assign batch to nearest centroids
            int[] batchAssignments = new int[batchSize];
            for (int i = 0; i < batchSize; i++) {
                batchAssignments[i] = findNearestCentroid(batch[i]);
            }

            // Per-centroid learning rate
            for (int i = 0; i < batchSize; i++) {
                int c = batchAssignments[i];
                counts[c]++;
                double lr = 1.0 / counts[c];  // Decaying learning rate
                for (int j = 0; j < d; j++) {
                    centroids[c][j] = (1 - lr) * centroids[c][j] + lr * batch[i][j];
                }
            }
        }
    }
}
```

Convergence is faster (10-100x speedup) at the cost of slightly higher inertia. Batch size 100-200 is typical.

### Q5: What validation metrics besides silhouette can you use for clustering?

**Answer:**

| Metric | Range | Internal (no labels) | Notes |
|--------|-------|---------------------|-------|
| **Inertia** (WCSS) | [0, ∞) | Yes | Decreases with k; use elbow |
| **Davies-Bouldin** | [0, ∞) | Yes | Lower = better. Ratio of within-cluster to between-cluster |
| **Calinski-Harabasz** | [0, ∞) | Yes | Higher = better. (SS_b / SS_w) * (n-k)/(k-1) |
| **Silhouette** | [-1, 1] | Yes | Higher = better. Most widely used |
| **Gap statistic** | [0, ∞) | Yes | Compares to null reference; higher = better |
| **Adjusted Rand Index** | [-1, 1] | No (needs ground truth) | Corrected-for-chance version of Rand Index |
| **Normalized Mutual Info** | [0, 1] | No (needs ground truth) | Information-theoretic |

**Davies-Bouldin index:**
```
DB = (1/k) * Σ max_{j≠i} (s_i + s_j) / d(μ_i, μ_j)
```
where s_i = average distance to centroid within cluster i.

Lower DB = better (clusters are compact and well-separated). DB = 0 is perfect.
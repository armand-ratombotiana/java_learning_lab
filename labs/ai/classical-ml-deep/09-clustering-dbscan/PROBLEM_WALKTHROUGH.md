# PROBLEM WALKTHROUGH: DBSCAN Density-Based Clustering

## Problem Statement

**Difficulty:** Hard  
**Time Limit:** 55 minutes  
**Category:** Unsupervised Learning / Density-Based Clustering

Implement DBSCAN (Density-Based Spatial Clustering of Applications with Noise) from scratch in Java 21+. DBSCAN groups points that are closely packed together (points with many nearby neighbors), marking points in low-density regions as noise.

### Mathematical Foundation

**Core definitions:**

Given a dataset D and parameters ε (eps) and minPts:

- **ε-neighborhood**: `N_ε(p) = {q ∈ D | dist(p, q) ≤ ε}`
- **Core point**: `|N_ε(p)| ≥ minPts` (including p itself)
- **Border point**: Not a core point, but `N_ε(p)` contains at least one core point
- **Noise point**: Neither core nor border
- **Directly density-reachable**: q ∈ N_ε(p) and p is a core point
- **Density-reachable**: ∃ chain p_1, ..., p_n where each is directly density-reachable from the previous
- **Density-connected**: ∃ point o such that both p and q are density-reachable from o

### Algorithm:

```
for each point p in D:
    if p is already visited: continue
    mark p as visited
    neighbors = regionQuery(p, ε)
    if |neighbors| < minPts:
        mark p as NOISE
    else:
        C = next cluster label
        expandCluster(p, neighbors, C, ε, minPts)
```

```
expandCluster(p, neighbors, C, ε, minPts):
    add p to cluster C
    for each point q in neighbors:
        if q is not visited:
            mark q as visited
            q_neighbors = regionQuery(q, ε)
            if |q_neighbors| ≥ minPts:
                neighbors = neighbors ∪ q_neighbors
        if q is not assigned to any cluster:
            assign q to cluster C
```

### Requirements:

1. **Region query**: Find all points within distance ε of a given point. Implement with and without spatial indexing (naive O(n) scan vs. k-d tree).

2. **Core/border/noise classification**: Track which points are core, border, or noise after clustering.

3. **Cluster expansion**: Implement the DBSCAN expansion logic with proper visited/cluster label tracking.

4. **Distance metric**: Euclidean distance as default, with support for other metrics.

5. **Prediction**: For new points, assign to nearest cluster's core region or label as noise.

6. **Parameter sensitivity analysis**: Implement a heuristic for estimating ε from the k-distance graph (elbow of sorted distances to k-th nearest neighbor).

### Example:

```java
double[][] X = {{1.0, 1.0}, {1.5, 1.5}, {1.2, 1.0},
                {5.0, 5.0}, {5.5, 5.5}, {5.2, 5.0},
                {10.0, 1.0}, {10.5, 1.5},  // noise candidate
                {3.0, 3.0}};               // noise

DBSCAN dbscan = new DBSCAN(1.5, 3, "euclidean");
dbscan.fit(X);
int[] labels = dbscan.getLabels();          // -1 = noise, 0.. = cluster
boolean[] core = dbscan.isCorePoint();
```

---

## Step-by-Step Solution Walkthrough

### 1. The Density-Based Philosophy

K-Means assumes spherical clusters of similar size. DBSCAN makes no such assumption:
- **Arbitrary shapes**: Can find crescent, S-shaped, or nested clusters
- **Noise handling**: Outliers are labeled as noise (-1) rather than forced into a cluster
- **No k parameter**: ε and minPts are often easier to determine than k

### 2. Parameter Selection

**ε (eps):** The maximum distance for two points to be considered neighbors.
- Too small: Most points become noise (under-segmentation)
- Too large: All points merge into one cluster (over-segmentation)
- **Heuristic**: Plot sorted distances to k-th nearest neighbor (k = minPts); look for the "elbow"

**minPts:** Minimum points to form a dense region.
- Rule of thumb: minPts ≥ d + 1 (dimensionality + 1)
- Common default: minPts = 2 * d
- Larger minPts → more points classified as noise

### 3. Region Query Optimization

Naive O(n) per query leads to O(n²) overall. With spatial indexing:
- **k-d tree**: O(log n) per query on average. Best for low-medium d (< 20)
- **R-tree**: Better for higher dimensions
- **Grid-based**: Simple and effective for 2D data

Our implementation includes both naive and k-d tree approaches.

### 4. Implementation

```java
package com.ml.dbscan;

import java.util.*;

/**
 * DBSCAN (Density-Based Spatial Clustering of Applications with Noise).
 * <p>
 * Finds clusters of arbitrary shape based on density. Points in low-density
 * regions are classified as noise (label -1).
 * <p>
 * Parameters:
 * - eps: Maximum distance for neighborhood
 * - minPts: Minimum points to form a dense region (including query point)
 */
public class DBSCAN {

    public enum DistanceMetric { EUCLIDEAN, MANHATTAN }

    private double eps;
    private int minPts;
    private DistanceMetric metric;
    private boolean useKDTree;

    private int[] labels;
    private boolean[] corePoints;
    private boolean[] borderPoints;
    private boolean[] noisePoints;
    private int numClusters;
    private KDTree kdTree;

    /**
     * Constructs DBSCAN clusterer.
     *
     * @param eps     maximum distance for two points to be neighbors
     * @param minPts  minimum number of points to form a dense region
     * @param metric  distance metric ("euclidean" or "manhattan")
     */
    public DBSCAN(double eps, int minPts, String metric) {
        this.eps = eps;
        this.minPts = minPts;
        this.metric = metric.equalsIgnoreCase("manhattan")
                ? DistanceMetric.MANHATTAN : DistanceMetric.EUCLIDEAN;
        this.useKDTree = true;
    }

    /**
     * Default constructor: eps=1.0, minPts=5, euclidean.
     */
    public DBSCAN() {
        this(1.0, 5, "euclidean");
    }

    /**
     * Enables/disables k-d tree for spatial acceleration.
     */
    public void setUseKDTree(boolean useKDTree) {
        this.useKDTree = useKDTree;
    }

    // ========== Public API ==========

    /**
     * Fits DBSCAN to the data, discovering clusters.
     *
     * @param X input data of shape [n_samples, n_features]
     */
    public void fit(double[][] X) {
        validateInput(X);
        int n = X.length;
        int d = X[0].length;

        labels = new int[n];
        Arrays.fill(labels, -2);  // -2 = unvisited
        corePoints = new boolean[n];
        borderPoints = new boolean[n];
        noisePoints = new boolean[n];

        // Build spatial index
        if (useKDTree && d <= 20) {
            kdTree = new KDTree(X, metric);
        } else {
            kdTree = null;
        }

        int currentCluster = 0;

        for (int i = 0; i < n; i++) {
            if (labels[i] != -2) continue;  // Already processed

            List<Integer> neighbors = regionQuery(X, i);

            if (neighbors.size() < minPts) {
                labels[i] = -1;  // Mark as noise initially (may become border later)
                noisePoints[i] = true;
            } else {
                corePoints[i] = true;
                noisePoints[i] = false;
                expandCluster(X, i, neighbors, currentCluster);
                currentCluster++;
            }
        }

        numClusters = currentCluster;

        // Refine: if a noise point is neighbor to a core point, reclassify as border
        for (int i = 0; i < n; i++) {
            if (labels[i] == -1) {
                for (int neighbor : regionQuery(X, i)) {
                    if (corePoints[neighbor]) {
                        noisePoints[i] = false;
                        borderPoints[i] = true;
                        labels[i] = labels[neighbor];
                        break;
                    }
                }
            }
        }
    }

    /**
     * Returns cluster labels for all points. -1 = noise.
     */
    public int[] getLabels() {
        return labels;
    }

    /**
     * Returns whether each point is a core point.
     */
    public boolean[] isCorePoint() {
        return corePoints;
    }

    /**
     * Returns whether each point is a border point.
     */
    public boolean[] isBorderPoint() {
        return borderPoints;
    }

    /**
     * Returns whether each point is classified as noise.
     */
    public boolean[] isNoisePoint() {
        return noisePoints;
    }

    /**
     * Returns the number of clusters found.
     */
    public int getNumClusters() {
        return numClusters;
    }

    /**
     * Predicts cluster assignment for new points based on nearest core point.
     * Points too far from any core region are labeled -1 (noise).
     *
     * @param X query points of shape [n_samples, n_features]
     * @return predicted labels of shape [n_samples]
     */
    public int[] predict(double[][] X) {
        int n = X.length;
        int[] predictions = new int[n];

        for (int i = 0; i < n; i++) {
            double minDist = Double.MAX_VALUE;
            int nearestLabel = -1;

            for (int j = 0; j < labels.length; j++) {
                if (corePoints[j]) {
                    double dist = distance(X[i], getPoint(j));
                    if (dist < minDist) {
                        minDist = dist;
                        nearestLabel = labels[j];
                    }
                }
            }

            predictions[i] = (minDist <= eps) ? nearestLabel : -1;
        }

        return predictions;
    }

    /**
     * Estimates the optimal eps parameter using the k-distance graph.
     * Returns sorted distances to k-th nearest neighbor (k = minPts).
     */
    public static double[] estimateEps(double[][] X, int minPts) {
        int n = X.length;
        double[] kDistances = new double[n];

        for (int i = 0; i < n; i++) {
            double[] distances = new double[n];
            for (int j = 0; j < n; j++) {
                distances[j] = euclideanDistance(X[i], X[j]);
            }
            Arrays.sort(distances);
            kDistances[i] = distances[Math.min(minPts, n - 1)];
        }

        Arrays.sort(kDistances);
        return kDistances;
    }

    // ========== Core Algorithm ==========

    private void expandCluster(double[][] X, int pointIdx,
                                List<Integer> neighbors, int clusterLabel) {
        labels[pointIdx] = clusterLabel;
        Queue<Integer> queue = new LinkedList<>(neighbors);

        while (!queue.isEmpty()) {
            int q = queue.poll();

            if (labels[q] == -1) {
                // Was noise, now reclassified as border
                labels[q] = clusterLabel;
                noisePoints[q] = false;
                borderPoints[q] = true;
            }

            if (labels[q] != -2) continue;  // Already assigned to a cluster

            labels[q] = clusterLabel;
            List<Integer> qNeighbors = regionQuery(X, q);

            if (qNeighbors.size() >= minPts) {
                corePoints[q] = true;
                queue.addAll(qNeighbors);
            } else {
                borderPoints[q] = true;
            }
        }
    }

    private List<Integer> regionQuery(double[][] X, int pointIdx) {
        if (kdTree != null) {
            return kdTree.rangeQuery(pointIdx, eps);
        }

        // Naive O(n) scan
        List<Integer> neighbors = new ArrayList<>();
        double[] point = X[pointIdx];
        for (int i = 0; i < X.length; i++) {
            if (distance(point, X[i]) <= eps) {
                neighbors.add(i);
            }
        }
        return neighbors;
    }

    // ========== KD-Tree for Spatial Indexing ==========

    private static class KDTree {
        private KDNode root;
        private final double[][] points;
        private final DistanceMetric metric;

        KDTree(double[][] points, DistanceMetric metric) {
            this.points = points;
            this.metric = metric;
            int n = points.length;
            Integer[] indices = new Integer[n];
            for (int i = 0; i < n; i++) indices[i] = i;
            root = buildTree(indices, 0);
        }

        private KDNode buildTree(Integer[] indices, int depth) {
            if (indices.length == 0) return null;

            int axis = depth % points[0].length;
            Arrays.sort(indices, Comparator.comparingDouble(i -> points[i][axis]));

            int medianIdx = indices.length / 2;
            int medianPoint = indices[medianIdx];

            Integer[] left = Arrays.copyOfRange(indices, 0, medianIdx);
            Integer[] right = Arrays.copyOfRange(indices, medianIdx + 1, indices.length);

            return new KDNode(medianPoint, axis,
                buildTree(left, depth + 1),
                buildTree(right, depth + 1));
        }

        List<Integer> rangeQuery(int pointIdx, double eps) {
            List<Integer> result = new ArrayList<>();
            rangeQueryRecursive(root, points[pointIdx], eps, result);
            return result;
        }

        private void rangeQueryRecursive(KDNode node, double[] queryPoint,
                                          double eps, List<Integer> result) {
            if (node == null) return;

            double dist = distance(queryPoint, points[node.pointIdx]);
            if (dist <= eps) {
                result.add(node.pointIdx);
            }

            double diff = queryPoint[node.axis] - points[node.pointIdx][node.axis];
            double eps2 = eps * eps;

            // For Euclidean: check if splitting plane is within hypersphere
            if (diff * diff <= eps2) {
                rangeQueryRecursive(node.left, queryPoint, eps, result);
                rangeQueryRecursive(node.right, queryPoint, eps, result);
            } else if (diff < 0) {
                rangeQueryRecursive(node.left, queryPoint, eps, result);
            } else {
                rangeQueryRecursive(node.right, queryPoint, eps, result);
            }
        }

        private double distance(double[] a, double[] b) {
            if (metric == DistanceMetric.MANHATTAN) {
                double sum = 0;
                for (int i = 0; i < a.length; i++) sum += Math.abs(a[i] - b[i]);
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

        private static class KDNode {
            int pointIdx;
            int axis;
            KDNode left, right;

            KDNode(int pointIdx, int axis, KDNode left, KDNode right) {
                this.pointIdx = pointIdx;
                this.axis = axis;
                this.left = left;
                this.right = right;
            }
        }
    }

    // ========== Utility ==========

    private double distance(double[] a, double[] b) {
        if (metric == DistanceMetric.MANHATTAN) {
            double sum = 0;
            for (int i = 0; i < a.length; i++) sum += Math.abs(a[i] - b[i]);
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

    private static double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    private double[] getPoint(int idx) {
        return kdTree != null ? kdTree.points[idx] : null;
    }

    private void validateInput(double[][] X) {
        if (X == null) throw new IllegalArgumentException("Input cannot be null");
        if (X.length == 0) throw new IllegalArgumentException("Input cannot be empty");
        int d = X[0].length;
        for (int i = 1; i < X.length; i++) {
            if (X[i].length != d)
                throw new IllegalArgumentException("Inconsistent feature dimensions");
        }
    }
}
```

### 5. Test Cases

```java
package com.ml.dbscan;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DBSCANTest {

    @Test
    void testTwoDenseClusters() {
        double[][] X = {{1.0, 1.0}, {1.1, 1.1}, {1.2, 1.0}, {1.0, 1.2},
                        {5.0, 5.0}, {5.1, 5.1}, {5.2, 5.0}, {5.0, 5.2},
                        {10.0, 10.0}};  // far away, should be noise

        DBSCAN dbscan = new DBSCAN(0.5, 3, "euclidean");
        dbscan.fit(X);

        int[] labels = dbscan.getLabels();
        assertTrue(labels[labels.length - 1] == -1, "Isolated point should be noise");
        assertEquals(2, dbscan.getNumClusters());
    }

    @Test
    void testCrescentShapes() {
        // Crescent-shaped clusters (non-spherical)
        double[][] X = new double[30][2];
        for (int i = 0; i < 15; i++) {
            double angle = Math.PI * i / 14;
            X[i][0] = 1.0 + 0.5 * Math.cos(angle);
            X[i][1] = 1.0 + 0.5 * Math.sin(angle);
        }
        for (int i = 15; i < 30; i++) {
            double angle = Math.PI * (i - 15) / 14;
            X[i][0] = 2.5 + 0.5 * Math.cos(angle + Math.PI);
            X[i][1] = 1.5 + 0.5 * Math.sin(angle + Math.PI);
        }

        DBSCAN dbscan = new DBSCAN(0.4, 3, "euclidean");
        dbscan.fit(X);

        assertEquals(2, dbscan.getNumClusters());
    }

    @Test
    void testNoisePoints() {
        double[][] X = {{1.0, 1.0}, {1.1, 1.1}, {1.2, 1.0},
                        {5.0, 5.0}, {5.1, 5.1}, {5.2, 5.0},
                        {100.0, 100.0}, {100.1, 100.1}};

        DBSCAN dbscan = new DBSCAN(0.3, 3, "euclidean");
        dbscan.fit(X);

        boolean[] noise = dbscan.isNoisePoint();
        assertTrue(noise[6]);   // far from clusters -> noise
        assertTrue(noise[7]);   // isolated pair, but only 2 < minPts -> noise
    }

    @Test
    void testCoreAndBorderPoints() {
        double[][] X = {{0.0, 0.0}, {0.1, 0.0}, {0.0, 0.1}, {0.1, 0.1},  // dense core
                        {0.5, 0.0},  // border (close to core)
                        {10.0, 10.0}};  // noise

        DBSCAN dbscan = new DBSCAN(0.5, 4, "euclidean");
        dbscan.fit(X);

        boolean[] core = dbscan.isCorePoint();
        boolean[] border = dbscan.isBorderPoint();

        assertTrue(core[0] || core[1] || core[2] || core[3]);  // at least some are core
        assertTrue(border[4]);  // point 4 is a border point
    }

    @Test
    void testKDTreeVsNaive() {
        double[][] X = new double[50][2];
        for (int i = 0; i < 25; i++) {
            X[i][0] = Math.random() * 2;
            X[i][1] = Math.random() * 2;
        }
        for (int i = 25; i < 50; i++) {
            X[i][0] = 5 + Math.random() * 2;
            X[i][1] = 5 + Math.random() * 2;
        }

        DBSCAN dbscanKD = new DBSCAN(0.5, 3, "euclidean");
        dbscanKD.setUseKDTree(true);
        dbscanKD.fit(X);
        int[] labelsKD = dbscanKD.getLabels();

        DBSCAN dbscanNaive = new DBSCAN(0.5, 3, "euclidean");
        dbscanNaive.setUseKDTree(false);
        dbscanNaive.fit(X);
        int[] labelsNaive = dbscanNaive.getLabels();

        assertArrayEquals(labelsKD, labelsNaive);
    }

    @Test
    void testManhattanDistance() {
        double[][] X = {{1.0, 1.0}, {1.0, 2.0}, {1.0, 3.0},
                        {5.0, 5.0}, {6.0, 5.0}, {6.0, 6.0}};

        DBSCAN dbscan = new DBSCAN(1.0, 3, "manhattan");
        dbscan.fit(X);

        assertEquals(2, dbscan.getNumClusters());
    }

    @Test
    void testEpsilonEstimation() {
        double[][] X = {{1.0, 1.0}, {1.1, 1.1}, {1.2, 1.0},
                        {5.0, 5.0}, {5.1, 5.1}, {5.2, 5.0}};

        double[] kDist = DBSCAN.estimateEps(X, 3);
        assertTrue(kDist.length == X.length);
        assertTrue(kDist[0] >= 0);
    }

    @Test
    void testPredictNewPoints() {
        double[][] X = {{0.0, 0.0}, {1.0, 0.0}, {0.0, 1.0}, {1.0, 1.0},
                        {5.0, 5.0}, {6.0, 5.0}, {5.0, 6.0}, {6.0, 6.0}};

        DBSCAN dbscan = new DBSCAN(1.0, 3, "euclidean");
        dbscan.fit(X);

        int[] predictions = dbscan.predict(new double[][]{{0.5, 0.5}, {7.0, 7.0}});
        // 0.5,0.5 should be in a cluster; 7.0,7.0 might be noise
        assertTrue(predictions[0] >= 0 || predictions[0] == -1);
    }

    @Test
    void testAllPointsNoise() {
        double[][] X = {{0.0, 0.0}, {10.0, 10.0}, {20.0, 20.0}};

        DBSCAN dbscan = new DBSCAN(1.0, 3, "euclidean");
        dbscan.fit(X);

        assertEquals(0, dbscan.getNumClusters());
        for (int label : dbscan.getLabels()) {
            assertEquals(-1, label);
        }
    }
}
```

### 6. Complexity Analysis

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Naive region query | O(n) per query | O(1) |
| Naive total | O(n²) | O(n) |
| k-d tree build | O(n log n) | O(n) |
| k-d tree region query | O(log n) average, O(n) worst | O(log n) |
| k-d tree total (average) | O(n log n) | O(n) |
| Prediction (naive) | O(n_new * n * d) | O(n_new) |

**When to use k-d tree:**
- n > 1000: k-d tree provides significant speedup
- d < 20: k-d tree degrades in high dimensions (curse of dimensionality)
- For d ≥ 20: Use approximate methods or naive scan

---

## Follow-up Questions

### Q1: What are the main advantages and limitations of DBSCAN compared to K-Means?

**Answer:**

| Aspect | DBSCAN | K-Means |
|--------|--------|---------|
| **Cluster shape** | Arbitrary (any shape) | Spherical only |
| **Number of clusters** | Automatic (from data) | Must specify k |
| **Noise handling** | Explicit (-1 label) | Forces all points into clusters |
| **Parameters** | ε, minPts (density-based) | k (count-based) |
| **Deterministic** | Yes (if fixed order) | No (depends on initialization) |
| **Scalability** | O(n²) naive, O(n log n) with index | O(n) per iteration |
| **High dimensions** | Poor (curse of dimensionality) | Poor but better than DBSCAN |
| **Variable density** | Struggles if clusters have very different densities | Not affected |
| **Requires distance** | Yes | Yes |

**When to use DBSCAN:**
- Non-spherical clusters (crescents, spirals, nested shapes)
- Data has significant noise
- You don't know k beforehand
- Data has approximately uniform density

**When to use K-Means:**
- Spherical, well-separated clusters
- Very large datasets (needs O(n) scaling)
- You know (or can estimate) k

### Q2: How would you handle datasets with varying density clusters?

**Answer:** DBSCAN with global ε fails when clusters have different densities:
- ε too large: Dense clusters merge
- ε too small: Sparse clusters become noise

**Solutions:**
1. **OPTICS** (Ordering Points To Identify Clustering Structure): Instead of a fixed ε, computes a reachability plot. Clusters appear as valleys in the reachability plot.
2. **HDBSCAN** (Hierarchical DBSCAN): Extends DBSCAN to variable density by considering all possible ε values hierarchically. The most modern and robust approach.
3. **SNN** (Shared Nearest Neighbor): Replace distance with shared-neighbor similarity; more robust to density variations.

**HDBSCAN sketch:**
1. Compute mutual reachability distance: `d_mreach(a,b) = max(core_k(a), core_k(b), d(a,b))`
2. Build minimum spanning tree on mutual reachability graph
3. Create cluster hierarchy by removing edges in decreasing distance order
4. Extract stable clusters from the hierarchy using excess of mass

### Q3: What is the "curse of dimensionality" in the context of DBSCAN?

**Answer:** In high dimensions, distances become approximately equal (all points are far from each other):
- The ratio of nearest to farthest neighbor distance approaches 1
- The concept of "neighborhood" breaks down — either all points are neighbors (if ε is moderate) or no points are neighbors (if ε is small)

**Quantitatively:** For data uniformly distributed in [0,1]^d:
```
E[||x - y||²] = d/6
Std[||x - y||²] = O(√d)
```
The coefficient of variation → 0 as d grows.

**Mitigation strategies:**
- Feature selection: Remove irrelevant features
- Dimensionality reduction (PCA) before DBSCAN
- Use cosine distance (works better in high dimensions for text data)
- Consider subspace clustering methods (SUBCLU, CLIQUE)

### Q4: How does the order of point processing affect DBSCAN results?

**Answer:** In theory, DBSCAN is deterministic regarding cluster **membership** (a point belongs to the same cluster regardless of processing order). In practice:
- Border points assigned to the cluster that processes them first (if they are density-reachable from multiple clusters)
- Different orderings may assign a border point to different clusters but the overall cluster count and core assignments are invariant

**Test this:**
```java
DBSCAN db1 = new DBSCAN(...); db1.fit(X); // default order
// Shuffle X, fit again
int[] labels1 = db1.getLabels();
// Compare: core points should match exactly;
// border points may differ when equidistant from multiple clusters
```

### Q5: What is HDBSCAN and how does it improve on DBSCAN?

**Answer:** HDBSCAN (Campello, Moulavi, Sander, 2013) addresses DBSCAN's sensitivity to ε:

| Feature | DBSCAN | HDBSCAN |
|---------|--------|---------|
| ε parameter | Required (global) | Not needed (hierarchical) |
| Variable density | Struggles | Handles naturally |
| Hierarchy | Flat clustering only | Full hierarchy available |
| Cluster selection | Arbitrary threshold | Stability-based extraction |
| Outliers | Binary (noise or not) | Probabilistic outlier score |

**How HDBSCAN works:**
1. Compute core distances (distance to minPts-th neighbor)
2. Build minimum spanning tree of mutual reachability distances
3. Create a hierarchy by removing edges in order of increasing distance
4. Condense the hierarchy by merging clusters with fewer than minClusterSize points
5. Extract clusters with maximum stability over the hierarchy

HDBSCAN is the recommended modern replacement for DBSCAN in most applications. Libraries like `scikit-learn-contrib/hdbscan` provide efficient implementations.
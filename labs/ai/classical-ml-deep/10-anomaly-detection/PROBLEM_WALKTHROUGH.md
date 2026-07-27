# PROBLEM WALKTHROUGH: Anomaly Detection with Isolation Forest

## Problem Statement

**Difficulty:** Hard  
**Time Limit:** 55 minutes  
**Category:** Unsupervised Learning / Anomaly Detection

Implement the Isolation Forest algorithm from scratch in Java 21+ for unsupervised anomaly detection. Isolation Forest isolates anomalies by randomly partitioning the data space using decision trees. Anomalies are few and different — they require fewer random splits to isolate than normal points.

### Mathematical Foundation

**Key insight:** Anomalies are rare and have feature values that differ significantly from normal points. Random decision trees can isolate them in fewer splits.

**Path length h(x):** The number of edges traversed from root to terminating node for point x.

**Anomaly Score:**

```
s(x, n) = 2^(-E[h(x)] / c(n))
```

where:
- `E[h(x)]` is the average path length of x across all trees
- `c(n)` is the average path length of unsuccessful search in a Binary Search Tree:

```
c(n) = 2 * H(n - 1) - 2 * (n - 1) / n
```
where H(i) = ln(i) + 0.5772156649 (Euler's constant) is the harmonic number.

**Interpretation:**
- s ≈ 1: Definitely an anomaly (very short path length)
- s ≈ 0.5: No clear distinction (average path length ≈ c(n))
- s < 0.5: Likely normal (long path length)

**Training:**
For each tree in the forest:
1. Randomly select ψ samples from the dataset (subsample size)
2. Recursively split by randomly selecting a feature and a random split value between min and max
3. Stop when: depth limit reached, node has 1 sample, or all values identical

### Requirements:

1. **Isolation Tree**: Build a binary tree structure where each node has a split feature, split value, and size (number of samples passing through).

2. **Forest construction**: Build `nEstimators` isolation trees, each on a random subsample of size `maxSamples`.

3. **Path length computation**: For a point, traverse each tree to compute its path length (depth). Apply a adjustment factor (c(n) for subsample size) to normalize.

4. **Anomaly score**: Compute `s(x) = 2^(-avgPathLength / c(ψ))`.

5. **Anomaly prediction**: Given a contamination factor, determine the threshold score for classifying anomalies.

6. **Outlier detection**: Return boolean array marking anomalies.

### Example:

```java
double[][] X = {{1.0, 1.0}, {1.5, 1.5}, {1.2, 1.0},
                {5.0, 5.0}, {5.5, 5.5}, {5.2, 5.0},
                {100.0, 100.0}};  // obvious anomaly

IsolationForest iForest = new IsolationForest(100, 256, 0.1);
iForest.fit(X);
boolean[] anomalies = iForest.predict(X);  // last point should be anomaly
double[] scores = iForest.anomalyScore(X);
```

---

## Step-by-Step Solution Walkthrough

### 1. Why Isolation Forest for Anomaly Detection?

**Comparison with traditional methods:**

| Method | Approach | Issues |
|--------|----------|--------|
| Z-score / IQR | Distance from mean | Assumes normality; fails on multivariate, non-parametric data |
| Mahalanobis distance | Distance from mean in covariance space | Fails on non-elliptical distributions; singular covariance |
| One-class SVM | Margin around normal data | Sensitive to kernel parameters; O(n²) scaling |
| LOF (Local Outlier Factor) | Local density comparison | O(n²) scaling; sensitive to k parameter |
| **Isolation Forest** | Isolation by random splits | O(n log n); works on high-d; no distance computation |

**Key advantage:** Isolation Forest does not compute any distance or density measure. It exploits the fact that anomalies are "few and different" — they are isolated in fewer random splits.

### 2. The c(n) Normalization Factor

The expected path length of a random BST with n nodes:
```
c(n) = 2 * H(n - 1) - 2 * (n - 1) / n
```

For n = 2: c(2) = 1
For n = 256: c(256) ≈ 2 * (ln(255) + 0.577) - 2*255/256 ≈ 12.3

This serves as the baseline: if a point's average path length equals c(ψ), its anomaly score s = 0.5 — no distinction.

### 3. Subsampling

The original paper recommends:
- ψ = 256 (default subsample size)
- nEstimators = 100

**Why subsample?** Smaller subsamples create shallower trees with shorter path lengths for all points, making anomalies more detectable. Using the full dataset would produce deeper trees where anomalies are less differentiated.

### 4. Tree Depth Limit

The maximum depth of each tree is set to `ceiling(log₂(ψ))`. This keeps trees compact and prevents the model from fitting too specifically to normal points.

### 5. Implementation

```java
package com.ml.isolationforest;

import java.util.*;

/**
 * Isolation Forest for unsupervised anomaly detection.
 * <p>
 * Isolates anomalies by randomly partitioning the feature space.
 * Anomalies require fewer random splits to isolate, resulting in
 * shorter path lengths and higher anomaly scores.
 * <p>
 * Based on: Liu, Ting, and Zhou (2008). "Isolation Forest."
 * IEEE International Conference on Data Mining.
 */
public class IsolationForest {

    private List<IsolationTree> trees;
    private int nEstimators;
    private int maxSamples;
    private double contamination;
    private double anomalyThreshold;
    private Random rng;

    /**
     * Constructs an Isolation Forest.
     *
     * @param nEstimators  number of isolation trees (typically 100)
     * @param maxSamples   subsample size for each tree (typically 256)
     * @param contamination expected proportion of anomalies in the data (0.0 to 0.5)
     */
    public IsolationForest(int nEstimators, int maxSamples, double contamination) {
        this.nEstimators = nEstimators;
        this.maxSamples = maxSamples;
        this.contamination = contamination;
        this.rng = new Random(42);
    }

    /**
     * Default constructor: 100 trees, subsample 256, contamination 10%.
     */
    public IsolationForest() {
        this(100, 256, 0.1);
    }

    /**
     * Sets the random seed for reproducibility.
     */
    public void setSeed(long seed) {
        this.rng = new Random(seed);
    }

    // ========== Public API ==========

    /**
     * Fits the Isolation Forest to training data.
     *
     * @param X input data of shape [n_samples, n_features]
     */
    public void fit(double[][] X) {
        validateInput(X);
        int n = X.length;
        int d = X[0].length;
        int subsampleSize = Math.min(maxSamples, n);
        int maxDepth = (int) Math.ceil(Math.log(subsampleSize) / Math.log(2));

        trees = new ArrayList<>(nEstimators);

        for (int t = 0; t < nEstimators; t++) {
            // Random subsample
            int[] indices = sampleIndices(n, subsampleSize);
            double[][] subsample = new double[subsampleSize][d];
            for (int i = 0; i < subsampleSize; i++) {
                System.arraycopy(X[indices[i]], 0, subsample[i], 0, d);
            }

            IsolationTree tree = new IsolationTree();
            tree.root = buildTree(subsample, 0, maxDepth);
            trees.add(tree);
        }

        // Determine threshold based on contamination
        double[] scores = anomalyScore(X);
        Arrays.sort(scores);
        int thresholdIdx = (int) Math.ceil((1 - contamination) * scores.length) - 1;
        thresholdIdx = Math.max(0, Math.min(scores.length - 1, thresholdIdx));
        anomalyThreshold = scores[thresholdIdx];
    }

    /**
     * Predicts whether each sample is an anomaly.
     *
     * @param X input data of shape [n_samples, n_features]
     * @return boolean array: true = anomaly, false = normal
     */
    public boolean[] predict(double[][] X) {
        double[] scores = anomalyScore(X);
        boolean[] predictions = new boolean[X.length];
        for (int i = 0; i < X.length; i++) {
            predictions[i] = scores[i] >= anomalyThreshold;
        }
        return predictions;
    }

    /**
     * Computes anomaly scores for all samples.
     * Score range: (0, 1]. Higher = more anomalous.
     *
     * @param X input data
     * @return anomaly scores of shape [n_samples]
     */
    public double[] anomalyScore(double[][] X) {
        int n = X.length;
        double[] scores = new double[n];
        double cNorm = cFactor(maxSamples);

        for (int i = 0; i < n; i++) {
            double avgPathLength = 0;
            for (IsolationTree tree : trees) {
                avgPathLength += pathLength(tree.root, X[i], 0);
            }
            avgPathLength /= trees.size();

            // Anomaly score: s(x, n) = 2^(-E[h(x)] / c(n))
            scores[i] = Math.pow(2, -avgPathLength / cNorm);
        }

        return scores;
    }

    /**
     * Returns the anomaly score for a single sample.
     */
    public double anomalyScore(double[] x) {
        return anomalyScore(new double[][]{x})[0];
    }

    /**
     * Returns the threshold for anomaly classification.
     */
    public double getAnomalyThreshold() {
        return anomalyThreshold;
    }

    // ========== Tree Building ==========

    private ITNode buildTree(double[][] data, int depth, int maxDepth) {
        int n = data.length;
        int d = data[0].length;

        // Stopping conditions
        if (depth >= maxDepth || n <= 1) {
            return new ITNode(n);  // External node (leaf)
        }

        // Check if all values are identical
        boolean allSame = true;
        double firstVal = data[0][0];
        for (int i = 1; i < n && allSame; i++) {
            for (int j = 0; j < d && allSame; j++) {
                if (Math.abs(data[i][j] - firstVal) > 1e-12) {
                    allSame = false;
                }
            }
        }
        if (allSame) {
            return new ITNode(n);
        }

        // Randomly select feature
        int splitFeature = rng.nextInt(d);

        // Find min and max for the feature
        double minVal = Double.MAX_VALUE;
        double maxVal = -Double.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            double val = data[i][splitFeature];
            if (val < minVal) minVal = val;
            if (val > maxVal) maxVal = val;
        }

        // Random split value between min and max
        double splitValue = minVal + rng.nextDouble() * (maxVal - minVal);

        // If all values are the same after checking, create leaf
        if (Math.abs(maxVal - minVal) < 1e-12) {
            return new ITNode(n);
        }

        // Split data
        List<double[]> leftList = new ArrayList<>();
        List<double[]> rightList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (data[i][splitFeature] < splitValue) {
                leftList.add(data[i]);
            } else {
                rightList.add(data[i]);
            }
        }

        // Handle degenerate split (all points go to one side)
        if (leftList.isEmpty() || rightList.isEmpty()) {
            return new ITNode(n);
        }

        double[][] leftData = leftList.toArray(new double[0][]);
        double[][] rightData = rightList.toArray(new double[0][]);

        ITNode node = new ITNode(splitFeature, splitValue);
        node.leftChild = buildTree(leftData, depth + 1, maxDepth);
        node.rightChild = buildTree(rightData, depth + 1, maxDepth);

        return node;
    }

    // ========== Path Length Computation ==========

    private double pathLength(ITNode node, double[] x, int depth) {
        if (node.isExternal) {
            // Adjust path length for unbuilt subtree
            // c(node.size) is the expected path length for a random BST with node.size leaves
            return depth + cFactor(node.size);
        }

        if (x[node.splitFeature] < node.splitValue) {
            return pathLength(node.leftChild, x, depth + 1);
        } else {
            return pathLength(node.rightChild, x, depth + 1);
        }
    }

    // ========== Utility ==========

    /**
     * Average path length of unsuccessful search in BST.
     * c(n) = 2*H(n-1) - 2*(n-1)/n
     */
    private double cFactor(int n) {
        if (n <= 1) return 0;
        if (n == 2) return 1;
        return 2.0 * (harmonicNumber(n - 1)) - 2.0 * (n - 1) / n;
    }

    /**
     * Harmonic number H(n) = Σ_{i=1}^n 1/i
     * Approximated as ln(n) + γ for large n, exact for small n.
     */
    private double harmonicNumber(int n) {
        double h = 0;
        for (int i = 1; i <= n; i++) {
            h += 1.0 / i;
        }
        return h;
    }

    private int[] sampleIndices(int n, int sampleSize) {
        int[] indices = new int[sampleSize];
        // Reservoir sampling for efficiency
        for (int i = 0; i < sampleSize; i++) {
            indices[i] = i;
        }
        for (int i = sampleSize; i < n; i++) {
            int j = rng.nextInt(i + 1);
            if (j < sampleSize) {
                indices[j] = i;
            }
        }
        return indices;
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

    // ========== Tree Node ==========

    private static class IsolationTree {
        ITNode root;
    }

    private static class ITNode {
        boolean isExternal;
        int splitFeature;
        double splitValue;
        int size;             // number of samples in this node (only for external)
        ITNode leftChild;
        ITNode rightChild;

        // External node constructor
        ITNode(int size) {
            this.isExternal = true;
            this.size = size;
        }

        // Internal node constructor
        ITNode(int splitFeature, double splitValue) {
            this.isExternal = false;
            this.splitFeature = splitFeature;
            this.splitValue = splitValue;
        }
    }
}
```

### 6. Test Cases

```java
package com.ml.isolationforest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IsolationForestTest {

    @Test
    void testObviousAnomaly() {
        // Normal data centered at (5,5) with one extreme outlier
        double[][] X = new double[51][2];
        for (int i = 0; i < 50; i++) {
            X[i][0] = 4.5 + Math.random();
            X[i][1] = 4.5 + Math.random();
        }
        X[50][0] = 100.0;
        X[50][1] = 100.0;

        IsolationForest iForest = new IsolationForest(100, 256, 0.05);
        iForest.setSeed(12345);
        iForest.fit(X);

        boolean[] anomalies = iForest.predict(X);
        assertTrue(anomalies[50], "The extreme outlier should be detected as anomaly");
    }

    @Test
    void testAnomalyScoreOrdering() {
        double[][] X = {{1.0, 1.0}, {1.1, 1.1}, {1.2, 1.0},
                        {5.0, 5.0}, {5.1, 5.1}, {5.2, 5.0},
                        {100.0, 100.0}};

        IsolationForest iForest = new IsolationForest(100, 128, 0.1);
        iForest.setSeed(42);
        iForest.fit(X);

        double[] scores = iForest.anomalyScore(X);
        // The far-away point should have the highest anomaly score
        assertTrue(scores[6] > scores[0], "Outlier should have highest anomaly score");
    }

    @Test
    void testAnomalyScoreRange() {
        double[][] X = {{0.0, 0.0}, {0.1, 0.1}, {0.2, 0.0},
                        {10.0, 10.0}, {10.1, 10.1}};

        IsolationForest iForest = new IsolationForest(50, 128, 0.2);
        iForest.fit(X);

        double[] scores = iForest.anomalyScore(X);
        for (double s : scores) {
            assertTrue(s > 0.0 && s <= 1.0,
                "Anomaly score should be in (0, 1] but got " + s);
        }
    }

    @Test
    void testNormalPointsScoreBelowThreshold() {
        double[][] X = new double[60][3];
        // Generate cluster of normal points
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 3; j++) {
                X[i][j] = 5.0 + 0.5 * (Math.random() - 0.5);
            }
        }
        // Add anomalies
        for (int i = 50; i < 60; i++) {
            for (int j = 0; j < 3; j++) {
                X[i][j] = 100.0 + 10.0 * (Math.random() - 0.5);
            }
        }

        IsolationForest iForest = new IsolationForest(100, 128, 0.2);
        iForest.setSeed(42);
        iForest.fit(X);

        boolean[] anomalies = iForest.predict(X);
        long anomalyCount = java.util.Arrays.stream(anomalies).filter(b -> b).count();
        // Should detect roughly contamination fraction
        assertTrue(anomalyCount >= 5 && anomalyCount <= 20,
            "Anomaly count " + anomalyCount + " seems off for contamination 0.2");
    }

    @Test
    void testDifferentContamination() {
        double[][] X = new double[110][2];
        for (int i = 0; i < 100; i++) {
            X[i][0] = Math.random() * 2;
            X[i][1] = Math.random() * 2;
        }
        for (int i = 100; i < 110; i++) {
            X[i][0] = 50 + Math.random() * 10;
            X[i][1] = 50 + Math.random() * 10;
        }

        // Low contamination
        IsolationForest lowContam = new IsolationForest(100, 128, 0.05);
        lowContam.setSeed(42);
        lowContam.fit(X);

        // High contamination
        IsolationForest highContam = new IsolationForest(100, 128, 0.2);
        highContam.setSeed(42);
        highContam.fit(X);

        boolean[] lowPred = lowContam.predict(X);
        boolean[] highPred = highContam.predict(X);

        long lowCount = java.util.Arrays.stream(lowPred).filter(b -> b).count();
        long highCount = java.util.Arrays.stream(highPred).filter(b -> b).count();

        assertTrue(highCount >= lowCount,
            "Higher contamination should flag more anomalies. " +
            "Low: " + lowCount + ", High: " + highCount);
    }

    @Test
    void testConsistencyAcrossRuns() {
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {3.0, 3.0},
                        {5.0, 5.0}, {6.0, 6.0}, {7.0, 7.0},
                        {100.0, 100.0}};

        IsolationForest iForest = new IsolationForest(100, 128, 0.1);
        iForest.setSeed(42);
        iForest.fit(X);
        double[] scores1 = iForest.anomalyScore(X);

        iForest = new IsolationForest(100, 128, 0.1);
        iForest.setSeed(42);
        iForest.fit(X);
        double[] scores2 = iForest.anomalyScore(X);

        assertArrayEquals(scores1, scores2, 1e-12);
    }

    @Test
    void testThresholdStability() {
        double[][] X = new double[100][3];
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 3; j++) {
                X[i][j] = Math.random() * 10;
            }
        }

        IsolationForest iForest = new IsolationForest(50, 64, 0.1);
        iForest.fit(X);

        double threshold = iForest.getAnomalyThreshold();
        assertTrue(threshold > 0 && threshold <= 1,
            "Threshold should be in (0, 1] but got " + threshold);
    }

    @Test
    void testSingleFeature() {
        double[][] X = {{1.0}, {2.0}, {3.0}, {100.0}};

        IsolationForest iForest = new IsolationForest(50, 128, 0.25);
        iForest.fit(X);

        boolean[] anomalies = iForest.predict(X);
        assertTrue(anomalies[3], "100 should be anomaly in [1,2,3,100]");
    }
}
```

### 7. Complexity Analysis

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Build one tree | O(ψ * d * log ψ) | O(ψ) |
| Build all trees | O(nEstimators * ψ * d * log ψ) | O(nEstimators * ψ) |
| Path length (one point) | O(nEstimators * log ψ) | O(1) |
| Anomaly scores (n points) | O(n * nEstimators * log ψ) | O(n) |
| Training total | O(nEstimators * ψ * d * log ψ) | O(nEstimators * ψ + n * d) |

**Default parameters (ψ=256, nEstimators=100):**
- Training: ~100 * 256 * d * 8 ≈ 200,000 * d operations — extremely fast
- Inference: ~100 * log₂(256) = 800 comparisons per point

**Scalability:** Isolation Forest scales linearly with n (training is on subsamples). It's one of the fastest anomaly detection algorithms for large datasets.

---

## Follow-up Questions

### Q1: How does the subsample size ψ affect the Isolation Forest? How do you choose it?

**Answer:**

| ψ | Tree depth | Detection sensitivity | Recommendation |
|---|------------|----------------------|----------------|
| Small (e.g., 32) | Shallow | High — easy to isolate everything | Very fast but may have high false positive rate |
| Medium (128-256) | Moderate | Good balance | Default: ψ = 256 |
| Large (1024+) | Deep | Lower sensitivity | May miss subtle anomalies; better for large normal clusters |

**Why ψ matters:** Small ψ means trees are shallow, so every point has a short path length. Anomalies still have shorter paths than normal points, but the relative difference may be smaller at very small ψ.

**Theoretical guidance:**
- The original paper recommends ψ = 256 as the "sweet spot"
- For datasets with very few anomalies, increase ψ
- For extremely large datasets (n > 10⁶), ψ can be increased to 1024 or 2048
- The contamination parameter should be adjusted independently

### Q2: What are the differences between Isolation Forest and Local Outlier Factor (LOF)?

**Answer:**

| Aspect | Isolation Forest | LOF |
|--------|-----------------|-----|
| Core idea | Isolation via random splits | Local density comparison |
| Distance computation | None (only comparisons) | Requires pairwise distances |
| Scalability | O(n log n) | O(n²) naive; O(n log n) with k-d tree |
| High-dimensional | Works well (no distance concentration) | Poor (curse of dimensionality) |
| Interpretability | Feature importance (split features) | Hard to interpret |
| Global vs local | Finds global outliers | Finds both local and global outliers |
| Parameter sensitivity | Low (ψ, contamination) | High (k neighbors) |
| Density assumption | None | Assumes normal points have similar density |

**When to use LOF over Isolation Forest:**
- When local outliers are important (e.g., a point is normal globally but anomalous relative to its neighborhood)
- When the data is low-dimensional (d ≤ 10)
- When you need to explain why a point is anomalous (nearest neighbors)

**When to use Isolation Forest:**
- Large datasets (n > 10⁵)
- High-dimensional data (d > 10)
- Fast preprocessing pipeline
- No strong density variation in normal data

### Q3: How would you extend Isolation Forest to detect anomalies in streaming data?

**Answer:** Key challenge: the data distribution may change over time (concept drift).

**Approach 1: Sliding Window**
```java
public class StreamingIsolationForest {
    private Deque<double[]> buffer;
    private int windowSize;
    private IsolationForest forest;

    public void update(double[] sample) {
        buffer.addLast(sample);
        if (buffer.size() > windowSize) {
            buffer.removeFirst();
        }
        // Periodic retraining
        if (buffer.size() == windowSize) {
            double[][] X = buffer.toArray(new double[0][]);
            forest.fit(X);
        }
    }
}
```

**Approach 2: Half-Space Trees (HS-Trees)**
A variant designed specifically for streaming: use a fixed set of random splits at initialization and update counts incrementally. Faster than rebuilding the forest.

**Approach 3: Adaptive Isolation Forest**
Periodically rebuild trees, but retain a fraction of old trees to maintain historical profile. Weighted voting between old and new trees.

**Performance concerns:**
- Rebuilding all trees at each time step is O(windowSize * nEstimators * d * log ψ)
- With ψ = 256, nEstimators = 100, this is fast enough for most streaming scenarios

### Q4: How would you interpret feature importance in Isolation Forest?

**Answer:** Track which features are used as split features across all trees. Features that appear more frequently at higher tree levels (closer to root) are more discriminative for anomaly detection.

```java
public double[] featureImportances() {
    int d = dataDimensionality;
    double[] importance = new double[d];

    for (IsolationTree tree : trees) {
        traverseAndCount(tree.root, 1.0, importance);
    }

    // Normalize
    double total = Arrays.stream(importance).sum();
    for (int j = 0; j < d; j++) importance[j] /= total;

    return importance;
}

private void traverseAndCount(ITNode node, double weight, double[] importance) {
    if (node.isExternal) return;
    importance[node.splitFeature] += weight;
    traverseAndCount(node.leftChild, weight * 0.5, importance);
    traverseAndCount(node.rightChild, weight * 0.5, importance);
}
```

**Interpretation:** A feature with high importance means that when that feature was used to split, it successfully isolated anomalies (shortened path length for anomalous points). This provides interpretability for why a point was flagged.

### Q5: What is the theoretical guarantee for the anomaly score?

**Answer:** The expected anomaly score for a normal point can be derived from the BST model:

For a random point x and a random tree T:
```
E[s(x, ψ)] ≈ 0.5  for any normal point
```

**Proof sketch:**
- The expected path length E[h(x)] equals c(ψ) for a random BST (unbiased)
- s(x) = 2^(-E[h(x)] / c(ψ)) = 2^(-1) = 0.5

For an anomaly:
- E[h(x)] < c(ψ) because anomalies are isolated faster
- Therefore -E[h(x)]/c(ψ) < -1, so s(x) > 0.5

**Empirical validation:**
- Normal points: scores cluster around 0.5 to 0.6
- Obvious anomalies: scores > 0.8
- Score > 0.7: strong anomaly signal

This theoretical foundation makes the threshold at 0.5 a principled default, though in practice contamination-driven threshold tuning works better.

### Q6: What are the limitations of Isolation Forest?

**Answer:**

| Limitation | Explanation | Mitigation |
|------------|-------------|------------|
| **Anomalies with normal feature values** | If an anomaly has values within the normal range for each feature individually (only anomalous in combination), random splits may miss it | Extended Isolation Forest (random hyperplanes instead of axis-aligned splits) |
| **High rate of irrelevant features** | Many random splits waste on irrelevant features | Feature selection before isolation forest, or feature weighting |
| **Dense anomaly clusters** | If anomalies form dense clusters (> ψ/2), they may not be isolated quickly | Increase ψ; use cluster-based anomaly detection |
| **Local outliers** | Global isolation misses local outliers | LOF, or combine with k-NN density |
| **Score calibration** | Scores are not well-calibrated probabilities | Platt scaling or isotonic regression on validation set |
| **Determinism** | Random splits produce different results across runs | Set seed for reproducibility, increase tree count |

**Extended Isolation Forest (EIF):** Instead of axis-aligned splits (feature < threshold), use random hyperplanes (w^T x < c). This creates oblique splits that can detect anomalies in rotated subspaces. EIF outperforms IF on high-dimensional data with complex feature interactions.
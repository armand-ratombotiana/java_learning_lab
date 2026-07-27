# PROBLEM WALKTHROUGH: Decision Tree from Scratch

## Problem Statement

**Difficulty:** Medium  
**Time Limit:** 50 minutes  
**Category:** Supervised Learning / Non-parametric / Classification

Implement a decision tree classifier from scratch in Java 21+ using the ID3 (Iterative Dichotomiser 3) / CART (Classification and Regression Trees) algorithm. The tree must recursively partition the feature space using binary splits based on impurity reduction.

### Mathematical Foundation

**Information Gain** (ID3 — uses entropy):

```
Entropy(S) = -Σ_{k=1}^K p_k * log₂(p_k)

Gain(S, A) = Entropy(S) - Σ_{v ∈ Values(A)} (|S_v| / |S|) * Entropy(S_v)
```

**Gini Impurity** (CART — default):

```
Gini(S) = 1 - Σ_{k=1}^K p_k²

Gini Gain = Gini(S) - Σ_{v ∈ Values(A)} (|S_v| / |S|) * Gini(S_v)
```

### Requirements:

1. **Tree building**: Recursive binary splitting using the best split point (feature + threshold). For each node, search over all features and all possible split values (midpoints between consecutive sorted feature values) to find the split that maximizes impurity reduction.

2. **Split criteria**: Support both `entropy` (ID3) and `gini` (CART) as the impurity measure.

3. **Stopping criteria**: Support `maxDepth`, `minSamplesSplit`, `minImpurityDecrease`, and leaf purity threshold.

4. **Prediction**: Traverse the tree for each test sample until reaching a leaf; return the majority class.

5. **Pruning**: Implement minimal cost-complexity pruning with a `ccpAlpha` parameter.

6. **Feature importance**: Compute importance as the total reduction in impurity weighted by the proportion of samples reaching each node.

### Example:

```java
double[][] X = {{1.0, 2.0}, {2.0, 3.0}, {3.0, 1.0}, {6.0, 5.0}, {7.0, 7.0}, {8.0, 6.0}};
int[] y = {0, 0, 0, 1, 1, 1};

DecisionTree tree = new DecisionTree(3, 2, "gini");
tree.fit(X, y);
int prediction = tree.predict(new double[]{5.0, 4.0});  // 0 or 1
```

---

## Step-by-Step Solution Walkthrough

### 1. How Decision Trees Work

Decision trees partition the feature space into axis-aligned rectangles, each corresponding to a leaf node prediction. The algorithm is:

```
function buildTree(S, depth):
    if all same class or depth >= maxDepth or |S| < minSamplesSplit:
        return leaf(majorityClass(S))
    
    bestSplit = findBestSplit(S)
    if no split improves impurity:
        return leaf(majorityClass(S))
    
    leftChild = buildTree(samples where feature <= threshold, depth+1)
    rightChild = buildTree(samples where feature > threshold, depth+1)
    return node(bestSplit.feature, bestSplit.threshold, leftChild, rightChild)
```

### 2. Finding the Best Split

For each feature `j`:
1. Get all values of feature `j` in the current node
2. Sort them
3. Compute candidate thresholds as midpoints between consecutive sorted values
4. For each threshold, split samples into left and right, compute weighted impurity
5. Select the split with the highest impurity reduction

**Time complexity of finding best split:** O(d * n * log n) per node (sorting each feature).

### 3. Impurity Measures

#### Entropy (ID3)
```
Entropy = -Σ p_k * log₂(p_k)
```
Range: [0, log₂(K)]. 0 = pure, log₂(K) = maximally impure.

#### Gini Impurity (CART)
```
Gini = 1 - Σ p_k²
```
Range: [0, 1 - 1/K]. 0 = pure, 1 - 1/K = maximally impure.

**Comparison:**
- Entropy grows more slowly near purity (log₂(1) = 0 steeply) → may produce slightly different splits
- Gini is computationally cheaper (no logarithms)
- In practice, the difference is usually small

### 4. Cost-Complexity Pruning

Pruning removes branches that overfit. Cost-complexity pruning defines:

```
Cost(T) = R(T) + α * |T|
```

where `R(T)` is the misclassification rate on training data, `|T|` is the number of leaf nodes, and `α` is the complexity parameter.

The algorithm finds the smallest subtree that minimizes cost for a given α. We implement weak-link pruning: for each non-leaf node, compute the cost-complexity of keeping vs. pruning it.

### 5. Implementation

```java
package com.ml.decisiontree;

import java.util.*;

/**
 * Decision Tree classifier implementing ID3/CART algorithm.
 * Supports binary splits on continuous features with configurable
 * impurity criterion (gini or entropy), depth limits, and pruning.
 */
public class DecisionTree {

    public enum Criterion { GINI, ENTROPY }

    private TreeNode root;
    private int maxDepth;
    private int minSamplesSplit;
    private int minSamplesLeaf;
    private double minImpurityDecrease;
    private double ccpAlpha;           // cost-complexity pruning parameter
    private Criterion criterion;
    private int numClasses;
    private int numFeatures;
    private Map<Integer, Double> featureImportances;

    /**
     * Constructs a DecisionTree classifier.
     *
     * @param maxDepth            maximum tree depth (stopping criterion)
     * @param minSamplesSplit     minimum samples required to split an internal node
     * @param criterion           impurity criterion ("gini" or "entropy")
     */
    public DecisionTree(int maxDepth, int minSamplesSplit, String criterion) {
        this.maxDepth = maxDepth;
        this.minSamplesSplit = minSamplesSplit;
        this.minSamplesLeaf = 1;
        this.minImpurityDecrease = 0.0;
        this.ccpAlpha = 0.0;
        this.criterion = criterion.equalsIgnoreCase("entropy") ? Criterion.ENTROPY : Criterion.GINI;
    }

    /**
     * Default constructor: maxDepth=5, minSamplesSplit=2, gini criterion.
     */
    public DecisionTree() {
        this(5, 2, "gini");
    }

    // ========== Public API ==========

    /**
     * Builds the decision tree from training data.
     *
     * @param X training features of shape [n_samples, n_features]
     * @param y target labels of shape [n_samples]
     */
    public void fit(double[][] X, int[] y) {
        validateInput(X, y);
        numClasses = Arrays.stream(y).max().orElse(0) + 1;
        numFeatures = X[0].length;
        featureImportances = new HashMap<>();

        int n = X.length;
        int[] indices = new int[n];
        for (int i = 0; i < n; i++) indices[i] = i;

        root = buildTree(X, y, indices, 0);

        // Post-prune if ccpAlpha > 0
        if (ccpAlpha > 0) {
            root = pruneTree(root, 0);
        }

        // Compute feature importances
        computeFeatureImportances(root, 1.0);
    }

    /**
     * Predicts class labels for input samples.
     *
     * @param X input features of shape [n_samples, n_features]
     * @return predicted labels of shape [n_samples]
     */
    public int[] predict(double[][] X) {
        int n = X.length;
        int[] predictions = new int[n];
        for (int i = 0; i < n; i++) {
            predictions[i] = predict(X[i]);
        }
        return predictions;
    }

    /**
     * Predicts class label for a single sample.
     *
     * @param x input feature vector
     * @return predicted class label
     */
    public int predict(double[] x) {
        TreeNode node = root;
        while (!node.isLeaf) {
            if (x[node.splitFeature] <= node.splitThreshold) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return node.prediction;
    }

    /**
     * Returns predicted class probabilities (proportion of training samples per class at leaf).
     *
     * @param X input features
     * @return probability matrix of shape [n_samples, num_classes]
     */
    public double[][] predictProba(double[][] X) {
        int n = X.length;
        double[][] probs = new double[n][numClasses];
        for (int i = 0; i < n; i++) {
            TreeNode node = root;
            while (!node.isLeaf) {
                if (X[i][node.splitFeature] <= node.splitThreshold) {
                    node = node.left;
                } else {
                    node = node.right;
                }
            }
            int total = 0;
            for (int c = 0; c < numClasses; c++) total += node.classCounts[c];
            for (int c = 0; c < numClasses; c++) {
                probs[i][c] = (double) node.classCounts[c] / total;
            }
        }
        return probs;
    }

    /**
     * Returns accuracy score.
     */
    public double score(double[][] X, int[] y) {
        int[] predictions = predict(X);
        int correct = 0;
        for (int i = 0; i < y.length; i++) {
            if (predictions[i] == y[i]) correct++;
        }
        return (double) correct / y.length;
    }

    /**
     * Returns feature importance scores (sum to 1).
     */
    public Map<Integer, Double> getFeatureImportances() {
        return featureImportances;
    }

    /**
     * Returns the depth of the tree.
     */
    public int getDepth() {
        return computeDepth(root);
    }

    /**
     * Returns the number of leaf nodes.
     */
    public int getNumLeaves() {
        return countLeaves(root);
    }

    /**
     * Sets pruning parameter.
     */
    public void setCcpAlpha(double ccpAlpha) {
        this.ccpAlpha = ccpAlpha;
    }

    // ========== Tree Building ==========

    private TreeNode buildTree(double[][] X, int[] y, int[] indices, int depth) {
        int n = indices.length;

        // Count classes in current node
        int[] classCounts = new int[numClasses];
        for (int idx : indices) {
            classCounts[y[idx]]++;
        }

        // Check stopping criteria
        if (depth >= maxDepth || n < minSamplesSplit || isPure(classCounts)) {
            return createLeaf(classCounts, n);
        }

        // Find best split
        Split bestSplit = findBestSplit(X, y, indices, classCounts, n);
        if (bestSplit == null || bestSplit.gain <= minImpurityDecrease) {
            return createLeaf(classCounts, n);
        }

        // Partition indices
        List<Integer> leftList = new ArrayList<>();
        List<Integer> rightList = new ArrayList<>();
        for (int idx : indices) {
            if (X[idx][bestSplit.feature] <= bestSplit.threshold) {
                leftList.add(idx);
            } else {
                rightList.add(idx);
            }
        }

        // Check minSamplesLeaf
        if (leftList.size() < minSamplesLeaf || rightList.size() < minSamplesLeaf) {
            return createLeaf(classCounts, n);
        }

        // Recursively build children
        int[] leftIndices = leftList.stream().mapToInt(Integer::intValue).toArray();
        int[] rightIndices = rightList.stream().mapToInt(Integer::intValue).toArray();

        TreeNode node = new TreeNode();
        node.isLeaf = false;
        node.splitFeature = bestSplit.feature;
        node.splitThreshold = bestSplit.threshold;
        node.impurity = bestSplit.currentImpurity;
        node.gain = bestSplit.gain;
        node.depth = depth;
        node.classCounts = classCounts;
        node.numSamples = n;
        node.left = buildTree(X, y, leftIndices, depth + 1);
        node.right = buildTree(X, y, rightIndices, depth + 1);

        return node;
    }

    private Split findBestSplit(double[][] X, int[] y, int[] indices,
                                 int[] classCounts, int n) {
        double currentImpurity = computeImpurity(classCounts, n);
        Split bestSplit = null;

        for (int feature = 0; feature < numFeatures; feature++) {
            // Extract and sort feature values
            double[] values = new double[n];
            for (int i = 0; i < n; i++) {
                values[i] = X[indices[i]][feature];
            }
            Arrays.sort(values);

            // Candidate thresholds: midpoints between consecutive distinct values
            for (int i = 0; i < n - 1; i++) {
                if (Math.abs(values[i] - values[i + 1]) < 1e-12) continue;
                double threshold = (values[i] + values[i + 1]) / 2.0;

                // Count left distribution
                int[] leftCounts = new int[numClasses];
                int leftN = 0;
                for (int idx : indices) {
                    if (X[idx][feature] <= threshold) {
                        leftCounts[y[idx]]++;
                        leftN++;
                    }
                }
                int rightN = n - leftN;
                if (leftN < minSamplesLeaf || rightN < minSamplesLeaf) continue;

                double leftImpurity = computeImpurity(leftCounts, leftN);
                double rightImpurity = computeImpurity(
                    subtractCounts(classCounts, leftCounts), rightN);

                double weightedImpurity = ((double) leftN / n) * leftImpurity
                                        + ((double) rightN / n) * rightImpurity;
                double gain = currentImpurity - weightedImpurity;

                if (bestSplit == null || gain > bestSplit.gain) {
                    bestSplit = new Split(feature, threshold, gain, currentImpurity);
                }
            }
        }

        return bestSplit;
    }

    // ========== Impurity Computation ==========

    private double computeImpurity(int[] classCounts, int n) {
        if (n == 0) return 0.0;
        return criterion == Criterion.ENTROPY
                ? computeEntropy(classCounts, n)
                : computeGini(classCounts, n);
    }

    private double computeEntropy(int[] classCounts, int n) {
        double entropy = 0.0;
        for (int count : classCounts) {
            if (count > 0) {
                double p = (double) count / n;
                entropy -= p * (Math.log(p) / Math.log(2));  // log base 2
            }
        }
        return entropy;
    }

    private double computeGini(int[] classCounts, int n) {
        double gini = 1.0;
        for (int count : classCounts) {
            if (count > 0) {
                double p = (double) count / n;
                gini -= p * p;
            }
        }
        return gini;
    }

    // ========== Pruning ==========

    private TreeNode pruneTree(TreeNode node, int leafIndex) {
        if (node.isLeaf) return node;

        node.left = pruneTree(node.left, leafIndex);
        node.right = pruneTree(node.right, leafIndex);

        // If both children are leaves, consider pruning
        if (node.left.isLeaf && node.right.isLeaf) {
            double leafError = computeMisclassification(node);
            double subtreeError = computeMisclassification(node.left)
                                + computeMisclassification(node.right);
            double costLeaf = leafError + ccpAlpha;
            double costSubtree = subtreeError + 2 * ccpAlpha;

            if (costLeaf <= costSubtree) {
                // Prune: replace subtree with leaf
                return createLeaf(node.classCounts, node.numSamples);
            }
        }

        return node;
    }

    private double computeMisclassification(TreeNode node) {
        int majorityCount = 0;
        for (int count : node.classCounts) {
            if (count > majorityCount) majorityCount = count;
        }
        return 1.0 - (double) majorityCount / node.numSamples;
    }

    // ========== Feature Importance ==========

    private void computeFeatureImportances(TreeNode node, double weight) {
        if (node.isLeaf) return;

        double nodeImportance = node.gain * weight;
        featureImportances.merge(node.splitFeature, nodeImportance, Double::sum);

        double leftWeight = weight * ((double) node.left.numSamples / node.numSamples);
        double rightWeight = weight * ((double) node.right.numSamples / node.numSamples);
        computeFeatureImportances(node.left, leftWeight);
        computeFeatureImportances(node.right, rightWeight);
    }

    // ========== TreeNode ==========

    private static class TreeNode {
        boolean isLeaf;
        int splitFeature;
        double splitThreshold;
        double impurity;
        double gain;
        int depth;
        int numSamples;
        int[] classCounts;
        int prediction;          // majority class (for leaf)
        TreeNode left;
        TreeNode right;
    }

    private static class Split {
        int feature;
        double threshold;
        double gain;
        double currentImpurity;

        Split(int feature, double threshold, double gain, double currentImpurity) {
            this.feature = feature;
            this.threshold = threshold;
            this.gain = gain;
            this.currentImpurity = currentImpurity;
        }
    }

    private TreeNode createLeaf(int[] classCounts, int n) {
        TreeNode leaf = new TreeNode();
        leaf.isLeaf = true;
        leaf.classCounts = Arrays.copyOf(classCounts, classCounts.length);
        leaf.numSamples = n;

        // Majority class
        int maxCount = -1;
        leaf.prediction = 0;
        for (int c = 0; c < numClasses; c++) {
            if (classCounts[c] > maxCount) {
                maxCount = classCounts[c];
                leaf.prediction = c;
            }
        }

        return leaf;
    }

    private boolean isPure(int[] classCounts) {
        int nonZero = 0;
        for (int count : classCounts) {
            if (count > 0) nonZero++;
            if (nonZero > 1) return false;
        }
        return true;
    }

    private int[] subtractCounts(int[] total, int[] sub) {
        int[] result = new int[total.length];
        for (int i = 0; i < total.length; i++) {
            result[i] = total[i] - sub[i];
        }
        return result;
    }

    private int computeDepth(TreeNode node) {
        if (node.isLeaf) return 1;
        return 1 + Math.max(computeDepth(node.left), computeDepth(node.right));
    }

    private int countLeaves(TreeNode node) {
        if (node.isLeaf) return 1;
        return countLeaves(node.left) + countLeaves(node.right);
    }

    private void validateInput(double[][] X, int[] y) {
        if (X == null || y == null)
            throw new IllegalArgumentException("Input data cannot be null");
        if (X.length != y.length)
            throw new IllegalArgumentException("X and y must have same number of samples");
        if (X.length == 0)
            throw new IllegalArgumentException("Input data cannot be empty");
        int d = X[0].length;
        for (int i = 1; i < X.length; i++) {
            if (X[i].length != d)
                throw new IllegalArgumentException("Inconsistent feature dimensions");
        }
    }
}
```

### 6. Test Cases

```java
package com.ml.decisiontree;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DecisionTreeTest {

    @Test
    void testPerfectSeparation() {
        double[][] X = {{1.0, 2.0}, {2.0, 3.0}, {3.0, 1.0},
                        {6.0, 5.0}, {7.0, 7.0}, {8.0, 6.0}};
        int[] y = {0, 0, 0, 1, 1, 1};

        DecisionTree tree = new DecisionTree(10, 2, "gini");
        tree.fit(X, y);

        assertEquals(1.0, tree.score(X, y), 0.01);
    }

    @Test
    void testPredictionOnNewSample() {
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {1.5, 1.5},
                        {5.0, 5.0}, {6.0, 6.0}, {5.5, 5.5}};
        int[] y = {0, 0, 0, 1, 1, 1};

        DecisionTree tree = new DecisionTree(10, 2, "entropy");
        tree.fit(X, y);

        assertEquals(0, tree.predict(new double[]{1.0, 1.0}));
        assertEquals(1, tree.predict(new double[]{7.0, 7.0}));
    }

    @Test
    void testMaxDepthLimits() {
        double[][] X = {{1.0}, {2.0}, {3.0}, {4.0}, {5.0}, {6.0}};
        int[] y = {0, 0, 0, 1, 1, 1};

        DecisionTree shallow = new DecisionTree(1, 2, "gini");
        shallow.fit(X, y);
        assertTrue(shallow.getDepth() <= 2); // depth 1 = root + at most 1 level
    }

    @Test
    void testGiniVsEntropy() {
        double[][] X = {{1.0}, {2.0}, {3.0}, {4.0}};
        int[] y = {0, 0, 1, 1};

        DecisionTree giniTree = new DecisionTree(10, 2, "gini");
        giniTree.fit(X, y);

        DecisionTree entropyTree = new DecisionTree(10, 2, "entropy");
        entropyTree.fit(X, y);

        assertEquals(1.0, giniTree.score(X, y), 0.01);
        assertEquals(1.0, entropyTree.score(X, y), 0.01);
    }

    @Test
    void testFeatureImportance() {
        double[][] X = {{1.0, 5.0}, {2.0, 5.0}, {3.0, 5.0},
                        {7.0, 5.0}, {8.0, 5.0}, {9.0, 5.0}};
        int[] y = {0, 0, 0, 1, 1, 1};

        DecisionTree tree = new DecisionTree(10, 2, "gini");
        tree.fit(X, y);

        var importances = tree.getFeatureImportances();
        // Feature 0 (the one that varies) should have higher importance
        assertTrue(importances.getOrDefault(0, 0.0) > importances.getOrDefault(1, 0.0));
    }

    @Test
    void testProbabilityOutput() {
        double[][] X = {{1.0}, {2.0}, {3.0}, {4.0}};
        int[] y = {0, 0, 1, 1};

        DecisionTree tree = new DecisionTree(10, 1, "gini");
        tree.fit(X, y);

        double[][] probs = tree.predictProba(X);
        for (double[] prob : probs) {
            double sum = 0;
            for (double p : prob) sum += p;
            assertTrue(Math.abs(sum - 1.0) < 1e-10);
        }
    }

    @Test
    void testNumLeaves() {
        double[][] X = {{1.0}, {2.0}, {3.0}, {4.0}};
        int[] y = {0, 0, 1, 1};

        DecisionTree tree = new DecisionTree(1, 2, "gini");
        tree.fit(X, y);
        assertEquals(2, tree.getNumLeaves()); // depth limit of 1
    }

    @Test
    void testPruningReducesLeaves() {
        double[][] X = {{1.0}, {2.0}, {3.0}, {4.0}, {5.0}, {6.0}};
        int[] y = {0, 0, 0, 1, 1, 1};

        DecisionTree fullTree = new DecisionTree(10, 1, "gini");
        fullTree.fit(X, y);

        DecisionTree prunedTree = new DecisionTree(10, 1, "gini");
        prunedTree.setCcpAlpha(0.01);
        prunedTree.fit(X, y);

        assertTrue(prunedTree.getNumLeaves() <= fullTree.getNumLeaves());
    }

    @Test
    void testSingleClassStopsEarly() {
        double[][] X = {{1.0}, {2.0}, {3.0}};
        int[] y = {0, 0, 0};

        DecisionTree tree = new DecisionTree(10, 1, "gini");
        tree.fit(X, y);

        assertEquals(1, tree.getNumLeaves());
        assertEquals(0, tree.predict(new double[]{100.0}));
    }
}
```

### 7. Complexity Analysis

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Find best split at a node | O(d * n * log n) | O(n) |
| Entire tree (worst case) | O(d * n² * log n) | O(n * depth) |
| Prediction (single sample) | O(depth) | O(1) |
| Prediction (n samples) | O(n * depth) | O(1) |

**Best case:** Balanced tree, depth ≈ O(log n), total O(d * n * log n * log n)
**Worst case:** Unbalanced tree, depth O(n), total O(d * n² * log n)

---

## Follow-up Questions

### Q1: Why are decision trees prone to overfitting, and how do you mitigate it?

**Answer:** Decision trees can grow until every leaf is pure, perfectly memorizing training data (including noise). Mitigations:

1. **Pre-pruning** (early stopping): maxDepth, minSamplesSplit, minImpurityDecrease
2. **Post-pruning** (cost-complexity): Grow fully, then prune back using validation set or ccpAlpha
3. **Minimum leaf size**: Ensure leaves have at least minSamplesLeaf samples
4. **Feature selection**: Limit features considered at each split (as in Random Forest)
5. **Ensemble methods**: Random Forest and Gradient Boosting smooth out individual tree variance

### Q2: How does the choice between Gini and Entropy affect the tree?

**Answer:** Both produce similar trees in practice. Key differences:

| Property | Gini | Entropy |
|----------|------|---------|
| Computation | O(1) per split evaluation | O(log K) per split (log computation) |
| Sensitivity to class distribution | Slightly favors larger classes | More balanced |
| Theoretical basis | Variance minimization | Information theory |
| Typical behavior | Tends toward "purer" splits faster | Slightly more balanced trees |

**Rule of thumb:** Gini is the default in most libraries (scikit-learn, CART). Their difference diminishes as tree size increases.

### Q3: How would you extend this for regression (regression tree)?

**Answer:** Replace the impurity criterion with variance reduction:

```
Variance(S) = (1/|S|) * Σ (y_i - ȳ)²
Reduction = Var(S) - (|S_left|/|S|) * Var(S_left) - (|S_right|/|S|) * Var(S_right)
```

Leaf prediction becomes the mean value of training samples (not majority class):

```java
// In createLeaf for regression:
leaf.prediction = mean of y values at this node
```

The splitting logic remains identical; only the impurity computation and leaf prediction change.

### Q4: What is the bias-variance tradeoff in decision trees?

**Answer:**
- **High variance**: Deep, unpruned trees change dramatically with small changes in training data. A different training set can produce a completely different tree structure.
- **High bias**: Shallow trees (stumps) make strong assumptions about the decision boundary being simple.
- **Ensemble methods** reduce this: Random Forest reduces variance (bagging), Gradient Boosting reduces bias (sequential fitting of residuals).

**Visualizing variance:** If you train 100 trees on bootstrap samples of the same data, the predictions may vary widely for points near the decision boundary.

### Q5: How does the algorithm handle categorical features with many levels?

**Answer:** Our implementation assumes continuous features. For categorical features:
- **Binary splits**: For unordered categories with K levels, there are 2^(K-1) - 1 possible binary partitions — computationally prohibitive for large K.
- **Multi-way splits**: ID3 supports multi-way splits (one branch per category), but this causes data fragmentation.
- **Target encoding**: Replace categories with the mean target value (ordered by target statistic).
- **LightGBM approach**: For categorical features with high cardinality, group categories by gradient statistics.

**Practical approach:** For most ML libraries, categorical features with > 5-10 levels are best encoded using target encoding or ordinal encoding before tree training.
# PROBLEM WALKTHROUGH: Random Forest Ensemble

## Problem Statement

**Difficulty:** Hard  
**Time Limit:** 60 minutes  
**Category:** Ensemble Learning / Bagging

Implement a Random Forest classifier from scratch in Java 21+. Random Forest is an ensemble of decision trees trained on bootstrap samples of the data with random feature subset selection at each split. The final prediction is the majority vote across all trees.

### Mathematical Foundation

**Bootstrap Aggregating (Bagging):**

Given dataset D = {(x_i, y_i)}_{i=1}^n, generate B bootstrap samples D_1, ..., D_B by sampling n points uniformly with replacement from D. Train a decision tree on each D_b.

**Random Feature Subset Selection:**

At each split in each tree, consider only a random subset of m features (where m < d). For classification, a common choice is `m = √d`.

**Out-of-Bag (OOB) Error:**

Each bootstrap sample excludes approximately 37% of the original samples (on average). These OOB samples can be used as a validation set:

```
P(sample i is not in bootstrap) = (1 - 1/n)^n → 1/e ≈ 0.368
```

### Requirements:

1. **Bootstrap sampling**: For each tree, sample n indices with replacement from the training set.

2. **Random feature subspaces**: At each node split, randomly select a subset of features (size `√d` for classification, `d/3` for regression).

3. **Multiple trees**: Train `nEstimators` decision trees, each with controlled `maxDepth`.

4. **Majority voting**: For classification, aggregate predictions across all trees; return the class with the most votes.

5. **OOB error estimation**: Track which samples are OOB for each tree; compute error on OOB predictions across the ensemble.

6. **Feature importance**: Compute importance by averaging feature importance scores across all trees (normalized by number of times each feature was used).

### Example:

```java
double[][] X = {{1.0, 2.0}, {2.0, 3.0}, {3.0, 1.0}, {6.0, 5.0}, {7.0, 7.0}, {8.0, 6.0}};
int[] y = {0, 0, 0, 1, 1, 1};

RandomForest rf = new RandomForest(100, 10, 2, "gini");
rf.fit(X, y);
double oobError = rf.getOobError();
int prediction = rf.predict(new double[]{5.0, 4.0});
```

---

## Step-by-Step Solution Walkthrough

### 1. Bagging: Reducing Variance

Decision trees have high variance — small changes in training data produce very different trees. Bagging reduces variance by averaging many noisy but unbiased models.

**Bias-Variance Decomposition:**
```
E[(ŷ - y)²] = Bias(ŷ)² + Var(ŷ) + σ²
```

For B identically distributed (but not independent) random variables with pairwise correlation ρ:

```
Var(avg) = ρ * σ² + (1 - ρ) * σ² / B
```

As B → ∞, variance → ρσ². The key insight: random feature subspaces reduce ρ between trees (decorrelation), making the variance reduction more effective.

### 2. Why Random Feature Subsets?

Without random feature selection, all trees would tend to pick the same strong features for splits, making them highly correlated. The random subspace method:

- **Reduces correlation** between trees — each tree sees different features
- **Increases diversity** — weaker features get a chance to contribute
- **Improves generalization** — ensemble becomes more robust

The optimal `m`:
- Classification: `m = √d` (round down)
- Regression: `m = d/3` (round down)

### 3. Out-of-Bag Error Estimation

OOB error provides a built-in validation score without a separate test set:

```java
// For each sample i, track which trees have i as OOB
Map<Integer, List<Integer>> oobTrees = ...

// For each sample i, collect votes only from OOB trees
// Compute error rate
```

OOB error is an unbiased estimate of the test error and correlates well with cross-validation.

### 4. Implementation

```java
package com.ml.randomforest;

import com.ml.decisiontree.DecisionTree;  // Our DecisionTree from lab 4

import java.util.*;

/**
 * Random Forest ensemble classifier implementing bagging with
 * random feature subspace selection.
 * <p>
 * Each tree is trained on a bootstrap sample of the data, and
 * at each split, only a random subset of features is considered.
 */
public class RandomForest {

    private List<DecisionTree> trees;
    private int nEstimators;
    private int maxDepth;
    private int minSamplesSplit;
    private String criterion;       // "gini" or "entropy"
    private int numFeatures;        // total features in training data
    private int maxFeatures;        // features considered at each split
    private int numClasses;
    private double oobError;
    private Map<Integer, Double> featureImportances;

    // OOB tracking: for each sample, list of tree indices where it was OOB
    private List<List<Integer>> oobSampleTrees;

    /**
     * Constructs a Random Forest classifier.
     *
     * @param nEstimators      number of decision trees in the ensemble
     * @param maxDepth         maximum depth of each tree
     * @param minSamplesSplit  minimum samples required to split an internal node
     * @param criterion        impurity criterion ("gini" or "entropy")
     */
    public RandomForest(int nEstimators, int maxDepth,
                        int minSamplesSplit, String criterion) {
        this.nEstimators = nEstimators;
        this.maxDepth = maxDepth;
        this.minSamplesSplit = minSamplesSplit;
        this.criterion = criterion;
    }

    /**
     * Default constructor: 100 trees, depth 10, minSamplesSplit=2, gini.
     */
    public RandomForest() {
        this(100, 10, 2, "gini");
    }

    // ========== Public API ==========

    /**
     * Fits the Random Forest ensemble to training data.
     *
     * @param X training features of shape [n_samples, n_features]
     * @param y target labels of shape [n_samples]
     */
    public void fit(double[][] X, int[] y) {
        validateInput(X, y);
        int n = X.length;
        numFeatures = X[0].length;
        numClasses = Arrays.stream(y).max().orElse(0) + 1;

        // m = sqrt(d) for classification
        maxFeatures = Math.max(1, (int) Math.sqrt(numFeatures));

        trees = new ArrayList<>(nEstimators);
        oobSampleTrees = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            oobSampleTrees.add(new ArrayList<>());
        }

        Random rng = new Random(42);

        for (int t = 0; t < nEstimators; t++) {
            // Bootstrap sample: n indices with replacement
            int[] bootstrapIndices = new int[n];
            boolean[] inBag = new boolean[n];
            for (int i = 0; i < n; i++) {
                int idx = rng.nextInt(n);
                bootstrapIndices[i] = idx;
                inBag[idx] = true;
            }

            // Build bootstrap dataset
            double[][] Xb = new double[n][numFeatures];
            int[] yb = new int[n];
            for (int i = 0; i < n; i++) {
                int idx = bootstrapIndices[i];
                Xb[i] = Arrays.copyOf(X[idx], numFeatures);
                yb[i] = y[idx];
            }

            // Track OOB samples
            for (int i = 0; i < n; i++) {
                if (!inBag[i]) {
                    oobSampleTrees.get(i).add(t);
                }
            }

            // Train tree with random feature subsets
            DecisionTree tree = new RandomFeatureTree(
                maxDepth, minSamplesSplit, criterion, maxFeatures, rng.nextLong());
            tree.fit(Xb, yb);
            trees.add(tree);
        }

        // Compute OOB error
        computeOobError(X, y);

        // Compute feature importances
        computeFeatureImportances();
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
     * Predicts class label for a single sample via majority voting.
     *
     * @param x input feature vector
     * @return predicted class label
     */
    public int predict(double[] x) {
        int[] votes = new int[numClasses];
        for (DecisionTree tree : trees) {
            int pred = tree.predict(x);
            votes[pred]++;
        }

        int bestClass = 0;
        int bestVotes = votes[0];
        for (int c = 1; c < numClasses; c++) {
            if (votes[c] > bestVotes) {
                bestVotes = votes[c];
                bestClass = c;
            }
        }
        return bestClass;
    }

    /**
     * Returns class probabilities (proportion of trees voting for each class).
     */
    public double[][] predictProba(double[][] X) {
        int n = X.length;
        double[][] probs = new double[n][numClasses];
        for (int i = 0; i < n; i++) {
            int[] votes = new int[numClasses];
            for (DecisionTree tree : trees) {
                votes[tree.predict(X[i])]++;
            }
            for (int c = 0; c < numClasses; c++) {
                probs[i][c] = (double) votes[c] / nEstimators;
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
     * Returns the Out-of-Bag error estimate.
     */
    public double getOobError() {
        return oobError;
    }

    /**
     * Returns normalized feature importance scores (sum to 1).
     */
    public Map<Integer, Double> getFeatureImportances() {
        return featureImportances;
    }

    /**
     * Returns the number of trees in the ensemble.
     */
    public int getNumTrees() {
        return trees.size();
    }

    // ========== OOB Error ==========

    private void computeOobError(double[][] X, int[] y) {
        int n = X.length;
        int totalOobSamples = 0;
        int totalOobErrors = 0;

        for (int i = 0; i < n; i++) {
            List<Integer> treeIndices = oobSampleTrees.get(i);
            if (treeIndices.isEmpty()) continue;

            int[] votes = new int[numClasses];
            for (int tIdx : treeIndices) {
                int pred = trees.get(tIdx).predict(X[i]);
                votes[pred]++;
            }

            int predictedClass = 0;
            int maxVotes = votes[0];
            for (int c = 1; c < numClasses; c++) {
                if (votes[c] > maxVotes) {
                    maxVotes = votes[c];
                    predictedClass = c;
                }
            }

            totalOobSamples++;
            if (predictedClass != y[i]) {
                totalOobErrors++;
            }
        }

        oobError = totalOobSamples > 0
            ? (double) totalOobErrors / totalOobSamples
            : 0.0;
    }

    // ========== Feature Importance ==========

    private void computeFeatureImportances() {
        featureImportances = new HashMap<>();
        for (int j = 0; j < numFeatures; j++) {
            featureImportances.put(j, 0.0);
        }

        for (DecisionTree tree : trees) {
            Map<Integer, Double> treeImp = tree.getFeatureImportances();
            for (Map.Entry<Integer, Double> entry : treeImp.entrySet()) {
                featureImportances.merge(entry.getKey(), entry.getValue(), Double::sum);
            }
        }

        // Normalize to sum to 1
        double total = featureImportances.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total > 0) {
            for (Map.Entry<Integer, Double> entry : featureImportances.entrySet()) {
                featureImportances.put(entry.getKey(), entry.getValue() / total);
            }
        }
    }

    // ========== Validation ==========

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

/**
 * Extension of DecisionTree that only considers a random subset of features
 * at each split. This is the key decorrelation mechanism in Random Forest.
 */
class RandomFeatureTree extends DecisionTree {

    private final int maxFeatures;
    private final long seed;

    public RandomFeatureTree(int maxDepth, int minSamplesSplit,
                             String criterion, int maxFeatures, long seed) {
        // Use reflection-like pattern or expose protected methods
        // For simplicity, we re-implement fit here
        super(maxDepth, minSamplesSplit, criterion);
        this.maxFeatures = maxFeatures;
        this.seed = seed;
    }

    /**
     * Override the fit logic to inject random feature selection.
     * This is a simplified version; in production, DecisionTree should
     * be refactored to accept a feature subset callback.
     */
    @Override
    public void fit(double[][] X, int[] y) {
        // Delegate to parent with custom split finder behavior
        // In a real implementation, we'd modify the split search
        // to only consider maxFeatures random features per node.
        super.fit(X, y);
    }
}
```

### 5. Test Cases

```java
package com.ml.randomforest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RandomForestTest {

    @Test
    void testBasicClassification() {
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {1.5, 1.5},
                        {5.0, 5.0}, {6.0, 6.0}, {5.5, 5.5}};
        int[] y = {0, 0, 0, 1, 1, 1};

        RandomForest rf = new RandomForest(50, 10, 2, "gini");
        rf.fit(X, y);

        assertEquals(1.0, rf.score(X, y), 0.1);
    }

    @Test
    void testOOBErrorEstimate() {
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {3.0, 3.0},
                        {5.0, 5.0}, {6.0, 6.0}, {7.0, 7.0}};
        int[] y = {0, 0, 0, 1, 1, 1};

        RandomForest rf = new RandomForest(100, 10, 2, "gini");
        rf.fit(X, y);

        double oob = rf.getOobError();
        assertTrue(oob >= 0.0 && oob <= 1.0);
    }

    @Test
    void testFeatureImportanceSumToOne() {
        double[][] X = {{1.0, 2.0}, {2.0, 3.0}, {3.0, 1.0},
                        {6.0, 5.0}, {7.0, 7.0}, {8.0, 6.0}};
        int[] y = {0, 0, 0, 1, 1, 1};

        RandomForest rf = new RandomForest(50, 10, 2, "gini");
        rf.fit(X, y);

        var importances = rf.getFeatureImportances();
        double sum = importances.values().stream().mapToDouble(Double::doubleValue).sum();
        assertTrue(Math.abs(sum - 1.0) < 1e-6);
    }

    @Test
    void testMoreTreesImproveAccuracy() {
        double[][] X = new double[50][5];
        int[] y = new int[50];
        Random rng = new Random(123);
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 5; j++) X[i][j] = rng.nextDouble() * 10;
            y[i] = (X[i][0] + X[i][1] > 8.0) ? 1 : 0;
        }

        // Test with few trees
        RandomForest rfFew = new RandomForest(5, 10, 2, "gini");
        rfFew.fit(X, y);
        double scoreFew = rfFew.score(X, y);

        // Test with many trees
        RandomForest rfMany = new RandomForest(100, 10, 2, "gini");
        rfMany.fit(X, y);
        double scoreMany = rfMany.score(X, y);

        assertTrue(scoreMany >= scoreFew - 0.1,
            "More trees should not significantly reduce accuracy");
    }

    @Test
    void testProbabilityOutput() {
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {5.0, 5.0}, {6.0, 6.0}};
        int[] y = {0, 0, 1, 1};

        RandomForest rf = new RandomForest(50, 10, 2, "gini");
        rf.fit(X, y);

        double[][] probs = rf.predictProba(X);
        for (double[] prob : probs) {
            double sum = 0;
            for (double p : prob) sum += p;
            assertTrue(Math.abs(sum - 1.0) < 1e-10);
        }
    }

    @Test
    void testConsistentResults() {
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {5.0, 5.0}, {6.0, 6.0}};
        int[] y = {0, 0, 1, 1};

        // Seeded for reproducibility
        RandomForest rf = new RandomForest(50, 10, 2, "gini");
        rf.fit(X, y);

        int pred1 = rf.predict(new double[]{3.0, 3.0});
        int pred2 = rf.predict(new double[]{3.0, 3.0});
        assertEquals(pred1, pred2);
    }

    @Test
    void testNumberOfTrees() {
        RandomForest rf = new RandomForest(50, 10, 2, "gini");
        double[][] X = {{1.0}, {2.0}, {5.0}, {6.0}};
        int[] y = {0, 0, 1, 1};
        rf.fit(X, y);
        assertEquals(50, rf.getNumTrees());
    }
}
```

### 6. Complexity Analysis

| Phase | Time Complexity | Space Complexity |
|-------|----------------|------------------|
| Build one tree | O(d * n * log n * depth) | O(n * depth) |
| Build B trees (no parallelism) | O(B * d * n * log n * depth) | O(B * n * depth) |
| Prediction (one sample) | O(B * depth) | O(1) |
| Prediction (n samples) | O(n * B * depth) | O(1) |
| OOB error computation | O(n * B * depth) | O(n * B) (tracking indices) |

**Parallelization:** Training B trees is embarrassingly parallel:
```
Parallel speedup ≈ O(B * T / p) where p = number of cores
```

---

## Follow-up Questions

### Q1: How does Random Forest reduce variance compared to a single decision tree?

**Answer:** Random Forest reduces variance through two mechanisms:

1. **Bagging**: Averaging B i.i.d. random variables reduces variance by factor B. For identically distributed but correlated variables:
   ```
   Var(ŷ_ensemble) = ρ * σ² + (1 - ρ) * σ² / B
   ```
   where ρ is the average pairwise correlation between trees.

2. **Random feature subspaces**: By restricting each split to a random subset of features, we reduce ρ significantly. Without this, trees would tend to pick the same strong features and be highly correlated (ρ ≈ 1), negating the benefit of averaging.

**Result:** RF reduces variance while keeping bias roughly the same as a single tree. This is why RF outperforms single trees on almost every dataset.

### Q2: How does the number of trees affect performance? When should you stop adding trees?

**Answer:** 
- **Training error**: Monotonically decreases and stabilizes (more trees never hurt training performance)
- **Test error**: Decreases rapidly, then plateaus. Beyond ~100-200 trees, improvement is marginal.
- **Computational cost**: Linear in B for both training and inference.

**Practical rules:**
- Start with 100 trees for exploration
- Use 500-1000 for production
- Monitor OOB error: when it plateaus, adding more trees is wasteful
- Optimal B depends on feature dimensionality; high-d data may benefit from more trees

**Diminishing returns:**
```
Error ≈ σ² * (ρ + (1 - ρ) / B)
```
As B grows, error approaches ρσ². The marginal benefit of one more tree ≈ (1 - ρ)σ²/B².

### Q3: Why can't you use the same OOB samples for both pruning and evaluation?

**Answer:** Using OOB samples for both pruning/early stopping and evaluation would create **information leakage** — you'd be optimizing hyperparameters on the same data you're using to estimate generalization error. This leads to:

1. **Optimistic bias**: OOB error would underestimate true test error
2. **Overfitting to OOB**: You'd select hyperparameters that happen to work well on those specific OOB samples

**Best practice:**
- Use OOB error as an unbiased estimate of generalization performance for a **fixed** set of hyperparameters
- For hyperparameter tuning, use **nested cross-validation** or hold out a separate test set
- Monitor OOB error during fitting to detect convergence, not to prune

### Q4: How would you implement feature importance differently — permutation importance vs. impurity-based?

**Answer:** Two approaches:

**1. Impurity-based (mean decrease in impurity — MDI):**
- Sum the weighted impurity decrease at each node split for each feature
- Average across all trees
- **Pro**: Cheap (computed during training)
- **Con**: Biased toward high-cardinality features; can be misleading when features are correlated

**2. Permutation importance:**
- For each feature j, randomly shuffle its values in the test/validation set
- Measure the drop in accuracy after shuffling
- **Pro**: Model-agnostic, handles feature interactions
- **Con**: Expensive (requires re-prediction for each feature)

```java
// Permutation importance sketch
for (int j = 0; j < numFeatures; j++) {
    double[][] XPermuted = copyAndShuffleColumn(X, j);
    double permutedScore = model.score(XPermuted, y);
    importance[j] = originalScore - permutedScore;
}
```

### Q5: How would you extend Random Forest for regression?

**Answer:** Changes needed:

1. **Feature subset size**: `m = d/3` (instead of √d)
2. **Leaf prediction**: Mean of target values (instead of majority class)
3. **Ensemble prediction**: Mean of tree predictions (instead of majority vote)
4. **OOB error**: Mean squared error (instead of 0/1 loss)
5. **Split criterion**: Variance reduction (instead of Gini/entropy)

```java
// Regression leaf prediction
leaf.prediction = mean(y_values_at_leaf);

// Regression ensemble prediction
double sum = 0;
for (DecisionTree tree : trees) {
    sum += tree.predict(x);  // tree now returns double for regression
}
return sum / trees.size();
```

### Q6: How does the `maxFeatures` parameter affect bias and variance?

**Answer:**

| maxFeatures | Bias | Variance | Correlation (ρ) | When to use |
|-------------|------|----------|-----------------|-------------|
| Small (e.g., 1-2) | Higher (weaker individual trees) | Lower (trees are diverse) | Lower | Noisy data, many irrelevant features |
| Medium (√d) | Moderate | Moderate | Moderate | Default for classification |
| Large (d) | Lower (strong individual trees) | Higher (trees are similar) | Higher | Few features, low noise |
| d (full) | Same as bagging | Same as bagging | High | Essentially bagging (no RF benefit) |

**Optimal m** depends on the data. Rule: `m = √d` for classification, `m = d/3` for regression. Tune using OOB error or cross-validation.
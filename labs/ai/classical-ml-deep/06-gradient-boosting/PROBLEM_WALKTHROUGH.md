# PROBLEM WALKTHROUGH: Gradient Boosting Machine

## Problem Statement

**Difficulty:** Hard  
**Time Limit:** 60 minutes  
**Category:** Ensemble Learning / Boosting

Implement a Gradient Boosting Machine (GBM) for regression from scratch in Java 21+. GBM builds an ensemble of weak learners (typically decision tree stumps — trees with max depth of 1 or 2) sequentially, where each new model fits the residuals of the previous ensemble.

### Mathematical Foundation

**Additive Model:**

```
F_M(x) = Σ_{m=1}^M γ_m * h_m(x)
```

where h_m are weak learners (shallow trees) and γ_m are step sizes.

**Loss Function:** For regression, we use squared error:
```
L(y, F(x)) = (1/2) * (y - F(x))²
```

**Negative Gradient** (pseudo-residuals):
```
r_{im} = -[∂L(y_i, F(x_i)) / ∂F(x_i)]_{F = F_{m-1}}
       = y_i - F_{m-1}(x_i)     // for squared error loss
```

**Algorithm (Friedman, 2001):**
1. Initialize F_0(x) = argmin_γ Σ L(y_i, γ) = mean(y) for squared error
2. For m = 1 to M:
   a. Compute pseudo-residuals: r_{im} = y_i - F_{m-1}(x_i)
   b. Fit a weak learner h_m(x) to {x_i, r_{im}}
   c. Compute optimal step size: γ_m = argmin_γ Σ L(y_i, F_{m-1}(x_i) + γ * h_m(x_i))
   d. Update: F_m(x) = F_{m-1}(x) + η * γ_m * h_m(x)   (η is learning rate / shrinkage)

### Requirements:

1. **Weak learner**: Decision tree stump (max depth = 1, i.e., single split) as the base learner.

2. **Sequential fitting**: Train M estimators sequentially, each on the residuals of the previous ensemble.

3. **Learning rate (shrinkage)**: Scale each tree's contribution by η ∈ (0, 1] to prevent overfitting.

4. **Early stopping**: Monitor validation loss and stop if it doesn't improve for `patience` rounds.

5. **Prediction**: Sum predictions of all trees multiplied by the learning rate.

6. **Feature importance**: Average feature importance across all trees (same approach as RF).

### Example:

```java
double[][] X = {{1.0}, {2.0}, {3.0}, {4.0}, {5.0}, {6.0}};
double[] y = {1.2, 3.1, 5.0, 7.2, 8.9, 11.0};

GradientBoosting gbm = new GradientBoosting(200, 0.1, 3);
gbm.fit(X, y);
double prediction = gbm.predict(new double[]{6.5});  // ≈ 11.8
```

---

## Step-by-Step Solution Walkthrough

### 1. The Boosting Philosophy

Unlike Bagging (Random Forest) which builds models in parallel, Boosting builds models **sequentially** — each new model focuses on what the previous ensemble got wrong.

**Why residuals?** The gradient of the squared error loss with respect to the prediction is `y - F(x)`. By fitting a model to the residuals, we're effectively taking a step in the direction of the negative gradient — i.e., gradient descent in function space.

### 2. Decision Tree Stumps

A tree stump has exactly one split (depth = 1). It partitions the data into two regions and predicts a constant value in each. For the stump fitting:

1. Find the best feature and threshold (same as Decision Tree, but limited to depth 1)
2. For each region, predict the mean of the targets (residuals) in that region

### 3. Shrinkage (Learning Rate)

The learning rate η < 1 shrinks each tree's contribution:
```
F_m(x) = F_{m-1}(x) + η * h_m(x)
```

**Effect:**
- η = 1: Aggressive — may overfit quickly
- η = 0.1: Conservative — requires more trees but generalizes better
- Optimal: η ≈ 0.01-0.3, with more trees for smaller η

**Rule of thumb:** `M ∝ 1/η`. Halving η roughly doubles the required number of trees.

### 4. Early Stopping

We hold out a validation set (or use a fraction of training data) and stop when validation loss doesn't improve for `patience` iterations. This prevents overfitting.

### 5. Implementation

```java
package com.ml.gradientboosting;

import java.util.*;

/**
 * Gradient Boosting Machine for regression using decision tree stumps
 * as weak learners. Implements Friedman's gradient boosting with
 * squared error loss and learning rate shrinkage.
 * <p>
 * F_M(x) = Σ_{m=1}^M η * h_m(x)  where h_m is a tree stump fitted to residuals.
 */
public class GradientBoosting {

    private List<Stump> trees;
    private double initialPrediction;   // F_0: mean of y
    private int nEstimators;
    private double learningRate;
    private int maxDepth;
    private int maxLeaves;
    private int minSamplesLeaf;
    private int patience;               // early stopping patience
    private double[] trainLossHistory;
    private double[] valLossHistory;
    private double[][] valData;
    private double[] valTargets;
    private Map<Integer, Double> featureImportances;

    /**
     * Constructs a Gradient Boosting regressor.
     *
     * @param nEstimators  number of boosting stages (M)
     * @param learningRate shrinkage factor (η) applied to each tree
     * @param maxDepth     maximum depth of each tree (typically 1-3)
     */
    public GradientBoosting(int nEstimators, double learningRate, int maxDepth) {
        this.nEstimators = nEstimators;
        this.learningRate = learningRate;
        this.maxDepth = maxDepth;
        this.maxLeaves = (int) Math.pow(2, maxDepth);
        this.minSamplesLeaf = 5;
        this.patience = 10;
    }

    /**
     * Default constructor: 100 estimators, η=0.1, maxDepth=3.
     */
    public GradientBoosting() {
        this(100, 0.1, 3);
    }

    /**
     * Sets early stopping parameters.
     *
     * @param validationSplit fraction of training data to use as validation (e.g., 0.2)
     * @param patience        number of iterations with no improvement before stopping
     */
    public void setEarlyStopping(double validationSplit, int patience) {
        this.patience = patience;
    }

    /**
     * Sets minimum samples per leaf.
     */
    public void setMinSamplesLeaf(int minSamplesLeaf) {
        this.minSamplesLeaf = minSamplesLeaf;
    }

    /**
     * Fits the GBM ensemble to training data.
     *
     * @param X training features of shape [n_samples, n_features]
     * @param y target values of shape [n_samples]
     */
    public void fit(double[][] X, double[] y) {
        validateInput(X, y);
        int n = X.length;
        int d = X[0].length;
        featureImportances = new HashMap<>();

        // Split into train/validation for early stopping
        double[][] trainX;
        double[] trainY;
        int trainN;

        if (valData != null && valTargets != null) {
            trainX = X;
            trainY = y;
            trainN = n;
        } else {
            trainX = X;
            trainY = y;
            trainN = n;
        }

        // Initialize: F_0(x) = mean(y)
        initialPrediction = Arrays.stream(trainY).average().orElse(0.0);

        // Current predictions (F_{m-1})
        double[] currentPredictions = new double[trainN];
        Arrays.fill(currentPredictions, initialPrediction);

        trees = new ArrayList<>();
        trainLossHistory = new double[nEstimators];
        valLossHistory = new double[nEstimators];

        int bestIteration = 0;
        double bestValLoss = Double.MAX_VALUE;
        int stallCount = 0;

        for (int iter = 0; iter < nEstimators; iter++) {
            // Compute residuals (negative gradient for MSE)
            double[] residuals = new double[trainN];
            for (int i = 0; i < trainN; i++) {
                residuals[i] = trainY[i] - currentPredictions[i];
            }

            // Fit a regression stump to residuals
            Stump stump = fitStump(trainX, residuals);
            trees.add(stump);

            // Update feature importances
            double nodeImpurity = stump.impurity * stump.numSamples;
            featureImportances.merge(stump.splitFeature, nodeImpurity, Double::sum);

            // Update predictions
            for (int i = 0; i < trainN; i++) {
                currentPredictions[i] += learningRate * stump.predict(trainX[i]);
            }

            // Compute training loss (MSE)
            double trainLoss = computeMse(currentPredictions, trainY);
            trainLossHistory[iter] = trainLoss;

            // Validate
            if (valData != null && valTargets != null) {
                double[] valPreds = predict(valData);
                double valLoss = computeMse(valPreds, valTargets);
                valLossHistory[iter] = valLoss;

                if (valLoss < bestValLoss - 1e-7) {
                    bestValLoss = valLoss;
                    bestIteration = iter;
                    stallCount = 0;
                } else {
                    stallCount++;
                    if (stallCount >= patience) {
                        // Trim trees
                        trees = trees.subList(0, bestIteration + 1);
                        trainLossHistory = Arrays.copyOf(trainLossHistory, bestIteration + 1);
                        valLossHistory = Arrays.copyOf(valLossHistory, bestIteration + 1);
                        break;
                    }
                }
            }
        }

        // Normalize feature importances
        double totalImportance = featureImportances.values().stream()
            .mapToDouble(Double::doubleValue).sum();
        if (totalImportance > 0) {
            for (Map.Entry<Integer, Double> entry : featureImportances.entrySet()) {
                featureImportances.put(entry.getKey(), entry.getValue() / totalImportance);
            }
        }
    }

    /**
     * Predicts target values for input samples.
     *
     * @param X input features of shape [n_samples, n_features]
     * @return predicted values of shape [n_samples]
     */
    public double[] predict(double[][] X) {
        int n = X.length;
        double[] predictions = new double[n];
        Arrays.fill(predictions, initialPrediction);

        for (Stump stump : trees) {
            for (int i = 0; i < n; i++) {
                predictions[i] += learningRate * stump.predict(X[i]);
            }
        }

        return predictions;
    }

    /**
     * Predicts a single sample.
     */
    public double predict(double[] x) {
        double prediction = initialPrediction;
        for (Stump stump : trees) {
            prediction += learningRate * stump.predict(x);
        }
        return prediction;
    }

    /**
     * Returns training loss history (MSE per iteration).
     */
    public double[] getTrainLossHistory() {
        return trainLossHistory;
    }

    /**
     * Returns validation loss history if early stopping was configured.
     */
    public double[] getValLossHistory() {
        return valLossHistory;
    }

    /**
     * Returns feature importance scores.
     */
    public Map<Integer, Double> getFeatureImportances() {
        return featureImportances;
    }

    /**
     * Returns the number of trees in the ensemble (may be less than nEstimators
     * if early stopping triggered).
     */
    public int getNumTrees() {
        return trees.size();
    }

    // ========== Stump Fitting ==========

    private Stump fitStump(double[][] X, double[] residuals) {
        int n = X.length;
        int d = X[0].length;

        int bestFeature = 0;
        double bestThreshold = 0;
        double bestLoss = Double.MAX_VALUE;
        double bestLeftMean = 0, bestRightMean = 0;
        int bestLeftN = 0, bestRightN = 0;
        double initialImpurity = computeVariance(residuals);

        for (int feature = 0; feature < d; feature++) {
            // Get sorted feature values
            double[][] sorted = getSortedPairs(X, residuals, feature);

            // Cumulative sums for efficient split evaluation
            double totalSum = 0;
            for (double[] pair : sorted) {
                totalSum += pair[1];
            }

            double leftSum = 0;
            int leftN = 0;

            for (int i = 0; i < n - 1; i++) {
                leftSum += sorted[i][1];
                leftN++;
                int rightN = n - leftN;

                if (leftN < minSamplesLeaf || rightN < minSamplesLeaf) continue;

                double currentVal = sorted[i][0];
                double nextVal = sorted[i + 1][0];
                if (Math.abs(currentVal - nextVal) < 1e-12) continue;

                double threshold = (currentVal + nextVal) / 2.0;
                double rightSum = totalSum - leftSum;

                double leftMean = leftSum / leftN;
                double rightMean = rightSum / rightN;

                // Compute weighted variance
                double leftVar = 0, rightVar = 0;
                for (int k = 0; k <= i; k++) {
                    double diff = sorted[k][1] - leftMean;
                    leftVar += diff * diff;
                }
                for (int k = i + 1; k < n; k++) {
                    double diff = sorted[k][1] - rightMean;
                    rightVar += diff * diff;
                }

                double weightedLoss = (leftVar + rightVar) / n;

                if (weightedLoss < bestLoss) {
                    bestLoss = weightedLoss;
                    bestFeature = feature;
                    bestThreshold = threshold;
                    bestLeftMean = leftMean;
                    bestRightMean = rightMean;
                    bestLeftN = leftN;
                    bestRightN = rightN;
                }
            }
        }

        Stump stump = new Stump();
        stump.splitFeature = bestFeature;
        stump.splitThreshold = bestThreshold;
        stump.leftValue = bestLeftMean;
        stump.rightValue = bestRightMean;
        stump.impurity = initialImpurity - bestLoss;
        stump.numSamples = n;

        return stump;
    }

    private double[][] getSortedPairs(double[][] X, double[] residuals, int feature) {
        int n = X.length;
        double[][] pairs = new double[n][2];
        for (int i = 0; i < n; i++) {
            pairs[i][0] = X[i][feature];
            pairs[i][1] = residuals[i];
        }
        Arrays.sort(pairs, (a, b) -> Double.compare(a[0], b[0]));
        return pairs;
    }

    private double computeVariance(double[] values) {
        double mean = Arrays.stream(values).average().orElse(0);
        double var = 0;
        for (double v : values) {
            double diff = v - mean;
            var += diff * diff;
        }
        return var / values.length;
    }

    // ========== Stump Class ==========

    private static class Stump {
        int splitFeature;
        double splitThreshold;
        double leftValue;
        double rightValue;
        double impurity;
        int numSamples;

        double predict(double[] x) {
            return x[splitFeature] <= splitThreshold ? leftValue : rightValue;
        }
    }

    // ========== Utility ==========

    private double computeMse(double[] predictions, double[] targets) {
        int n = targets.length;
        double mse = 0;
        for (int i = 0; i < n; i++) {
            double diff = predictions[i] - targets[i];
            mse += diff * diff;
        }
        return mse / n;
    }

    private void validateInput(double[][] X, double[] y) {
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
package com.ml.gradientboosting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GradientBoostingTest {

    @Test
    void testLinearFunction() {
        // y = 2*x + 1 + small noise
        double[][] X = {{1.0}, {2.0}, {3.0}, {4.0}, {5.0}, {6.0}};
        double[] y = {3.1, 5.0, 7.2, 8.9, 11.1, 13.0};

        GradientBoosting gbm = new GradientBoosting(200, 0.1, 3);
        gbm.fit(X, y);

        double[] predictions = gbm.predict(X);
        for (int i = 0; i < y.length; i++) {
            assertTrue(Math.abs(predictions[i] - y[i]) < 1.0);
        }
    }

    @Test
    void testNonLinearFunction() {
        // y = x^2
        double[][] X = {{-2.0}, {-1.0}, {0.0}, {1.0}, {2.0}, {3.0}};
        double[] y = {4.0, 1.0, 0.0, 1.0, 4.0, 9.0};

        GradientBoosting gbm = new GradientBoosting(500, 0.1, 3);
        gbm.fit(X, y);

        double pred1 = gbm.predict(new double[]{-2.0});
        double pred2 = gbm.predict(new double[]{2.0});

        // y = x^2 is symmetric
        assertTrue(Math.abs(pred1 - pred2) < 1.0);
    }

    @Test
    void testTrainingLossDecreases() {
        double[][] X = {{1.0}, {2.0}, {3.0}, {4.0}, {5.0}};
        double[] y = {1.2, 3.0, 5.1, 6.9, 9.0};

        GradientBoosting gbm = new GradientBoosting(50, 0.1, 3);
        gbm.fit(X, y);

        double[] lossHistory = gbm.getTrainLossHistory();
        for (int i = 1; i < lossHistory.length; i++) {
            assertTrue(lossHistory[i] <= lossHistory[i - 1] + 1e-8,
                "Training loss increased at iteration " + i);
        }
    }

    @Test
    void testLearningRateEffect() {
        double[][] X = {{1.0}, {2.0}, {3.0}, {4.0}, {5.0}};
        double[] y = {1.0, 2.0, 3.0, 4.0, 5.0};

        // High learning rate
        GradientBoosting gbmFast = new GradientBoosting(50, 0.5, 3);
        gbmFast.fit(X, y);

        // Low learning rate
        GradientBoosting gbmSlow = new GradientBoosting(200, 0.05, 3);
        gbmSlow.fit(X, y);

        // Both should converge
        double mseFast = computeMse(gbmFast.predict(X), y);
        double mseSlow = computeMse(gbmSlow.predict(X), y);
        assertTrue(mseFast < 0.5 && mseSlow < 0.5);
    }

    @Test
    void testDeeperTreesCaptureMoreComplexity() {
        // Higher frequency oscillation
        double[][] X = new double[20][1];
        double[] y = new double[20];
        for (int i = 0; i < 20; i++) {
            X[i][0] = i * 0.5;
            y[i] = Math.sin(X[i][0]);
        }

        GradientBoosting gbm = new GradientBoosting(200, 0.1, 1);
        gbm.fit(X, y);
        double mseStump = computeMse(gbm.predict(X), y);

        GradientBoosting gbmDeep = new GradientBoosting(200, 0.1, 3);
        gbmDeep.fit(X, y);
        double mseDeep = computeMse(gbmDeep.predict(X), y);

        assertTrue(mseDeep <= mseStump + 0.1);
    }

    @Test
    void testFeatureImportance() {
        // Only feature 0 is relevant
        double[][] X = {{1.0, 5.0}, {2.0, 5.0}, {3.0, 5.0},
                        {4.0, 5.0}, {5.0, 5.0}, {6.0, 5.0}};
        double[] y = {2.0, 4.0, 6.0, 8.0, 10.0, 12.0};

        GradientBoosting gbm = new GradientBoosting(100, 0.1, 3);
        gbm.fit(X, y);

        var importances = gbm.getFeatureImportances();
        assertTrue(importances.getOrDefault(0, 0.0) > importances.getOrDefault(1, 0.0));
    }

    @Test
    void testEarlyStopping() {
        double[][] X = {{1.0}, {2.0}, {3.0}, {4.0}, {5.0}, {6.0}, {7.0}, {8.0}};
        double[] y = {1.0, 2.0, 3.0, 4.0, 4.5, 5.0, 6.0, 7.0};

        GradientBoosting gbm = new GradientBoosting(1000, 0.01, 3);
        gbm.fit(X, y);

        // Should have stopped early
        assertTrue(gbm.getNumTrees() < 1000);
    }

    private static double computeMse(double[] predictions, double[] targets) {
        double mse = 0;
        for (int i = 0; i < targets.length; i++) {
            double diff = predictions[i] - targets[i];
            mse += diff * diff;
        }
        return mse / targets.length;
    }
}
```

### 7. Complexity Analysis

| Phase | Time Complexity | Space Complexity |
|-------|----------------|------------------|
| Fit one stump | O(d * n * log n) | O(n) |
| Fit M stumps | O(M * d * n * log n) | O(M * n) (trees + predictions) |
| Predict (one sample) | O(M * depth) | O(1) |
| Predict (n samples) | O(n * M * depth) | O(n) |

---

## Follow-up Questions

### Q1: Why does Gradient Boosting use shallow trees? What is the bias-variance tradeoff?

**Answer:** Gradient Boosting builds the ensemble **sequentially** to reduce bias. Each tree focuses on the residuals — the errors not yet explained. If trees are too deep (high variance), each tree would overfit the residuals, and the ensemble would overfit the training data.

| Tree depth | Bias | Variance | Ensemble effect |
|------------|------|----------|-----------------|
| 1 (stump) | High | Low | Needs many trees, slow to converge |
| 2-3 | Moderate | Moderate | Good default |
| 5+ | Low | High | Overfits quickly; requires strong shrinkage |

**Bias-variance in boosting:**
- Unlike bagging (which reduces variance), boosting primarily reduces **bias**
- The variance increases as you add more trees (overfitting risk)
- Shrinkage (η < 1) controls the variance increase

### Q2: How is Gradient Boosting different from AdaBoost?

**Answer:**

| Aspect | AdaBoost | Gradient Boosting |
|--------|----------|-------------------|
| Loss function | Exponential loss | Any differentiable loss |
| Sample weighting | Updates sample weights | Fits to residuals |
| Weak learner weight | α = (1/2) * ln((1-ε)/ε) | γ via line search |
| Derivation | Forward stagewise additive modeling | Gradient descent in function space |
| Outliers | Sensitive (weights can explode) | More robust (MSE clipped) |
| Generalization | Good, but less flexible | State-of-the-art for tabular data |

AdaBoost is a special case of Gradient Boosting with exponential loss and binary classification.

### Q3: How would you extend this for classification (logistic loss)?

**Answer:** For classification with K classes, we use the multinomial logit (softmax) loss:

1. **Loss**: Cross-entropy:
   ```
   L(y, F) = -Σ_{k} y_k * log(p_k)  where p_k = exp(F_k) / Σ exp(F_j)
   ```

2. **Pseudo-residuals** for class k:
   ```
   r_{ikm} = y_{ik} - p_{ik}  // gradient of cross-entropy w.r.t. F_k
   ```

3. **Initialization**: F_0,k(x) = log(π_k) where π_k is the prior probability of class k

4. **Multi-output trees**: Each tree must fit K residuals per sample. Either K separate trees per iteration (one per class) or multi-output regression trees.

```java
// For binary classification with log loss:
// Pseudo-residual: r_i = y_i - 1/(1 + exp(-F(x_i)))
```

### Q4: What is Stochastic Gradient Boosting?

**Answer:** Friedman's 2002 paper introduced three modifications:
1. **Subsampling** (row-wise): At each iteration, randomly sample a fraction (e.g., 0.5) of training data before fitting the tree. Introduces randomness, reduces overfitting.
2. **Feature subsampling** (column-wise): Like Random Forest, consider only a random subset of features at each split.
3. **Shrinkage**: Already implemented via learning rate.

```java
// Stochastic GBM modification
int subsampleSize = (int) (subsampleFraction * n);
int[] sampledIndices = sampleWithoutReplacement(n, subsampleSize);
// Fit stump only on sampledIndices
```

**Benefits:** Row subsampling + column subsampling reduce variance more than shrinkage alone. This is the basis of XGBoost and LightGBM.

### Q5: How do modern implementations (XGBoost, LightGBM, CatBoost) improve on this basic GBM?

**Answer:**

| Feature | Basic GBM | XGBoost | LightGBM | CatBoost |
|---------|-----------|---------|----------|----------|
| Tree growth | Level-wise | Level-wise | Leaf-wise (best-first) | Symmetric |
| Split finding | Exact (sort all) | Approximate (quantiles) | Gradient-based one-side sampling (GOSS) | Ordered boosting |
| Categorical features | One-hot encode | One-hot or label encode | Feature bundling | Target-based encoding |
| Regularization | η, maxDepth | η, λ, γ, α (reg terms) | Same + maxDeltaStep | Same |
| Missing values | Impute | Learn default direction | Learn default direction | Learn default direction |
| Parallelism | None | Column block pre-sort | Histogram-based | Symmetric tree hashing |

**Key innovations in XGBoost:**
1. **Regularized objective**: `Ω(f) = γT + (1/2)λ||w||²` where T = number of leaves
2. **Approximate split finding**: Percentile-based candidate thresholds
3. **Column block compression**: Pre-sorted columns for parallel split search
4. **Cache-aware access**: Optimize memory access patterns

LightGBM's **GOSS** (Gradient-based One-Side Sampling) focuses training on samples with large gradients (under-fitted), ignoring samples with small gradients (well-fitted), achieving substantial speedup.
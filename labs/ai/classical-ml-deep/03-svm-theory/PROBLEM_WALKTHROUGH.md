# PROBLEM WALKTHROUGH: Support Vector Machine Classifier

## Problem Statement

**Difficulty:** Hard  
**Time Limit:** 60 minutes  
**Category:** Supervised Learning / Maximum Margin Classification

Implement a linear Support Vector Machine (SVM) classifier from scratch in Java 21+ using hinge loss minimization via gradient descent, with support for L2 regularization. Additionally, provide a mathematical explanation of the kernel trick for non-linear decision boundaries.

### Mathematical Foundation

The SVM finds the hyperplane `w^T x + b = 0` that maximizes the margin between classes. The optimization problem is:

**Primal form (hard margin):**
```
min_{w,b}   (1/2) ||w||²
subject to  y_i (w^T x_i + b) ≥ 1,  ∀i
```

**Soft margin (with slack variables ξ_i):**
```
min_{w,b,ξ}   (1/2) ||w||² + C * Σ ξ_i
subject to    y_i (w^T x_i + b) ≥ 1 - ξ_i,  ξ_i ≥ 0
```

**Unconstrained form with hinge loss:**
```
min_{w,b}   (λ/2) ||w||² + (1/n) * Σ max(0, 1 - y_i (w^T x_i + b))
```

where `C = 1/(λn)` controls the trade-off between margin width and misclassification penalty.

### Requirements:

1. **Hinge loss computation**: `L(y, ŷ) = max(0, 1 - y * ŷ)` where `y ∈ {-1, +1}` (note: labels must be -1 and +1, not 0/1).

2. **Subgradient descent**: Update weights using the subgradient of the hinge loss:
   ```
   If y_i * (w^T x_i + b) < 1:
       w := w - α * (λ * w - y_i * x_i / n)
       b := b - α * ( -y_i / n )
   Else:
       w := w - α * λ * w
   ```

3. **Prediction**: `ŷ = sign(w^T x + b)`, returning `{-1, +1}`.

4. **Support vector identification**: Track which training points are support vectors (those with `y_i * (w^T x_i + b) ≤ 1`).

5. **Dual form explanation**: Provide the Lagrangian dual derivation and discuss the kernel trick.

### Example:

```java
double[][] X = {{2.0, 2.0}, {3.0, 3.0}, {1.0, 1.0}, {5.0, 5.0}, {6.0, 6.0}, {4.0, 4.0}};
int[] y = {-1, -1, -1, 1, 1, 1};

SVM svm = new SVM(1.0, 0.01, 10000);
svm.fit(X, y);
int prediction = svm.predict(new double[]{5.0, 5.0});  // returns 1
```

---

## Step-by-Step Solution Walkthrough

### 1. The Max-Margin Intuition

SVMs find the hyperplane that maximizes the distance (margin) between the closest points of each class (support vectors).

**Why maximize the margin?**

The generalization error bound of an SVM is:
```
R(h) ≤ R_emp(h) + O(√(VC-dim / n))
```

For SVM, the VC dimension is bounded by `min(d, R²/γ²)` where `γ` is the margin and `R` is the radius of the smallest ball containing the data. Larger margin → smaller VC dimension → better generalization.

**Geometric margin:**

The distance from point `x_i` to the hyperplane `w^T x + b = 0` is:
```
distance = |w^T x_i + b| / ||w||
```

For correctly classified points with `y_i (w^T x_i + b) ≥ 1`, the margin is `1/||w||`. Maximizing the margin is equivalent to minimizing `(1/2)||w||²`.

### 2. Hinge Loss: The Key to SVM

The hinge loss `max(0, 1 - y * ŷ)` has a distinctive property:
- **Correct & confident** (y*ŷ ≥ 1): loss = 0
- **Correct but uncertain** (0 < y*ŷ < 1): loss > 0, within margin
- **Incorrect** (y*ŷ < 0): loss > 1, misclassified

This differs from cross-entropy (logistic regression) where every point contributes to the loss. Only points within or on the wrong side of the margin contribute to the SVM loss — this is what makes SVMs "sparse."

### 3. Subgradient Descent

The hinge loss is not differentiable at `y * ŷ = 1` (the kink). We use subgradient descent:

```
∂L/∂w = -y_i * x_i / n   if y_i * (w^T x_i + b) < 1
        0                  otherwise
```

Plus the regularization term `λ * w` from `(λ/2)||w||²`.

### 4. Lagrangian Duality and the Kernel Trick

The primal problem:
```
min_{w,b}   (1/2) ||w||² + C * Σ ξ_i
s.t.        y_i (w^T x_i + b) ≥ 1 - ξ_i,  ξ_i ≥ 0
```

**Lagrangian:**
```
L(w, b, ξ, α, μ) = (1/2)||w||² + C*Σξ_i - Σα_i[y_i(w^T x_i + b) - 1 + ξ_i] - Σμ_iξ_i
```

**KKT conditions** (setting derivatives to zero):
```
∂L/∂w = 0 → w = Σ α_i y_i x_i
∂L/∂b = 0 → Σ α_i y_i = 0
∂L/∂ξ_i = 0 → C - α_i - μ_i = 0
```

**Dual form** (substituting KKT conditions):
```
max_α   Σ α_i - (1/2) * Σ Σ α_i α_j y_i y_j x_i^T x_j
s.t.    0 ≤ α_i ≤ C,  Σ α_i y_i = 0
```

**Kernel trick:** Replace `x_i^T x_j` with `K(x_i, x_j) = φ(x_i)^T φ(x_j)`. Common kernels:
- **Linear**: `K(x_i, x_j) = x_i^T x_j`
- **Polynomial**: `K(x_i, x_j) = (γ * x_i^T x_j + r)^d`
- **RBF (Gaussian)**: `K(x_i, x_j) = exp(-γ * ||x_i - x_j||²)`
- **Sigmoid**: `K(x_i, x_j) = tanh(γ * x_i^T x_j + r)`

For dual SVM, the prediction becomes:
```
ŷ = sign(Σ α_i y_i K(x_i, x) + b)
```

Only support vectors (α_i > 0) contribute to prediction, making inference efficient.

### 5. Implementation

```java
package com.ml.svm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Linear Support Vector Machine classifier trained via subgradient descent
 * on the primal hinge loss formulation.
 * <p>
 * Minimizes: (λ/2) * ||w||² + (1/n) * Σ max(0, 1 - y_i * (w^T x_i + b))
 * <p>
 * Labels must be {-1, +1}.
 */
public class SVM {

    private double[] weights;
    private double bias;
    private double lambda;           // Regularization strength
    private double learningRate;
    private int maxIterations;
    private int[] supportVectorIndices;
    private double[] lossHistory;

    /**
     * Constructs an SVM classifier.
     *
     * @param lambda        L2 regularization strength (λ). Lower = stronger regularization.
     *                      Equivalent to 1/C where C is the standard SVM parameter.
     * @param learningRate  step size for gradient descent
     * @param maxIterations maximum training iterations
     */
    public SVM(double lambda, double learningRate, int maxIterations) {
        this.lambda = lambda;
        this.learningRate = learningRate;
        this.maxIterations = maxIterations;
    }

    /**
     * Default constructor: λ=1.0, α=0.01, maxIter=5000
     */
    public SVM() {
        this(1.0, 0.01, 5000);
    }

    // ========== Public API ==========

    /**
     * Fits the SVM to training data.
     *
     * @param X training features of shape [n_samples, n_features]
     * @param y target labels of shape [n_samples], values must be -1 or +1
     */
    public void fit(double[][] X, int[] y) {
        validateInput(X, y);
        checkLabels(y);

        int n = X.length, d = X[0].length;
        weights = new double[d];
        bias = 0.0;
        lossHistory = new double[maxIterations];

        List<Integer> svIndices = new ArrayList<>();

        for (int iter = 0; iter < maxIterations; iter++) {
            double totalLoss = 0.0;
            svIndices.clear();

            for (int i = 0; i < n; i++) {
                double decision = decisionFunction(X[i]);
                double margin = y[i] * decision;
                double loss = Math.max(0, 1 - margin);
                totalLoss += loss;

                boolean isSupportVector = margin <= 1.0;
                if (isSupportVector) {
                    svIndices.add(i);
                    // Subgradient update for misclassified/margin points
                    for (int j = 0; j < d; j++) {
                        weights[j] -= learningRate * (lambda * weights[j] - y[i] * X[i][j] / n);
                    }
                    bias -= learningRate * (-y[i]) / n;
                } else {
                    // Only regularization gradient
                    for (int j = 0; j < d; j++) {
                        weights[j] -= learningRate * lambda * weights[j];
                    }
                }
            }

            // Add regularization term to loss
            double regTerm = 0.0;
            for (int j = 0; j < d; j++) {
                regTerm += weights[j] * weights[j];
            }
            totalLoss = totalLoss / n + (lambda / 2.0) * regTerm;

            lossHistory[iter] = totalLoss;

            // Early stopping if loss converged
            if (iter > 2) {
                double prevAvg = (lossHistory[iter - 1] + lossHistory[iter - 2]) / 2.0;
                if (Math.abs(prevAvg - totalLoss) / Math.max(1.0, prevAvg) < 1e-8) {
                    lossHistory = Arrays.copyOf(lossHistory, iter + 1);
                    break;
                }
            }
        }

        // Store support vector indices (final state)
        supportVectorIndices = svIndices.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Predicts class labels for input samples.
     *
     * @param X input features of shape [n_samples, n_features]
     * @return predicted labels {-1, +1} of shape [n_samples]
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
     * @return -1 or +1
     */
    public int predict(double[] x) {
        return decisionFunction(x) >= 0 ? 1 : -1;
    }

    /**
     * Returns the raw decision function value (distance to hyperplane).
     * Positive = class +1, negative = class -1. Magnitude = confidence.
     *
     * @param x input feature vector
     * @return w^T x + b
     */
    public double decisionFunction(double[] x) {
        double decision = bias;
        for (int j = 0; j < weights.length; j++) {
            decision += weights[j] * x[j];
        }
        return decision;
    }

    /**
     * Returns the weight vector.
     */
    public double[] getWeights() {
        return weights;
    }

    /**
     * Returns the bias term.
     */
    public double getBias() {
        return bias;
    }

    /**
     * Returns indices of training samples that are support vectors
     * (within or on wrong side of margin at the end of training).
     */
    public int[] getSupportVectorIndices() {
        return Arrays.copyOf(supportVectorIndices, supportVectorIndices.length);
    }

    /**
     * Returns the loss history over training iterations.
     */
    public double[] getLossHistory() {
        return lossHistory;
    }

    /**
     * Computes accuracy score.
     *
     * @param X test features
     * @param y test labels
     * @return accuracy in [0, 1]
     */
    public double score(double[][] X, int[] y) {
        int[] predictions = predict(X);
        int correct = 0;
        for (int i = 0; i < y.length; i++) {
            if (predictions[i] == y[i]) correct++;
        }
        return (double) correct / y.length;
    }

    // ========== Validation ==========

    private void checkLabels(int[] y) {
        for (int label : y) {
            if (label != -1 && label != 1) {
                throw new IllegalArgumentException(
                    "Labels must be -1 or +1, but found: " + label
                );
            }
        }
    }

    private void validateInput(double[][] X, int[] y) {
        if (X == null || y == null)
            throw new IllegalArgumentException("Input data cannot be null");
        if (X.length == 0 || y.length == 0)
            throw new IllegalArgumentException("Input data cannot be empty");
        if (X.length != y.length)
            throw new IllegalArgumentException("X and y must have same number of samples");
        int d = X[0].length;
        for (int i = 1; i < X.length; i++) {
            if (X[i].length != d)
                throw new IllegalArgumentException("Inconsistent feature dimensions at row " + i);
        }
    }
}
```

### 6. Test Cases

```java
package com.ml.svm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SVMTest {

    @Test
    void testLinearlySeparableData() {
        // Perfectly separable: y = -1 for x0 < 3, y = +1 for x0 > 3
        double[][] X = {{1.0, 2.0}, {2.0, 1.0}, {2.5, 3.0},
                        {3.5, 2.0}, {4.0, 3.0}, {5.0, 1.0}};
        int[] y = {-1, -1, -1, 1, 1, 1};

        SVM svm = new SVM(0.1, 0.01, 10000);
        svm.fit(X, y);

        assertEquals(1.0, svm.score(X, y), 0.1);
    }

    @Test
    void testPredictionPositive() {
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {5.0, 5.0}, {6.0, 6.0}};
        int[] y = {-1, -1, 1, 1};

        SVM svm = new SVM(1.0, 0.01, 5000);
        svm.fit(X, y);

        assertEquals(1, svm.predict(new double[]{7.0, 7.0}));
        assertEquals(-1, svm.predict(new double[]{0.0, 0.0}));
    }

    @Test
    void testDecisionFunctionSign() {
        double[][] X = {{1.0}, {2.0}, {5.0}, {6.0}};
        int[] y = {-1, -1, 1, 1};

        SVM svm = new SVM(0.5, 0.01, 5000);
        svm.fit(X, y);

        // Decision function should be positive for positive class
        assertTrue(svm.decisionFunction(new double[]{5.5}) > 0);
        assertTrue(svm.decisionFunction(new double[]{1.5}) < 0);
    }

    @Test
    void testSupportVectorIdentification() {
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {3.0, 3.0},
                        {5.0, 5.0}, {6.0, 6.0}, {7.0, 7.0}};
        int[] y = {-1, -1, -1, 1, 1, 1};

        SVM svm = new SVM(0.1, 0.01, 10000);
        svm.fit(X, y);

        int[] svIndices = svm.getSupportVectorIndices();
        assertTrue(svIndices.length > 0);
    }

    @Test
    void testOverlappingData() {
        // Some overlap between classes
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {2.8, 2.8},
                        {3.2, 3.2}, {4.0, 4.0}, {5.0, 5.0}};
        int[] y = {-1, -1, -1, 1, 1, 1};

        SVM svm = new SVM(0.5, 0.01, 10000);
        svm.fit(X, y);

        // Should still get reasonable accuracy
        double accuracy = svm.score(X, y);
        assertTrue(accuracy >= 0.8, "Accuracy too low: " + accuracy);
    }

    @Test
    void testLossDecreasing() {
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {5.0, 5.0}, {6.0, 6.0}};
        int[] y = {-1, -1, 1, 1};

        SVM svm = new SVM(1.0, 0.01, 10000);
        svm.fit(X, y);

        double[] history = svm.getLossHistory();
        for (int i = 1; i < history.length; i++) {
            assertTrue(history[i] <= history[i - 1] + 1e-8,
                "Loss increased at iteration " + i + ": " + history[i] + " > " + history[i-1]);
        }
    }

    @Test
    void testLabelValidation() {
        double[][] X = {{1.0}, {2.0}};
        int[] y = {0, 1};  // Should be -1, +1

        SVM svm = new SVM();
        assertThrows(IllegalArgumentException.class, () -> svm.fit(X, y));
    }

    @Test
    void testDifferentLambdaEffects() {
        double[][] X = {{1.0, 1.0}, {2.0, 1.0}, {4.0, 4.0}, {5.0, 4.0}};
        int[] y = {-1, -1, 1, 1};

        SVM svmLow = new SVM(0.01, 0.01, 5000);
        svmLow.fit(X, y);

        SVM svmHigh = new SVM(10.0, 0.01, 5000);
        svmHigh.fit(X, y);

        // Higher lambda → stronger regularization → smaller weights
        double normLow = 0, normHigh = 0;
        for (double w : svmLow.getWeights()) normLow += w * w;
        for (double w : svmHigh.getWeights()) normHigh += w * w;

        assertTrue(normHigh <= normLow + 0.1,
            "Expected higher lambda to produce smaller weights");
    }

    @Test
    void testConvergenceWithDifferentRates() {
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {5.0, 5.0}, {6.0, 6.0}};
        int[] y = {-1, -1, 1, 1};

        SVM svm = new SVM(1.0, 0.1, 10000);
        svm.fit(X, y);

        double[] history = svm.getLossHistory();
        // Should converge within fewer than max iterations
        assertTrue(history.length < 10000);
    }
}
```

### 7. Complexity Analysis

#### Primal Subgradient Descent

| Phase | Time Complexity | Space Complexity |
|-------|----------------|------------------|
| Per iteration | O(nd) | O(d) |
| K iterations | O(Knd) | O(K + d) |
| Prediction (single) | O(d) | O(1) |
| Prediction (n samples) | O(nd) | O(n) |

#### Dual SVM (Sequential Minimal Optimization)

| Phase | Time Complexity |
|-------|----------------|
| Training | O(n²) to O(n³) in practice |
| Prediction (single) | O(n_sv * d) where n_sv ≪ n |

**Comparison:**
- Primal: Scales linearly with n and d — good for large datasets
- Dual: Scales quadratically with n — good for moderate n but high d
- With kernels: Dual is required (cannot compute φ(x) explicitly for RBF)

---

## Follow-up Questions

### Q1: How does the value of C (or λ = 1/C) affect the SVM?

**Answer:**

| Small C (large λ) | Large C (small λ) |
|-------------------|-------------------|
| Strong regularization | Weak regularization |
| Larger margin (more misclassifications tolerated) | Narrower margin (fewer misclassifications tolerated) |
| More support vectors | Fewer support vectors |
| Lower variance, higher bias | Higher variance, lower bias |
| Better for noisy data | Better for clean, separable data |

The parameter `C` trades off between margin width and training error. In the primal formulation `(λ/2)||w||² + (1/n)Σhinge(...)`, λ = 1/(C*n).

**Practical rule:** Use cross-validation to select C. Start with C=1.0 and try orders of magnitude (0.01, 0.1, 1, 10, 100).

### Q2: What makes a point a support vector?

**Answer:** Points with `α_i > 0` in the dual formulation. In the primal, we track points where `y_i * (w^T x_i + b) ≤ 1`:

- α_i = 0: Not a support vector. Point is correctly classified and outside the margin. Contributes nothing to the decision boundary.
- 0 < α_i < C: Margin support vector. Point lies exactly on the margin boundary. These fully determine w.
- α_i = C: Bounded support vector. Point is either within the margin or misclassified (slack ξ_i > 0).

**Property:** Only support vectors affect the decision boundary. Moving a non-support-vector point has no effect on the model.

### Q3: How does the kernel trick allow SVMs to learn non-linear decision boundaries?

**Answer:** The dual formulation only depends on dot products `x_i^T x_j`. The kernel trick replaces this with `K(x_i, x_j) = φ(x_i)^T φ(x_j)`, implicitly mapping data into a higher-dimensional feature space without explicitly computing φ.

**RBF kernel example:**
```
K(x_i, x_j) = exp(-γ * ||x_i - x_j||²) = φ(x_i)^T φ(x_j)
```

where φ maps to an infinite-dimensional Hilbert space (for γ > 0). The linear SVM in this transformed space corresponds to a non-linear decision boundary in the original space.

**Why not just add explicit polynomial features?**
- Kernel SVM can use an infinite-dimensional feature space (RBF kernel) that would be impossible to represent explicitly.
- Kernel computation is O(d) per pair, while explicit φ computation could be infinite or exponential in d.

### Q4: What is the SMO (Sequential Minimal Optimization) algorithm?

**Answer:** SMO solves the dual SVM problem efficiently without a general QP solver:
1. Select two α_i, α_j to optimize (heuristic: choose the pair that most violates KKT conditions)
2. Solve the 2-variable subproblem analytically (closed form)
3. Update b to satisfy KKT conditions
4. Repeat until convergence

SMO converges in O(n²) to O(n³) iterations. Each iteration is O(1) for linear kernels or O(d) for general kernels.

### Q5: How would you extend this to multi-class classification?

**Answer:** SVMs are inherently binary. For K > 2:

1. **One-vs-One (OvO)**: Train K*(K-1)/2 binary SVMs (every pair). Vote for the most common class. Used by LIBSVM.

2. **One-vs-Rest (OvR)**: Train K classifiers (each class vs. all others). Pick the class with highest confidence score.

3. **DAG-SVM**: Arrange OvO classifiers in a directed acyclic graph for faster inference.

OvO is usually preferred for SVM because each subproblem is balanced (equal +1/-1 examples if the original data is balanced). However, it scales quadratically in K.

### Q6: When would you prefer logistic regression over SVM?

**Answer:**

| Use Logistic Regression | Use SVM |
|------------------------|---------|
| Need calibrated probabilities | Need hard classification with max margin |
| Very large datasets (n > 10⁵) | Moderate n (10³ - 10⁴) |
| Features are roughly independent | Complex feature interactions |
| Online/streaming learning | Batch training acceptable |
| Soft probabilistic boundary | Clear margin separation expected |

**Rule of thumb (Andrew Ng):**
- n ≫ d (many samples, few features): Use logistic regression or linear SVM
- n ≈ d (moderate both): Use SVM with RBF kernel
- n ≪ d (few samples, many features): Use logistic regression with regularization
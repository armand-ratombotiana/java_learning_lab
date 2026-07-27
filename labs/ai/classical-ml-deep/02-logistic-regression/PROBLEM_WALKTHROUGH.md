# PROBLEM WALKTHROUGH: Binary Classification with Logistic Regression

## Problem Statement

**Difficulty:** Medium  
**Time Limit:** 50 minutes  
**Category:** Supervised Learning / Classification

Implement logistic regression from scratch in Java 21+ for binary classification. Your model must predict the probability that a given input belongs to the positive class using the sigmoid function, and classify based on a decision boundary.

### Mathematical Foundation

The logistic regression model assumes:

```
P(y=1 | x) = σ(w^T x + b) = 1 / (1 + e^-(w^T x + b))
```

where `σ(z) = 1 / (1 + e^(-z))` is the sigmoid (logistic) function.

### Requirements:

1. **Sigmoid function**: Numerically stable implementation `σ(z) = 1 / (1 + e^(-z))` with clipping to avoid overflow.

2. **Binary Cross-Entropy Loss** (log loss):
   ```
   J(w, b) = -(1/n) * Σ [y_i * log(ŷ_i) + (1 - y_i) * log(1 - ŷ_i)]
   ```

3. **Gradient Descent Training**: Update weights using:
   ```
   w_j := w_j - α * (1/n) * Σ (σ(w^T x_i + b) - y_i) * x_{ij}
   b := b - α * (1/n) * Σ (σ(w^T x_i + b) - y_i)
   ```

4. **L2 Regularization** (optional but implement): Add `(λ/2n) * ||w||²` to loss and `(λ/n) * w_j` to gradient.

5. **Prediction**: `predict_proba(x)` returns probability; `predict(x)` returns class label (0 or 1) using threshold 0.5.

6. **Decision boundary visualization support**: Return coefficients for boundary equation `w^T x + b = 0`.

7. **Multi-class extension**: Implement one-vs-rest (OvR) strategy for k > 2 classes.

### Example:

```java
double[][] X = {{2.0, 1.0}, {3.0, 2.0}, {1.0, 0.5}, {5.0, 4.0}, {6.0, 5.0}};
double[] y = {0, 0, 0, 1, 1};

LogisticRegression clf = new LogisticRegression();
clf.fit(X, y);
double prob = clf.predictProba(new double[]{4.0, 3.0});  // ≈ 0.85
int label = clf.predict(new double[]{4.0, 3.0});          // 1
```

---

## Step-by-Step Solution Walkthrough

### 1. Why Logistic Regression When the Name Says "Regression"?

Despite its name, logistic regression is a **classification** algorithm. The "regression" part refers to the fact that it regresses the log-odds of the probability onto the feature space.

**Log-odds interpretation:**

```
log(P(y=1|x) / P(y=0|x)) = w^T x + b
```

This means the decision boundary (where P(y=1) = P(y=0) = 0.5) is linear: `w^T x + b = 0`.

### 2. The Sigmoid Function: Numerical Stability

The sigmoid function `σ(z) = 1 / (1 + e^(-z))` has two numerical issues:

1. **Overflow**: When `z < -745`, `e^(-z) = e^745` overflows `double` (~1.7e308 max). Solution: return 0.0 for very negative z.
2. **Underflow**: When `z > 745`, `e^(-z) ≈ 0.0`, sigmoid ≈ 1.0 — safe but should still clip.

**Implementation of stable sigmoid:**

```java
private double sigmoid(double z) {
    if (z > 20) return 1.0;       // exp(-20) ≈ 2e-9, negligible
    if (z < -20) return 0.0;      // exp(20) ≈ 4.8e8, fine
    return 1.0 / (1.0 + Math.exp(-z));
}
```

### 3. Binary Cross-Entropy Loss: Derivation

Cross-entropy loss for binary classification measures the "distance" between the true distribution `y` and the predicted distribution `ŷ`:

```
J = -(1/n) * Σ [y_i * log(ŷ_i) + (1 - y_i) * log(1 - ŷ_i)]
```

**Why not MSE?** MSE with sigmoid leads to a non-convex optimization landscape with many local minima. Cross-entropy with sigmoid is convex, guaranteeing global optimum.

**Gradient derivation:**

Let `σ(z_i) = ŷ_i` where `z_i = w^T x_i + b`.

∂J/∂w_j = -(1/n) * Σ [y_i * (1/σ(z_i)) * σ(z_i) * (1 - σ(z_i)) * x_{ij}
         - (1 - y_i) * (1/(1 - σ(z_i))) * σ(z_i) * (1 - σ(z_i)) * x_{ij}]

Using `σ'(z) = σ(z) * (1 - σ(z))`:

∂J/∂w_j = -(1/n) * Σ [y_i * (1 - σ(z_i)) * x_{ij} - (1 - y_i) * σ(z_i) * x_{ij}]
         = (1/n) * Σ (σ(z_i) - y_i) * x_{ij}

This is identical in form to the linear regression gradient! The only difference is that `h_w(x) = σ(w^T x + b)` is now non-linear.

### 4. L2 Regularization (Ridge)

L2 regularization adds a penalty on large weights to prevent overfitting:

```
J_reg = J + (λ / 2n) * ||w||²
```

Gradient update with L2:
```
w_j := w_j - α * [(1/n) * Σ (σ(w^T x_i + b) - y_i) * x_{ij} + (λ/n) * w_j]
```

Note: we typically do **not** regularize the bias term `b`.

### 5. Multi-Class: One-vs-Rest (OvR)

For K classes, train K binary classifiers where classifier `k` treats class `k` as positive (1) and all others as negative (0). During prediction, compute all K probabilities and select the class with the highest score:

```
ŷ = argmax_k P(y=k | x) = argmax_k σ(w_k^T x + b_k)
```

OvR is simple but can have issues with class imbalance and overlapping decision boundaries. Softmax (multinomial logistic regression) is generally preferred for multi-class but requires more complex gradient computation.

### 6. Implementation

```java
package com.ml.logisticregression;

import java.util.Arrays;

/**
 * Logistic Regression classifier supporting binary classification via
 * sigmoid function, binary cross-entropy loss, and gradient descent optimization.
 * <p>
 * Supports L2 regularization and multi-class classification via one-vs-rest.
 */
public class LogisticRegression {

    private double[] weights;
    private double bias;
    private double learningRate;
    private int maxIterations;
    private double tolerance;
    private double lambda;          // L2 regularization strength
    private boolean fitIntercept;   // whether to include bias term
    private double[] lossHistory;

    // Multi-class fields
    private boolean multiClass;
    private int numClasses;
    private double[][] multiWeights;
    private double[] multiBiases;

    /**
     * Default constructor. α=0.01, maxIter=5000, ε=1e-7, no regularization.
     */
    public LogisticRegression() {
        this(0.01, 5000, 1e-7, 0.0, true);
    }

    /**
     * Full parameter constructor.
     *
     * @param learningRate  gradient descent step size
     * @param maxIterations maximum number of training iterations
     * @param tolerance     convergence threshold for loss change
     * @param lambda        L2 regularization strength (0 = no regularization)
     * @param fitIntercept  whether to learn bias term
     */
    public LogisticRegression(double learningRate, int maxIterations,
                              double tolerance, double lambda,
                              boolean fitIntercept) {
        this.learningRate = learningRate;
        this.maxIterations = maxIterations;
        this.tolerance = tolerance;
        this.lambda = lambda;
        this.fitIntercept = fitIntercept;
    }

    // ========== Public API ==========

    /**
     * Fits the model to training data.
     * Automatically detects multi-class if y contains more than 2 unique values.
     *
     * @param X training features of shape [n_samples, n_features]
     * @param y target labels of shape [n_samples] (0 or 1 for binary; 0..K-1 for multi)
     */
    public void fit(double[][] X, int[] y) {
        validateInput(X, y);

        int[] classes = Arrays.stream(y).distinct().sorted().toArray();
        numClasses = classes.length;
        multiClass = numClasses > 2;

        if (multiClass) {
            fitOneVsRest(X, y, classes);
        } else {
            fitBinary(X, y);
        }
    }

    /**
     * Predicts class probabilities for input samples.
     *
     * @param X input features of shape [n_samples, n_features]
     * @return probability matrix of shape [n_samples, num_classes]
     */
    public double[][] predictProba(double[][] X) {
        if (multiClass) {
            return predictProbaMulti(X);
        }
        double[][] probs = new double[X.length][2];
        for (int i = 0; i < X.length; i++) {
            double p1 = predictProbaSingle(X[i]);
            probs[i][0] = 1.0 - p1;
            probs[i][1] = p1;
        }
        return probs;
    }

    /**
     * Predicts class label for a single sample (binary).
     *
     * @param x input feature vector
     * @return predicted probability of positive class
     */
    public double predictProba(double[] x) {
        if (multiClass) {
            throw new IllegalStateException("Use predictProba(double[][]) for multi-class");
        }
        return predictProbaSingle(x);
    }

    /**
     * Predicts class labels for input samples.
     *
     * @param X input features of shape [n_samples, n_features]
     * @return predicted class labels of shape [n_samples]
     */
    public int[] predict(double[][] X) {
        int n = X.length;
        int[] predictions = new int[n];
        if (multiClass) {
            double[][] probs = predictProba(X);
            for (int i = 0; i < n; i++) {
                predictions[i] = argmax(probs[i]);
            }
        } else {
            for (int i = 0; i < n; i++) {
                predictions[i] = predictProbaSingle(X[i]) >= 0.5 ? 1 : 0;
            }
        }
        return predictions;
    }

    /**
     * Predicts class label for a single sample.
     */
    public int predict(double[] x) {
        return predict(new double[][]{x})[0];
    }

    /**
     * Returns the decision boundary coefficients (binary only).
     * Boundary: weights[0]*x[0] + ... + weights[d-1]*x[d-1] + bias = 0
     */
    public double[] getCoefficients() {
        if (multiClass) {
            throw new IllegalStateException("Multi-class has multiple coefficient sets");
        }
        double[] coeff = new double[weights.length + 1];
        System.arraycopy(weights, 0, coeff, 0, weights.length);
        coeff[weights.length] = bias;
        return coeff;
    }

    /**
     * Returns the loss history over training iterations.
     */
    public double[] getLossHistory() {
        return lossHistory;
    }

    // ========== Binary Training ==========

    private void fitBinary(double[][] X, int[] y) {
        int n = X.length, d = X[0].length;
        weights = new double[d];
        bias = 0.0;
        lossHistory = new double[maxIterations];
        double[] yDouble = toDoubleArray(y);

        for (int iter = 0; iter < maxIterations; iter++) {
            double[] predictions = predictRaw(X);
            double loss = computeCrossEntropy(predictions, yDouble);
            lossHistory[iter] = loss;

            if (iter > 0 && Math.abs(lossHistory[iter - 1] - loss) < tolerance) {
                lossHistory = Arrays.copyOf(lossHistory, iter + 1);
                break;
            }

            // Compute gradients
            double[] dw = new double[d];
            double db = 0.0;
            for (int i = 0; i < n; i++) {
                double error = predictions[i] - yDouble[i];
                for (int j = 0; j < d; j++) {
                    dw[j] += error * X[i][j];
                }
                db += error;
            }

            // Average and add regularization
            for (int j = 0; j < d; j++) {
                dw[j] = dw[j] / n + (lambda / n) * weights[j];
            }
            db /= n;

            // Update
            for (int j = 0; j < d; j++) {
                weights[j] -= learningRate * dw[j];
            }
            if (fitIntercept) {
                bias -= learningRate * db;
            }
        }
    }

    // ========== Multi-Class (One-vs-Rest) ==========

    private void fitOneVsRest(double[][] X, int[] y, int[] classes) {
        int n = X.length, d = X[0].length;
        numClasses = classes.length;
        multiWeights = new double[numClasses][d];
        multiBiases = new double[numClasses];
        lossHistory = new double[maxIterations];

        for (int c = 0; c < numClasses; c++) {
            int[] binaryY = new int[n];
            for (int i = 0; i < n; i++) {
                binaryY[i] = (y[i] == classes[c]) ? 1 : 0;
            }

            // Train binary classifier for this class
            double[] w = new double[d];
            double b = 0.0;

            for (int iter = 0; iter < maxIterations; iter++) {
                double lossTotal = 0.0;

                for (int i = 0; i < n; i++) {
                    double z = b;
                    for (int j = 0; j < d; j++) z += w[j] * X[i][j];
                    double pred = sigmoid(z);
                    lossTotal += binaryCrossEntropyLoss(pred, binaryY[i]);

                    double error = pred - binaryY[i];
                    for (int j = 0; j < d; j++) {
                        w[j] -= learningRate * (error * X[i][j] + (lambda / n) * w[j]);
                    }
                    if (fitIntercept) {
                        b -= learningRate * error;
                    }
                }

                lossHistory[iter] = lossTotal / n;
                if (iter > 0 && Math.abs(lossHistory[iter - 1] - lossHistory[iter]) < tolerance) {
                    break;
                }
            }

            multiWeights[c] = w;
            multiBiases[c] = b;
        }
    }

    private double[][] predictProbaMulti(double[][] X) {
        int n = X.length;
        double[][] probs = new double[n][numClasses];
        for (int i = 0; i < n; i++) {
            for (int c = 0; c < numClasses; c++) {
                double z = multiBiases[c];
                for (int j = 0; j < X[i].length; j++) {
                    z += multiWeights[c][j] * X[i][j];
                }
                probs[i][c] = sigmoid(z);
            }
            // Normalize to sum to 1 (softmax-like)
            double sum = 0.0;
            for (int c = 0; c < numClasses; c++) sum += probs[i][c];
            if (sum > 0) {
                for (int c = 0; c < numClasses; c++) probs[i][c] /= sum;
            }
        }
        return probs;
    }

    // ========== Core Computation ==========

    private double predictProbaSingle(double[] x) {
        double z = bias;
        for (int j = 0; j < weights.length; j++) {
            z += weights[j] * x[j];
        }
        return sigmoid(z);
    }

    private double[] predictRaw(double[][] X) {
        int n = X.length;
        double[] predictions = new double[n];
        for (int i = 0; i < n; i++) {
            double z = bias;
            for (int j = 0; j < weights.length; j++) {
                z += weights[j] * X[i][j];
            }
            predictions[i] = sigmoid(z);
        }
        return predictions;
    }

    /**
     * Numerically stable sigmoid function.
     * Clips input to avoid overflow in exp().
     */
    private double sigmoid(double z) {
        if (z > 20) return 1.0;
        if (z < -20) return 0.0;
        return 1.0 / (1.0 + Math.exp(-z));
    }

    private double computeCrossEntropy(double[] predictions, double[] y) {
        int n = y.length;
        double loss = 0.0;
        for (int i = 0; i < n; i++) {
            loss += binaryCrossEntropyLoss(predictions[i], y[i]);
        }
        return loss / n;
    }

    /**
     * Binary cross-entropy loss for a single sample.
     * Adds epsilon clipping to avoid log(0).
     */
    private double binaryCrossEntropyLoss(double pred, double y) {
        double eps = 1e-12;
        pred = Math.max(eps, Math.min(1 - eps, pred));
        return -(y * Math.log(pred) + (1 - y) * Math.log(1 - pred));
    }

    // ========== Utility ==========

    private int argmax(double[] arr) {
        int idx = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[idx]) idx = i;
        }
        return idx;
    }

    private double[] toDoubleArray(int[] arr) {
        double[] result = new double[arr.length];
        for (int i = 0; i < arr.length; i++) result[i] = arr[i];
        return result;
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
                throw new IllegalArgumentException("Inconsistent feature dimensions");
        }
    }
}
```

### 7. Test Cases

```java
package com.ml.logisticregression;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LogisticRegressionTest {

    @Test
    void testBinaryClassification() {
        // Perfectly separable 2D data
        double[][] X = {{1.0, 1.0}, {2.0, 1.0}, {1.0, 0.5}, {5.0, 4.0}, {6.0, 5.0}, {5.5, 4.5}};
        int[] y = {0, 0, 0, 1, 1, 1};

        LogisticRegression clf = new LogisticRegression(0.1, 10000, 1e-7, 0.0, true);
        clf.fit(X, y);

        int[] preds = clf.predict(X);
        for (int i = 0; i < y.length; i++) {
            assertEquals(y[i], preds[i]);
        }
    }

    @Test
    void testProbabilityOutput() {
        double[][] X = {{1.0}, {2.0}, {3.0}, {10.0}, {11.0}, {12.0}};
        int[] y = {0, 0, 0, 1, 1, 1};

        LogisticRegression clf = new LogisticRegression(0.01, 10000, 1e-7, 0.0, true);
        clf.fit(X, y);

        double[][] probs = clf.predictProba(X);
        for (int i = 0; i < y.length; i++) {
            assertTrue(probs[i][1] >= 0.0 && probs[i][1] <= 1.0);
            assertTrue(Math.abs((probs[i][0] + probs[i][1]) - 1.0) < 1e-10);
        }
    }

    @Test
    void testProbabilityOrdering() {
        double[][] X = {{1.0}, {2.0}, {3.0}, {10.0}, {11.0}, {12.0}};
        int[] y = {0, 0, 0, 1, 1, 1};

        LogisticRegression clf = new LogisticRegression(0.01, 10000, 1e-7, 0.0, true);
        clf.fit(X, y);

        // Larger x values should have higher probability of class 1
        double probSmall = clf.predictProba(new double[]{1.0});
        double probLarge = clf.predictProba(new double[]{12.0});
        assertTrue(probLarge > probSmall);
    }

    @Test
    void testDecisionBoundary() {
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {3.0, 3.0}, {5.0, 5.0}, {6.0, 6.0}, {7.0, 7.0}};
        int[] y = {0, 0, 0, 1, 1, 1};

        LogisticRegression clf = new LogisticRegression(0.1, 10000, 1e-7, 0.0, true);
        clf.fit(X, y);

        double[] coeff = clf.getCoefficients(); // [w1, w2, bias]
        // A point on the boundary should give probability ~0.5
        double boundaryX = -coeff[2] / coeff[0]; // x1 = x2, so w1*x + w2*x + bias = 0
        double probAtBoundary = clf.predictProba(new double[]{boundaryX, boundaryX});
        assertTrue(probAtBoundary > 0.3 && probAtBoundary < 0.7);
    }

    @Test
    void testL2Regularization() {
        // Generate data with redundant features
        double[][] X = new double[20][10];
        int[] y = new int[20];
        for (int i = 0; i < 20; i++) {
            X[i][0] = Math.random() * 10;
            for (int j = 1; j < 10; j++) X[i][j] = X[i][0] + Math.random() * 0.1;
            y[i] = X[i][0] > 5 ? 1 : 0;
        }

        LogisticRegression clfNoReg = new LogisticRegression(0.01, 5000, 1e-7, 0.0, true);
        clfNoReg.fit(X, y);

        LogisticRegression clfReg = new LogisticRegression(0.01, 5000, 1e-7, 1.0, true);
        clfReg.fit(X, y);

        double[] coeff = clfReg.getCoefficients();
        double normReg = 0;
        for (int j = 0; j < coeff.length - 1; j++) normReg += coeff[j] * coeff[j];
        assertTrue(normReg > 0);
    }

    @Test
    void testMultiClassOneVsRest() {
        double[][] X = {{1.0, 1.0}, {2.0, 1.0}, {1.0, 2.0},
                        {5.0, 5.0}, {6.0, 5.0}, {5.0, 6.0},
                        {9.0, 1.0}, {10.0, 1.0}, {9.0, 2.0}};
        int[] y = {0, 0, 0, 1, 1, 1, 2, 2, 2};

        LogisticRegression clf = new LogisticRegression(0.1, 10000, 1e-7, 0.0, true);
        clf.fit(X, y);

        int[] preds = clf.predict(X);
        for (int i = 0; i < y.length; i++) {
            assertEquals(y[i], preds[i]);
        }

        double[][] probs = clf.predictProba(X);
        for (int i = 0; i < X.length; i++) {
            assertEquals(3, probs[i].length);
            double sum = 0;
            for (double p : probs[i]) sum += p;
            assertTrue(Math.abs(sum - 1.0) < 1e-6);
        }
    }

    @Test
    void testLossDecreasing() {
        double[][] X = {{1.0, 1.0}, {2.0, 1.0}, {5.0, 4.0}, {6.0, 5.0}};
        int[] y = {0, 0, 1, 1};

        LogisticRegression clf = new LogisticRegression(0.1, 1000, 1e-10, 0.0, true);
        clf.fit(X, y);

        double[] history = clf.getLossHistory();
        for (int i = 1; i < history.length; i++) {
            assertTrue(history[i] <= history[i - 1] + 1e-10,
                "Loss increased at iteration " + i);
        }
    }

    @Test
    void testSigmoidExtremes() {
        LogisticRegression clf = new LogisticRegression();
        // We can test the sigmoid via prediction on extreme values
        double[][] X = {{-1000.0}, {0.0}, {1000.0}};
        int[] y = {0, 0, 1};
        clf.fit(X, y);

        double pNeg = clf.predictProba(new double[]{-1000.0});
        double pZero = clf.predictProba(new double[]{0.0});
        double pPos = clf.predictProba(new double[]{1000.0});

        assertTrue(pNeg < 0.01);
        assertTrue(pPos > 0.99);
    }

    @Test
    void testNullInput() {
        LogisticRegression clf = new LogisticRegression();
        assertThrows(IllegalArgumentException.class,
            () -> clf.fit(null, new int[]{1}));
    }

    @Test
    void testConsistentDimensions() {
        double[][] X = {{1.0}, {2.0, 3.0}};
        int[] y = {0, 1};
        LogisticRegression clf = new LogisticRegression();
        assertThrows(IllegalArgumentException.class, () -> clf.fit(X, y));
    }
}
```

### 8. Complexity Analysis

#### Binary Logistic Regression

| Phase | Time Complexity | Space Complexity |
|-------|----------------|------------------|
| Per iteration (forward + backward) | O(nd) | O(d) |
| K iterations total | O(Knd) | O(K + d) |
| Prediction (single sample) | O(d) | O(1) |
| Prediction (n samples) | O(nd) | O(n) |

#### Multi-class (One-vs-Rest)

| Phase | Time Complexity | Space Complexity |
|-------|----------------|------------------|
| Per class per iteration | O(nd) | O(d) |
| K classes × T iterations | O(KTnd) | O(Kd) |

---

## Follow-up Questions

### Q1: How does logistic regression relate to the perceptron algorithm?

**Answer:** Both are linear classifiers, but:

| Aspect | Logistic Regression | Perceptron |
|--------|-------------------|------------|
| Activation | Sigmoid (smooth, differentiable) | Step function (0/1, non-differentiable) |
| Loss function | Cross-entropy (convex) | Hinge loss / 0-1 loss |
| Update rule | Gradient descent on smooth loss | Error-driven: w += α(y - ŷ)x |
| Output | Probability ∈ (0,1) | Class label {0,1} |
| Convergence | Guaranteed (convex loss) | Only if data is linearly separable |

Perceptron is a special case where we use `sign(w^T x)` and update only on misclassifications. Logistic regression is smoother and produces calibrated probabilities.

### Q2: How would you handle class imbalance?

**Answer:** Several techniques:

1. **Class weighting**: Modify loss to penalize misclassifications of the minority class more heavily:
   ```
   w_0 = n / (2 * n_0),  w_1 = n / (2 * n_1)
   J = -(1/n) * Σ [w_1 * y_i * log(ŷ_i) + w_0 * (1-y_i) * log(1-ŷ_i)]
   ```

2. **Resampling**:
   - **Oversampling** minority class (e.g., SMOTE — Synthetic Minority Oversampling TEchnique)
   - **Undersampling** majority class (random or Tomek links)

3. **Threshold tuning**: Adjust the decision threshold from 0.5 to optimize precision-recall:
   ```java
   // Find optimal threshold via ROC curve
   double bestThreshold = 0.5;
   for (double t = 0.1; t <= 0.9; t += 0.05) {
       double f1 = computeF1Score(predictions, y, t);
       if (f1 > bestF1) { bestF1 = f1; bestThreshold = t; }
   }
   ```

4. **Anomaly detection**: For extreme imbalance (1:1000+), consider one-class SVM or isolation forest instead.

### Q3: What metrics would you use to evaluate a logistic regression classifier?

**Answer:** Beyond accuracy (which can be misleading for imbalanced data):

| Metric | Formula | Best for |
|--------|---------|----------|
| **Precision** | TP / (TP + FP) | When false positives are costly |
| **Recall (Sensitivity)** | TP / (TP + FN) | When false negatives are costly |
| **F1-Score** | 2 * P * R / (P + R) | Balanced view of precision & recall |
| **ROC-AUC** | Area under TPR vs FPR curve | Overall ranking quality |
| **Log Loss** | Cross-entropy | Probabilistic calibration |
| **Cohen's Kappa** | (p_o - p_e) / (1 - p_e) | Agreement beyond chance |

For multi-class: macro/micro-averaged F1, confusion matrix, per-class precision/recall.

### Q4: What is the difference between one-vs-rest (OvR) and softmax (multinomial) logistic regression?

**Answer:**

| Aspect | OvR | Softmax (Multinomial) |
|--------|-----|----------------------|
| Number of classifiers | K binary classifiers | Single model with K outputs |
| Probability normalization | Heuristic (sum ≠ 1, must renormalize) | Natural (sum = 1 by definition) |
| Training | Train K independent models | Jointly train all parameters |
| Decision boundary | Can produce ambiguous regions | Single consistent decision boundary |
| Gradient computation | Simple per-class gradients | Requires Jacobian matrix |
| When to use | Simple, easy to parallelize | When classes are mutually exclusive |

OvR trains classifiers independently so you can parallelize across classes. Softmax is conceptually cleaner but requires the Jacobian of the softmax function (which has a block-diagonal Hessian approximation making optimization efficient).

### Q5: How would you implement a custom learning rate schedule to improve convergence?

**Answer:** Common schedules:

1. **Time-based decay**: `α_t = α_0 / (1 + k * t)`
2. **Step decay**: Halve α every N epochs
3. **Exponential decay**: `α_t = α_0 * exp(-r * t)`
4. **Cosine annealing**: Cyclic LR with cosine warm restarts
5. **ReduceLROnPlateau**: Reduce α by factor γ when validation loss stalls

Implementation:

```java
private double getLearningRate(int epoch) {
    // Exponential decay
    return learningRate * Math.exp(-decayRate * epoch);
}

// In the training loop:
for (int iter = 0; iter < maxIterations; iter++) {
    double lr = getLearningRate(iter);
    // ... update with lr instead of learningRate
}
```

The best schedule depends on the dataset. Plateau detection is often most practical — it doesn't require pre-specifying a schedule but does add the overhead of validation after each epoch.
# PROBLEM WALKTHROUGH: Implement Linear Regression from Scratch

## Problem Statement

**Difficulty:** Medium  
**Time Limit:** 45 minutes  
**Category:** Supervised Learning / Regression

Implement a linear regression model from scratch in Java 21+ that supports both the closed-form (Ordinary Least Squares / Normal Equation) solution and the iterative Gradient Descent solution. Your model must fit a linear hypothesis of the form:

```
h_w(x) = w^T x + b
```

Given a training dataset `{(x_i, y_i)}_{i=1}^n` where `x_i ∈ ℝ^d` and `y_i ∈ ℝ`, you must minimize the Mean Squared Error (MSE) loss function:

```
J(w, b) = (1/2n) * Σ_{i=1}^n (h_w(x_i) - y_i)^2
```

### Requirements:

1. **Closed-form solution** (Normal Equation): Compute `θ = (X^T X)^(-1) X^T y` directly using matrix operations, where `θ` includes both weights and bias via an augmented feature matrix.

2. **Gradient Descent solution**: Implement batch gradient descent that iteratively updates parameters using:
   ```
   w_j := w_j - α * (1/n) * Σ_{i=1}^n (h_w(x_i) - y_i) * x_{ij}
   b := b - α * (1/n) * Σ_{i=1}^n (h_w(x_i) - y_i)
   ```

3. **Convergence criteria**: Stop training when `||J_{t+1} - J_t|| < ε` or when `max_iterations` is reached.

4. **Prediction**: Given new input `x`, return `ŷ = w^T x + b`.

5. **Evaluation**: Compute R² score and Mean Squared Error on test data.

### Example:

```java
// Training data: y = 2*x + 1 + noise
double[][] X = {{1.0}, {2.0}, {3.0}, {4.0}, {5.0}};
double[] y = {3.2, 5.1, 7.0, 8.9, 11.2};

LinearRegression model = new LinearRegression();
model.fit(X, y);  // default: gradient descent
double prediction = model.predict(new double[]{6.0});  // ≈ 13.1
```

---

## Step-by-Step Solution Walkthrough

### 1. Understanding the Mathematical Formulation

Linear regression assumes a linear relationship between input features `x` and target `y`. The goal is to find parameters `(w, b)` that minimize the sum of squared residuals.

#### 1.1 Closed-Form (Normal Equation)

For the closed-form solution, we augment the feature matrix by prepending a column of ones to account for the bias term `b`. Let:

```
X_aug = [1, X]   (n × (d+1) matrix)
θ = [b, w^T]^T   ((d+1) × 1 vector)
```

The normal equation is derived by setting the gradient of `J(θ)` to zero:

```
∇J(θ) = (1/n) * X_aug^T (X_aug θ - y) = 0
X_aug^T X_aug θ = X_aug^T y
θ = (X_aug^T X_aug)^(-1) X_aug^T y
```

**Derivation details:**

Starting from MSE: `J(θ) = (1/2n) ||Xθ - y||² = (1/2n) (Xθ - y)^T (Xθ - y)`

Expand: `J(θ) = (1/2n) (θ^T X^T X θ - 2θ^T X^T y + y^T y)`

Take gradient w.r.t θ: `∇J(θ) = (1/n) (X^T X θ - X^T y)`

Set to zero: `X^T X θ = X^T y` → `θ = (X^T X)^(-1) X^T y`

**Important considerations:**

- `X^T X` must be invertible (non-singular). If features are collinear, `X^T X` is singular → use pseudo-inverse or regularization.
- Time complexity: `O(d³ + d²n)` — dominated by matrix inversion (cubic in number of features).
- For high-dimensional data (d > 10^4), gradient descent is preferred.

#### 1.2 Gradient Descent

Gradient descent is an iterative first-order optimization algorithm:

```
θ := θ - α * ∇J(θ)
```

The gradient for each parameter:

```
∂J/∂w_j = (1/n) * Σ_{i=1}^n (h_w(x_i) - y_i) * x_{ij}
∂J/∂b = (1/n) * Σ_{i=1}^n (h_w(x_i) - y_i)
```

**Vectorized form** (with bias absorbed into weights):

`∇J(θ) = (1/n) * X^T (Xθ - y)`

**Update rule:**

`θ := θ - (α/n) * X^T (Xθ - y)`

**Convergence criteria:**

We track the loss `J_t` at each iteration and stop when:
```
|J_t - J_{t-1}| < ε   and   |J_{t-1} - J_{t-2}| < ε
```
(two consecutive improvements below threshold to avoid early stopping on oscillation).

#### 1.3 R² Score (Coefficient of Determination)

R² measures the proportion of variance explained by the model:

```
R² = 1 - SS_res / SS_tot
SS_res = Σ (y_i - ŷ_i)²    (residual sum of squares)
SS_tot = Σ (y_i - ȳ)²      (total sum of squares)
```

R² ranges from (-∞, 1]. R² = 1 means perfect fit. R² = 0 means the model predicts the mean. R² < 0 means the model is worse than predicting the mean.

### 2. Java Implementation Strategy

#### 2.1 Matrix Operations

Since Java doesn't have built-in matrix libraries in the standard library (pre-dating modern ML frameworks), we implement a lightweight `Matrix` utility class that supports:
- Matrix multiplication
- Matrix transpose
- Matrix inverse (via Gaussian elimination)
- Matrix-vector multiplication

#### 2.2 Design Choices

- **Package**: `com.ml.linearregression`
- **Main class**: `LinearRegression` — follows sklearn-style API convention
- **Inner helper**: `MatrixUtils` — static methods for matrix operations
- **Constructor parameters**: learning rate `α`, iterations `maxIter`, tolerance `ε`, solver type
- **Fit method**: Dispatches to closed-form or gradient descent based on configuration

#### 2.3 Edge Cases and Numerical Stability

- **Singular matrix**: In closed-form, check `det(X^T X)` before inversion; fall back to pseudo-inverse or throw informative exception.
- **Feature scaling**: Gradient descent converges faster when features are standardized (z-score). We include an option for automatic standardization.
- **Overflow/underflow**: Use `double` (64-bit IEEE 754) which handles most practical ranges.
- **Learning rate selection**: If loss diverges (NaN or increases), the learning rate is too high.

### 3. Implementation

```java
package com.ml.linearregression;

import java.util.Arrays;

/**
 * Linear Regression implementation supporting both Ordinary Least Squares
 * (closed-form via normal equation) and Batch Gradient Descent.
 * <p>
 * Hypothesis: h_w(x) = w^T x + b
 * Loss: Mean Squared Error J(w,b) = (1/2n) * Σ(h_w(x_i) - y_i)^2
 */
public class LinearRegression {

    private double[] weights;
    private double bias;
    private double learningRate;
    private int maxIterations;
    private double tolerance;
    private boolean useClosedForm;
    private boolean standardized;
    private double[] mean;
    private double[] std;
    private double[] lossHistory;

    /**
     * Default constructor. Uses gradient descent with α=0.01, maxIter=1000, ε=1e-6.
     */
    public LinearRegression() {
        this(0.01, 1000, 1e-6, false, false);
    }

    /**
     * Full parameter constructor.
     *
     * @param learningRate  step size for gradient descent
     * @param maxIterations maximum number of iterations
     * @param tolerance     convergence threshold for loss change
     * @param useClosedForm if true, use normal equation instead of gradient descent
     * @param standardize   if true, z-score normalize features before training
     */
    public LinearRegression(double learningRate, int maxIterations,
                            double tolerance, boolean useClosedForm,
                            boolean standardize) {
        this.learningRate = learningRate;
        this.maxIterations = maxIterations;
        this.tolerance = tolerance;
        this.useClosedForm = useClosedForm;
        this.standardized = standardize;
        this.lossHistory = new double[0];
    }

    /**
     * Fits the model to training data.
     *
     * @param X training features of shape [n_samples, n_features]
     * @param y target values of shape [n_samples]
     * @throws IllegalArgumentException if data is invalid
     */
    public void fit(double[][] X, double[] y) {
        int n = X.length;
        int d = X[0].length;

        validateInput(X, y);

        // Standardize features if requested
        double[][] Xprocessed = X;
        if (standardized) {
            mean = new double[d];
            std = new double[d];
            Xprocessed = standardize(X, mean, std);
        }

        if (useClosedForm) {
            fitClosedForm(Xprocessed, y);
        } else {
            fitGradientDescent(Xprocessed, y, n, d);
        }
    }

    /**
     * Closed-form solution using the Normal Equation:
     * θ = (X^T X)^(-1) X^T y
     * <p>
     * Augments X with a column of ones for the bias term.
     */
    private void fitClosedForm(double[][] X, double[] y) {
        int n = X.length;
        int d = X[0].length;

        // Augment X with column of 1s for bias: [1, X]
        double[][] Xaug = new double[n][d + 1];
        for (int i = 0; i < n; i++) {
            Xaug[i][0] = 1.0;
            System.arraycopy(X[i], 0, Xaug[i], 1, d);
        }

        // Compute X^T * X
        double[][] Xt = transpose(Xaug);
        double[][] XtX = multiply(Xt, Xaug);

        // Compute X^T * y
        double[] Xty = multiplyVector(Xt, y);

        // Solve (X^T * X) * theta = X^T * y via Gaussian elimination
        double[] theta = solveLinearSystem(XtX, Xty);

        // Extract bias and weights
        bias = theta[0];
        weights = new double[d];
        System.arraycopy(theta, 1, weights, 0, d);

        // Record final loss
        lossHistory = new double[]{computeMse(X, y)};
    }

    /**
     * Gradient descent iterative solution.
     */
    private void fitGradientDescent(double[][] X, double[] y, int n, int d) {
        weights = new double[d];
        bias = 0.0;
        lossHistory = new double[maxIterations];

        double prevLoss = Double.MAX_VALUE;
        int stagnateCount = 0;

        for (int iter = 0; iter < maxIterations; iter++) {
            // Compute predictions
            double[] predictions = predictRaw(X);

            // Compute loss
            double loss = computeMse(predictions, y);
            lossHistory[iter] = loss;

            // Check convergence
            double diff = prevLoss - loss;
            if (Math.abs(diff) < tolerance) {
                stagnateCount++;
                if (stagnateCount >= 2) {
                    lossHistory = Arrays.copyOf(lossHistory, iter + 1);
                    break;
                }
            } else {
                stagnateCount = 0;
            }
            prevLoss = loss;

            // Compute gradients
            double[] errors = new double[n];
            for (int i = 0; i < n; i++) {
                errors[i] = predictions[i] - y[i];
            }

            double dw[] = new double[d];
            double db = 0.0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < d; j++) {
                    dw[j] += errors[i] * X[i][j];
                }
                db += errors[i];
            }

            // Average gradients
            for (int j = 0; j < d; j++) {
                dw[j] /= n;
            }
            db /= n;

            // Check for divergence (gradient explosion)
            boolean diverged = false;
            for (int j = 0; j < d; j++) {
                if (Double.isNaN(dw[j]) || Double.isInfinite(dw[j])) {
                    diverged = true;
                    break;
                }
            }
            if (diverged) {
                throw new ArithmeticException(
                    "Gradient descent diverged. Try reducing learning rate or standardizing features."
                );
            }

            // Update parameters
            for (int j = 0; j < d; j++) {
                weights[j] -= learningRate * dw[j];
            }
            bias -= learningRate * db;
        }
    }

    /**
     * Predicts target values for given input features.
     *
     * @param X input features of shape [n_samples, n_features]
     * @return predicted values of shape [n_samples]
     */
    public double[] predict(double[][] X) {
        double[][] Xproc = X;
        if (standardized && mean != null) {
            Xproc = applyStandardization(X, mean, std);
        }
        return predictRaw(Xproc);
    }

    /**
     * Predicts a single sample.
     *
     * @param x input feature vector of length n_features
     * @return predicted value
     */
    public double predict(double[] x) {
        return predict(new double[][]{x})[0];
    }

    /**
     * Raw prediction without standardization.
     */
    private double[] predictRaw(double[][] X) {
        int n = X.length;
        double[] predictions = new double[n];
        for (int i = 0; i < n; i++) {
            predictions[i] = bias;
            for (int j = 0; j < weights.length; j++) {
                predictions[i] += weights[j] * X[i][j];
            }
        }
        return predictions;
    }

    /**
     * Computes Mean Squared Error.
     */
    private double computeMse(double[] predictions, double[] y) {
        int n = y.length;
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            double diff = predictions[i] - y[i];
            sum += diff * diff;
        }
        return sum / n;
    }

    /**
     * Computes MSE for given features and targets.
     */
    private double computeMse(double[][] X, double[] y) {
        return computeMse(predictRaw(X), y);
    }

    /**
     * Returns the R² score (coefficient of determination).
     *
     * @param X test features
     * @param y test targets
     * @return R² score
     */
    public double score(double[][] X, double[] y) {
        double[] predictions = predict(X);
        double ssRes = 0.0, ssTot = 0.0;
        double meanY = Arrays.stream(y).average().orElse(0.0);
        for (int i = 0; i < y.length; i++) {
            ssRes += (y[i] - predictions[i]) * (y[i] - predictions[i]);
            ssTot += (y[i] - meanY) * (y[i] - meanY);
        }
        return 1.0 - ssRes / ssTot;
    }

    /**
     * Returns the loss history over iterations.
     */
    public double[] getLossHistory() {
        return Arrays.copyOf(lossHistory, lossHistory.length);
    }

    /**
     * Returns the learned weights (excluding bias).
     */
    public double[] getWeights() {
        return weights;
    }

    /**
     * Returns the learned bias term.
     */
    public double getBias() {
        return bias;
    }

    // ========== Matrix Utility Methods ==========

    private static double[][] transpose(double[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        double[][] result = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }

    private static double[][] multiply(double[][] a, double[][] b) {
        int m = a.length, n = a[0].length, p = b[0].length;
        double[][] result = new double[m][p];
        for (int i = 0; i < m; i++) {
            for (int k = 0; k < n; k++) {
                double aik = a[i][k];
                if (aik == 0.0) continue;
                for (int j = 0; j < p; j++) {
                    result[i][j] += aik * b[k][j];
                }
            }
        }
        return result;
    }

    private static double[] multiplyVector(double[][] matrix, double[] vector) {
        int m = matrix.length, n = matrix[0].length;
        double[] result = new double[m];
        for (int i = 0; i < m; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += matrix[i][j] * vector[j];
            }
            result[i] = sum;
        }
        return result;
    }

    /**
     * Solves A*x = b for x using Gaussian elimination with partial pivoting.
     * A is assumed to be square and non-singular.
     */
    private static double[] solveLinearSystem(double[][] A, double[] b) {
        int n = b.length;
        double[][] aug = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, aug[i], 0, n);
            aug[i][n] = b[i];
        }

        // Forward elimination with partial pivoting
        for (int col = 0; col < n; col++) {
            // Find pivot
            int maxRow = col;
            double maxVal = Math.abs(aug[col][col]);
            for (int row = col + 1; row < n; row++) {
                double val = Math.abs(aug[row][col]);
                if (val > maxVal) {
                    maxVal = val;
                    maxRow = row;
                }
            }

            // Swap rows
            double[] temp = aug[col];
            aug[col] = aug[maxRow];
            aug[maxRow] = temp;

            // Check for singular matrix
            if (Math.abs(aug[col][col]) < 1e-12) {
                throw new ArithmeticException(
                    "Singular matrix: features may be collinear. " +
                    "Use gradient descent or add regularization."
                );
            }

            // Eliminate below
            for (int row = col + 1; row < n; row++) {
                double factor = aug[row][col] / aug[col][col];
                for (int j = col; j <= n; j++) {
                    aug[row][j] -= factor * aug[col][j];
                }
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = aug[i][n];
            for (int j = i + 1; j < n; j++) {
                sum -= aug[i][j] * x[j];
            }
            x[i] = sum / aug[i][i];
        }
        return x;
    }

    /**
     * Computes determinant of a square matrix (used for singularity check).
     */
    private static double determinant(double[][] matrix) {
        int n = matrix.length;
        if (n == 1) return matrix[0][0];

        double det = 0.0;
        for (int j = 0; j < n; j++) {
            det += matrix[0][j] * cofactor(matrix, 0, j) * (j % 2 == 0 ? 1 : -1);
        }
        return det;
    }

    private static double cofactor(double[][] matrix, int row, int col) {
        return determinant(minor(matrix, row, col));
    }

    private static double[][] minor(double[][] matrix, int row, int col) {
        int n = matrix.length;
        double[][] minor = new double[n - 1][n - 1];
        int r = 0;
        for (int i = 0; i < n; i++) {
            if (i == row) continue;
            int c = 0;
            for (int j = 0; j < n; j++) {
                if (j == col) continue;
                minor[r][c++] = matrix[i][j];
            }
            r++;
        }
        return minor;
    }

    // ========== Standardization ==========

    private double[][] standardize(double[][] X, double[] mean, double[] std) {
        int n = X.length, d = X[0].length;
        // Compute mean
        for (int j = 0; j < d; j++) {
            double sum = 0.0;
            for (int i = 0; i < n; i++) {
                sum += X[i][j];
            }
            mean[j] = sum / n;
        }
        // Compute std
        for (int j = 0; j < d; j++) {
            double sumSq = 0.0;
            for (int i = 0; i < n; i++) {
                double diff = X[i][j] - mean[j];
                sumSq += diff * diff;
            }
            std[j] = Math.sqrt(sumSq / n);
            if (std[j] < 1e-12) std[j] = 1.0; // avoid division by zero
        }
        return applyStandardization(X, mean, std);
    }

    private double[][] applyStandardization(double[][] X, double[] mean, double[] std) {
        int n = X.length, d = X[0].length;
        double[][] result = new double[n][d];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < d; j++) {
                result[i][j] = (X[i][j] - mean[j]) / std[j];
            }
        }
        return result;
    }

    private void validateInput(double[][] X, double[] y) {
        if (X == null || y == null) {
            throw new IllegalArgumentException("Input data cannot be null");
        }
        if (X.length == 0 || y.length == 0) {
            throw new IllegalArgumentException("Input data cannot be empty");
        }
        if (X.length != y.length) {
            throw new IllegalArgumentException(
                "Number of samples in X (" + X.length + ") must match y (" + y.length + ")"
            );
        }
        int d = X[0].length;
        for (int i = 1; i < X.length; i++) {
            if (X[i].length != d) {
                throw new IllegalArgumentException(
                    "Inconsistent feature dimensions at row " + i
                );
            }
        }
    }
}
```

### 4. Test Cases

```java
package com.ml.linearregression;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LinearRegressionTest {

    @Test
    void testClosedFormPerfectFit() {
        // y = 3*x + 2, no noise
        double[][] X = {{1.0}, {2.0}, {3.0}, {4.0}, {5.0}};
        double[] y = {5.0, 8.0, 11.0, 14.0, 17.0};

        LinearRegression model = new LinearRegression(0.01, 1000, 1e-8, true, false);
        model.fit(X, y);

        assertEquals(2.0, model.getBias(), 1e-10);
        assertEquals(3.0, model.getWeights()[0], 1e-10);
        assertEquals(1.0, model.score(X, y), 1e-10);
    }

    @Test
    void testGradientDescentConvergence() {
        double[][] X = {{1.0}, {2.0}, {3.0}, {4.0}, {5.0}};
        double[] y = {3.2, 5.1, 7.0, 8.9, 11.2};

        LinearRegression model = new LinearRegression(0.01, 10000, 1e-6, false, false);
        model.fit(X, y);

        double prediction = model.predict(new double[]{6.0});
        assertTrue(prediction > 12.5 && prediction < 14.0);
        assertTrue(model.score(X, y) > 0.95);
    }

    @Test
    void testMultipleFeatures() {
        // y = 2*x1 + 3*x2 + 1
        double[][] X = {{1.0, 1.0}, {2.0, 2.0}, {3.0, 3.0}, {4.0, 4.0}};
        double[] y = {6.0, 11.0, 16.0, 21.0};

        LinearRegression model = new LinearRegression(0.01, 10000, 1e-8, true, false);
        model.fit(X, y);

        assertEquals(1.0, model.getBias(), 1e-8);
        assertEquals(2.0, model.getWeights()[0], 1e-8);
        assertEquals(3.0, model.getWeights()[1], 1e-8);
    }

    @Test
    void testClosedFormMatchesGradientDescent() {
        double[][] X = {{1.0}, {2.0}, {3.0}, {4.0}, {5.0}, {6.0}};
        double[] y = {2.8, 4.1, 6.2, 7.9, 10.3, 11.8};

        LinearRegression cf = new LinearRegression(0.01, 1000, 1e-8, true, false);
        cf.fit(X, y);

        LinearRegression gd = new LinearRegression(0.01, 50000, 1e-8, false, false);
        gd.fit(X, y);

        assertEquals(cf.getBias(), gd.getBias(), 1e-4);
        assertEquals(cf.getWeights()[0], gd.getWeights()[0], 1e-4);
    }

    @Test
    void testStandardizationHelpsConvergence() {
        // Features with very different scales
        double[][] X = {{1000.0}, {2000.0}, {3000.0}, {4000.0}, {5000.0}};
        double[] y = {2001.0, 4002.0, 6003.0, 8004.0, 10005.0};

        LinearRegression model = new LinearRegression(0.1, 10000, 1e-6, false, true);
        model.fit(X, y);

        assertTrue(model.score(X, y) > 0.999);
    }

    @Test
    void testSingularMatrixException() {
        double[][] X = {{1.0, 2.0}, {2.0, 4.0}, {3.0, 6.0}};
        double[] y = {1.0, 2.0, 3.0};

        LinearRegression model = new LinearRegression(0.01, 1000, 1e-8, true, false);
        assertThrows(ArithmeticException.class, () -> model.fit(X, y));
    }

    @Test
    void testSingleSample() {
        double[][] X = {{2.5}};
        double[] y = {5.0};

        LinearRegression model = new LinearRegression(0.01, 1000, 1e-8, false, false);
        model.fit(X, y);

        // With 1 sample, any line through the point works; predict should return y
        double pred = model.predict(new double[]{2.5});
        assertEquals(5.0, pred, 1e-8);
    }

    @Test
    void testLossHistoryDecreasing() {
        double[][] X = {{1.0}, {2.0}, {3.0}, {4.0}, {5.0}};
        double[] y = {3.2, 5.1, 7.0, 8.9, 11.2};

        LinearRegression model = new LinearRegression(0.01, 10000, 1e-10, false, false);
        model.fit(X, y);

        double[] history = model.getLossHistory();
        for (int i = 1; i < history.length; i++) {
            assertTrue(history[i] <= history[i - 1] + 1e-10,
                "Loss increased at iteration " + i);
        }
    }

    @Test
    void testNullInput() {
        LinearRegression model = new LinearRegression();
        assertThrows(IllegalArgumentException.class, () -> model.fit(null, new double[]{1.0}));
    }

    @Test
    void testInconsistentDimensions() {
        double[][] X = {{1.0}, {2.0, 3.0}};
        double[] y = {1.0, 2.0};
        LinearRegression model = new LinearRegression();
        assertThrows(IllegalArgumentException.class, () -> model.fit(X, y));
    }
}
```

### 5. Complexity Analysis

#### Closed-Form (Normal Equation)

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Augment X | O(nd) | O(nd) |
| Compute X^T X | O(nd²) | O(d²) |
| Compute X^T y | O(nd) | O(d) |
| Solve (X^T X)θ = X^T y | O(d³) | O(d²) |
| **Total** | **O(d³ + nd²)** | **O(nd + d²)** |

- **Best for:** d < 10⁴, n is moderate
- **Pros:** Exact solution (up to numerical precision), no hyperparameters
- **Cons:** O(d³) inversion is prohibitive for high dimensions; requires non-singular X^T X

#### Gradient Descent

| Operation | Per Iteration | K Iterations |
|-----------|---------------|--------------|
| Forward pass (predict) | O(nd) | O(Knd) |
| Gradient computation | O(nd) | O(Knd) |
| Parameter update | O(d) | O(Kd) |
| **Total** | **O(nd)** | **O(Knd)** |

- **Best for:** d > 10⁴, very large n
- **Pros:** Scales linearly with features, works with streaming data
- **Cons:** Requires tuning learning rate; may converge to local minimum (convex for linear regression, so global minimum is guaranteed but may be slow)

**Convergence rate:** Gradient descent converges at a linear rate O(ρ^K) where ρ < 1 depends on the condition number of X^T X. Well-conditioned (standardized) data converges faster.

---

## Follow-up Questions

### Q1: How would you extend this to handle polynomial regression?

**Answer:** Polynomial regression is a special case of linear regression where we create new features by raising existing features to powers. For example, for degree-2 polynomial regression with input `x`, the augmented feature vector becomes `[1, x, x²]`. The implementation remains identical — we just preprocess X by adding polynomial features. We can add a `PolynomialFeatures` transformer:

```java
static double[][] polynomialFeatures(double[][] X, int degree) {
    int n = X.length, d = X[0].length;
    // For degree 2 with 1 feature: [1, x, x^2]
    int numFeatures = d * degree + 1;
    double[][] result = new double[n][numFeatures];
    for (int i = 0; i < n; i++) {
        result[i][0] = 1.0;  // bias term handled by feature
        int idx = 1;
        for (int j = 0; j < d; j++) {
            for (int p = 1; p <= degree; p++) {
                result[i][idx++] = Math.pow(X[i][j], p);
            }
        }
    }
    return result;
}
```

### Q2: How does ridge regression (L2 regularization) modify the solution?

**Answer:** Ridge regression adds an L2 penalty `λ||w||²` to the loss function:

```
J(w) = (1/2n) * ||Xw - y||² + (λ/2) * ||w||²
```

This modifies the gradient to:
```
∇J(w) = (1/n) * (X^T (Xw - y) + λw)
```

And the closed-form solution becomes:
```
θ = (X^T X + λI)^(-1) X^T y
```

The key benefit: even if X^T X is singular, `X^T X + λI` is always invertible (adding λ to diagonal ensures full rank). Ridge also reduces overfitting by shrinking weights toward zero.

### Q3: What is the effect of the learning rate on convergence? How would you implement adaptive learning rates?

**Answer:** 
- **Too large (α > 1/λ_max)**: Loss oscillates or diverges
- **Too small (α ≪ 1/λ_max)**: Very slow convergence
- **Just right**: Linear convergence, ideally with α = 1/L where L is the Lipschitz constant of ∇J

Adaptive learning rate strategies:
- **Learning rate scheduling**: `α_t = α_0 / (1 + decay * t)`
- **AdaGrad**: Per-parameter adaptive `α_t = α_0 / sqrt(G_t + ε)` where G_t accumulates squared gradients
- **RMSProp**: Moving average of squared gradients
- **Adam**: Combines momentum + RMSProp with bias correction

Implementation sketch for learning rate decay:

```java
double decayRate = 0.95;
for (int iter = 0; iter < maxIterations; iter++) {
    double currentLr = learningRate * Math.pow(decayRate, iter);
    // ... gradient update with currentLr
}
```

### Q4: What assumptions does linear regression make about the data? How do violations affect the model?

**Answer:** Key assumptions of Ordinary Least Squares:

| Assumption | Violation Consequence | Mitigation |
|------------|----------------------|------------|
| **Linearity**: Relationship between X and y is linear | Biased predictions, high error | Polynomial features, transformations |
| **Independence**: Observations are independent | Inflated R², underestimated standard errors | Time-series models (ARIMA), clustered standard errors |
| **Homoscedasticity**: Constant variance of errors | Inefficient estimates, wrong confidence intervals | Weighted least squares, robust standard errors |
| **Normality of errors** (for inference) | Invalid hypothesis tests | Bootstrapping for inference |
| **No multicollinearity**: Features not perfectly correlated | Unstable coefficients, high variance | Ridge regression, PCA, feature selection |
| **Exogeneity**: Errors uncorrelated with predictors | Biased coefficients (omitted variable bias) | Instrumental variables, include omitted features |

### Q5: How would you implement stochastic gradient descent (SGD) instead of batch GD?

**Answer:** SGD updates parameters using a single randomly-selected sample per iteration (or mini-batch):

```java
private void fitSGD(double[][] X, double[] y, int n, int d, int batchSize) {
    weights = new double[d];
    bias = 0.0;

    for (int iter = 0; iter < maxIterations; iter++) {
        // Shuffle data
        shuffle(X, y);

        for (int start = 0; start < n; start += batchSize) {
            int end = Math.min(start + batchSize, n);
            int batchN = end - start;

            // Mini-batch gradient
            double[] dw = new double[d];
            double db = 0.0;

            for (int i = start; i < end; i++) {
                double pred = bias;
                for (int j = 0; j < d; j++) pred += weights[j] * X[i][j];
                double error = pred - y[i];

                for (int j = 0; j < d; j++) dw[j] += error * X[i][j];
                db += error;
            }

            // Update
            for (int j = 0; j < d; j++) {
                weights[j] -= learningRate * dw[j] / batchN;
            }
            bias -= learningRate * db / batchN;
        }
    }
}
```

SGD converges faster for large datasets because it makes multiple updates per epoch. Mini-batch (typically 32-256 samples) balances noise vs. computational efficiency and is the standard in deep learning.
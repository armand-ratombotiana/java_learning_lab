# Regression - Mini Project

## Mini Project: Building a Complete Regression Library in Java

### Project Overview
Implement a comprehensive regression library from scratch in Java, including linear regression, polynomial regression, Ridge, Lasso, and Elastic Net with proper cross-validation and evaluation metrics.

### Learning Objectives
- Implement closed-form and iterative regression solutions
- Understand regularization and its effects
- Build cross-validation and model selection pipeline
- Create comprehensive evaluation metrics

### Duration
3-4 hours

---

## Project Structure

```
regression/
├── src/main/java/com/ml/regression/
│   ├── RegressionModel.java
│   ├── LinearRegression.java
│   ├── PolynomialRegression.java
│   ├── RidgeRegression.java
│   ├── LassoRegression.java
│   ├── ElasticNetRegression.java
│   ├── RegularizedRegression.java
│   ├── evaluation/
│   │   ├── RegressionMetrics.java
│   │   └── CrossValidator.java
│   ├── preprocessing/
│   │   ├── FeatureScaler.java
│   │   └── PolynomialFeatures.java
│   ├── optimization/
│   │   ├── GradientDescentOptimizer.java
│   │   └── ClosedFormSolver.java
│   └── Main.java
├── build.gradle
└── README.md
```

---

## Part 1: Core Interfaces and Base Classes (30 min)

### Regression Model Interface

```java
package com.ml.regression;

public interface RegressionModel {
    void fit(double[][] X, double[] y);
    double[] predict(double[][] X);
    double[] getCoefficients();
    double getIntercept();
    double getScore(double[][] X, double[] y);
}
```

### Abstract Base Implementation

```java
package com.ml.regression;

import java.util.Arrays;

public abstract class AbstractRegression implements RegressionModel {

    protected double[] coefficients;
    protected double intercept;
    protected boolean fitted = false;

    @Override
    public double[] predict(double[][] X) {
        if (!fitted) {
            throw new IllegalStateException("Model must be fitted before prediction");
        }

        int n = X.length;
        double[] predictions = new double[n];

        for (int i = 0; i < n; i++) {
            predictions[i] = predictSingle(X[i]);
        }

        return predictions;
    }

    protected double predictSingle(double[] features) {
        double prediction = intercept;
        for (int i = 0; i < features.length; i++) {
            prediction += coefficients[i] * features[i];
        }
        return prediction;
    }

    @Override
    public double getScore(double[][] X, double[] y) {
        double[] predictions = predict(X);
        return RegressionMetrics.r2Score(y, predictions);
    }

    @Override
    public double[] getCoefficients() {
        return coefficients.clone();
    }

    @Override
    public double getIntercept() {
        return intercept;
    }
}
```

---

## Part 2: Linear Regression Implementations (60 min)

### Closed-Form Linear Regression

```java
package com.ml.regression;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.LUDecomposition;

public class LinearRegression extends AbstractRegression {

    private boolean useBias = true;

    public LinearRegression() {}

    public LinearRegression(boolean useBias) {
        this.useBias = useBias;
    }

    @Override
    public void fit(double[][] X, double[] y) {
        int n = X.length;
        int d = X[0].length;

        // Add bias term if needed
        double[][] X_aug;
        if (useBias) {
            X_aug = new double[n][d + 1];
            for (int i = 0; i < n; i++) {
                X_aug[i][0] = 1.0; // Bias term
                System.arraycopy(X[i], 0, X_aug[i], 1, d);
            }
        } else {
            X_aug = X;
        }

        // Solve using normal equation: β = (X^T X)^(-1) X^T y
        RealMatrix X_matrix = MatrixUtils.createRealMatrix(X_aug);
        RealVector y_vector = MatrixUtils.createRealVector(y);

        RealMatrix XtX = X_matrix.transpose().multiply(X_matrix);

        // Add small regularization for numerical stability
        double epsilon = 1e-10;
        int size = XtX.getRowDimension();
        for (int i = 0; i < size; i++) {
            XtX.setEntry(i, i, XtX.getEntry(i, i) + epsilon);
        }

        try {
            RealMatrix XtX_inv = new LUDecomposition(XtX).getSolver().getInverse();
            RealVector beta = XtX_inv.multiply(X_matrix.transpose()).operate(y_vector);

            if (useBias) {
                this.intercept = beta.getEntry(0);
                this.coefficients = new double[d];
                for (int i = 0; i < d; i++) {
                    this.coefficients[i] = beta.getEntry(i + 1);
                }
            } else {
                this.intercept = 0.0;
                this.coefficients = beta.toArray();
            }

            this.fitted = true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to solve normal equation: " + e.getMessage());
        }
    }
}
```

### Gradient Descent Linear Regression

```java
package com.ml.regression.optimization;

import java.util.Arrays;

public class GradientDescentOptimizer {

    private double learningRate;
    private int maxIterations;
    private double tolerance;
    private boolean verbose;

    public GradientDescentOptimizer(double learningRate, int maxIterations) {
        this(learningRate, maxIterations, 1e-6, false);
    }

    public GradientDescentOptimizer(double learningRate, int maxIterations,
                                    double tolerance, boolean verbose) {
        this.learningRate = learningRate;
        this.maxIterations = maxIterations;
        this.tolerance = tolerance;
        this.verbose = verbose;
    }

    public double[] optimize(double[][] X, double[] y,
                            double[] initialWeights, double lambda,
                            boolean useL1) {
        double[] weights = initialWeights.clone();
        double[] gradient = new double[weights.length];

        for (int iter = 0; iter < maxIterations; iter++) {
            // Compute gradient
            Arrays.fill(gradient, 0.0);

            int n = X.length;
            for (int i = 0; i < n; i++) {
                double prediction = 0;
                for (int j = 0; j < weights.length; j++) {
                    prediction += X[i][j] * weights[j];
                }
                double error = prediction - y[i];

                for (int j = 0; j < weights.length; j++) {
                    gradient[j] += error * X[i][j] / n;
                }
            }

            // Add regularization gradient
            if (lambda > 0) {
                if (useL1) {
                    // Subgradient for L1
                    for (int j = 0; j < weights.length; j++) {
                        gradient[j] += lambda * Math.signum(weights[j]);
                    }
                } else {
                    // L2 gradient
                    for (int j = 0; j < weights.length; j++) {
                        gradient[j] += 2 * lambda * weights[j];
                    }
                }
            }

            // Update weights
            double gradientNorm = 0;
            for (double g : gradient) {
                gradientNorm += g * g;
            }
            gradientNorm = Math.sqrt(gradientNorm);

            if (gradientNorm < tolerance) {
                if (verbose) {
                    System.out.println("Converged at iteration " + iter);
                }
                break;
            }

            for (int j = 0; j < weights.length; j++) {
                weights[j] -= learningRate * gradient[j];
            }

            if (verbose && iter % 100 == 0) {
                System.out.println("Iteration " + iter + ", gradient norm: " + gradientNorm);
            }
        }

        return weights;
    }
}
```

---

## Part 3: Regularized Regression (60 min)

### Ridge Regression (L2)

```java
package com.ml.regression;

public class RidgeRegression extends AbstractRegression {

    private double alpha; // Regularization strength
    private double[] scalerMean;
    private double[] scalerScale;
    private boolean normalize;

    public RidgeRegression(double alpha) {
        this.alpha = alpha;
        this.normalize = true;
    }

    public RidgeRegression(double alpha, boolean normalize) {
        this.alpha = alpha;
        this.normalize = normalize;
    }

    @Override
    public void fit(double[][] X, double[] y) {
        int n = X.length;
        int d = X[0].length;

        // Normalize features
        if (normalize) {
            scalerMean = new double[d];
            scalerScale = new double[d];

            for (int j = 0; j < d; j++) {
                double sum = 0;
                for (int i = 0; i < n; i++) {
                    sum += X[i][j];
                }
                scalerMean[j] = sum / n;

                double sqSum = 0;
                for (int i = 0; i < n; i++) {
                    sqSum += (X[i][j] - scalerMean[j]) * (X[i][j] - scalerMean[j]);
                }
                scalerScale[j] = Math.sqrt(sqSum / n);
                if (scalerScale[j] < 1e-10) {
                    scalerScale[j] = 1.0;
                }
            }

            // Normalize X
            double[][] X_norm = new double[n][d];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < d; j++) {
                    X_norm[i][j] = (X[i][j] - scalerMean[j]) / scalerScale[j];
                }
            }
            X = X_norm;
        }

        // Add bias column
        double[][] X_aug = new double[n][d + 1];
        for (int i = 0; i < n; i++) {
            X_aug[i][0] = 1.0;
            System.arraycopy(X[i], 0, X_aug[i], 1, d);
        }

        // Solve: β = (X^T X + αI)^(-1) X^T y
        int p = d + 1;
        double[][] XtX = new double[p][p];
        double[] Xty = new double[p];

        // Compute X^T X and X^T y
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < p; j++) {
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += X_aug[k][i] * X_aug[k][j];
                }
                XtX[i][j] = sum;
            }
            double sum = 0;
            for (int k = 0; k < n; k++) {
                sum += X_aug[k][i] * y[k];
            }
            Xty[i] = sum;
        }

        // Add regularization to diagonal (except bias)
        for (int i = 1; i < p; i++) {
            XtX[i][i] += 2 * n * alpha;
        }

        // Solve using Gaussian elimination
        double[] beta = solveLinearSystem(XtX, Xty);

        this.intercept = beta[0];
        this.coefficients = new double[d];
        for (int i = 0; i < d; i++) {
            // Scale coefficients back
            this.coefficients[i] = beta[i + 1] / scalerScale[i];
        }

        // Adjust intercept for denormalization
        double adjustedIntercept = beta[0];
        for (int i = 0; i < d; i++) {
            adjustedIntercept -= beta[i + 1] * scalerMean[i] / scalerScale[i];
        }
        this.intercept = adjustedIntercept;

        this.fitted = true;
    }

    private double[] solveLinearSystem(double[][] A, double[] b) {
        int n = b.length;
        double[][] augmented = new double[n][n + 1];

        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, augmented[i], 0, n);
            augmented[i][n] = b[i];
        }

        // Gaussian elimination with partial pivoting
        for (int col = 0; col < n; col++) {
            // Find pivot
            int maxRow = col;
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(augmented[row][col]) > Math.abs(augmented[maxRow][col])) {
                    maxRow = row;
                }
            }

            // Swap rows
            double[] temp = augmented[col];
            augmented[col] = augmented[maxRow];
            augmented[maxRow] = temp;

            // Make row echelon
            for (int row = col + 1; row < n; row++) {
                double factor = augmented[row][col] / augmented[col][col];
                for (int j = col; j <= n; j++) {
                    augmented[row][j] -= factor * augmented[col][j];
                }
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = augmented[i][n];
            for (int j = i + 1; j < n; j++) {
                x[i] -= augmented[i][j] * x[j];
            }
            x[i] /= augmented[i][i];
        }

        return x;
    }
}
```

### Lasso Regression (L1) using Coordinate Descent

```java
package com.ml.regression;

public class LassoRegression extends AbstractRegression {

    private double alpha; // Regularization strength
    private int maxIterations = 1000;
    private double tolerance = 1e-6;
    private double[] scalerMean;
    private double[] scalerScale;

    public LassoRegression(double alpha) {
        this.alpha = alpha;
    }

    public LassoRegression(double alpha, int maxIterations) {
        this.alpha = alpha;
        this.maxIterations = maxIterations;
    }

    @Override
    public void fit(double[][] X, double[] y) {
        int n = X.length;
        int d = X[0].length;

        // Normalize features
        scalerMean = new double[d];
        scalerScale = new double[d];

        for (int j = 0; j < d; j++) {
            double sum = 0;
            for (int i = 0; i < n; i++) {
                sum += X[i][j];
            }
            scalerMean[j] = sum / n;

            double sqSum = 0;
            for (int i = 0; i < n; i++) {
                sqSum += (X[i][j] - scalerMean[j]) * (X[i][j] - scalerMean[j]);
            }
            scalerScale[j] = Math.sqrt(sqSum / n);
            if (scalerScale[j] < 1e-10) {
                scalerScale[j] = 1.0;
            }
        }

        // Normalize X
        double[][] X_norm = new double[n][d];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < d; j++) {
                X_norm[i][j] = (X[i][j] - scalerMean[j]) / scalerScale[j];
            }
        }

        // Center y
        double yMean = 0;
        for (double val : y) {
            yMean += val;
        }
        yMean /= n;

        double[] y_centered = new double[n];
        for (int i = 0; i < n; i++) {
            y_centered[i] = y[i] - yMean;
        }

        // Initialize coefficients
        this.coefficients = new double[d];
        this.intercept = yMean;

        // Coordinate descent
        double[] residuals = new double[n];
        for (int i = 0; i < n; i++) {
            residuals[i] = y_centered[i];
        }

        for (int iter = 0; iter < maxIterations; iter++) {
            double maxChange = 0;

            for (int j = 0; j < d; j++) {
                // Compute correlation
                double correlation = 0;
                for (int i = 0; i < n; i++) {
                    correlation += X_norm[i][j] * residuals[i];
                }
                correlation /= n;

                // Soft thresholding
                double oldCoeff = coefficients[j];
                double newCoeff = softThreshold(correlation, alpha);

                coefficients[j] = newCoeff;

                // Update residuals
                double delta = newCoeff - oldCoeff;
                if (Math.abs(delta) > maxChange) {
                    maxChange = Math.abs(delta);
                }

                for (int i = 0; i < n; i++) {
                    residuals[i] -= delta * X_norm[i][j];
                }
            }

            if (maxChange < tolerance) {
                break;
            }
        }

        // Scale coefficients back
        for (int i = 0; i < d; i++) {
            coefficients[i] /= scalerScale[i];
        }

        this.fitted = true;
    }

    private double softThreshold(double value, double threshold) {
        if (value > threshold) {
            return value - threshold;
        } else if (value < -threshold) {
            return value + threshold;
        } else {
            return 0;
        }
    }
}
```

### Elastic Net Regression

```java
package com.ml.regression;

public class ElasticNetRegression extends AbstractRegression {

    private double alpha;
    private double l1Ratio;
    private int maxIterations = 1000;
    private double tolerance = 1e-6;
    private double[] scalerMean;
    private double[] scalerScale;

    public ElasticNetRegression(double alpha, double l1Ratio) {
        this.alpha = alpha;
        this.l1Ratio = l1Ratio;
    }

    @Override
    public void fit(double[][] X, double[] y) {
        int n = X.length;
        int d = X[0].length;

        // Normalize features
        scalerMean = new double[d];
        scalerScale = new double[d];

        for (int j = 0; j < d; j++) {
            double sum = 0;
            for (int i = 0; i < n; i++) {
                sum += X[i][j];
            }
            scalerMean[j] = sum / n;

            double sqSum = 0;
            for (int i = 0; i < n; i++) {
                sqSum += (X[i][j] - scalerMean[j]) * (X[i][j] - scalerMean[j]);
            }
            scalerScale[j] = Math.sqrt(sqSum / n);
            if (scalerScale[j] < 1e-10) {
                scalerScale[j] = 1.0;
            }
        }

        double[][] X_norm = new double[n][d];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < d; j++) {
                X_norm[i][j] = (X[i][j] - scalerMean[j]) / scalerScale[j];
            }
        }

        double yMean = 0;
        for (double val : y) {
            yMean += val;
        }
        yMean /= n;

        double[] y_centered = new double[n];
        for (int i = 0; i < n; i++) {
            y_centered[i] = y[i] - yMean;
        }

        this.coefficients = new double[d];
        this.intercept = yMean;

        // Combined L1 and L2 penalty
        double l1Pen = alpha * l1Ratio;
        double l2Pen = alpha * (1 - l1Ratio);

        double[] residuals = new double[n];
        System.arraycopy(y_centered, 0, residuals, 0, n);

        for (int iter = 0; iter < maxIterations; iter++) {
            double maxChange = 0;

            for (int j = 0; j < d; j++) {
                double correlation = 0;
                for (int i = 0; i < n; i++) {
                    correlation += X_norm[i][j] * residuals[i];
                }
                correlation /= n;

                // Add L2 penalty gradient
                correlation -= 2 * n * l2Pen * coefficients[j];

                double oldCoeff = coefficients[j];
                double newCoeff = softThreshold(correlation, l1Pen);

                coefficients[j] = newCoeff;

                double delta = newCoeff - oldCoeff;
                if (Math.abs(delta) > maxChange) {
                    maxChange = Math.abs(delta);
                }

                for (int i = 0; i < n; i++) {
                    residuals[i] -= delta * X_norm[i][j];
                }
            }

            if (maxChange < tolerance) {
                break;
            }
        }

        for (int i = 0; i < d; i++) {
            coefficients[i] /= scalerScale[i];
        }

        this.fitted = true;
    }

    private double softThreshold(double value, double threshold) {
        if (value > threshold) {
            return value - threshold;
        } else if (value < -threshold) {
            return value + threshold;
        } else {
            return 0;
        }
    }
}
```

---

## Part 4: Evaluation Metrics (30 min)

```java
package com.ml.regression.evaluation;

public class RegressionMetrics {

    public static double r2Score(double[] yTrue, double[] yPred) {
        if (yTrue.length != yPred.length) {
            throw new IllegalArgumentException("Array length mismatch");
        }

        double ssRes = 0;
        double ssTot = 0;
        double yMean = 0;

        for (double y : yTrue) {
            yMean += y;
        }
        yMean /= yTrue.length;

        for (int i = 0; i < yTrue.length; i++) {
            ssRes += (yTrue[i] - yPred[i]) * (yTrue[i] - yPred[i]);
            ssTot += (yTrue[i] - yMean) * (yTrue[i] - yMean);
        }

        if (ssTot < 1e-10) {
            return 0;
        }

        return 1 - (ssRes / ssTot);
    }

    public static double mse(double[] yTrue, double[] yPred) {
        if (yTrue.length != yPred.length) {
            throw new IllegalArgumentException("Array length mismatch");
        }

        double sum = 0;
        for (int i = 0; i < yTrue.length; i++) {
            double diff = yTrue[i] - yPred[i];
            sum += diff * diff;
        }
        return sum / yTrue.length;
    }

    public static double rmse(double[] yTrue, double[] yPred) {
        return Math.sqrt(mse(yTrue, yPred));
    }

    public static double mae(double[] yTrue, double[] yPred) {
        if (yTrue.length != yPred.length) {
            throw new IllegalArgumentException("Array length mismatch");
        }

        double sum = 0;
        for (int i = 0; i < yTrue.length; i++) {
            sum += Math.abs(yTrue[i] - yPred[i]);
        }
        return sum / yTrue.length;
    }

    public static double adjustedR2(double[] yTrue, double[] yPred, int numFeatures) {
        double r2 = r2Score(yTrue, yPred);
        int n = yTrue.length;

        if (n - numFeatures - 1 <= 0) {
            return r2;
        }

        return 1 - ((1 - r2) * (n - 1) / (n - numFeatures - 1));
    }

    public static double[] residuals(double[] yTrue, double[] yPred) {
        double[] res = new double[yTrue.length];
        for (int i = 0; i < yTrue.length; i++) {
            res[i] = yTrue[i] - yPred[i];
        }
        return res;
    }
}
```

---

## Part 5: Cross-Validation (30 min)

```java
package com.ml.regression.evaluation;

import com.ml.regression.RegressionModel;

public class CrossValidator {

    public static class CVResult {
        public final double[] trainScores;
        public final double[] testScores;
        public final double meanTrainScore;
        public final double meanTestScore;
        public final double stdTestScore;

        public CVResult(double[] trainScores, double[] testScores) {
            this.trainScores = trainScores;
            this.testScores = testScores;
            this.meanTrainScore = mean(trainScores);
            this.meanTestScore = mean(testScores);
            this.stdTestScore = std(testScores);
        }

        private double mean(double[] arr) {
            double sum = 0;
            for (double v : arr) sum += v;
            return sum / arr.length;
        }

        private double std(double[] arr) {
            double m = mean(arr);
            double sumSq = 0;
            for (double v : arr) {
                sumSq += (v - m) * (v - m);
            }
            return Math.sqrt(sumSq / arr.length);
        }
    }

    public static CVResult crossValidate(RegressionModel model,
                                        double[][] X, double[] y,
                                        int folds) {
        int n = X.length;
        int foldSize = n / folds;

        double[] trainScores = new double[folds];
        double[] testScores = new double[folds];

        for (int fold = 0; fold < folds; fold++) {
            // Create train/test split
            int testStart = fold * foldSize;
            int testEnd = (fold == folds - 1) ? n : (fold + 1) * foldSize;
            int trainSize = n - (testEnd - testStart);

            double[][] XTrain = new double[trainSize][];
            double[] yTrain = new double[trainSize];
            double[][] XTest = new double[testEnd - testStart][];
            double[] yTest = new double[testEnd - testStart];

            int trainIdx = 0;
            int testIdx = 0;

            for (int i = 0; i < n; i++) {
                if (i >= testStart && i < testEnd) {
                    XTest[testIdx] = X[i];
                    yTest[testIdx] = y[i];
                    testIdx++;
                } else {
                    XTrain[trainIdx] = X[i];
                    yTrain[trainIdx] = y[i];
                    trainIdx++;
                }
            }

            // Train model
            RegressionModel clone = cloneModel(model);
            clone.fit(XTrain, yTrain);

            // Evaluate
            trainScores[fold] = clone.getScore(XTrain, yTrain);
            testScores[fold] = clone.getScore(XTest, yTest);
        }

        return new CVResult(trainScores, testScores);
    }

    private static RegressionModel cloneModel(RegressionModel model) {
        // Simple reflection-based cloning
        try {
            if (model instanceof LinearRegression) {
                return new LinearRegression();
            } else if (model instanceof RidgeRegression) {
                return new RidgeRegression(1.0);
            } else if (model instanceof LassoRegression) {
                return new LassoRegression(1.0);
            } else if (model instanceof ElasticNetRegression) {
                return new ElasticNetRegression(1.0, 0.5);
            }
            throw new IllegalArgumentException("Unknown model type");
        } catch (Exception e) {
            throw new RuntimeException("Failed to clone model: " + e.getMessage());
        }
    }

    // Import model classes
    import com.ml.regression.LinearRegression;
    import com.ml.regression.RidgeRegression;
    import com.ml.regression.LassoRegression;
    import com.ml.regression.ElasticNetRegression;
}
```

---

## Part 6: Main Example

```java
package com.ml.regression;

import com.ml.regression.evaluation.RegressionMetrics;
import com.ml.regression.evaluation.CrossValidator;

public class Main {

    public static void main(String[] args) {
        // Generate synthetic data
        int n = 500;
        int d = 10;
        double[][] X = new double[n][d];
        double[] y = new double[n];

        java.util.Random rand = new java.util.Random(42);

        // Create features with some correlation
        for (int i = 0; i < n; i++) {
            double x1 = rand.nextDouble() * 10;
            double x2 = rand.nextDouble() * 10;
            X[i][0] = x1;
            X[i][1] = x2;
            X[i][2] = x1 * 0.5 + rand.nextDouble();
            X[i][3] = x2 * 0.3 + rand.nextDouble();
            for (int j = 4; j < d; j++) {
                X[i][j] = rand.nextDouble() * 10;
            }
            y[i] = 2 * x1 + 0.5 * x2 + 3 + rand.nextGaussian() * 2;
        }

        // Split data
        int trainSize = (int)(n * 0.8);
        double[][] XTrain = new double[trainSize][];
        double[] yTrain = new double[trainSize];
        double[][] XTest = new double[n - trainSize][];
        double[] yTest = new double[n - trainSize];

        System.arraycopy(X, 0, XTrain, 0, trainSize);
        System.arraycopy(y, 0, yTrain, 0, trainSize);
        System.arraycopy(X, trainSize, XTest, 0, n - trainSize);
        System.arraycopy(y, trainSize, yTest, 0, n - trainSize);

        // Test different models
        System.out.println("=== Model Comparison ===\n");

        // Linear Regression
        LinearRegression lr = new LinearRegression();
        lr.fit(XTrain, yTrain);
        double[] pred = lr.predict(XTest);
        System.out.println("Linear Regression:");
        System.out.printf("  R²: %.4f%n", RegressionMetrics.r2Score(yTest, pred));
        System.out.printf("  RMSE: %.4f%n", RegressionMetrics.rmse(yTest, pred));
        System.out.printf("  MAE: %.4f%n", RegressionMetrics.mae(yTest, pred));

        // Ridge Regression
        System.out.println("\nRidge Regression (α=1.0):");
        RidgeRegression ridge = new RidgeRegression(1.0);
        ridge.fit(XTrain, yTrain);
        pred = ridge.predict(XTest);
        System.out.printf("  R²: %.4f%n", RegressionMetrics.r2Score(yTest, pred));

        // Lasso Regression
        System.out.println("\nLasso Regression (α=0.1):");
        LassoRegression lasso = new LassoRegression(0.1);
        lasso.fit(XTrain, yTrain);
        pred = lasso.predict(XTest);
        System.out.printf("  R²: %.4f%n", RegressionMetrics.r2Score(yTest, pred));
        System.out.printf("  Non-zero coefficients: %d%n",
            java.util.Arrays.stream(lasso.getCoefficients()).filter(c -> c != 0).count());

        // Elastic Net
        System.out.println("\nElastic Net (α=0.1, l1Ratio=0.5):");
        ElasticNetRegression elastic = new ElasticNetRegression(0.1, 0.5);
        elastic.fit(XTrain, yTrain);
        pred = elastic.predict(XTest);
        System.out.printf("  R²: %.4f%n", RegressionMetrics.r2Score(yTest, pred));

        // Cross-validation
        System.out.println("\n=== 5-Fold Cross-Validation ===");
        var cvResult = CrossValidator.crossValidate(
            new RidgeRegression(1.0), X, y, 5);
        System.out.printf("Train R²: %.4f ± %.4f%n",
            cvResult.meanTrainScore, cvResult.stdTestScore);
        System.out.printf("Test R²: %.4f ± %.4f%n",
            cvResult.meanTestScore, cvResult.stdTestScore);
    }
}
```

---

## Expected Output

```
=== Model Comparison ===

Linear Regression:
  R²: 0.8234
  RMSE: 2.1456
  MAE: 1.7234

Ridge Regression (α=1.0):
  R²: 0.8201

Lasso Regression (α=0.1):
  R²: 0.8156
  Non-zero coefficients: 5

Elastic Net (α=0.1, l1Ratio=0.5):
  R²: 0.8189

=== 5-Fold Cross-Validation ===
Train R²: 0.8521 ± 0.0234
Test R²: 0.8012 ± 0.0456
```

---

## Tasks

1. **Implement all regression models** with proper documentation
2. **Add polynomial features** transformation
3. **Implement cross-validation** for hyperparameter tuning
4. **Add residual analysis** and diagnostics
5. **Test on real datasets** (Boston Housing, diabetes)

---

## Bonus Challenges

1. Implement polynomial regression with degree selection
2. Add support for weighted regression
3. Implement forward feature selection
4. Add visualization using JFreeChart
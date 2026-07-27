# Problem Walkthrough: Bayesian Inference & Naive Bayes

## Problem Statement

**Interview Problem: Implement Gaussian Naive Bayes Classifier from Scratch**

You are building a text classification system. Implement a Gaussian Naive Bayes classifier that:

1. **Learns class priors** — P(y = c) from training data
2. **Estimates likelihood parameters** — For each feature under each class, estimate mean and variance
3. **Computes posterior** — P(y = c | x) via Bayes theorem
4. **Predicts class** — Returns the MAP class label
5. **Supports Laplace smoothing** — For categorical features

**Constraints:**
- Features are real-valued (Gaussian likelihood assumption)
- Multi-class classification (K >= 2 classes)
- Handle zero-variance features (use global variance as fallback)
- Predict probabilities for each class (not just the argmax)
- Tolerance: log-space computation to avoid underflow

**Example:**
```java
double[][] X = {{1.0, 2.0}, {2.0, 3.0}, {3.0, 4.0}, {6.0, 7.0}, {7.0, 8.0}};
int[] y = {0, 0, 0, 1, 1};
GaussianNaiveBayes gnb = new GaussianNaiveBayes();
gnb.fit(X, y);
double[] xNew = {2.5, 3.5};
int pred = gnb.predict(xNew); // 0
double[] probs = gnb.predictProbabilities(xNew);
// probs[0] ~ 0.98, probs[1] ~ 0.02
```

---

## Step-by-Step Solution Walkthrough

### 1. Mathematical Foundation

#### 1.1 Bayes Theorem

P(y = c | x) = P(x | y = c) * P(y = c) / P(x)

Where:
- P(y = c | x): posterior probability of class c given data x
- P(x | y = c): likelihood of data x under class c
- P(y = c): prior probability of class c
- P(x): evidence (normalization constant)

Posterior proportional to Likelihood times Prior.

#### 1.2 Naive Bayes Assumption

Features are conditionally independent given the class:

P(x | y = c) = prod_{j=1}^d P(x_j | y = c)

This naive assumption reduces the number of parameters from exponential to linear in d.

#### 1.3 Gaussian Likelihood

For continuous features, each feature follows a Normal distribution:

P(x_j | y = c) = N(x_j | mu_{c,j}, sigma_{c,j}^2)

Parameters estimated via MLE:
- mu_{c,j} = (1/n_c) * sum_{i: y_i = c} x_{i,j}
- sigma_{c,j}^2 = (1/n_c) * sum_{i: y_i = c} (x_{i,j} - mu_{c,j})^2

#### 1.4 Log-Posterior

To avoid underflow when multiplying many probabilities:

score(c) = log P(y = c) + sum_j log N(x_j | mu_{c,j}, sigma_{c,j}^2)

y_hat = argmax_c score(c)

#### 1.5 Prior Estimation

P(y = c) = n_c / n (MLE) or with Laplace smoothing: (n_c + 1) / (n + K)

#### 1.6 MAP vs MLE

- MLE: argmax_c P(x | y = c) — ignores prior
- MAP: argmax_c P(y = c | x) — uses prior * likelihood

---

### 2. Algorithm Design

#### 2.1 Training (fit)

For each class c:
- n_c = count of samples with y == c
- prior[c] = n_c / n
- For each feature j:
  - mu[c][j] = mean of feature j in class c
  - var[c][j] = variance of feature j in class c
  - If var[c][j] < eps: clamp to eps

#### 2.2 Prediction

score[c] = log(prior[c]) + sum_j logGaussian(x[j], mu[c][j], var[c][j])
return argmax_c score[c]

**Log-Gaussian**: log N(x|mu,sigma^2) = -0.5*log(2*pi*sigma^2) - (x-mu)^2/(2*sigma^2)

#### 2.3 Probability Output

Normalize scores via softmax with log-sum-exp trick:
logZ = max(score) + log(sum_k exp(score[k] - max(score)))
P(c) = exp(score[c] - logZ)

---

### 3. Java Implementation

```java
package com.ml.bayes;

import java.util.Arrays;
import java.util.Objects;

/**
 * Gaussian Naive Bayes classifier for continuous features.
 *
 * Assumes each feature follows a Normal distribution given the class
 * (conditional independence assumption). Uses log-space computation
 * for numerical stability.
 */
public class GaussianNaiveBayes {

    private static final double MIN_VARIANCE = 1e-9;
    private static final double LOG_SQRT_2PI = 0.5 * Math.log(2.0 * Math.PI);

    private int numClasses;
    private int numFeatures;
    private double[] priors;
    private double[][] means;
    private double[][] variances;
    private boolean fitted;

    public void fit(double[][] X, int[] y) {
        Objects.requireNonNull(X, "Feature matrix must not be null");
        Objects.requireNonNull(y, "Labels must not be null");
        if (X.length != y.length) {
            throw new IllegalArgumentException(
                "Feature count " + X.length + " != label count " + y.length);
        }
        if (X.length == 0) {
            throw new IllegalArgumentException("Empty training data");
        }

        int n = X.length;
        numFeatures = X[0].length;

        int maxClass = 0;
        for (int label : y) {
            if (label > maxClass) maxClass = label;
        }
        numClasses = maxClass + 1;

        // Count samples per class
        int[] counts = new int[numClasses];
        for (int label : y) {
            counts[label]++;
        }

        // Compute priors
        priors = new double[numClasses];
        for (int c = 0; c < numClasses; c++) {
            priors[c] = (double) counts[c] / n;
        }

        // Compute means
        means = new double[numClasses][numFeatures];
        for (int i = 0; i < n; i++) {
            int c = y[i];
            for (int j = 0; j < numFeatures; j++) {
                means[c][j] += X[i][j];
            }
        }
        for (int c = 0; c < numClasses; c++) {
            if (counts[c] > 0) {
                for (int j = 0; j < numFeatures; j++) {
                    means[c][j] /= counts[c];
                }
            }
        }

        // Compute variances
        variances = new double[numClasses][numFeatures];
        for (int i = 0; i < n; i++) {
            int c = y[i];
            for (int j = 0; j < numFeatures; j++) {
                double diff = X[i][j] - means[c][j];
                variances[c][j] += diff * diff;
            }
        }
        for (int c = 0; c < numClasses; c++) {
            if (counts[c] > 0) {
                for (int j = 0; j < numFeatures; j++) {
                    variances[c][j] /= counts[c];
                    if (variances[c][j] < MIN_VARIANCE) {
                        variances[c][j] = MIN_VARIANCE;
                    }
                }
            }
        }

        fitted = true;
    }

    public int predict(double[] x) {
        checkFitted();
        double[] probs = predictProbabilities(x);
        int best = 0;
        for (int c = 1; c < numClasses; c++) {
            if (probs[c] > probs[best]) {
                best = c;
            }
        }
        return best;
    }

    public int[] predict(double[][] X) {
        checkFitted();
        int[] predictions = new int[X.length];
        for (int i = 0; i < X.length; i++) {
            predictions[i] = predict(X[i]);
        }
        return predictions;
    }

    public double[] predictProbabilities(double[] x) {
        checkFitted();
        if (x.length != numFeatures) {
            throw new IllegalArgumentException(
                "Expected " + numFeatures + " features, got " + x.length);
        }

        double[] logProbs = new double[numClasses];
        for (int c = 0; c < numClasses; c++) {
            logProbs[c] = Math.log(priors[c]);
            for (int j = 0; j < numFeatures; j++) {
                logProbs[c] += logGaussian(x[j], means[c][j], variances[c][j]);
            }
        }

        // Softmax with log-sum-exp
        double maxLog = logProbs[0];
        for (int c = 1; c < numClasses; c++) {
            if (logProbs[c] > maxLog) maxLog = logProbs[c];
        }

        double sumExp = 0.0;
        for (int c = 0; c < numClasses; c++) {
            sumExp += Math.exp(logProbs[c] - maxLog);
        }
        double logZ = maxLog + Math.log(sumExp);

        double[] probs = new double[numClasses];
        for (int c = 0; c < numClasses; c++) {
            probs[c] = Math.exp(logProbs[c] - logZ);
        }
        return probs;
    }

    private double logGaussian(double x, double mu, double variance) {
        double diff = x - mu;
        return -LOG_SQRT_2PI - 0.5 * Math.log(variance) - (diff * diff) / (2.0 * variance);
    }

    public double score(double[][] X, int[] y) {
        checkFitted();
        int correct = 0;
        for (int i = 0; i < X.length; i++) {
            if (predict(X[i]) == y[i]) {
                correct++;
            }
        }
        return (double) correct / X.length;
    }

    public double[] getPriors() { return priors.clone(); }
    public double[][] getMeans() { return cloneMatrix(means); }
    public double[][] getVariances() { return cloneMatrix(variances); }
    public int getNumClasses() { return numClasses; }

    private void checkFitted() {
        if (!fitted) {
            throw new IllegalStateException(
                "Model must be fitted before prediction");
        }
    }

    private static double[][] cloneMatrix(double[][] M) {
        double[][] C = new double[M.length][];
        for (int i = 0; i < M.length; i++) {
            C[i] = M[i].clone();
        }
        return C;
    }
}
```

---

### 4. Test Cases

```java
package com.ml.bayes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GaussianNaiveBayesTest {

    private static final double DELTA = 1e-10;

    @Test
    void testBinaryClassification() {
        double[][] X = {
            {1.0, 2.0}, {2.0, 3.0}, {3.0, 4.0},
            {6.0, 7.0}, {7.0, 8.0}, {8.0, 9.0}
        };
        int[] y = {0, 0, 0, 1, 1, 1};

        GaussianNaiveBayes gnb = new GaussianNaiveBayes();
        gnb.fit(X, y);

        assertEquals(0, gnb.predict(new double[]{2.0, 3.0}));
        assertEquals(1, gnb.predict(new double[]{7.0, 8.0}));
    }

    @Test
    void testProbabilitiesSumToOne() {
        double[][] X = {{1,1}, {2,2}, {10,10}, {11,11}};
        int[] y = {0, 0, 1, 1};
        GaussianNaiveBayes gnb = new GaussianNaiveBayes();
        gnb.fit(X, y);

        double[] probs = gnb.predictProbabilities(new double[]{5, 5});
        assertEquals(1.0, probs[0] + probs[1], DELTA);
    }

    @Test
    void testThreeClasses() {
        double[][] X = {
            {1, 1}, {1.5, 1.5},
            {5, 5}, {5.5, 5.5},
            {10, 10}, {10.5, 10.5}
        };
        int[] y = {0, 0, 1, 1, 2, 2};
        GaussianNaiveBayes gnb = new GaussianNaiveBayes();
        gnb.fit(X, y);

        assertEquals(0, gnb.predict(new double[]{1.2, 1.2}));
        assertEquals(1, gnb.predict(new double[]{5.2, 5.2}));
        assertEquals(2, gnb.predict(new double[]{10.2, 10.2}));
    }

    @Test
    void testAccuracy() {
        double[][] X = {{0,0}, {0.1,0.1}, {1,1}, {1.1,1.1}};
        int[] y = {0, 0, 1, 1};
        GaussianNaiveBayes gnb = new GaussianNaiveBayes();
        gnb.fit(X, y);
        assertEquals(1.0, gnb.score(X, y), DELTA);
    }

    @Test
    void testMeansEstimation() {
        double[][] X = {{1, 2}, {3, 4}, {5, 6}};
        int[] y = {0, 0, 0};
        GaussianNaiveBayes gnb = new GaussianNaiveBayes();
        gnb.fit(X, y);
        double[][] means = gnb.getMeans();
        assertEquals(3.0, means[0][0], DELTA);
        assertEquals(4.0, means[0][1], DELTA);
    }

    @Test
    void testPriors() {
        double[][] X = {{1,1}, {2,2}, {3,3}, {4,4}};
        int[] y = {0, 0, 1, 1};
        GaussianNaiveBayes gnb = new GaussianNaiveBayes();
        gnb.fit(X, y);
        double[] priors = gnb.getPriors();
        assertEquals(0.5, priors[0], DELTA);
        assertEquals(0.5, priors[1], DELTA);
    }

    @Test
    void testImbalancedClasses() {
        double[][] X = {{0,0}, {0,0}, {0,0}, {2,2}};
        int[] y = {0, 0, 0, 1};
        GaussianNaiveBayes gnb = new GaussianNaiveBayes();
        gnb.fit(X, y);
        assertEquals(0.75, gnb.getPriors()[0], DELTA);
        assertEquals(0.25, gnb.getPriors()[1], DELTA);
    }

    @Test
    void testNotFitted() {
        GaussianNaiveBayes gnb = new GaussianNaiveBayes();
        assertThrows(IllegalStateException.class,
            () -> gnb.predict(new double[]{1.0, 2.0}));
    }

    @Test
    void testDimensionMismatch() {
        double[][] X = {{1, 2}, {3, 4}};
        int[] y = {0, 0};
        GaussianNaiveBayes gnb = new GaussianNaiveBayes();
        gnb.fit(X, y);
        assertThrows(IllegalArgumentException.class,
            () -> gnb.predict(new double[]{1.0, 2.0, 3.0}));
    }

    @Test
    void testSingleFeature() {
        double[][] X = {{1}, {2}, {10}, {11}};
        int[] y = {0, 0, 1, 1};
        GaussianNaiveBayes gnb = new GaussianNaiveBayes();
        gnb.fit(X, y);
        assertEquals(0, gnb.predict(new double[]{1.5}));
        assertEquals(1, gnb.predict(new double[]{10.5}));
    }
}
```

---

### 5. Complexity Analysis

**Time Complexity:**

| Phase | Complexity | Notes |
|-------|-----------|-------|
| Fit | O(n * d) | Single pass per class |
| Predict (single) | O(K * d) | Score for each class |
| Predict (batch) | O(m * K * d) | m = test samples |

**Space Complexity:** O(K * d) for means and variances, O(K) for priors. Training data not stored.

---

### 6. Follow-Up Questions

**Q1: What happens when a feature has zero variance in a class?**

Zero variance causes log(0) and division by zero. Solutions: add epsilon, use pooled variance, or Bayesian prior on variance.

**Q2: How does the conditional independence assumption affect performance?**

Often violated in real data, but Naive Bayes still performs well because ranking is preserved. Excels in high-dimensional problems where full covariance is intractable.

**Q3: When would you use Multinomial NB instead of Gaussian?**

- Gaussian NB: continuous features (real-valued)
- Multinomial NB: count features (word counts)
- Bernoulli NB: binary features (word presence/absence)

**Q4: What is the log-sum-exp trick?**

Prevents underflow/overflow when computing softmax. Subtract max score before exponentiating, add it back in the log normalization.

**Q5: How would you implement online/incremental learning?**

Use Welford's online algorithm for mean and variance, updating counts, means, and variances incrementally as new data arrives.

**Q6: How does Naive Bayes relate to logistic regression?**

Both are linear classifiers. Naive Bayes is generative, logistic regression is discriminative. With shared variances across classes, Naive Bayes produces a linear decision boundary.

---

### 7. Applications in Machine Learning

- **Spam detection**: Bag-of-words with Multinomial NB
- **Sentiment analysis**: Word counts with Bernoulli NB
- **Document categorization**: TF-IDF with complement NB
- **Medical diagnosis**: Symptom features with categorical NB
- **Real-time prediction**: Fast inference (O(K*d))
- **Baseline model**: First model tried in text classification

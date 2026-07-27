# Problem Walkthrough: Information Theory Metrics

## Problem Statement

**Interview Problem: Implement Information Theory Metrics for Machine Learning**

You are building a feature selection tool. Implement an `InformationTheory` utility class that computes fundamental information-theoretic quantities:

1. **Entropy** — H(X) = -sum P(x) * log P(x) for discrete distributions
2. **Joint Entropy** — H(X, Y) = -sum P(x,y) * log P(x,y)
3. **Conditional Entropy** — H(Y | X) = H(X, Y) - H(X)
4. **Cross-Entropy** — H(P, Q) = -sum P(x) * log Q(x)
5. **KL Divergence** — D_KL(P || Q) = sum P(x) * log(P(x)/Q(x))
6. **Jensen-Shannon Divergence** — Symmetric, finite version of KL
7. **Mutual Information** — I(X; Y) = H(X) - H(X | Y)

**Constraints:**
- Estimate all quantities from discrete count data
- Handle zero counts with smoothing
- Use natural log (nats) or log base 2 (bits) — configurable
- Validate probability distributions (must sum to ~1 within tolerance)

**Example:**
```java
double[] P = {0.8, 0.2};
double[] Q = {0.5, 0.5};
double h = InformationTheory.entropy(P, 2); // 0.722 bits
double kl = InformationTheory.klDivergence(P, Q, 2);
double ce = InformationTheory.crossEntropy(P, Q, 2);
```

---

## Step-by-Step Solution Walkthrough

### 1. Mathematical Foundation

#### 1.1 Shannon Entropy

H(X) = -sum_{x} P(x) * log_b P(x)

**Properties:**
- H(X) >= 0, H(X) = 0 iff X is deterministic
- H(X) <= log_b |X| (max when uniform)

#### 1.2 Joint and Conditional Entropy

H(X, Y) = -sum_{x,y} P(x,y) * log_b P(x,y)
H(Y | X) = sum_x P(x) * H(Y | X = x) = H(X, Y) - H(X)

**Chain rule**: H(X, Y) = H(X) + H(Y | X)

#### 1.3 Cross-Entropy and KL Divergence

H(P, Q) = -sum P(x) * log Q(x) = H(P) + D_KL(P || Q)
D_KL(P || Q) = sum P(x) * log (P(x) / Q(x))

**Properties:**
- D_KL(P || Q) >= 0 (Gibbs' inequality), = 0 iff P = Q
- Asymmetric: D_KL(P||Q) != D_KL(Q||P)
- H(P, Q) >= H(P)

#### 1.4 Jensen-Shannon Divergence

JSD(P || Q) = 0.5 * D_KL(P || M) + 0.5 * D_KL(Q || M) where M = (P+Q)/2

**Properties:**
- Symmetric, 0 <= JSD <= log_b(2)
- sqrt(JSD) is a metric

#### 1.5 Mutual Information

I(X; Y) = H(X) - H(X | Y) = H(X) + H(Y) - H(X, Y)
= sum_{x,y} P(x,y) * log_b (P(x,y) / (P(x) * P(y)))

**Properties:**
- I(X; Y) >= 0, = 0 iff X and Y are independent
- I(X; X) = H(X)

---

### 2. Algorithm Design

#### 2.1 Entropy

```
function entropy(probs, base):
    return -sum(p * log_b(p) for p in probs if p > 0)
```

Zero handling: 0 * log(0) = 0.

#### 2.2 KL Divergence

```
function klDivergence(P, Q, base):
    return sum(P[i] * log_b(P[i]/Q[i]) for i where P[i] > 0)
```

If P[i] > 0 and Q[i] = 0: return inf.

#### 2.3 Mutual Information from Counts

Build joint probability matrix, compute marginals, then:
I = sum_{x,y} P(x,y) * log_b(P(x,y) / (P(x) * P(y)))

---

### 3. Java Implementation

```java
package com.ml.info;

import java.util.Objects;

/**
 * Information theory metrics: entropy, cross-entropy, KL divergence,
 * Jensen-Shannon divergence, and mutual information.
 */
public final class InformationTheory {

    private InformationTheory() {}

    public static double entropy(double[] probs, double base) {
        validateDistribution(probs);
        double h = 0.0;
        for (double p : probs) {
            if (p > 0.0) {
                h -= p * log(p, base);
            }
        }
        return h;
    }

    public static double entropy(double[] probs) {
        return entropy(probs, Math.E);
    }

    public static double binaryEntropy(double p, double base) {
        if (p < 0 || p > 1) {
            throw new IllegalArgumentException(
                "p must be in [0, 1]: " + p);
        }
        if (p == 0 || p == 1) return 0.0;
        return -p * log(p, base) - (1 - p) * log(1 - p, base);
    }

    public static double jointEntropy(double[][] joint, double base) {
        Objects.requireNonNull(joint);
        double h = 0.0;
        for (double[] row : joint) {
            for (double p : row) {
                if (p > 0) {
                    h -= p * log(p, base);
                }
            }
        }
        return h;
    }

    public static double conditionalEntropy(double[][] joint, double base) {
        double[] px = marginalizeRows(joint);
        double hJoint = jointEntropy(joint, base);
        double hX = entropy(px, base);
        return hJoint - hX;
    }

    public static double crossEntropy(double[] P, double[] Q, double base) {
        validateSameLength(P, Q);
        double ce = 0.0;
        for (int i = 0; i < P.length; i++) {
            if (P[i] > 0) {
                if (Q[i] == 0) {
                    return Double.POSITIVE_INFINITY;
                }
                ce -= P[i] * log(Q[i], base);
            }
        }
        return ce;
    }

    public static double klDivergence(double[] P, double[] Q, double base) {
        validateSameLength(P, Q);
        double kl = 0.0;
        for (int i = 0; i < P.length; i++) {
            if (P[i] == 0.0) continue;
            if (Q[i] == 0.0) {
                return Double.POSITIVE_INFINITY;
            }
            kl += P[i] * log(P[i] / Q[i], base);
        }
        return kl;
    }

    public static double jsDivergence(double[] P, double[] Q, double base) {
        validateSameLength(P, Q);
        int n = P.length;
        double[] M = new double[n];
        for (int i = 0; i < n; i++) {
            M[i] = 0.5 * (P[i] + Q[i]);
        }
        return 0.5 * klDivergence(P, M, base)
             + 0.5 * klDivergence(Q, M, base);
    }

    public static double jsDistance(double[] P, double[] Q, double base) {
        return Math.sqrt(jsDivergence(P, Q, base));
    }

    public static double mutualInformation(double[][] joint, double base) {
        double[] px = marginalizeRows(joint);
        double[] py = marginalizeCols(joint);
        double mi = 0.0;
        for (int i = 0; i < joint.length; i++) {
            for (int j = 0; j < joint[i].length; j++) {
                double pxy = joint[i][j];
                if (pxy > 0 && px[i] > 0 && py[j] > 0) {
                    mi += pxy * log(pxy / (px[i] * py[j]), base);
                }
            }
        }
        return mi;
    }

    public static double mutualInformationFromSamples(
            int[] xValues, int[] yValues,
            int numX, int numY, double base) {
        int n = xValues.length;
        double[][] joint = new double[numX][numY];
        for (int i = 0; i < n; i++) {
            joint[xValues[i]][yValues[i]]++;
        }
        for (int i = 0; i < numX; i++) {
            for (int j = 0; j < numY; j++) {
                joint[i][j] /= n;
            }
        }
        return mutualInformation(joint, base);
    }

    private static double log(double x, double base) {
        if (base == Math.E) return Math.log(x);
        if (base == 2.0) return Math.log(x) / Math.log(2.0);
        return Math.log(x) / Math.log(base);
    }

    private static void validateDistribution(double[] probs) {
        Objects.requireNonNull(probs);
        double sum = 0.0;
        for (double p : probs) {
            if (p < 0) throw new IllegalArgumentException(
                "Negative probability: " + p);
            sum += p;
        }
        if (Math.abs(sum - 1.0) > 1e-8 && probs.length > 0) {
            throw new IllegalArgumentException(
                "Probabilities must sum to 1, got: " + sum);
        }
    }

    private static void validateSameLength(double[] a, double[] b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        if (a.length != b.length) {
            throw new IllegalArgumentException(
                "Length mismatch: " + a.length + " vs " + b.length);
        }
    }

    private static double[] marginalizeRows(double[][] joint) {
        int rows = joint.length;
        double[] px = new double[rows];
        for (int i = 0; i < rows; i++) {
            double sum = 0.0;
            for (double p : joint[i]) sum += p;
            px[i] = sum;
        }
        return px;
    }

    private static double[] marginalizeCols(double[][] joint) {
        int rows = joint.length;
        int cols = joint[0].length;
        double[] py = new double[cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                py[j] += joint[i][j];
            }
        }
        return py;
    }
}
```

---

### 4. Test Cases

```java
package com.ml.info;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InformationTheoryTest {

    private static final double DELTA = 1e-10;

    @Test
    void testEntropyUniform() {
        double[] probs = {0.25, 0.25, 0.25, 0.25};
        assertEquals(2.0, InformationTheory.entropy(probs, 2), DELTA);
    }

    @Test
    void testEntropyDeterministic() {
        assertEquals(0.0, InformationTheory.entropy(
            new double[]{1.0, 0.0, 0.0}, 2), DELTA);
    }

    @Test
    void testBinaryEntropy() {
        assertEquals(1.0, InformationTheory.binaryEntropy(0.5, 2), DELTA);
        assertEquals(0.0, InformationTheory.binaryEntropy(0.0, 2), DELTA);
        assertEquals(0.0, InformationTheory.binaryEntropy(1.0, 2), DELTA);
    }

    @Test
    void testCrossEntropy() {
        double[] P = {1.0, 0.0};
        double[] Q = {0.5, 0.5};
        assertEquals(1.0, InformationTheory.crossEntropy(P, Q, 2), DELTA);
        assertEquals(0.0, InformationTheory.crossEntropy(P, P, 2), DELTA);
    }

    @Test
    void testKLDivergence() {
        double[] P = {0.8, 0.2};
        double[] Q = {0.5, 0.5};
        double kl = InformationTheory.klDivergence(P, Q, 2);
        assertTrue(kl > 0);
        assertEquals(0.0, InformationTheory.klDivergence(P, P, 2), DELTA);
    }

    @Test
    void testKLDivergenceInfinite() {
        double[] P = {0.5, 0.5};
        double[] Q = {1.0, 0.0};
        assertEquals(Double.POSITIVE_INFINITY,
            InformationTheory.klDivergence(P, Q, 2));
    }

    @Test
    void testJSDivergenceSymmetry() {
        double[] P = {0.8, 0.2};
        double[] Q = {0.5, 0.5};
        double jsd = InformationTheory.jsDivergence(P, Q, Math.E);
        double jsdRev = InformationTheory.jsDivergence(Q, P, Math.E);
        assertEquals(jsd, jsdRev, DELTA);
        assertEquals(0.0, InformationTheory.jsDivergence(P, P, Math.E), DELTA);
    }

    @Test
    void testMutualInformationIndependent() {
        double[][] joint = {{0.25, 0.25}, {0.25, 0.25}};
        assertEquals(0.0, InformationTheory.mutualInformation(joint, 2), DELTA);
    }

    @Test
    void testMutualInformationDeterministic() {
        double[][] joint = {{0.5, 0.0}, {0.0, 0.5}};
        assertEquals(1.0, InformationTheory.mutualInformation(joint, 2), DELTA);
    }

    @Test
    void testJointEntropy() {
        double[][] joint = {{0.25, 0.25}, {0.25, 0.25}};
        assertEquals(2.0, InformationTheory.jointEntropy(joint, 2), DELTA);
    }

    @Test
    void testConditionalEntropy() {
        double[][] joint = {{0.5, 0.0}, {0.0, 0.5}};
        assertEquals(0.0, InformationTheory.conditionalEntropy(joint, 2), DELTA);
    }

    @Test
    void testInvalidDistribution() {
        assertThrows(IllegalArgumentException.class,
            () -> InformationTheory.entropy(new double[]{0.5, 0.6}, 2));
    }
}
```

---

### 5. Complexity Analysis

| Operation | Complexity |
|-----------|-----------|
| Entropy | O(K) |
| Joint Entropy | O(Kx * Ky) |
| Cross-Entropy / KL | O(K) |
| JS Divergence | O(K) |
| Mutual Information | O(Kx * Ky) |

Space: O(1) for most operations, O(Kx + Ky) for marginal calculations.

---

### 6. Follow-Up Questions

**Q1: Prove that KL divergence is always non-negative (Gibbs' inequality).**

D_KL(P||Q) = -sum P(x) * log(Q(x)/P(x)) >= -log(sum P(x) * Q(x)/P(x)) = -log(1) = 0

Using Jensen's inequality on the convex function -log.

**Q2: What is the relationship between cross-entropy loss and KL divergence?**

For classification with one-hot y: L = -log(hat{y}_{true}) = H(y, hat{y}) = D_KL(y || hat{y}). Minimizing cross-entropy is minimizing KL divergence from the true distribution.

**Q3: When would mutual information be preferred over correlation?**

Correlation captures only linear dependence. MI captures ANY statistical dependence. MI-based feature selection (mRMR) outperforms correlation-based when features have non-linear relationships with the target.

**Q4: What is the data processing inequality?**

If X -> Y -> Z forms a Markov chain: I(X; Y) >= I(X; Z). Processing cannot increase information. This bounds the information in learned representations.

**Q5: How would you estimate MI for continuous variables?**

Methods: KDE, k-NN based (Kraskov-Stogbauer-Grassberger), binning, or neural estimation (MINE).

---

### 7. Applications in ML

| Application | Metric | Purpose |
|-------------|--------|---------|
| Classification loss | Cross-Entropy | Measure prediction error |
| Feature selection | Mutual Information | Identify relevant features |
| Decision trees | Information Gain | Select splitting features |
| GANs | JS Divergence | Discriminator objective |
| Variational Inference | ELBO (KL-based) | Approximate posterior |

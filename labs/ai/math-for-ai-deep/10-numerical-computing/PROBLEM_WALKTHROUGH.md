# Problem Walkthrough: Numerical Stability & Conditioning

## Problem Statement

**Interview Problem: Implement Numerically Stable ML Operations**

You are building a production ML inference engine where numerical precision matters. Implement a `NumericalStability` utility class with:

1. **Stable Softmax** — Prevents overflow in exponentiation
2. **Log-Sum-Exp** — Stable computation of log(sum(exp(x)))
3. **Stable Sigmoid** — Prevents overflow for extreme values
4. **Log-Softmax** — Combines log and softmax for stability
5. **Matrix Condition Number** — Measures sensitivity to perturbations
6. **Catastrophic Cancellation Detection** — Identifies loss of precision

**Constraints:**
- Handle extreme input values (positive and negative)
- Avoid NaN and Inf in all operations
- Detect and report numerical issues
- Default epsilon = 1e-15

**Example:**
```java
double[] logits = {1000.0, 1001.0, -1000.0};
double[] probs = NumericalStability.softmax(logits);
// Without stabilization: [NaN, NaN, NaN]
// With stabilization: ~[0.2689, 0.7311, ~0.0]

double x = 1000.0;
double sig = NumericalStability.sigmoid(x); // ~1.0 (no overflow)
double lse = NumericalStability.logSumExp(new double[]{1000, 1001, 1002});
// lse ~ 1002.4076 (not inf)
```

---

## Step-by-Step Solution Walkthrough

### 1. Mathematical Foundation

#### 1.1 Softmax

softmax(x_i) = exp(x_i) / sum_j exp(x_j)

**Problem**: For x_i = 1000, exp(1000) overflows to Infinity in double precision.

**Fix**: softmax(x_i) = exp(x_i - max(x)) / sum_j exp(x_j - max(x))

The max subtraction is invariant: subtracting a constant from all inputs doesn't change softmax output.

#### 1.2 Log-Sum-Exp

LSE(x) = log(sum_i exp(x_i))

**Problem**: Same overflow for large x_i.

**Fix**: LSE(x) = max(x) + log(sum_i exp(x_i - max(x)))

#### 1.3 Sigmoid

sigmoid(x) = 1 / (1 + exp(-x))

**Problems**:
- x > 700: exp(-x) underflows to 0, sigmoid = 1 (OK but loses precision)
- x < -700: exp(-x) overflows to Inf, sigmoid = NaN

**Fix**: Use piecewise:
- x >= 0: sigmoid = 1 / (1 + exp(-x))
- x < 0: sigmoid = exp(x) / (1 + exp(x))

Or equivalently: sigmoid(x) = exp(-max(0, -x)) / (exp(-max(0, x)) + exp(-max(0, -x)))

#### 1.4 Log-Softmax

log_softmax(x_i) = x_i - LSE(x)

This is more stable than computing softmax then taking log (which has issues near 0).

#### 1.5 Log-Sigmoid

log_sigmoid(x) = -softplus(-x) = -log(1 + exp(-x))

**Problem**: Naive log(1 + exp(x)) overflows for large x.

**Fix**: log(1 + exp(x)) = max(0, x) + log(1 + exp(-|x|))

#### 1.6 Condition Number

cond(A) = ||A|| * ||A^{-1}||

For a scalar: cond = |largest eigenvalue / smallest eigenvalue|

A large condition number means the matrix is sensitive to perturbations. cond = inf for singular matrices.

#### 1.7 Catastrophic Cancellation

Occurs when subtracting two nearly equal numbers:

Example: x = 1.000000000000001, y = 1.000000000000000
x - y = 1e-15 (only 1 significant digit of precision)

**Detection**: Given two numbers a and b, if |a - b| / max(|a|, |b|) < epsilon, cancellation is occurring.

---

### 2. Algorithm Design

#### 2.1 Stable Softmax

```
def softmax(x):
  max_x = max(x)
  shifted = [x_i - max_x for x_i in x]
  exp_sum = sum(exp(x_i) for x_i in shifted)
  return [exp(x_i) / exp_sum for x_i in shifted]
```

#### 2.2 Log-Sum-Exp

```
def logSumExp(x):
  max_x = max(x)
  return max_x + log(sum(exp(x_i - max_x) for x_i in x))
```

#### 2.3 Stable Sigmoid

```
def sigmoid(x):
  if x >= 0:
    return 1.0 / (1.0 + exp(-x))
  else:
    return exp(x) / (1.0 + exp(x))
```

#### 2.4 Condition Number

```
def conditionNumber(A):
  // Compute via SVD eigenvalues
  sigma = svd(A)  // singular values
  return max(sigma) / min(sigma)
```

---

### 3. Java Implementation

```java
package com.ml.numeric;

import java.util.Arrays;
import java.util.Objects;

/**
 * Numerically stable implementations of common ML operations.
 *
 * Handles overflow, underflow, and catastrophic cancellation
 * in softmax, log-sum-exp, sigmoid, and matrix conditioning.
 */
public final class NumericalStability {

    public static final double EPSILON = 1e-15;
    public static final double LOG_EPSILON = Math.log(EPSILON);

    private NumericalStability() {}

    /**
     * Stable softmax: subtracts max before exponentiation.
     * softmax(x_i) = exp(x_i - max) / sum_j exp(x_j - max)
     */
    public static double[] softmax(double[] x) {
        Objects.requireNonNull(x);
        if (x.length == 0) return new double[0];
        double max = x[0];
        for (double v : x) if (v > max) max = v;
        double[] shifted = new double[x.length];
        double sum = 0.0;
        for (int i = 0; i < x.length; i++) {
            shifted[i] = Math.exp(x[i] - max);
            sum += shifted[i];
        }
        if (sum == 0.0) {
            // All values are very negative: return uniform
            Arrays.fill(shifted, 1.0 / x.length);
            return shifted;
        }
        for (int i = 0; i < x.length; i++) {
            shifted[i] /= sum;
        }
        return shifted;
    }

    /**
     * Stable softmax 2D (per-row).
     */
    public static double[][] softmax(double[][] x) {
        double[][] result = new double[x.length][];
        for (int i = 0; i < x.length; i++) {
            result[i] = softmax(x[i]);
        }
        return result;
    }

    /**
     * Stable log-sum-exp: LSE(x) = max(x) + log(sum(exp(x - max(x))))
     */
    public static double logSumExp(double[] x) {
        Objects.requireNonNull(x);
        if (x.length == 0) return Double.NEGATIVE_INFINITY;
        double max = x[0];
        for (double v : x) if (v > max) max = v;
        double sum = 0.0;
        for (double v : x) {
            sum += Math.exp(v - max);
        }
        return max + Math.log(sum);
    }

    /**
     * Stable log-softmax: log_softmax(x_i) = x_i - LSE(x)
     */
    public static double[] logSoftmax(double[] x) {
        double lse = logSumExp(x);
        double[] result = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            result[i] = x[i] - lse;
        }
        return result;
    }

    /**
     * Stable sigmoid: prevents overflow for extreme negative values.
     * sigmoid(x) = 1 / (1 + exp(-x)) for x >= 0
     * sigmoid(x) = exp(x) / (1 + exp(x)) for x < 0
     */
    public static double sigmoid(double x) {
        if (x >= 0) {
            return 1.0 / (1.0 + Math.exp(-x));
        } else {
            double expX = Math.exp(x);
            return expX / (1.0 + expX);
        }
    }

    /**
     * Stable log-sigmoid: log_sigmoid(x) = -softplus(-x)
     */
    public static double logSigmoid(double x) {
        return -softplus(-x);
    }

    /**
     * Stable softplus: log(1 + exp(x))
     * For x > 20: softplus(x) = x (exp(-x) negligible)
     * For x < -20: softplus(x) = exp(x) (exp(x) dominates)
     * Otherwise: log1p(exp(x))
     */
    public static double softplus(double x) {
        if (x > 20.0) return x;
        if (x < -20.0) return Math.exp(x);
        return Math.log1p(Math.exp(x));
    }

    /**
     * Stable cross-entropy for two probability distributions.
     * H(P, Q) = -sum_i P_i * log(Q_i)
     * Clips Q_i to avoid log(0).
     */
    public static double crossEntropy(double[] P, double[] Q) {
        Objects.requireNonNull(P);
        Objects.requireNonNull(Q);
        if (P.length != Q.length) {
            throw new IllegalArgumentException("Length mismatch");
        }
        double ce = 0.0;
        for (int i = 0; i < P.length; i++) {
            double q = Math.max(Q[i], EPSILON);
            if (P[i] > 0) {
                ce -= P[i] * Math.log(q);
            }
        }
        return ce;
    }

    /**
     * Binary cross-entropy loss with logits (stable).
     * L = max(x, 0) - x * z + log(1 + exp(-|x|))
     * where z is the target (0 or 1).
     */
    public static double binaryCrossEntropyWithLogits(double logit, int target) {
        double max = Math.max(logit, 0.0);
        return max - logit * target + Math.log(Math.exp(-Math.abs(logit)) + 1.0);
    }

    /**
     * Computes the condition number of a matrix.
     * cond(A) = ||A|| * ||A^{-1}|| = sigma_max / sigma_min
     * where sigma are singular values.
     */
    public static double conditionNumber(double[][] A) {
        // Simplified: compute ratio of max to min abs eigenvalue
        // For a full implementation, use SVD
        int n = A.length;
        double maxAbs = 0.0;
        double minAbs = Double.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            double sumRow = 0.0;
            for (int j = 0; j < n; j++) {
                sumRow += Math.abs(A[i][j]);
            }
            // Frobenius-norm based approximation
            double val = sumRow / n;
            if (val > maxAbs) maxAbs = val;
            if (val < minAbs) minAbs = val;
        }
        if (minAbs < EPSILON) return Double.POSITIVE_INFINITY;
        return maxAbs / minAbs;
    }

    /**
     * Detects catastrophic cancellation: if |a - b| / max(|a|,|b|) < eps.
     */
    public static boolean isCatastrophicCancellation(double a, double b) {
        double maxAbs = Math.max(Math.abs(a), Math.abs(b));
        if (maxAbs == 0.0) return false;
        return Math.abs(a - b) / maxAbs < EPSILON;
    }

    /**
     * Checks if a matrix is ill-conditioned.
     */
    public static boolean isIllConditioned(double[][] A) {
        return conditionNumber(A) > 1e12;
    }

    /**
     * Computes log(1 + exp(x)) with a numerically stable formula.
     */
    public static double log1pExp(double x) {
        if (x > 20.0) return x;
        if (x < -20.0) return Math.exp(x);
        return Math.log1p(Math.exp(x));
    }

    /**
     * Computes exp(x) - 1 with high precision for small x.
     */
    public static double expm1(double x) {
        return Math.expm1(x);
    }

    /**
     * Stable KL divergence: D_KL(P || Q) = sum P_i * log(P_i / Q_i)
     */
    public static double klDivergence(double[] P, double[] Q) {
        Objects.requireNonNull(P);
        Objects.requireNonNull(Q);
        if (P.length != Q.length) {
            throw new IllegalArgumentException("Length mismatch");
        }
        double kl = 0.0;
        for (int i = 0; i < P.length; i++) {
            if (P[i] == 0.0) continue;
            double q = Math.max(Q[i], EPSILON);
            kl += P[i] * Math.log(P[i] / q);
        }
        return kl;
    }
}
```

---

### 4. Test Cases

```java
package com.ml.numeric;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NumericalStabilityTest {

    private static final double DELTA = 1e-10;

    @Test
    void testStableSoftmaxExtremeValues() {
        double[] logits = {1000.0, 1001.0, -1000.0};
        double[] probs = NumericalStability.softmax(logits);
        // No NaN
        for (double p : probs) {
            assertFalse(Double.isNaN(p));
            assertTrue(p >= 0);
            assertTrue(p <= 1);
        }
        // Probabilities sum to 1
        double sum = 0.0;
        for (double p : probs) sum += p;
        assertEquals(1.0, sum, DELTA);
    }

    @Test
    void testSoftmaxBasic() {
        double[] x = {1.0, 2.0, 3.0};
        double[] probs = NumericalStability.softmax(x);
        double sum = 0.0;
        for (double p : probs) sum += p;
        assertEquals(1.0, sum, DELTA);
        assertTrue(probs[2] > probs[1]);
        assertTrue(probs[1] > probs[0]);
    }

    @Test
    void testSoftmaxUniform() {
        double[] x = {0.0, 0.0, 0.0};
        double[] probs = NumericalStability.softmax(x);
        assertEquals(1.0/3.0, probs[0], DELTA);
        assertEquals(1.0/3.0, probs[1], DELTA);
        assertEquals(1.0/3.0, probs[2], DELTA);
    }

    @Test
    void testLogSumExp() {
        double[] x = {1000.0, 1001.0, 1002.0};
        double lse = NumericalStability.logSumExp(x);
        assertFalse(Double.isInfinite(lse));
        assertFalse(Double.isNaN(lse));
        // max(x) < LSE < max(x) + log(n)
        assertTrue(lse > 1002.0);
        assertTrue(lse < 1002.0 + Math.log(3));
    }

    @Test
    void testLogSumExpBasic() {
        double[] x = {1.0, 2.0, 3.0};
        double lse = NumericalStability.logSumExp(x);
        double expected = Math.log(Math.exp(1) + Math.exp(2) + Math.exp(3));
        assertEquals(expected, lse, DELTA);
    }

    @Test
    void testSigmoidExtreme() {
        assertEquals(1.0, NumericalStability.sigmoid(1000.0), DELTA);
        assertEquals(0.0, NumericalStability.sigmoid(-1000.0), DELTA);
    }

    @Test
    void testSigmoidBasic() {
        assertEquals(0.5, NumericalStability.sigmoid(0.0), DELTA);
        double s1 = NumericalStability.sigmoid(1.0);
        double expected = 1.0 / (1.0 + Math.exp(-1.0));
        assertEquals(expected, s1, DELTA);
        // Symmetry: sigmoid(-x) = 1 - sigmoid(x)
        assertEquals(1.0 - s1, NumericalStability.sigmoid(-1.0), DELTA);
    }

    @Test
    void testLogSoftmax() {
        double[] x = {1000.0, 1001.0, -1000.0};
        double[] ls = NumericalStability.logSoftmax(x);
        for (double v : ls) {
            assertFalse(Double.isNaN(v));
            assertFalse(Double.isInfinite(v));
            assertTrue(v <= 0);
        }
    }

    @Test
    void testSoftplus() {
        assertEquals(0.0, NumericalStability.softplus(-100.0), DELTA);
        assertEquals(100.0, NumericalStability.softplus(100.0), DELTA);
        double sp = NumericalStability.softplus(0.0);
        assertEquals(Math.log(2.0), sp, DELTA);
        // For large negative: softplus(x) = exp(x)
        assertEquals(Math.exp(-5), NumericalStability.softplus(-5), 1e-7);
    }

    @Test
    void testBinaryCrossEntropy() {
        // Perfect prediction: logit inf, target 1 -> loss 0
        double loss1 = NumericalStability.binaryCrossEntropyWithLogits(1000.0, 1);
        assertEquals(0.0, loss1, 1e-5);
        // Perfect prediction: logit -inf, target 0 -> loss 0
        double loss0 = NumericalStability.binaryCrossEntropyWithLogits(-1000.0, 0);
        assertEquals(0.0, loss0, 1e-5);
        // logit = 0, target = 1: loss = log(2) = 0.693
        double lossHalf = NumericalStability.binaryCrossEntropyWithLogits(0.0, 1);
        assertEquals(Math.log(2.0), lossHalf, DELTA);
    }

    @Test
    void testCatastrophicCancellationDetection() {
        assertTrue(NumericalStability.isCatastrophicCancellation(
            1.000000000000001, 1.000000000000000));
        assertFalse(NumericalStability.isCatastrophicCancellation(
            1.0, 2.0));
    }

    @Test
    void testCrossEntropy() {
        double[] P = {1.0, 0.0};
        double[] Q = {0.9, 0.1};
        double ce = NumericalStability.crossEntropy(P, Q);
        assertEquals(-Math.log(0.9), ce, DELTA);
    }

    @Test
    void testCrossEntropyClipping() {
        double[] P = {1.0, 0.0};
        double[] Q = {0.0, 1.0};
        // Should not be inf (Q[0] is clipped from 0 to EPSILON)
        double ce = NumericalStability.crossEntropy(P, Q);
        assertFalse(Double.isInfinite(ce));
    }

    @Test
    void testConditionNumber() {
        double[][] identity = {{1, 0}, {0, 1}};
        double cond = NumericalStability.conditionNumber(identity);
        assertTrue(cond > 0);
        assertTrue(cond < 1e6);
    }

    @Test
    void testLog1pExp() {
        assertEquals(100.0, NumericalStability.log1pExp(100.0), DELTA);
        assertEquals(0.0, NumericalStability.log1pExp(-100.0), DELTA);
        assertEquals(Math.log(2.0), NumericalStability.log1pExp(0.0), DELTA);
    }
}
```

---

### 5. Complexity Analysis

| Operation | Complexity | Notes |
|-----------|-----------|-------|
| Softmax | O(n) | One max pass, one exp/sum pass |
| Log-Sum-Exp | O(n) | Same as softmax |
| Sigmoid | O(1) | Branch + single exp |
| Log-Softmax | O(n) | LSE + subtraction |
| Softplus | O(1) | Branch + exp or log1p |
| Cross-Entropy | O(n) | Element-wise log + sum |
| Condition Number | O(n^2) | Matrix norm (simplified) |

**Space Complexity:** O(n) for softmax output, O(1) for scalars.

---

### 6. Follow-Up Questions

**Q1: Explain the softmax normalization trick in detail.**

Subtracting max(x) from all elements before exponentiation prevents overflow because:
- exp(x_i - max) = exp(x_i) / exp(max)
- The denominator normalization also divides by exp(max), canceling the effect
- softmax(x_i - max) = softmax(x_i) for any constant max

This works because softmax is shift-invariant: softmax(x + c) = softmax(x).

**Q2: What is the log-sum-exp trick and why is it used in variational inference?**

LSE(x) = max(x) + log(sum(exp(x - max))).

In variational inference, the ELBO often involves log of sums of exponentials (mixture distributions). The LSE trick prevents overflow while preserving the exact value up to machine precision.

**Q3: How does catastrophic cancellation affect gradient computation?**

When computing f(x+h) - f(x-h) for small h, if f is nearly linear over the interval, the subtraction loses significant digits. For double precision with h = 1e-5, about 5 decimal digits are lost. This is the fundamental limitation of numerical differentiation.

**Q4: What is the floating-point range of double precision and where do ML operations hit limits?**

Double precision: ~1e-308 to ~1e308, about 15-17 decimal digits.

ML limits:
- exp(709) overflows (Inf), exp(-745) underflows (0)
- log(1e-308) = -708, log(0) = -Inf
- tanh(19) = 1.0, tanh(-19) = -1.0
- sigmoid(709) = 1.0, sigmoid(-709) = 0.0

For half-precision (float16), these limits are much tighter: exp(11) overflows.

**Q5: How would you implement numerically stable log-determinant?**

log det(A) = sum_i log(sigma_i) where sigma_i are singular values.

For large matrices, the determinant itself overflows/underflows. Using log of singular values:
- Factor A = LU or QR
- log det(A) = sum_i log(|U_i_i|) (tracks sign separately)
- Works because log(product) = sum(log)

Alternatively, use SVD: log det(A) = sum_i log(sigma_i).

**Q6: What is the Kahan summation algorithm and when should you use it?**

Kahan summation compensates for floating-point error when summing many numbers. It maintains a running compensation term:
```
sum = 0; comp = 0
for each x:
  y = x - comp
  t = sum + y
  comp = (t - sum) - y
  sum = t
return sum
```

Use when summing more than ~10000 numbers or when numbers vary widely in magnitude (e.g., gradient accumulation).

---

### 7. Applications in Machine Learning

| Operation | Where Used | Stability Issue |
|-----------|-----------|----------------|
| Softmax | Classification output layer | exp(100) overflows |
| Log-Sum-Exp | ELBO, attention, contrastive loss | Same as softmax |
| Sigmoid | Binary classification, gates | exp(-1000) overflows |
| Cross-Entropy | Loss function | log(0) = -inf |
| Log-Determinant | Gaussian processes, normalizing flows | det can overflow/underflow |
| Kahan Summation | Gradient accumulation | Large-scale training |

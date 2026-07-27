# Problem Walkthrough: Probability Distributions & Sampling

## Problem Statement

**Interview Problem: Implement PDF, CDF, and Sampling for Common Distributions**

You are building a probabilistic programming library. Implement classes for three fundamental probability distributions with methods for PDF, CDF, and random sampling:

1. **Normal (Gaussian) Distribution** — PDF, CDF (via error function approximation), Box-Muller sampling
2. **Bernoulli Distribution** — PMF, CDF, sampling
3. **Uniform Distribution** — PDF, CDF, sampling
4. **Parameter Estimation (MLE)** — Fit distribution parameters from data

**Constraints:**
- All distributions implement a common `Distribution` interface
- PDF/CDF methods must handle edge cases (extreme values, degenerate parameters)
- Sampling must be statistically correct (pass basic statistical tests)
- Use only standard Java libraries (no external stats packages)
- CDF approximation error < 1.5e-7

**Example:**
```java
NormalDist norm = new NormalDist(0, 1);
double pdf = norm.pdf(0.0); // ~0.3989
double cdf = norm.cdf(1.96); // ~0.975
double sample = norm.sample(); // random ~N(0,1)

BernoulliDist bern = new BernoulliDist(0.7);
double pmf = bern.pmf(1); // 0.7
int bernSample = bern.sample(); // 1 with 70% probability

UniformDist unif = new UniformDist(0, 1);
double unifSample = unif.sample(); // random ~U(0,1)
```

---

## Step-by-Step Solution Walkthrough

### 1. Mathematical Foundation

#### 1.1 Probability Density Function (PDF)

For continuous random variable X with distribution F_X:
- f_X(x) = dF_X(x)/dx (derivative of CDF)
- f_X(x) >= 0 for all x
- integral_{-inf}^{inf} f_X(x) dx = 1
- P(a <= X <= b) = integral_a^b f_X(x) dx

**Normal PDF**: f(x | mu, sigma^2) = (1 / sqrt(2*pi*sigma^2)) * exp(-(x - mu)^2 / (2*sigma^2))

#### 1.2 Probability Mass Function (PMF)

For discrete random variable:
- p_X(k) = P(X = k)
- p_X(k) >= 0
- sum_k p_X(k) = 1

**Bernoulli PMF**: P(X = k) = p^k * (1-p)^{1-k} for k in {0, 1}

#### 1.3 Cumulative Distribution Function (CDF)

F_X(x) = P(X <= x)

**Uniform CDF**: F(x) = (x - a) / (b - a) for x in [a, b]

**Normal CDF**: No closed form. Must use approximation:
Phi(z) = (1 / sqrt(2*pi)) * integral_{-inf}^z exp(-t^2/2) dt

#### 1.4 Normal CDF Approximation

We implement the **Abramowitz and Stegun approximation** (formula 7.1.26) using the error function:

Phi(z) = 0.5 * erfc(-z / sqrt(2))

**Error function approximation** (Hastings polynomial):
erf(x) = 1 - (a1*t + a2*t^2 + a3*t^3 + a4*t^4 + a5*t^5) * exp(-x^2)

where t = 1 / (1 + p*|x|), with specific constants p, a1...a5.

Maximum error: 1.5e-7.

#### 1.5 Inverse Transform Sampling

For any distribution with invertible CDF:
1. Sample u ~ Uniform(0, 1)
2. Return x = F^{-1}(u)

Works for:
- Uniform: direct formula
- Bernoulli: threshold u against p
- Normal: uses Box-Muller instead (no closed-form inverse CDF)

#### 1.6 Box-Muller Transform

Generate independent N(0,1) samples from uniform:
1. Sample u1, u2 ~ Uniform(0, 1)
2. z0 = sqrt(-2*ln(u1)) * cos(2*pi*u2)
3. z1 = sqrt(-2*ln(u1)) * sin(2*pi*u2)

Both z0, z1 ~ N(0, 1) independently.

#### 1.7 Maximum Likelihood Estimation (MLE)

Given i.i.d. samples X1, ..., Xn from distribution with parameters theta:

theta_hat_MLE = argmax_theta prod_i f(X_i | theta) = argmax_theta sum_i log f(X_i | theta)

**Normal MLE**:
- mu_hat = (1/n) sum_i X_i (sample mean)
- sigma_hat^2 = (1/n) sum_i (X_i - mu_hat)^2 (biased variance; use n-1 for unbiased)

**Bernoulli MLE**:
- p_hat = (1/n) sum_i X_i (sample proportion)

**Uniform MLE**:
- a_hat = min(X_i), b_hat = max(X_i)

---

### 2. Algorithm Design

#### 2.1 Normal Distribution

**PDF computation**: Direct formula with Math.exp.
- Risk: exp of large negative number -> underflow to 0 (fine, PDF ~ 0).

**CDF computation**:
1. Compute z = (x - mu) / sigma
2. Use erfc approximation for Phi(z)
3. Handle z -> inf (CDF -> 1) and z -> -inf (CDF -> 0)

**Sampling**:
- Box-Muller: generate 2 uniforms, return 2 normals
- Store the second for next call to avoid waste

#### 2.2 Bernoulli Distribution

**PMF**: Direct p and 1-p.
**CDF**: Step function: 0 for x < 0, (1-p) for 0 <= x < 1, 1 for x >= 1.
**Sampling**: u = Uniform(0,1), return 1 if u < p, else 0.

#### 2.3 Uniform Distribution

**PDF**: 1/(b-a) for x in [a,b], 0 otherwise.
**CDF**: (x-a)/(b-a) for x in [a,b], 0 for x < a, 1 for x > b.
**Sampling**: a + (b-a) * u where u ~ Uniform(0,1).

#### 2.4 MLE Fitting

**Normal**: O(n) - single pass for mean, second pass for variance.
**Bernoulli**: O(n) - count successes.
**Uniform**: O(n) - track min and max.

---

### 3. Java Implementation

```java
package com.ml.stats;

import java.util.Objects;
import java.util.Random;

/**
 * Interface for univariate probability distributions.
 */
public interface Distribution {

    double logPdf(double x);
    double pdf(double x);
    double cdf(double x);
    double sample();
    void fit(double[] data);
}

/**
 * Normal (Gaussian) distribution with parameters mu and sigma.
 *
 * PDF: f(x) = 1/(sigma*sqrt(2*pi)) * exp(-(x-mu)^2/(2*sigma^2))
 * CDF: Phi((x-mu)/sigma) via error function approximation
 */
public class NormalDist implements Distribution {

    private static final double P = 0.3275911;
    private static final double A1 = 0.254829592;
    private static final double A2 = -0.284496736;
    private static final double A3 = 1.421413741;
    private static final double A4 = -1.453152027;
    private static final double A5 = 1.061405429;

    private static final double SQRT2 = Math.sqrt(2.0);
    private static final double SQRT2PI = Math.sqrt(2.0 * Math.PI);

    private final Random rng;
    private double mu;
    private double sigma;
    private double nextGaussian;
    private boolean hasNextGaussian;

    public NormalDist(double mu, double sigma) {
        this(mu, sigma, new Random());
    }

    public NormalDist(double mu, double sigma, Random rng) {
        if (sigma <= 0) {
            throw new IllegalArgumentException(
                "Standard deviation must be positive: " + sigma);
        }
        this.mu = mu;
        this.sigma = sigma;
        this.rng = Objects.requireNonNull(rng);
    }

    public double mu() { return mu; }
    public double sigma() { return sigma; }

    @Override
    public double logPdf(double x) {
        double z = (x - mu) / sigma;
        return -0.5 * z * z - Math.log(sigma * SQRT2PI);
    }

    @Override
    public double pdf(double x) {
        double z = (x - mu) / sigma;
        return Math.exp(-0.5 * z * z) / (sigma * SQRT2PI);
    }

    @Override
    public double cdf(double x) {
        double z = (x - mu) / sigma;
        return 0.5 * erfc(-z / SQRT2);
    }

    private static double erfc(double x) {
        double t = 1.0 / (1.0 + P * Math.abs(x));
        double poly = t * (A1 + t * (A2 + t * (A3 + t * (A4 + t * A5))));
        double result = poly * Math.exp(-x * x);
        return x >= 0 ? result : 2.0 - result;
    }

    public double icdf(double p) {
        if (p <= 0 || p >= 1) {
            throw new IllegalArgumentException(
                "p must be in (0, 1): " + p);
        }
        double[] a = {-3.969683028665376e+01, 2.209460984245205e+02,
                      -2.759285104469687e+02, 1.383577518672690e+02,
                      -3.066479806614716e+01, 2.506628277459239e+00};
        double[] b = {-5.447609879822406e+01, 1.615858368580409e+02,
                      -1.556989798598866e+02, 6.680131188771972e+01,
                      -1.328068155288572e+01};
        double[] c = {-7.784894002430293e-03, -3.223964580411365e-01,
                      -2.400758277161838e+00, -2.549732539343734e+00,
                      4.374664141464968e+00, 2.938163982698783e+00};
        double[] d = {7.784695709041462e-03, 3.224671290700398e-01,
                      2.445134137142996e+00, 3.754408661907416e+00};
        double q, r;
        if (p < 0.02425) {
            q = Math.sqrt(-2.0 * Math.log(p));
            double num = ((((c[0]*q + c[1])*q + c[2])*q + c[3])*q + c[4])*q + c[5];
            double den = (((d[0]*q + d[1])*q + d[2])*q + d[3])*q + 1.0;
            return mu + sigma * (num / den);
        } else if (p > 0.97575) {
            q = Math.sqrt(-2.0 * Math.log(1.0 - p));
            double num = ((((c[0]*q + c[1])*q + c[2])*q + c[3])*q + c[4])*q + c[5];
            double den = (((d[0]*q + d[1])*q + d[2])*q + d[3])*q + 1.0;
            return mu - sigma * (num / den);
        } else {
            q = p - 0.5;
            r = q * q;
            double num = (((((a[0]*r + a[1])*r + a[2])*r + a[3])*r + a[4])*r + a[5])*q;
            double den = ((((b[0]*r + b[1])*r + b[2])*r + b[3])*r + b[4])*r + 1.0;
            return mu + sigma * (num / den);
        }
    }

    @Override
    public double sample() {
        if (hasNextGaussian) {
            hasNextGaussian = false;
            return mu + sigma * nextGaussian;
        }
        double u1, u2, s;
        do {
            u1 = 2.0 * rng.nextDouble() - 1.0;
            u2 = 2.0 * rng.nextDouble() - 1.0;
            s = u1 * u1 + u2 * u2;
        } while (s >= 1.0 || s == 0.0);
        double factor = Math.sqrt(-2.0 * Math.log(s) / s);
        nextGaussian = u2 * factor;
        hasNextGaussian = true;
        return mu + sigma * (u1 * factor);
    }

    @Override
    public void fit(double[] data) {
        Objects.requireNonNull(data);
        if (data.length < 2) {
            throw new IllegalArgumentException(
                "Need at least 2 data points for fitting");
        }
        double sum = 0.0;
        for (double x : data) sum += x;
        mu = sum / data.length;
        double sumSq = 0.0;
        for (double x : data) {
            double d = x - mu;
            sumSq += d * d;
        }
        sigma = Math.sqrt(sumSq / (data.length - 1));
        if (sigma < 1e-15) {
            throw new ArithmeticException(
                "Degenerate data: zero variance estimated");
        }
    }
}

/**
 * Bernoulli distribution with success probability p.
 */
public class BernoulliDist implements Distribution {

    private final Random rng;
    private double p;

    public BernoulliDist(double p) {
        this(p, new Random());
    }

    public BernoulliDist(double p, Random rng) {
        if (p < 0 || p > 1) {
            throw new IllegalArgumentException(
                "p must be in [0, 1]: " + p);
        }
        this.p = p;
        this.rng = Objects.requireNonNull(rng);
    }

    public double p() { return p; }

    @Override
    public double logPdf(double x) {
        if (x == 0) return Math.log(1.0 - p);
        if (x == 1) return Math.log(p);
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double pdf(double x) {
        if (x == 0) return 1.0 - p;
        if (x == 1) return p;
        return 0.0;
    }

    @Override
    public double cdf(double x) {
        if (x < 0) return 0.0;
        if (x < 1) return 1.0 - p;
        return 1.0;
    }

    @Override
    public double sample() {
        return rng.nextDouble() < p ? 1.0 : 0.0;
    }

    @Override
    public void fit(double[] data) {
        Objects.requireNonNull(data);
        if (data.length == 0) {
            throw new IllegalArgumentException("Need data for fitting");
        }
        int successes = 0;
        for (double x : data) {
            if (x == 1.0) successes++;
        }
        p = (double) successes / data.length;
    }

    public void fitLaplace(double[] data) {
        Objects.requireNonNull(data);
        int successes = 0;
        int total = data.length;
        for (double x : data) {
            if (x == 1.0) successes++;
        }
        p = (double)(successes + 1) / (total + 2);
    }
}

/**
 * Continuous uniform distribution over [a, b].
 */
public class UniformDist implements Distribution {

    private final Random rng;
    private double a;
    private double b;

    public UniformDist(double a, double b) {
        this(a, b, new Random());
    }

    public UniformDist(double a, double b, Random rng) {
        if (b <= a) {
            throw new IllegalArgumentException(
                "b must be > a: a=" + a + ", b=" + b);
        }
        this.a = a;
        this.b = b;
        this.rng = Objects.requireNonNull(rng);
    }

    public double a() { return a; }
    public double b() { return b; }

    @Override
    public double logPdf(double x) {
        if (x < a || x > b) return Double.NEGATIVE_INFINITY;
        return -Math.log(b - a);
    }

    @Override
    public double pdf(double x) {
        if (x < a || x > b) return 0.0;
        return 1.0 / (b - a);
    }

    @Override
    public double cdf(double x) {
        if (x <= a) return 0.0;
        if (x >= b) return 1.0;
        return (x - a) / (b - a);
    }

    @Override
    public double sample() {
        return a + (b - a) * rng.nextDouble();
    }

    @Override
    public void fit(double[] data) {
        Objects.requireNonNull(data);
        if (data.length < 1) {
            throw new IllegalArgumentException("Need data for fitting");
        }
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (double x : data) {
            if (x < min) min = x;
            if (x > max) max = x;
        }
        if (min >= max) {
            throw new IllegalArgumentException(
                "Degenerate data: all values equal");
        }
        a = min;
        b = max;
    }
}
```

---

### 4. Test Cases

```java
package com.ml.stats;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DistributionTest {

    private static final double DELTA = 1e-10;

    @Test
    void testNormalPdf() {
        NormalDist n = new NormalDist(0, 1);
        double invSqrt2pi = 1.0 / Math.sqrt(2 * Math.PI);
        assertEquals(invSqrt2pi, n.pdf(0.0), DELTA);
        assertEquals(invSqrt2pi * Math.exp(-0.5), n.pdf(1.0), DELTA);
        assertEquals(invSqrt2pi * Math.exp(-2.0), n.pdf(2.0), DELTA);
    }

    @Test
    void testNormalCdf() {
        NormalDist n = new NormalDist(0, 1);
        assertEquals(0.5, n.cdf(0.0), 1e-7);
        assertEquals(0.975, n.cdf(1.96), 1e-3);
        assertEquals(0.0, n.cdf(-10.0), DELTA);
        assertEquals(1.0, n.cdf(10.0), DELTA);
    }

    @Test
    void testNormalLogPdf() {
        NormalDist n = new NormalDist(0, 1);
        double lp = n.logPdf(0.0);
        double pdf = n.pdf(0.0);
        assertEquals(Math.log(pdf), lp, DELTA);
    }

    @Test
    void testNormalIcdf() {
        NormalDist n = new NormalDist(0, 1);
        assertEquals(0.0, n.icdf(0.5), 1e-8);
        assertEquals(1.96, n.icdf(0.975), 1e-2);
        assertThrows(IllegalArgumentException.class, () -> n.icdf(0.0));
        assertThrows(IllegalArgumentException.class, () -> n.icdf(1.0));
    }

    @Test
    void testNormalSampling() {
        NormalDist n = new NormalDist(5, 2);
        double sum = 0.0;
        int nSamples = 10000;
        for (int i = 0; i < nSamples; i++) {
            sum += n.sample();
        }
        double sampleMean = sum / nSamples;
        assertEquals(5.0, sampleMean, 0.2);
    }

    @Test
    void testNormalFit() {
        double[] data = {1.0, 2.0, 3.0, 4.0, 5.0};
        NormalDist n = new NormalDist(0, 1);
        n.fit(data);
        assertEquals(3.0, n.mu(), DELTA);
        assertEquals(Math.sqrt(2.5), n.sigma(), DELTA);
    }

    @Test
    void testBernoulliPmf() {
        BernoulliDist b = new BernoulliDist(0.7);
        assertEquals(0.7, b.pdf(1.0), DELTA);
        assertEquals(0.3, b.pdf(0.0), DELTA);
        assertEquals(0.0, b.pdf(0.5), DELTA);
    }

    @Test
    void testBernoulliCdf() {
        BernoulliDist b = new BernoulliDist(0.7);
        assertEquals(0.0, b.cdf(-0.5), DELTA);
        assertEquals(0.3, b.cdf(0.5), DELTA);
        assertEquals(1.0, b.cdf(1.5), DELTA);
    }

    @Test
    void testBernoulliSampling() {
        BernoulliDist b = new BernoulliDist(0.5);
        int heads = 0;
        int nFlips = 10000;
        for (int i = 0; i < nFlips; i++) {
            if (b.sample() == 1.0) heads++;
        }
        assertEquals(0.5, (double) heads / nFlips, 0.02);
    }

    @Test
    void testBernoulliFit() {
        double[] data = {1, 1, 1, 0, 0};
        BernoulliDist b = new BernoulliDist(0.5);
        b.fit(data);
        assertEquals(0.6, b.p(), DELTA);
        b.fitLaplace(data);
        assertEquals(4.0/7.0, b.p(), DELTA);
    }

    @Test
    void testUniformPdf() {
        UniformDist u = new UniformDist(0, 10);
        assertEquals(0.1, u.pdf(3.0), DELTA);
        assertEquals(0.1, u.pdf(9.0), DELTA);
        assertEquals(0.0, u.pdf(-1.0), DELTA);
        assertEquals(0.0, u.pdf(11.0), DELTA);
    }

    @Test
    void testUniformCdf() {
        UniformDist u = new UniformDist(2, 5);
        assertEquals(0.0, u.cdf(1.0), DELTA);
        assertEquals(0.0, u.cdf(2.0), DELTA);
        assertEquals(0.5, u.cdf(3.5), DELTA);
        assertEquals(1.0, u.cdf(5.0), DELTA);
        assertEquals(1.0, u.cdf(6.0), DELTA);
    }

    @Test
    void testUniformSampling() {
        UniformDist u = new UniformDist(2, 10);
        double sum = 0.0;
        int nSamples = 10000;
        for (int i = 0; i < nSamples; i++) {
            double s = u.sample();
            assertTrue(s >= 2 && s <= 10);
            sum += s;
        }
        assertEquals(6.0, sum / nSamples, 0.1);
    }

    @Test
    void testUniformFit() {
        double[] data = {3.5, 7.2, 1.8, 9.1, 4.6};
        UniformDist u = new UniformDist(0, 1);
        u.fit(data);
        assertEquals(1.8, u.a(), DELTA);
        assertEquals(9.1, u.b(), DELTA);
    }

    @Test
    void testInvalidParameters() {
        assertThrows(IllegalArgumentException.class,
            () -> new NormalDist(0, -1));
        assertThrows(IllegalArgumentException.class,
            () -> new UniformDist(5, 3));
        assertThrows(IllegalArgumentException.class,
            () -> new BernoulliDist(1.5));
    }
}
```

---

### 5. Complexity Analysis

**Time Complexity:**

| Operation | Normal | Bernoulli | Uniform |
|-----------|--------|-----------|---------|
| PDF | O(1) | O(1) | O(1) |
| CDF | O(1) | O(1) | O(1) |
| Sample | O(1) | O(1) | O(1) |
| Fit (n data) | O(n) | O(n) | O(n) |

**Space Complexity:** All operations O(1) auxiliary space.

---

### 6. Follow-Up Questions

**Q1: What is the difference between MLE and MAP estimation?**

MLE: theta_hat = argmax P(data | theta)
MAP: theta_hat = argmax P(theta | data) = argmax P(data | theta) * P(theta)

MAP incorporates a prior P(theta). As n -> inf, MAP converges to MLE. For small samples, prior regularizes the estimate.

**Q2: Why use log-pdf instead of pdf in computations?**

Products of many small probabilities underflow to 0. Using log-probabilities avoids underflow, simplifies derivatives, and is the basis for cross-entropy loss.

**Q3: What is the central limit theorem and why does it matter?**

sqrt(n) * (X_bar - mu) / sigma -> N(0, 1). Justifies Normal approximations, confidence intervals, and asymptotic normality of MLE.

**Q4: How would you implement a mixture distribution?**

A mixture of K components with weights w_k: f(x) = sum_k w_k * f_k(x). Sampling: first sample component, then sample from that component.

**Q5: How would you sample from a truncated normal distribution?**

Acceptance-rejection for mild truncation; inverse CDF sampling for severe truncation: u ~ Uniform(F(a), F(b)), x = F^{-1}(u).

---

### 7. Applications in Machine Learning

| Distribution | ML Application |
|-------------|----------------|
| Normal | Parameter initialization, VAE, Gaussian processes |
| Bernoulli | Binary classification, dropout |
| Uniform | Parameter initialization, noise injection |
| Mixture | Clustering (GMM), density estimation |

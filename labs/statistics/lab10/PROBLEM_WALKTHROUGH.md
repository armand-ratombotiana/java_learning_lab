# Problem Walkthrough: Statistical Power & Effect Size

## Problem 1: Sizing the Recommendation Test — Company: Netflix
### Interview Scenario
"You're at Netflix's experimentation platform team. The recommendations team is testing a new personalization model and measuring weekly watch time (hours). A pilot gives mean lift of 3.5 hours with sample standard deviations of 10.0 hours (control) and 9.7 hours (treatment), 50 users per group. The team wants to run the full test with 100 users per group and needs to know: how powerful will it be? What is the minimum detectable effect? And what sample sizes are required for the standard effect-size targets? They need a single, explainable answer — the product team doesn't speak in p-values, it speaks in 'how big a difference can this test actually find?'"

### The Problem
1. Compute the pooled standard deviation from the two pilot samples
2. Compute Cohen's d for the observed lift and interpret it with Cohen's benchmarks
3. Compute power for the planned n = 100 per group at alpha = 0.05
4. Compute the minimum detectable effect at n = 100 and n = 200 per group
5. Compute sample size per group for d = 0.2, 0.5, 0.8 targets at 80% power
6. Print the power curve n = 10..100 at d = 0.5 so the team can see the shape

### Solution Walkthrough
- Step 1: Reuse the lab's `StatisticalPower` methods verbatim: `erfc` (rational approximation), `normCdf` (0.5·erfc(-z/sqrt(2))), `normInv` (Acklam inverse + one refinement step), `pooledStdDev`, `cohensD`, `powerTwoSampleMeans`, `powerFromCohensD`, `minimumDetectableEffect`, `sampleSizePerGroup`, `sampleSizeFromCohensD`, `interpretCohensD`
- Step 2: `pooledStdDev(10.0, 50, 9.7103, 50)` pools the variances weighted by degrees of freedom: sqrt((49·10² + 49·9.7103²)/98) = 9.8562 hours — the equal-variance residual scale the t-test itself uses
- Step 3: `cohensD(3.5, 9.8562)` = 0.3551 — between small and medium on Cohen's scale, so `interpretCohensD` returns "small"; the descriptive effect size uses the pooled scale
- Step 4: Power is computed on the population sigma (10.0): `powerTwoSampleMeans(3.5, 10.0, 100, 0.05)` maps delta/sigma = 0.35, shift = 0.35·sqrt(50) = 2.475, and evaluates power = 1 - Phi(1.960 - 2.475) + Phi(-1.960 - 2.475) = 0.6967 — the planned test would only catch the real effect 70% of the time, below the 80% bar
- Step 5: `minimumDetectableEffect(10.0, 100, 0.05, 0.20)` = 10·2.8016·sqrt(2/100) = 3.9620 hours — the test needs a 3.96-hour lift to be reliably found; doubling to n = 200 shrinks the MDE to 2.8016, exactly the inverse-square law: 2x data, sqrt(2)x detectable effect
- Step 6: `sampleSizeFromCohensD` inverts the power equation: n = 2·(z_alpha/2 + z_beta)²/d² gives 392.44 (d = 0.2), 62.79 (d = 0.5), 24.53 (d = 0.8); the power curve shows 80% crossed between n = 60 and n = 70 at d = 0.5 (power 0.7819 at n = 60, 0.8409 at n = 70)

### Code
```java
package com.statistics.lab10;

/**
 * Mirrors the lab's StatisticalPower class (erfc, normCdf, normInv,
 * pooledStdDev, cohensD, power for two-sample means, minimum detectable
 * effect, sample size per group, Cohen's interpretation) and applies it
 * to a Netflix-style watch-time recommendation experiment.
 */
public final class RecommendationPower {

    private RecommendationPower() {
    }

    public static double erfc(double x) {
        double z = Math.abs(x);
        double t = 1.0 / (1.0 + 0.5 * z);
        double r = t * Math.exp(-z * z - 1.26551223 +
            t * (1.00002368 +
            t * (0.37409196 +
            t * (0.09678418 +
            t * (-0.18628806 +
            t * (0.27886807 +
            t * (-1.13520398 +
            t * (1.48851587 +
            t * (-0.82215223 +
            t * (0.17087277))))))))));
        return x >= 0 ? r : 2 - r;
    }

    public static double normCdf(double z) {
        return 0.5 * erfc(-z / Math.sqrt(2));
    }

    public static double normInv(double p) {
        if (p <= 0 || p >= 1) {
            throw new IllegalArgumentException("p must be in (0,1)");
        }
        double[] a = {-3.969683028665376e1, 2.209460984245205e2,
                      -2.759285104469687e2, 1.383577518672690e2,
                      -3.066479806614716e1, 2.506628277459239};
        double[] b = {-5.447609879822406e1, 1.615858368580409e2,
                      -1.556989798598866e2, 6.680131188771972e1,
                      -1.328068155288572e1};
        double[] c = {-7.784894002430293e-3, -3.223964580411365e-1,
                      -2.400758277161838, -2.549732539343734,
                      4.374664141464968, 2.938163982698783};
        double[] d = {7.784695709041462e-3, 3.224671290398e-1,
                      2.445134137142996, 3.754408661907416};
        double pLow = 0.02425;
        double pHigh = 1 - pLow;
        double x;
        if (p < pLow) {
            double q = Math.sqrt(-2 * Math.log(p));
            x = (((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5])
                / ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1);
        } else if (p <= pHigh) {
            double q = p - 0.5;
            double r = q * q;
            x = (((((a[0] * r + a[1]) * r + a[2]) * r + a[3]) * r + a[4]) * r + a[5]) * q
                / (((((b[0] * r + b[1]) * r + b[2]) * r + b[3]) * r + b[4]) * r + 1);
        } else {
            double q = Math.sqrt(-2 * Math.log(1 - p));
            x = -(((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5])
                / ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1);
        }
        double e = 0.5 * erfc(-x / Math.sqrt(2)) - p;
        double u = e * Math.sqrt(2 * Math.PI) * Math.exp(x * x / 2);
        x -= u / (1 + x * u / 2);
        return x;
    }

    public static double pooledStdDev(double s1, int n1, double s2, int n2) {
        return Math.sqrt(((n1 - 1) * s1 * s1 + (n2 - 1) * s2 * s2) / (n1 + n2 - 2));
    }

    public static double cohensD(double delta, double sigma) {
        return delta / sigma;
    }

    public static double powerFromCohensD(double d, int n, double alpha) {
        double zAlpha2 = normInv(1 - alpha / 2);
        double shift = d * Math.sqrt(n / 2.0);
        return 1 - normCdf(zAlpha2 - shift) + normCdf(-zAlpha2 - shift);
    }

    public static double powerTwoSampleMeans(double delta, double sigma,
                                              int n, double alpha) {
        return powerFromCohensD(cohensD(delta, sigma), n, alpha);
    }

    public static double minimumDetectableEffect(double sigma, int n,
                                                  double alpha, double beta) {
        double zAlpha2 = normInv(1 - alpha / 2);
        double zBeta = normInv(1 - beta);
        return sigma * (zAlpha2 + zBeta) * Math.sqrt(2.0 / n);
    }

    public static double sampleSizeFromCohensD(double d, double alpha, double beta) {
        double zAlpha2 = normInv(1 - alpha / 2);
        double zBeta = normInv(1 - beta);
        return 2 * (zAlpha2 + zBeta) * (zAlpha2 + zBeta) / (d * d);
    }

    public static double sampleSizePerGroup(double delta, double sigma,
                                             double alpha, double beta) {
        return sampleSizeFromCohensD(cohensD(delta, sigma), alpha, beta);
    }

    public static String interpretCohensD(double d) {
        double a = Math.abs(d);
        if (a < 0.2) {
            return "negligible";
        }
        if (a < 0.5) {
            return "small";
        }
        if (a < 0.8) {
            return "medium";
        }
        return "large";
    }

    public static void main(String[] args) {
        System.out.println("=== Netflix: watch-time recommendation test ===");
        double pooled = pooledStdDev(10.0, 50, 9.7103, 50);
        double d = cohensD(3.5, pooled);
        System.out.printf("Pooled SD = %.4f%n", pooled);
        System.out.printf("Cohen's d = %.4f -> %s%n", d, interpretCohensD(d));
        double power = powerTwoSampleMeans(3.5, 10.0, 100, 0.05);
        System.out.printf("Power at n = 100 per group: %.4f%n", power);

        System.out.println("\n=== Netflix: minimum detectable effect vs sample size ===");
        double mde100 = minimumDetectableEffect(10.0, 100, 0.05, 0.20);
        double mde200 = minimumDetectableEffect(10.0, 200, 0.05, 0.20);
        System.out.printf("MDE at n = 100 per group: %.4f%n", mde100);
        System.out.printf("MDE at n = 200 per group: %.4f%n", mde200);

        System.out.println("\n=== Netflix: sample size per group for target effect sizes ===");
        double[] targets = {0.2, 0.5, 0.8};
        for (double targetD : targets) {
            double n = sampleSizeFromCohensD(targetD, 0.05, 0.20);
            System.out.printf("d = %.1f -> n per group: %.2f -> %d%n",
                targetD, n, (int) Math.ceil(n));
        }

        System.out.println("\n=== Netflix: power curve, n = 10..100, d = 0.5 ===");
        for (int n = 10; n <= 100; n += 10) {
            double p = powerFromCohensD(0.5, n, 0.05);
            System.out.printf("n=%d power=%.4f%n", n, p);
        }
    }
}
```

### Expected Output
```
=== Netflix: watch-time recommendation test ===
Pooled SD = 9.8562
Cohen's d = 0.3551 -> small
Power at n = 100 per group: 0.6967

=== Netflix: minimum detectable effect vs sample size ===
MDE at n = 100 per group: 3.9620
MDE at n = 200 per group: 2.8016

=== Netflix: sample size per group for target effect sizes ===
d = 0.2 -> n per group: 392.44 -> 393
d = 0.5 -> n per group: 62.79 -> 63
d = 0.8 -> n per group: 24.53 -> 25

=== Netflix: power curve, n = 10..100, d = 0.5 ===
n=10 power=0.2010
n=20 power=0.3526
n=30 power=0.4907
n=40 power=0.6088
n=50 power=0.7054
n=60 power=0.7819
n=70 power=0.8409
n=80 power=0.8854
n=90 power=0.9184
n=100 power=0.9424
```

### Company Evaluation
- Netflix: watch-time experiment sizing, guardrail tests for recommendation changes, MDE-driven rollout decisions.
- Google: latency and revenue tests at scale, platform-wide power calculators, sequential testing.
- Amazon: purchase-rate tests, factorial sizing, MDE dashboards for catalog experiments.
- Uber: trip-time tests with known large sigma, driver-payout experiments, city-level power analysis.

---

## Problem 2: The Classical Anchor — Company: Google
### Interview Scenario
"You're at Google's experimentation platform. The textbook case must come out right before the calculator serves product teams: delta = 5, sigma = 10, alpha = 0.05, power = 0.80. If the sample size and the power at that size don't agree, every downstream tool inherits the bug."

### The Problem
1. Compute sample size per group for delta = 5, sigma = 10
2. Compute the actual power at that sample size
3. Confirm the values against the lab's demo numbers

### Solution Walkthrough
- Step 1: `sampleSizePerGroup(5.0, 10.0, 0.05, 0.20)` maps to d = 0.5 and returns 2·(1.960 + 0.842)²/0.25 = 62.79 → 63 per group — the lab's canonical demo value
- Step 2: `powerTwoSampleMeans(5.0, 10.0, 63, 0.05)` = 1 - Phi(1.960 - 0.5·sqrt(31.5)) = 0.8013 — just above the 80% bar, confirming the ceil-up convention is meaningful
- Step 3: The two numbers agree: n = 63 achieves 80.1% power, so the calculator's inversion is consistent end-to-end

### Code
```java
double n = sampleSizePerGroup(5.0, 10.0, 0.05, 0.20);
System.out.printf("Sample size per group: %.2f -> %d%n", n, (int) Math.ceil(n));
double p = powerTwoSampleMeans(5.0, 10.0, (int) Math.ceil(n), 0.05);
System.out.printf("Power at n = %d: %.4f%n", (int) Math.ceil(n), p);
```

### Expected Output
```
Sample size per group: 62.79 -> 63
Power at n = 63: 0.8013
```

---

## Problem 3: MDE Round-Trip — Company: Uber
### Interview Scenario
"You're at Uber comparing ETA accuracy before and after a routing change. The team can allocate 100 drivers per group and wants to know the minimum ETA improvement the test can detect. You also need to verify the machinery: the MDE you report should be exactly the effect the sample-size function considers 'detectable at n = 100'."

### The Problem
1. Compute the MDE at n = 100 per group with sigma = 10 minutes
2. Convert the MDE back into a Cohen's d and a required sample size
3. Show the round-trip closes at exactly n = 100

### Solution Walkthrough
- Step 1: `minimumDetectableEffect(10.0, 100, 0.05, 0.20)` = 10·(1.960 + 0.842)·sqrt(0.02) = 3.9620 minutes
- Step 2: `cohensD(3.9620, 10.0)` = 0.3962; `sampleSizeFromCohensD(0.3962, 0.05, 0.20)` = 2·(1.960 + 0.842)²/0.3962² = 100.00
- Step 3: The closed loop is the strongest sanity check: the effect the MDE names at n = 100 is precisely the effect whose required sample size is n = 100 — any mismatch would mean the two formulas disagree, and the round-trip validates the whole `StatisticalPower` chain

### Code
```java
double mde = minimumDetectableEffect(10.0, 100, 0.05, 0.20);
double d = cohensD(mde, 10.0);
double n = sampleSizeFromCohensD(d, 0.05, 0.20);
System.out.printf("MDE at n = 100: %.4f minutes -> d = %.4f -> n per group: %.2f%n",
    mde, d, n);
```

### Expected Output
```
MDE at n = 100: 3.9620 minutes -> d = 0.3962 -> n per group: 100.00
```

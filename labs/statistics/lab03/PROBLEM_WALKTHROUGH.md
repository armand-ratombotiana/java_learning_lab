# Problem Walkthrough: Hypothesis Testing

## Problem 1: Checkout A/B Test Decision — Company: Booking.com
### Interview Scenario
"You're at Booking.com on the growth experimentation team. The checkout team shipped a redesigned flow to 10 randomized sessions while 10 control sessions used the old flow; revenue per session (EUR) was recorded. You must decide: does the new flow change revenue? Separately, a form change was rolled out and checkout times were measured before/after on the same 8 users — did it slow users down? And is the average order value still consistent with the 120 EUR target the finance team quoted?"

### The Problem
1. Run a two-sample t-test comparing revenue per session between control and variant
2. Run a paired t-test on before/after checkout times for the same 8 users
3. Run a one-sample t-test of order value against the 120 EUR target
4. Check weekday booking distribution against uniformity with a chi-square goodness-of-fit
5. Check whether device type (mobile/desktop) is independent of conversion with a chi-square independence test

### Solution Walkthrough
- Step 1: Reuse the lab's `HypothesisTesting` methods verbatim: `twoSampleTTest`, `pairedTTest`, `oneSampleTTest`, `chiSquareGoodnessOfFit`, `chiSquareIndependence` — same statistics, same p-values, same reject rule `pValue < alpha`
- Step 2: Two-sample test on control {120.5, ..., 119.5} vs variant {124.1, ..., 126.4}: pooled variance via `sp = Math.sqrt(((n1-1)*v1 + (n2-1)*v2)/(n1+n2-2))`, t = -6.8697, p ≈ 0.000002 — the variant is statistically distinguishable
- Step 3: Paired test computes differences `after[i] - before[i]`; t = 25.2758 on df = 7 — the form change measurably lengthened checkout time
- Step 4: One-sample test of control against 120: t = 0.5045, p = 0.626 — no evidence the mean order value differs from target
- Step 5: Goodness-of-fit against uniform weekday expectation: χ² = 10.5, df = 6, p = 0.105 — no strong evidence of weekday pattern
- Step 6: Independence table {230,170; 190,210}: χ² = 8.0201, df = 1, p = 0.004626 — device and conversion are associated; follow up with effect size before acting

### Code
```java
package com.statistics.lab03;

/**
 * Mirrors the lab's HypothesisTesting class (t-tests, z-test, chi-square)
 * and applies it to a Booking.com checkout A/B test.
 */
public final class CheckoutExperiment {

    private CheckoutExperiment() {
    }

    public static String oneSampleTTest(double[] data, double mu0, double alpha) {
        int n = data.length;
        int df = n - 1;
        double mean = mean(data);
        double sd = stdDev(data);
        double se = sd / Math.sqrt(n);
        double t = (mean - mu0) / se;
        double pValue = 2 * (1 - tCdf(Math.abs(t), df));
        boolean reject = pValue < alpha;
        return String.format(
            "One-sample t-test: t=%.4f, df=%d, p=%.6f, reject H0=%b (alpha=%.2f)%n  sample mean=%.4f, H0 mean=%.4f",
            t, df, pValue, reject, alpha, mean, mu0);
    }

    public static String twoSampleTTest(double[] data1, double[] data2, double alpha) {
        int n1 = data1.length, n2 = data2.length;
        double m1 = mean(data1), m2 = mean(data2);
        double v1 = sampleVariance(data1), v2 = sampleVariance(data2);
        double sp = Math.sqrt(((n1 - 1) * v1 + (n2 - 1) * v2) / (n1 + n2 - 2));
        int df = n1 + n2 - 2;
        double se = sp * Math.sqrt(1.0 / n1 + 1.0 / n2);
        double t = (m1 - m2) / se;
        double pValue = 2 * (1 - tCdf(Math.abs(t), df));
        boolean reject = pValue < alpha;
        return String.format(
            "Two-sample t-test: t=%.4f, df=%d, p=%.6f, reject H0=%b (alpha=%.2f)%n  mean1=%.4f, mean2=%.4f",
            t, df, pValue, reject, alpha, m1, m2);
    }

    public static String pairedTTest(double[] before, double[] after, double alpha) {
        int n = before.length;
        double[] diffs = new double[n];
        for (int i = 0; i < n; i++) {
            diffs[i] = after[i] - before[i];
        }
        double dBar = mean(diffs);
        double sd = stdDev(diffs);
        double se = sd / Math.sqrt(n);
        double t = dBar / se;
        int df = n - 1;
        double pValue = 2 * (1 - tCdf(Math.abs(t), df));
        boolean reject = pValue < alpha;
        return String.format(
            "Paired t-test: t=%.4f, df=%d, p=%.6f, reject H0=%b (alpha=%.2f)%n  mean diff=%.4f",
            t, df, pValue, reject, alpha, dBar);
    }

    public static String chiSquareGoodnessOfFit(long[] observed, double[] expected, double alpha) {
        int k = observed.length;
        double chiSq = 0;
        for (int i = 0; i < k; i++) {
            chiSq += (observed[i] - expected[i]) * (observed[i] - expected[i]) / expected[i];
        }
        int df = k - 1;
        double pValue = 1 - chiSquareCdf(chiSq, df);
        boolean reject = pValue < alpha;
        return String.format(
            "Chi-square goodness-of-fit: chi2=%.4f, df=%d, p=%.6f, reject H0=%b (alpha=%.2f)",
            chiSq, df, pValue, reject, alpha);
    }

    public static String chiSquareIndependence(long[][] table, double alpha) {
        int rows = table.length;
        int cols = table[0].length;
        double[] rowSums = new double[rows];
        double[] colSums = new double[cols];
        double total = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                rowSums[r] += table[r][c];
                colSums[c] += table[r][c];
                total += table[r][c];
            }
        }
        double chiSq = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double expected = (rowSums[r] * colSums[c]) / total;
                if (expected > 0) {
                    chiSq += (table[r][c] - expected) * (table[r][c] - expected) / expected;
                }
            }
        }
        int df = (rows - 1) * (cols - 1);
        double pValue = 1 - chiSquareCdf(chiSq, df);
        boolean reject = pValue < alpha;
        return String.format(
            "Chi-square independence: chi2=%.4f, df=%d, p=%.6f, reject H0=%b (alpha=%.2f)",
            chiSq, df, pValue, reject, alpha);
    }

    private static double mean(double[] data) {
        double sum = 0;
        for (double v : data) sum += v;
        return sum / data.length;
    }
    private static double sampleVariance(double[] data) {
        double m = mean(data);
        double ss = 0;
        for (double v : data) ss += (v - m) * (v - m);
        return ss / (data.length - 1);
    }
    private static double stdDev(double[] data) {
        return Math.sqrt(sampleVariance(data));
    }

    private static double tCdf(double t, int df) {
        double x = df / (t * t + df);
        return 1 - 0.5 * incompleteBeta(x, df / 2.0, 0.5);
    }
    private static double chiSquareCdf(double x, int df) {
        return regularizedGamma(df / 2.0, x / 2.0);
    }

    private static double regularizedGamma(double a, double x) {
        if (x < 0 || a <= 0) {
            return 0;
        }
        double sum = 1.0 / a;
        double term = sum;
        for (int k = 1; k < 100; k++) {
            term *= x / (a + k);
            sum += term;
            if (Math.abs(term) < 1e-15) {
                break;
            }
        }
        return sum * Math.exp(-x + a * Math.log(x) - logGamma(a));
    }
    private static double logGamma(double x) {
        double[] c = {
            76.18009172947146, -86.50532032941677, 24.01409824083091,
            -1.231739572450155, 0.1208650973866179e-2, -0.5395239384953e-5
        };
        double y = x;
        double tmp = x + 5.5;
        tmp -= (x + 0.5) * Math.log(tmp);
        double ser = 1.000000000190015;
        for (int j = 0; j < 6; j++) {
            ser += c[j] / ++y;
        }
        return -tmp + Math.log(2.5066282746310005 * ser / x);
    }
    private static double incompleteBeta(double x, double a, double b) {
        if (x < 0 || x > 1) {
            return 0;
        }
        if (x == 0 || x == 1) {
            return x;
        }
        double bt = Math.exp(logGamma(a + b) - logGamma(a) - logGamma(b)
            + a * Math.log(x) + b * Math.log(1 - x));
        if (x < (a + 1) / (a + b + 2)) {
            return bt * betaCf(x, a, b) / a;
        } else {
            return 1 - bt * betaCf(1 - x, b, a) / b;
        }
    }
    private static double betaCf(double x, double a, double b) {
        int maxIter = 100;
        double eps = 3e-12;
        double qab = a + b;
        double qap = a + 1;
        double qam = a - 1;
        double c = 1;
        double d = 1 - qab * x / qap;
        if (Math.abs(d) < 1e-30) {
            d = 1e-30;
        }
        d = 1 / d;
        double h = d;
        for (int m = 1; m <= maxIter; m++) {
            int m2 = 2 * m;
            double aa = m * (b - m) * x / ((qam + m2) * (a + m2));
            d = 1 + aa * d;
            d = Math.abs(d) < 1e-30 ? 1e-30 : d;
            c = 1 + aa / c;
            c = Math.abs(c) < 1e-30 ? 1e-30 : c;
            d = 1 / d;
            h *= d * c;
            aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
            d = 1 + aa * d;
            d = Math.abs(d) < 1e-30 ? 1e-30 : d;
            c = 1 + aa / c;
            c = Math.abs(c) < 1e-30 ? 1e-30 : c;
            d = 1 / d;
            double del = d * c;
            h *= del;
            if (Math.abs(del - 1) < eps) {
                break;
            }
        }
        return h;
    }

    public static void main(String[] args) {
        double alpha = 0.05;

        System.out.println("=== A/B test: new checkout flow vs control ===");
        double[] control = {120.5, 118.2, 122.0, 119.7, 121.3, 120.9, 117.8, 122.4, 120.1, 119.5};
        double[] variant = {124.1, 126.0, 123.4, 125.8, 122.9, 127.2, 124.6, 125.1, 123.0, 126.4};
        System.out.println(twoSampleTTest(control, variant, alpha));

        System.out.println("\n=== Paired: checkout time (min) before/after a form change ===");
        double[] before = {3.4, 2.9, 3.1, 3.6, 2.8, 3.3, 3.0, 3.5};
        double[] after = {3.9, 3.3, 3.6, 4.1, 3.2, 3.8, 3.4, 4.0};
        System.out.println(pairedTTest(before, after, alpha));

        System.out.println("\n=== One-sample: avg order value vs target 120 EUR ===");
        System.out.println(oneSampleTTest(control, 120.0, alpha));

        System.out.println("\n=== Chi-square goodness-of-fit: weekday bookings ===");
        long[] observed = {120, 90, 110, 100, 105, 95, 80};
        double[] expected = new double[7];
        for (int i = 0; i < 7; i++) expected[i] = 100;
        System.out.println(chiSquareGoodnessOfFit(observed, expected, alpha));

        System.out.println("\n=== Chi-square independence: device x conversion ===");
        long[][] table = {
            {230, 170},
            {190, 210}
        };
        System.out.println(chiSquareIndependence(table, alpha));
    }
}
```

### Expected Output
```
=== A/B test: new checkout flow vs control ===
Two-sample t-test: t=-6.8697, df=18, p=0.000002, reject H0=true (alpha=0.05)
  mean1=120.2400, mean2=124.8500

=== Paired: checkout time (min) before/after a form change ===
Paired t-test: t=25.2758, df=7, p=0.000000, reject H0=true (alpha=0.05)
  mean diff=0.4625

=== One-sample: avg order value vs target 120 EUR ===
One-sample t-test: t=0.5045, df=9, p=0.625999, reject H0=false (alpha=0.05)
  sample mean=120.2400, H0 mean=120.0000

=== Chi-square goodness-of-fit: weekday bookings ===
Chi-square goodness-of-fit: chi2=10.5000, df=6, p=0.105114, reject H0=false (alpha=0.05)

=== Chi-square independence: device x conversion ===
Chi-square independence: chi2=8.0201, df=1, p=0.004626, reject H0=true (alpha=0.05)
```

### Company Evaluation
- Booking.com: large-scale A/B platforms, sequential testing, guardrail metrics, p-value interpretation across thousands of concurrent experiments.
- Airbnb: search ranking experiments, booking conversion tests, paired tests on repeat users.
- Google: search latency regressions, z-tests with known fleet variance, chi-square on query classification.
- Stripe: fee and fraud-rate experiments, one-sample tests against contractual targets, chi-square device-association checks.

---

## Problem 2: Search Latency Regression Gate — Company: Google
### Interview Scenario
"You're at Google Search. A candidate ranking change is claimed to be latency-neutral. You measured 20 query latencies (ms) from a distribution with a known standard deviation of 3 ms, and the sample mean is 50.25 ms against the 50 ms baseline. Decide whether to gate the change."

### The Problem
1. Run the z-test with known σ = 3 against μ₀ = 50
2. Interpret the p-value correctly
3. State the sample-size implication for detecting a 0.25 ms drift

### Solution Walkthrough
- Step 1: `zTest(large, 50, 3, 0.05)`: se = σ/√n = 3/√20 ≈ 0.6708, z = 0.3727, p = 0.709388 — no evidence of regression
- Step 2: With p ≈ 0.71, a drift this size is a routine sample fluctuation; the change passes the latency gate
- Step 3: To detect 0.25 ms at α = 0.05 with 80% power you would need n ≈ 2(1.96+0.84)²·3²/0.25² ≈ 2260 queries — the 20-query gate can only catch large regressions

### Code
```java
double[] large = {52, 48, 51, 53, 49, 50, 52, 47, 51, 50,
    53, 49, 48, 52, 50, 51, 49, 50, 52, 48};
System.out.println(zTest(large, 50, 3, 0.05));
```

### Expected Output
```
Z-test: z=0.3727, p=0.709388, reject H0=false (alpha=0.05)
  sample mean=50.2500, H0 mean=50.0000
```

---

## Problem 3: Feature Adoption by Plan — Company: Stripe
### Interview Scenario
"You're at Stripe. You suspect adoption of a new payout feature depends on the customer's plan (Standard vs Plus). From a sample: Standard customers {30 adopted, 20 not}, Plus customers {15 adopted, 35 not}. Decide if the plans differ."

### The Problem
1. Build the 2×2 contingency table
2. Run the chi-square independence test
3. Interpret the result and its df

### Solution Walkthrough
- Step 1: Table {30,20; 15,35} — row sums 50/50, column sums 45/55, total 100
- Step 2: Expected cells: (50·45)/100 = 22.5, (50·55)/100 = 27.5, and symmetric for row 2; `chiSquareIndependence` sums (O-E)²/E → χ² = 9.0909
- Step 3: df = (2-1)(2-1) = 1, p = 0.002569 — the plan-adoption association is significant at α = 0.05

### Code
```java
long[][] table = {
    {30, 20},
    {15, 35}
};
System.out.println(chiSquareIndependence(table, 0.05));
```

### Expected Output
```
Chi-square independence: chi2=9.0909, df=1, p=0.002569, reject H0=true (alpha=0.05)
```

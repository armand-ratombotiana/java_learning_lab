# Problem Walkthrough: Experimental Design

## Problem 1: Experiment Sizing for the Booking Page — Company: Booking.com
### Interview Scenario
"You're at Booking.com building a self-serve experimentation platform. The checkout team wants to test a conversion lift from 8.0% to 9.5%, and the session-experience team wants to detect a +2 minute session-duration effect with known sigma = 10. Both tests must run at alpha = 0.05 with 80% power. You must compute the sample sizes, verify the normal quantile machinery, and analyze a 2^2 factorial pilot on the booking page (A = price display, B = review snippet) whose response means are [10, 14, 12, 20]."

### The Problem
1. Compute per-variant sample size for the conversion test (proportions 0.080 vs 0.095)
2. Compute per-group sample size for the session-duration test (delta = 2.0, sigma = 10)
3. Verify `normInv`/`normCdf` round-trips on the critical z-values
4. Analyze the 2^2 factorial pilot: main effects A, B, and the A x B interaction
5. Run the 2^3 factorial contrast analysis on the 8-run booking-page pilot
6. Interpret the interaction for the launch decision

### Solution Walkthrough
- Step 1: Reuse the lab's `ExperimentalDesign` methods verbatim: `sampleSizeMeans`, `sampleSizeProportions`, `factorial2`, `factorial3`, `normInv`, `normCdf`, `erfc`
- Step 2: `sampleSizeProportions(0.08, 0.095, 0.05, 0.20)` → 5566.60 → 5567 per variant — the (p₁-p₂)² denominator makes small lifts expensive
- Step 3: `sampleSizeMeans(2.0, 10.0, 0.05, 0.20)` → 392.44 → 393 per group
- Step 4: Round-trip checks z = ±1.96, ±1.645, 0 through `normCdf` then `normInv` — errors at the 6th decimal
- Step 5: `factorial2({10, 14, 12, 20})` → effect A = 6.00, effect B = 4.00, interaction AB = 4.00 — a real interaction, so the launch ships the (A+, B+) combination
- Step 6: `factorial3` on the 8-run pilot: A = 6.50, B = 4.50, C = 1.50, AB = 2.50, AC = 0.50, BC = 0.50, ABC = 0.50 — only A, B, AB carry signal

### Code
```java
package com.statistics.lab08;

/**
 * Mirrors the lab's ExperimentalDesign class (sample size for means and
 * proportions, 2^2 and 2^3 factorial analysis, normal quantile helpers)
 * and applies it to a Booking.com-style experimentation platform: sizing
 * a checkout test and analyzing a 2^2 factorial on the booking page.
 */
public final class ExperimentDesigner {

    private ExperimentDesigner() {
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
        double[] d = {7.784695709041462e-3, 3.224671290700398e-1,
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

    public static double normCdf(double z) {
        return 0.5 * erfc(-z / Math.sqrt(2));
    }

    public static double sampleSizeMeans(double delta, double sigma,
                                          double alpha, double beta) {
        double zAlpha2 = normInv(1 - alpha / 2);
        double zBeta = normInv(1 - beta);
        return 2 * Math.pow(zAlpha2 + zBeta, 2) * sigma * sigma / (delta * delta);
    }

    public static double sampleSizeProportions(double p1, double p2,
                                                double alpha, double beta) {
        double zAlpha2 = normInv(1 - alpha / 2);
        double zBeta = normInv(1 - beta);
        double num = Math.pow(zAlpha2 + zBeta, 2) * (p1 * (1 - p1) + p2 * (1 - p2));
        double den = (p1 - p2) * (p1 - p2);
        return num / den;
    }

    public record Factorial2Result(double effectA, double effectB,
                                    double interactionAB) {
    }

    public static Factorial2Result factorial2(double[] means) {
        if (means.length != 4) {
            throw new IllegalArgumentException("Need exactly 4 means for 2^2 design");
        }
        double mA = (means[1] + means[3]) / 2 - (means[0] + means[2]) / 2;
        double mB = (means[2] + means[3]) / 2 - (means[0] + means[1]) / 2;
        double mAB = (means[3] - means[2]) - (means[1] - means[0]);
        return new Factorial2Result(mA, mB, mAB);
    }

    public static double[] factorial3(double[] means) {
        if (means.length != 8) {
            throw new IllegalArgumentException("Need exactly 8 means for 2^3 design");
        }
        int[][] signs = {
            {-1, -1, -1,  1,  1,  1, -1},
            { 1, -1, -1, -1, -1,  1,  1},
            {-1,  1, -1, -1,  1, -1,  1},
            { 1,  1, -1,  1, -1, -1, -1},
            {-1, -1,  1,  1, -1, -1,  1},
            { 1, -1,  1, -1,  1, -1, -1},
            {-1,  1,  1, -1, -1,  1, -1},
            { 1,  1,  1,  1,  1,  1,  1}
        };
        double[] effects = new double[7];
        for (int j = 0; j < 7; j++) {
            double sum = 0;
            for (int i = 0; i < 8; i++) {
                sum += signs[i][j] * means[i];
            }
            effects[j] = sum / 4;
        }
        return effects;
    }

    public static void main(String[] args) {
        double alpha = 0.05;
        double beta = 0.20;

        System.out.println("=== Sample size: checkout conversion 8.0% -> 9.5% ===");
        double nProp = sampleSizeProportions(0.08, 0.095, alpha, beta);
        System.out.printf("p1=0.080, p2=0.095, alpha=0.05, power=0.80%n");
        System.out.printf("n per variant: %.2f -> %d%n", nProp, (int) Math.ceil(nProp));

        System.out.println("\n=== Sample size: session duration +2 min, sigma=10 ===");
        double nM = sampleSizeMeans(2.0, 10.0, alpha, beta);
        System.out.printf("delta=2.0, sigma=10.0, alpha=0.05, power=0.80%n");
        System.out.printf("n per group: %.2f -> %d%n", nM, (int) Math.ceil(nM));

        System.out.println("\n=== Norm quantile sanity checks ===");
        double[] zs = {-1.96, -1.645, 0, 1.645, 1.96};
        for (double z : zs) {
            double p = normCdf(z);
            double zBack = normInv(p);
            System.out.printf("z=%.3f -> p=%.6f -> z'=%.6f%n", z, p, zBack);
        }

        System.out.println("\n=== 2^2 factorial: A=price display, B=review snippet ===");
        double[] f2means = {10, 14, 12, 20};
        Factorial2Result f2 = factorial2(f2means);
        System.out.printf("Means [A-,B-]=%.0f [A+,B-]=%.0f [A-,B+]=%.0f [A+,B+]=%.0f%n",
            f2means[0], f2means[1], f2means[2], f2means[3]);
        System.out.printf("Effect A (price display): %.2f%n", f2.effectA());
        System.out.printf("Effect B (review snippet): %.2f%n", f2.effectB());
        System.out.printf("Interaction AB: %.2f%n", f2.interactionAB());

        System.out.println("\n=== 2^3 factorial: A x B x C (booking page) ===");
        double[] f3means = {5, 9, 7, 15, 6, 10, 8, 18};
        double[] f3 = factorial3(f3means);
        String[] labels = {"A", "B", "C", "AB", "AC", "BC", "ABC"};
        for (int i = 0; i < labels.length; i++) {
            System.out.printf("Effect %s: %.2f%n", labels[i], f3[i]);
        }
    }
}
```

### Expected Output
```
=== Sample size: checkout conversion 8.0% -> 9.5% ===
p1=0.080, p2=0.095, alpha=0.05, power=0.80
n per variant: 5566.60 -> 5567

=== Sample size: session duration +2 min, sigma=10 ===
delta=2.0, sigma=10.0, alpha=0.05, power=0.80
n per group: 392.44 -> 393

=== Norm quantile sanity checks ===
z=-1.960 -> p=0.024998 -> z'=-1.960000
z=-1.645 -> p=0.049985 -> z'=-1.645000
z=0.000 -> p=0.500000 -> z'=0.000000
z=1.645 -> p=0.950015 -> z'=1.645000
z=1.960 -> p=0.975002 -> z'=1.960000

=== 2^2 factorial: A=price display, B=review snippet ===
Means [A-,B-]=10 [A+,B-]=14 [A-,B+]=12 [A+,B+]=20
Effect A (price display): 6.00
Effect B (review snippet): 4.00
Interaction AB: 4.00

=== 2^3 factorial: A x B x C (booking page) ===
Effect A: 6.50
Effect B: 4.50
Effect C: 1.50
Effect AB: 2.50
Effect AC: 0.50
Effect BC: 0.50
Effect ABC: 0.50
```

### Company Evaluation
- Booking.com: self-serve experiment sizing, sequential testing, guardrail sample sizes, factorial pilots on booking pages.
- Google: search ranking factorial experiments, sample-size calculators for latency gates, blocking by datacenter.
- Amazon: checkout page factorial designs, interaction-aware launch decisions, purchase-rate sample sizes.
- Airbnb: search layout factorial pilots, blocking by market, power analysis for host-funnel tests.

---

## Problem 2: Classical Sizing Numbers — Company: Google
### Interview Scenario
"You're at Google's experimentation platform team. The textbook cases must come out right: delta=5, sigma=10, alpha=0.05, beta=0.20 for means; p1=0.10, p2=0.30 for proportions. If these are off, every downstream calculator is off."

### The Problem
1. Compute sample size for means
2. Compute sample size for proportions
3. Confirm the demo values from the lab

### Solution Walkthrough
- Step 1: `sampleSizeMeans(5, 10, 0.05, 0.20)` → 62.79 → 63 — matches the lab's demo
- Step 2: `sampleSizeProportions(0.10, 0.30, 0.05, 0.20)` → 58.87 → 59 — matches the lab's demo
- Step 3: These two anchors validate the whole calculator before it serves product teams

### Code
```java
double n = sampleSizeMeans(5.0, 10.0, 0.05, 0.20);
System.out.printf("Sample size per group: %.2f -> %d%n", n, (int) Math.ceil(n));
double nProp = sampleSizeProportions(0.10, 0.30, 0.05, 0.20);
System.out.printf("Sample size per group: %.2f -> %d%n", nProp, (int) Math.ceil(nProp));
```

### Expected Output
```
Sample size per group: 62.79 -> 63
Sample size per group: 58.87 -> 59
```

---

## Problem 3: 2^3 Factorial on the Booking Page — Company: Airbnb
### Interview Scenario
"You're at Airbnb running an 8-run factorial on search results with three factors: photo count (A), review snippet (B), price emphasis (C). Response means in standard order are [5, 9, 7, 15, 6, 10, 8, 18]. Which effects matter?"

### The Problem
1. Compute all 7 contrasts (A, B, C, AB, AC, BC, ABC)
2. Identify the dominant effects
3. Decide what to ship

### Solution Walkthrough
- Step 1: `factorial3` multiplies the contrast sign matrix (divisor 2^(k-1) = 4) by the means vector
- Step 2: A = 6.50, B = 4.50, C = 1.50, AB = 2.50 — the two-factor interaction AB is large relative to C, AC, BC, ABC (0.50)
- Step 3: Ship the (A+, B+) combination and ignore C — the interaction dominates C's main effect

### Code
```java
double[] f3means = {5, 9, 7, 15, 6, 10, 8, 18};
double[] f3 = factorial3(f3means);
String[] labels = {"A", "B", "C", "AB", "AC", "BC", "ABC"};
for (int i = 0; i < labels.length; i++) {
    System.out.printf("Effect %s: %.2f%n", labels[i], f3[i]);
}
```

### Expected Output
```
Effect A: 6.50
Effect B: 4.50
Effect C: 1.50
Effect AB: 2.50
Effect AC: 0.50
Effect BC: 0.50
Effect ABC: 0.50
```

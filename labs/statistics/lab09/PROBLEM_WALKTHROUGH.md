# Problem Walkthrough: Non-parametric Statistics

## Problem 1: Airbnb Host Experience Review — Company: Airbnb
### Interview Scenario
"You're at Airbnb's data science team. The reviews team is considering a shift from a 1-5 star rating scale to a 1-10 scale and wants to know whether the switch changes the distribution of review sentiment. A second project compares host response times before and after a new notifications rollout, and a third compares nightly prices across cities whose distributions are right-skewed with outliers. A fourth asks whether photo-first vs price-first listing layouts change city-level preferences. Your manager is skeptical of t-tests and ANOVA for these datasets: review ratings are ordinal, prices are heavy-tailed, and the samples are small. You need a single analysis that handles all four questions with rank-based tests."

### The Problem
1. Compare old 1-5 scale review scores vs new 1-10 scale scores with Mann-Whitney U
2. Show the robustness of the rank test when one group contains a 100x outlier
3. Compare host response times before vs after the notifications rollout with Wilcoxon signed-rank
4. Compare nightly prices across three cities with Kruskal-Wallis H
5. Demonstrate the H statistic's behavior when group medians are nearly identical
6. Compare listing-layout preferences across cities with the Friedman Q statistic

### Solution Walkthrough
- Step 1: Reuse the lab's `NonParametricTests` methods verbatim: `rank` (average ranks for ties), `concat`, `mannWhitneyU`, `wilcoxonSignedRank`, `kruskalWallis`, `friedman`
- Step 2: For the scale change, `mannWhitneyU` ranks the combined 8 scores: every new-scale value outranks every old-scale value, so R₁ = 1+2+3+4 = 10 and U₁ = 10 - 4·5/2 = 0; U₂ = 16; `smallerU` = 0 — the most extreme possible statistic, and the p-value is significant at any level
- Step 3: The outlier demo replaces one old-scale value with 100: ranks become {1,2,3,4,10} vs {6,7,8,9}, giving U₁ = 5, U₂ = 20 — the ranks absorb the outlier completely, whereas a t-test's variance would explode
- Step 4: For response times, `wilcoxonSignedRank` computes paired differences, ranks the absolute differences, and signs the ranks; all five differences are negative, so W+ = 0.0 and W- = 15.0 with n = 5 — the strongest possible disagreement with the null
- Step 5: `kruskalWallis` ranks all 9 prices together: rank sums {6, 15, 24} feed H = 12/(9·10) · (6²/3 + 15²/3 + 24²/3) - 30 = 7.200000 — beyond the χ²(2) critical value 5.99; the interleaved, nearly-identical-median demo returns H = 0.800000, correctly not significant
- Step 6: `friedman` ranks each city's three layout scores within the row, then pools per-layout rank sums: Q = 8.000000 for 4 blocks × 3 treatments — exactly the χ²(2) critical value at α = 0.05, showing layout orderings differ consistently across cities

### Code
```java
package com.statistics.lab09;

import java.util.Arrays;

/**
 * Mirrors the lab's NonParametricTests class (rank with average ranks for
 * ties, Mann-Whitney U, Wilcoxon signed-rank, Kruskal-Wallis H, Friedman Q)
 * and applies it to Airbnb-style analyses: rating-scale changes, host
 * response times, city pricing, and listing-layout preferences.
 */
public final class RankBasedAnalyzer {

    private RankBasedAnalyzer() {
    }

    public static double[] rank(double[] values) {
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        double[] ranks = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            int first = Arrays.binarySearch(sorted, values[i]);
            int last = first;
            while (last + 1 < sorted.length && sorted[last + 1] == values[i]) {
                last++;
            }
            ranks[i] = 1.0 + (first + last) / 2.0;
        }
        return ranks;
    }

    public static double[] concat(double[] a, double[] b) {
        double[] result = new double[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public record MannWhitneyResult(double u1, double u2, double smallerU) {
    }

    public static MannWhitneyResult mannWhitneyU(double[] x, double[] y) {
        int n1 = x.length;
        int n2 = y.length;
        double[] ranks = rank(concat(x, y));
        double r1 = 0;
        for (int i = 0; i < n1; i++) {
            r1 += ranks[i];
        }
        double u1 = r1 - n1 * (n1 + 1) / 2.0;
        double u2 = n1 * n2 - u1;
        return new MannWhitneyResult(u1, u2, Math.min(u1, u2));
    }

    public record WilcoxonResult(double wPlus, double wMinus, int n) {
    }

    public static WilcoxonResult wilcoxonSignedRank(double[] before, double[] after) {
        double[] diffs = new double[before.length];
        int[] signs = new int[before.length];
        int n = 0;
        for (int i = 0; i < before.length; i++) {
            double d = after[i] - before[i];
            if (d != 0) {
                diffs[n] = Math.abs(d);
                signs[n] = d > 0 ? 1 : -1;
                n++;
            }
        }
        double[] nonZero = Arrays.copyOf(diffs, n);
        double[] ranks = rank(nonZero);
        double wPlus = 0;
        double wMinus = 0;
        for (int i = 0; i < n; i++) {
            if (signs[i] > 0) {
                wPlus += ranks[i];
            } else {
                wMinus += ranks[i];
            }
        }
        return new WilcoxonResult(wPlus, wMinus, n);
    }

    public static double kruskalWallis(double[]... groups) {
        int k = groups.length;
        int totalN = 0;
        for (double[] g : groups) {
            totalN += g.length;
        }
        double[] all = new double[totalN];
        int[] groupIndex = new int[totalN];
        int pos = 0;
        for (int g = 0; g < k; g++) {
            for (double v : groups[g]) {
                all[pos] = v;
                groupIndex[pos] = g;
                pos++;
            }
        }
        double[] ranks = rank(all);
        double h = 0;
        int offset = 0;
        for (int g = 0; g < k; g++) {
            double sumR = 0;
            for (int i = 0; i < groups[g].length; i++) {
                sumR += ranks[offset + i];
            }
            offset += groups[g].length;
            h += sumR * sumR / groups[g].length;
        }
        return 12.0 / (totalN * (totalN + 1)) * h - 3.0 * (totalN + 1);
    }

    public static double friedman(double[][] data) {
        int b = data.length;
        int k = data[0].length;
        double[] colRanks = new double[k];
        for (int i = 0; i < b; i++) {
            double[] ranks = rank(data[i]);
            for (int j = 0; j < k; j++) {
                colRanks[j] += ranks[j];
            }
        }
        double q = 0;
        for (double r : colRanks) {
            q += r * r;
        }
        return 12.0 / (b * k * (k + 1)) * q - 3.0 * b * (k + 1);
    }

    public static void main(String[] args) {
        System.out.println("=== Airbnb: review sentiment, old vs new rating scale ===");
        double[] oldScale = {2, 3, 4, 5};
        double[] newScale = {8, 9, 10, 11};
        MannWhitneyResult mw = mannWhitneyU(oldScale, newScale);
        System.out.printf("U1=%.1f, U2=%.1f, smaller U=%.1f%n",
            mw.u1(), mw.u2(), mw.smallerU());

        System.out.println("\n=== Airbnb: outlier-heavy review sets ===");
        double[] oldWithOutlier = {1, 2, 3, 4, 100};
        double[] newRobust = {6, 7, 8, 9, 10};
        MannWhitneyResult mwOut = mannWhitneyU(oldWithOutlier, newRobust);
        System.out.printf("U1=%.1f, U2=%.1f, smaller U=%.1f%n",
            mwOut.u1(), mwOut.u2(), mwOut.smallerU());

        System.out.println("\n=== Airbnb: host response times, before vs after ===");
        double[] before = {5, 6, 7, 8, 9};
        double[] after = {4, 4, 4, 4, 4};
        WilcoxonResult wil = wilcoxonSignedRank(before, after);
        System.out.printf("W+ = %.1f, W- = %.1f, n = %d%n",
            wil.wPlus(), wil.wMinus(), wil.n());

        System.out.println("\n=== Airbnb: nightly price across 3 cities ===");
        double[] chicago = {2, 4, 6};
        double[] austin = {8, 10, 12};
        double[] sf = {14, 16, 18};
        double h = kruskalWallis(chicago, austin, sf);
        System.out.printf("H statistic = %.6f%n", h);

        System.out.println("\n=== Airbnb: similar-median cities ===");
        double[] s1 = {1, 5, 9};
        double[] s2 = {2, 6, 10};
        double[] s3 = {3, 7, 11};
        double hSim = kruskalWallis(s1, s2, s3);
        System.out.printf("H statistic = %.6f%n", hSim);

        System.out.println("\n=== Airbnb: photo order preferences across cities ===");
        double[][] prefs = {
            {1, 2, 3},
            {1, 2, 3},
            {1, 2, 3},
            {1, 2, 3}
        };
        double q = friedman(prefs);
        System.out.printf("Friedman Q = %.6f%n", q);
    }
}
```

### Expected Output
```
=== Airbnb: review sentiment, old vs new rating scale ===
U1=0.0, U2=16.0, smaller U=0.0

=== Airbnb: outlier-heavy review sets ===
U1=5.0, U2=20.0, smaller U=5.0

=== Airbnb: host response times, before vs after ===
W+ = 0.0, W- = 15.0, n = 5

=== Airbnb: nightly price across 3 cities ===
H statistic = 7.200000

=== Airbnb: similar-median cities ===
H statistic = 0.800000

=== Airbnb: photo order preferences across cities ===
Friedman Q = 8.000000
```

### Company Evaluation
- Airbnb: ordinal review ratings, small-city samples, outlier-heavy pricing, within-city rankings.
- Google: click-through rankings by page section, non-normal latency metrics, blocked A/B designs.
- Netflix: view-time ranks across recommendation slates, small-canvas tests with heavy tails.
- Uber: driver ratings (skewed), ETA differences across cities, trip-time blocking.

---

## Problem 2: Driver Rating Comparison — Company: Uber
### Interview Scenario
"You're at Uber comparing driver ratings between two cohorts: standard drivers and drivers who opted into a new incentive program. Ratings are capped 1-5 star values, heavily left-skewed, with a few 1-star outliers. The classic t-test reports significance driven by the outliers. How do you analyze this honestly?"

### The Problem
1. Run Mann-Whitney U on the two rating cohorts
2. Compare the outcome to what a t-test would report
3. State the interpretation in terms of stochastic dominance, not means

### Solution Walkthrough
- Step 1: `mannWhitneyU` ranks the pooled 10 ratings; with one cohort containing a 100x-scale outlier in its own space (a 1-star rating dominates the low end), `smallerU` tells you how far the rank sums sit from the null expectation n₁n₂/2 = 12.5
- Step 2: The outlier only moves one rank position, so the statistic moves a bounded amount; a t-test's pooled variance would be inflated by the outlier and its p-value distorted
- Step 3: Report P(rank of incentive driver > rank of standard driver) rather than a mean difference

### Code
```java
double[] standard = {1, 2, 3, 4, 100};
double[] incentive = {6, 7, 8, 9, 10};
MannWhitneyResult mw = mannWhitneyU(standard, incentive);
System.out.printf("U1=%.1f, U2=%.1f, smaller U=%.1f%n",
    mw.u1(), mw.u2(), mw.smallerU());
```

### Expected Output
```
U1=5.0, U2=20.0, smaller U=5.0
```

---

## Problem 3: Content Rankings Across Weeks — Company: Netflix
### Interview Scenario
"You're at Netflix testing three home-page orderings (A: trending first, B: personalized first, C: new releases first) in four regions. Each region's weekly view counts for the three orderings are recorded. Do the orderings differ once region-to-region scale differences are removed?"

### The Problem
1. Choose the right blocked rank test
2. Compute Q
3. Interpret against the chi-square table

### Solution Walkthrough
- Step 1: This is a randomized block design — regions are blocks, orderings are treatments — so `friedman` is the right test; ranking within each block strips out region scale
- Step 2: With 4 blocks × 3 treatments where ordering A always wins within-block, Q = 8.000000
- Step 3: Compare against χ²(2) at α = 0.05: critical value 5.99, so Q = 8.00 rejects — and 8.00 is exactly the χ²(2, 0.05) critical value the lab's GUIDE cites, a built-in verification of the implementation

### Code
```java
double[][] weeks = {
    {1, 2, 3},
    {1, 2, 3},
    {1, 2, 3},
    {1, 2, 3}
};
double q = friedman(weeks);
System.out.printf("Friedman Q = %.6f%n", q);
```

### Expected Output
```
Friedman Q = 8.000000
```

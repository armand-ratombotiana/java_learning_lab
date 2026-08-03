# Problem Walkthrough: ANOVA

## Problem 1: Thumbnail Design Test — Company: Netflix
### Interview Scenario
"You're at Netflix on the personalization experimentation team. Three thumbnail design variants (A, B, C) were shown to 5 randomized member cohorts each, and weekly watch time (minutes) was recorded per member: A ≈ 12.5, B ≈ 15.1, C ≈ 12.0. Product wants to know: is there any real difference, and if so, exactly which designs beat which? The launch decision needs a single test with a controlled error rate — not three pairwise t-tests."

### The Problem
1. Run a one-way ANOVA across the three designs at α = 0.05
2. Report the full ANOVA table: SSB, SSW, SST, df, MSB, MSW, F, p-value
3. Only if significant, run Tukey's HSD on all three pairs
4. Identify which designs differ and by how much
5. Run a control case with near-identical designs to show a non-significant outcome

### Solution Walkthrough
- Step 1: Reuse the lab's `Anova` class verbatim: `oneWayAnova`, `AnovaResult`, `TukeyResult`, `tukeyHSD` — same `studentizedRangeQ` approximation and the same `incompleteBeta`-based `fCdf`
- Step 2: `oneWayAnova` computes the grand mean, group means, SSB = Σ nᵢ(x̄ᵢ - x̄)², SSW = ΣΣ(xᵢⱼ - x̄ᵢ)²; the result: SSB = 31.8880, SSW = 2.1480, SST = 34.0360, dfB = 2, dfW = 12
- Step 3: MSB/MSW = 15.9440/0.1790 → F = 89.0726, p < 0.000001, `significant = true` — the omnibus test rejects
- Step 4: The `significant` flag gates Tukey: `tukeyHSD(groups, names, result.msw, alpha)` with q ≈ 3.31 + 2/12 for k = 3; HSD = 0.6578
- Step 5: Pairs: A vs B (diff -2.80, significant), A vs C (diff 0.52, not), B vs C (diff 3.32, significant) — design B is the clear winner
- Step 6: Control run on three near-identical designs: F = 0.0513, p = 0.950218, `significant = false` — no Tukey needed; validates the funnel

### Code
```java
package com.statistics.lab04;

/**
 * Mirrors the lab's Anova class (one-way ANOVA, Tukey's HSD post-hoc)
 * and applies it to a Netflix-style thumbnail experiment.
 */
public final class ThumbnailExperiment {

    private ThumbnailExperiment() {
    }

    public static class AnovaResult {
        public final double ssb, ssw, sst;
        public final int dfb, dfw, dft;
        public final double msb, msw;
        public final double fStatistic;
        public final double pValue;
        public final boolean significant;

        public AnovaResult(double ssb, double ssw, int dfb, int dfw,
                           double fStatistic, double pValue, double alpha) {
            this.ssb = ssb;
            this.ssw = ssw;
            this.sst = ssb + ssw;
            this.dfb = dfb;
            this.dfw = dfw;
            this.dft = dfb + dfw;
            this.msb = ssb / dfb;
            this.msw = ssw / dfw;
            this.fStatistic = fStatistic;
            this.pValue = pValue;
            this.significant = pValue < alpha;
        }

        @Override
        public String toString() {
            return String.format(
                "ANOVA:%n  SSB=%.4f, SSW=%.4f, SST=%.4f%n  dfB=%d, dfW=%d, dfT=%d%n  MSB=%.4f, MSW=%.4f%n  F=%.4f, p=%.6f, significant=%b",
                ssb, ssw, sst, dfb, dfw, dft, msb, msw, fStatistic, pValue, significant);
        }
    }

    public static AnovaResult oneWayAnova(double[][] groups, double alpha) {
        int k = groups.length;
        int n = 0;
        for (double[] g : groups) {
            n += g.length;
        }
        double grandSum = 0;
        for (double[] g : groups) {
            for (double v : g) {
                grandSum += v;
            }
        }
        double grandMean = grandSum / n;
        double[] groupMeans = new double[k];
        int[] groupSizes = new int[k];
        for (int i = 0; i < k; i++) {
            double sum = 0;
            for (double v : groups[i]) {
                sum += v;
            }
            groupSizes[i] = groups[i].length;
            groupMeans[i] = sum / groupSizes[i];
        }
        double ssb = 0;
        for (int i = 0; i < k; i++) {
            ssb += groupSizes[i] * (groupMeans[i] - grandMean) * (groupMeans[i] - grandMean);
        }
        double ssw = 0;
        for (int i = 0; i < k; i++) {
            for (double v : groups[i]) {
                ssw += (v - groupMeans[i]) * (v - groupMeans[i]);
            }
        }
        int dfb = k - 1;
        int dfw = n - k;
        double msw = ssw / dfw;
        double msb = ssb / dfb;
        double f = msb / msw;
        double p = 1 - fCdf(f, dfb, dfw);
        return new AnovaResult(ssb, ssw, dfb, dfw, f, p, alpha);
    }
    public static record TukeyResult(String group1, String group2, double diff, double hsd, boolean significant) {
        @Override
        public String toString() {
            return String.format("%s vs %s: diff=%.4f, HSD=%.4f, significant=%b",
                group1, group2, diff, hsd, significant);
        }
    }
    public static TukeyResult[] tukeyHSD(double[][] groups, String[] groupNames, double msw, double alpha) {
        int k = groups.length;
        double q = studentizedRangeQ(k, totalN(groups) - k, alpha);
        int numPairs = k * (k - 1) / 2;
        TukeyResult[] results = new TukeyResult[numPairs];
        int idx = 0;
        for (int i = 0; i < k; i++) {
            for (int j = i + 1; j < k; j++) {
                double diff = mean(groups[i]) - mean(groups[j]);
                double se = Math.sqrt(msw / 2.0 * (1.0 / groups[i].length + 1.0 / groups[j].length));
                double hsd = q * se;
                results[idx++] = new TukeyResult(groupNames[i], groupNames[j], diff, hsd, Math.abs(diff) > hsd);
            }
        }
        return results;
    }
    private static double mean(double[] data) {
        double sum = 0;
        for (double v : data) sum += v;
        return sum / data.length;
    }
    private static int totalN(double[][] groups) {
        int n = 0;
        for (double[] g : groups) n += g.length;
        return n;
    }
    private static double studentizedRangeQ(int k, int df, double alpha) {
        if (alpha != 0.05) {
            return 3.0;
        }
        double[] qs = {2.77, 3.31, 3.63, 3.86};
        return k <= 5 ? qs[k - 2] + 2.0 / df : 3.86 + 0.2 * (k - 5) + 2.0 / df;
    }
    private static double fCdf(double f, int df1, int df2) {
        double x = df1 * f / (df1 * f + df2);
        return incompleteBeta(x, df1 / 2.0, df2 / 2.0);
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
        double qab = a + b, qap = a + 1, qam = a - 1;
        double c = 1, d = 1 - qab * x / qap;
        d = Math.abs(d) < 1e-30 ? 1e-30 : d;
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
            if (Math.abs(del - 1) < eps) break;
        }
        return h;
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
    public static void main(String[] args) {
        double alpha = 0.05;
        System.out.println("=== One-Way ANOVA: 3 thumbnail designs, watch minutes ===");
        double[][] groups = {
            {12.4, 13.1, 11.9, 12.8, 12.2},
            {15.2, 16.0, 14.8, 15.5, 14.9},
            {12.0, 11.6, 12.3, 11.8, 12.1}
        };
        String[] names = {"Design A", "Design B", "Design C"};
        AnovaResult result = oneWayAnova(groups, alpha);
        System.out.println(result);
        if (result.significant) {
            System.out.println("\n=== Tukey's HSD Post-Hoc (which designs differ?) ===");
            TukeyResult[] tukey = tukeyHSD(groups, names, result.msw, alpha);
            for (TukeyResult tr : tukey) {
                System.out.println("  " + tr);
            }
        }

        System.out.println("\n=== One-Way ANOVA: near-identical designs (sanity check) ===");
        double[][] groups2 = {
            {12.0, 12.1, 11.9, 12.2, 12.0},
            {12.1, 12.0, 12.2, 11.9, 12.1},
            {12.0, 11.9, 12.1, 12.0, 12.2}
        };
        AnovaResult result2 = oneWayAnova(groups2, alpha);
        System.out.println(result2);
    }
}
```

### Expected Output
```
=== One-Way ANOVA: 3 thumbnail designs, watch minutes ===
ANOVA:
  SSB=31.8880, SSW=2.1480, SST=34.0360
  dfB=2, dfW=12, dfT=14
  MSB=15.9440, MSW=0.1790
  F=89.0726, p=0.000000, significant=true

=== Tukey's HSD Post-Hoc (which designs differ?) ===
  Design A vs Design B: diff=-2.8000, HSD=0.6578, significant=true
  Design A vs Design C: diff=0.5200, HSD=0.6578, significant=false
  Design B vs Design C: diff=3.3200, HSD=0.6578, significant=true

=== One-Way ANOVA: near-identical designs (sanity check) ===
ANOVA:
  SSB=0.0013, SSW=0.1560, SST=0.1573
  dfB=2, dfW=12, dfT=14
  MSB=0.0007, MSW=0.0130
  F=0.0513, p=0.950218, significant=false
```

### Company Evaluation
- Netflix: multi-variant creative tests, watch-time as the response, Tukey shortlists before rollout.
- Spotify: playlist shuffling algorithms across device groups, session-length ANOVA.
- Booking.com: multi-price-point experiments, omnibus test then HSD against control.
- Airbnb: search ranking cohorts, review-snippet variants, non-parametric Kruskal-Wallis fallback for skewed host metrics.

---

## Problem 2: Landing Page Copy, Three Variants — Company: Booking.com
### Interview Scenario
"You're at Booking.com. Three landing-page copy variants ran on small cohorts with 5 sessions each: A {23, 25, 21, 24, 22}, B {28, 30, 27, 29, 31}, C {20, 22, 19, 21, 23} (booking-intent score). Decide whether the copies differ, and which pair to ship."

### The Problem
1. Run the one-way ANOVA on the three cohorts
2. If significant, run Tukey's HSD
3. Report which variant(s) beat which

### Solution Walkthrough
- Step 1: `oneWayAnova` on the lab's exact demo data: SSB = 173.3333, SSW = 30.0000, F = 34.6667, p = 0.000010 — significant
- Step 2: Tukey with `result.msw = 2.5`, q ≈ 3.4778 (k=3, df=12), HSD = 2.4584
- Step 3: B vs A (diff 6.0) and B vs C (diff 8.0) exceed HSD; A vs C (diff 2.0) does not — variant B is the ship candidate

### Code
```java
double[][] groups = {
    {23, 25, 21, 24, 22},
    {28, 30, 27, 29, 31},
    {20, 22, 19, 21, 23}
};
String[] names = {"Copy A", "Copy B", "Copy C"};
AnovaResult r = oneWayAnova(groups, 0.05);
System.out.println(r);
if (r.significant) {
    for (TukeyResult tr : tukeyHSD(groups, names, r.msw, 0.05)) {
        System.out.println("  " + tr);
    }
}
```

### Expected Output
```
ANOVA:
  SSB=173.3333, SSW=30.0000, SST=203.3333
  dfB=2, dfW=12, dfT=14
  MSB=86.6667, MSW=2.5000
  F=34.6667, p=0.000010, significant=true

  Copy A vs Copy B: diff=-6.0000, HSD=2.4584, significant=true
  Copy A vs Copy C: diff=2.0000, HSD=2.4584, significant=false
  Copy B vs Copy C: diff=8.0000, HSD=2.4584, significant=true
```

---

## Problem 3: App Theme Colors, No Effect Found — Company: Spotify
### Interview Scenario
"You're at Spotify A/B-testing three app theme colors on playlist play counts. The cohorts return near-identical averages {11, 12, 12}-ish. Product is asking you to 'just pick the best one'."

### The Problem
1. Run the ANOVA on the near-identical cohorts
2. Explain why stopping at the omnibus result is correct
3. State what would be needed to distinguish the colors

### Solution Walkthrough
- Step 1: `oneWayAnova` on {10,11,10,12,11}, {11,10,12,11,10}, {12,11,10,11,12}: F = 0.3810, p = 0.691185 — no evidence of a difference
- Step 2: The `significant = false` flag means no post-hoc, no winner — 'picking the best one' would be selecting among noise
- Step 3: Distinguishing a ~0.5 unit effect would need far larger cohorts; the honest answer to product is 'we can't tell them apart with this sample — here's the power analysis needed'

### Code
```java
double[][] groups2 = {
    {10, 11, 10, 12, 11},
    {11, 10, 12, 11, 10},
    {12, 11, 10, 11, 12}
};
System.out.println(oneWayAnova(groups2, 0.05));
```

### Expected Output
```
ANOVA:
  SSB=0.5333, SSW=8.4000, SST=8.9333
  dfB=2, dfW=12, dfT=14
  MSB=0.2667, MSW=0.7000
  F=0.3810, p=0.691185, significant=false
```

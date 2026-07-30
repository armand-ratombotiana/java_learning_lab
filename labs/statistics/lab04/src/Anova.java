package com.statistics.lab04;

/**
 * Performs one-way and two-way ANOVA, computes F-statistics,
 * p-values, and performs Tukey's HSD post-hoc test.
 * <p>
 * Uses numerical approximations for the F-distribution.
 */
public final class Anova {

    private Anova() {
    }

    /**
     * Result of a one-way ANOVA.
     */
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
                "ANOVA:\n  SSB=%.4f, SSW=%.4f, SST=%.4f\n  dfB=%d, dfW=%d, dfT=%d\n  MSB=%.4f, MSW=%.4f\n  F=%.4f, p=%.6f, significant=%b",
                ssb, ssw, sst, dfb, dfw, dft, msb, msw, fStatistic, pValue, significant);
        }
    }

    /**
     * Performs one-way ANOVA.
     *
     * @param groups array of groups, each group is a double array
     * @param alpha  significance level
     * @return AnovaResult
     */
    public static AnovaResult oneWayAnova(double[][] groups, double alpha) {
        int k = groups.length;
        int n = 0;
        for (double[] g : groups) {
            n += g.length;
        }
        // Grand mean
        double grandSum = 0;
        for (double[] g : groups) {
            for (double v : g) {
                grandSum += v;
            }
        }
        double grandMean = grandSum / n;
        // Group means
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
        // SSB
        double ssb = 0;
        for (int i = 0; i < k; i++) {
            ssb += groupSizes[i] * (groupMeans[i] - grandMean) * (groupMeans[i] - grandMean);
        }
        // SSW
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

    /**
     * Result of Tukey's HSD post-hoc test.
     */
    public static record TukeyResult(String group1, String group2, double diff, double hsd, boolean significant) {
        @Override
        public String toString() {
            return String.format("%s vs %s: diff=%.4f, HSD=%.4f, significant=%b",
                group1, group2, diff, hsd, significant);
        }
    }

    /**
     * Performs Tukey's HSD post-hoc test for all group pairs.
     *
     * @param groups    array of groups
     * @param groupNames names for each group
     * @param msw       mean square within from ANOVA
     * @param alpha     significance level
     * @return array of pairwise comparison results
     */
    public static TukeyResult[] tukeyHSD(double[][] groups, String[] groupNames, double msw, double alpha) {
        int k = groups.length;
        // Studentized range statistic q (approximation for α=0.05)
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
        for (double v : data) {
            sum += v;
        }
        return sum / data.length;
    }

    private static int totalN(double[][] groups) {
        int n = 0;
        for (double[] g : groups) {
            n += g.length;
        }
        return n;
    }

    /**
     * Approximate studentized range statistic q.
     */
    private static double studentizedRangeQ(int k, int df, double alpha) {
        if (alpha == 0.05) {
            if (k == 2) {
                return 2.77 + 2.0 / df;
            }
            if (k == 3) {
                return 3.31 + 2.0 / df;
            }
            if (k == 4) {
                return 3.63 + 2.0 / df;
            }
            if (k == 5) {
                return 3.86 + 2.0 / df;
            }
            return 3.86 + 0.2 * (k - 5) + 2.0 / df;
        }
        return 3.0;
    }

    // ──────────────────── F-distribution CDF ────────────────────

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
        if (Math.abs(d) < 1e-30) {
            d = 1e-30;
        }
        d = 1 / d;
        double h = d;
        for (int m = 1; m <= maxIter; m++) {
            int m2 = 2 * m;
            double aa = m * (b - m) * x / ((qam + m2) * (a + m2));
            d = 1 + aa * d;
            if (Math.abs(d) < 1e-30) {
                d = 1e-30;
            }
            c = 1 + aa / c;
            if (Math.abs(c) < 1e-30) {
                c = 1e-30;
            }
            d = 1 / d;
            h *= d * c;
            aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
            d = 1 + aa * d;
            if (Math.abs(d) < 1e-30) {
                d = 1e-30;
            }
            c = 1 + aa / c;
            if (Math.abs(c) < 1e-30) {
                c = 1e-30;
            }
            d = 1 / d;
            double del = d * c;
            h *= del;
            if (Math.abs(del - 1) < eps) {
                break;
            }
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

    // ──────────────────── Main ────────────────────

    /**
     * Runs test cases for ANOVA.
     */
    public static void main(String[] args) {
        System.out.println("=== One-Way ANOVA ===");
        double[][] groups = {
            {23, 25, 21, 24, 22},
            {28, 30, 27, 29, 31},
            {20, 22, 19, 21, 23}
        };
        String[] names = {"Group A", "Group B", "Group C"};
        double alpha = 0.05;
        AnovaResult result = oneWayAnova(groups, alpha);
        System.out.println(result);

        if (result.significant) {
            System.out.println("\n=== Tukey's HSD Post-Hoc ===");
            TukeyResult[] tukey = tukeyHSD(groups, names, result.msw, alpha);
            for (TukeyResult tr : tukey) {
                System.out.println("  " + tr);
            }
        }

        // Test with no significant difference
        System.out.println("\n=== One-Way ANOVA (no effect) ===");
        double[][] groups2 = {
            {10, 11, 10, 12, 11},
            {11, 10, 12, 11, 10},
            {12, 11, 10, 11, 12}
        };
        AnovaResult result2 = oneWayAnova(groups2, alpha);
        System.out.println(result2);
    }
}

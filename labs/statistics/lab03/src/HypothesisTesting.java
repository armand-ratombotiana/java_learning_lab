package com.statistics.lab03;

/**
 * Performs hypothesis tests: one-sample t-test, two-sample t-test,
 * paired t-test, z-test, chi-square goodness-of-fit, and
 * chi-square test of independence.
 * <p>
 * Uses numerical approximations for the t-distribution,
 * normal distribution, and chi-square distribution CDFs.
 */
public final class HypothesisTesting {

    private HypothesisTesting() {
    }

    // ──────────────────── T-Tests ────────────────────

    /**
     * Performs a one-sample t-test.
     *
     * @param data   sample values
     * @param mu0    null hypothesis population mean
     * @param alpha  significance level
     * @return test summary string
     */
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
            "One-sample t-test: t=%.4f, df=%d, p=%.6f, reject H0=%b (α=%.2f)%n  sample mean=%.4f, H0 mean=%.4f",
            t, df, pValue, reject, alpha, mean, mu0);
    }

    /**
     * Performs a two-sample (independent) t-test assuming equal variance.
     *
     * @param data1 first group
     * @param data2 second group
     * @param alpha significance level
     * @return test summary string
     */
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
            "Two-sample t-test: t=%.4f, df=%d, p=%.6f, reject H0=%b (α=%.2f)%n  mean1=%.4f, mean2=%.4f",
            t, df, pValue, reject, alpha, m1, m2);
    }

    /**
     * Performs a paired t-test.
     *
     * @param before paired before measurements
     * @param after  paired after measurements
     * @param alpha  significance level
     * @return test summary string
     */
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
            "Paired t-test: t=%.4f, df=%d, p=%.6f, reject H0=%b (α=%.2f)%n  mean diff=%.4f",
            t, df, pValue, reject, alpha, dBar);
    }

    // ──────────────────── Z-Test ────────────────────

    /**
     * Performs a one-sample z-test (requires known population standard deviation).
     *
     * @param data       sample values
     * @param mu0        null hypothesis population mean
     * @param sigma      known population standard deviation
     * @param alpha      significance level
     * @return test summary string
     */
    public static String zTest(double[] data, double mu0, double sigma, double alpha) {
        int n = data.length;
        double m = mean(data);
        double se = sigma / Math.sqrt(n);
        double z = (m - mu0) / se;
        double pValue = 2 * (1 - normalCdf(Math.abs(z)));
        boolean reject = pValue < alpha;
        return String.format(
            "Z-test: z=%.4f, p=%.6f, reject H0=%b (α=%.2f)%n  sample mean=%.4f, H0 mean=%.4f",
            z, pValue, reject, alpha, m, mu0);
    }

    // ──────────────────── Chi-Square Tests ────────────────────

    /**
     * Performs a chi-square goodness-of-fit test.
     *
     * @param observed array of observed frequencies
     * @param expected array of expected frequencies
     * @param alpha    significance level
     * @return test summary string
     */
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
            "Chi-square goodness-of-fit: χ²=%.4f, df=%d, p=%.6f, reject H0=%b (α=%.2f)",
            chiSq, df, pValue, reject, alpha);
    }

    /**
     * Performs a chi-square test of independence.
     *
     * @param table 2D contingency table
     * @param alpha significance level
     * @return test summary string
     */
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
            "Chi-square independence: χ²=%.4f, df=%d, p=%.6f, reject H0=%b (α=%.2f)",
            chiSq, df, pValue, reject, alpha);
    }

    // ──────────────────── Distribution helpers ────────────────────

    private static double mean(double[] data) {
        double sum = 0;
        for (double v : data) {
            sum += v;
        }
        return sum / data.length;
    }

    private static double sampleVariance(double[] data) {
        double m = mean(data);
        double ss = 0;
        for (double v : data) {
            ss += (v - m) * (v - m);
        }
        return ss / (data.length - 1);
    }

    private static double stdDev(double[] data) {
        return Math.sqrt(sampleVariance(data));
    }

    /**
     * Standard normal CDF using error function.
     */
    private static double normalCdf(double z) {
        return 0.5 * (1 + erf(z / Math.sqrt(2)));
    }

    private static double erf(double x) {
        double a1 = 0.254829592, a2 = -0.284496736, a3 = 1.421413741;
        double a4 = -1.453152027, a5 = 1.061405429, p = 0.3275911;
        int sign = (x < 0) ? -1 : 1;
        x = Math.abs(x);
        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);
        return sign * y;
    }

    /**
     * t-distribution CDF using regularized incomplete beta function.
     */
    private static double tCdf(double t, int df) {
        double x = df / (t * t + df);
        return 1 - 0.5 * incompleteBeta(x, df / 2.0, 0.5);
    }

    /**
     * Chi-square CDF using regularized incomplete gamma function.
     */
    private static double chiSquareCdf(double x, int df) {
        return regularizedGamma(df / 2.0, x / 2.0);
    }

    /**
     * Regularized incomplete gamma function P(a, x) using series expansion.
     */
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

    /**
     * Natural log of gamma function using Lanczos approximation.
     */
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

    /**
     * Regularized incomplete beta function using continued fraction.
     */
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

    // ──────────────────── Main ────────────────────

    /**
     * Runs test cases for all hypothesis tests.
     */
    public static void main(String[] args) {
        double alpha = 0.05;

        System.out.println("=== One-Sample T-Test ===");
        double[] data = {2.3, 2.8, 3.1, 2.9, 2.5, 3.0, 2.7, 2.6, 2.9, 2.4};
        System.out.println(oneSampleTTest(data, 2.5, alpha));

        System.out.println("\n=== Two-Sample T-Test ===");
        double[] group1 = {2.3, 2.8, 3.1, 2.9, 2.5};
        double[] group2 = {3.2, 3.5, 3.0, 3.8, 3.3};
        System.out.println(twoSampleTTest(group1, group2, alpha));

        System.out.println("\n=== Paired T-Test ===");
        double[] before = {70, 75, 68, 72, 74};
        double[] after = {72, 78, 70, 76, 73};
        System.out.println(pairedTTest(before, after, alpha));

        System.out.println("\n=== Z-Test ===");
        double[] large = {52, 48, 51, 53, 49, 50, 52, 47, 51, 50,
            53, 49, 48, 52, 50, 51, 49, 50, 52, 48};
        System.out.println(zTest(large, 50, 3, alpha));

        System.out.println("\n=== Chi-Square Goodness-of-Fit ===");
        long[] observed = {25, 30, 20, 25};
        double[] expected = {25, 25, 25, 25};
        System.out.println(chiSquareGoodnessOfFit(observed, expected, alpha));

        System.out.println("\n=== Chi-Square Independence ===");
        long[][] table = {
            {30, 20},
            {15, 35}
        };
        System.out.println(chiSquareIndependence(table, alpha));
    }
}

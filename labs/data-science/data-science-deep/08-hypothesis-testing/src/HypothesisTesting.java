package com.datascience.deep.lab08;

import java.util.*;

public final class HypothesisTesting {

    // -- t-Tests --

    public record TTestResult(double t, double p, double df, double meanDiff,
                              double ciLo, double ciHi, String type) {
        public TTestResult { String.valueOf(type); }
    }

    public static TTestResult oneSampleTTest(double[] sample, double mu0, double alpha) {
        int n = sample.length;
        double m = mean(sample);
        double se = Math.sqrt(variance(sample) / n);
        double t = (m - mu0) / se;
        double df = n - 1;
        double p = 2.0 * (1.0 - studentTCdf(Math.abs(t), df));
        double me = studentTQuantile(1.0 - alpha / 2.0, df) * se;
        return new TTestResult(t, p, df, m - mu0, m - me, m + me, "One-sample t-test");
    }

    public static TTestResult welchTTest(double[] x, double[] y, double alpha) {
        double n1 = x.length, n2 = y.length;
        double m1 = mean(x), m2 = mean(y);
        double v1 = variance(x), v2 = variance(y);
        double se = Math.sqrt(v1 / n1 + v2 / n2);
        double t = (m1 - m2) / se;
        double df = Math.pow(v1 / n1 + v2 / n2, 2)
            / (Math.pow(v1 / n1, 2) / (n1 - 1) + Math.pow(v2 / n2, 2) / (n2 - 1));
        double p = 2.0 * (1.0 - studentTCdf(Math.abs(t), df));
        double me = studentTQuantile(1.0 - alpha / 2.0, df) * se;
        return new TTestResult(t, p, df, m1 - m2, (m1 - m2) - me, (m1 - m2) + me, "Welch t-test");
    }

    public static TTestResult pairedTTest(double[] before, double[] after, double alpha) {
        double[] diffs = new double[before.length];
        for (int i = 0; i < before.length; i++) diffs[i] = after[i] - before[i];
        return oneSampleTTest(diffs, 0.0, alpha);
    }

    // -- Chi-Square Test --

    public record ChiSquareResult(double chiSq, double p, int df, double cramersV) {}

    public static ChiSquareResult chiSquareTest(int[][] table) {
        int rows = table.length, cols = table[0].length;
        double[] rowSums = new double[rows];
        double[] colSums = new double[cols];
        int n = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rowSums[i] += table[i][j];
                colSums[j] += table[i][j];
                n += table[i][j];
            }
        }
        double chiSq = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double expected = rowSums[i] * colSums[j] / n;
                if (expected > 0) chiSq += Math.pow(table[i][j] - expected, 2) / expected;
            }
        }
        int df = (rows - 1) * (cols - 1);
        double p = 1.0 - chiSquareCdf(chiSq, df);
        double cramersV = Math.sqrt(chiSq / (n * Math.min(rows - 1, cols - 1)));
        return new ChiSquareResult(chiSq, p, df, cramersV);
    }

    // -- Mann-Whitney U Test --

    public record MannWhitneyResult(double u, double p, double rankBiserial) {}

    public static MannWhitneyResult mannWhitneyTest(double[] x, double[] y) {
        int n1 = x.length, n2 = y.length;
        double[] combined = new double[n1 + n2];
        System.arraycopy(x, 0, combined, 0, n1);
        System.arraycopy(y, 0, combined, n1, n2);
        double[] ranks = rank(combined);
        double r1 = 0;
        for (int i = 0; i < n1; i++) r1 += ranks[i];
        double u1 = r1 - (double) n1 * (n1 + 1) / 2.0;
        double u2 = (double) n1 * n2 - u1;
        double u = Math.min(u1, u2);
        double mu = (double) n1 * n2 / 2.0;
        double su = Math.sqrt((double) n1 * n2 * (n1 + n2 + 1) / 12.0);
        double z = (u - mu) / su;
        double p = 2.0 * (1.0 - normalCDF(Math.abs(z)));
        double rb = 1.0 - 2.0 * u / (n1 * n2);
        return new MannWhitneyResult(u, p, rb);
    }

    // -- One-Way ANOVA --

    public record AnovaResult(double f, double p, int dfBetween, int dfWithin, double etaSq, double omegaSq) {}

    public static AnovaResult oneWayAnova(double[][] groups, double alpha) {
        int k = groups.length;
        double grandMean = 0;
        int totalN = 0;
        for (double[] g : groups) { for (double v : g) grandMean += v; totalN += g.length; }
        grandMean /= totalN;
        double ssBetween = 0, ssWithin = 0;
        for (int j = 0; j < k; j++) {
            double gm = Arrays.stream(groups[j]).average().orElseThrow();
            ssBetween += groups[j].length * Math.pow(gm - grandMean, 2);
            for (double v : groups[j]) ssWithin += Math.pow(v - gm, 2);
        }
        int dfB = k - 1, dfW = totalN - k;
        double msB = ssBetween / dfB, msW = ssWithin / dfW;
        double f = msB / msW;
        double p = 1.0 - fCdf(f, dfB, dfW);
        double etaSq = ssBetween / (ssBetween + ssWithin);
        double omegaSq = (ssBetween - dfB * msW) / (ssBetween + ssWithin + msW);
        return new AnovaResult(f, p, dfB, dfW, etaSq, omegaSq);
    }

    // -- Kruskal-Wallis Test --

    public record KruskalWallisResult(double h, double p, int df) {}

    public static KruskalWallisResult kruskalWallisTest(double[][] groups) {
        int k = groups.length;
        int n = Arrays.stream(groups).mapToInt(g -> g.length).sum();
        double[] all = new double[n];
        int[] gids = new int[n];
        int idx = 0;
        for (int j = 0; j < k; j++) {
            for (double v : groups[j]) { all[idx] = v; gids[idx] = j; idx++; }
        }
        double[] ranks = rank(all);
        double[] rSum = new double[k];
        for (int i = 0; i < n; i++) rSum[gids[i]] += ranks[i];
        double h = 12.0 / (n * (n + 1.0)) * Arrays.stream(rSum).map(r -> r * r / n).sum() - 3.0 * (n + 1);
        double p = 1.0 - chiSquareCdf(h, k - 1);
        return new KruskalWallisResult(h, p, k - 1);
    }

    // -- Bootstrap Test --

    public static double bootstrapTwoSample(double[] x, double[] y, int nResamples) {
        double obsDiff = mean(x) - mean(y);
        double[] combined = new double[x.length + y.length];
        System.arraycopy(x, 0, combined, 0, x.length);
        System.arraycopy(y, 0, combined, x.length, y.length);
        int n1 = x.length;
        Random rng = new Random(42L);
        int extreme = 0;
        for (int r = 0; r < nResamples; r++) {
            double[] res = new double[combined.length];
            for (int i = 0; i < combined.length; i++) res[i] = combined[rng.nextInt(combined.length)];
            double m1 = mean(Arrays.copyOf(res, n1));
            double m2 = mean(Arrays.copyOfRange(res, n1, res.length));
            if (Math.abs(m1 - m2) >= Math.abs(obsDiff)) extreme++;
        }
        return (double) extreme / nResamples;
    }

    // -- Utility --

    private static double mean(double[] x) { return Arrays.stream(x).average().orElseThrow(); }

    private static double variance(double[] x) {
        double m = mean(x);
        return Arrays.stream(x).map(v -> Math.pow(v - m, 2)).sum() / (x.length - 1);
    }

    private static double[] rank(double[] x) {
        int n = x.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, Comparator.comparingDouble(i -> x[i]));
        double[] ranks = new double[n];
        for (int i = 0; i < n; ) {
            int j = i;
            while (j < n && x[idx[j]] == x[idx[i]]) j++;
            double avg = (i + j + 1) / 2.0;
            for (int k = i; k < j; k++) ranks[idx[k]] = avg;
            i = j;
        }
        return ranks;
    }

    private static double studentTCdf(double t, double df) {
        double x = df / (df + t * t);
        return 1.0 - 0.5 * incompleteBeta(df / 2.0, 0.5, x);
    }

    private static double studentTQuantile(double p, double df) {
        if (df > 100) return normalQuantile(p);
        double t = normalQuantile(p);
        double t2 = t * t;
        return t + (t2 + t) / (2 * df) + (t2 + 1) * (t2 + 3) / (4 * df * df);
    }

    private static double normalCDF(double z) {
        double t = 1.0 / (1.0 + 0.2316419 * Math.abs(z));
        double d = 0.3989422804014327;
        double p = d * Math.exp(-z * z / 2.0)
            * (t * (0.319381530 + t * (-0.356563782 + t * (1.781477937
            + t * (-1.821255978 + t * 1.330274429)))));
        return z > 0 ? 1.0 - p : p;
    }

    private static double normalQuantile(double p) {
        if (p < 1e-15) return -8.0; if (p > 1 - 1e-15) return 8.0;
        double t = Math.sqrt(-2.0 * Math.log(1.0 - p));
        return t - (2.515517 + 0.802853 * t + 0.010328 * t * t)
            / (1.0 + 1.432788 * t + 0.189269 * t * t + 0.001308 * t * t * t);
    }

    private static double chiSquareCdf(double x, int k) { return incompleteBeta(k / 2.0, 0.5, x / (x + k)); }

    private static double fCdf(double f, int df1, int df2) {
        double x = df1 * f / (df1 * f + df2);
        return incompleteBeta(df1 / 2.0, df2 / 2.0, x);
    }

    private static double incompleteBeta(double a, double b, double x) {
        if (x < 0 || x > 1) return 0; if (x == 0 || x == 1) return x;
        double bt = Math.exp(logGamma(a + b) - logGamma(a) - logGamma(b)
            + a * Math.log(x) + b * Math.log(1.0 - x));
        return (x < (a + 1) / (a + b + 2)) ? bt * betaCF(a, b, x) / a : 1.0 - bt * betaCF(b, a, 1.0 - x) / b;
    }

    private static double betaCF(double a, double b, double x) {
        int maxIter = 200; double eps = 3e-12;
        double qab = a + b, qap = a + 1.0, qam = a - 1.0;
        double c = 1.0, d = 1.0 - qab * x / qap;
        if (Math.abs(d) < 1e-30) d = 1e-30; d = 1.0 / d; double h = d;
        for (int m = 1; m <= maxIter; m++) {
            int m2 = 2 * m;
            double aa = m * (b - m) * x / ((qam + m2) * (a + m2));
            d = 1.0 + aa * d; if (Math.abs(d) < 1e-30) d = 1e-30;
            c = 1.0 + aa / c; if (Math.abs(c) < 1e-30) c = 1e-30;
            d = 1.0 / d; h *= d * c;
            aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
            d = 1.0 + aa * d; if (Math.abs(d) < 1e-30) d = 1e-30;
            c = 1.0 + aa / c; if (Math.abs(c) < 1e-30) c = 1e-30;
            d = 1.0 / d; double del = d * c; h *= del;
            if (Math.abs(del - 1.0) < eps) break;
        }
        return h;
    }

    private static double logGamma(double x) {
        double[] cof = {76.18009172947146, -86.50532032941677, 24.01409824083091,
                        -1.231739572450155, 0.1208650973866179e-2, -0.5395239384953e-5};
        double y = x, tmp = x + 5.5; tmp -= (x + 0.5) * Math.log(tmp);
        double ser = 1.000000000190015;
        for (int j = 0; j < 6; j++) ser += cof[j] / ++y;
        return -tmp + Math.log(2.5066282746310005 * ser / x);
    }
}

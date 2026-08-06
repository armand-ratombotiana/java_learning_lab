package com.datascience.deep.lab06;

import java.util.*;
import java.util.function.DoubleUnaryOperator;

public final class StatisticalPower {

    public record PowerResult(double effectSize, int n, double alpha, double power) {}

    // -- Effect Sizes --

    public static double cohensD(double mean1, double mean2, double sd1, double sd2) {
        double pooled = Math.sqrt((sd1 * sd1 + sd2 * sd2) / 2.0);
        return Math.abs(mean1 - mean2) / pooled;
    }

    public static double cohensF(double[] groupMeans, double grandMean, double withinSD) {
        int k = groupMeans.length;
        double ssBetween = 0;
        for (int j = 0; j < k; j++) ssBetween += Math.pow(groupMeans[j] - grandMean, 2);
        double msBetween = ssBetween / (k - 1);
        return Math.sqrt(msBetween) / withinSD;
    }

    public static double cohensH(double p1, double p2) {
        return Math.abs(2.0 * Math.asin(Math.sqrt(p1)) - 2.0 * Math.asin(Math.sqrt(p2)));
    }

    // -- Two-Sample t-Test Power and Sample Size --

    public static PowerResult powerTTest(double d, int n, double alpha) {
        double df = 2.0 * n - 2.0;
        double ncp = d * Math.sqrt(n / 2.0);
        double tCrit = studentTQuantile(1.0 - alpha / 2.0, df);
        double power = 1.0 - nonCentralTCdf(tCrit, df, ncp) + nonCentralTCdf(-tCrit, df, ncp);
        return new PowerResult(d, n, alpha, power);
    }

    public static int sampleSizeTTest(double d, double power, double alpha) {
        double zAlpha = normalQuantile(1.0 - alpha / 2.0);
        double zBeta = normalQuantile(power);
        return (int) Math.ceil(2.0 * Math.pow(zAlpha + zBeta, 2) / (d * d));
    }

    // -- Two-Proportion z-Test Power and Sample Size --

    public static PowerResult powerProportionTest(double p1, double p2, int n, double alpha) {
        double h = cohensH(p1, p2);
        double zAlpha = normalQuantile(1.0 - alpha / 2.0);
        double zBeta = h * Math.sqrt(n / 2.0) - zAlpha;
        double power = normalCDF(zBeta);
        return new PowerResult(h, n, alpha, power);
    }

    public static int sampleSizeProportionTest(double p1, double p2, double power, double alpha) {
        double h = cohensH(p1, p2);
        double zAlpha = normalQuantile(1.0 - alpha / 2.0);
        double zBeta = normalQuantile(power);
        return (int) Math.ceil(2.0 * Math.pow(zAlpha + zBeta, 2) / (h * h));
    }

    // -- ANOVA Power --

    public static PowerResult powerAnova(double f, int k, int nPerGroup, double alpha) {
        int df1 = k - 1;
        int df2 = k * (nPerGroup - 1);
        double ncp = f * f * k * nPerGroup;
        double fCrit = fQuantile(1.0 - alpha, df1, df2);
        double power = 1.0 - nonCentralFCdf(fCrit, df1, df2, ncp);
        return new PowerResult(f, k * nPerGroup, alpha, power);
    }

    // -- Equivalence Testing (TOST) --

    public static boolean tostEquivalence(double mean1, double mean2, double sd, int n1, int n2, double margin, double alpha) {
        double se = sd * Math.sqrt(1.0 / n1 + 1.0 / n2);
        double t1 = (mean1 - mean2 + margin) / se;
        double t2 = (mean1 - mean2 - margin) / se;
        double df = n1 + n2 - 2;
        double p1 = 1.0 - studentTCdf(t1, df);
        double p2 = studentTCdf(t2, df);
        return Math.max(p1, p2) < alpha;
    }

    // -- Power Curves --

    public record CurvePoint(int n, double power) {}

    public static List<CurvePoint> powerCurveTTest(double d, double alpha, int maxN, int step) {
        List<CurvePoint> points = new ArrayList<>();
        for (int n = step; n <= maxN; n += step) {
            points.add(new CurvePoint(n, powerTTest(d, n, alpha).power()));
        }
        return points;
    }

    // -- Internal: Distribution functions --

    private static double nonCentralTCdf(double t, double df, double ncp) {
        // AS 243 approximation by Lenth
        double[] w = {0.0055657196642445571, 0.012915947284065419, 0.020181515297735382,
                      0.027298621498568734, 0.034213810770299537, 0.040875750923643261,
                      0.047235083490265582, 0.053244713860759962, 0.058860144245324798,
                      0.064039797355015492, 0.068745323835736638, 0.072941885005653087,
                      0.076598410645870678, 0.079687828912071602, 0.082187266011339763,
                      0.084078218979661945, 0.085346685739338627, 0.085983275670394821};
        double[] x = {0.044489365833267018, 0.12293832291667808, 0.24004711246024697,
                      0.36592037646623693, 0.49779325136153839, 0.63286801811867705,
                      0.76876580765185254, 0.90334340252563218, 1.0346010812318106,
                      1.1607428260992599, 1.2801261110710194, 1.3912142130984963,
                      1.4926202269622091, 1.5830906767348207, 1.6614220458467077,
                      1.7266506543584874, 1.7778972050106661, 1.8144000000000000};
        double integral = 0;
        for (int i = 0; i < 18; i++) {
            double u = (t * x[i] + df) / (t + x[i] * Math.sqrt(df));
            integral += w[i] * studentTPdf(u, df);
        }
        return normalCDF(ncp - t) + integral;
    }

    private static double nonCentralFCdf(double f, int df1, int df2, double ncp) {
        // Simplified: Patnaik's two-moment central F approximation
        double k = (df1 + 2 * ncp) / (df1 + ncp);
        double v = (df1 + ncp) * (df1 + ncp) / (df1 + 2 * ncp);
        double fAdj = f / k;
        return fCdf(fAdj, (int) v, df2);
    }

    private static double studentTPdf(double t, double df) {
        return Math.exp(logGamma((df + 1) / 2) - logGamma(df / 2))
            * Math.pow(1 + t * t / df, -(df + 1) / 2) / Math.sqrt(df * Math.PI);
    }

    private static double fCdf(double f, int df1, int df2) {
        double x = df1 * f / (df1 * f + df2);
        return incompleteBeta(df1 / 2.0, df2 / 2.0, x);
    }

    private static double fQuantile(double p, int df1, int df2) {
        // Simple Newton search
        double lo = 0, hi = 10000;
        for (int iter = 0; iter < 50; iter++) {
            double mid = (lo + hi) / 2;
            double cdf = fCdf(mid, df1, df2);
            if (cdf < p) lo = mid;
            else hi = mid;
        }
        return (lo + hi) / 2;
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
        if (p < 1e-15) return -8.0;
        if (p > 1 - 1e-15) return 8.0;
        double t = Math.sqrt(-2.0 * Math.log(1.0 - p));
        return t - (2.515517 + 0.802853 * t + 0.010328 * t * t)
            / (1.0 + 1.432788 * t + 0.189269 * t * t + 0.001308 * t * t * t);
    }

    private static double studentTCdf(double t, double df) {
        double x = df / (df + t * t);
        return 1.0 - 0.5 * incompleteBeta(df / 2.0, 0.5, x);
    }

    private static double studentTQuantile(double p, double df) {
        if (df > 100) return normalQuantile(p);
        double t = normalQuantile(p);
        double t2 = t * t;
        double g = (t2 + 1) * (t2 + 3) / (4 * df);
        return t + (t2 + t) / (2 * df) + g / df;
    }

    private static double incompleteBeta(double a, double b, double x) {
        if (x < 0 || x > 1) return 0;
        if (x == 0 || x == 1) return x;
        double bt = Math.exp(logGamma(a + b) - logGamma(a) - logGamma(b)
            + a * Math.log(x) + b * Math.log(1.0 - x));
        if (x < (a + 1) / (a + b + 2)) return bt * betaCF(a, b, x) / a;
        return 1.0 - bt * betaCF(b, a, 1.0 - x) / b;
    }

    private static double betaCF(double a, double b, double x) {
        int maxIter = 200; double eps = 3e-12;
        double qab = a + b, qap = a + 1.0, qam = a - 1.0;
        double c = 1.0, d = 1.0 - qab * x / qap;
        if (Math.abs(d) < 1e-30) d = 1e-30;
        d = 1.0 / d; double h = d;
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
        double y = x, tmp = x + 5.5;
        tmp -= (x + 0.5) * Math.log(tmp);
        double ser = 1.000000000190015;
        for (int j = 0; j < 6; j++) ser += cof[j] / ++y;
        return -tmp + Math.log(2.5066282746310005 * ser / x);
    }
}

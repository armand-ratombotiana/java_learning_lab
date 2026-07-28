package com.datascience.deep.lab01;

import java.util.*;
import java.util.stream.IntStream;

public final class ABTesting {

    public record ABTestResult(
        double zScore, double pValue, double ciLower, double ciUpper,
        double lift, boolean significant, long requiredSampleSize
    ) {
        public ABTestResult {
            if (Double.isNaN(zScore) || Double.isNaN(pValue) || Double.isNaN(lift)) {
                throw new IllegalArgumentException("Invalid test statistics");
            }
        }
    }

    public sealed interface Metric permits BinaryMetric, ContinuousMetric {
        ABTestResult analyze(double[] control, double[] treatment, double alpha);
    }

    public record BinaryMetric(int controlConversions, int controlTotal,
                               int treatmentConversions, int treatmentTotal) implements Metric {
        @Override
        public ABTestResult analyze(double[] control, double[] treatment, double alpha) {
            double nA = controlTotal, nB = treatmentTotal;
            double pA = (double) controlConversions / nA;
            double pB = (double) treatmentConversions / nB;
            double pPooled = (double) (controlConversions + treatmentConversions) / (nA + nB);
            double se = Math.sqrt(pPooled * (1 - pPooled) * (1.0 / nA + 1.0 / nB));
            double z = (pB - pA) / se;
            double pValue = 2.0 * (1.0 - normalCDF(Math.abs(z)));
            double me = normalQuantile(1.0 - alpha / 2.0) * se;
            long sampleSize = requiredSampleSize(pA, 0.05, alpha, 0.8);
            return new ABTestResult(z, pValue, (pB - pA) - me, (pB - pA) + me,
                                    (pB - pA) / pA, pValue < alpha, sampleSize);
        }
    }

    public record ContinuousMetric() implements Metric {
        @Override
        public ABTestResult analyze(double[] control, double[] treatment, double alpha) {
            double nA = control.length, nB = treatment.length;
            double meanA = mean(control), meanB = mean(treatment);
            double varA = variance(control, meanA);
            double varB = variance(treatment, meanB);
            double se = Math.sqrt(varA / nA + varB / nB);
            double t = (meanB - meanA) / se;
            double df = Math.pow(varA / nA + varB / nB, 2)
                / (Math.pow(varA / nA, 2) / (nA - 1) + Math.pow(varB / nB, 2) / (nB - 1));
            double pValue = 2.0 * (1.0 - studentTCdf(Math.abs(t), df));
            double me = studentTQuantile(1.0 - alpha / 2.0, df) * se;
            long sampleSize = requiredSampleSizeContinuous(meanA, meanB - meanA, variance(control, meanA), alpha, 0.8);
            return new ABTestResult(t, pValue, (meanB - meanA) - me, (meanB - meanA) + me,
                                    (meanB - meanA) / meanA, pValue < alpha, sampleSize);
        }
    }

    public sealed interface Correction permits BonferroniCorrection, BenjaminiHochberg {
        double[] adjust(double[] pValues);
    }

    public record BonferroniCorrection() implements Correction {
        @Override
        public double[] adjust(double[] pValues) {
            int m = pValues.length;
            return Arrays.stream(pValues).map(p -> Math.min(1.0, p * m)).toArray();
        }
    }

    public record BenjaminiHochberg() implements Correction {
        @Override
        public double[] adjust(double[] pValues) {
            int m = pValues.length;
            var indexed = IntStream.range(0, m)
                .mapToObj(i -> new double[]{pValues[i], i})
                .sorted(Comparator.comparingDouble(a -> a[0]))
                .toArray(double[][]::new);
            double[] adjusted = new double[m];
            double cumulative = 1.0;
            for (int i = m - 1; i >= 0; i--) {
                double rank = i + 1;
                double q = indexed[i][0] * m / rank;
                cumulative = Math.min(cumulative, q);
                adjusted[(int) indexed[i][1]] = cumulative;
            }
            return adjusted;
        }
    }

    public static long requiredSampleSize(double baseline, double mde, double alpha, double power) {
        double zAlpha = normalQuantile(1.0 - alpha / 2.0);
        double zBeta = normalQuantile(power);
        double pAvg = baseline + mde / 2.0;
        return (long) Math.ceil(
            2.0 * pAvg * (1.0 - pAvg) * Math.pow(zAlpha + zBeta, 2) / (mde * mde)
        );
    }

    public static long requiredSampleSizeContinuous(double meanC, double effect, double variance, double alpha, double power) {
        double zAlpha = normalQuantile(1.0 - alpha / 2.0);
        double zBeta = normalQuantile(power);
        return (long) Math.ceil(
            2.0 * variance * Math.pow(zAlpha + zBeta, 2) / (effect * effect)
        );
    }

    public static double cupedAdjustment(double[] preA, double[] postA, double[] preB, double[] postB) {
        double covA = covariance(preA, postA);
        double varA = variance(preA, mean(preA));
        double covB = covariance(preB, postB);
        double varB = variance(preB, mean(preB));
        double theta = (covA / varA + covB / varB) / 2.0;
        double adjMeanB = mean(postB) - theta * (mean(preB) - mean(preA));
        return adjMeanB - mean(postA);
    }

    public static class SequentialTest {
        private final double alpha;
        private final List<Double> pValueHistory = new ArrayList<>();

        public SequentialTest(double alpha) {
            this.alpha = alpha;
        }

        public double update(double[] controlBatch, double[] treatmentBatch) {
            double zSum = 0.0, infoSum = 0.0;
            int m = Math.min(controlBatch.length, treatmentBatch.length);
            for (int i = 0; i < m; i++) {
                zSum += treatmentBatch[i] - controlBatch[i];
                infoSum += 1.0;
            }
            double z = zSum / Math.sqrt(infoSum);
            double p = Math.exp(-0.5 * z * z) / (0.5 * alpha);
            pValueHistory.add(Math.min(1.0, p));
            return p;
        }

        public boolean stopped() {
            if (pValueHistory.isEmpty()) return false;
            return pValueHistory.get(pValueHistory.size() - 1) < alpha;
        }

        public List<Double> getPValueHistory() {
            return Collections.unmodifiableList(pValueHistory);
        }
    }

    // -- Utility methods --

    private static double mean(double[] x) {
        return Arrays.stream(x).average().orElseThrow();
    }

    private static double sum(double[] x) {
        return Arrays.stream(x).sum();
    }

    private static double variance(double[] x, double mean) {
        return Arrays.stream(x).map(v -> Math.pow(v - mean, 2)).sum() / (x.length - 1);
    }

    private static double covariance(double[] x, double[] y) {
        double mx = mean(x), my = mean(y);
        double cov = 0.0;
        for (int i = 0; i < x.length; i++) {
            cov += (x[i] - mx) * (y[i] - my);
        }
        return cov / (x.length - 1);
    }

    private static double variance(double[] x) {
        double m = mean(x);
        return variance(x, m);
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
        if (x < (a + 1) / (a + b + 2)) {
            return bt * betaCF(a, b, x) / a;
        }
        return 1.0 - bt * betaCF(b, a, 1.0 - x) / b;
    }

    private static double betaCF(double a, double b, double x) {
        int maxIter = 200;
        double eps = 3e-12;
        double qab = a + b, qap = a + 1.0, qam = a - 1.0;
        double c = 1.0, d = 1.0 - qab * x / qap;
        if (Math.abs(d) < 1e-30) d = 1e-30;
        d = 1.0 / d;
        double h = d;
        for (int m = 1; m <= maxIter; m++) {
            int m2 = 2 * m;
            double aa = m * (b - m) * x / ((qam + m2) * (a + m2));
            d = 1.0 + aa * d;
            if (Math.abs(d) < 1e-30) d = 1e-30;
            c = 1.0 + aa / c;
            if (Math.abs(c) < 1e-30) c = 1e-30;
            d = 1.0 / d;
            h *= d * c;
            aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
            d = 1.0 + aa * d;
            if (Math.abs(d) < 1e-30) d = 1e-30;
            c = 1.0 + aa / c;
            if (Math.abs(c) < 1e-30) c = 1e-30;
            d = 1.0 / d;
            double del = d * c;
            h *= del;
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

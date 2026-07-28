package com.datascience.deep.lab02;

import java.util.*;
import java.util.function.DoubleUnaryOperator;

public final class CausalInference {

    public record PropensityScore(double[] scores, double[] weights) {}

    public static PropensityScore estimatePropensity(double[][] features, boolean[] treated) {
        int n = features.length, p = features[0].length;
        double[] beta = new double[p];
        double lr = 0.01;
        for (int iter = 0; iter < 500; iter++) {
            double[] gradient = new double[p];
            for (int i = 0; i < n; i++) {
                double linear = dot(beta, features[i]);
                double pred = sigmoid(linear);
                double error = (treated[i] ? 1.0 : 0.0) - pred;
                for (int j = 0; j < p; j++) gradient[j] += error * features[i][j];
            }
            double reg = 0.001;
            for (int j = 0; j < p; j++) {
                beta[j] += lr * (gradient[j] - reg * beta[j]);
            }
        }
        double[] scores = new double[n];
        double[] weights = new double[n];
        for (int i = 0; i < n; i++) {
            scores[i] = sigmoid(dot(beta, features[i]));
            weights[i] = treated[i] ? 1.0 / Math.min(scores[i], 0.999)
                                    : 1.0 / Math.min(1.0 - scores[i], 0.999);
        }
        return new PropensityScore(scores, weights);
    }

    public static double[] stabilizeWeights(double[] weights, boolean[] treated) {
        double nT = 0, nC = 0;
        for (boolean t : treated) { if (t) nT++; else nC++; }
        double pT = nT / (nT + nC);
        double[] stabilized = new double[weights.length];
        for (int i = 0; i < weights.length; i++) {
            stabilized[i] = treated[i] ? pT * weights[i] : (1 - pT) * weights[i];
        }
        return stabilized;
    }

    public static double[] stdDiff(double[][] features, boolean[] treated, int[] treatedIdx, int[] controlIdx) {
        int p = features[0].length;
        double[] smd = new double[p];
        for (int j = 0; j < p; j++) {
            double mT = 0, mC = 0;
            for (int i : treatedIdx) mT += features[i][j];
            for (int i : controlIdx) mC += features[i][j];
            mT /= treatedIdx.length; mC /= controlIdx.length;
            double vT = 0, vC = 0;
            for (int i : treatedIdx) vT += Math.pow(features[i][j] - mT, 2);
            for (int i : controlIdx) vC += Math.pow(features[i][j] - mC, 2);
            vT /= (treatedIdx.length - 1); vC /= (controlIdx.length - 1);
            smd[j] = (mT - mC) / Math.sqrt((vT + vC) / 2.0);
        }
        return smd;
    }

    public record DiDResult(double att, double se, double ciLower, double ciUpper, double pValue) {}

    public static DiDResult differenceInDifferences(
            double[] yPreT, double[] yPostT, double[] yPreC, double[] yPostC, double alpha) {
        double dT = mean(yPostT) - mean(yPreT);
        double dC = mean(yPostC) - mean(yPreC);
        double att = dT - dC;
        double se = Math.sqrt(variance(yPostT) / yPostT.length + variance(yPreT) / yPreT.length
                            + variance(yPostC) / yPostC.length + variance(yPreC) / yPreC.length);
        double z = att / se;
        double p = 2.0 * (1.0 - normalCDF(Math.abs(z)));
        double me = normalQuantile(1.0 - alpha / 2.0) * se;
        return new DiDResult(att, se, att - me, att + me, p);
    }

    public record IVResult(double beta, double se, double ciLower, double ciUpper, double pValue) {}

    public static IVResult twoStageLeastSquares(double[] instrument, double[] treatment, double[] outcome, double alpha) {
        int n = instrument.length;
        double pi1 = covariance(instrument, treatment) / variance(instrument);
        double pi0 = mean(treatment) - pi1 * mean(instrument);
        double[] tHat = new double[n];
        for (int i = 0; i < n; i++) tHat[i] = pi0 + pi1 * instrument[i];
        double betaIV = covariance(tHat, outcome) / variance(tHat);
        double beta0 = mean(outcome) - betaIV * mean(tHat);
        double[] residuals = new double[n];
        for (int i = 0; i < n; i++) residuals[i] = outcome[i] - beta0 - betaIV * tHat[i];
        double mse = sumSq(residuals) / (n - 2);
        double varTHat = variance(tHat);
        double seBeta = Math.sqrt(mse / (n * varTHat));
        double df = n - 2;
        double t = betaIV / seBeta;
        double p = 2.0 * (1.0 - studentTCdf(Math.abs(t), df));
        double me = studentTQuantile(1.0 - alpha / 2.0, df) * seBeta;
        return new IVResult(betaIV, seBeta, betaIV - me, betaIV + me, p);
    }

    public record RosenbaumBounds(double gamma, double pValueUpper, double pValueLower) {}

    public static RosenbaumBounds rosenbaumTest(double[] matchedDiff, double gamma) {
        int pairs = matchedDiff.length;
        double sumPi = 0, sumVar = 0;
        for (double diff : matchedDiff) {
            double pi = diff > 0 ? 1.0 / (1.0 + 1.0 / gamma) : 1.0 / (1.0 + gamma);
            sumPi += pi;
            sumVar += pi * (1 - pi);
        }
        double z = (sumPi - pairs / 2.0) / Math.sqrt(sumVar);
        return new RosenbaumBounds(gamma, 1.0 - normalCDF(z), normalCDF(z));
    }

    // -- Utility methods --

    private static double dot(double[] a, double[] b) {
        double s = 0;
        for (int i = 0; i < a.length; i++) s += a[i] * b[i];
        return s;
    }

    private static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-Math.max(-36, Math.min(36, x))));
    }

    private static double mean(double[] x) {
        return Arrays.stream(x).average().orElseThrow();
    }

    private static double variance(double[] x) {
        double m = mean(x);
        return Arrays.stream(x).map(v -> Math.pow(v - m, 2)).sum() / (x.length - 1);
    }

    private static double covariance(double[] x, double[] y) {
        double mx = mean(x), my = mean(y);
        double cov = 0;
        for (int i = 0; i < x.length; i++) cov += (x[i] - mx) * (y[i] - my);
        return cov / (x.length - 1);
    }

    private static double sumSq(double[] x) {
        return Arrays.stream(x).map(v -> v * v).sum();
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

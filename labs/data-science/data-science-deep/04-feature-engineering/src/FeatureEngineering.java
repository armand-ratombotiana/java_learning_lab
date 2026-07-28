package com.datascience.deep.lab04;

import java.util.*;

public final class FeatureEngineering {

    // -- Polynomial Features --

    public static double[][] polynomialFeatures(double[][] X, int degree, boolean interaction, boolean bias) {
        int n = X.length, p = X[0].length;
        List<int[]> combos = new ArrayList<>();
        if (bias) combos.add(new int[p]);
        for (int d = 1; d <= degree; d++) generateCombos(combos, new int[p], 0, d, p);
        if (interaction) {
            for (int j1 = 0; j1 < p; j1++) {
                for (int j2 = j1 + 1; j2 < p; j2++) {
                    int[] powers = new int[p];
                    powers[j1] = 1; powers[j2] = 1;
                    combos.add(powers);
                }
            }
        }
        double[][] result = new double[n][combos.size()];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < combos.size(); j++) {
                double val = 1.0;
                int[] c = combos.get(j);
                for (int k = 0; k < p; k++) val *= Math.pow(X[i][k], c[k]);
                result[i][j] = val;
            }
        }
        return result;
    }

    private static void generateCombos(List<int[]> acc, int[] current, int pos, int remaining, int p) {
        if (pos == p - 1) {
            current[pos] = remaining;
            acc.add(current.clone());
            current[pos] = 0;
            return;
        }
        for (int v = 0; v <= remaining; v++) {
            current[pos] = v;
            generateCombos(acc, current, pos + 1, remaining - v, p);
        }
    }

    // -- Binning --

    public sealed interface Binning permits EqualWidth, EqualFrequency {
        int[] fitTransform(double[] values, int bins);
    }

    public record EqualWidth() implements Binning {
        @Override
        public int[] fitTransform(double[] values, int bins) {
            double min = Arrays.stream(values).min().orElseThrow();
            double max = Arrays.stream(values).max().orElseThrow();
            double width = (max - min) / bins;
            if (width == 0) return new int[values.length];
            int[] labels = new int[values.length];
            for (int i = 0; i < values.length; i++) {
                labels[i] = Math.min(bins - 1, (int) ((values[i] - min) / width));
            }
            return labels;
        }
    }

    public record EqualFrequency() implements Binning {
        @Override
        public int[] fitTransform(double[] values, int bins) {
            int n = values.length;
            double[] sorted = Arrays.copyOf(values, n);
            Arrays.sort(sorted);
            int[] labels = new int[n];
            for (int i = 0; i < n; i++) {
                int rank = 0;
                while (rank < n && values[i] > sorted[rank]) rank++;
                labels[i] = Math.min(bins - 1, rank * bins / n);
            }
            return labels;
        }
    }

    // -- Target Encoding --

    public static double[] targetEncode(double[] categories, double[] target, double smooth) {
        int n = categories.length;
        Map<Double, double[]> stats = new HashMap<>();
        for (int i = 0; i < n; i++) {
            stats.computeIfAbsent(categories[i], k -> new double[2]);
            stats.get(categories[i])[0]++;
            stats.get(categories[i])[1] += target[i];
        }
        double globalMean = Arrays.stream(target).average().orElseThrow();
        double[] encoded = new double[n];
        for (int i = 0; i < n; i++) {
            double[] s = stats.get(categories[i]);
            encoded[i] = (s[1] + smooth * globalMean) / (s[0] + smooth);
        }
        return encoded;
    }

    // -- Feature Selection --

    public static boolean[] varianceThreshold(double[][] X, double threshold) {
        int p = X[0].length;
        boolean[] keep = new boolean[p];
        for (int j = 0; j < p; j++) {
            int n = X.length;
            double sum = 0;
            for (double[] row : X) sum += row[j];
            double mean = sum / n;
            double var = 0;
            for (double[] row : X) var += Math.pow(row[j] - mean, 2);
            var /= (n - 1);
            keep[j] = var >= threshold;
        }
        return keep;
    }

    public static boolean[] correlationFilter(double[][] X, double threshold) {
        int p = X[0].length;
        boolean[] keep = new boolean[p];
        Arrays.fill(keep, true);
        for (int i = 0; i < p; i++) {
            if (!keep[i]) continue;
            for (int j = i + 1; j < p; j++) {
                if (Math.abs(pearson(getCol(X, i), getCol(X, j))) > threshold) keep[j] = false;
            }
        }
        return keep;
    }

    public static double mutualInformation(double[] x, double[] y, int bins) {
        double[] xd = discretize(x, bins);
        double[] yd = discretize(y, bins);
        double[][] joint = new double[bins][bins];
        double[] mx = new double[bins], my = new double[bins];
        int n = xd.length;
        for (int i = 0; i < n; i++) {
            int xi = (int) xd[i], yi = (int) yd[i];
            joint[xi][yi]++; mx[xi]++; my[yi]++;
        }
        double mi = 0;
        for (int i = 0; i < bins; i++) {
            for (int j = 0; j < bins; j++) {
                if (joint[i][j] > 0) {
                    mi += (joint[i][j] / n) * Math.log((joint[i][j] * n) / (mx[i] * my[j]));
                }
            }
        }
        return mi;
    }

    // -- Utilities --

    private static double[] discretize(double[] x, int bins) {
        double min = Arrays.stream(x).min().orElseThrow();
        double max = Arrays.stream(x).max().orElseThrow();
        double[] d = new double[x.length];
        if (max == min) return d;
        for (int i = 0; i < x.length; i++) {
            int v = (int) ((x[i] - min) / (max - min) * bins);
            d[i] = Math.min(bins - 1, v);
        }
        return d;
    }

    private static double pearson(double[] a, double[] b) {
        int n = a.length;
        double ma = Arrays.stream(a).average().orElseThrow();
        double mb = Arrays.stream(b).average().orElseThrow();
        double cov = 0, va = 0, vb = 0;
        for (int i = 0; i < n; i++) {
            cov += (a[i] - ma) * (b[i] - mb);
            va += Math.pow(a[i] - ma, 2);
            vb += Math.pow(b[i] - mb, 2);
        }
        if (va == 0 || vb == 0) return 0;
        return cov / Math.sqrt(va * vb);
    }

    private static double[] getCol(double[][] X, int j) {
        double[] col = new double[X.length];
        for (int i = 0; i < X.length; i++) col[i] = X[i][j];
        return col;
    }
}

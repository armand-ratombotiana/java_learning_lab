package com.ml.lab06;

import java.util.*;

/**
 * Gaussian Naive Bayes — classification for continuous features.
 * <p>
 * Computes per-class mean and variance for each feature,
 * then predicts via Bayes rule with the Gaussian PDF.
 */
public class Main {

    static class GaussianNB {
        private final Map<Integer, Double> priors = new HashMap<>();
        private final Map<Integer, double[]> means = new HashMap<>();
        private final Map<Integer, double[]> vars = new HashMap<>();
        private final Set<Integer> classes = new HashSet<>();

        // ──────────────────────────────────────────
        // Fit
        // ──────────────────────────────────────────

        public void fit(double[][] X, int[] y) {
            int n = X.length, m = X[0].length;
            Map<Integer, List<double[]>> byClass = new HashMap<>();
            for (int i = 0; i < n; i++) {
                byClass.computeIfAbsent(y[i], k -> new ArrayList<>()).add(X[i]);
                classes.add(y[i]);
            }
            for (int c : classes) {
                List<double[]> samples = byClass.get(c);
                priors.put(c, (double) samples.size() / n);
                double[] mu = new double[m];
                double[] var = new double[m];
                for (double[] s : samples) for (int j = 0; j < m; j++) mu[j] += s[j];
                for (int j = 0; j < m; j++) mu[j] /= samples.size();
                for (double[] s : samples)
                    for (int j = 0; j < m; j++)
                        var[j] += (s[j] - mu[j]) * (s[j] - mu[j]);
                for (int j = 0; j < m; j++) var[j] /= Math.max(1, samples.size() - 1);
                means.put(c, mu);
                vars.put(c, var);
            }
        }

        // ──────────────────────────────────────────
        // Gaussian PDF
        // ──────────────────────────────────────────

        private double gaussianPdf(double x, double mean, double var) {
            double eps = 1e-9;
            return Math.exp(-(x - mean) * (x - mean) / (2 * var + eps))
                    / Math.sqrt(2 * Math.PI * var + eps);
        }

        // ──────────────────────────────────────────
        // Predict
        // ──────────────────────────────────────────

        public int predict(double[] x) {
            int bestClass = -1;
            double bestScore = Double.NEGATIVE_INFINITY;
            for (int c : classes) {
                double score = Math.log(priors.get(c));
                double[] mu = means.get(c);
                double[] v = vars.get(c);
                for (int j = 0; j < x.length; j++) {
                    score += Math.log(gaussianPdf(x[j], mu[j], v[j]) + 1e-12);
                }
                if (score > bestScore) {
                    bestScore = score;
                    bestClass = c;
                }
            }
            return bestClass;
        }

        // ──────────────────────────────────────────
        // Accuracy
        // ──────────────────────────────────────────

        public double accuracy(double[][] X, int[] y) {
            int ok = 0;
            for (int i = 0; i < X.length; i++) {
                if (predict(X[i]) == y[i]) ok++;
            }
            return (double) ok / X.length;
        }
    }

    // ──────────────────────────────────────────────
    // Main — test cases
    // ──────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("=== Naive Bayes Lab ===");

        // Iris-like synthetic data (sepal length, sepal width)
        double[][] X = {
            {5.1, 3.5}, {4.9, 3.0}, {5.4, 3.9}, {5.0, 3.6}, {5.8, 4.0},
            {7.0, 3.2}, {6.4, 3.2}, {6.9, 3.1}, {6.5, 2.8}, {7.2, 3.6},
            {6.3, 2.8}, {5.7, 2.6}, {5.9, 3.0}, {6.2, 2.2}, {6.1, 2.9}
        };
        int[] y = {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2};

        GaussianNB nb = new GaussianNB();
        nb.fit(X, y);

        System.out.printf("Training accuracy = %.2f%n", nb.accuracy(X, y));

        double[][] test = {{5.0, 3.4}, {6.5, 3.0}, {5.8, 2.7}};
        int[] expected = {0, 1, 2};
        for (int i = 0; i < test.length; i++) {
            int pred = nb.predict(test[i]);
            System.out.printf("Test %d → predicted=%d expected=%d %s%n",
                    i, pred, expected[i], pred == expected[i] ? "✓" : "✗");
        }
    }
}

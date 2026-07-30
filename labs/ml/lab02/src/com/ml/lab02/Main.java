package com.ml.lab02;

/**
 * Logistic Regression — binary classification via sigmoid and cross-entropy.
 * <p>
 * Uses gradient descent to find optimal weights; evaluates accuracy
 * and decision boundary on synthetic 2D data.
 */
public class Main {

    // ──────────────────────────────────────────────
    // Sigmoid
    // ──────────────────────────────────────────────

    public static double sigmoid(double z) {
        return 1.0 / (1.0 + Math.exp(-z));
    }

    // ──────────────────────────────────────────────
    // Predict probability
    // ──────────────────────────────────────────────

    public static double predictProb(double[] weights, double[] features) {
        double z = 0;
        for (int i = 0; i < weights.length; i++) {
            z += weights[i] * features[i];
        }
        return sigmoid(z);
    }

    // ──────────────────────────────────────────────
    // Fit — gradient descent
    // ──────────────────────────────────────────────

    public static double[] fit(double[][] X, double[] y, double lr, int epochs) {
        int m = X.length, n = X[0].length;
        double[] w = new double[n];
        for (int ep = 0; ep < epochs; ep++) {
            double[] grad = new double[n];
            for (int i = 0; i < m; i++) {
                double pred = predictProb(w, X[i]);
                double err  = pred - y[i];
                for (int j = 0; j < n; j++) {
                    grad[j] += err * X[i][j];
                }
            }
            for (int j = 0; j < n; j++) {
                w[j] -= lr * grad[j] / m;
            }
        }
        return w;
    }

    // ──────────────────────────────────────────────
    // Predict class (0 or 1)
    // ──────────────────────────────────────────────

    public static int predictClass(double[] weights, double[] features) {
        return predictProb(weights, features) >= 0.5 ? 1 : 0;
    }

    // ──────────────────────────────────────────────
    // Accuracy
    // ──────────────────────────────────────────────

    public static double accuracy(double[][] X, double[] y, double[] w) {
        int correct = 0;
        for (int i = 0; i < X.length; i++) {
            if (predictClass(w, X[i]) == (int) y[i]) correct++;
        }
        return (double) correct / X.length;
    }

    // ──────────────────────────────────────────────
    // Main — test cases
    // ──────────────────────────────────────────────

    public static void main(String[] args) {
        // Synthetic 2D data: two clusters
        double[][] X = {
            {1, 2.0, 3.0}, {1, 1.0, 2.5}, {1, 2.5, 2.8}, {1, 3.0, 4.0},
            {1, 5.0, 6.0}, {1, 6.0, 5.5}, {1, 5.5, 7.0}, {1, 7.0, 6.5}
        };
        double[] y = {0, 0, 0, 0, 1, 1, 1, 1};

        System.out.println("=== Logistic Regression Lab ===");
        double[] w = fit(X, y, 0.1, 5000);
        System.out.print("Weights: ");
        for (double v : w) System.out.printf("%.4f ", v);
        System.out.println();

        double acc = accuracy(X, y, w);
        System.out.printf("Accuracy = %.2f%n", acc);

        // Predict a new point
        double[] test = {1, 4.0, 4.5};
        double prob = predictProb(w, test);
        int cls = predictClass(w, test);
        System.out.printf("Test point (4.0, 4.5) → prob=%.4f, class=%d%n", prob, cls);
    }
}

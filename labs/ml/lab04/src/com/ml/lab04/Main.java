package com.ml.lab04;

/**
 * Support Vector Machine — linear SVM via gradient descent on primal form.
 * <p>
 * Demonstrates hinge loss, margin maximization, and classification
 * on synthetic linearly separable 2D data.
 */
public class Main {

    // ──────────────────────────────────────────────
    // Fit linear SVM (primal, subgradient descent)
    // ──────────────────────────────────────────────

    /**
     * Minimizes  ½‖w‖² + C Σ max(0, 1 − yⁱ(w·xⁱ + b))
     * @param X training features (each row includes bias term 1)
     * @param y labels in {-1, 1}
     * @param C regularization strength
     * @param lr learning rate
     * @param epochs iterations
     * @return [w0, w1, ..., bias]
     */
    public static double[] fit(double[][] X, double[] y, double C, double lr, int epochs) {
        int m = X.length, n = X[0].length;
        double[] w = new double[n];
        for (int ep = 0; ep < epochs; ep++) {
            for (int i = 0; i < m; i++) {
                double dot = 0;
                for (int j = 0; j < n; j++) dot += w[j] * X[i][j];
                double margin = y[i] * dot;
                if (margin < 1) {
                    for (int j = 0; j < n; j++) {
                        w[j] -= lr * (w[j] - C * y[i] * X[i][j]);
                    }
                } else {
                    for (int j = 0; j < n; j++) {
                        w[j] -= lr * w[j];
                    }
                }
            }
        }
        return w;
    }

    // ──────────────────────────────────────────────
    // Predict
    // ──────────────────────────────────────────────

    public static int predict(double[] w, double[] x) {
        double dot = 0;
        for (int j = 0; j < w.length; j++) dot += w[j] * x[j];
        return dot >= 0 ? 1 : -1;
    }

    // ──────────────────────────────────────────────
    // Accuracy
    // ──────────────────────────────────────────────

    public static double accuracy(double[][] X, double[] y, double[] w) {
        int ok = 0;
        for (int i = 0; i < X.length; i++) {
            if (predict(w, X[i]) == (int) y[i]) ok++;
        }
        return (double) ok / X.length;
    }

    // ──────────────────────────────────────────────
    // Main — test cases
    // ──────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("=== Support Vector Machine Lab ===");

        // Linearly separable data (bias term 1 prepended)
        double[][] X = {
            {1, 1.0, 2.0}, {1, 1.5, 1.8}, {1, 2.0, 1.0}, {1, 0.8, 2.5},
            {1, 5.0, 6.0}, {1, 6.0, 5.0}, {1, 5.5, 6.5}, {1, 4.8, 7.0}
        };
        double[] y = {-1, -1, -1, -1, 1, 1, 1, 1};

        double[] w = fit(X, y, 1.0, 0.01, 2000);
        System.out.print("Weights: ");
        for (double v : w) System.out.printf("%.4f ", v);
        System.out.println();

        double acc = accuracy(X, y, w);
        System.out.printf("Training accuracy = %.2f%n", acc);

        // Test new point
        double[] test1 = {1, 2.5, 2.0};
        double[] test2 = {1, 5.0, 5.0};
        System.out.printf("(2.5, 2.0) → %d%n", predict(w, test1));
        System.out.printf("(5.0, 5.0) → %d%n", predict(w, test2));
    }
}

package com.ml.lab09;

import java.util.*;

/**
 * Gradient Boosting — ensemble of decision stumps for binary classification.
 * <p>
 * Uses shallow trees (depth 1) as weak learners, fitting negative gradients
 * with a configurable learning rate.
 */
public class Main {

    static class Stump {
        int feature;
        double threshold;
        double leftVal, rightVal;

        void fit(double[][] X, double[] residuals, double[] weights) {
            int n = X.length, m = X[0].length;
            double bestLoss = Double.MAX_VALUE;
            for (int f = 0; f < m; f++) {
                double[] vals = new double[n];
                for (int i = 0; i < n; i++) vals[i] = X[i][f];
                Arrays.sort(vals);
                for (int t = 0; t < n - 1; t++) {
                    double thresh = (vals[t] + vals[t + 1]) / 2;
                    double lSum = 0, lW = 0, rSum = 0, rW = 0;
                    for (int i = 0; i < n; i++) {
                        if (X[i][f] <= thresh) {
                            lSum += residuals[i] * weights[i];
                            lW   += weights[i];
                        } else {
                            rSum += residuals[i] * weights[i];
                            rW   += weights[i];
                        }
                    }
                    if (lW == 0 || rW == 0) continue;
                    double lVal = lSum / lW;
                    double rVal = rSum / rW;
                    double loss = 0;
                    for (int i = 0; i < n; i++) {
                        double err = residuals[i] - (X[i][f] <= thresh ? lVal : rVal);
                        loss += weights[i] * err * err;
                    }
                    if (loss < bestLoss) {
                        bestLoss = loss;
                        this.feature = f;
                        this.threshold = thresh;
                        this.leftVal = lVal;
                        this.rightVal = rVal;
                    }
                }
            }
        }

        double predict(double[] x) {
            return x[feature] <= threshold ? leftVal : rightVal;
        }
    }

    // ──────────────────────────────────────────────
    // Gradient Boosting Classifier
    // ──────────────────────────────────────────────

    static class GBC {
        List<Stump> stumps = new ArrayList<>();
        double lr;
        int nEstimators;

        GBC(double lr, int nEstimators) {
            this.lr = lr;
            this.nEstimators = nEstimators;
        }

        void fit(double[][] X, double[] y) {
            int n = X.length;
            double[] rawPred = new double[n];
            for (int iter = 0; iter < nEstimators; iter++) {
                // Class 1 probability from raw prediction
                double[] prob = new double[n];
                for (int i = 0; i < n; i++) prob[i] = 1.0 / (1.0 + Math.exp(-rawPred[i]));

                // Negative gradient (residuals)
                double[] residuals = new double[n];
                double[] weights = new double[n];
                for (int i = 0; i < n; i++) {
                    residuals[i] = y[i] - prob[i];
                    weights[i] = prob[i] * (1 - prob[i]);
                }

                Stump stump = new Stump();
                stump.fit(X, residuals, weights);
                stumps.add(stump);

                // Update raw predictions
                for (int i = 0; i < n; i++) {
                    rawPred[i] += lr * stump.predict(X[i]);
                }
            }
        }

        int predict(double[] x) {
            double raw = 0;
            for (Stump s : stumps) raw += lr * s.predict(x);
            return 1.0 / (1.0 + Math.exp(-raw)) >= 0.5 ? 1 : 0;
        }
    }

    // ──────────────────────────────────────────────
    // Main — test cases
    // ──────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("=== Gradient Boosting Lab ===");

        double[][] X = {
            {1.0, 2.0}, {2.0, 1.0}, {1.5, 1.5},
            {5.0, 5.0}, {6.0, 5.5}, {5.5, 4.5}
        };
        double[] y = {0, 0, 0, 1, 1, 1};

        GBC gbc = new GBC(0.5, 50);
        gbc.fit(X, y);

        int correct = 0;
        for (int i = 0; i < X.length; i++) {
            int pred = gbc.predict(X[i]);
            if (pred == (int) y[i]) correct++;
            System.out.printf("True=%d Pred=%d%n", (int) y[i], pred);
        }
        System.out.printf("Accuracy = %d/%d = %.2f%n", correct, X.length, (double) correct / X.length);
    }
}

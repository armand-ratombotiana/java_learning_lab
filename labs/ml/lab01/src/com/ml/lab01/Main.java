package com.ml.lab01;

/**
 * Linear Regression — OLS, Gradient Descent, R², MSE, MAE.
 * <p>
 * Demonstrates both closed-form (OLS) and iterative (gradient descent)
 * solutions on synthetic univariate data.
 */
public class Main {

    // ──────────────────────────────────────────────
    // OLS: Closed-form solution  β = (XᵀX)⁻¹Xᵀy
    // ──────────────────────────────────────────────

    /**
     * Fits univariate linear regression using OLS.
     * @param x   predictor array
     * @param y   target array
     * @return    [slope, intercept]
     */
    public static double[] fitOLS(double[] x, double[] y) {
        int n = x.length;
        double sx = 0, sy = 0, sxx = 0, sxy = 0;
        for (int i = 0; i < n; i++) {
            sx  += x[i];
            sy  += y[i];
            sxx += x[i] * x[i];
            sxy += x[i] * y[i];
        }
        double slope     = (n * sxy - sx * sy) / (n * sxx - sx * sx);
        double intercept = (sy - slope * sx) / n;
        return new double[]{slope, intercept};
    }

    // ──────────────────────────────────────────────
    // Gradient Descent
    // ──────────────────────────────────────────────

    /**
     * Fits univariate linear regression using gradient descent.
     * @param x       predictor array
     * @param y       target array
     * @param lr      learning rate
     * @param epochs  number of iterations
     * @return        [slope, intercept]
     */
    public static double[] fitGD(double[] x, double[] y, double lr, int epochs) {
        int n = x.length;
        double m = 0.0, b = 0.0;
        for (int ep = 0; ep < epochs; ep++) {
            double dm = 0.0, db = 0.0;
            for (int i = 0; i < n; i++) {
                double pred = m * x[i] + b;
                double err  = pred - y[i];
                dm += err * x[i];
                db += err;
            }
            m -= lr * dm / n;
            b -= lr * db / n;
        }
        return new double[]{m, b};
    }

    // ──────────────────────────────────────────────
    // Predict
    // ──────────────────────────────────────────────

    public static double predict(double slope, double intercept, double x) {
        return slope * x + intercept;
    }

    // ──────────────────────────────────────────────
    // Metrics
    // ──────────────────────────────────────────────

    public static double mse(double[] y, double[] yHat) {
        double s = 0;
        for (int i = 0; i < y.length; i++) {
            double d = y[i] - yHat[i];
            s += d * d;
        }
        return s / y.length;
    }

    public static double mae(double[] y, double[] yHat) {
        double s = 0;
        for (int i = 0; i < y.length; i++) {
            s += Math.abs(y[i] - yHat[i]);
        }
        return s / y.length;
    }

    public static double r2(double[] y, double[] yHat) {
        double yBar = 0;
        for (double v : y) yBar += v;
        yBar /= y.length;
        double ssRes = 0, ssTot = 0;
        for (int i = 0; i < y.length; i++) {
            ssRes += (y[i] - yHat[i]) * (y[i] - yHat[i]);
            ssTot += (y[i] - yBar)    * (y[i] - yBar);
        }
        return 1 - ssRes / ssTot;
    }

    // ──────────────────────────────────────────────
    // Main — test cases
    // ──────────────────────────────────────────────

    public static void main(String[] args) {
        // Synthetic data: y = 2.5x + 1.2 + noise
        double[] x = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] y = {3.7, 6.2, 8.9, 11.5, 14.0, 16.8, 19.1, 21.9, 24.3, 27.0};

        System.out.println("=== Linear Regression Lab ===");

        // OLS
        double[] ols = fitOLS(x, y);
        System.out.printf("OLS     → slope = %.4f, intercept = %.4f%n", ols[0], ols[1]);

        // Gradient Descent
        double[] gd = fitGD(x, y, 0.01, 1000);
        System.out.printf("GD      → slope = %.4f, intercept = %.4f%n", gd[0], gd[1]);

        // Predict
        double[] yHat = new double[y.length];
        double[] yHatGd = new double[y.length];
        for (int i = 0; i < y.length; i++) {
            yHat[i]   = predict(ols[0], ols[1], x[i]);
            yHatGd[i] = predict(gd[0], gd[1], x[i]);
        }

        System.out.printf("MSE(OLS)= %.4f  MAE(OLS)= %.4f  R²(OLS)= %.4f%n",
                mse(y, yHat), mae(y, yHat), r2(y, yHat));
        System.out.printf("MSE(GD) = %.4f  MAE(GD) = %.4f  R²(GD) = %.4f%n",
                mse(y, yHatGd), mae(y, yHatGd), r2(y, yHatGd));
    }
}

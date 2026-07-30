package com.statistics.lab05;

import java.util.Arrays;

/**
 * Computes Pearson and Spearman correlation coefficients, performs
 * ordinary least squares (OLS) simple and multiple linear regression,
 * and calculates R-squared and residuals.
 * <p>
 * For multiple regression, the normal equations are solved via
 * Gaussian elimination on the matrix X<sup>T</sup>X.
 */
public final class CorrelationAndRegression {

    private CorrelationAndRegression() {
    }

    /**
     * Returns the arithmetic mean of the given data.
     */
    public static double mean(double[] data) {
        double sum = 0;
        for (double v : data) {
            sum += v;
        }
        return sum / data.length;
    }

    /**
     * Computes the Pearson product-moment correlation coefficient r.
     *
     * @param x first variable values
     * @param y second variable values
     * @return Pearson r in [-1, 1]
     */
    public static double pearson(double[] x, double[] y) {
        double mx = mean(x);
        double my = mean(y);
        double sxy = 0, sxx = 0, syy = 0;
        for (int i = 0; i < x.length; i++) {
            double dx = x[i] - mx;
            double dy = y[i] - my;
            sxy += dx * dy;
            sxx += dx * dx;
            syy += dy * dy;
        }
        return sxy / Math.sqrt(sxx * syy);
    }

    /**
     * Assigns ranks to an array (averaging ties).
     *
     * @param values input array (not modified)
     * @return array of ranks (1-based)
     */
    public static double[] rank(double[] values) {
        int n = values.length;
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        double[] ranks = new double[n];
        for (int i = 0; i < n; i++) {
            double v = values[i];
            int first = 0, last = 0;
            for (int j = 0; j < n; j++) {
                if (sorted[j] == v) {
                    first = j;
                    break;
                }
            }
            for (int j = n - 1; j >= 0; j--) {
                if (sorted[j] == v) {
                    last = j;
                    break;
                }
            }
            ranks[i] = 1.0 + (first + last) / 2.0;
        }
        return ranks;
    }

    /**
     * Computes Spearman's rank correlation coefficient rho.
     *
     * @param x first variable values
     * @param y second variable values
     * @return Spearman rho in [-1, 1]
     */
    public static double spearman(double[] x, double[] y) {
        return pearson(rank(x), rank(y));
    }

    /**
     * Performs simple linear regression: y = slope * x + intercept.
     *
     * @param x predictor values
     * @param y response values
     * @return array [slope, intercept, rSquared]
     */
    public static double[] simpleRegression(double[] x, double[] y) {
        double mx = mean(x);
        double my = mean(y);
        double sxy = 0, sxx = 0;
        for (int i = 0; i < x.length; i++) {
            double dx = x[i] - mx;
            double dy = y[i] - my;
            sxy += dx * dy;
            sxx += dx * dx;
        }
        double slope = sxy / sxx;
        double intercept = my - slope * mx;

        double ssRes = 0, ssTot = 0;
        for (int i = 0; i < y.length; i++) {
            double pred = slope * x[i] + intercept;
            ssRes += (y[i] - pred) * (y[i] - pred);
            ssTot += (y[i] - my) * (y[i] - my);
        }
        double rSquared = 1.0 - ssRes / ssTot;
        return new double[]{slope, intercept, rSquared};
    }

    /**
     * Solves a linear system Ax = b via Gaussian elimination with partial pivoting.
     *
     * @param A square coefficient matrix
     * @param b right-hand side vector
     * @return solution vector x
     */
    public static double[] solveLinearSystem(double[][] A, double[] b) {
        int n = b.length;
        double[][] aug = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, aug[i], 0, n);
            aug[i][n] = b[i];
        }
        for (int col = 0; col < n; col++) {
            int pivot = col;
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(aug[row][col]) > Math.abs(aug[pivot][col])) {
                    pivot = row;
                }
            }
            double[] tmp = aug[col];
            aug[col] = aug[pivot];
            aug[pivot] = tmp;
            double pivVal = aug[col][col];
            for (int j = col; j <= n; j++) {
                aug[col][j] /= pivVal;
            }
            for (int row = 0; row < n; row++) {
                if (row != col) {
                    double factor = aug[row][col];
                    for (int j = col; j <= n; j++) {
                        aug[row][j] -= factor * aug[col][j];
                    }
                }
            }
        }
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = aug[i][n];
        }
        return x;
    }

    /**
     * Performs multiple linear regression: y = X * beta.
     * The first column of X should be all 1s for the intercept.
     *
     * @param X design matrix (n x p), each row is an observation
     * @param y response vector (n)
     * @return array of coefficients (beta), length p
     */
    public static double[] multipleRegression(double[][] X, double[] y) {
        int n = X.length;
        int p = X[0].length;
        double[][] XtX = new double[p][p];
        double[] Xty = new double[p];
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < p; j++) {
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += X[k][i] * X[k][j];
                }
                XtX[i][j] = sum;
            }
            double sum = 0;
            for (int k = 0; k < n; k++) {
                sum += X[k][i] * y[k];
            }
            Xty[i] = sum;
        }
        return solveLinearSystem(XtX, Xty);
    }

    /**
     * Computes residuals: observed - predicted.
     *
     * @param y observed values
     * @param predicted predicted values
     * @return array of residuals
     */
    public static double[] residuals(double[] y, double[] predicted) {
        double[] res = new double[y.length];
        for (int i = 0; i < y.length; i++) {
            res[i] = y[i] - predicted[i];
        }
        return res;
    }

    /**
     * Predicts values using linear regression coefficients.
     *
     * @param X design matrix
     * @param beta coefficients
     * @return predicted values
     */
    public static double[] predict(double[][] X, double[] beta) {
        double[] pred = new double[X.length];
        for (int i = 0; i < X.length; i++) {
            double sum = 0;
            for (int j = 0; j < beta.length; j++) {
                sum += X[i][j] * beta[j];
            }
            pred[i] = sum;
        }
        return pred;
    }

    /**
     * Runs test cases for correlation and regression.
     */
    public static void main(String[] args) {
        System.out.println("=== Pearson Correlation ===");
        double[] x1 = {1, 2, 3, 4, 5};
        double[] y1 = {2, 4, 6, 8, 10};
        System.out.printf("Perfect positive: r = %.6f%n", pearson(x1, y1));

        double[] y2 = {5, 4, 3, 2, 1};
        System.out.printf("Perfect negative: r = %.6f%n", pearson(x1, y2));

        double[] x2 = {1, 2, 3, 4, 5, 6, 7, 8};
        double[] y3 = {2.1, 4.0, 6.2, 7.9, 10.1, 11.8, 14.0, 16.2};
        System.out.printf("Near-perfect:     r = %.6f%n", pearson(x2, y3));

        System.out.println("\n=== Spearman Correlation ===");
        double[] x3 = {1, 2, 3, 4, 5, 6, 7};
        double[] y4 = {3, 1, 4, 2, 7, 5, 6};
        System.out.printf("Spearman rho:     rho = %.6f%n", spearman(x3, y4));

        double[] x4 = {10, 20, 30, 40, 50};
        double[] y5 = {100, 200, 300, 400, 500};
        System.out.printf("Spearman (linear): rho = %.6f%n", spearman(x4, y5));

        System.out.println("\n=== Simple Linear Regression ===");
        double[] b = simpleRegression(x1, y1);
        System.out.printf("Slope = %.4f, Intercept = %.4f, R² = %.6f%n", b[0], b[1], b[2]);

        double[] b2 = simpleRegression(x2, y3);
        System.out.printf("Slope = %.4f, Intercept = %.4f, R² = %.6f%n", b2[0], b2[1], b2[2]);

        System.out.println("\n=== Multiple Regression ===");
        // y = 1 + 2*x1 - 0.5*x2, with x1 and x2 non-collinear
        double[][] X = {
            {1, 1, 1},
            {1, 2, 3},
            {1, 3, 2},
            {1, 4, 5},
            {1, 5, 4}
        };
        double[] ym = {2.5, 3.5, 6.0, 6.5, 9.0}; // 1 + 2x1 - 0.5x2
        double[] beta = multipleRegression(X, ym);
        System.out.print("Coefficients (intercept, x1, x2): ");
        for (double c : beta) System.out.printf("%.4f ", c);
        System.out.println();
        System.out.println("Expected: intercept=1.0000, x1=2.0000, x2=-0.5000");

        System.out.println("\n=== Residuals ===");
        double[] pred = predict(X, beta);
        double[] res = residuals(ym, pred);
        for (int i = 0; i < res.length; i++) {
            System.out.printf("Obs=%.0f Pred=%.4f Res=%.4f%n", ym[i], pred[i], res[i]);
        }

        System.out.println("\n=== Residuals from Simple Regression ===");
        double[] bSimple = simpleRegression(x2, y3);
        double[] predSimple = new double[x2.length];
        for (int i = 0; i < x2.length; i++) {
            predSimple[i] = bSimple[0] * x2[i] + bSimple[1];
        }
        double[] resSimple = residuals(y3, predSimple);
        double ssRes = 0;
        for (double r : resSimple) ssRes += r * r;
        System.out.printf("Sum of squared residuals: %.6f%n", ssRes);
        for (int i = 0; i < resSimple.length; i++) {
            System.out.printf("x=%.0f Obs=%.1f Pred=%.4f Res=%.4f%n",
                x2[i], y3[i], predSimple[i], resSimple[i]);
        }
    }
}

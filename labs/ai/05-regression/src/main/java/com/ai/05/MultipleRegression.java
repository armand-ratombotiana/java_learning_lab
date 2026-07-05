package com.ai05;

public class MultipleRegression {
    private double[] coefficients;

    public void fit(double[][] X, double[] y) {
        int n = X.length;
        int m = X[0].length;
        double[][] XtX = new double[m][m];
        double[] Xty = new double[m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < m; j++)
                for (int k = 0; k < n; k++)
                    XtX[i][j] += X[k][i] * X[k][j];
        for (int i = 0; i < m; i++)
            for (int k = 0; k < n; k++)
                Xty[i] += X[k][i] * y[k];
        coefficients = solveLinearSystem(XtX, Xty);
    }

    private double[] solveLinearSystem(double[][] A, double[] b) {
        int n = b.length;
        double[][] augmented = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, augmented[i], 0, n);
            augmented[i][n] = b[i];
        }
        for (int col = 0; col < n; col++) {
            int pivot = col;
            for (int row = col + 1; row < n; row++)
                if (Math.abs(augmented[row][col]) > Math.abs(augmented[pivot][col]))
                    pivot = row;
            double[] temp = augmented[col];
            augmented[col] = augmented[pivot];
            augmented[pivot] = temp;
            double pivotVal = augmented[col][col];
            for (int j = col; j <= n; j++)
                augmented[col][j] /= pivotVal;
            for (int row = 0; row < n; row++) {
                if (row != col) {
                    double factor = augmented[row][col];
                    for (int j = col; j <= n; j++)
                        augmented[row][j] -= factor * augmented[col][j];
                }
            }
        }
        double[] result = new double[n];
        for (int i = 0; i < n; i++)
            result[i] = augmented[i][n];
        return result;
    }

    public double predict(double[] x) {
        double sum = 0;
        for (int i = 0; i < coefficients.length; i++)
            sum += coefficients[i] * x[i];
        return sum;
    }

    public double[] getCoefficients() { return coefficients; }

    public static void main(String[] args) {
        System.out.println("=== Multiple Regression Demo ===");
        double[][] X = {{1, 1}, {1, 2}, {1, 3}, {1, 4}, {1, 5}};
        double[] y = {3, 5, 7, 9, 11};
        MultipleRegression mr = new MultipleRegression();
        mr.fit(X, y);
        System.out.println("Coefficients: " + java.util.Arrays.toString(mr.getCoefficients()));
        System.out.println("Predict [1,6]: " + mr.predict(new double[]{1, 6}));
    }
}

package com.math12;

import java.util.Arrays;

public class LinearAlgebra {

    public static double[] addVectors(double[] a, double[] b) {
        if (a.length != b.length) throw new IllegalArgumentException("Vector dimensions must match");
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) result[i] = a[i] + b[i];
        return result;
    }

    public static double[] subtractVectors(double[] a, double[] b) {
        if (a.length != b.length) throw new IllegalArgumentException("Vector dimensions must match");
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) result[i] = a[i] - b[i];
        return result;
    }

    public static double[] scaleVector(double[] v, double scalar) {
        double[] result = new double[v.length];
        for (int i = 0; i < v.length; i++) result[i] = v[i] * scalar;
        return result;
    }

    public static double dotProduct(double[] a, double[] b) {
        if (a.length != b.length) throw new IllegalArgumentException("Vector dimensions must match");
        double result = 0;
        for (int i = 0; i < a.length; i++) result += a[i] * b[i];
        return result;
    }

    public static double vectorNorm(double[] v) {
        return Math.sqrt(dotProduct(v, v));
    }

    public static double[] crossProduct(double[] a, double[] b) {
        if (a.length != 3 || b.length != 3) throw new IllegalArgumentException("Cross product requires 3D vectors");
        return new double[]{
            a[1] * b[2] - a[2] * b[1],
            a[2] * b[0] - a[0] * b[2],
            a[0] * b[1] - a[1] * b[0]
        };
    }

    public static double[][] addMatrices(double[][] a, double[][] b) {
        int rows = a.length, cols = a[0].length;
        if (b.length != rows || b[0].length != cols) throw new IllegalArgumentException("Matrix dimensions must match");
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result[i][j] = a[i][j] + b[i][j];
        return result;
    }

    public static double[][] multiplyMatrices(double[][] a, double[][] b) {
        int r1 = a.length, c1 = a[0].length, c2 = b[0].length;
        if (b.length != c1) throw new IllegalArgumentException("Matrix dimensions incompatible for multiplication");
        double[][] result = new double[r1][c2];
        for (int i = 0; i < r1; i++)
            for (int j = 0; j < c2; j++)
                for (int k = 0; k < c1; k++)
                    result[i][j] += a[i][k] * b[k][j];
        return result;
    }

    public static double[][] transpose(double[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        double[][] result = new double[cols][rows];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result[j][i] = matrix[i][j];
        return result;
    }

    public static double[] multiplyMatrixVector(double[][] matrix, double[] vector) {
        int rows = matrix.length, cols = matrix[0].length;
        if (vector.length != cols) throw new IllegalArgumentException("Vector length must match matrix columns");
        double[] result = new double[rows];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result[i] += matrix[i][j] * vector[j];
        return result;
    }

    public static double determinant(double[][] matrix) {
        int n = matrix.length;
        if (n != matrix[0].length) throw new IllegalArgumentException("Matrix must be square");
        if (n == 1) return matrix[0][0];
        if (n == 2) return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        double det = 0;
        for (int j = 0; j < n; j++) {
            det += (j % 2 == 0 ? 1 : -1) * matrix[0][j] * determinant(minor(matrix, 0, j));
        }
        return det;
    }

    private static double[][] minor(double[][] matrix, int row, int col) {
        int n = matrix.length;
        double[][] result = new double[n - 1][n - 1];
        for (int i = 0, ri = 0; i < n; i++) {
            if (i == row) continue;
            for (int j = 0, rj = 0; j < n; j++) {
                if (j == col) continue;
                result[ri][rj++] = matrix[i][j];
            }
            ri++;
        }
        return result;
    }

    public static double[][] inverse(double[][] matrix) {
        int n = matrix.length;
        if (n != matrix[0].length) throw new IllegalArgumentException("Matrix must be square");
        double det = determinant(matrix);
        if (Math.abs(det) < 1e-12) throw new ArithmeticException("Matrix is singular");
        double[][] adj = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adj[j][i] = ((i + j) % 2 == 0 ? 1 : -1) * determinant(minor(matrix, i, j));
            }
        }
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                adj[i][j] /= det;
        return adj;
    }

    public static double[][] identity(int size) {
        double[][] I = new double[size][size];
        for (int i = 0; i < size; i++) I[i][i] = 1;
        return I;
    }

    public static double[] solveLinearSystem(double[][] a, double[] b) {
        int n = a.length;
        double[][] aug = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, aug[i], 0, n);
            aug[i][n] = b[i];
        }
        // Gaussian elimination with partial pivoting
        for (int col = 0; col < n; col++) {
            int maxRow = col;
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(aug[row][col]) > Math.abs(aug[maxRow][col])) maxRow = row;
            }
            double[] temp = aug[col]; aug[col] = aug[maxRow]; aug[maxRow] = temp;
            double pivot = aug[col][col];
            if (Math.abs(pivot) < 1e-12) throw new ArithmeticException("Singular matrix");
            for (int j = col; j <= n; j++) aug[col][j] /= pivot;
            for (int row = 0; row < n; row++) {
                if (row != col) {
                    double factor = aug[row][col];
                    for (int j = col; j <= n; j++) aug[row][j] -= factor * aug[col][j];
                }
            }
        }
        double[] x = new double[n];
        for (int i = 0; i < n; i++) x[i] = aug[i][n];
        return x;
    }

    public static EigenDecomposition powerIteration(double[][] matrix, int iterations) {
        int n = matrix.length;
        double[] v = new double[n];
        for (int i = 0; i < n; i++) v[i] = Math.random();
        v = scaleVector(v, 1.0 / vectorNorm(v));
        for (int iter = 0; iter < iterations; iter++) {
            v = multiplyMatrixVector(matrix, v);
            v = scaleVector(v, 1.0 / vectorNorm(v));
        }
        double eigenvalue = dotProduct(multiplyMatrixVector(matrix, v), v);
        return new EigenDecomposition(eigenvalue, v);
    }

    public record EigenDecomposition(double eigenvalue, double[] eigenvector) {}

    public static double[][] gramSchmidtQR(double[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        double[][] q = new double[m][n];
        for (int j = 0; j < n; j++) {
            double[] v = new double[m];
            for (int i = 0; i < m; i++) v[i] = matrix[i][j];
            for (int k = 0; k < j; k++) {
                double[] qk = new double[m];
                for (int i = 0; i < m; i++) qk[i] = q[i][k];
                double dot = dotProduct(v, qk);
                for (int i = 0; i < m; i++) v[i] -= dot * qk[i];
            }
            double norm = vectorNorm(v);
            for (int i = 0; i < m; i++) q[i][j] = norm > 1e-12 ? v[i] / norm : 0;
        }
        return q;
    }

    public static double[][] svd2x2(double[][] a) {
        if (a.length != 2 || a[0].length != 2) throw new IllegalArgumentException("Requires 2x2 matrix");
        double a11 = a[0][0], a12 = a[0][1], a21 = a[1][0], a22 = a[1][1];
        double theta = 0.5 * Math.atan2(2 * (a11 * a21 + a12 * a22), (a11 * a11 + a12 * a12) - (a21 * a21 + a22 * a22));
        double c = Math.cos(theta), s = Math.sin(theta);
        double sigma1 = Math.sqrt((a11 * c + a21 * s) * (a11 * c + a21 * s) + (a12 * c + a22 * s) * (a12 * c + a22 * s));
        double sigma2 = Math.sqrt((a11 * s - a21 * c) * (a11 * s - a21 * c) + (a12 * s - a22 * c) * (a12 * s - a22 * c));
        return new double[][]{{sigma1, 0}, {0, sigma2}};
    }
}

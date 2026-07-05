package com.ai01;

import java.util.Arrays;

public class Eigenvalues {

    public static double powerIteration(Matrix matrix, int iterations) {
        int n = matrix.getRows();
        double[] b = new double[n];
        for (int i = 0; i < n; i++)
            b[i] = 1.0;
        double eigenvalue = 0;
        for (int iter = 0; iter < iterations; iter++) {
            double[] newB = new double[n];
            for (int i = 0; i < n; i++) {
                double sum = 0;
                for (int j = 0; j < n; j++)
                    sum += matrix.get(i, j) * b[j];
                newB[i] = sum;
            }
            double norm = 0;
            for (double v : newB) norm += v * v;
            norm = Math.sqrt(norm);
            for (int i = 0; i < n; i++) b[i] = newB[i] / norm;
            eigenvalue = VectorOps.dotProduct(b, matrixMultiplyVector(matrix, b));
        }
        return eigenvalue;
    }

    private static double[] matrixMultiplyVector(Matrix m, double[] v) {
        int n = m.getRows();
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0;
            for (int j = 0; j < n; j++)
                sum += m.get(i, j) * v[j];
            result[i] = sum;
        }
        return result;
    }

    public static double[] qrAlgorithm(Matrix matrix, int iterations) {
        int n = matrix.getRows();
        Matrix A = new Matrix(matrix.getData());
        for (int iter = 0; iter < iterations; iter++) {
            Matrix q = new Matrix(n, n);
            Matrix r = new Matrix(n, n);
            qrDecomposition(A, q, r);
            A = r.multiply(q);
        }
        double[] eigenvalues = new double[n];
        for (int i = 0; i < n; i++)
            eigenvalues[i] = A.get(i, i);
        return eigenvalues;
    }

    private static void qrDecomposition(Matrix A, Matrix Q, Matrix R) {
        int n = A.getRows();
        double[][] a = A.getData();
        double[][] q = new double[n][n];
        double[][] r = new double[n][n];
        for (int k = 0; k < n; k++) {
            double[] v = new double[n];
            System.arraycopy(a[k], 0, v, 0, n);
            for (int i = 0; i < k; i++) {
                double dot = 0;
                for (int j = 0; j < n; j++)
                    dot += q[j][i] * a[j][k];
                r[i][k] = dot;
                for (int j = 0; j < n; j++)
                    v[j] -= dot * q[j][i];
            }
            double norm = 0;
            for (double x : v) norm += x * x;
            norm = Math.sqrt(norm);
            r[k][k] = norm;
            for (int j = 0; j < n; j++)
                q[j][k] = v[j] / norm;
        }
        for (int i = 0; i < n; i++)
            System.arraycopy(q[i], 0, Q.getData()[i], 0, n);
        for (int i = 0; i < n; i++)
            System.arraycopy(r[i], 0, R.getData()[i], 0, n);
    }

    public static void main(String[] args) {
        System.out.println("=== Eigenvalue Computation Demo ===");
        double[][] data = {{4, 1}, {2, 3}};
        Matrix m = new Matrix(data);
        System.out.println("Matrix:");
        m.print();
        double dominantEigenvalue = powerIteration(m, 100);
        System.out.println("Dominant eigenvalue (power iteration): " + dominantEigenvalue);
        double[] allEigenvalues = qrAlgorithm(m, 100);
        System.out.println("All eigenvalues (QR algorithm): " + Arrays.toString(allEigenvalues));
    }
}

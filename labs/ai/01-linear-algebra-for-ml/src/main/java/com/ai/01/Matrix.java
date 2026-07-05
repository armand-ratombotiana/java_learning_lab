package com.ai01;

import java.util.Arrays;

public class Matrix {
    private final double[][] data;
    private final int rows, cols;

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }

    public Matrix(double[][] data) {
        this.rows = data.length;
        this.cols = data[0].length;
        this.data = new double[rows][cols];
        for (int i = 0; i < rows; i++)
            System.arraycopy(data[i], 0, this.data[i], 0, cols);
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public double get(int i, int j) { return data[i][j]; }
    public void set(int i, int j, double val) { data[i][j] = val; }
    public double[][] getData() { return data; }

    public Matrix add(Matrix other) {
        checkDimensions(other);
        Matrix result = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[i][j] = this.data[i][j] + other.data[i][j];
        return result;
    }

    public Matrix subtract(Matrix other) {
        checkDimensions(other);
        Matrix result = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[i][j] = this.data[i][j] - other.data[i][j];
        return result;
    }

    public Matrix multiply(Matrix other) {
        if (this.cols != other.rows)
            throw new IllegalArgumentException("Matrix dimensions mismatch for multiplication");
        Matrix result = new Matrix(this.rows, other.cols);
        for (int i = 0; i < this.rows; i++)
            for (int j = 0; j < other.cols; j++)
                for (int k = 0; k < this.cols; k++)
                    result.data[i][j] += this.data[i][k] * other.data[k][j];
        return result;
    }

    public Matrix transpose() {
        Matrix result = new Matrix(cols, rows);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[j][i] = this.data[i][j];
        return result;
    }

    public double determinant() {
        if (rows != cols)
            throw new IllegalArgumentException("Determinant requires square matrix");
        return detRecursive(data);
    }

    private double detRecursive(double[][] m) {
        int n = m.length;
        if (n == 1) return m[0][0];
        if (n == 2) return m[0][0] * m[1][1] - m[0][1] * m[1][0];
        double det = 0;
        for (int col = 0; col < n; col++) {
            double[][] sub = new double[n - 1][n - 1];
            for (int i = 1; i < n; i++)
                for (int j = 0, subCol = 0; j < n; j++)
                    if (j != col) sub[i - 1][subCol++] = m[i][j];
            det += (col % 2 == 0 ? 1 : -1) * m[0][col] * detRecursive(sub);
        }
        return det;
    }

    public Matrix inverse() {
        if (rows != cols)
            throw new IllegalArgumentException("Inverse requires square matrix");
        int n = rows;
        double[][] augmented = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(data[i], 0, augmented[i], 0, n);
            augmented[i][n + i] = 1;
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
            for (int j = 0; j < 2 * n; j++)
                augmented[col][j] /= pivotVal;
            for (int row = 0; row < n; row++) {
                if (row != col) {
                    double factor = augmented[row][col];
                    for (int j = 0; j < 2 * n; j++)
                        augmented[row][j] -= factor * augmented[col][j];
                }
            }
        }
        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++)
            System.arraycopy(augmented[i], n, inv[i], 0, n);
        return new Matrix(inv);
    }

    private void checkDimensions(Matrix other) {
        if (this.rows != other.rows || this.cols != other.cols)
            throw new IllegalArgumentException("Matrix dimensions must match");
    }

    public void print() {
        for (double[] row : data)
            System.out.println(Arrays.toString(row));
    }

    public static void main(String[] args) {
        System.out.println("=== Matrix Operations Demo ===");
        double[][] a = {{1, 2}, {3, 4}};
        double[][] b = {{5, 6}, {7, 8}};
        Matrix mA = new Matrix(a);
        Matrix mB = new Matrix(b);
        System.out.println("Matrix A:");
        mA.print();
        System.out.println("Matrix B:");
        mB.print();
        System.out.println("A + B:");
        mA.add(mB).print();
        System.out.println("A * B:");
        mA.multiply(mB).print();
        System.out.println("A Transpose:");
        mA.transpose().print();
        System.out.println("Determinant of A: " + mA.determinant());
        System.out.println("Inverse of A:");
        mA.inverse().print();
    }
}

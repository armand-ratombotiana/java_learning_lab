package com.ai01;

import java.util.Arrays;

public class VectorOps {

    public static double dotProduct(double[] a, double[] b) {
        if (a.length != b.length)
            throw new IllegalArgumentException("Vectors must have same length");
        double sum = 0;
        for (int i = 0; i < a.length; i++)
            sum += a[i] * b[i];
        return sum;
    }

    public static double[] crossProduct3D(double[] a, double[] b) {
        if (a.length != 3 || b.length != 3)
            throw new IllegalArgumentException("Cross product requires 3D vectors");
        return new double[]{
            a[1] * b[2] - a[2] * b[1],
            a[2] * b[0] - a[0] * b[2],
            a[0] * b[1] - a[1] * b[0]
        };
    }

    public static double norm(double[] v) {
        double sum = 0;
        for (double x : v) sum += x * x;
        return Math.sqrt(sum);
    }

    public static double[] normalize(double[] v) {
        double n = norm(v);
        double[] result = new double[v.length];
        for (int i = 0; i < v.length; i++)
            result[i] = v[i] / n;
        return result;
    }

    public static double[] add(double[] a, double[] b) {
        if (a.length != b.length)
            throw new IllegalArgumentException("Vectors must have same length");
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++)
            result[i] = a[i] + b[i];
        return result;
    }

    public static double[] scalarMultiply(double[] v, double scalar) {
        double[] result = new double[v.length];
        for (int i = 0; i < v.length; i++)
            result[i] = v[i] * scalar;
        return result;
    }

    public static double cosineSimilarity(double[] a, double[] b) {
        return dotProduct(a, b) / (norm(a) * norm(b));
    }

    public static double[] matrixVectorMultiply(Matrix m, double[] v) {
        int rows = m.getRows();
        int cols = m.getCols();
        if (cols != v.length)
            throw new IllegalArgumentException("Matrix columns must match vector length");
        double[] result = new double[rows];
        for (int i = 0; i < rows; i++) {
            double sum = 0;
            for (int j = 0; j < cols; j++)
                sum += m.get(i, j) * v[j];
            result[i] = sum;
        }
        return result;
    }

    public static double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    public static double[] elementWiseMultiply(double[] a, double[] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++)
            result[i] = a[i] * b[i];
        return result;
    }

    public static void main(String[] args) {
        System.out.println("=== Vector Operations Demo ===");
        double[] v1 = {1, 2, 3};
        double[] v2 = {4, 5, 6};
        System.out.println("v1 = " + Arrays.toString(v1));
        System.out.println("v2 = " + Arrays.toString(v2));
        System.out.println("Dot product: " + dotProduct(v1, v2));
        System.out.println("Cross product: " + Arrays.toString(crossProduct3D(v1, v2)));
        System.out.println("Norm of v1: " + norm(v1));
        System.out.println("Normalized v1: " + Arrays.toString(normalize(v1)));
        System.out.println("Cosine similarity: " + cosineSimilarity(v1, v2));
        System.out.println("Euclidean distance: " + euclideanDistance(v1, v2));
        double[][] mat = {{1, 2}, {3, 4}};
        double[] vec = {5, 6};
        System.out.println("Matrix * vector: " + Arrays.toString(matrixVectorMultiply(new Matrix(mat), vec)));
    }
}

package com.datasci.12;

import java.util.*;

public final class Utils {

    private Utils() {}

    // Matrix operations
    public static double[][] transpose(double[][] matrix) {
        if (matrix.length == 0) return new double[0][0];
        int rows = matrix.length, cols = matrix[0].length;
        double[][] result = new double[cols][rows];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result[j][i] = matrix[i][j];
        return result;
    }

    public static double[][] multiply(double[][] a, double[][] b) {
        int m = a.length, n = a[0].length, p = b[0].length;
        double[][] result = new double[m][p];
        for (int i = 0; i < m; i++)
            for (int k = 0; k < n; k++)
                for (int j = 0; j < p; j++)
                    result[i][j] += a[i][k] * b[k][j];
        return result;
    }

    public static double[] matrixVectorMultiply(double[][] matrix, double[] vector) {
        int m = matrix.length, n = vector.length;
        double[] result = new double[m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                result[i] += matrix[i][j] * vector[j];
        return result;
    }

    // Distance metrics
    public static double euclideanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    public static double manhattanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.abs(a[i] - b[i]);
        }
        return sum;
    }

    public static double cosineSimilarity(double[] a, double[] b) {
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // Statistics
    public static double mean(double[] values) {
        return Arrays.stream(values).average().orElse(Double.NaN);
    }

    public static double variance(double[] values) {
        double mean = mean(values);
        return Arrays.stream(values).map(v -> (v - mean) * (v - mean)).average().orElse(Double.NaN);
    }

    public static double stdDev(double[] values) {
        return Math.sqrt(variance(values));
    }

    public static double covariance(double[] x, double[] y) {
        double meanX = mean(x), meanY = mean(y);
        double sum = 0.0;
        for (int i = 0; i < x.length; i++) {
            sum += (x[i] - meanX) * (y[i] - meanY);
        }
        return sum / (x.length - 1);
    }

    public static double correlation(double[] x, double[] y) {
        return covariance(x, y) / (stdDev(x) * stdDev(y));
    }

    // Normalization
    public static double[] zScoreNormalize(double[] values) {
        double mean = mean(values);
        double std = stdDev(values);
        if (std == 0) return values.clone();
        return Arrays.stream(values).map(v -> (v - mean) / std).toArray();
    }

    public static double[] minMaxNormalize(double[] values) {
        double min = Arrays.stream(values).min().orElse(0);
        double max = Arrays.stream(values).max().orElse(0);
        if (max == min) return values.clone();
        return Arrays.stream(values).map(v -> (v - min) / (max - min)).toArray();
    }

    public static double[][] standardize(double[][] features) {
        int cols = features[0].length;
        double[][] result = new double[features.length][cols];
        for (int j = 0; j < cols; j++) {
            final int col = j;
            double[] colVals = Arrays.stream(features).mapToDouble(row -> row[col]).toArray();
            double[] normalized = zScoreNormalize(colVals);
            for (int i = 0; i < features.length; i++) {
                result[i][col] = normalized[i];
            }
        }
        return result;
    }

    public static double[][] shuffleRows(double[][] data, double[] labels, Random rng) {
        int n = data.length;
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        Collections.shuffle(Arrays.asList(indices), rng);
        double[][] shuffled = new double[n][];
        for (int i = 0; i < n; i++) shuffled[i] = data[indices[i]];
        return shuffled;
    }

    public static int[] argmax(double[] values) {
        double max = Double.NEGATIVE_INFINITY;
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
                indices.clear();
                indices.add(i);
            } else if (values[i] == max) {
                indices.add(i);
            }
        }
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }
}

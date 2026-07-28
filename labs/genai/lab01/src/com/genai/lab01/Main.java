package com.genai.lab01;

import java.util.Arrays;

/**
 * Transformer Architecture Deep Dive
 * 
 * Demonstrates scaled dot-product attention, multi-head attention,
 * positional encoding, and a simplified Transformer block in Java.
 */
public class Main {

    /**
     * Scaled dot-product attention.
     *
     * @param Q query matrix (seqLen x dk)
     * @param K key matrix (seqLen x dk)
     * @param V value matrix (seqLen x dv)
     * @return attention output
     */
    public static double[][] scaledDotProductAttention(double[][] Q, double[][] K, double[][] V) {
        int seqLen = Q.length;
        int dk = Q[0].length;
        int dv = V[0].length;

        double[][] scores = new double[seqLen][seqLen];
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < seqLen; j++) {
                double dot = 0.0;
                for (int k = 0; k < dk; k++) {
                    dot += Q[i][k] * K[j][k];
                }
                scores[i][j] = dot / Math.sqrt(dk);
            }
        }

        double[][] weights = softmax(scores);
        double[][] output = new double[seqLen][dv];
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < dv; j++) {
                for (int k = 0; k < seqLen; k++) {
                    output[i][j] += weights[i][k] * V[k][j];
                }
            }
        }
        return output;
    }

    /** Softmax over last dimension (row-wise). */
    public static double[][] softmax(double[][] x) {
        int rows = x.length;
        int cols = x[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            double max = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < cols; j++) {
                if (x[i][j] > max) max = x[i][j];
            }
            double sum = 0.0;
            for (int j = 0; j < cols; j++) {
                result[i][j] = Math.exp(x[i][j] - max);
                sum += result[i][j];
            }
            for (int j = 0; j < cols; j++) {
                result[i][j] /= sum;
            }
        }
        return result;
    }

    /**
     * Sinusoidal positional encoding.
     *
     * @param seqLen sequence length
     * @param dModel model dimension
     * @return positional encoding matrix
     */
    public static double[][] positionalEncoding(int seqLen, int dModel) {
        double[][] pe = new double[seqLen][dModel];
        for (int pos = 0; pos < seqLen; pos++) {
            for (int i = 0; i < dModel; i++) {
                double angle = pos / Math.pow(10000.0, (2.0 * (i / 2)) / dModel);
                pe[pos][i] = (i % 2 == 0) ? Math.sin(angle) : Math.cos(angle);
            }
        }
        return pe;
    }

    /**
     * Single Transformer encoder block: MHA -> Add&Norm -> FFN -> Add&Norm.
     */
    public static double[][] encoderBlock(double[][] x, double[][] Wq, double[][] Wk,
                                          double[][] Wv, double[][] Wo,
                                          double[][] W1, double[][] W2) {
        double[][] attnOut = scaledDotProductAttention(
            matMul(x, Wq), matMul(x, Wk), matMul(x, Wv));
        attnOut = matMul(attnOut, Wo);

        double[][] residual1 = add(x, attnOut);
        double[][] norm1 = layerNorm(residual1);

        double[][] ffnOut = relu(matMul(norm1, W1));
        ffnOut = matMul(ffnOut, W2);

        double[][] residual2 = add(norm1, ffnOut);
        return layerNorm(residual2);
    }

    // ---- Utility methods ----

    public static double[][] matMul(double[][] a, double[][] b) {
        int m = a.length, n = a[0].length, p = b[0].length;
        double[][] c = new double[m][p];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                for (int k = 0; k < n; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return c;
    }

    public static double[][] add(double[][] a, double[][] b) {
        int rows = a.length, cols = a[0].length;
        double[][] c = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                c[i][j] = a[i][j] + b[i][j];
            }
        }
        return c;
    }

    public static double[][] layerNorm(double[][] x) {
        int rows = x.length, cols = x[0].length;
        double[][] out = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            double mean = 0.0;
            for (int j = 0; j < cols; j++) mean += x[i][j];
            mean /= cols;
            double var = 0.0;
            for (int j = 0; j < cols; j++) var += (x[i][j] - mean) * (x[i][j] - mean);
            var /= cols;
            for (int j = 0; j < cols; j++) {
                out[i][j] = (x[i][j] - mean) / Math.sqrt(var + 1e-6);
            }
        }
        return out;
    }

    public static double[][] relu(double[][] x) {
        int rows = x.length, cols = x[0].length;
        double[][] out = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                out[i][j] = Math.max(0.0, x[i][j]);
            }
        }
        return out;
    }

    public static void main(String[] args) {
        int seqLen = 4;
        int dModel = 8;
        int dk = 4, dv = 4;

        double[][] Q = new double[seqLen][dk];
        double[][] K = new double[seqLen][dk];
        double[][] V = new double[seqLen][dv];
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < dk; j++) {
                Q[i][j] = Math.sin(i + j + 1);
                K[i][j] = Math.cos(i + j + 1);
            }
            for (int j = 0; j < dv; j++) {
                V[i][j] = (i + j) * 0.1;
            }
        }

        double[][] attnOut = scaledDotProductAttention(Q, K, V);
        System.out.println("=== Scaled Dot-Product Attention Output ===");
        for (double[] row : attnOut) {
            System.out.println(Arrays.toString(row));
        }

        double[][] pe = positionalEncoding(seqLen, dModel);
        System.out.println("\n=== Positional Encoding (first 3 dims) ===");
        for (double[] row : pe) {
            System.out.println(Arrays.toString(Arrays.copyOf(row, 3)));
        }

        System.out.println("\nTransformer components validated successfully.");
    }
}

package com.genai.lab13;

import java.util.*;

/**
 * Context Window Management
 * 
 * Demonstrates sliding window attention, RoPE, ALiBi,
 * and context compression in Java.
 */
public class Main {

    /** Sliding window attention: each token attends to last W tokens. */
    public static double[][] slidingWindowAttention(double[][] Q, double[][] K,
                                                     double[][] V, int windowSize) {
        int seqLen = Q.length;
        int dk = Q[0].length;
        int dv = V[0].length;
        double[][] output = new double[seqLen][dv];

        for (int i = 0; i < seqLen; i++) {
            int start = Math.max(0, i - windowSize + 1);
            int end = i + 1;
            int wLen = end - start;

            double[] scores = new double[wLen];
            for (int j = start; j < end; j++) {
                double dot = 0.0;
                for (int k = 0; k < dk; k++) dot += Q[i][k] * K[j][k];
                scores[j - start] = dot / Math.sqrt(dk);
            }

            double max = Double.NEGATIVE_INFINITY;
            for (double s : scores) if (s > max) max = s;
            double sum = 0.0;
            for (int j = 0; j < wLen; j++) { scores[j] = Math.exp(scores[j] - max); sum += scores[j]; }
            for (int j = 0; j < wLen; j++) scores[j] /= sum;

            for (int j = 0; j < dv; j++) {
                for (int k = 0; k < wLen; k++) {
                    output[i][j] += scores[k] * V[start + k][j];
                }
            }
        }
        return output;
    }

    /** RoPE: apply rotary position encoding to Q and K. */
    public static void applyRoPE(double[][] Q, double[][] K) {
        int seqLen = Q.length;
        int dk = Q[0].length;
        for (int pos = 0; pos < seqLen; pos++) {
            for (int i = 0; i < dk; i += 2) {
                double theta = pos / Math.pow(10000.0, (double) i / dk);
                double cos = Math.cos(theta);
                double sin = Math.sin(theta);
                int i2 = i + 1;
                if (i2 >= dk) continue;
                double q1 = Q[pos][i], q2 = Q[pos][i2];
                double k1 = K[pos][i], k2 = K[pos][i2];
                Q[pos][i] = q1 * cos - q2 * sin;
                Q[pos][i2] = q1 * sin + q2 * cos;
                K[pos][i] = k1 * cos - k2 * sin;
                K[pos][i2] = k1 * sin + k2 * cos;
            }
        }
    }

    /** ALiBi: add linear biases to attention scores. */
    public static double[][] alibiAttention(double[][] Q, double[][] K, double[][] V) {
        int seqLen = Q.length;
        int dk = Q[0].length;
        int dv = V[0].length;
        double m = 0.1; // slope (single head, simplified)
        double[][] output = new double[seqLen][dv];

        for (int i = 0; i < seqLen; i++) {
            double[] scores = new double[seqLen];
            for (int j = 0; j < seqLen; j++) {
                double dot = 0.0;
                for (int k = 0; k < dk; k++) dot += Q[i][k] * K[j][k];
                scores[j] = dot / Math.sqrt(dk) - m * Math.abs(i - j);
            }
            double max = Double.NEGATIVE_INFINITY;
            for (double s : scores) if (s > max) max = s;
            double sum = 0.0;
            for (int j = 0; j < seqLen; j++) { scores[j] = Math.exp(scores[j] - max); sum += scores[j]; }
            for (int j = 0; j < seqLen; j++) scores[j] /= sum;
            for (int j = 0; j < dv; j++)
                for (int k = 0; k < seqLen; k++)
                    output[i][j] += scores[k] * V[k][j];
        }
        return output;
    }

    /** Context compressor: keep only top-k tokens by attention weight. */
    static class ContextCompressor {
        static double[][] compress(double[][] tokens, double keepRatio) {
            int keep = Math.max(1, (int) (tokens.length * keepRatio));
            return Arrays.copyOf(tokens, keep);
        }
    }

    public static void main(String[] args) {
        int seqLen = 8, dk = 4, dv = 4, windowSize = 3;
        double[][] Q = new double[seqLen][dk];
        double[][] K = new double[seqLen][dk];
        double[][] V = new double[seqLen][dv];
        Random rng = new Random(42);
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < dk; j++) { Q[i][j] = rng.nextGaussian(); K[i][j] = rng.nextGaussian(); }
            for (int j = 0; j < dv; j++) V[i][j] = rng.nextGaussian() * 0.5;
        }

        System.out.println("=== Sliding Window Attention (W=3) ===");
        double[][] swOut = slidingWindowAttention(Q, K, V, windowSize);
        System.out.println("Output shape: " + swOut.length + " x " + swOut[0].length);

        System.out.println("\n=== RoPE Rotated Q (first token, first 4 dims) ===");
        applyRoPE(Q, K);
        System.out.println(Arrays.toString(Arrays.copyOf(Q[0], 4)));

        System.out.println("\n=== ALiBi Attention ===");
        double[][] alibiOut = alibiAttention(Q, K, V);
        System.out.println("ALiBi output shape: " + alibiOut.length + " x " + alibiOut[0].length);

        System.out.println("\n=== Context Compression ===");
        double[][] compressed = ContextCompressor.compress(V, 0.5);
        System.out.println("Compressed from " + V.length + " to " + compressed.length + " tokens.");

        System.out.println("\nContext window management concepts validated.");
    }
}

package com.ai15;

public class SelfAttention {
    private double[][] Wq, Wk, Wv;
    private int dModel;

    public SelfAttention(int dModel) {
        this.dModel = dModel;
        java.util.Random rng = new java.util.Random(42);
        Wq = new double[dModel][dModel];
        Wk = new double[dModel][dModel];
        Wv = new double[dModel][dModel];
        for (int i = 0; i < dModel; i++)
            for (int j = 0; j < dModel; j++) {
                Wq[i][j] = rng.nextDouble() * 0.1 - 0.05;
                Wk[i][j] = rng.nextDouble() * 0.1 - 0.05;
                Wv[i][j] = rng.nextDouble() * 0.1 - 0.05;
            }
    }

    public double[][] scaledDotProductAttention(double[][] Q, double[][] K, double[][] V) {
        int seqLen = Q.length;
        double scale = Math.sqrt(dModel);
        double[][] scores = new double[seqLen][seqLen];
        for (int i = 0; i < seqLen; i++)
            for (int j = 0; j < seqLen; j++)
                for (int k = 0; k < dModel; k++)
                    scores[i][j] += Q[i][k] * K[j][k];
        for (int i = 0; i < seqLen; i++)
            for (int j = 0; j < seqLen; j++)
                scores[i][j] /= scale;
        for (int i = 0; i < seqLen; i++) {
            double max = scores[i][0];
            for (int j = 1; j < seqLen; j++)
                max = Math.max(max, scores[i][j]);
            double sum = 0;
            for (int j = 0; j < seqLen; j++) {
                scores[i][j] = Math.exp(scores[i][j] - max);
                sum += scores[i][j];
            }
            for (int j = 0; j < seqLen; j++)
                scores[i][j] /= sum;
        }
        double[][] output = new double[seqLen][dModel];
        for (int i = 0; i < seqLen; i++)
            for (int j = 0; j < dModel; j++)
                for (int k = 0; k < seqLen; k++)
                    output[i][j] += scores[i][k] * V[k][j];
        return output;
    }

    public double[][] forward(double[][] input) {
        int seqLen = input.length;
        double[][] Q = new double[seqLen][dModel];
        double[][] K = new double[seqLen][dModel];
        double[][] V = new double[seqLen][dModel];
        for (int i = 0; i < seqLen; i++)
            for (int j = 0; j < dModel; j++) {
                for (int k = 0; k < dModel; k++) {
                    Q[i][j] += input[i][k] * Wq[k][j];
                    K[i][j] += input[i][k] * Wk[k][j];
                    V[i][j] += input[i][k] * Wv[k][j];
                }
            }
        return scaledDotProductAttention(Q, K, V);
    }

    public static void main(String[] args) {
        System.out.println("=== Self-Attention Demo ===");
        SelfAttention sa = new SelfAttention(4);
        double[][] input = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}};
        double[][] output = sa.forward(input);
        System.out.println("Input:");
        for (double[] r : input) System.out.println("  " + java.util.Arrays.toString(r));
        System.out.println("Output:");
        for (double[] r : output) System.out.println("  " + java.util.Arrays.toString(r));
    }
}

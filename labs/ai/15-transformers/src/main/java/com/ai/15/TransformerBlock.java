package com.ai15;

public class TransformerBlock {
    private MultiHeadAttention attention;
    private double[][] W1, W2;
    private double[] b1, b2;
    private int dModel, dff;

    public TransformerBlock(int dModel, int numHeads, int dff) {
        this.dModel = dModel;
        this.dff = dff;
        attention = new MultiHeadAttention(dModel, numHeads);
        java.util.Random rng = new java.util.Random(42);
        W1 = new double[dModel][dff];
        b1 = new double[dff];
        W2 = new double[dff][dModel];
        b2 = new double[dModel];
        for (int i = 0; i < dModel; i++)
            for (int j = 0; j < dff; j++)
                W1[i][j] = rng.nextDouble() * 0.1 - 0.05;
        for (int i = 0; i < dff; i++)
            for (int j = 0; j < dModel; j++)
                W2[i][j] = rng.nextDouble() * 0.1 - 0.05;
    }

    private double relu(double x) { return Math.max(0, x); }

    private double[][] layerNorm(double[][] x) {
        int seq = x.length, dim = x[0].length;
        double[][] result = new double[seq][dim];
        for (int i = 0; i < seq; i++) {
            double mean = 0;
            for (int j = 0; j < dim; j++) mean += x[i][j];
            mean /= dim;
            double var = 0;
            for (int j = 0; j < dim; j++) var += (x[i][j] - mean) * (x[i][j] - mean);
            var /= dim;
            double std = Math.sqrt(var + 1e-6);
            for (int j = 0; j < dim; j++)
                result[i][j] = (x[i][j] - mean) / std;
        }
        return result;
    }

    private double[][] addAndNorm(double[][] x, double[][] residual) {
        int seq = x.length, dim = x[0].length;
        double[][] sum = new double[seq][dim];
        for (int i = 0; i < seq; i++)
            for (int j = 0; j < dim; j++)
                sum[i][j] = x[i][j] + residual[i][j];
        return layerNorm(sum);
    }

    public double[][] forward(double[][] input) {
        double[][] attnOut = attention.forward(input);
        double[][] attnResidual = addAndNorm(attnOut, input);
        int seq = attnResidual.length;
        double[][] ffnIn = new double[seq][dff];
        for (int i = 0; i < seq; i++)
            for (int j = 0; j < dff; j++) {
                double sum = b1[j];
                for (int k = 0; k < dModel; k++)
                    sum += attnResidual[i][k] * W1[k][j];
                ffnIn[i][j] = relu(sum);
            }
        double[][] ffnOut = new double[seq][dModel];
        for (int i = 0; i < seq; i++)
            for (int j = 0; j < dModel; j++) {
                double sum = b2[j];
                for (int k = 0; k < dff; k++)
                    sum += ffnIn[i][k] * W2[k][j];
                ffnOut[i][j] = sum;
            }
        return addAndNorm(ffnOut, attnResidual);
    }

    public static void main(String[] args) {
        System.out.println("=== Transformer Block Demo ===");
        TransformerBlock tb = new TransformerBlock(8, 4, 16);
        double[][] input = new double[3][8];
        java.util.Random rng = new java.util.Random(42);
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 8; j++)
                input[i][j] = rng.nextDouble();
        double[][] output = tb.forward(input);
        System.out.println("Input shape: 3x8");
        System.out.println("Output shape: " + output.length + "x" + output[0].length);
        System.out.println("Output: " + java.util.Arrays.toString(output[0]));
    }
}

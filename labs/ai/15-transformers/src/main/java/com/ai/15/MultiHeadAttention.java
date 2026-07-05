package com.ai15;

public class MultiHeadAttention {
    private SelfAttention[] heads;
    private double[][] Wo;
    private int dModel, numHeads, dHead;

    public MultiHeadAttention(int dModel, int numHeads) {
        this.dModel = dModel;
        this.numHeads = numHeads;
        this.dHead = dModel / numHeads;
        heads = new SelfAttention[numHeads];
        for (int h = 0; h < numHeads; h++)
            heads[h] = new SelfAttention(dHead);
        Wo = new double[dModel][dModel];
        java.util.Random rng = new java.util.Random(42);
        for (int i = 0; i < dModel; i++)
            for (int j = 0; j < dModel; j++)
                Wo[i][j] = rng.nextDouble() * 0.1 - 0.05;
    }

    public double[][] forward(double[][] input) {
        int seqLen = input.length;
        double[][][] headOutputs = new double[numHeads][seqLen][dHead];
        for (int h = 0; h < numHeads; h++) {
            double[][] headInput = new double[seqLen][dHead];
            for (int i = 0; i < seqLen; i++)
                System.arraycopy(input[i], h * dHead, headInput[i], 0, dHead);
            headOutputs[h] = heads[h].forward(headInput);
        }
        double[][] concat = new double[seqLen][dModel];
        for (int i = 0; i < seqLen; i++)
            for (int h = 0; h < numHeads; h++)
                System.arraycopy(headOutputs[h][i], 0, concat[i], h * dHead, dHead);
        double[][] output = new double[seqLen][dModel];
        for (int i = 0; i < seqLen; i++)
            for (int j = 0; j < dModel; j++)
                for (int k = 0; k < dModel; k++)
                    output[i][j] += concat[i][k] * Wo[k][j];
        return output;
    }

    public static void main(String[] args) {
        System.out.println("=== Multi-Head Attention Demo ===");
        MultiHeadAttention mha = new MultiHeadAttention(8, 4);
        double[][] input = new double[3][8];
        java.util.Random rng = new java.util.Random(42);
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 8; j++)
                input[i][j] = rng.nextDouble();
        double[][] output = mha.forward(input);
        System.out.println("Input shape: 3x8");
        System.out.println("Output shape: " + output.length + "x" + output[0].length);
        System.out.println("Output sample: " + java.util.Arrays.toString(output[0]));
    }
}

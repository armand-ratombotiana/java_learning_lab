package com.ai20;

import java.util.*;

public class LoRAConcept {
    private double[][] originalWeights;
    private double[][] loraA;
    private double[][] loraB;
    private int rank;
    private double scalingFactor;

    public LoRAConcept(double[][] originalWeights, int rank, double alpha) {
        this.originalWeights = originalWeights;
        this.rank = rank;
        this.scalingFactor = alpha / rank;
        int d = originalWeights.length;
        int k = originalWeights[0].length;
        loraA = new double[d][rank];
        loraB = new double[rank][k];
        Random rng = new Random(42);
        for (int i = 0; i < d; i++)
            for (int r = 0; r < rank; r++)
                loraA[i][r] = rng.nextDouble() * 0.01;
        for (int r = 0; r < rank; r++)
            for (int j = 0; j < k; j++)
                loraB[r][j] = 0;
    }

    public double[][] getAdaptedWeights() {
        int d = originalWeights.length;
        int k = originalWeights[0].length;
        double[][] adapted = new double[d][k];
        for (int i = 0; i < d; i++)
            for (int j = 0; j < k; j++) {
                adapted[i][j] = originalWeights[i][j];
                for (int r = 0; r < rank; r++)
                    adapted[i][j] += scalingFactor * loraA[i][r] * loraB[r][j];
            }
        return adapted;
    }

    public void fineTuneStep(double[][] input, double[][] gradOutput, double learningRate) {
        int d = originalWeights.length;
        int k = originalWeights[0].length;
        int batchSize = input.length;
        double[][] loraBGrad = new double[rank][k];
        double[][] loraAGrad = new double[d][rank];
        for (int b = 0; b < batchSize; b++) {
            double[] hidden = new double[rank];
            for (int r = 0; r < rank; r++) {
                double sum = 0;
                for (int i = 0; i < d; i++)
                    sum += input[b][i] * loraA[i][r];
                hidden[r] = sum;
            }
            for (int r = 0; r < rank; r++)
                for (int j = 0; j < k; j++)
                    loraBGrad[r][j] += hidden[r] * gradOutput[b][j];
            for (int i = 0; i < d; i++)
                for (int r = 0; r < rank; r++) {
                    double sum = 0;
                    for (int j = 0; j < k; j++)
                        sum += loraB[r][j] * gradOutput[b][j];
                    loraAGrad[i][r] += input[b][i] * sum;
                }
        }
        for (int r = 0; r < rank; r++)
            for (int j = 0; j < k; j++)
                loraB[r][j] -= learningRate * loraBGrad[r][j] / batchSize;
        for (int i = 0; i < d; i++)
            for (int r = 0; r < rank; r++)
                loraA[i][r] -= learningRate * loraAGrad[i][r] / batchSize;
    }

    public int countTrainableParams() {
        int d = originalWeights.length;
        int k = originalWeights[0].length;
        return d * rank + rank * k;
    }

    public int countOriginalParams() {
        return originalWeights.length * originalWeights[0].length;
    }

    public double[][] getLoraA() { return loraA; }
    public double[][] getLoraB() { return loraB; }

    public static void main(String[] args) {
        System.out.println("=== LoRA Concept Demo ===");
        double[][] W = new double[10][20];
        Random rng = new Random(42);
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 20; j++)
                W[i][j] = rng.nextDouble() * 0.1 - 0.05;
        LoRAConcept lora = new LoRAConcept(W, 2, 4.0);
        System.out.println("Original params: " + lora.countOriginalParams());
        System.out.println("Trainable (LoRA) params: " + lora.countTrainableParams());
        System.out.println("Parameter reduction: " + (100 * lora.countTrainableParams() / (double) lora.countOriginalParams()) + "%");
        double[][] adaptedBefore = lora.getAdaptedWeights();
        System.out.println("Adapted weight[0][0] before: " + adaptedBefore[0][0]);
        double[][] input = {{1, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
        double[][] gradOutput = {{0.1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
        lora.fineTuneStep(input, gradOutput, 0.01);
        double[][] adaptedAfter = lora.getAdaptedWeights();
        System.out.println("Adapted weight[0][0] after fine-tune: " + adaptedAfter[0][0]);
    }
}

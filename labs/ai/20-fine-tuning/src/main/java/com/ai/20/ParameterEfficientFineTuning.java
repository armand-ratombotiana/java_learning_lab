package com.ai20;

import java.util.*;

public class ParameterEfficientFineTuning {
    private double[] fullParams;
    private double[] adapterParams;
    private boolean[] frozen;
    private int adapterSize;

    public ParameterEfficientFineTuning(int totalParams, double adapterRatio) {
        this.fullParams = new double[totalParams];
        this.frozen = new boolean[totalParams];
        java.util.Random rng = new java.util.Random(42);
        for (int i = 0; i < totalParams; i++)
            fullParams[i] = rng.nextDouble() * 0.1 - 0.05;
        this.adapterSize = (int) (totalParams * adapterRatio);
        this.adapterParams = new double[adapterSize];
        Set<Integer> adapterIndices = new HashSet<>();
        while (adapterIndices.size() < adapterSize)
            adapterIndices.add(rng.nextInt(totalParams));
        for (int idx : adapterIndices) {
            frozen[idx] = false;
        }
        for (int i = 0; i < totalParams; i++)
            if (frozen[i]) frozen[i] = true;
    }

    public double computeOutput(double[] input) {
        double sum = 0;
        for (int i = 0; i < Math.min(input.length, fullParams.length); i++)
            sum += input[i] * fullParams[i];
        return sum;
    }

    public void fineTune(double[][] X, double[] y, int epochs, double learningRate) {
        int n = X.length;
        for (int epoch = 0; epoch < epochs; epoch++) {
            double loss = 0;
            for (int s = 0; s < n; s++) {
                double pred = computeOutput(X[s]);
                double error = pred - y[s];
                loss += error * error;
                for (int i = 0; i < Math.min(X[s].length, fullParams.length); i++) {
                    if (!frozen[i])
                        fullParams[i] -= learningRate * error * X[s][i];
                }
            }
            if (epoch % 10 == 0)
                System.out.println("Epoch " + epoch + " loss: " + (loss / n));
        }
    }

    public int getTrainableParams() {
        int count = 0;
        for (boolean f : frozen) if (!f) count++;
        return count;
    }

    public int getTotalParams() { return fullParams.length; }

    public double computeMemorySavings() {
        return (1.0 - getTrainableParams() / (double) getTotalParams()) * 100;
    }

    public static void main(String[] args) {
        System.out.println("=== Parameter-Efficient Fine-Tuning Demo ===");
        ParameterEfficientFineTuning peft = new ParameterEfficientFineTuning(100, 0.1);
        System.out.println("Total params: " + peft.getTotalParams());
        System.out.println("Trainable params: " + peft.getTrainableParams());
        System.out.println("Memory savings: " + peft.computeMemorySavings() + "%");
        double[][] X = new double[10][100];
        double[] y = new double[10];
        Random rng = new Random(42);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 100; j++) X[i][j] = rng.nextDouble();
            y[i] = rng.nextDouble();
        }
        peft.fineTune(X, y, 30, 0.01);
        System.out.println("Sample prediction: " + peft.computeOutput(X[0]));
    }
}

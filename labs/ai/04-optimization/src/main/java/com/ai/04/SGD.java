package com.ai04;

import java.util.*;

public class SGD {
    private double[] weights;
    private double learningRate;
    private int maxEpochs;
    private double lambda;

    public SGD(double learningRate, int maxEpochs, double lambda) {
        this.learningRate = learningRate;
        this.maxEpochs = maxEpochs;
        this.lambda = lambda;
    }

    public void fit(double[][] X, double[] y) {
        int m = X[0].length;
        int n = X.length;
        weights = new double[m];
        Random rng = new Random(42);
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < n; i++) indices.add(i);

        for (int epoch = 0; epoch < maxEpochs; epoch++) {
            Collections.shuffle(indices, rng);
            double epochLoss = 0;
            for (int idx : indices) {
                double[] x = X[idx];
                double prediction = predict(x);
                double error = prediction - y[idx];
                epochLoss += error * error;
                for (int j = 0; j < m; j++) {
                    double gradient = error * x[j] + lambda * weights[j];
                    weights[j] -= learningRate * gradient;
                }
            }
            if (epoch % 10 == 0)
                System.out.println("Epoch " + epoch + " loss: " + (epochLoss / n));
        }
    }

    public double predict(double[] x) {
        double sum = 0;
        for (int i = 0; i < weights.length; i++)
            sum += weights[i] * x[i];
        return sum;
    }

    public static void main(String[] args) {
        System.out.println("=== SGD Demo ===");
        double[][] X = {{1, 1}, {1, 2}, {1, 3}, {1, 4}, {1, 5}};
        double[] y = {3, 5, 7, 9, 11};
        SGD sgd = new SGD(0.01, 50, 0.001);
        sgd.fit(X, y);
        System.out.println("Weights: " + java.util.Arrays.toString(sgd.weights));
        System.out.println("Prediction for [1,6]: " + sgd.predict(new double[]{1, 6}));
    }
}

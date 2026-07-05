package com.ai11;

public class Perceptron {
    private double[] weights;
    private double bias;
    private double learningRate;
    private int maxEpochs;

    public Perceptron(double learningRate, int maxEpochs) {
        this.learningRate = learningRate;
        this.maxEpochs = maxEpochs;
    }

    public void fit(double[][] X, int[] y) {
        int m = X[0].length;
        int n = X.length;
        weights = new double[m];
        bias = 0;
        java.util.Random rng = new java.util.Random(42);
        for (int i = 0; i < m; i++) weights[i] = rng.nextDouble() * 0.1 - 0.05;

        for (int epoch = 0; epoch < maxEpochs; epoch++) {
            int errors = 0;
            for (int i = 0; i < n; i++) {
                int prediction = predict(X[i]);
                int error = y[i] - prediction;
                if (error != 0) {
                    errors++;
                    for (int j = 0; j < m; j++)
                        weights[j] += learningRate * error * X[i][j];
                    bias += learningRate * error;
                }
            }
            if (errors == 0) break;
        }
    }

    public int predict(double[] x) {
        double sum = bias;
        for (int i = 0; i < weights.length; i++)
            sum += weights[i] * x[i];
        return sum >= 0 ? 1 : 0;
    }

    public double[] getWeights() { return weights; }

    public static void main(String[] args) {
        System.out.println("=== Perceptron Demo ===");
        double[][] X = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        int[] y = {0, 0, 0, 1};
        Perceptron p = new Perceptron(0.1, 100);
        p.fit(X, y);
        System.out.println("Weights: " + java.util.Arrays.toString(p.getWeights()));
        for (int i = 0; i < X.length; i++)
            System.out.println("x=" + java.util.Arrays.toString(X[i]) + " -> " + p.predict(X[i]));
    }
}

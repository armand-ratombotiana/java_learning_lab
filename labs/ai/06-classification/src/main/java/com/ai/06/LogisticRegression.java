package com.ai06;

public class LogisticRegression {
    private double[] weights;
    private double bias;
    private double learningRate;
    private int maxIterations;

    public LogisticRegression(double learningRate, int maxIterations) {
        this.learningRate = learningRate;
        this.maxIterations = maxIterations;
    }

    private double sigmoid(double z) {
        return 1 / (1 + Math.exp(-z));
    }

    public void fit(double[][] X, int[] y) {
        int n = X.length;
        int m = X[0].length;
        weights = new double[m];
        bias = 0;
        for (int iter = 0; iter < maxIterations; iter++) {
            double[] dw = new double[m];
            double db = 0;
            double loss = 0;
            for (int i = 0; i < n; i++) {
                double z = bias;
                for (int j = 0; j < m; j++) z += weights[j] * X[i][j];
                double pred = sigmoid(z);
                loss += -y[i] * Math.log(Math.max(pred, 1e-15)) - (1 - y[i]) * Math.log(Math.max(1 - pred, 1e-15));
                double dz = pred - y[i];
                for (int j = 0; j < m; j++) dw[j] += dz * X[i][j];
                db += dz;
            }
            for (int j = 0; j < m; j++) weights[j] -= learningRate * dw[j] / n;
            bias -= learningRate * db / n;
            if (iter % 100 == 0)
                System.out.println("Iter " + iter + " loss: " + (loss / n));
        }
    }

    public double predictProbability(double[] x) {
        double z = bias;
        for (int i = 0; i < weights.length; i++)
            z += weights[i] * x[i];
        return sigmoid(z);
    }

    public int predict(double[] x) {
        return predictProbability(x) >= 0.5 ? 1 : 0;
    }

    public static void main(String[] args) {
        System.out.println("=== Logistic Regression Demo ===");
        double[][] X = {{2.0, 1.0}, {3.0, 2.0}, {1.0, 3.0}, {6.0, 5.0}, {7.0, 6.0}, {8.0, 5.0}};
        int[] y = {0, 0, 0, 1, 1, 1};
        LogisticRegression lr = new LogisticRegression(0.1, 1000);
        lr.fit(X, y);
        double[][] test = {{1.5, 2.0}, {7.0, 5.5}};
        for (double[] t : test)
            System.out.println("Test " + java.util.Arrays.toString(t) + " -> prob=" + lr.predictProbability(t) + " class=" + lr.predict(t));
    }
}

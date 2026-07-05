package com.ai04;

public class GradientDescent {
    private double[] weights;
    private double learningRate;
    private int maxIterations;
    private double tolerance;

    public GradientDescent(double learningRate, int maxIterations, double tolerance) {
        this.learningRate = learningRate;
        this.maxIterations = maxIterations;
        this.tolerance = tolerance;
    }

    public double[] fit(double[][] X, double[] y) {
        int n = X.length;
        int m = X[0].length;
        weights = new double[m];
        for (int iter = 0; iter < maxIterations; iter++) {
            double[] gradients = new double[m];
            double loss = 0;
            for (int i = 0; i < n; i++) {
                double prediction = predict(X[i]);
                double error = prediction - y[i];
                loss += error * error;
                for (int j = 0; j < m; j++)
                    gradients[j] += error * X[i][j];
            }
            loss /= (2 * n);
            for (int j = 0; j < m; j++)
                gradients[j] /= n;
            boolean converged = true;
            for (int j = 0; j < m; j++) {
                double update = learningRate * gradients[j];
                weights[j] -= update;
                if (Math.abs(update) > tolerance) converged = false;
            }
            if (iter % 100 == 0)
                System.out.println("Iter " + iter + " loss: " + loss);
            if (converged) break;
        }
        return weights;
    }

    public double predict(double[] x) {
        double sum = 0;
        for (int i = 0; i < weights.length; i++)
            sum += weights[i] * x[i];
        return sum;
    }

    public static void main(String[] args) {
        System.out.println("=== Gradient Descent Demo ===");
        double[][] X = {{1, 1}, {1, 2}, {1, 3}, {1, 4}};
        double[] y = {2, 4, 6, 8};
        GradientDescent gd = new GradientDescent(0.01, 1000, 1e-6);
        double[] w = gd.fit(X, y);
        System.out.println("Weights: " + java.util.Arrays.toString(w));
        System.out.println("Prediction for [1,5]: " + gd.predict(new double[]{1, 5}));
    }
}

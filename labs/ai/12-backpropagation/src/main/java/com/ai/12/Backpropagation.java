package com.ai12;

public class Backpropagation {
    private double[] weights;
    private double learningRate;

    public Backpropagation(int numWeights, double learningRate) {
        this.learningRate = learningRate;
        this.weights = new double[numWeights];
        java.util.Random rng = new java.util.Random(42);
        for (int i = 0; i < numWeights; i++)
            weights[i] = rng.nextDouble() * 0.1 - 0.05;
    }

    public double forwardSingle(double x) {
        return weights[0] * x + (weights.length > 1 ? weights[1] : 0);
    }

    public double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public double forwardNet(double[] x) {
        double hidden = 0;
        for (int i = 0; i < x.length; i++)
            hidden += weights[i] * x[i];
        return sigmoid(hidden);
    }

    public double[] backward(double[] x, double target) {
        double output = forwardNet(x);
        double error = output - target;
        double dZ = error * output * (1 - output);
        double[] gradients = new double[weights.length];
        for (int i = 0; i < weights.length; i++)
            gradients[i] = dZ * x[i];
        return gradients;
    }

    public void updateWeights(double[] gradients) {
        for (int i = 0; i < weights.length; i++)
            weights[i] -= learningRate * gradients[i];
    }

    public void train(double[][] X, double[] y, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            double loss = 0;
            for (int i = 0; i < X.length; i++) {
                double pred = forwardNet(X[i]);
                loss += (pred - y[i]) * (pred - y[i]);
                double[] grads = backward(X[i], y[i]);
                updateWeights(grads);
            }
            if (epoch % 100 == 0)
                System.out.println("Epoch " + epoch + " loss: " + (loss / X.length));
        }
    }

    public double[] getWeights() { return weights; }

    public static void main(String[] args) {
        System.out.println("=== Backpropagation Demo ===");
        double[][] X = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        double[] y = {0, 1, 1, 0};
        Backpropagation bp = new Backpropagation(2, 0.5);
        bp.train(X, y, 500);
        for (int i = 0; i < X.length; i++)
            System.out.println("x=" + java.util.Arrays.toString(X[i]) + " -> " + bp.forwardNet(X[i]));
    }
}

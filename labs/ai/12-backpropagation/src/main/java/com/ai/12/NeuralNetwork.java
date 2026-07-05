package com.ai12;

public class NeuralNetwork {
    private double[][] W1, W2;
    private double[] b1, b2;
    private double learningRate;

    public NeuralNetwork(int inputSize, int hiddenSize, int outputSize, double learningRate) {
        this.learningRate = learningRate;
        java.util.Random rng = new java.util.Random(42);
        W1 = new double[inputSize][hiddenSize];
        b1 = new double[hiddenSize];
        W2 = new double[hiddenSize][outputSize];
        b2 = new double[outputSize];
        for (int i = 0; i < inputSize; i++)
            for (int j = 0; j < hiddenSize; j++)
                W1[i][j] = rng.nextDouble() * 0.1 - 0.05;
        for (int i = 0; i < hiddenSize; i++)
            for (int j = 0; j < outputSize; j++)
                W2[i][j] = rng.nextDouble() * 0.1 - 0.05;
    }

    private double sigmoid(double x) { return 1 / (1 + Math.exp(-x)); }
    private double sigmoidDeriv(double x) { double s = sigmoid(x); return s * (1 - s); }

    public double[] forward(double[] input) {
        double[] hidden = new double[b1.length];
        for (int j = 0; j < hidden.length; j++) {
            double sum = b1[j];
            for (int i = 0; i < input.length; i++)
                sum += input[i] * W1[i][j];
            hidden[j] = sigmoid(sum);
        }
        double[] output = new double[b2.length];
        for (int j = 0; j < output.length; j++) {
            double sum = b2[j];
            for (int i = 0; i < hidden.length; i++)
                sum += hidden[i] * W2[i][j];
            output[j] = sigmoid(sum);
        }
        return output;
    }

    public void train(double[][] X, double[][] y, int epochs) {
        int n = X.length;
        for (int epoch = 0; epoch < epochs; epoch++) {
            double loss = 0;
            for (int s = 0; s < n; s++) {
                double[] hidden = new double[b1.length];
                double[] hiddenRaw = new double[b1.length];
                for (int j = 0; j < hidden.length; j++) {
                    double sum = b1[j];
                    for (int i = 0; i < X[0].length; i++)
                        sum += X[s][i] * W1[i][j];
                    hiddenRaw[j] = sum;
                    hidden[j] = sigmoid(sum);
                }
                double[] output = new double[b2.length];
                double[] outputRaw = new double[b2.length];
                for (int j = 0; j < output.length; j++) {
                    double sum = b2[j];
                    for (int i = 0; i < hidden.length; i++)
                        sum += hidden[i] * W2[i][j];
                    outputRaw[j] = sum;
                    output[j] = sigmoid(sum);
                }
                for (int j = 0; j < output.length; j++)
                    loss += (output[j] - y[s][j]) * (output[j] - y[s][j]);
                double[] dZ2 = new double[output.length];
                for (int j = 0; j < output.length; j++)
                    dZ2[j] = (output[j] - y[s][j]) * sigmoidDeriv(outputRaw[j]);
                double[] dH = new double[hidden.length];
                for (int i = 0; i < hidden.length; i++) {
                    double error = 0;
                    for (int j = 0; j < output.length; j++)
                        error += W2[i][j] * dZ2[j];
                    dH[i] = error * sigmoidDeriv(hiddenRaw[i]);
                }
                for (int i = 0; i < hidden.length; i++)
                    for (int j = 0; j < output.length; j++)
                        W2[i][j] -= learningRate * hidden[i] * dZ2[j];
                for (int j = 0; j < output.length; j++)
                    b2[j] -= learningRate * dZ2[j];
                for (int i = 0; i < X[0].length; i++)
                    for (int j = 0; j < hidden.length; j++)
                        W1[i][j] -= learningRate * X[s][i] * dH[j];
                for (int j = 0; j < hidden.length; j++)
                    b1[j] -= learningRate * dH[j];
            }
            if (epoch % 100 == 0)
                System.out.println("Epoch " + epoch + " loss: " + (loss / n));
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Neural Network with Backpropagation Demo ===");
        NeuralNetwork nn = new NeuralNetwork(2, 4, 1, 0.5);
        double[][] X = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        double[][] y = {{0}, {1}, {1}, {0}};
        nn.train(X, y, 1000);
        for (int i = 0; i < X.length; i++)
            System.out.println("x=" + java.util.Arrays.toString(X[i]) + " -> " + java.util.Arrays.toString(nn.forward(X[i])));
    }
}

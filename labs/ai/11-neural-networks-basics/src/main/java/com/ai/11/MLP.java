package com.ai11;

public class MLP {
    private int[] layerSizes;
    private double[][][] weights;
    private double[][] biases;
    private double learningRate;

    public MLP(int[] layerSizes, double learningRate) {
        this.layerSizes = layerSizes;
        this.learningRate = learningRate;
        java.util.Random rng = new java.util.Random(42);
        weights = new double[layerSizes.length - 1][][];
        biases = new double[layerSizes.length - 1][];
        for (int l = 0; l < layerSizes.length - 1; l++) {
            weights[l] = new double[layerSizes[l]][layerSizes[l + 1]];
            biases[l] = new double[layerSizes[l + 1]];
            for (int i = 0; i < layerSizes[l]; i++)
                for (int j = 0; j < layerSizes[l + 1]; j++)
                    weights[l][i][j] = rng.nextDouble() * 0.1 - 0.05;
        }
    }

    public double[] forward(double[] input) {
        double[] current = input.clone();
        for (int l = 0; l < weights.length; l++) {
            double[] next = new double[layerSizes[l + 1]];
            for (int j = 0; j < layerSizes[l + 1]; j++) {
                double sum = biases[l][j];
                for (int i = 0; i < layerSizes[l]; i++)
                    sum += current[i] * weights[l][i][j];
                next[j] = ActivationFunctions.tanh(sum);
            }
            current = next;
        }
        return current;
    }

    public void train(double[][] X, double[][] y, int epochs) {
        int n = X.length;
        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0;
            for (int sample = 0; sample < n; sample++) {
                double[][] activations = new double[layerSizes.length][];
                activations[0] = X[sample].clone();
                for (int l = 1; l < layerSizes.length; l++) {
                    activations[l] = new double[layerSizes[l]];
                    for (int j = 0; j < layerSizes[l]; j++) {
                        double sum = biases[l - 1][j];
                        for (int i = 0; i < layerSizes[l - 1]; i++)
                            sum += activations[l - 1][i] * weights[l - 1][i][j];
                        activations[l][j] = ActivationFunctions.tanh(sum);
                    }
                }
                double[] output = activations[layerSizes.length - 1];
                double[] deltas = new double[output.length];
                for (int j = 0; j < output.length; j++) {
                    double err = output[j] - y[sample][j];
                    totalLoss += err * err;
                    deltas[j] = err * ActivationFunctions.tanhDerivative(output[j]);
                }
                for (int l = layerSizes.length - 2; l >= 0; l--) {
                    double[] prevDeltas = new double[layerSizes[l]];
                    for (int i = 0; i < layerSizes[l]; i++) {
                        double error = 0;
                        for (int j = 0; j < layerSizes[l + 1]; j++)
                            error += weights[l][i][j] * deltas[j];
                        prevDeltas[i] = error * ActivationFunctions.tanhDerivative(activations[l][i]);
                        for (int j = 0; j < layerSizes[l + 1]; j++)
                            weights[l][i][j] -= learningRate * activations[l][i] * deltas[j];
                    }
                    for (int j = 0; j < layerSizes[l + 1]; j++)
                        biases[l][j] -= learningRate * deltas[j];
                    deltas = prevDeltas;
                }
            }
            if (epoch % 100 == 0)
                System.out.println("Epoch " + epoch + " loss: " + (totalLoss / n));
        }
    }

    public static void main(String[] args) {
        System.out.println("=== MLP Demo (XOR) ===");
        MLP mlp = new MLP(new int[]{2, 4, 1}, 0.1);
        double[][] X = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        double[][] y = {{0}, {1}, {1}, {0}};
        mlp.train(X, y, 1000);
        for (int i = 0; i < X.length; i++)
            System.out.println("x=" + java.util.Arrays.toString(X[i]) + " -> " + java.util.Arrays.toString(mlp.forward(X[i])));
    }
}

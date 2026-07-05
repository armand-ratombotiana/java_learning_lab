package com.ai11;

public class ActivationFunctions {

    public static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public static double sigmoidDerivative(double x) {
        double s = sigmoid(x);
        return s * (1 - s);
    }

    public static double relu(double x) {
        return Math.max(0, x);
    }

    public static double reluDerivative(double x) {
        return x > 0 ? 1 : 0;
    }

    public static double tanh(double x) {
        return Math.tanh(x);
    }

    public static double tanhDerivative(double x) {
        double t = tanh(x);
        return 1 - t * t;
    }

    public static double leakyRelu(double x, double alpha) {
        return x > 0 ? x : alpha * x;
    }

    public static double leakyReluDerivative(double x, double alpha) {
        return x > 0 ? 1 : alpha;
    }

    public static double[] softmax(double[] x) {
        double max = x[0];
        for (double v : x) max = Math.max(max, v);
        double[] result = new double[x.length];
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            result[i] = Math.exp(x[i] - max);
            sum += result[i];
        }
        for (int i = 0; i < x.length; i++)
            result[i] /= sum;
        return result;
    }

    public static void main(String[] args) {
        System.out.println("=== Activation Functions Demo ===");
        double[] inputs = {-3, -2, -1, 0, 1, 2, 3};
        System.out.println("x\tsigmoid\trelu\ttanh");
        for (double x : inputs)
            System.out.printf("%.0f\t%.4f\t%.4f\t%.4f%n", x, sigmoid(x), relu(x), tanh(x));
        double[] logits = {2.0, 1.0, 0.1};
        double[] probs = softmax(logits);
        System.out.println("Softmax of " + java.util.Arrays.toString(logits) + ": " + java.util.Arrays.toString(probs));
    }
}

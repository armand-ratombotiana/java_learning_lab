package com.ai14;

public class SimpleRNN {
    private double[][] Wxh, Whh;
    private double[] bh, Why;
    private double by;
    private int inputSize, hiddenSize, outputSize;
    private double[] h;

    public SimpleRNN(int inputSize, int hiddenSize, int outputSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        java.util.Random rng = new java.util.Random(42);
        Wxh = new double[inputSize][hiddenSize];
        Whh = new double[hiddenSize][hiddenSize];
        bh = new double[hiddenSize];
        Why = new double[hiddenSize];
        by = 0;
        h = new double[hiddenSize];
        for (int i = 0; i < inputSize; i++)
            for (int j = 0; j < hiddenSize; j++)
                Wxh[i][j] = rng.nextDouble() * 0.1 - 0.05;
        for (int i = 0; i < hiddenSize; i++)
            for (int j = 0; j < hiddenSize; j++)
                Whh[i][j] = rng.nextDouble() * 0.1 - 0.05;
        for (int i = 0; i < hiddenSize; i++)
            Why[i] = rng.nextDouble() * 0.1 - 0.05;
    }

    public void resetState() {
        h = new double[hiddenSize];
    }

    public double step(double[] x) {
        double[] newH = new double[hiddenSize];
        for (int j = 0; j < hiddenSize; j++) {
            double sum = bh[j];
            for (int i = 0; i < inputSize; i++)
                sum += x[i] * Wxh[i][j];
            for (int i = 0; i < hiddenSize; i++)
                sum += h[i] * Whh[i][j];
            newH[j] = Math.tanh(sum);
        }
        h = newH;
        double output = by;
        for (int i = 0; i < hiddenSize; i++)
            output += h[i] * Why[i];
        return output;
    }

    public double[] getHiddenState() { return h; }

    public static void main(String[] args) {
        System.out.println("=== Simple RNN Demo ===");
        SimpleRNN rnn = new SimpleRNN(1, 4, 1);
        double[] seq = {0.1, 0.2, 0.3, 0.4, 0.5};
        System.out.println("Processing sequence: " + java.util.Arrays.toString(seq));
        for (int t = 0; t < seq.length; t++) {
            double output = rnn.step(new double[]{seq[t]});
            System.out.println("t=" + t + " input=" + seq[t] + " output=" + output + " hidden=" + java.util.Arrays.toString(rnn.getHiddenState()));
        }
    }
}

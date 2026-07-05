package com.ai14;

public class SequenceProcessor {
    private SimpleRNN rnn;

    public SequenceProcessor(int inputSize, int hiddenSize) {
        this.rnn = new SimpleRNN(inputSize, hiddenSize, 1);
    }

    public double[] processSequence(double[][] sequence) {
        rnn.resetState();
        int T = sequence.length;
        double[] outputs = new double[T];
        for (int t = 0; t < T; t++)
            outputs[t] = rnn.step(sequence[t]);
        return outputs;
    }

    public double[] lastHidden(double[][] sequence) {
        rnn.resetState();
        for (double[] x : sequence) rnn.step(x);
        return rnn.getHiddenState();
    }

    public static double[] generateSineData(int length) {
        double[] data = new double[length];
        for (int i = 0; i < length; i++)
            data[i] = Math.sin(i * 0.1);
        return data;
    }

    public static void main(String[] args) {
        System.out.println("=== Sequence Processing Demo ===");
        double[] data = generateSineData(10);
        System.out.println("Sine data: " + java.util.Arrays.toString(data));
        double[][] seq = new double[data.length][1];
        for (int i = 0; i < data.length; i++) seq[i][0] = data[i];
        SequenceProcessor sp = new SequenceProcessor(1, 4);
        double[] outputs = sp.processSequence(seq);
        System.out.println("RNN outputs: " + java.util.Arrays.toString(outputs));
        double[] h = sp.lastHidden(seq);
        System.out.println("Final hidden state: " + java.util.Arrays.toString(h));
    }
}

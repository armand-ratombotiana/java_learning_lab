package com.ai14;

public class LSTMCell {
    private double[] h, c;
    private double[][] Wf, Wi, Wo, Wc;
    private double[] bf, bi, bo, bc;
    private int inputSize, hiddenSize;

    public LSTMCell(int inputSize, int hiddenSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        java.util.Random rng = new java.util.Random(42);
        Wf = new double[inputSize + hiddenSize][hiddenSize];
        Wi = new double[inputSize + hiddenSize][hiddenSize];
        Wo = new double[inputSize + hiddenSize][hiddenSize];
        Wc = new double[inputSize + hiddenSize][hiddenSize];
        bf = new double[hiddenSize];
        bi = new double[hiddenSize];
        bo = new double[hiddenSize];
        bc = new double[hiddenSize];
        h = new double[hiddenSize];
        c = new double[hiddenSize];
        for (int i = 0; i < inputSize + hiddenSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                Wf[i][j] = rng.nextDouble() * 0.1 - 0.05;
                Wi[i][j] = rng.nextDouble() * 0.1 - 0.05;
                Wo[i][j] = rng.nextDouble() * 0.1 - 0.05;
                Wc[i][j] = rng.nextDouble() * 0.1 - 0.05;
            }
        }
    }

    public void reset() {
        h = new double[hiddenSize];
        c = new double[hiddenSize];
    }

    private double sigmoid(double x) { return 1 / (1 + Math.exp(-x)); }

    public double[] step(double[] x) {
        double[] combined = new double[inputSize + hiddenSize];
        System.arraycopy(x, 0, combined, 0, inputSize);
        System.arraycopy(h, 0, combined, inputSize, hiddenSize);

        double[] f = new double[hiddenSize];
        double[] ig = new double[hiddenSize];
        double[] o = new double[hiddenSize];
        double[] cTilde = new double[hiddenSize];

        for (int j = 0; j < hiddenSize; j++) {
            double sumF = bf[j], sumI = bi[j], sumO = bo[j], sumC = bc[j];
            for (int i = 0; i < inputSize + hiddenSize; i++) {
                sumF += combined[i] * Wf[i][j];
                sumI += combined[i] * Wi[i][j];
                sumO += combined[i] * Wo[i][j];
                sumC += combined[i] * Wc[i][j];
            }
            f[j] = sigmoid(sumF);
            ig[j] = sigmoid(sumI);
            o[j] = sigmoid(sumO);
            cTilde[j] = Math.tanh(sumC);
        }

        for (int j = 0; j < hiddenSize; j++) {
            c[j] = f[j] * c[j] + ig[j] * cTilde[j];
            h[j] = o[j] * Math.tanh(c[j]);
        }
        return h.clone();
    }

    public double[] getH() { return h; }
    public double[] getC() { return c; }

    public static void main(String[] args) {
        System.out.println("=== LSTM Cell Demo ===");
        LSTMCell lstm = new LSTMCell(1, 3);
        double[] seq = {0.1, 0.2, 0.3, 0.4, 0.5};
        for (int t = 0; t < seq.length; t++) {
            double[] hOut = lstm.step(new double[]{seq[t]});
            System.out.println("t=" + t + " input=" + seq[t] + " h=" + java.util.Arrays.toString(hOut));
        }
    }
}

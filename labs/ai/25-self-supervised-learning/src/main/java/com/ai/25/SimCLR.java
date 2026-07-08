package com.ai25;

import java.util.*;

public class SimCLR {
    private final Encoder encoder;
    private final ProjectionHead projection;
    private final Random random;
    private final int inputDim, projectionDim;
    private double temperature;

    public SimCLR(int inputDim, int hiddenDim, int projectionDim, double temperature) {
        this.inputDim = inputDim;
        this.projectionDim = projectionDim;
        this.temperature = temperature;
        this.random = new Random(42);
        this.encoder = new Encoder(inputDim, hiddenDim, projectionDim);
        this.projection = new ProjectionHead(projectionDim, projectionDim);
    }

    public double[] encode(double[] input) {
        return encoder.forward(input);
    }

    public double[] project(double[] encoded) {
        return projection.forward(encoded);
    }

    public double ntxentLoss(double[][] zis, double[][] zjs) {
        int n = zis.length;
        double loss = 0;
        for (int i = 0; i < n; i++) {
            double simPos = cosineSimilarity(zis[i], zjs[i]) / temperature;
            double sumExp = 0;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    sumExp += Math.exp(cosineSimilarity(zis[i], zjs[j]) / temperature);
                    sumExp += Math.exp(cosineSimilarity(zis[i], zis[j]) / temperature);
                }
            }
            loss += -simPos + Math.log(Math.max(sumExp + Math.exp(simPos), 1e-15));
        }
        return loss / n;
    }

    private double cosineSimilarity(double[] a, double[] b) {
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        return dot / (Math.sqrt(na) * Math.sqrt(nb) + 1e-15);
    }

    public double[][] augment(double[] input, int seed) {
        Random augRng = new Random(seed);
        double[] aug1 = new double[inputDim];
        double[] aug2 = new double[inputDim];
        for (int i = 0; i < inputDim; i++) {
            double noise1 = augRng.nextGaussian() * 0.1;
            double noise2 = augRng.nextGaussian() * 0.1;
            double mask1 = augRng.nextDouble() > 0.1 ? 1 : 0;
            double mask2 = augRng.nextDouble() > 0.1 ? 1 : 0;
            aug1[i] = input[i] * mask1 + noise1;
            aug2[i] = input[i] * mask2 + noise2;
        }
        return new double[][]{aug1, aug2};
    }

    public void train(double[][] data, int epochs, double learningRate) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            int n = data.length;
            double[][] zis = new double[n][projectionDim];
            double[][] zjs = new double[n][projectionDim];
            for (int i = 0; i < n; i++) {
                double[][] augmented = augment(data[i], epoch * n + i);
                zis[i] = project(encode(augmented[0]));
                zjs[i] = project(encode(augmented[1]));
            }
            double loss = ntxentLoss(zis, zjs);
            if (epoch % 200 == 0) System.out.printf("Epoch %d: Contrastive Loss = %.4f%n", epoch, loss);
        }
    }

    static class Encoder {
        double[][] w1, w2; double[] b1, b2;
        final int inputDim, hiddenDim, outputDim;

        Encoder(int id, int hd, int od) {
            inputDim = id; hiddenDim = hd; outputDim = od;
            Random r = new Random(42);
            w1 = new double[id][hd]; b1 = new double[hd];
            w2 = new double[hd][od]; b2 = new double[od];
            for (int i = 0; i < id; i++) for (int h = 0; h < hd; h++) w1[i][h] = r.nextGaussian() * 0.1;
            for (int h = 0; h < hd; h++) for (int o = 0; o < od; o++) w2[h][o] = r.nextGaussian() * 0.1;
        }

        double[] forward(double[] input) {
            double[] h = new double[hiddenDim];
            for (int j = 0; j < hiddenDim; j++) { double s = b1[j]; for (int i = 0; i < inputDim; i++) s += input[i] * w1[i][j]; h[j] = Math.max(0, s); }
            double[] out = new double[outputDim];
            for (int o = 0; o < outputDim; o++) { double s = b2[o]; for (int j = 0; j < hiddenDim; j++) s += h[j] * w2[j][o]; out[o] = Math.max(0, s); }
            return out;
        }
    }

    static class ProjectionHead {
        double[][] w; double[] b;
        final int inputDim, outputDim;

        ProjectionHead(int id, int od) {
            inputDim = id; outputDim = od;
            Random r = new Random(42);
            w = new double[id][od]; b = new double[od];
            for (int i = 0; i < id; i++) for (int o = 0; o < od; o++) w[i][o] = r.nextGaussian() * 0.1;
        }

        double[] forward(double[] input) {
            double[] out = new double[outputDim];
            for (int o = 0; o < outputDim; o++) { double s = b[o]; for (int i = 0; i < inputDim; i++) s += input[i] * w[i][o]; out[o] = s; }
            double norm = 0; for (double v : out) norm += v * v;
            norm = Math.sqrt(norm) + 1e-15;
            for (int o = 0; o < outputDim; o++) out[o] /= norm;
            return out;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== SimCLR Demo ===");
        SimCLR simclr = new SimCLR(10, 32, 16, 0.5);
        Random rng = new Random(42);
        double[][] data = new double[50][10];
        for (int s = 0; s < 50; s++) for (int i = 0; i < 10; i++) data[s][i] = rng.nextDouble();
        simclr.train(data, 1000, 0.01);
        double[] rep = simclr.encode(data[0]);
        System.out.print("Learned representation: ");
        for (double v : rep) System.out.printf("%.4f ", v);
        System.out.println();
    }
}

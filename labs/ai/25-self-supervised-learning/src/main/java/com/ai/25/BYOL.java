package com.ai25;

import java.util.*;

public class BYOL {
    private final OnlineNetwork online;
    private final TargetNetwork target;
    private final Predictor predictor;
    private final Random random;
    private final int inputDim, projectionDim;
    private double tau;

    public BYOL(int inputDim, int hiddenDim, int projectionDim, double tau) {
        this.inputDim = inputDim;
        this.projectionDim = projectionDim;
        this.tau = tau;
        this.random = new Random(42);
        this.online = new OnlineNetwork(inputDim, hiddenDim, projectionDim);
        this.target = new TargetNetwork(inputDim, hiddenDim, projectionDim);
        this.predictor = new Predictor(projectionDim, projectionDim);
    }

    public double[] encode(double[] input) {
        return online.encode(input);
    }

    public void updateTarget() {
        double[][] targetW1 = target.encoder.w1;
        double[][] onlineW1 = online.encoder.w1;
        for (int i = 0; i < targetW1.length; i++)
            for (int j = 0; j < targetW1[i].length; j++)
                targetW1[i][j] = tau * targetW1[i][j] + (1 - tau) * onlineW1[i][j];
    }

    public double loss(double[] projOnline, double[] projTarget) {
        double[] pred = predictor.forward(projOnline);
        double dot = 0, np = 0, nt = 0;
        for (int i = 0; i < pred.length; i++) {
            dot += pred[i] * projTarget[i];
            np += pred[i] * pred[i];
            nt += projTarget[i] * projTarget[i];
        }
        return 2 - 2 * dot / (Math.sqrt(np) * Math.sqrt(nt) + 1e-15);
    }

    public void train(double[][] data, int epochs, double lr) {
        for (int ep = 0; ep < epochs; ep++) {
            double totalLoss = 0;
            for (double[] input : data) {
                Random augRng = new Random(ep * data.length + Arrays.hashCode(input));
                double[] aug1 = new double[inputDim];
                double[] aug2 = new double[inputDim];
                for (int i = 0; i < inputDim; i++) {
                    aug1[i] = input[i] + (augRng.nextDouble() > 0.2 ? 0 : -input[i]) + augRng.nextGaussian() * 0.05;
                    aug2[i] = input[i] + (augRng.nextDouble() > 0.2 ? 0 : -input[i]) + augRng.nextGaussian() * 0.05;
                }
                double[] p1 = online.project(aug1);
                double[] p2 = target.project(aug2);
                totalLoss += loss(p1, p2);
                updateTarget();
            }
            if (ep % 100 == 0) System.out.printf("BYOL Epoch %d: Loss = %.4f%n", ep, totalLoss / data.length);
        }
    }

    static class OnlineNetwork {
        SimCLR.Encoder encoder;
        SimCLR.ProjectionHead projector;
        OnlineNetwork(int id, int hd, int pd) {
            encoder = new SimCLR.Encoder(id, hd, pd);
            projector = new SimCLR.ProjectionHead(pd, pd);
        }
        double[] encode(double[] input) { return encoder.forward(input); }
        double[] project(double[] input) { return projector.forward(encode(input)); }
    }

    static class TargetNetwork {
        SimCLR.Encoder encoder;
        SimCLR.ProjectionHead projector;
        TargetNetwork(int id, int hd, int pd) {
            encoder = new SimCLR.Encoder(id, hd, pd);
            projector = new SimCLR.ProjectionHead(pd, pd);
        }
        double[] project(double[] input) { return projector.forward(encoder.forward(input)); }
    }

    static class Predictor {
        double[][] w; double[] b;
        final int inputDim, outputDim;
        Predictor(int id, int od) {
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
        System.out.println("=== BYOL Demo ===");
        Random rng = new Random(42);
        double[][] data = new double[30][10];
        for (int s = 0; s < 30; s++) for (int i = 0; i < 10; i++) data[s][i] = rng.nextDouble();
        BYOL byol = new BYOL(10, 32, 16, 0.99);
        byol.train(data, 500, 0.01);
        double[] rep = byol.encode(data[0]);
        System.out.print("Learned representation: "); for (double v : rep) System.out.printf("%.4f ", v);
        System.out.println();
    }
}

package com.ai22;

import java.util.*;

public class cGAN {
    private final ConditionalGenerator generator;
    private final ConditionalDiscriminator discriminator;
    private final Random random;
    private final int latentDim, conditionDim;

    public cGAN(int latentDim, int conditionDim, int dataDim, int hiddenSize) {
        this.latentDim = latentDim;
        this.conditionDim = conditionDim;
        this.random = new Random(42);
        this.generator = new ConditionalGenerator(latentDim + conditionDim, hiddenSize, dataDim);
        this.discriminator = new ConditionalDiscriminator(dataDim + conditionDim, hiddenSize);
    }

    public double[] generate(double[] latent, double[] condition) {
        double[] combined = new double[latent.length + condition.length];
        System.arraycopy(latent, 0, combined, 0, latent.length);
        System.arraycopy(condition, 0, combined, latent.length, condition.length);
        return generator.forward(combined);
    }

    public double[] sampleLatent() {
        double[] z = new double[latentDim];
        for (int i = 0; i < latentDim; i++) z[i] = random.nextGaussian();
        return z;
    }

    public void train(double[][] realData, double[][] conditions, int epochs, double lr) {
        for (int ep = 0; ep < epochs; ep++) {
            int idx = random.nextInt(realData.length);
            double[] real = realData[idx];
            double[] cond = conditions[idx];
            double[] noise = sampleLatent();
            double[] fake = generate(noise, cond);

            double[] realPair = new double[real.length + cond.length];
            System.arraycopy(real, 0, realPair, 0, real.length);
            System.arraycopy(cond, 0, realPair, real.length, cond.length);
            double[] fakePair = new double[fake.length + cond.length];
            System.arraycopy(fake, 0, fakePair, 0, fake.length);
            System.arraycopy(cond, 0, fakePair, fake.length, cond.length);

            discriminator.train(new double[][]{realPair}, new double[]{1.0},
                    new double[][]{fakePair}, new double[]{0.0}, lr);

            double[] combined = new double[noise.length + cond.length];
            System.arraycopy(noise, 0, combined, 0, noise.length);
            System.arraycopy(cond, 0, combined, noise.length, cond.length);
            generator.train(discriminator, combined, new double[]{1.0}, lr);

            if (ep % 500 == 0) {
                double[][] batchReal = {realPair};
                double[][] batchFake = {fakePair};
                double rScore = discriminator.forward(realPair)[0];
                double fScore = discriminator.forward(fakePair)[0];
                System.out.printf("cGAN Epoch %d: D(real)=%.4f D(fake)=%.4f%n", ep, rScore, fScore);
            }
        }
    }

    static class ConditionalGenerator {
        double[][] w1, w2; double[] b1, b2;
        final int inputDim, hiddenSize, dataDim;
        ConditionalGenerator(int id, int hs, int dd) {
            inputDim = id; hiddenSize = hs; dataDim = dd;
            Random r = new Random(42);
            w1 = new double[id][hs]; b1 = new double[hs];
            w2 = new double[hs][dd]; b2 = new double[dd];
            for (int i = 0; i < id; i++) for (int h = 0; h < hs; h++) w1[i][h] = r.nextGaussian() * 0.1;
            for (int h = 0; h < hs; h++) for (int d = 0; d < dd; d++) w2[h][d] = r.nextGaussian() * 0.1;
        }
        double[] forward(double[] input) {
            double[] h = new double[hiddenSize];
            for (int j = 0; j < hiddenSize; j++) { double s = b1[j]; for (int i = 0; i < inputDim; i++) s += input[i] * w1[i][j]; h[j] = Math.max(0, s); }
            double[] out = new double[dataDim];
            for (int d = 0; d < dataDim; d++) { double s = b2[d]; for (int j = 0; j < hiddenSize; j++) s += h[j] * w2[j][d]; out[d] = Math.tanh(s); }
            return out;
        }
        void train(ConditionalDiscriminator disc, double[] input, double[] target, double lr) {
            double[] h = new double[hiddenSize];
            for (int j = 0; j < hiddenSize; j++) { double s = b1[j]; for (int i = 0; i < inputDim; i++) s += input[i] * w1[i][j]; h[j] = Math.max(0, s); }
            double[] fake = new double[dataDim];
            for (int d = 0; d < dataDim; d++) { double s = b2[d]; for (int j = 0; j < hiddenSize; j++) s += h[j] * w2[j][d]; fake[d] = Math.tanh(s); }
            double[] condPart = Arrays.copyOfRange(input, inputDim - 1, inputDim);
            // simplified: just update generator weights opposite to discriminator gradient
            for (int j = 0; j < hiddenSize; j++) for (int d = 0; d < dataDim; d++) w2[j][d] += lr * h[j] * fake[d];
            for (int d = 0; d < dataDim; d++) b2[d] += lr * fake[d];
            for (int i = 0; i < inputDim; i++) for (int j = 0; j < hiddenSize; j++) w1[i][j] += lr * input[i] * h[j];
            for (int j = 0; j < hiddenSize; j++) b1[j] += lr * h[j];
        }
    }

    static class ConditionalDiscriminator {
        double[][] w1, w2; double[] b1, b2;
        final int inputDim, hiddenSize;
        ConditionalDiscriminator(int id, int hs) {
            inputDim = id; hiddenSize = hs;
            Random r = new Random(42);
            w1 = new double[id][hs]; b1 = new double[hs];
            w2 = new double[hs][1]; b2 = new double[1];
            for (int i = 0; i < id; i++) for (int h = 0; h < hs; h++) w1[i][h] = r.nextGaussian() * 0.1;
            for (int h = 0; h < hs; h++) w2[h][0] = r.nextGaussian() * 0.1;
        }
        double[] forward(double[] input) {
            double[] h = new double[hiddenSize];
            for (int j = 0; j < hiddenSize; j++) { double s = b1[j]; for (int i = 0; i < inputDim; i++) s += input[i] * w1[i][j]; h[j] = Math.max(0, s); }
            double s = b2[0]; for (int j = 0; j < hiddenSize; j++) s += h[j] * w2[j][0];
            return new double[]{1.0 / (1.0 + Math.exp(-s))};
        }
        void train(double[][] real, double[] rl, double[][] fake, double[] fl, double lr) {
            for (int s = 0; s < real.length; s++) {
                double[] h = new double[hiddenSize];
                for (int j = 0; j < hiddenSize; j++) { double v = b1[j]; for (int i = 0; i < inputDim; i++) v += real[s][i] * w1[i][j]; h[j] = Math.max(0, v); }
                double pred = b2[0]; for (int j = 0; j < hiddenSize; j++) pred += h[j] * w2[j][0];
                double prob = 1.0/(1.0+Math.exp(-pred));
                double d = (prob - rl[s]);
                for (int j = 0; j < hiddenSize; j++) { w2[j][0] -= lr * h[j] * d; if (h[j] > 0) { for (int i = 0; i < inputDim; i++) w1[i][j] -= lr * real[s][i] * w2[j][0] * d; b1[j] -= lr * w2[j][0] * d; } }
                b2[0] -= lr * d;
            }
            for (int s = 0; s < fake.length; s++) {
                double[] h = new double[hiddenSize];
                for (int j = 0; j < hiddenSize; j++) { double v = b1[j]; for (int i = 0; i < inputDim; i++) v += fake[s][i] * w1[i][j]; h[j] = Math.max(0, v); }
                double pred = b2[0]; for (int j = 0; j < hiddenSize; j++) pred += h[j] * w2[j][0];
                double prob = 1.0/(1.0+Math.exp(-pred));
                double d = (prob - fl[s]);
                for (int j = 0; j < hiddenSize; j++) { w2[j][0] -= lr * h[j] * d; if (h[j] > 0) { for (int i = 0; i < inputDim; i++) w1[i][j] -= lr * fake[s][i] * w2[j][0] * d; b1[j] -= lr * w2[j][0] * d; } }
                b2[0] -= lr * d;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Conditional GAN Demo ===");
        Random rng = new Random(42);
        int n = 100;
        double[][] data = new double[n][3];
        double[][] conds = new double[n][2];
        for (int i = 0; i < n; i++) {
            conds[i][0] = rng.nextDouble();
            conds[i][1] = rng.nextDouble();
            data[i][0] = conds[i][0] * 2 + 0.1 * rng.nextGaussian();
            data[i][1] = conds[i][1] * 3 + 0.1 * rng.nextGaussian();
            data[i][2] = conds[i][0] + conds[i][1] + 0.1 * rng.nextGaussian();
        }
        cGAN cgan = new cGAN(5, 2, 3, 16);
        cgan.train(data, conds, 2000, 0.01);
        double[] testCond = {0.7, 0.3};
        double[] gen = cgan.generate(cgan.sampleLatent(), testCond);
        System.out.print("Generated with condition [0.7,0.3]: ");
        for (double v : gen) System.out.printf("%.4f ", v);
        System.out.println();
    }
}

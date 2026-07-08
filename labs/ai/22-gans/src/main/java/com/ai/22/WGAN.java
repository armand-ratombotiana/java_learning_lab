package com.ai22;

import java.util.*;

public class WGAN {
    private final Generator generator;
    private final Critic critic;
    private final Random random;
    private final int latentDim;
    private final double clipValue;

    public WGAN(int latentDim, int dataDim, int hiddenSize, double clipValue) {
        this.latentDim = latentDim;
        this.clipValue = clipValue;
        this.random = new Random(42);
        this.generator = new Generator(latentDim, hiddenSize, dataDim);
        this.critic = new Critic(dataDim, hiddenSize);
    }

    public double[] generate(double[] latent) {
        return generator.forward(latent);
    }

    public double[] sampleLatent() {
        double[] z = new double[latentDim];
        for (int i = 0; i < latentDim; i++) z[i] = random.nextGaussian();
        return z;
    }

    public void train(double[][] realData, int epochs, int nCritic, double learningRate) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int k = 0; k < nCritic; k++) {
                int idx = random.nextInt(realData.length);
                double[] realSample = realData[idx];
                double[] noise = sampleLatent();
                double[] fakeSample = generator.forward(noise);

                double criticLoss = critic.trainStep(realSample, fakeSample, learningRate);
                critic.clipWeights(clipValue);
            }

            double[] noise = sampleLatent();
            generator.train(critic, noise, learningRate);

            if (epoch % 500 == 0) {
                double[] testNoise = sampleLatent();
                double[] testFake = generator.forward(testNoise);
                double criticReal = critic.forward(realData[random.nextInt(realData.length)]);
                double criticFake = critic.forward(testFake);
                double wassersteinDist = criticReal - criticFake;
                System.out.printf("Epoch %d: Wasserstein Distance = %.4f%n", epoch, wassersteinDist);
            }
        }
    }

    static class Generator {
        double[][] w1, w2; double[] b1, b2;
        final int latentDim, hiddenSize, dataDim;

        Generator(int ld, int hs, int dd) {
            latentDim = ld; hiddenSize = hs; dataDim = dd;
            Random r = new Random(42);
            w1 = new double[ld][hs]; b1 = new double[hs];
            w2 = new double[hs][dd]; b2 = new double[dd];
            for (int i = 0; i < ld; i++) for (int h = 0; h < hs; h++) w1[i][h] = r.nextGaussian() * 0.1;
            for (int h = 0; h < hs; h++) for (int d = 0; d < dd; d++) w2[h][d] = r.nextGaussian() * 0.1;
        }

        double[] forward(double[] latent) {
            double[] hidden = new double[hiddenSize];
            for (int h = 0; h < hiddenSize; h++) {
                double s = b1[h];
                for (int i = 0; i < latentDim; i++) s += latent[i] * w1[i][h];
                hidden[h] = Math.max(0, s);
            }
            double[] out = new double[dataDim];
            for (int d = 0; d < dataDim; d++) {
                double s = b2[d];
                for (int h = 0; h < hiddenSize; h++) s += hidden[h] * w2[h][d];
                out[d] = Math.tanh(s);
            }
            return out;
        }

        void train(Critic c, double[] latent, double lr) {
            double[] hidden = new double[hiddenSize];
            for (int h = 0; h < hiddenSize; h++) {
                double s = b1[h];
                for (int i = 0; i < latentDim; i++) s += latent[i] * w1[i][h];
                hidden[h] = Math.max(0, s);
            }
            double[] fake = new double[dataDim];
            for (int d = 0; d < dataDim; d++) {
                double s = b2[d];
                for (int h = 0; h < hiddenSize; h++) s += hidden[h] * w2[h][d];
                fake[d] = Math.tanh(s);
            }
            double[][] gradGen = new double[1][dataDim];
            double[][] gradTanh = c.backward(fake);
            for (int d = 0; d < dataDim; d++) gradGen[0][d] = -gradTanh[0][d];

            double[] dTanh = new double[dataDim];
            for (int d = 0; d < dataDim; d++) dTanh[d] = gradGen[0][d] * (1 - fake[d] * fake[d]);

            for (int h = 0; h < hiddenSize; h++)
                for (int d = 0; d < dataDim; d++) w2[h][d] -= lr * hidden[h] * dTanh[d];
            for (int d = 0; d < dataDim; d++) b2[d] -= lr * dTanh[d];

            double[] dHidden = new double[hiddenSize];
            for (int h = 0; h < hiddenSize; h++) {
                for (int d = 0; d < dataDim; d++) dHidden[h] += w2[h][d] * dTanh[d];
                if (hidden[h] <= 0) dHidden[h] = 0;
            }
            for (int i = 0; i < latentDim; i++)
                for (int h = 0; h < hiddenSize; h++) w1[i][h] -= lr * latent[i] * dHidden[h];
            for (int h = 0; h < hiddenSize; h++) b1[h] -= lr * dHidden[h];
        }
    }

    static class Critic {
        double[][] w1, w2; double[] b1, b2;
        final int dataDim, hiddenSize;

        Critic(int dd, int hs) { dataDim = dd; hiddenSize = hs;
            Random r = new Random(42);
            w1 = new double[dd][hs]; b1 = new double[hs];
            w2 = new double[hs][1]; b2 = new double[1];
            for (int i = 0; i < dd; i++) for (int h = 0; h < hs; h++) w1[i][h] = r.nextGaussian() * 0.1;
            for (int h = 0; h < hs; h++) w2[h][0] = r.nextGaussian() * 0.1;
        }

        double forward(double[] data) {
            double[] hidden = new double[hiddenSize];
            for (int h = 0; h < hiddenSize; h++) {
                double s = b1[h];
                for (int i = 0; i < dataDim; i++) s += data[i] * w1[i][h];
                hidden[h] = Math.max(0, s);
            }
            double s = b2[0];
            for (int h = 0; h < hiddenSize; h++) s += hidden[h] * w2[h][0];
            return s;
        }

        double trainStep(double[] real, double[] fake, double lr) {
            double realScore = forward(real);
            double fakeScore = forward(fake);
            double loss = fakeScore - realScore;

            double[] hiddenR = new double[hiddenSize];
            for (int h = 0; h < hiddenSize; h++) {
                double s = b1[h];
                for (int i = 0; i < dataDim; i++) s += real[i] * w1[i][h];
                hiddenR[h] = Math.max(0, s);
            }
            double[] hiddenF = new double[hiddenSize];
            for (int h = 0; h < hiddenSize; h++) {
                double s = b1[h];
                for (int i = 0; i < dataDim; i++) s += fake[i] * w1[i][h];
                hiddenF[h] = Math.max(0, s);
            }

            double[][] dw1 = new double[dataDim][hiddenSize];
            double[] db1 = new double[hiddenSize];
            double[] dw2 = new double[hiddenSize];
            double db2 = 0;

            for (int h = 0; h < hiddenSize; h++) {
                dw2[h] += hiddenF[h] - hiddenR[h];
            }
            db2 = 0;

            for (int h = 0; h < hiddenSize; h++) {
                if (hiddenF[h] > 0) {
                    db1[h] += w2[h][0];
                    for (int i = 0; i < dataDim; i++) dw1[i][h] += fake[i] * w2[h][0];
                }
                if (hiddenR[h] > 0) {
                    db1[h] -= w2[h][0];
                    for (int i = 0; i < dataDim; i++) dw1[i][h] -= real[i] * w2[h][0];
                }
            }

            for (int i = 0; i < dataDim; i++)
                for (int h = 0; h < hiddenSize; h++) w1[i][h] -= lr * dw1[i][h];
            for (int h = 0; h < hiddenSize; h++) b1[h] -= lr * db1[h];
            for (int h = 0; h < hiddenSize; h++) w2[h][0] -= lr * dw2[h];
            b2[0] -= lr * db2;

            return loss;
        }

        void clipWeights(double c) {
            for (int i = 0; i < dataDim; i++)
                for (int h = 0; h < hiddenSize; h++) w1[i][h] = Math.max(-c, Math.min(c, w1[i][h]));
            for (int h = 0; h < hiddenSize; h++) b1[h] = Math.max(-c, Math.min(c, b1[h]));
            for (int h = 0; h < hiddenSize; h++) w2[h][0] = Math.max(-c, Math.min(c, w2[h][0]));
            b2[0] = Math.max(-c, Math.min(c, b2[0]));
        }

        double[][] backward(double[] data) {
            double[] hidden = new double[hiddenSize];
            for (int h = 0; h < hiddenSize; h++) {
                double s = b1[h];
                for (int i = 0; i < dataDim; i++) s += data[i] * w1[i][h];
                hidden[h] = Math.max(0, s);
            }
            double[][] grad = new double[1][dataDim];
            for (int i = 0; i < dataDim; i++) {
                double g = 0;
                for (int h = 0; h < hiddenSize; h++) {
                    if (hidden[h] > 0) g += w1[i][h] * w2[h][0];
                }
                grad[0][i] = g;
            }
            return grad;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== WGAN Demo ===");
        WGAN wgan = new WGAN(10, 5, 16, 0.01);
        Random rng = new Random(42);
        double[][] data = new double[500][5];
        for (int s = 0; s < 500; s++)
            for (int d = 0; d < 5; d++) data[s][d] = 0.3 * rng.nextGaussian() + 1.0;

        wgan.train(data, 2000, 5, 0.0001);
        double[] gen = wgan.generate(wgan.sampleLatent());
        System.out.print("WGAN generated sample: ");
        for (double v : gen) System.out.printf("%.4f ", v);
        System.out.println();
    }
}

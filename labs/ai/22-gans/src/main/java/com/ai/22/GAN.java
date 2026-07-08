package com.ai22;

import java.util.*;

public class GAN {
    private final Generator generator;
    private final Discriminator discriminator;
    private final Random random;
    private final int latentDim;

    public GAN(int latentDim, int dataDim, int hiddenSize) {
        this.latentDim = latentDim;
        this.random = new Random(42);
        this.generator = new Generator(latentDim, hiddenSize, dataDim);
        this.discriminator = new Discriminator(dataDim, hiddenSize, 1);
    }

    public double[] generate(double[] latent) {
        return generator.forward(latent);
    }

    public double discriminate(double[] data) {
        return discriminator.forward(data)[0];
    }

    public void trainStep(double[] realData, double learningRate) {
        double[] noise = sampleLatent();
        double[] fakeData = generator.forward(noise);

        double[][] realBatch = {realData};
        double[][] fakeBatch = {fakeData};

        double[] realLabels = {1.0};
        double[] fakeLabels = {0.0};
        double[] misleadingLabels = {1.0};

        discriminator.train(realBatch, realLabels, fakeBatch, fakeLabels, learningRate);
        generator.train(discriminator, noise, misleadingLabels, learningRate);
    }

    public double[] sampleLatent() {
        double[] z = new double[latentDim];
        for (int i = 0; i < latentDim; i++) z[i] = random.nextGaussian();
        return z;
    }

    public void train(double[][] realData, int epochs, double learningRate) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            double dLoss = 0;
            for (double[] data : realData) {
                double[] noise = sampleLatent();
                double[] fake = generator.forward(noise);
                double dReal = discriminator.forward(data)[0];
                double dFake = discriminator.forward(fake)[0];
                dLoss += -(Math.log(Math.max(dReal, 1e-15)) + Math.log(Math.max(1 - dFake, 1e-15)));

                double[][] realBatch = {data};
                double[][] fakeBatch = {fake};
                discriminator.train(realBatch, new double[]{1.0}, fakeBatch, new double[]{0.0}, learningRate);

                double[] noise2 = sampleLatent();
                generator.train(discriminator, noise2, new double[]{1.0}, learningRate);
            }
            if (epoch % 500 == 0) {
                System.out.printf("Epoch %d: D Loss = %.4f%n", epoch, dLoss / realData.length);
            }
        }
    }

    static class Generator {
        double[][] w1, w2;
        double[] b1, b2;
        final int latentDim, hiddenSize, dataDim;

        Generator(int latentDim, int hiddenSize, int dataDim) {
            this.latentDim = latentDim;
            this.hiddenSize = hiddenSize;
            this.dataDim = dataDim;
            Random rng = new Random(42);
            w1 = new double[latentDim][hiddenSize];
            b1 = new double[hiddenSize];
            w2 = new double[hiddenSize][dataDim];
            b2 = new double[dataDim];
            for (int i = 0; i < latentDim; i++)
                for (int h = 0; h < hiddenSize; h++) w1[i][h] = rng.nextGaussian() * 0.1;
            for (int h = 0; h < hiddenSize; h++)
                for (int d = 0; d < dataDim; d++) w2[h][d] = rng.nextGaussian() * 0.1;
        }

        double[] forward(double[] latent) {
            double[] hidden = new double[hiddenSize];
            for (int h = 0; h < hiddenSize; h++) {
                double sum = b1[h];
                for (int i = 0; i < latentDim; i++) sum += latent[i] * w1[i][h];
                hidden[h] = Math.max(0, sum);
            }
            double[] output = new double[dataDim];
            for (int d = 0; d < dataDim; d++) {
                double sum = b2[d];
                for (int h = 0; h < hiddenSize; h++) sum += hidden[h] * w2[h][d];
                output[d] = Math.tanh(sum);
            }
            return output;
        }

        void train(Discriminator disc, double[] latent, double[] targetLabels, double lr) {
            double[] hidden = new double[hiddenSize];
            for (int h = 0; h < hiddenSize; h++) {
                double sum = b1[h];
                for (int i = 0; i < latentDim; i++) sum += latent[i] * w1[i][h];
                hidden[h] = Math.max(0, sum);
            }
            double[] fakeData = new double[dataDim];
            for (int d = 0; d < dataDim; d++) {
                double sum = b2[d];
                for (int h = 0; h < hiddenSize; h++) sum += hidden[h] * w2[h][d];
                fakeData[d] = Math.tanh(sum);
            }

            double[][] gradOutput = disc.backward(fakeData, targetLabels);

            double[] dTanh = new double[dataDim];
            for (int d = 0; d < dataDim; d++) dTanh[d] = gradOutput[0][d] * (1 - fakeData[d] * fakeData[d]);

            double[][] dw2 = new double[hiddenSize][dataDim];
            double[] db2 = new double[dataDim];
            for (int d = 0; d < dataDim; d++) {
                db2[d] = dTanh[d];
                for (int h = 0; h < hiddenSize; h++) dw2[h][d] = hidden[h] * dTanh[d];
            }

            double[] dHidden = new double[hiddenSize];
            for (int h = 0; h < hiddenSize; h++) {
                for (int d = 0; d < dataDim; d++) dHidden[h] += w2[h][d] * dTanh[d];
                if (hidden[h] <= 0) dHidden[h] = 0;
            }

            double[][] dw1 = new double[latentDim][hiddenSize];
            double[] db1 = new double[hiddenSize];
            for (int h = 0; h < hiddenSize; h++) {
                db1[h] = dHidden[h];
                for (int i = 0; i < latentDim; i++) dw1[i][h] = latent[i] * dHidden[h];
            }

            for (int i = 0; i < latentDim; i++)
                for (int h = 0; h < hiddenSize; h++) w1[i][h] -= lr * dw1[i][h];
            for (int h = 0; h < hiddenSize; h++) b1[h] -= lr * db1[h];
            for (int h = 0; h < hiddenSize; h++)
                for (int d = 0; d < dataDim; d++) w2[h][d] -= lr * dw2[h][d];
            for (int d = 0; d < dataDim; d++) b2[d] -= lr * db2[d];
        }
    }

    static class Discriminator {
        double[][] w1, w2;
        double[] b1, b2;
        final int dataDim, hiddenSize;

        Discriminator(int dataDim, int hiddenSize, int outputSize) {
            this.dataDim = dataDim;
            this.hiddenSize = hiddenSize;
            Random rng = new Random(42);
            w1 = new double[dataDim][hiddenSize];
            b1 = new double[hiddenSize];
            w2 = new double[hiddenSize][outputSize];
            b2 = new double[outputSize];
            for (int i = 0; i < dataDim; i++)
                for (int h = 0; h < hiddenSize; h++) w1[i][h] = rng.nextGaussian() * 0.1;
            for (int h = 0; h < hiddenSize; h++)
                for (int o = 0; o < outputSize; o++) w2[h][o] = rng.nextGaussian() * 0.1;
        }

        double[] forward(double[] data) {
            double[] hidden = new double[hiddenSize];
            for (int h = 0; h < hiddenSize; h++) {
                double sum = b1[h];
                for (int i = 0; i < dataDim; i++) sum += data[i] * w1[i][h];
                hidden[h] = Math.max(0, sum);
            }
            double logit = b2[0];
            for (int h = 0; h < hiddenSize; h++) logit += hidden[h] * w2[h][0];
            return new double[]{1.0 / (1.0 + Math.exp(-logit))};
        }

        void train(double[][] realData, double[] realLabels, double[][] fakeData, double[] fakeLabels, double lr) {
            int n = realData.length + fakeData.length;
            double[][] dw1 = new double[dataDim][hiddenSize];
            double[] db1 = new double[hiddenSize];
            double[][] dw2 = new double[hiddenSize][1];
            double[] db2 = new double[1];

            for (int s = 0; s < realData.length; s++) {
                trainSample(realData[s], realLabels[s], dw1, db1, dw2, db2, n);
            }
            for (int s = 0; s < fakeData.length; s++) {
                trainSample(fakeData[s], fakeLabels[s], dw1, db1, dw2, db2, n);
            }

            for (int i = 0; i < dataDim; i++)
                for (int h = 0; h < hiddenSize; h++) w1[i][h] -= lr * dw1[i][h];
            for (int h = 0; h < hiddenSize; h++) b1[h] -= lr * db1[h];
            for (int h = 0; h < hiddenSize; h++) w2[h][0] -= lr * dw2[h][0];
            b2[0] -= lr * db2[0];
        }

        private void trainSample(double[] data, double label, double[][] dw1, double[] db1,
                                  double[][] dw2, double[] db2, int n) {
            double[] hidden = new double[hiddenSize];
            for (int h = 0; h < hiddenSize; h++) {
                double sum = b1[h];
                for (int i = 0; i < dataDim; i++) sum += data[i] * w1[i][h];
                hidden[h] = Math.max(0, sum);
            }
            double logit = b2[0];
            for (int h = 0; h < hiddenSize; h++) logit += hidden[h] * w2[h][0];
            double prob = 1.0 / (1.0 + Math.exp(-logit));
            double dLoss = (prob - label) / n;

            db2[0] += dLoss;
            for (int h = 0; h < hiddenSize; h++) dw2[h][0] += hidden[h] * dLoss;

            for (int h = 0; h < hiddenSize; h++) {
                double dH = w2[h][0] * dLoss;
                if (hidden[h] > 0) {
                    db1[h] += dH;
                    for (int i = 0; i < dataDim; i++) dw1[i][h] += data[i] * dH;
                }
            }
        }

        double[][] backward(double[] data, double[] targetLabels) {
            double[] hidden = new double[hiddenSize];
            for (int h = 0; h < hiddenSize; h++) {
                double sum = b1[h];
                for (int i = 0; i < dataDim; i++) sum += data[i] * w1[i][h];
                hidden[h] = Math.max(0, sum);
            }
            double logit = b2[0];
            for (int h = 0; h < hiddenSize; h++) logit += hidden[h] * w2[h][0];
            double prob = 1.0 / (1.0 + Math.exp(-logit));
            double dLoss = (prob - targetLabels[0]);

            double[][] gradOutput = new double[1][dataDim];
            for (int i = 0; i < dataDim; i++) {
                double dHidden = 0;
                for (int h = 0; h < hiddenSize; h++) {
                    double dH = w2[h][0] * dLoss;
                    if (hidden[h] > 0) dHidden += w1[i][h] * dH;
                }
                gradOutput[0][i] = dHidden;
            }
            return gradOutput;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== GAN Demo ===");
        int latentDim = 10;
        int dataDim = 5;
        GAN gan = new GAN(latentDim, dataDim, 16);

        double[][] syntheticData = new double[1000][dataDim];
        Random rng = new Random(42);
        for (int s = 0; s < 1000; s++) {
            for (int d = 0; d < dataDim; d++) {
                syntheticData[s][d] = 0.5 * rng.nextGaussian() + 1.0;
            }
        }

        gan.train(syntheticData, 2000, 0.01);

        double[] sample = gan.sampleLatent();
        double[] generated = gan.generate(sample);
        System.out.print("Generated sample: ");
        for (double v : generated) System.out.printf("%.4f ", v);
        System.out.println();

        double dScore = gan.discriminate(generated);
        System.out.printf("Discriminator score for generated data: %.4f (should be ~0.5)%n", dScore);

        double[] realSample = syntheticData[0];
        double realScore = gan.discriminate(realSample);
        System.out.printf("Discriminator score for real data: %.4f%n", realScore);
    }
}

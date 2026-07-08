package com.ai23;

import java.util.*;

public class VAE {
    private final Encoder encoder;
    private final Decoder decoder;
    private final Random random;
    private final int inputDim, latentDim;

    public VAE(int inputDim, int latentDim, int hiddenSize) {
        this.inputDim = inputDim;
        this.latentDim = latentDim;
        this.random = new Random(42);
        this.encoder = new Encoder(inputDim, latentDim, hiddenSize);
        this.decoder = new Decoder(latentDim, inputDim, hiddenSize);
    }

    public double[] encode(double[] input) {
        return encoder.mean(input);
    }

    public double[] decode(double[] latent) {
        return decoder.forward(latent);
    }

    public double[] reparameterize(double[] mean, double[] logVar) {
        double[] z = new double[latentDim];
        for (int i = 0; i < latentDim; i++) {
            double std = Math.exp(0.5 * logVar[i]);
            z[i] = mean[i] + std * random.nextGaussian();
        }
        return z;
    }

    public record VAEResult(double[] reconstructed, double[] mean, double[] logVar, double loss) {}

    public VAEResult forward(double[] input) {
        double[] mean = encoder.mean(input);
        double[] logVar = encoder.logVar(input);
        double[] z = reparameterize(mean, logVar);
        double[] reconstructed = decoder.forward(z);
        double reconstructionLoss = 0;
        for (int i = 0; i < inputDim; i++) {
            double diff = reconstructed[i] - input[i];
            reconstructionLoss += diff * diff;
        }
        double klDiv = -0.5 * latentDim + 0.5 * Arrays.stream(logVar).sum()
                - 0.5 * Arrays.stream(logVar).map(Math::exp).sum()
                + 0.5 * Arrays.stream(mean).map(m -> m * m).sum();
        return new VAEResult(reconstructed, mean, logVar, reconstructionLoss + klDiv);
    }

    public void train(double[][] data, int epochs, double learningRate) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0;
            for (double[] input : data) {
                VAEResult result = forward(input);
                totalLoss += result.loss;

                double[] reconGrad = new double[inputDim];
                for (int i = 0; i < inputDim; i++) reconGrad[i] = 2 * (result.reconstructed[i] - input[i]) / data.length;

                decoder.train(result.mean, reconGrad, learningRate);

                double[] klGradMean = new double[latentDim];
                double[] klGradLogVar = new double[latentDim];
                for (int i = 0; i < latentDim; i++) {
                    klGradMean[i] = result.mean[i] / data.length;
                    klGradLogVar[i] = 0.5 * (Math.exp(result.logVar[i]) - 1) / data.length;
                }
                encoder.train(result.reconstructed, input, result.mean, result.logVar, reconGrad,
                        klGradMean, klGradLogVar, learningRate);
            }
            if (epoch % 200 == 0) {
                System.out.printf("Epoch %d: Avg Loss = %.4f%n", epoch, totalLoss / data.length);
            }
        }
    }

    static class Encoder {
        double[][] w1, w2Mean, w2LogVar;
        double[] b1, b2Mean, b2LogVar;
        final int inputDim, latentDim, hiddenSize;

        Encoder(int id, int ld, int hs) {
            inputDim = id; latentDim = ld; hiddenSize = hs;
            Random r = new Random(42);
            w1 = new double[id][hs]; b1 = new double[hs];
            w2Mean = new double[hs][ld]; b2Mean = new double[ld];
            w2LogVar = new double[hs][ld]; b2LogVar = new double[ld];
            for (int i = 0; i < id; i++) for (int h = 0; h < hs; h++) w1[i][h] = r.nextGaussian() * 0.01;
            for (int h = 0; h < hs; h++) for (int l = 0; l < ld; l++) { w2Mean[h][l] = r.nextGaussian() * 0.01; w2LogVar[h][l] = r.nextGaussian() * 0.01; }
        }

        double[] hidden(double[] input) {
            double[] h = new double[hiddenSize];
            for (int j = 0; j < hiddenSize; j++) { double s = b1[j]; for (int i = 0; i < inputDim; i++) s += input[i] * w1[i][j]; h[j] = Math.max(0, s); }
            return h;
        }

        double[] mean(double[] input) {
            double[] h = hidden(input);
            double[] m = new double[latentDim];
            for (int l = 0; l < latentDim; l++) { double s = b2Mean[l]; for (int j = 0; j < hiddenSize; j++) s += h[j] * w2Mean[j][l]; m[l] = s; }
            return m;
        }

        double[] logVar(double[] input) {
            double[] h = hidden(input);
            double[] lv = new double[latentDim];
            for (int l = 0; l < latentDim; l++) { double s = b2LogVar[l]; for (int j = 0; j < hiddenSize; j++) s += h[j] * w2LogVar[j][l]; lv[l] = s; }
            return lv;
        }

        void train(double[] reconstructed, double[] input, double[] mean, double[] logVar,
                   double[] reconGrad, double[] klGradMean, double[] klGradLogVar, double lr) {
            double[] h = hidden(input);
            for (int l = 0; l < latentDim; l++) {
                for (int j = 0; j < hiddenSize; j++) {
                    w2Mean[j][l] -= lr * h[j] * klGradMean[l];
                    w2LogVar[j][l] -= lr * h[j] * klGradLogVar[l];
                }
                b2Mean[l] -= lr * klGradMean[l];
                b2LogVar[l] -= lr * klGradLogVar[l];
            }
            double[] dHidden = new double[hiddenSize];
            for (int j = 0; j < hiddenSize; j++) {
                for (int l = 0; l < latentDim; l++) {
                    dHidden[j] += w2Mean[j][l] * klGradMean[l] + w2LogVar[j][l] * klGradLogVar[l];
                }
                if (h[j] <= 0) dHidden[j] = 0;
            }
            for (int i = 0; i < inputDim; i++) for (int j = 0; j < hiddenSize; j++) w1[i][j] -= lr * input[i] * dHidden[j];
            for (int j = 0; j < hiddenSize; j++) b1[j] -= lr * dHidden[j];
        }
    }

    static class Decoder {
        double[][] w1, w2; double[] b1, b2;
        final int latentDim, inputDim, hiddenSize;

        Decoder(int ld, int id, int hs) {
            latentDim = ld; inputDim = id; hiddenSize = hs;
            Random r = new Random(42);
            w1 = new double[ld][hs]; b1 = new double[hs];
            w2 = new double[hs][id]; b2 = new double[id];
            for (int l = 0; l < ld; l++) for (int h = 0; h < hs; h++) w1[l][h] = r.nextGaussian() * 0.01;
            for (int h = 0; h < hs; h++) for (int i = 0; i < id; i++) w2[h][i] = r.nextGaussian() * 0.01;
        }

        double[] forward(double[] latent) {
            double[] h = new double[hiddenSize];
            for (int j = 0; j < hiddenSize; j++) { double s = b1[j]; for (int l = 0; l < latentDim; l++) s += latent[l] * w1[l][j]; h[j] = Math.max(0, s); }
            double[] out = new double[inputDim];
            for (int i = 0; i < inputDim; i++) { double s = b2[i]; for (int j = 0; j < hiddenSize; j++) s += h[j] * w2[j][i]; out[i] = sigmoid(s); }
            return out;
        }

        void train(double[] latent, double[] gradOutput, double lr) {
            double[] h = new double[hiddenSize];
            for (int j = 0; j < hiddenSize; j++) { double s = b1[j]; for (int l = 0; l < latentDim; l++) s += latent[l] * w1[l][j]; h[j] = Math.max(0, s); }
            double[] out = new double[inputDim];
            for (int i = 0; i < inputDim; i++) { double s = b2[i]; for (int j = 0; j < hiddenSize; j++) s += h[j] * w2[j][i]; out[i] = sigmoid(s); }

            for (int j = 0; j < hiddenSize; j++) for (int i = 0; i < inputDim; i++) w2[j][i] -= lr * h[j] * gradOutput[i] * out[i] * (1 - out[i]);
            for (int i = 0; i < inputDim; i++) b2[i] -= lr * gradOutput[i] * out[i] * (1 - out[i]);

            double[] dH = new double[hiddenSize];
            for (int j = 0; j < hiddenSize; j++) {
                for (int i = 0; i < inputDim; i++) dH[j] += w2[j][i] * gradOutput[i] * out[i] * (1 - out[i]);
                if (h[j] <= 0) dH[j] = 0;
            }
            for (int l = 0; l < latentDim; l++) for (int j = 0; j < hiddenSize; j++) w1[l][j] -= lr * latent[l] * dH[j];
            for (int j = 0; j < hiddenSize; j++) b1[j] -= lr * dH[j];
        }

        private double sigmoid(double x) { return 1.0 / (1.0 + Math.exp(-x)); }
    }

    public static void main(String[] args) {
        System.out.println("=== VAE Demo ===");
        VAE vae = new VAE(8, 3, 16);
        Random rng = new Random(42);
        double[][] data = new double[200][8];
        for (int s = 0; s < 200; s++) for (int i = 0; i < 8; i++) data[s][i] = rng.nextDouble() > 0.5 ? 1 : 0;
        vae.train(data, 1000, 0.01);
        double[] sample = data[0];
        VAEResult result = vae.forward(sample);
        System.out.print("Original: "); for (double v : sample) System.out.printf("%.0f ", v);
        System.out.println();
        System.out.print("Reconstructed: "); for (double v : result.reconstructed) System.out.printf("%.3f ", v);
        System.out.println();
        System.out.printf("Loss: %.4f (Recon + KL)%n", result.loss);
        double[] z = vae.encode(sample);
        System.out.print("Latent code: "); for (double v : z) System.out.printf("%.4f ", v);
        System.out.println();
    }
}

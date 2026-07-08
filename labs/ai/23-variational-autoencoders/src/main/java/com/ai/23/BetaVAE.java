package com.ai23;

import java.util.*;

public class BetaVAE {
    private final VAE.Encoder encoder;
    private final VAE.Decoder decoder;
    private final Random random;
    private final int inputDim, latentDim;
    private final double beta;

    public BetaVAE(int inputDim, int latentDim, int hiddenSize, double beta) {
        this.inputDim = inputDim;
        this.latentDim = latentDim;
        this.beta = beta;
        this.random = new Random(42);
        this.encoder = new VAE.Encoder(inputDim, latentDim, hiddenSize);
        this.decoder = new VAE.Decoder(latentDim, inputDim, hiddenSize);
    }

    public double[] reparameterize(double[] mean, double[] logVar) {
        double[] z = new double[latentDim];
        for (int i = 0; i < latentDim; i++) {
            z[i] = mean[i] + Math.exp(0.5 * logVar[i]) * random.nextGaussian();
        }
        return z;
    }

    public double computeLoss(double[] input, double[] recon, double[] mean, double[] logVar) {
        double reconLoss = 0;
        for (int i = 0; i < inputDim; i++) {
            double diff = recon[i] - input[i];
            reconLoss += diff * diff;
        }
        double kl = -0.5 * Arrays.stream(logVar).sum()
                + 0.5 * Arrays.stream(logVar).map(Math::exp).sum()
                + 0.5 * Arrays.stream(mean).map(m -> m * m).sum()
                - 0.5 * latentDim;
        return reconLoss + beta * kl;
    }

    public double[] forward(double[] input) {
        double[] mean = encoder.mean(input);
        double[] logVar = encoder.logVar(input);
        double[] z = reparameterize(mean, logVar);
        return decoder.forward(z);
    }

    public static void main(String[] args) {
        System.out.println("=== Beta-VAE Demo ===");
        BetaVAE bvae = new BetaVAE(8, 3, 16, 4.0);
        Random rng = new Random(42);
        double[][] data = new double[100][8];
        for (int s = 0; s < 100; s++) for (int i = 0; i < 8; i++) data[s][i] = rng.nextDouble() > 0.5 ? 1 : 0;
        double[] sample = data[0];
        double[] recon = bvae.forward(sample);
        double[] mean = bvae.encoder.mean(sample);
        double[] logVar = bvae.encoder.logVar(sample);
        double loss = bvae.computeLoss(sample, recon, mean, logVar);
        System.out.printf("Beta-VAE (beta=%.1f) loss: %.4f%n", bvae.beta, loss);
        System.out.print("Reconstruction: "); for (double v : recon) System.out.printf("%.3f ", v);
        System.out.println();
    }
}

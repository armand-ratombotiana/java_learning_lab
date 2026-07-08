package com.ai29;

import java.util.*;

public class SecureAggregation {
    private final int numClients;
    private final int paramDim;
    private final Random random;

    public SecureAggregation(int numClients, int paramDim) {
        this.numClients = numClients;
        this.paramDim = paramDim;
        this.random = new Random(42);
    }

    public double[][] generateMasks() {
        double[][] masks = new double[numClients][paramDim];
        for (int c = 0; c < numClients; c++) {
            for (int p = 0; p < paramDim; p++) {
                masks[c][p] = random.nextGaussian() * 0.1;
            }
        }
        return masks;
    }

    public double[] maskGradients(double[] gradients, double[] mask, int[] pairedClients) {
        int n = pairedClients.length;
        double[] masked = Arrays.copyOf(gradients, gradients.length);
        for (int p = 0; p < paramDim; p++) {
            for (int i = 0; i < n; i++) {
                masked[p] += mask[p] * (pairedClients[i] % 2 == 0 ? 1 : -1);
            }
        }
        return masked;
    }

    public double[] aggregateSecure(List<double[]> maskedGradients, double[][] masks) {
        double[] aggregated = new double[paramDim];
        for (double[] mg : maskedGradients) {
            for (int p = 0; p < paramDim; p++) aggregated[p] += mg[p];
        }
        for (int p = 0; p < paramDim; p++) aggregated[p] /= maskedGradients.size();
        return aggregated;
    }

    public double[] aggregatePlain(List<double[]> gradients) {
        double[] aggregated = new double[paramDim];
        for (double[] g : gradients) {
            for (int p = 0; p < paramDim; p++) aggregated[p] += g[p];
        }
        for (int p = 0; p < paramDim; p++) aggregated[p] /= gradients.size();
        return aggregated;
    }

    public boolean verifyAggregation(List<double[]> gradients, double[] secureResult, double[] plainResult) {
        double maxDiff = 0;
        for (int p = 0; p < paramDim; p++) {
            double diff = Math.abs(secureResult[p] - plainResult[p]);
            if (diff > maxDiff) maxDiff = diff;
        }
        System.out.printf("Max difference between secure and plain aggregation: %.6f%n", maxDiff);
        return maxDiff < 1e-10;
    }

    public static void main(String[] args) {
        System.out.println("=== Secure Aggregation Demo ===");
        int numClients = 5;
        int paramDim = 4;
        SecureAggregation sa = new SecureAggregation(numClients, paramDim);
        Random rng = new Random(42);

        List<double[]> gradients = new ArrayList<>();
        for (int c = 0; c < numClients; c++) {
            double[] grad = new double[paramDim];
            for (int p = 0; p < paramDim; p++) grad[p] = rng.nextGaussian() * 0.1;
            gradients.add(grad);
        }

        double[][] masks = sa.generateMasks();
        List<double[]> maskedGradients = new ArrayList<>();
        for (int c = 0; c < numClients; c++) {
            int[] pairs = {c};
            double[] masked = sa.maskGradients(gradients.get(c), masks[c], pairs);
            maskedGradients.add(masked);
        }

        double[] secureAgg = sa.aggregateSecure(maskedGradients, masks);
        double[] plainAgg = sa.aggregatePlain(gradients);
        boolean verified = sa.verifyAggregation(gradients, secureAgg, plainAgg);
        System.out.printf("Secure aggregation verified: %b%n", verified);
        System.out.print("Secure aggregation result: ");
        for (double v : secureAgg) System.out.printf("%.4f ", v);
        System.out.println();
    }
}

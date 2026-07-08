package com.ai27;

import java.util.*;

public class PrototypicalNetworks {
    private final Encoder encoder;
    private final Random random;
    private final int inputDim, embeddingDim;

    public PrototypicalNetworks(int inputDim, int embeddingDim, int hiddenDim) {
        this.inputDim = inputDim;
        this.embeddingDim = embeddingDim;
        this.random = new Random(42);
        this.encoder = new Encoder(inputDim, hiddenDim, embeddingDim);
    }

    public double[] encode(double[] input) {
        return encoder.forward(input);
    }

    public double[][] computePrototypes(List<double[]> supportSet, int[] labels, int numClasses) {
        double[][] prototypes = new double[numClasses][embeddingDim];
        int[] counts = new int[numClasses];
        for (int i = 0; i < supportSet.size(); i++) {
            double[] embedding = encode(supportSet.get(i));
            int label = labels[i];
            for (int d = 0; d < embeddingDim; d++) prototypes[label][d] += embedding[d];
            counts[label]++;
        }
        for (int c = 0; c < numClasses; c++) {
            if (counts[c] > 0) {
                for (int d = 0; d < embeddingDim; d++) prototypes[c][d] /= counts[c];
            }
        }
        return prototypes;
    }

    public double classify(double[] query, double[][] prototypes) {
        double[] qEmb = encode(query);
        double minDist = Double.MAX_VALUE;
        int bestClass = -1;
        for (int c = 0; c < prototypes.length; c++) {
            double dist = 0;
            for (int d = 0; d < embeddingDim; d++) {
                double diff = qEmb[d] - prototypes[c][d];
                dist += diff * diff;
            }
            if (dist < minDist) { minDist = dist; bestClass = c; }
        }
        return bestClass;
    }

    public double trainEpisode(List<double[]> support, int[] supportLabels,
                                 List<double[]> query, int[] queryLabels, int numClasses, double lr) {
        double[][] prototypes = computePrototypes(support, supportLabels, numClasses);
        double loss = 0;
        for (int i = 0; i < query.size(); i++) {
            double[] qEmb = encode(query.get(i));
            double[] logits = new double[numClasses];
            for (int c = 0; c < numClasses; c++) {
                double dist = 0;
                for (int d = 0; d < embeddingDim; d++) {
                    double diff = qEmb[d] - prototypes[c][d];
                    dist += diff * diff;
                }
                logits[c] = -dist;
            }
            double maxLogit = Arrays.stream(logits).max().orElse(0);
            double sumExp = 0;
            for (int c = 0; c < numClasses; c++) sumExp += Math.exp(logits[c] - maxLogit);
            double prob = Math.exp(logits[queryLabels[i]] - maxLogit) / sumExp;
            loss += -Math.log(Math.max(prob, 1e-15));
        }
        return loss / query.size();
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
            for (int o = 0; o < outputDim; o++) { double s = b2[o]; for (int j = 0; j < hiddenDim; j++) s += h[j] * w2[j][o]; out[o] = s; }
            return out;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Prototypical Networks Demo ===");
        Random rng = new Random(42);
        int dim = 5;
        PrototypicalNetworks pn = new PrototypicalNetworks(dim, 4, 16);
        List<double[]> support = new ArrayList<>();
        int[] supLabels = new int[6];
        for (int c = 0; c < 2; c++) {
            for (int i = 0; i < 3; i++) {
                double[] pt = new double[dim];
                for (int j = 0; j < dim; j++) pt[j] = (c == 0 ? 1.0 : 0.0) + rng.nextGaussian() * 0.1;
                support.add(pt);
                supLabels[support.size() - 1] = c;
            }
        }
        double[][] prots = pn.computePrototypes(support, supLabels, 2);
        double[] query = {0.9, 0.9, 0.9, 0.9, 0.9};
        double predClass = pn.classify(query, prots);
        System.out.printf("Query classified as class %.0f (expected 0)%n", predClass);
        double[] query2 = {0.1, 0.1, 0.1, 0.1, 0.1};
        double predClass2 = pn.classify(query2, prots);
        System.out.printf("Query classified as class %.0f (expected 1)%n", predClass2);
    }
}

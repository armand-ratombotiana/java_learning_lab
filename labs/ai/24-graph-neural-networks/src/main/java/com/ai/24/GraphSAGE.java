package com.ai24;

import java.util.*;

public class GraphSAGE {
    private double[][] w1, w2;
    private double[] b1, b2;
    private final int inputDim, hiddenDim, outputDim;
    private final Random random;

    public GraphSAGE(int inputDim, int hiddenDim, int outputDim) {
        this.inputDim = inputDim;
        this.hiddenDim = hiddenDim;
        this.outputDim = outputDim;
        this.random = new Random(42);
        w1 = new double[2 * inputDim][hiddenDim];
        b1 = new double[hiddenDim];
        w2 = new double[hiddenDim][outputDim];
        b2 = new double[outputDim];
        for (int i = 0; i < 2 * inputDim; i++) for (int h = 0; h < hiddenDim; h++) w1[i][h] = random.nextGaussian() * 0.1;
        for (int h = 0; h < hiddenDim; h++) for (int o = 0; o < outputDim; o++) w2[h][o] = random.nextGaussian() * 0.1;
    }

    public double[] forward(double[] nodeFeatures, List<double[]> neighborFeatures) {
        double[] neighborMean = new double[inputDim];
        for (double[] nf : neighborFeatures) {
            for (int i = 0; i < inputDim; i++) neighborMean[i] += nf[i];
        }
        if (!neighborFeatures.isEmpty()) {
            for (int i = 0; i < inputDim; i++) neighborMean[i] /= neighborFeatures.size();
        }
        double[] combined = new double[2 * inputDim];
        System.arraycopy(nodeFeatures, 0, combined, 0, inputDim);
        System.arraycopy(neighborMean, 0, combined, inputDim, inputDim);

        double[] hidden = new double[hiddenDim];
        for (int h = 0; h < hiddenDim; h++) {
            double s = b1[h];
            for (int i = 0; i < 2 * inputDim; i++) s += combined[i] * w1[i][h];
            hidden[h] = Math.max(0, s);
        }
        double[] out = new double[outputDim];
        for (int o = 0; o < outputDim; o++) {
            double s = b2[o];
            for (int h = 0; h < hiddenDim; h++) s += hidden[h] * w2[h][o];
            out[o] = s;
        }
        double norm = 0; for (double v : out) norm += v * v;
        norm = Math.sqrt(norm) + 1e-15;
        for (int o = 0; o < outputDim; o++) out[o] /= norm;
        return out;
    }

    public static void main(String[] args) {
        System.out.println("=== GraphSAGE Demo ===");
        Random rng = new Random(42);
        GraphSAGE sage = new GraphSAGE(4, 8, 3);
        double[] nodeFeat = {0.5, 0.3, 0.8, 0.2};
        List<double[]> neighbors = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            double[] nf = new double[4];
            for (int j = 0; j < 4; j++) nf[j] = rng.nextDouble();
            neighbors.add(nf);
        }
        double[] embedding = sage.forward(nodeFeat, neighbors);
        System.out.print("Node embedding: "); for (double v : embedding) System.out.printf("%.4f ", v);
        System.out.println();
    }
}

package com.ai24;

import java.util.*;

public class GCN {
    private final int inputDim, hiddenDim, outputDim;
    private double[][] w1, w2;
    private double[] b1, b2;
    private final Random random;

    public GCN(int inputDim, int hiddenDim, int outputDim) {
        this.inputDim = inputDim;
        this.hiddenDim = hiddenDim;
        this.outputDim = outputDim;
        this.random = new Random(42);
        w1 = new double[inputDim][hiddenDim];
        b1 = new double[hiddenDim];
        w2 = new double[hiddenDim][outputDim];
        b2 = new double[outputDim];
        for (int i = 0; i < inputDim; i++)
            for (int h = 0; h < hiddenDim; h++) w1[i][h] = random.nextGaussian() * 0.1;
        for (int h = 0; h < hiddenDim; h++)
            for (int o = 0; o < outputDim; o++) w2[h][o] = random.nextGaussian() * 0.1;
    }

    public double[][] forward(double[][] features, double[][] adjacency) {
        int n = features.length;
        double[][] normalizedAdj = normalizeAdjacency(adjacency);
        double[][] hidden = new double[n][hiddenDim];
        for (int v = 0; v < n; v++) {
            for (int h = 0; h < hiddenDim; h++) {
                double sum = b1[h];
                for (int u = 0; u < n; u++) {
                    if (normalizedAdj[v][u] != 0) {
                        for (int i = 0; i < inputDim; i++) {
                            sum += normalizedAdj[v][u] * features[u][i] * w1[i][h];
                        }
                    }
                }
                hidden[v][h] = Math.max(0, sum);
            }
        }
        double[][] output = new double[n][outputDim];
        for (int v = 0; v < n; v++) {
            for (int o = 0; o < outputDim; o++) {
                double sum = b2[o];
                for (int h = 0; h < hiddenDim; h++) sum += hidden[v][h] * w2[h][o];
                output[v][o] = Math.max(0, sum);
            }
        }
        return output;
    }

    private double[][] normalizeAdjacency(double[][] adj) {
        int n = adj.length;
        double[][] normalized = new double[n][n];
        int[] degrees = new int[n];
        for (int i = 0; i < n; i++) {
            int deg = 0;
            for (int j = 0; j < n; j++) {
                if (adj[i][j] != 0 || i == j) deg++;
            }
            degrees[i] = deg;
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double val = (i == j ? 1.0 : adj[i][j]);
                normalized[i][j] = val / Math.sqrt(degrees[i] * degrees[j]);
            }
        }
        return normalized;
    }

    public double[][] getNodeEmbeddings(double[][] features, double[][] adjacency) {
        return forward(features, adjacency);
    }

    public static void main(String[] args) {
        System.out.println("=== GCN Demo ===");
        int numNodes = 5;
        double[][] features = new double[numNodes][3];
        Random rng = new Random(42);
        for (int i = 0; i < numNodes; i++)
            for (int j = 0; j < 3; j++) features[i][j] = rng.nextDouble();

        double[][] adj = new double[numNodes][numNodes];
        adj[0][1] = adj[1][0] = 1; adj[1][2] = adj[2][1] = 1;
        adj[2][3] = adj[3][2] = 1; adj[3][4] = adj[4][3] = 1;

        GCN gcn = new GCN(3, 8, 4);
        double[][] embeddings = gcn.forward(features, adj);
        System.out.println("Node embeddings after GCN layer:");
        for (int i = 0; i < numNodes; i++) {
            System.out.printf("Node %d: ", i);
            for (double v : embeddings[i]) System.out.printf("%.4f ", v);
            System.out.println();
        }
    }
}

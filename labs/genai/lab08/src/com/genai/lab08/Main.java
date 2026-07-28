package com.genai.lab08;

import java.util.*;

/**
 * Multimodal Models
 * 
 * Demonstrates CLIP-style dual encoder, image patch embedding,
 * cross-modal attention, and contrastive loss in Java.
 */
public class Main {

    /** Image patch embedding: split image into patches, project. */
    static class PatchEmbedding {
        final int patchSize;
        final int dModel;
        final double[][] projection;

        PatchEmbedding(int patchSize, int dModel) {
            this.patchSize = patchSize;
            this.dModel = dModel;
            projection = new double[patchSize * patchSize * 3][dModel];
            Random rng = new Random(42);
            for (int i = 0; i < projection.length; i++)
                for (int j = 0; j < dModel; j++)
                    projection[i][j] = rng.nextGaussian() * 0.02;
        }

        /** Simulate image as 3D array (height x width x RGB). */
        double[][] embed(double[][][] image) {
            int h = image.length, w = image[0].length;
            int numPatchesH = h / patchSize;
            int numPatchesW = w / patchSize;
            double[][] patches = new double[numPatchesH * numPatchesW][dModel];
            int idx = 0;
            for (int i = 0; i < numPatchesH; i++) {
                for (int j = 0; j < numPatchesW; j++) {
                    double[] flat = new double[patchSize * patchSize * 3];
                    int fi = 0;
                    for (int pi = 0; pi < patchSize; pi++) {
                        for (int pj = 0; pj < patchSize; pj++) {
                            for (int c = 0; c < 3; c++) {
                                flat[fi++] = image[i * patchSize + pi][j * patchSize + pj][c];
                            }
                        }
                    }
                    for (int k = 0; k < dModel; k++) {
                        for (int fi2 = 0; fi2 < flat.length; fi2++) {
                            patches[idx][k] += flat[fi2] * projection[fi2][k];
                        }
                    }
                    idx++;
                }
            }
            return patches;
        }
    }

    /** Simple text encoder: hash-based embedding. */
    static class TextEncoder {
        final int dModel;
        final Random rng;
        final Map<String, double[]> embedCache = new HashMap<>();

        TextEncoder(int dModel, long seed) {
            this.dModel = dModel;
            this.rng = new Random(seed);
        }

        double[] encode(String text) {
            return embedCache.computeIfAbsent(text, t -> {
                double[] vec = new double[dModel];
                for (int i = 0; i < dModel; i++) vec[i] = rng.nextGaussian();
                double norm = Math.sqrt(Arrays.stream(vec).map(v -> v * v).sum());
                for (int i = 0; i < dModel; i++) vec[i] /= norm;
                return vec;
            });
        }

        double[] averageEncode(String[] tokens) {
            double[] sum = new double[dModel];
            for (String t : tokens) {
                double[] e = encode(t);
                for (int i = 0; i < dModel; i++) sum[i] += e[i];
            }
            double norm = Math.sqrt(Arrays.stream(sum).map(v -> v * v).sum());
            for (int i = 0; i < dModel; i++) sum[i] /= norm;
            return sum;
        }
    }

    /** Contrastive loss (InfoNCE). */
    static double contrastiveLoss(double[][] imageEmbs, double[][] textEmbs, double temperature) {
        int n = imageEmbs.length;
        double loss = 0.0;
        for (int i = 0; i < n; i++) {
            double[] logits = new double[n];
            for (int j = 0; j < n; j++) {
                double sim = 0.0;
                for (int k = 0; k < imageEmbs[i].length; k++)
                    sim += imageEmbs[i][k] * textEmbs[j][k];
                logits[j] = sim / temperature;
            }
            double max = Double.NEGATIVE_INFINITY;
            for (double l : logits) if (l > max) max = l;
            double sumExp = 0.0;
            for (double l : logits) sumExp += Math.exp(l - max);
            loss += -(logits[i] - max) + Math.log(sumExp);
        }
        return loss / n;
    }

    /** Cross-modal attention: text queries attend to image keys/values. */
    static double[][] crossModalAttention(double[][] textQ, double[][] imgK, double[][] imgV) {
        int tLen = textQ.length, iLen = imgK.length, dk = textQ[0].length;
        double[][] scores = new double[tLen][iLen];
        for (int i = 0; i < tLen; i++) {
            for (int j = 0; j < iLen; j++) {
                double dot = 0.0;
                for (int k = 0; k < dk; k++) dot += textQ[i][k] * imgK[j][k];
                scores[i][j] = dot / Math.sqrt(dk);
            }
        }
        double[][] weights = softmax(scores);
        int dv = imgV[0].length;
        double[][] out = new double[tLen][dv];
        for (int i = 0; i < tLen; i++)
            for (int j = 0; j < dv; j++)
                for (int k = 0; k < iLen; k++)
                    out[i][j] += weights[i][k] * imgV[k][j];
        return out;
    }

    static double[][] softmax(double[][] x) {
        int rows = x.length, cols = x[0].length;
        double[][] r = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            double max = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < cols; j++) if (x[i][j] > max) max = x[i][j];
            double sum = 0.0;
            for (int j = 0; j < cols; j++) { r[i][j] = Math.exp(x[i][j] - max); sum += r[i][j]; }
            for (int j = 0; j < cols; j++) r[i][j] /= sum;
        }
        return r;
    }

    public static void main(String[] args) {
        int dModel = 8;
        int patchSize = 4;
        int imgSize = 8;

        PatchEmbedding patchEmb = new PatchEmbedding(patchSize, dModel);
        TextEncoder textEnc = new TextEncoder(dModel, 99);

        // Simulate 2x2 image (8x8 pixels, 3 channels)
        double[][][] image = new double[imgSize][imgSize][3];
        Random rng = new Random(7);
        for (int i = 0; i < imgSize; i++)
            for (int j = 0; j < imgSize; j++)
                for (int c = 0; c < 3; c++)
                    image[i][j][c] = rng.nextDouble();

        double[][] imgEmb = patchEmb.embed(image);
        System.out.println("=== Patch Embeddings ===");
        System.out.println("Number of patches: " + imgEmb.length);

        String[] tokens = {"a", "cat", "sitting", "on", "mat"};
        double[] textEmb = textEnc.averageEncode(tokens);
        System.out.println("\n=== Text Embedding ===");
        System.out.println("Dim: " + textEmb.length);

        System.out.println("\n=== Contrastive Loss ===");
        double[][] imgBatch = {imgEmb[0], imgEmb[1]};
        double[][] textBatch = {textEmb, textEmb};
        double loss = contrastiveLoss(imgBatch, textBatch, 0.07);
        System.out.printf("Loss: %.4f%n", loss);

        System.out.println("\nMultimodal model concepts validated.");
    }
}

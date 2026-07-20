package com.capstone.vectordb;

public class CosineSimilarity {
    private CosineSimilarity() {}

    public static float compute(float[] a, float[] b) {
        if (a.length != b.length) throw new IllegalArgumentException("Vector dimensions must match");
        float dotProduct = 0f, normA = 0f, normB = 0f;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        float magnitude = (float) (Math.sqrt(normA) * Math.sqrt(normB));
        return magnitude == 0 ? 0 : dotProduct / magnitude;
    }

    public static float computeSquared(float[] a, float[] b) {
        float sim = compute(a, b);
        return sim * sim;
    }

    public static float[] normalize(float[] vector) {
        float norm = 0f;
        for (float v : vector) norm += v * v;
        norm = (float) Math.sqrt(norm);
        if (norm == 0) return vector.clone();
        float[] normalized = new float[vector.length];
        for (int i = 0; i < vector.length; i++) normalized[i] = vector[i] / norm;
        return normalized;
    }

    public static float[][] normalizeBatch(float[][] vectors) {
        float[][] result = new float[vectors.length][];
        for (int i = 0; i < vectors.length; i++) result[i] = normalize(vectors[i]);
        return result;
    }

    public static float[] centroid(float[][] vectors) {
        if (vectors.length == 0) throw new IllegalArgumentException("Empty vectors");
        int dim = vectors[0].length;
        float[] center = new float[dim];
        for (float[] vec : vectors) {
            for (int i = 0; i < dim; i++) center[i] += vec[i];
        }
        for (int i = 0; i < dim; i++) center[i] /= vectors.length;
        return center;
    }
}

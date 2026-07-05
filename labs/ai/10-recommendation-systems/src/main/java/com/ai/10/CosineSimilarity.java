package com.ai10;

public class CosineSimilarity {

    public static double cosineSimilarity(double[] a, double[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static double[][] similarityMatrix(double[][] data) {
        int n = data.length;
        double[][] sim = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                sim[i][j] = cosineSimilarity(data[i], data[j]);
        return sim;
    }

    public static int[] mostSimilar(double[][] data, int queryIdx, int topK) {
        int n = data.length;
        double[] sims = new double[n];
        int[] indices = new int[n];
        for (int i = 0; i < n; i++) {
            sims[i] = cosineSimilarity(data[queryIdx], data[i]);
            indices[i] = i;
        }
        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++)
                if (sims[j] < sims[j + 1]) {
                    double ts = sims[j]; sims[j] = sims[j + 1]; sims[j + 1] = ts;
                    int ti = indices[j]; indices[j] = indices[j + 1]; indices[j + 1] = ti;
                }
        int[] result = new int[Math.min(topK, n)];
        System.arraycopy(indices, 0, result, 0, result.length);
        return result;
    }

    public static void main(String[] args) {
        System.out.println("=== Cosine Similarity Demo ===");
        double[][] users = {
            {5, 3, 0, 1}, {4, 0, 0, 1}, {1, 1, 0, 5},
            {1, 0, 0, 4}, {0, 1, 5, 4}
        };
        System.out.println("User-item matrix:");
        for (double[] u : users) System.out.println("  " + java.util.Arrays.toString(u));
        double[][] simMatrix = similarityMatrix(users);
        System.out.println("Similarity matrix:");
        for (double[] row : simMatrix) System.out.println("  " + java.util.Arrays.toString(row));
        int[] similar = mostSimilar(users, 0, 3);
        System.out.println("Most similar to user 0: " + java.util.Arrays.toString(similar));
    }
}

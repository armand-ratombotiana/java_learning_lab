package com.ai17;

import java.util.*;

public class EmbeddingSimilarity {
    private Map<String, double[]> documentEmbeddings;

    public EmbeddingSimilarity() {
        this.documentEmbeddings = new HashMap<>();
    }

    public void addEmbedding(String docId, double[] embedding) {
        documentEmbeddings.put(docId, embedding);
    }

    public double computeSimilarity(String id1, String id2) {
        double[] e1 = documentEmbeddings.get(id1);
        double[] e2 = documentEmbeddings.get(id2);
        if (e1 == null || e2 == null) return -1;
        return cosineSimilarity(e1, e2);
    }

    private double cosineSimilarity(double[] a, double[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10);
    }

    public double[][] computeAllSimilarities() {
        int n = documentEmbeddings.size();
        List<String> ids = new ArrayList<>(documentEmbeddings.keySet());
        double[][] matrix = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                matrix[i][j] = cosineSimilarity(documentEmbeddings.get(ids.get(i)), documentEmbeddings.get(ids.get(j)));
        return matrix;
    }

    public double[] embedQuery(String query) {
        double[] emb = new double[10];
        Random rng = new Random(query.hashCode() & 0x7fffffff);
        for (int i = 0; i < emb.length; i++) emb[i] = rng.nextDouble();
        return emb;
    }

    public static void main(String[] args) {
        System.out.println("=== Embedding Similarity Demo ===");
        EmbeddingSimilarity es = new EmbeddingSimilarity();
        Random rng = new Random(42);
        for (String doc : List.of("ML basics", "Neural networks", "NLP concepts", "CV techniques")) {
            double[] emb = new double[10];
            for (int i = 0; i < 10; i++) emb[i] = rng.nextDouble();
            es.addEmbedding(doc, emb);
        }
        double[][] simMatrix = es.computeAllSimilarities();
        System.out.println("Similarity matrix:");
        for (double[] row : simMatrix) {
            for (double v : row) System.out.printf("%.3f ", v);
            System.out.println();
        }
        double[] queryEmb = es.embedQuery("deep learning");
        System.out.println("Query embedding (hash-based): " + Arrays.toString(queryEmb));
    }
}

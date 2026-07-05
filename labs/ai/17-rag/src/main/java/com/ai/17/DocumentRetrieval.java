package com.ai17;

import java.util.*;

public class DocumentRetrieval {
    private List<String> documents;
    private List<double[]> embeddings;
    private Random rng;

    public DocumentRetrieval() {
        this.documents = new ArrayList<>();
        this.embeddings = new ArrayList<>();
        this.rng = new Random(42);
    }

    public void addDocument(String doc, double[] embedding) {
        documents.add(doc);
        embeddings.add(embedding);
    }

    public void addDocument(String doc) {
        double[] emb = new double[10];
        for (int i = 0; i < emb.length; i++) emb[i] = rng.nextDouble();
        documents.add(doc);
        embeddings.add(emb);
    }

    public List<Map.Entry<String, Double>> retrieve(String query, double[] queryEmbedding, int topK) {
        List<Map.Entry<String, Double>> results = new ArrayList<>();
        for (int i = 0; i < documents.size(); i++) {
            double sim = cosineSimilarity(queryEmbedding, embeddings.get(i));
            results.add(new AbstractMap.SimpleEntry<>(documents.get(i), sim));
        }
        results.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return results.subList(0, Math.min(topK, results.size()));
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

    public List<String> chunkDocument(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split("\\s+");
        int start = 0;
        while (start < words.length) {
            int end = Math.min(start + chunkSize, words.length);
            StringBuilder chunk = new StringBuilder();
            for (int i = start; i < end; i++) {
                if (i > start) chunk.append(" ");
                chunk.append(words[i]);
            }
            chunks.add(chunk.toString());
            start += chunkSize - overlap;
        }
        return chunks;
    }

    public static void main(String[] args) {
        System.out.println("=== Document Retrieval Demo ===");
        DocumentRetrieval dr = new DocumentRetrieval();
        dr.addDocument("Machine learning is a subset of artificial intelligence");
        dr.addDocument("Deep learning uses neural networks with many layers");
        dr.addDocument("Natural language processing enables computers to understand text");
        dr.addDocument("Computer vision allows machines to interpret images");
        double[] queryEmb = new double[10];
        Random rng = new Random(42);
        for (int i = 0; i < 10; i++) queryEmb[i] = rng.nextDouble() * 0.8 + 0.1;
        List<Map.Entry<String, Double>> results = dr.retrieve("neural networks", queryEmb, 3);
        System.out.println("Query: neural networks");
        for (Map.Entry<String, Double> r : results)
            System.out.println("  score=" + String.format("%.4f", r.getValue()) + " " + r.getKey());
        String sample = "This is a sample document that we want to split into smaller chunks for better retrieval performance";
        List<String> chunks = dr.chunkDocument(sample, 5, 2);
        System.out.println("Chunks:");
        for (String c : chunks) System.out.println("  " + c);
    }
}

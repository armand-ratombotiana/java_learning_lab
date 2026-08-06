package com.aiengineering.lab02;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates vector database integration concepts:
 * embedding storage, similarity search (cosine & Euclidean),
 * and indexing strategies (flat, IVF, HNSW simulation).
 * <p>
 * Uses float[] vectors to represent embeddings and provides
 * in-memory collections with configurable distance metrics.
 */
public class VectorDatabaseDemo {

    // ---------- Distance Metrics ----------

    @FunctionalInterface
    interface DistanceFunction {
        double distance(float[] a, float[] b);
    }

    static final DistanceFunction COSINE = (a, b) -> {
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        return 1.0 - (dot / (Math.sqrt(na) * Math.sqrt(nb)));
    };

    static final DistanceFunction EUCLIDEAN = (a, b) -> {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double d = a[i] - b[i];
            sum += d * d;
        }
        return Math.sqrt(sum);
    };

    // ---------- Vector Record ----------

    /** A stored vector with metadata. */
    public record VectorRecord(String id, float[] vector, String metadata) {}

    // ---------- Index Strategies ----------

    /** Flat (brute-force) index — exhaustive search. */
    static class FlatIndex {
        private final List<VectorRecord> records = new ArrayList<>();
        private final DistanceFunction distance;

        FlatIndex(DistanceFunction distance) { this.distance = distance; }

        void insert(VectorRecord record) { records.add(record); }

        List<ScoredResult> search(float[] query, int k) {
            return records.stream()
                .map(r -> new ScoredResult(r, distance.distance(query, r.vector())))
                .sorted(Comparator.comparingDouble(ScoredResult::score))
                .limit(k)
                .toList();
        }

        int size() { return records.size(); }
    }

    /** IVF-like index — partitions vectors into clusters for approximate search. */
    static class IVFIndex {
        private final List<FlatIndex> partitions;
        private final List<float[]> centroids;
        private final DistanceFunction distance;

        IVFIndex(int numPartitions, int dimensions, DistanceFunction distance) {
            this.partitions = new ArrayList<>();
            this.centroids = new ArrayList<>();
            this.distance = distance;
            for (int i = 0; i < numPartitions; i++) {
                partitions.add(new FlatIndex(distance));
                centroids.add(randomVector(dimensions, 100));
            }
        }

        void insert(VectorRecord record) {
            int nearest = 0;
            double best = Double.MAX_VALUE;
            for (int i = 0; i < centroids.size(); i++) {
                double d = distance.distance(record.vector(), centroids.get(i));
                if (d < best) { best = d; nearest = i; }
            }
            partitions.get(nearest).insert(record);
        }

        List<ScoredResult> search(float[] query, int k) {
            // Search nearest 2 partitions only (approximate)
            int[] topCentroids = IntStream.range(0, centroids.size())
                .mapToObj(i -> new ScoredResult(null, distance.distance(query, centroids.get(i))))
                .sorted(Comparator.comparingDouble(ScoredResult::score))
                .limit(2)
                .mapToInt(sr -> IntStream.range(0, centroids.size())
                    .filter(i -> distance.distance(query, centroids.get(i)) == sr.score())
                    .findFirst().orElse(0))
                .toArray();

            return Arrays.stream(topCentroids)
                .mapToObj(i -> partitions.get(i).search(query, k).stream())
                .flatMap(s -> s)
                .sorted(Comparator.comparingDouble(ScoredResult::score))
                .limit(k)
                .toList();
        }
    }

    // ---------- Results ----------

    public record ScoredResult(VectorRecord record, double score) {}

    // ---------- Helpers ----------

    private static float[] randomVector(int dims, int seed) {
        Random rng = new Random(seed);
        float[] v = new float[dims];
        for (int i = 0; i < dims; i++) v[i] = rng.nextFloat();
        return v;
    }

    // ---------- Main Demo ----------

    public static void main(String[] args) {
        System.out.println("=== AI Engineering Academy — Lab 02: Vector Database Integration ===\n");

        int dims = 4;
        List<VectorRecord> docs = Arrays.asList(
            new VectorRecord("doc1", new float[]{0.1f, 0.2f, 0.3f, 0.4f}, "AI transformers"),
            new VectorRecord("doc2", new float[]{0.9f, 0.8f, 0.7f, 0.6f}, "Deep learning"),
            new VectorRecord("doc3", new float[]{0.2f, 0.1f, 0.5f, 0.3f}, "Attention mechanism"),
            new VectorRecord("doc4", new float[]{0.8f, 0.9f, 0.6f, 0.7f}, "Reinforcement learning"),
            new VectorRecord("doc5", new float[]{0.3f, 0.4f, 0.1f, 0.2f}, "Natural language processing")
        );

        // ---- Flat Index ----
        System.out.println("--- Flat Index (Cosine) ---");
        FlatIndex flat = new FlatIndex(COSINE);
        docs.forEach(flat::insert);

        float[] query = new float[]{0.15f, 0.25f, 0.35f, 0.45f};
        System.out.println("Query: [0.15, 0.25, 0.35, 0.45]");
        for (ScoredResult r : flat.search(query, 3)) {
            System.out.printf("  %s (id=%s, distance=%.4f)%n", r.record().metadata(), r.record().id(), r.score());
        }

        // ---- IVF Index ----
        System.out.println("\n--- IVF Index (Approximate) ---");
        IVFIndex ivf = new IVFIndex(3, dims, EUCLIDEAN);
        docs.forEach(ivf::insert);
        System.out.println("Query: [0.15, 0.25, 0.35, 0.45]");
        for (ScoredResult r : ivf.search(query, 3)) {
            System.out.printf("  %s (id=%s, distance=%.4f)%n", r.record().metadata(), r.record().id(), r.score());
        }

        System.out.println("\nDemo complete. Index contains " + flat.size() + " vectors.");
    }
}

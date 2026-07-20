package com.capstone.vectordb;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

public class VectorIndex {
    private final List<float[]> vectors = new CopyOnWriteArrayList<>();
    private final List<String> ids = new CopyOnWriteArrayList<>();
    private final Map<String, Integer> idIndex = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> metadata = new ConcurrentHashMap<>();
    private final SimilarityFunction similarityFn;
    private volatile boolean built = false;

    @FunctionalInterface
    public interface SimilarityFunction {
        float calculate(float[] a, float[] b);
    }

    public enum Metric { COSINE, L2, INNER_PRODUCT }

    public VectorIndex() { this(Metric.COSINE); }

    public VectorIndex(Metric metric) {
        this.similarityFn = switch (metric) {
            case COSINE -> VectorIndex::cosineSimilarity;
            case L2 -> VectorIndex::l2Distance;
            case INNER_PRODUCT -> VectorIndex::innerProduct;
        };
    }

    public void insert(String id, float[] vector) {
        insert(id, vector, Map.of());
    }

    public void insert(String id, float[] vector, Map<String, String> meta) {
        if (idIndex.containsKey(id)) {
            int idx = idIndex.get(id);
            vectors.set(idx, vector);
        } else {
            idIndex.put(id, vectors.size());
            ids.add(id);
            vectors.add(vector);
        }
        metadata.put(id, meta);
        built = false;
    }

    public void delete(String id) {
        Integer idx = idIndex.remove(id);
        if (idx != null) {
            ids.set(idx, null);
            vectors.set(idx, null);
            metadata.remove(id);
        }
    }

    public Optional<float[]> getVector(String id) {
        Integer idx = idIndex.get(id);
        if (idx == null) return Optional.empty();
        return Optional.ofNullable(vectors.get(idx));
    }

    public Optional<Map<String, String>> getMetadata(String id) {
        return Optional.ofNullable(metadata.get(id));
    }

    public void updateMetadata(String id, Map<String, String> meta) {
        if (idIndex.containsKey(id)) metadata.put(id, meta);
    }

    public record SearchResult(String id, float similarity) {}

    public List<SearchResult> search(float[] query, int k) {
        return search(query, k, null);
    }

    public List<SearchResult> search(float[] query, int k, Map<String, String> filter) {
        PriorityQueue<SearchResult> heap = new PriorityQueue<>(
            Comparator.comparingDouble(SearchResult::similarity));

        for (int i = 0; i < vectors.size(); i++) {
            float[] vec = vectors.get(i);
            if (vec == null) continue;
            String id = ids.get(i);
            if (filter != null && !matchesFilter(id, filter)) continue;
            float sim = similarityFn.calculate(query, vec);
            heap.offer(new SearchResult(id, sim));
            if (heap.size() > k) heap.poll();
        }

        List<SearchResult> results = new ArrayList<>();
        while (!heap.isEmpty()) results.add(heap.poll());
        Collections.reverse(results);
        return results;
    }

    public int size() { return (int) vectors.stream().filter(Objects::nonNull).count(); }

    public void clear() { vectors.clear(); ids.clear(); idIndex.clear(); metadata.clear(); built = false; }

    public boolean isBuilt() { return built; }

    private boolean matchesFilter(String id, Map<String, String> filter) {
        Map<String, String> meta = metadata.get(id);
        if (meta == null) return false;
        for (Map.Entry<String, String> f : filter.entrySet()) {
            if (!f.getValue().equals(meta.get(f.getKey()))) return false;
        }
        return true;
    }

    public static float cosineSimilarity(float[] a, float[] b) {
        float dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        float denom = (float)(Math.sqrt(normA) * Math.sqrt(normB));
        return denom == 0 ? 0 : dot / denom;
    }

    public static float l2Distance(float[] a, float[] b) {
        float sum = 0;
        for (int i = 0; i < a.length; i++) {
            float diff = a[i] - b[i];
            sum += diff * diff;
        }
        return (float) -Math.sqrt(sum);
    }

    public static float innerProduct(float[] a, float[] b) {
        float dot = 0;
        for (int i = 0; i < a.length; i++) dot += a[i] * b[i];
        return dot;
    }
}

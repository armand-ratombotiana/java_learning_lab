package com.capstone.rag;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class VectorStore {
    private final EmbeddingInterface embeddingFn;
    private final Map<String, IndexEntry> entries = new ConcurrentHashMap<>();
    private final List<String> entryOrder = new CopyOnWriteArrayList<>();

    public record IndexEntry(String id, float[] vector, String text, Map<String, String> metadata) {
        public IndexEntry { metadata = metadata == null ? Map.of() : Map.copyOf(metadata); }
    }

    public record SearchResult(String id, float score, String text, Map<String, String> metadata) {}

    public VectorStore(EmbeddingInterface embeddingFn) {
        this.embeddingFn = embeddingFn;
    }

    public void addText(String id, String text, Map<String, String> metadata) {
        float[] vector = embeddingFn.embed(text);
        entries.put(id, new IndexEntry(id, vector, text, metadata));
        entryOrder.add(id);
    }

    public void addTexts(List<String> ids, List<String> texts, List<Map<String, String>> metadatas) {
        List<float[]> vectors = embeddingFn.embedBatch(texts);
        for (int i = 0; i < ids.size(); i++) {
            entries.put(ids.get(i), new IndexEntry(ids.get(i), vectors.get(i), texts.get(i),
                metadatas != null && i < metadatas.size() ? metadatas.get(i) : Map.of()));
            entryOrder.add(ids.get(i));
        }
    }

    public List<SearchResult> search(String query, int k) {
        return search(query, k, null);
    }

    public List<SearchResult> search(String query, int k, Map<String, String> filter) {
        float[] queryVec = embeddingFn.embed(query);
        PriorityQueue<SearchResult> heap = new PriorityQueue<>(
            Comparator.comparingDouble(SearchResult::score));
        for (Map.Entry<String, IndexEntry> e : entries.entrySet()) {
            IndexEntry entry = e.getValue();
            if (filter != null && !matchesFilter(entry.metadata(), filter)) continue;
            float score = cosineSimilarity(queryVec, entry.vector());
            heap.offer(new SearchResult(entry.id(), score, entry.text(), entry.metadata()));
            if (heap.size() > k) heap.poll();
        }
        List<SearchResult> results = new ArrayList<>();
        while (!heap.isEmpty()) results.add(heap.poll());
        Collections.reverse(results);
        return results;
    }

    public Optional<IndexEntry> get(String id) {
        return Optional.ofNullable(entries.get(id));
    }

    public boolean delete(String id) {
        entryOrder.remove(id);
        return entries.remove(id) != null;
    }

    public int size() { return entries.size(); }
    public void clear() { entries.clear(); entryOrder.clear(); }

    private boolean matchesFilter(Map<String, String> entryMeta, Map<String, String> filter) {
        for (Map.Entry<String, String> f : filter.entrySet()) {
            if (!f.getValue().equals(entryMeta.get(f.getKey()))) return false;
        }
        return true;
    }

    private float cosineSimilarity(float[] a, float[] b) {
        float dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i]; na += a[i] * a[i]; nb += b[i] * b[i];
        }
        return (float) (dot / (Math.sqrt(na) * Math.sqrt(nb)));
    }
}

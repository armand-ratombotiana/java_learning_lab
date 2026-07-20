package com.capstone.vectordb;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class VectorStore {
    private final VectorIndex index;
    private final HNSWGraph hnsw;
    private final Path persistencePath;
    private final Map<String, Map<String, String>> metadataIndex = new ConcurrentHashMap<>();
    private final AtomicLong version = new AtomicLong(0);

    public VectorStore() {
        this.index = new VectorIndex();
        this.hnsw = new HNSWGraph(index);
        this.persistencePath = Path.of("vectordb_data");
    }

    public VectorStore(Path persistencePath) {
        this.index = new VectorIndex();
        this.hnsw = new HNSWGraph(index);
        this.persistencePath = persistencePath;
    }

    public void insert(String id, float[] vector) {
        insert(id, vector, Map.of());
    }

    public void insert(String id, float[] vector, Map<String, String> metadata) {
        index.insert(id, vector, metadata);
        hnsw.insert(id, vector);
        if (!metadata.isEmpty()) metadataIndex.put(id, metadata);
        version.incrementAndGet();
    }

    public boolean delete(String id) {
        index.delete(id);
        metadataIndex.remove(id);
        version.incrementAndGet();
        return true;
    }

    public Optional<float[]> get(String id) {
        return index.getVector(id);
    }

    public Optional<Map<String, String>> getMetadata(String id) {
        return index.getMetadata(id);
    }

    public List<VectorIndex.SearchResult> search(float[] query, int k) {
        return search(query, k, null, SearchMode.BRUTE_FORCE);
    }

    public List<VectorIndex.SearchResult> search(float[] query, int k, Map<String, String> filter, SearchMode mode) {
        return switch (mode) {
            case BRUTE_FORCE -> index.search(query, k, filter);
            case HNSW -> hnsw.search(query, k, 100);
        };
    }

    public List<VectorIndex.SearchResult> searchWithScore(float[] query, int k, float minScore) {
        var results = index.search(query, k);
        return results.stream().filter(r -> r.similarity() >= minScore).toList();
    }

    public long persist() {
        try {
            Files.createDirectories(persistencePath);
            Path dataFile = persistencePath.resolve("vectors.dat");
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(dataFile)))) {
                oos.writeLong(version.get());
                oos.writeInt(index.size());
                for (int i = 0; i < index.size(); i++) {
                    oos.writeObject(ids.get(i));
                    oos.writeObject(vectors.get(i));
                    oos.writeObject(metadataIndex.get(ids.get(i)));
                }
            }
            return version.get();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to persist vector store", e);
        }
    }

    public long load() {
        Path dataFile = persistencePath.resolve("vectors.dat");
        if (!Files.exists(dataFile)) return 0;
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(dataFile)))) {
            long ver = ois.readLong();
            int count = ois.readInt();
            for (int i = 0; i < count; i++) {
                String id = (String) ois.readObject();
                float[] vec = (float[]) ois.readObject();
                Map<String, String> meta = (Map<String, String>) ois.readObject();
                index.insert(id, vec, meta != null ? meta : Map.of());
                if (meta != null) metadataIndex.put(id, meta);
            }
            version.set(ver);
            return ver;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load vector store", e);
        }
    }

    public long getVersion() { return version.get(); }

    public int size() { return index.size(); }

    public void clear() {
        index.clear();
        metadataIndex.clear();
        version.set(0);
    }

    public enum SearchMode { BRUTE_FORCE, HNSW }

    private final List<String> ids = new ArrayList<>();
    private final List<float[]> vectors = new ArrayList<>();
}

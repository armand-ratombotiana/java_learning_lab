package com.vector.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class VectorStore {

    private final HNSWIndex hnswIndex;
    private final Map<Integer, VectorRecord> records;
    private final Map<String, Set<Integer>> collections;

    public void insert(String collection, int id, double[] vector, Map<String, Object> metadata) {
        VectorRecord record = new VectorRecord(id, vector, metadata, System.currentTimeMillis());
        records.put(id, record);
        hnswIndex.insert(id, vector);
        collections.computeIfAbsent(collection, k -> ConcurrentHashMap.newKeySet()).add(id);
    }

    public List<SearchResult> search(String collection, double[] queryVector, int topK) {
        List<HNSWIndex.SearchResult> results = hnswIndex.search(queryVector, topK, topK);
        List<SearchResult> response = new ArrayList<>();

        for (HNSWIndex.SearchResult result : results) {
            VectorRecord record = records.get(result.id());
            if (record != null) {
                response.add(new SearchResult(result.id(), result.distance(), record.metadata()));
            }
        }

        return response;
    }

    public Optional<VectorRecord> get(int id) {
        return Optional.ofNullable(records.get(id));
    }

    public void delete(int id, String collection) {
        records.remove(id);
        if (collection != null) {
            Set<Integer> coll = collections.get(collection);
            if (coll != null) {
                coll.remove(id);
            }
        }
    }

    public record VectorRecord(
        int id,
        double[] vector,
        Map<String, Object> metadata,
        long timestamp
    ) {}

    public record SearchResult(int id, double distance, Map<String, Object> metadata) {}
}
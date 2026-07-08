package com.arch.apicomposition;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CompositeService {
    private final Map<String, DataFetcher> fetchers = new ConcurrentHashMap<>();

    public void registerFetcher(String name, DataFetcher fetcher) {
        fetchers.put(name, fetcher);
    }

    public CompositeResponse fetchAll(String entityId, String... fetcherNames) {
        CompositeResponse response = new CompositeResponse(entityId);
        for (String name : fetcherNames) {
            DataFetcher fetcher = fetchers.get(name);
            if (fetcher != null) {
                try {
                    response.addData(name, fetcher.fetch(entityId));
                } catch (Exception e) {
                    response.addError(name, e.getMessage());
                }
            }
        }
        return response;
    }
}

class CompositeResponse {
    private final String entityId;
    private final Map<String, Object> data = new HashMap<>();
    private final Map<String, String> errors = new HashMap<>();

    public CompositeResponse(String entityId) { this.entityId = entityId; }
    public void addData(String key, Object value) { data.put(key, value); }
    public void addError(String key, String error) { errors.put(key, error); }
    public Object getData(String key) { return data.get(key); }
    public Map<String, Object> getAllData() { return Map.copyOf(data); }
    public boolean hasErrors() { return !errors.isEmpty(); }
}

interface DataFetcher {
    Object fetch(String entityId);
}

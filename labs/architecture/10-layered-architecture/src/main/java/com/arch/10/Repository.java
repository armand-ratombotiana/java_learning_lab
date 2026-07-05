package com.arch.layered;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Repository {
    private final Map<String, Entity> store = new ConcurrentHashMap<>();

    public void save(Entity entity) {
        store.put(entity.getId(), entity);
        System.out.println("Repository saved: " + entity.getId());
    }

    public Entity findById(String id) {
        return store.get(id);
    }
}

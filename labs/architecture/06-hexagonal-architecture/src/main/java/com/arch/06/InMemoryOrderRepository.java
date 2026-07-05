package com.arch.hexagonal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

public class InMemoryOrderRepository implements OrderRepositoryPort {
    private final Map<String, Order> store = new ConcurrentHashMap<>();

    public void save(Order order) {
        store.put(order.getId(), order);
    }

    public Order findById(String id) {
        return store.get(id);
    }

    public List<Order> findAll() {
        return new ArrayList<>(store.values());
    }
}

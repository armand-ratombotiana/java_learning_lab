package com.arch.platform;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DeveloperPortal {
    private final Map<String, ServiceComponent> catalog = new ConcurrentHashMap<>();
    private final Map<String, List<String>> ownership = new ConcurrentHashMap<>();

    public void registerComponent(ServiceComponent component) {
        catalog.put(component.id(), component);
    }

    public void assignOwner(String componentId, String team) {
        ownership.computeIfAbsent(componentId, k -> new CopyOnWriteArrayList<>()).add(team);
    }

    public Optional<ServiceComponent> getComponent(String id) {
        return Optional.ofNullable(catalog.get(id));
    }

    public List<ServiceComponent> searchByName(String name) {
        return catalog.values().stream()
            .filter(c -> c.name().toLowerCase().contains(name.toLowerCase()))
            .toList();
    }

    public List<ServiceComponent> getComponentsByType(String type) {
        return catalog.values().stream()
            .filter(c -> c.type().equals(type))
            .toList();
    }

    public List<String> getOwners(String componentId) {
        return ownership.getOrDefault(componentId, List.of());
    }

    public List<ServiceComponent> getAllComponents() { return List.copyOf(catalog.values()); }
    public int getComponentCount() { return catalog.size(); }
}

record ServiceComponent(String id, String name, String type, String description, String lifecycle) {}

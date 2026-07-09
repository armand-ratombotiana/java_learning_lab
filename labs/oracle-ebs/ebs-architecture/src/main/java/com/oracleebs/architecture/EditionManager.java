package com.oracleebs.architecture;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EditionManager {
    public enum EditionState { PREPARE, APPLY, FINALIZE, CUTOVER, CLEANUP }

    public static class Edition {
        private final String name;
        private final EditionState state;
        private final Instant created;
        private final String description;

        public Edition(String name, EditionState state, String description) {
            this.name = name;
            this.state = state;
            this.created = Instant.now();
            this.description = description;
        }

        public String getName() { return name; }
        public EditionState getState() { return state; }
        public Instant getCreated() { return created; }
        public String getDescription() { return description; }
    }

    private final Map<String, Edition> editions;
    private String currentEdition;

    public EditionManager() {
        this.editions = new ConcurrentHashMap<>();
        this.currentEdition = "RUNNING";
        Edition base = new Edition("RUNNING", EditionState.CUTOVER, "Base running edition");
        editions.put("RUNNING", base);
    }

    public boolean createEdition(String name, String description) {
        if (editions.containsKey(name)) return false;
        if (currentEdition.equals("PATCHING")) return false;
        Edition ed = new Edition(name, EditionState.PREPARE, description);
        editions.put(name, ed);
        return true;
    }

    public EditionState transitionEdition(String name, EditionState newState) {
        Edition ed = editions.get(name);
        if (ed == null) throw new IllegalArgumentException("Edition not found: " + name);
        Edition updated = new Edition(name, newState, ed.getDescription());
        editions.put(name, updated);
        return newState;
    }

    public boolean setCurrentEdition(String name) {
        if (!editions.containsKey(name)) return false;
        this.currentEdition = name;
        return true;
    }

    public Optional<Edition> getEdition(String name) {
        return Optional.ofNullable(editions.get(name));
    }

    public String getCurrentEdition() {
        return currentEdition;
    }

    public boolean isOnlinePatching() {
        return editions.values().stream().anyMatch(e -> e.getState() == EditionState.PREPARE || e.getState() == EditionState.APPLY);
    }

    public int getEditionCount() {
        return editions.size();
    }

    public List<Edition> getEditionsByState(EditionState state) {
        return editions.values().stream()
            .filter(e -> e.getState() == state)
            .toList();
    }
}

package com.capstone.agent;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class AgentMemory {
    private final List<MemoryEntry> shortTerm = new CopyOnWriteArrayList<>();
    private final List<MemoryEntry> longTerm = new CopyOnWriteArrayList<>();
    private final Map<String, List<MemoryEntry>> episodicIndex = new ConcurrentHashMap<>();
    private static final int SHORT_TERM_LIMIT = 100;

    public record MemoryEntry(String content, String type, double importance, long timestamp, Map<String, String> metadata) {
        public MemoryEntry { metadata = metadata == null ? Map.of() : Map.copyOf(metadata); }
    }

    public void addToShortTerm(String content) {
        addToShortTerm(content, "observation", 0.5);
    }

    public void addToShortTerm(String content, String type, double importance) {
        MemoryEntry entry = new MemoryEntry(content, type, importance, System.currentTimeMillis(), Map.of());
        shortTerm.add(entry);
        if (shortTerm.size() > SHORT_TERM_LIMIT) consolidate();
    }

    public void addToLongTerm(String content) {
        MemoryEntry entry = new MemoryEntry(content, "experience", 1.0, System.currentTimeMillis(), Map.of());
        longTerm.add(entry);
        String key = content.length() > 20 ? content.substring(0, 20) : content;
        episodicIndex.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(entry);
    }

    public List<String> getShortTerm() {
        return shortTerm.stream().map(MemoryEntry::content).collect(Collectors.toList());
    }

    public List<String> getLongTerm() {
        return longTerm.stream().map(MemoryEntry::content).collect(Collectors.toList());
    }

    public List<MemoryEntry> recall(String query) {
        List<MemoryEntry> results = new ArrayList<>();
        String q = query.toLowerCase();
        for (MemoryEntry entry : longTerm) {
            if (entry.content().toLowerCase().contains(q)) results.add(entry);
        }
        for (MemoryEntry entry : shortTerm) {
            if (entry.content().toLowerCase().contains(q) && !results.contains(entry)) results.add(entry);
        }
        results.sort(Comparator.comparingDouble(MemoryEntry::importance).reversed());
        return results;
    }

    public List<MemoryEntry> getEpisodicMemory(String key) {
        return List.copyOf(episodicIndex.getOrDefault(key, List.of()));
    }

    public int shortTermSize() { return shortTerm.size(); }
    public int longTermSize() { return longTerm.size(); }

    public void consolidate() {
        while (shortTerm.size() > SHORT_TERM_LIMIT * 0.8) {
            MemoryEntry oldest = shortTerm.remove(0);
            longTerm.add(oldest);
        }
    }

    public void clear() { shortTerm.clear(); longTerm.clear(); episodicIndex.clear(); }
}

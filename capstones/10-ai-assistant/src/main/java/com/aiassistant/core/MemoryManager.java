package com.aiassistant.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemoryManager {

    private final Map<String, List<Memory.MemoryEntry>> shortTermMemory;
    private final Map<String, List<Memory.MemoryEntry>> longTermMemory;

    private static final int SHORT_TERM_MAX = 10;

    public MemoryManager() {
        this.shortTermMemory = new HashMap<>();
        this.longTermMemory = new HashMap<>();
    }

    public void addToShortTerm(String userId, String content) {
        shortTermMemory.computeIfAbsent(userId, k -> new ArrayList<>())
            .add(new Memory.MemoryEntry(content, System.currentTimeMillis()));

        List<Memory.MemoryEntry> entries = shortTermMemory.get(userId);
        if (entries.size() > SHORT_TERM_MAX) {
            consolidateToLongTerm(userId);
        }
    }

    public void addToLongTerm(String userId, String content, double importance) {
        longTermMemory.computeIfAbsent(userId, k -> new ArrayList<>())
            .add(new Memory.MemoryEntry(content, System.currentTimeMillis(), importance));
    }

    private void consolidateToLongTerm(String userId) {
        List<Memory.MemoryEntry> shortTerm = shortTermMemory.get(userId);
        if (shortTerm != null && !shortTerm.isEmpty()) {
            longTermMemory.computeIfAbsent(userId, k -> new ArrayList<>())
                .addAll(shortTerm.subList(0, shortTerm.size() / 2));
            shortTermMemory.put(userId, new ArrayList<>(shortTerm.subList(shortTerm.size() / 2, shortTerm.size())));
        }
    }

    public List<String> getRecentContext(String userId, int maxMessages) {
        List<Memory.MemoryEntry> shortTerm = shortTermMemory.getOrDefault(userId, List.of());
        List<String> context = new ArrayList<>();

        int start = Math.max(0, shortTerm.size() - maxMessages);
        for (int i = start; i < shortTerm.size(); i++) {
            context.add(shortTerm.get(i).content());
        }

        return context;
    }

    public List<String> searchLongTerm(String userId, String query) {
        List<Memory.MemoryEntry> longTerm = longTermMemory.getOrDefault(userId, List.of());
        
        return longTerm.stream()
            .filter(e -> e.content().toLowerCase().contains(query.toLowerCase()))
            .map(Memory.MemoryEntry::content)
            .toList();
    }

    public void clearShortTerm(String userId) {
        shortTermMemory.remove(userId);
    }

    public record MemoryEntry(String content, long timestamp, double importance) {
        public MemoryEntry(String content, long timestamp) {
            this(content, timestamp, 0.5);
        }
    }
}
package com.ai19;

import java.util.*;

public class AgentMemory {
    private List<Map.Entry<String, String>> shortTerm;
    private List<Map.Entry<String, String>> longTerm;
    private int shortTermCapacity;

    public AgentMemory(int shortTermCapacity) {
        this.shortTerm = new ArrayList<>();
        this.longTerm = new ArrayList<>();
        this.shortTermCapacity = shortTermCapacity;
    }

    public void remember(String key, String value) {
        shortTerm.add(new AbstractMap.SimpleEntry<>(key, value));
        if (shortTerm.size() > shortTermCapacity) {
            Map.Entry<String, String> oldest = shortTerm.remove(0);
            longTerm.add(oldest);
        }
        if (longTerm.size() > 100) longTerm.remove(0);
    }

    public String recall(String key) {
        for (int i = shortTerm.size() - 1; i >= 0; i--)
            if (shortTerm.get(i).getKey().equals(key))
                return shortTerm.get(i).getValue();
        for (int i = longTerm.size() - 1; i >= 0; i--)
            if (longTerm.get(i).getKey().equals(key))
                return longTerm.get(i).getValue();
        return null;
    }

    public String recallRecent(String key, int n) {
        int count = 0;
        for (int i = shortTerm.size() - 1; i >= 0 && count < n; i--) {
            if (shortTerm.get(i).getKey().equals(key)) {
                return shortTerm.get(i).getValue();
            }
        }
        return null;
    }

    public void consolidate() {
        for (Map.Entry<String, String> entry : shortTerm) {
            boolean found = false;
            for (Map.Entry<String, String> lt : longTerm)
                if (lt.getKey().equals(entry.getKey())) { found = true; break; }
            if (!found) longTerm.add(entry);
        }
        shortTerm.clear();
    }

    public String getContext() {
        StringBuilder ctx = new StringBuilder("Recent context:\n");
        for (Map.Entry<String, String> e : shortTerm)
            ctx.append("- ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        return ctx.toString();
    }

    public void clearShortTerm() { shortTerm.clear(); }

    public static void main(String[] args) {
        System.out.println("=== Agent Memory Demo ===");
        AgentMemory mem = new AgentMemory(3);
        mem.remember("name", "Agent-42");
        mem.remember("task", "Calculate 5+3");
        mem.remember("result", "8");
        System.out.println("Recall 'name': " + mem.recall("name"));
        System.out.println("Recall 'task': " + mem.recall("task"));
        mem.remember("next_task", "Search documents");
        mem.remember("search_query", "machine learning");
        System.out.println("Short-term memory context:");
        System.out.println(mem.getContext());
        mem.consolidate();
        System.out.println("After consolidation, recall 'name': " + mem.recall("name"));
    }
}

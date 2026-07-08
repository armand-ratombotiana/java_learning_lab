package com.dataeng.eleven;

import java.util.*;

public class DagValidator {
    private final Map<String, List<String>> adjacency = new HashMap<>();

    public void addEdge(String from, String to) {
        adjacency.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        adjacency.putIfAbsent(to, new ArrayList<>());
    }

    public boolean hasCycle() {
        Set<String> white = new HashSet<>(adjacency.keySet());
        Set<String> grey = new HashSet<>();
        Set<String> black = new HashSet<>();
        for (String node : List.copyOf(white)) {
            if (dfs(node, white, grey, black)) return true;
        }
        return false;
    }

    private boolean dfs(String node, Set<String> white, Set<String> grey, Set<String> black) {
        white.remove(node);
        grey.add(node);
        for (String neighbor : adjacency.getOrDefault(node, Collections.emptyList())) {
            if (black.contains(neighbor)) continue;
            if (grey.contains(neighbor)) return true;
            if (dfs(neighbor, white, grey, black)) return true;
        }
        grey.remove(node);
        black.add(node);
        return false;
    }

    public List<String> topologicalSort() {
        Map<String, Integer> inDegree = new HashMap<>();
        for (String node : adjacency.keySet()) inDegree.put(node, 0);
        for (List<String> edges : adjacency.values()) {
            for (String n : edges) inDegree.merge(n, 1, Integer::sum);
        }

        Queue<String> queue = new LinkedList<>();
        inDegree.forEach((node, deg) -> { if (deg == 0) queue.add(node); });

        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String node = queue.poll();
            result.add(node);
            for (String n : adjacency.getOrDefault(node, Collections.emptyList())) {
                inDegree.merge(n, -1, Integer::sum);
                if (inDegree.get(n) == 0) queue.add(n);
            }
        }
        return result;
    }

    public int estimateDuration(Map<String, Integer> durations) {
        Map<String, Integer> earliest = new HashMap<>();
        for (String node : topologicalSort()) {
            int maxPred = 0;
            for (Map.Entry<String, List<String>> e : adjacency.entrySet()) {
                if (e.getValue().contains(node)) {
                    maxPred = Math.max(maxPred, earliest.getOrDefault(e.getKey(), 0));
                }
            }
            earliest.put(node, maxPred + durations.getOrDefault(node, 0));
        }
        return earliest.values().stream().mapToInt(v -> v).max().orElse(0);
    }
}

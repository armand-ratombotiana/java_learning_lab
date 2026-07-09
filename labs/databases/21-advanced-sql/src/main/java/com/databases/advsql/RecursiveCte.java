package com.databases.advsql;

import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

public class RecursiveCte {

    public record Edge(int from, int to) {}
    public record TreeNode(int id, String name, int parentId) {}
    public record OrgNode(int id, String name, int parentId, int level, String path) {}

    public static List<OrgNode> traverseTree(List<TreeNode> nodes, int rootId) {
        var children = nodes.stream().collect(Collectors.groupingBy(TreeNode::parentId, Collectors.toList()));
        var result = new ArrayList<OrgNode>();
        traverse(rootId, 1, "", children, result);
        return result;
    }

    private static void traverse(int id, int level, String path, Map<Integer, List<TreeNode>> children, List<OrgNode> result) {
        var node = children.values().stream().flatMap(List::stream)
            .filter(n -> n.id() == id).findFirst().orElse(null);
        if (node == null) return;
        String newPath = path.isEmpty() ? node.name() : path + " / " + node.name();
        result.add(new OrgNode(node.id(), node.name(), node.parentId(), level, newPath));
        var kids = children.getOrDefault(id, List.of());
        for (var kid : kids) {
            traverse(kid.id(), level + 1, newPath, children, result);
        }
    }

    public static List<List<Integer>> findAllPaths(Map<Integer, List<Integer>> graph, int start, int end) {
        var result = new ArrayList<List<Integer>>();
        var current = new ArrayList<Integer>();
        current.add(start);
        dfsPaths(graph, start, end, current, result, new HashSet<>());
        return result;
    }

    private static void dfsPaths(Map<Integer, List<Integer>> graph, int node, int end,
        List<Integer> path, List<List<Integer>> result, Set<Integer> visited) {
        if (node == end) {
            result.add(new ArrayList<>(path));
            return;
        }
        visited.add(node);
        for (var neighbor : graph.getOrDefault(node, List.of())) {
            if (!visited.contains(neighbor)) {
                path.add(neighbor);
                dfsPaths(graph, neighbor, end, path, result, visited);
                path.remove(path.size() - 1);
            }
        }
        visited.remove(node);
    }

    public static List<Integer> topologicalSort(Map<Integer, List<Integer>> deps) {
        var inDegree = new HashMap<Integer, Long>();
        deps.keySet().forEach(n -> inDegree.putIfAbsent(n, 0L));
        deps.forEach((node, neighbors) -> {
            neighbors.forEach(n -> inDegree.merge(n, 1L, Long::sum));
        });
        var queue = inDegree.entrySet().stream()
            .filter(e -> e.getValue() == 0).map(Map.Entry::getKey)
            .collect(Collectors.toCollection(ArrayDeque::new));
        var result = new ArrayList<Integer>();
        while (!queue.isEmpty()) {
            int node = queue.poll();
            result.add(node);
            for (var neighbor : deps.getOrDefault(node, List.of())) {
                long newDegree = inDegree.merge(neighbor, -1L, (a, b) -> a - 1);
                if (newDegree == 0) queue.add(neighbor);
            }
        }
        return result;
    }

    public static Set<Integer> reachableNodes(Map<Integer, List<Integer>> graph, int start) {
        var visited = new HashSet<Integer>();
        var queue = new ArrayDeque<Integer>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            int node = queue.poll();
            for (var neighbor : graph.getOrDefault(node, List.of())) {
                if (visited.add(neighbor)) queue.add(neighbor);
            }
        }
        return visited;
    }

    public static Map<Integer, List<Integer>> sampleGraph() {
        var g = new HashMap<Integer, List<Integer>>();
        g.put(1, List.of(2, 3));
        g.put(2, List.of(4, 5));
        g.put(3, List.of(6));
        g.put(4, List.of());
        g.put(5, List.of(6));
        g.put(6, List.of());
        return g;
    }

    public static List<TreeNode> sampleOrg() {
        return List.of(
            new TreeNode(1, "CEO", 0),
            new TreeNode(2, "CTO", 1),
            new TreeNode(3, "CFO", 1),
            new TreeNode(4, "Dev Lead", 2),
            new TreeNode(5, "QA Lead", 2),
            new TreeNode(6, "Developer", 4)
        );
    }
}
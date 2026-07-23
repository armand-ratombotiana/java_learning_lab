package com.leetcode.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LeetCode 133: Clone Graph
 * https://leetcode.com/problems/clone-graph/
 *
 * Return a deep copy of an undirected graph.
 * Each node has a value and a list of neighbors.
 *
 * Time Complexity: O(V + E)
 * Space Complexity: O(V)
 */
public class CloneGraph {

    public static class Node {
        public int val;
        public List<Node> neighbors;
        public Node(int val) {
            this.val = val;
            this.neighbors = new ArrayList<>();
        }
    }

    private final Map<Node, Node> visited = new HashMap<>();

    /**
     * Approach 1 (Optimal): DFS with HashMap
     * Clone nodes recursively, track visited to avoid cycles.
     * Time: O(V + E), Space: O(V)
     */
    public Node cloneGraph(Node node) {
        if (node == null) return null;
        if (visited.containsKey(node)) return visited.get(node);

        Node clone = new Node(node.val);
        visited.put(node, clone);

        for (Node neighbor : node.neighbors) {
            clone.neighbors.add(cloneGraph(neighbor));
        }
        return clone;
    }

    public static void main(String[] args) {
        CloneGraph cg = new CloneGraph();

        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);
        n1.neighbors.add(n2); n1.neighbors.add(n4);
        n2.neighbors.add(n1); n2.neighbors.add(n3);
        n3.neighbors.add(n2); n3.neighbors.add(n4);
        n4.neighbors.add(n1); n4.neighbors.add(n3);

        Node cloned = cg.cloneGraph(n1);
        System.out.println("Original: " + n1.val + " -> " + n1.neighbors.stream().map(n -> String.valueOf(n.val)).toList());
        System.out.println("Cloned:   " + cloned.val + " -> " + cloned.neighbors.stream().map(n -> String.valueOf(n.val)).toList());
        System.out.println("Different objects: " + (n1 != cloned) + " (expected: true)");
        System.out.println("Same structure: " + (n1.neighbors.size() == cloned.neighbors.size()) + " (expected: true)");

        // Null
        System.out.println("Null: " + (cg.cloneGraph(null) == null ? "null" : "fail"));
    }
}

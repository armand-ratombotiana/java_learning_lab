# Code Deep Dive: Graph Theory

```java
package com.mathacademy.graph;

import java.util.*;

public class Graph {
    
    private Map<Integer, List<Edge>> adj = new HashMap<>();
    
    public static class Edge {
        int to; double weight;
        public Edge(int to, double weight) { this.to = to; this.weight = weight; }
    }
    
    public void addVertex(int v) { adj.putIfAbsent(v, new ArrayList<>()); }
    
    public void addEdge(int from, int to, double weight) {
        addVertex(from); addVertex(to);
        adj.get(from).add(new Edge(to, weight));
    }
    
    public void addUndirectedEdge(int a, int b, double weight) {
        addEdge(a, b, weight); addEdge(b, a, weight);
    }
    
    public List<Integer> bfs(int start) {
        List<Integer> result = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.add(start); visited.add(start);
        while (!queue.isEmpty()) {
            int v = queue.poll();
            result.add(v);
            for (Edge e : adj.getOrDefault(v, new ArrayList<>())) {
                if (!visited.contains(e.to)) {
                    visited.add(e.to);
                    queue.add(e.to);
                }
            }
        }
        return result;
    }
    
    public List<Integer> dfs(int start) {
        List<Integer> result = new ArrayList<>();
        dfsHelper(start, new HashSet<>(), result);
        return result;
    }
    
    private void dfsHelper(int v, Set<Integer> visited, List<Integer> result) {
        visited.add(v); result.add(v);
        for (Edge e : adj.getOrDefault(v, new ArrayList<>())) {
            if (!visited.contains(e.to)) dfsHelper(e.to, visited, result);
        }
    }
    
    public Map<Integer, Double> dijkstra(int start) {
        Map<Integer, Double> dist = new HashMap<>();
        PriorityQueue<Pair> pq = new PriorityQueue<>(Comparator.comparingDouble(p -> p.dist));
        for (int v : adj.keySet()) dist.put(v, Double.MAX_VALUE);
        dist.put(start, 0.0); pq.add(new Pair(start, 0));
        while (!pq.isEmpty()) {
            Pair p = pq.poll();
            if (p.dist > dist.get(p.vertex)) continue;
            for (Edge e : adj.getOrDefault(p.vertex, new ArrayList<>())) {
                double newDist = dist.get(p.vertex) + e.weight;
                if (newDist < dist.get(e.to)) {
                    dist.put(e.to, newDist);
                    pq.add(new Pair(e.to, newDist));
                }
            }
        }
        return dist;
    }
    
    public List<Edge> kruskalMST() {
        List<Edge> edges = new ArrayList<>();
        for (List<Edge> list : adj.values()) edges.addAll(list);
        edges.sort(Comparator.comparingDouble(e -> e.weight));
        UnionFind uf = new UnionFind(adj.keySet());
        List<Edge> mst = new ArrayList<>();
        for (Edge e : edges) {
            if (uf.find(e.from) != uf.find(e.to)) {
                mst.add(e); uf.union(e.from, e.to);
                if (mst.size() == adj.size() - 1) break;
            }
        }
        return mst;
    }
    
    static class Pair { int vertex; double dist; Pair(int v, double d) { vertex=v; dist=d; } }
    
    static class UnionFind {
        Map<Integer, Integer> parent = new HashMap<>();
        UnionFind(Iterable<Integer> vertices) { for (int v : vertices) parent.put(v, v); }
        int find(int x) { parent.putIfAbsent(x, x); return parent.get(x) == x ? x : (parent.put(x, find(parent.get(x))), parent.get(x)); }
        void union(int a, int b) { a = find(a); b = find(b); if (a != b) parent.put(a, b); }
    }
}
```
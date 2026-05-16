# Code Deep Dive: Graph Theory Implementations in Java

## 1. Graph Data Structures

### 1.1 Graph Representation

```java
package com.mathacademy.graph;

import java.util.*;

public class Graph {
    
    private Map<Integer, List<Edge>> adjacencyList;
    private boolean directed;
    
    public static class Edge {
        public int to;
        public double weight;
        
        public Edge(int to) {
            this(to, 1.0);
        }
        
        public Edge(int to, double weight) {
            this.to = to;
            this.weight = weight;
        }
        
        @Override
        public String toString() {
            return "(" + to + ", " + weight + ")";
        }
    }
    
    public Graph() {
        this.adjacencyList = new HashMap<>();
        this.directed = false;
    }
    
    public Graph(boolean directed) {
        this.adjacencyList = new HashMap<>();
        this.directed = directed;
    }
    
    public void addVertex(int vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }
    
    public void addEdge(int from, int to) {
        addEdge(from, to, 1.0);
    }
    
    public void addEdge(int from, int to, double weight) {
        addVertex(from);
        addVertex(to);
        
        adjacencyList.get(from).add(new Edge(to, weight));
        
        if (!directed) {
            adjacencyList.get(to).add(new Edge(from, weight));
        }
    }
    
    public void removeEdge(int from, int to) {
        List<Edge> fromList = adjacencyList.get(from);
        if (fromList != null) {
            fromList.removeIf(e -> e.to == to);
        }
        
        if (!directed) {
            List<Edge> toList = adjacencyList.get(to);
            if (toList != null) {
                toList.removeIf(e -> e.to == from);
            }
        }
    }
    
    public void removeVertex(int vertex) {
        adjacencyList.remove(vertex);
        
        for (List<Edge> edges : adjacencyList.values()) {
            edges.removeIf(e -> e.to == vertex);
        }
    }
    
    public boolean hasVertex(int vertex) {
        return adjacencyList.containsKey(vertex);
    }
    
    public boolean hasEdge(int from, int to) {
        List<Edge> edges = adjacencyList.get(from);
        if (edges == null) return false;
        
        return edges.stream().anyMatch(e -> e.to == to);
    }
    
    public List<Integer> getVertices() {
        return new ArrayList<>(adjacencyList.keySet());
    }
    
    public List<Edge> getEdges(int from) {
        return new ArrayList<>(adjacencyList.getOrDefault(from, new ArrayList<>()));
    }
    
    public List<Edge> getAllEdges() {
        List<Edge> allEdges = new ArrayList<>();
        
        for (List<Edge> edges : adjacencyList.values()) {
            allEdges.addAll(edges);
        }
        
        if (!directed) {
            Collections.reverse(allEdges);
            Set<String> seen = new HashSet<>();
            allEdges.removeIf(e -> {
                String key = e.to + "-" + e.weight;
                return !seen.add(key);
            });
        }
        
        return allEdges;
    }
    
    public int getDegree(int vertex) {
        if (!adjacencyList.containsKey(vertex)) {
            return 0;
        }
        
        int degree = adjacencyList.get(vertex).size();
        
        if (!directed) {
            degree = degree / 2;
        }
        
        return degree;
    }
    
    public int getInDegree(int vertex) {
        if (!directed) {
            return getDegree(vertex);
        }
        
        int inDegree = 0;
        for (List<Edge> edges : adjacencyList.values()) {
            for (Edge e : edges) {
                if (e.to == vertex) {
                    inDegree++;
                }
            }
        }
        
        return inDegree;
    }
    
    public int getOutDegree(int vertex) {
        if (!directed) {
            return getDegree(vertex);
        }
        
        return adjacencyList.getOrDefault(vertex, new ArrayList<>()).size();
    }
    
    public int getVertexCount() {
        return adjacencyList.size();
    }
    
    public int getEdgeCount() {
        int count = 0;
        for (List<Edge> edges : adjacencyList.values()) {
            count += edges.size();
        }
        
        if (!directed) {
            count /= 2;
        }
        
        return count;
    }
    
    public boolean isDirected() {
        return directed;
    }
}
```

## 2. Graph Traversals

### 2.1 Breadth-First Search (BFS)

```java
package com.mathacademy.graph;

import java.util.*;

public class BreadthFirstSearch {
    
    public static List<Integer> bfs(Graph graph, int start) {
        if (!graph.hasVertex(start)) {
            throw new IllegalArgumentException("Start vertex does not exist");
        }
        
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        
        queue.add(start);
        visited.add(start);
        
        while (!queue.isEmpty()) {
            int current = queue.poll();
            result.add(current);
            
            for (Graph.Edge edge : graph.getEdges(current)) {
                if (!visited.contains(edge.to)) {
                    visited.add(edge.to);
                    queue.add(edge.to);
                }
            }
        }
        
        return result;
    }
    
    public static Map<Integer, Integer> bfsWithDistance(Graph graph, int start) {
        Map<Integer, Integer> distances = new HashMap<>();
        
        if (!graph.hasVertex(start)) {
            return distances;
        }
        
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        
        queue.add(start);
        visited.add(start);
        distances.put(start, 0);
        
        while (!queue.isEmpty()) {
            int current = queue.poll();
            int currentDist = distances.get(current);
            
            for (Graph.Edge edge : graph.getEdges(current)) {
                if (!visited.contains(edge.to)) {
                    visited.add(edge.to);
                    distances.put(edge.to, currentDist + 1);
                    queue.add(edge.to);
                }
            }
        }
        
        return distances;
    }
    
    public static Map<Integer, Integer> bfsWithParent(Graph graph, int start) {
        Map<Integer, Integer> parent = new HashMap<>();
        
        if (!graph.hasVertex(start)) {
            return parent;
        }
        
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        
        queue.add(start);
        visited.add(start);
        
        while (!queue.isEmpty()) {
            int current = queue.poll();
            
            for (Graph.Edge edge : graph.getEdges(current)) {
                if (!visited.contains(edge.to)) {
                    visited.add(edge.to);
                    parent.put(edge.to, current);
                    queue.add(edge.to);
                }
            }
        }
        
        return parent;
    }
    
    public static List<Integer> shortestPath(Graph graph, int start, int end) {
        Map<Integer, Integer> parent = bfsWithParent(graph, start);
        
        if (!parent.containsKey(end) && start != end) {
            return new ArrayList<>();
        }
        
        List<Integer> path = new ArrayList<>();
        int current = end;
        
        while (current != start) {
            path.add(current);
            current = parent.get(current);
        }
        
        path.add(start);
        Collections.reverse(path);
        
        return path;
    }
}
```

### 2.2 Depth-First Search (DFS)

```java
package com.mathacademy.graph;

import java.util.*;

public class DepthFirstSearch {
    
    public static List<Integer> dfs(Graph graph, int start) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        
        dfsHelper(graph, start, visited, result);
        
        return result;
    }
    
    private static void dfsHelper(Graph graph, int vertex, Set<Integer> visited, List<Integer> result) {
        visited.add(vertex);
        result.add(vertex);
        
        for (Graph.Edge edge : graph.getEdges(vertex)) {
            if (!visited.contains(edge.to)) {
                dfsHelper(graph, edge.to, visited, result);
            }
        }
    }
    
    public static List<Integer> dfsIterative(Graph graph, int start) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Stack<Integer> stack = new Stack<>();
        
        stack.push(start);
        
        while (!stack.isEmpty()) {
            int current = stack.pop();
            
            if (!visited.contains(current)) {
                visited.add(current);
                result.add(current);
                
                List<Graph.Edge> edges = graph.getEdges(current);
                for (int i = edges.size() - 1; i >= 0; i--) {
                    int neighbor = edges.get(i).to;
                    if (!visited.contains(neighbor)) {
                        stack.push(neighbor);
                    }
                }
            }
        }
        
        return result;
    }
    
    public static List<List<Integer>> findConnectedComponents(Graph graph) {
        List<List<Integer>> components = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        
        for (int vertex : graph.getVertices()) {
            if (!visited.contains(vertex)) {
                List<Integer> component = new ArrayList<>();
                dfsComponentHelper(graph, vertex, visited, component);
                components.add(component);
            }
        }
        
        return components;
    }
    
    private static void dfsComponentHelper(Graph graph, int vertex, Set<Integer> visited, List<Integer> component) {
        visited.add(vertex);
        component.add(vertex);
        
        for (Graph.Edge edge : graph.getEdges(vertex)) {
            if (!visited.contains(edge.to)) {
                dfsComponentHelper(graph, edge.to, visited, component);
            }
        }
    }
    
    public static boolean isCyclic(Graph graph) {
        Set<Integer> visited = new HashSet<>();
        Set<Integer> recStack = new HashSet<>();
        
        for (int vertex : graph.getVertices()) {
            if (isCyclicHelper(graph, vertex, visited, recStack)) {
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean isCyclicHelper(Graph graph, int vertex, Set<Integer> visited, Set<Integer> recStack) {
        visited.add(vertex);
        recStack.add(vertex);
        
        for (Graph.Edge edge : graph.getEdges(vertex)) {
            if (!visited.contains(edge.to)) {
                if (isCyclicHelper(graph, edge.to, visited, recStack)) {
                    return true;
                }
            } else if (recStack.contains(edge.to)) {
                return true;
            }
        }
        
        recStack.remove(vertex);
        return false;
    }
    
    public static List<Integer> topologicalSort(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException("Topological sort only works on directed graphs");
        }
        
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Stack<Integer> stack = new Stack<>();
        
        for (int vertex : graph.getVertices()) {
            if (!visited.contains(vertex)) {
                topologicalSortHelper(graph, vertex, visited, stack);
            }
        }
        
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
        
        return result;
    }
    
    private static void topologicalSortHelper(Graph graph, int vertex, Set<Integer> visited, Stack<Integer> stack) {
        visited.add(vertex);
        
        for (Graph.Edge edge : graph.getEdges(vertex)) {
            if (!visited.contains(edge.to)) {
                topologicalSortHelper(graph, edge.to, visited, stack);
            }
        }
        
        stack.push(vertex);
    }
}
```

## 3. Shortest Path Algorithms

### 3.1 Dijkstra's Algorithm

```java
package com.mathacademy.graph;

import java.util.*;

public class Dijkstra {
    
    private static class Node implements Comparable<Node> {
        int vertex;
        double distance;
        
        Node(int vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
        
        @Override
        public int compareTo(Node other) {
            return Double.compare(this.distance, other.distance);
        }
    }
    
    public static Map<Integer, Double> shortestPaths(Graph graph, int source) {
        Map<Integer, Double> distances = new HashMap<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        Set<Integer> visited = new HashSet<>();
        
        for (int vertex : graph.getVertices()) {
            distances.put(vertex, Double.MAX_VALUE);
        }
        
        distances.put(source, 0.0);
        priorityQueue.add(new Node(source, 0.0));
        
        while (!priorityQueue.isEmpty()) {
            Node current = priorityQueue.poll();
            int u = current.vertex;
            
            if (visited.contains(u)) {
                continue;
            }
            
            visited.add(u);
            
            for (Graph.Edge edge : graph.getEdges(u)) {
                int v = edge.to;
                double weight = edge.weight;
                double newDistance = distances.get(u) + weight;
                
                if (newDistance < distances.get(v)) {
                    distances.put(v, newDistance);
                    priorityQueue.add(new Node(v, newDistance));
                }
            }
        }
        
        return distances;
    }
    
    public static Map<Integer, Integer> shortestPathPredecessors(Graph graph, int source) {
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> predecessors = new HashMap<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        Set<Integer> visited = new HashSet<>();
        
        for (int vertex : graph.getVertices()) {
            distances.put(vertex, Double.MAX_VALUE);
        }
        
        distances.put(source, 0.0);
        priorityQueue.add(new Node(source, 0.0));
        
        while (!priorityQueue.isEmpty()) {
            Node current = priorityQueue.poll();
            int u = current.vertex;
            
            if (visited.contains(u)) {
                continue;
            }
            
            visited.add(u);
            
            for (Graph.Edge edge : graph.getEdges(u)) {
                int v = edge.to;
                double weight = edge.weight;
                double newDistance = distances.get(u) + weight;
                
                if (newDistance < distances.get(v)) {
                    distances.put(v, newDistance);
                    predecessors.put(v, u);
                    priorityQueue.add(new Node(v, newDistance));
                }
            }
        }
        
        return predecessors;
    }
    
    public static List<Integer> getPath(Map<Integer, Integer> predecessors, int source, int destination) {
        List<Integer> path = new ArrayList<>();
        
        if (!predecessors.containsKey(destination) && source != destination) {
            return path;
        }
        
        int current = destination;
        
        while (current != source) {
            path.add(current);
            current = predecessors.get(current);
        }
        
        path.add(source);
        Collections.reverse(path);
        
        return path;
    }
}
```

### 3.2 Bellman-Ford Algorithm

```java
package com.mathacademy.graph;

import java.util.*;

public class BellmanFord {
    
    public static Map<Integer, Double> shortestPaths(Graph graph, int source) {
        Map<Integer, Double> distances = new HashMap<>();
        
        for (int vertex : graph.getVertices()) {
            distances.put(vertex, Double.MAX_VALUE);
        }
        
        distances.put(source, 0.0);
        
        int vertexCount = graph.getVertexCount();
        
        for (int i = 0; i < vertexCount - 1; i++) {
            for (int u : graph.getVertices()) {
                for (Graph.Edge edge : graph.getEdges(u)) {
                    int v = edge.to;
                    double weight = edge.weight;
                    
                    if (distances.get(u) != Double.MAX_VALUE &&
                        distances.get(u) + weight < distances.get(v)) {
                        distances.put(v, distances.get(u) + weight);
                    }
                }
            }
        }
        
        return distances;
    }
    
    public static boolean detectNegativeCycle(Graph graph) {
        Map<Integer, Double> distances = new HashMap<>();
        
        for (int vertex : graph.getVertices()) {
            distances.put(vertex, 0.0);
        }
        
        int vertexCount = graph.getVertexCount();
        
        for (int i = 0; i < vertexCount; i++) {
            for (int u : graph.getVertices()) {
                for (Graph.Edge edge : graph.getEdges(u)) {
                    int v = edge.to;
                    double weight = edge.weight;
                    
                    if (distances.get(u) != Double.MAX_VALUE &&
                        distances.get(u) + weight < distances.get(v)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
}
```

## 4. Minimum Spanning Tree

### 4.1 Kruskal's Algorithm

```java
package com.mathacademy.graph;

import java.util.*;

public class Kruskal {
    
    private static class UnionFind {
        private Map<Integer, Integer> parent;
        private Map<Integer, Integer> rank;
        
        public UnionFind(Set<Integer> vertices) {
            parent = new HashMap<>();
            rank = new HashMap<>();
            
            for (int v : vertices) {
                parent.put(v, v);
                rank.put(v, 0);
            }
        }
        
        public int find(int x) {
            if (parent.get(x) != x) {
                parent.put(x, find(parent.get(x)));
            }
            return parent.get(x);
        }
        
        public boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            
            if (rootX == rootY) {
                return false;
            }
            
            if (rank.get(rootX) < rank.get(rootY)) {
                parent.put(rootX, rootY);
            } else if (rank.get(rootX) > rank.get(rootY)) {
                parent.put(rootY, rootX);
            } else {
                parent.put(rootY, rootX);
                rank.put(rootX, rank.get(rootX) + 1);
            }
            
            return true;
        }
    }
    
    public static List<Graph.Edge> minimumSpanningTree(Graph graph) {
        List<Graph.Edge> allEdges = graph.getAllEdges();
        allEdges.sort(Comparator.comparingDouble(e -> e.weight));
        
        UnionFind uf = new UnionFind(new HashSet<>(graph.getVertices()));
        List<Graph.Edge> mst = new ArrayList<>();
        
        for (Graph.Edge edge : allEdges) {
            int from = getOtherVertex(graph, edge.to, edge.weight);
            
            if (uf.union(from, edge.to)) {
                mst.add(edge);
                
                if (mst.size() == graph.getVertexCount() - 1) {
                    break;
                }
            }
        }
        
        return mst;
    }
    
    private static int getOtherVertex(int vertex, int to, double weight) {
        for (int v : new HashSet<>(new HashSet<>(Arrays.asList(1, 2, 3, 4, 5)))) {
            if (v != to) return v;
        }
        return vertex;
    }
    
    public static double getMSTWeight(List<Graph.Edge> mst) {
        double weight = 0;
        for (Graph.Edge edge : mst) {
            weight += edge.weight;
        }
        return weight;
    }
}
```

### 4.2 Prim's Algorithm

```java
package com.mathacademy.graph;

import java.util.*;

public class Prim {
    
    private static class Node implements Comparable<Node> {
        int vertex;
        double key;
        
        Node(int vertex, double key) {
            this.vertex = vertex;
            this.key = key;
        }
        
        @Override
        public int compareTo(Node other) {
            return Double.compare(this.key, other.key);
        }
    }
    
    public static List<Graph.Edge> minimumSpanningTree(Graph graph, int start) {
        Set<Integer> inMST = new HashSet<>();
        PriorityQueue<Node> pq = new PriorityQueue<>();
        List<Graph.Edge> mst = new ArrayList<>();
        
        inMST.add(start);
        
        for (Graph.Edge edge : graph.getEdges(start)) {
            pq.add(new Node(edge.to, edge.weight));
        }
        
        while (!pq.isEmpty() && inMST.size() < graph.getVertexCount()) {
            Node current = pq.poll();
            
            if (inMST.contains(current.vertex)) {
                continue;
            }
            
            inMST.add(current.vertex);
            
            for (Graph.Edge edge : graph.getEdges(current.vertex)) {
                if (!inMST.contains(edge.to)) {
                    pq.add(new Node(edge.to, edge.weight));
                }
            }
            
            Graph.Edge mstEdge = findMSTEdge(graph, current.vertex, inMST);
            if (mstEdge != null) {
                mst.add(mstEdge);
            }
        }
        
        return mst;
    }
    
    private static Graph.Edge findMSTEdge(Graph graph, int vertex, Set<Integer> inMST) {
        double minWeight = Double.MAX_VALUE;
        Graph.Edge minEdge = null;
        
        for (Graph.Edge edge : graph.getEdges(vertex)) {
            if (inMST.contains(edge.to) && edge.weight < minWeight) {
                minWeight = edge.weight;
                minEdge = edge;
            }
        }
        
        return minEdge;
    }
}
```

## 5. Graph Properties

### 5.1 GraphAnalyzer Class

```java
package com.mathacademy.graph;

import java.util.*;

public class GraphAnalyzer {
    
    public static boolean isConnected(Graph graph) {
        if (graph.getVertexCount() == 0) {
            return true;
        }
        
        List<Integer> vertices = graph.getVertices();
        List<Integer> bfs = BreadthFirstSearch.bfs(graph, vertices.get(0));
        
        return bfs.size() == graph.getVertexCount();
    }
    
    public static boolean isBipartite(Graph graph) {
        Map<Integer, Integer> color = new HashMap<>();
        
        for (int vertex : graph.getVertices()) {
            if (!color.containsKey(vertex)) {
                if (!isBipartiteHelper(graph, vertex, color, 0)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private static boolean isBipartiteHelper(Graph graph, int vertex, Map<Integer, Integer> color, int expectedColor) {
        color.put(vertex, expectedColor);
        
        for (Graph.Edge edge : graph.getEdges(vertex)) {
            int neighbor = edge.to;
            
            if (!color.containsKey(neighbor)) {
                if (!isBipartiteHelper(graph, neighbor, color, 1 - expectedColor)) {
                    return false;
                }
            } else if (color.get(neighbor) == expectedColor) {
                return false;
            }
        }
        
        return true;
    }
    
    public static int getConnectivity(Graph graph) {
        if (graph.getVertexCount() == 0) {
            return 0;
        }
        
        List<List<Integer>> components = DepthFirstSearch.findConnectedComponents(graph);
        
        if (components.size() == 1) {
            return 1;
        }
        
        return components.size();
    }
    
    public static boolean hasEulerianPath(Graph graph) {
        int oddDegreeCount = 0;
        
        for (int vertex : graph.getVertices()) {
            if (graph.getDegree(vertex) % 2 != 0) {
                oddDegreeCount++;
            }
        }
        
        return oddDegreeCount == 0 || oddDegreeCount == 2;
    }
    
    public static boolean hasEulerianCircuit(Graph graph) {
        if (graph.getVertexCount() == 0) {
            return false;
        }
        
        if (!isConnected(graph)) {
            return false;
        }
        
        for (int vertex : graph.getVertices()) {
            if (graph.getDegree(vertex) % 2 != 0) {
                return false;
            }
        }
        
        return true;
    }
    
    public static int getChromaticNumber(Graph graph) {
        List<Integer> vertices = graph.getVertices();
        int n = vertices.size();
        
        int[] colors = new int[n];
        Arrays.fill(colors, -1);
        
        return chromaticNumberHelper(graph, vertices, colors, 0, 0);
    }
    
    private static int chromaticNumberHelper(Graph graph, List<Integer> vertices, int[] colors, int index, int maxColor) {
        if (index == vertices.size()) {
            return maxColor + 1;
        }
        
        Set<Integer> usedColors = new HashSet<>();
        
        for (Graph.Edge edge : graph.getEdges(vertices.get(index))) {
            int neighborIndex = vertices.indexOf(edge.to);
            if (neighborIndex >= 0 && neighborIndex < index) {
                usedColors.add(colors[neighborIndex]);
            }
        }
        
        for (int c = 0; c <= maxColor; c++) {
            if (!usedColors.contains(c)) {
                colors[index] = c;
                int result = chromaticNumberHelper(graph, vertices, colors, index + 1, maxColor);
                if (result > 0) return result;
            }
        }
        
        colors[index] = maxColor + 1;
        return chromaticNumberHelper(graph, vertices, colors, index + 1, maxColor + 1);
    }
}
```

## 6. Testing and Validation

### 6.1 GraphTest Class

```java
package com.mathacademy.graph;

import java.util.*;

public class GraphTest {
    
    public static void main(String[] args) {
        testGraphOperations();
        testGraphTraversals();
        testShortestPath();
        testMST();
        testGraphProperties();
    }
    
    private static void testGraphOperations() {
        System.out.println("=== Graph Operations ===");
        
        Graph graph = new Graph();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);
        
        System.out.println("Vertices: " + graph.getVertices());
        System.out.println("Edge count: " + graph.getEdgeCount());
        System.out.println("Degree(1): " + graph.getDegree(1));
    }
    
    private static void testGraphTraversals() {
        System.out.println("\n=== Graph Traversals ===");
        
        Graph graph = new Graph();
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(2, 5);
        graph.addEdge(3, 6);
        
        System.out.println("BFS from 1: " + BreadthFirstSearch.bfs(graph, 1));
        System.out.println("DFS from 1: " + DepthFirstSearch.dfs(graph, 1));
        System.out.println("Shortest path 1 to 5: " + BreadthFirstSearch.shortestPath(graph, 1, 5));
    }
    
    private static void testShortestPath() {
        System.out.println("\n=== Shortest Path ===");
        
        Graph weightedGraph = new Graph();
        weightedGraph.addEdge(1, 2, 4.0);
        weightedGraph.addEdge(1, 3, 2.0);
        weightedGraph.addEdge(2, 3, 1.0);
        weightedGraph.addEdge(2, 4, 5.0);
        weightedGraph.addEdge(3, 4, 8.0);
        
        Map<Integer, Double> distances = Dijkstra.shortestPaths(weightedGraph, 1);
        System.out.println("Dijkstra from 1: " + distances);
    }
    
    private static void testMST() {
        System.out.println("\n=== Minimum Spanning Tree ===");
        
        Graph weightedGraph = new Graph();
        weightedGraph.addEdge(1, 2, 1.0);
        weightedGraph.addEdge(1, 3, 3.0);
        weightedGraph.addEdge(2, 3, 1.0);
        weightedGraph.addEdge(2, 4, 6.0);
        weightedGraph.addEdge(3, 4, 4.0);
        
        List<Graph.Edge> mst = Kruskal.minimumSpanningTree(weightedGraph);
        System.out.println("MST edges: " + mst);
        System.out.println("MST weight: " + Kruskal.getMSTWeight(mst));
    }
    
    private static void testGraphProperties() {
        System.out.println("\n=== Graph Properties ===");
        
        Graph graph = new Graph();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);
        
        System.out.println("Connected: " + GraphAnalyzer.isConnected(graph));
        System.out.println("Bipartite: " + GraphAnalyzer.isBipartite(graph));
        System.out.println("Eulerian circuit: " + GraphAnalyzer.hasEulerianCircuit(graph));
    }
}
```
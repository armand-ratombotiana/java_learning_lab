# Deep Dive: Graph Algorithms

## 1. Tarjan's Strongly Connected Components (SCC) Algorithm

Tarjan's algorithm finds SCCs in O(V+E) using a single DFS with low-link values.

### Algorithm Intuition

The key insight: SCCs form subtrees in the DFS tree. If a node can reach an ancestor (a back edge), then all nodes along that path form an SCC.

```java
public class TarjanSCC {
    private final int n;
    private final List<Integer>[] graph;
    private int[] index, lowLink;
    private boolean[] onStack;
    private Deque<Integer> stack;
    private int currentIndex;
    private List<List<Integer>> components;
    
    @SuppressWarnings("unchecked")
    public TarjanSCC(int n) {
        this.n = n;
        graph = new List[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
    }
    
    public void addEdge(int from, int to) {
        graph[from].add(to);
    }
    
    public List<List<Integer>> findSCCs() {
        index = new int[n];
        lowLink = new int[n];
        onStack = new boolean[n];
        stack = new ArrayDeque<>();
        components = new ArrayList<>();
        Arrays.fill(index, -1); // unvisited
        currentIndex = 0;
        
        for (int v = 0; v < n; v++) {
            if (index[v] == -1) {
                strongConnect(v);
            }
        }
        return components;
    }
    
    private void strongConnect(int v) {
        index[v] = currentIndex;
        lowLink[v] = currentIndex;
        currentIndex++;
        stack.push(v);
        onStack[v] = true;
        
        for (int w : graph[v]) {
            if (index[w] == -1) {
                // Tree edge
                strongConnect(w);
                lowLink[v] = Math.min(lowLink[v], lowLink[w]);
            } else if (onStack[w]) {
                // Back edge or cross edge to a node still on stack
                lowLink[v] = Math.min(lowLink[v], index[w]);
            }
            // Cross edge to a node no longer on stack: ignore
        }
        
        // If v is a root of an SCC
        if (lowLink[v] == index[v]) {
            List<Integer> component = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                component.add(w);
            } while (w != v);
            components.add(component);
        }
    }
}
```

### Correctness Proof

**Theorem**: Tarjan's algorithm correctly partitions vertices into strongly connected components.

**Proof**:

Let `S(v)` be the set of nodes reachable from v that share the same SCC as v.

**Invariant 1**: For all visited nodes u, `lowLink[u] = min{ index[w] | w reachable from u via zero or more tree edges followed by at most one back/cross edge to a node w on the stack }`.

Proof: When u is first visited, `lowLink[u] = index[u]`. For each child v, after `strongConnect(v)` returns:
- If `lowLink[v] < lowLink[u]`, there's a path from v to a node with lower index that's still on the stack, so u can reach it too.
- If v has a back edge to u's ancestor, `index[ancestor] < lowLink[u]`.

**Invariant 2**: If v is on the stack, then v is in the current SCC being constructed.

**Theorem**: `lowLink[v] == index[v]` iff v is the root of an SCC.

Proof (→): If v is the root, no descendant can reach an ancestor of v (by definition). So `lowLink[v] = index[v]`.

Proof (←): If `lowLink[v] = index[v]`, then no descendant of v can reach any ancestor of v that's on the stack. The nodes popped after v form a maximal set where each node can reach v and v can reach each node — precisely the SCC containing v.

### Kosaraju's Algorithm (Alternative)

```java
public class KosarajuSCC {
    // Alternative approach: two DFS passes
    // 1. DFS on original graph, record finish order
    // 2. DFS on transpose graph, in reverse finish order
    // Each DFS tree in step 2 is an SCC
    
    private final int n;
    private final List<Integer>[] graph, reverse;
    
    @SuppressWarnings("unchecked")
    public KosarajuSCC(int n) {
        this.n = n;
        graph = new List[n];
        reverse = new List[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
            reverse[i] = new ArrayList<>();
        }
    }
    
    public List<List<Integer>> findSCCs() {
        // Step 1: DFS to get finish order
        boolean[] visited = new boolean[n];
        List<Integer> order = new ArrayList<>();
        for (int v = 0; v < n; v++) {
            if (!visited[v]) dfs1(v, visited, order);
        }
        
        // Step 2: DFS on reverse graph in reverse order
        visited = new boolean[n];
        List<List<Integer>> components = new ArrayList<>();
        for (int i = n - 1; i >= 0; i--) {
            int v = order.get(i);
            if (!visited[v]) {
                List<Integer> component = new ArrayList<>();
                dfs2(v, visited, component);
                components.add(component);
            }
        }
        return components;
    }
    
    private void dfs1(int v, boolean[] visited, List<Integer> order) {
        visited[v] = true;
        for (int w : graph[v]) {
            if (!visited[w]) dfs1(w, visited, order);
        }
        order.add(v);
    }
    
    private void dfs2(int v, boolean[] visited, List<Integer> component) {
        visited[v] = true;
        component.add(v);
        for (int w : reverse[v]) {
            if (!visited[w]) dfs2(w, visited, component);
        }
    }
    
    // Tarjan is preferred because:
    // - Single DFS pass (vs two passes)
    // - No need for transpose graph
    // - Works with iterative DFS (avoid stack overflow)
    // - Slightly more efficient in practice
}
```

### Applications of SCC

```java
public class SCCApplications {
    // 1. 2-SAT (2-Satisfiability)
    // Problem: Find assignment to boolean variables satisfying clauses (x ∨ y)
    // Reduction: Each clause (a ∨ b) → implications (¬a → b) and (¬b → a)
    // Solution: Variable x is true if SCC(x) < SCC(¬x) in topological order
    
    // 2. Dependency Resolution
    // Each SCC = cycle of interdependent modules
    // Condense SCCs into DAG → topological ordering of dependencies
    
    // 3. Condensation Graph (DAG of SCCs)
    // Every directed graph can be condensed into a DAG of SCCs
    // This DAG's topological order gives processing order
    
    // 4. Minimum Edges to Make Graph Strongly Connected
    // Condense to DAG, count sources and sinks:
    // Answer = max(sources, sinks)
}
```

## 2. Dinic vs Edmonds-Karp Complexity Analysis

### Edmonds-Karp (BFS-based augmenting paths)

```java
public class EdmondsKarp {
    // Time: O(V · E²)
    // Each BFS finds augmenting path in O(E)
    // Each augmentation sends at least 1 unit of flow
    // Maximum flow ≤ maximum capacity ≤ |f|
    // Number of augmentations: O(V · E) per unit of flow? No!
    // Actually: each BFS increases shortest-path distance from s to t
    // Distance ≤ V, so at most V·E augmentations before termination
    // Total: O(V · E²)
    
    private static final int INF = Integer.MAX_VALUE;
    
    public static int maxFlow(int[][] capacity, int s, int t) {
        int n = capacity.length;
        int[][] flow = new int[n][n];
        int[] parent = new int[n];
        int maxFlow = 0;
        
        while (true) {
            // BFS to find augmenting path
            Arrays.fill(parent, -1);
            Queue<Integer> q = new LinkedList<>();
            q.add(s);
            parent[s] = s;
            
            while (!q.isEmpty() && parent[t] == -1) {
                int u = q.poll();
                for (int v = 0; v < n; v++) {
                    if (parent[v] == -1 && capacity[u][v] - flow[u][v] > 0) {
                        parent[v] = u;
                        q.add(v);
                    }
                }
            }
            
            if (parent[t] == -1) break;
            
            // Find bottleneck
            int bottleneck = INF;
            for (int v = t; v != s; v = parent[v]) {
                int u = parent[v];
                bottleneck = Math.min(bottleneck, capacity[u][v] - flow[u][v]);
            }
            
            // Augment flow
            for (int v = t; v != s; v = parent[v]) {
                int u = parent[v];
                flow[u][v] += bottleneck;
                flow[v][u] -= bottleneck;
            }
            
            maxFlow += bottleneck;
        }
        
        return maxFlow;
    }
}
```

### Dinic (Level Graph + Blocking Flow)

```java
public class Dinic {
    // Time: O(V² · E) in general, O(E√V) for bipartite matching
    // 
    // Key innovations:
    // 1. Level graph: BFS assigns levels (shortest distance from s)
    // 2. Blocking flow: DFS finds all augmenting paths in level graph
    // 3. After blocking flow, at least one distance increases
    // 4. Maximum distance = V, so at most V blocking flow phases
    // 5. Each blocking flow: O(E) time per DFS, O(E) DFS calls
    // Total: O(V · E) per blocking flow? Actually O(V · E²) worst case
    // But with level graph optimization: O(V² · E)
    //
    // Better bound: O(min(V^(2/3), √E) · E) for unit capacities
    // O(E√V) for bipartite matching (same as Hopcroft-Karp)
    
    private static class Edge {
        int to, rev;
        int capacity;
        Edge(int t, int c, int r) { to = t; capacity = c; rev = r; }
    }
    
    private final List<Edge>[] graph;
    private final int n;
    private int[] level, next;
    
    @SuppressWarnings("unchecked")
    public Dinic(int n) {
        this.n = n;
        graph = new List[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        level = new int[n];
        next = new int[n];
    }
    
    public void addEdge(int from, int to, int capacity) {
        graph[from].add(new Edge(to, capacity, graph[to].size()));
        graph[to].add(new Edge(from, 0, graph[from].size() - 1));
    }
    
    private boolean bfs(int s, int t) {
        Arrays.fill(level, -1);
        Queue<Integer> q = new LinkedList<>();
        level[s] = 0;
        q.add(s);
        while (!q.isEmpty()) {
            int u = q.poll();
            for (Edge e : graph[u]) {
                if (e.capacity > 0 && level[e.to] == -1) {
                    level[e.to] = level[u] + 1;
                    q.add(e.to);
                }
            }
        }
        return level[t] != -1;
    }
    
    private int dfs(int u, int t, int flow) {
        if (u == t) return flow;
        for (; next[u] < graph[u].size(); next[u]++) {
            Edge e = graph[u].get(next[u]);
            if (e.capacity > 0 && level[e.to] == level[u] + 1) {
                int pushed = dfs(e.to, t, Math.min(flow, e.capacity));
                if (pushed > 0) {
                    e.capacity -= pushed;
                    graph[e.to].get(e.rev).capacity += pushed;
                    return pushed;
                }
            }
        }
        return 0;
    }
    
    public int maxFlow(int s, int t) {
        int flow = 0;
        while (bfs(s, t)) {
            Arrays.fill(next, 0);
            int pushed;
            while ((pushed = dfs(s, t, Integer.MAX_VALUE)) > 0) {
                flow += pushed;
            }
        }
        return flow;
    }
}
```

### Complexity Comparison

```java
public class ComplexityComparison {
    // Algorithm        | Time Complexity         | Space    | Use Case
    // ================|=========================|==========|=====================
    // Ford-Fulkerson  | O(E·|f|)               | O(V+E)   | Small integer flows
    // Edmonds-Karp    | O(V·E²)                | O(V+E)   | Simple, guaranteed
    // Dinic           | O(V²·E)                | O(V+E)   | General purpose
    // Dinic (unit)    | O(min(V^(2/3),√E)·E)   | O(V+E)   | Bipartite matching
    // Push-Relabel    | O(V²·√E)               | O(V+E)   | Dense graphs
    // HLPP            | O(V²·√E)               | O(V+E)   | Highest-label
    
    // Practical performance:
    // - Dinic is fastest for most cases (sparse graphs)
    // - Push-Relabel is faster for dense graphs (e.g., V=1000, E=50000)
    // - Edmonds-Karp is simple but rarely used in practice
    // - For bipartite matching: Dinic = Hopcroft-Karp = O(E√V)
    
    static void example() {
        int V = 1000, E = 10000;
        // Sparse: Dinic is best
        // Dense: Push-Relabel
        
        // Dinic: O(V²·E) = O(1000²·10000) = O(10^10) worst case
        // Push-Relabel: O(V²·√E) = O(1000²·100) = O(10^8) worst case
        // In practice, both are much faster:
        // Dinic on random graphs: near O(E·√V)
        // Push-Relabel: near O(V³) worst but O(E) typically
    }
}
```

## 3. Push-Relabel Algorithm

The push-relabel algorithm (Goldberg-Tarjan) takes a different approach to max flow:

```java
public class PushRelabel {
    // Instead of augmenting paths, push-relabel:
    // 1. Maintains preflow: flow that may exceed conservation
    // 2. Gives each node a height (label)
    // 3. Pushes flow from higher to lower nodes
    // 4. Relabels (increases height) when stuck
    
    // Key concept: excess flow
    // - Source has infinite excess initially
    // - Flow is pushed down height gradient
    // - When a node has excess but no downhill edge, it's relabeled
    // - When sink is unreachable, excess flows back to source
    
    private static class Edge {
        int to, rev;
        int capacity;
        Edge(int t, int c, int r) { to = t; capacity = c; rev = r; }
    }
    
    private final List<Edge>[] graph;
    private final int n;
    private int[] height, excess;
    
    @SuppressWarnings("unchecked")
    public PushRelabel(int n) {
        this.n = n;
        graph = new List[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        height = new int[n];
        excess = new int[n];
    }
    
    public void addEdge(int from, int to, int capacity) {
        graph[from].add(new Edge(to, capacity, graph[to].size()));
        graph[to].add(new Edge(from, 0, graph[from].size() - 1));
    }
    
    private void push(Edge e, int u) {
        int v = e.to;
        int delta = Math.min(excess[u], e.capacity);
        e.capacity -= delta;
        graph[v].get(e.rev).capacity += delta;
        excess[u] -= delta;
        excess[v] += delta;
    }
    
    private void relabel(int u) {
        int minHeight = Integer.MAX_VALUE;
        for (Edge e : graph[u]) {
            if (e.capacity > 0) {
                minHeight = Math.min(minHeight, height[e.to]);
            }
        }
        if (minHeight < Integer.MAX_VALUE) {
            height[u] = minHeight + 1;
        }
    }
    
    private void discharge(int u) {
        while (excess[u] > 0) {
            for (Edge e : graph[u]) {
                if (e.capacity > 0 && height[u] == height[e.to] + 1) {
                    push(e, u);
                    if (excess[u] == 0) return;
                }
            }
            relabel(u);
        }
    }
    
    public int maxFlow(int s, int t) {
        height[s] = n;
        for (Edge e : graph[s]) {
            excess[s] += e.capacity;
            push(e, s);
        }
        
        // Active nodes (have excess, not source/sink)
        List<Integer> active = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (i != s && i != t && excess[i] > 0) {
                active.add(i);
            }
        }
        
        while (!active.isEmpty()) {
            int u = active.remove(active.size() - 1);
            discharge(u);
            if (excess[u] > 0 && u != s && u != t) {
                active.add(u);
            }
        }
        
        return excess[t];
    }
    
    // The Highest-Label Preflow-Push (HLPP) variant:
    // - Always selects the highest label node for discharge
    // - O(V²·√E) time complexity
    // - Faster in practice than basic push-relabel
}
```

### Why Push-Relabel Beats Dinic on Dense Graphs

```java
// Dinic on dense graph (V=1000, E=500,000):
// BFS: O(E) per phase, DFS: O(E) per path
// Each phase may have many DFS runs (blocking flow)
// Total: O(V²·E) = O(10^9) operations
//
// Push-Relabel on dense graph:
// Each edge push is O(1)
// Each node is relabeled at most V times
// Total pushes: O(V²·E) worst case, but typically far less
// Gap heuristic: skip nodes that can't reach sink
// Practical performance: near O(V³) on worst-case
// Modern improvements (HLPP with gap): O(V²·√E)
//
// Bottom line:
// Sparse (E = O(V)): Dinic is faster
// Dense (E = O(V²)): Push-Relabel is faster
```

## 4. Planarity Testing (Kuratowski's Theorem)

```java
public class PlanarityTesting {
    // A graph is planar if it can be drawn without edge crossings.
    //
    // Kuratowski's Theorem:
    // A graph is planar iff it contains no subgraph that is a subdivision
    // of K₅ (5-clique) or K₃,₃ (complete bipartite on 3+3 vertices).
    //
    // K₅: 5 vertices, each connected to all others (10 edges)
    // K₃,₃: 2 sets of 3 vertices, all edges across (9 edges)
    //
    // Hopcroft-Tarjan planarity test (1974):
    // O(V) time, uses DFS tree and embedding
    //
    // Algorithm outline:
    // 1. Find a DFS tree of the graph
    // 2. Classify edges as tree edges or back edges
    // 3. Assign low-point for each vertex (lowest ancestor reachable via back edges)
    // 4. Use pathfinding to embed edges in the planar embedding
    // 5. If embedding fails → graph is non-planar
    
    // For practical use: Boyer-Myrvold planarity test (2004)
    // O(V) time, implements the full embedding
    
    // Simple heuristic tests:
    public static boolean quickCheck(int V, int E) {
        // Necessary condition for planarity: E ≤ 3V - 6 (for V ≥ 3)
        // K₃,₃ has V=6, E=9, and 3V-6 = 12, so passes this test
        // But it's non-planar → need full test
        if (V < 5) return true;
        if (E > 3 * V - 6) return false;
        return true; // Need full test
    }
}
```

## 5. Graph Isomorphism (VF2 Algorithm)

Graph isomorphism is one of the few problems with unknown complexity (NP-intermediate).

```java
public class VF2Algorithm {
    // VF2 is a backtracking algorithm for graph isomorphism.
    // It's exponential in worst case but fast in practice.
    //
    // State: (M, M₁⁻¹, M₂⁻¹) where M maps nodes of G1 to G2
    
    private final int[][] g1, g2;
    private final int n1, n2;
    
    public VF2Algorithm(int[][] g1, int[][] g2) {
        this.g1 = g1;
        this.g2 = g2;
        this.n1 = g1.length;
        this.n2 = g2.length;
    }
    
    public boolean isomorphic() {
        if (n1 != n2) return false;
        // Simple degree check
        int[] deg1 = Arrays.stream(g1).mapToInt(row -> row.length).sorted().toArray();
        int[] deg2 = Arrays.stream(g2).mapToInt(row -> row.length).sorted().toArray();
        if (!Arrays.equals(deg1, deg2)) return false;
        
        return match(new int[n1], new int[n2], 0);
    }
    
    private boolean match(int[] map1to2, int[] map2to1, int depth) {
        if (depth == n1) return true;
        
        // Choose next node from G1 (heuristic: most constrained first)
        int u = selectNode(map1to2, depth);
        
        for (int v = 0; v < n2; v++) {
            if (map2to1[v] != -1) continue;
            if (!isFeasible(u, v, map1to2, map2to1)) continue;
            
            map1to2[u] = v;
            map2to1[v] = u;
            if (match(map1to2, map2to1, depth + 1)) return true;
            map1to2[u] = -1;
            map2to1[v] = -1;
        }
        return false;
    }
    
    private boolean isFeasible(int u, int v, int[] map1to2, int[] map2to1) {
        // Check feasibility rules:
        // 1. Degree check: |N(u)| must equal |N(v)|
        // 2. Neighborhood consistency: mapped neighbors must match
        // 3. Look-ahead: number of unmapped neighbors in G1 reaching 
        //    mapped nodes ≤ number in G2
        // 4. Look-ahead: number of unmapped neighbors not reaching 
        //    mapped nodes must match
        
        if (g1[u].length != g2[v].length) return false;
        
        int mappedCount1 = 0, mappedCount2 = 0;
        for (int w : g1[u]) {
            if (map1to2[w] != -1 && !isAdjacent(v, map1to2[w])) return false;
            if (map1to2[w] != -1) mappedCount1++;
        }
        for (int w : g2[v]) {
            if (map2to1[w] != -1 && !isAdjacent(u, map2to1[w])) return false;
            if (map2to1[w] != -1) mappedCount2++;
        }
        if (mappedCount1 != mappedCount2) return false;
        
        return true;
    }
    
    private boolean isAdjacent(int u, int v) {
        for (int w : g1[u]) if (w == v) return true;
        return false;
    }
    
    private int selectNode(int[] map, int depth) {
        // Heuristic: pick node with most mapped neighbors (deepest first)
        // This is the "most constrained" heuristic
        int best = -1, bestMapped = -1;
        for (int u = 0; u < n1; u++) {
            if (map[u] != -1) continue;
            int mapped = 0;
            for (int w : g1[u]) if (map[w] != -1) mapped++;
            if (mapped > bestMapped) {
                bestMapped = mapped;
                best = u;
            }
        }
        return best;
    }
}
```

## 6. Graph Algorithm Summary

```java
public class GraphAlgorithmSummary {
    // Shortest Paths:
    // Dijkstra: O(E log V) — non-negative weights
    // Bellman-Ford: O(VE) — negative weights allowed
    // Floyd-Warshall: O(V³) — all-pairs
    // Johnson: O(VE + V² log V) — all-pairs with sparse graph
    
    // Minimum Spanning Tree:
    // Kruskal: O(E log V) — sort edges, union-find
    // Prim: O(E log V) — priority queue, similar to Dijkstra
    // Boruvka: O(E log V) — parallelizable
    
    // Max Flow:
    // Dinic: O(V²E) — general
    // Push-Relabel: O(V²√E) — dense graphs
    // Edmonds-Karp: O(VE²) — simple but slow
    
    // Matching:
    // Kuhn-Munkres: O(V³) — assignment problem
    // Hopcroft-Karp: O(E√V) — bipartite matching
    // Blossom: O(V³) — general matching (Edmonds)
    
    // Connectivity:
    // Tarjan SCC: O(V+E) — strongly connected components
    // Union-Find: O(α(V)) — dynamic connectivity
    // Bridge/Articulation: O(V+E) — cut detection
}
```

The common thread in graph algorithms is the use of DFS/BFS as building blocks, with clever bookkeeping (low-link values, level graphs, potentials) to achieve optimal asymptotic complexity.

$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\17-network-flow"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Network Flow Algorithms — Overview

This lab covers maximum flow and minimum cut algorithms: Ford-Fulkerson, Edmonds-Karp, Dinic's algorithm, Push-Relabel, and their applications including bipartite matching and min-cost max-flow. Network flow problems model many real-world scenarios involving movement of resources through networks with capacity constraints.

## Learning Objectives

- Understand the max-flow min-cut theorem
- Implement Ford-Fulkerson with DFS augmenting paths
- Implement Edmonds-Karp using BFS for shortest augmenting paths
- Build Dinic's algorithm with level graphs and blocking flows
- Solve bipartite matching via reduction to max flow
- Compute min s-t cuts using max flow

## Prerequisites

- Graph representation (adjacency lists)
- BFS and DFS traversal
- Basic understanding of linear programming concepts
"@

wf "THEORY.md" @"
# Network Flow — Theoretical Foundation

## Flow Network Definition

A flow network is a directed graph G = (V, E) with source s and sink t. Each edge (u,v) has a non-negative capacity c(u,v). A flow f satisfies: (1) capacity constraint: f(u,v) <= c(u,v), (2) skew symmetry: f(u,v) = -f(v,u), (3) flow conservation: sum of flow into v equals sum of flow out of v for all v != s,t.

## Max-Flow Min-Cut Theorem

The maximum value of an s-t flow equals the minimum capacity of an s-t cut. This theorem, proven by Ford and Fulkerson in 1954, is fundamental to flow theory. It shows that finding a maximum flow is equivalent to finding a cut of minimum capacity separating s from t.

## Augmenting Path Algorithms

The residual network G_f has edges with residual capacity c_f(u,v) = c(u,v) - f(u,v). An augmenting path is a path from s to t in G_f. Ford-Fulkerson repeatedly finds any augmenting path and increases flow by the minimum residual capacity along the path.

## Dinic's Algorithm

Dinic's algorithm improves on Edmonds-Karp by computing a level graph (BFS layers from s) and then finding a blocking flow using DFS. Multiple augmentations are performed in each phase, reducing the number of BFS phases to O(sqrt(E)) for unit capacities.

## Push-Relabel Algorithm

Push-Relabel takes a different approach, allowing flow to accumulate at intermediate nodes (excess) and then pushing it forward or relabeling (increasing height) when stuck. This local operation approach leads to O(V^2 E) worst-case time with O(V^3) for the highest-label variant.

## Bipartite Matching via Max Flow

A bipartite graph can be reduced to a flow network by adding a source connected to left vertices and a sink connected to right vertices, with all capacities set to 1. The max flow value equals the size of the maximum matching.
"@

wf "WHY_IT_EXISTS.md" @"
# Why Network Flow Algorithms Exist

Network flow theory emerged from operations research and the need to optimize resource distribution through networks. The original problem was motivated by the Soviet railway network: during the Cold War, the US Air Force wanted to determine the maximum amount of cargo that could be transported through Soviet rail lines under capacity constraints.

The Ford-Fulkerson method (1956) provided the first algorithmic framework for solving max flow problems. The key insight was the residual network and augmenting paths, which transformed a complex optimization problem into a sequence of path-finding steps.

However, Ford-Fulkerson with DFS could be exponential in pathological cases. The Edmonds-Karp algorithm (1972) fixed this by using BFS, guaranteeing O(VE^2) time. This was a major theoretical breakthrough that made max flow computations practical for real-world problems.

Dinic's algorithm (1970) independently discovered a similar approach but with the crucial innovation of level graphs and blocking flows. This reduced the number of augmenting phases dramatically, giving O(V^2 E) performance and O(min(V^{2/3}, sqrt(E)) E) for unit capacities.

Network flow is a remarkably versatile framework. It models transportation networks, communication networks, bipartite matching, project selection, baseball elimination, image segmentation, and many other problems. The max-flow min-cut theorem provides a duality that gives deep insights into the structure of these problems.

Modern applications include assigning tasks to processors, routing traffic in computer networks, segmenting images in computer vision, and analyzing social networks. The push-relabel algorithm with its global relabeling heuristic is among the fastest in practice for large sparse graphs.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Network Flow Matters

Network flow algorithms are critical infrastructure in numerous domains. In telecommunications, they determine maximum data throughput between nodes. In logistics, they optimize supply chain distribution. In computer vision, graph cut algorithms for image segmentation are based on min-cut computations.

## Practical Impact

Bipartite matching via max flow is used in job assignment systems, where workers need to be matched to tasks with constraints. The assignment problem, solved via min-cost max-flow, optimizes for both feasibility and cost. Baseball elimination uses max flow to determine if a team can still win the pennant. Open-pit mining uses max flow to determine the optimal ore extraction boundary.

## Performance Matters

For a network with 10,000 nodes and 100,000 edges, Ford-Fulkerson with DFS might take millions of iterations, while Dinic typically converges in tens of iterations. Push-Relabel with gap heuristics is even faster for large networks. Choosing the right algorithm can mean the difference between minutes and milliseconds.

## Interview Relevance

Max flow problems appear in technical interviews at top companies. The ability to translate real-world problems into flow networks demonstrates strong problem-solving skills. Common interview problems include bipartite matching, edge-disjoint paths, and project selection.
"@

wf "HISTORY.md" @"
# History of Network Flow Algorithms

1954: Ford and Fulkerson published the max-flow min-cut theorem for the Soviet railway problem.

1956: The Ford-Fulkerson augmenting path method was published, establishing the algorithmic framework.

1969: Dinitz (Dinic) discovered his algorithm while studying minimum-cost flows at Moscow State University.

1970: Dinic's algorithm with level graphs was published in Soviet journal "Doklady Akademii Nauk SSSR".

1972: Edmonds and Karp proved that BFS-based augmentation gives polynomial time O(VE^2).

1974: Karzanov introduced the preflow-push (push-relabel) concept with O(V^3) time.

1978: Cherkassky improved Dinic's algorithm analysis for unit capacity networks.

1986: Goldberg and Tarjan published the push-relabel algorithm with O(V^2 sqrt(E)) time.

1988: Ahuja and Orlin introduced capacity scaling for min-cost flow.

1990: Goldberg's highest-label push-relabel with global relabeling became the practical standard.

1993: The BGL (Boost Graph Library) included max flow implementations for practical use.

1997: Boykov-Kolmogorov algorithm for computer vision graph cuts (efficient for grid graphs).

2010s: Parallel and distributed max flow algorithms for large-scale graph processing.

2020s: Learning-based flow algorithms using graph neural networks show promise.
"@

wf "MENTAL_MODELS.md" @"
# Mental Models for Network Flow

## Augmenting Path — "Find and Fill"

Think of a water distribution system with pipes of varying diameters. Each pipe has a maximum capacity. You want to send as much water as possible from the source to the sink. You find a path from source to sink, fill it to its minimum pipe capacity, then look for the next path through remaining capacity. Keep going until no more water can get through.

## Residual Network — "The Second Direction"

The residual network represents remaining capacity forward AND backward. Backward residual edges are like allowing water to flow backward through a pipe to undo a previous decision. This is the key insight that makes flow algorithms correct: you can always reverse flow to make room for better alternatives.

## Level Graph (Dinic) — "Layered City"

Imagine a city with buildings at different heights. You can only move from a lower floor to a higher floor. The level graph in Dinic ensures you always make forward progress toward the sink, never sideways or backward. This prevents wasted exploration.

## Min-Cut — "The Bottleneck"

A cut is a set of edges that, if removed, disconnect the source from the sink. The min-cut is the smallest total capacity of such a set. It represents the fundamental bottleneck in the network. The max-flow equals the min-cut, so finding one gives you the other.

## Push-Relabel — "Overflow then Fix"

Unlike the disciplined augmenting path approach, push-relabel allows some nodes to be temporarily overloaded (have excess flow). The algorithm pushes excess to neighbors with lower height, and if stuck, increases the node's height (relabel) so flow can escape. This localized approach converges efficiently without global path searches.
"@

wf "HOW_IT_WORKS.md" @"
# How Network Flow Works

## Ford-Fulkerson Example

Network: s->a(10), s->b(5), a->b(15), a->t(10), b->t(10)

Path 1: s->a->t, min capacity = min(10,10) = 10
  After: s->a(0), a->t(0), residual: a->s(10), t->a(10)

Path 2: s->b->t, min capacity = min(5,10) = 5
  After: s->b(0), b->t(5), residual: b->s(5), t->b(5)

Path 3: s->a->b->t via residuals? Let's check:
  s->a: residual = 0 (forward). But a->s has 10 backward.
  Actually, s->a is saturated, so we need alternative.
  s->b is saturated too. Flow = 15. Max flow = 15.

## Edmonds-Karp Difference

Edmonds-Karp always picks the SHORTEST augmenting path (by number of edges). This guarantees that each edge is saturated at most O(V) times, giving O(VE^2) bound.

## Dinic Level Graph

Phase 1: BFS from s gives levels: s(0), a(1), b(1), t(2). DFS finds blocking flow: s->a->t (10), s->b->t (5). After no more augmenting paths in level graph, recompute levels.

Phase 2: s(0), then? a(1) but s->a has 0 capacity, b(1) but s->b has 0 capacity. t is unreachable. Done. Max flow = 15.
"@

wf "INTERNALS.md" @"
# Network Flow — Internal Mechanics

## Residual Edge Representation

```java
class Edge {
    int to, rev;
    long cap;
    Edge(int to, int rev, long cap) {
        this.to = to; this.rev = rev; this.cap = cap;
    }
}

void addEdge(int from, int to, long cap) {
    graph[from].add(new Edge(to, graph[to].size(), cap));
    graph[to].add(new Edge(from, graph[from].size() - 1, 0));
}
```

## Dinic: Level Graph (BFS)

```java
boolean bfs(int s, int t) {
    Arrays.fill(level, -1);
    Queue<Integer> q = new LinkedList<>();
    level[s] = 0;
    q.offer(s);
    while (!q.isEmpty()) {
        int v = q.poll();
        for (Edge e : graph[v]) {
            if (e.cap > 0 && level[e.to] < 0) {
                level[e.to] = level[v] + 1;
                q.offer(e.to);
            }
        }
    }
    return level[t] >= 0;
}
```

## Dinic: Blocking Flow (DFS)

```java
long dfs(int v, int t, long f) {
    if (v == t) return f;
    for (int i = it[v]; i < graph[v].size(); i++) {
        it[v] = i;
        Edge e = graph[v].get(i);
        if (e.cap > 0 && level[v] < level[e.to]) {
            long d = dfs(e.to, t, Math.min(f, e.cap));
            if (d > 0) {
                e.cap -= d;
                graph[e.to].get(e.rev).cap += d;
                return d;
            }
        }
    }
    return 0;
}
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Network Flow

## Max-Flow Min-Cut Theorem

For any flow network, the value of the maximum s-t flow equals the capacity of the minimum s-t cut. Proof uses the duality between flows and cuts: any flow value is bounded by any cut capacity, and there exists a cut whose capacity equals the maximum flow.

## Ford-Fulkerson Complexity

Each augmentation increases flow by at least 1. With integer capacities C, at most C flow increases, each requiring O(E) DFS: O(C * E). With irrational capacities, the algorithm may not terminate.

## Edmonds-Karp Complexity

Each BFS finds the shortest augmenting path. Each edge can be critical (saturated) at most O(V) times because each time an edge is critical, its source's distance from s increases. Total: O(V * E^2).

## Dinic Complexity

Each BFS phase produces a level graph. The blocking flow found in each phase increases the shortest path distance from s to t. This distance can increase at most V times. Each DFS for the blocking flow takes O(E) per path found, with at most O(E) paths per phase. Total: O(V^2 E), O(min(V^{2/3}, sqrt(E)) E) for unit capacities.

## Bipartite Matching via Flow

The reduction to max flow gives unit capacities on all edges. Dinic on unit capacity networks runs in O(sqrt(V) * E). The max flow value equals the size of the maximum matching.
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Network Flow

## Residual Network After First Augmentation

Original: s-(10)->a, s-(5)->b, a-(15)->b, a-(10)->t, b-(10)->t

After s->a->t (10):
- s->a: 0 forward, 10 backward
- a->t: 0 forward, 10 backward
- Others unchanged

After s->b->t (5):
- s->b: 0 forward, 5 backward
- b->t: 5 forward, 5 backward
- a->b: still 15 forward

Final flow: 15 (saturated both s->a and s->b)

## Level Graph Visualization

Level 0: [s]
Level 1: [a, b] (both reachable from s with capacity)
Level 2: [t] (reachable from a or b)
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Network Flow

## Dinic Algorithm Complete Implementation

The Dinic algorithm implementation requires three components: graph construction with residual edges, BFS for level graph computation, and DFS for blocking flow. The it[] array (current edge pointer) is crucial for the optimizations — it ensures each edge is examined at most once per phase.

## Ford-Fulkerson Edge Cases

With integer capacities, Ford-Fulkerson always terminates. However, if capacities are irrational and the algorithm repeatedly picks bad augmenting paths, it may converge only in the limit (infinite steps). This is a theoretical concern, not practical.

## Bipartite Matching Construction

To use max flow for bipartite matching:
1. Create a source node linked to all left-side vertices (capacity 1)
2. Connect each left vertex to its adjacent right vertices (capacity 1)
3. Connect each right vertex to the sink (capacity 1)
4. Run max flow
5. A flow of 1 on a left-to-right edge indicates a matched pair

## Min-Cut Construction

After max flow is computed, run BFS/DFS from s in the residual network. Vertices reachable from s are in S; others are in T. Any original edge from S to T that is saturated (0 residual capacity) is part of the min-cut.

## Push-Relabel Overview

Each node v has a height h(v) and excess e(v). Initially, h(s) = V, all others 0. Push from source to all neighbors saturates outgoing edges. The main loop pushes excess from higher nodes to lower nodes, relabeling (increasing height) when no push is possible. When no node has excess except s and t, the algorithm terminates.
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Network Flow Implementation

## Dinic Implementation Steps

1. Build graph with adjacency lists of edges (to, rev, cap)
2. Initialize flow = 0
3. BFS from s to compute level[] (distance from s):
   a. Set level[s] = 0, queue all adjacent with cap > 0
   b. If t is unreachable, break
4. While BFS reaches t:
   a. Initialize it[] (current edge pointer for each node) to 0
   b. While DFS finds a path from s to t:
      - Track minimum residual capacity along path
      - Subtract from forward edges, add to reverse edges
      - Add to total flow
   c. After DFS returns 0, run BFS again for new level graph
5. Return total flow

## Bipartite Matching Steps

1. Build flow network: source->left size 1, left->right size 1, right->sink size 1
2. Run Dinic on the constructed network
3. Extract matching: check which left->right edges have 0 residual capacity
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes — Network Flow

- **Missing reverse edges** — Every edge needs a reverse edge with 0 initial capacity
- **Wrong rev index** — Setting rev incorrectly breaks residual updates
- **Overflow with int capacities** — Use long for capacities that may exceed 2^31
- **BFS/DFS infinite loops** — Not checking visited states in DFS
- **Forgetting level graph recomputation** — Must recompute after each blocking flow
- **Self-loops** — Adding edges from v to v can cause problems
- **Integer capacity but long flow** — Flow accumulation may exceed int range
- **Multiple edges between same nodes** — Should merge them into one edge
- **Not handling disconnected graphs** — BFS returning false means no more flow
- **Reusing graph without resetting** — Residual capacities must be reset between runs
"@

wf "DEBUGGING.md" @"
# Debugging — Network Flow

## Print Residual Capacities

```java
void printGraph() {
    for (int v = 0; v < n; v++) {
        for (Edge e : graph[v]) {
            if (e.cap > 0) {
                System.out.println(v + " -> " + e.to + " : " + e.cap);
            }
        }
    }
}
```

## Verify Flow Conservation

```java
boolean isFlowValid() {
    for (int v = 0; v < n; v++) {
        if (v == s || v == t) continue;
        long net = 0;
        for (Edge e : graph[v]) net += (originalCap - e.cap);
        if (net != 0) return false;
    }
    return true;
}
```

## Common Debug Scenarios

- Flow too low: check for missing edges or wrong capacities
- Infinite loop: check that DFS makes progress each call
- Wrong min cut: verify reachable set from source in residual graph
"@

wf "REFACTORING.md" @"
# Refactoring — Network Flow

## Extract Dinic into Reusable Class

```java
class DinicMaxFlow {
    private List<Edge>[] graph;
    private int[] level, it;
    
    public DinicMaxFlow(int n) { ... }
    public void addEdge(int from, int to, long cap) { ... }
    public long maxFlow(int s, int t) { ... }
}
```

## Edge Data Record (Java 16+)

Use records for immutable edge data: record Edge(int to, int rev, long cap) {}

## Factory Pattern for Flow Algorithms

```java
interface MaxFlowAlgorithm {
    long computeMaxFlow(int n, int s, int t, List<EdgeDef> edges);
}
```

Allow easy switching between Ford-Fulkerson, Edmonds-Karp, Dinic, and Push-Relabel.

## Parallel BFS/DFS

For very large graphs, parallelize level computation and blocking flow search using thread pools.

## Memory Optimization

Use int[] instead of objects for edges: store to, rev, cap in parallel arrays.
"@

wf "PERFORMANCE.md" @"
# Performance — Network Flow

## Algorithm Comparison

| Algorithm | Time Complexity | Practical Performance |
|-----------|----------------|---------------------|
| Ford-Fulkerson | O(C * E) | Slow for large capacities |
| Edmonds-Karp | O(V * E^2) | Consistent but slow |
| Dinic | O(V^2 * E) | Fast, widely used |
| Dinic (unit) | O(min(V^{2/3}, sqrt(E)) * E) | Very fast for bipartite matching |
| Push-Relabel (HL) | O(V^2 * sqrt(E)) | Fastest in practice |

## Benchmark Data

For a random graph with V=1000, E=10000:
- Ford-Fulkerson: 50-500ms (depends on capacities)
- Edmonds-Karp: 200-300ms
- Dinic: 10-50ms
- Push-Relabel: 5-20ms

## Optimization Tips

- Use adjacency with Edge objects for clarity; switch to arrays for speed
- Preallocate edge lists with expected capacity
- Use iterative DFS with explicit stack to avoid recursion depth issues
"@

wf "SECURITY.md" @"
# Security — Network Flow

## Capacity Overflow

If capacities are provided from untrusted sources, they could cause integer overflow. Use long and validate capacities.

## Graph Size DoS

An attacker could specify a very large graph (millions of nodes) causing memory exhaustion. Validate n and edge count before processing.

## Resource Exhaustion

Pathological graphs can cause exponential iterations in Ford-Fulkerson with DFS. Always prefer Edmonds-Karp or Dinic for safety.

## Irrational Capacities

In theory, irrational capacities can cause non-termination in Ford-Fulkerson. Always use rational (integer) capacities or use a polynomial algorithm.
"@

wf "ARCHITECTURE.md" @"
# Architecture — Network Flow

## Component Design

```
MaxFlow Library
├── Graph (adjacency list with residual support)
├── Algorithm interface
│   ├── FordFulkerson (DFS)
│   ├── EdmondsKarp (BFS)
│   ├── Dinic (level graph + blocking flow)
│   └── PushRelabel (global relabeling)
└── Applications
    ├── BipartiteMatching
    ├── MinCut
    └── MinCostMaxFlow
```

## Integration with Other Libraries

Use with graph loading from files (DIMACS format), visualization tools (Graphviz), and benchmarking harnesses.

## Test Strategy

- Test on small networks with known max flows
- Test bipartite matching on randomly generated graphs
- Test min-cut recovery by verifying s-t disconnection
"@

wf "EXERCISES.md" @"
# Exercises — Network Flow

## Beginner
1. Implement a flow network with addEdge and print residual capacities
2. Trace Ford-Fulkerson on a network with 4 nodes
3. Implement Edmonds-Karp using BFS for shortest path
4. Compute max flow on a simple triangle network

## Intermediate
5. Implement Dinic with level graph and blocking flow
6. Solve bipartite matching for a 5x5 bipartite graph
7. Compute min-cut from max flow solution
8. Verify the max-flow min-cut theorem on sample networks

## Advanced
9. Implement Push-Relabel with global relabeling heuristic
10. Solve the assignment problem using min-cost max-flow
11. Find edge-disjoint paths between two vertices in a graph
12. Implement the baseball elimination algorithm using max flow
"@

wf "QUIZ.md" @"
# Quiz — Network Flow

1. What does the max-flow min-cut theorem state?
2. Why does Edmonds-Karp use BFS instead of DFS?
3. What is the purpose of reverse edges in the residual network?
4. How does a level graph improve Dinic over Edmonds-Karp?
5. What is the augmentation step in Ford-Fulkerson?
6. How is bipartite matching reduced to max flow?
7. What defines a valid cut in a flow network?
8. Why can Ford-Fulkerson fail with irrational capacities?
"@

wf "FLASHCARDS.md" @"
# Flashcards — Network Flow

- Q: Max-flow min-cut theorem? -> A: Max flow value = min cut capacity
- Q: Ford-Fulkerson worst-case? -> A: O(C*E) with integer capacities
- Q: Edmonds-Karp complexity? -> A: O(V * E^2)
- Q: Dinic complexity? -> A: O(V^2 * E)
- Q: Dinic for unit capacities? -> A: O(sqrt(V) * E)
- Q: Purpose of level graph? -> A: Ensures forward progress toward sink
- Q: Bipartite matching via flow? -> A: Add source/sink with unit capacities
- Q: Min-cut from max flow? -> A: Reachable nodes from source in residual
"@

wf "INTERVIEW.md" @"
# Interview Questions — Network Flow

1. "Explain the max-flow min-cut theorem." — Fundamental duality in network flow
2. "Find the maximum matching in a bipartite graph." — Reduce to max flow
3. "Find edge-disjoint paths between two nodes." — Set unit capacities on all edges
4. "Can a team still win the pennant?" — Baseball elimination via max flow
5. "Model task assignment as a flow problem." — Bipartite matching with constraints
6. "Design an algorithm to find the minimum s-t cut." — Run max flow, find reachable set
"@

wf "REFLECTION.md" @"
# Reflection — Network Flow

- Why is the residual network concept so powerful? What other algorithms use similar ideas?
- How does the max-flow min-cut theorem connect to duality in other optimization problems?
- Why do different augmenting path strategies lead to such different time complexities?
- How would you adapt these algorithms for dynamic graphs where capacities change over time?
- What are the limitations of flow-based modeling for real-world problems?
"@

wf "REFERENCES.md" @"
# References — Network Flow

- Ford, L.R., Fulkerson, D.R. "Flows in Networks." Princeton University Press, 1962.
- Edmonds, J., Karp, R.M. "Theoretical Improvements in Algorithmic Efficiency for Network Flow Problems." JACM, 1972.
- Dinic, E.A. "Algorithm for Solution of a Problem of Maximum Flow in a Network with Power Estimation." Soviet Math. Doklady, 1970.
- Goldberg, A.V., Tarjan, R.E. "A New Approach to the Maximum-Flow Problem." JACM, 1988.
- Ahuja, R.K., Magnanti, T.L., Orlin, J.B. "Network Flows." Prentice Hall, 1993.
- Cormen, T.H. et al. "Introduction to Algorithms." MIT Press, 4th Edition, 2022.
"@

Write-Host "17-network-flow: All 24 markdown files created"

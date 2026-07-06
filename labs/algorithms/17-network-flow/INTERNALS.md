# Network Flow — Internal Mechanics

## Residual Edge Representation

`java
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
`

## Dinic: Level Graph (BFS)

`java
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
`

## Dinic: Blocking Flow (DFS)

`java
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
`
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

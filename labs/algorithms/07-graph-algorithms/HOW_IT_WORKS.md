# How Graph Algorithms Work

## BFS
`
Graph: A-B-C, A-D, D-E, C-E
Start at A:
Queue: [A] → visit A → [B, D]
Queue: [B, D] → visit B → [D, C]
Queue: [D, C] → visit D → [C, E]
Queue: [C, E] → visit C → [E]
Queue: [E] → visit E → []
Order: A, B, D, C, E
`

## Dijkstra
`
From A to F:
A(0) → relax B(4), C(2)
C(2) → relax D(5)
B(4) → relax E(6)
D(5) → relax F(7)
E(6) → no update
F(7) → done
`
"@

wf "INTERNALS.md" @"
# Graph Algorithms — Internal Mechanics

## BFS
`java
public List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
    List<Integer> result = new ArrayList<>();
    Queue<Integer> queue = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    queue.offer(start);
    visited.add(start);
    while (!queue.isEmpty()) {
        int node = queue.poll();
        result.add(node);
        for (int neighbor : graph.getOrDefault(node, List.of())) {
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                queue.offer(neighbor);
            }
        }
    }
    return result;
}
`

## DFS Recursive
`java
public void dfs(Map<Integer, List<Integer>> graph, int node, Set<Integer> visited, List<Integer> result) {
    visited.add(node);
    result.add(node);
    for (int neighbor : graph.getOrDefault(node, List.of())) {
        if (!visited.contains(neighbor))
            dfs(graph, neighbor, visited, result);
    }
}
`

## Dijkstra
`java
public int[] dijkstra(Map<Integer, List<Edge>> graph, int n, int src) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    PriorityQueue<Node> pq = new PriorityQueue<>();
    pq.offer(new Node(src, 0));
    while (!pq.isEmpty()) {
        Node curr = pq.poll();
        if (curr.dist > dist[curr.id]) continue;
        for (Edge e : graph.getOrDefault(curr.id, List.of())) {
            int nd = curr.dist + e.weight;
            if (nd < dist[e.to]) {
                dist[e.to] = nd;
                pq.offer(new Node(e.to, nd));
            }
        }
    }
    return dist;
}
`
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Graph Algorithms

## Graph Definitions
- V vertices, E edges
- Adjacency matrix: O(V²) space
- Adjacency list: O(V + E) space

## Shortest Path Properties
- Triangle inequality: dist(u,v) ≤ dist(u,w) + dist(w,v)
- Subpath of shortest path is also shortest
- No shortest path exists if negative cycle is reachable

## MST Properties
- Cut property: Lightest edge crossing any cut is in some MST
- Cycle property: Heaviest edge in any cycle is not in any MST

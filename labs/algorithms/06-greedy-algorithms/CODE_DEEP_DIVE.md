# Code Deep Dive — Greedy

## Dijkstra's Algorithm
`java
public int[] dijkstra(Map<Integer, List<Edge>> graph, int n, int source) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[source] = 0;
    PriorityQueue<Node> pq = new PriorityQueue<>();
    pq.offer(new Node(source, 0));
    while (!pq.isEmpty()) {
        Node curr = pq.poll();
        if (curr.dist > dist[curr.id]) continue;
        for (Edge e : graph.getOrDefault(curr.id, List.of())) {
            int newDist = curr.dist + e.weight;
            if (newDist < dist[e.to]) {
                dist[e.to] = newDist;
                pq.offer(new Node(e.to, newDist));
            }
        }
    }
    return dist;
}
`

## Kruskal's MST
`java
int kruskal(int n, List<Edge> edges) {
    Collections.sort(edges);
    UnionFind uf = new UnionFind(n);
    int totalWeight = 0;
    for (Edge e : edges) {
        if (uf.union(e.u, e.v)) totalWeight += e.weight;
    }
    return totalWeight;
}
`
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Greedy

## Solving Greedy Problems
1. Identify greedy choice: What local decision leads to global optimum?
2. Define sorting/priority: What order yields the greedy choice?
3. Make the choice: Select current best option
4. Update state: Reduce to smaller instance
5. Repeat until solved

## Proving Greedy Works
1. Show greedy choice property: First greedy choice is part of some optimal solution
2. Show optimal substructure: After greedy choice, remaining problem is equivalent to original on smaller input

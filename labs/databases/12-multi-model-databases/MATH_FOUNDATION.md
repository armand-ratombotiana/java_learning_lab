# Math Foundation: Multi-Model & Polyglot

## Graph Traversal Complexity
```
BFS/DFS: O(V + E)
Dijkstra: O((V + E) log V)
PageRank: O(V × iterations)
Shortest path: Linear in graph size
```

## Vector Similarity
```
Cosine Similarity: A·B / (||A|| × ||B||)
Euclidean Distance: √(Σ(Ai - Bi)²)
ANN (Approximate Nearest Neighbor): ~O(log N) with HNSW
```

## Consistency Models
- **Strong**: All reads see latest write. Latency proportional to distance to leader.
- **Bounded Staleness**: Read lag ≤ K versions or Δ time.
- **Session**: Read-your-writes within session.
- **Eventual**: All replicas converge given no writes.

## RU (Request Units) in Cosmos DB
```
RU cost ≈ a × documentSize + b × indexEntries + c × queryComplexity
1 RU ≈ 1KB read, ~5KB write
Throughput: RU/s per partition
Throttling: 429 (too many requests) when exceeded
```

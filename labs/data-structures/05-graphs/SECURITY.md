# Security Considerations for Graphs

## Denial of Service

### Huge Graph Creation

```java
// Attacker-controlled vertex count
int vertices = Integer.MAX_VALUE;
int[][] matrix = new int[vertices][vertices];  // OutOfMemoryError!
```

Always validate vertex/edge counts before allocation.

### Exponential Path Explosion

In unweighted graphs, the number of paths between two vertices can be exponential. Algorithms enumerating all paths are vulnerable to DoS with crafted inputs.

### Deep DFS Stack Overflow

```java
// Malicious linear graph (V = 100,000)
void dfs(int v) {
    visited[v] = true;
    for (int n : adj[v])
        if (!visited[n]) dfs(n);  // StackOverflowError at depth ~10K
}
```

Mitigation: use iterative BFS or explicit stack for unknown-depth graphs.

## Graph Injection

If graph data comes from external sources:
- Validate vertex IDs are within bounds
- Check for duplicate edges (can cause O(E²) time in naive implementations)
- Verify no self-loops unless expected

## Serialization

Deserializing graph data from untrusted sources:
- Can create arbitrary edge connections
- May specify a huge vertex count leading to OOM
- Could encode a graph triggering worst-case algorithm performance

## PageRank Spam

In web graphs, attackers create link farms to artificially boost PageRank. Algorithms must detect and penalize such manipulation.

## Privacy

- **Social graph inference**: graph structure can reveal relationships not explicitly stated
- **Traffic analysis**: communication graphs can reveal who talks to whom
- **Differential privacy** needed for graph data publication (edge-DP, node-DP)

## Thread Safety

Graphs are not thread-safe. Concurrent modification can:
- Lose edges
- Create inconsistent adjacency structures
- Cause iterations to miss vertices

Use explicit synchronization or immutable graph snapshots for concurrent access.

# Mathematical Foundation of Dijkstra's

## 📐 Proof of Correctness (By Induction)
Dijkstra's algorithm is guaranteed to find the shortest path because of the following invariant:
*For every visited node $v$, the distance $dist(v)$ recorded by the algorithm is the absolute shortest path from the source to $v$.*

**Base Case**: The source node $s$ has $dist(s) = 0$, which is trivially the shortest path.
**Inductive Step**: When we pick a node $u$ with the minimum distance from the unvisited set, we assume $dist(u)$ is correct. If there were a shorter path through some unvisited node $x$, then $dist(x)$ would have to be smaller than $dist(u)$ (since all weights are non-negative), and we would have picked $x$ instead of $u$.

## 📈 Complexity Analysis
Let $V$ be the number of vertices (nodes) and $E$ be the number of edges.

### 1. Naive Implementation (Array-based)
- Picking the minimum distance node: $O(V)$ per node.
- Total time to pick all nodes: $O(V^2)$.
- Updating neighbors: $O(1)$ per edge, total $O(E)$.
- **Total Time**: $O(V^2)$. Better for dense graphs where $E \approx V^2$.

### 2. Optimized Implementation (Binary Heap / Priority Queue)
- Picking the minimum distance node: $O(\log V)$ using a Priority Queue.
- Total time to pick all nodes: $O(V \log V)$.
- Relaxing an edge (updating the heap): $O(\log V)$ per edge.
- **Total Time**: $O((V+E) \log V)$. Significantly better for sparse graphs.

### 3. Space Complexity
- We must store the distances for all nodes: $O(V)$.
- We must store the graph (Adjacency List): $O(V + E)$.
- **Total Space**: $O(V + E)$.
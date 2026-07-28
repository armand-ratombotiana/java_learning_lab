# LeetCode Pattern Cheatsheet for Data Engineering Interviews

## 1. Sliding Window
- **Use when**: Contiguous subarray/substring, fixed or variable length
- **Template**: Maintain left/right pointers, expand right, shrink left when condition violated
- **DE relevance**: Windowed aggregations in streaming (tumbling/hoping windows)

## 2. Two Pointers
- **Use when**: Sorted array, pair search, in-place partitioning
- **Template**: left=0, right=n-1, move based on sum/comparison
- **DE relevance**: Merge sort in external sorting, partitioned file reads

## 3. HashMap / Counting
- **Use when**: Frequency counting, duplicate detection, histogram
- **Template**: Build frequency map, iterate to check conditions
- **DE relevance**: Data profiling, cardinality estimation, distinct count sketches

## 4. Heap (Priority Queue)
- **Use when**: Top-K, median streaming, merge K-sorted lists
- **Template**: Min-heap for largest K, max-heap for smallest K
- **DE relevance**: Merge K shuffled partitions in Spark, top-N in streaming

## 5. Union-Find (DSU)
- **Use when**: Connected components, graph cycles, equivalence
- **Template**: parent array, find with path compression, union by rank
- **DE relevance**: Data lineage graph connectivity, lineage impact analysis

## 6. Topological Sort
- **Use when**: Dependency resolution, DAG ordering
- **Template**: Build adjacency + in-degree, Kahn's algorithm (BFS)
- **DE relevance**: DAG scheduling (Airflow/Spark), lineage graph traversal

## 7. Trie (Prefix Tree)
- **Use when**: Prefix matching, autocomplete, IP routing
- **Template**: Node with children array/map, boolean isEnd
- **DE relevance**: Data catalog tag search, column name prefix matching

## 8. Binary Search
- **Use when**: Sorted data, "minimum feasible" or "maximum possible"
- **Template**: lo, hi, while(lo<hi), mid = lo+(hi-lo)/2, check(mid)
- **DE relevance**: Partition pruning, index lookup, log-structured merge trees

## 9. BFS / DFS (Graph Traversal)
- **Use when**: Shortest path, connectivity, all paths
- **Template**: BFS uses queue, DFS uses stack/recursion, visited set
- **DE relevance**: Schema traversal, lineage graph exploration, dependency resolution

## 10. Dynamic Programming
- **Use when**: Optimal substructure, overlapping subproblems
- **Template**: Define dp[i] or dp[i][j], base case, recurrence relation
- **DE relevance**: Optimizing multi-stage pipeline resource allocation

## 11. Bit Manipulation
- **Use when**: State compression, bloom filters, flags
- **Template**: XOR for find unique, bitmask for subsets
- **DE relevance**: Bloom filters in HBase/Redis, HyperLogLog sketches

## 12. Monotonic Stack / Queue
- **Use when**: Next greater/smaller element, sliding window max
- **Template**: Maintain increasing/decreasing stack; pop while condition
- **DE relevance**: Time-series anomaly detection, latency peak analysis

---

### Data Engineering-Specific Algorithm Patterns

| Pattern                | Application                                              |
|------------------------|----------------------------------------------------------|
| External Merge Sort    | Sorting data that doesn't fit in memory                  |
| Hash Partitioning      | Distributing keys across reducers/partitions             |
| Consistent Hashing     | Ring-based partition for horizontal scaling              |
| Merge Join             | Joining two sorted datasets without loading all in memory|
| Count-Min Sketch       | Approximate frequency counting with bounded error        |
| HyperLogLog            | Cardinality estimation using minimal memory              |
| Reservoir Sampling     | Random sampling from a stream of unknown size            |
| Token Bucket           | Rate limiting in streaming ingestion                     |

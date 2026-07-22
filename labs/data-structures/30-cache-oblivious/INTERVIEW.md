# Interview Questions: Cache-Oblivious Data Structures

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| (No standard LeetCode problems — system design focus) | — | Google, Microsoft, Oracle, Amazon, Meta | Memory hierarchy / cache-efficient algorithms |

## NeetCode Reference
Not in NeetCode. Cache-oblivious algorithms are a system design / architecture topic.

## Company-Specific Questions

### Google
- Explain the ideal cache model (Z = cache size, L = cache line size) and why cache-oblivious algorithms are optimal for all cache sizes simultaneously
- How does the van Emde Boas layout for binary trees achieve cache-oblivious performance?
- Design a cache-oblivious matrix multiplication algorithm (recursive blocking) and analyze its I/O complexity
- Compare cache-aware vs cache-oblivious algorithms — when would you prefer each?

### Microsoft
- How does Windows NT's virtual memory manager benefit from cache-oblivious data layouts?
- Implement a cache-oblivious B-tree (Frigo B-tree) — how does it achieve O(log_B N) block transfers?
- How would you design a cache-oblivious sorting algorithm (Funnelsort)?

### Meta
- How does the memory hierarchy (L1/L2/L3 cache, RAM, disk) affect algorithm design?
- Explain the recursive matrix transpose algorithm — why is it cache-oblivious?
- Design a cache-oblivious data layout for a social graph adjacency list

### Amazon
- How does DynamoDB use cache-oblivious principles for SSTable block layout?
- Design a cache-oblivious index for S3-based data lake queries
- What is the Z-order curve and how does it create a cache-oblivious layout for 2D data?

### Apple
- How does Metal (GPU programming) use cache-oblivious tiling for shader performance?
- Design a cache-oblivious pixel layout for image processing filters
- How would you lay out a texture atlas for cache-oblivious access patterns?

### Oracle
- How do database storage engines use cache-oblivious B-tree layouts for B+-tree nodes?
- Explain the I/O complexity of cache-oblivious vs cache-aware database algorithms
- How does Oracle Exadata's smart scan benefit from cache-oblivious data layouts?

## Real Production Scenarios

- **Scenario 1: Cache-Oblivious Matrix Multiplication** — A scientific computing library uses recursive matrix multiplication with a base case tuned for small sizes. The algorithm divides matrices into quadrants recursively, computing 8 sub-products. The recursive structure ensures that working sets fit into cache regardless of cache size, achieving near-peak FLOP rates on any hardware.

- **Scenario 2: Database Index Layout** — A storage engine lays out B-tree nodes in a recursive block layout (van Emde Boas layout) instead of the traditional sequential layout. This ensures that any range query on the tree accesses nodes in a cache-friendly order, reducing cache misses by 30-50% compared to standard B-trees.

- **Scenario 3: Sorting Large Datasets** — A big data platform uses Funnelsort (cache-oblivious sorting algorithm) instead of quicksort for sorting data that doesn't fit in cache. Funnelsort's I/O complexity is O(n/B · log_M/B (n/B)) block transfers — optimal for the cache-oblivious model — and adapts automatically to any cache level.

## Interview Tips

- Time: Same as traditional complexity (e.g., O(n log n) for sorting) but with optimal I/O complexity
- Space (cache): analyzed in terms of cache misses / block transfers. Optimal I/O complexity is matched against the ideal cache model
- Key concept: an algorithm is cache-oblivious if it has optimal I/O complexity for any cache size Z and line size L, without knowing Z or L
- Ideal cache model: fully associative, automatic replacement, tall cache assumption (Z = Ω(L²))
- The tall cache assumption (Z ≥ L²) is critical for many cache-oblivious proofs and matches real hardware
- I/O complexity lower bounds: sorting Ω(n/B · log_M/B (n/B)), matrix multiply Ω(n³/B√M)

## Java-Specific Considerations

- Cache-oblivious algorithms focus on data layout and access patterns, not language-specific
- In Java, cache misses are harder to control due to: object headers (unnecessary bytes), garbage collection (object relocation), JIT compilation (inlining and code generation)
- Memory layout control in Java: use `int[]` or `long[]` flat arrays instead of `ArrayList<Node>` to avoid pointer chasing and object header overhead
- `sun.misc.Contended` annotation (Java 8+) prevents false sharing (related cache optimization)
- `java.nio.ByteBuffer` — direct memory allocation for cache-oblivious layouts outside GC heap
- `VarHandle` (Java 9+) provides memory access with ordering semantics
- `StringUTF16` — Java 9+ stores strings as byte[] with coder field (cache-friendly vs char[] with 2 bytes per char)
- `java.util.Arrays` methods use cache-friendly loops with system intrinsics
- `System.arraycopy()` is a native, cache-efficient bulk copy operation
- JVM's JIT compiles small recursive methods inline — recursion depth for recursive cache-oblivious algorithms (log n) is acceptable
- For cache-oblivious B-trees: store tree nodes in a single `long[]` array with encoded child pointers (position * 2 + type bit) to reduce memory indirection
- Practical Java implementation of cache-oblivious layouts: use flat arrays with indices, not objects with references

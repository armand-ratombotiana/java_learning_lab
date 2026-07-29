# Guide: Union-Find (Disjoint Set Union)

## Overview

**Union-Find**, also called **Disjoint Set Union (DSU)** or **Disjoint Set Data Structure**, tracks a partition of elements into disjoint (non-overlapping) subsets. It supports two operations:
- **Find**: Determine which subset an element belongs to
- **Union**: Merge two subsets into one

With path compression and union by rank/size, both operations run in amortised O(α(n)) time — where α(n) is the inverse Ackermann function, which grows so slowly that it's effectively constant (< 5 for all practical n).

### Why Not Use a Graph Library?

For dynamic connectivity problems, DSU is:
- **Simpler**: No graph construction needed
- **Faster**: O(α(n)) vs O(n + m) for BFS/DFS
- **Online**: Handles edges being added over time
- **Memory efficient**: Just parent + rank arrays

---

## ASCII Diagram

```
Initial state:  {0} {1} {2} {3} {4} {5} {6}
Each element is its own parent (self-loop):
  0    1    2    3    4    5    6
  ↓    ↓    ↓    ↓    ↓    ↓    ↓
  0    1    2    3    4    5    6

After union(1,2):
  0    1    2    3    4    5    6
  ↓    ↓   ↗    ↓    ↓    ↓    ↓
  0    1   2    3    4    5    6
       ↙
      1 (parent of 2)

After union(2,3):
  0    1 ──→ 2 ──→ 3    4    5    6
  ↓    ↓              ↓    ↓    ↓
  0    1              4    5    6
       ↓
       2 (path compression: 2.find() → 1)
       ↓
       3

After union(5,6):
  0    1 ──→ 3    4    5 ──→ 6
  ↓    ↓         ↓    ↓
  0    1         4    5 (path compress: 6.find() → 5)
```

### Path Compression

```
find(3):
  3.parent = 2
  2.parent = 1
  1.parent = 1 (root)
  → compress: 3.parent = 1, 2.parent = 1
  → return 1

Before: 3 → 2 → 1
After:  3 → 1, 2 → 1
```

---

## Source Code Walkthrough

The implementation is in `src/UnionFind.java`.

### Structure (lines ~3-8)

```java
class UnionFind {
    private final int[] parent;
    private final int[] rank;    // used for union by rank
    private int components;       // count of disjoint sets

    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        components = n;
        for (int i = 0; i < n; i++) parent[i] = i;
    }
}
```

### Find with Path Compression (lines ~10-16)

```java
public int find(int x) {
    if (parent[x] != x) {
        parent[x] = find(parent[x]); // path compression
    }
    return parent[x];
}
```

**Walkthrough `find(3)` with parent chain 3→2→1→1:**

```
Step 1: x=3, parent[3]=2 ≠ 3 → recurse find(2)
Step 2: x=2, parent[2]=1 ≠ 2 → recurse find(1)
Step 3: x=1, parent[1]=1 == 1 → return 1
Step 2: parent[2] = 1, return 1
Step 1: parent[3] = 1, return 1
```

After this, both 2 and 3 point directly to root 1. Future finds are O(1).

### Union by Rank (lines ~18-30)

```java
public boolean union(int x, int y) {
    int rootX = find(x);
    int rootY = find(y);
    if (rootX == rootY) return false; // already same set

    if (rank[rootX] < rank[rootY]) {
        parent[rootX] = rootY;        // attach smaller tree under larger
    } else if (rank[rootX] > rank[rootY]) {
        parent[rootY] = rootX;
    } else {
        parent[rootY] = rootX;        // same rank: attach, increase rank
        rank[rootX]++;
    }
    components--;
    return true;
}
```

**Walkthrough `union(4, 8)` with n=10:**

```
Phase 1 (several previous unions):
  Sets: {0,1,2}, {3,4,5}, {6,7}, {8,9}
  find(4) → root = 3 (after compression)
  find(8) → root = 8 (no compression yet)

Phase 2 (attach):
  rank[3] = 1, rank[8] = 0
  rank[3] > rank[8] → parent[8] = 3

Result: {0,1,2,3,4,5,8,9}, {6,7}
  components = 2
```

### Connected / Count (lines ~32-40)

```java
public boolean connected(int x, int y) {
    return find(x) == find(y);
}

public int count() {
    return components;
}
```

---

## Complexity Table

| Operation | Without Optimisation | With Path Compression | With Both | Notes |
|-----------|---------------------|----------------------|-----------|-------|
| Find | O(n) | O(log n) | O(α(n)) | α(n) ≤ 4 for n ≤ 10^6000 |
| Union | O(n) | O(log n) | O(α(n)) | Same as find |
| Connected | O(n) | O(log n) | O(α(n)) | Two finds |
| Count | O(1) | O(1) | O(1) | Tracked separately |

### α(n) — Inverse Ackermann Function

**Values:**
- α(10³) = 4
- α(10⁶) = 4
- α(10⁹) = 4
- α(10^6000) = 5

**Practical meaning**: For any realistic n, DSU operations are O(1).

---

## Comparison with Alternatives

| Feature | DSU | BFS/DFS | Graph Library | Balanced BST |
|---------|-----|---------|---------------|-------------|
| Dynamic edges | Yes (add only) | No (needs full graph) | Yes | Yes |
| Delete edges | No (offline DQ) | N/A | Yes | No |
| Connectivity query | O(α(n)) | O(n+m) per query | O(n+m) | O(log n) |
| Memory | O(n) | O(n+m) | O(n+m) | O(n) |
| Implementation | ~20 lines | ~50 lines | Large | ~200 lines |
| Worst-case | O(α(n)) | O(n+m) | O(n+m) | O(log n) |

**When NOT to use DSU:**
- Need to DELETE edges (DSU only supports additions)
- Need to query path properties (distance, shortest path)
- Graph is static and you only need one connectivity check (BFS is fine)
- Need dynamic graph with deletions (use Link-Cut Tree or offline D&C)

---

## Use Cases

### 1. Kruskal's Minimum Spanning Tree
**Algorithm**: Sort edges by weight, union vertices for each edge. If find(u) != find(v), add edge to MST.
**Why DSU**: Cycle detection in O(α(n)) per edge.

### 2. Number of Islands II (Dynamic Grid)
**Problem**: Grid starts all water. Add land cells one at a time. Count islands after each addition.
**Why DSU**: DSU tracks connected components. Each new land cell union with its 4 neighbours.

### 3. Social Network Friend Circles
**Problem**: Find connected components in a friendship graph.
**Why DSU**: Union friendships, find components. Count distinct roots.

### 4. Accounts Merge (LC 721)
**Problem**: Merge accounts that share an email address.
**Why DSU**: Union accounts by common email, then collect emails per root.

### 5. Percolation / Image Segmentation
**Problem**: Determine when water can flow through a porous material.
**Why DSU**: Virtual top/bottom nodes. Union open cells with neighbours. Check if top and bottom are connected.

### 6. Satisfiability of Equations (LC 990)
**Problem**: Given equations like "a==b" and "a!=c", determine if they're satisfiable.
**Why DSU**: Union all `==` pairs. Then verify `!=` pairs are in different sets.

---

## Common Pitfalls

### 1. Forgetting Path Compression
```java
// WRONG — O(n) find
public int find(int x) {
    while (parent[x] != x) x = parent[x];
    return x;
}

// RIGHT — path compression
public int find(int x) {
    if (parent[x] != x) parent[x] = find(parent[x]);
    return parent[x];
}
```

### 2. Union Without Rank/Size
Always attach smaller tree to larger. Without this, worst-case tree height = O(n).

### 3. 0-Index vs 1-Index Confusion
DSU typically uses 0-indexed. For 1-indexed problems, allocate n+1 size.

### 4. Not Initialising Parent
Every element must start as its own parent. `parent[i] = i;`

### 5. Off-by-One in Union
Call `union(x, y)` not `union(find(x), find(y))`. The union implementation calls find internally.

---

## Advanced Variants

### DSU with Rollback (Undoable Union)
Support `snapshot()` and `rollback()` for offline dynamic connectivity. Each union pushes a record of changes to a stack. Rollback pops records and restores.

### DSU with Delete (Offline)
Use divide-and-conquer on time intervals. Add edges during their lifetime, query at points. O((n+q) log n) using segment tree over time.

### Disjoint Set Union on Values (HashMap-based)
```java
class DSUMap<K> {
    Map<K, K> parent = new HashMap<>();
    Map<K, Integer> rank = new HashMap<>();

    K find(K x) {
        if (!parent.containsKey(x)) { parent.put(x, x); rank.put(x, 0); }
        if (!parent.get(x).equals(x)) {
            parent.put(x, find(parent.get(x)));
        }
        return parent.get(x);
    }
}
```

### Path Halving / Splitting
Variants of path compression that don't use recursion — helpful in languages without tail-call optimisation.

---

## Testing the Implementation

```java
UnionFind uf = new UnionFind(10);

uf.union(0, 1);
uf.union(2, 3);
uf.union(4, 5);
uf.union(6, 7);
uf.union(8, 9);

assert uf.count() == 5;  // 5 pairs = 5 components

uf.union(1, 2); // connect {0,1} with {2,3}
assert uf.count() == 4;
assert uf.connected(0, 3) == true;

uf.union(5, 6);
assert uf.count() == 3;

assert uf.connected(0, 9) == false;
```

### Edge Case Tests
```java
// Single element
UnionFind uf1 = new UnionFind(1);
assert uf1.find(0) == 0;
assert uf1.count() == 1;

// All elements already connected
UnionFind uf2 = new UnionFind(5);
uf2.union(0, 1);
uf2.union(1, 2);
uf2.union(2, 3);
uf2.union(3, 4);
assert uf2.count() == 1;
assert uf2.connected(0, 4) == true;

// Union of already-connected elements
assert uf2.union(0, 4) == false; // returns false, no change
```

---

## Key Interview Takeaways

1. **DSU = component tracking with O(α(n)) operations**. The near-constant time is the key selling point.

2. **Two optimisations**: Path compression (flattens tree) + union by rank (controls height). Use both.

3. **Must always call find in recursion**: `parent[x] = find(parent[x])` — this is the recursive path compression.

4. **Applications**: MST (Kruskal), dynamic connectivity, graph component analysis, equation satisfiability.

5. **Limitations**: Can't delete edges, can't query path length, only bipartite tracking.

6. **Variants**: Offline D&C for deletions, rollback for snapshots, HashMap-based for arbitrary objects.
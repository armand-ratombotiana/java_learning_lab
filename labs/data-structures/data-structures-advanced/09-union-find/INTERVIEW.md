# Interview Questions: Union-Find (Disjoint Set Union)

## 17 FAANG-Style Interview Questions

### Question 1
> Implement Union-Find with path compression and union by rank.

**Answer:**

```java
class UnionFind {
    int[] parent, rank;
    int components;

    UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        components = n;
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }

    boolean union(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return false;
        if (rank[rx] < rank[ry]) parent[rx] = ry;
        else if (rank[rx] > rank[ry]) parent[ry] = rx;
        else { parent[ry] = rx; rank[rx]++; }
        components--;
        return true;
    }

    boolean connected(int x, int y) { return find(x) == find(y); }
    int count() { return components; }
}
```

---

### Question 2
> Explain the inverse Ackermann function. Why is DSU effectively O(1)?

**Answer:**
The inverse Ackermann function α(n) grows incredibly slowly:
- α(10³) = 4
- α(10⁶) = 4
- α(10⁹) = 4
- α(10^6000) = 5

For any practical input size, α(n) ≤ 5. So DSU with path compression + union by rank has amortised O(α(n)) ≈ O(1).

---

### Question 3
> Solve "Number of Islands" (LC 200) using DSU.

**Answer:**

```java
int numIslands(char[][] grid) {
    int m = grid.length, n = grid[0].length;
    UnionFind uf = new UnionFind(m * n);
    int ones = 0;

    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (grid[i][j] == '1') {
                ones++;
                int idx = i * n + j;
                if (i > 0 && grid[i-1][j] == '1') uf.union(idx, (i-1)*n + j);
                if (j > 0 && grid[i][j-1] == '1') uf.union(idx, i*n + (j-1));
            }
        }
    }
    return ones - (n*m - uf.count()); // total elements - (total - components)
}
```

**Alternative**: BFS/DFS is simpler. DSU is useful when grid changes dynamically (Number of Islands II).

---

### Question 4
> Find redundant connection in a graph (LC 684).

**Answer:**
For each edge [u, v], if find(u) == find(v), this edge creates a cycle → it's redundant. Otherwise union(u, v).

```java
int[] findRedundantConnection(int[][] edges) {
    UnionFind uf = new UnionFind(edges.length + 1);
    for (int[] e : edges) {
        if (!uf.union(e[0], e[1])) return e;
    }
    return null;
}
```

---

### Question 5
> Solve "Accounts Merge" (LC 721).

**Answer:**

```java
List<List<String>> accountsMerge(List<List<String>> accounts) {
    Map<String, Integer> emailToId = new HashMap<>();
    Map<String, String> emailToName = new HashMap<>();
    int id = 0;

    UnionFind uf = new UnionFind(accounts.size() * 10); // upper bound

    for (List<String> acc : accounts) {
        String name = acc.get(0);
        for (int i = 1; i < acc.size(); i++) {
            String email = acc.get(i);
            emailToName.put(email, name);
            if (!emailToId.containsKey(email)) {
                emailToId.put(email, id++);
            }
            // Union first email with all others in this account
            if (i == 1) continue;
            uf.union(emailToId.get(acc.get(1)), emailToId.get(email));
        }
    }

    Map<Integer, List<String>> groups = new HashMap<>();
    for (String email : emailToId.keySet()) {
        int root = uf.find(emailToId.get(email));
        groups.computeIfAbsent(root, k -> new ArrayList<>()).add(email);
    }

    List<List<String>> result = new ArrayList<>();
    for (List<String> emails : groups.values()) {
        Collections.sort(emails);
        List<String> acc = new ArrayList<>();
        acc.add(emailToName.get(emails.get(0)));
        acc.addAll(emails);
        result.add(acc);
    }
    return result;
}
```

---

### Question 6
> Determine if equations are satisfiable (LC 990).

**Answer:**
Two passes: first, process all `==` equations (union). Second, check all `!=` equations (if find(x) == find(y), return false).

---

### Question 7
> Find the longest consecutive sequence in an unsorted array (LC 128).

**Answer:**
DSU over value range. For each value, if value-1 exists, union. If value+1 exists, union. Track component sizes. Return max size.

Alternative (simpler): Use HashSet, for each element check if it starts a sequence (no element-1), then count consecutive elements.

---

### Question 8
> Number of operations to make network connected (LC 1319).

**Answer:**
Union all cable connections. Count components (computers with unique find). Result = components - 1. If total cables < n-1, return -1 (not enough cables).

---

### Question 9
> Design DSU that supports undo (rollback union).

**Answer:**

```java
class RollbackDSU {
    int[] parent, size;
    Stack<int[]> history = new Stack<>();

    int find(int x) {
        while (parent[x] != x) x = parent[x];
        return x; // NO path compression (breaks rollback)
    }

    boolean union(int a, int b) {
        a = find(a); b = find(b);
        if (a == b) { history.push(null); return false; }
        if (size[a] < size[b]) { int t = a; a = b; b = t; }
        history.push(new int[]{b, parent[b], a, size[a]});
        parent[b] = a;
        size[a] += size[b];
        return true;
    }

    void snapshot() { history.push(new int[]{-1}); } // marker

    void rollback() {
        while (!history.isEmpty()) {
            int[] op = history.pop();
            if (op[0] == -1) break;
            parent[op[0]] = op[1];
            size[op[3]] = op[3]; // restore old size
        }
    }
}
```

---

### Question 10
> Design a DSU with arbitrary objects (not just integer indices).

**Answer:**
Use HashMap-based DSU:
```java
class DSU<K> {
    Map<K, K> parent = new HashMap<>();
    Map<K, Integer> size = new HashMap<>();

    K find(K x) {
        if (!parent.containsKey(x)) { parent.put(x, x); size.put(x, 1); }
        if (!parent.get(x).equals(x)) parent.put(x, find(parent.get(x)));
        return parent.get(x);
    }

    void union(K a, K b) {
        a = find(a); b = find(b);
        if (a.equals(b)) return;
        if (size.get(a) < size.get(b)) { K t = a; a = b; b = t; }
        parent.put(b, a);
        size.put(a, size.get(a) + size.get(b));
    }
}
```

---

### Question 11
> Solve "Number of Islands II" (LC 305) — adding land cells dynamically.

**Answer:**
Maintain DSU over grid. For each new land cell, increment counter. Union with 4 neighbours if they're land. Decrement counter for each successful union. Return counter.

---

### Question 12
> Find the largest component size by common factor (LC 952).

**Answer:**
For each number, factorise it. For each prime factor p, union(num, p). After processing all numbers, find the largest component among the original numbers.

---

### Question 13
> Solve "Longest Consecutive Sequence" using DSU.

**Answer:**
DSU over seen values. For each value v:
1. Mark v as seen
2. If v-1 is seen, union(v, v-1)
3. If v+1 is seen, union(v, v+1)
4. Track max component size

---

### Question 14
> How does DSU help in Kruskal's MST algorithm?

**Answer:**
Sort edges by weight. For each edge (u, v, w): if find(u) != find(v), union and add edge to MST. DSU provides O(α(n)) cycle detection vs O(n) naive check.

---

### Question 15
> Minimise Hamming Distance After Swap Operations (LC 1722).

**Answer:**
Union indices that can be swapped (connected by swap operations). Group elements by component. Compare source vs target counts within each component — sum(sources) - sum(matching) = min swaps.

---

### Question 16
> Check if a graph is bipartite using DSU.

**Answer:**
Maintain DSU over 2n nodes (n for each colour). For edge (u, v):
- union(u, v+n) and union(u+n, v)
- After all unions, check that for all i, find(i) != find(i+n)

Alternative: BFS colouring is simpler.

---

### Question 17
> Find the earliest moment when all friends become connected (LC 1101).

**Answer:**
Sort logs by timestamp. Process in order, union friends. When components == 1, return current timestamp.
# Interview Questions: Skip List

## 17 FAANG-Style Interview Questions

### Question 1
> Implement a skip list with insert, search, and delete.

**Answer:**

```java
class SkipList {
    static class Node {
        int val;
        Node[] forward;
        Node(int val, int level) { this.val = val; forward = new Node[level + 1]; }
    }

    Node head;
    int maxLevel = 16;
    int currentLevel = 0;
    Random rand = new Random();

    SkipList() { head = new Node(Integer.MIN_VALUE, maxLevel); }

    int randomLevel() {
        int level = 0;
        while (rand.nextDouble() < 0.5 && level < maxLevel) level++;
        return level;
    }

    void insert(int val) {
        Node[] update = new Node[maxLevel + 1];
        Node cur = head;
        for (int i = currentLevel; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].val < val)
                cur = cur.forward[i];
            update[i] = cur;
        }
        int level = randomLevel();
        if (level > currentLevel) {
            for (int i = currentLevel + 1; i <= level; i++) update[i] = head;
            currentLevel = level;
        }
        Node newNode = new Node(val, level);
        for (int i = 0; i <= level; i++) {
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }
    }

    boolean search(int val) {
        Node cur = head;
        for (int i = currentLevel; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].val < val)
                cur = cur.forward[i];
        }
        cur = cur.forward[0];
        return cur != null && cur.val == val;
    }

    boolean delete(int val) {
        Node[] update = new Node[maxLevel + 1];
        Node cur = head;
        for (int i = currentLevel; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].val < val)
                cur = cur.forward[i];
            update[i] = cur;
        }
        cur = cur.forward[0];
        if (cur == null || cur.val != val) return false;
        for (int i = 0; i <= currentLevel; i++) {
            if (update[i].forward[i] != cur) break;
            update[i].forward[i] = cur.forward[i];
        }
        while (currentLevel > 0 && head.forward[currentLevel] == null)
            currentLevel--;
        return true;
    }
}
```

---

### Question 2
> How does a skip list compare to a balanced BST for a concurrent environment?

**Answer:**
Skip list wins for concurrency:
- **Fine-grained locking**: Each node has its own lock. Locking nodes during insertion only affects adjacent nodes.
- **CAS operations**: Forward pointers can be updated atomically. No lock needed for search.
- **No rebalancing**: BST rotations require locking multiple nodes at higher levels.
- **Lock-free skip list exists**: Using `AtomicReference` and mark nodes (Java's `ConcurrentSkipListMap`).

BST requires locking from root to leaf during rotations, which is a scalability bottleneck.

---

### Question 3
> What's the expected number of levels in a skip list with n elements?

**Answer:**
With p = 0.5:
- Expected max level: log₂ n
- Probability of level k: (0.5)^(k+1)
- Expected pointers per node: 1/(1-p) = 2
- Level 0: n nodes, Level 1: n/2, Level 2: n/4, ...

For n = 10⁶, max level ≈ log₂(10⁶) ≈ 20. Set MAX_LEVEL = 32 as safety buffer.

---

### Question 4
> Design a skip list for a gaming leaderboard with top-N queries.

**Answer:**

```java
class Leaderboard {
    SkipList sl; // sorted by score
    Map<Integer, Integer> scores; // player ID → score

    void addScore(int playerId, int score) {
        Integer old = scores.get(playerId);
        if (old != null) sl.delete(old); // remove old score
        scores.put(playerId, score);
        sl.insert(score);
    }

    List<Integer> topN(int n) {
        List<Integer> result = new ArrayList<>();
        Node cur = sl.head;
        // Find the maximum element
        for (int i = sl.currentLevel; i >= 0; i--)
            while (cur.forward[i] != null && cur.forward[i].val != null)
                cur = cur.forward[i];
        // Traverse backwards (requires dual-link) or use reverse iterator
        // Simplified: use sorted map
    }
}
```

**Optimisation**: Use an **indexable skip list** that stores subtree node count at each skip pointer. Then `rank(playerId)` and `kthPlayer(k)` are both O(log n).

---

### Question 5
> Explain the random level generation. What happens if you always generate level 0?

**Answer:**
If all nodes have level 0, the skip list degrades to a simple sorted linked list. Search becomes O(n). Level generation with p=0.5 creates the multi-level structure that enables O(log n) traversal.

---

### Question 6
> How does Java's ConcurrentSkipListMap implement lock-free operations?

**Answer:**
- Forward pointers use `AtomicReference<Node>` for CAS
- Logical deletion: node is "marked" (deleted flag set) before unlinking
- **Find helper**: Scans and cleans marked nodes during traversal
- **Insert**: CAS on `prev.forward[i]` — if CAS fails, retry from scratch
- **Delete**: Mark node as deleted (logical removal), then CAS unlink (physical removal)

This ensures linearizability without blocking.

---

### Question 7
> What's the worst-case time complexity for a skip list? How do you mitigate it?

**Answer:**
Worst case: O(n) — all nodes get level 0 (extremely unlikely: (0.5)^n probability).
Mitigation: Use a deterministic PRNG with good distribution. Use a hybrid approach: if performance degrades, rebuild the skip list.

In practice, the probability of O(n) for n = 1000 is (0.5)^1000 ≈ 10^(-301) — effectively impossible.

---

### Question 8
> Given a skip list, implement a range scan returning all values in [l, r].

**Answer:**

```java
List<Integer> rangeScan(int l, int r) {
    List<Integer> result = new ArrayList<>();
    Node cur = head;
    // Find starting position
    for (int i = currentLevel; i >= 0; i--)
        while (cur.forward[i] != null && cur.forward[i].val < l)
            cur = cur.forward[i];

    cur = cur.forward[0]; // first element ≥ l
    while (cur != null && cur.val <= r) {
        result.add(cur.val);
        cur = cur.forward[0];
    }
    return result;
}
```

Complexity: O(log n + k) where k = number of elements in range. Much faster than RB tree O(log n + k) due to cache-friendly linked list traversal.

---

### Question 9
> Implement an indexable skip list that supports O(log n) rank queries.

**Answer:**
Each `forward` pointer stores NOT just the next node, but also the number of nodes it skips (span):

```java
static class Node {
    int val;
    Node[] forward;
    int[] span; // span[i] = number of nodes skipped by forward[i]
}

int rank(int val) {
    Node cur = head;
    int rank = 0;
    for (int i = currentLevel; i >= 0; i--) {
        while (cur.forward[i] != null && cur.forward[i].val < val) {
            rank += cur.span[i];
            cur = cur.forward[i];
        }
    }
    return rank + 1; // 1-indexed rank
}
```

---

### Question 10
> How would you implement a backward iterator for a skip list?

**Answer:**
Skip lists are singly linked at each level. For backward iteration:
- Add a `backward` pointer at level 0 (doubly linked base list)
- Or traverse the entire list forward into a stack/array, then reverse

The doubly linked variant is used in Redis ZSET.

---

### Question 11
> Compare skip list probability p = 0.25 vs p = 0.5.

**Answer:**
- **p = 0.5** (standard): Expected height = log₂ n, ~2 pointers per node
- **p = 0.25**: Expected height = log₄ n ≈ 0.5·log₂ n (lower), ~1.33 pointers per node (less memory), but longer search path (more steps)

Trade-off: p=0.5 → faster search, more memory. p=0.25 → more memory efficient, slower search.

---

### Question 12
> How does Redis use skip lists for sorted sets?

**Answer:**
Redis ZSET uses hash table (for score lookup by member) + skip list (for ordered operations). The skip list stores `(member, score)` pairs sorted by score. Duplicate scores are allowed — in that case, sort by member name.

---

### Question 13
> What's the memory overhead of a skip list compared to a BST?

**Answer:**
Each node in a skip list has on average 2 forward pointers (p=0.5). Each pointer = 8 bytes (64-bit). Node overhead = 8 + 2*8 + value = ~24-32 bytes.
BST: ~3 pointers per node (left, right, parent) = 24 bytes + value = ~32-40 bytes.
Comparable, but skip list has extra head nodes at higher levels.

---

### Question 14
> Implement a lock-free skip list insertion using CAS.

**Answer:**

```java
boolean insert(int val) {
    while (true) {
        Node[] update = new Node[MAX_LEVEL];
        Node cur = head;
        for (int i = currentLevel; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].val < val)
                cur = cur.forward[i];
            update[i] = cur;
        }
        cur = cur.forward[0];
        if (cur != null && cur.val == val) return false; // exists

        int level = randomLevel();
        Node newNode = new Node(val, level);

        for (int i = 0; i <= level; i++) {
            newNode.forward[i] = update[i].forward[i];
            if (!CAS(update[i].forward[i], newNode.forward[i], newNode)) {
                // CAS failed, retry
                continue;
            }
        }
        return true;
    }
}
```

**Note**: Simplified. Real implementations need mark nodes for deletion.

---

### Question 15
> How would you implement a persistent skip list?

**Answer:**
Copy path nodes (similar to persistent BST):
- PathCopy(node, level): create new node with same forward pointers, copy recursively
- On insert, create new nodes along search path + new node
- Share unchanged nodes between versions
- Root = pointer to new head node

---

### Question 16
> Design a time-series database storage engine using skip lists.

**Answer:**
Each time-series has a skip list keyed by timestamp:
- `insert(timestamp, value)` → O(log n)
- `rangeScan(t1, t2)` → O(log n + k) → values in time range
- `latestValue()` → follow level-0 forward to tail

Flush to disk as SSTable when skip list reaches size threshold.

---

### Question 17
> What happens if you use a terrible random number generator that always returns the same value?

**Answer:**
If RNG always returns 0: every node has level 0 → skip list degrades to sorted linked list. Insert/search/delete become O(n). Always use a quality PRNG (Java's `ThreadLocalRandom` is sufficient).
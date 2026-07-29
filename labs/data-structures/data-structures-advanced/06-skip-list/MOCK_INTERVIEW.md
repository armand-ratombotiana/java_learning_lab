# Mock Interview: Skip List

## Setting

- **Round**: System design / data structures deep dive
- **Duration**: 45 minutes
- **Focus**: Skip list internals, concurrent design

---

## Transcript

### Part 1: Warm-up (5 min)

**Interviewer:** What's the skip list and how does it relate to other balanced tree structures?

**Candidate:** A skip list is a multi-level linked list that achieves O(log n) expected search time using probabilistic balancing. Each node is assigned a random level — higher level nodes act as "express lanes" that skip over lower-level nodes.

Compared to Red-Black trees, skip lists are:
- Simpler to implement (~60 lines vs ~200 lines)
- Easier to make concurrent (lock-free variants exist)
- Better for range queries (forward pointers are cache-friendly)

**Interviewer:** When would you NOT use a skip list?

**Candidate:**
1. Need guaranteed O(log n) (use RB-tree)
2. Memory-constrained embedded systems (skip list has 2x pointer overhead)
3. Very small datasets (< 100 elements)

---

### Part 2: Core Problem — Design a Skip List (25 min)

**Interviewer:** Implement a skip list from scratch.

**Candidate:**

```java
class SkipList {
    class Node {
        int val;
        Node[] next;
        Node(int v, int l) { val = v; next = new Node[l+1]; }
    }

    Node head;
    int maxLevel = 16, curLevel = 0;
    Random rand = new Random();

    SkipList() { head = new Node(Integer.MIN_VALUE, maxLevel); }

    int randomLevel() {
        int l = 0;
        while (l < maxLevel && rand.nextDouble() < 0.5) l++;
        return l;
    }

    void insert(int v) {
        Node[] update = new Node[maxLevel+1];
        Node cur = head;
        for (int i = curLevel; i >= 0; i--) {
            while (cur.next[i] != null && cur.next[i].val < v)
                cur = cur.next[i];
            update[i] = cur;
        }

        int level = randomLevel();
        if (level > curLevel) {
            for (int i = curLevel+1; i <= level; i++) update[i] = head;
            curLevel = level;
        }

        Node newNode = new Node(v, level);
        for (int i = 0; i <= level; i++) {
            newNode.next[i] = update[i].next[i];
            update[i].next[i] = newNode;
        }
    }

    boolean search(int v) {
        Node cur = head;
        for (int i = curLevel; i >= 0; i--)
            while (cur.next[i] != null && cur.next[i].val < v)
                cur = cur.next[i];
        cur = cur.next[0];
        return cur != null && cur.val == v;
    }

    boolean delete(int v) {
        Node[] update = new Node[maxLevel+1];
        Node cur = head;
        for (int i = curLevel; i >= 0; i--) {
            while (cur.next[i] != null && cur.next[i].val < v)
                cur = cur.next[i];
            update[i] = cur;
        }
        cur = cur.next[0];
        if (cur == null || cur.val != v) return false;

        for (int i = 0; i <= curLevel; i++) {
            if (update[i].next[i] != cur) break;
            update[i].next[i] = cur.next[i];
        }
        while (curLevel > 0 && head.next[curLevel] == null) curLevel--;
        return true;
    }
}
```

**Interviewer:** Walk me through inserting 60 into a list with [10, 20, 30, 40, 50, 70, 80, 90, 95].

**Candidate:**
Let me trace:

Current levels: level 3 has head → 80 → null; level 2 has head → 40 → 80 → 95; level 1 has head → 20 → 40 → 60 → 80 → 90 → 95; level 0 has all.

Starting at level 3:
- head → 80, 80 > 60, drop to level 2
Level 2:
- head → 40, 40 < 60 → move to 40
- 40 → 80, 80 > 60, drop to level 1
Level 1:
- 40 → 60? No, 40.next[1] = 80. 80 > 60, drop to level 0
Level 0:
- 40 → 50, 50 < 60 → move to 50
- 50 → 70, 70 > 60, stop.

update[3] = head, update[2] = 40, update[1] = 40, update[0] = 50.

Generate level: say level = 2.

Since level (2) ≤ curLevel (3), no new levels needed.

newLevel = 2 → splice at levels 0, 1, 2:
- newNode.next[0] = 50.next[0] (= 70), 50.next[0] = newNode
- newNode.next[1] = 40.next[1] (= 80), 40.next[1] = newNode
- newNode.next[2] = 40.next[2] (= 80), 40.next[2] = newNode

Now 60 appears at levels 0, 1, 2.

**Interviewer:** Good. How would you handle duplicates?

**Candidate:** Several approaches:
1. Allow duplicates by using `<` (not `<=`) in search comparisons
2. Store a list of duplicate values at each node (e.g., `List<Integer>`)
3. Use a unique key per element (e.g., (value, insertionOrder))

For Redis ZSET, they sort by score first, then by member name for ties.

---

### Part 3: Follow-up (10 min)

**Interviewer:** How would you make this skip list thread-safe?

**Candidate:**
**Option 1: Coarse-grained locking** — wrap all public methods with `synchronized`. Simple but doesn't scale.

**Option 2: Fine-grained locking** — lock individual nodes during traversal. Insert locks update[i] nodes at each level. However, lock ordering matters to prevent deadlock.

**Option 3: Lock-free (CAS-based)** — use `AtomicReferenceArray<Node>` for forward pointers. Insert uses CAS to atomically update forward pointers. Delete uses "mark" nodes (logical deletion) + CAS for unlinking. This is how Java's `ConcurrentSkipListMap` works.

**Interviewer:** What's the simplest approach for a real system?

**Candidate:** If the system isn't heavily contested, use Java's `ConcurrentSkipListMap<Integer, String>` directly — it's already implemented and tested. If you need to implement it, fine-grained locking with `ReentrantLock` per node is manageable.

---

### Part 4: System Design (5 min)

**Interviewer:** How would you use a skip list in a distributed key-value store?

**Candidate:**
Each node in the cluster maintains a skip list for its key range (consistent hashing). The skip list supports:
- **Insert/Update**: Route by key hash to the owning node, insert into local skip list
- **Range scan**: Query the owning node, which traverses level-0 forward pointers
- **Concurrent readers/writers**: Lock-free skip list avoids contention

Redis Cluster uses skip lists for sorted set operations across shards.

---

## Debrief

### What Went Well
- Implemented skip list correctly from scratch
- Trace with insert 60 example was accurate
- Good lock-free vs fine-grained locking analysis

### Areas for Growth
- Could mention `span` attribute for rank queries
- Memory overhead (2n pointers) not mentioned

### Score
| Category | Score (1-5) |
|----------|-------------|
| Skip List Knowledge | 5 |
| Code Quality | 5 |
| Trace Accuracy | 5 |
| Complexity Analysis | 4 |
| Concurrency Discussion | 4 |
| System Design | 4 |
| **Overall** | **4.5 / 5** |
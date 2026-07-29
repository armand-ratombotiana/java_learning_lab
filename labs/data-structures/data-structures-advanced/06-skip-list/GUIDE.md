# Guide: Skip List

## Overview

A **Skip List** is a probabilistic data structure that allows O(log n) average search, insertion, and deletion in an ordered sequence. It consists of multiple levels of linked lists, where each higher level "skips" over elements, enabling fast traversal.

Invented by William Pugh in 1989, skip lists are an alternative to balanced BSTs (Red-Black, AVL) — simpler to implement and naturally concurrent.

### Why Not Use a Balanced BST?

| Aspect | Red-Black Tree | Skip List |
|--------|---------------|-----------|
| Implementation | Complex (rotations, colours) | Simple (linked lists) |
| Concurrency | Hard (fine-grained locking) | Easy (CAS on pointers) |
| Average complexity | O(log n) | O(log n) |
| Worst case | O(log n) guaranteed | O(n) (unlucky RNG) |
| Range scan | In-order traversal | Forward pointers (cache-friendly) |

**Key Insight**: Skip lists trade guaranteed O(log n) for simplicity + concurrent performance. In practice, worst-case O(n) never occurs with a good PRNG.

---

## ASCII Diagram

```
Level 3:  -∞ ---------------------------------------→ 80 ---------------→ null
Level 2:  -∞ -----------------------→ 40 ---------→ 80 ------→ 95 ----→ null
Level 1:  -∞ -------→ 20 ---------→ 40 ---→ 60 --→ 80 --→ 90 → 95 → null
Level 0:  -∞ → 10 → 20 → 30 → 40 → 50 → 60 → 70 → 80 → 90 → 95 → null
         (base list — all elements)
```

### Node Structure

Each node has:
- Value (the element)
- An array of `forward` pointers (size = node's level)
- `level`: randomly assigned during insert

### Search Path for Value 60

Start at highest level (level 3):
```
-∞ → 80      (80 > 60, drop to level 2)
-∞ → 40 → 80  (40 < 60, continue; 80 > 60, drop to level 1)
40 → 60       (60 found)
```

Total steps: 4 (vs 6 in base list). With more elements, savings increase.

---

## Source Code Walkthrough

### Node Structure (lines ~5-10)

```java
class SkipListNode {
    int value;
    SkipListNode[] forward;
    int level;

    SkipListNode(int value, int level) {
        this.value = value;
        this.forward = new SkipListNode[level + 1];
        this.level = level;
    }
}
```

### Level Generation (lines ~12-18)

```java
private int randomLevel() {
    int level = 0;
    while (random.nextDouble() < 0.5 && level < MAX_LEVEL) {
        level++;
    }
    return level;
}
```

Each node independently gets level L with probability p^L. With p = 0.5:
- Level 0: 50% of elements
- Level 1: 25% of elements
- Level 2: 12.5% of elements
- Level k: p^(k+1) fraction

### Insert (lines ~20-45)

```java
public void insert(int value) {
    SkipListNode[] update = new SkipListNode[MAX_LEVEL + 1];
    SkipListNode current = head;

    // Find position at each level
    for (int i = currentLevel; i >= 0; i--) {
        while (current.forward[i] != null && current.forward[i].value < value) {
            current = current.forward[i];
        }
        update[i] = current;
    }

    // Generate random level
    int newLevel = randomLevel();
    if (newLevel > currentLevel) {
        for (int i = currentLevel + 1; i <= newLevel; i++) {
            update[i] = head;
        }
        currentLevel = newLevel;
    }

    // Create node and splice
    SkipListNode newNode = new SkipListNode(value, newLevel);
    for (int i = 0; i <= newLevel; i++) {
        newNode.forward[i] = update[i].forward[i];
        update[i].forward[i] = newNode;
    }
}
```

**Walkthrough `insert(60)` with current list:**

```
Step 1: Find predecessors at each level (update array)
  Level 3: head → 80 (stop, 80 > 60) → update[3] = head
  Level 2: head → 40 → 80 (stop, 80 > 60) → update[2] = 40
  Level 1: 40 → 60 doesn't exist yet → 40 → 60 → 70... stop
    Actually: from 40, forward[1] = 60 doesn't exist → skip to 80
    Correction: 40.forward[1] = 80 (next at level 1). So 40.forward[1].value=80 > 60. update[1] = 40
  Level 0: same pattern → insert at appropriate position

Step 2: Generate random level (say level = 2)

Step 3: Splice at levels 0, 1, 2
  newNode.forward[0] = update[0].forward[0] (50 or null)
  update[0].forward[0] = newNode
  ... same for levels 1 and 2
```

### Search (lines ~47-58)

```java
public boolean search(int value) {
    SkipListNode current = head;
    for (int i = currentLevel; i >= 0; i--) {
        while (current.forward[i] != null && current.forward[i].value < value) {
            current = current.forward[i];
        }
    }
    current = current.forward[0];
    return current != null && current.value == value;
}
```

### Delete (lines ~60-80)

```java
public boolean delete(int value) {
    SkipListNode[] update = new SkipListNode[MAX_LEVEL + 1];
    SkipListNode current = head;

    for (int i = currentLevel; i >= 0; i--) {
        while (current.forward[i] != null && current.forward[i].value < value) {
            current = current.forward[i];
        }
        update[i] = current;
    }

    current = current.forward[0];
    if (current == null || current.value != value) return false;

    for (int i = 0; i <= currentLevel; i++) {
        if (update[i].forward[i] != current) break;
        update[i].forward[i] = current.forward[i];
    }

    while (currentLevel > 0 && head.forward[currentLevel] == null) {
        currentLevel--;
    }
    return true;
}
```

---

## Complexity Table

| Operation | Average | Worst (unlucky) | Space |
|-----------|---------|-----------------|-------|
| Search | O(log n) | O(n) | O(1) |
| Insert | O(log n) | O(n) | O(log n) expected |
| Delete | O(log n) | O(n) | O(1) |
| Range scan [l, r] | O(log n + k) | O(n) | O(k) |

**Height properties** (p = 0.5):
- Max level: ~log₂ n with high probability
- Pointers per node: 1/(1-p) = 2 on average
- Prob(height > 3·log₂ n) < 1/n²

### Search Path Length

Expected number of steps: log₁/ₚ n ≈ (ln n) / (ln(1/p)) = (ln n) / (ln 2) ≈ 1.44·log₂ n

---

## Comparison with Alternatives

| Feature | Skip List | Red-Black Tree | AVL Tree | B-Tree |
|---------|-----------|---------------|----------|--------|
| Search | O(log n) avg | O(log n) | O(log n) | O(log n) |
| Insert | O(log n) avg | O(log n) | O(log n) | O(log n) |
| Delete | O(log n) avg | O(log n) | O(log n) | O(log n) |
| Memory | ~2n pointers | ~2n pointers | ~2n pointers | n pointers |
| Concurrent | Easy (CAS) | Hard | Hard | Hard |
| Range scan | Fast (forward ptr) | Slow (parent ptr) | Slow | Fast (leaf chain) |
| Implementation | ~60 lines | ~200 lines | ~150 lines | ~150 lines |
| Worst case | O(n) (rare) | O(log n) | O(log n) | O(log n) |

**When NOT to use skip list:**
- Need guaranteed O(log n) worst case (use RB-tree)
- Memory constrained (skip list uses ~2x pointers of BST)
- Very small n (< 100): linked list or array is simpler
- Deterministic behavior needed

---

## Use Cases

### 1. Redis Sorted Sets (ZSET)
**System**: Redis in-memory database
**Why skip list**: Simple, concurrent, range operations (ZRANGE, ZREVRANGE)
**Operations**: ZADD, ZRANK, ZRANGE — all O(log n)

### 2. ConcurrentSkipListMap (Java)
**System**: Java standard library
**Why skip list**: Lock-free using CAS (Compare-And-Swap). No blocking for concurrent reads/writes. Used in high-throughput systems.

### 3. LevelDB / RocksDB Memtable
**System**: LSM-tree storage engine
**Why skip list**: Insert in sorted order, flush to SSTable. Concurrent inserts don't block. Write-ahead logging for durability.

### 4. Gaming Leaderboards
**System**: Real-time score tracking
**Why skip list**: Insert/update score, range scan top N, rank query — all O(log n). Simple to understand and maintain.

### 5. Event Processing / Time Series
**System**: Event stream with ordered timestamps
**Why skip list**: Insert in order by timestamp, scan forward for events in window. Concurrent producers don't block.

---

## Common Pitfalls

### 1. Forgetting the Head Sentinel
Head node has value = -∞ (or minimum value). Its level = maxLevel. Its forward pointers initialise to null.

### 2. Wrong Level Update During Insert
Must update `update[]` for levels from 0 to newLevel. If newLevel > currentLevel, set `update[i] = head` for the new levels.

### 3. Not Tracking Current Max Level
`currentLevel` tracks the maximum level of any node. This reduces search time. Don't always start from MAX_LEVEL.

### 4. Remove Floors During Delete
After deleting, if `head.forward[currentLevel] == null`, decrement currentLevel. This prevents searching excessively high levels.

### 5. Duplicate Values
Skip list typically assumes unique keys. For duplicates:
- Use `<` not `<=` for search to avoid infinite loops
- Store values in a list at each node

---

## Advanced Variants

### Indexable Skip List
Each node tracks the width (number of elements) of each skip. Enables O(log n) rank queries: "find k-th element" or "find rank of value X".

### Concurrent Skip List
- **Lock-free**: Use `AtomicReference` for forward pointers, CAS for updates
- **Fine-grained locking**: Lock individual nodes during modification
- **Java's `ConcurrentSkipListMap`**: Implements lock-free using CAS and `marker` nodes for logical deletion

### Deterministic Skip List
Uses fixed pattern for levels instead of random. Different variants:
- **1-2-3 Skip List**: Ensures at most 3 nodes of same level in a row
- Guarantees O(log n) worst case but requires structural changes

### Rainbow Skip List
Data structure for temporal/versioned data. Multiple "stripes" for different time ranges. Used in versioned key-value stores.

---

## Testing the Implementation

```java
SkipList sl = new SkipList();

sl.insert(10);
sl.insert(20);
sl.insert(5);
sl.insert(15);

assert sl.search(10) == true;
assert sl.search(7) == false;
assert sl.search(20) == true;

sl.delete(20);
assert sl.search(20) == false;
assert sl.search(10) == true; // still there

// Range scan
List<Integer> range = sl.rangeScan(5, 15);
assert range.equals(List.of(5, 10, 15));
```

### Edge Case Tests
```java
// Empty list
SkipList empty = new SkipList();
assert empty.search(1) == false;
assert empty.delete(1) == false;

// Single element
SkipList single = new SkipList();
single.insert(42);
assert single.search(42) == true;
single.delete(42);
assert single.search(42) == false;

// Duplicates
SkipList dup = new SkipList();
dup.insert(5);
dup.insert(5); // second insert should not create duplicate
// Only verify that search returns true
assert dup.search(5) == true;

// Ordered traversal
SkipList ordered = new SkipList();
ordered.insert(3);
ordered.insert(1);
ordered.insert(2);
assert ordered.rangeScan(Integer.MIN_VALUE, Integer.MAX_VALUE)
    .equals(List.of(1, 2, 3));
```

---

## Key Interview Takeaways

1. **Skip list = simple concurrent sorted map**. Know that it's used in Redis, Java ConcurrentSkipListMap, and LevelDB.

2. **Probabilistic balancing**: Coin flip determines level. Expected O(log n), worst O(n) with astronomically low probability.

3. **Range scan is O(log n + k)**: Much faster than RB tree's parent-pointer traversal.

4. **Java specific**: `ConcurrentSkipListMap`, `ConcurrentSkipListSet` are the JDK implementations. Know their methods: `ceilingKey`, `floorKey`, `subMap`.

5. **Draw it**: Whiteboard diagram of multi-level linked list is clearer than code explanation.

6. **Level generation with p=0.5**: About n/2 nodes at level 0, n/4 at level 1, etc.
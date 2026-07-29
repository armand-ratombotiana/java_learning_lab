# Guide: Treap (Randomised BST)

## Overview

A **Treap** is a hybrid data structure that combines a **binary search tree** (BST) with a **binary heap**. Each node has both a key (BST property) and a randomly assigned priority (heap property). This combination gives balanced trees with high probability — O(log n) average for all operations.

The name "treap" comes from **Tree** + **HeAP**.

### Why Not Use a Red-Black Tree?

| Aspect | Red-Black Tree | Treap |
|--------|---------------|-------|
| Balance guarantee | O(log n) worst-case | O(log n) expected |
| Complexity | ~200 lines (insert+delete fixup) | ~60 lines (split+merge) |
| Split/merge | Not supported | O(log n) expected |
| Implicit operations | No | Yes (array-like) |
| Randomness | Deterministic | Random priorities |

**Key Insight**: Treap is simpler to implement than RB tree, and supports split/merge operations that RB tree cannot. For competitive programming and quick implementations, treap is often preferred.

---

## ASCII Diagram

```
Key-Value pairs: (key=a, priority=15), (key=b, priority=3),
(key=c, priority=20), (key=d, priority=7), (key=e, priority=10)

Treap structure (BST by key, Max-Heap by priority):

           (c, 20)      ← highest priority = root
          /       \
      (a, 15)    (e, 10)
      /    \      /    \
    nil   (b,3) nil    (d,7)
```

**Property**: In-order traversal gives keys in sorted order: a, b, c, d, e.

### Cartesian Tree

A treap is a **Cartesian tree** of the (key, priority) pairs:
- BST invariant applies to keys
- Heap invariant applies to priorities

### Split and Merge

Two fundamental operations that make treap powerful:

**split(root, key)** → (L, R): Split tree into left (keys ≤ key) and right (keys > key).

**merge(L, R)** → root: Merge two treaps where all keys in L ≤ all keys in R.

---

## Source Code Walkthrough

### Node Structure (lines ~5-10)

```java
class TreapNode {
    int key;
    int priority;
    TreapNode left, right;

    TreapNode(int key) {
        this.key = key;
        this.priority = ThreadLocalRandom.current().nextInt();
    }
}
```

### Split (lines ~12-30)

```java
TreapNode[] split(TreapNode root, int key) {
    if (root == null) return new TreapNode[]{null, null};

    if (root.key <= key) {
        // Root and its left subtree go to L
        // Split right subtree
        TreapNode[] pair = split(root.right, key);
        root.right = pair[0];
        return new TreapNode[]{root, pair[1]};
    } else {
        // Root and its right subtree go to R
        // Split left subtree
        TreapNode[] pair = split(root.left, key);
        root.left = pair[1];
        return new TreapNode[]{pair[0], root};
    }
}
```

**Walkthrough split(root with key range [1..10], key=5):**

```
Root = 8 (priority=25)
Left = 3 (priority=18), Right = 9 (priority=12)

1. root.key=8 > 5 → go left
2. split(3's subtree, 5):
   3.key=3 ≤ 5 → R-3.left=null, split(3.right=null, 5) → {null, null}
   3.right = null
   Return {3, null}
3. root.left = null (pair[1]=null)
   Return {3's subtree, root with 8,9}
```

### Merge (lines ~32-50)

```java
TreapNode merge(TreapNode left, TreapNode right) {
    if (left == null || right == null)
        return left == null ? right : left;

    if (left.priority > right.priority) {
        // Left has higher priority → left is root of result
        left.right = merge(left.right, right);
        return left;
    } else {
        // Right has higher priority → right is root of result
        right.left = merge(left, right.left);
        return right;
    }
}
```

**Heap property**: Higher priority node becomes the parent. This is the "randomisation" that provides balance.

### Insert (lines ~52-58)

```java
void insert(int key) {
    TreapNode newNode = new TreapNode(key);
    TreapNode[] pair = split(root, key);
    root = merge(merge(pair[0], newNode), pair[1]);
}
```

**Algorithm**: Split at key, merge left half + new node + right half.

### Delete (lines ~60-68)

```java
boolean delete(int key) {
    if (search(key) == false) return false;
    TreapNode[] pair = split(root, key);
    TreapNode[] midPair = split(pair[0], key - 1);
    root = merge(midPair[0], pair[1]);
    return true;
}
```

**Algorithm**: Split at key (separates >key), split left part at key-1 (separates =key), merge remaining parts.

---

## Complexity Table

| Operation | Average | Worst | Notes |
|-----------|---------|-------|-------|
| Search | O(log n) | O(n) | BST search, priority-guided |
| Insert | O(log n) | O(n) | Split + merge |
| Delete | O(log n) | O(n) | Two splits + merge |
| Split | O(log n) | O(n) | Recursive, priority-guided |
| Merge | O(log n) | O(n) | Priority-based root selection |
| Order statistic | O(log n) | O(n) | With subtree size augmentation |

---

## Comparison with Alternatives

| Feature | Treap | RB Tree | AVL Tree | Skip List |
|---------|-------|---------|----------|-----------|
| Balance | Expected | Guaranteed | Guaranteed | Expected |
| Split/merge | O(log n) | O(n) | O(n) | O(n) |
| Implicit ops | Yes | No | No | No |
| Implementation | ~60 lines | ~200 lines | ~150 lines | ~60 lines |
| Randomness | Required | None | None | Required |
| Persistent | Easy (path copy) | Hard | Hard | Hard |

**When NOT to use treap:**
- Need guaranteed O(log n) (use RB tree)
- Memory constrained (treap stores priority = 4 extra bytes per node)
- Deterministic behaviour required (some environments don't allow randomness)
- Very small n — overhead of priority + random calls

---

## Use Cases

### 1. Order-Statistic Tree
**Problem**: Find k-th smallest element, or rank of a given element
**Treap extension**: Augment each node with subtree size
```java
int kth(TreapNode root, int k) {
    int leftSize = (root.left == null) ? 0 : root.left.size;
    if (k <= leftSize) return kth(root.left, k);
    if (k == leftSize + 1) return root.key;
    return kth(root.right, k - leftSize - 1);
}
```

### 2. Implicit Treap (Array Operations)
**Problem**: Array with insert/delete/reverse at arbitrary positions
**Treap**: Split by position (subtree size), not key. No key in nodes — only implicit ordering.
**Operations**: Insert at position, delete at position, range reverse (lazy flag).

### 3. Range Query with Updates
**Problem**: Like segment tree but need insert/delete of arbitrary positions
**Implicit treap**: Each node stores aggregated value of its subtree (sum, min, max). Supports range query and point update with O(log n) complexity.

### 4. Competitive Programming
**Why treap**: Simpler than segment tree + BIT for complex operations (split, merge, reverse, insert, delete). Single data structure for many algorithms.

### 5. Collaborative Text Editor (Rope)
**Why treap**: Implicit treap for string operations — insert character at position, delete range, concatenate strings in O(log n).

---

## Common Pitfalls

### 1. Priority Collisions
Two nodes can have the same priority. Handle with tie-breaker: compare second random value, or compare by key.

### 2. Recursion Depth
Recursive split/merge with n = 10⁵ can stack overflow. Use iterative version or increase stack size.

### 3. Implicit Treap Key Confusion
In implicit treap, there is NO key. Position is determined by subtree size. `split(root, k)` splits first k elements from the rest.

### 4. Forgetting Subtree Size Updates
After split/merge/insert/delete, must update `size` field for all modified nodes.

### 5. Random Quality
Use `ThreadLocalRandom` (Java) or a well-seeded PRNG. `Math.random()` is slower and has less entropy.

---

## Advanced Variants

### Implicit Treap (Treap without Keys)
No key field. Position in array = in-order position. Split by subtree size.

```java
class ImplicitNode {
    int priority;
    int value;
    int size;      // subtree size
    boolean reversed; // lazy flag for reverse
    ImplicitNode left, right;
}
```

Operations:
- `splitBySize(root, k)`: first k elements, rest
- `merge(L, R)`: concatenate
- `insert(pos, value)`: split at pos, merge L + newNode + R
- `reverse(l, r)`: split into 3 parts, apply lazy flag to middle

### Persistent Treap
Functional (immutable) treap. Each insert/delete creates new nodes along the path. Old versions remain accessible. O(log n) space per modification. Used in purely functional languages (Haskell's Data.Set).

### Treap with Implicit Keys + Aggregates
Combine implicit ordering with range sum/min/max queries. Segment tree + BST insertion/deletion.

---

## Testing the Implementation

```java
Treap treap = new Treap();
treap.insert(10);
treap.insert(5);
treap.insert(15);
treap.insert(3);
treap.insert(7);

assert treap.search(7) == true;
assert treap.search(12) == false;
treap.delete(10);
assert treap.search(10) == false;

// In-order traversal should be sorted
List<Integer> sorted = treap.inOrder();
assert sorted.equals(List.of(3, 5, 7, 15));
```

### Edge Case Tests
```java
// Empty treap
Treap empty = new Treap();
assert empty.search(1) == false;
assert empty.kth(1) == null;

// Single element
Treap single = new Treap();
single.insert(42);
assert single.kth(1) == 42;

// Duplicates
Treap dup = new Treap();
dup.insert(5);
dup.insert(5); // should handle (or disallow)
```

---

## Key Interview Takeaways

1. **Treap = BST (key) + Heap (priority)**. The random priority creates balanced trees with high probability.

2. **Split and merge** are the key operations. All other operations (insert, delete, range ops) are built on these.

3. **Implicit treap** is treap without keys — works like an array. This is more useful than standard treap for many problems.

4. **Order statistics**: Augment with subtree size for O(log n) k-th element.

5. **Range reverse**: Lazy flag in implicit treap for O(log n) range reversal.

6. **Persistence**: Treap is naturally persistent (functional). Old versions remain valid after modification.

7. **Competitive programming**: Treap can replace segment tree + BIT + balanced tree in one structure.
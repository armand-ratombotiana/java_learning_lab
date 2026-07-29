# Interview Questions: Treap

## 17 FAANG-Style Interview Questions

### Question 1
> Implement a treap with insert, delete, and search. How does randomisation provide balance?

**Answer:**
Each node gets a random priority. The treap maintains BST ordering by key and max-heap ordering by priority. Since priorities are random, the tree shape is that of a random BST, which has expected height O(log n).

---

### Question 2
> Explain the split and merge operations. Why are they O(log n) expected?

**Answer:**
**split(root, key)**: Recursively descend based on DFS order. If root.key ≤ key, root goes to left tree; split the right subtree and attach left part as root's new right child. Otherwise symmetric.

**merge(L, R)**: If L.priority > R.priority, L is root, recursively merge L.right with R. Otherwise symmetric.

**Complexity**: Each step advances one level down the tree. Expected height = O(log n), so O(log n) expected time.

---

### Question 3
> What's the difference between a standard treap and an implicit treap?

**Answer:**
| Aspect | Standard Treap | Implicit Treap |
|--------|---------------|----------------|
| Key | Explicit (key field) | Position in array |
| Split by | Key value | Number of elements (k) |
| Use case | Ordered set | Dynamic array |
| Order statistics | By subtree size | By subtree size |
| Range reverse | Not meaningful | Lazy flag |

**Implicit treap**: No `key` field. `split(root, k)` splits first k elements into left, rest into right. Used for dynamic array operations.

---

### Question 4
> Implement k-th smallest element in a treap. What augmentation is needed?

**Answer:**
Augment each node with `size = size(left) + 1 + size(right)`.

```java
int kth(TreapNode node, int k) {
    if (node == null) return -1;
    int leftSize = node.left == null ? 0 : node.left.size;
    if (k <= leftSize) return kth(node.left, k);
    if (k == leftSize + 1) return node.key;
    return kth(node.right, k - leftSize - 1);
}
```

---

### Question 5
> How would you handle duplicate keys in a treap?

**Answer:**
Options:
1. **Store count**: Each node stores `count` of duplicate values
2. **Multiset treap**: Use `<` for left subtree, `≤` for right subtree (or vice versa)
3. **Unique constraint**: Simply disallow duplicates (standard set semantics)

Option 2 is simplest for a multiset:
```java
if (root.key < key) go right;
else go left; // including equal
```

---

### Question 6
> Implement range reverse on an implicit treap.

**Answer:**
Augment with a `reversed` lazy flag:

```java
void push(ImplicitNode node) {
    if (node != null && node.reversed) {
        swap(node.left, node.right);
        if (node.left != null) node.left.reversed ^= true;
        if (node.right != null) node.right.reversed ^= true;
        node.reversed = false;
    }
}

void reverse(ImplicitNode root, int l, int r) {
    ImplicitNode[] split1 = split(root, l);     // [0..l-1], [l..n-1]
    ImplicitNode[] split2 = split(split1[1], r - l + 1); // [l..r], [r+1..n-1]
    if (split2[0] != null) split2[0].reversed ^= true;
    root = merge(split1[0], merge(split2[0], split2[1]));
}
```

---

### Question 7
> Compare treap and segment tree for range min queries with insert/delete.

**Answer:**
| Aspect | Segment Tree | Implicit Treap |
|--------|-------------|----------------|
| Build | O(n) | O(n) |
| Range min | O(log n) | O(log n) |
| Point update | O(log n) | O(log n) |
| Insert position | O(n) (shift) | O(log n) |
| Delete position | O(n) (shift) | O(log n) |
| Range reverse | O(n) | O(log n) |

**Choose treap** when you need insert/delete at arbitrary positions. **Choose segment tree** when array is fixed size.

---

### Question 8
> What is a persistent treap? How does it work?

**Answer:**
A persistent treap creates new nodes only for modified nodes. Since treap is a functional data structure:
- `split` creates new nodes along the path (no node is mutated)
- `merge` creates a new root
- Old versions remain valid

**Space**: O(log n) per modification (path length). **Time**: O(log n). Useful for versioned data structures.

---

### Question 9
> Design a data structure that supports insert, delete, and getRandom in O(log n).

**Answer:**
Implicit treap + subtree sizing:
- `insert(pos, value)`: O(log n) via split/merge
- `delete(pos)`: O(log n) via split/merge
- `getRandom()`: Generate random k in [1, size], call kth(k) on implicit treap

This is more versatile than the standard "HashMap + ArrayList" solution (which has O(1) but doesn't support order-based operations).

---

### Question 10
> How would you merge two treaps where all keys in L ≤ all keys in R?

**Answer:**
This is the standard `merge(L, R)` operation:
```java
TreapNode merge(TreapNode L, TreapNode R) {
    if (L == null || R == null) return L == null ? R : L;
    if (L.priority > R.priority) {
        L.right = merge(L.right, R);
        updateSize(L);
        return L;
    } else {
        R.left = merge(L, R.left);
        updateSize(R);
        return R;
    }
}
```

---

### Question 11
> Design a text editor (rope) using an implicit treap.

**Answer:**
Each node stores a substring (character array or String). Leaf nodes are "chunks" of characters. Internal nodes aggregate:
- `length`: total characters in subtree
- `hash`: for equality comparison

Operations:
- `insert(pos, text)`: split at pos, merge chunks
- `delete(l, r)`: split into 3, merge outer
- `getChar(pos)`: navigate by subtree size
- `substring(l, r)`: split twice, merge middle

---

### Question 12
> What's the expected depth of a treap with n nodes?

**Answer:**
Expected depth = O(log n). More precisely, the expected depth of a node is approximately 2·ln n ≈ 1.39·log₂ n. This is because random priorities create a random BST shape, which has expected logarithmic height.

---

### Question 13
> How would you implement a treap that supports range sum queries with point updates?

**Answer:**
Augment each node with `sum` = left.sum + value + right.sum. Update on split/merge/insert:

```java
void updateSize(TreapNode node) {
    if (node != null) {
        node.sum = (node.left == null ? 0 : node.left.sum)
                 + node.value
                 + (node.right == null ? 0 : node.right.sum);
    }
}
```

Range query: split into [0..l-1], [l..r], [r+1..n-1]. Read middle.sum. Merge back.

---

### Question 14
> Compare treap with skip list for implementing an order statistic tree.

**Answer:**
| Aspect | Treap | Skip List |
|--------|-------|-----------|
| Order statistics | O(log n) | O(log n) |
| Implementation | ~60 lines | ~60 lines |
| Concurrency | Hard | Easy (CAS) |
| Memory | key + priority + 2 ptrs | value + avg 2 forward ptrs |
| Persistence | Easy | Hard |

**Choose treap** for persistence, functional programming, and simplicity. **Choose skip list** for concurrency.

---

### Question 15
> How would you implement a treap that supports lazy range updates (add value to range)?

**Answer:**
Add `lazyAdd` field to each node. On split/merge, push lazy before recursing:
```java
void push(ImplicitNode node) {
    if (node != null && node.lazyAdd != 0) {
        node.value += node.lazyAdd;
        if (node.left != null) node.left.lazyAdd += node.lazyAdd;
        if (node.right != null) node.right.lazyAdd += node.lazyAdd;
        node.lazyAdd = 0;
    }
}
```

---

### Question 16
> Explain the "implicit" in implicit treap. What does it mean for the binary search property?

**Answer:**
"Implicit" means there is no explicit key to BST-order by. Instead, the in-order position determines ordering. The BST invariant is: all nodes in the left subtree come before the current node in the array (their in-order position is earlier), and all nodes in the right subtree come after.

Split by size: `split(root, k)` separates the first k elements (by in-order position) from the rest. This is the "key" in implicit treap — not a stored value, but an implicit position.

---

### Question 17
> Design a data structure for an online judge's ranking system using treap.

**Answer:**
Implicit treap of submissions sorted by timestamp:
- `submit(userId, problemId, score)`: append to end
- `rankOf(userId)`: find position by user ID (need user ID → pos map)
- `topK(k)`: traverse first k elements
- `range(l, r)`: split into 3, read middle

For per-user aggregates, maintain HashMap of user → score + position in treap.
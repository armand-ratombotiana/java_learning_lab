# Interview Questions: Red-Black Tree

## 17 FAANG-Style Interview Questions

### Question 1
> What are the five properties of a Red-Black tree? Why are they important?

**Answer:**
1. Every node is red or black
2. Root is black
3. NULL/NIL leaves are black
4. Red nodes have only black children (no two reds adjacent)
5. All paths from root to NIL have the same number of black nodes (black height)

These properties ensure the tree is balanced: the longest path (alternating red-black) is at most twice the shortest path (all black). This guarantees O(log n) height.

---

### Question 2
> Implement a Red-Black tree insert with fixup.

**Answer:**
Standard BST insert + fixup based on uncle colour. (See GUIDE.md for full implementation.)

Key points:
- New node is always red
- Fixup loops while parent is red
- Three cases based on uncle colour
- Root set to black at end

---

### Question 3
> How many rotations can happen during a single insert? During a delete?

**Answer:**
- **Insert**: At most 2 rotations (O(1) amortised)
- **Delete**: At most 3 rotations (O(1) amortised)

This is why RB tree is better for write-heavy workloads than AVL tree (which can have O(log n) rotations per insert/delete).

---

### Question 4
> Compare Red-Black trees with AVL trees. When would you use each?

**Answer:**
| Aspect | RB Tree | AVL Tree |
|--------|---------|----------|
| Height | ≤ 2·log₂(n+1) | ≤ 1.44·log₂ n |
| Insert rotations | ≤ 2 | O(log n) |
| Delete rotations | ≤ 3 | O(log n) |
| Search speed | Slower (taller) | Faster (shorter) |

**Use RB tree** for insert/delete-heavy workloads (Java TreeMap, Linux CFS scheduler).
**Use AVL tree** for search-heavy workloads (database indexing, read-mostly workloads).

---

### Question 5
> What happens if we insert a node and the uncle is red?

**Answer:**
**Case 1**: Recolour parent (red→black), uncle (red→black), grandparent (black→red). Then move the violation up to the grandparent and continue. This doesn't require rotations — just colour flips.

---

### Question 6
> How does Java's TreeMap use Red-Black trees?

**Answer:**
`TreeMap<K,V>` is a RB tree implementation. It:
- Stores key-value pairs sorted by key
- Guarantees O(log n) for `get`, `put`, `remove`
- Provides `subMap`, `headMap`, `tailMap` for range views
- Uses `Comparator` or natural ordering

The RB tree ensures sorted order and balanced performance.

---

### Question 7
> Design a consistent hashing ring using a Red-Black tree.

**Answer:**
Use `TreeMap<Long, Server>`:
- **Add server**: Insert N virtual nodes (hash(server + i) % ring size)
- **Remove server**: Delete all virtual nodes
- **Find server for key**: `treeMap.ceilingEntry(keyHash)` — returns server with hash ≥ key (wrap around with `firstEntry()`)
- **Complexity**: O(log S) per operation, S = servers × virtual nodes

---

### Question 8
> What's a left-leaning Red-Black tree? How is it different?

**Answer:**
LLRB (Sedgewick) enforces that red nodes are always left children. This reduces insert fixup from 6 cases to 3. It's simpler to implement but the tree shape is more constrained.

**Properties** simplified:
- No red node has a red right child
- No node has two red children
- Equivalent to 2-3 tree (not 2-3-4)

---

### Question 9
> Explain the "double black" problem during deletion.

**Answer:**
When a black node is deleted and its replacement is also black, the black height property (property 5) is violated. The replacement inherits an "extra blackness", creating a "double black" node.

**Fixup**: Rotate and recolour to eliminate the double black. Cases depend on sibling's colour and sibling's children's colours.

---

### Question 10
> How is a Red-Black tree equivalent to a 2-3-4 B-tree?

**Answer:**
Merge each black node with its red children (if any) into a single 2-3-4 tree node:
- Black node only → 2-node (1 key)
- Black + left red child → 3-node (2 keys)
- Black + right red child → 3-node (2 keys)
- Black + two red children → 4-node (3 keys)

This correspondence proves RB tree height ≤ 2·height of 2-3-4 tree = 2·log₄(n+1).

---

### Question 11
> What's the time complexity of verifying RB tree properties? How would you implement it?

**Answer:**
O(n) — DFS traversal checking each property.
```java
int verify(Node node, int blackCount) {
    if (node == NIL) {
        if (blackCount != expectedBlackHeight) throw error;
        return 1;
    }
    if (node.isRed && (node.left.isRed || node.right.isRed)) throw error;
    int left = verify(node.left, blackCount + (node.isRed ? 0 : 1));
    int right = verify(node.right, blackCount + (node.isRed ? 0 : 1));
    if (left != right) throw error;
    return left;
}
```

---

### Question 12
> Why are Red-Black trees used for the Linux Completely Fair Scheduler (CFS)?

**Answer:**
CFS maintains tasks sorted by `vruntime` (virtual runtime). Operations:
- Insert new task (wakeup → O(log n))
- Pick next task (leftmost → O(log n))
- Update vruntime (delete + reinsert → O(log n))
- Move task between runqueues

RB tree is chosen over AVL because CFS is insert/delete heavy (many task switches). The O(1) amortised rotations per operation are critical.

---

### Question 13
> Design an LRU cache with O(log n) eviction using a Red-Black tree.

**Answer:**
Combine HashMap + TreeMap:
- `HashMap<K, Node>` for key → (value, timestamp) lookup
- `TreeMap<Long, K>` for timestamp → key ordering
- On access: update timestamp, remove old entry from TreeMap, insert new
- On evict: remove first entry from TreeMap, remove from HashMap
- O(log n) for all operations

---

### Question 14
> What's the difference between top-down and bottom-up RB tree insertion?

**Answer:**
- **Bottom-up** (standard): Insert as BST, then walk up fixing violations
- **Top-down**: While descending to insert, split any 4-nodes (black with two red children). This prevents violations at lower levels, so no fixup walk-up needed.

Top-down is more complex to implement but avoids the second pass.

---

### Question 15
> Implement a function that converts a sorted array to a Red-Black tree in O(n).

**Answer:**
Build a perfectly balanced BST (like sorted array to BST), then colour nodes to satisfy RB properties.

```java
Node build(int[] arr, int l, int r) {
    if (l > r) return NIL;
    int mid = l + (r - l) / 2;
    Node node = new Node(arr[mid]);
    node.left = build(arr, l, mid - 1);
    node.right = build(arr, mid + 1, r);
    node.isRed = false; // all nodes black
    return node;
}
```

All nodes black → RB tree valid (every path has same black height). Height = log₂ n.

---

### Question 16
> How would you implement a Red-Black tree with duplicate keys?

**Answer:**
Each node stores a count or list of values:
```java
class Node {
    int key;
    List<String> values = new ArrayList<>();
    Node left, right, parent;
    boolean isRed;
}
```

Or use a secondary discriminator (e.g., insertion timestamp) when comparing.

---

### Question 17
> Convert a BST to a Red-Black tree.

**Answer:**
1. In-order traverse BST → sorted array
2. Build perfectly balanced BST from sorted array (O(n))
3. Colour all nodes black

The resulting tree is a valid RB tree (property 5 holds with all black). Height = log₂ n.
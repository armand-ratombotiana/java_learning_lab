# Guide: Red-Black Tree

## Overview

A **Red-Black Tree** is a self-balancing binary search tree where each node has an extra bit — the "colour" (red or black). These colours, along with specific invariants, ensure the tree remains approximately balanced during insertions and deletions.

The Red-Black Tree guarantees O(log n) time for all operations (search, insert, delete) in the **worst case**, unlike treap or skip list which only guarantee it in expectation.

### Why Not Use AVL Tree?

| Aspect | Red-Black Tree | AVL Tree |
|--------|---------------|----------|
| Balance | Relaxed (≤ 2·log₂(n+1)) | Strict (≤ 1.44·log₂ n) |
| Insert rotations | O(1) amortised (≤ 2) | O(log n) |
| Delete rotations | O(1) amortised (≤ 3) | O(log n) |
| Search | O(log n) | O(log n) (faster — more balanced) |
| Use case | Insert-heavy workloads | Search-heavy workloads |

**Key Insight**: Red-Black trees have fewer rotations (max 2 per insert, 3 per delete), making them better for write-heavy workloads. AVL trees are more balanced, making them better for read-heavy workloads.

---

## ASCII Diagram

```
Properties:
1. Each node is either red or black
2. Root is always black
3. NULL leaves (NIL) are black
4. A red node cannot have red children (no red-red)
5. Every path from root to NIL has the same number of black nodes

Valid RB Tree:
          (10) B
         /     \
      (5) R   (15) R      ← red children of black root
      /   \    /   \
    (3) B (7)B(12)B (20)B ← black leaves
    /  \  / \  / \  / \
  NIL NIL NIL NIL NIL NIL NIL NIL
```

### Insertion Rotation Cases

After inserting a red node, check and fix using uncle colour:

**Case 1: Uncle is red** — recolor parent, uncle, grandparent. Move up to grandparent.
**Case 2: Uncle is black, node is "inner child"** — rotate parent to make it "outer child".
**Case 3: Uncle is black, node is "outer child"** — rotate grandparent, recolor.

---

## Source Code Walkthrough

The implementation follows the classic CLRS (Cormen) algorithm.

### Node Structure (lines ~5-10)

```java
class RBNode {
    int key;
    RBNode left, right, parent;
    boolean isRed; // true = red, false = black

    RBNode(int key) {
        this.key = key;
        this.isRed = true; // new nodes are always red
    }
}
```

### Properties (lines ~12-18)

```java
private final RBNode NIL; // sentinel leaf node, always black
private RBNode root;

public RedBlackTree() {
    NIL = new RBNode(0);
    NIL.isRed = false;
    root = NIL;
}
```

The NIL sentinel replaces null. This simplifies code — no null checks for children.

### Rotations (lines ~20-45)

```java
private void leftRotate(RBNode x) {
    RBNode y = x.right;
    x.right = y.left;
    if (y.left != NIL) y.left.parent = x;
    y.parent = x.parent;
    if (x.parent == NIL) root = y;
    else if (x == x.parent.left) x.parent.left = y;
    else x.parent.right = y;
    y.left = x;
    x.parent = y;
}

private void rightRotate(RBNode y) {
    RBNode x = y.left;
    y.left = x.right;
    if (x.right != NIL) x.right.parent = y;
    x.parent = y.parent;
    if (y.parent == NIL) root = x;
    else if (y == y.parent.right) y.parent.right = x;
    else y.parent.left = x;
    x.right = y;
    y.parent = x;
}
```

**Left rotate**: O(1) pointer changes. Right rotate is symmetric.

### Insert (lines ~47-75)

```java
public void insert(int key) {
    RBNode z = new RBNode(key);
    RBNode y = NIL;
    RBNode x = root;

    // Standard BST insert
    while (x != NIL) {
        y = x;
        if (z.key < x.key) x = x.left;
        else x = x.right;
    }
    z.parent = y;
    if (y == NIL) root = z;
    else if (z.key < y.key) y.left = z;
    else y.right = z;
    z.left = NIL;
    z.right = NIL;
    z.isRed = true;

    insertFixup(z);
}
```

### Insert Fixup (lines ~77-110)

```java
private void insertFixup(RBNode z) {
    while (z.parent.isRed) {
        if (z.parent == z.parent.parent.left) {
            RBNode y = z.parent.parent.right; // uncle
            if (y.isRed) {
                // Case 1: Uncle is red → recolor
                z.parent.isRed = false;
                y.isRed = false;
                z.parent.parent.isRed = true;
                z = z.parent.parent;
            } else {
                if (z == z.parent.right) {
                    // Case 2: Inner child → rotate parent
                    z = z.parent;
                    leftRotate(z);
                }
                // Case 3: Outer child → rotate grandparent
                z.parent.isRed = false;
                z.parent.parent.isRed = true;
                rightRotate(z.parent.parent);
            }
        } else { // symmetric (parent is right child)
            // mirror of above: left/right swapped
            // ... same pattern with left/right swapped
        }
    }
    root.isRed = false;
}
```

**Fixup logic**: While parent is red (violation of property 4):

| Case | Uncle Colour | Action |
|------|-------------|--------|
| 1 | Red | Recolor parent, uncle, grandparent; move up |
| 2 | Black, inner child | Rotate parent to make outer child |
| 3 | Black, outer child | Rotate grandparent + recolor |

### Delete (lines ~112-155)

More complex than insert. Involves finding successor/predecessor for nodes with two children, then calling `deleteFixup` with a "double black" concept.

```java
public void delete(int key) {
    RBNode z = search(root, key);
    if (z == NIL) return;

    RBNode y = z;
    RBNode x;
    boolean yOriginalRed = y.isRed;

    if (z.left == NIL) {
        x = z.right;
        transplant(z, z.right);
    } else if (z.right == NIL) {
        x = z.left;
        transplant(z, z.left);
    } else {
        y = minimum(z.right);
        yOriginalRed = y.isRed;
        x = y.right;
        if (y.parent == z) {
            x.parent = y;
        } else {
            transplant(y, y.right);
            y.right = z.right;
            y.right.parent = y;
        }
        transplant(z, y);
        y.left = z.left;
        y.left.parent = y;
        y.isRed = z.isRed;
    }

    if (!yOriginalRed) deleteFixup(x);
}
```

---

## Complexity Table

| Operation | Time | Rotations | Notes |
|-----------|------|-----------|-------|
| Search | O(log n) | 0 | Standard BST search |
| Insert | O(log n) | ≤ 2 | O(1) amortised rotations |
| Delete | O(log n) | ≤ 3 | O(1) amortised rotations |
| Minimum/Maximum | O(log n) | 0 | Right/leftmost node |
| Successor/Predecessor | O(log n) | 0 | Parent chain |
| Verify RB properties | O(n) | 0 | DFS check |

### Tree Height

**Guaranteed**: height ≤ 2·log₂(n + 1)
**Proof**: Merge red nodes into black parents → 2-3-4 tree of height h' = log₄(n+1). Original tree height ≤ 2·h' = 2·log₂(n+1) / log₂(4) = log₂(n+1).

---

## Comparison with Alternatives

| Feature | Red-Black Tree | AVL Tree | B-Tree | Skip List |
|---------|---------------|----------|--------|-----------|
| Search | O(log n) | O(log n) faster | O(log n) | O(log n) avg |
| Insert | O(log n) | O(log n) | O(log n) | O(log n) avg |
| Delete | O(log n) | O(log n) | O(log n) | O(log n) avg |
| Insert rotations | ≤ 2 | O(log n) | — | — |
| Memory | 3 ptrs + colour | 3 ptrs + height | variable | ~2n ptrs |
| Concurrent | Hard | Hard | Hard | Easy (CAS) |
| Worst case | O(log n) | O(log n) | O(log n) | O(n) |

**When NOT to use RB tree:**
- Read-heavy: AVL is faster (more balanced)
- Concurrent: Skip list is easier
- Need guaranteed O(log n): RB tree is fine, but so are others
- Very small n: Array-based structures are simpler

---

## Use Cases

### 1. Java TreeMap / TreeSet
**System**: JDK standard library
**Why RB tree**: Balanced O(log n) worst-case, simple enough for library implementation

### 2. Linux Completely Fair Scheduler (CFS)
**System**: Process scheduling
**Why RB tree**: Insert/delete tasks by virtual runtime. RB tree stores runqueue. O(log n) enqueue/dequeue.

### 3. Nginx Timer Management
**System**: Event loop timer wheel
**Why RB tree**: Timers sorted by expiration time. Insert new timer, find next expired — O(log n).

### 4. PostgreSQL B-Tree Index (variant)
**System**: Database indexing
**Why related**: B-tree is a generalisation of RB tree (more than 2 children per node). RB tree concepts inform B-tree implementation.

### 5. Consistent Hashing Rings
**System**: Distributed caching (Memcached, Dynamo)
**Implementation**: TreeMap<Long, Server> — RB tree maps hash → server, ceilingKey finds server for a key.

---

## Common Pitfalls

### 1. Forgetting NIL Colour
NIL must be black. A null left/right child is implicitly a black leaf. Using null references instead of a NIL sentinel requires null checks everywhere.

### 2. Wrong Uncle Check
Uncle is the sibling of the parent, NOT the sibling of the node itself: `parent.parent.right` (or `.left`).

### 3. Root Always Black
After insertFixup, set `root.isRed = false`. This is required by property 2.

### 4. Red-Red Violation After Insert
New nodes are red. If parent is also red, we have a violation. Fixup handles this by uncle-case logic.

### 5. Delete's Double-Black Problem
When a black node is deleted (or removed), its child inherits an "extra blackness". Fixup resolves this by rotating and recolouring.

---

## Advanced Variants

### Left-Leaning Red-Black Tree (LLRB)
Simplified variant by Robert Sedgewick. Enforces that red nodes are always left children. Fewer cases in insert fixup (only 3 vs 6). Used in some teaching materials.

### AA Tree
Red-Black variant that allows only right red nodes. Simpler code, fewer cases. Used in C++ standard library for some implementations.

### B-Tree (2-3-4 Tree)
A Red-Black tree is isometric to a 2-3-4 (B-Tree of order 4). Each black node with its optional red children represents a 2-3-4 tree node. This correspondence helps understand RB tree properties.

---

## Testing the Implementation

```java
RedBlackTree rb = new RedBlackTree();
rb.insert(10);
rb.insert(5);
rb.insert(15);
rb.insert(3);
rb.insert(7);
rb.insert(12);
rb.insert(18);

assert rb.search(10) == true;
assert rb.search(8) == false;
assert rb.verifyProperties() == true;

rb.delete(10);
assert rb.search(10) == false;
assert rb.verifyProperties() == true;
```

### Property Verification
```java
boolean verifyProperties() {
    return !root.isRed                          // property 2
        && !hasRedRedViolation(root)            // property 4
        && hasEqualBlackHeight(root);           // property 5
}
```

---

## Key Interview Takeaways

1. **RB tree = guaranteed O(log n) worst case**. This is the most important property.

2. **Red children have black parent** (no red-red). This is the property that gets violated and needs fixing.

3. **Insert fixup has 3 cases** based on uncle colour. Maximum 2 rotations.

4. **Delete fixup is harder** — involves "double black" and more cases. Know that it exists and is O(log n).

5. **Java TreeMap** is a RB tree. Know its API: `ceilingKey`, `floorKey`, `subMap`, `headMap`, `tailMap`.

6. **LLRB** is a simplified variant. Mention it to show depth of knowledge.

7. **Rotations are O(1)** — pointer reassignments, not value movements.
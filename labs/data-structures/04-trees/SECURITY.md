# Security Considerations for Trees

## Stack Overflow via Deep Trees

```java
// Attacker-controlled data inserted in sorted order
// Creates degenerate BST → O(n) height
TreeNode root = null;
for (int value : attackerControlledSortedValues) {
    root = insert(root, value);  // degenerates to linked list
}
// Recursive traversal now causes StackOverflowError
```

Mitigation: use self-balancing trees (AVL, Red-Black) or iterative algorithms.

## Denial of Service

- **Unbounded tree growth**: attacker inserts many nodes, exhausting heap memory
- **Hash collision (HashMap treeification)** : attacker crafts colliding keys, forcing bucket to treeify (Java 8+), but still O(log n) per operation — the attack is mitigated compared to Java 7 (O(n) per bucket)

## Integer Overflow in BST

```java
if (node.val <= min) return false;  // min = Long.MIN_VALUE
if (node.val >= max) return false;  // max = Long.MAX_VALUE

// Without defensive widening, using Integer.MIN_VALUE/MAX_VALUE fails
// when node.val == Integer.MAX_VALUE and max == Integer.MAX_VALUE
```

## Serialization Attacks

If you implement serialization for trees:
- Deserializing a crafted tree can cause **deep recursion** → stack overflow
- Deserializing a billion-node tree can cause **OOM**
- Always validate input size before tree construction

## Timing Attacks

Tree operations reveal information through timing:
- BST `contains` takes longer to return `false` if the key is close to the root (early termination)
- In a balanced tree, all paths take approximately the same time (mitigated)
- In a degenerate tree, timing varies by search path length

## Race Conditions

Trees are **not thread-safe** by default. Concurrent modification can:
- Create cycles (one node's child points to another's ancestor)
- Lose entire subtrees
- Violate BST invariants
- Produce `ConcurrentModificationException` in iterators

Use explicit synchronization or `java.util.concurrent` structures for concurrent access.

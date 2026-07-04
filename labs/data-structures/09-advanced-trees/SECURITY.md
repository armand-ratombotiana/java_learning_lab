# Security Considerations for Advanced Trees

## Denial of Service

### BST Degeneration Attack

```java
// Attacker inserts sorted data → degenerate BST
// O(n) per operation → O(n²) total
for (int i = 0; i < 100000; i++) {
    bst.insert(i);  // degenerates to linked list
}
```

Mitigation: use self-balancing trees (AVL, Red-Black). Java's TreeMap already does this.

### B-Tree Size Attack

Loading a B-tree with many insertions causes node splits. Controlled by order parameter; larger order = more memory per node but fewer splits.

### Segment Tree Memory Exhaustion

```java
int[] arr = new int[Integer.MAX_VALUE / 4];  // huge array
SegmentTree st = new SegmentTree(arr);        // 4x memory → OOM
```

## TreeMap Security

- `TreeMap` uses `Comparator` for ordering. A malicious Comparator can:
  - Throw exceptions, causing DoS
  - Return inconsistent results, corrupting the tree
  - Leak information through timing

## Concurrent Modification

TreeMap is **not thread-safe**. Concurrent modification can:
- Create cycles in the tree
- Lose nodes
- Cause infinite iteration
- Violate Red-Black invariants

Use `Collections.synchronizedSortedMap()` or explicit synchronization.

## Null Key Injection

```java
TreeMap<String, String> map = new TreeMap<>();
map.put(null, "value");  // NullPointerException!
// TreeMap does not allow null keys (by default)
```

## Information Leakage via NavigableMap

NavigableMap methods (`lowerKey`, `higherKey`) reveal key proximity. If keys are sensitive (e.g., user IDs), these methods can be used to enumerate keys.

## Secure Practices

- Always use balanced trees (AVL or Red-Black) for untrusted data
- Validate Comparator consistency before use
- Avoid TreeMap with user-controlled Comparators
- Use synchronized wrappers for concurrent access
- Limit input size to prevent memory exhaustion

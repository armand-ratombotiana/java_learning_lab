# Internals: Tree Representation in Java

## Node Representation

```java
// Generic binary tree node
class TreeNode<E> {
    E val;
    TreeNode<E> left;
    TreeNode<E> right;

    TreeNode(E x) { val = x; }
}
```

In HotSpot, a `TreeNode` object:
- Header: 12 bytes (compressed OOPs)
- `val` reference: 4 bytes
- `left` reference: 4 bytes
- `right` reference: 4 bytes
- Padding: 4 bytes
- **Total**: ~28 bytes per node

## Memory Layout in Heap

```
Heap (before compaction):
┌──────────┐    ┌──────────┐    ┌──────────┐
│ Root(10) │    │ Left(5)  │    │ Right(15)│
│ left → ──┼────→│ val=5   │    │ val=15   │
│ right→ ──┼──┐  │ left=null│    │ left=null│
│ val=10   │  │  │ right=null│   │ right=null│
└──────────┘  │  └──────────┘    └──────────┘
              └──────────────────────────────────→
```

Due to separate allocations, nodes are scattered across heap memory — trees have poor cache locality compared to arrays.

## Array Representation (Heap-like)

A complete binary tree can be stored in an array without pointers:

```
Index:     0    1    2    3    4    5    6
Values:  [ 50,  30,  20,  15,  10,   8,   5 ]
            ↑    ↑         ↑         ↑
          root left(1)  left(3)   left(1).right(4)

For node at index i:
  left child: 2i + 1
  right child: 2i + 2
  parent: (i - 1) / 2
```

This is used for **binary heaps** and is the most cache-friendly representation.

## Java's TreeMap Internals

`java.util.TreeMap<K,V>` uses a **Red-Black tree**:

```java
static final class Entry<K,V> implements Map.Entry<K,V> {
    K key;
    V value;
    Entry<K,V> left;
    Entry<K,V> right;
    Entry<K,V> parent;
    boolean color = BLACK;  // RED or BLACK
}
```

NavigableMap methods (`lowerKey`, `floorKey`, `ceilingKey`, `higherKey`) use tree traversal to find bounds in O(log n).

## Java 8 HashMap Treeification

When a HashMap bucket chain exceeds `TREEIFY_THRESHOLD = 8`, the linked list is converted to a `TreeNode` (Red-Black tree):

```java
static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
    TreeNode<K,V> parent;
    TreeNode<K,V> left;
    TreeNode<K,V> right;
    TreeNode<K,V> prev;    // needed to unlink next upon deletion
    boolean red;
}
```

This prevents O(n) lookup for heavily colliding hash keys (HashDoS mitigation).

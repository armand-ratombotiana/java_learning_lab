# Performance of Linked Lists

## Time Complexity

| Operation | Singly Linked | Doubly Linked | ArrayList |
|-----------|--------------|---------------|-----------|
| Access by index | O(n) | O(n) | O(1) |
| Search | O(n) | O(n) | O(n) |
| Insert at head | O(1) | O(1) | O(n) |
| Insert at tail | O(1)* | O(1) | O(1) |
| Insert at middle | O(n) | O(n) | O(n) |
| Delete at head | O(1) | O(1) | O(n) |
| Delete at tail | O(n) | O(1) | O(1) |
| Delete at middle | O(n) | O(1)** | O(n) |
| Get size | O(1) | O(1) | O(1) |

\*With tail pointer
\*\*If node reference is already known; O(n) to find the node

## Space Complexity

### Memory per Element (HotSpot 64-bit, compressed OOPs)

| Component | Singly Node | Doubly Node | ArrayList slot |
|-----------|------------|-------------|---------------|
| Object header | 12 | 12 | 0 |
| `data` reference | 4 | 4 | 4 |
| `next` reference | 4 | 4 | 0 |
| `prev` reference | 0 | 4 | 0 |
| Padding | 4 | 4 | 0 |
| **Total per element** | **24 bytes** | **28 bytes** | **4 bytes** |

For 1 million `Integer` elements:
- LinkedList: ~28 MB for nodes + ~16 MB for Integer objects = ~44 MB
- ArrayList: ~4 MB for references + ~16 MB for Integer objects = ~20 MB

## Cache Behavior

Linked lists have **poor cache locality** because nodes are allocated independently on the heap and may be scattered across memory. Traversing a linked list causes a cache miss at nearly every node.

| Metric | ArrayList | LinkedList |
|--------|-----------|------------|
| Sequential read throughput | ~10 GB/s | ~0.5-1 GB/s |
| Cache misses per element | ~0 (prefetched) | ~1 |
| Random access | O(1), 1 cache miss | O(n), n cache misses |

## When to Use LinkedList

- Frequent insert/delete at both ends (queue/deque operations)
- You need O(1) insertion at arbitrary positions (with existing node reference)
- Memory overhead is acceptable
- Traversal is rare or only sequential

## When to Avoid

- Random access is common (use ArrayList)
- Memory is tight
- Data is primitive-heavy (use specialized collections like `IntArrayList`)
- You need cache-efficient traversal

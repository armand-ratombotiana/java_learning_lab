# Theory: Linked Lists

## Structure

A linked list is a linear collection of nodes where each node contains data and a reference (pointer) to the next node. Unlike arrays, elements are **not stored contiguously** in memory.

### Singly Linked List

```java
class Node<E> {
    E data;
    Node<E> next;
}
```

Each node points only forward. Traversal is one-directional.

### Doubly Linked List

```java
class Node<E> {
    E data;
    Node<E> prev;
    Node<E> next;
}
```

Each node points both forward and backward, enabling bidirectional traversal and O(1) deletion at known positions.

### Circular Linked List

The tail's `next` points to the head (and for doubly circular, head's `prev` points to tail). Useful for round-robin scheduling and cyclic buffers.

## Sentinel Nodes (Dummy Nodes)

Special nodes that simplify edge-case handling. A sentinel head node has `data = null` and `next` pointing to the real first node. This eliminates null checks for empty-list operations.

```java
// Without sentinel — special case for empty list
if (head == null) { head = newNode; }

// With sentinel — no special case
sentinel.next = newNode;
```

## Time Complexity

| Operation | Singly Linked | Doubly Linked | Array |
|-----------|--------------|---------------|-------|
| Access by index | O(n) | O(n) | O(1) |
| Insert at head | O(1) | O(1) | O(n) |
| Insert at tail | O(n)* | O(1)** | O(1) |
| Insert at known node | O(1) | O(1) | N/A |
| Delete at head | O(1) | O(1) | O(n) |
| Delete at known node | O(n)*** | O(1) | N/A |
| Search | O(n) | O(n) | O(n) |

\*With tail pointer: O(1) for singly linked
\*\*With tail pointer
\*\*\*Need previous node — require O(n) to find it

## Memory Overhead

Each node stores data + 1 or 2 references. A linked list of n integers:

- **Singly**: n × (4 bytes int + 4/8 bytes reference + object header ~16 bytes) ≈ 24–32n bytes
- **ArrayList**: 4n bytes + ~20 bytes array overhead

Linked lists use 6–8× more memory than arrays for primitive data.

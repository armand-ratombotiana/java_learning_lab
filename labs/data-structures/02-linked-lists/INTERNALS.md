# Internals: LinkedList in Java

## java.util.LinkedList<E>

**Package**: `java.util`
**Extends**: `AbstractSequentialList<E>`
**Implements**: `List<E>`, `Deque<E>`, `Cloneable`, `Serializable`

### Internal Node Class

```java
private static class Node<E> {
    E item;
    Node<E> next;
    Node<E> prev;

    Node(Node<E> prev, E element, Node<E> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }
}
```

### Core Fields

```java
transient int size = 0;
transient Node<E> first;  // header
transient Node<E> last;   // trailer
```

`transient` because serialization is custom (writes elements, not nodes).

### Key Implementation Details

**linkFirst(E)**: insert at head
```java
private void linkFirst(E e) {
    final Node<E> f = first;
    final Node<E> newNode = new Node<>(null, e, f);
    first = newNode;
    if (f == null) last = newNode;
    else f.prev = newNode;
    size++;
    modCount++;
}
```

**linkLast(E)**: insert at tail (mirror of linkFirst)

**linkBefore(E, Node)**: insert before a given node

**unlink(Node)**: remove a node — connects prev.next to node.next and vice versa

### Performance Characteristics

| Operation | Method | Time |
|-----------|--------|------|
| Add first | `addFirst()` | O(1) |
| Add last | `addLast()` | O(1) |
| Get first | `getFirst()` | O(1) |
| Get last | `getLast()` | O(1) |
| Get at index | `get(i)` | O(n) |
| Remove at index | `remove(i)` | O(n) |
| Contains | `contains(o)` | O(n) |

### Node Search Optimization

LinkedList uses a **binary-style search** for indexed access:

```java
Node<E> node(int index) {
    if (index < (size >> 1)) {
        Node<E> x = first;
        for (int i = 0; i < index; i++) x = x.next;
        return x;
    } else {
        Node<E> x = last;
        for (int i = size - 1; i > index; i--) x = x.prev;
        return x;
    }
}
```

If the index is in the first half, traverse from head; otherwise, traverse from tail. This halves the average traversal cost but still O(n).

### Iterator Behavior

LinkedList's `ListIterator` tracks a "last returned" node. On `remove()`, it uses the internal `unlink()` method. It checks `modCount` against `expectedModCount` for fail-fast behavior.

### Memory Usage

Each element (Node object) in HotSpot:
- Object header: 12–16 bytes
- `item` reference: 4–8 bytes
- `next` reference: 4–8 bytes
- `prev` reference: 4–8 bytes
- **Total per element**: ~40–48 bytes + the element itself

Compare with ArrayList: ~4 bytes per element + array overhead.

# Internals: Java TreeMap (Red-Black Tree)

## TreeMap Structure

```java
public class TreeMap<K,V> extends AbstractMap<K,V>
    implements NavigableMap<K,V>, Cloneable, java.io.Serializable {

    private final Comparator<? super K> comparator;
    private transient Entry<K,V> root;
    private transient int size = 0;

    // Red-Black node
    static final class Entry<K,V> implements Map.Entry<K,V> {
        K key;
        V value;
        Entry<K,V> left;
        Entry<K,V> right;
        Entry<K,V> parent;
        boolean color = BLACK;
    }
}
```

### Color Constants

```java
private static final boolean RED = false;
private static final boolean BLACK = true;
```

### Key Methods

**put(K key, V value)**:
1. Standard BST insertion
2. Fix Red-Black violations (up to 2 rotations)

**deleteEntry(Entry<K,V> p)**:
1. Standard BST deletion
2. If deleted node was black, fix violations

### Fix After Insertion

```java
private void fixAfterInsertion(Entry<K,V> x) {
    x.color = RED;
    while (x != null && x != root && x.parent.color == RED) {
        if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
            Entry<K,V> y = rightOf(parentOf(parentOf(x)));
            if (colorOf(y) == RED) {          // Case 1: uncle is red
                setColor(parentOf(x), BLACK);
                setColor(y, BLACK);
                setColor(parentOf(parentOf(x)), RED);
                x = parentOf(parentOf(x));
            } else {
                if (x == rightOf(parentOf(x))) { // Case 2: x is right child
                    x = parentOf(x);
                    rotateLeft(x);
                }                                 // Case 3: x is left child
                setColor(parentOf(x), BLACK);
                setColor(parentOf(parentOf(x)), RED);
                rotateRight(parentOf(parentOf(x)));
            }
        } else {
            // symmetric (mirror) cases
        }
    }
    root.color = BLACK;
}
```

### Performance Characteristics

- **get**: O(log n) — standard BST search
- **put**: O(log n) — BST insert + fix (amortized O(1) rotations)
- **remove**: O(log n) — BST delete + fix
- **Iteration**: O(n) — inorder traversal
- **subMap, headMap, tailMap**: O(log n) for view creation, O(log n + m) for iteration

### NavigableMap Methods

- `lowerKey(k)`: greatest key < k
- `floorKey(k)`: greatest key ≤ k
- `ceilingKey(k)`: least key ≥ k
- `higherKey(k)`: least key > k
- All O(log n)

### Comparator vs Comparable

TreeMap requires either a `Comparator` or keys implementing `Comparable`. Unlike HashMap, null keys are not allowed (by default) because `compareTo(null)` throws NPE.

### SubMap Views

```java
// Returns a view, not a copy — backed by the original map
NavigableMap<K,V> subMap = treeMap.subMap(low, true, high, true);
// Modifications to subMap affect treeMap and vice versa
```

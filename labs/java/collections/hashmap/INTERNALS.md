# HashMap Internals & Memory Layout

## 🔬 Deep Dive into `java.util.HashMap`

Java's `HashMap` is a highly optimized, complex data structure. It is not just a simple array of linked lists. Let's dissect its internal architecture.

### 1. The Underlying Array (The Table)
Internally, `HashMap` maintains an array of `Node<K,V>` objects called the `table`.
```java
transient Node<K,V>[] table;
```
The size of this table is **always a power of two**. This is a critical optimization that allows the map to use bitwise AND (`&`) instead of modulo (`%`) to calculate the bucket index:
`index = (n - 1) & hash` (where `n` is the length of the table).

### 2. The Node Structure
Each element in the map is wrapped in a `Node` (which implements `Map.Entry`).
```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next; // Pointer to the next node in the chain
}
```

### 3. Treeification (The Java 8 Optimization)
Prior to Java 8, severe hash collisions could degrade `HashMap` performance to O(n) because buckets became long linked lists. 

Java 8 introduced **Treeification**. If a bucket's linked list grows beyond a certain threshold (`TREEIFY_THRESHOLD = 8`), and the total table capacity is at least `MIN_TREEIFY_CAPACITY = 64`, the linked list is converted into a **Red-Black Tree**.

This improves the worst-case lookup time from O(n) to **O(log n)**.
```java
static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
    TreeNode<K,V> parent;
    TreeNode<K,V> left;
    TreeNode<K,V> right;
    TreeNode<K,V> prev;
    boolean red;
}
```

### 4. The Hash Function Spread (`hash()`)
Java doesn't just use the key's `hashCode()` directly. It applies a supplemental hash function to defend against poor quality hash codes.
```java
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```
This XORs the higher 16 bits of the hash code with the lower 16 bits. This ensures that even if the table is small (meaning only the lower bits are used for the index calculation), the higher bits still influence the final bucket index, reducing collisions.

## 📊 Memory Layout Implications
A `HashMap` has significant memory overhead compared to a primitive array:
1. The `table` array object itself.
2. The `Node` object for every entry (contains 4 references/primitives: hash, key, value, next).
3. The actual Key and Value objects.

For memory-constrained environments, alternatives like primitive maps (e.g., Eclipse Collections `IntIntHashMap`) or flat arrays might be necessary to avoid the object header and reference overhead of millions of `Node` objects.
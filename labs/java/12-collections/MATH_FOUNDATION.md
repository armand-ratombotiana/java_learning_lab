# Collections — Mathematical Foundation

## Big-O Notation for Collections

### List Performance

| Operation | ArrayList | LinkedList |
|-----------|-----------|------------|
| get(i) | O(1) | O(n) |
| add(e) | O(1)* | O(1) |
| add(i, e) | O(n) | O(n) |
| remove(i) | O(n) | O(n) |
| contains(e) | O(n) | O(n) |
| iterator.next() | O(1) | O(1) |
| iterator.remove() | O(n) | O(1) |

*Amortized; O(n) when resize occurs.

### Set Performance

| Operation | HashSet | LinkedHashSet | TreeSet |
|-----------|---------|---------------|---------|
| add(e) | O(1)* | O(1)* | O(log n) |
| contains(e) | O(1)* | O(1)* | O(log n) |
| remove(e) | O(1)* | O(1)* | O(log n) |
| iteration | O(n) | O(n) | O(n) |

*Assuming good hash distribution.

### Map Performance

| Operation | HashMap | LinkedHashMap | TreeMap |
|-----------|---------|---------------|---------|
| put(k,v) | O(1)* | O(1)* | O(log n) |
| get(k) | O(1)* | O(1)* | O(log n) |
| containsKey(k) | O(1)* | O(1)* | O(log n) |
| iteration | O(n) | O(n) | O(n) |

## Hash Function Quality

Hash distribution determines HashMap performance:

- **Perfect hash**: All O(1) — every key goes to its own bucket
- **Worst case**: All O(n) — every key goes to the same bucket
- **Expected case**: O(1) with uniform distribution

Effective hashCode() formula (Effective Java):

```java
int result = 1;
result = 31 * result + field1.hashCode();
result = 31 * result + field2.hashCode();
// 31 used because odd prime, multiplication optimizable to shift-subtract
```

## Load Factor Math

HashMap load factor α = n / N (entries / capacity):
- Average bucket length: α
- Expected search cost: 1 + α/2
- Higher α = less space, more collisions
- Lower α = more space, fewer collisions
- Default 0.75 balances space/time

## Red-Black Tree Properties

TreeSet/TreeMap use red-black trees with these invariants:
1. Every node is red or black
2. Root is black
3. Every leaf (null) is black
4. Red nodes have black children
5. All paths from a node to its leaves have the same number of black nodes

These guarantee O(log n) height — at most 2× the minimum possible.

## Complexity Analysis for Common Patterns

```java
// Iterating HashMap entries: O(n)
for (Map.Entry<K, V> entry : map.entrySet()) { ... }

// Nested loop on same collection: O(n²)
for (String a : list) {
    if (list.contains(a)) ...  // O(n) per iteration
}

// Better with Set: O(n)
Set<String> set = new HashSet<>(list);
for (String a : list) {
    if (set.contains(a)) ...  // O(1) per iteration
}
```

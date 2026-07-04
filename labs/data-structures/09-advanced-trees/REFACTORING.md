# Refactoring Advanced Trees

## Use TreeMap Instead of Manual BST

```java
// Before — manual BST
class BstMap<K, V> {
    private Node root;
    // ... custom implementation
}

// After
TreeMap<K, V> map = new TreeMap<>();
```

## Use NavigableMap Methods

```java
// Before — manual iteration
for (K key : map.keySet()) {
    if (key.compareTo(low) >= 0 && key.compareTo(high) <= 0) {
        // process
    }
}

// After
NavigableMap<K, V> subMap = map.subMap(low, true, high, true);
subMap.forEach((k, v) -> process(k, v));
```

## Replace TreeMap with EnumMap for Enums

```java
// Before
Map<DayOfWeek, String> map = new TreeMap<>();

// After (faster if enums are the key)
Map<DayOfWeek, String> map = new EnumMap<>(DayOfWeek.class);
```

## Extract Fenwick/Segment Tree Operations

```java
// Before — inline fenwick operations
int[] bit = new int[n + 1];
for (int i = 1; i <= n; i++) {
    int idx = i;
    while (idx <= n) {
        bit[idx] += val;
        idx += idx & -idx;
    }
}

// After
FenwickTree ft = new FenwickTree(n);
ft.update(i, val);
```

## Use Arrays Instead of Objects for Segment Trees

```java
// Before — object-based
class SegTreeNode {
    int sum;
    SegTreeNode left, right;
}

// After — array-based (faster, less memory)
int[] tree = new int[4 * n];
int[] lazy = new int[4 * n];
```

## Comparator vs Comparable

```java
// Before — key implements Comparable (required by TreeMap)
class Key implements Comparable<Key> { ... }

// After — provide Comparator separately
TreeMap<Key, Value> map = new TreeMap<>(
    Comparator.comparing(Key::getId)
);
```

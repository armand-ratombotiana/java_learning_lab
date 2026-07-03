# Deep Dive: Sorted Collections

## 1. Introduction to Sorted Collections
While `HashSet` and `HashMap` provide $O(1)$ access times, they completely lose the ordering of elements. When you need to maintain elements in a specific order (e.g., alphabetical, chronological, or numerical) and perform range queries (e.g., "get all dates between X and Y"), you must use sorted collections: `TreeSet` and `TreeMap`.

## 2. Internals: The Red-Black Tree
`TreeSet` is actually just a wrapper around a `TreeMap` (where the values are dummy objects). `TreeMap` is implemented as a **Red-Black Tree**.

### What is a Red-Black Tree?
It is a self-balancing binary search tree.
*   **Binary Search Tree (BST)**: Every left child is smaller than its parent, and every right child is larger.
*   **Self-Balancing**: If you insert elements in sorted order (1, 2, 3, 4, 5) into a naive BST, it becomes a linked list, degrading performance to $O(N)$. A Red-Black tree applies specific coloring rules and performs "rotations" during insertion and deletion to ensure the tree remains roughly balanced.
*   **Performance**: Because it is balanced, `get`, `put`, `remove`, and `containsKey` are guaranteed to run in **$O(\log N)$** time.

## 3. The `Comparable` and `Comparator` Interfaces
For a `TreeMap` or `TreeSet` to sort its elements, it must know how to compare them.

1.  **Natural Ordering (`Comparable`)**: The class of the elements implements `Comparable<T>` and overrides `compareTo(T o)`.
    *   *Example*: `String`, `Integer`, `LocalDate` all implement `Comparable`.
2.  **Custom Ordering (`Comparator`)**: You pass a `Comparator<T>` to the constructor of the `TreeSet` or `TreeMap`. This overrides the natural ordering.
    *   *Example*: `new TreeSet<>(Comparator.comparing(User::getLastName).thenComparing(User::getFirstName))`

## 4. The `NavigableMap` and `NavigableSet` Interfaces
`TreeMap` and `TreeSet` implement `NavigableMap` and `NavigableSet` (introduced in Java 6). These interfaces provide powerful methods for navigation and range queries that are impossible with Hash-based collections.

### Key Methods:
*   `lowerKey(K key)`: Greatest key strictly less than the given key.
*   `floorKey(K key)`: Greatest key less than or equal to the given key.
*   `ceilingKey(K key)`: Least key greater than or equal to the given key.
*   `higherKey(K key)`: Least key strictly greater than the given key.
*   `firstKey()` / `lastKey()`: The absolute minimum / maximum keys in the map.

### Polling Methods:
*   `pollFirstEntry()` / `pollLastEntry()`: Removes and returns the lowest/highest entry. This is incredibly useful for implementing priority queues or scheduling systems.

## 5. Range Operations (SubMaps and SubSets)
The most powerful feature of sorted collections is the ability to create views of a specific range of data.

```java
TreeMap<LocalDate, String> schedule = new TreeMap<>();
// ... populate schedule ...

// Get all events in the year 2026
LocalDate start = LocalDate.of(2026, 1, 1);
LocalDate end = LocalDate.of(2027, 1, 1);

// subMap(fromKey, fromInclusive, toKey, toInclusive)
NavigableMap<LocalDate, String> events2026 = schedule.subMap(start, true, end, false);
```
*   **Crucial Concept**: `subMap`, `headMap`, and `tailMap` return **views**. They do not copy the data. If you add or remove an element from the `subMap` (provided it falls within the range boundaries), it modifies the original `TreeMap`.
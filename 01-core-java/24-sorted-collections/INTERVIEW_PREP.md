# Interview Preparation: Sorted Collections

This document covers advanced questions related to Red-Black Trees, Comparators, and the `NavigableMap` interface.

## Q1: How does a `TreeMap` handle collisions, and how does this differ from a `HashMap`?
**Answer:**
A `HashMap` handles collisions (two different keys having the same hash code) by placing them in the same bucket and chaining them together using a Linked List (or a Red-Black tree if the bin gets too large).
A `TreeMap` **does not have collisions** in the same sense because it doesn't use hash codes. It uses a `Comparator` (or `compareTo`). If `compareTo` returns `0`, the `TreeMap` considers the keys to be exactly the same (duplicates). It will simply overwrite the old value with the new value. It will *not* store both keys.

## Q2: What is a Red-Black Tree, and why is it used instead of a standard Binary Search Tree (BST) for `TreeMap`?
**Answer:**
A standard BST can become unbalanced. If you insert sorted data (e.g., 1, 2, 3, 4, 5), the BST degrades into a Linked List, and search performance drops from $O(\log N)$ to $O(N)$.
A Red-Black Tree is a **self-balancing** binary search tree. It enforces strict rules about node coloring (red or black) and path lengths. If an insertion or deletion violates these rules, the tree performs "rotations" to re-balance itself. This guarantees that the tree's height remains logarithmic, ensuring $O(\log N)$ performance for `get`, `put`, and `remove` operations, regardless of insertion order.

## Q3: Explain the difference between `headMap(K toKey)` and `subMap(K fromKey, K toKey)`. What happens if you modify the returned map?
**Answer:**
*   `headMap(toKey)` returns a view of the map containing all keys strictly less than `toKey`.
*   `subMap(fromKey, toKey)` returns a view of the map containing keys ranging from `fromKey` (inclusive) to `toKey` (exclusive).
Because these methods return **views** (not copies), any modifications made to the returned map are written directly through to the original backing `TreeMap`. Conversely, changes to the original map will be visible in the sub-map (provided the changes fall within the sub-map's key range). If you try to insert a key into a sub-map that falls outside its defined range, it throws an `IllegalArgumentException`.

## Q4: Why must objects used as keys in a `TreeMap` or elements in a `TreeSet` be immutable (or at least have immutable comparison fields)?
**Answer:**
When an object is inserted into a Red-Black tree, it is routed left or right down the tree based on its comparison to existing nodes, until it finds its correct sorted position. 
If you later mutate a field on that object that changes its `compareTo` result, the object is now sitting in the *wrong branch* of the tree. The tree will not automatically re-sort itself. Subsequent calls to `contains()`, `remove()`, or `get()` will traverse the tree looking for the new value, but they will look down the wrong branch and fail to find the object, effectively corrupting the entire data structure.

## Q5: How would you implement a Priority Queue using the `NavigableSet` interface?
**Answer:**
While Java has a dedicated `PriorityQueue` class, you can use a `TreeSet` (which implements `NavigableSet`) to achieve similar behavior with added features.
You insert tasks into the `TreeSet` (they must implement `Comparable` based on priority). To process the highest priority task, you call `set.pollFirst()` (or `pollLast()`, depending on your sort order). This method atomicly removes and returns the first element in the sorted set. The advantage over `PriorityQueue` is that `TreeSet` allows you to iterate over all elements in sorted order and prevents duplicate tasks.
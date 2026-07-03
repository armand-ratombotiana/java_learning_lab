# Module 23: Data Structures & Algorithms - Edge Cases & Pitfalls

---

## Pitfall 1: Unbalanced Binary Search Trees

### ❌ Wrong
Inserting elements into a Binary Search Tree (BST) in sorted order (e.g., 1, 2, 3, 4, 5). This degrades the tree into a Linked List, changing time complexity from O(log n) to O(n).

### ✅ Correct
Use self-balancing trees like AVL or Red-Black Trees (which Java uses internally for `TreeMap` and `TreeSet`) to guarantee O(log n) performance regardless of insertion order.

---

## Pitfall 2: Bad Hash Functions

### ❌ Wrong
Implementing a custom `hashCode()` method that returns the same constant or has very poor distribution. This causes all elements to fall into the same bucket, turning a Hash Table's O(1) lookup time into an O(n) Linked List search.

### ✅ Correct
Ensure `hashCode()` is uniformly distributed. Rely on Java's `Objects.hash()` or IDE-generated `equals()` and `hashCode()` implementations to minimize collisions.

---

## Pitfall 3: Ignoring Space Complexity

### ❌ Wrong
Choosing an algorithm strictly based on Time Complexity while ignoring Space Complexity. For example, using Merge Sort in an embedded system with extremely tight memory constraints, where allocating an extra O(n) array crashes the system.

### ✅ Correct
Consider both Time and Space Complexity. If memory is tight, an in-place sort like Quick Sort or Heap Sort (O(1) extra space) might be preferred over Merge Sort.
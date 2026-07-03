# Collections — Flashcards

## Card 1
**Q:** What are the four main collection interfaces?
**A:** List, Set, Queue, Map. (Collection is the superinterface of List, Set, Queue.)

## Card 2
**Q:** What's the difference between ArrayList and LinkedList?
**A:** ArrayList uses a dynamic array (O(1) get, O(n) insert middle). LinkedList uses doubly-linked nodes (O(n) get, O(1) add at ends).

## Card 3
**Q:** How does HashSet ensure uniqueness?
**A:** Uses HashMap internally. Elements stored as keys, a dummy object as value. Uses hashCode() + equals().

## Card 4
**Q:** What is the load factor?
**A:** The threshold (default 0.75) that triggers HashMap resize when exceeded. Trade-off between space and time.

## Card 5
**Q:** What is a TreeSet?
**A:** A Set backed by a Red-Black tree. Elements stored in sorted order. O(log n) operations.

## Card 6
**Q:** Why use ArrayDeque over Stack?
**A:** ArrayDeque is faster (no synchronization), more complete API (Deque), and doesn't extend Vector (poor inheritance).

## Card 7
**Q:** What is ConcurrentModificationException?
**A:** Thrown when a collection is structurally modified while being iterated (except via Iterator.remove()).

## Card 8
**Q:** What does Map.merge() do?
**A:** Atomically inserts or updates: if key absent, put value; if present, apply remapping function.

## Card 9
**Q:** What is a WeakHashMap?
**A:** A Map where keys are weak references. Entries are automatically removed when the key is no longer strongly referenced elsewhere.

## Card 10
**Q:** When to use CopyOnWriteArrayList?
**A:** For read-heavy, write-infrequent scenarios (e.g., listener lists). Reads are O(1), writes copy entire array.

## Card 11
**Q:** What is the default capacity of ArrayList?
**A:** 10. Creates internal Object[10] on first add.

## Card 12
**Q:** How does LinkedHashMap maintain order?
**A:** Maintains a doubly-linked list running through all entries. Can be insertion-order or access-order.

## Card 13
**Q:** What's the difference between poll() and remove() on Queue?
**A:** Both remove head. poll() returns null if empty; remove() throws NoSuchElementException.

## Card 14
**Q:** What is the performance of HashMap.get() in the worst case?
**A:** O(n) — when all keys hash to the same bucket. Mitigated by treeification (Java 8+).

## Card 15
**Q:** How do you create an immutable list in Java 9+?
**A:** `List.of("a", "b", "c")` or `List.copyOf(existingList)`.

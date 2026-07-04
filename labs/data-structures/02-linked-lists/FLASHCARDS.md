# Flashcards: Linked Lists

---

**Q**: What is the time complexity of accessing an element by index in a linked list?
**A**: O(n) — must traverse from head to reach the position.

---

**Q**: What is the time complexity of inserting at the head of a linked list?
**A**: O(1) — create node, point to old head, update head reference.

---

**Q**: What is Floyd's algorithm used for?
**A**: Cycle detection — slow and fast pointers; if they meet, a cycle exists.

---

**Q**: What does a sentinel node simplify?
**A**: Edge cases — no special handling for empty list operations.

---

**Q**: How does Java's LinkedList optimize node search by index?
**A**: It starts from head if index < size/2, from tail otherwise.

---

**Q**: What is the advantage of a doubly linked list over singly?
**A**: O(1) deletion at a known node and bidirectional traversal.

---

**Q**: Why do linked lists have poor cache performance?
**A**: Nodes are allocated non-contiguously on the heap, causing cache misses.

---

**Q**: How do you reverse a singly linked list in-place?
**A**: Iterate with prev, current, next pointers; reverse each node's next reference.

---

**Q**: What is the memory overhead per node in a doubly linked list?
**A**: ~28 bytes (object header + prev ref + next ref + data ref + padding).

---

**Q**: What exception occurs when modifying a list during for-each iteration?
**A**: `ConcurrentModificationException` — use Iterator.remove() instead.

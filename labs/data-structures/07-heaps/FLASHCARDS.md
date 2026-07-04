# Flashcards: Heaps

---

**Q**: What is the time complexity of inserting into a binary heap?
**A**: O(log n) — append to end, then siftUp.

---

**Q**: What is the time complexity of extracting the min/max from a heap?
**A**: O(log n) — swap root with last, remove last, siftDown.

---

**Q**: What is the time complexity of heapify?
**A**: O(n) — bottom-up siftDown from last non-leaf to root.

---

**Q**: What is the parent index of node at index i?
**A**: (i - 1) / 2

---

**Q**: What is the left child index of node at index i?
**A**: 2i + 1

---

**Q**: What is the heap property?
**A**: Min-heap: parent ≤ both children. Max-heap: parent ≥ both children.

---

**Q**: How do you get a max-heap from Java's PriorityQueue?
**A**: `new PriorityQueue<>(Comparator.reverseOrder())`

---

**Q**: What is the advantage of an array-backed heap over a node-based tree?
**A**: Better cache locality, no pointer overhead, O(1) index access.

---

**Q**: What is heap sort's time and space complexity?
**A**: O(n log n) time, O(1) extra space (in-place).

---

**Q**: What is a complete binary tree?
**A**: All levels filled except possibly the last, filled left to right.

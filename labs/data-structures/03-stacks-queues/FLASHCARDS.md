# Flashcards: Stacks & Queues

---

**Q**: What is LIFO?
**A**: Last In, First Out — the last element added is the first removed (stack).

---

**Q**: What is FIFO?
**A**: First In, First Out — the first element added is the first removed (queue).

---

**Q**: What is the recommended Java class for stack and queue operations?
**A**: `ArrayDeque` — faster than Stack and LinkedList.

---

**Q**: How does a circular queue track head and tail?
**A**: Both indices increment modularly; `tail = (tail + 1) % capacity`.

---

**Q**: What is a monotonic stack used for?
**A**: Finding next greater/smaller elements in O(n) time.

---

**Q**: What is the time complexity of priority queue insert and remove?
**A**: O(log n) — both require siftUp/siftDown in the binary heap.

---

**Q**: How do you implement a queue using two stacks?
**A**: Push to in-stack; when out-stack is empty, transfer all from in to out; pop from out-stack.

---

**Q**: What is the difference between poll() and remove() on a Queue?
**A**: poll() returns null on empty; remove() throws NoSuchElementException.

---

**Q**: What is the MinStack problem?
**A**: A stack with push, pop, top, and getMin — all O(1).

---

**Q**: What is ArrayDeque's capacity invariant?
**A**: Power of 2; head/tail indices use `& (len - 1)` for fast modulo.

# Flashcards: Hash Tables

---

**Q**: What is the average time complexity of hash table operations?
**A**: O(1) average, O(n) worst case.

---

**Q**: What is load factor?
**A**: Number of entries divided by capacity. Triggers resize when exceeded.

---

**Q**: What is the default load factor of Java's HashMap?
**A**: 0.75

---

**Q**: What is separate chaining?
**A**: Each bucket holds a linked list (or tree) of entries that hash to the same index.

---

**Q**: What is open addressing?
**A**: On collision, probe for the next empty slot (linear, quadratic, double hashing).

---

**Q**: How does Java 8+ mitigate HashDoS attacks on HashMap?
**A**: Long chains (>8) are converted to Red-Black trees for O(log n) worst-case.

---

**Q**: Why does HashMap use power-of-2 capacity?
**A**: Enables fast index computation: `hash & (length - 1)` instead of modulo.

---

**Q**: What happens when you use a mutable object as a HashMap key?
**A**: The hash changes, making the key unfindable — a memory leak.

---

**Q**: What is the hash spreading function in Java's HashMap?
**A**: `hash ^= (hash >>> 16)` — XOR high bits into low bits for better distribution.

---

**Q**: Which map should you use for sorted keys?
**A**: TreeMap (Red-Black tree, O(log n) operations, sorted iteration).

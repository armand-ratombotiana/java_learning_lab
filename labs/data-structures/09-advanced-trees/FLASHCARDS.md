# Flashcards: Advanced Trees

---

**Q**: What is the balance factor of an AVL tree?
**A**: height(left) - height(right), must be in {-1, 0, 1}.

---

**Q**: What are the 4 rotation types in AVL trees?
**A**: LL (right rotate), RR (left rotate), LR (left then right), RL (right then left).

---

**Q**: What is the height bound of a Red-Black tree?
**A**: ≤ 2 × log₂(n+1)

---

**Q**: What are the 5 Red-Black tree invariants?
**A**: Root black, leaves black, no consecutive reds, equal black height, all nodes red or black.

---

**Q**: What is a B-tree used for?
**A**: Database indexing and file systems — disk-optimized with high branching factor.

---

**Q**: What is the space complexity of a segment tree?
**A**: O(4n) for array-based implementation.

---

**Q**: What is the Fenwick tree update formula?
**A**: `idx += idx & -idx` (add LSB to index).

---

**Q**: What is the Fenwick tree query formula?
**A**: `idx -= idx & -idx` (remove LSB from index).

---

**Q**: Which is stricter: AVL or Red-Black?
**A**: AVL (balance factor ≤ 1 vs Red-Black height ≤ 2 log n).

---

**Q**: Which Java class provides NavigableMap operations?
**A**: TreeMap (lowerKey, floorKey, ceilingKey, higherKey).

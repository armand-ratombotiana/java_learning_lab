# Flashcards: Arrays

---

**Q**: What is the time complexity of accessing an array element by index?
**A**: O(1) — constant time, independent of array size.

---

**Q**: What is the amortized cost of appending to a dynamic array?
**A**: O(1) — occasional resizing cost is spread across all appends.

---

**Q**: What growth factor does Java's ArrayList use?
**A**: 1.5x (50% increase each resize).

---

**Q**: What is spatial locality and why does it matter for arrays?
**A**: Adjacent memory addresses are loaded into the CPU cache together, making sequential access fast.

---

**Q**: How does Java prevent buffer overflow on arrays?
**A**: Runtime bounds checking — every access validates `0 <= index < length`.

---

**Q**: What is the memory address formula for `arr[i]`?
**A**: `baseAddress + elementSize × i`

---

**Q**: What is row-major order?
**A**: Multi-dimensional array elements are stored row by row in linear memory. `arr[row][col]` → `base + (row × cols + col) × elemSize`.

---

**Q**: Why does `ArrayList.remove(0)` cost O(n)?
**A**: All subsequent elements must be shifted left by one position.

---

**Q**: What exception occurs when casting `list.toArray()` to `String[]`?
**A**: `ClassCastException` — `toArray()` returns `Object[]`. Use `toArray(new String[0])`.

---

**Q**: What intrinsic method does `Arrays.copyOf` use internally?
**A**: `System.arraycopy` — a native method optimized to a single CPU instruction.

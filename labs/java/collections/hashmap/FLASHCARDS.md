# HashMap Flashcards (Spaced Repetition)

Use these flashcards for spaced repetition learning (e.g., Anki).

**Q: What is the average time complexity for HashMap get() and put()?**
A: O(1)

**Q: What is the worst-case time complexity for HashMap get() in Java 8+?**
A: O(log n) due to Red-Black treeification of bins.

**Q: What is the default initial capacity of a HashMap?**
A: 16

**Q: What is the default load factor of a HashMap?**
A: 0.75

**Q: How does HashMap calculate the bucket index from a hash code?**
A: `(n - 1) & hash`, where `n` is the table capacity (must be a power of two).

**Q: What happens if two keys have the same hash code?**
A: A hash collision occurs. The entries are chained together in a linked list (or Red-Black tree) at that bucket index.

**Q: At what threshold does a linked list in a bucket convert to a Red-Black tree?**
A: 8 elements (`TREEIFY_THRESHOLD`).

**Q: What is the `hashCode()` / `equals()` contract?**
A: If `a.equals(b)` is true, then `a.hashCode() == b.hashCode()` must also be true.

**Q: Is HashMap thread-safe?**
A: No. Concurrent modifications can lead to structural corruption (e.g., infinite loops pre-Java 8, or lost updates).

**Q: What should you use instead of HashMap for high-concurrency environments?**
A: `ConcurrentHashMap`
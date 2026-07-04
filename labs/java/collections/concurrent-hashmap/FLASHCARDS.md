# ConcurrentHashMap Flashcards

Use these flashcards for spaced repetition learning.

**Q: Why is `Hashtable` slow?**
A: It synchronizes every method on the entire map object, serializing all access.

**Q: What was the primary concurrency mechanism in Java 7's `ConcurrentHashMap`?**
A: Lock Striping (Segment locks).

**Q: What is the primary concurrency mechanism in Java 8's `ConcurrentHashMap`?**
A: CAS (Compare-And-Swap) for empty buckets, and locking the head node for collisions.

**Q: What does the `volatile` keyword do in `ConcurrentHashMap`?**
A: It ensures memory visibility, meaning changes made by one thread are immediately visible to others, allowing lock-free reads.

**Q: What is a `ForwardingNode`?**
A: A special node placed at the head of a bucket during a resize operation to indicate that the bucket's contents have been moved to the new table.

**Q: How does `ConcurrentHashMap` handle resizing efficiently?**
A: Multiple threads can collaboratively resize the map. If a thread encounters a `ForwardingNode`, it helps move other buckets to the new table.

**Q: Why is `map.put(key, map.get(key) + 1)` dangerous in a concurrent environment?**
A: It is a "Check-Then-Act" race condition. Two threads might read the same value before either writes the updated value, resulting in a lost update.

**Q: Which method should you use to atomically increment a value in a `ConcurrentHashMap`?**
A: `merge(key, 1, Integer::sum)` or `compute(key, (k, v) -> v == null ? 1 : v + 1)`.

**Q: Will `ConcurrentHashMap` throw a `ConcurrentModificationException` if modified during iteration?**
A: No. Its iterators are "weakly consistent" and will not throw this exception.
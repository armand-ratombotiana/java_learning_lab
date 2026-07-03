# Module 46: Performance Optimization - Edge Cases & Pitfalls

---

## Pitfall 1: Premature Optimization

### ❌ Wrong
Spending hours rewriting readable, maintainable code into complex bitwise operations or custom object pools because you "think" it will be faster.

### ✅ Correct
Write clean, readable code first. Only optimize after profiling the application under realistic load (using tools like JMH or JFR) proves that a specific method is a bottleneck. "Premature optimization is the root of all evil" (Donald Knuth).

---

## Pitfall 2: Memory Leaks via Caches

### ❌ Wrong
Creating a `static Map<String, User>` to cache database lookups to "improve performance," but never implementing an eviction policy. The map grows indefinitely until it causes an `OutOfMemoryError`.

### ✅ Correct
Use a proper caching library (like Caffeine or Guava) that supports Size-based, Time-based, or Reference-based (Weak/Soft references) eviction to ensure memory is eventually reclaimed.

---

## Pitfall 3: String Concatenation in Loops

### ❌ Wrong
Using the `+=` operator on `String` objects inside a tight loop with thousands of iterations. Because Strings are immutable in Java, this creates a new String object and throws away the old one on every single iteration, thrashing the Garbage Collector.

### ✅ Correct
Use `StringBuilder` (or `StringBuffer` if thread-safety is strictly required) when appending strings in a loop to mutate the underlying character array directly.
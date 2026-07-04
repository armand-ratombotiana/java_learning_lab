# Quizzes: Memory Management

Test your knowledge of Garbage Collection, Reference Types, and Escape Analysis.

## Quiz 1: Garbage Collection Mechanics

**Q1: What is the primary assumption behind the Weak Generational Hypothesis?**
- A) Objects allocated together die together.
- B) Most allocated objects die young (become unreachable quickly), and few references exist from older objects to younger objects.
- C) Garbage collection should only happen when the JVM is out of memory.
- D) Large objects live longer than small objects.
*Answer: B (This justifies separating the heap into Young and Old generations to optimize collection times).*

**Q2: Which Garbage Collector in Java uses "Regions" instead of strict physical separation of generations, and attempts to meet a user-defined pause-time goal?**
- A) Serial GC
- B) Parallel GC
- C) ZGC
- D) G1 GC (Garbage-First)
*Answer: D*

## Quiz 2: Reference Types

**Q1: If an object is ONLY reachable via a `WeakReference`, when will the Garbage Collector reclaim it?**
- A) Never.
- B) Only when the JVM is about to throw an `OutOfMemoryError`.
- C) On the very next Garbage Collection cycle.
- D) After 15 Minor GC cycles.
*Answer: C*

**Q2: What is the classic use case for a `SoftReference`?**
- A) To prevent memory leaks in `ThreadLocal` variables.
- B) To implement a memory-sensitive cache. The GC will keep the cached objects as long as there is plenty of RAM, but will clear the cache to prevent an `OutOfMemoryError` if memory gets tight.
- C) To close database connections.
- D) To pass data between threads.
*Answer: B*

## Quiz 3: Escape Analysis

**Q1: What is "Scalar Replacement" in the context of the JIT compiler?**
- A) Replacing a `double` with a `float` to save memory.
- B) When the JIT compiler determines an object never escapes a method, it avoids allocating the object on the Heap. Instead, it breaks the object down into its primitive fields (scalars) and stores them directly on the Thread Stack or in CPU registers.
- C) Replacing an object with a `WeakReference`.
- D) Moving an object from the Young Generation to the Old Generation.
*Answer: B*
# Module 46: Performance Optimization - Quizzes

---

## Q1: JIT Compilation
What does the "Escape Analysis" optimization in the JIT compiler do?

A) It prevents hackers from escaping the JVM sandbox.
B) It analyzes if an object created inside a method is referenced outside of that method. If it isn't, the JVM can allocate the object on the Stack instead of the Heap, eliminating the need for Garbage Collection for that object.
C) It automatically closes database connections when they escape the try block.
D) It detects infinite loops.

**Answer**: B
**Explanation**: Escape analysis is a powerful optimization where the compiler determines the dynamic scope of pointers. If an object does not "escape" the method, it can be allocated on the thread's stack and destroyed instantly when the method returns, reducing GC pressure.

---

## Q2: Garbage Collection
Which of the following Garbage Collectors is specifically designed for ultra-low latency, promising pause times under a millisecond even on multi-terabyte heaps?

A) Serial GC
B) Parallel GC
C) ZGC (Z Garbage Collector)
D) CMS (Concurrent Mark Sweep)

**Answer**: C
**Explanation**: ZGC (and Shenandoah) perform almost all of their work (including compaction) concurrently with the application threads, resulting in incredibly low pause times regardless of the heap size.

---

## Q3: String Operations
Why is using `StringBuilder` inside a loop much faster than using the `+=` operator on a `String`?

A) `StringBuilder` runs in a separate thread.
B) Strings in Java are immutable. The `+=` operator creates a brand-new String object in memory on every iteration, causing massive memory allocation and GC overhead. `StringBuilder` mutates a single internal character array.
C) `StringBuilder` bypasses the JVM.
D) `+=` causes network latency.

**Answer**: B
**Explanation**: Because Strings cannot be changed once created, concatenation forces the creation of new objects. `StringBuilder` provides a mutable buffer that expands efficiently.
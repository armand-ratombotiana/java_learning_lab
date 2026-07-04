# Interview Preparation: Memory Management

This document covers advanced questions related to GC algorithms, memory leaks, Escape Analysis, and Reference types.

## Q1: Explain the Weak Generational Hypothesis and how it shapes the JVM Heap.
**Answer:**
The hypothesis states two things: 1) Most newly created objects die very quickly (e.g., local variables inside a method). 2) Older objects rarely hold references to newer objects.
Because of this, the JVM divides the heap into the Young Generation (Eden + Survivors) and the Old Generation. 
When the Young Gen fills up, a "Minor GC" runs. Because most objects are dead, the GC only has to copy a tiny fraction of surviving objects to the next space, making Minor GCs extremely fast. Only objects that survive multiple Minor GCs are promoted to the Old Gen. This separation prevents the GC from having to scan the entire massive heap every time it needs memory.

## Q2: What is the difference between a `WeakReference` and a `SoftReference`?
**Answer:**
*   **`WeakReference`**: If an object is *only* reachable via a WeakReference, the Garbage Collector will reclaim it on the very next GC cycle, regardless of how much free memory the JVM has. It is used for mapping metadata to lifecycles you don't control (e.g., `WeakHashMap`).
*   **`SoftReference`**: If an object is *only* reachable via a SoftReference, the GC will try to leave it alone. It will *only* reclaim the object if the JVM is desperately low on memory and is about to throw an `OutOfMemoryError`. It is used specifically for memory-sensitive caches.

## Q3: What is "Escape Analysis" and "Scalar Replacement"?
**Answer:**
Escape Analysis is an optimization performed by the JIT compiler. The compiler analyzes the scope of an object. If it determines that the object never "escapes" the method it was created in (i.e., it isn't returned, passed to another thread, or assigned to a global variable), the compiler can optimize away the heap allocation entirely.
This is done via **Scalar Replacement**: the object is broken down into its primitive fields (scalars), and those primitives are stored directly on the Thread Stack or in CPU registers. This results in zero heap allocation and zero garbage collection overhead for that object.

## Q4: Why is overriding the `finalize()` method considered a dangerous anti-pattern?
**Answer:**
`finalize()` is unpredictable and dangerous. 
1.  There is no guarantee it will ever be called.
2.  It runs on a low-priority background thread, delaying the reclamation of the object's memory.
3.  **Resurrection**: Inside `finalize()`, a developer can accidentally assign `this` to a static field. The object is now strongly reachable again! The GC has to abort the collection. This breaks JVM invariants and causes massive performance issues.
Instead of `finalize()`, developers should use `try-with-resources` (for deterministic cleanup) or `java.lang.ref.Cleaner` (for safe, post-mortem cleanup).

## Q5: How does a `ThreadLocal` cause a memory leak in an Application Server like Tomcat?
**Answer:**
Application servers use Thread Pools. The threads are long-lived and reused across many HTTP requests.
If an application sets a value in a `ThreadLocal` during a request but forgets to call `remove()` at the end of the request, the Thread continues to hold a strong reference to that object. 
Because the object references its Class, and the Class references the application's ClassLoader, the entire application ClassLoader cannot be garbage collected when the application is undeployed. If you redeploy the app 5 times, you have 5 copies of the entire application loaded in the Metaspace, eventually causing an `OutOfMemoryError: Metaspace`.
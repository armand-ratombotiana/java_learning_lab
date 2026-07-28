# Interview Questions: Project Loom Internals

## Basic
1. What is a carrier thread in Project Loom?
2. What does it mean for a virtual thread to be "mounted" or "unmounted"?
3. How does the ForkJoinPool scheduler work with virtual threads?

## Intermediate
4. What is pinning and why does it reduce scalability?
5. How does `synchronized` cause pinning but `ReentrantLock` does not?
6. How can you detect pinning at runtime?

## Advanced
7. Explain the role of continuations in virtual thread scheduling.
8. How does the JVM handle virtual thread stack copying during yield?
9. What happens to native frames when a virtual thread yields?
10. How do object monitors interact with virtual thread pinning?

## Expert
11. Compare the cost of parking a virtual thread vs a platform thread.
12. How does the JIT compiler optimize code that runs on virtual threads vs platform threads?
13. What changes were made to `java.util.concurrent` locks to support virtual threads?
14. How does the GC handle stack segments of yielded virtual threads?

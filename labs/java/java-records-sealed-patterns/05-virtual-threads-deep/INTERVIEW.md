# Interview Questions: Virtual Threads

## Basic
1. What is a virtual thread and how does it differ from a platform thread?
2. How do you create a virtual thread?
3. What is the default stack size of a virtual thread?

## Intermediate
4. What is structured concurrency and how does `StructuredTaskScope` help?
5. What is pinning? When does it occur?
6. How do scoped values differ from thread-local variables?

## Advanced
7. Explain how the JVM's scheduler maps virtual threads to carrier threads.
8. What happens when a virtual thread blocks on I/O?
9. How does `ShutdownOnSuccess` handle task cancellation?
10. Why does `synchronized` cause pinning but `ReentrantLock` does not?

## Expert
11. What is a continuation in Project Loom? How does it relate to virtual threads?
12. How does the thread-per-request model benefit from virtual threads in a web server?
13. What are the performance implications of millions of virtual threads?
14. How do ScopedValue rebindings work with nested scope?

# Interview Questions — Virtual Threads

## Beginner
1. What are virtual threads and how do they differ from platform threads?
2. What problem does Project Loom solve?
3. How do you create a virtual thread in Java?

## Intermediate
4. Explain the M:N scheduling model used by virtual threads.
5. What is pinning? What causes it and how do you fix it?
6. How does blocking I/O work with virtual threads? Does the carrier block?
7. Compare virtual threads to reactive programming (CompletableFuture / WebFlux).

## Advanced
8. Describe the internal implementation of `VirtualThread` — Continuation, carrier, park/unpark.
9. How does `StructuredTaskScope` guarantee error propagation and task cancellation?
10. What is the impact of virtual threads on GC? (Continuation objects, stack chunks on heap)

## Design
11. "Design a web server architecture using virtual threads for an e-commerce platform."
12. "How would you migrate a 100,000-line codebase from reactive to virtual threads?"

## Problem-Solving
13. "Our service has high pinning rates. How do you diagnose and fix it?"
14. "Why is our virtual-thread-based service slower than expected under CPU load?"

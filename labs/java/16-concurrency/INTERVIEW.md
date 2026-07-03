# Interview Questions — Concurrency

## Beginner
1. What is the difference between a thread and a process?
2. How do you create a thread in Java? Compare `extends Thread` vs `implements Runnable`.
3. What is `synchronized`? What does it guarantee?

## Intermediate
4. Explain `java.util.concurrent` — name five classes and their use.
5. What is a thread pool? Why use it?
6. How does `ReentrantLock` differ from `synchronized`? When would you use one over the other?
7. Explain `CompletableFuture` — how does it improve upon `Future`?

## Advanced
8. Describe the internals of `ConcurrentHashMap` (segmentation, CAS, treebins).
9. What is the Java Memory Model? What guarantees does `volatile` provide?
10. How would you detect a deadlock in production?
11. Design a concurrent rate-limiter.
12. Explain work-stealing in `ForkJoinPool`.

## Problem-Solving
13. "Implement a thread-safe singleton with lazy initialisation."
14. "Given a service that crashes under load, how would you throttle requests?"
15. "How would you parallelise a pipeline of IO-heavy tasks?"

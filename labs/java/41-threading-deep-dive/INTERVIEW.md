# Interview Questions: Threading Deep Dive

## Beginner
1. Explain the difference between `Thread.start()` and `Thread.run()`.
2. What is a thread pool and why would you use one?
3. What is the difference between `sleep()` and `wait()`?
4. How do you create a thread in Java?

## Intermediate
5. Explain how ThreadPoolExecutor decides when to create a new thread.
6. What is work-stealing in ForkJoinPool?
7. Compare thenApply, thenApplyAsync, thenCompose, and thenComposeAsync.
8. What is the completion stage order for CompletableFuture.allOf?

## Advanced
9. How does the JVM implement the `ctl` field in ThreadPoolExecutor?
10. Explain the Treiber stack used for CompletableFuture's dependent chain.
11. How does StructuredTaskScope prevent thread leaks from subtasks?
12. What memory ordering guarantees does fork/join provide?
13. How does ForkJoinPool's common pool differ from a custom instance?
14. What happens when a CompletableFuture's dependent throws an exception?

## Expert
15. Design a thread pool that dynamically adjusts size based on queue latency.
16. How would you implement a priority-based thread pool using PriorityBlockingQueue?
17. What are the performance implications of using StructuredTaskScope with 10,000 subtasks?
18. How does the JVM handle InterruptedException in CompletableFuture stages?
19. Explain the role of VarHandle in the ForkJoinPool implementation.
20. How would you implement a thread-safe work-stealing deque without Lock?

## Answers
Answers are available in the SOLUTION directory.

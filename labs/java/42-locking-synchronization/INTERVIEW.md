# Interview Questions: Locking

## Beginner
1. What is the difference between synchronized and ReentrantLock?
2. What is a race condition and how does locking prevent it?
3. What is volatile and how is it different from synchronized?
4. What is deadlock? How do you prevent it?

## Intermediate
5. Explain the AQS framework. How does it support both exclusive and shared modes?
6. What is the difference between fair and unfair locks? When would you use each?
7. How does StampedLock's optimistic read differ from a regular read lock?
8. What is the ABA problem and how do AtomicStampedReference and AtomicMarkableReference solve it?

## Advanced
9. How does the JVM implement biased locking? Why was it removed in Java 15?
10. Explain the CLH queue algorithm. How does it guarantee FIFO ordering?
11. How does LockSupport.park() interact with thread interruption?
12. Describe the memory ordering guarantees of compareAndSwap vs getAndSet vs getAndAdd.
13. How would you implement a scalable read-write lock that avoids writer starvation?
14. What is the role of VarHandle in modern Java concurrency (Java 9+)?

## Expert
15. Design a lock-free hash map using CAS operations. How do you handle resizing?
16. How would you implement a distributed lock across JVMs (e.g., using ZooKeeper or Redis)?
17. What are the performance tradeoffs of striped locking versus single-lock designs?
18. How does the JVM's lock coarsening optimization merge adjacent synchronized blocks?
19. Implement a counting semaphore using only CAS (no AQS, no synchronized).
20. How would you adapt locking strategies for virtual threads (Project Loom)?

## Answers
Available in the SOLUTION directory.

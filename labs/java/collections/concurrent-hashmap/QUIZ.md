# ConcurrentHashMap Assessment Quiz

Test your deep understanding of Java's `ConcurrentHashMap`.

## Core Mechanics
1. Why is `Collections.synchronizedMap()` considered a poor choice for highly concurrent applications?
2. What was "Lock Striping" in Java 7, and why was it abandoned in Java 8?
3. How does `ConcurrentHashMap` achieve lock-free reads?
4. What is a CAS (Compare-And-Swap) operation, and when does `ConcurrentHashMap` use it?

## Internals & Edge Cases
5. When a thread attempts to `put` a value and encounters a hash collision in `ConcurrentHashMap`, what exactly does it lock?
6. Explain the purpose of the `ForwardingNode`.
7. How do multiple threads collaborate during a `resize()` operation in `ConcurrentHashMap`?
8. Why are the `val` and `next` fields of the internal `Node` class marked as `volatile`?

## Practical Usage
9. What is the "Check-Then-Act" anti-pattern in the context of concurrent collections?
10. Which method should you use to safely increment a counter stored in a `ConcurrentHashMap`?
11. Why is it dangerous to perform a blocking operation (like a database call) inside the lambda function passed to `computeIfAbsent()`?
12. If Thread A is writing to bucket index 5, and Thread B is writing to bucket index 42 simultaneously, will either thread block the other? Why or why not?

*(Answers provided in the SOLUTION directory)*
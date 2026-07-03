# Lab 03: Stacks & Queues

<div align="center">

![Difficulty](https://img.shields.io/badge/Difficulty-Beginner-brightgreen?style=for-the-badge)
![Time](https://img.shields.io/badge/Time-3_4_hours-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

**Fundamental abstract data types — LIFO, FIFO, and priority-based ordering**

</div>

---

## Learning Objectives

- Understand the Stack (LIFO) and Queue (FIFO) abstractions
- Implement stacks using arrays and linked lists
- Implement queues, circular queues, and deques
- Understand priority queues and heap-based implementation
- Recognize real-world use cases: expression evaluation, BFS, scheduling
- Implement a circular buffer (ring buffer)

## Prerequisites

- Lab 01: Arrays
- Lab 02: Linked Lists

## Topics Covered

- Stack operations: push, pop, peek, isEmpty
- Queue operations: enqueue, dequeue, peek, isEmpty
- Array-backed vs linked-list-backed implementations
- Circular queue with fixed capacity
- Double-ended queue (Deque)
- Priority queue: min-heap and max-heap behavior
- Big O: all operations O(1) amortized for array-backed
- Java collections: `Stack`, `ArrayDeque`, `LinkedList`, `PriorityQueue`
- Monotonic stack/queue patterns
- Common pitfalls: stack underflow, queue full (circular), null elements

## Exercises

1. Implement a stack using an array (with dynamic resizing)
2. Implement a queue using two stacks
3. Implement a circular queue with fixed capacity
4. Check balanced parentheses using a stack
5. Implement a min-stack (returns minimum in O(1))

## Estimated Time: 3-4 hours

--- 

*Start with* `THEORY.md` *for conceptual understanding, then* `CODE_DEEP_DIVE.md` *for implementations.*

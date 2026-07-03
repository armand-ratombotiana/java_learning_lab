# Module 25: Queue & Deque Advanced

## Overview
This module explores the advanced data structures used for task scheduling, buffering, and inter-thread communication. You will learn the internal mechanics of Binary Heaps, the performance benefits of `ArrayDeque`, and how to use `BlockingQueue` variants to build robust producer-consumer architectures.

## Learning Objectives
By the end of this module, you will be able to:
1. Understand the internal array-based Binary Heap structure of a `PriorityQueue` and its $O(\log N)$ time complexities.
2. Replace legacy `Stack` and slow `LinkedList` implementations with the highly optimized `ArrayDeque`.
3. Distinguish between `add()`, `offer()`, and `put()` to properly handle bounded queue constraints.
4. Choose the correct `BlockingQueue` variant (`ArrayBlockingQueue`, `LinkedBlockingQueue`, `SynchronousQueue`) for concurrent architectures.
5. Utilize `TransferQueue` for synchronous hand-offs between producers and consumers.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on PriorityQueues, Deques, BlockingQueues, and TransferQueues.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Multi-Tiered Task Scheduler demonstrating `PriorityQueue` sorting, FIFO queues, and LIFO stacks (`ArrayDeque`).
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the `add` vs `offer` trap, unbounded queue memory leaks, and mutating elements in a PriorityQueue.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of Binary Heaps, queue contracts, and blocking mechanics.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding `ArrayDeque` vs `LinkedList`, single vs two-lock queues, and `SynchronousQueue`.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of the standard Collections Framework (Module 03).
*   Understanding of the `Comparable` interface (Module 24).
*   Basic understanding of Concurrency and Threads (Module 05).
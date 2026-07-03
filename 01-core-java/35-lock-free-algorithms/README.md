# Module 35: Lock-Free Algorithms

## Overview
This module explores the pinnacle of high-performance concurrency: Lock-Free programming. You will learn how to build complex data structures that guarantee system-wide progress without ever using a blocking mechanism, relying entirely on hardware-level Compare-And-Swap (CAS) instructions.

## Learning Objectives
By the end of this module, you will be able to:
1. Understand the theoretical differences between Pessimistic Locking, Lock-Free, and Wait-Free algorithms.
2. Implement a thread-safe, lock-free Stack (LIFO) using the Treiber Stack algorithm.
3. Understand the complexities of the Michael-Scott lock-free Queue algorithm.
4. Identify the ABA problem and mitigate it using `AtomicStampedReference`.
5. Understand why the Java Garbage Collector is a massive advantage when writing lock-free code compared to C/C++.
6. Identify scenarios where lock-free algorithms perform worse than traditional locks (High Contention Livelock).

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on the Lock-Free philosophy, Treiber Stacks, Michael-Scott Queues, and performance analysis.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Lock-Free Stack from scratch and write a multi-threaded contention test to prove its correctness.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the ABA problem, memory reclamation, lost updates in compound operations, and Livelock.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of CAS loops, Queue vs Stack complexity, and GC benefits.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding Lock-Free vs Wait-Free guarantees, the $O(N)$ size of `ConcurrentLinkedQueue`, and the ABA problem.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic Concurrency (Module 05).
*   Solid understanding of Atomic Variables and CAS (Module 30).
*   Understanding of basic data structures (Stacks and Queues).
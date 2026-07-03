# Module 32: Advanced Thread Pools

## Overview
This module explores the critical internals of Java's thread pool implementations. Moving beyond the convenient but dangerous `Executors` factory methods, you will learn how to configure robust `ThreadPoolExecutor`s, apply backpressure using rejection policies, and leverage the `ForkJoinPool` for divide-and-conquer algorithms.

## Learning Objectives
By the end of this module, you will be able to:
1. Explain the exact task submission logic (Core -> Queue -> Max -> Reject) of a `ThreadPoolExecutor`.
2. Identify and avoid the `OutOfMemoryError` and Thread Explosion traps hidden in the `Executors` factory methods.
3. Configure bounded queues and utilize `CallerRunsPolicy` to implement natural backpressure in high-load systems.
4. Understand the Work-Stealing algorithm and double-ended queues (deques) used by `ForkJoinPool`.
5. Identify the dangers of executing blocking I/O operations inside the `ForkJoinPool.commonPool()`.
6. Implement proper exception handling for tasks submitted via `Runnable` and `Callable`.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on `ThreadPoolExecutor` parameters, tuning strategies, `ForkJoinPool`, and Rejection Policies.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a production-ready Thread Pool demonstrating `CallerRunsPolicy`, and implement a `RecursiveTask` using `ForkJoinPool`.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about unbounded queue OOMs, `ThreadLocal` leaks, swallowed exceptions, and common pool deadlocks.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of queue logic, work stealing, and exception handling.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding the task submission sequence, the dangers of `newFixedThreadPool`, and `ForkJoinPool` internals.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic Concurrency and Threads (Module 05).
*   Understanding of Queues and BlockingQueues (Module 25).
*   Familiarity with Exception Handling (Module 06).
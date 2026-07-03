# Module 29: Advanced Lock Mechanisms

## Overview
This module explores the explicit locking mechanisms introduced in the `java.util.concurrent.locks` package. Moving beyond the intrinsic `synchronized` keyword, you will learn how to implement interruptible locks, read-write locks, optimistic reading, and complex thread coordination using Condition variables.

## Learning Objectives
By the end of this module, you will be able to:
1. Explain the limitations of the `synchronized` keyword and when to use explicit `Lock` implementations.
2. Utilize `ReentrantLock` to implement timeouts (`tryLock`) and interruptible locking.
3. Optimize read-heavy concurrent applications using `ReadWriteLock`.
4. Maximize throughput using `StampedLock` and Optimistic Reading.
5. Coordinate multiple threads in producer-consumer scenarios using multiple `Condition` variables on a single lock.
6. Identify and prevent critical concurrency bugs like missed `finally` blocks, lock upgrading deadlocks, and spurious wakeups.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on `ReentrantLock`, `ReadWriteLock`, `StampedLock`, and `Condition` variables.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a high-performance Bounded Buffer using `Condition` variables and a Coordinate Tracker using `StampedLock`.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the missing `finally` block disaster, lock upgrading deadlocks, and spurious wakeups.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of explicit lock features, optimistic reading, and condition mechanics.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding `ReentrantLock` advantages, `StampedLock` performance, and why `await()` requires a `while` loop.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic Concurrency and the `synchronized` keyword (Module 05).
*   Understanding of standard Exception Handling (Module 06).
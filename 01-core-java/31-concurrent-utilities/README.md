# Module 31: Concurrent Utilities (Synchronizers)

## Overview
This module explores the high-level thread coordination tools provided by the `java.util.concurrent` package. You will learn how to move beyond basic mutual exclusion (locks) to orchestrate complex, multi-stage parallel algorithms using latches, barriers, and semaphores.

## Learning Objectives
By the end of this module, you will be able to:
1. Distinguish between mutual exclusion (Locks) and thread coordination (Synchronizers).
2. Utilize `CountDownLatch` to implement "starting gun" and "finish line" synchronization patterns.
3. Utilize `CyclicBarrier` to synchronize threads in multi-stage parallel processing algorithms.
4. Implement rate limiting and resource pooling using `Semaphore`.
5. Understand the dynamic registration capabilities of `Phaser`.
6. Identify and prevent critical bugs like permit leakage, missed countdowns, and broken barriers.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on `CountDownLatch`, `CyclicBarrier`, `Semaphore`, and `Phaser`.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Multi-Stage Data Processing Pipeline demonstrating rate limiting (`Semaphore`), starting guns (`CountDownLatch`), and stage merging (`CyclicBarrier`).
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the missed `countDown()` deadlock, permit leakage, broken barriers, and permit inflation.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of Latch vs Barrier mechanics and Semaphore limits.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding `CyclicBarrier` exceptions, `Phaser` flexibility, and deadlock prevention.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic Concurrency and Threads (Module 05).
*   Understanding of Exception Handling (Module 06).
*   Familiarity with Advanced Lock Mechanisms (Module 29 - recommended).
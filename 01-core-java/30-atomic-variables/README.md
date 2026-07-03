# Module 30: Atomic Variables & Hardware Concurrency

## Overview
This module dives into the lowest levels of Java concurrency, exploring how modern CPUs handle thread synchronization without using OS-level locks. You will learn how to write high-throughput, lock-free algorithms using Compare-And-Swap (CAS) instructions and the `java.util.concurrent.atomic` package.

## Learning Objectives
By the end of this module, you will be able to:
1. Understand the difference between Pessimistic Locking and Optimistic Concurrency (CAS).
2. Utilize `AtomicInteger`, `AtomicLong`, and `AtomicReference` for lock-free state management.
3. Write custom CAS spin-loops for compound atomic operations.
4. Optimize memory usage in large data structures using `AtomicFieldUpdater` and `VarHandle`.
5. Maximize throughput under extreme contention using `LongAdder`.
6. Identify and prevent complex concurrency bugs like the ABA problem and False Sharing.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on CPU CAS instructions, the Atomic package, memory optimization with updaters, and Java 9 `VarHandle`.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Lock-Free Bank Account using custom CAS loops and profile the massive performance difference between `AtomicLong` and `LongAdder`.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the ABA problem, high-contention CPU burn, False Sharing (cache line bouncing), and compound operation bugs.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of CAS mechanics, `LongAdder`, and `volatile` guarantees.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding the ABA problem, False Sharing, and why `VarHandle` is replacing `Unsafe`.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic Concurrency and the `volatile` keyword (Module 05).
*   Understanding of Advanced Lock Mechanisms (Module 29).
*   Basic understanding of CPU architecture (L1 caches) is helpful but not required.
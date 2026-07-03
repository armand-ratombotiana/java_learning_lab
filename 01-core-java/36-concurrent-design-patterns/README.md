# Module 36: Concurrent Design Patterns

## Overview
This module elevates concurrency from low-level mechanics (locks and atomic variables) to high-level architectural patterns. You will learn how to design robust, scalable, and maintainable multi-threaded systems by applying proven design patterns that decouple business logic from synchronization complexities.

## Learning Objectives
By the end of this module, you will be able to:
1. Implement the Producer-Consumer pattern using `BlockingQueue`s to decouple task generation from task execution.
2. Utilize bounded buffers to implement natural backpressure and prevent `OutOfMemoryError`s.
3. Cleanly shut down multi-threaded pipelines using the Poison Pill pattern.
4. Optimize read-heavy data structures using the Reader-Writer pattern.
5. Prevent redundant processing using the Balking pattern.
6. Understand the philosophy of the Actor Model and how it eliminates shared mutable state.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on Producer-Consumer, Reader-Writer, Thread Pools, the Actor Pattern, and the Balking Pattern.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a multi-threaded Log Processing Pipeline demonstrating a bounded Producer-Consumer queue, Poison Pill shutdown, and Balking disk saves.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about unbounded buffer OOMs, interrupt corruption, Writer Starvation, and Balking race conditions.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of backpressure, poison pills, and actor state management.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding the necessity of bounded buffers, graceful degradation, and lock-free architectures.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic Concurrency (Module 05).
*   Understanding of Advanced Lock Mechanisms (Module 29).
*   Understanding of Queues and BlockingQueues (Module 25).
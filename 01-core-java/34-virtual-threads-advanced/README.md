# Module 34: Advanced Virtual Threads

## Overview
This module introduces the most significant change to the Java concurrency model in decades: Project Loom. You will learn how Virtual Threads allow you to write simple, blocking, synchronous code that scales to millions of concurrent operations, bypassing the complexity of reactive programming frameworks.

## Learning Objectives
By the end of this module, you will be able to:
1. Explain the difference between Platform Threads (OS-managed) and Virtual Threads (JVM-managed).
2. Utilize `Executors.newVirtualThreadPerTaskExecutor()` to achieve massive concurrency for I/O-bound tasks.
3. Identify and prevent "Thread Pinning" caused by `synchronized` blocks.
4. Understand why pooling Virtual Threads is a critical anti-pattern.
5. Use `StructuredTaskScope` to orchestrate multiple concurrent subtasks with Fail-Fast and First-to-Finish policies.
6. Replace `ThreadLocal` variables with efficient, immutable `ScopedValue`s.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on Virtual Thread mechanics (mounting/unmounting), Structured Concurrency, Scoped Values, and migration strategies.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Virtual Thread Weather Aggregator demonstrating 10,000 concurrent tasks and a Fail-Fast `StructuredTaskScope`.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the dangers of Thread Pinning, the pooling anti-pattern, `ThreadLocal` memory exhaustion, and CPU-bound limitations.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of unmounting mechanics, pinning, and structured concurrency policies.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding Platform vs Virtual threads, the `ShutdownOnSuccess` policy, and `ScopedValue` memory efficiency.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic Concurrency and Threads (Module 05).
*   Understanding of Advanced Thread Pools (Module 32).
*   Familiarity with Java 21 features.
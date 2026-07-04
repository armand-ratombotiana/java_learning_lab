# Module 61: Memory Management & Garbage Collection

## Overview
This module explores the life and death of objects in the Java Virtual Machine. You will learn the architectural philosophy behind the Garbage Collector, how to tune it, and how to use advanced reference types to build memory-sensitive applications that bend, rather than break, under heavy load.

## Learning Objectives
By the end of this module, you will be able to:
1. Explain the Weak Generational Hypothesis and the physical layout of the JVM Heap (Eden, Survivor, Old).
2. Differentiate between modern Garbage Collectors (G1, ZGC, Parallel) and their specific use cases.
3. Utilize `WeakReference` and `SoftReference` to build self-cleaning caches and prevent memory leaks.
4. Understand how the JIT compiler uses Escape Analysis and Scalar Replacement to completely eliminate heap allocation for local objects.
5. Identify and prevent insidious memory leaks caused by `ThreadLocal` variables and Lapsed Listeners.
6. Understand why overriding `finalize()` is a dangerous anti-pattern.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on the Generational Heap, G1/ZGC, Reference Types, and Escape Analysis.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Memory-Sensitive Image Cache that uses `SoftReference`s to automatically evict data and prevent `OutOfMemoryError`s under heavy memory pressure.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about `ThreadLocal` ClassLoader leaks, the Lapsed Listener problem, Premature Promotion, and defeating Escape Analysis.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of GC mechanics, Reference Types, and Scalar Replacement.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding the Generational Hypothesis, Weak vs Soft references, and the dangers of `finalize()`.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of JVM Internals (Module 59).
*   Understanding of standard Collections (Module 03).
*   Understanding of Thread Pools (Module 32 - helpful for understanding ThreadLocal leaks).
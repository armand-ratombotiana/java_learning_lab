# Module 38: Memory Visibility & Ordering

## Overview
This module explores the invisible mechanics of the Java Virtual Machine. You will learn how modern CPU architectures and compiler optimizations can cause threads to see stale or corrupted data, and how to use the Java Memory Model (JMM) to ensure safe data sharing across threads without relying exclusively on heavy locks.

## Learning Objectives
By the end of this module, you will be able to:
1. Understand how CPU L1/L2 caches cause the Memory Visibility problem.
2. Explain the concept of Instruction Reordering and why compilers do it.
3. Apply the JMM "Happens-Before" rules to mathematically prove thread safety.
4. Utilize the `volatile` keyword to guarantee memory visibility and prevent reordering.
5. Identify the fatal flaw in the pre-Java 5 Double-Checked Locking (DCL) pattern and fix it.
6. Implement the Initialization-on-Demand Holder idiom for lock-free lazy loading.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on the JMM, Happens-Before rules, Volatile semantics, and Double-Checked Locking.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a program that deliberately triggers an infinite loop via a visibility failure, and implement safe Singleton publication.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the infinite loop trap, volatile compound operations, object publication traps, and over-synchronization.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of the JMM, instruction reordering, and happens-before relationships.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding `volatile` vs `synchronized`, DCL flaws, and the Holder idiom.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic Concurrency and the `synchronized` keyword (Module 05).
*   Understanding of Atomic Variables (Module 30).
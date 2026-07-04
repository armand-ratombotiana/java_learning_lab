# Module 59: JVM Internals

## Overview
This module takes you under the hood of the Java Virtual Machine. You will move past writing Java code to understanding exactly how the JVM loads, manages, and executes that code. You will explore the memory architecture, the ClassLoader delegation model, and the incredible optimizations performed by the Just-In-Time (JIT) compiler.

## Learning Objectives
By the end of this module, you will be able to:
1. Explain the three main subsystems of the JVM: ClassLoader, Memory Areas, and Execution Engine.
2. Understand the Parent-Delegation model and how it protects core Java APIs.
3. Differentiate between the Heap, the Stack, and the Metaspace.
4. Understand how the JIT compiler transforms slow interpreted bytecode into highly optimized native machine code.
5. Identify the causes of JVM-specific errors like `OutOfMemoryError: Metaspace` and `StackOverflowError`.
6. Write a custom ClassLoader to load bytecode dynamically.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on Class Loading phases, Runtime Data Areas, the Interpreter, Tiered Compilation, and Deoptimization.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a JIT Warm-up Profiler to visualize compiler optimizations, and implement a Custom ClassLoader to load classes from raw byte arrays.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about ClassLoader deadlocks, Metaspace exhaustion, cold start latency spikes, and JIT deoptimization storms.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of delegation hierarchies, memory areas, and JIT mechanics.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding Heap vs Metaspace, JIT vs AOT compilation, and the causes of `StackOverflowError`.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of standard Java execution (compiling vs running).
*   Familiarity with Reflection (Module 14 - helpful for the custom ClassLoader).
*   Basic understanding of OS memory concepts (RAM vs CPU Cache).
# Module 60: Profiling & Benchmarking

## Overview
This module explores the scientific method of software optimization. You will learn why "eyeball profiling" and simple timers are fundamentally flawed in modern Java, and how to use industry-standard tools like JMH and JFR to gather empirical data, identify true bottlenecks, and prove the efficacy of your code changes.

## Learning Objectives
By the end of this module, you will be able to:
1. Explain the flaws of using `System.nanoTime()` for benchmarking Java code.
2. Configure and run Java Microbenchmark Harness (JMH) tests to measure isolated methods accurately.
3. Utilize JMH `@State` and `Blackhole` objects to defeat JIT compiler optimizations like Dead Code Elimination and Constant Folding.
4. Understand the "Observer Effect" in profiling and why JDK Flight Recorder (JFR) is safe for production use.
5. Identify the "Coordinated Omission" trap in load testing.
6. Apply a rigorous, step-by-step methodology for performance tuning.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on the Fallacy of Eyeball Profiling, JMH mechanics, JFR, JDK Mission Control, and Optimization Methodology.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a JMH benchmark suite to empirically prove the performance differences between `String` concatenation (`+`), `StringBuilder`, and `String.format()`.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about Dead Code Elimination traps, Constant Folding, Coordinated Omission, and Profiler Overhead.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of JMH Blackholes, JFR overhead, and benchmarking methodology.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding why simple loops fail as benchmarks, how to prevent JIT deletion of code, and the dangers of Coordinated Omission.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of JVM Internals and the JIT Compiler (Module 59).
*   Familiarity with Maven (for setting up JMH).
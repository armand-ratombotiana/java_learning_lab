# Module 48: Lazy Evaluation

## Overview
This module explores Lazy Evaluation, a cornerstone of functional programming. You will learn how delaying computation until the last possible moment can dramatically improve application performance, prevent unnecessary database calls, and enable the processing of infinite data structures.

## Learning Objectives
By the end of this module, you will be able to:
1. Differentiate between Eager and Lazy evaluation strategies.
2. Utilize `Supplier<T>` to create Thunks (delayed computations) in Java.
3. Implement thread-safe Memoization to cache the results of expensive lazy computations.
4. Generate and process infinite data structures using `Stream.iterate` and `Stream.generate`.
5. Apply short-circuiting stream operations (`limit`, `takeWhile`) to prevent `OutOfMemoryError`s when working with infinite streams.
6. Prevent performance bugs by correctly choosing between `Optional.orElse()` and `Optional.orElseGet()`.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on Eager vs Lazy evaluation, Thunks, Memoization, Infinite Streams, and Short-Circuiting logic.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build an infinite Fibonacci sequence generator and a thread-safe `Memoizer` utility to cache expensive database calls.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the `orElse` trap, Infinite Stream OOMs, deferred exceptions, and capturing mutable state.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of Thunks, `orElseGet`, and Stream laziness.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding how infinite streams avoid memory exhaustion and the definition of a Thunk.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of standard Java Streams (Module 04).
*   Understanding of Functional Interfaces and `Supplier` (Module 19).
*   Basic understanding of thread safety and Double-Checked Locking (Module 38 - recommended for the Memoizer project).
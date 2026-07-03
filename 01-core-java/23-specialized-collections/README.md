# Module 23: Specialized Collections

## Overview
This module explores the specialized data structures provided by the Java Collections Framework. Moving beyond the standard `ArrayList` and `HashMap`, you will learn how to optimize for specific memory constraints, high-performance bitwise operations, and unique object identity requirements.

## Learning Objectives
By the end of this module, you will be able to:
1. Understand how `WeakReference`s work and how `WeakHashMap` prevents memory leaks in caching scenarios.
2. Utilize `IdentityHashMap` to perform reference-equality lookups, crucial for graph traversal and serialization.
3. Replace `HashSet` and `HashMap` with `EnumSet` and `EnumMap` for massive performance and memory gains when working with Enums.
4. Identify and avoid the "Value Trap" and "String Literal Trap" when using `WeakHashMap`.
5. Understand the internal bit-vector mechanics of `EnumSet`.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on `WeakHashMap`, `IdentityHashMap`, `EnumSet`, and `EnumMap`.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build an Object Graph Serializer that uses `IdentityHashMap` to prevent circular reference loops and `WeakHashMap` to cache class metadata safely.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the WeakHashMap value trap, String literal keys, and Enum collection null rejections.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of memory management, reference equality, and bit vectors.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding memory leaks, identity vs equality, and EnumSet performance.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of the standard Collections Framework (Module 03).
*   Understanding of Java Enums (Module 01/02).
*   Basic understanding of Garbage Collection concepts (Module 21 - helpful but not strictly required).
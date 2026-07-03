# Module 27: Functional Data Structures

## Overview
This module explores the paradigm shift from mutable, object-oriented collections to immutable, persistent data structures. You will learn how functional programming languages handle state without mutation, the mechanics of structural sharing, and the limitations of implementing these concepts in Java.

## Learning Objectives
By the end of this module, you will be able to:
1. Understand the difference between standard Java immutability (which requires full copying) and persistent data structures (which use structural sharing).
2. Implement a purely functional Singly Linked List (Cons List).
3. Understand the theoretical performance of Hash Array Mapped Tries (HAMT) for functional vectors and maps.
4. Explain the concept of Tail Call Optimization (TCO) and why its absence in Java causes StackOverflows in recursive algorithms.
5. Utilize lazy evaluation concepts to optimize data processing.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on the mutability problem, Persistent Data Structures, Tries, Lazy Evaluation, and Tail Recursion.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a functional Cons List from scratch, demonstrating structural sharing and the `foldLeft` operation.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about StackOverflow traps, memory churn during bulk operations, and lazy evaluation memory leaks.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of structural sharing, TCO, and HAMT performance.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding the $O(N)$ append problem, path copying, and the benefits of lazy evaluation.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of standard Collections (Module 03).
*   Understanding of Functional Interfaces and Lambdas (Module 19).
*   Basic understanding of Recursion.
# Module 21: Custom Collections Implementation

## Overview
This module explores the internal mechanics of the Java Collections Framework. By learning how to build custom collections from scratch, you will gain a deep understanding of memory management, iterator contracts, and the performance implications of different data structures.

## Learning Objectives
By the end of this module, you will be able to:
1. Understand when to build a custom collection instead of using standard JCF classes.
2. Utilize abstract base classes (`AbstractCollection`, `AbstractList`, `AbstractSet`) to minimize boilerplate code.
3. Implement custom iterators that adhere to the `Iterator` contract.
4. Implement fail-fast mechanics using `modCount` to prevent concurrent modification bugs.
5. Identify and prevent memory leaks (lingering references) in array-backed collections.
6. Understand the limitations of generics regarding array creation.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on abstract base classes, iterator mechanics, fail-fast implementation, and Big O performance.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a custom Ring Buffer (Circular Queue) extending `AbstractCollection`, complete with a fail-fast iterator.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about memory leaks, generic array casting, and breaking the `equals()`/`hashCode()` contracts.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of collection internals and memory management.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding `modCount`, primitive collections, and type erasure in arrays.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of the standard Collections Framework (Module 03).
*   Familiarity with Generics (Module 08).
*   Understanding of Big O notation basics.
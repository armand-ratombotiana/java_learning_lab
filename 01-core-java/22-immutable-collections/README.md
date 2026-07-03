# Module 22: Immutable Collections

## Overview
This module explores the critical concepts of immutability within the Java Collections Framework. You will learn how to protect application state, prevent concurrent modification bugs, and correctly use the modern factory methods introduced in Java 9 and Java 10.

## Learning Objectives
By the end of this module, you will be able to:
1. Differentiate between an unmodifiable view (`Collections.unmodifiableList`) and a truly immutable collection (`List.copyOf`).
2. Utilize Java 9 factory methods (`List.of`, `Set.of`, `Map.of`) to create concise, memory-efficient collections.
3. Understand the strict constraints of modern immutable collections (no nulls, no duplicates in sets/maps).
4. Implement defensive copying to protect internal object state from external mutation.
5. Distinguish between structural immutability (the collection) and element immutability (the objects inside).

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on unmodifiable views, Java 9 factory methods, `copyOf()`, and element immutability.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Secure Configuration Manager demonstrating defensive copying and `Map.copyOf()`.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the mutation trap of unmodifiable views, null value rejections, and `Set.of()` duplicate exceptions.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of immutable collection behavior and constraints.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding deep vs. shallow immutability, randomized iteration order, and safe getter implementation.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of the standard Collections Framework (Module 03).
*   Understanding of Advanced Encapsulation and Immutability (Module 17).
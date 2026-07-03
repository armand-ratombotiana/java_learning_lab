# Module 24: Sorted Collections

## Overview
This module explores the sorted and navigable collections in Java: `TreeSet` and `TreeMap`. You will learn the internal mechanics of Red-Black Trees, the critical rules of comparison logic, and how to perform advanced range queries and navigation.

## Learning Objectives
By the end of this module, you will be able to:
1. Understand the performance characteristics ($O(\log N)$) and self-balancing nature of Red-Black Trees.
2. Implement robust `Comparable` and `Comparator` logic that is consistent with `equals()`.
3. Utilize the `NavigableMap` and `NavigableSet` interfaces to perform ceiling, floor, and polling operations.
4. Create and manipulate dynamic range views using `subMap`, `headMap`, and `tailMap`.
5. Identify and prevent critical bugs related to mutating keys after insertion.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on Red-Black Trees, Comparators, Navigable interfaces, and Range Operations.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build an Event Scheduling System that prevents double-booking and generates daily itineraries using `TreeMap` and `subMap`.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the `compareTo` vs `equals` trap, mutating keys, and `ClassCastException`s on insertion.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of Red-Black Tree internals and Navigable methods.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding tree balancing, collision handling in TreeMaps vs HashMaps, and view constraints.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of the standard Collections Framework (Module 03).
*   Understanding of Object `equals()` and `hashCode()` contracts.
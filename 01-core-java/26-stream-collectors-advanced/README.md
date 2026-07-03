# Module 26: Advanced Stream Collectors

## Overview
This module explores the advanced capabilities of the Java Streams API's reduction phase. Moving beyond standard `toList()` and `groupingBy()` operations, you will learn how to design, implement, and optimize custom `Collector` implementations for complex data aggregation.

## Learning Objectives
By the end of this module, you will be able to:
1. Understand the four core functions of the `Collector` interface: supplier, accumulator, combiner, and finisher.
2. Implement custom collectors using `Collector.of()` to perform complex, single-pass aggregations.
3. Understand the role of the `combiner` function in parallel stream execution.
4. Apply `Collector.Characteristics` (like `CONCURRENT` and `UNORDERED`) to optimize stream performance safely.
5. Utilize `Collectors.teeing()` (Java 12+) to calculate multiple metrics simultaneously.
6. Identify and prevent memory exhaustion when using stateful terminal operations on large streams.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on the `Collector` interface, custom collector creation, characteristics, and the teeing collector.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Statistical Analyzer that uses a custom collector to calculate moving averages and `teeing()` to find min/max values in a single pass.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the dangers of un-implemented combiners, misusing the `CONCURRENT` flag, and `groupingBy` memory limits.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of custom collector mechanics and parallel stream behavior.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding the four collector functions, parallel combiners, and stateful stream operations.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of the standard Streams API (Module 04).
*   Understanding of Functional Interfaces and Lambdas (Module 19).
*   Basic understanding of Concurrency (Module 05).
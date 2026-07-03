# Module 19: Functional Interfaces & SAM

## Overview
This module explores the foundational mechanism that enables functional programming in Java: the Functional Interface. You will learn how the Single Abstract Method (SAM) concept allows lambdas and method references to integrate seamlessly into Java's strong, static type system.

## Learning Objectives
By the end of this module, you will be able to:
1. Define what makes an interface a Functional Interface and understand the `@FunctionalInterface` annotation.
2. Utilize the core functional interfaces provided in the `java.util.function` package (`Predicate`, `Function`, `Consumer`, `Supplier`).
3. Refactor lambda expressions into Method References for cleaner code.
4. Understand how Target Typing allows the compiler to infer lambda types.
5. Identify and resolve edge cases related to variable capture (effectively final) and checked exceptions inside lambdas.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on SAM, the `java.util.function` package, Method References, and Target Typing.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a flexible Rule Engine utilizing `Predicate`, `Function`, and `Consumer` to evaluate and transform data.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about checked exception limitations, the "effectively final" rule, and ambiguous method overloading.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of SAM rules and built-in functional types.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding lambda scoping, target typing, and interface design.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of standard OOP concepts and Interfaces (Module 02).
*   Basic familiarity with Lambda Expressions (Module 10 - recommended).
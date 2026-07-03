# Module 54: Type Inference

## Overview
This module explores how the Java compiler acts as a silent partner, reducing boilerplate by inferring types from context. You will learn the history of type inference in Java, culminating in the introduction of the `var` keyword in Java 10, and how to use it effectively without sacrificing code readability or type safety.

## Learning Objectives
By the end of this module, you will be able to:
1. Understand the concept of Target Typing and how it applies to Generics and Lambdas.
2. Utilize the `var` keyword to reduce boilerplate in local variable declarations.
3. Identify situations where `var` degrades readability and should be avoided.
4. Avoid the Diamond Operator trap when using `var` with collections.
5. Resolve Poly Expression ambiguities when assigning lambdas to `var`.
6. Understand how `var` interacts with non-denotable types (anonymous inner classes).

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on the evolution of Type Inference, Target Typing, and the strict rules governing the `var` keyword.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Data Processor demonstrating clean `var` usage, and an Edge Case Demo that explicitly breaks and fixes `var` traps.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the Diamond Operator fallback, Anonymous Class subtyping traps, Poly Expression ambiguity, and primitive vs wrapper inference.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of dynamic vs static typing, poly expressions, and `var` usage guidelines.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding why `var` is restricted to local variables, and what a non-denotable type is.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of Java Basics and Variable Scope (Module 01).
*   Solid understanding of Generics (Module 08).
*   Understanding of Lambdas and Functional Interfaces (Module 10/19).
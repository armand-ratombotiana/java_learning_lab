# Module 53: Generics Advanced

## Overview
This module explores the advanced capabilities and complex edge cases of Java's generic type system. You will move beyond simple `<T>` parameters to master recursive bounds, wildcard flexibility, and architectural patterns that rely on deep compiler introspection.

## Learning Objectives
By the end of this module, you will be able to:
1. Implement the Self-Type Idiom (CRTP) to enable fluent method chaining across inheritance hierarchies.
2. Utilize Recursive Type Bounds (`<T extends Comparable<T>>`) to enforce strict self-referential contracts.
3. Apply the PECS rule (Producer Extends, Consumer Super) to design highly flexible, type-safe APIs.
4. Understand how Type Erasure forces the compiler to generate hidden Bridge Methods.
5. Identify and resolve wildcard capture errors using generic helper methods.
6. Understand the limitations of multiple class bounds and the `Enum` recursive bound trap.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on Recursive Bounds, the Self-Type Idiom, Multiple Bounds, and the PECS rule.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build an Advanced Generic Data Pipeline demonstrating a CRTP Builder, recursive bound utilities, and PECS data transfer.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the `Enum` bound trap, wildcard capture errors, Bridge Method `ClassCastException`s, and CRTP inheritance limits.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of PECS read/write restrictions, CRTP mechanics, and multiple bounds syntax.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding generic invariance, Bridge Methods, and the definition of PECS.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic Generics (Module 08).
*   Solid understanding of standard OOP concepts and Inheritance (Module 02).
*   Understanding of the Builder Pattern (Module 11/51).
# Module 55: Variance & Covariance

## Overview
This module dives into the deep type theory behind Java's Generics and Arrays. You will learn why certain subtyping relationships are allowed while others are strictly forbidden, and how to use Wildcards to regain flexibility without sacrificing compile-time safety.

## Learning Objectives
By the end of this module, you will be able to:
1. Define and differentiate between Invariance, Covariance, and Contravariance.
2. Understand why Java Arrays are covariant and the runtime dangers (`ArrayStoreException`) this introduces.
3. Understand why Java Generics are invariant by default to guarantee compile-time safety.
4. Master the PECS rule (Producer Extends, Consumer Super) to design flexible, wildcard-enabled APIs.
5. Identify the difference between `List<Object>` and the unbounded wildcard `List<?>`.
6. Understand the limitations of Use-Site Variance in Java compared to other languages.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on Subtyping, Array Covariance, Generic Invariance, and the PECS rule.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a type-safe Animal Shelter data transfer utility demonstrating both `? extends` and `? super` wildcards in action.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the `ArrayStoreException`, the `add(null)` loophole, unbounded wildcard restrictions, and type erasure conflicts.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of subtyping rules, read/write restrictions, and wildcard syntax.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding the definition of PECS, the flaws of array covariance, and `List<Object>` vs `List<?>`.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic Generics (Module 08).
*   Understanding of Inheritance and Polymorphism (Module 02).
*   Familiarity with Advanced Generics (Module 53 - recommended).
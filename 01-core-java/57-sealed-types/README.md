=======
# Module 57: Sealed Types

## Overview
This module introduces Sealed Classes and Interfaces, a feature that brings Algebraic Data Types (ADTs) to Java. You will learn how to move beyond the binary choice of "open for extension" or "completely final," allowing you to design strictly controlled, finite class hierarchies that guarantee architectural integrity.

## Learning Objectives
By the end of this module, you will be able to:
1. Define controlled inheritance hierarchies using the `sealed` and `permits` keywords.
2. Understand the strict compiler rules for permitted subclasses (`final`, `sealed`, `non-sealed`).
3. Combine Sealed Interfaces with Java Records to model Sum Types (Algebraic Data Types).
4. Leverage compiler-enforced Exhaustiveness Checking in `switch` expressions to eliminate runtime bugs.
5. Identify the edge cases of sealing across package and module boundaries.
6. Understand the limitations of mocking sealed types in unit tests.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on the evolution of inheritance, the rules of sealing, ADTs, and Exhaustiveness Checking.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a robust Payment Processor demonstrating a sealed hierarchy of payment methods and an exhaustive pattern-matching `switch`.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the package boundary trap, the danger of `non-sealed`, implicit permits, and unit testing hurdles.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of the `sealed` modifier, subclass modifiers, and exhaustiveness.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding `final` vs `sealed`, ADTs, and why exhaustiveness is critical for maintainability.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of Inheritance and Polymorphism (Module 02).
*   Understanding of Java Records (Module 17 / 58).
*   Understanding of Pattern Matching and Switch Expressions (Module 49).
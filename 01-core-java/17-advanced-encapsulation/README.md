# Module 17: Advanced Encapsulation

## Overview
This module explores advanced techniques for protecting object state and defining strict domain boundaries. Moving beyond basic `private` fields and getters/setters, you will learn how to design truly immutable objects, leverage modern Java features like Records and Sealed Classes, and understand module-level encapsulation.

## Learning Objectives
By the end of this module, you will be able to:
1. Understand the limitations of anemic domain models (getters/setters everywhere).
2. Implement true immutability using defensive copying.
3. Utilize Java Records for concise, secure data carriers.
4. Apply Sealed Classes to create closed hierarchies and Algebraic Data Types (ADTs).
5. Implement exhaustive pattern matching with `switch` expressions.
6. Understand how the Java Module System (Jigsaw) provides strong architectural encapsulation.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on immutability rules, Records, Sealed Classes, and Jigsaw.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a secure domain model using immutable `Money` classes, sealed `Transaction` hierarchies, and defensive Records.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about leaky immutability, reflection bypasses, and serialization traps.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of true immutability and modern language features.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding Records vs. Classes, DDD with sealed classes, and reflection security.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of standard OOP concepts (Module 02).
*   Familiarity with Java 17+ syntax.
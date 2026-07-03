# Module 20: Advanced Polymorphism

## Overview
This module goes beyond the basic concepts of method overriding to explore the complex mechanics of Java's polymorphic engine. You will learn how Java resolves method calls when dealing with static methods, covariant return types, generic type erasure, and multiple interface inheritance.

## Learning Objectives
By the end of this module, you will be able to:
1. Distinguish between dynamic binding (overriding) and static binding (method hiding).
2. Utilize covariant return types to improve API fluency and reduce casting.
3. Understand how the Java compiler uses Bridge Methods to maintain polymorphism with generics.
4. Resolve the "Diamond Problem" introduced by default methods in interfaces.
5. Identify and prevent critical bugs related to calling overridable methods from constructors.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on Covariant Returns, Method Hiding, Method Resolution Order (MRO), and Bridge Methods.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build an Advanced Polymorphic Data Processor demonstrating covariant builders, static hiding, and default method resolution.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about field hiding, overloading vs. overriding confusion, and constructor initialization traps.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of static vs dynamic binding and bridge methods.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding the Diamond Problem, static overriding, and generic type erasure.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of standard OOP concepts (Module 02).
*   Familiarity with Generics (Module 08).
*   Understanding of Interfaces and Default Methods (Module 02).
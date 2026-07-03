# Module 52: Higher-Order Functions

## Overview
This module explores the final frontier of functional programming in Java. You will learn how to treat functions as first-class citizens—passing them as arguments to inject behavior, returning them as results to build dynamic factories, and utilizing advanced concepts like Currying and Partial Application to create highly modular, declarative code.

## Learning Objectives
By the end of this module, you will be able to:
1. Define and implement Higher-Order Functions in Java using Functional Interfaces.
2. Replace verbose GoF design patterns (like Strategy and Template Method) with concise lambda injections.
3. Understand how Closures work in Java and the necessity of the "effectively final" rule.
4. Implement Currying to transform multi-argument functions into chains of single-argument functions.
5. Apply Partial Application to "bake in" configuration data and create specialized functions from generic ones.
6. Identify and prevent memory leaks caused by lambdas capturing large objects from their enclosing scope.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on Higher-Order Functions, Closures, Currying, and Partial Application.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Dynamic Retry & Logging Framework using function decorators, and demonstrate partial application with a Tax Calculator.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the Closure memory leak, type signature hell, effectively final loop restrictions, and currying performance overhead.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of Strategy pattern replacement, Currying vs Partial Application, and Closures.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding the definition of a Closure, the difference between currying and partial application, and the risks of returning lambdas.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of Functional Interfaces and SAM (Module 19).
*   Solid understanding of Functional Composition (Module 47).
*   Understanding of Java memory management (Garbage Collection).
# Module 50: Functional Error Handling

## Overview
This module explores a radical departure from traditional Java programming: treating errors as data rather than control flow interruptions. You will learn why Exceptions break functional pipelines, and how to use the `Either` and `Try` monads to build robust, predictable, and exception-free architectures.

## Learning Objectives
By the end of this module, you will be able to:
1. Explain why throwing Exceptions violates the principles of pure functional programming.
2. Implement and utilize the `Either` (Result) pattern to represent mutually exclusive Success and Failure states.
3. Utilize the `Try` monad to safely wrap legacy, exception-throwing Java code into a functional pipeline.
4. Chain operations safely using Right-Biased `map` and `flatMap` methods.
5. Recover from errors cleanly using terminal operations like `fold` and `getOrElse`.
6. Identify the dangers of swallowing fatal JVM errors (`OutOfMemoryError`) inside custom `Try` implementations.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on the drawbacks of Exceptions, the `Either` pattern, the `Try` monad, and error recovery strategies.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a custom, Right-Biased `Either` monad from scratch and use it to construct a data validation pipeline that never throws an exception.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about swallowing fatal errors, type inference traps, Left/Right bias confusion, and overusing `Try` for standard business logic.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of Exception drawbacks, `Either` vs `Optional`, and error recovery mechanics.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding Referential Transparency, Right-Bias, and escaping functional pipelines at architectural boundaries.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of Functional Composition and Monads (Module 47).
*   Understanding of standard Exception Handling (Module 06).
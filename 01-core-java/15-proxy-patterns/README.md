# Module 15: Proxy Patterns & Dynamic Proxies

## Overview
This module explores the Proxy design pattern and its dynamic implementation in Java. Proxies are a foundational concept in enterprise Java, forming the backbone of Aspect-Oriented Programming (AOP), transaction management, lazy loading, and security frameworks.

## Learning Objectives
By the end of this module, you will be able to:
1. Understand the structural Proxy pattern and its use cases (virtual, protection, remote).
2. Implement static proxies manually.
3. Utilize JDK Dynamic Proxies (`java.lang.reflect.Proxy`) to proxy interfaces at runtime.
4. Understand how CGLIB generates subclasses to proxy concrete classes.
5. Identify and resolve the "Self-Invocation" problem in Spring AOP.
6. Write custom `InvocationHandler` implementations for cross-cutting concerns like logging or retries.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on static proxies, JDK Dynamic Proxies, CGLIB, and their application in enterprise frameworks like Spring and Hibernate.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a custom `@Retry` annotation framework using JDK Dynamic Proxies and an `InvocationHandler`.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the self-invocation problem, issues with `final` classes, and `ClassCastException` traps.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of proxy mechanics and limitations.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding Proxy vs. Decorator, Spring AOP internals, and Hibernate lazy loading.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and prerequisites.

## Prerequisites
*   Strong understanding of Object-Oriented Programming (Module 02)
*   Familiarity with Interfaces and Inheritance
*   Basic understanding of Reflection (Module 14 - recommended)
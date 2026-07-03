# Module 51: Immutability Patterns

## Overview
This module explores the mechanics of building truly immutable objects in Java. You will learn how to move beyond basic `final` keywords to implement deep defensive copying, build complex immutable state using the Builder pattern, and safely generate new states using "Wither" methods.

## Learning Objectives
By the end of this module, you will be able to:
1. State the strict rules required to make a Java class truly immutable.
2. Differentiate between shallow copying and deep defensive copying to prevent encapsulation leaks.
3. Utilize the Builder pattern to separate mutable construction from immutable runtime state.
4. Implement "Wither" methods to functionally "modify" state by returning new instances.
5. Understand the "Initialization Safety" guarantee provided by the Java Memory Model for `final` fields.
6. Understand the performance limitations of `CopyOnWriteArrayList` and the concept of Persistent Data Structures.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on Immutability rules, Copy-on-Write patterns, Builders, and Persistent Data Structures.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Thread-Safe Immutable Configuration object demonstrating deep defensive copying and Wither methods.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the shallow copy trap, `java.util.Date` clone vulnerabilities, Object Publication races, and Wither memory churn.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of deep copies, Initialization Safety, and Wither mechanics.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding the exact rules of immutability, `CopyOnWrite` performance, and JMM guarantees.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of standard OOP concepts (Module 02).
*   Solid understanding of Advanced Encapsulation (Module 17).
*   Understanding of Memory Visibility and Object Publication (Module 38).
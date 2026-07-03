# Module 43: Advanced Serialization

## Overview
This module explores the deep mechanics and severe security implications of Java's built-in serialization framework. You will learn how to customize the serialization process, protect object invariants using proxy patterns, and secure your applications against Remote Code Execution (RCE) vulnerabilities using input filters.

## Learning Objectives
By the end of this module, you will be able to:
1. Understand the mechanics of `serialVersionUID` and how to maintain backward compatibility.
2. Customize the serialization process using `writeObject` and `readObject` to handle encryption or `transient` field initialization.
3. Protect object invariants by forcing deserialization through constructors using the Serialization Proxy Pattern.
4. Understand how Deserialization Gadgets lead to Remote Code Execution (RCE) vulnerabilities.
5. Secure applications against malicious payloads using Java 9's `ObjectInputFilter`.
6. Prevent the Singleton pattern from being broken during deserialization.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on `writeObject`, `readObject`, the Serialization Proxy Pattern, Gadgets, and `ObjectInputFilter`.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Secure Serialization Pipeline that encrypts sensitive fields and uses an allow-list filter to block a simulated malicious gadget.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the Singleton bypass, `serialVersionUID` crashes, `transient` NPEs, and inner class serialization traps.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of constructor bypassing, proxy mechanics, and security filters.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding why Java Serialization is considered dangerous, how RCE works, and the `readResolve` method.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic File I/O and Streams (Module 07).
*   Understanding of Object-Oriented design and Constructors (Module 02).
*   Understanding of the Singleton Pattern (Module 15).
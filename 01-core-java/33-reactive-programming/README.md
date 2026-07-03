# Module 33: Reactive Programming & Flow API

## Overview
This module introduces the paradigm shift from imperative, blocking concurrency to asynchronous, non-blocking reactive streams. You will learn the core principles of the Reactive Manifesto, the mechanics of Backpressure, and the standard Java 9 `Flow` API interfaces that enable interoperability between modern reactive frameworks like Project Reactor and RxJava.

## Learning Objectives
By the end of this module, you will be able to:
1. Explain the performance limitations of the "Thread-per-Request" model.
2. Understand the four principles of the Reactive Manifesto (Responsive, Resilient, Elastic, Message Driven).
3. Implement the four core interfaces of the Java 9 Flow API: `Publisher`, `Subscriber`, `Subscription`, and `Processor`.
4. Understand and implement the Backpressure contract (`request(n)`) to prevent consumer memory exhaustion.
5. Identify the fatal consequences of blocking an event-loop thread in a reactive pipeline.
6. Understand the difference between lazy reactive pipelines and immediate imperative execution.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on Reactive Principles, Backpressure, and the Java 9 Flow API execution lifecycle.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a raw Java Flow API Publisher and Subscriber from scratch, demonstrating asynchronous execution and explicit backpressure request handling.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the dangers of blocking `onNext()`, breaking the backpressure contract, swallowed exceptions, and the `request(Long.MAX_VALUE)` trap.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of Reactive principles, Flow interfaces, and event-loop blocking.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding Backpressure, Java Streams vs Reactive Streams, and why the Flow API was added to the JDK.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic Concurrency and Threads (Module 05).
*   Understanding of standard Java Streams (Module 04).
*   Familiarity with Advanced Thread Pools (Module 32 - helpful for understanding event loops).
# Module 42: HTTP Client (Java 11+)

## Overview
This module explores the modern `java.net.http.HttpClient` API, which completely replaces the legacy `HttpURLConnection`. You will learn how to build fluent, asynchronous HTTP requests, handle large response bodies safely, and establish persistent, bi-directional WebSocket connections.

## Learning Objectives
By the end of this module, you will be able to:
1. Explain the architectural advantages of the Java 11 `HttpClient` over legacy networking classes.
2. Build and execute synchronous and asynchronous HTTP requests using the fluent Builder API.
3. Combine multiple asynchronous HTTP requests using `CompletableFuture` for parallel data aggregation.
4. Choose the appropriate `BodyHandler` to prevent `OutOfMemoryError`s when downloading large files.
5. Establish and manage a `WebSocket` connection, including handling reactive backpressure in the listener.
6. Identify and prevent severe resource leaks caused by improper `HttpClient` instantiation and unbounded thread pools.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on `HttpClient`, `HttpRequest`, `HttpResponse`, Asynchronous Execution, and WebSockets.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build an Async API Aggregator that fetches data from three endpoints concurrently, and a WebSocket client that echoes messages.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the `HttpClient` instantiation leak, `sendAsync` thread exhaustion, WebSocket backpressure traps, and HTTP/2 downgrades.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of client pooling, asynchronous futures, and body handlers.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding thread pool exhaustion, streaming large payloads, and WebSocket reactive mechanics.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of standard Java interfaces and Lambdas (Module 10/19).
*   Understanding of `CompletableFuture` and Asynchronous programming (Module 05).
*   Familiarity with Reactive Programming concepts (Module 33 - helpful for WebSockets).
# Module 46: Reactive I/O

## Overview
This module represents the culmination of the Advanced I/O & Networking track. It merges the high-performance mechanics of Non-Blocking I/O (NIO) with the asynchronous, backpressure-aware architecture of Reactive Streams. You will learn how to build systems that handle massive data flows without ever blocking a thread or exhausting memory.

## Learning Objectives
By the end of this module, you will be able to:
1. Explain the architectural intersection of NIO (Selectors) and Reactive Streams (Flow API).
2. Utilize `AsynchronousFileChannel` to read and write files entirely in the background using `CompletionHandler` callbacks.
3. Understand how the Java 11 `HttpClient` implements end-to-end backpressure using TCP Windowing.
4. Identify the catastrophic consequences of blocking an Event Loop thread in frameworks like Netty or Spring WebFlux.
5. Safely offload legacy blocking operations to bounded thread pools in a reactive pipeline.
6. Prevent native memory leaks by correctly managing `ByteBuffer` lifecycles in reactive streams.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on the marriage of NIO and Reactive Streams, AsynchronousFileChannel, and Reactive HTTP.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Reactive File Copy Utility that uses chained `CompletionHandler`s to copy massive files without blocking a single thread.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about Event Loop freezes, callback thread exhaustion, native buffer leaks, and the `Long.MAX_VALUE` backpressure trap.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of AIO mechanics, callback execution, and event loop blocking.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding AIO vs NIO, offloading blocking tasks, and how reactive clients prevent OutOfMemory errors.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of Reactive Programming and Backpressure (Module 33).
*   Solid understanding of NIO Channels and Buffers (Module 39).
*   Understanding of Thread Pools and Executors (Module 32).
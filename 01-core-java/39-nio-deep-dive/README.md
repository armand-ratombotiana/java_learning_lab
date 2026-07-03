# Module 39: NIO Deep Dive (New I/O)

## Overview
This module explores the high-performance, non-blocking I/O architecture introduced in Java 1.4. You will move away from the traditional thread-per-connection model and learn how to use Channels, Buffers, and Selectors to build massively scalable servers capable of handling tens of thousands of concurrent connections on a single thread.

## Learning Objectives
By the end of this module, you will be able to:
1. Explain the architectural differences and scalability limits between `java.io` (Streams) and `java.nio` (Channels/Buffers).
2. Master the mechanics of `ByteBuffer`, specifically the critical role of the `flip()`, `clear()`, and `compact()` methods.
3. Configure `SocketChannel`s for non-blocking operations.
4. Implement the Reactor pattern using a `Selector` to multiplex thousands of network connections onto a single event-loop thread.
5. Identify and mitigate severe NIO bugs, including partial writes, Direct Buffer memory leaks, and the 100% CPU epoll bug.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on Channels, Buffers, Non-Blocking mode, and the Multiplexing Loop.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a fully functional, single-threaded non-blocking Chat Server from scratch using raw NIO components.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the missing `flip()` trap, partial writes, direct buffer management, and the epoll bug.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of buffer capacity/limit/position and selector key management.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding Double-Copy overhead, Direct Buffers, and the Reactor pattern.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic File I/O (Module 07).
*   Solid understanding of Concurrency and the cost of OS Threads (Module 05).
*   Familiarity with network sockets is helpful but not strictly required.
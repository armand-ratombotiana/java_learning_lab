# Pedagogic Guide: Reactive I/O

## 1. Module Overview
This module is the culmination of the I/O and Concurrency tracks. It combines the raw performance of non-blocking hardware interfaces with the elegant, safe abstractions of reactive programming. It prepares learners to work with modern, high-throughput frameworks like Spring WebFlux, Vert.x, and Netty.

## 2. Learning Paths

### Path A: The Spring WebFlux Developer (Focus: Event Loops & Integration)
**Target Audience**: Developers migrating from Spring MVC (Tomcat) to Spring WebFlux (Netty).
*   **Focus**: `EDGE_CASES.md` (Blocking the Event Loop) and `INTERVIEW_PREP.md`.
*   **Key Takeaway**: Understanding that a single blocking JDBC call can freeze the entire web server, and mastering the pattern of offloading legacy blocking code to bounded elastic thread pools.

### Path B: The Framework Architect (Focus: AIO & Callbacks)
**Target Audience**: Senior developers building custom proxies, file transfer utilities, or low-level data pipelines.
*   **Focus**: `DEEP_DIVE.md` (AsynchronousFileChannel) and `MINI_PROJECT.md`.
*   **Key Takeaway**: Mastering the "Callback Hell" of chained `CompletionHandler`s, and understanding how to manage thread pools for asynchronous callbacks to prevent resource exhaustion.

## 3. Teaching Strategies

### The "Restaurant Kitchen" Metaphor (Event Loop vs Thread Pool)
To explain why blocking an Event Loop is so catastrophic compared to blocking a traditional Thread Pool:
*   **Thread Pool (Tomcat/Spring MVC)**: A restaurant with 200 waiters (threads). A waiter takes an order and goes to the kitchen to wait for the food (blocking I/O). If the kitchen is slow, that waiter is stuck. However, there are 199 other waiters still taking orders. The system degrades gracefully.
*   **Event Loop (Netty/WebFlux)**: A restaurant with exactly 4 super-fast waiters on roller skates (1 per CPU core). They take an order, hand it to the kitchen, and immediately skate to the next table. But if one of these waiters decides to stand in the kitchen and wait for a steak to cook (a blocking JDBC call), 25% of your entire restaurant's capacity is instantly frozen. If 4 waiters do this, the restaurant completely stops functioning.

### The "Domino Effect" Metaphor (Callback Chaining)
To explain the `MINI_PROJECT.md` (Asynchronous File Copy):
Explain that asynchronous programming without `CompletableFuture` or Reactive Streams requires chaining callbacks.
It's like setting up dominos. 
1.  You push the first domino (start the read).
2.  When the read finishes, its callback pushes the next domino (start the write).
3.  When the write finishes, its callback pushes the next domino (clear buffer, start the next read).
The main thread just sets up the dominos and walks away. The dominos fall on their own in the background.

## 4. Common Mental Blocks & Clarifications

### Block 1: "What is the difference between NIO (Selectors) and AIO (Callbacks)?"
*   **Clarification**: Use the "Pizza Buzzer" analogy.
    *   **NIO (Selector)**: You order pizza and wait in your car. Every 5 minutes, you walk inside and ask, "Is it ready?" (Polling the Selector).
    *   **AIO (CompletionHandler)**: You order pizza and give them your phone number (Callback). You drive home and watch TV. When the pizza is ready, they call you.

### Block 2: "If Reactive I/O is so fast, why doesn't everything use it?"
*   **Clarification**: Explain the trade-offs. Reactive I/O is incredible for scaling to millions of connections (C10K problem). However, it is notoriously difficult to debug. Stack traces in reactive code are practically useless because the code executed asynchronously across multiple threads. Furthermore, if the application is purely CPU-bound (doing heavy math), reactive I/O provides zero benefit and actually slows things down due to framework overhead.

### Block 3: "How does backpressure actually stop the sender from sending data?"
*   **Clarification**: This is a brilliant question that bridges the gap between software and hardware. Explain that when the Java Subscriber stops calling `request(n)`, the Java framework stops reading from the OS Socket Buffer. As data arrives over the network, the OS Socket Buffer fills up. When it's full, the OS TCP stack automatically sends a "Window Size = 0" packet to the sender's OS. The sender's OS physically stops transmitting packets. The backpressure travels from the Java heap all the way down the network cable to the sender.

## 5. Assessment Strategy
*   **Formative**: Provide a Spring WebFlux controller method that calls `Thread.sleep(1000)`. Ask the learner to explain the exact consequence of this code on a 4-core machine under heavy load.
*   **Summative**: The `MINI_PROJECT.md` requires the learner to implement an asynchronous file copy using raw `AsynchronousFileChannel`s and `CompletionHandler`s. By successfully chaining the read and write callbacks into a loop without causing a `StackOverflowError`, they prove they understand the mechanics of purely asynchronous execution.
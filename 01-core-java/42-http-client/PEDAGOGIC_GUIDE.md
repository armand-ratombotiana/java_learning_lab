# Pedagogic Guide: Java HTTP Client

## 1. Module Overview
This module introduces the modern way to interact with the web in Java. It replaces outdated, boilerplate-heavy code with fluent, asynchronous, and reactive paradigms. It serves as a practical application of concepts learned in earlier modules (CompletableFutures, Builder Pattern, Reactive Streams).

## 2. Learning Paths

### Path A: The API Consumer (Focus: Builders & Handlers)
**Target Audience**: Developers who need to integrate with REST APIs, download files, or fetch configuration data.
*   **Focus**: `DEEP_DIVE.md` (Core Components) and `MINI_PROJECT.md` (Async Aggregator).
*   **Key Takeaway**: Mastering the Builder pattern to construct requests, and understanding how to use `CompletableFuture.allOf()` to dramatically reduce overall latency when fetching independent data sources.

### Path B: The System Integrator (Focus: Resources & WebSockets)
**Target Audience**: Senior developers building microservice gateways, real-time dashboards, or high-throughput proxies.
*   **Focus**: `EDGE_CASES.md` (Client Leaks, Thread Exhaustion) and the WebSocket sections.
*   **Key Takeaway**: Understanding the severe consequences of instantiating `HttpClient` per request, and mastering the reactive backpressure mechanics required to build stable WebSocket clients.

## 3. Teaching Strategies

### The "Restaurant Menu" Metaphor (Builders)
To explain the Builder pattern used in `HttpRequest`:
You don't go into a kitchen and shout "Give me a burger with cheese but no onions on a gluten-free bun!" all at once. It's confusing.
Instead, you get a menu (the Builder). You check the box for Burger. You check the box for Cheese. You check the box for Gluten-Free. You uncheck Onions. Once the menu is perfectly filled out, you hand it to the waiter (`build()`). The kitchen then creates the immutable Burger (`HttpRequest`). You cannot change the burger once it's built.

### The "Delivery Truck vs. Pipeline" Metaphor (Body Handlers)
To explain `BodyHandlers.ofString()` vs `BodyHandlers.ofInputStream()`:
*   **ofString() (Delivery Truck)**: You order 10,000 bricks. The supplier puts all 10,000 bricks into one massive dump truck and dumps them in your living room all at once. Your house collapses (OutOfMemoryError).
*   **ofInputStream() (Pipeline)**: You order 10,000 bricks. The supplier sets up a conveyor belt. They send you one brick at a time. You pick it up, process it, and throw it away, then grab the next one. Your house only ever holds 1 brick at a time, so it never collapses, no matter how many bricks are ordered.

## 4. Common Mental Blocks & Clarifications

### Block 1: "Why does `sendAsync()` crash my app when `send()` works fine?"
*   **Clarification**: This is the most dangerous trap of the API. Explain that `send()` blocks the current thread. If you have 10 threads, you can only do 10 things. `sendAsync()` returns immediately. If a loop calls `sendAsync()` 10,000 times, the default `HttpClient` will try to spawn 10,000 background threads to handle them. Show them how to inject a bounded `ExecutorService` into the `HttpClient` builder to enforce limits.

### Block 2: "Why did my WebSocket stop receiving messages?"
*   **Clarification**: This requires revisiting Reactive Programming (Module 33). The `WebSocket.Listener` is not a passive event listener; it is an active participant in flow control. If the developer implements `onText` but forgets to call `webSocket.request(1)` or return the `super` method, the JVM thinks the application is overwhelmed and tells the OS to stop reading from the TCP socket.

### Block 3: "Why is `HttpClient` better than `HttpURLConnection`?"
*   **Clarification**: Show a side-by-side code comparison. To read a JSON string in Java 8, you had to open a connection, get an InputStream, wrap it in an InputStreamReader, wrap that in a BufferedReader, write a `while(line != null)` loop, append to a StringBuilder, catch 3 different exceptions, and manually close the streams in a `finally` block. In Java 11, it's `client.send(req, BodyHandlers.ofString()).body()`. The visual difference is staggering.

## 5. Assessment Strategy
*   **Formative**: Provide a snippet of code that creates a new `HttpClient` inside a `for` loop and makes a request. Ask the learner to identify the architectural flaw and explain what will happen to the server.
*   **Summative**: The `MINI_PROJECT.md` requires the learner to build an Async Aggregator. By forcing them to use `CompletableFuture` to fetch three APIs simultaneously, they prove they understand how to use the modern API to minimize latency in microservice architectures.
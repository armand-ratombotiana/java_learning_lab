# Module 36: Advanced Design Patterns - Quizzes

---

## Q1: Microservices Resilience
Which design pattern is primarily used to prevent a single downstream service failure from causing a cascading failure across an entire microservices architecture?

A) Message Translator
B) Observer Pattern
C) Circuit Breaker
D) Strangler Fig

**Answer**: C
**Explanation**: The Circuit Breaker pattern detects failures and prevents the application from repeatedly trying to execute an operation that's likely to fail, returning a fallback response or an immediate error instead.

---

## Q2: Legacy Migration
What is the purpose of the Strangler Fig pattern?

A) To encrypt old data.
B) To incrementally replace a legacy monolithic system by gradually rewriting specific features into new services.
C) To compress application logs.
D) To find circular dependencies in code.

**Answer**: B
**Explanation**: Just like a strangler fig vine grows around a tree until the tree dies, this pattern wraps a legacy application with new microservices. Over time, features are routed to the new services until the legacy app can be decommissioned.

---

## Q3: Asynchronous Programming
When using Java's `CompletableFuture` to execute an asynchronous task that performs heavy, blocking I/O operations (like database queries), what is the best practice?

A) Provide a custom `Executor` (like a cached thread pool) to the async method to prevent starving the default `ForkJoinPool`.
B) Call `.join()` immediately after initiating the future.
C) Use a `while(true)` loop to check if it's done.
D) It is impossible to do blocking I/O asynchronously in Java.

**Answer**: A
**Explanation**: By default, `CompletableFuture` runs tasks on the common `ForkJoinPool`, which is sized to the number of CPU cores. Blocking these threads with I/O will quickly starve the application. Passing a dedicated `Executor` prevents this.
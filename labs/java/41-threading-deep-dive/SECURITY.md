# Security Considerations for Threading

## Thread Pool Exhaustion (DoS)
An attacker can submit tasks to a thread pool faster than they complete, filling the queue (if bounded) or exhausting memory (if unbounded). Bounded queues with rejection protect against this, but AbortPolicy crashes the caller. Use CallerRunsPolicy to throttle the caller or a custom policy that blocks when the queue is full.

## Thread Injection
If an attacker can control the thread pool configuration (via JMX, configuration injection, or deserialization), they could set corePoolSize to Integer.MAX_VALUE, creating millions of threads. Protect thread pool configuration from untrusted input.

## ThreadLocal Data Leakage
ThreadLocals are not cleaned up when a thread returns to the pool. If sensitive data (credentials, session tokens) is stored in a ThreadLocal, the next task using that thread can access it. Use `remove()` in a finally block or use a request-scoped context.

## InterruptedException Handling
Swallowing InterruptedException is a security anti-pattern:
```java
try { Thread.sleep(1000); } catch (InterruptedException e) { /* ignore */ }
```
This prevents thread pools from shutting down gracefully. Always either re-interrupt (`Thread.currentThread().interrupt()`) or propagate the exception.

## CompletableFuture and Continuation Injection
If a malicious actor can inject code into the CompletableFuture pipeline (e.g., via a compromised method reference), they can execute arbitrary code on the common pool. Validate all function arguments passed to thenApply/exceptionally.

## StructuredTaskScope Resource Exhaustion
Creating a StructuredTaskScope that forks thousands of subtasks can exhaust the thread pool. The scope does not limit subtask count — the application must enforce bounds. Use a semaphore or limit the number of forked tasks.

## Thread Safety of Shared State
Improper synchronization allows data races that can leak sensitive information. A partially constructed object (visible via the `this` escape in a constructor) may expose uninitialized security-critical state. Use safe publication patterns (final fields, volatile, or synchronized).

# Interview Questions: Structured Concurrency & Scoped Values

## Company-Specific Focus

### Google
- StructuredTaskScope: managing virtual threads with scoped lifecycle
- Shutdown on failure: capturing errors from concurrent subtasks
- Structured concurrency vs unstructured thread management

### Microsoft
- StructuredTaskScope vs C# Task.WhenAll pattern
- Error propagation in structured concurrency
- Scoped values for context propagation

### Amazon
- Scoped values replacing ThreadLocal in virtual thread environment
- Request-scoped context propagation without memory leaks
- Structured concurrency for microservice orchestration

### Meta
- Task failure handling: shutting down sibling tasks on failure
- ScopedValue: inheriting context across virtual threads
- Memory safety: scoped values prevent ThreadLocal leaks

### Apple
- Structured concurrency for clean resource management
- ScopedValue vs ThreadLocal: why scoped values are safer

### Oracle
- JEP 428: Structured Concurrency (Incubator)
- JEP 429: Scoped Values (Incubator)
- JEP 446: Scoped Values (Second Preview in Java 21)
- JEP 453: Structured Concurrency (Preview in Java 21)

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1114 Print in Order | Easy | Google, Amazon, Microsoft | Structured coordination of tasks |
| 1115 Print FooBar Alternately | Medium | Google, Amazon | Structured coordination |
| 1242 Web Crawler Multithreaded | Medium | Amazon, Google | Structured crawling |
| 1226 The Dining Philosophers | Medium | Amazon, Google, Microsoft | Structured resource management |

## Real Production Scenarios
- **Twitter**: Using StructuredTaskScope for an API that calls 4 downstream services — any failure cancels all others
- **LinkedIn**: Scoped values for tracing context in virtual threads — replaced ThreadLocal for zero leaks
- **Uber**: Scoped values for request-scoped security context propagating across async boundaries

## Interview Patterns & Tips
- **StructuredTaskScope**: Subtasks are managed within a scope; all must complete or be cancelled
- **ShutdownOnFailure**: If one subtask fails, all remaining active subtasks are cancelled
- **ScopedValue**: A value bound to a thread/scope, inherited by child virtual threads
- **ScopedValue vs ThreadLocal**: ScopedValue is immutable (per scope), avoids memory leaks

## Deep Dive Questions
- **JVM**: How does StructuredTaskScope manage virtual thread lifecycle?
- **ScopedValue**: How is ScopedValue implemented at the JVM level?
- **Rebind**: How does ScopedValue.where() work for rebinding values in subtasks?
- **Memory**: Why do scoped values prevent memory leaks that ThreadLocal had?
- **Performance**: What is the cost of structured concurrency vs unstructured thread management?
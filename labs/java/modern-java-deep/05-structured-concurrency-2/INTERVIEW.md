# Interview Questions: Structured Concurrency 2

## Company-Specific Focus

### Google
- Structured concurrency: subtasks share the same lifecycle as the enclosing scope
- StructuredTaskScope: Java's implementation of structured concurrency
- Fork/join within scope: all subtasks must complete before scope closes

### Microsoft
- Structured concurrency vs C# Task patterns
- Scoped values: context propagation without ThreadLocal

### Amazon
- Scoped context: using ScopedValue for request-scoped tracing
- Shutdown policies: ShutdownOnFailure and ShutdownOnSuccess

### Meta
- Structured concurrency patterns: error propagation and cancellation
- Virtual threads + structured concurrency: clean async code

### Apple
- Resource management: structured scopes ensure no resource leaks
- Scoped values: immutable context per scope

### Oracle
- JEP 428: Structured Concurrency (Incubator)
- JEP 453: Structured Concurrency (Preview in JDK 21)
- ScopedValue: JEP 446 (Preview in JDK 21)
- StructuredTaskScope: ShutdownOnFailure, ShutdownOnSuccess

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1114 Print in Order | Easy | Google, Amazon | Structured subtask ordering |
| 1242 Web Crawler Multithreaded | Medium | Amazon, Google | Structured scope for crawling |

## Real Production Scenarios
- **LinkedIn**: StructuredTaskScope for orchestrating downstream service calls
- **Uber**: Scoped values replacing ThreadLocal in virtual thread-based services

## Interview Patterns & Tips
- **Scope management**: all forks complete before scope.join() returns
- **Error handling**: ShutdownOnFailure cancels all if any fails
- **ScopedValue**: immutable, inheritable by child virtual threads

## Deep Dive Questions
- **StructuredTaskScope**: How does it manage thread lifecycle?
- **ShutdownOnFailure**: How does cancellation propagate?
- **ScopedValue vs ThreadLocal**: How does ScopedValue prevent memory leaks?
- **Virtual thread integration**: How does structured concurrency leverage virtual threads?
- **Performance**: What is the cost of structured concurrency?
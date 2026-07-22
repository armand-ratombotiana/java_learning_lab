# Interview Questions: Structured Concurrency

## Company-Specific Focus

### Google
- Structured concurrency principle: task lifetime is contained within a syntactic scope
- StructuredTaskScope: managing virtual thread lifecycle
- ShutdownOnFailure vs ShutdownOnSuccess: failure handling strategies

### Microsoft
- Structured concurrency vs C# Task-based async
- Error propagation: if one subtask fails, cancel remaining tasks

### Amazon
- Request scope: structured concurrency for request-per-thread model
- Resource management: all subtasks complete before scope exits

### Meta
- Structured concurrency patterns: fork-join within a scope
- Timeouts: applying timeouts to subtask scope execution

### Apple
- Virtual threads + structured concurrency: clean resource management
- Scoped values + structured concurrency: context propagation

### Oracle
- JEP 428: Structured Concurrency (First Incubator)
- JEP 453: Structured Concurrency (Preview in JDK 21)
- StructuredTaskScope implementation

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1114 Print in Order | Easy | Google, Amazon | Structured task ordering |
| 1242 Web Crawler Multithreaded | Medium | Amazon, Google | Structured crawling scope |

## Real Production Scenarios
- **Uber**: StructuredTaskScope for parallel API calls — any failure cancels scope, saves resources
- **LinkedIn**: Structured concurrency for request processing with 5 downstream calls

## Interview Patterns & Tips
- **Structured**: subtasks are created within a scope, complete before scope returns
- **ShutdownOnFailure**: cancel if any subtask fails
- **ShutdownOnSuccess**: return first successful result
- **Virtual threads**: structured concurrency works naturally with virtual threads

## Deep Dive Questions
- **Scope**: How does StructuredTaskScope enforce structured lifetime?
- **Fork**: What happens when a subtask is forked?
- **Join**: How does scope.join() wait for all subtasks?
- **Exception handling**: How does structured concurrency propagate exceptions?
- **Virtual thread integration**: How do virtual threads interact with structured concurrency?
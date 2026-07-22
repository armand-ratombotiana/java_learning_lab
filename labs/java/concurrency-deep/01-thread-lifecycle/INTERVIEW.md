# Interview Questions: Thread Lifecycle

## Company-Specific Focus

### Google
- Thread states: NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED
- State transitions: what methods cause which transitions
- Thread creation: extending Thread vs implementing Runnable

### Microsoft
- Java thread lifecycle vs C# Thread/ThreadPool
- Thread state monitoring: getState() method

### Amazon
- Thread lifecycle in application servers: thread pooling
- Thread death: uncaught exception handling with UncaughtExceptionHandler

### Meta
- Thread vs Runnable: why Runnable is preferred
- Daemon threads: JVM exits when only daemon threads remain

### Apple
- Thread naming and identification for debugging
- Thread priority: JVM to OS mapping differences on macOS

### Oracle
- JVM Thread implementation: 1:1 native thread mapping
- Thread states in the JVM specification

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1114 Print in Order | Easy | Google, Amazon, Microsoft | Thread ordering via CountDownLatch |
| 1115 Print FooBar Alternately | Medium | Google, Amazon | Two-thread coordination |
| 1116 Print Zero Even Odd | Medium | Google, Microsoft, Apple | Multi-thread state control |
| 1117 Building H2O | Medium | Google, Amazon | Thread lifecycle management |
| 1226 The Dining Philosophers | Medium | Amazon, Meta, Google | Thread state management |

## Real Production Scenarios
- **LinkedIn**: 200 threads stuck in BLOCKED state due to lock contention — thread dump showed the bottleneck
- **Uber**: Thread leak from not handling InterruptedException — threads never terminated, degraded service

## Interview Patterns & Tips
- **start() vs run()**: start creates a new thread; run executes in current thread
- **InterruptedException**: handle by restoring interrupt flag or propagating
- **Thread.join()**: wait for another thread to complete

## Deep Dive Questions
- **JVM thread creation**: How does the JVM create a native thread via JNI?
- **Thread state representation**: How does the JVM track thread states?
- **Thread priority mapping**: How does Java thread priority map to OS priority on Linux/Windows/macOS?
- **Thread stack**: What is the default thread stack size?
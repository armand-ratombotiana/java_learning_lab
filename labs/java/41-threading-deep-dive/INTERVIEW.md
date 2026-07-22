# Interview Questions: Threading Deep Dive

## Company-Specific Focus

### Google
- Thread lifecycle: NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED
- Thread priorities and OS scheduling: JVM thread priority to native thread priority mapping
- Daemon vs user threads: when to use each

### Microsoft
- Java threads vs CLR threads: mapping to OS threads
- ThreadGroup API: legacy, why it's mostly deprecated
- Thread.UncaughtExceptionHandler for robust applications

### Amazon
- Worker thread pools for request processing in high throughput services
- Thread per request vs event loop model: tradeoffs at scale
- Thread creation overhead: why thread pooling is essential

### Meta
- Thread context switching: cost and measurement
- Thread stack size: default, tuning, implications
- Platform threads vs virtual threads in Java 21+

### Apple
- Thread local storage: ThreadLocal, InheritableThreadLocal
- ThreadLocal memory leaks in application server environments
- Thread naming and identification for debugging

### Oracle
- JVM thread implementation: 1:1 mapping to OS threads (HotSpot)
- Thread priorities: Windows, Linux, macOS mapping differences
- Thread state representation in the JVM
- java.lang.Thread internals: native thread creation

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1114 Print in Order | Easy | Google, Apple, Microsoft | Thread ordering with CountDownLatch |
| 1115 Print FooBar Alternately | Medium | Amazon, Google | Two-thread coordination |
| 1116 Print Zero Even Odd | Medium | Google, Apple, Microsoft | Multi-thread coordination |
| 1117 Building H2O | Medium | Google, Amazon | Complex thread synchronization |
| 1226 The Dining Philosophers | Medium | Amazon, Google, Microsoft | Classic thread synchronization problem |

## Real Production Scenarios
- **Uber**: Thread pool exhaustion in a rate-limited service — all 200 threads were blocked on a network call
- **LinkedIn**: Thread leak caused by not properly handling InterruptedException — threads never terminated
- **Twitter**: Thread dump analysis revealed 300 threads stuck in BLOCKED state on a single synchronized map

## Interview Patterns & Tips
- **Thread dump analysis**: Learn to read thread dumps for deadlock detection
- **InterruptedException**: Always handle it properly (restore interrupt flag or propagate)
- **Thread pools**: Use ThreadPoolExecutor directly, not Executors factory methods

## Deep Dive Questions
- **JVM**: How does the JVM create a platform thread? Through JNI and native OS thread creation
- **Thread state**: How does the JVM track thread states and transitions?
- **Context switching**: What is the cost of context switching between threads?
- **Memory**: What is the default thread stack size? How much memory does an idle thread consume?
- **Java 21+**: How do virtual threads differ in memory consumption and lifecycle?
# 05 - Java Concurrency

Multithreading and concurrent programming in Java. Covers thread creation, synchronization, executors, concurrent collections, atomic variables, and CompletableFuture for asynchronous programming.

## Prerequisites

- Java 17+
- Maven 3.x

## Key Concepts

- Thread creation: extending `Thread`, implementing `Runnable`
- Synchronization: `synchronized` keyword, intrinsic locks
- ExecutorService: thread pools (`newFixedThreadPool`, `newCachedThreadPool`), `Future`
- Concurrent collections: `ConcurrentHashMap`, `BlockingQueue`, `LinkedBlockingQueue`
- Atomic variables: `AtomicInteger`, `AtomicBoolean`, `AtomicReference`
- CompletableFuture: `supplyAsync`, `thenAccept`, `join`
- Race conditions and thread safety
- Deadlock prevention

## Module Structure

- `src/main/java/com/learning/lab/module05/Lab.java` - Main lab source
- `RESOURCES/` - Supplementary diagrams and materials
- `SOLUTION/` - Solution implementations

## Learning Objectives

- Create and manage threads in Java
- Use synchronized blocks and atomic classes for thread safety
- Leverage thread pools via ExecutorService
- Implement async workflows with CompletableFuture

## Estimated Time

- Core lab: 2-3 hours
- With projects/extensions: 4-6 hours

## How to Run

```bash
cd 05-concurrency
mvn compile exec:java -Dexec.mainClass="com.learning.lab.module05.Lab"
```

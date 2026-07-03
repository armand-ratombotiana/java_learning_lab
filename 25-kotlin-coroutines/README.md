# 25 - Kotlin Coroutines (Java Async Patterns)

Asynchronous programming patterns comparing Java and Kotlin approaches. Covers Java virtual threads (Kotlin launch equivalent), CompletableFuture (async/await equivalent), parallel streams (Flow equivalent), and concept mapping between Kotlin coroutines and Java concurrency APIs.

## Prerequisites

- Java 21+
- Maven 3.x

## Key Concepts

- Java Virtual Threads vs Kotlin `launch` coroutine
- Java CompletableFuture vs Kotlin `async`/`await`
- Java Parallel Streams vs Kotlin `Flow`
- Java BlockingQueue vs Kotlin `Channel`
- Java ExecutorService vs Kotlin `Dispatchers`
- Java `Thread.sleep()` vs Kotlin `delay()`
- Structured concurrency comparison

## Module Structure

- `kotlin-coroutines-basics/` - Kotlin vs Java async patterns comparison

## Learning Objectives

- Understand coroutine concepts through Java equivalents
- Map Kotlin coroutine patterns to Java concurrency APIs
- Use virtual threads and CompletableFuture for async workflows

## Estimated Time

- 1-2 hours

## How to Build

```bash
cd 25-kotlin-coroutines
mvn clean package
```

Run the lab:

```bash
cd kotlin-coroutines-basics
mvn compile exec:java -Dexec.mainClass="com.learning.kotlin.KotlinCoroutinesLab"
```

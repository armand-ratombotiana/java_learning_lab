# Threading Deep Dive — Overview

This lab explores the internals of Java threading: thread lifecycle states, ThreadPoolExecutor mechanics, ForkJoinPool work-stealing, CompletableFuture composition, and structured concurrency (JEP 428).

## Learning Objectives
- Distinguish all six thread states and their transitions
- Understand ThreadPoolExecutor core/max pool, work queues, keep-alive, and rejection policies
- Explain ForkJoinPool work-stealing and RecursiveTask decomposition
- Build asynchronous pipelines with CompletableFuture (thenApply, thenCompose, allOf, anyOf)
- Use StructuredTaskScope for structured concurrency with ShutdownOnSuccess/ShutdownOnFailure

## Prerequisites
- Java 21+
- Basic knowledge of threads and Runnable
- Familiarity with lambda expressions and functional interfaces

## Files in This Lab
- Java sources: `src/main/java/com/javaacademy/lab41/threading/`
- Tests: `src/test/java/com/javaacademy/lab41/threading/`
- Documentation: 24 .md files covering theory, history, mental models, internals, exercises, quizzes, and more
- Subdirectories: MINI_PROJECT, REAL_WORLD_PROJECT, CHALLENGE, TESTS, BENCHMARK, DIAGRAMS, SOLUTION

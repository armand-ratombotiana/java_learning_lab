# Lab 05: Virtual Threads Deep Dive

## Overview
Virtual threads (Project Loom, finalized in Java 21) are lightweight threads that dramatically simplify concurrent programming. This lab covers structured concurrency, scoped values, and the thread-local vs scoped values trade-off.

## Goals
- Create and manage virtual threads with `Thread.ofVirtual()`
- Use structured concurrency with `StructuredTaskScope`
- Share immutable context with scoped values
- Understand when to use scoped values vs thread-local
- Handle cancellation and error propagation

## Prerequisites
- Java 21+
- Basic understanding of concurrency (platform threads)
- Familiarity with `Callable` and `Future`

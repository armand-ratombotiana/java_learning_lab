# 50 - Virtual Threads

Virtual threads (Project Loom) are lightweight threads that dramatically reduce the cost of concurrency, enabling millions of threads on a single JVM without OS thread limitations.

## Overview

- **Topic**: Virtual Thread Implementation
- **Prerequisites**: Java concurrency, thread basics
- **Duration**: 2-3 hours

## Key Concepts

- Virtual thread creation
- Structured concurrency
- Thread pooling vs virtual threads
- Migration patterns

## Getting Started

Run the training code:
```bash
cd 50-virtual-threads
mvn compile exec:java -Dexec.mainClass=com.learning.virtualthreads.Lab
```

## Module Contents

- Thread.ofVirtual() API
- Scoped values
- Structured concurrency
- Migration strategies
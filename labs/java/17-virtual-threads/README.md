# Lab 17: Virtual Threads (Project Loom)

**Objective:** Understand virtual threads (Project Loom), their differences from platform threads, and structured concurrency.

**Key Concepts:**
- Virtual vs platform threads
- `Thread.ofVirtual()` and `Executors.newVirtualThreadPerTaskExecutor()`
- Structured concurrency (`StructuredTaskScope`)
- Pin prevention and carrier threads
- `jdk.internal.misc` carrier mechanics

**Prerequisites:** Java 21+, concurrency (lab 16)

**Estimated time:** 90 minutes

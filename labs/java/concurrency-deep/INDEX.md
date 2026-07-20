# Concurrency Deep - Micro-Labs

## Overview
This module contains 10 atomic deep-dive micro-labs.

## Lab Index
| # | Lab | Description |
|---|-----|-------------|
| 01 | [Thread Lifecycle](./01-thread-lifecycle/) | NEW/RUNNABLE/BLOCKED/WAITING/TIMED_WAITING/TERMINATED, OS thread states, JVM thread dump analysis |
| 02 | [Synchronized Internals](./02-synchronized-internals/) | Monitor enter/exit, biased locking (removed in 21+), lightweight/heavyweight locks, object header mark word |
| 03 | [Volatile Semantics](./03-volatile-semantics/) | JSR-133 happens-before, memory barrier insertion (x86 vs ARM), atomicity vs visibility, DCL problem |
| 04 | [Atomic Classes](./04-atomic-classes/) | CAS loop, VarHandle.compareAndSet, AtomicInteger vs synchronized, ABA problem, striped counters (LongAdder) |
| 05 | [Lock Framework (AQS)](./05-lock-framework/) | AQS (AbstractQueuedSynchronizer), CLH lock queue, ConditionObject, ReentrantLock vs synchronized benchmarks |
| 06 | [CompletableFuture](./06-completable-future/) | CompletableFuture chain, supplyAsync/runAsync, thenApply/thenCompose, allOf/anyOf, exceptionally/completeExceptionally |
| 07 | [Fork-Join Pool](./07-fork-join-pool/) | Work-stealing, FJTask, RecursiveAction/RecursiveTask, managedBlocker, parallelism level tuning |
| 08 | [StampedLock](./08-stamped-lock/) | ReadWriteLock vs StampedLock, optimistic read, tryConvertToWriteLock, validation pattern |
| 09 | [Phaser](./09-phaser/) | Phaser phase counting, party registration/deregistration, cyclic barrier vs phaser, dynamic party count |
| 10 | [Structured Concurrency](./10-structured-concurrency/) | StructuredTaskScope, ShutdownOnFailure/Success, task scope hierarchy, cancellation propagation |

## How to Use
Each micro-lab is self-contained with 24 markdown files, Java source code,
JUnit 5 tests, and 7 subdirectories for hands-on work.
Start from lab 01 and progress sequentially.
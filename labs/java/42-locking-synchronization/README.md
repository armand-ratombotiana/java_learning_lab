# Locking & Synchronization Internals — Overview

This lab explores Java's locking internals: synchronized (thin/inflated monitors), ReentrantLock and AQS, StampedLock, LockSupport, and CAS-based atomics.

## Learning Objectives
- Understand synchronized monitors (thin locks, inflated locks, biased locking)
- Build a custom lock using AbstractQueuedSynchronizer
- Distinguish fair vs unfair ReentrantLock behavior
- Use StampedLock's optimistic reads for high concurrency
- Explain LockSupport park/unpark mechanics
- Implement atomic operations with Unsafe CAS

## Prerequisites
- Java 21+
- Threading fundamentals (Lab 41)
- Basic understanding of memory ordering

## Files in This Lab
- Java sources: `src/main/java/com/javaacademy/lab42/locking/`
- Tests: `src/test/java/com/javaacademy/lab42/locking/`
- 24 documentation .md files covering all aspects of locking theory and practice

# Lab 04: Project Loom — Virtual Threads Internals

## Overview
Project Loom introduced virtual threads (Java 21) as a fundamental JVM feature. This lab dives into internals: carrier threads, continuation-based scheduling, mount/unmount, pinning, and thread-local optimization.

## Goals
- Understand the virtual thread lifecycle (mount/unmount)
- Learn how carrier threads multiplex virtual threads
- Identify and avoid pinning scenarios
- Understand continuations and their role in scheduling
- Analyze thread-local behavior with virtual threads

## Prerequisites
- Java 21+
- Knowledge of operating system threads
- Familiarity with `synchronized` and `ReentrantLock`

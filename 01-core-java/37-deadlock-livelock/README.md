# Module 37: Deadlock & Livelock

## Overview
This module explores the dark side of concurrency: when threads stop making progress. You will learn the theoretical foundations of deadlocks, how to prevent them using architectural patterns, and how to identify and resolve livelocks and thread starvation.

## Learning Objectives
By the end of this module, you will be able to:
1. Identify the four Coffman conditions required for a deadlock to occur.
2. Implement the "Lock Ordering" strategy to mathematically prevent Circular Wait deadlocks.
3. Understand how to use `tryLock()` with Random Backoff to prevent Livelock.
4. Distinguish between Deadlock (frozen), Livelock (active but stuck), and Starvation (perpetually bypassed).
5. Diagnose deadlocks in running applications using JVM thread dumps and JMX.
6. Identify the hidden dangers of holding locks while calling external "alien" methods.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on the Coffman conditions, Lock Ordering, Livelock, Starvation, and Deadlock Detection.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Bank Transfer simulator that deliberately causes a deadlock, and then resolve it using both Lock Ordering and Random Backoff strategies.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about hidden locks in alien methods, hash code collisions in lock ordering, `tryLock` livelock traps, and static initializer deadlocks.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of the Coffman conditions, Livelock vs Deadlock, and Starvation.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding deadlock prevention, lock ordering tie-breakers, and fair locks.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic Concurrency and the `synchronized` keyword (Module 05).
*   Understanding of Advanced Lock Mechanisms (Module 29).
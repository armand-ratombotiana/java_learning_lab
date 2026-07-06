# Lab 47: Profiling & Observability Deep Dive

async-profiler, JFR, heap dump analysis, JMX, continuous profiling

## Overview

This lab provides a comprehensive deep-dive into profiling and observability. You will explore the theoretical foundations, practical implementations, and advanced concepts through hands-on coding exercises and real-world scenarios.

## Learning Objectives

- Understand the fundamental concepts of profiling and observability
- Implement working code using profiling and observability patterns
- Analyze performance characteristics and trade-offs
- Apply best practices in production scenarios
- Debug and optimize profiling and observability related issues

## Prerequisites

- Java 21+ Development Kit
- Maven 3.8+ or Gradle 7+
- Basic understanding of Java programming
- Familiarity with concurrent programming concepts

## Lab Structure

| File | Description |
|------|-------------|
| THEORY.md | Theoretical foundation |
| WHY_IT_EXISTS.md | Motivation and history |
| HOW_IT_WORKS.md | Mechanical explanation |
| INTERNALS.md | Implementation details |
| CODE_DEEP_DIVE.md | Annotated code walkthrough |
| EXERCISES.md | Practice problems |
| QUIZ.md | Self-assessment questions |
| MINI_PROJECT/ | Hands-on project |

## Key Topics

- async-profiler (CPU, allocation, wall, lock profiling)
- JFR event types and custom events
- Heap dump analysis with MAT/Eclipse
- GC log analysis
- JMX monitoring and MBeans

## Java Source Files

- CpuProfilingTarget.java - AllocationProfilingTarget.java - LockProfilingTarget.java - JfrEventStreamExample.java - JmxMonitorExample.java - Join-With "
"

## Estimated Time

- Theory: 45-60 minutes
- Practice: 60-90 minutes
- Exercises: 45-60 minutes
- Project: 60-90 minutes
- Total: 3-5 hours
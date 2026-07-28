# Lab 05: Structured Concurrency

## Overview
Structured Concurrency (finalized in Java 21) treats groups of concurrent tasks as a single unit of work. This lab covers `StructuredTaskScope`, `ShutdownOnSuccess`, `ShutdownOnFailure`, error propagation, and cancellation semantics.

## Goals
- Use `StructuredTaskScope` for task composition
- Handle success and failure patterns with built-in policies
- Propagate exceptions between subtasks
- Implement timeouts and cancellation
- Build resilient concurrent workflows

## Prerequisites
- Java 21+
- Virtual threads basics
- Familiarity with `Future` and `Callable`

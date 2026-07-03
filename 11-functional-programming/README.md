# 11 - Functional Programming

Functional programming principles in Java. Covers functional interfaces, pure functions (no side effects, deterministic), higher-order functions (functions as arguments/return values), method references, closures (stateful function factories), function composition (andThen, compose, Predicate.and/or), and immutability concepts.

## Prerequisites

- Java 17+
- Maven 3.x

## Key Concepts

- Functional interfaces: `Predicate`, `Function`, `Consumer`, `Supplier`, `UnaryOperator`, `BiFunction`
- Pure functions: deterministic output, no side effects
- Higher-order functions: functions that accept or return functions
- Method references: `String::toUpperCase`, `String::concat`, `LocalTime::now`
- Closures: capturing and modifying state across invocations
- Function composition: `andThen()`, `compose()`, `Predicate.and()`, `Predicate.or()`
- Immutability and its role in functional programming

## Module Structure

- `src/main/java/com/learning/lab/module11/Lab.java` - Main lab source
- `RESOURCES/` - Reference materials and diagrams

## Learning Objectives

- Apply functional programming concepts in Java
- Compose functions for complex data transformations
- Distinguish pure vs impure functions

## Estimated Time

- 1-2 hours

## How to Run

```bash
cd 11-functional-programming
mvn compile exec:java -Dexec.mainClass="com.learning.lab.module11.Lab"
```

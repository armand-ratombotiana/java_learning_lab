# 12 - Java 21 Features

Modern Java language features introduced in JDK 21. Covers virtual threads (lightweight concurrency), pattern matching for instanceof and switch, records (compact data carriers), sealed classes (restricted inheritance hierarchies), and switch expressions (arrow syntax, pattern labels).

## Prerequisites

- Java 21+
- Maven 3.x

## Key Concepts

- Virtual threads: `Executors.newVirtualThreadPerTaskExecutor()`, lightweight concurrency
- Pattern matching: `instanceof` with binding variable, nested pattern matching in switch
- Records: compact constructors, accessor methods, `equals()`/`hashCode()`/`toString()` auto-generation
- Sealed classes & interfaces: `sealed`, `permits`, exhaustive switch over sealed types
- Switch expressions: arrow syntax (`->`), multiple case labels, yield for block bodies
- Record patterns (preview): destructuring records in pattern matching

## Module Structure

- `src/main/java/com/learning/lab/module12/Lab.java` - Main lab source

## Learning Objectives

- Write concurrent programs with virtual threads
- Use pattern matching and switch expressions effectively
- Model data with records and restricted hierarchies with sealed classes

## Estimated Time

- 2-3 hours

## How to Run

```bash
cd 12-java-21-features
mvn compile exec:java -Dexec.mainClass="com.learning.lab.module12.Lab"
```

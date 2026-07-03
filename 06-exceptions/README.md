# 06 - Java Exception Handling

Exception handling mechanisms in Java. Covers try-catch blocks, multi-catch, try-with-resources, checked vs unchecked exceptions, custom exceptions, exception hierarchy, and the throw/throws keywords.

## Prerequisites

- Java 17+
- Maven 3.x

## Key Concepts

- Try-catch blocks and exception propagation
- Multi-catch syntax (Java 7+)
- Try-with-resources for auto-closable resources
- Checked (compile-time) vs unchecked (runtime) exceptions
- Custom exception classes with error codes
- Exception hierarchy: `Throwable` > `Exception` / `Error`
- `throw` and `throws` keywords
- Best practices for exception handling

## Module Structure

- `src/main/java/com/learning/lab/module06/Lab.java` - Main lab source

## Learning Objectives

- Properly handle exceptions using try-catch blocks
- Differentiate between checked and unchecked exceptions
- Create and use custom exception types
- Implement try-with-resources for resource management

## Estimated Time

- 1-2 hours

## How to Run

```bash
cd 06-exceptions
mvn compile exec:java -Dexec.mainClass="com.learning.lab.module06.Lab"
```

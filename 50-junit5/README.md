# JUnit 5 - Advanced Testing

## Overview
JUnit 5 is the next generation of JUnit with modern features like parameterized tests, nested tests, and dynamic test generation.

## Key Features
- @ParameterizedTest for data-driven tests
- @Nested for hierarchical test groups
- @RepeatedTest for multiple runs
- DynamicTest at runtime
- Extensions API

## Project Structure
```
50-junit5/
  junit5-advanced/
    src/main/java/com/learning/junit5/JUnit5Lab.java
```

## Running
```bash
cd 50-junit5/junit5-advanced
mvn compile exec:java
```

## Concepts Covered
- Parameterized tests with sources
- Nested test classes
- Display names
- Timeout and exception assertions

## Dependencies
- JUnit Jupiter
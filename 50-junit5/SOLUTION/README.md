# JUnit 5 Solution

## Overview
This module covers advanced JUnit 5 features.

## Key Features

### Tests
- @Test
- @Disabled
- @DisplayName

### Nested Tests
- @Nested
- Inner test classes

### Parameterized Tests
- @ValueSource
- @CsvSource
- @MethodSource

### Assertions
- assertAll
- assertThrows
- assertTimeout

## Usage

```java
JUnit5Solution solution = new JUnit5Solution();

// Basic test
solution.testAddition();

// Nested tests
JUnit5Solution.MathTests mathTests = new JUnit5Solution.MathTests();
mathTests.testMultiply();

// Parameterized
// @ValueSource, @CsvSource annotations

// Exception
solution.testException();
```

## Dependencies
- JUnit 5 (Jupiter)
- JUnit 5 Params
- JUnit 5 Launcher
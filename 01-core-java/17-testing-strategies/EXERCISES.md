# Exercises: Testing Strategies

## Exercise 1: Unit Tests with JUnit
Write unit tests for a Calculator class:
- testAdd, testSubtract, testMultiply, testDivide
- Test exception handling for division by zero

## Exercise 2: Mockito Mocks
Create mock objects for a UserRepository.
Verify interactions in UserService.

```java
when(userRepo.findById(1L)).thenReturn(Optional.of(user));
```

## Exercise 3: Parameterized Tests
Use @ParameterizedTest with multiple inputs:
- @CsvSource or @MethodSource
- Test edge cases from one test method

## Exercise 4: Integration Test
Write an integration test using @SpringBootTest.
Test repository with embedded database.

## Exercise 5: Test Coverage
Run tests with JaCoCo and analyze coverage report.
Identify uncovered code paths.

## Solutions
See src/main/java/com/learning/testing/TestingStrategiesLab.java
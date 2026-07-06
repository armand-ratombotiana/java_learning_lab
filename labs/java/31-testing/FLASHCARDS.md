# JUnit 5 Flashcards — Spaced Repetition

## Q: What is the difference between `@Test` and `@ParameterizedTest`?
**A:** `@Test` marks a method as a single test case that runs once with no parameters. `@ParameterizedTest` allows the same test method to be executed multiple times with different arguments supplied by a source annotation (`@ValueSource`, `@CsvSource`, `@MethodSource`, etc.). This reduces boilerplate when testing the same logic against many inputs.

## Q: What is `Mockito.when().thenReturn()` used for?
**A:** It stubs a method call on a mock object. `when(mock.someMethod()).thenReturn(value)` tells Mockito: "when `someMethod` is invoked on this mock, return `value` instead of executing the real implementation." This isolates the unit under test from its dependencies. You can also chain `.thenThrow()` for exception testing.

## Q: What is a Test Double? Name 5 types.
**A:** A Test Double is a generic term for any object that stands in for a real dependency during testing. The five types (per Gerard Meszaros) are:

1. **Dummy** — passed around but never used (e.g., filling a parameter list).
2. **Fake** — a working lightweight implementation (e.g., an in-memory database).
3. **Stub** — provides canned answers to calls made during the test.
4. **Spy** — wraps a real object and records interactions; can also stub partial behavior.
5. **Mock** — pre-programmed with expectations about which methods will be called and in what order; verifies behavior.

## Q: What is TDD and its 3 phases?
**A:** Test-Driven Development is a discipline where you write tests before production code. The cycle is:

1. **Red** — Write a failing test for the next small piece of desired behavior.
2. **Green** — Write the minimal production code to make the test pass.
3. **Refactor** — Clean up both test and production code while keeping tests green.

Repeat the cycle for each incremental feature.

## Q: What is JaCoCo and what does it measure?
**A:** JaCoCo (Java Code Coverage) is a byte-code instrumentation library that measures test coverage. It computes:

- **Instruction coverage** (line coverage) — percentage of bytecode instructions executed.
- **Branch coverage** — percentage of conditional branches (`if`/`else`, `switch`) taken.
- **Cyclomatic complexity** — number of linearly independent paths through code.
- **Method/class coverage** — percentage of methods and classes touched by tests.

JaCoCo integrates with Maven/Gradle and can enforce coverage thresholds in the build.

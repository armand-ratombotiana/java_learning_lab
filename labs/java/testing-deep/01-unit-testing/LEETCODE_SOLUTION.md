# Design a Test Framework with JUnit-Style Assertions

> **Category**: Testing Deep (Unit Testing / Assertions)

## Problem

Design a lightweight test framework that discovers and runs test methods, reports pass/fail results, and supports JUnit-style assertions.

## Solution

A simple test framework with:
- `@Test` annotation for test method discovery
- `Assertions` utility class with `assertEquals`, `assertTrue`, `assertThrows`
- A test runner that scans for `@Test` methods and reports results

```java
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

/**
 * Lightweight JUnit-Style Test Framework.
 *
 * Usage: TestRunner.run(MyTestClass.class);
 */
public class SimpleTestFramework {

    // ─── Annotations ───

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Test {}

    // ─── Assertions ───

    public static class Assertions {

        public static void assertEquals(int expected, int actual) {
            if (expected != actual) {
                throw new AssertionError("Expected <" + expected + "> but was <" + actual + ">");
            }
        }

        public static void assertEquals(Object expected, Object actual) {
            if (!Objects.equals(expected, actual)) {
                throw new AssertionError("Expected <" + expected + "> but was <" + actual + ">");
            }
        }

        public static void assertTrue(boolean condition) {
            if (!condition) throw new AssertionError("Expected true but was false");
        }

        public static void assertTrue(boolean condition, String message) {
            if (!condition) throw new AssertionError(message);
        }

        public static void assertThrows(Class<? extends Throwable> expectedType, Runnable code) {
            try {
                code.run();
            } catch (Throwable t) {
                if (expectedType.isInstance(t)) return;
                throw new AssertionError("Expected " + expectedType.getSimpleName()
                    + " but caught " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
            throw new AssertionError("Expected " + expectedType.getSimpleName() + " but no exception was thrown");
        }
    }

    // ─── Test Runner ───

    public static class TestResult {
        final String testName;
        final boolean passed;
        final String message;

        TestResult(String testName, boolean passed, String message) {
            this.testName = testName;
            this.passed = passed;
            this.message = message;
        }
    }

    public static List<TestResult> run(Class<?> testClass) {
        List<TestResult> results = new ArrayList<>();

        for (Method method : testClass.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Test.class)) continue;
            if (method.getParameterCount() != 0) continue;

            String testName = testClass.getSimpleName() + "#" + method.getName();
            try {
                Object instance = testClass.getDeclaredConstructor().newInstance();
                method.invoke(instance);
                results.add(new TestResult(testName, true, "PASS"));
                System.out.println("  ✓ " + testName);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                results.add(new TestResult(testName, false, cause.getMessage()));
                System.out.println("  ✗ " + testName + ": " + cause.getMessage());
            } catch (Exception e) {
                results.add(new TestResult(testName, false, "Setup error: " + e.getMessage()));
                System.out.println("  ✗ " + testName + ": " + e.getMessage());
            }
        }

        return results;
    }

    // ─────────────────────
    // Example test class
    // ─────────────────────
    public static class CalculatorTest {

        @Test
        void testAdd() {
            Assertions.assertEquals(5, 2 + 3);
        }

        @Test
        void testSubtract() {
            Assertions.assertEquals(1, 3 - 2);
        }

        @Test
        void testDivisionByZero() {
            Assertions.assertThrows(ArithmeticException.class, () -> { int x = 1 / 0; });
        }

        @Test
        void testFailing() {
            Assertions.assertTrue(false, "This test is designed to fail");
        }
    }

    // ─────────────────────
    // Main
    // ─────────────────────
    public static void main(String[] args) {
        System.out.println("Running tests...");
        List<TestResult> results = run(CalculatorTest.class);

        long passed = results.stream().filter(r -> r.passed).count();
        long failed = results.size() - passed;

        System.out.println("\nResults: " + passed + " passed, " + failed + " failed out of " + results.size());
        assert passed == 3 : "Expected 3 passed, got " + passed;
        assert failed == 1 : "Expected 1 failed, got " + failed;
        System.out.println("All framework tests passed.");
    }
}
```

## Key Insights

1. **Reflection-based discovery**: `@Test` annotation at runtime + `Method.invoke()` for execution.
2. **Exception-driven assertions**: JUnit uses `AssertionError` (or custom exceptions) to signal failure — the test runner catches these in `InvocationTargetException`.
3. **Lifecycle**: A new instance per test method (JUnit's default behavior). Can be extended with `@BeforeEach`/`@AfterEach` hooks.
4. **Reporting**: Results are collected and summarized; in practice, a CI tool would format these as XML (JUnit XML format).

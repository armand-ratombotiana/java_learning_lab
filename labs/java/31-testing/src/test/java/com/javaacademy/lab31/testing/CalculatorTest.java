package com.javaacademy.lab31.testing;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import java.util.stream.Stream;

class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    @DisplayName("Addition of two positive numbers")
    void addPositiveNumbers() {
        assertEquals(5, calculator.add(2, 3), "2 + 3 should equal 5");
    }

    @Test
    @DisplayName("Addition with negative numbers")
    void addNegativeNumbers() {
        assertEquals(-1, calculator.add(2, -3));
    }

    @Test
    @DisplayName("Subtraction produces correct difference")
    void subtract() {
        assertEquals(3, calculator.subtract(7, 4));
    }

    @Test
    @DisplayName("Multiply by zero yields zero")
    void multiplyByZero() {
        assertEquals(0, calculator.multiply(42, 0));
    }

    @Test
    @DisplayName("Divide two integers produces correct quotient")
    void divide() {
        assertEquals(2.5, calculator.divide(5, 2), 1e-9);
    }

    @Test
    @DisplayName("Division by zero throws ArithmeticException")
    void divideByZero() {
        assertThrows(ArithmeticException.class, () -> calculator.divide(1, 0));
    }

    @Test
    @DisplayName("Factorial of zero is one")
    void factorialZero() {
        assertEquals(1, calculator.factorial(0));
    }

    @Test
    @DisplayName("Factorial of positive number")
    void factorialPositive() {
        assertEquals(120, calculator.factorial(5));
    }

    @Test
    @DisplayName("Factorial of negative throws exception")
    void factorialNegative() {
        assertThrows(IllegalArgumentException.class, () -> calculator.factorial(-1));
    }

    @Test
    @DisplayName("Square root of positive number")
    void sqrtPositive() {
        assertEquals(3.0, calculator.sqrt(9), 1e-9);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 1, 4, 9, 16, 25})
    @DisplayName("Square root parameterized test")
    void sqrtValues(double input) {
        double expected = Math.sqrt(input);
        assertEquals(expected, calculator.sqrt(input), 1e-9);
    }

    @ParameterizedTest
    @CsvSource({
        "1, 1, 2",
        "10, 20, 30",
        "-5, 5, 0",
        "0, 0, 0",
        "100, -50, 50"
    })
    @DisplayName("Add parameterized with CSV source")
    void addParameterized(double a, double b, double expected) {
        assertEquals(expected, calculator.add(a, b), 1e-9);
    }

    @ParameterizedTest
    @MethodSource("addTestProvider")
    @DisplayName("Add parameterized with method source")
    void addMethodSource(double a, double b, double expected) {
        assertEquals(expected, calculator.add(a, b), 1e-9);
    }

    static Stream<Arguments> addTestProvider() {
        return Stream.of(
            Arguments.of(1.5, 2.5, 4.0),
            Arguments.of(0.1, 0.2, 0.3),
            Arguments.of(-1, -1, -2)
        );
    }

    @Test
    @DisplayName("Assumption demo — only runs on certain systems")
    void assumptionDemo() {
        assumeTrue(System.getProperty("os.name").contains("Windows"),
                   "This test only runs on Windows");
        assertNotNull(calculator);
    }

    @Nested
    @DisplayName("Edge case tests")
    class EdgeCases {
        @Test
        @DisplayName("Very large numbers")
        void largeNumbers() {
            assertEquals(Double.POSITIVE_INFINITY, calculator.multiply(Double.MAX_VALUE, 2));
        }

        @Test
        @DisplayName("NaN propagation")
        void nanPropagation() {
            assertTrue(Double.isNaN(calculator.add(Double.NaN, 5)));
        }

        @Test
        @DisplayName("Operation enum test")
        void operationEnum() {
            assertEquals(7, calculator.calculate(3, 4, Calculator.Operation.ADD));
        }
    }
}

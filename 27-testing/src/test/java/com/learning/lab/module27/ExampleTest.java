package com.learning.lab.module27;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ExampleTest {

    @Test
    @DisplayName("Simple addition test")
    void testAddition() {
        Calculator calc = new Calculator();
        assertEquals(10, calc.add(5, 5), "5 + 5 should equal 10");
    }

    @Test
    void testSubtraction() {
        Calculator calc = new Calculator();
        assertEquals(5, calc.subtract(10, 5));
    }

    @Test
    void testMultiplication() {
        Calculator calc = new Calculator();
        assertEquals(50, calc.multiply(10, 5));
    }

    @Test
    void testDivision() {
        Calculator calc = new Calculator();
        assertEquals(2.0, calc.divide(10, 5), 0.01);
    }

    @Test
    void testDivisionByZero() {
        Calculator calc = new Calculator();
        assertThrows(ArithmeticException.class, () -> calc.divide(10, 0));
    }

    @Test
    void testArrayOperations() {
        int[] numbers = {1, 2, 3, 4, 5};
        assertEquals(5, numbers.length);
        assertArrayEquals(new int[]{1, 2, 3}, new int[]{1, 2, 3});
    }

    @Test
    void testStringOperations() {
        String str = "Hello";
        assertTrue(str.startsWith("He"));
        assertFalse(str.startsWith("X"));
        assertEquals("hello", str.toLowerCase());
    }

    @Test
    void testNullChecks() {
        String nullString = null;
        assertNull(nullString);
        assertNotNull("value");
    }

    @Test
    void testSameAndEquals() {
        String s1 = new String("test");
        String s2 = "test";
        assertEquals(s1, s2);
    }

    @Test
    void testListOperations() {
        java.util.List<String> list = new java.util.ArrayList<>();
        list.add("a");
        list.add("b");
        assertEquals(2, list.size());
        assertTrue(list.contains("a"));
    }

    @Test
    @Disabled("Skipping for demo purposes")
    void testSkippedTest() {
        fail("This test should not run");
    }

    @Test
    void testAssumption() {
        assumeTrue(System.getProperty("os.name") != null);
        assertNotNull(System.getProperty("os.name"));
    }

    @Nested
    class CalculatorNestedTests {
        @Test
        void testPower() {
            Calculator calc = new Calculator();
            assertEquals(8, calc.power(2, 3));
        }

        @Test
        void testSquareRoot() {
            Calculator calc = new Calculator();
            assertEquals(4, calc.sqrt(16), 0.01);
        }
    }

    @Test
    void testMultipleAssertions() {
        Calculator calc = new Calculator();
        int result = calc.add(3, 4);
        assertAll("Multiple checks",
            () -> assertEquals(7, result),
            () -> assertTrue(result > 5),
            () -> assertFalse(result < 0)
        );
    }
}

class Calculator {
    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public double divide(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return a / b;
    }

    public int power(int base, int exponent) {
        return (int) Math.pow(base, exponent);
    }

    public double sqrt(double value) {
        return Math.sqrt(value);
    }
}
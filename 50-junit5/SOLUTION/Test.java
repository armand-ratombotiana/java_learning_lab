package com.learning.junit5;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.Duration;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JUnit5SolutionTest {

    private JUnit5Solution solution;

    @BeforeEach
    void setUp() {
        solution = new JUnit5Solution();
    }

    @Test
    void testTestAddition() {
        solution.testAddition();
    }

    @Test
    void testMultiply() {
        JUnit5Solution.MathTests mathTests = new JUnit5Solution.MathTests();
        mathTests.testMultiply();
    }

    @Test
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void testIsPositive(int number) {
        assertTrue(number > 0);
    }

    @Test
    @ParameterizedTest
    @CsvSource({"a,1", "b,2"})
    void testFruitCount(String fruit, int count) {
        assertNotNull(fruit);
        assertTrue(count > 0);
    }

    @Test
    void testException() {
        assertThrows(ArithmeticException.class, () -> {
            int x = 1 / 0;
        });
    }

    @Test
    void testTimeout() {
        assertTimeout(Duration.ofSeconds(1), () -> {
            Thread.sleep(50);
        });
    }

    @Test
    void testGrouped() {
        solution.testGrouped();
    }
}
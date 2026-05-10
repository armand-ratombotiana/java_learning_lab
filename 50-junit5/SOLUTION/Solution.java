package com.learning.junit5;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class JUnit5Solution {

    @Test
    @DisplayName("Test addition")
    void testAddition() {
        assertEquals(4, 2 + 2);
    }

    @Test
    @Disabled("Not ready yet")
    void testDisabled() {
        fail("This test is disabled");
    }

    @Nested
    class MathTests {
        @Test
        void testMultiply() {
            assertEquals(12, 3 * 4);
        }

        @Test
        void testDivision() {
            assertEquals(2, 10 / 5);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void testIsPositive(int number) {
        assertTrue(number > 0);
    }

    @ParameterizedTest
    @CsvSource({
        "apple, 1",
        "banana, 2",
        "cherry, 3"
    })
    void testFruitCount(String fruit, int count) {
        assertNotNull(fruit);
        assertTrue(count > 0);
    }

    @ParameterizedTest
    @MethodSource("stringProvider")
    void testWithMethodSource(String argument) {
        assertNotNull(argument);
    }

    static Stream<String> stringProvider() {
        return Stream.of("apple", "banana", "cherry");
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
            Thread.sleep(100);
        });
    }

    @Test
    void testGrouped() {
        assertAll("Math operations",
            () -> assertEquals(10, 5 + 5),
            () -> assertEquals(20, 10 * 2),
            () -> assertEquals(5, 15 - 10)
        );
    }

    @Test
    void testOptional() {
        Optional<String> optional = Optional.of("test");
        assertTrue(optional.isPresent());
        assertEquals("test", optional.get());
    }
}
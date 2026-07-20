package com.learning.lab24;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class PatternMatchingTest {

    @Test
    @DisplayName("Instanceof pattern matching extracts variable")
    void instanceofPatternMatching() {
        Object obj = "Hello, Pattern Matching!";
        if (obj instanceof String s) {
            assertTrue(s.startsWith("Hello"));
        } else {
            fail("Should match String pattern");
        }
    }

    @Test
    @DisplayName("Instanceof with AND condition")
    void instanceofWithAndCondition() {
        Object obj = "Java 21";
        if (obj instanceof String s && s.length() > 3) {
            assertEquals("Java 21", s);
        } else {
            fail("Should match pattern with condition");
        }
    }

    @Test
    @DisplayName("Switch pattern matching with type patterns")
    void switchPatternMatching() {
        Object obj = 42;
        String result = switch (obj) {
            case Integer i -> "Integer: " + i;
            case String s -> "String: " + s;
            case null -> "null";
            default -> "Unknown";
        };
        assertEquals("Integer: 42", result);
    }

    @Test
    @DisplayName("Switch with guarded patterns")
    void switchGuardedPatterns() {
        Object obj = "long string here";
        String result = switch (obj) {
            case String s when s.length() > 10 -> "Long string";
            case String s -> "Short string";
            case null -> "null";
            default -> "Other";
        };
        assertEquals("Long string", result);
    }

    @Test
    @DisplayName("Switch with null case")
    void switchNullCase() {
        Object obj = null;
        String result = switch (obj) {
            case null -> "null value";
            case String s -> s;
            default -> "other";
        };
        assertEquals("null value", result);
    }

    @Test
    @DisplayName("Deconstruction pattern with records")
    void deconstructionPattern() {
        Object obj = new Point(3, 4);
        if (obj instanceof Point(int x, int y)) {
            assertEquals(3, x);
            assertEquals(4, y);
        } else {
            fail("Pattern should match");
        }
    }

    @Test
    @DisplayName("Nested deconstruction pattern")
    void nestedDeconstruction() {
        record Line(Point start, Point end) {}
        Object obj = new Line(new Point(0, 0), new Point(10, 20));
        if (obj instanceof Line(Point(int x1, int y1), Point(int x2, int y2))) {
            assertEquals(0, x1);
            assertEquals(0, y1);
            assertEquals(10, x2);
            assertEquals(20, y2);
        } else {
            fail("Nested pattern should match");
        }
    }
}

record Point(int x, int y) {}

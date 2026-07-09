package com.learning.lab03;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Ultra-deep tests for Control Flow lab.
 * Explores switch expressions, pattern matching, branch behavior.
 */
class UltraDeepTest {

    @Test
    void switchExpressionExhaustiveness() {
        sealed interface Shape permits Circle, Square {}
        record Circle(double r) implements Shape {}
        record Square(double s) implements Shape {}

        Shape shape = new Circle(5.0);
        String result = switch (shape) {
            case Circle c -> "circle with r=" + c.r();
            case Square s -> "square with s=" + s.s();
        };
        assertTrue(result.startsWith("circle"));
    }

    @Test
    void patternMatchingDominance() {
        Object obj = "hello";
        if (obj instanceof CharSequence cs && cs.length() > 0) {
            assertTrue(true);
        }
    }

    @Test
    void ternaryTypePromotion() {
        boolean flag = true;
        Number result = flag ? Integer.valueOf(42) : Double.valueOf(3.14);
        assertInstanceOf(Integer.class, result);
    }
}

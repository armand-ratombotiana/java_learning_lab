package com.learning.lab24;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PatternMatchingUltraDeepTest {

    @Test
    void exhaustiveSwitchOnSealedTypes() {
        sealed interface Expr permits Const, Add {}
        record Const(int value) implements Expr {}
        record Add(Expr left, Expr right) implements Expr {}
        Expr expr = new Add(new Const(1), new Const(2));
        int result = switch (expr) {
            case Const c -> c.value();
            case Add a -> evaluate(a.left()) + evaluate(a.right());
        };
        assertEquals(3, result);
    }

    private int evaluate(Expr e) {
        return switch (e) {
            case Const c -> c.value();
            case Add a -> evaluate(a.left()) + evaluate(a.right());
        };
    }

    @Test
    void guardedPatternWithMultipleConditions() {
        Object obj = "Hello";
        String result = switch (obj) {
            case String s when s.length() > 5 && s.startsWith("H") -> "Long H string";
            case String s when s.length() > 5 -> "Long string";
            case String s -> "Short string";
            case null -> "null";
            default -> "other";
        };
        assertEquals("Short string", result);
    }

    @Test
    void patternMatchingWithTernary() {
        Object obj = "test";
        String result = obj instanceof String s ? s.toUpperCase() : "N/A";
        assertEquals("TEST", result);
    }

    @Test
    void switchWithEnumPatterns() {
        enum Status { ACTIVE, INACTIVE, PENDING }
        Status s = Status.ACTIVE;
        String label = switch (s) {
            case ACTIVE -> "Active";
            case INACTIVE -> "Inactive";
            case PENDING -> "Pending";
        };
        assertEquals("Active", label);
    }
}

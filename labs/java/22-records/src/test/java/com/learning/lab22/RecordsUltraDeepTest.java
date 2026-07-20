package com.learning.lab22;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecordsUltraDeepTest {

    @Test
    void recordEqualityWithDifferentReferences() {
        var p1 = new PersonRecord("X", 1);
        var p2 = new PersonRecord("X", 1);
        assertNotSame(p1, p2);
        assertEquals(p1, p2);
    }

    @Test
    void recordInequalityWithDifferentValues() {
        var p1 = new PersonRecord("A", 1);
        var p2 = new PersonRecord("B", 1);
        assertNotEquals(p1, p2);
    }

    @Test
    void recordCanonicalConstructorCanTransform() {
        record Normalized(String value) {
            Normalized {
                value = value != null ? value.trim().toLowerCase() : "";
            }
        }
        var n = new Normalized("  HELLO  ");
        assertEquals("hello", n.value());
    }

    @Test
    void recordInstanceMethods() {
        record Calculator(int a, int b) {
            public int sum() { return a + b; }
            public int product() { return a * b; }
        }
        var calc = new Calculator(3, 4);
        assertEquals(7, calc.sum());
        assertEquals(12, calc.product());
    }

    @Test
    void localRecordWithCustomMethod() {
        record Pair(int x, int y) {
            public int sum() { return x + y; }
        }
        assertEquals(5, new Pair(2, 3).sum());
    }
}

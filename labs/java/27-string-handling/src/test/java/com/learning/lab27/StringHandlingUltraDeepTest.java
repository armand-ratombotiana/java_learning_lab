package com.learning.lab27;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StringHandlingUltraDeepTest {

    @Test
    void stringConcatVsBuilderPerformance() {
        String result = "";
        for (int i = 0; i < 10; i++) {
            result += String.valueOf(i);
        }
        assertEquals("0123456789", result);
    }

    @Test
    void stringBuilderCapacityGrowth() {
        StringBuilder sb = new StringBuilder(5);
        sb.append("12345");
        assertEquals(5, sb.length());
        sb.append("6");
        assertTrue(sb.capacity() > 5);
    }

    @Test
    void stringInternReducesMemory() {
        String a = new String("unique-" + System.currentTimeMillis());
        String b = new String(a);
        assertNotSame(a, b);
        assertSame(a.intern(), b.intern());
    }

    @Test
    void textBlockStripIndent() {
        String block = """
                \tindented
                """;
        assertEquals("\tindented\n", block);
    }

    @Test
    void stringFormattedMethod() {
        String result = "Value: %d".formatted(42);
        assertEquals("Value: 42", result);
    }

    @Test
    void stringIndentAddsSpaces() {
        String result = "hello".indent(4);
        assertTrue(result.startsWith("    "));
    }
}
